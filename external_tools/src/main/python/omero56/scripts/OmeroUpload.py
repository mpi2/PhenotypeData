"""
    Upload images to omero database.

    This script queries solr for records that have download paths. For each
    download path it checks whether it already exists in Omero. If it does not
    then it checks whether it exists in the IMPC file system. If present
    in the file system it is uploaded to Omero.
"""
import os
import sys
import argparse
import requests
import json
import glob
import logging

import psycopg2

from OmeroService import OmeroService
from OmeroPropertiesParser import OmeroPropertiesParser

def main(argv):
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
    parser.add_argument('--profile-path', dest='profilePath',
                        help='Explicit path to file from which to read ' + \
                             'profile e.g. ' + \
                             '/home/kola/application.properties. ' + \
                             'Overrides value of --profile argument.'
    )
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

    # Get values from property file and use as defaults that can be overridden
    # by command line parameters
    if args.profilePath is not None:
        try:
            pp = OmeroPropertiesParser()
            omeroProps = pp.getOmeroProps(args.profilePath)
        except Exception as e:
            logger.error("Could not read application properties file from " + args.profilePath)
            logger.error("Error was: " + str(e))
            return
    else:
        try:
            pp = OmeroPropertiesParser(args.profile)
            omeroProps = pp.getOmeroProps()
        except Exception as e:
            logger.error("Could not read application properties file for profile " + args.profile)
            logger.error("Error was: " + str(e))
            return

    try:
        root_dir = args.rootDestinationDir if args.rootDestinationDir<>None else omeroProps['rootdestinationdir']
        solrRoot = args.solrRoot if args.solrRoot <> None else omeroProps['solrurl']
        omeroHost = args.omeroHost if args.omeroHost<>None else omeroProps['omerohost']
    except Exception as e:
        logger.exception("Could not assign some properties expected as command line arguments or in application.properties file - did you specify the right profile? Error message was: " + str(e))

    solrFilterQuery = args.solrFilterQuery
    if solrFilterQuery is None:
        # Default query to get all records of interest from Solr
        solrFilterQuery = """experiment/select?q=observation_type:image_record&fq=(download_file_path:(*mousephenotype.org*%20OR%20*images/3i*)%20AND%20-download_file_path:*.mov%20AND%20-download_file_path:*.fcs%20AND%20-download_file_path:*.bz2%20AND%20-download_file_path:*.nrrd)&rows=10000000&wt=json&fl=download_file_path,pipeline_stable_id,procedure_stable_id,parameter_stable_id,production_center,phenotyping_center"""

    try:    
        omeroPort = omeroProps['omeroport']
        omeroUsername = omeroProps['omerouser']
        omeroPass = omeroProps['omeropass']
        group = omeroProps['omerogroup']
    except Exception as e:
        logger.error("Could not assign omero login properties from application.properties file - did you specify the right profile?")
        logger.error("Error was: " + str(e))
        return

    # Other values needed within this script.
    splitString = 'impc/'
    # Assuming files to exclude is taken care of in solrquery
    files_to_exclude = ['.fcs','.mov','.bz2']
    # Upload whole dir if it contains more than this number of files
    load_whole_dir_threshold = 300

    logger.info("running main intelligent omero upload method")
    logger.info('rootDestinationDir is "' + root_dir + '"')
    
    # Get records from Solr
    solr_query_url = solrRoot + solrFilterQuery
    logger.info("Querying solr with the following query" + solrFilterQuery)
    solr_json = json.loads(requests.get(solr_query_url).text)
    solr_recs = solr_json['response']['docs']
    logger.info("Number of records returned from Solr: " + str(len(solr_recs)))

    solr_directory_to_filenames_map = {}
    for rec in solr_recs:
        # On 03/12/2019 got a key error for phenotyping centre. This should
        # not happen, but occurred when downloading the images. Added a
        # similar fix as to downloadimages.py
        try:
            fname = os.path.split(rec['download_file_path'])[-1]
            key = os.path.join(rec['phenotyping_center'],rec['pipeline_stable_id'],rec['procedure_stable_id'],rec['parameter_stable_id'],fname)
            solr_directory_to_filenames_map[key] = rec['download_file_path']
        except KeyError as e:
            msg = "Key " + str(e)+  " not returned by solr - not uploading"
            if rec.has_key('download_file_path'): 
                msg += " " + rec['download_file_path']
            else:
                msg += " unspecified filename (no download_file_path)!"
            print msg
            continue

    # Get images from Omero
    # Sometimes omero on the server throws an ICE memory limit exception. In that case go directly
    # via postgres. This may return more records than going via omero, but that should not
    # be a problem.
    omeroS = OmeroService(omeroHost, omeroPort, omeroUsername, omeroPass, group)
    try:
        omero_file_list = omeroS.getImagesAlreadyInOmero()
    except Exception as e: # TODO: Use exact exception here. It's something like ::Ice::MemoryLimitException
        logger.warn("Problem attempting to get images from omero. Attempting via Postgres")
        logger.warn("Exception message was " + str(e))
        omero_file_list = omeroS.getImagesAlreadyInOmeroViaPostgres(omeroProps)
    logger.info("Number of files from omero = " + str(len(omero_file_list)))
    
    # Don't use OmeroService to get annotations as we are having a out of
    # memory error when it is running on the server. Query omero directly
    # to get annotations
    try:
        logger.info("Attempting to get annotations directly from Postgres DB")
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
        query = 'SELECT ds.name, (SELECT o.name FROM originalfile o ' + \
                'WHERE o.id=a.file) AS filename FROM datasetannotationlink ' + \
                'dsal INNER JOIN dataset ds ON dsal.parent=ds.id ' + \
                'INNER JOIN annotation a ON dsal.child= a.id'
        cur.execute(query)
        omero_annotation_list = []
        for ann in cur.fetchall():
            dir_parts = ann[0].split('-')
            if len(dir_parts) == 4:
                dir_parts.append(ann[1])
                omero_annotation_list.append("/".join(dir_parts))
        conn.close()
    except KeyError as e:
        logger.error("Could not connect to omero postgres database. Key " + str(e) + \
              " not present in omero properties file. Attempting to use " + \
              " OmeroService.getAnnotationsAlreadyInOmero")
        conn.close()
        omero_annotation_list = omeroS.getAnnotationsAlreadyInOmero()
        
        
        
    #omero_annotation_list = omeroS.getAnnotationsAlreadyInOmero()
    logger.info("Number of annotations from omero = " + str(len(omero_annotation_list)))
    omero_file_list.extend(omero_annotation_list)
    omero_dir_list = list(set([os.path.split(f)[0] for f in omero_file_list]))


    # Get the files in NFS
    list_nfs_filenames = []
    os.path.walk(root_dir, add_to_list, list_nfs_filenames)
    list_nfs_filenames = [f.split(root_dir)[-1] for f in list_nfs_filenames]
    logger.info("Number of files from NFS = " + str(len(list_nfs_filenames)))


    # Modified to carry out case-insensitive comparisons.
    set_solr_filenames = set([k.lower() for k in solr_directory_to_filenames_map.keys()])
    set_omero_filenames = set([f.lower() for f in omero_file_list])
    dict_nfs_filenames = {}
    for f in list_nfs_filenames:
        # Note that if more than one file maps to the same case insensitive value
        # only the last one encountered will be used
        dict_nfs_filenames[f.lower()] = f
    set_nfs_filenames = set(dict_nfs_filenames.keys())

    files_to_upload = set_solr_filenames - set_omero_filenames
    files_to_upload_available = files_to_upload.intersection(set_nfs_filenames)
    files_to_upload_unavailable = files_to_upload - files_to_upload_available

    # Create a dictionary for the files to upload with the directory as the
    # key and the original nfs filenames as the values, so each dir can be passed to 
    # omero with associated files
    dict_files_to_upload = {}
    for f in files_to_upload_available:
        dirname, filename = os.path.split(dict_nfs_filenames[f])
        if dict_files_to_upload.has_key(dirname):
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
            "truncated at 100) were present in Solr but absent in NFS:" + \
            file_list
        logger.warning(message)

def add_to_list(L,dirname,names):
    """Add files to list whilst walking through dir tree"""

    for n in names:
        fullname = os.path.join(dirname, n)
        if os.path.isfile(fullname):
            L.append(fullname)

if __name__ == "__main__":
    main(sys.argv[1:])
