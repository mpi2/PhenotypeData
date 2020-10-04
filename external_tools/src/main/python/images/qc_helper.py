"""
    This file contains helper functions for using CNNs with pytorch
"""

# Helper function to read Dicom images
# Arguments:
#   im_path: the path to the image to be read
#
# Returns:
#   Image as a numpy array with dimension 3
def readDicom(im_path):
    import numpy as np
    import SimpleITK as sitk
    
    im = sitk.ReadImage(im_path)
    im = np.squeeze(sitk.GetArrayFromImage(im))
    # Handle issue that some png images have an alpha layer -> 4 planes
    if im.ndim == 3 and im.shape[2] > 3:
        im = im[:,:,:3]
    im = im.astype(np.float)*255./im.max()
    return im.astype(np.uint8)

# Crop input image into a square.
#   Assume object of interest is in the centre of the image and perform
#   something similar to pytorch's centre crop, that makes the image square
#   by removing pixels from the longer dimension. E.g. if width is greater
#   than height then remove (width-height)/2 from left and right and new
#   image is heightxheight which can then be rescaled as desired.
# Arguments:
#   im: input image to be cropped
# Returns:
#   Cropped image as a uint8 PIL image
def crop_to_square(im):
    import numpy as np
    from PIL import Image
    
    # Crop an image to be square by removing pixels from left/right
    # or top/bottom from the larger dimension
    
    #w, h = im.size
    w, h = im.shape[:2]
    if w == h:
        # Do nothing
        pass 
    elif w > h:
        # crop left right
        x_min = np.int((w - h)/2)
        im = im.crop((x_min-1,0,x_min+h-1,h))
    else:
        y_min = np.int((h - w)/2)
        left = 0
        upper = y_min+w-1
        right = w
        lower = y_min-1
        im = im[left:right,lower:upper]
    if len(im.shape) < 3 or im.shape[2] != 3:
        im = np.repeat(im[:,:,np.newaxis],3,2)
    return Image.fromarray(im.astype(np.uint8))
    #return im.crop((left,lower,right,upper))

# Create confusion matrix
# Arguments:
#   row_arr: vector of values along rows (e.g. outputs from model)
#   col_arr: vector of values down cols (e.g. target values from test data)
#
# Returns:
#   Confusion matrix as pandas dataframe
def conf_mat(row_arr,col_arr):
    import pandas as pd
    import numpy as np
    if not np.all(row_arr.shape == col_arr.shape):
        print("Need shapes of both arrays to be equal")
        return False
    n = row_arr.shape[-1]
    unique_values = list(set(row_arr[:]).union(set(col_arr[:])))
    unique_values.sort()
    n2 = len(unique_values)
    cmat = np.zeros((n2,n2),np.int)
    
    for r, c in zip(row_arr, col_arr):
        row_index = unique_values.index(r)
        col_index = unique_values.index(c)
        cmat[row_index,col_index] += 1

    return pd.DataFrame(data=cmat, index=unique_values, columns=unique_values)


# Dataset class to load in images and labels
from torch.utils.data import Dataset
import os
import pandas as pd
class ImageLabelDataset(Dataset):
    """Load images and labels"""
    

    
    def __init__(self, imdetails, labels, path_column='imagename', label_column='verified_classlabel', root_dir=None, transform=None):
        """
        Args:
            imdetails (pandas dataframe): Dataframe with at least two 
                columns- one containing the label and the other containing
                details of the file_path
            labels (ordered dict): An ordered dict mapping the names to 
                call labels in this ipynb with what they are called in the
                file. The model uses numbers which are determined by the 
                order in the ordered dict
            path_column (string): The name of the dataframe column 
                containing the paths
            label_column (string): The name of the dataframe column 
                containing the labels
            root_dir (string): Directory with all the images. If this is 
                to be determined from the file_path set it to None 
                (the default)
            transform (callable, optional): Optional transform to be 
                applied on a sample.
        """
        self.imdetails = imdetails
        self.root_dir = root_dir
        self.transform = transform
        
        self.label_keys = list(labels.keys())
        self.label_values = list(labels.values())
        self.path_column = path_column
        self.label_column = label_column
        
    def __len__(self):
        return len(self.imdetails)
    
    def __getitem__(self, idx):
        img_path = self.imdetails[self.path_column].iloc[idx]
        im_name = os.path.split(img_path)[-1]
        if self.root_dir is not None:
            img_path = os.path.join(self.root_dir,im_name)
        image = readDicom(img_path)
    
        if self.transform:
            image = self.transform(image)
        
        label_index = self.label_keys.index(self.imdetails[self.label_column].iloc[idx])
        #label = torch.ones(1) * label
        #return {'image': image, 'name': im_name, 'label': label}
        #return  image, label.type(torch.LongTensor)
        return  image, label_index, im_name
    
