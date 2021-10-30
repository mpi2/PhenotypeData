"""
    Upload images to omero.

    This version of OmeroUpload uses a csv file to get the download paths -
    as opposed to querying the solr database. For each download path it
    checks whether it already exists in Omero. If it does not then it
    checks whether it exists in the IMPC file system. If present in the
    file system it is uploaded to Omero.
"""
import os
from pathlib import Path
import sys
import argparse
import glob
import logging
import csv

from omeroservice import OmeroService
from utils import get_properties_from_config_server
from utils import get_properties_from_configuration_file


parser = argparse.ArgumentParser(
    description='Upload images to omero'
)
parser.add_argument('-i', '--input-file-path', required=True,
                    help='Path to CSV file containing images info'
)
parser.add_argument('-d', '--root-destination-dir',
                    help='Root of directory were downloaded to'
)
parser.add_argument('-o', '--omero-host',
                    help='Hostname for server hosting omero instance'
)
parser.add_argument('--omero-db-user',
                    help='name of the omero user for postgres')
parser.add_argument('--omero-db-pass',
                    help='Password of the omero postgress user')
parser.add_argument('--omero-db-name',
                    help='Name of the postgres database omero uses')
parser.add_argument('--omero-db-host',
                    help='Hostname of server hosting the omero postgres db')
parser.add_argument('--omero-db-port', default='5432',
                    help='Port to connect to the postgres server')
parser.add_argument('--config-server-host', default='wp-np2-8a.ebi.ac.uk',
                    help='Hostname for the configuration server')
parser.add_argument('--config-server-port', default='8888',
                    help='Port for the configuration server')
parser.add_argument('--config-server-name', default='omero-k8s',
                    help='Name to query in the config server')
parser.add_argument('--config-server-profile', default='dev',
                    help='profile from which to read config: dev, prod, live, ...')
parser.add_argument('--profile-path',
                    help='Explicit path to file from which to read ' + \
                         'profile e.g. ' + \
                         '/home/kola/configurations/dev/omero-dev.properties. ' + \
                         'Overrides value of all --config-server arguments.'
)
parser.add_argument('--split-string', default="impc/",
                    help='string to use to split image paths to allow ' + \
                         'parsing of pipeline,procedure,parameter,filename')
parser.add_argument('--logfile-path', dest='logfilePath', default=None,
                    help='path to save logfile')

args = parser.parse_args()

# Configure logger - if logging output file not specified create in this
# directory with timestamp
if args.logfilePath is None or args.logfilePath=="":
    import time
    import datetime
    t = time.time()
    tstamp = datetime.datetime.fromtimestamp(t).strftime('%Y%m%d_%H%M%S')
    logfile_path = "omeroupload_" + tstamp + ".log"
else:
    logfile_path = args.logfilePath

log_format = '%(asctime)s - %(name)s - %(levelname)s:%(message)s'
logging.basicConfig(format=log_format, filename=logfile_path,
                    level=logging.INFO)
log_formatter = logging.Formatter(log_format)
logger = logging.getLogger('OmeroUploadMainMethod')
root_logger = logging.getLogger()

console_handler = logging.StreamHandler()
console_handler.setFormatter(log_formatter)
root_logger.addHandler(console_handler)

# Get values from property file or profile and use as defaults that can be
# overridden by command line parameters
if args.profile_path is not None:
    try:
        profile_path = args.profile_path
        omero_props = get_properties_from_configuration_file(profile_path)
    except Exception as e:
        logger.error(f"Could not read application properties file from {profile_path}")
        logger.error(f"Error was: {e}")
        sys.exit(-1)
else:
    try:
        omero_props =  get_properties_from_config_server(
                            server=args.config_server_host,
                            port=args.config_server_port,
                            name=args.config_server_name,
                            profile=args.config_server_profile)
    except Exception as e:
        logger.error("Could not read application properties file for profile " + args.config_server_profile)
        logger.error(f"Error was: {e}")
        sys.exit(-1)

try:
    root_dir = args.root_destination_dir if args.root_destination_dir is not None else omero_props['rootdestinationdir']
    omero_host = args.omero_host if args.omero_host is not None else omero_props['omerohost']
except Exception as e:
    logger.exception("Could not assign some properties expected as command line arguments or in application.properties file - did you specify the right profile? Error message was: " + str(e))

