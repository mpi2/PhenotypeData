#!/usr/bin/python

"""program to populate the omero_id into the imageObservation table so we can then index them with pure java from the database and solr experiment index"""

import os
import sys
import os.path
import argparse

import psycopg2
import csv

from OmeroPropertiesParser import OmeroPropertiesParser

def main(argv):
    print "running main method of get_omero_ids - using postgresQL directly!!"

    parser = argparse.ArgumentParser(
        description='Populate omero_ids into a csv file so they can be included in the images core. This version uses postgresQl directly and was implemented for DR12 (30/06/2020)'
    )
    parser.add_argument('-i', '--input-file', dest='inputFilePath',
                        required=True,
                        help='Path to CSV file contiaining images info'
    )
    parser.add_argument('-o', '--output-file', dest='outputFilePath',
                        required=True,
                        help='Path to write CSV file with omero ids'
    )
    parser.add_argument('--omeroDbUser', dest='omeroDbUser', 
                        help='name of the omero postgres database')
    parser.add_argument('--omeroDbPass', dest='omeroDbPass',
                        help='Password for the omero postgress database')
    parser.add_argument('--omeroDbName', dest='omeroDbName',
                        help='Name of the postgres database omero uses')
    parser.add_argument('--omeroDbHost', dest='omeroDbHost',
                        help='Hostname for the server hosting the omero postgres database')
    parser.add_argument('--omeroDbPort', dest='omeroDbPort',
                        help='Port to connect on the postgres server hosting the omero database')
 
    parser.add_argument('--profile', dest='profile', default='dev',
                        help='Name of profile from which to read config: ' + \
                             'dev, prod, live, ... Assumed to be present ' + \
                             'in configfiles/profilename/application.properties'
    )
    parser.add_argument('--profile-path', dest='profilePath',
                        help='Explicit path to file from which to read ' + \
                             'profile e.g. ' + \
                             '/home/kola/configfiles/dev/application.properties'
    )
    parser.add_argument('-d', '--rootDestinationDir', dest='rootDestinationDir',
                        help='Root directory for destination files were downloaded to'
    )
    
    args = parser.parse_args()
    
    # Get values from property file and use as defaults that can be overridden
    # by command line parameters
    if args.profilePath is not None:
        try:
            pp = OmeroPropertiesParser()
            omeroProps = pp.getOmeroProps(args.profilePath)
        except Exception as e:
            print "Could not read application properties file from " + args.profilePath
            print "Error was: " + str(e)
            return
    else:
        try:
            pp = OmeroPropertiesParser(args.profile)
            omeroProps = pp.getOmeroProps()
        except Exception as e:
            print "Could not read application properties file for profile " + args.profile
            print "Error was: " + str(e)
            return

    try:
        root_dir = args.rootDestinationDir if args.rootDestinationDir<>None else omeroProps['rootdestinationdir']
        # Remove initial '/' if it exists. This is not present in omero db
        if root_dir[0] == '/':
            root_dir = root_dir[1:]
    except Exception as e:
        print "Could not assign root_dir from either command line or properties file. Did you specify the right profile? Error message was: " + str(e)
        return



    # Get Postgres connection for directly querying omero database
    try:
        print "Attempting to connect directly to Postgres DB"
        omeroDbUser = args.omeroDbUser if args.omeroDbUser is not None else omeroProps['omerodbuser']
        omeroDbPass = args.omeroDbPass if args.omeroDbPass is not None else omeroProps['omerodbpass']
        omeroDbName = args.omeroDbName if args.omeroDbName is not None else omeroProps['omerodbname']
        omeroDbHost = args.omeroDbHost if args.omeroDbHost is not None else omeroProps['omerodbhost']
        if args.omeroDbPort is not None:
            omeroDbPort = args.omeroDbPort
        elif 'omerodbport' in omeroProps:
            omeroDbPort = omeroProps['omerodbport']
        else:
            omeroDbPort = '5432'
    
        psqlConn = psycopg2.connect(database=omeroDbName, user=omeroDbUser,
                                password=omeroDbPass, host=omeroDbHost,
                                port=omeroDbPort)
        print "Connected to Postgres DB"
    except KeyError as e:
        print "Could not connect to omero postgres database. Key " + str(e) + \
              " not present in omero properties file. Aborting!"
        sys.exit()
    except Exception as e:
        print "Could not connect to omero postgres database. Error: " + str(e)
        sys.exit()

    # Get project and Dataset ids for querying omero image records
    project_dict = get_project_and_dataset_ids(psqlConn)

    # Count number of rows to process
    with open(args.inputFilePath, 'rb') as fid:
        csv_reader = csv.reader(fid)
        n_rows = sum(1 for row in csv_reader)
        str_n_rows = str(n_rows-1)

    # Get handle to csv file and update records
    rows_processed = 0
    omero_ids_obtained = 0
    with open(args.inputFilePath, 'rb') as fid:
        pg_cur = psqlConn.cursor()
        csv_reader = csv.reader(fid)

        # Skip header
        header = csv_reader.next()
        header += ["omero_id",]

        # Get handle for writing updated records
        with open(args.outputFilePath, 'wb') as fid_out:
            csv_writer = csv.writer(fid_out)
            csv_writer.writerow(header)
        
            # Update omero ids for each line in input
            for row in csv_reader:
                rows_processed += 1
                if rows_processed % 1000 == 0:
                    print "Processed " + str(rows_processed) + " of " + str_n_rows
                download_file_path=row[1].lower()
                if (download_file_path.find('mousephenotype.org') < 0 and \
                    download_file_path.find('file:') < 0) or \
                        download_file_path.endswith('.mov') or \
                        download_file_path.endswith('.fcs') or \
                        download_file_path.endswith('.nrrd') or \
                        download_file_path.endswith('.bz2'):
                
                    row.append("-1")
                    csv_writer.writerow(row)
                    continue

                project_name = row[2]
                pipeline_stable_id = row[3]
                procedure_stable_id = row[4]
                parameter_stable_id = row[6]
                imagename = os.path.split(download_file_path)[-1]
                image_nfs_path = os.path.join(root_dir, project_name,pipeline_stable_id,procedure_stable_id,parameter_stable_id,imagename)

                dataset_name = "-".join([project_name, pipeline_stable_id, procedure_stable_id, parameter_stable_id])

                try:
                    project_ids = project_dict[project_name].keys()
                except KeyError as e:
                    message = "ERROR: Could not get project details for image " + imagename + " in dataset " + dataset_name + ". KeyError was: " + str(e)
                    print message
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
                            image_nfs_dir = os.path.join("/",root_dir, project_name,pipeline_stable_id,procedure_stable_id,parameter_stable_id)
                            query = "SELECT a.id FROM annotation a " + \
                                    "INNER JOIN datasetannotationlink dsal ON " +\
                                    "a.id=dsal.child " + \
                                    "INNER JOIN originalfile of ON a.file=of.id " + \
                                    "WHERE dsal.parent=" + str(dataset_id) + \
                                    " AND of.path='" + image_nfs_dir + "'" + \
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
                            query = "SELECT i.id FROM image i INNER JOIN " + \
                                "datasetimagelink dsil ON i.id=dsil.child " + \
                                "INNER JOIN filesetentry fse ON " + \
                                "i.fileset=fse.fileset " + \
                                "WHERE dsil.parent=" + str(dataset_id) + \
                                " AND LOWER(fse.clientpath)=LOWER('" + image_nfs_path + \
                                "') AND LOWER(i.name)='" + imagename + "'"

                        pg_cur.execute(query)
                        omero_ids = pg_cur.fetchall()
                        n_omero_ids = len(omero_ids)
                        if n_omero_ids == 0:
                            error_message += "ERROR: Got 0 omero_ids instead of 1. Not updating omero_id for " + image_nfs_path + "\n"
                        elif n_omero_ids > 1:
                            error_message = "WARNING: Got " + str(n_omero_ids) + " omero_ids instead of 1 - using last in list for " + image_nfs_path + "\n"
                            omero_id = omero_ids[-1][0]
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
                    print error_message
                row.append(str(omero_id))
                csv_writer.writerow(row)
                    
    
    psqlConn.close()
    print "Got " + str(omero_ids_obtained) + " omero ids from " + str_n_rows + " records"

