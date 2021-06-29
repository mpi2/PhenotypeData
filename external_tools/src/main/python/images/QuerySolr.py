"""
    Query cores in solr
"""

import requests
import json
import pandas as pd
from pandas.io.json import json_normalize

class QuerySolr:
    def __init__(self, host=None, port=None, core=None, query=None):
        self.set_host() if host is None else self.set_host(host)
        self.set_port() if port is None else self.set_port(port)
        self.set_core() if core is None else self.set_core(core)
        self.set_query() if query is None else self.set_query(query)

    def set_host(self, host="http://wp-np3-84.ebi.ac.uk"):
        self.host = host

    def set_port(self, port=8986):
        self.port = f":{port}"

    def clear_port(self):
        self.port = ""

    def set_core(self, core="solr/experiment"):
        self.core = core

    def set_query(self, query=""):
        self.query = query

    def get_query_string(self):
        try:
            return "/".join((self.host+self.port, self.core, self.query))
        except Exception as e:
            print("Could not create query string. Exception was: "+str(e))

    def run_query(self, return_raw=False):
        assert (self.core is not None), "The core to query is not defined - cannot run query"
        assert (self.query is not None and len(self.query) > 0) , "The query string is not defined - cannot run query"

        try:
            print("Processing query")
            v = json.loads(requests.get(self.get_query_string()).text)
            if return_raw:
                return v
            results = v['response']['docs']
            return json_normalize(results)
        except Exception as e:
            print("Could not run query - Error was: " + str(e))
            
# Instantiate and use object to get details from experiment core:        
def get_solr_expt_core_by_pipeline_and_parameter(parameter, pipeline=None, host=None, port=None, verbose=False):
    """
        Get metadata from solr expt core based on parameter and pipeline ids
    """
    qs = QuerySolr()
    if host is not None:
        qs.set_host(host)
    if port is not None:
        qs.set_port(port)
    qs.set_core("solr/experiment")
    query = "select?fq=observation_type:image_record&q=parameter_stable_id:" + parameter
    if pipeline is not None:
        query += "&fq=pipeline_stable_id:" + pipeline
    query += "&rows=1000000&wt=json&indent=on"
    # Add field list
    field_list = "age_in_days,age_in_weeks,allele_accession_id,allele_symbol,allelic_composition,biological_model_id,biological_sample_group,biological_sample_id,colony_id,datasource_name,date_of_birth,date_of_experiment,developmental_stage_acc,developmental_stage_name,download_file_path,experiment_id,experiment_source_id,external_sample_id,gene_accession_id,gene_symbol,genetic_background,id,litter_id,parameter_name,parameter_stable_id,phenotyping_center,phenotyping_center_id,pipeline_name,pipeline_stable_id,procedure_group,procedure_name,procedure_stable_id,production_center,project_name,sex,strain_accession_id,strain_name,weight,weight_date,weight_days_old,weight_parameter_stable_id,zygosity"
    query += "&fl=" + field_list
    
    qs.set_query(query)
    
    if verbose:
        print("Query string = " + qs.get_query_string())
    return qs.run_query()


# Instantiate and use object to get details from images core:        
def get_solr_image_core_by_pipeline_and_parameter(parameter, pipeline=None, host=None, port=None, verbose=False):
    """
        Get limited list of metadata from solr image core based on parameter and pipeline ids
    """
    qs = QuerySolr()
    if host is not None:
        qs.set_host(host)
    if port is not None:
        qs.set_port(port)
    qs.set_core("solr/impc_images")
    query = "select?fq=observation_type:image_record&q=parameter_stable_id:" + parameter
    if pipeline is not None:
        query += "&fq=pipeline_stable_id:" + pipeline
    query += "&rows=1000000&wt=json&indent=on"
    # Add field list
    field_list = "biological_sample_id,download_file_path,experiment_id,external_sample_id,increment_value"
    query += "&fl=" + field_list
    
    qs.set_query(query)
    
    if verbose:
        print("Query string = " + qs.get_query_string())
    return qs.run_query()
