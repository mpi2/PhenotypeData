#!/usr/bin/python
import platform
import locale
import time
import omero
import sys
import getopt
import requests
from OmeroCrawler import OmeroCrawler
from Solr import Solr


def main(argv):
    print "running main OmeroCrawler"
    solrRoot="http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/"
    omeroHost='ves-ebi-cf'
    begin=0
    end=100000000
    context="dev"# can be live which changes the render_image urls we point to not the solr url
    #omeroHost="ves-ebi-ce"#localhost
    
    
    try:
          opts, args = getopt.getopt(argv,"s:o:b:e:c:",[])
    except getopt.GetoptError:
        print 'error OmeroCrawler.py -d <rootDestinationDir> -s <solrRoot> -o <omerohost>'
        sys.exit(2)
    for opt, arg in opts:
        print 'succcess arg=', arg
        if opt in ("-s", "--solrRoot"):
             solrRoot = arg
        elif opt in ("-o", "--omeroHost"):
             omeroHost = arg
        elif opt in ("-b", "--begin"):
             begin = arg
             print "setting begin="+str(begin)
        elif opt in ("-e", "--end"):
             end = arg
             print "setting end="+str(end)
        elif opt in ("-c", "--context"):
            context=arg
            print "context="+context
            
    print "solrRoot "+ solrRoot+ "omeroHost "+omeroHost
    print "getting urls from file"
    newUrls=[]
    with open('/Users/jwarren/Documents/DataImportFiles/500ErrorsForDev', 'r') as f:
        urls = f.readlines()
    f.closed


    for url in urls:
        newUrl=url.split('url=')[1]
        print "newUrl="+str(newUrl)
        newUrls.append(newUrl)
    
    omeroCrawler=OmeroCrawler(solrRoot, omeroHost)
    omeroCrawler.crawlUrls(newUrls, int(begin), int(end), context)
    
    
if __name__ == "__main__":
    main(sys.argv[1:]) 
        