"""Get image details from solr

Script to allow using solr to create an input csv file for omero upload. I
wrote this mainly to allow me to get images by date ranges when recreating
omero with version 5.6 and namespaced by data-release.
"""

from pathlib import Path
import argparse
import re
from utils import get_details_from_solr

parser = argparse.ArgumentParser(
    description="Get image details from solr with optional date range",
)
parser.add_argument("-o", "--output-path", required=True,
                    help="path to write output")
parser.add_argument("-d", "--date-range",
                    help="date range 20200101-20211231 i.e. YYYYMMDD")
parser.add_argument("-n", "--nrows", default="9999999",
                    help="Number of rows to retrieve. Default is all rows")
args = parser.parse_args()

# Format the date range in solr form if specified
if args.date_range is not None:
    regex = re.compile("(\d{8})-(\d{8})")
    matches = regex.match(args.date_range)
    if matches is None or len(matches.groups()) != 2:
        err_msg = "Invalid date range need YYYYMMDD-YYYYMMDD. " + \
                 f"e.g. 20200101-20211231. Got {args.date_range}"
        raise ValueError(err_msg)
    match1 = matches.groups()[0]
    match2 = matches.groups()[1]
    start_date = f"{match1[:4]}-{match1[4:6]}-{match1[6:]}"
    end_date = f"{match2[:4]}-{match2[4:6]}-{match2[6:]}"
    date_range = f"[{start_date}T00:00:00Z%20TO%20{end_date}T00:00:00Z]"
else:
    date_range = None

# Create query adding args from command line
field_list = "observation_id,download_file_path,phenotyping_center," + \
             "pipeline_stable_id,procedure_stable_id,datasource_name," + \
             "parameter_stable_id"
query_string = f"q=download_file_path:*mousephenotype.org*&fl={field_list}"
if date_range is not None:
    query_string += f"&fq=date_of_experiment:{date_range}"
query_string += f"&rows={args.nrows}"

image_details = get_details_from_solr(solr_host="wp-np2-e1.ebi.ac.uk",
                port="8986",
                core="impc_images",
                query_string=query_string,
                output_type="csv")

try:
    Path(args.output_path).write_text(image_details, "utf-8")
except Exception as e:
    print(f"Problem writing output file to {args.output_path}. Error was: {e}")