class ImageDataset(Dataset):
    """Load images only"""
    

    
    def __init__(self, imdetails, path_column, root_dir=None, transform=None):
        """
        Args:
            imdetails (pandas dataframe): Dataframe with at least one 
                column containing the file_path
            path_column (string): The name of the dataframe column 
                containing the paths
            root_dir (string): Directory with all the images. If this is 
                to be determined from the file_path set it to None 
                (the default)
            transform (callable, optional): Optional transform to be 
                applied on a sample.
        """
        self.imdetails = imdetails
        self.root_dir = root_dir
        self.transform = transform
        
        self.path_column = path_column
        
    def __len__(self):
        return len(self.imdetails)
    
    def __getitem__(self, idx):
        img_path = self.imdetails[self.path_column].iloc[idx]
        im_name = os.path.split(img_path)[-1]
        if self.root_dir is not None:
            img_path = os.path.join(self.root_dir,im_name)
        image = readDicom(img_path)
    
        if self.transform:
            image = self.transform(image)
        
        return  image, im_name


def parse_model_desc(model_desc_path):
    """Parse a model description file for building DL models for QC
    
    Parameters
    ----------
    model_desc_path : str - Path to json file containing info (see example 
                            below. Both model (.pt) and info (.json) files
                            must be in the same directory)

    returns
    -------
    model_info : dict - Dictionary of all contents in the file
    label_map : Ordered dict - Map of labels (keys) to descriptions. An
                               ordered dict is used so the indicies of the
                               keys relate to the output of the model.
    files_to_process : dict - Dictionary of files to process for each class

    Example
    -------
    Example contents of model description (json) file:
    {
        "model_description": "hind_leg_hip",
        "model_fname": "bcm_all_structures_model.pt",
        "model_version": 2,
        "n_per_class": 100,
        "n_epochs": 2,
        "batch_size": 32,
        "num_workers": 0,
        "learning_rate": 0.01,
        "classes": {
                "head_dorsal": {
                        "index": 0,
                        "class": 1,
                        "parameter_stable_id": "IMPC_XRY_051_001",
                        "training_images": {
                            "1": "/home/kola/temp/BCM_IMPC_XRY_051_001.csv"
                        }
                },
                 "forepaw": {
                        "index": 1,
                        "class": 2,
                        "parameter_stable_id": "IMPC_XRY_049_001",
                        "training_images": {
                            "1": "/home/kola/temp/BCM_IMPC_XRY_049_001.csv"
                        }
                }
        }
    }

    When creating single structure (binary) models there are just a positive
    and a negative class and the classes part of the above example will be
    something like:
    "classes": {
                "hind_leg_hip": {
                        "index": 0,
                        "class": 6,
                        "parameter_stable_id": "IMPC_XRY_052_001",
                        "training_images": {
                            "1": "hind_leg_and_hip.csv"
                        }
                },
                "not_hind_leg_hip": {
                        "index": 1,
                        "class": -3,
                        "parameter_stable_id": "",
                        "training_images": {
                            "1": "forepaw.csv",
                            "2": "skull_dorsal.csv",
                            "3": "whole_body_lateral.csv",
                            "4": "whole_body_dorsal.csv"
                        }
                }
    }
    """

    from collections import OrderedDict
    import json


    with open(model_desc_path) as fid:
        model_info = json.load(fid)

    # Need to enumerate classes to get label_map and files_to_process
    label_map = OrderedDict()
    files_to_process = {}
    for key, value in model_info['classes'].items():
        label_map[value['class']] = key
        files_to_process[value['class']] = list(value['training_images'].values())

    # Add model directory (obtained from model_desc_path)
    model_info['model_dir'] = os.path.dirname(model_desc_path)

    return model_info, label_map, files_to_process

