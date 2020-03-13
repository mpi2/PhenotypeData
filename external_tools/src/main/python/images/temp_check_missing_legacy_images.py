"""
    Temporary script to download legacy images missing from EBI nfs.

"""

import sys
import os
import pandas as pd
import requests
import json

def get_images_for_directory(directory, dataframe,
                             output_dir_base,
                             nfs_dir_base):
    """
    Get all images in a directory from the solr dataframe and store on nfs
    
    Parameters
    ----------
    directory : directory from solr list for which to get images.
    
    dataframe : Pandas dataframe containing results of solr search 
        with columns
            'fullResolutionFilePath', 'largeThumbnailFilePath',
            'originalFileName' and 'smallThumbnailFilePath'
    
    output_dir_base : base directory to store images. A subdirectory with
        the specific directory of interest (1st parameter to function)
        will be created here. Note that this directory must not contain
        the 'images' sub directory.
    
    nfs_dir_base : base directory where images are stored on NFS. Can be 
        set to None if we do not want to check if images exist before
        downloading. Note that this directory must not contain the images
        sub directory.
    """

    sanger_url = "https://img1.sanger.ac.uk/"
    check_nfs = False
    if nfs_dir_base is not None:
        nfs_dir = os.path.join(nfs_dir_base,"images", directory)
        if os.path.isdir(nfs_dir):
            print("Setting check_nfs to true")
            check_nfs = True
    
    output_dir = os.path.join(output_dir_base, 'images', directory)
    if not os.path.isdir(output_dir):
        print(f"Output dir {output_dir} does not exist. Creating it...")
        os.makedirs(output_dir)

    columns = ['fullResolutionFilePath','largeThumbnailFilePath',
        'smallThumbnailFilePath']
    for column in columns:
        idx = dataframe[dataframe[column].str.contains("/"+directory+"/")].index
        for i in idx:
            image_path = dataframe.loc[i][column]
            if check_nfs:
                nfs_image_path = os.path.join(nfs_dir_base, image_path)
                print(f"Checking existence of {nfs_image_path}")
                if os.path.isfile(nfs_image_path):
                    print(f"{nfs_image_path} exists on nfs-not downloading")
                    continue
            url = sanger_url + image_path
            image_path = os.path.join(output_dir_base, image_path)

            # Before downloading check the file does not exist
            if os.path.isfile(image_path):
                print(f"{image_path} exists in output dir-not downloading")
                continue

            command = f"wget -P {output_dir} {url}"
            print(command)
            ret_val = os.system(command)
            if ret_val != 0:
                print(f"Unable to download {url} to {output_dir}")

nfs_base_dir = '/nfs/public/ro/mousephenotype/'
output_dir = '/nfs/nobackup/spot/machine_learning/impc_legacy_images'
#nfs_base_dir = '/home/kola/temp/nfs/public/ro/mousephenotype/'
#output_dir = '/home/kola/temp/nfs/nobackup/spot/machine_learning/impc_legacy_images'

# Get paths of all legacy images using solr
query = 'http://ves-ebi-d1.ebi.ac.uk:8988/solr/images/select?fl=fullResolutionFilePath,largeThumbnailFilePath,originalFileName,smallThumbnailFilePath&q=*:*&wt=json&indent=on&rows=10000000'

response = json.loads(requests.get(query).text)['response']['docs']
df_response = pd.DataFrame(response)

# Get the unique subdirectories - if a directory is missing from nfs we
# simply download all the referenced images from Sanger
#
# Otherwise we check that the directory contains all the images we expect
# and only download missing images

# Get directory name. Solr paths are images/dir_name/filename
# Get for individual columns in case dirs different!
split_paths = lambda s: s.split("/")[1]
dirs_from_solr = list(df_response['fullResolutionFilePath'].apply(split_paths).values)
dirs_from_solr.extend(
    list(df_response['largeThumbnailFilePath'].apply(split_paths).values))
dirs_from_solr.extend(
    list(df_response['smallThumbnailFilePath'].apply(split_paths).values))
solr_dirs = set(dirs_from_solr)

nfs_dirs = set(os.listdir(os.path.join(nfs_base_dir,'images')))

solr_dirs_not_in_nfs = list(solr_dirs.difference(nfs_dirs))
solr_dirs_in_nfs = list(solr_dirs.intersection(nfs_dirs))
n_solr_dirs = len(solr_dirs)
n_solr_dirs_not_in_nfs = len(solr_dirs_not_in_nfs)
n_solr_dirs_in_nfs = len(solr_dirs_in_nfs)

if n_solr_dirs_not_in_nfs + n_solr_dirs_in_nfs != n_solr_dirs:
    print("Number of dirs do not match! Expected {} got {}".format(
        n_solr_dirs, n_solr_dirs_not_in_nfs+n_solr_dirs_in_nfs))
    print(f"\tNumber of solr dirs in nfs: {n_solr_dirs_in_nfs}")
    print(f"\tNumber of solr dirs NOT in nfs: {n_solr_dirs_not_in_nfs}")
    print(f"\tTotal number of dirs in solr: {n_solr_dirs}")
# Get images for dirs missing in nfs
for d in solr_dirs_not_in_nfs:
    get_images_for_directory(d, df_response, output_dir, None)

# Get missing images from dirs present in both nfs and solr
for d in solr_dirs_in_nfs:
    get_images_for_directory(d, df_response, output_dir, nfs_base_dir)
