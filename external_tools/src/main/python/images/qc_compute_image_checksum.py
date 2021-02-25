"""Compute checksum for image to allow identification of duplicates

"""

import argparse
import hashlib
from pathlib import Path
import pandas as pd

def checksum(im_path, hfunc="sha1", chunk_size=4096):
    """Compute checksum of file at given path

    inputs:
        im_path: String: Path to file to read
        hfunc: String: hashlib function to use for hash. Options are:
                           sha1, sha224, sha256, sha384, sha512, blake2b
                           blake2s, md5
        chunk_size: int: size of binary chunks to use in reading file

    returns:
        checksum_value: String: String of hexadecimal value of checksum
    """
    
    hash_func = eval(f"hashlib.{hfunc}()")
    with open(im_path, "rb") as fid:
        for chunk in iter(lambda: fid.read(chunk_size), b""):
            hash_func.update(chunk)
    return hash_func.hexdigest()

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Compute checksum of binary file(s) in a dir")

    parser.add_argument("-d", dest="input_dir", required=True,
        help="Top level directory containing files of interest"
    )
    parser.add_argument("-o", dest="output_path", default="checksums.csv",
        help="Path for saving csv containing checksum values"
    )
    parser.add_argument("-p", dest="pattern", default="*.*",
        help="Pattern to search in the given directory - remember to escape wildcards e.g. use \\*.png for all png files"
    )
    parser.add_argument("-a", "--algorithm", dest="algorithm",
        default="sha1", help="algorithm to use for HASH. Default is sha1"
    )
    args = parser.parse_args()
    
    base_path = Path(args.input_dir)
    pattern = args.pattern
    algorithm = args.algorithm

    data = [
        (checksum(f.absolute(),algorithm), str(f.absolute())) 
        for f in base_path.rglob(pattern) if f.is_file()
    ]
    
    if len(data) > 0:
        df = pd.DataFrame(data=data, columns=(algorithm, "path"))
        df.to_csv(args.output_path, index=False)
        print(f"Written checksums to {args.output_path}")
    else:
        "No files found in {base_path} with pattern:{pattern} - exiting"
