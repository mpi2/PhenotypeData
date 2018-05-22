"""
    Upload .lei and .lif files from Leica microscope to Omero.
    The files are assumed to be stored in Directories depicting
    the dataset and fileset they should belong to.
    All the files in the directories will be loaded into Omero.
"""
import os
import sys
import argparse
import requests
import json
import pdb

import psycopg2

from OmeroService import OmeroService
from OmeroPropertiesParser import OmeroPropertiesParser

# Find all .lei and .lif files
def find_leica_files(basedir):
    fpaths = []
    for d, dnames, fnames in os.walk(basedir):
        for f in fnames:
            ext = os.path.splitext(f)[-1]
            if ext == '.lei' or ext == '.lif':
                fpaths.append(os.path.join(d,f))
    return fpaths


# For each file, if not already in omero - upload into omero.
parser = argparse.ArgumentParser(
    description='Run main intelligent omero upload method'
)
parser.add_argument('-d', '--rootDestinationDir', dest='rootDestinationDir',
                    help='Root directory for destination files were downloaded to'
)
parser.add_argument('-s', '--solrRoot', dest='solrRoot',
                    help='Root of the url of the solr service to use'
)
parser.add_argument('-o', '--omeroHost', dest='omeroHost',
                    help='Hostname for server hosting omero instance'
)
parser.add_argument('-fq', '--solrFilterQuery', dest='solrFilterQuery', default=None,
                    help='Filter to apply to solr query. If not supplied uses hardcoded standard filter'
)
parser.add_argument('--omeroDbUser', dest='omeroDbUser', 
                    help='name of the omero postgres database')
parser.add_argument('--omeroDbPass', dest='omeroDbPass',
                    help='Password for the omero postgress database')
parser.add_argument('--omeroDbName', dest='omeroDbName',
                    help='Name of the postgres database omero uses')
parser.add_argument('--omeroDbHost', dest='omeroDbHost',
                    help='Hostname for the server hosting the omero postgres database')
parser.add_argument('--omeroDbPort', dest='omeroDbPort',
                    help='Port to connect on the postgres server hosting the omero database')
parser.add_argument('--profile', dest='profile', default='dev',
                    help='profile from which to read config: dev, prod, live, ...')

args = parser.parse_args()

# Get values from property file and use as defaults that can be overridden
# by command line parameters
try:
    pp = OmeroPropertiesParser(args.profile)
    omeroProps = pp.getOmeroProps()
except Exception, e:
    print "Could not read application properties file for profile " + args.profile
    print "Error was: " + str(e)
    sys.exit(-1)

root_dir = args.rootDestinationDir if args.rootDestinationDir<>None else omeroProps['rootdestinationdir']
solrRoot = args.solrRoot if args.solrRoot <> None else omeroProps['solrurl']
omeroHost = args.omeroHost if args.omeroHost<>None else omeroProps['omerohost']

solrFilterQuery = args.solrFilterQuery
if solrFilterQuery is None:
    # Default query to get all records of interest from Solr
    solrFilterQuery = """experiment/select?q=observation_type:image_record&fq=download_file_path:(*lei*+OR+*lif*)&fl=download_file_path,pipeline_stable_id,procedure_stable_id,parameter_stable_id,production_center,phenotyping_center&rows=10000000&wt=json"""

try:    
    omeroPort = omeroProps['omeroport']
    omeroUsername = omeroProps['omerouser']
    omeroPass = omeroProps['omeropass']
    group = omeroProps['omerogroup']
except Exception, e:
    print "Could not assign omero login properties from application.properties file - did you specify the right profile?"
    print "Error was: " + str(e)
    sys.exit(-1)

# Get records from Solr
solr_query_url = solrRoot + solrFilterQuery
print "Querying solr with the following query" + solrFilterQuery
#solr_json = json.loads(requests.get(solr_query_url).text)
#solr_recs = solr_json['response']['docs']

# Mock results from solr for testing on local machine
with open('/home/kola/temp/embl/solr_query_results.txt', 'rt') as fid:
    solr_json = json.load(fid)
solr_recs = solr_json['response']['docs']

print "Number of records returned from Solr: " + str(len(solr_recs))

# To simplify processing of the Leica data the download_file_path has a 
# root directory and all images are either stored in this root directory 
# (for .lif files or in a subdirectory within the root directory for .lei files
download_url_root = 'file:///nfs/komp2/web/images/3i/ear/'

# Keep a record of which mouse numbers are associated with which leica files
# Use this to check what has been uploaded at the end
solr_leica_to_mouse_no_map = {}

# Map similar to that used in OmeroUpload
solr_directory_to_filenames_map = {}

for rec in solr_recs:
    #fname = os.path.split(rec['download_file_path'])[-1]
    fname = rec['download_file_path'].split(download_url_root)[-1]
    # Remove extra info omero appends to specify explicit filename in Leica
    # archive
    fname, mouse_no = fname.split('[')
    fname = fname.strip(' ')
    key = os.path.join(rec['phenotyping_center'],rec['pipeline_stable_id'],rec['procedure_stable_id'],rec['parameter_stable_id'],fname)
    solr_directory_to_filenames_map[key] = rec['download_file_path'].split('[')[0].strip(' ')

    # Store mouse no with filename
    mouse_no = mouse_no.split(']')[0].strip(' ').upper()
    if solr_leica_to_mouse_no_map.has_key(fname):
        solr_leica_to_mouse_no_map[fname].append(mouse_no)
    else:
        solr_leica_to_mouse_no_map[fname] = [mouse_no,]

