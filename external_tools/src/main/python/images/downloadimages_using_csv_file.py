#!/usr/bin/python

"""Download images from DCC using information in a csv file
Allows download of images using information from a csv file instead
of the solr core which the original downloadimages.py script uses. This is
for DR12, but may be used for future data-releases.

See also downloadimages.py and downloadimages_using_media_api.py

"""
import sys
import os
import requests
import argparse
import csv

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
    parser.add_argument('-i', '--input-file', required=True,
                        dest='inputFilePath',
                        help='Path to csv file containing images info'
    )
    parser.add_argument('--not-downloaded', dest='notDownloadedOutputPath',
                        help='path to save list of files that could not be downloaded'
    )

    args = parser.parse_args()

    rootDestinationDir = args.initialDestinationDir
    finalDestinationDir = args.finalDestinationDir
    inputFilePath = args.inputFilePath

    print "running python image download script for impc images"

    print 'rootDestinationDir is "', rootDestinationDir

    notDownloaded = runWithCsvFileAsDataSource(rootDestinationDir, finalDestinationDir, inputFilePath)
    print str(len(notDownloaded)) + " files could not be downloaded"
    if len(notDownloaded) > 0:
        notDownloadedOutputPath =  args.notDownloadedOutputPath if args.notDownloadedOutputPath <> None else createNotDownloadedOutputPath(rootDestinationDir)
        with open(notDownloadedOutputPath, 'wt') as fid:
            fid.writelines(notDownloaded)

        print "Written files that could not be downloaded to " + notDownloadedOutputPath

def runWithCsvFileAsDataSource(rootDestinationDir, finalDestinationDir, inputFilePath):
    """
        Download images using CSV file as the datasource.
        Return urls that cannot be downloaded
    """

    notDownloaded = []
    numFound=0

    # Preset observation ID. This is passed to processFile in the
    # loop below but is not used by the function.
    # ToDo: remove from function definition
    observation_id=None

    # We get the files we are interested in from the CSV file
    # Assume rows  headings are as follows:
    #     0 - observation_id
    #     1 - download_file_path
    #     2 - phenotyping_center
    #     3 - pipeline_stable_id
    #     4 - procedure_stable_id
    #     5 - datasource_name
    #     6 - parameter_stable_id
    with open(inputFilePath, 'r') as fid:
        csv_reader = csv.reader(fid)

        # Skip header
        csv_reader.next()

        for row in csv_reader:
            numFound += 1
            download_file_path=row[1].lower()
            if download_file_path.find('mousephenotype.org') < 0 or \
                    download_file_path.endswith('.mov') or \
                    download_file_path.endswith('.fcs') or \
                    download_file_path.endswith('.bz2'):
                continue

            phenotyping_center = row[2]
            pipeline_stable_id = row[3]
            procedure_stable_id = row[4]
            parameter_stable_id = row[5]
            if len(phenotyping_center) == 0 or \
               len(pipeline_stable_id) == 0 or \
               len(procedure_stable_id) == 0 or \
               len(parameter_stable_id) == 0:
            
                print "Did not receive a required field - " + \
                      "phenotyping_center='" + phenotyping_center + \
                      "', pipeline_stable_id='" + pipeline_stable_id + \
                      "', procedure_stable_id='" + procedure_stable_id + \
                      "', parameter_stable_id='" + parameter_stable_id + \
                      "' - not downloading " + download_file_path
                notDownloaded.append(download_file_path+'\n')
                continue
            downloaded = processFile(observation_id, rootDestinationDir, finalDestinationDir, phenotyping_center,pipeline_stable_id, procedure_stable_id, parameter_stable_id, download_file_path)
            if not downloaded:
                notDownloaded.append(download_file_path+'\n')
        
    print 'number found in CSV file = '+str(numFound)+' number of images not downloaded = '+str(len(notDownloaded))

    return notDownloaded

if __name__ == "__main__":
    main(sys.argv[1:])
