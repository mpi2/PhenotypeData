""" Utils to setup test environment.
        e..g. Need a root directory for images. Currently in ./test_data, 
              but need more flexibility.

"""

from pathlib import Path
import shutil

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
        # Create mocks for files in final_destination_dir
        # Create mocks for 2 files in initial_destination_dir
        n_initial_dest_dir = 0
        for p in fid.readlines():
            if p.find("final_destination_dir") > 0:
                temp_path = test_data_dir.joinpath(p.strip("\n"))
                if not temp_path.parent.is_dir():
                    temp_path.parent.mkdir(parents=True)
                if not temp_path.is_file():
                    # Create empty file
                    open(str(temp_path), 'a').close()
            elif p.find("initial_destination_dir") > 0 and n_initial_dest_dir < 2:
                temp_path = test_data_dir.joinpath(p.strip("\n"))
                if not temp_path.parent.is_dir():
                    temp_path.parent.mkdir(parents=True)
                if not temp_path.is_file():
                    # Create empty file
                    open(str(temp_path), 'a').close()
                n_initial_dest_dir += 1
                

    

def _teardown_test_environment():
    """Cleans up test environment by deleting ./test_data/test_download_images

    """
    test_dir = base_path.joinpath('test_data','test_download_images')
    if test_dir.is_dir():
        shutil.rmtree(str(test_dir))

