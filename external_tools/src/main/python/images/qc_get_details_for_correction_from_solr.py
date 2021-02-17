# coding: utf-8

""" Script to get details for images to correct from Solr
    This script queries the experiment and impc_images cores and uses
    details of the QC'd processed files to filter images whose details need
    correction. It writes out the necessary details to send to the DCC to allow
    correction.
"""

import sys
import os
from pathlib import Path
import argparse
import requests
import json
from datetime import datetime

import pandas as pd

from QuerySolr import QuerySolr
from qc_mappings import parameter_id_to_class_map, sites_nospaces_to_spaces_map
from qc_mappings import CLASS_TO_PARAMETER_ID_MAP

def _parameter_from_class(classlabel, prefix="IMPC"): 
    try: 
        return prefix + "_XRY_" + \
            CLASS_TO_PARAMETER_ID_MAP[int(classlabel)]+"_001" 
    except (ValueError, KeyError,): 
        return "UNKNOWN_PARAMETER_STABLE_ID" 
    except: 
        return "PARAMETER_MAP_ERROR"

# Parameters for this run
parser = argparse.ArgumentParser(
    description = "Get details for images with wrong parameter stable IDs"
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
    '-d', '--base-dir', dest='dir_base', required=True, 
    help='Base directory for location of images'
)
parser.add_argument(
    '-o', '--output-base-dir', dest='output_base_dir', default="/nfs/nobackup/spot/machine_learning/impc_mouse_xrays/quality_control_all_sites/images_to_classify/",
    help='Base directory for reading and writing files associated with prediction. There must be an "output" subdirectory here containing processed files by SITE'
)
parser.add_argument(
    '--observation-id-path', dest='observation_id_path', required=True,
    help='path to csv containing observation IDs. This is normally ' + \
         'provided by Federico at the start of the data-release'
)
parser.add_argument(
    '--nchunks', dest='nchunks', default=100, type=int,
    help='Number of observation IDs to process with each solr query'
)

args = parser.parse_args()
phenotyping_center = args.site_name;
parameter_stable_id = args.parameter_stable_id
dir_base = args.dir_base

# Read in file with observation_ids
df_obs = pd.read_csv(args.observation_id_path)
# ToDo use observation_id as index once this is included in csv for QC
# df_obs.set_index('observation_id', drop=False, inplace=True)

# Create object to use for querying Solr impc_images and expt cores
query_solr_images = QuerySolr(host=args.host, port=args.port)
query_solr_images.set_core('solr/impc_images')
field_list_images = ",".join([
    'download_file_path',
    'increment_value',
])
query_solr_expt = QuerySolr(host=args.host, port=args.port)
query_solr_expt.set_core('solr/experiment')
field_list_expt = ",".join([
    "age_in_days", "age_in_weeks", "allele_accession_id", "allele_symbol",
    "allelic_composition", "biological_model_id", "biological_sample_group",
    "biological_sample_id", "colony_id", "datasource_name", "date_of_birth",
    "date_of_experiment", "developmental_stage_acc", "developmental_stage_name",
    "download_file_path", "experiment_id", "experiment_source_id", 
    "external_sample_id", "gene_accession_id", "gene_symbol",
    "genetic_background", "id", "litter_id", "parameter_name",
    "parameter_stable_id", "phenotyping_center", "phenotyping_center_id",
    "pipeline_name", "pipeline_stable_id", "procedure_group", "procedure_name",
    "procedure_stable_id", "production_center", "project_name", "sex",
    "strain_accession_id", "strain_name", "weight", "weight_date",
    "weight_days_old", "weight_parameter_stable_id", "zygosity",
])

# If No phenotyping centre specified we need to run over all phenotyping
# centers
phenotyping_centers = []
if args.site_name is not None:
    phenotyping_centers.append(args.site_name)
else:
    # get all phenotyping centres from output base directory
    # directories of interest are named SITE_PROC_XRY_PID_001
    phenotyping_centers_set = set()
    for p in Path(dir_base).glob("*_XRY_???_001"):
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
        pattern = f"{phenotyping_center}{parameter_stable_id}"
        for p in Path(dir_base).glob(pattern):
            pid = "_".join(p.name.split("_")[1:])
            if phenotyping_center in parameter_stable_ids:
                parameter_stable_ids[phenotyping_center].append(pid)
            else:
                parameter_stable_ids[phenotyping_center] = [pid,]

# For each phenotyping center, go through parameters and get details where
# there are images whose parameter IDs need correcting
for phenotyping_center, pids in parameter_stable_ids.items():
    if phenotyping_center == "HRWL" or phenotyping_center == "HRWLLA":
        fname_suffix = "3_structures_plus_hind_leg_hip_processed.csv"
    else:
        fname_suffix = "all_structures_processed.csv"

    phenotyping_center_spaces = sites_nospaces_to_spaces_map(phenotyping_center)

    for pid in pids:
        dir_name = f"{phenotyping_center}_{pid}"
        fname = f"{dir_name}_{fname_suffix}"
        fpath = os.path.join(dir_base,dir_name, fname)
        df = pd.read_csv(fpath)

        #   1) Check if any images were misclassified
        expected_class_label = parameter_id_to_class_map(pid)
        df_wrong = df[df['verified_classlabel'] != expected_class_label]
        if len(df_wrong) == 0:
            print(f"Nothing to process for {dir_name}")
            continue

        #   2) Extract the observation IDs for this parameter stable ID
        df_obs_temp = df_obs[(df_obs['phenotyping_center']==phenotyping_center_spaces) &  (df_obs['parameter_stable_id']==pid)]
        # ToDo change index to observation ID once this is included in QC data
        df_obs_temp['key'] = df_obs_temp['download_file_path'].map(lambda s: os.path.basename(s))
        df_obs_temp.set_index('key', inplace=True)
        df_wrong['key'] = df_wrong['imagename'].map(lambda s: os.path.basename(s))
        df_wrong.set_index('key', inplace=True)
        df_wrong['observation_id'] = df_obs_temp.loc[df_wrong.index]['observation_id']

        #   3) Get details from experiment core
        df_results = None
        observation_ids = df_wrong['observation_id'].values.tolist()
        n_observation_ids = len(observation_ids)
        for start_ind in range(0, n_observation_ids, args.nchunks):
            end_ind = min(start_ind+args.nchunks, n_observation_ids)
            q_obs_id = " ".join(observation_ids[start_ind:end_ind])
            #query = f"select?fl={field_list_expt}&q=parameter_stable_id:{parameter_stable_id}%20AND%20phenotyping_center:{phenotyping_center_spaces}&rows=10000000"
            query = f"select?q=observation_id:({q_obs_id})&rows={args.nchunks}"
            #query = query.replace(" ","\ ")
            query_solr_expt.set_query(query)
            results_expt = query_solr_expt.run_query()
            if df_results is None:
                df_results = results_expt.copy()
            else:
                df_results = pd.concat((df_results, results_expt))
        if len(df_results) == 0:
            print(f"No expt core details for {phenotyping_center}:{parameter_stable_id}")
            continue


        #   4) Get correct parameter stable ID
        df_wrong.set_index('observation_id', inplace=True)
        df_results.set_index('observation_id', drop=False, inplace=True)
        df_results['correct_parameter_stable_id'] = df_wrong.loc[df_results.index]['verified_classlabel'].map(lambda s: _parameter_from_class(s))
        #   5) Save details
        df_results.sort_values('observation_id', inplace=True)
        fpath = os.path.join(args.output_base_dir, fname)
        df_results.to_csv(fpath, index=False)

