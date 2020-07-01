"""
    Script to files (mainly images) from one dir to another
    
    Reads list of files to move. If images does not verify whether they are
    corrupt - simply moves from one directory to that other creating the 
    required directory structure if necessary
"""

import os
import argparse

parser = argparse.ArgumentParser(
            description="Move files (images) from one dir to another")

parser.add_argument('-i', dest='inputFiles', required=True,
                    help='File containing list of files/images to move'
)
parser.add_argument('-s', dest='splitString',
                    help='token to separate the basedir from input files'
)
parser.add_argument('-r', dest='replacementString',
                    help='String to replace the split string with'
)                    

args = parser.parse_args()

input_files = args.inputFiles
split_string = "" if args.splitString is None else args.splitString
replacement_string = "" if args.replacementString is None else args.replacementString

n_success = 0
n_failed = 0
with open(input_files,'rt') as f:
    fnames = [fname.strip('\n') for fname in f.readlines()]
    n_total = len(fnames)
    if n_total == 0:
        print(f"No files to move found in {input_files}")
    for fname in fnames:
        try:
            fname2 = fname.replace(split_string, replacement_string)
            os.renames(fname, fname2)
            n_success += 1
        except Exception as e:
            str_err = str(e)
            print(f"Failed to move {fname2} error was: {str_err}")
            n_failed += 1

if n_success > 0:
    print(f"Successfully moved {n_success}/{n_total} files")
if n_failed > 0:
    print(f"Problems moving {n_failed}/{n_total} files")
