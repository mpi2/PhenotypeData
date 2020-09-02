""" Train a model

"""

import os
import argparse

import numpy as np
import pandas as pd

import torch
import torch.nn as nn
import torch.optim as optim

import torchvision
from torchvision import datasets, models, transforms
import matplotlib.pyplot as plt

# Import helper functions
import qc_helper as helper

# Parameters for this run
parser = argparse.ArgumentParser(
    description = "Build quality control model for X-ray images (can be used for specific site)"
)

parser.add_argument(
    '-m', '--model-desc-path', dest='model_desc_path', required=True,
    help="Path to description file for model to build"
)

args = parser.parse_args()

########################################################################
# Inputs needed

# File containing description of the model and all inputs needed
model_info, label_map, files_to_process = helper.parse_model_desc(args.model_desc_path)

# CSV files containing paths to images to be used to build the models.
# The names of these files should reflect the class of the images they
# contain
# files_to_process

# Number of images per class
n_per_class = model_info['n_per_class']

# Ordered dictionary of the label map.
#   keys = class that will be written out to file
#   values = descriptions of the class - should correspond to the filename
#
#   Using ordered dictionary so that a list of the keys can also be created
#   This list maps the keys from the label_map to the indicies of the output of the model
#
classes = list(label_map.keys())
n_classes = len(classes)

# Number of epochs for training
n_epochs = model_info['n_epochs']

# Learning rate
lr = model_info['learning_rate']

# define dataloader parameters
batch_size = model_info['batch_size']
num_workers= model_info['num_workers']

########################################################################
# Load images into dataframes
# Throws an error if n_per_class not met

# Convert the files to process to a list as we are building model of all
# structures
f = []
[f.extend(f2) for f2 in list(files_to_process.values())];
files_to_process = f

df_temp = pd.read_csv(files_to_process[0])
im_details = df_temp.sample(n=n_per_class, random_state=0)

for f in files_to_process[1:]:
    df_temp = pd.read_csv(f)
    im_details = pd.concat((im_details, df_temp.sample(n=n_per_class, random_state=0)))

# Re-index to make monotonic and unique
im_details = im_details.set_index(keys=np.arange(len(im_details)))
# Split into training, test and validation
# Randomly split training (60%), validation (20%) and testing (20%)
df_training = im_details.sample(frac=0.6, random_state=0)
im_details = im_details.loc[~im_details.index.isin(df_training.index)]

#im_details.set_index(keys=np.arange(len(im_details)))
df_validation = im_details.sample(frac=0.5, random_state=0)
df_testing = im_details.loc[~im_details.index.isin(df_validation.index)]

# Create transforms and dataset
im_size = 224
data_transform = transforms.Compose([ transforms.ToTensor(),
                                      transforms.Normalize((0.48527132, 0.46777139, 0.39808026), (0.26461128, 0.25852081, 0.26486896))])

training_dataset = helper.ImageLabelDataset(df_training, labels=label_map, path_column="imagename",
                                  root_dir=None, transform=data_transform)
validation_dataset = helper.ImageLabelDataset(df_validation, labels=label_map, path_column="imagename",
                                  root_dir=None, transform=data_transform)
testing_dataset = helper.ImageLabelDataset(df_testing, labels=label_map, path_column="imagename",
                                  root_dir=None, transform=data_transform)
# Get some data stats
n_train = len(training_dataset)
n_valid = len(validation_dataset)
n_test = len(testing_dataset)

# prepare data loaders
train_loader = torch.utils.data.DataLoader(training_dataset,
                                            batch_size=batch_size, 
                                            num_workers=num_workers,
                                            shuffle=False)
valid_loader = torch.utils.data.DataLoader(validation_dataset,
                                            batch_size=batch_size, 
                                            num_workers=num_workers,
                                            shuffle=False)
test_loader = torch.utils.data.DataLoader(testing_dataset, 
                                            batch_size=batch_size, 
                                            num_workers=num_workers,
                                            shuffle=False)


# Visualize some sample data - plot to output directory for model

# obtain one batch of training images
dataiter = iter(train_loader)
images, labels, im_paths = dataiter.next()
images = images.numpy().copy()

# plot the images in the batch, along with the corresponding labels
fig = plt.figure(figsize=(25, 4))
for idx in np.arange(20):
    ax = fig.add_subplot(2, 20/2, idx+1, xticks=[], yticks=[])
    im = helper.unit_scaling(images[idx])
    plt.imshow(np.transpose(im, (1, 2, 0)))
    ax.set_title(label_map[labels[idx].numpy()+1])

figure_output_path = os.path.join(model_info['model_dir'],os.path.splitext(model_info['model_fname'])[0]+"sample_data.png")
plt.savefig(figure_output_path)
plt.close(fig)


# Load the pretrained model from pytorch

import os
# Set the location to download pre-trained models from model zoo
# default is ~/.torch, but this uses up my quota
os.environ['TORCH_MODEL_ZOO'] = '/nfs/nobackup/spot/machine_learning/model_zoo/'

model = models.vgg16(pretrained=True)

