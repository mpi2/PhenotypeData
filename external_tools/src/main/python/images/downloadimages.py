#!/usr/bin/python

#this program gets the download_file_paths (http mousephenotype uris) from the experiment core and then downloads the images 
import os
import requests
import json
import sys
import os.path
import sys
import argparse
from OmeroPropertiesParser import OmeroPropertiesParser

responseFailed=0
numberOfImageDownloadAttemps=0
totalNumberOfImagesWeHave=0
numFoundInSolr=0

uniqueUris=set()

def main(argv):
    """Download images referred to in solr index onto holding area on disk"""
    
    parser = argparse.ArgumentParser(
        description='Download images referred to in solr index onto holding area on disk'
    )
    parser.add_argument('-d1', '--initialDestinationDir', dest='initialDestinationDir',
                        help='Directory for root of holding destination to store images'
    )
    parser.add_argument('-d2', '--finalDestinationDir', dest='finalDestinationDir',
                        help='Directory for root of final destination to store images'
    )
    parser.add_argument('-s', '--rootSolrUrl', dest='rootSolrUrl',
                        help='URL to root of solr index'
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
    parser.add_argument('--not-downloaded', dest='notDownloadedOutputPath',
                        help='path to save list of files that could not be downloaded'
    )
    parser.add_argument('--pass', dest='komp2Pass',
                        help='Password for komp2db'
    )
    parser.add_argument('--profile', dest='profile', default='dev',
                        help='profile from which to read config: dev, prod or live')
    parser.add_argument('--profile-path', dest='profilePath',
                        help='Explicit path to file from which to read ' + \
                             'profile e.g. ' + \
                             '/home/kola/application.properties. ' + \
                             'Overrides value of --profile argument.'
    )
    parser.add_argument('-m', '--map-urls', dest='mapUrls', 
                        action='store_true', default=False,
                        help='Flag to indicate whether to map urls'
    )
    parser.add_argument('-mp', '--map-urls-path', dest='mapUrlsPath',
                        help='Path to mapping file for old -> new urls. This option can only be used if -m flag is invoked'
    )

    args = parser.parse_args()
    # Ensure if mapping urls path supplied the mapping url flag is set
    if args.mapUrlsPath is not None and not args.mapUrls:
        print "A mapUrlsPath is supplied but the mapping url flag is not set. Please re-run including the '-m' flag for mapping urls or excluding -mp if no mapping is required"
        return
    elif args.mapUrls:
        if args.mapUrlsPath is None:
            map_urls_path = os.path.join(os.path.dirname(__file__),'../../resources/harwell_old_url_to_new_url_map.txt')
        else:
            map_urls_path = args.mapUrlsPath

        global url_map
        with open(map_urls_path, 'rt') as fid:
            url_map = {l.split()[0]:l.split()[1] for l in fid.readlines()}
        

    # Get values from property file and use as defaults that can be overridden
    # by command line parameters
    if args.profilePath is not None:
        try:
            pp = OmeroPropertiesParser()
            omeroProps = pp.getOmeroProps(args.profilePath)
        except Exception as e:
            print "Could not read application properties file from " + args.profilePath
            print "Error was: " + str(e)
            return
    else:
        try:
            pp = OmeroPropertiesParser(args.profile)
            omeroProps = pp.getOmeroProps()
        except Exception as e:
            print "Could not read application properties file for profile " + args.profile
            print "Error was: " + str(e)
            return

    rootSolrUrl = args.rootSolrUrl if args.rootSolrUrl<>None else omeroProps['solrurl']
    #solrQuery="""experiment/select?q=observation_type:image_record&fq=download_file_path:(download_file_path:*bhjlk01.jax.org/images/IMPC_ALZ_001/*%20AND%20!download_file_path:*.mov)&fl=id,download_file_path,phenotyping_center,pipeline_stable_id,procedure_stable_id,datasource_name,parameter_stable_id&wt=json&indent=on&rows=10000000"""
    #solrQuery="""experiment/select?q=observation_type:image_record&fq=(download_file_path:*mousephenotype.org*%20AND%20!download_file_path:*.mov%20AND%20!download_file_path:*.bz2)&fl=id,download_file_path,phenotyping_center,pipeline_stable_id,procedure_stable_id,datasource_name,parameter_stable_id&wt=json&indent=on&rows=10000000"""
    solrQuery="""experiment/select?q=observation_type:image_record&fq=(download_file_path:*mousephenotype.org*%20AND%20!download_file_path:*.mov%20AND%20!download_file_path:*.bz2)&fl=id,download_file_path,phenotyping_center,pipeline_stable_id,procedure_stable_id,datasource_name,parameter_stable_id&wt=json&indent=on&rows=10"""

    #note cant split this url over a few lines as puts in newlines into url which doesn't work
    rootDestinationDir = args.initialDestinationDir if args.initialDestinationDir<>None else omeroProps['rootdestinationdir'] 
    finalDestinationDir = args.finalDestinationDir if args.finalDestinationDir<>None else omeroProps['finaldestinationdir'] 
    print "running python image download script for impc images"

    print 'rootDestinationDir is "', rootDestinationDir
    solrUrl=rootSolrUrl+solrQuery;
    print 'solrUrl', solrUrl

    notDownloaded = runWithSolrAsDataSource(solrUrl, rootDestinationDir, finalDestinationDir, args.mapUrls)
    print str(len(notDownloaded)) + " files could not be downloaded"
    if len(notDownloaded) > 0:
        notDownloadedOutputPath =  args.notDownloadedOutputPath if args.notDownloadedOutputPath <> None else createNotDownloadedOutputPath(rootDestinationDir)
        with open(notDownloadedOutputPath, 'wt') as fid:
            fid.writelines(notDownloaded)

        print "Written files that could not be downloaded to " + notDownloadedOutputPath

def runWithSolrAsDataSource(solrUrl, rootDestinationDir, finalDestinationDir, map_urls):
    """
        Download images using Solr as the datasource.
        Return urls that cannot be downloaded
    """

    notDownloaded = []
    v = json.loads(requests.get(solrUrl).text)
    docs=v['response']['docs']
    numFoundInSolr=v['response']['numFound']
    for doc in docs:
        download_file_path=doc['download_file_path']
        download_file_path=download_file_path.lower()
        # replace old urls if necessary
        if map_urls:
            old_url = os.path.split(download_file_path)[0] + "/"
            old_url = old_url.replace('http://images.mousephenotype.org','')
            new_url = url_map[old_url]
            download_file_path =  download_file_path.replace(old_url, new_url)
        datasource_id=doc['datasource_name']
        phenotyping_center=doc['phenotyping_center']
        #experiment=doc['experiment']
        pipeline_stable_id=doc['pipeline_stable_id']
        observation_id=doc['id']
        procedure_stable_id=doc['procedure_stable_id']
        parameter_stable_id=doc['parameter_stable_id']
        downloaded = processFile(observation_id, rootDestinationDir, finalDestinationDir, phenotyping_center,pipeline_stable_id, procedure_stable_id, parameter_stable_id, download_file_path)
        if not downloaded:
            notDownloaded.append(download_file_path+'\n')
        
    print 'number found in solr='+str(numFoundInSolr)+' number of failed responses='+str(responseFailed)+' number of requests='+str(numberOfImageDownloadAttemps)+' total totalNumberOfImagesWeHave='+str(totalNumberOfImagesWeHave)
    return notDownloaded

def createDestinationFilePath(rootDestinationDir, phenotyping_center, pipeline_stable_id, procedure, parameter, download_file_path):
    directory="/".join([rootDestinationDir,phenotyping_center, pipeline_stable_id,procedure,parameter])
    return directory

def createNotDownloadedOutputPath(rootDestinationDir):
    """
        Create path to store files that could not be downloaded.
        This uses the current date and time and the root destination dir
    """
    
    import time
    from datetime import datetime

    today = datetime.fromtimestamp(time.time())
    return os.path.join(rootDestinationDir, today.strftime("%Y%m%d_%H%M%S_could_not_download.txt"))

def processFile(observation_id,  rootDestinationDir, finalDestinationDir, phenotyping_center,pipeline_stable_id, procedure, parameter, downloadFilePath):
    global totalNumberOfImagesWeHave
    global responseFailed
    global numberOfImageDownloadAttemps

    downloadFilename = os.path.split(downloadFilePath)[-1]
    holding_directory = createDestinationFilePath(rootDestinationDir, phenotyping_center, pipeline_stable_id, procedure,parameter, downloadFilePath)
    final_directory = createDestinationFilePath(finalDestinationDir, phenotyping_center, pipeline_stable_id, procedure,parameter, downloadFilePath)
    holding_filename = os.path.join(holding_directory, downloadFilename)
    clean_filename = os.path.join(final_directory, downloadFilename)
    print "clean_filename = " + clean_filename

    if holding_filename in uniqueUris:
        print '---------------------!!!!!!!!!!error the filePath is not unique and has been specified before:'+holding_filename
    uniqueUris.add(holding_filename)
    #print 'saving file to '+dstfilename
    if os.path.isfile(clean_filename):
        print clean_filename + " already exists in 'clean' directory - not downloading"
        totalNumberOfImagesWeHave+=1
    elif os.path.isfile(holding_filename):
        #print "file already here"
        totalNumberOfImagesWeHave+=1
    else:
        numberOfImageDownloadAttemps+=1
        print 'saving file to '+holding_filename
        response=requests.get(downloadFilePath, stream=True)
        #print response.status_code
        if response.status_code != 200:
            print "Error status code is not 200="+str(response.status_code)+"downloadFilePath:"+downloadFilePath
            responseFailed+=1
            return False
        if response.status_code == 200:
            totalNumberOfImagesWeHave+=1
            #check directory exists before trying to write file and if not then make it
            if not os.path.exists(holding_directory):
                os.makedirs(holding_directory)

            with open(holding_filename, 'wb') as f:
                for chunk in response.iter_content(1024):
                    f.write(chunk)
   
    if totalNumberOfImagesWeHave%10000==0 :
        print "totalNumber of images we have="+str(totalNumberOfImagesWeHave)

    return True

if __name__ == "__main__":
    main(sys.argv[1:])
