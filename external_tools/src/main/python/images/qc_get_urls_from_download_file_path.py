"""Get urls from nfs file paths
    This script uses download_file_paths from solr (imagename column in 
    csv file from xray QC) to obtain the download_urls in the csv file 
    provided by Federico.
    The download_file_paths are nfs paths of the form:
        impc_base_dir/site/pipeline_stable_id/
        procedure_stable_id/parameter_stable_id/filename 
    These need to be mapped to the download_urls for DCC.
"""

from pathlib import Path
import argparse
import pandas as pd
from qc_mappings import PARAMETER_ID_TO_CLASS_MAP, CLASS_TO_PARAMETER_ID_MAP

# Local function to get parameters using class labels
def _parameter_from_class(classlabel):
    try:
        return "IMPC_XRY_"+CLASS_TO_PARAMETER_ID_MAP[int(classlabel)]+"_001"
    except (ValueError, KeyError,):
        return "UNKNOWN_PARAMETER_STABLE_ID"
    except:
        return "PARAMETER_MAP_ERROR"

parser = argparse.ArgumentParser(
    description = "Get urls from nfs file paths"
)
parser.add_argument(
    '-u', '--url-csv-path', dest='url_csv_path', required=True,
    help='path to csv containing urls. This is normally provided by ' +\
         'Federico at the start of the data-release'
)
parser.add_argument(
    '-i', '--input-base-dir', dest='input_base_dir', required=True,
    help='Base directory containing verified QC files'
)
parser.add_argument(
    '-o', '--output-base-dir', dest='output_base_dir',
    help='Directory to store output containing mapped csvs. Defauts to ' +\
         'input-base-dir if not supplied'
)

args = parser.parse_args()

# Read in file with urls and create key using pipeline, procedure, parameter and filename
df_urls = pd.read_csv(args.url_csv_path)

df_urls['key'] = df_urls['pipeline_stable_id'] + df_urls['procedure_stable_id'] + df_urls['parameter_stable_id'] + df_urls['download_file_path'].map(lambda x: x.split('/')[-1])
df_urls = df_urls.set_index('key')

input_base_dir = Path(args.input_base_dir)
if args.output_base_dir is None:
    output_base_dir = Path(args.input_base_dir)
else:
    output_base_dir = Path(args.output_base_dir)

to_process = [str(p) for p in input_base_dir.glob('**/*structures*processed.csv')]
for fpath in to_process:
    fname = fpath.split('/')[-1]
    df = pd.read_csv(fpath)
    if 'verified_classlabel' not in df.columns:
        print(f"No 'verified classlabel' column in {fname} - not processing")
        continue
        
    # Filter out records with wrong parameter IDs
    parameter_id = fname.split('_')[3]
    expected_label = PARAMETER_ID_TO_CLASS_MAP[parameter_id]
    df = df[df['verified_classlabel'] != expected_label]
    if len(df) == 0:
        print(f"No incorrectly annotated images for {fname}")
        continue
    df['key'] = df['imagename'].map(lambda s: "".join(s.split('/')[-4:]))
    df.set_index('key', inplace=True)
    df['download_file_path'] = df_urls.loc[df.index]['download_file_path']
    df['correct_parameter_id'] = \
        df['verified_classlabel'].map(lambda x: _parameter_from_class(x))
    df.sort_values('correct_parameter_id', inplace=True)
    out_fname = fname[:-4]+"_url.csv"
    out_path = output_base_dir.joinpath(out_fname)
    df.to_csv(out_path, index=False, columns=['download_file_path', 'correct_parameter_id'])
    print(f"Written output to {out_path}")
    

