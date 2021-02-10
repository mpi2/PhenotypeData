# coding: utf-8

""" Script to compute figures of mis-annotated x-ray images
"""

import sys
import os
from pathlib import Path
import argparse

import pandas as pd

from qc_mappings import parameter_id_to_class_map, sites_nospaces_to_spaces_map


# Parameters for this run
parser = argparse.ArgumentParser(
    description = "Compute number of mis-annotated images"
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
    '-d', '--base-dir', dest='base_dir', required=True, 
    help='Base directory for location of images'
)
parser.add_argument(
    '-o', '--output-filename', dest='output_filename', default="qc_summary.csv",
    help='Name of file to write summary figures to'
)

args = parser.parse_args()
parameter_stable_id = args.parameter_stable_id
base_dir= args.base_dir

# If No phenotyping centre specified we need to run over all phenotyping
# centers
phenotyping_centers = []
if args.site_name is not None:
    phenotyping_centers.append(args.site_name)
else:
    # get all phenotyping centres from output base directory
    # directories of interest are named SITE_PROC_XRY_PID_001
    phenotyping_centers_set = set()
    for p in Path(base_dir).glob("*_XRY_???_001"):
        phenotyping_centers_set.add(p.name.split("_")[0])

    phenotyping_centers = list(phenotyping_centers_set)
    
# get xry parameters if necessary
parameter_stable_ids = {}
if parameter_stable_id.count('*') == 0:
    for phenotyping_center in phenotyping_centers:
        parameter_stable_ids[phenotyping_center] = [parameter_stable_id,]
else:
    # get parameter_stable_ids for each phenotyping center
    for phenotyping_center in phenotyping_centers:
        pattern = f"{phenotyping_center}_{parameter_stable_id}"
        for p in Path(base_dir).glob(pattern):
            pid = "_".join(p.name.split("_")[1:])
            if phenotyping_center in parameter_stable_ids:
                parameter_stable_ids[phenotyping_center].append(pid)
            else:
                parameter_stable_ids[phenotyping_center] = [pid,]

# For each phenotyping center, go through parameters and get details where
# there are images whose parameter IDs need correcting
summary_table = {}
col_label_set = set()
for phenotyping_center, pids in parameter_stable_ids.items():
    if phenotyping_center == "HRWL":
        fname_suffix = "3_structures_plus_hind_leg_hip_processed.csv"
    else:
        fname_suffix = "all_structures_processed.csv"
    phenotyping_center_spaces = sites_nospaces_to_spaces_map(phenotyping_center)

    for pid in pids:
        dir_name = f"{phenotyping_center}_{pid}"
        fname = f"{dir_name}_{fname_suffix}"
        fpath = os.path.join(base_dir,dir_name, fname)
        df = pd.read_csv(fpath)

        #Check if any images were misclassified
        expected_class_label = parameter_id_to_class_map(pid)
        df_wrong = df[df['verified_classlabel'] != expected_class_label]

        # Get row and column labels for this figure
        row_label = dir_name.split("_")[1]
        if row_label == "IMPC":
            row_label = dir_name.split("_")[0]
        col_label = dir_name.split("_")[3]
        col_label_set.add(col_label)

        # Update values
        if row_label not in summary_table:
            summary_table[row_label] = {}
        summary_table[row_label][col_label] = f"{len(df_wrong)}/{len(df)}"

row_labels = list(summary_table.keys())
row_labels.sort()
col_labels = list(col_label_set)
col_labels.sort()
summary = "Site," + ",".join(col_labels) + "\n"
for row_label in row_labels:
    summary_row = [row_label,]
    for col_label in col_labels:
        try:
            summary_row.append(summary_table[row_label][col_label])
        except KeyError:
            summary_row.append("-")
    summary += ",".join(summary_row) + "\n"
            
output_path = os.path.join(base_dir,args.output_filename)
with open(output_path,'wt') as fid:
    fid.write(summary)
print(f"Written summary to {output_path}")
