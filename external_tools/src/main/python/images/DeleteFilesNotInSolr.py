#!/usr/bin/python

"""Delete file not in Solr ..."""
import os
import requests
import json
import sys
import os.path
import sys
import argparse
import shutil
from common import splitString
from DirectoryWalker import DirectoryWalker
import itertools
from OmeroPropertiesParser import OmeroPropertiesParser


responseFailed=0
numberOfImageDownloadAttemps=0
totalNumberOfImagesWeHave=0
numFoundInSolr=0

uniquePathsFromSolr=set()
uniqueFilesWithPathsFromNfs=set()

def main(argv):
    parser = argparse.ArgumentParser(
        description='Delete files not in Solr'
    )
    parser.add_argument('-d', '--rootDestinationDir', dest='rootDestinationDir',
                        help='Directory for root of destination to store images'    )
    parser.add_argument('-s', '--rootSolrUrl', dest='rootSolrUrl',
                        help='URL to root of solr index'
    )
    parser.add_argument('-H', '--host', dest='komp2Host',
                        help='Hostname for server hosting komp2 db'
    )
    parser.add_argument('-p', '--port', dest='komp2Port',
                        help='Port by which to connect to komp2 db'
    )
    parser.add_argument('-u', '--user', dest='komp2User',
                        help='Username for connecting to komp2 db'
    )
    parser.add_argument('-db', '--database', dest='komp2Db',
                        help='Database to connect to for komp2db'
    )
    parser.add_argument('--pass', dest='komp2Pass',
                        help='Password for komp2db'
    )
    parser.add_argument('--profile', dest='profile', default='dev',
                        help='profile from which to read config: dev, prod, live, ...')

    args = parser.parse_args()
    
    # Get values from property file and use as defaults that can be overridden
    # by command line parameters
    try:
        pp = OmeroPropertiesParser(args.profile)
        omeroProps = pp.getOmeroProps()
    except:
        omeroProps = {}

    rootSolrUrl = args.rootSolrUrl if args.rootSolrUrl <> None else omeroProps['solrurl']
    komp2Host = args.komp2Host if args.komp2Host<>None else omeroProps['komp2host']
    komp2Port = args.komp2Port if args.komp2Port<>None else omeroProps['komp2port']
    komp2db = args.komp2Db if args.komp2Db<>None else omeroProps['komp2db']
    komp2User = args.komp2User if args.komp2User<>None else omeroProps['komp2user']
    komp2Pass = args.komp2Pass if args.komp2Pass<>None else omeroProps['komp2pass']
    rootDestinationDir = args.rootDestinationDir if args.rootDestinationDir<>None else omeroProps['rootdestinationdir']

    #note cant split this url over a few lines as puts in newlines into url which doesn't work
    #solrQuery="""experiment/select?q=observation_type:image_record&fq=download_file_path:(download_file_path:*bhjlk01.jax.org/images/IMPC_ALZ_001/*%20AND%20!download_file_path:*.mov)&fl=id,download_file_path,phenotyping_center,pipeline_stable_id,procedure_stable_id,datasource_name,parameter_stable_id&wt=json&indent=on&rows=10000000"""
    solrQuery="""experiment/select?q=observation_type:image_record&fq=(download_file_path:*mousephenotype.org*%20AND%20!download_file_path:*.mov)&fl=id,download_file_path,phenotyping_center,pipeline_stable_id,procedure_stable_id,datasource_name,parameter_stable_id&wt=json&indent=on&rows=1000000"""

    print("running python image copy script for impc images")

    print 'rootDestinationDir is "', rootDestinationDir
    solrUrl=rootSolrUrl+solrQuery;
    print 'solrUrl', solrUrl
    getPathsFromSolr(solrUrl, rootDestinationDir)
    #for solrPath in itertools.islice(uniquePathsFromSolr, 3):
        #print "solrPath="+solrPath
    dirWalker=DirectoryWalker()
    rel_directory_to_filenames_map=dirWalker.getFilesFromDir(rootDestinationDir)
    n=10
    for key, value in rel_directory_to_filenames_map.items():
        count=0
        for name in value:
            filePath=rootDestinationDir+key+"/"+name
            uniqueFilesWithPathsFromNfs.add(filePath)
            count+=1
            #if count<= n: print "filePath="+str(filePath)
    print "uniquePathsFromSolr length=",len(uniquePathsFromSolr)
    print "uniqueFilesWithPathsFromNfs=", len(uniqueFilesWithPathsFromNfs)
    filesNotInSolrAnymore=uniqueFilesWithPathsFromNfs.difference(uniquePathsFromSolr)
    print "files not in solr anymore size=",len(filesNotInSolrAnymore)
    for currentPath in filesNotInSolrAnymore:
        print "trying to move this file="+currentPath
        destinationPath=currentPath.replace("/nfs/komp2/web/images/clean/impc/", "/nfs/komp2/web/images/impc/")
        print "destPath="+destinationPath
        moveFile(currentPath, destinationPath)
        #os.remove(filePath) maybe move to another directory rather than delete
        
    
    #runWithSolrAsDataSource(solrUrl, cnx, rootDestinationDir)


