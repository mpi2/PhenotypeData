#!/usr/bin/python

import platform
import locale
import time
import sys


import os

def main(argv):
    dirWalker=DirectoryWalker()
    root_dir="/Users/jwarren/Documents/images/impc/"
    rel_directory_to_filenames_map=dirWalker.getFilesFromDir(root_dir)
    for key, value in rel_directory_to_filenames_map.items():
        print "key="+key
        print "value="+str(value)
        
    

class DirectoryWalker:
        
    
    def __init__(self):
        print "initiating DirectoryWalker object" 
       
        
    def getFilesFromDir(self, root_directory):
        self.root_directory=root_directory
        self.rel_directory_to_filenames_map={}
        for dirName, subdirList, fileList in os.walk(self.root_directory):
            #print('Found directory: %s' % dirName)
            relDir=dirName.split('impc/')[1]
            for fname in fileList:
                if relDir.count('/') == 3:
                    self.rel_directory_to_filenames_map.setdefault(relDir,list()).append( fname )
        return self.rel_directory_to_filenames_map

if __name__ == "__main__":
    main(sys.argv[1:]) 
