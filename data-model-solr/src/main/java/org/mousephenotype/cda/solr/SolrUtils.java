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
package org.mousephenotype.cda.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.SangerImageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author mrelac
 */
public class SolrUtils {

    private static final Logger logger = LoggerFactory.getLogger(SolrUtils.class);

    public static final int BATCH_SIZE = 2000;
    // PRIVATE METHODS
    private static void printItemList(String label, List<String> itemList) {
        System.out.print("\t" + label);
        int itemCount = 0;
        if (itemList == null) {
            System.out.print("\t[null]");
        } else {
            for (String item : itemList) {
                if (itemCount == 0) {
                    System.out.print("\t[");
                } else {
                    System.out.print(", ");
                }
                itemCount ++;
                System.out.print(item);
                if (itemCount == itemList.size()) {
                    System.out.print("]");
                }
            }
        }

        System.out.println();
    }

    // UTILITY METHODS
    /**
     * Extract the <code>HttpSolrClient</code> from the <code>SolrClient</code>,
     * if there is one. Most SolrClient implementations contain an <code>
     * HttpSolrClient</code> instance. If the supplied solrClient does, that
     * instance is returned; otherwise, null is returned. The method is
     * synchronized to insure thread safety.
     *
     * @param solrClient the <code>SolrClient</code> instance
     * @return the embedded <code>HttpSolrClient</code>, if there is one; null
     * otherwise
     */
    public static synchronized HttpSolrClient getHttpSolrServer(SolrClient solrClient) {
        if (solrClient instanceof HttpSolrClient) {
            return (HttpSolrClient) solrClient;
        }

        HttpSolrClient httpSolrClient = null;
        try {
            Field[] fieldList = solrClient.getClass().getDeclaredFields();
            for (Field field : fieldList) {
                field.setAccessible(true);
                Object o = field.get(solrClient);
                if (o instanceof HttpSolrClient) {
                    httpSolrClient = (HttpSolrClient) o;
                    return httpSolrClient;
                }
            }
        } catch (Exception e) {
            logger.error("Exception while trying to extract HttpSolrClient from SolrClient: " + e.getLocalizedMessage());
        }

        return httpSolrClient;
    }

    /**
     * Extract the SOLR base URL from the <code>SolrClient</code> instance
     *
     * @param solrClient the <code>SolrClient</code> instance
     * @return the SOLR server base URL, if it can be found; or an empty string
     * if it cannot.
     */
    public static String getBaseURL(SolrClient solrClient) {
        HttpSolrClient httpSolrClient
                = (solrClient instanceof HttpSolrClient
                ? (HttpSolrClient) solrClient
                        : getHttpSolrServer(solrClient));
        if (httpSolrClient != null) {
            return httpSolrClient.getBaseURL();
        }

        return "";
    }

    // POPULATE METHODS
    /**
     * Fetch a map of image terms indexed by ma id
     *
     * @param imagesCore a valid solr connection
     * @return a map, indexed by child ma id, of all parent terms with
     * associations
     * @throws SolrServerException, IOException
     */
    public static Map<String, List<SangerImageDTO>> populateSangerImagesMap(SolrClient imagesCore) throws SolrServerException, IOException {
        Map<String, List<SangerImageDTO>> map = new HashMap();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery(SangerImageDTO.MA_ID + ":*");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = null;
            response = imagesCore.query(query);
            
            total = response.getResults().getNumFound();
            List<SangerImageDTO> imageList = response.getBeans(SangerImageDTO.class);

            for (SangerImageDTO image : imageList) {
                for (String termId : image.getMaId()) {
                    if ( ! map.containsKey(termId)) {
                        map.put(termId, new ArrayList<SangerImageDTO>());
                    }
                    String imageId = image.getId();
                    List<SangerImageDTO> sangerImageList = map.get(termId);

                    boolean imageFound = false;
                    for (SangerImageDTO dto : sangerImageList) {
                        if (dto.getId().equalsIgnoreCase(imageId)) {
                            imageFound = true;
                            break;
                        }
                    }
                    // Don't add duplicate images.
                    if ( ! imageFound) {
                        map.get(termId).add(image);
                    }
                }
            }
            pos += BATCH_SIZE;
        }