try:
    omero_port = omero_props['omeroport']
    omero_username = omero_props['omerouser']
    omero_pass = omero_props['omeropass']
    omero_group = omero_props['omerogroup']
except Exception as e:
    logger.error("Could not assign omero login properties from application.properties file - did you specify the right profile?")
    logger.error("Error was: " + str(e))
    sys.exit(-1)

# Assuming files to exclude is taken care of in solrquery
files_to_exclude = ['.fcs','.mov','.bz2','.nrrd']
# Upload whole dir if it contains more than this number of files
load_whole_dir_threshold = 300

logger.info("running main intelligent omero upload method")
logger.info('rootDestinationDir is "' + root_dir + '"')


# Get records from the CSV file
# Assume rows  headings are as follows:
#     0 - observation_id
#     1 - download_file_path
#     2 - phenotyping_center
#     3 - pipeline_stable_id
#     4 - procedure_stable_id
#     5 - datasource_name
#     6 - parameter_stable_id
#     7 - checksum - this was a sha256 checksum at time code was written

csv_directory_to_filenames_map = {}
n_from_csv_file = 0
with open(args.input_file_path, 'r') as fid:
    csv_reader = csv.reader(fid)

    # Row heading order changed 24/03/2021. Best not to assume
    # Find column with necessary indicies from header
    header_row = next(csv_reader)
    try:
        download_file_path_idx = header_row.index("download_file_path")
        phenotyping_center_idx = header_row.index("phenotyping_center")
        pipeline_stable_idx = header_row.index("pipeline_stable_id")
        procedure_stable_idx = header_row.index("procedure_stable_id")
        parameter_stable_idx = header_row.index("parameter_stable_id")
        checksum_idx = header_row.index("checksum")
    except ValueError as e:
        print( "Fatal Error:")
        print( str(e), header_row)
        print( "Exiting")
        sys.exit(-1)

    for row in csv_reader:
        download_file_path=row[download_file_path_idx].lower()
        if download_file_path.find('mousephenotype.org') < 0 or \
                download_file_path.endswith('.mov') or \
                download_file_path.endswith('.fcs') or \
                download_file_path.endswith('.nrrd') or \
                download_file_path.endswith('.bz2') or \
                download_file_path.endswith('.arf'):
            continue

        phenotyping_center = row[phenotyping_center_idx]
        pipeline_stable_id = row[pipeline_stable_idx]
        procedure_stable_id = row[procedure_stable_idx]
        parameter_stable_id = row[parameter_stable_idx]
        checksum = row[checksum_idx]
        if len(phenotyping_center) == 0 or \
           len(pipeline_stable_id) == 0 or \
           len(procedure_stable_id) == 0 or \
           len(parameter_stable_id) == 0 or \
           len(checksum) == 0:

            print ("Did not receive a required field - " + \
                  "phenotyping_center='" + phenotyping_center + \
                  "', pipeline_stable_id='" + pipeline_stable_id + \
                  "', procedure_stable_id='" + procedure_stable_id + \
                  "', parameter_stable_id='" + parameter_stable_id + \
                  "', checksum='" + checksum + \
                  "' - not uploading: " + download_file_path)
            continue
        fname = checksum + os.path.splitext(download_file_path)[-1]
        key = os.path.join(phenotyping_center,pipeline_stable_id,procedure_stable_id,parameter_stable_id,fname)
        csv_directory_to_filenames_map[key] = download_file_path
        n_from_csv_file += 1
logger.info("Number of uploadable records returned from CSV file: " + str(n_from_csv_file))

# Get images from Omero
# Sometimes omero on the server throws an ICE memory limit exception. In that case go directly
# via postgres. This may return more records than going via omero, but that should not
# be a problem.
omeroS = OmeroService(omero_host, omero_port, omero_username, omero_pass, omero_group, args.split_string)
try:
    omero_file_list = omeroS.getImagesAlreadyInOmero()
except Exception as e: # TODO: Use exact exception here. It's something like ::Ice::MemoryLimitException
    logger.warn("Problem attempting to get images from omero. Attempting via Postgres")
    logger.warn("Exception message was " + str(e))
    omero_file_list = omeroS.getImagesAlreadyInOmeroViaPostgres(omero_props)
logger.info("Number of files from omero = " + str(len(omero_file_list)))

