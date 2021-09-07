#!/usr/bin/python
import platform
import argparse
import locale
import time
import sys
import collections
import logging

import omero
import omero.all
import omero.rtypes
import omero.cli
from omero.gateway import *
from omero.rtypes import wrap
from omero.model import DatasetI, ProjectI
import psycopg2

# TODO: Make this configurable
splitString = "impc"

class OmeroService:
    def __init__(self, omeroHost, omeroPort, omeroUsername, omeroPass, group):
        self.logger = logging.getLogger(__name__)
        self.logger.info("init OmeroService")
        self.omeroHost=omeroHost
        self.omeroPort=omeroPort
        self.omeroUsername=omeroUsername
        self.omeroPass=omeroPass
        self.group=group
        self.conn=self.getConnection()
        
    def getConnection(self):
        try: 
            self.cli = omero.cli.CLI()
            self.cli.loadplugins()
            cmd = f"login {self.omeroUsername}@{self.omeroHost}:{self.omeroPort} -w {self.omeroPass}"
            self.cli.invoke(cmd, strict=True)
            ##self.cli.invoke(["login", self.omeroUsername+'@'+self.omeroHost, "-w", self.omeroPass, "-C"], strict=True)
            ##cli.invoke(["login", "%s@localhost" % user, "-w", passw, "-C"], strict=True)
            self.cli.invoke(["sessions", "group", self.group], strict=True)
            sessionId = self.cli._event_context.sessionUuid
            conn = BlitzGateway(self.omeroUsername, self.omeroPass, host=self.omeroHost, port=self.omeroPort, secure=True)
            conn.connect(sUuid = sessionId)
    
            #user = self.conn.getUser()
            #group = self.conn.getGroupFromContext()
    
            # The 'context' of our current session
            #ctx = self.conn.getEventContext()

            return conn
        except Exception as e:
            self.logger.exception(e)
    
    def getImagesAlreadyInOmero(self):
        query = 'SELECT clientPath FROM FilesetEntry WHERE fileset.id >= :id';
        params = omero.sys.ParametersI()
        params.addId(omero.rtypes.rlong(0))
        omero_file_data = self.conn.getQueryService().projection(query, params)

        # Get the filepath by splitting the indir path
        omero_file_list = []
        for ofd in omero_file_data:
            try:
                #indir,ofd_path = ofd[1].split(root_dir[1:])
                ofd_path = ofd[0].val.split('impc/')[-1]
            except Exception as e:
                self.logger.error("Problem extracting root_dir from clientpath " + ofd[0].val)
                self.logger.error("Error was: " + str(e))
                omero_file_list.append(ofd[0].val)
                continue
            omero_file_list.append(ofd_path)
        return omero_file_list

    def getImagesAlreadyInOmeroViaPostgres(self, omeroDbDetails):
        # Get files already in omero by directly querying postgres db
        try:
            print("Attempting to get file list directly from Postgres DB")
            omeroDbUser = omeroDbDetails['omerodbuser']
            omeroDbPass = omeroDbDetails['omerodbpass']
            omeroDbName = omeroDbDetails['omerodbname']
            omeroDbHost = omeroDbDetails['omerodbhost']
            omeroDbPort = omeroDbDetails['omerodbport']
        
            conn = psycopg2.connect(database=omeroDbName, user=omeroDbUser,
                                    password=omeroDbPass, host=omeroDbHost,
                                    port=omeroDbPort)
            cur = conn.cursor()
            # Get the actual files uploaded to Omero
            query = "SELECT DISTINCT clientpath FROM filesetentry " + \
                    "INNER JOIN fileset ON filesetentry.fileset=fileset.id " +\
                    "WHERE fileset.id >=0"
            cur.execute(query)
            omero_file_list = []
            for f in cur.fetchall():
                omero_file_list.append(f[0].split('impc/')[-1])
        
            ## Get the images contained in the leica files uploaded to Omero
            ## These images are in the download_urls obtained from solr
            #query = "SELECT name FROM image " + \
            #        "WHERE name LIKE '%.lif%' OR name LIKE '%.lei%'"
            #cur.execute(query)
            #omero_image_list = []
            #for i in cur.fetchall():
            #    omero_image_list.append(i[0])
            conn.close()
            return omero_file_list

        except KeyError as e:
            message = "Could not connect to omero postgres database. Key " +\
                      str(e) + \
                      " not present in omero properties file. Aborting!"
            # What about logger?
            print(message)
            if 'conn' in locals():
                conn.close()
            sys.exit()


    def getAnnotationsAlreadyInOmero(self):
        #query = 'SELECT ds.name, (SELECT o.name FROM originalfile o ' + \
        #        'WHERE o.id=a.file) AS filename FROM datasetannotationlink ' + \
        #        'dsal, dataset ds, annotation a where dsal.parent=ds.id and ' + \
        #        ' dsal.child=a.id and ' + \
        #        ' dsal.child >= :id'
        
        omero_annotation_list = []
        file_annotations = self.conn.listFileAnnotations()
        for fa in file_annotations:
            links = fa.getParentLinks('dataset')
            for link in links:
                datasets = link.getAncestry()
                for ds in datasets:
                    dir_parts = ds.getName().split('-')
                    if len(dir_parts) == 4:
                        dir_parts.append(fa.getFileName())
                        omero_annotation_list.append("/".join(dir_parts))
        return omero_annotation_list


    def filterProjectFunction(self, project):
        self.logger.info("project name="+project.getName())
        if project.getName().startswith(self.filterProjectName):
            return True
        else:
            return False
    
    def loadFileOrDir(self, directory,  project=None, dataset=None, filenames=None):
        if filenames is not None:
            n_files = len(filenames)
        else:
            n_files = 0

        message = f"loadFileOrDir with: Directory={directory}, " + \
                  f"project={project}, dataset={dataset}, # of files={n_files}"
        self.logger.info(message)
        
        #Either load individual files (if names supplied) or load whole dir
        if filenames is not None:
            for filename in filenames:
                fullPath=directory+"/"+filename
                self.logger.info("loading file="+fullPath)
                try:
                    self.load(fullPath, project, dataset)
                except Exception as e:
                    self.logger.warning("OmeroService Unexpected error loading file:" + str(e))
                    self.logger.warning("Skipping " + fullPath + " and continuing")
                    continue
                
        else:
            self.logger.info("loading directory")
            try:
                self.load(directory, project, dataset)
            except Exception as e:
                    self.logger.exception("OmeroService Unexpected error loading directory:" + str(e))

    def load(self, path, project=None, dataset=None):  
        self.logger.info("-"*10)
        self.logger.info("path="+path)
        self.logger.info("project="+str(project))
        self.logger.info("dataset="+str(dataset))
        self.getConnection()
            
       
        import_args = ["import"]
        if project is not None:
            self.logger.info("project in load is not None. Project name: "+project)
        if dataset is not None:
            self.logger.info("dataset in load is not None. Dataset name: "+dataset)
            dsId = self.create_containers(dataset, project)
            self.logger.info("datasetId="+str(dsId))
            import_args.extend(["--","-d", str(dsId), "--exclude","filename"])#"--no_thumbnails",,"--debug", "ALL"])
            #import_args.extend(["--","--transfer","ln_s","-d", str(dsId), "--exclude","filename"])#"--no_thumbnails",,"--debug", "ALL"])
            #import_args.extend(["--", "-d", str(dsId)])#"--no_thumbnails",,"--debug", "ALL"])
        else:
            self.logger.warning("dataset is None!!!!!!!!!!!!!!!!!!!!")
        
        self.logger.info('importing project=' + str(project) +  ', dataset=' + str(dataset) + ', filename=' + str(path))
        
        if(path.endswith('.pdf')):
            self.logger.info("We have a pdf document- loading as attachment "+str(path))#we need to upload as an attachment
            namespace = "imperial.training.demo"
            fileAnn = self.conn.createFileAnnfromLocalFile(str(path), mimetype=None, ns=namespace, desc=None)
            self.logger.info("fileAnn="+str(fileAnn))
            datasetForAnnotation = self.conn.getObject("Dataset", dsId)
            self.logger.info( "Attaching FileAnnotation to Dataset: " + str(datasetForAnnotation) + ", File ID: " + str(fileAnn.getId()) + ", File Name: " + fileAnn.getFile().getName() + ", Size:" + str(fileAnn.getFile().getSize()))
            self.logger.info("Dataset="+str(datasetForAnnotation))
            datasetForAnnotation.linkAnnotation(fileAnn)
            self.logger.info("linked annotation!")
        else:
            import_args.append(path)
            self.cli.invoke(import_args, strict=True)
        # KB 07/09/2021 - for omero 5.6 closing the session prevents other
        # images from being uploaded. This line worked in 5.4
        #self.conn._closeSession()
        
    def create_containers(self, dataset, project=None):
        """
        Creates containers with names provided if they don't exist already.
        Returns Dataset ID.
        """

        params = omero.sys.Parameters()
        params.theFilter = omero.sys.Filter()
        params.theFilter.ownerId = wrap(self.conn.getUser().getId())

        from omero.rtypes import rstring
        d = None
        prId = None
        if project is not None:
            #p = self.conn.getObject("Project", attributes={'name': project}, params=params)
            p = [proj for proj in self.conn.getObjects("Project", attributes={'name': project})]
            n_projects = len(p)
            if n_projects == 0:
                self.logger.info("Creating Project:" + project)
                p = omero.model.ProjectI()
                p.name = wrap(str(project))
                prId = self.conn.getUpdateService().saveAndReturnObject(p).id.val
                self.logger.info("Project id after created="+str(prId))
            elif n_projects > 1:
                # Project names must be unique
                raise Exception(f"More than one project with name '{project}' found - cannot choose project to load files into!!!")
            else:
                p = p[0]
                self.logger.info( "Using Project:" + project + ":" + p.getName())
                prId = p.getId()
                # Since Project already exists, check children for Dataset
                for c in p.listChildren():
                    self.logger.info("c getname="+c.getName())
                    if c.getName() == dataset:
                        self.logger.info("c=d matches name")
                        d = c
    
        if d is None:
            self.logger.info( "Creating Dataset:" + dataset)
            d = omero.model.DatasetI()
            d.name = wrap(str(dataset))
            dsId = self.conn.getUpdateService().saveAndReturnObject(d).id.val
            if prId is not None:
                self.logger.info("Linking Project-Dataset...")
                link = omero.model.ProjectDatasetLinkI()
                link.child = omero.model.DatasetI(dsId, False)
                link.parent = omero.model.ProjectI(prId, False)
                self.conn.getUpdateService().saveObject(link)
                
        else:
            self.logger.info( "Using Dataset:" + dataset + ":" + d.getName())
            dsId = d.getId()
        
        return dsId
    
    def print_obj(self, obj, indent=0):
        """
        Helper method to display info about OMERO objects.
        Not all objects will have a "name" or owner field.
        """
        msg = """%s%s:%s  Name:"%s" (owner=%s)""" % (\
                " " * indent,
                obj.OMERO_CLASS,\
                obj.getId(),\
                obj.getName(),\
                obj.getOwnerOmeName())
        self.logger.info(msg)
    
if __name__ == "__main__":
    main(sys.argv[1:]) 
