#!/usr/bin/python
import sys

import requests
import json
import collections
import ntpath
import logging

def main(argv):
    print "running solr main method"
    solr=Solr("http://wp-np2-e1.ebi.ac.uk:8090/mi/impc/dev/solr/")
    solr.getAllPhenCenterPipelinesAndProceduresAndParameters()

class Solr:
    def __init__(self, solrRootUrl):
        self.logger = logging.getLogger(__name__)
        self.numberOfFilesInSolr=0
        self.solrRootUrl=solrRootUrl
        self.solrExperimentRootUrl=solrRootUrl+"experiment/"
        self.logger.info("initialising solr python object with solr root url="+solrRootUrl)
        self.logger.info("initialising solr python object with solrExperimentRootUrl="+self.solrExperimentRootUrl)
        #pipelines= http://wp-np2-e1.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=observation_type:image_record&fq=download_file_path:(download_file_path:*mousephenotype.org*%20AND%20!download_file_path:*.pdf%20!download_file_path:*.mov)&facet.mincount=1&facet=true&facet.field=pipeline_stable_id&wt=json&indent=on&rows=0
        self.phenotyping_centers=[]
        self.pipelines=[]
        self.procedures=[]
        self.parameters=[]
        # KB 21/11/2017 Added OR part to allow 3i images to be pulled as well
        self.standardFilter='download_file_path:(*mousephenotype.org*%20OR%20*images/3i*)%20AND%20!download_file_path:*.mov%20AND%20!download_file_path:*.fcs%20AND%20!download_file_path:*.bz2'

        #self.getParametersForProcedureAndPipeline("TCP_001", "IMPC_XRY_001")

    #procedures for pipeline=http://wp-np2-e1.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=observation_type:image_record&fq=download_file_path:(download_file_path:*mousephenotype.org*%20AND%20!download_file_path:*.pdf%20!download_file_path:*.mov%20AND%20pipeline_stable_id:TCP_001)&facet.mincount=1&facet=true&facet.field=procedure_stable_id&wt=json&indent=on&rows=0
    #parameters for pipline, procedure http://wp-np2-e1.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=observation_type:image_record&fq=download_file_path:(download_file_path:*mousephenotype.org*%20AND%20!download_file_path:*.pdf%20!download_file_path:*.mov%20AND%20pipeline_stable_id:TCP_001%20AND%20procedure_stable_id:IMPC_XRY_001)&facet.mincount=1&facet=true&facet.field=parameter_stable_id&wt=json&indent=on&rows=0
    #get first 100 image_record names for pipeline, procedure, parameter
    #http://wp-np2-e1.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=observation_type:image_record&fq=download_file_path:(download_file_path:*mousephenotype.org*%20AND%20!download_file_path:*.pdf%20!download_file_path:*.mov%20AND%20pipeline_stable_id:TCP_001%20AND%20procedure_stable_id:IMPC_XRY_001%20AND%20parameter_stable_id:IMPC_XRY_048_001)&facet.mincount=1&facet=true&facet.field=parameter_stable_id&wt=json&indent=on&rows=100

    #get a map of directories paths to list of filenames

    def getAllPhenCenterPipelinesAndProceduresAndParameters(self):
        self.phenotyping_centers=self.getPhenotypingCenters()
        self.directory_map = collections.OrderedDict()
        for phenCenter in self.phenotyping_centers:
            #print "phenCenter="+phenCenter
            self.pipelines=self.getPipelines(phenCenter)
            for pipe in self.pipelines:
                self.procedures=self.getProceduresForPipeline(phenCenter, pipe)
                #print "pipe="+pipe
                for proc in self.procedures:
                    #print "proc="+proc
                    self.parameters=self.getParametersForProcedureAndPipeline(phenCenter, pipe, proc)
                    for param in self.parameters:
                        directoryKey=phenCenter+"/"+pipe+"/"+proc+"/"+param
                        self.logger.info("directoryKey="+directoryKey)
                        filenames=self.getImageObservationsForParametersForProcedureAndPipeline(phenCenter, pipe, proc, param)
                        #print "filenames="+str(filenames)
                        self.directory_map[directoryKey] = filenames
                        #directory_map['b'] = 'B'
                        #directory_map['c'] = 'C'
                        #for k, v in d.items():
                        #    print k, v

        self.logger.info("number of image files found in solr="+str(self.numberOfFilesInSolr))
        self.logger.info("Directory map size="+str(len(self.directory_map)))
        return self.directory_map
        #get a map of directories paths to list of filenames
    def getPhenotypingCenters(self):
        phenotypingCenterUrl=self.solrExperimentRootUrl+'select?q=observation_type:image_record&fq=download_file_path:('+self.standardFilter+')&facet.mincount=1&facet=true&facet.field=phenotyping_center&wt=json&indent=on&rows=0&facet.limit=-1'
        phenotyping_centers=[]
        #print "running get pipelines "+phenotypingCenterUrl
        try:
            v = json.loads(requests.get(phenotypingCenterUrl).text)
            self.logger.info( v['facet_counts']['facet_fields']['phenotyping_center'])
            phenCenters=v['facet_counts']['facet_fields']['phenotyping_center']
        except Exception as e:
            self.logger.exception("Error trying to load request from Solr. Error msg: " + str(e))

        i=0
        for doc in phenCenters:
            #print "doc="+str(doc)
            if i %2 ==0: #add every other value in this array as solr alternates id with the count
                #print "adding pipeline "+str(doc)
                phenotyping_centers.append(doc)
            i=i+1
        return phenotyping_centers

    def getPipelines(self, phenCenter):
        pipelineUrl=self.solrExperimentRootUrl+'select?q=observation_type:image_record&fq=download_file_path:('+self.standardFilter+'%20AND%20phenotyping_center:"'+phenCenter+'")&facet.mincount=1&facet=true&facet.field=pipeline_stable_id&wt=json&indent=on&rows=0&facet.limit=-1'
        pipelines=[]
        #print "running get pipelines "+pipelineUrl
        v = json.loads(requests.get(pipelineUrl).text)
        #print  v['facet_counts']['facet_fields']['pipeline_stable_id']
        pipes=v['facet_counts']['facet_fields']['pipeline_stable_id']
        i=0
        for doc in pipes:
            #print "doc="+str(doc)
            if i %2 ==0: #add every other value in this array as solr alternates id with the count
                #print "adding pipeline "+str(doc)
                pipelines.append(doc)
            i=i+1
        return pipelines

    def getProceduresForPipeline(self, phenCenter, pipeline_stable_id):
        pipeProcedureUrl=self.solrExperimentRootUrl+'select?q=observation_type:image_record&fq=download_file_path:('+self.standardFilter+'%20AND%20phenotyping_center:"'+phenCenter+'"%20AND%20pipeline_stable_id:"'+pipeline_stable_id+'")&facet.mincount=1&facet=true&facet.field=procedure_stable_id&wt=json&indent=on&rows=0&facet.limit=-1'
        #print "running get pipelines "+pipeProcedureUrl
        procedures=[]
        v = json.loads(requests.get(pipeProcedureUrl).text)
        docs=v['response']['docs']
        numFoundInSolr=v['response']['numFound']
        #print  v['facet_counts']['facet_fields']['procedure_stable_id']
        procs=v['facet_counts']['facet_fields']['procedure_stable_id']
        i=0
        for doc in procs:
            #print "doc="+str(doc)
            if i %2 ==0: #add every other value in this array as solr alternates id with the count
                #print "adding procedure "+str(doc)
                procedures.append(doc)
            i=i+1
        return procedures
        #print 'number found in solr='+str(numFoundInSolr)

    def getParametersForProcedureAndPipeline(self, phenCenter, pipeline_stable_id, procedure_stable_id):
        parameters=[]
        pipeProcedureParameterUrl=self.solrExperimentRootUrl+'select?q=observation_type:image_record&fq=download_file_path:('+self.standardFilter+'%20AND%20phenotyping_center:"'+phenCenter+'"%20AND%20pipeline_stable_id:"'+pipeline_stable_id+'"%20AND%20procedure_stable_id:"'+procedure_stable_id+'")&facet.mincount=1&facet=true&facet.field=parameter_stable_id&wt=json&indent=on&rows=0&facet.limit=-1'
        #print "running get parameters "+pipeProcedureParameterUrl
        v = json.loads(requests.get(pipeProcedureParameterUrl).text)
        #print v['facet_counts']
        docs=v['response']['docs']
        numFoundInSolr=v['response']['numFound']
        params=v['facet_counts']['facet_fields']['parameter_stable_id']
        i=0
        for doc in params:
            #print "doc="+str(doc)
            if i %2 ==0: #add every other value in this array as solr alternates id with the count
                #print "adding procedure "+str(doc)
                parameters.append(doc)
            i=i+1
        return parameters
        #print 'number found in solr='+str(numFoundInSolr)

    def getImageObservationsForParametersForProcedureAndPipeline(self , phenCenter, pipeline_stable_id, procedure_stable_id, parameter_stable_id):
        filenames=[]
        pipeProcedureParameterUrl=self.solrExperimentRootUrl+'select?q=observation_type:image_record&fq=download_file_path:('+self.standardFilter+'%20AND%20phenotyping_center:"'+phenCenter+'"%20AND%20pipeline_stable_id:"'+pipeline_stable_id+'"%20AND%20procedure_stable_id:"'+procedure_stable_id+'"%20AND%20parameter_stable_id:"'+parameter_stable_id+'")&facet.mincount=1&facet=true&facet.field=parameter_stable_id&wt=json&indent=on&rows=1000000&facet.limit=-1'
        self.logger.info(pipeProcedureParameterUrl)
        try:
            v = json.loads(requests.get(pipeProcedureParameterUrl).text)
            #print v['facet_counts']
            docs=v['response']['docs']
            numFoundInSolr=v['response']['numFound']
            self.numberOfFilesInSolr=self.numberOfFilesInSolr+numFoundInSolr
            self.logger.info("number of observations for this key is="+str(numFoundInSolr))
            params=v['facet_counts']['facet_fields']['parameter_stable_id']
        except Exception as e:
            self.logger.exception("Error trying to load request from Solr. Error msg: " + str(e))

        i=0
        for doc in docs:
            download_file_path=doc['download_file_path']
            #print "download_file_path="+download_file_path
            filename=ntpath.basename(download_file_path.lower())
            #print "filename="+filename
            filenames.append(filename)
            i=i+1
        return filenames

    def getRenderImageUrls(self):
        urls=[]
        jpegsUrl=self.solrRootUrl+'impc_images/select?q=!omero_id:0&fl=jpeg_url&fq=jpeg_url:*omero*&wt=json&indent=on&rows=100000000'
        try:
            v = json.loads(requests.get(jpegsUrl).text)
            #print v['facet_counts']
            docs=v['response']['docs']
            numFoundInSolr=v['response']['numFound']
            self.numberOfFilesInSolr=self.numberOfFilesInSolr+numFoundInSolr
            self.logger.info("number of observations for this key is="+str(numFoundInSolr))
        except Exception as e:
            self.logger.exception("Error trying to load request from Solr. Error msg: " + str(e))

        i=0
        for doc in docs:
            jpeg_url=doc['jpeg_url']

            if jpeg_url.startswith("//"):
                jpeg_url = "http:" + jpeg_url

            #print "adding render image url="+jpeg_url
            urls.append(jpeg_url)
            i=i+1
            if(i % 10000==0):
                 self.logger.info(str(i)+" images imported into url list")

        self.logger.info("jpeg urls size="+str(len(urls)))
        return urls

if __name__ == "__main__":
    main(sys.argv[1:])
