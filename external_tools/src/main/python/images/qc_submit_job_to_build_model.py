"""Submit jobs to build models for a particular site
    Submit jobs to build models for a particular site. Each model should
    have a model_description.json file.

    Assumes names of models end in:
        all_structures_model
        forepaw_model
        head_dorsal_model
        head_lateral_model
        whole_body_dorsal_model
        whole_body_lateral_model
        hind_leg_hip_model
"""

import os
import argparse

parser = argparse.ArgumentParser(
    description="Submit jobs to build models for particular site"
)

parser.add_argument(
    '-s', '--site', dest='site', required=True,
    help="Site to build model(s) for"
)
parser.add_argument(
    '-m', '--models', dest='models', required=True,
    help="Comma separated list of models to build"
)

expected_model_names = [
    'all_structures_model',
    'forepaw_model',
    'head_dorsal_model',
    'head_lateral_model',
    'whole_body_dorsal_model',
    'whole_body_lateral_model',
    'hind_leg_hip_model',
]
args = parser.parse_args()
site = args.site
models = args.models.split(",")

for model in models:
    #try:
    #    expected_model_names.index(model)
    #except ValueError as e:
    #    print(f"{model} is not a valid model name - skipping!")
    #    continue

    model_description_path = f"/nfs/nobackup/spot/machine_learning/impc_mouse_xrays/quality_control_separate_sites/qc_models/{site}/{site}_{model}.json"
    if not os.path.isfile(model_description_path):
        print(f"Cannot find {model_description_path} - skipping!")
        continue
    model_command = f"python /nfs/nobackup/spot/machine_learning/impc_mouse_xrays/quality_control_separate_sites/code/qc_train_model.py -m {model_description_path}"
    submit_command = f"bsub -J {site}_{model}  -M 15000 -R \"rusage[mem=15000]\" -q research-rh74 -P gpu -gpu \"num=1:j_exclusive=yes\" bash -c 'source activate pytorch_cuda92;export QT_QPA_PLATFORM=\"offscreen\"; {model_command}'"
    retval = os.system(submit_command)
    if retval != 0:
        print(f"There was an error submitting job to build {site}_{model}")
    else:
        print(f"Submitted job to build {site}_{model}")

