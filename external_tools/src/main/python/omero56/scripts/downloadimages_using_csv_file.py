#!/usr/bin/env python
"""Download images from DCC using checksums to prevent duplication

    This incarnation of download images uses checksums to prevent the download
    of duplicate images - a problem meaning about 33% of our images are 
    redundant.
    
"""
import sys
import os
import requests
import argparse
from .ImageDownloader import ImageDownloader


# Import helper functions from original downloadimages.py
#from downloadimages import createDestinationFilePath, createNotDownloadedOutputPath, processFile
from .downloadimages import processFile

uniqueUris=set()

def parse_args(args):
    """Get command line arguments using argparse
        parameters:
            args: list of arguments (if from command line exclude program name
                  i.e. sys.argv[1:])
        returns:
            object containing parsed arguments
    """
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
    parser.add_argument('-c', '--checksums-file', required=True,
                        dest='checksumsFilePath',
                        help='Path to csv file containing checksums of existing images'
    )
    parser.add_argument('--not-downloaded', dest='notDownloadedOutputPath',
                        help='path to save list of files that could not be downloaded'
    )

    return parser.parse_args(args)


# Set up downloader object
downloader = ImageDownloader(input_file_path,
                        checksums_path,
                        initial_destination_dir,
                        final_destination_dir,
                        not_downloaded_output_path)

downloader.set_extensions_to_ignore(extenstions_to_ignore)

if args.verbose:
    downloader.set_verbose(args.verbose)

# Get the images
downloader.get_images()

# Print summary of output
print("Downloaded {downloader.n_downloaded} images")

def downloadimages_using_csv_file(input_file_path, 
                                  checksums_path,
                                  initial_destination_dir, 
                                  final_destination_dir,
                                  not_downloaded_output_path=None):
    """Download images using DCC media API onto holding area on disk

    Parameters
    ----------
    input_file_path : str
        Path to the csv file containing details of images to download.
    checksums_path : str
        Path to the csv file containing details of checksums of existing files.
    initial_destination_dir : str
        Path to the holding directory to which images will be downloaded. 
        Usually ends with dr_xx, where xx is current data release.
    final_destination_dir : str
        Path to the more permanent storage for the images. This is usually a
        level above the subdirectories for individual data-releases, as files
        across all data-releases have to be scanned to ensure we do not upload
        a file we already have

    Returns
    -------
    notDownloaded: list of str
        A list of files that could not be downloaded
    """

    print("running python image download script for impc images")

    print(f"rootDestinationDir is {initial_destination_dir}")

    notDownloaded = runWithCsvFileAsDataSource(initial_destination_dir, final_destination_dir, input_file_path)
    print(f"{len(notDownloaded)} files could not be downloaded")
    #if len(notDownloaded) > 0:
    #    not_downloaded_output_path =  not_downloaded_output_path if not_downloaded_output_path is not None else createNotDownloadedOutputPath(initial_destination_dir)
     #   with open(not_downloaded_output_path, 'wt') as fid:
     #       fid.writelines(notDownloaded)

     #     print("Written files that could not be downloaded to {not_downloaded_output_path}")

def runWithCsvFileAsDataSource(initial_destination_dir, final_destination_dir, input_file_path, checksums_path):
    """
        Download images using CSV file as the datasource.
        Return urls that cannot be downloaded
    """

    notDownloaded = []
    numFound=0
    numDownloaded=0

    # We get the files we are interested in from the CSV file
    # Assume we have the following columns
    #     download_file_path
    #     phenotyping_center
    #     pipeline_stable_id
    #     procedure_stable_id
    #     parameter_stable_id

    with open(input_file_path, 'r') as fid:
        csv_reader = csv.reader(fid)

        # Find column with necessary indicies from header
        header_row = next(csv_reader)
        try:
            download_file_path_idx = header_row.index("download_file_path")
            phenotyping_center_idx = header_row.index("phenotyping_center")
            pipeline_stable_idx = header_row.index("pipeline_stable_id")
            procedure_stable_idx = header_row.index("procedure_stable_id")
            parameter_stable_idx = header_row.index("parameter_stable_id")
        except ValueError as e:
            print("Fatal Error:")
            print(f"{str(e)} {header_row}")
            print("Exiting")
            sys.exit(-1)

        for row in csv_reader:
            numFound += 1
            download_file_path=row[download_file_path_idx].lower()
            if download_file_path.find('mousephenotype.org') < 0 or \
                    download_file_path.endswith('.mov') or \
                    download_file_path.endswith('.fcs') or \
                    download_file_path.endswith('.bz2'):
                continue

            phenotyping_center = row[phenotyping_center_idx]
            pipeline_stable_id = row[pipeline_stable_idx]
            procedure_stable_id = row[procedure_stable_idx]
            parameter_stable_id = row[parameter_stable_idx]
            if len(phenotyping_center) == 0 or \
               len(pipeline_stable_id) == 0 or \
               len(procedure_stable_id) == 0 or \
               len(parameter_stable_id) == 0:
            
                print("Did not receive a required field - " + \
                      "phenotyping_center='" + phenotyping_center + \
                      "', pipeline_stable_id='" + pipeline_stable_id + \
                      "', procedure_stable_id='" + procedure_stable_id + \
                      "', parameter_stable_id='" + parameter_stable_id + \
                      "' - not downloading " + download_file_path)
                notDownloaded.append(download_file_path+'\n')
                continue
            downloaded = processFile(initial_destination_dir, final_destination_dir, phenotyping_center,pipeline_stable_id, procedure_stable_id, parameter_stable_id, download_file_path)
            if not downloaded:
                notDownloaded.append(download_file_path+'\n')
            else:
                numDownloaded += 1
        
    print(f"number found in CSV file = {numFound}")
    print(f"number of images successfully downloaded = {numDownloaded}")
    print(f"number of images not downloaded = {len(notDownloaded)}")

    return notDownloaded

if __name__ == "__main__":
    args = parse_args(sys.argv[1:])
    downloadimages_using_csv_file(input_file_path=args.inputFilePath,
        initial_destination_dir=args.initialDestinationDir,
        final_destination_dir=args.finalDestinationDir,
        not_downloaded_output_path=args.notDownloadedOutputPath)
