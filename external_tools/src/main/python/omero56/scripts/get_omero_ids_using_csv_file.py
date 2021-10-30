#!/usr/bin/python

"""program to populate the omero_id into the imageObservation table so we can then index them with pure java from the database and solr experiment index"""

import os
import sys
import os.path
import argparse

import psycopg2
import csv

from utils import get_properties_from_config_server
from utils import get_properties_from_configuration_file


def main(argv):
    print("running main method of get_omero_ids - using postgresQL directly!!")

    parser = argparse.ArgumentParser(
        description='Populate omero_ids into a csv file so they can be included in the images core. This version uses postgresQl directly and was implemented for DR12 (30/06/2020)'
    )
    parser.add_argument('-i', '--input-file-path',
                        required=True,
                        help='Path to CSV file contiaining images info'
    )
    parser.add_argument('-o', '--output-file-path',
                        required=True,
                        help='Path to write CSV file with omero ids'
    )
    parser.add_argument('--omero-db-user', 
                        help='name of the omero postgres database')
    parser.add_argument('--omero-db-pass',
                        help='Password for the omero postgress database')
    parser.add_argument('--omero-db-name',
                        help='Name of the postgres database omero uses')
    parser.add_argument('--omero-db-host',
                        help='Hostname for the server hosting the omero postgres database')
    parser.add_argument('--omero-db-port',
                        help='Port to connect on the postgres server hosting the omero database')
    parser.add_argument('--config-server-host', default='wp-np2-8a.ebi.ac.uk',
                    help='Hostname for the configuration server')
    parser.add_argument('--config-server-port', default='8888',
                    help='Port for the configuration server')
    parser.add_argument('--config-server-name', default='omero-k8s',
                    help='Name to query in the config server')
    parser.add_argument('--config-server-profile', default='dev',
                    help='profile from which to read config: dev, prod, live, ...')
    parser.add_argument('--profile-path',
                        help='Explicit path to file from which to read ' + \
                             'profile e.g. ' + \
                             '/home/kola/configurations/omero/dev.properties'
    )
    
    args = parser.parse_args()
    
    # Get values from property file and use as defaults that can be overridden
    # by command line parameters
    if args.profile_path is not None:
        try:
            profile_path = args.profile_path
            omero_props = get_properties_from_configuration_file(profile_path)
        except Exception as e:
            logger.error(f"Could not read application properties file from {profile_path}")
            logger.error(f"Error was: {e}")
            sys.exit(-1)
    else:
        try:
            omero_props =  get_properties_from_config_server(
                                server=args.config_server_host,
                                port=args.config_server_port,
                                name=args.config_server_name,
                                profile=args.config_server_profile)
        except Exception as e:
            print("Could not read application properties file for profile " + args.config_server_profile)
            print(f"Error was: {e}")
            sys.exit(-1)

    # Get Postgres connection for directly querying omero database
    try:
        print("Attempting to connect directly to Postgres DB")
        omero_db_user = args.omero_db_user if args.omero_db_user is not None else omero_props['omerodbuser']
        omero_db_pass = args.omero_db_pass if args.omero_db_pass is not None else omero_props['omerodbpass']
        omero_db_name = args.omero_db_name if args.omero_db_name is not None else omero_props['omerodbname']
        omero_db_host = args.omero_db_host if args.omero_db_host is not None else omero_props['omerodbhost']
        if args.omero_db_port is not None:
            omero_db_port = args.omero_db_port
        elif 'omerodbport' in omero_props:
            omero_db_port = omero_props['omerodbport']
        else:
            omero_db_port = '5432'
    
        psql_conn = psycopg2.connect(database=omero_db_name, user=omero_db_user,
                                password=omero_db_pass, host=omero_db_host,
                                port=omero_db_port)
        print("Connected to Postgres DB")
    except KeyError as e:
        print("Could not connect to omero postgres database. Key " + str(e) + \
              " not present in omero properties file. Aborting!")
        sys.exit()
    except Exception as e:
        print("Could not connect to omero postgres database. Error: " + str(e))
        sys.exit()

    # Get project and Dataset ids for querying omero image records
    project_dict = get_project_and_dataset_ids(psql_conn)

    # Count number of rows to process
    with open(args.input_file_path, 'r') as fid:
        csv_reader = csv.reader(fid)
        n_rows = sum(1 for row in csv_reader)
        str_n_rows = str(n_rows-1)

    # Get handle to csv file and update records
    rows_processed = 0
    omero_ids_obtained = 0
    with open(args.input_file_path, 'r') as fid:
        pg_cur = psql_conn.cursor()
        csv_reader = csv.reader(fid)

        # Process header
        # For Omero 5.6.3 (Python3 - use pandas dataframe)
        header = next(csv_reader)

        try:
            download_file_path_idx = header.index("download_file_path")
            phenotyping_center_idx = header.index("phenotyping_center")
            pipeline_stable_idx = header.index("pipeline_stable_id")
            procedure_stable_idx = header.index("procedure_stable_id")
            parameter_stable_idx = header.index("parameter_stable_id")
            checksum_idx = header.index("checksum")
        except ValueError as e:
            print("Fatal Error:")
            print(str(e), header)
            print("Exiting")
            sys.exit(-1)

        header += ["omero_id",]

        # Get handle for writing updated records
        with open(args.output_file_path, 'w') as fid_out:
            csv_writer = csv.writer(fid_out)
            csv_writer.writerow(header)
        
            # Update omero ids for each line in input
            for row in csv_reader:
                rows_processed += 1
                if rows_processed % 1000 == 0:
                    print("Processed " + str(rows_processed) + " of " + str_n_rows)
                download_file_path=row[download_file_path_idx].lower()
                if (download_file_path.find('mousephenotype.org') < 0 and \
                    download_file_path.find('file:') < 0) or \
                        download_file_path.endswith('.mov') or \
                        download_file_path.endswith('.fcs') or \
                        download_file_path.endswith('.nrrd') or \
                        download_file_path.endswith('.bz2'):
                
                    row.append("-1")
                    csv_writer.writerow(row)
                    continue

                project_name = row[phenotyping_center_idx]
                pipeline_stable_id = row[pipeline_stable_idx]
                procedure_stable_id = row[procedure_stable_idx]
                parameter_stable_id = row[parameter_stable_idx]
                # If there is not checksum use the actual image name (e.g. for 3i)
                if len(row[checksum_idx]) > 0:
                    imagename = row[checksum_idx]+os.path.splitext(download_file_path)[-1]
                else:
                    imagename = os.path.split(download_file_path)[-1]
                    
                image_nfs_path = os.path.join(project_name,pipeline_stable_id,procedure_stable_id,parameter_stable_id,imagename)

                dataset_name = "-".join([project_name, pipeline_stable_id, procedure_stable_id, parameter_stable_id])

                try:
                    project_ids = project_dict[project_name].keys()
                except KeyError as e:
                    message = "ERROR: Could not get project details for image " + imagename + " in dataset " + dataset_name + ". KeyError was: " + str(e)
                    print(message)
                    row.append("-1")
                    csv_writer.writerow(row)
                    continue
                
                # In the following loop we search for the omero ID using the
                # project name and dataset name. This is complicated by
                # some project names and/or dataset names being duplicated
                # in omero. We therefore map the project names and dataset
                # names to their respective keys and loop through both 
                # project ids and dataset ids. We exit
                # the loop(s) once a valid omero_id has been found.
                omero_id = -1
                error_message = ""
                for project_id in project_ids:
                    # If we cannot find the project ID we don't even bother 
                    # going further.
                    try:
                        dataset_ids = project_dict[project_name][project_id][dataset_name]
                    except KeyError as e:
                        error_message += "ERROR: Could not get dataset details for image " + imagename + " in dataset " + dataset_name + ". KeyError was: " + str(e) + "\n"
                        continue
                    
                    for dataset_id in dataset_ids:
                        if imagename.endswith('pdf'):
                            image_nfs_dir = os.path.join("/",project_name,pipeline_stable_id,procedure_stable_id,parameter_stable_id)
                            query = "SELECT a.id FROM annotation a " + \
                                    "INNER JOIN datasetannotationlink dsal ON " +\
                                    "a.id=dsal.child " + \
                                    "INNER JOIN originalfile of ON a.file=of.id " + \
                                    "WHERE dsal.parent=" + str(dataset_id) + \
                                    " AND of.name='" + imagename + "'"
                        # Special case for 3i Ear epidemis images
                        elif imagename.find('.lif')>0 or imagename.find('.lei')>0:
                            query = "SELECT DISTINCT i.id, i.name FROM " + \
                                "image i INNER JOIN " + \
                                "datasetimagelink dsil ON i.id=dsil.child " + \
                                "INNER JOIN filesetentry fse ON " + \
                                "i.fileset=fse.fileset " + \
                                "WHERE LOWER(i.name)='" + imagename + "'"
                        else:
                            # Use LIKE for imagename as some images have 
                            # more than one plane and are suffixed. e.g # 1
                            query = "SELECT i.id FROM image i INNER JOIN " + \
                                "datasetimagelink dsil ON i.id=dsil.child " + \
                                "INNER JOIN filesetentry fse ON " + \
                                "i.fileset=fse.fileset " + \
                                "WHERE dsil.parent=" + str(dataset_id) + \
                                " AND LOWER(i.name) LIKE '" + imagename + "%'"

                        pg_cur.execute(query)
                        omero_ids = pg_cur.fetchall()
                        n_omero_ids = len(omero_ids)
                        if n_omero_ids == 0:
                            error_message += "ERROR: Got 0 omero_ids instead of 1. Not updating omero_id for " + image_nfs_path + "\n"
                        elif n_omero_ids > 1:
                            error_message = "WARNING: Got " + str(n_omero_ids) + " omero_ids instead of 1 - using first in list for " + image_nfs_path + "\n"
                            omero_id = omero_ids[0][0]
                            #omero_id = omero_ids[-1][0]
                            omero_ids_obtained += 1
                            break
                        else:
                            # We have found a valid omero_id - exit the loop
                            error_message = ""
                            omero_id = omero_ids[0][0]
                            omero_ids_obtained += 1
                            break
                   
                    # If we have a valid omero ID this record has been
                    # successfully processed - move to next one
                    if omero_id != -1:
                        break

                if len(error_message) > 0:
                    print(error_message)
                row.append(str(omero_id))
                csv_writer.writerow(row)
                    
    
    psql_conn.close()
    print("Got " + str(omero_ids_obtained) + " omero ids from " + str_n_rows + " records")

