#!/usr/bin/python

"""Script to pre-generate thumbnails (and image pyramids?) to enhance 
performance of omero. The URLs are constructed using omero ids from the 
csv file used to build the images core starting from data-release 12
"""

import os
import sys
import requests
import argparse

import csv

from OmeroPropertiesParser import OmeroPropertiesParser

def main(argv):
    print "running main method of omero_crawler_using_csv_file!!"

    parser = argparse.ArgumentParser(
        description='Pre-generate thumbnails (and image pyramids?) by requesting for the thumbnails and images from omero. URLs are constructed using omero ids from a csv file'
    )
    parser.add_argument('-i', '--input-file', dest='inputFilePath',
                        required=True,
                        help='Path to CSV file containing images info'
    )
    parser.add_argument('-b', '--begin', dest='begin',
                        default=0, type=int,
                        help='Row in file to start url generation from'
    )
    parser.add_argument('-e', '--end', dest='end', 
                        default=100000000, type=int,
                        help='row in file to end url generation')
    parser.add_argument('-o', '--omero-base-url', dest='omeroBaseUrl',
                        default="https://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/",
                        help='Base url for querying images and thumbnails in omero')
 
    args = parser.parse_args()
    
    # Count number of rows to process
    with open(args.inputFilePath, 'rb') as fid:
        csv_reader = csv.reader(fid)
        n_rows = sum(1 for row in csv_reader)
        str_n_rows = str(n_rows-1)
    
    if args.end < n_rows:
        str_n_rows = str(args.end)
        

    # Get handle to csv file and create urls
    rows_processed = 0

    with open(args.inputFilePath, 'rb') as fid:
        csv_reader = csv.reader(fid)

        # Get index for omero_id field
        header = csv_reader.next()
        try:
            omero_index = header.index("omero_id")
        except ValueError as v:
            print "File does not have field for omero_id - cannot continue!"
            sys.exit(-1)

        for row in csv_reader:
            # Skip to starting row
            if rows_processed < args.begin:
                rows_processed += 1
                continue

            if rows_processed > 0 and rows_processed % 1000 == 0:
                print "Processed " + str(rows_processed) + " of " + str_n_rows
                sys.stdout.flush()
            omero_id=row[omero_index]
            if int(omero_id) < 0:
                continue

            thumbnail_url = os.path.join(args.omeroBaseUrl, "render_thumbnail", omero_id)
            r = requests.get(thumbnail_url)
            if (r.status_code != requests.codes.ok):
                print "row="+str(rows_processed) +", url="+ thumbnail_url+", status="+str(r.status_code)

                # Request a full image if response not 200 to generate
                # a pyramid
                image_url = os.path.join(args.omeroBaseUrl, "render_image", omero_id)
                r2 = requests.get(image_url)
            rows_processed += 1
            if rows_processed > args.end:
                break

if __name__ == "__main__":
    main(sys.argv[1:])
