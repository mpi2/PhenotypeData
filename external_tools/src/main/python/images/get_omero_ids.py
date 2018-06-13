#!/usr/bin/python

"""program to populate the omero_id into the imageObservation table so we can then index them with pure java from the database and solr experiment index"""

import os
import json
from xml.dom.minidom import parseString
import glob
import shutil
import sys
import os.path
import sys
import argparse
import mysql.connector
from mysql.connector import errorcode
import omero.rtypes
from common import splitString
from database import getDbConnection,getFullResolutionFilePaths
from OmeroPropertiesParser import OmeroPropertiesParser

def main(argv):
    print "running main method of get_omero_ids!!"

    parser = argparse.ArgumentParser(
        description='Populate omero_ids into the komp2 image_record_observation table so we can then index them with pure java from the database and solr experiment index'
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
    parser.add_argument('-o', '--omeroHost', dest='omeroHost',
                        help='Hostname for server hosting omero instance'
    )
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
    omeroHost = args.omeroHost if args.omeroHost<>None else omeroProps['omerohost']
    print 'setting omeroHost='+omeroHost

    komp2User = args.komp2User if args.komp2User<>None else omeroProps['komp2user']
    komp2Pass = args.komp2Pass if args.komp2Pass<>None else omeroProps['komp2pass']

    omeroPort = omeroProps['omeroport']
    omeroUser = omeroProps['omerouser']
    omeroPass = omeroProps['omeropass']

    global loadedCount
    loadedCount=0

    print "about to run getdb with arguments komp2db="+komp2db
    dbConn=getDbConnection(komp2Host, komp2Port, komp2db, komp2User, komp2Pass)
    #cnx=getDbConnection(komp2Host, komp2Port, komp2db, komp2User, komp2Pass)
    fullResPathsAlreadyHave=getFullResolutionFilePaths(dbConn)
    getOmeroIdsAndPaths(dbConn, omeroUser, omeroPass, omeroHost, omeroPort, fullResPathsAlreadyHave)


def print_obj(obj, indent=0):
    """
    Helper method to display info about OMERO objects.
    Not all objects will have a "name" or owner field.
    """
    print """%s%s:%s  Name:"%s" (owner=%s)""" % (\
            " " * indent,
            obj.OMERO_CLASS,\
            obj.getId(),\
            obj.getName(),\
            obj.getOwnerOmeName())

def getOriginalFile(imageObj):
    fileset = imageObj.getFileset()

    for origFile in fileset.listFiles():
        name = origFile.getName()
        path = origFile.getPath()
    	print path, name
    return path, name

def getOmeroIdsAndPaths(dbConn, omeroUser, omeroPass, omeroHost, omeroPort, fullResPathsAlreadyHave):

    from omero.gateway import BlitzGateway
    # Connect to the Python Blitz Gateway
    # =============================================================
    # Make a simple connection to OMERO, printing details of the
    # connection. See OmeroPy/Gateway for more info
    group='public_group'
    conn = BlitzGateway(omeroUser, omeroPass, host=omeroHost, port=omeroPort,  group=group)
    connected = conn.connect()
    #conn.SERVICE_OPTS.setOmeroGroup(-1)#3 is the current public_group may change - what is the correct way to do this?
    # Check if you are connected.
    # =============================================================
    if not connected:
        import sys
        sys.stderr.write("Error: Connection not available, please check your user name and password.\n")
        sys.exit
    # Using secure connection.
    # =============================================================
    # By default, once we have logged in, data transfer is not encrypted (faster)
    # To use a secure connection, call setSecure(Tru
    # conn.setSecure(True)         # <--------- Uncomment t
    # Current session details
    # =============================================================
    # By default, you will have logged into your 'current' group in OMERO. This
    # can be changed by switching group in the OMERO.insight or OMERO.web clien
    user = conn.getUser()
    print "Current user:"
    print "   ID:", user.getId()
    print "   Username:", user.getName()
    print "   Full Name:", user.getFullName()
    print "Member of:"
    for g in conn.getGroupsMemberOf():
        print "   ID:", g.getName(), " Name:", g.getId()
    group = conn.getGroupFromContext()
    print "Current group: ", group.getName()
    print "Other Members of current group:"
    for exp in conn.listColleagues():
        print "   ID:", exp.getId(), exp.getOmeName(), " Name:", exp.getFullName()
    print "Owner of:"
    for g in conn.listOwnedGroups():
        print "   ID:", g.getName(), " Name:", g.getId()
    # New in OMERO 5
    print "Admins:"
    for exp in conn.getAdministrators():
        print "   ID:", exp.getId(), exp.getOmeName(), " Name:", exp.getFullName()
    # The 'context' of our current session
    ctx = conn.getEventContext()
    print ctx     # for more info
    #print ctx
    # The only_owned=True parameter limits the Projects which are returned.
    # If the parameter is omitted or the value is False, then all Projects
    # visible in the current group are returned.
    #print "\nList Projects:"
    #print "=" * 50
    my_expId = conn.getUser().getId()
    for project in conn.listProjects(my_expId):
        print_obj(project)
        for dataset in project.listChildren():
            print_obj(dataset, 2)
            for image in dataset.listChildren():
                #print_obj(image, 4)
                #getOriginalFile(image)
                fileset = image.getFileset()
                filesetId=fileset.getId()
                #print 'filesetId=',filesetId
                query = 'SELECT clientPath FROM FilesetEntry WHERE fileset.id = :id'
                params = omero.sys.ParametersI()
                params.addId(omero.rtypes.rlong(filesetId))
                #print 'params=',params
                for path in conn.getQueryService().projection(query, params):
                    #print 'path=', path[0].val
                    originalUploadedFilePathToOmero=path[0].val
                    #print originalUploadedFilePathToOmero
                    #print originalUploadedFilePathToOmero+" id="+str(image.getId())

                    # If this is a leica (.lif or .lei) file do the matching differently
                    if originalUploadedFilePathToOmero.find('.lif') > 0 or originalUploadedFilePathToOmero.find('.lei') > 0:
                        # Get all images stored in the same file
                        query = 'SELECT id, name FROM Image WHERE fileset.id = :id'
                        original_path,original_im_name = os.path.split(originalUploadedFilePathToOmero)
                        for im_details in conn.getQueryService().projection(query, params):
                            omero_id = im_details[0].val
                            omero_im_name = im_details[1].val
                            if omero_im_name.find(original_im_name) >= 0:
                                modifiedUploadedFilePathToOmero = originalUploadedFilePathToOmero.replace(original_im_name, omero_im_name)
                                storeOmeroId(dbConn, omero_id, modifiedUploadedFilePathToOmero, fullResPathsAlreadyHave )
                    else:
                        storeOmeroId(dbConn, image.getId(), originalUploadedFilePathToOmero, fullResPathsAlreadyHave )
            #print "\nProject="+project.getName()+"Annotations on Dataset:", dataset.getName()
            for ann in dataset.listAnnotations():
                #filesetId=ann.getId()
                #query = 'SELECT clientPath FROM FileSet WHERE file.id = :id'
                #params = omero.sys.ParametersI()
                #params.addId(omero.rtypes.rlong(filesetId))
                #for path in conn.getQueryService().projection(query, params):
                    #print 'path=', path[0].val
                    #originalUploadedFilePathToOmero=path[0].val
                    if isinstance(ann, omero.gateway.FileAnnotationWrapper):
                        #print "Annotation ID:", ann.getId(), ann.getFile().getName(), "Size:", ann.getFile().getSize()
                        originalUploadedFilePathToOmero=dataset.getName().replace("-","/")+"/"+ann.getFile().getName()
                        #print "originalUploadedFilePathToOmero for annotation="+originalUploadedFilePathToOmero
                        storeOmeroId(dbConn, ann.getId(), originalUploadedFilePathToOmero, fullResPathsAlreadyHave )
    # Close connection:
    # =================================================================
    # When you are done, close the session to free up server resources.
    conn._closeSession()

def storeOmeroId(cnx, omero_id, originalUploadedFilePathToOmero, fullResPathsAlreadyHave):
    global loadedCount
    loadedCount=loadedCount+1
    if loadedCount % 1000==0:
        print "loadedCount="+str(loadedCount)
    #print "originalUploadedFilePathToOmero="+originalUploadedFilePathToOmero
    if splitString in originalUploadedFilePathToOmero:
        fullResolutionFilePath=originalUploadedFilePathToOmero.split(splitString,1)[1]#destinationFilePath.replace(nfsDir,"")
    elif "images/impc/" in originalUploadedFilePathToOmero:
        fullResolutionFilePath=originalUploadedFilePathToOmero.split("images/impc/",1)[1]
    else:
        fullResolutionFilePath=originalUploadedFilePathToOmero#just use this String if impc is not in the original file path as this must be an annotation which we will just have the relatvie path
    #if fullResolutionFilePath not in fullResPathsAlreadyHave:
    # print 'need to store the new url here with omero_id='+str(omero_id)+' fullResolutionFilePath= '+fullResolutionFilePath
    try:
        cur = cnx.cursor(buffered=True)
            #SQL query to INSERT a record into the table FACTRESTTBL.
        cur.execute("""UPDATE image_record_observation SET omero_id=%s WHERE full_resolution_file_path=%s""", (omero_id, fullResolutionFilePath))
            #if cur.rowcount != 1: #note that if the uri has already been added the row count will be 0
               # print("error affected rows = {}".format(cur.rowcount)+ downloadFilePath)
    except mysql.connector.Error as err:
            print(err)

if __name__ == "__main__":
    main(sys.argv[1:])