def read_files_to_process(files_to_process, label_map, n_per_class, random_state=0):
    """Read files to process into dataframe

    Read the files to process and extracts image paths and label values for
    each example. We assume the files to process are csv and contain at 
    least two columns
        1) imagename - path to image
        2) verified_classlabel - label to associate with image
    If the verified_classlabel is different from the label in the label
    map, that in the label map will be used and a warning printed. This is 
    legitimate when building structure specific models where we use the 
    label "-3" for the structures we are not interested in. "-3" was chosen
    as we use "-1" to indicate images that cannot be read and "-2" for
    images that although they can be read have some issue - e.g. have
    no mouse in the image.

    Parameters
    ----------
    files_to_process: dict - key is label, value is list of paths to 
                             files to process.
    label_map: Ordered dict - Keys are labels and values are the 
                              descriptions of the labels
    n_per_class: int - number of examples to take from the class
    random_state: int - seed for randomising choice of examples

    Returns
    -------
    df_im_details: pandas dataframe - examples to process and their labels
    """

    import pandas as pd
    import numpy as np

    im_details = None
    for label, file_paths in files_to_process.items():
        df_temp = _read_files_to_process(file_paths, n_per_class, random_state)
        # Set all the verified_classlabels to the label for this class
        df_temp['verified_classlabel'] = label
        if im_details is None:
            im_details = df_temp
        else:
            im_details = pd.concat((im_details, df_temp))
    
    # Re-index to make indicies monotonic and unique
    im_details = im_details.set_index(keys=np.arange(len(im_details)))
    return im_details


def _read_files_to_process(file_paths, n_per_class, random_state):
    """Local function to read in files to process

    """
    import pandas as pd
    import numpy as np

    # If we have one file only just read and randomly sample it.
    if len(file_paths) == 1:
        return pd.read_csv(file_paths[0]).sample(n=n_per_class, random_state=random_state)

    # For more than one file we want to make up the number of examples
    # to be as evenly distributed from each file as possible.
    list_file_contents = []
    list_n_per_file = []

    for file_path in file_paths:
        list_file_contents.append(pd.read_csv(file_path))
        list_n_per_file.append(len(list_file_contents[-1]))

    n_files = len(list_file_contents)
    n_examples_per_file = n_per_class // n_files

    # Iteratively training set. Try to balance out examples from each file
    # by including all examples from files with below the average needed
    # then recalculating the average
    im_details = None
    while np.min(list_n_per_file) < n_examples_per_file:
        index = list_n_per_file.index(np.min(list_n_per_file))
        if im_details is None:
            im_details = list_file_contents[index].copy()
        else:
            im_details = pd.concat((im_details, list_file_contents[index]))
        list_n_per_file.pop(index);
        list_file_contents.pop(index);
        if len(list_file_contents) == 0:
            break
            
        n_examples_per_file = (n_per_class - len(im_details))//len(list_file_contents)
    
    # Sample rest of negative training set from negative classes that have more than the average number of
    # negative examples per class needed
    for df_temp in list_file_contents:
        if im_details is None:
            im_details = df_temp.sample(n=n_examples_per_file, random_state=random_state)
        else:
            im_details = pd.concat((im_details, df_temp.sample(n=n_examples_per_file, random_state=random_state)))

    return im_details


def unit_scaling(im):
    """Scale image to range [0,1]

    Accepts a numpy array from pytorch's .numpy.copy() function and
    scales the values to between 0 and 1. This utitlity function is 
    needed because after applying mean/sd normalisation some pixel values
    are below zero and displaying the images does not look good.

    Unusually there does not seem to be a function in pytorch for 
    normalising to between 0 and 1. And the MinMaxScalars in sklearn seem
    to apply to single dimensions - not geared for image data.

    Parameters
    ----------
    im : numpy array - expected to be z,x,y i.e. first dim contains whole
         images


    returns
    -------
    im : rescaled version of image with pixel values in [0,1] (done inplace
         for efficiency)

    """
    import numpy as np

    if im.ndim == 3:
        for i in np.arange(3):
            im[i,:,:] -= np.min(im[i,:,:])
            im[i,:,:] /= np.max(im[i,:,:])
    elif im.ndim == 2:
        im -= np.min(im)
        im /= np.max(im)

    return im

