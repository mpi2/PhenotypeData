"""Test the ImageDownloader class

"""

from pathlib import Path
import shutil

import pytest

from .context import scripts

from .test_environment_utils import _setup_test_environment, _teardown_test_environment;
from scripts.imagedownloader import ImageDownloader

# TODO: Put setup code in class method -> setup_class!!!
# TODO: Use pytest fixtures if possible
# Start with a new test environment
_teardown_test_environment()
_setup_test_environment()

base_path = Path(Path(__file__).parent)

initial_destination_dir = base_path.joinpath("test_data","test_download_images","initial_destination_dir","dr_15")
final_destination_dir = base_path.joinpath("test_data","test_download_images","final_destination_dir")
input_file_path = base_path.joinpath("test_data","test_download_images","initial_destination_dir","test_download_images_using_csv_file_impc_images_input.csv")
checksums_path = base_path.joinpath("test_data","test_download_images","final_destination_dir","test_download_images_using_csv_file_checksums.csv")
not_downloaded_output_path = base_path.joinpath("test_data","test_download_images","initial_destination_dir","not_downloaded.txt")

image_downloader = ImageDownloader(
                        input_file_path=input_file_path,
                        checksums_path=checksums_path,
                        initial_destination_dir=initial_destination_dir,
                        final_destination_dir=final_destination_dir,
                        not_downloaded_output_path=not_downloaded_output_path)

image_downloader.set_verbose(True)

n_downloaded = image_downloader.download_images()

class TestImageDownloader:
    @classmethod
    def teardown_class(cls):
        _teardown_test_environment()

    def test_checksums_read(self):
        # Test that it can read Checksums file
        n_checksums = len(image_downloader.dfp_checksum_map.keys())
        assert(n_checksums == 22)
    
    def test_checksum_final_dest_dir_map(self):
        """Check the mapping of checksums to nfspaths for final dest dir"""
        assert(len(image_downloader.checksum_final_dest_map.keys()) == 11)
    
    def test_checksum_initial_dest_dir_map(self):
        """Check the mapping of checksums to nfspaths for final dest dir"""
        assert(len(image_downloader.checksum_initial_dest_map.keys()) == 2)
    
    def test_correct_number_of_files_downloaded(self):
        """Check that the correct number of files were downloaded"""
        #initial_destination_dir = Path(image_downloader.initial_destination_dir)
        #n_files = len([f for f in initial_destination_dir.rglob("*.*")])
        assert(n_downloaded == 12)
    
    def test_only_expected_files_present(self):
        """Check download directories contain all and only the expected files"""
        
        n_expected_files = 0
        n_verified_files = 0
    
        test_data_dir = base_path.joinpath("test_data")
        expected_files_path = test_data_dir.joinpath("test_download_images_using_csv_file_expected_files.txt")
        with expected_files_path.open() as fid:
            for p in fid.readlines():
                path = test_data_dir.joinpath(p.strip())
                if path.is_file():
                    n_verified_files += 1
                else:
                    print(f"{path} not found")
                n_expected_files += 1
        
        assert(n_verified_files == n_expected_files)
        
        # Check no extra files are present
        n_files = len([f for f in test_data_dir.joinpath("test_download_images").rglob("*.*")])
        assert(n_files == n_expected_files)
