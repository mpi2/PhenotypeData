#!/usr/bin/python
import platform
import argparse
import locale
import time
import omero
import sys
import collections
import omero.all
import omero.rtypes
import omero
import omero.cli
from omero.gateway import *
from omero.rtypes import wrap
from omero.model import DatasetI, ProjectI
from common import splitString
from OmeroPropertiesParser import OmeroPropertiesParser

def main(argv):
    print "running main method in OmeroService"

    parser = argparse.ArgumentParser(
        description='Create an OmeroService object to interact with Omero'
    )
    parser.add_argument('-p', '--profile', dest='profile', default='dev',
                        help='profile to read omero properties from'
    )

    args = parser.parse_args()

    try:
        pp = OmeroPropertiesParser(args.profile)
        omeroProps = pp.getOmeroProps()
    except:
        omeroProps = {}
    
    omeroHost=omeroProps['omerohost']
    omeroPort=omeroProps['omeroport']
    omeroUsername=omeroProps['omerouser']
    omeroPass=omeroProps['omeropass']
    group=omeroProps['omerogroup']

    omeroS=OmeroService(omeroHost, omeroPort, omeroUsername, omeroPass, group)
    #directory_to_filename_map=omeroS.getImagesAlreadyInOmero("HMGU_HMGU_001")
    #print str(len(directory_to_filename_map))+" directories with images already in omero"
    #for key, value in directory_to_filename_map.items():
    #    print key +" "+str(value)
        
    #omeroS.loadFileOrDir("/nfs/komp2/web/images/impc/WTSI/MGP_001/MGP_XRY_001/IMPC_XRY_034_001/","WTSI-MGP_001", "IMPC_XRY_001-IMPC_XRY_034_001", ["97075.dcm"])
    omeroS.loadFileOrDir("/nfs/komp2/web/images/clean/impc/JAX/JAX_001/JAX_ERG_001/JAX_ERG_027_001/206931.pdf","MRC Harwell", "MRC Harwell-MGP_001-IMPC_XRY_001-IMPC_XRY_034_001")
    
