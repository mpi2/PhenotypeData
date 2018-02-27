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

#import psycopg2

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
        return

    root_dir = args.rootDestinationDir if args.rootDestinationDir<>None else omeroProps['rootdestinationdir']
    solrRoot = args.solrRoot if args.solrRoot <> None else omeroProps['solrurl']
    omeroHost = args.omeroHost if args.omeroHost<>None else omeroProps['omerohost']

    solrFilterQuery = args.solrFilterQuery
    if solrFilterQuery is None:
        solrFilterQuery = """experiment/select?q=observation_type:image_record&fq=(download_file_path:(*mousephenotype.org*%20OR%20images/3i*)%20AND%20-download_file_path:*.mov%20AND%20-download_file_path:*.fcs%20AND%20-download_file_path:*.bz2)&rows=10000000&wt=json&fl=download_file_path,pipeline_stable_id,procedure_stable_id,parameter_stable_id,production_center,phenotyping_center"""
        #solrFilterQuery = """experiment/select?q=observation_type:image_record&fq=(download_file_path:(*17725*)%20AND%20-download_file_path:*.mov%20AND%20-download_file_path:*.fcs%20AND%20-download_file_path:*.bz2)&rows=1000000&wt=json&fl=download_file_path,pipeline_stable_id,procedure_stable_id,parameter_stable_id,production_center,phenotyping_center"""

    try:    
        omeroPort = omeroProps['omeroport']
        omeroUsername = omeroProps['omerouser']
        omeroPass = omeroProps['omeropass']
        group = omeroProps['omerogroup']
        #omeroDbUser = omeroProps['omerodbuser']
        #omeroDbPass = omeroProps['omerodbpass']
        #omeroDbName = omeroProps['omerodbname']
        #omeroDbHost = omeroProps['omerodbhost']
        #omeroDbPort = omeroProps['omerodbport']
    except Exception, e:
        print "Could not assign omero login properties from application.properties file - did you specify the right profile?"
        print "Error was: " + str(e)
        return

    # Other args needed within this script. Will discuss with JM how to incorporat these
    splitString = 'impc/'
    dirsToExclude = ['_ERG_','_ABR_']
    filesToExlude = ['.fcs','.mov','.bz2']

    print "running main intelligent omero upload method"
    print 'rootDestinationDir is "', root_dir
    
    #omeroUpload=OmeroUpload(root_dir, omeroHost, omeroPort, omeroUsername, omeroPass, group)#/Users/jwarren/Documents/images/impc/")
    solr_query_url = solrRoot + solrFilterQuery
    print solrFilterQuery
    solr_json = json.loads(requests.get(solr_query_url).text)
    solr_recs = solr_json['response']['docs']
    solr_directory_to_filenames_map = {}
    for rec in solr_recs:
        fname = os.path.split(rec['download_file_path'])[-1]
        key = os.path.join(rec['phenotyping_center'],rec['pipeline_stable_id'],rec['procedure_stable_id'],rec['parameter_stable_id'],fname)
        solr_directory_to_filenames_map[key] = rec['download_file_path']
    
    
    #conn = psycopg2.connect(dbname=omeroDbName, user=omeroDbUser, password=omeroDbPass,port=omeroDbPort, host=omeroDbHost)
    #cur = conn.cursor()
    ##############################
    ## The section below has been commented out as I am unable to link the
    ## filesetentry to the correct dataset. I am therefore not reconstructing
    ## the file path based on phenotyping center, pipeline, procedure and 
    ## parameter. This is the preferable approach. For the moment I am simply
    ## splitting based on indir_base.
    ##sql = 'select f.id, f.clientpath, d.name from filesetentry f inner join dataset d on f.fileset=d.id';
    ##cur.execute(sql)
    ##omero_file_data = cur.fetchall()
    ### Reconstruct the filepath based on phenotyping center, pipeline, procedure and parameter
    ##omero_file_list = [os.path.join(os.path.sep.join(ofd[-1].split('-')),os.path.split (ofd[1])[-1]) for ofd in omero_file_data]
    ##omero_file_list = [os.path.join(os.path.sep.join(ofd[-1].split('-')),os.path.split (ofd[1])[-1]) for ofd in omero_file_data]

    #sql = 'select f.id, f.clientpath from filesetentry f';
    #cur.execute(sql)
    #omero_file_data = cur.fetchall()
    #print "Number of files from omero = " + str(len(omero_file_data))
    ## Get the filepath by splitting the indir path
    #omero_file_list = []
    #for ofd in omero_file_data:
    #    try:
    #        #indir,ofd_path = ofd[1].split(root_dir[1:])
    #        ofd_path = ofd[1].split('impc/')[-1]
    #    except Exception as e:
    #        print "Problem extracting root_dir from clientpath " + ofd[1]
    #        print "Error was: " + e.message
    #        omero_file_list.append(ofd[1])
    #        continue
    #    #if indir is None or len(indir) < 1:
    #    #    print "Did not extract root_dir from " + ofd[1]
    #    omero_file_list.append(ofd_path)

    omeroS = OmeroService(omeroHost, omeroPort, omeroUsername, omeroPass, group)
    omero_file_list = omeroS.getImagesAlreadyInOmero2()
    print "Number of files from omero = " + str(len(omero_file_list))


    # Get the files in NFS
    nfs_file_list = []
    os.path.walk(root_dir, add_to_list, nfs_file_list)
    nfs_file_list = [f.split(root_dir)[-1] for f in nfs_file_list]


    set_solr_filenames = set(solr_directory_to_filenames_map.keys())
    set_omero_filenames = set(omero_file_list)
    set_nfs_file_list = set(nfs_file_list)

    files_to_upload = set_solr_filenames - set_omero_filenames
    files_to_upload_available = files_to_upload.intersection(set_nfs_file_list)
    files_to_upload_unavailable = files_to_upload - files_to_upload_available

    # Create a dictionary for the files to upload with the directory as the
    # key and the filenames as the values, so each dir can be passed to 
    # omero with associated files
    dict_files_to_upload = {}
    for f in files_to_upload_available:
        dirname, filename = os.path.split(f)
        if dict_files_to_upload.has_key(dirname):
            dict_files_to_upload[dirname].append(filename)
        else:
            dict_files_to_upload[dirname] = [filename]

    # Upload files
    #omeroS = OmeroService(omeroHost, omeroPort, omeroUsername, omeroPass, group)
    for directory in dict_files_to_upload.keys():
        filenames = dict_files_to_upload[directory]
        print "uploading directory " + directory + " and files:"
        print filenames
        dir_structure = directory.split('/')
        project = dir_structure[0]
        # Below we assume dir_structure is list with elements:
        # [project, pipeline, procedure, parameter]
        dataset = "-".join(dir_structure)
        fullpath = os.path.join(root_dir, directory)
        #omeroS.loadFileOrDir(fullpath, project=project, dataset=dataset, filenames=filenames)

    return files_to_upload_available, files_to_upload_unavailable, nfs_file_list

def add_to_list(L,dirname,names):
    """Add files to list whilst walking through dir tree"""

    for n in names:
        fullname = os.path.join(dirname, n)
        if os.path.isfile(fullname):
            L.append(fullname)

if __name__ == "__main__":
    solr_json = main(sys.argv[1:])