        return map;
    }

    /**
     * Fetch a map of image terms indexed by ma id
     *
     * @param imagesCore a valid solr connection
     * @return a map, indexed by child ma id, of all parent terms with
     * associations
     * @throws SolrServerException, IOException
     */
    public static Map<String, List<SangerImageDTO>> populateSangerImagesByMgiAccession(SolrClient imagesCore) throws SolrServerException, IOException {
        Map<String, List<SangerImageDTO>> map = new HashMap();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("mgi_accession_id:*");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = null;
            response = imagesCore.query(query);
            total = response.getResults().getNumFound();
            List<SangerImageDTO> imageList = response.getBeans(SangerImageDTO.class);
            for (SangerImageDTO image : imageList) {
            	
                if ( ! map.containsKey(image.getAccession())) {
                    map.put(image.getAccession(), new ArrayList<SangerImageDTO>());
                }
                String imageId = image.getId();
                List<SangerImageDTO> sangerImageList = map.get(image.getAccession());

                boolean imageFound = false;
                for (SangerImageDTO dto : sangerImageList) {
                    if (dto.getId().equalsIgnoreCase(imageId)) {
                        imageFound = true;
                        break;
                    }
                }
                // Don't add duplicate images.
                if ( ! imageFound) {
                    map.get(image.getAccession()).add(image);
                }

            }
            pos += BATCH_SIZE;
        }

        return map;
    }

    /**
     * Fetch all alleles
     *
     * @param alleleCore a valid solr connection
     * @return a list of all alleles
     *
     * @throws SolrServerException, IOException
     */
    public static List<AlleleDTO> getAllAlleles(SolrClient alleleCore) throws SolrServerException, IOException {
        List<AlleleDTO> alleleList = new ArrayList<>();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("*:*");

        query.setRows(Integer.MAX_VALUE);
        QueryResponse response = null;
        response = alleleCore.query(query);
        total = response.getResults().getNumFound();
        logger.info("  total alleles=" + total);
        alleleList = response.getBeans(AlleleDTO.class);

        logger.debug("  Loaded {} alleles", alleleList.size());

        return alleleList;
    }

    /**
     * Fetch a map of mgi accessions to alleles
     *
     * @param alleleCore a valid solr connection
     * @return a map, indexed by MGI Accession id, of all alleles
     *
     * @throws SolrServerException, IOException
     */
    public static Map<String, List<AlleleDTO>> populateAllelesMap(SolrClient alleleCore) throws SolrServerException, IOException {

        Map<String, List<AlleleDTO>> alleles = new HashMap<>();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("*:*");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = null;
            response = alleleCore.query(query);
            total = response.getResults().getNumFound();
            List<AlleleDTO> alleleList = response.getBeans(AlleleDTO.class);
            for (AlleleDTO allele : alleleList) {
                String key = allele.getMgiAccessionId();
                if ( ! alleles.containsKey(key)) {
                    alleles.put(key, new ArrayList<>());
                }
                alleles.get(key).add(allele);
            }
            pos += BATCH_SIZE;
        }
        logger.debug("  Loaded {} alleles", alleles.size());

        return alleles;
    }


    /**
     * Fetch a map of mp terms associated to hp terms, indexed by mp id.
     *
     * @param phenodigm_core a valid solr connection
     * @return a map, indexed by mp id, of all hp terms
     *
     * @throws SolrServerException, IOException
     */
    public static Map<String, List<Map<String, String>>> populateMpToHpTermsMap(SolrClient phenodigm_core)
            throws SolrServerException, IOException {

		// url="q=mp_id:&quot;${nodeIds.term_id}&quot;&amp;rows=999&amp;fq=type:mp_hp&amp;fl=hp_id,hp_term"
        // processor="XPathEntityProcessor" >
        //
        // <field column="hp_id" xpath="/response/result/doc/str[@name='hp_id']"
        // />
        // <field column="hp_term"
        // xpath="/response/result/doc/str[@name='hp_term']" />
        Map<String, List<Map<String, String>>> mpToHp = new HashMap<>();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("mp_id:*");
        query.addFilterQuery("type:mp_hp");// &amp;fl=hp_id,hp_term);
        query.add("fl=hp_id,hp_term");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = null;
            response = phenodigm_core.query(query);
            total = response.getResults().getNumFound();
            SolrDocumentList solrDocs = response.getResults();
            for (SolrDocument doc : solrDocs) {
                if (doc.containsKey("hp_id")) {
                    String hp = (String) doc.get("hp_id");
                    if (doc.containsKey("mp_id")) {

                        String mp = (String) doc.get("mp_id");
                        List<Map<String, String>> mapList = new ArrayList<>();
                        Map<String, String> entryMap = new HashMap<>();
                        if (mpToHp.containsKey(mp)) {
                            mapList = mpToHp.get(mp);
                        }
                        entryMap.put("hp_id", hp);
                        if (doc.containsKey("hp_term")) {
                            String hpTerm = (String) doc.get("hp_term");
                            entryMap.put("hp_term", hpTerm);
                        }
                        mapList.add(entryMap);
                        mpToHp.put(mp, mapList);
                    }
                }

            }
            pos += BATCH_SIZE;
        }

        return mpToHp;
    }

    /**
     * Get a map of MpDTOs by key mgiAccesion
     *
     * @param mpSolrServer
     * @return the map
     * @throws SolrServerException, IOException
     */
    public static Map<String, List<MpDTO>> populateMgiAccessionToMp(SolrClient mpSolrServer) throws SolrServerException, IOException {

        Map<String, List<MpDTO>> mps = new HashMap<>();
        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("mgi_accession_id:*");
        //query.add("fl=mp_id,mp_term,mp_definition,mp_term_synonym,ontology_subset,hp_id,hp_term,top_level_mp_id,top_level_mp_term,top_level_mp_term_synonym,intermediate_mp_id,intermediate_mp_term,intermediate_mp_term_synonym,child_mp_id,child_mp_term,child_mp_term_synonym,inferred_ma_id,inferred_ma_term,inferred_ma_term_synonym,inferred_selected_top_level_ma_id,inferred_selected_top_level_ma_term,inferred_selected_top_level_ma_term_synonym,inferred_child_ma_id,inferred_child_ma_term,inferred_child_ma_term_synonym");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = null;
            response = mpSolrServer.query(query);
            total = response.getResults().getNumFound();
            List<MpDTO> mpBeans = response.getBeans(MpDTO.class);

            for (MpDTO mp : mpBeans) {
                if (mp.getMgiAccessionId() != null &&  ! mp.getMgiAccessionId().equals("")) {
                    for (String geneAccession : mp.getMgiAccessionId()) {

                        if (mps.containsKey(geneAccession)) {
                            mps.get(geneAccession).add(mp);
                        } else {
                            List<MpDTO> mpListPerGene = new ArrayList<>();
                            mpListPerGene.add(mp);
                            mps.put(geneAccession, mpListPerGene);
                        }
                    }
                }
            }
            pos += BATCH_SIZE;
        }
        return mps;
    }

    /**
     * Dumps out the list of <code>SangerImageDTO</code>, prepending the <code>
     * what</code> string for map identification.
     *
     * @param map the map to dump
     * @param what a string identifying the map, prepended to the output.
     * @param maxIterations The maximum number of iterations to dump. Any value
     * not greater than 0 (including null) will dump the entire map.
     */
    public static void dumpSangerImagesMap(Map<String, List<SangerImageDTO>> map, String what, Integer maxIterations) {

        if ((maxIterations == null) || (maxIterations < 1)) {
            maxIterations = map.size();
        }

        logger.info(what);

        Iterator<Entry<String, List<SangerImageDTO>>> it = map.entrySet().iterator();
        while ((it.hasNext()) && (maxIterations-- > 0)) {
            Entry<String, List<SangerImageDTO>> entry = it.next();
            logger.info("  KEY: " + entry.getKey());
            List<SangerImageDTO> dtoList = entry.getValue();
            for (SangerImageDTO dto : dtoList) {
                printItemList("procedure_name:", dto.getProcedureName());
                printItemList("expName:", dto.getExpName());
                printItemList("expName_exp:", dto.getExpNameExp());
                printItemList("symbol_gene:", dto.getSymbolGene());

                printItemList("mgi_accession_id:", dto.getMgiAccessionId());
                printItemList("marker_symbol:", dto.getMarkerSymbol());
                printItemList("marker_name:", dto.getMarkerName());
                printItemList("marker_synonym:", dto.getMarkerSynonym());
                printItemList("marker_type:", dto.getMarkerType());
                printItemList("human_gene_symbol:", dto.getHumanGeneSymbol());

                printItemList("status:", dto.getStatus());
                printItemList("imits_phenotype_started:", dto.getImitsPhenotypeStarted());
                printItemList("imits_phenotype_complete:", dto.getImitsPhenotypeComplete());
                printItemList("imits_phenotype_status:", dto.getImitsPhenotypeStatus());

                printItemList("phenotype_status:", dto.getLatestPhenotypeStatus());

                logger.info("\tlegacy_phenotype_status:\t" + dto.getLegacyPhenotypeStatus());
                printItemList("latest_production_centre:", dto.getLatestProductionCentre());
                printItemList("latest_phenotyping_centre:", dto.getLatestPhenotypingCentre());

                printItemList("allele_name:", dto.getAlleleName());
            }

            System.out.println();
        }
    }

    /**
     * Given a solr document and a field name, returns null if the value is null;
     * otherwise, returns the field value as a String.
     * @param doc Solr document
     * @param fieldName field name
     * @return field value as String, or null if value is null
     */
   public static String getFieldValue(SolrDocument doc, String fieldName) {
        Object o = doc.getFieldValue(fieldName);
        return (o == null ? null : o.toString());
    }
}