"""
    Script to move corrupt images to 'dirty' directory
    
    Reads list of images to move. Does not verify that images are corrupt - 
    Simply moves to 'dirty' directory of appropriate data-release creating
    the required directory structure
"""

import os
import argparse

parser = argparse.ArgumentParser(
            description="Move corrupt images to 'dirty' dir")

parser.add_argument('-i', dest='inputFiles', required=True,
                    help='File containing list of images to move'
)
parser.add_argument('-s', dest='splitString',
                    help='token to separate the basedir from input files'
)
parser.add_argument('-r', dest='replacementString',
                    help='String to replace the split string with'
)                    
parser.add_argument('-d', dest='destDirBase', required=True,
                    help='Path to the base of the destination dir'
)                    

args = parser.parse_args()

input_files = args.inputFiles
split_string = "" if args.splitString is None else args.splitString
replacement_string = "" if args.replacementString is None else args.replacementString



with open(input_files,'rt') as f:
    fnames = [fname.strip('\n') for fname in f.readlines()]
    for fname in fnames:
        fname = fname.replace(' ','\ ')
        fname2 = fname.replace(split_string, replacement_string)
        if os.path.exists(fname2):
            continue
        out_dir = os.path.dirname(fname2)
        if not os.path.isdir(out_dir):
            os.makedirs(out_dir)
        command = "mv " + fname + " " + fname2
        print(command)
        os.system(command)
