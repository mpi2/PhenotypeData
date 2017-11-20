#!/usr/bin/python

#this program gets the download_file_paths (http mousephenotype uris) from the experiment core and then downloads the images 
import os
import requests
import json
import sys
import os.path
import sys
import getopt
from common import splitString


responseFailed=0
numberOfImageDownloadAttemps=0
totalNumberOfImagesWeHave=0
numFoundInSolr=0


uniqueUris=set()
removeFilesSet=set()

def main(argv):
    rootSolrUrl="""http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/"""
    solrQuery="""experiment/select?q=observation_type:image_record&fq=(download_file_path:*mousephenotype.org*%20AND%20!download_file_path:*.mov%20AND%20!download_file_path:*.bz2)&fl=id,download_file_path,phenotyping_center,pipeline_stable_id,procedure_stable_id,datasource_name,parameter_stable_id&wt=json&indent=on&rows=10000000"""
    #solrQuery="""experiment/select?q=observation_type:image_record&fq=(download_file_path:"http://www.mousephenotype.org/images/src/8/9/0/19/91/2174/176031.dcm"%20AND%20!download_file_path:*.mov%20AND%20!download_file_path:*.bz2)&fl=id,download_file_path,phenotyping_center,pipeline_stable_id,procedure_stable_id,datasource_name,parameter_stable_id&wt=json&indent=on&rows=10000000"""
    #note cant split this url over a few lines as puts in newlines into url which doesn't work
    rootDestinationDir='/nfs/komp2/web/images/clean/impc'#/Users/jwarren/images/impc'#'/nfs/komp2/web/images/impc/images'
    print("running python image download script for impc images")


    try:
          opts, args = getopt.getopt(argv,"d:s:n:h:p:u::db:pass:",[])
    except getopt.GetoptError:
        print 'downloadimages.py -d <rootDestinationDir>  -s <rootSolrUrl> -h <komp2host> -p <komp2Port> -b <komp2db> -u <komp2User> -pass <komp2Pass>'
        sys.exit(2)
    for opt, arg in opts:
        print 'downloadimages.py -d <rootDestinationDir> -s <rootSolrUrl> -h <komp2host> -p <komp2Port> -b <komp2db> -u <komp2User> -pass <komp2Pass>'
       
        if opt in ("-d", "--rootDestinationDir"):
            rootDestinationDir = arg
        elif opt in ("-s", "--rootSolrUrl"):
             rootSolrUrl = arg
    print 'rootDestinationDir is "', rootDestinationDir
    solrUrl=rootSolrUrl+solrQuery;
    print 'solrUrl', solrUrl
    runWithSolrAsDataSource(solrUrl, rootDestinationDir)


def runWithSolrAsDataSource(solrUrl, rootDestinationDir):
    """
    need to get these passed in as arguments - the host and db name etc for jenkins to run
    first get the list of download urls and the data source, experiment, procdure and parameter and observation id for the images
    """
    v = json.loads(requests.get(solrUrl).text)
    docs=v['response']['docs']
    numFoundInSolr=v['response']['numFound']
    for doc in docs:
        download_file_path=doc['download_file_path']
        download_file_path=download_file_path.lower()
        datasource_id=doc['datasource_name']
        phenotyping_center=doc['phenotyping_center']
        #experiment=doc['experiment']
        pipeline_stable_id=doc['pipeline_stable_id']
        observation_id=doc['id']
        procedure_stable_id=doc['procedure_stable_id']
        parameter_stable_id=doc['parameter_stable_id']
        processFile(observation_id, rootDestinationDir,phenotyping_center,pipeline_stable_id, procedure_stable_id, parameter_stable_id, download_file_path)
    print 'remove files set' 
    for downloadFile in removeFilesSet:
        print downloadFile
    print 'number found in solr='+str(numFoundInSolr)+' number of failed responses='+str(responseFailed)+' number of requests='+str(numberOfImageDownloadAttemps)+' total totalNumberOfImagesWeHave='+str(totalNumberOfImagesWeHave)

def createDestinationFilePath(rootDestinationDir, phenotyping_center, pipeline_stable_id, procedure, parameter, download_file_path):
    directory="/".join([rootDestinationDir,phenotyping_center, pipeline_stable_id,procedure,parameter])
    return directory

def processFile(observation_id,  rootDestinationDir, phenotyping_center,pipeline_stable_id, procedure, parameter, downloadFilePath):
        global totalNumberOfImagesWeHave
        global responseFailed
        global numberOfImageDownloadAttemps
        directory = createDestinationFilePath(rootDestinationDir, phenotyping_center, pipeline_stable_id, procedure,parameter, downloadFilePath)
        dstfilename=directory+"/"+str(downloadFilePath.split('/')[-1])
        if dstfilename in uniqueUris:
            print '---------------------!!!!!!!!!!error the filePath is not unique and has been specified before:'+dstfilename+' downloadFilePath='+downloadFilePath
        uniqueUris.add(dstfilename)
        fullResolutionFilePath=dstfilename.split(splitString,1)[1]
        #print 'saving file to '+dstfilename
        if os.path.isfile(dstfilename):
            #print("file already here")
            totalNumberOfImagesWeHave+=1
            #great file already here as should be - now check the size of the file
            size=os.path.getsize(dstfilename)
            #print("file size on nfs="+str(size))
            head=requests.head(downloadFilePath)
            if 'Content-Length' in head.headers:
                remoteSize=head.headers['Content-Length']
                #print("remote file size="+str(remoteSize))
                if int(size) < int(remoteSize) :
                    removeFilesSet.add(downloadFilePath)
                    print(downloadFilePath+"  file size on nfs="+str(size)+" remote file size="+str(remoteSize)+"should download a new file and remove file image from omero")
                    os.remove(dstfilename)
                    print("removed file:"+dstfilename)
            else:
                print("Content-Length is None for "+downloadFilePath+ "  status code="+str(head.status_code))
#         else:
#                 numberOfImageDownloadAttemps+=1
#                 print 'saving file to '+dstfilename
#                 response=requests.get(downloadFilePath, stream=True)
#                 #print response.status_code
#                 if response.status_code != 200:
#                     print "Error status code is not 200="+str(response.status_code)+"downloadFilePath:"+downloadFilePath
#                     responseFailed+=1
#                 if response.status_code == 200:
#                     totalNumberOfImagesWeHave+=1
#                     #check directory exists before trying to write file and if not then make it
#                     if not os.path.exists(directory):
#                         os.makedirs(directory)
# 
#                     with open(dstfilename, 'wb') as f:
#                         for chunk in response.iter_content(1024):
#                             f.write(chunk)
   
        if totalNumberOfImagesWeHave%10000==0 :
            print "totalNumber of images we have="+str(totalNumberOfImagesWeHave)



if __name__ == "__main__":
    main(sys.argv[1:])