def getPathsFromSolr(solrUrl, rootDestinationDir):
    """
    need to get these passed in as arguments - the host and db name etc for jenkins to run
    first get the list of download urls and the data source, experiment, procdure and parameter and observation id for the images
    """
    v = json.loads(requests.get(solrUrl).text)
    docs=v['response']['docs']
    numFoundInSolr=v['response']['numFound']
    for doc in docs:
        download_file_path=doc['download_file_path']
        datasource_id=doc['datasource_name']
        phenotyping_center=doc['phenotyping_center']
        #experiment=doc['experiment']
        pipeline_stable_id=doc['pipeline_stable_id']
        observation_id=doc['id']
        procedure_stable_id=doc['procedure_stable_id']
        parameter_stable_id=doc['parameter_stable_id']
        global rootDesitinationdir
        getUniqueFilePathsFromSolr(observation_id, rootDestinationDir,phenotyping_center,pipeline_stable_id, procedure_stable_id, parameter_stable_id, download_file_path)
        
    print 'number found in solr='+str(numFoundInSolr)+' number of failed responses='+str(responseFailed)+' number of requests='+str(numberOfImageDownloadAttemps)+' total totalNumberOfImagesWeHave='+str(totalNumberOfImagesWeHave)

def createDestinationFilePath(rootDestinationDir, phenotyping_center, pipeline_stable_id, procedure, parameter, download_file_path):
    directory="/".join([rootDestinationDir,phenotyping_center, pipeline_stable_id,procedure,parameter])
    return directory

def getUniqueFilePathsFromSolr(observation_id,  rootDestinationDir, phenotyping_center,pipeline_stable_id, procedure, parameter, downloadFilePath):
        global totalNumberOfImagesWeHave
        global responseFailed
        global numberOfImageDownloadAttemps
        directory = createDestinationFilePath(rootDestinationDir, phenotyping_center, pipeline_stable_id, procedure,parameter, downloadFilePath)
        #print "directory "+str(directory)
        dstfilename=directory+"/"+str(downloadFilePath.split('/')[-1])
        #print "dstfilename="+str(dstfilename)
        #/nfs/komp2/web/images/impc/MRC Harwell/HRWL_001/IMPC_XRY_001/IMPC_XRY_034_001/114182.dcm
        # new file paths are /nfs/public/ro/pheno-archive-images/images/impc
        if dstfilename in uniquePathsFromSolr:
            print '---------------------!!!!!!!!!!error the filePath is not unique and has been specified before:'+dstfilename
        uniquePathsFromSolr.add(dstfilename.replace("impc//", "impc/"))#hack to remove the extra slash after impc but don't want to effect other module used by other code
        #destDirectory=os.path.dirname(destPath)
        #print "destination directory for copy is "+destDirectory
        #if not os.path.exists(destDirectory):
        #                os.makedirs(destDirectory)
        #print 'saving file to '+destPath
        #if not os.path.isfile(destPath):
        #    try:
        #       shutil.copyfile(dstfilename,destPath)
        #    except IOError:
        #        print "file does not exist "+str(dstfilename)+"  continuing"
        #totalNumberOfImagesWeHave=totalNumberOfImagesWeHave+1
        #if totalNumberOfImagesWeHave%1000==0 :
        #    print "totalNumber of images we have="+str(totalNumberOfImagesWeHave)
        
def moveFile(currentPath, destinationPath):
    
        filename=str(destinationPath.split('/')[-1])
        destDirectory=destinationPath.replace(filename, "")
        print "making directory="+destDirectory
        if not os.path.exists(destDirectory):
            os.makedirs(destDirectory)
        print 'moving file to '+destinationPath
        if not os.path.isfile(destinationPath):
            try:
               shutil.move(currentPath,destinationPath)
            except IOError:
                print "file does not exist "+str(currentPath)+"  continuing"

if __name__ == "__main__":
    main(sys.argv[1:])
