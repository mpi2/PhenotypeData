#!/bin/bash

# This script is based on run_qc_appl_all_sites_model.sh - runs only 
# montages. It is useful if the model results have been generated, but the
# montages need to be recreated


# Get parameters passed to string.
# Parameter 1 must be site (phenotyping centre). One of
# BCM, JAX, WTSI, KMPC, UCD, CCP-IMG, HMGU, ICS, RBRC, TCP or HRWL
#
# Parameter 2 must be csv of parameter IDs (three letters). Valid ones are
# 034,048,049,050,051,052
#
# Parameter 3 must be the data-release e.g. 13 for data-release 13
#
# Parameter 4 must be the string TRUE if processing Late Adult
if [ "$#" -lt 3 ] || [ "$#" -gt 4 ]
then
    echo "Need three args - SITE PARAMETER DATA-RELEASE e.g. $0 UCD 034,048 13"
    echo "Or four parameters for Late Adult e.g.  $0 UCD 034,048 13 LA"
    exit 1
fi


SITE=$1
# Set data-release!
DR=$3
LATE_ADULT=$4

# Convert parameter list to array using comma as internal field separator
OLD_IFS="${IFS}"
IFS=","
PIDS=(${2})
IFS="${OLD_IFS}"
#PIDS=(034 048 049 050 051 052)

# Lower case version
site="${SITE,,}"

OUTPUT_BASE_DIR="/nfs/nobackup/spot/machine_learning/impc_mouse_xrays/quality_control_separate_sites/dr_${DR}/output"
LOG_DIR="/nfs/nobackup/spot/machine_learning/impc_mouse_xrays/quality_control_separate_sites/dr_${DR}/logs"
MODEL_DIR="/nfs/nobackup/spot/machine_learning/impc_mouse_xrays/quality_control_separate_sites/qc_models_used_for_dr12"
CODE_DIR="/nfs/nobackup/spot/machine_learning/impc_mouse_xrays/quality_control_separate_sites/code"

# Assign structures depending on site
if [[ $SITE == "BCM" ]] || [[ $SITE == "JAX" ]] || [[ $SITE == "WTSI" ]]
then
    # BCM,JAX,WTSI - only missing hind_leg_hip
    STRUCTURES="all_structures,head_dorsal,head_lateral,whole_body_dorsal,whole_body_lateral,forepaw"

elif [[ $SITE == "KMPC" ]] || [[ $SITE == "UCD" ]] || [[ $SITE == "CCP-IMG" ]]
then
    # KMPC,UCD - all structures
    STRUCTURES="all_structures,head_dorsal,head_lateral,whole_body_dorsal,whole_body_lateral,hind_leg_hip,forepaw"

elif [[ $SITE == "HMGU" ]] || [[ $SITE == "ICS" ]] || [[ $SITE == "RBRC" ]]
then
    # HMGU,ICS,RBRC have only two structures. Therefore only the all_structures model for the time-being
    STRUCTURES="all_structures"

elif [[ $SITE == "TCP" ]]
then
    # TCP - only missing forepaw
    STRUCTURES="all_structures,head_dorsal,head_lateral,whole_body_dorsal,whole_body_lateral,hind_leg_hip"

elif [[ $SITE == "HRWL" ]]
then
    # Harwell is a special case with composite model of 3 structures plus hind leg hips
    STRUCTURES="3_structures_plus_hind_leg_hip,head_dorsal,whole_body_dorsal,whole_body_lateral,hind_leg_hip"

else
    echo "$SITE not recognised as a site - exiting"
    exit 1
fi

#for PID in 034 048 049 050 051 052
for PID in ${PIDS[@]};
do

    # Construct the parameter stable ID
    if [[ $LATE_ADULT == "LA" ]]
    then
        late_adult="la_"
        if [[ $SITE == "CCP-IMG" ]]
        then
            PARAMETER_STABLE_ID="CCPLA_XRY_${PID}_001"
        else
            PARAMETER_STABLE_ID="${SITE}LA_XRY_${PID}_001"
        fi
    else
        late_adult=""
        if [[ $SITE == "CCP-IMG" ]]
        then
            PARAMETER_STABLE_ID="CCP_XRY_${PID}_001"
        else
            PARAMETER_STABLE_ID="IMPC_XRY_${PID}_001"
        fi
    fi
    # Create array of files that contain results of models for each structure
    unset PROCESSED_STRUCTURES
    OLD_IFS="${IFS}"
    IFS=","
    STRUCTURES_ARR=(${STRUCTURES})
    for i in "${!STRUCTURES_ARR[@]}"; do
        PROCESSED_STRUCTURES[$i]="${SITE}_${PARAMETER_STABLE_ID}_${STRUCTURES_ARR[$i]}_processed.csv"
    done
    PROCESSED_STRUCTURES="${PROCESSED_STRUCTURES[*]}"
    IFS="${OLD_IFS}"

    #python run_qc_apply_all_sites_model.py --site-name "${site}" --parameter-stable-id "${PARAMETER_STABLE_ID}" -s "${STRUCTURES}" --output-base-dir "${OUTPUT_BASE_DIR}" -m "${MODEL_DIR}" -c "${CODE_DIR}"

    # TODO: Use a log dir to store output and error instead of email

    #bsub  -w "done(${site}_${PID}*)" -M 10000 -R "rusage[mem=10000]"  -J ${site}_${PID} bash -c 'source activate pytorch_cuda92; export QT_QPA_PLATFORM=offscreen; python /nfs/nobackup/spot/machine_learning/impc_mouse_xrays/quality_control_separate_sites/code/create_montage_to_display_classes.py --input-dir "${OUTPUT_BASE_DIR}"/"${SITE}"_"${PARAMETER_STABLE_ID}" -i "${PROCESSED_STRUCTURES}" -o /nfs/nobackup/spot/machine_learning/impc_mouse_xrays/quality_control_separate_sites/dr_12/output/"${SITE}"_"${PARAMETER_STABLE_ID}"/;'
    JOB_NAME="${site}_${late_adult}${PID}"
    bsub -M 10000 -R "rusage[mem=10000]"  -J ${JOB_NAME} -e ${LOG_DIR}/${JOB_NAME}.err -o ${LOG_DIR}/${JOB_NAME}.out bash -c 'source activate pytorch_cuda92; export QT_QPA_PLATFORM=offscreen; python '"${CODE_DIR}"'/create_montage_to_display_classes.py --input-dir '"${OUTPUT_BASE_DIR}"'/'"${SITE}"'_'"${PARAMETER_STABLE_ID}"' -i '"${PROCESSED_STRUCTURES}"' -o '"${OUTPUT_BASE_DIR}"'/'"${SITE}"'_'"${PARAMETER_STABLE_ID}"'/;'

    echo "Submitted jobs for SITE=${SITE}, PID=${PID}"
done