# Freeze training for all "features" layers
for param in model.features.parameters():
    param.require_grad = False

# Replace last layer for our use case (and add a softmax layer)
num_features = model.classifier[6].in_features
features = list(model.classifier.children())[:-1]
features.extend([nn.Linear(num_features, n_classes)])
# Add softmax layer
features.extend([nn.Softmax(dim=1)])

model.classifier = nn.Sequential(*features)


# specify loss function (categorical cross-entropy) - as I have included softmax layer only need NLL
criterion = nn.NLLLoss()

# specify optimizer (stochastic gradient descent) and learning rate
optimizer = optim.SGD(model.parameters(), lr=0.01)

# Use scheduler to decrease learning rate every step_size epochs
step_size = 10
scheduler = optim.lr_scheduler.StepLR(optimizer, step_size, gamma=0.5, last_epoch=-1)

# Train the model
#
# check if CUDA is available
train_on_gpu = torch.cuda.is_available()
if not train_on_gpu:
    print('CUDA is not available.  Training on CPU ...')
else:
    model = model.cuda()
    print('CUDA is available!  Training on GPU ...')

# initialize tracker for minimum validation loss and allow model with
# lowest validation loss to be saved
valid_loss_min = np.Inf
# Use a variable for this!!!
save_path = os.path.join(model_info['model_dir'], model_info['model_fname'])
  
for epoch in np.arange(1,n_epochs+1):
    training_loss = 0.0
    model.train()
    scheduler.step()
    for i, inputs in enumerate(train_loader):
        data, target, im_paths = inputs
        if train_on_gpu:
            data, target = data.cuda(), target.cuda()
        optimizer.zero_grad()
        outputs = model(data)
        loss = criterion(outputs, target)
        loss.backward()
        optimizer.step()
        
        training_loss += loss.item()
        
    #Apply model to validation set
    model.eval()
    validation_loss = 0.0
    for i, inputs in enumerate(valid_loader):
        data, target, im_paths = inputs
        if train_on_gpu:
            data, target = data.cuda(), target.cuda()
        outputs = model(data)
        loss = criterion(outputs, target)
        validation_loss += loss.item()    
        
    print('{0:d}, training_loss: {1:.5f}, validation_loss: {2:.5f}'.format(epoch, training_loss/n_train, validation_loss/n_valid))
    
    if validation_loss < valid_loss_min:
        valid_loss_min = validation_loss
        torch.save(model.state_dict(), save_path)
print('Finished Training')

# Load the best model
model.load_state_dict(torch.load(save_path))
model.eval()
    
# track test loss over all 6 classes
test_loss = 0.0
class_correct = list(0. for i in range(n_classes))
class_total = list(0. for i in range(n_classes))

# Lists for confusion matrix to give insight into results
list_targets = []
list_outputs = []

# iterate over test data
for data, target, im_path in test_loader:
    # move tensors to GPU if CUDA is available
    if train_on_gpu:
        data, target = data.cuda(), target.cuda()
    # forward pass: compute predicted outputs by passing inputs to the model
    output = model(data)
    # calculate the batch loss
    loss = criterion(output, target)
    # update  test loss 
    # test_loss += loss.item()*data.size(0) - correct way to calculate loss, but use same metric as for training and validation
    test_loss += loss.item()
    # convert output probabilities to predicted class
    _, pred = torch.max(output, 1)    
    # compare predictions to true label
    correct_tensor = pred.eq(target.data.view_as(pred))
    correct = np.squeeze(correct_tensor.numpy()) if not train_on_gpu else np.squeeze(correct_tensor.cpu().numpy())
    # calculate test accuracy for each object class
    for i in range(target.data.size()[0]):
        label = target.data[i]
        class_correct[label] += correct[i].item()
        class_total[label] += 1
    
    # Add to list for calculating confusion matrix
    list_outputs.extend(np.squeeze(pred.numpy()) if not train_on_gpu else np.squeeze(pred.cpu().numpy()))
    list_targets.extend(np.squeeze(target.numpy()) if not train_on_gpu else np.squeeze(target.cpu().numpy()))

# calculate avg test loss
test_loss = test_loss/n_test
print('Test Loss: {:.6f}\n'.format(test_loss))

for i in range(n_classes):
    if class_total[i] > 0:
        print("Test Accuracy of {class_desc:>18s}: {class_accuracy:.2f} ({n_correct:.0f}/{class_total:.0f})".format(
                class_desc=label_map[classes[i]],
                class_accuracy=100 * class_correct[i] / class_total[i],
                n_correct=np.sum(class_correct[i]),
                class_total=np.sum(class_total[i])))
    else:
        print('Test Accuracy of {:>18s}: N/A (no training examples)'.format(label_map[classes[i]]))

print('\nTest Accuracy (Overall): {:.2f}% ({:.0f}/{:.0f})'.format(
    100. * np.sum(class_correct) / np.sum(class_total),
    np.sum(class_correct), np.sum(class_total)))

print('\nConfusion matrix:')
print(helper.conf_mat(np.array(list_targets),np.array(list_outputs)))
