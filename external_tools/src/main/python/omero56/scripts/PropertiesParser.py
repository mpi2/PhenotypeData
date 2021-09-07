# -*- coding: utf-8 -*-
import configparser
import logging

class PropertiesParser(object):

    """Parse a java like properties file

    Parser wrapping around ConfigParser allowing reading of java like
    properties file. Based on stackoverflow example:
    https://stackoverflow.com/questions/2819696/parsing-properties-file-in-python/2819788#2819788

    Example usage
    -------------
    >>> pp = PropertiesParser()
    >>> props = pp.parse('/home/kola/configfiles/dev/application.properties')
    >>> print props
    
    """

    def __init__(self):
        self.secheadname = 'fakeSectionHead'
        self.sechead = '[' + self.secheadname + ']\n'
        #self.logger = logging.getLogger(__name__)

    def readline(self):
        if self.sechead:
            try:
                return self.sechead
            finally:
                self.sechead = None
        else:
            return self.fp.readline()

    def parse(self, filepath):
        """Parse file containing java like properties."""

        try:
            self.fp = open(filepath)
            cp = configparser.SafeConfigParser()
            cp.readfp(self)
            self.fp.close()

            # reset the section head incase the parser will be used again
            self.sechead = '[' + self.secheadname + ']\n'
            return cp.items(self.secheadname)
        except Exception as e:
            #self.logger.error("Problem parsing " + filepath + ". Error message: " + str(e))
            print("Problem parsing " + filepath + ". Error message: " + str(e))
            return {}
            
