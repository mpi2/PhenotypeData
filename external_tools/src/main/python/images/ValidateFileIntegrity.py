"""
    Validate the integrity of image files

    In data-release 7 and 8 we had issues with image files that were corrupt.
    This was causing problems when Omero tried to upload them.

    This script checks the filetypes specified and reports any that seem 
    corrupt. It does this by attempting to load them using the imread function
    in matplotlib.pyplot
"""
import os
import sys
import argparse
import logging

# Import pyplot using 'agg' backend as there is no display on the server
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

# Use SimpleItk for dicom images use readDicom from below
import qc_helper as helper


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description='Run script to verify integrity of image files')
    parser.add_argument('-d', '--rootDir', dest='rootDir',
                        help='Root directory to start search for images')
    parser.add_argument('-t', '--filetypes', dest='filetypes',
                        default='jpg,jpeg,tif,tiff,png,dcm,bmp',
                        help='comma separated list of filetypes to verify')
    parser.add_argument('--logfile-path', dest='logfilePath', default=None,
                        help='path to save logfile')
    parser.add_argument('-f', '--filelist-path', dest='filelist_path',
                        help='path to file containing files to check')
    parser.add_argument('-o', '--output-path', dest='outputPath',
                        help='path to save list of corrupt images. If not supplied no list is saved but the paths to the corrupt images could be extracted from the log file')
    args = parser.parse_args()
    
    # Configure logger - if logging output file not specified create in this
    # directory with timestamp
    if args.logfilePath is None or args.logfilePath=="":
        import time
        import datetime
        t = time.time()
        tstamp = datetime.datetime.fromtimestamp(t).strftime('%Y%m%d_%H%M%S')
        logfile_path = "validate_file_integrity_" + tstamp + ".log"
    else:
        logfile_path = args.logfilePath

    log_format = '%(asctime)s - %(name)s - %(levelname)s:%(message)s'
    logging.basicConfig(format=log_format, filename=logfile_path,
                        level=logging.INFO)
    log_formatter = logging.Formatter(log_format)
    logger = logging.getLogger('ValidateFileIntegrity')
    root_logger = logging.getLogger()
    
    console_handler = logging.StreamHandler()
    console_handler.setFormatter(log_formatter)
    root_logger.addHandler(console_handler)

    logger.info("running main method to validate integrity of the following image types: " + args.filetypes)
    
    # List of filetypes to check - including '.'
    filetypes = args.filetypes.split(',')
    for i in range(len(filetypes)):
        if filetypes[i][0] != '.':
            filetypes[i] = "."+filetypes[i]
            
    nfs_file_list = []
    # If --filelist-path is preset simply use this list
    if args.filelist_path is not None:
        logger.info('loading list of files to check from "' + args.filelist_path + '"')
        with open(args.filelist_path,'rt') as fid:
            for f in fid.readlines():
                f2 = f.strip('\n')
                ext = os.path.splitext(f2)[-1]
                try:
                    filetypes.index(ext)
                    nfs_file_list.append(f2)
                except ValueError:
                    continue
    elif args.rootDir is not None:
        logger.info('rootDir is "' + args.rootDir + '"')
        # Get the files in NFS
        file_tuple = os.walk(args.rootDir)
        nfs_file_list = []
        for ft in file_tuple:
            for f in ft[2]:
                ext = os.path.splitext(f)[-1]
                try:
                    filetypes.index(ext)
                    nfs_file_list.append(os.path.join(ft[0],f))
                except ValueError:
                    continue
    else:
        logger.error("At least one of --filelist-path or --rootDir must be supplied. Exiting")
        sys.exit(-1)

    logger.info("Number of files from NFS = " + str(len(nfs_file_list)))

    n_invalid = 0
    corrupt_files = []
    for f in nfs_file_list:
        try:
            if f.endswith('dcm'):
                im = helper.readDicom(f)
            else:
                im = plt.imread(f)
            # If image is empty add it to corrupt files list
            # KB 28/06/2021 - this seems unintuitive, but in dr14 there were
            # two images with no data!
            if im.size < 1:
                raise Exception(f + " does not contain image data")
        except Exception as e:
            logger.error("Problem with " + f + ". Error was: " + str(e))
            n_invalid += 1
            corrupt_files.append(os.path.abspath(f)+'\n')

            

    logger.info("Number of invalid files: " + str(n_invalid))
    
    if n_invalid > 0 and args.outputPath is not None:
        if os.path.isfile(args.outputPath):
            try:
                with open(args.outputPath, 'rt') as fid:
                    fnames = [f.strip('\n') for f in fid.readlines()]
                    for f in fnames:
                        corrupt_files.append(os.path.abspath(f) + '\n')
            except Exception as e:
                print(args.outputPath + " already exists. However, " + \
                      "attempt to read it and merge current results " + \
                      "with it failed. Will therefore overwrite it. " + \
                      "Error was: " + str(e))
            set_corrupt_files = set(corrupt_files)
            corrupt_files = list(set_corrupt_files)
            corrupt_files.sort()
        try:
            with open(args.outputPath, 'wt') as fid:
                fid.writelines(corrupt_files)

            logger.info("Written paths of corrupt images to " + args.outputPath)
        except Exception as e:
            logger.error("Problem writing paths of corrupt images to " + args.outputPath + ". Error was: " + str(e))

