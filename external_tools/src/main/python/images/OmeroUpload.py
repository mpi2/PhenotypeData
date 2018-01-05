#!/usr/bin/python
import platform
import locale
import time
import omero
import sys
import getopt
import collections
import argparse

from Solr import Solr
from DirectoryWalker import DirectoryWalker
from OmeroPropertiesParser import OmeroPropertiesParser
from OmeroService import OmeroService

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
    parser.add_argument('-b', '--begin', dest='begin', type=int, default=0,
                        help='Starting index for files to upload'
    )
    parser.add_argument('-e', '--end', dest='end', type=int, default=1000000,
                        help='end index for files to upload'
    )
    parser.add_argument('-fp', '--filterProject', dest='filterProject', default=None,
                        help='Filter to apply to select a particular project in Omero'
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

    begin = args.begin
    end = args.end
    filterProject = args.filterProject
    solrFilterQuery = args.solrFilterQuery

    try:    
        omeroPort = omeroProps['omeroport']
        omeroUsername = omeroProps['omerouser']
        omeroPass = omeroProps['omeropass']
        group = omeroProps['omerogroup']
    except Exception, e:
        print "Could not assign omero login properties from application.properties file - did you specify the right profile?"
        print "Error was: " + str(e)
        return


    print "running main intelligent omero upload method"
    print 'rootDestinationDir is "', root_dir
    
    omeroUpload=OmeroUpload(root_dir, omeroHost, omeroPort, omeroUsername, omeroPass, group)#/Users/jwarren/Documents/images/impc/")
    solr_directory_to_filenames_map=omeroUpload.getSolrDirectoryMap(solrRoot, solrFilterQuery)
    omero_directory_to_filenames_map=omeroUpload.getOmeroDirectoriesAndFilesMap(omeroHost, filterProject)
    nfs_directory_to_filenames_map=omeroUpload.getDirectoriesWithFileNamesFromNFS(root_dir)
    
    #run this normally after all images loaded
    #want to get from experiment core the directories    
    #sys.exit()
    
    i=0
    
    #loop over the directories we should have (got from solr) and check what we have in nfs and what we have in omero
    #We need to load in the order we get from SOLR so we can start at any point from that solr ordered list
    for directory, filenames in solr_directory_to_filenames_map.items():
        print "i="+str(i)
        if i>=end:
            break;
        if i<begin:
        #if "_ERG_" not in directory and "_ABR_" not in directory:
            print "skipping directory"+ directory#+ " value="+str(filenames)
        else:
            numberShouldBeInOmero=len(filenames)
            print "processing directory "+directory+ "should be according to solr this number of files="+str(numberShouldBeInOmero)
            if directory in nfs_directory_to_filenames_map:
                numberInNfs=len(nfs_directory_to_filenames_map[directory])
                print "we have an NFS directory with this number of files in it :"+ str(numberInNfs)
            #test if we have any files already in omero for this directory
            if directory not in omero_directory_to_filenames_map:
                print "directory not in omero at all!"
                #omeroUpload.load(directory)
                #as we may have pdfs to upload now we cannot do it at a directory level check the parameters to make sure we won't have pdfs in this directory!
                if "_ERG_" not in directory and "_ABR_" not in directory and "_IMM_" not in directory: 
                    omeroUpload.load(directory)
                else:
                    print "loading files"
                    
                    omeroUpload.load(directory, filenames)
            else:
                numberInOmero=len(omero_directory_to_filenames_map[directory])
                filesNotInOmero=list(set(filenames) - set(omero_directory_to_filenames_map[directory]))
                filesNotNfsButInSolr=list(set(filenames) - set(nfs_directory_to_filenames_map[directory]))
                if len(filesNotNfsButInSolr) >0:
                    print "need to download some more files for this dir="+str(filesNotNfsButInSolr)
                print "number should be in omero="+str(numberShouldBeInOmero)+"in omero already"+str(numberInOmero)+" filesNotInOmero length="+str(len(filesNotInOmero))
                print "directory="+directory+" filesNotInOmero="+str(filesNotInOmero)
                if "_ERG_" not in directory and "_ABR_" not in directory:
                    print "loading directory" 
                    omeroUpload.load(directory)
                else:
                    print "loading files"
                    omeroUpload.load(directory, filesNotInOmero)
            #if (numberInOmero<len(filesNotInOmero)) or (len(filesNotInOmero)> 300):
#                    print "should load full dir"
#                    try:
#                        omeroUpload.load(directory)
#                    except:
#                        print "Unexpected error loading directory:", sys.exc_info()[0]
#                        continue
#                        
#                else:
            #print "should load on per file basis"
                #need the list that is not already in omero for this dire
            #try:
             #   print "directory="+directory+" filesNotInOmero="+str(filesNotInOmero)
             #   omeroUpload.load(directory, filesNotInOmero)
            #except:
            #    print "Unexpected error loading file:", sys.exc_info()[0]
            #    continue
                       
            
            
        i=i+1
    
     
    
class OmeroUpload:
    
    def __init__(self, root_dir, omeroHost, omeroPort, omeroUsername, omeroPass, group):
        print "Initialising OmeroUpload"
        self.root_dir=root_dir
        self.omeroS=OmeroService(omeroHost, omeroPort, omeroUsername, omeroPass, group)
        
    def getDirectoriesWithFileNamesFromNFS(self, root_dir):
        self.dirWalker=DirectoryWalker()
        #root_dir='/nfs/komp2/web/images/impc/'#"/Users/jwarren/Documents/images/impc/"
        rel_directory_to_filenames_map=self.dirWalker.getFilesFromDir(root_dir)
        for key, value in rel_directory_to_filenames_map.items():
            print "dir in nfs file walker="+key
            #print "value="+str(value)
        return rel_directory_to_filenames_map
    
    def getSolrDirectoryMap(self, solr_experiment_url, solr_filter_query=None):
        self.solr=Solr(solr_experiment_url)
        
        # KB 05/01/2018 allow user parameter to set solr filter so we can modify the map returned
        if type(solr_filter_query) == str and len(solr_filter_query) > 0:
            self.solr.standardFilter = solr_filter_query

        directory_map=self.solr.getAllPhenCenterPipelinesAndProceduresAndParameters()
        #get a list of all directories that should contain images and the image filenames for those directories
        return directory_map
    
    def getOmeroDirectoriesAndFilesMap(self, omeroHost, project=None):
        print "running method to get Omero directories and file maps from OmeroService"
        directory_to_filename_map=self.omeroS.getImagesAlreadyInOmero(project)
        print str(len(directory_to_filename_map))+" directories already in omero"
        for key, value in directory_to_filename_map.items():
            print "getting directory and filenames from omero dir"+key +" number of images="+str(len(value))
        return directory_to_filename_map
    
    def load(self, directory, filenames=None):
        print "uploading directory"+directory
        #ICS/ICS_001/IMPC_XRY_001/IMPC_XRY_034_001
        dir_structure=directory.split('/')
        project=dir_structure[0]
        dataset=dir_structure[0]+"-"+dir_structure[1]+"-"+dir_structure[2]+"-"+dir_structure[3]

        # Filter out uploading FACS files -- OMERO doesn't seem to handle these yet..?
        filtered_filenames = filenames
        
        if filenames:
        	filtered_filenames = filter(lambda x: not x.endswith(".fcs"), filenames)

        self.omeroS.loadFileOrDir(self.root_dir+directory, project=project, dataset=dataset, filenames=filtered_filenames)
        
        
if __name__ == "__main__":
    main(sys.argv[1:]) 
   
