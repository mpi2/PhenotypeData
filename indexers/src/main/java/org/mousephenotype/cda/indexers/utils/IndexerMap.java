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

package org.mousephenotype.cda.indexers.utils;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.pojo.Synonym;
import org.mousephenotype.cda.indexers.beans.OrganisationBean;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.OntologyBean;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.SangerImageDTO;
import org.mousephenotype.cda.utilities.MpCsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * This class encapsulates the code and data necessary to represent all of the
 * maps used to build the various phenotype archive cores. The intention is that
 * the first caller to any given map will trigger the map to be loaded; subsequent
 * calls will simply return the cached map.
 *
 * @author mrelac
 */

public class IndexerMap {

    private static final Logger logger = LoggerFactory.getLogger(IndexerMap.class);

    protected static Map<String, Set<String>>               mpToHpTermsMap  = null;     // made protected so cacheing can be tested.
    private static   Map<String, List<SangerImageDTO>>      sangerImagesMap = null;
    private static   Map<String, List<AlleleDTO>>           allelesMap      = null;
    private static   List<AlleleDTO>                        alleles         = null;
    private static   Map<Long, ImpressBaseDTO>              pipelineMap     = null;
    private static   Map<Long, ImpressBaseDTO>              procedureMap    = null;
    private static   Map<Long, ParameterDTO>                parameterMap    = null;
    private static   Map<Long, OrganisationBean>            organisationMap = null;
    private static   Map<String, Map<String, List<String>>> maUberonEfoMap  = null;
    private static   DmddRestData                           dmddRestData;

    public static final String MP_HP_CSV_FILENAME = "impc_search_index.csv";
    

    // PUBLIC METHODS

