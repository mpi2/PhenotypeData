# coding: utf-8

""" Script to run qc_apply_all_sites_model
    This script runs the QC models on images. It will normally be run after
    a data release to carry out QC on images present in the images core.
    
    Before running it the text files containing the paths to the images to
    be processed must have been generated. See 
    qc_generate_list_of_images_to_process.py

    This script is a wraper around qc_apply_all_sites_model.py
"""

import sys
import os
import argparse
import requests
import json
from datetime import datetime

import pandas as pd

# Parameters for this run
parser = argparse.ArgumentParser(
    description = "Apply QC models to images"
)
parser.add_argument(
    '--site-name', dest='site_name', required=True,
    help='Abbreviated name of site (phenotyping_center) as in the ' +\
         'directory in images/clean'
)
parser.add_argument(
    '--parameter-stable-id', dest='parameter_stable_id', required=True,
    help='Parameter stable ID.'
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
    '-m', '--model-dir-base', dest='model_dir_base', required=True,
    help="Path to directory containing models to use for predictions"
)
parser.add_argument(
    '--cluster-login-node', dest='cluster_login_node', default='ebi-login',
    help='Node from which cluster jobs are submitted'
)
parser.add_argument(
    '-s', '--structures', dest='structures', required=True,
    help='comma separated list of structures whose models will be applied'
)
parser.add_argument(
    '-u', '--user-email', dest='user_email', default='kola@ebi.ac.uk',
    help='email to which notification of LSF jobs will be sent.'
)

args = parser.parse_args()
print_every = args.print_every
site_name_lc = args.site_name.lower();
site_name_uc = args.site_name.upper();
parameter_stable_id = args.parameter_stable_id.upper()
pid = parameter_stable_id.split("_")[-2]
model_dir_base = args.model_dir_base
code_path = os.path.join(args.code_dir, "qc_apply_all_sites_model.py")

# If late adult include in job name.
if parameter_stable_id.find("LA_") >= 0:
    late_adult = "la_"
else:
    late_adult = ""


for structure in args.structures.split(","):
    job_name = f"{site_name_lc}_{late_adult}{pid}_{structure}"
    output_filename = "_".join([site_name_uc, parameter_stable_id, structure])
    model_desc_fname = "_".join([site_name_lc, structure, "model.json"])
    model_desc_path = os.path.join(model_dir_base, site_name_lc, model_desc_fname)
    output_dir = os.path.join(args.output_base_dir, site_name_uc+"_"+parameter_stable_id) 
    # Uncomment for Jenkins
    #command = f"bash -c 'source ~/conda_setup.sh; conda activate " + \
    #          "/nfs/production3/komp2/web/image_qc/code/python3;" + \
    #          f"python {code_path} --site-name {site_name_uc} " + \
    #          f"--parameter-stable-id {parameter_stable_id} -p -1 " + \
    #          f"-o {output_dir} -m {model_desc_path} " + \
    #          f"--output-filename {output_filename}'"
    command = f"bash -c 'source activate pytorch_cuda92;" + \
              f"python {code_path} --site-name {site_name_uc} " + \
              f"--parameter-stable-id {parameter_stable_id} -p -1 " + \
              f"-o {output_dir} -m {model_desc_path} " + \
              f"--output-filename {output_filename}'"
    #print(command)

    # Uncomment line below for use by Jenkins
    # Still need to add -o and -e ( -o {logs_dir}/{output_stem}.out -e {logs_dir}/{output_stem}.err {output_filepath} )
    #submit_command = f'ssh tc_mi01@{args.cluster_login_node} "bsub -u {args.user_email} -M 15000 -R  \\"rusage[mem=15000]\\" -J {job_name} {command}"'

    # Submit command below for my user name as I was having issues
    # running the commands to save images on /nfs/nobackup
    submit_command = f'bsub -M 10000 -R  "rusage[mem=10000]" -J {job_name} {command}'
    print(submit_command)
    retval = os.system(submit_command)
    if retval == 0:
        print(f"Submitted job for {job_name} successfully")
    else:
        print(f"Problem submitting job for {job_name}!")

