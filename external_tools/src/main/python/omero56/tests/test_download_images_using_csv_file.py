"""Test downloading images from DCC using csv. Need a root directory for
    images. Currently in ./test_data, but need more flexibility.

"""

from pathlib import Path
import shutil

from .context import scripts
from scripts.downloadimages_using_csv_file import runWithCsvFileAsDataSource


base_path = Path(Path(__file__).parent)

def _setup_test_environment():
    """Set up the directories and sample data needed for the test

    Creates a directory called ./test_data/test_download_images and
    creates some files in the final_destination_dir sub directory.

    Also copies sample checksum and input files needed during the download

    """
    
    # Create the base dir for these tests
    test_data_dir = base_path.joinpath('test_data')
    test_download_images_dir = test_data_dir.joinpath('test_download_images')
    if not test_download_images_dir.is_dir():
        test_download_images_dir.mkdir(parents=True)

    # Create the sample data
    initial_destination_dir = test_download_images_dir.joinpath("initial_destination_dir")
    if not initial_destination_dir.is_dir():
        initial_destination_dir.mkdir()
    
    impc_images_input_path = test_data_dir.joinpath('test_download_images_using_csv_file_impc_images_input.csv')
    shutil.copy(str(impc_images_input_path), str(initial_destination_dir))

    final_destination_dir = test_download_images_dir.joinpath("final_destination_dir")
    if not final_destination_dir.is_dir():
        final_destination_dir.mkdir()

    checksums_path = test_data_dir.joinpath('test_download_images_using_csv_file_checksums.csv')
    shutil.copy(str(checksums_path), str(final_destination_dir))

    expected_files_path = test_data_dir.joinpath('test_download_images_using_csv_file_expected_files.txt')
    with expected_files_path.open() as fid:
        for p in fid.readlines():
            if p.find("final_destination_dir") > 0:
                temp_path = test_data_dir.joinpath(p.strip("\n"))
                if not temp_path.parent.is_dir():
                    temp_path.parent.mkdir(parents=True)
                if not temp_path.is_file():
                    # Create empty file
                    open(str(temp_path), 'a').close()

    

def _teardown_test_environment():
    """Cleans up test environment by deleting ./test_data/test_download_images

    """
    test_dir = base_path.joinpath('test_data','test_download_images')
    if test_dir.is_dir():
        shutil.rmtree(str(test_dir))

def test_download_images_using_csv_file():
    """Test script to download images using csv file

    Assumes a test directory has been setup in ./test_data with sample data to
    facilitate this test.
    ToDo: Discuss with JM/FL/RW about how to setup this test without the
    overhead of too many files. - Use setup and cleanup?

    """
    #initial_destination_dir = os.path.join(base_path,"test_data/test_download_images/initial_destination_dir/dr_15")
    initial_destination_dir = base_path.joinpath("test_data","test_download_images","initial_destination_dir","dr_15")
    final_destination_dir = base_path.joinpath("test_data","test_download_images","final_destination_dir")
    input_file_path = base_path.joinpath("test_data","test_download_images","initial_destination_dir","test_download_images_using_csv_file_impc_images_input.csv")
    checksums_path = base_path.joinpath("test_data","test_download_images","final_destination_dir","test_download_images_using_csv_file_checksums.csv")


    # Prepare test environment - ensure we start with a clean one
    _teardown_test_environment()
    _setup_test_environment()

    not_downloaded = []
    #try:
    not_downloaded = runWithCsvFileAsDataSource(
        initial_destination_dir=str(initial_destination_dir),
        final_destination_dir=str(final_destination_dir),
        input_file_path=str(input_file_path),
        checksums_path=str(checksums_path))

    # Check if we have expected results
    expected_number_of_not_downloaded=1
    actual_number_of_not_downloaded = len(not_downloaded)
    assert expected_number_of_not_downloaded==actual_number_of_not_downloaded
    # Number of files in initial_destination_dir = expected number to be downloaded
    expected_number_of_downloaded_files = 12
    actual_number_of_downloaded_files = len([f for f in initial_destination_dir.rglob("*.*")])

    assert actual_number_of_downloaded_files == expected_number_of_downloaded_files
        # File names as expected

    #except Exception as e:
    #    print(e)
    #    assert True==False, str(e)

    #finally:
        # Clean up test environment
    _teardown_test_environment()
