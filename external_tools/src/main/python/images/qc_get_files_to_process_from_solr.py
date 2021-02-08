# coding: utf-8

""" Script to get files to process from Solr images core
    This script queries a solr core to get the files to apply QC to
"""

import sys
import os
import argparse
import requests
import json
from datetime import datetime

import pandas as pd

from QuerySolr import QuerySolr

# Helper function to change names of sites to standard format
# For the moment these are UC Davies and MRC Harwell.
def sanitise_center_name(input_name):
    name_map = {
        "UC Davis": "UCD",
        "UC_Davis": "UCD",
        "MRC Harwell": "HRWL",
        "MRC_Harwell": "HRWL",
    }

    for key, value in name_map.items():
        input_name = input_name.replace(key, value)
    return input_name


# Parameters for this run
parser = argparse.ArgumentParser(
    description = "Get files to process from Solr images core"
)
parser.add_argument('-H', '--host', dest='host', 
                    default="http://ves-ebi-d0.ebi.ac.uk",
                    help='host to use for solr query - defaults to dev'
)
parser.add_argument('-P', '--port', dest='port', default='8986',
                    help='port to use for solr query'
)
parser.add_argument(
    '--site-name', dest='site_name', default=None,
    help='Abbreviated name of site (phenotyping_center) as in the ' +\
         'directory in images/clean. Do not use this parameter if you ' +\
         'want to run for all sites'
)
parser.add_argument(
    '--parameter-stable-id', dest='parameter_stable_id', default='*_XRY_*',
    help='Parameter stable ID. Do not use this parameter if you want all'
)
parser.add_argument(
    '-d', '--base-dir', dest='dir_base', default="/nfs/komp2/web/images/clean/impc/",
    help='Base directory for location of images'
)
parser.add_argument(
    '-c', '--code-dir', dest='code_dir', default="/nfs/komp2/web/image_qc/apply_dl_models/code/",
    help='Directory containing code to run models'
)
parser.add_argument(
    '-o', '--output-base-dir', dest='output_base_dir', default="/nfs/nobackup/spot/machine_learning/impc_mouse_xrays/quality_control_all_sites/images_to_classify/",
    help='Base directory for reading and writing files associated with prediction. Three subdirectories (output,logs and jobs) will be created here'
)
parser.add_argument(
    '--create-site-parameter-dirs', dest='create_site_parameter_dirs',
    type=bool, default=True,
    help='set flag to create subdirectories for each site/parameter combination'
)

args = parser.parse_args()
phenotyping_center = args.site_name;
parameter_stable_id = args.parameter_stable_id
dir_base = args.dir_base
code_dir = args.code_dir

# Create object to use for querying Solr
query_solr = QuerySolr(host=args.host, port=args.port)
query_solr.set_core('solr/impc_images')

# If No phenotyping centre specified we need to run over all phenotyping
# centers
phenotyping_centers = []
if args.site_name is not None:
    phenotyping_centers.append(args.site_name)
else:
    # get all phenotyping centres from Solr
    query = f"select?facet.field=phenotyping_center&facet=on&fl=phenotyping_center&q=parameter_stable_id:{parameter_stable_id}&rows=0&start=0"
    query_solr.set_query(query)
    results = query_solr.run_query(return_raw=True)
    results = results['facet_counts']['facet_fields']['phenotyping_center']
    for i in range(0, len(results), 2): # stride=2 as we have center,count
        if results[i+1] > 0:
            phenotyping_centers.append(results[i])
    
# get xry parameters if necessary
parameter_stable_ids = []
if parameter_stable_id.count('*') == 0:
    parameter_stable_ids.append(parameter_stable_id)
else:
    # get parameter_stable_ids from Solr
    query = f"select?facet.field=parameter_stable_id&facet=on&fl=parameter_stable_id&q=parameter_stable_id:{parameter_stable_id}&rows=0&start=0"
    query_solr.set_query(query)
    results = query_solr.run_query(return_raw=True)
    results = results['facet_counts']['facet_fields']['parameter_stable_id']
    for i in range(0, len(results), 2): # stride=2 as we have center,count
        if results[i+1] > 0:
            parameter_stable_ids.append(results[i])


# Create directories for files
output_dir_stem = os.path.join(args.output_base_dir,'output')
if not os.path.isdir(output_dir_stem):
    os.mkdir(output_dir_stem)

# For each center for each parameter
#   1) Generate list of files to process

for phenotyping_center in phenotyping_centers:
    # have version of phenotyping_center with no spaces
    #phenotyping_center_ns = phenotyping_center.replace(' ', '_')
    phenotyping_center_ns = sanitise_center_name(phenotyping_center)

    for parameter_stable_id in parameter_stable_ids:
        query = f"select?fl=pipeline_stable_id,procedure_stable_id,download_file_path&q=parameter_stable_id:{parameter_stable_id}%20AND%20phenotyping_center:{phenotyping_center}&rows=10000000"
        query = query.replace(" ","\ ")
        query_solr.set_query(query)
        results = query_solr.run_query()
        
        if len(results) == 0:
            print(f"No images for {phenotyping_center}:{parameter_stable_id}")
            continue

        files_to_process = []
        for index, row in results.iterrows():
            pipeline_stable_id = row['pipeline_stable_id']
            procedure_stable_id = row['procedure_stable_id']
            filename = os.path.split(row['download_file_path'])[-1]
            filepath = os.path.join(dir_base,phenotyping_center, pipeline_stable_id, procedure_stable_id, parameter_stable_id, filename)
            files_to_process.append(filepath+"\n")

        #output_filename = pipeline_stable_id.split('_')[0]+'_'+parameter_stable_id+'.txt'
        output_stem = phenotyping_center_ns + '_' + parameter_stable_id
        output_filename = output_stem + '.txt'

        # Create subdirectory for site/parameter combination if flag set
        if args.create_site_parameter_dirs:
            output_dir = os.path.join(output_dir_stem, output_stem)
            if not os.path.isdir(output_dir):
                os.mkdir(output_dir)
        else:
            output_dir = output_dir_stem
        output_filepath = os.path.join(output_dir, output_filename)
        with open(output_filepath, 'wt') as fid:
            fid.writelines(["imagename,classlabel\n"])
            fid.writelines(files_to_process)
