#!/usr/bin/env python

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
not_downloaded_output_path : str
    Path to store urls to images that could not be downloaded.
"""

import sys
import argparse
from ImageDownloader import ImageDownloader


parser = argparse.ArgumentParser(
    description='Download images using DCC media API onto holding area on disk'
)
parser.add_argument('-d1', '--initial-destination-dir', required=True,
                    help='Directory for root of holding destination to store images'
)
parser.add_argument('-d2', '--final-destination-dir', required=True,
                    help='Directory for root of final destination to store images'
)
parser.add_argument('-i', '--input-file-path', required=True,
                    help='Path to csv file containing images info'
)
parser.add_argument('-c', '--checksums-file-path', required=True,
                    help='Path to csv file containing checksums of existing images'
)
parser.add_argument('--not-downloaded-output-path',
                    help='path to save list of files that could not be downloaded'
)
parser.add_argument('-v', '--verbose', action='store_true',
                    help='print verbose messages'
)

args = parser.parse_args()

# Set up downloader object
downloader = ImageDownloader(args.input_file_path,
                             args.checksums_file_path,
                             args.initial_destination_dir,
                             args.final_destination_dir,
                             args.not_downloaded_output_path)

# ToDo: Not yet implemented - currently hardcoded
#downloader.set_extensions_to_ignore(extenstions_to_ignore)

if args.verbose:
    downloader.set_verbose(args.verbose)

# Get the images
downloader.download_images()

# Print summary of output
print(f"Downloaded {downloader.n_downloaded} images")
