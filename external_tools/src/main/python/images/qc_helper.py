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
    im = sitk.GetArrayFromImage(im)
    return np.squeeze((im.astype(np.float)*255./im.max()).astype(np.uint8))

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
    

    
    def __init__(self, imdetails, labels, path_column, label_column, root_dir=None, transform=None):
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