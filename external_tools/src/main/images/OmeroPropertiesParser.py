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

        omero.rootdestinationdir=
        omero.solrurl=
        omero.komp2host=
        omero.komp2port=
        omero.komp2db=
        omero.komp2user=
        omero.komp2pass=
        
        omero.omerohost=
        omero.omeroport=
        omero.omerouser=
        omero.omeropass=
        omero.omerogroup=

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
        

    def getOmeroProps(self, filepath="", omerons="omero."):
        """Get omero properties from java like properties file
        
        Keyword arguments:
        filepath -- the path of the file. If empty computes this from profile
        omerons -- the namespace defining omero properties
        
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
        return omeroprops
