# coding: utf-8

""" Application of all sites QC mode
    We apply the model built from data from all sites. This has to be
    run by submission to the LSF queue on the cluster.

    This script was generated from an ipython notebook of the same name
    then modified into its current form
"""

import sys
import os
import re
import argparse
import numpy as np
import torch

import torchvision
from torchvision import datasets, models, transforms
import torch.nn as nn
import pandas as pd

# Import helper functions
import qc_helper as helper

# Parameters for this run
parser = argparse.ArgumentParser(
    description = "Apply quality control model to X-ray images"
)

parser.add_argument(
    '--site-name', dest='site_name', required=True,
    help='Abbreviated name of site as in the directory in images/clean'
)
parser.add_argument(
    '--parameter-stable-id', dest='parameter_stable_id', required=True,
    help='Parameter stable ID as specified in IMPRESS'
)
parser.add_argument(
    '-d', '--base-dir', dest='dir_base', default="/nfs/komp2/web/images/clean/impc/",
    help='Base directory for location of images'
)
parser.add_argument(
    '-p', '--print-every', dest='print_every', default=500, type=int,
    help='Number of iterations before printing prediction stats note that this also saves the predictions up to this point which is useful incase the program crashes. Use -1 to prevent printing anything.'
)
parser.add_argument(
    '-o', '--output-dir', dest='output_dir', default="/nfs/nobackup/spot/machine_learning/impc_mouse_xrays/quality_control_all_sites/images_to_classify/",
    help='Directory to read and write files associated with prediction'
)
parser.add_argument(
    '-m', '--model-path', dest='model_path',
    default="/nfs/nobackup/spot/machine_learning/impc_mouse_xrays/quality_control_all_sites/model_transfer.pt",
    help="Path to model to use for predictions"
)
parser.add_argument(
    '-n', '--n-classes', dest='n_classes', type=int,
    default=6, help="Number of classes the model outputs"
)
parser.add_argument(
    '-v', '--model-version', dest='model_version', type=int,
    default=1, help="Version of model 1 -> no softmax layer, or 2"
)
parser.add_argument(
    '--output-filename', dest='output_filename',
    help="Name to use for output file base. If not specified the site name a nd parameter stable ID are concatenated"
)

args = parser.parse_args()
print_every = args.print_every
site_name = args.site_name;
parameter_stable_id = args.parameter_stable_id
dir_base = args.dir_base
model_version = args.model_version

if model_version != 1 and model_version != 2:
    print("Model version must be 1 or 2. - Exiting")
    sys.exit(-1)

# File containing description of the model and all inputs needed
model_info, label_map, files_to_process = helper.parse_model_desc(args.model_path)
classes = list(label_map.keys())
n_classes = len(classes)

to_process = os.path.join(args.output_dir,site_name+"_"+parameter_stable_id+".txt")

# Create the output file names using argument if supplied or from site
# name and parameter stable ID if --output-filename parameter not supplied
if args.output_filename is None:
    output_filename = site_name+"_"+parameter_stable_id
else:
    output_filename = args.output_filename

processed_output_path = os.path.join(args.output_dir,output_filename+"_processed.csv")
mis_classified_output_path = os.path.join(args.output_dir,output_filename+"_misclassified.csv")
unable_to_read_output_path = os.path.join(args.output_dir,output_filename+"_unable_to_read.csv")

# Dict to map parameter_stable_ids to expected_class
#parameter_to_class_map = {
#    'IMPC_XRY_051_001' : 1,
#    'IMPC_XRY_049_001' : 2,
#    'IMPC_XRY_034_001' : 3,
#    'IMPC_XRY_048_001' : 4,
#    'IMPC_XRY_050_001' : 5,
#    'IMPC_XRY_052_001' : 6,
#}

# Because of inclusion of LA need to make mapping more general
parameter_to_class_map = {
    '_XRY_051_001' : 1,
    '_XRY_049_001' : 2,
    '_XRY_034_001' : 3,
    '_XRY_048_001' : 4,
    '_XRY_050_001' : 5,
    '_XRY_052_001' : 6,
}
regex = re.compile('(_XRY_0[0-9]{2}_001)')
parameter_id_stem = regex.findall(parameter_stable_id)[0]
expected_class = parameter_to_class_map[parameter_id_stem]

# In[3]:


# check if CUDA is available
use_cuda = torch.cuda.is_available()

if not use_cuda:
    print('CUDA is not available.  Training on CPU ...')
else:
    print('CUDA is available!  Training on GPU ...')


# In[4]:

#classes = parameter_to_class_map.values()
#n_classes = len(classes)

