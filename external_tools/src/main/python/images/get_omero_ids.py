#!/usr/bin/python

"""program to populate the omero_id into the imageObservation table so we can then index them with pure java from the database and solr experiment index"""

import os
import sys
import os.path
import argparse

import mysql.connector
from mysql.connector import errorcode

import psycopg2

from common import splitString
from database import getDbConnection,getFullResolutionFilePaths
from OmeroPropertiesParser import OmeroPropertiesParser

def main(argv):
    print "running main method of get_omero_ids - using postgresQL directly!!"

    parser = argparse.ArgumentParser(
        description='Populate omero_ids into the komp2 image_record_observation table so we can then index them with pure java from the database and solr experiment index. This version uses postgresQl directly'
    )
    parser.add_argument('-H', '--host', dest='komp2Host',
                        help='Hostname for server hosting komp2 db'
    )
    parser.add_argument('-p', '--port', dest='komp2Port',
                        help='Port by which to connect to komp2 db'
    )
    parser.add_argument('-u', '--user', dest='komp2User',
                        help='Username for connecting to komp2 db'
    )
    parser.add_argument('-db', '--database', dest='komp2Db',
                        help='Database to connect to for komp2db'
    )
    parser.add_argument('--pass', dest='komp2Pass',
                        help='Password for komp2db'
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

    komp2Host = args.komp2Host if args.komp2Host<>None else omeroProps['komp2host']
    print "setting komp2Host="+komp2Host
    komp2Port = args.komp2Port if args.komp2Port<>None else omeroProps['komp2port']
    print 'setting komp2Port='+komp2Port
    komp2db = args.komp2Db if args.komp2Db<>None else omeroProps['komp2db']
    print 'setting komp2db='+komp2db

    komp2User = args.komp2User if args.komp2User<>None else omeroProps['komp2user']
    komp2Pass = args.komp2Pass if args.komp2Pass<>None else omeroProps['komp2pass']

    global loadedCount
    loadedCount=0

    print "about to run getdb with arguments komp2db="+komp2db
    dbConn=getDbConnection(komp2Host, komp2Port, komp2db, komp2User, komp2Pass)
    #cnx=getDbConnection(komp2Host, komp2Port, komp2db, komp2User, komp2Pass)

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

    
    getOmeroIdsAndPaths(dbConn, psqlConn)
    dbConn.close()
    psqlConn.close()


def getOmeroIdsAndPaths(dbConn, psqlConn):

    # We query the postgres DB for details of the root user in the assumption that all necessary image and annotation
    # steps were done via this user.
    
    # Get a cursor for the postgres db
    pg_cur = psqlConn.cursor()
    query = "SELECT id FROM experimenter WHERE lastname='root'"
    pg_cur.execute(query)
    if pg_cur.rowcount != 1:
        print "Error - expected one row from query to get user ID for root. got " + str(pg_cur.rowcount) + " - exiting"
        sys.exit(-1)
    
    my_expId = str(pg_cur.fetchone()[0])
    query = "SELECT id, name FROM project WHERE owner_id=" + my_expId
    pg_cur.execute(query)
    for project_id, project_name in pg_cur.fetchall():
        print "Processing project: " + project_name
        
        query = "Select ds.id, ds.name from dataset ds inner join projectdatasetlink pdsl on ds.id=pdsl.child where pdsl.parent="+str(project_id)
        #print query
        pg_cur.execute(query)
        for dataset in pg_cur.fetchall():
            dataset_id, dataset_name = dataset
            print "Processing dataset: " + dataset_name
            if dataset_name.find('MGP_EEI_114_001') >= 0:
                query = "SELECT i.id, fse.clientpath, i.name FROM image i " + \
                    "INNER JOIN datasetimagelink dsil ON i.id=dsil.child " + \
                    "INNER JOIN filesetentry fse ON i.fileset=fse.fileset " + \
                    "WHERE dsil.parent=" + str(dataset_id) + " "\
                    "AND (fse.clientpath LIKE '%lif' OR fse.clientpath LIKE '%lei')"
                #print query
                pg_cur.execute(query)
                for omero_id, image_path, image_name in pg_cur.fetchall():
                    newpath = os.path.split(image_path.split('impc/')[-1])[0]
                    image_path = os.path.join(newpath,image_name)
                    #print "Processing image: " + image_path
                    storeOmeroId(dbConn, omero_id, image_path)
    
            else:
                query = "SELECT i.id, fse.clientpath, i.name FROM image i " + \
                    "INNER JOIN datasetimagelink dsil ON i.id=dsil.child " + \
                    "INNER JOIN filesetentry fse ON i.fileset=fse.fileset " + \
                    "WHERE dsil.parent=" + str(dataset_id)
                #print query
                pg_cur.execute(query)
                for omero_id, image_path, image_name in pg_cur.fetchall():
                    #print "Processing image: " + image_path
                    storeOmeroId(dbConn, omero_id, image_path)
    

            # Deal with annotations if present
            query = "SELECT a.id, of.path, of.name FROM annotation a " + \
                "INNER JOIN datasetannotationlink dsal ON a.id=dsal.child " + \
                "INNER JOIN originalfile of ON a.file=of.id " + \
                "WHERE dsal.parent=" + str(dataset_id)
        
            pg_cur.execute(query)
            if pg_cur.rowcount > 0:
                print "Processing annotations for dataset: " + dataset_name
                #print query
            for annotation_id, annotation_dir, annotation_name in pg_cur.fetchall():
                annotation_id=str(annotation_id)
                if annotation_dir is not None and annotation_name is not None:
                    annotation_path = os.path.join(annotation_dir,annotation_name)
                    #print "Annotation details: " + annotation_id + ": " + annotation_path
                    storeOmeroId(dbConn, annotation_id, annotation_path)
                else:
                    message = "Cannot update annotation_id: " + str(annotation_id) + \
                              " - annotation_dir = " + str(annotation_dir) + \
                              ", annotation_name = " + str(annotation_name)
                    print message
                    continue


def storeOmeroId(cnx, omero_id, originalUploadedFilePathToOmero):
    global loadedCount
    loadedCount=loadedCount+1
    if loadedCount % 1000==0:
        print "loadedCount="+str(loadedCount)
    if splitString in originalUploadedFilePathToOmero:
        fullResolutionFilePath=originalUploadedFilePathToOmero.split(splitString,1)[1]#destinationFilePath.replace(nfsDir,"")
    elif "images/impc/" in originalUploadedFilePathToOmero:
        fullResolutionFilePath=originalUploadedFilePathToOmero.split("images/impc/",1)[1]
    else:
        fullResolutionFilePath=originalUploadedFilePathToOmero#just use this String if impc is not in the original file path as this must be an annotation which we will just have the relatvie path

    try:
        mysql_cur = cnx.cursor(buffered=True)
        mysql_cur.execute("""UPDATE image_record_observation SET omero_id=%s WHERE full_resolution_file_path=%s""", (omero_id, fullResolutionFilePath))
    except mysql.connector.Error as err:
            print(err)

if __name__ == "__main__":
    main(sys.argv[1:])