try:
    omero_annotation_list = omeroS.getAnnotationsAlreadyInOmero()
except Exception as e: # TODO: Use exact exception here. It's something like ::Ice::MemoryLimitException
    logger.warn("Problem attempting to get annotations from omero. Attempting via Postgres")
    logger.warn("Exception message was " + str(e))
    omero_annotation_list = omeroS.getAnnotationsAlreadyInOmeroViaPostgres(omero_props)
logger.info("Number of annotations from omero = " + str(len(omero_annotation_list)))
omero_file_list.extend(omero_annotation_list)
omero_dir_list = list(set([os.path.split(f)[0] for f in omero_file_list]))

# Get the files in NFS
list_nfs_filenames = []
list_nfs_filenames = [str(f).split(root_dir)[-1] for f in Path(root_dir).rglob("*.*")]
logger.info("Number of files from NFS = " + str(len(list_nfs_filenames)))
# Modified to carry out case-insensitive comparisons.
set_csv_filenames = set([k.lower() for k in csv_directory_to_filenames_map.keys()])
set_omero_filenames = set([f.lower() for f in omero_file_list])
dict_nfs_filenames = {}
for f in list_nfs_filenames:
    # Note that if more than one file maps to the same case insensitive value
    # only the last one encountered will be used
    dict_nfs_filenames[f.lower()] = f
set_nfs_filenames = set(dict_nfs_filenames.keys())
#print(f"In NFS: {set_nfs_filenames}")

files_to_upload = set_csv_filenames - set_omero_filenames
files_to_upload_available = files_to_upload.intersection(set_nfs_filenames)
files_to_upload_unavailable = files_to_upload - files_to_upload_available

# Create a dictionary for the files to upload with the directory as the
# key and the original nfs filenames as the values, so each dir can be passed to
# omero with associated files
dict_files_to_upload = {}
for f in files_to_upload_available:
    dirname, filename = os.path.split(dict_nfs_filenames[f])
    if dirname in dict_files_to_upload:
        dict_files_to_upload[dirname].append(filename)
    else:
        dict_files_to_upload[dirname] = [filename]

# Upload files
n_dirs_to_upload = len(dict_files_to_upload)
for index, directory in zip(range(n_dirs_to_upload),dict_files_to_upload.keys()):
    filenames = dict_files_to_upload[directory]
    n_files_to_upload = len(filenames)
    logger.info("About to upload directory " + str(index+1) + " of " + \
        str(n_dirs_to_upload) + ". Dir name: " + directory + \
        " with " + str(n_files_to_upload) + " files")
    dir_structure = directory.split('/')
    project = dir_structure[0]
    # Below we assume dir_structure is list with elements:
    # [project, pipeline, procedure, parameter]
    dataset = "-".join(dir_structure)
    fullpath = os.path.join(root_dir, directory)

    # if dir contains pdf file we cannot load whole directory
    if len(glob.glob(os.path.join(fullpath,'*.pdf'))) > 0:
        logger.info("##### Dir contains pdfs - loading file by file #####")
        omeroS.loadFileOrDir(fullpath, project=project, dataset=dataset, filenames=filenames)
    else:
        # Check if the dir is in omero.
        # If not we can import the whole dir irrespective of number of files
        dir_not_in_omero = True
        try:
            if omero_dir_list.index(directory) >= 0:
                dir_not_in_omero = False
        except ValueError:
            pass

        if dir_not_in_omero or n_files_to_upload > load_whole_dir_threshold:
            logger.info("##### Loading whole directory #####")
            omeroS.loadFileOrDir(fullpath, project=project, dataset=dataset, filenames=None)
        else:
            logger.info("##### Loading file by file #####")
            omeroS.loadFileOrDir(fullpath, project=project, dataset=dataset, filenames=filenames)

n_files_to_upload_unavailable = len(files_to_upload_unavailable)
logger.warning("Number of files unavailable for upload (not in NFS): " + \
    str(n_files_to_upload_unavailable))
if n_files_to_upload_unavailable > 0:
    file_list = ""
    for i, f in zip(range(n_files_to_upload_unavailable), files_to_upload_unavailable):
        file_list += '\n' + f
        if i > 99:
            break
    message = "The following files (converted to lower case and " + \
        "truncated at 100) were present in CSV but absent in NFS:" + \
        file_list
    logger.warning(message)
