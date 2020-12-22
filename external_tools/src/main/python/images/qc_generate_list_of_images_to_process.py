# coding: utf-8

""" Script to run qc_apply_all_sites_model
    This script prepares required artifacts to allow running the QC on
    images. It will normally be run after a data release to carry out
    QC on images present in the images core. However, use can be customised
    for more specific purposes using the parameters

    This script is a wraper around qc_apply_all_sites_model.py
"""

import sys
import os
import argparse
import requests
import json
from datetime import datetime

import pandas as pd

from QuerySolr import QuerySolr


# Parameters for this run
parser = argparse.ArgumentParser(
    description = "Setup QC for sites and submit qc script to LSF"
)
parser.add_argument('-H', '--host', dest='host', 
                    default="ves-ebi-d0.ebi.ac.uk",
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
    '-p', '--print-every', dest='print_every', default=-1, type=int,
    help='Number of iterations before printing prediction stats note that this also saves the predictions up to this point which is useful incase the program crashes. Use -1 to prevent printing anything.'
)
parser.add_argument(
    '-o', '--output-base-dir', dest='output_base_dir', required=True,
    help='Base directory for reading and writing files associated with prediction. Three subdirectories (output,logs and jobs) will be created here'
)
parser.add_argument(
    '-q', '--queue-name', dest='queue_name', default='research-rh74',
    help='LSF queue for submitting jobs'
)
parser.add_argument(
    '-m', '--model-path', dest='model_path', required=True,
    help="Path to model to use for predictions"
)
parser.add_argument(
    '--cluster-login-node', dest='cluster_login_node', default='ebi-login',
    help='Node from which cluster jobs are submitted'
)
parser.add_argument(
    '--create-site-parameter-dirs', dest='create_site_parameter_dirs',
    type=bool, default=True,
    help='set flag to create subdirectories for each site/parameter combination'
)

args = parser.parse_args()
print_every = args.print_every
phenotyping_center = args.site_name;
parameter_stable_id = args.parameter_stable_id
dir_base = args.dir_base
code_dir = args.code_dir

# Create object to use for querying Solr
query_solr = QuerySolr(host=args.host, port=args.port)
query_solr.set_core('impc_images')

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
jobs_dir = os.path.join(args.output_base_dir,'jobs')
if not os.path.isdir(jobs_dir):
    os.mkdir(jobs_dir)
logs_dir = os.path.join(args.output_base_dir,'logs')
if not os.path.isdir(logs_dir):
    os.mkdir(logs_dir)

# For each center for each parameter
#   1) Generate list of files to process
#   2) Submit job to LSF

for phenotyping_center in phenotyping_centers:
    # have version of phenotyping_center with no spaces
    phenotyping_center_ns = phenotyping_center.replace(' ', '_')
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
        
        # Generate script to run QC and display results
        today = datetime.today().strftime("%d/%m/%Y_%H:%M:%S")
        postfix = datetime.today().strftime("%Y%m%d_%H%M%S")

        output_filename = output_stem + ".sh"
        output_filepath = os.path.join(jobs_dir, output_filename)
        output = [
            f"# Generated: {today}\n",
            "source ~/conda_setup.sh\n",
            "conda activate /nfs/production3/komp2/web/image_qc/code/python3\n",
            "export QT_QPA_PLATFORM='offscreen'\n",
            f"python {code_dir}/qc_apply_all_sites_model.py -m {args.model_path} -o {output_dir} -p -1 -d {dir_base} --parameter-stable-id {parameter_stable_id} --site-name {phenotyping_center_ns}\n",
            f"python {code_dir}/create_montage_to_display_classes.py -i {output_dir}/{output_stem}_processed.csv -o {output_dir}/",
        ]
        with open(output_filepath, 'wt') as fid:
            fid.writelines(output)
        os.system(f"chmod a+x {output_filepath}")
        print(f"Saved jobscript to {output_filepath}")

        # Submit to LSF
        submit_command = f'ssh tc_mi01@{args.cluster_login_node} "bsub -M 15000 -R  \\"rusage[mem=15000]\\" -q {args.queue_name} -J IMPC_qc_images_{output_stem}_{postfix} -o {logs_dir}/{output_stem}.out -e {logs_dir}/{output_stem}.err {output_filepath}"'
        print(submit_command)
        retval = os.system(submit_command)
        if retval == 0:
            print(f"Submitted job for {output_stem} successfully")
        else:
            print(f"Problem submitting job for {output_stem}!")

