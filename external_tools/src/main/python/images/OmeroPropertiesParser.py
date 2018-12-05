# -*- coding: utf-8 -*-
from PropertiesParser import PropertiesParser
import os
class OmeroPropertiesParser(PropertiesParser):

    """Parse omero properties from a java like properties file

    On creation can set path to properties file based on MI conventions used
    for java code (i.e. dev, prod or live folder in configfiles folder in 
    user's home directory

    Has a method (getOmeroProps) to allow return of omero properties (assumed 
    to be namespaced by 'omero.'
    
    As of 27/10/2017 the following are expected in the properties file:

        datasource.komp2.url
        datasource.komp2.username
        datasource.komp2.password
        
        omero.rootdestinationdir=
        omero.solrurl=
        omero.omerohost=
        omero.omeroport=
        omero.omerouser=
        omero.omeropass=
        omero.omerogroup=

    datasource.komp2.url is used to get the hostname, port and db name for
    the komp2 database. It is assumed to be of the form:
        jdbc:mysql://mysql-mi-dev:4356/jenkins_dev_komp2...

    Example usage
    -------------
    >>> pp = OmeroPropertiesParser('dev') # looks for '/home/kola/configfiles/dev/application.properties'
    >>> props = pp.getOmeroProps()
    >>> print props
    >>> rootDestinationDir = props['rootdestinationdir']
    
    """

    def __init__(self, profile="dev"):
        super(OmeroPropertiesParser, self).__init__()
        self.profile = profile

    def __getPathFromProfile(self):
        if len(self.profile) == 0:
            return ""
        else:
            return os.path.join(os.environ['HOME'],'configfiles', self.profile, 'application.properties')
        

    def getOmeroProps(self, filepath="", omerons="omero.", clargs=None):
        """Get omero properties from java like properties file
        
        Keyword arguments:
        filepath -- the path of the file. If empty computes this from profile
        omerons -- the namespace defining omero properties
        clargs -- parser object containing command line parameters as namespace
        
        Returns:
            dictionary whose keys are omero property names stripped of namespace

        """
        
        if len(filepath) == 0:
            filepath = self.__getPathFromProfile()

        props = self.parse(filepath)

        omeroprops = {}
        for key, value in props:
            if key.find(omerons) == 0:
                omerokey = "".join(key.split(omerons)[1:])
                omeroprops[omerokey] = value
            elif key.find('datasource.komp2.username') == 0:
                omeroprops['komp2user'] = value
            elif key.find('datasource.komp2.password') == 0:
                omeroprops['komp2pass'] = value
            elif key.find('datasource.komp2.url') == 0:
                komp2parts = value.split('mysql://')[-1]
                try:
                    komp2host, komp2parts = komp2parts.split(':')[:2]
                except:
                    komp2host, komp2parts = komp2parts.split('/')[:2]
                    komp2port = "3306"
                try:
                    komp2port, komp2parts = komp2parts.split('/')[:2]
                except:
                    pass
                komp2db = komp2parts.split('?')[0]
                omeroprops['komp2host'] = komp2host
                omeroprops['komp2port'] = komp2port
                omeroprops['komp2db'] = komp2db

        # If no command line args object passed return
        if clargs is None:
            return omeroprops

        # Otherwise attempt to override values
        # dict comprehensions do not work with python < 2.7 (server is 2.6.6!)
        # clargs_dict = {k.lower(): v for k, v in vars(clargs).items()}
        clargs_dict = dict((k.lower(), v) for k, v in vars(clargs).items())
        for k in omeroprops.keys():
            k2 = k.lower()
            if k2 in clargs_dict and clargs_dict[k2] is not None:
                omeroprops[k] = clargs_dict[k2]

        #Now add values that begin with namespace but are not in properties
        #If the namespace ends with a dot remove it
        if omerons[-1] == '.':
            omerons = omerons[:-1]
        for k in clargs_dict.keys():
            if clargs_dict[k] is not None and k not in omeroprops.keys() and k.find(omerons) == 0:
                omeroprops[k] = clargs_dict[k]
        return omeroprops