def get_project_and_dataset_ids(psqlConn):
    """
        return dict with all projects and dataset ids in omero
    """

    pg_cur = psqlConn.cursor()
    query = "SELECT id FROM experimenter WHERE lastname='root'"
    pg_cur.execute(query)
    my_expId = str(pg_cur.fetchone()[0])
    query = "SELECT id, name FROM project WHERE owner_id=" + my_expId
    pg_cur.execute(query)
    projects = pg_cur.fetchall()

    project_dict = {}
    for project_id, project_name in projects:
        if len(project_name) > 0:
            if project_dict.has_key(project_name):
                project_dict[project_name][project_id] = {}
            else:
                project_dict[project_name] = {project_id: {},}
            query = "Select ds.id, ds.name from dataset ds inner join projectdatasetlink pdsl on ds.id=pdsl.child where pdsl.parent="+str(project_id)
            pg_cur.execute(query)
            datasets = pg_cur.fetchall()        
            #project_dict[project_name]['datasets'] = {}
            for dataset_id, dataset_name in datasets:
                if project_dict[project_name][project_id].has_key(dataset_name):
                    project_dict[project_name][project_id][dataset_name].append(dataset_id)
                else:
                    project_dict[project_name][project_id][dataset_name] = [dataset_id,]

    return project_dict

if __name__ == "__main__":
    main(sys.argv[1:])