# Read in metadata
imdetails = pd.read_csv(to_process)

n_images = len(imdetails)
print(f"Number of images available: {n_images}")

# Create transforms and dataset
im_size = 224
data_transform = transforms.Compose([ transforms.Lambda(lambda im: helper.crop_to_square(im)),
                                      transforms.Resize(im_size), 
                                      transforms.ToTensor(),
                                      transforms.Normalize((0.48527132, 0.46777139, 0.39808026), (0.26461128, 0.25852081, 0.26486896))])

dataset = helper.ImageDataset(imdetails, path_column="imagename",
                                  root_dir=None, transform=data_transform)
# define dataloader parameters
batch_size = 10
num_workers=0


### Transforming the Data
# 
# When we perform transfer learning, we have to shape our input data into the shape that the pre-trained model expects. VGG16 expects `224`-dim square images as input and so, we resize each flower image to fit this mold.

# ## Define the Model
# 
# To define a model for training we'll follow these steps:
# 1. Load in a pre-trained VGG16 model
# 2. "Freeze" all the parameters, so the net acts as a fixed feature extractor 
# 3. Remove the last layer
# 4. Replace the last layer with a linear classifier of our own
# 
# **Freezing simply means that the parameters in the pre-trained model will *not* change during training.**

# In[9]:

# Load the pretrained model from pytorch
model_transfer = models.vgg16(pretrained=True)

# Freeze training for all "features" layers
for param in model_transfer.features.parameters():
    param.require_grad = False

# Replace last layer for our use case
num_features = model_transfer.classifier[6].in_features
features = list(model_transfer.classifier.children())[:-1]
features.extend([nn.Linear(num_features, n_classes)])
if model_version == 2:
    features.extend([nn.Softmax(dim=1)])

model_transfer.classifier = nn.Sequential(*features)
    
# Load our learnt weights
model_path = os.path.join(model_info['model_dir'],model_info['model_fname'])
if use_cuda:
    model_transfer = model_transfer.cuda()
    model_transfer.load_state_dict(torch.load(model_path))
else:
    model_transfer.load_state_dict(torch.load(model_path, map_location='cpu'))
    

print("Configured model from: " + model_path)

# Apply the model to qc images
n_images = len(dataset)
predictions = np.ones([n_images,],np.byte) * -1
class_scores = np.zeros([n_images,], np.float)
mis_classifieds = []
unable_to_read = []

for i in range(n_images):
    try:
        image, imname = dataset[i]
        if use_cuda:
            image = image.cuda()

        output = model_transfer(image.unsqueeze(0))
        output =np.squeeze(output.data.cpu().numpy())
        index = np.argwhere(output == output.max())[0]
        #predictions[i] = index+1
        predictions[i] = classes[index[0]]
        class_scores[i] = output[index]
    
        if predictions[i] != expected_class:
            mis_classifieds.append((i,imdetails['imagename'][i],predictions[i], class_scores[i]))
        if print_every > 0 and i%print_every == 0:
            print(f"Iteration {i}")
            print("Number of misclassifieds: {0}".format(len(mis_classifieds)))
            # Also save predictions in case job crashes
            processed_output_path_temp = "{0}_{1:05d}".format(processed_output_path,i)
            imdetails['classlabel'] = predictions
            imdetails['classscore'] = class_scores
            imdetails.to_csv(processed_output_path_temp, index=False)
    except Exception as e:
        print("An error occured")
        print(e)
        unable_to_read.append(i)

# Save the new dataframe
imdetails['classlabel'] = predictions
imdetails['classscore'] = class_scores
imdetails = imdetails.astype({'classscore': 'float64'})
imdetails.to_csv(processed_output_path, index=False, float_format='%.2f')
print("Saved processed images to " + processed_output_path)

# Save misclassifieds
if len(mis_classifieds) > 0:
    mis_classifieds_df = pd.DataFrame(columns=('index','imagepath','expected','predicted','classscore'))
    for i, (index, im_path, predicted, class_score) in enumerate(mis_classifieds):
        mis_classifieds_df.loc[i] = [index, im_path, expected_class, predicted, class_score]
    mis_classifieds_df.to_csv(mis_classified_output_path, index=False)
    print("Saved misclassified images to " + mis_classified_output_path)

if len(unable_to_read) > 0:
    unable_to_read_df = pd.DataFrame(columns=('index','imagepath',))
    for i, ind in enumerate(unable_to_read):
        unable_to_read_df.loc[i] = [ind, imdetails['imagename'][ind]]

    unable_to_read_df.to_csv(unable_to_read_output_path, index=False)
    print("Saved unable_to_read to " + unable_to_read_output_path)