# Get candidates for upload from the file system
nfs_leica_list = find_leica_files(root_dir)
nfs_leica_list = [f.split(root_dir)[-1] for f in nfs_leica_list]
# Because of the way the leica files are stored we may need to do some further
# processing to enable comparison with files in omero
# Assume full nfs path is of the form:
#   root_dir + impress_details_dir + leica_specific_dir + filename
# Whereas omero path is of the form:
#   impress_details_dir + filename
# Use a dict to map between the two. Keys are path in omero form, values are
# path in nfs form
impress_details_dir = 'WTSI/MGP_001/MGP_EEI_001/MGP_EEI_114_001/'
leica_part = [f.split(impress_details_dir)[-1] for f in nfs_leica_list]
path_to_leica, leica_filename = map(list, zip(*[(os.path.dirname(p), os.path.basename(p)) for p in leica_part]))
nfs_leica_dict = {}
for fn, nfs_path  in zip(leica_filename, nfs_leica_list):
    nfs_path_omero_style = os.path.join(impress_details_dir, fn)
    if not nfs_leica_dict.has_key(nfs_path_omero_style):
        nfs_leica_dict[nfs_path_omero_style] = [nfs_path,]
    else:
        nfs_leica_dict[nfs_path_omero_style].append(nfs_path)

# Get files already in omero
try:
    print "Attempting to get Leica file list directly from Postgres DB"
    omeroDbUser = args.omeroDbUser if args.omeroDbUser is not None else omeroProps['omerodbuser']
    omeroDbPass = args.omeroDbPass if args.omeroDbPass is not None else omeroProps['omerodbpass']
    omeroDbName = args.omeroDbName if args.omeroDbName is not None else omeroProps['omerodbname']
    omeroDbHost = args.omeroDbHost if args.omeroDbHost is not None else omeroProps['omerodbhost']
    if args.omeroDbPort is not None:
        omeroDbPort = args.omeroDbPort
    elif 'omerodbport' in omeroProps:
        omeroDbPort = omeroProps['omerodbport']
    else:
        omeroDbPort = '5432'

    conn = psycopg2.connect(database=omeroDbName, user=omeroDbUser,
                            password=omeroDbPass, host=omeroDbHost,
                            port=omeroDbPort)
    cur = conn.cursor()
    # Get the actual leica files uploaded to Omero
    query = "SELECT clientpath FROM filesetentry " + \
            "WHERE clientpath LIKE '%lif' OR clientpath LIKE '%lei'"
    cur.execute(query)
    omero_leica_file_list = []
    for f in cur.fetchall():
        omero_leica_file_list.append(f[0].split('impc/')[-1])

    # Get the images contained in the leica files uploaded to Omero
    # These images are in the download_urls obtained from solr
    query = "SELECT name FROM image " + \
            "WHERE name LIKE '%.lif%' OR name LIKE '%.lei%'"
    cur.execute(query)
    omero_leica_image_list = []
    for i in cur.fetchall():
        omero_leica_image_list.append(i[0])
    conn.close()
except KeyError as e:
    print "Could not connect to omero postgres database. Key " + str(e) + \
          " not present in omero properties file. Aborting!"
    conn.close()
    sys.exit()
print "Leica files in Omero: "
for f in omero_leica_file_list:
    print f

print "Leica images in Omero: "
for i in omero_leica_image_list:
    print i
pdb.set_trace()
set_solr_filenames = set(solr_directory_to_filenames_map.keys())
set_omero_filenames = set(omero_leica_file_list)
set_nfs_file_list = set(nfs_leica_list)
set_nfs_file_list_omero_style = set(nfs_leica_dict.keys())

files_to_upload_omero_style = set_solr_filenames - set_omero_filenames
files_to_upload_available = files_to_upload_omero_style.intersection(set_nfs_file_list_omero_style)
files_to_upload_unavailable = files_to_upload_omero_style - files_to_upload_available

# Delete keys of files we do not want to upload
keys_to_delete = list(set_nfs_file_list_omero_style - files_to_upload_available)
for k in keys_to_delete:
    del nfs_leica_dict[k]

# Get number of files to upload
n_files_to_upload = 0
for k in nfs_leica_dict.keys():
    n_files_to_upload += len(nfs_leica_dict[k])

# Upload files
omeroS = OmeroService(omeroHost, omeroPort, omeroUsername, omeroPass, group)
index = 0
n_files_uploaded = 0
n_files_upload_failed = 0
for fn_omero in nfs_leica_dict.keys():
    for fn in nfs_leica_dict[fn_omero]:
        print "About to upload file " + str(index+1) + " of " + str(n_files_to_upload)
        index += 1
        # Use the directory from the omero path as this has the correct format
        # for centre, pipeline, procedure & parameter
        directory = os.path.dirname(fn_omero)
        dir_structure = directory.split('/')
        project = dir_structure[0]
        # Below we assume dir_structure is list with elements:
        # [project, pipeline, procedure, parameter]
        dataset = "-".join(dir_structure)
        # Use the path to the actual file on nfs for dir from which to upload
        fullpath = os.path.join(root_dir, fn)
        try:
            #omeroS.load(fullpath, project=project, dataset=dataset)
            n_files_uploaded += 1
        except Exception as e:
            print "There was a problem loading " + fn + " into omero. " +\
                  "Error message was: " + str(e)
            print "Continuing to next image"
            n_files_upload_failed += 1
            continue
