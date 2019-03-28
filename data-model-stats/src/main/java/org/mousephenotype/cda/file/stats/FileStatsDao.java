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
package org.mousephenotype.cda.file.stats;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class FileStatsDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //"https://www.ebi.ac.uk/~hamedhm/windowing/DR9.2/jobs/ExtractedPValues/DR9.2_V1/";
    private static final String indexFilename="Index_V1_DR92.txt";
    private static final String successFileName="output_Successful.tsv";
    
    
    private final String rootStatsDirectory;
   
    private final String originalDirectory;//need this to chop off path from the index file for replacement with local root directory!
	private File indexFile;
	private List<String> succesfulOnly;

	@Inject
    public FileStatsDao(@Value("${root_stats_directory}")String rootDataDirectory,@Value("${original_stats_directory}") String originalDirectory) {
        this.rootStatsDirectory = rootDataDirectory;
        this.originalDirectory=originalDirectory;
        this.readIndexFile();
        
    }

    public Stats getStatsSummary(String center, String procedure, String parameter, String colonyId, String zygosity, String metadata) {
    	String path=this.getFilePathFromIndex(center, procedure, parameter, colonyId, zygosity, metadata);
    	Stats result=null;
    	if(path.isEmpty()) {
    		System.err.println("no file at that path "+path);
    	}else {
    		result= this.readSuccesFile(path);
    	}
    	return result;
    }
    
    private Stats readSuccesFile(String path) {
    	//need the details section of the json object
    	List<String> lines=null;
    	try (Stream<String> stream = Files.lines(Paths.get(path))) {
			//stream.forEach(System.out::println);
			lines = stream.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	assert(lines.size()==1);
    	String data=lines.get(0);
    	String[]sections=data.split("\"result\"");
    	System.out.println(sections.length);
    	String summaryInfo=sections[0].replace("{", "");//remove useless { on the end!!
    	String json="{\"result\""+sections[1];
    	System.out.println("summaryInfo="+summaryInfo);
    	
    	
    	
    	//System.out.println("json="+json);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	StatsJson value =null;
    	try {
    		value = mapper.readValue(json , StatsJson.class);
    		//System.out.println(value.getResult().getDetails().getResponseType());
    		//System.out.println(value.getResult().getDetails());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Stats stats=new Stats(value.getResult());
    	int sampleGroupSize=value.getResult().getDetails().getOriginalBiologicalSampleGroup().size();
    	int sexSize=value.getResult().getDetails().getOriginalSex().size();
    	int responseSize=value.getResult().getDetails().getOriginalResponse().size();
    	int dateOfExperimentSize=value.getResult().getDetails().getOriginalDateOfExperiment().size();
    	assert(sampleGroupSize==sexSize && sampleGroupSize==responseSize && sampleGroupSize==dateOfExperimentSize);//all these lists should be the same size as refer to points.
    	
    	addDataFromFileHeader(summaryInfo, stats);
    	return stats;
    }

	private void addDataFromFileHeader(String summaryInfo, Stats stats) {
		String[] summaryFields = summaryInfo.split("\t");
    	stats.setParameterStableId(summaryFields[4]);
    	stats.setParameterStableName(summaryFields[5]);
    	stats.setPhenotypingCenter(summaryFields[6]);
    	stats.setAllele(summaryFields[7]);
    	stats.setGeneSymbol(summaryFields[8]);
    	stats.setGeneAccession(summaryFields[9]);
    	stats.setPipelineStableId(summaryFields[11]);
    	stats.setAlleleAccession(summaryFields[12]);
    	stats.setMetaDataGroup(summaryFields[13]);
    	stats.setZygosity(summaryFields[14]);
    	stats.setColonyId(summaryFields[15]);
	}
    
    /**
     * 
     * @param center
     * @param procedure
     * @param parameter
     * @param colonyId
     * @param zygosity
     * @param metadata
     * @return path but empty string if no success file at that path
     */
    public String getFilePathFromIndex(String center, String procedure, String parameter, String colonyId, String zygosity, String metadata) {
    	
    	String pathToFile=rootStatsDirectory+"/"+center+"/"+procedure+"/"+parameter+"/"+colonyId+"/"+zygosity+"/"+metadata+"/"+successFileName;
    	if(succesfulOnly.contains(pathToFile)) {
    		return pathToFile;
    	}
    	return "";
    }

    

    /**
     * only public for testing purposes!!
     * @return
     */
	public File readIndexFile() {
		// TODO Auto-generated method stub
		this.indexFile=new File(rootStatsDirectory+"/"+indexFilename);
		try (Stream<String> stream = Files.lines(Paths.get(rootStatsDirectory+"/"+indexFilename))) {

			//stream.forEach(System.out::println);
			//succesfulOnly=stream.filter(string -> string.endsWith(successFileName)).distinct().collect(Collectors.toList());
			succesfulOnly=stream
					.filter(string -> string.endsWith(successFileName))
					.map(string -> string.replace(originalDirectory, rootStatsDirectory))
					.distinct().collect(Collectors.toList());//.distinct().collect(Collectors.toList());
			
			//succesfulOnly.forEach(System.out::println);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return indexFile;
	}


	public List<String> getParameterOptionsForRequest(String phenotypingCenter, String parameterStableId,
			String metadataGroup) {
		
		Stream<String> filePaths=succesfulOnly.stream();
		List<String> pathsForRequest = filePaths
		.filter(string -> string.contains(phenotypingCenter))
		.filter(string -> string.contains(parameterStableId)).collect(Collectors.toList());
		return pathsForRequest;
		
	}

	

	public List<Stats> getAllStatsFromFiles() {
		
		List<Stats>statsList=new ArrayList<>();
		for(String path:succesfulOnly) {
			if(path.contains("IMPC_HEM_038_001")&& path.contains("MARC")) {
				
				Stats tempStats=readSuccesFile(path);
				//watch this as it's a hack to get the points as json objects in a list rather than seperate arrays and so we can store them this way in mongo
				tempStats.setPoints();
				statsList.add(tempStats);
			}
			
		}
		return statsList;
		
	}

    
	
}
