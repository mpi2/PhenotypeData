#! /bin/bash

# Wrapper to set off the models for each site with the run_models bash
# script

# Needs user to pass in data-release to apply QC to
# e.g. ./qc_run_wrapper.sh 14 for data-release 14

if [ "$#" -ne 1 ]
then
    echo "Need the data-release e.g. $0 14 (for data-release 14)"
    exit 1
fi

DR=$1

./run_qc_apply_all_sites_model.sh BCM 034,048,049,050,051 ${DR} LA
./run_qc_apply_all_sites_model.sh BCM 034,048,049,050,051 ${DR}
./run_qc_apply_all_sites_model.sh CCP-IMG 034,048,049,050,051,052 ${DR}
./run_qc_apply_all_sites_model.sh HMGU 034,048 ${DR} LA
./run_qc_apply_all_sites_model.sh HMGU 034,048 ${DR}
./run_qc_apply_all_sites_model.sh HRWL 034,048,049,050,051 ${DR} LA
./run_qc_apply_all_sites_model.sh HRWL 034,048,049,050,051,052 ${DR}
./run_qc_apply_all_sites_model.sh ICS 034,048 ${DR} LA
./run_qc_apply_all_sites_model.sh ICS 034,048 ${DR}
./run_qc_apply_all_sites_model.sh JAX 034,048,049,050,051 ${DR}
./run_qc_apply_all_sites_model.sh KMPC 034,048,049,050,051,052 ${DR}
./run_qc_apply_all_sites_model.sh KMPC 034,048,049,050,051,052 ${DR} LA
./run_qc_apply_all_sites_model.sh RBRC 034,048,049,050,051,052 ${DR}
./run_qc_apply_all_sites_model.sh TCP 034,048,050,051,052 ${DR}
./run_qc_apply_all_sites_model.sh TCP 034,048,050,051 ${DR} LA
./run_qc_apply_all_sites_model.sh UCD 034,048,049,050,051,052 ${DR}
./run_qc_apply_all_sites_model.sh UCD 034,048 ${DR} LA
