"""Class to allow downloading of images from DCC

"""

import sys
from pathlib import Path
import csv
from requests import get
import hashlib

class ImageDownloader:
    def __init__(self,
                 input_file_path,
                 checksums_path,
                 initial_destination_dir,
                 final_destination_dir,
                 not_downloaded_output_path):

        # Some defaults
        self.verbose = False
        self.n_downloaded = 0
        self.checksum_col_name = "checksum" # 256 checksum
        self.download_file_path_col_name = "download_file_path"
        self.dfp_checksum_map = {}

        self.input_file_path = input_file_path
        self.checksums_path = checksums_path
        self.initial_destination_dir = initial_destination_dir
        self.final_destination_dir = final_destination_dir
        self.not_downloaded_output_path = not_downloaded_output_path
        self.verbose = False

        # Create the download file path -> checksum map
        self._read_checksums()

        # Create dict mapping fnames (checksums) to filepaths in final_dest_dir
        self.checksum_final_dest_map = self._create_filename_to_nfspath_map(self.final_destination_dir)
        # Create dict mapping fnames (checksums) to filepaths in final_dest_dir
        self.checksum_initial_dest_map = self._create_filename_to_nfspath_map(self.initial_destination_dir)
        print("Checksum_initial_dest_map")
        print(self.checksum_initial_dest_map)

    def set_verbose(self, verbose_flag):
        """Set the verbosity of output messages

        """
        self.verbose = verbose_flag

    def download_images(self):
        """Download images that are not currently on server"""
        
        self.n_downloaded = 0
        self.n_could_not_download = 0
        self.could_not_download = []
        # Check list of images to download vs images on server
        input_file = Path(self.input_file_path)
        with open(input_file, "rt") as fid:
            header_row = fid.readline().strip().split(",")
            dfp_index = self._get_column_index(header_row, self.download_file_path_col_name, input_file)
            phenotyping_center_index = self._get_column_index(header_row, "phenotyping_center", input_file)
            pipeline_stable_id_index = self._get_column_index(header_row, "pipeline_stable_id", input_file)
            procedure_stable_id_index = self._get_column_index(header_row, "procedure_stable_id", input_file)
            parameter_stable_id_index = self._get_column_index(header_row, "parameter_stable_id", input_file)

            # Process all download file paths in input file
            while True:
                row = fid.readline()
                if row == "":
                    break
                row = row.strip().split(",")
                dfp = row[dfp_index]
                if dfp in self.dfp_checksum_map:
                    checksum = self.dfp_checksum_map[dfp]
                else:
                    # Download file and compute checksum
                    # TODO: This is inefficient - we may download image again.
                    #       maybe use downloaded_flag to prevent this?
                    print(f"Checksum is not provided for {dfp} - downloading image to compute checksum.")
                    response = get(dfp)
                    if response.status_code == 200:
                        hash_func = hashlib.sha1()
                        hash_func.update(response.content)
                        checksum = hash_func.hexdigest()
                        # Add checksum to download file path - checksum map
                        self.dfp_checksum_map[dfp] = checksum
                    else:
                        print(f"Problem downloading {dfp}. Response was {response.status_code}")
                        checksum = None

                if checksum is None:
                    self.could_not_download.append(dfp)
                elif checksum not in self.checksum_final_dest_map and \
                     checksum not in self.checksum_initial_dest_map:

                    # Download file and if 200 write using checksum as filename
                    response = get(dfp)
                    if response.status_code == 200:
                        # Create path based on site,pipeline,procedure,parameter
                        ext = Path(dfp).suffix
                        path = Path(
                            self.initial_destination_dir,
                            row[phenotyping_center_index],
                            row[pipeline_stable_id_index],
                            row[procedure_stable_id_index],
                            row[parameter_stable_id_index],
                            checksum + ext
                        )
                        if not path.parent.is_dir():
                            path.parent.mkdir(parents=True)
                        
                        with open(path, "wb") as download_fid:
                            if self.verbose:
                                print(f"Saving {dfp} to {path}")
                            download_fid.write(response.content)
                        self.n_downloaded += 1
                    else:
                        print(f"Problem downloading {dfp}. Response was {response.status_code}")
                        self.could_not_download.append(dfp)

        self.n_could_not_download = len(self.could_not_download)
        self._write_could_not_download()
        return self.n_downloaded

    def save_images_mapped_to_checksums(self, output_path):
        """Save input csv file with column containing checksums"""
        
        input_file = Path(self.input_file_path)
        with open(input_file, "rt") as fid_in:
            header_row = fid_in.readline().strip().split(",")
            dfp_index = self._get_column_index(header_row, self.download_file_path_col_name, input_file)
            # Get column of checksum. If it does not exist the 
            # exit_on_error=False flag makes function return 'None'
            checksum_index = self._get_column_index(header_row, self.checksum_col_name, input_file, exit_on_error=False)
            output_file = Path(output_path)
            with open(output_file, "wt") as fid_out:
                if checksum_index is None:
                    header_row.append(self.checksum_col_name)
                fid_out.write(",".join(header_row) + "\n")
                
                # Process all download file paths in input file
                while True:
                    row = fid_in.readline()
                    if row == "":
                        break
                    row = row.strip().split(",")
                    dfp = row[dfp_index]
                    if dfp in self.dfp_checksum_map:
                        checksum = self.dfp_checksum_map[dfp]
                    else:
                        checksum = ""

                    if checksum_index is None:
                        row.append(checksum)
                    else:
                        row[checksum_index] = checksum

                    fid_out.write(",".join(row) + "\n")
        print(f"Written version of input file with checksums to {output_path}")
        

    def _write_could_not_download(self):
        """Write out names of files that could not be downloaded"""

        if self.n_could_not_download > 0:
            try:
                Path(self.not_downloaded_output_path).write_text("\n".join(self.could_not_download))
                if self.verbose:
                    msg = f"Written urls of {self.n_could_not_download} file(s)" + \
                          f" that could not be downloaded to {self.not_downloaded_output_path}"
            except FileNotFoundError as err:
                msg = f"Problem writing {self.n_could_not_download} file(s) to " + \
                      f"{self.could_not_download_path}. Error was: {err}"  

    def _get_column_index(self, header_row, column_name, input_file=None, exit_on_error=True):
        """Return index to column in header row of input_file"""
        try:
            return header_row.index(column_name)
        except ValueError:
            if input_file is not None:
                print(f"FATAL ERROR - no column named {column_name} in {input_file}. Exiting!")
                print(f"Columns present are: {header_row}")
            else:
                print(f"FATAL ERROR - no column named {column_name} in {header_row}. Exiting!")
            if exit_on_error:
                sys.exit(-1)
            else:
                return None

    def _create_filename_to_nfspath_map(self, base_nfs_path):
        """Create dict mapping filenames to nfs file paths"""
        
        mapping_dict = {}
        for f in Path(base_nfs_path).rglob("*.*"):
            # Need to EEI confocal images (Infact all of 3i?)
            fname = f.stem
            if fname in mapping_dict:
                print(f"Warning: duplicate for {fname} - {f}")
                mapping_dict[fname].append(f)
            else:
                mapping_dict[fname] = [f,]
        return mapping_dict
            
    def _read_checksums(self):
        """Helper function to create dict mapping download paths to checksums

        """
        
        # TODO: This will not be needed if we can get DCC to include checksum
        #       in date-release XMLs
        with open(self.checksums_path, "rt") as fid:
            csv_reader = csv.reader(fid)
            header = next(csv_reader)
            try:
                download_file_path_idx = header.index(self.download_file_path_col_name)
                checksum_idx = header.index(self.checksum_col_name)
            except ValueError as e:
                print("Fatal Error:")
                print(f"{str(e)} {header}")
                print("Exiting")
                sys.exit(-1)

            for row in csv_reader:
                download_file_path = row[download_file_path_idx]
                checksum = row[checksum_idx]
                
                if download_file_path in self.dfp_checksum_map:
                    if self.dfp_checksum_map[download_file_path] != checksum:
                        print(f"Warning! - {download_file_path} has more than one checksum: {self.dfp_checksum_map[download_file_path]} and {checksum}")
                else:
                    self.dfp_checksum_map[download_file_path] = checksum


