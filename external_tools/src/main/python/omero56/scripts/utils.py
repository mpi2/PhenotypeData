"""Utilities to help with interacting with Omero server and managing images

"""
from pathlib import Path
import requests
import json
import pandas as pd

# For reading Dicom images
import numpy as np
import SimpleITK as sitk


def get_properties_from_config_server(server, port, name, profile):
    """Get properties from EBI config server
    
    """

    # The configuration server is accessed in the form:
    # server:port/name/profile
    # e.g. server =  wp-np2-8a.ebi.ac.uk
    #      port   =  8888
    #      name   =  pa
    #      profile = dev

    url = f"http://{server}:{port}/{name}/{profile}"
    response = requests.get(url)
    if response.status_code != 200:
        msg = "Cound not connect to {url}. Response code was {response.status_code}"
        raise Exception(msg)
        
    props = response.json()
    return props['propertySources'][0]['source']
    

def get_properties_from_configuration_file(filepath):
    """Get properties from a configuration file
    
    """
    
    return _parse_properties(Path(filepath).read_text())

def _parse_properties(str_props):
    """Given a string of properties parse to dictionary of key value pairs"""

    props = {}
    for line in str_props.split("\n"):
        line = line.strip()
        if len(line) == 0 or line[0] == '#':
            continue
        pos = line.find("=")
        if pos > 0:
            key = line[:pos]
            value = line[pos+1:]
            props[key] = value

    return props

def get_details_from_solr(solr_host="/wp-np2-e1.ebi.ac.uk",
                          port="8986",
                          core=None,
                          query_string="*:*&rows=10",
                          output_type="csv"):
    """Get details from solr as either json or csv

    Uses dev solr as default.
    """

    solr_url = "http://wp-np2-e1.ebi.ac.uk:8986/solr/impc_images/"
    request_url = f"http://{solr_host}:{port}/solr/{core}/select?{query_string}"
    response = requests.get(request_url)
    if response.status_code != 200:
        msg = f"Problem connecting to {request_url}."
        msg += f"Response code was {response.status_code}"
        if 'error' in response.json() and 'msg' in response.json()['error']:
            msg += f"\n\nError message: {response.json()['error']['msg']}"
        raise Exception(msg)
    
    response_data = response.json()['response']['docs']
    if output_type == "json":
        return response.json()
    elif output_type == "csv":
        return pd.DataFrame(response_data).to_csv(index=False)


def get_image_details_from_solr(solr_host="/wp-np2-e1.ebi.ac.uk",
                port="8986",
                core="impc_images",
                query_string=None,
                output_path="impc_images_from_solr.csv"):
    """Get image details from and save to file as csv

    """

    if query_string is None:
        query_string = "q=download_file_path:*mousephenotype.org*&" + \
                       f"fl={field_list}&" + \
                       f"fq=date_of_experiment:{date_range}"
        query_string = "fl=observation_id,download_file_path,phenotyping_center,pipeline_stable_id,procedure_stable_id,datasource_name,parameter_stable_id&fq=date_of_experiment:[2021-01-01T00:00:00Z%20TO%20NOW]&q=download_file_path:*mousephenotype.org*"

def read_dicom(im_path):
    """Read Dicom images

    Helper function to read Dicom images
    Arguments:
      im_path: the path to the image to be read
    
    Returns:
      Image as a numpy array with dimension 3
    """
    
    im = sitk.ReadImage(im_path)
    im = np.squeeze(sitk.GetArrayFromImage(im))
    # Handle issue that some png images have an alpha layer -> 4 planes
    if im.ndim == 3 and im.shape[2] > 3:
        im = im[:,:,:3]
    im = im.astype(np.float)*255./im.max()
    return im.astype(np.uint8)


