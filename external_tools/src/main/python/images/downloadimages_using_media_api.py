#!/usr/bin/python

"""Download images from DCC using media API
Allows download of images using the media API provided by the DCC instead
of the solr core which the original downloadimages.py script uses. This is
necessary sometimes to get a headstart on the data-release.

See also downloadimages.py and downloadimages_using_xml.py

"""
import sys
import os
import requests
import json
import argparse

# Import helper functions from original downloadimages.py
from downloadimages import createDestinationFilePath, createNotDownloadedOutputPath, processFile

uniqueUris=set()

def main(argv):
    """Download images using DCC media API onto holding area on disk"""
    
    parser = argparse.ArgumentParser(
        description='Download images using DCC media API onto holding area on disk'
    )
    parser.add_argument('-d1', '--initialDestinationDir', required=True,
                        dest='initialDestinationDir',
                        help='Directory for root of holding destination to store images'
    )
    parser.add_argument('-d2', '--finalDestinationDir', required=True,
                        dest='finalDestinationDir',
                        help='Directory for root of final destination to store images'
    )
    parser.add_argument('--not-downloaded', dest='notDownloadedOutputPath',
                        help='path to save list of files that could not be downloaded'
    )

    args = parser.parse_args()

    rootDestinationDir = args.initialDestinationDir
    finalDestinationDir = args.finalDestinationDir

    print "running python image download script for impc images"

    print 'rootDestinationDir is "', rootDestinationDir

    notDownloaded = runWithMediaApiAsDataSource(rootDestinationDir, finalDestinationDir)
    print str(len(notDownloaded)) + " files could not be downloaded"
    if len(notDownloaded) > 0:
        notDownloadedOutputPath =  args.notDownloadedOutputPath if args.notDownloadedOutputPath <> None else createNotDownloadedOutputPath(rootDestinationDir)
        with open(notDownloadedOutputPath, 'wt') as fid:
            fid.writelines(notDownloaded)

        print "Written files that could not be downloaded to " + notDownloadedOutputPath

def runWithMediaApiAsDataSource(rootDestinationDir, finalDestinationDir):
    """
        Download images using Media API as the datasource.
        Return urls that cannot be downloaded
    """

    notDownloaded = []
    numFound=0

    # We get the files we are interested in for each site using the 
    # media API
    sites = [
        ('bcm', 'BCM',),
        ('gmc','HMGU',),
        ('h', 'MRC Harwell'),
        ('ics', 'ICS',),
        ('j', 'JAX',),
        ('tcp', 'TCP'),
        ('ning', 'NING',),
        ('rbrc', 'RBRC',),
        ('ucd', 'UC Davis',),
        ('wtsi', 'WTSI',),
        ('kmpc', 'KMPC',),
        ('ccpcz', 'CCP-IMG',),
    ]


    # Preset observation ID. This is passed to processFile in the
    # loop below but is not used by the function.
    # ToDo: remove from function definition
    observation_id=None

    for site, phenotyping_center in sites:
        query_string = "https://api.mousephenotype.org/media/dccUrl/" +\
            site + "?status=done" # +"&start=0&resultsize=2"
        print query_string

        v = json.loads(requests.get(query_string).text)
        try:
            docs = v['mediaFiles']
        except KeyError as key_error:
            print "WARNING - no media files returned for site: " + site
            continue
            
        numFound += len(docs)

        for doc in docs:
            download_file_path=doc['dccUrl']
            download_file_path=download_file_path.lower()
            if download_file_path.find('mousephenotype.org') < 0 or \
                    download_file_path.endswith('.mov') or \
                    download_file_path.endswith('.bz2'):
                continue


            # On 13/11/2019 got a KeyError for phenotyping centre. This 
            # should not happen, but code modified appropriately
            try:
                pipeline_stable_id=doc['pipelineKey']
                procedure_stable_id=doc['procedureKey']
                parameter_stable_id=doc['parameterKey']
            except KeyError as e:
                print "Key " + str(e)+  " not returned by solr - not downloading " + download_file_path
                notDownloaded.append(download_file_path+'\n')
                continue
            downloaded = processFile(observation_id, rootDestinationDir, finalDestinationDir, phenotyping_center,pipeline_stable_id, procedure_stable_id, parameter_stable_id, download_file_path)
            if not downloaded:
                notDownloaded.append(download_file_path+'\n')
        
    print 'number found in media API ='+str(numFound)+' number of images not downloaded = '+str(len(notDownloaded))

    return notDownloaded

if __name__ == "__main__":
    main(sys.argv[1:])