class OmeroService:
    def __init__(self, omeroHost, omeroPort, omeroUsername, omeroPass, group):
        print "init OmeroService"
        self.omeroHost=omeroHost
        self.omeroPort=omeroPort
        self.omeroUsername=omeroUsername
        self.omeroPass=omeroPass
        self.group=group
        self.conn=self.getConnection()
        
    def getConnection(self):
        self.cli = omero.cli.CLI()
        self.cli.loadplugins()
        self.cli.invoke(["login", self.omeroUsername+'@'+self.omeroHost, "-w", self.omeroPass, "-C"], strict=True)
        #cli.invoke(["login", "%s@localhost" % user, "-w", passw, "-C"], strict=True)
        self.cli.invoke(["sessions", "group", self.group], strict=True)
        sessionId = self.cli._event_context.sessionUuid
        self.conn = BlitzGateway(host=self.omeroHost)
        self.conn.connect(sUuid = sessionId)
    
        user = self.conn.getUser()
        #print "Current user:"
        #print "   ID:", user.getId()
        #print "   Username:", user.getName()
        #print "   Full Name:", user.getFullName()
    
        #print "Member of:"
        #for g in self.conn.getGroupsMemberOf():
            #print "   ID:", g.getId(), " Name:", g.getName()
        group = self.conn.getGroupFromContext()
        #print "Current group: ", group.getName()
    
        #print "Other Members of current group:"
        #for exp in conn.listColleagues():
        #    print "   ID:", exp.getId(), exp.getOmeName(), " Name:", exp.getFullName()
    
        #print "Owner of:"
        #for g in conn.listOwnedGroups():
        #    print "   ID:", g.getName(), " Name:", g.getId()
    
        # New in OMERO 5
        #print "Admins:"
        #for exp in conn.getAdministrators():
        #   print "   ID:", exp.getId(), exp.getOmeName(), " Name:", exp.getFullName()
    
        # The 'context' of our current session
        ctx = self.conn.getEventContext()
        # print ctx     # for more info
        #print ctx
        return self.conn
    
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
                print "Problem extracting root_dir from clientpath " + ofd[0].val
                print "Error was: " + e.message
                omero_file_list.append(ofd[0].val)
                continue
            #if indir is None or len(indir) < 1:
            #    print "Did not extract root_dir from " + ofd[1]
            omero_file_list.append(ofd_path)
        return omero_file_list

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
        print "project name="+project.getName()
        if project.getName().startswith(self.filterProjectName):
            return True
        else:
            return False
    
    def loadFileOrDir(self, directory,  project=None, dataset=None, filenames=None):
        print "loadFileOrDir with:", directory,  project, dataset, filenames
        #chop dir to get project and dataset
        
        #if filenames is non then load the entire dir
        if filenames is not None:
            for file in filenames:
                fullPath=directory+"/"+file
                print "loading file="+fullPath
                try:
                    self.load(fullPath, project, dataset)
                except:
                    print "OmeroService Unexpected error loading file:", sys.exc_info()[0]
                    print "continue"
                    continue
                
        else:
            print "loading directory"
            try:
                self.load(directory, project, dataset)
            except:
                    print "OmeroService Unexpected error loading directory:", sys.exc_info()[0]
                    print "continue"
                    raise

    def load(self, path, project=None, dataset=None):  
        print "-"*10
        print "path=",path," project=", project,"dataset=",dataset
        #if self.cli is None or self.conn is None:
            #print "cli is none!!!!!"
        self.getConnection()
            
       
        import_args = ["import"]
        if project is not None:
            "project in load is not None"+project
            #project=project.replace(" ","-")
        if dataset is not None:
            print "dataset in load is not None"+dataset
            dsId = self.create_containers(self.cli, dataset, self.omeroHost, project)
            print "dsId="+str(dsId)
            import_args.extend(["--","-d", str(dsId), "--exclude","filename"])#"--no_thumbnails",,"--debug", "ALL"])
            #import_args.extend(["--","--transfer","ln_s","-d", str(dsId), "--exclude","filename"])#"--no_thumbnails",,"--debug", "ALL"])
            #import_args.extend(["--", "-d", str(dsId)])#"--no_thumbnails",,"--debug", "ALL"])
        else:
            print "dataset is None!!!!!!!!!!!!!!!!!!!!"
        
        print 'importing project=' ,project, 'dataset=',dataset, 'filename=', path
        
        if(path.endswith('.pdf')):
            print "We have a pdf document- loading as attachment "+str(path)#we need to upload as an attachment
            namespace = "imperial.training.demo"
            fileAnn = self.conn.createFileAnnfromLocalFile(str(path), mimetype=None, ns=namespace, desc=None)
            print "fileAnn="+str(fileAnn)
            datasetForAnnotation = self.conn.getObject("Dataset", dsId)
            print "Attaching FileAnnotation to Dataset: ", str(datasetForAnnotation)," File ID:", fileAnn.getId(), ",", fileAnn.getFile().getName(), "Size:", fileAnn.getFile().getSize()
            print "Dataset="+str(datasetForAnnotation)
            datasetForAnnotation.linkAnnotation(fileAnn)
            print "linked annotation!"
        else:
            import_args.append(path)
            #print " import args="
            #print import_args
            self.cli.invoke(import_args, strict=True)
        self.conn._closeSession()
        #print "-" * 100
        
    def create_containers(self, cli, dataset, omeroHost, project=None):
        """
        Creates containers with names provided if they don't exist already.
        Returns Dataset ID.
        """
        #print 'create containers method called'
        params = omero.sys.Parameters()
        params.theFilter = omero.sys.Filter()
        params.theFilter.ownerId = wrap(self.conn.getUser().getId())
        #print "ownerId="+conn.getUser().getId()
        #project=None
        from omero.rtypes import rstring
        d = None
        prId = None
        if project is not None:
            p = self.conn.getObject("Project", attributes={'name': project}, params=params)
            if p is None:
                print "Creating Project:", project
                p = omero.model.ProjectI()
                p.name = wrap(str(project))
                prId = self.conn.getUpdateService().saveAndReturnObject(p).id.val
                print "Project id after created="+str(prId)
            else:
                print "Using Project:", project, p
                prId = p.getId()
                # Since Project already exists, check children for Dataset
                for c in p.listChildren():
                    print "c getname="+c.getName()
                    if c.getName() == dataset:
                        print "c=d matches name"
                        d = c
    
        #if d is None:
        #    print "d is None"
        #    d = self.conn.getObject("Dataset", attributes={'name': dataset}, params=params)
    
        if d is None:
            print "Creating Dataset:", dataset
            d = omero.model.DatasetI()
            d.name = wrap(str(dataset))
            dsId = self.conn.getUpdateService().saveAndReturnObject(d).id.val
            if prId is not None:
                print "Linking Project-Dataset..."
                link = omero.model.ProjectDatasetLinkI()
                link.child = omero.model.DatasetI(dsId, False)
                link.parent = omero.model.ProjectI(prId, False)
                self.conn.getUpdateService().saveObject(link)
                
        else:
            print "Using Dataset:", dataset, d
            dsId = d.getId()
        
        return dsId
    
    def print_obj(self, obj, indent=0):
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
    
if __name__ == "__main__":
    main(sys.argv[1:]) 