def get_project_and_dataset_ids(psql_conn):
    """
        return dict with all projects and dataset ids in omero
    """

    pg_cur = psql_conn.cursor()
    query = "SELECT id FROM experimenter WHERE lastname='root'"
    pg_cur.execute(query)
    my_expId = str(pg_cur.fetchone()[0])
    query = "SELECT id, name FROM project WHERE owner_id=" + my_expId
    pg_cur.execute(query)
    projects = pg_cur.fetchall()

    project_dict = {}
    for project_id, project_name in projects:
        if len(project_name) > 0:
            if project_name in project_dict:
                project_dict[project_name][project_id] = {}
            else:
                project_dict[project_name] = {project_id: {},}
            query = "Select ds.id, ds.name from dataset ds inner join projectdatasetlink pdsl on ds.id=pdsl.child where pdsl.parent="+str(project_id)
            pg_cur.execute(query)
            datasets = pg_cur.fetchall()        
            #project_dict[project_name]['datasets'] = {}
            for dataset_id, dataset_name in datasets:
                if dataset_name in project_dict[project_name][project_id]:
                    project_dict[project_name][project_id][dataset_name].append(dataset_id)
                else:
                    project_dict[project_name][project_id][dataset_name] = [dataset_id,]

    return project_dict

if __name__ == "__main__":
    main(sys.argv[1:])
