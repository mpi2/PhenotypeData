#!/usr/bin/python

"""Create a CSV file using the DCC media API

"""
import sys
import os
import requests
import json
import argparse

uniqueUris=set()

parser = argparse.ArgumentParser(
    description='Create CSV file of images ready to download using DCC media API'
)
parser.add_argument('-o', '--output-path', default="./media_api_files.csv",
                    dest='output_path',
                    help='Path to save the csv file'
)
parser.add_argument('-s', '--start', help='record to start from')
parser.add_argument('-r', '--result-size', 
                    help='Number of records to get per site. ' + \
                    'Has no effect if "start" not supplied')
parser.add_argument('-v', '--verbose', action='store_true', help='print verbose messages')

args = parser.parse_args()

# We get the files we are interested in for each site using the 
# media API
sites = [
    ('bcm', 'BCM',),
    ('gmc','HMGU',),
    ('h', 'MRC Harwell'),
    ('ics', 'ICS',),
    ('j', 'JAX',),
    ('tcp', 'TCP'),
    ('ning', 'NING',),
    ('rbrc', 'RBRC',),
    ('ucd', 'UC Davis',),
    ('wtsi', 'WTSI',),
    ('kmpc', 'KMPC',),
    ('ccpcz', 'CCP-IMG',),
]

header = "checksum,download_file_path,phenotyping_center," + \
         "pipeline_stable_id,procedure_stable_id,datasource_name," + \
         "parameter_stable_id\n"
datasource_name = "IMPC"
im_details = [header,]
numFound = 0

for site, phenotyping_center in sites:
    query_string = f"https://api.mousephenotype.org/media/dccUrl/{site}?status=done"
    if args.start is not None:
        query_string += f"&start={args.start}"
        
    if args.result_size is not None:
        query_string += f"&resultsize={args.result_size}"
    
    if args.verbose:
        print(query_string)

    v = json.loads(requests.get(query_string).text)
    try:
        docs = v['mediaFiles']
    except KeyError as key_error:
        print("WARNING - no media files returned for site: " + site)
        continue
        
    numFound += len(docs)

    for doc in docs:
        download_file_path=doc['dccUrl']
        download_file_path=download_file_path.lower()
        if download_file_path.find('mousephenotype.org') < 0 or \
                download_file_path.endswith('.mov') or \
                download_file_path.endswith('.bz2'):
            continue


        # On 13/11/2019 got a KeyError for phenotyping centre. This 
        # should not happen, but code modified appropriately
        try:
            pipeline_stable_id=doc['pipelineKey']
            procedure_stable_id=doc['procedureKey']
            parameter_stable_id=doc['parameterKey']
            im_details.append(",".join([
                    doc['checksum'],
                    doc['dccUrl'],
                    phenotyping_center,
                    doc['pipelineKey'],
                    doc['procedureKey'],
                    datasource_name,
                    doc['parameterKey'],
                ]) + "\n"
            )
        except KeyError as e:
            print("Key " + str(e)+  " not returned by media API - not including " + download_file_path)
            continue

with open(args.output_path, "wt") as fid:
    fid.writelines(im_details)
print(f"Found {numFound} urls. Written output to {args.output_path}")