	public Map<String, List<EmbryoStrain>> populateEmbryoData(String embryoViewerFilename) {
    	EmbryoRestGetter embryoGetter=new EmbryoRestGetter(embryoViewerFilename);
    	
		EmbryoRestData restData=null;
		try {
			restData = embryoGetter.getEmbryoRestData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<EmbryoStrain> strains = restData.getStrains();
		Map<String,List<EmbryoStrain>> mgiToEmbryoMap=new HashMap<>();
		for(EmbryoStrain strain: strains){
			String mgi=strain.getMgiGeneAccessionId();
			if(!mgiToEmbryoMap.containsKey(mgi)){
				mgiToEmbryoMap.put(mgi,new ArrayList<>());
			}
			mgiToEmbryoMap.get(mgi).add(strain);
		}
		return mgiToEmbryoMap;
	}
	
	public Map<String, List<DmddDataUnit>> populateDmddImagedData(String dmddFileName) {
    	DmddRestGetter dmddGetter=new DmddRestGetter(dmddFileName);
    	
		
		try {
			dmddRestData = dmddGetter.getEmbryoRestData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<DmddDataUnit> imaged = dmddRestData.getImaged();
		Map<String,List<DmddDataUnit>> mgiToDmddImagedMap=new HashMap<>();
		for(DmddDataUnit strain: imaged){
			String mgi=strain.getGeneAccession();
			if(!mgiToDmddImagedMap.containsKey(mgi)){
				mgiToDmddImagedMap.put(mgi,new ArrayList<>());
			}
			mgiToDmddImagedMap.get(mgi).add(strain);
		}
		return mgiToDmddImagedMap;
	}
	
	public Map<String, List<DmddDataUnit>> populateDmddLethalData(String dmddFileName) {
    	if (dmddRestData==null) {//should have this data already from imaged data call so don't do again if so.
			DmddRestGetter dmddGetter = new DmddRestGetter(dmddFileName);
			try {
				dmddRestData = dmddGetter.getEmbryoRestData();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		List<DmddDataUnit> lethal = dmddRestData.getEarlyLethal();
		Map<String,List<DmddDataUnit>> mgiToDmddLethalMap=new HashMap<>();
		for(DmddDataUnit strain: lethal){
			String mgi=strain.getGeneAccession();
			if(!mgiToDmddLethalMap.containsKey(mgi)){
				mgiToDmddLethalMap.put(mgi,new ArrayList<>());
			}
			mgiToDmddLethalMap.get(mgi).add(strain);
		}
		return mgiToDmddLethalMap;
	}
    
    
    
    /**
     * Fetch a map of AlleleDTOs terms indexed by mgi_accession_id
     *
     * @param alleleCore a valid solr connection
     * @return a map, indexed by MGI Accession id, of all alleles
     *
     * @throws IndexerException
     */
    public static Map<String, List<AlleleDTO>> getGeneToAlleles(SolrClient alleleCore) throws IndexerException {
        if (allelesMap == null) {
            try {
                allelesMap = SolrUtils.populateAllelesMap(alleleCore);
            } catch (SolrServerException | IOException e) {
                throw new IndexerException("Unable to query allele core in SolrUtils.populateAllelesMap()", e);
            }
        }

        return allelesMap;
    }

    /**
     * Returns a cached map of all mp terms to hp term names, indexed by mp id. The
     * data source is the monarch mp-hp csv mapping file that replaced an old
     * OntologyParser service that depended on the unreliable creation of the
     * mp-hp.owl ontology.
     *
     * @param mpHpCsvPath the fully-qualified path to the impc_search_index.csv ontology mapping file provided by Monarch
     * @return a cached map of each mp term and its corresponding list of hp terms as provided by Monarch
     *
     * @throws IndexerException
     */
    public static Map<String, Set<String>> getMpToHpTerms(String mpHpCsvPath) throws IndexerException {

        if (mpToHpTermsMap == null) {

            // As of 05-May-2020 there were just under 13,000 mp-hp terms in the latest impc_search_index.csv from Monarch.
            mpToHpTermsMap = new HashMap<>(20000);

            try {
                // The Monarch input file format has 2 columns we use: 'phenotype' and 'value'.
                // Get the column numbers from the heading (first) row.

                MpCsvReader reader = new MpCsvReader(mpHpCsvPath);
                List<String> row = reader.read();
                final String PHENOTYPE = "phenotype";
                final String VALUE = "value";
                int mpIdCol = row.indexOf(PHENOTYPE);
                int nameCol = row.indexOf(VALUE);
                if (mpIdCol < 0)  throw new IndexerException("Required heading " + PHENOTYPE + " is missing.");
                if (nameCol < 0)  throw new IndexerException("Required heading " + VALUE + " is missing.");

                while ((row = reader.read()) != null) {
                    Set<String> terms = mpToHpTermsMap.get(row.get(mpIdCol));
                    if (terms == null) {
                        terms = new HashSet<>();
                        mpToHpTermsMap.put(row.get(mpIdCol), terms);
                    }
                    terms.add(row.get(nameCol));
                }

            } catch (IOException e) {

                throw new IndexerException("Unable to parse mp-hp term file " + mpHpCsvPath, e);
            }

            logger.info(" Added {} unique mp-hp terms from impc_search_index.csv" + mpToHpTermsMap.size());
        }

        return mpToHpTermsMap;
    }

    /**
     * Fetch a map of AlleleDTOs terms indexed by mgi_accession_id
     *
     * @param alleleCore a valid solr connection
     * @return a map, indexed by MGI Accession id, of all alleles
     *
     * @throws IndexerException
     */
    public static List<AlleleDTO> getAlleles(SolrClient alleleCore) throws IndexerException {
        if (alleles== null) {
            try {
                alleles = SolrUtils.getAllAlleles(alleleCore);
            } catch (SolrServerException | IOException e) {
                throw new IndexerException("Unable to query allele core in SolrUtils.getAllAlleles()", e);
            }
        }

        return alleles;
    }

    /**
     * Returns a cached map of all sanger image terms associated to all ma ids,
     * indexed by ma term id.
     *
     * @param imagesCore a valid solr connection
     * @return a cached map of all sanger image terms associated to all ma ids,
     * indexed by ma term id.
     * @throws IndexerException
     */
    public static Map<String, List<SangerImageDTO>> getSangerImagesByMA(SolrClient imagesCore) throws IndexerException {
        if (sangerImagesMap == null) {
            try {
                sangerImagesMap = SolrUtils.populateSangerImagesMap(imagesCore);
            } catch (SolrServerException | IOException e) {
                throw new IndexerException("Unable to query images_core in SolrUtils.populateSangerImagesMap()", e);
            }
        }

        return sangerImagesMap;
    }

    /**
     * Returns a cached map of all IMPReSS pipeline terms, indexed by internal database id.
     *
     * @param connection active database connection
     *
     * @throws SQLException when a database exception occurs
     * @return a cached list of all impress pipeline terms, indexed by internal database id.
     */
    public static Map<Long, ImpressBaseDTO> getImpressPipelines(Connection connection) throws SQLException {
        if (pipelineMap == null) {
            pipelineMap = OntologyUtils.populateImpressPipeline(connection);
        }
        return pipelineMap;
    }

    /**
     * Returns a cached map of all IMPReSS procedure terms, indexed by internal database id.
     *
     * @param connection active database connection
     *
     * @throws SQLException when a database exception occurs
     * @return a cached list of all impress procedure terms, indexed by internal database id.
     */
    public static Map<Long, ImpressBaseDTO> getImpressProcedures(Connection connection) throws SQLException {
        if (procedureMap == null) {
            procedureMap = OntologyUtils.populateImpressProcedure(connection);
        }
        return procedureMap;
    }

    /**
     * Returns a cached map of all IMPReSS parameter terms, indexed by internal database id.
     *
     * @param connection active database connection
     *
     * @throws SQLException when a database exception occurs
     * @return a cached list of all impress parameter terms, indexed by internal database id.
     */
    public static Map<Long, ParameterDTO> getImpressParameters(Connection connection) throws SQLException {
        if (parameterMap == null) {
            parameterMap = OntologyUtils.populateImpressParameter(connection);
        }
        return parameterMap;
    }

    /**
     * Returns a cached map of all organisations, indexed by internal database id.
     *
     * @param connection active database connection
     *
     * @throws SQLException when a database exception occurs
     * @return a cached list of all impress parameter terms, indexed by internal database id.
     */
    public static Map<Long, OrganisationBean> getOrganisationMap(Connection connection) throws SQLException {
        if (organisationMap == null) {
            organisationMap = OntologyUtils.populateOrganisationMap(connection);
        }
        return organisationMap;
    }


    // UTILITY METHODS


    /**
     * Dumps out the list of <code>SangerImageDTO</code>, prepending the <code>
     * what</code> string for map identification.
     * @param map the map to dump
     * @param what a string identifying the map, prepended to the output.
     * @param maxIterations The maximum number of iterations to dump. Any value
     * not greater than 0 (including null) will dump the entire map.
     */
    public static void dumpSangerImagesMap(Map<String, List<SangerImageDTO>> map, String what, Integer maxIterations) {
        SolrUtils.dumpSangerImagesMap(map, what, maxIterations);
    }


	public static Map<String, Synonym> getSynonymsBySynonym(Connection connection) throws SQLException {
        Map<String, Synonym> map = new HashMap<>();

        String query = "SELECT * FROM synonym";

        try (PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet rs = p.executeQuery();
            while (rs.next()) {

                Synonym b = new Synonym();
                b.setAccessionId(rs.getString("acc"));
                b.setDbId(rs.getLong("db_id"));
                b.setSymbol(rs.getString("symbol"));

                map.put(b.getSymbol(), b);
            }
        }

        return map;
    }

	/**
	 * get a map of ontology_entity_id to entity
	 * @param connection
	 * @return
	 * @throws SQLException 
	 */
    public static Map<Long, List<OntologyBean>> getOntologyParameterSubTerms(Connection connection) throws SQLException {

        Map<Long, List<OntologyBean>> map = new HashMap<>();

        String query = "SELECT ontology_observation_id, acc, name, description, term_value FROM ontology_entity, ontology_term WHERE ontology_entity.term = ontology_term.acc";
        try (PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {

                Long ontObsId = resultSet.getLong("ontology_observation_id");

                OntologyBean b = new OntologyBean();
                b.setId(resultSet.getString("acc"));
                b.setName(resultSet.getString("name"));
                b.setDescription(resultSet.getString("description"));

                if ( ! map.containsKey(ontObsId)) {
                    map.put(ontObsId, new ArrayList<>());
                }

                map.get(ontObsId).add(b);
            }
        }
        return map;
    }
}
