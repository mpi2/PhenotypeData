#!/usr/bin/python
import platform
import locale
import time
import omero
import sys
import getopt
import requests
from Solr import Solr


def main(argv):
    print "running main OmeroCrawler"
    solrRoot="https://www.ebi.ac.uk/mi/impc/solr/"
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
    omeroCrawler=OmeroCrawler(solrRoot, omeroHost)
    omeroCrawler.crawl(int(begin), int(end), context)
    
class OmeroCrawler:
    def __init__(self, solrRoot, omeroHost):
        print "Initialising OmeroCrawler"
        self.solrRoot=solrRoot
        self.omeroHost=omeroHost
        
    def crawl(self, begin, end, context=None):
        print "crawling"
        solr=Solr(self.solrRoot)
        urls=solr.getRenderImageUrls()
        self.crawlUrls(urls, begin, end, context)

    def crawlUrls(self, urls, begin, end, context):
        i=0
        for url in urls:
            #print "i="+str(i)
            if i>=begin:
                url=url.replace('render_image','render_thumbnail')
                if context is not None:
                    #http://www.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/141436
                    #http://ves-oy-ca/omero/webgateway/render_thumbnail/89930/200/
                    #temporarily replace live link from solr with omero dev link 
                    url=url.replace('www','wwwdev')
                    if context == 'live':
                        #print "context is live and omero host=" + self.omeroHost 
                        url=url.replace('www.ebi.ac.uk/mi/media',self.omeroHost)
                #print url
                r = requests.get(url)
                if i % 100 ==0:
                    print i
                if (r.status_code != requests.codes.ok):
                    print "i="+str(i) +"url="+ url+" status="+str(r.status_code)
                    r2 = requests.get(url.replace('render_thumbnail', 'render_image'))#request a full image if response not 200 to generate a pyramid
                if i>=end:
                    break
            i=i+1
            
        
if __name__ == "__main__":
    main(sys.argv[1:]) 
        
    
