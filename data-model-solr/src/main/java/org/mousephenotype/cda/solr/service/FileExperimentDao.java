/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package org.mousephenotype.cda.solr.service;

import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.util.NamedList;
import org.mousephenotype.cda.constants.Constants;
import org.mousephenotype.cda.db.pojo.DiscreteTimePoint;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.enumerations.BatchClassification;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.generic.util.JSONRestUtil;
import org.mousephenotype.cda.solr.service.dto.*;
import org.mousephenotype.cda.solr.web.dto.CategoricalDataObject;
import org.mousephenotype.cda.solr.web.dto.CategoricalSet;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class FileExperimentDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //"https://www.ebi.ac.uk/~hamedhm/windowing/DR9.2/jobs/ExtractedPValues/DR9.2_V1/";
    private static final String indexFilename="Index_V1_DR92.txt";
    private static final String successFileName="output_Successful.tsv";
    
    private final String rootStatsDirectory;
    private final String originalDirectory;//need this to chop off path from the index file for replacement with local root directory!
	private File indexFile;
	private Stream<String> succesfulOnly;


    
    public FileExperimentDao(String rootDataDirectory, String originalDirectory) {
        this.rootStatsDirectory = rootDataDirectory;
        this.originalDirectory=originalDirectory;
    }

    
    public File getFileByCenterProcedureParameterAndColonyId(String center, String procedure, String parameter, String colonyId, String zygosity, String metadata) {
    	//https://wwwdev.ebi.ac.uk/~hamedhm/windowing/DR8/jobs/Results_8/MARC/IMPC_HEM/IMPC_HEM_038_001/1110018G07Rik_HEPD0633_2_C09_1/homozygote/08aa37a898ab923b9ffdbd01c0077040/output_Successful.tsv
    	//similar file in DR 9.2 /nfs/nobackup/spot/mouseinformatics/HAMED_HA/DR9.2/jobs/Results_9.2_V1/MARC/IMPC_HEM/IMPC_HEM_038_001/1110018G07Rik_HEPD0633_2_C09_1/homozygote/08aa37a898ab923b9ffdbd01c0077040
    	File file=new File(rootStatsDirectory+"/"+center+"/"+procedure+"/"+parameter+"/"+colonyId+"/"+zygosity+"/"+metadata+"/"+successFileName);
    	return file;
    }


	public File readIndexFile() {
		// TODO Auto-generated method stub
		this.indexFile=new File(rootStatsDirectory+"/"+indexFilename);
		try (Stream<String> stream = Files.lines(Paths.get(rootStatsDirectory+"/"+indexFilename))) {

			//stream.forEach(System.out::println);
			succesfulOnly=stream.filter(string -> string.endsWith(successFileName));
			
			//succesfulOnly.forEach(System.out::println);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return indexFile;
	}

    
	
}
