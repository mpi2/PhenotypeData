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

package org.mousephenotype.cda.indexers;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.db.dao.GwasDAO;
import org.mousephenotype.cda.db.dao.GwasDTO;
import org.mousephenotype.cda.indexers.beans.AutosuggestBean;
import org.mousephenotype.cda.indexers.beans.SangerGeneBean;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.service.dto.*;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@EnableAutoConfiguration
public class AutosuggestIndexer extends AbstractIndexer implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(AutosuggestIndexer.class);


    @Autowired
    @Qualifier("autosuggestIndexing")
    private SolrClient autosuggestCore;

    @NotNull
    @Value("${imits.solr.host}")
    private String imitsSolrHost;

    @Autowired
    @Qualifier("geneCore")
    private SolrClient geneCore;

    @Autowired
    @Qualifier("mpCore")
    private SolrClient mpCore;

    @Autowired
    @Qualifier("diseaseCore")
    private SolrClient diseaseCore;

    @Autowired
    @Qualifier("anatomyCore")
    private SolrClient anatomyCore;

	@Autowired
	@Qualifier("phenodigmCore")
	private SolrClient phenodigmCore;

	@Autowired
   	private GwasDAO gwasDao;

    private SolrClient sangerAlleleCore;

    public static final long MIN_EXPECTED_ROWS = 218000;
    public static final int PHENODIGM_CORE_MAX_RESULTS = 350000;

    // Sets used to insure uniqueness when loading core components.

    // gene
    Set<String> mgiAccessionIdSet = new HashSet();
    Set<String> mgiAlleleAccessionIdSet = new HashSet();
    Set<String> markerSymbolSet = new HashSet();
    Set<String> markerNameSet = new HashSet();
    Set<String> markerSynonymSet = new HashSet();
    Set<String> humanGeneSymbolSet = new HashSet();

    // mp
    Set<String> mpIdSet = new HashSet();
    Set<String> mpTermSet = new HashSet();
    Set<String> mpTermSynonymSet = new HashSet();
    Set<String> mpAltIdSet = new HashSet();

    // disease
    Set<String> diseaseIdSet = new HashSet();
    Set<String> diseaseTermSet = new HashSet();
    Set<String> diseaseAltsSet = new HashSet();

    // anatomy
    Set<String> anatomyIdSet = new HashSet();
    Set<String> anatomyTermSet = new HashSet();
    Set<String> anatomyTermSynonymSet = new HashSet();
    Set<String> anatomyAltIdSet = new HashSet();

    // hp
    Set<String> hpIdSet = new HashSet();
    Set<String> hpTermSet = new HashSet();
    Set<String> hpSynonymSet = new HashSet();

    // impcGwas
    // gene
    Set<String> gwasMgiGeneIdSet = new HashSet();
    Set<String> gwasMgiGeneSymbolSet = new HashSet();

    // mp
    Set<String> gwasMpIdSet = new HashSet();
    Set<String> gwasMpTermSet = new HashSet();

    // gwas
    Set<String> gwasTraitSet = new HashSet();
    Set<String> gwasSnipIdSet = new HashSet();
    Set<String> gwasReportedGeneSymbolSet = new HashSet();
    Set<String> gwasMappedGeneSymbolSet = new HashSet();
    Set<String> gwasUpstreamGeneSymbolSet = new HashSet();
    Set<String> gwasDownstreamGeneSymbolSet = new HashSet();

    // allele2 (for product tab on search page)
    Set<String> allele2MgiAccsSet = new HashSet();
    Set<String> allele2AlleleNameSet = new HashSet();
    Set<String> allele2MarkerSymbolSet = new HashSet();
    Set<String> allele2MarkerSynonymSet = new HashSet();
    Set<String> allele2IkmcProjectSet = new HashSet();
    Set<String> allele2GeneAlleleSet = new HashSet();
    Map<String, List<String>> markerSymbolSynonymsMap = new HashMap<>();


    String mapKey;

    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(autosuggestCore);
    }

    private void initializeSolrCores() {

        final String SANGER_ALLELE_URL = imitsSolrHost +"/allele2";

        // Use system proxy if set for external solr servers
        if (System.getProperty("externalProxyHost") != null && System.getProperty("externalProxyPort") != null) {

            String PROXY_HOST = System.getProperty("externalProxyHost");
            Integer PROXY_PORT = Integer.parseInt(System.getProperty("externalProxyPort"));

            HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build();

            logger.info(" Using Proxy Settings: " + PROXY_HOST + " on port: " + PROXY_PORT);

            this.sangerAlleleCore = new HttpSolrClient(SANGER_ALLELE_URL, client);
        } else {
            this.sangerAlleleCore = new HttpSolrClient(SANGER_ALLELE_URL);
        }
    }


    @Override
    public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();

        try {
            initializeSolrCores();

            autosuggestCore.deleteByQuery("*:*");

            populateGeneAutosuggestTerms();
            populateMpAutosuggestTerms();
            populateDiseaseAutosuggestTerms();
            populateAnatomyAutosuggestTerms();
            populateProductAutosuggestTerms(); // must run after populateGeneAutosuggestTerms to use the map markerSymbolSynonymsMap
            populateHpAutosuggestTerms();
            populateGwasAutosuggestTerms();

            // Final commit
            autosuggestCore.commit();

        } catch (SQLException | SolrServerException | IOException e) {
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", documentCount, commonUtils.msToHms(System.currentTimeMillis() - start));
        return runStatus;
    }

    private void populateGeneAutosuggestTerms() throws SolrServerException, IOException {

        List<String> geneFields = Arrays.asList(GeneDTO.MGI_ACCESSION_ID, GeneDTO.MARKER_SYMBOL, GeneDTO.MARKER_NAME, GeneDTO.MARKER_SYNONYM, GeneDTO.HUMAN_GENE_SYMBOL, GeneDTO.ALLELE_ACCESSION_ID);

        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(geneFields, ","))
            .setRows(Integer.MAX_VALUE);

        List<GeneDTO> genes = geneCore.query(query).getBeans(GeneDTO.class);
        for (GeneDTO gene : genes) {

            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : geneFields) {

                AutosuggestBean a = new AutosuggestBean();
                a.setDocType("gene");

                switch (field) {
                    case GeneDTO.MGI_ACCESSION_ID:
                        mapKey = gene.getMgiAccessionId();
                        if (mgiAccessionIdSet.add(mapKey)) {
                            a.setMgiAccessionId(gene.getMgiAccessionId());
                            beans.add(a);
                        }
                        break;
                    case GeneDTO.MARKER_SYMBOL:
                        mapKey = gene.getMarkerSymbol();
                        if (markerSymbolSet.add(mapKey)) {
                            markerSymbolSynonymsMap.put(mapKey, new ArrayList<String>());
                            a.setMarkerSymbol(gene.getMarkerSymbol());
                            beans.add(a);
                        }
                        break;
                    case GeneDTO.MARKER_NAME:
                        mapKey = a.getMarkerName();
                        if (markerNameSet.add(mapKey)) {
                            a.setMarkerName(gene.getMarkerName());
                            beans.add(a);
                        }
                        break;
                    case GeneDTO.MARKER_SYNONYM:
                        if (gene.getMarkerSynonym() != null) {
                            for (String s : gene.getMarkerSynonym()) {
                                markerSymbolSynonymsMap.get(gene.getMarkerSymbol()).add(s);
                                if (markerSynonymSet.add(s)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMarkerSynonym(s);
                                    asyn.setDocType("gene");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case GeneDTO.HUMAN_GENE_SYMBOL:
                        if (gene.getHumanGeneSymbol() != null) {
                            for (String s : gene.getHumanGeneSymbol()) {
                                mapKey = s;
                                if (humanGeneSymbolSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setHumanGeneSymbol(s);
                                    asyn.setDocType("gene");
                                    beans.add(asyn);
                                }
                            }
                        }
                    case GeneDTO.ALLELE_ACCESSION_ID:
                        if (gene.getAlleleAccessionIds() != null) {
                            for (String s : gene.getAlleleAccessionIds()) {
                                mapKey = s;
                                if (mgiAlleleAccessionIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setHumanGeneSymbol(s);
                                    asyn.setDocType("gene");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                documentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }

        }
//        Iterator it = markerSymbolSynonymsMap.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            System.out.println("CHK: "+pair.getKey() + " = " + pair.getValue());
//           // it.remove(); // avoids a ConcurrentModificationException
//        }
//        System.out.println("check map size: "+ markerSymbolSynonymsMap.size());
//        System.out.println("chk syn: " + markerSymbolSynonymsMap.get("P2rx4"));

    }

    private void populateMpAutosuggestTerms() throws SolrServerException, IOException {

        List<String> mpFields = Arrays.asList(
                MpDTO.MP_ID, MpDTO.MP_TERM, MpDTO.MP_TERM_SYNONYM, MpDTO.ALT_MP_ID, MpDTO.TOP_LEVEL_MP_ID, MpDTO.TOP_LEVEL_MP_TERM,
                MpDTO.TOP_LEVEL_MP_TERM_SYNONYM, MpDTO.INTERMEDIATE_MP_ID, MpDTO.INTERMEDIATE_MP_TERM, MpDTO.PARENT_MP_ID, MpDTO.PARENT_MP_TERM, MpDTO.PARENT_MP_TERM_SYNONYM,
                MpDTO.INTERMEDIATE_MP_TERM_SYNONYM, MpDTO.CHILD_MP_ID, MpDTO.CHILD_MP_TERM, MpDTO.CHILD_MP_TERM_SYNONYM);

        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(mpFields, ","))
            .setRows(Integer.MAX_VALUE);

        List<MpDTO> mps = mpCore.query(query).getBeans(MpDTO.class);
        for (MpDTO mp : mps) {

            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : mpFields) {

                AutosuggestBean a = new AutosuggestBean();
                a.setDocType("mp");

                switch (field) {
                    case MpDTO.MP_ID:
                        mapKey = mp.getMpId();
                        if (mpIdSet.add(mapKey)) {
                            a.setMpId(mp.getMpId());
                            beans.add(a);
                        }
                        break;
                    case MpDTO.MP_TERM:
                        mapKey = mp.getMpTerm();
                        if (mpTermSet.add(mapKey)) {
                            a.setMpTerm(mp.getMpTerm());
                            beans.add(a);
                        }
                        break;
                    case MpDTO.MP_TERM_SYNONYM:
                        if (mp.getMpTermSynonym() != null) {
                            for (String s : mp.getMpTermSynonym()) {
                                mapKey = s;
                                if (mpTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpTermSynonym(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.ALT_MP_ID:
                        logger.debug("Working on ALT MP ID -1");
                        if ( mp.getAltMpIds() != null) {

                            for (String s : mp.getAltMpIds()) {
                                mapKey = s;
                                logger.debug("GOT ALT MP ID: " + mapKey);
                                if (mpAltIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpId(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.TOP_LEVEL_MP_ID:
                        if (mp.getTopLevelMpId() != null) {
                            for (String s : mp.getTopLevelMpId()) {
                                mapKey = s;
                                if (mpIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpId(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.TOP_LEVEL_MP_TERM:
                        if (mp.getTopLevelMpTerm() != null) {
                            for (String s : mp.getTopLevelMpTerm()) {
                                mapKey = s;
                                if (mpTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpTerm(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.TOP_LEVEL_MP_TERM_SYNONYM:
                        if (mp.getTopLevelMpTermSynonym() != null) {
                            for (String s : mp.getTopLevelMpTermSynonym()) {
                                mapKey = s;
                                if (mpTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpTermSynonym(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.INTERMEDIATE_MP_ID:
                        if (mp.getIntermediateMpId() != null) {
                            for (String s : mp.getIntermediateMpId()) {
                                mapKey = s;
                                if (mpIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpId(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.INTERMEDIATE_MP_TERM:
                        if (mp.getIntermediateMpTerm() != null) {
                            for (String s : mp.getIntermediateMpTerm()) {
                                mapKey = s;
                                if (mpTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpTerm(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.INTERMEDIATE_MP_TERM_SYNONYM:
                        if (mp.getIntermediateMpTermSynonym() != null) {
                            for (String s : mp.getIntermediateMpTermSynonym()) {
                                mapKey = s;
                                if (mpTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpTermSynonym(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.PARENT_MP_ID:
                        mapKey = mp.getMpId();
                        if (mpIdSet.add(mapKey)) {
                            a.setMpId(mp.getMpId());
                            beans.add(a);
                        }
                        break;
                    case MpDTO.PARENT_MP_TERM:
                        mapKey = mp.getMpTerm();
                        if (mpTermSet.add(mapKey)) {
                            a.setMpTerm(mp.getMpTerm());
                            beans.add(a);
                        }
                        break;
                    case MpDTO.PARENT_MP_TERM_SYNONYM:
                        if (mp.getMpTermSynonym() != null) {
                            for (String s : mp.getMpTermSynonym()) {
                                mapKey = s;
                                if (mpTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpTermSynonym(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.CHILD_MP_ID:
                        if (mp.getChildMpId() != null) {
                            for (String s : mp.getChildMpId()) {
                                mapKey = s;
                                if (mpIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpId(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.CHILD_MP_TERM:
                        if (mp.getChildMpTerm() != null) {
                            for (String s : mp.getChildMpTerm()) {
                                mapKey = s;
                                if (mpTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpTerm(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.CHILD_MP_TERM_SYNONYM:
                        if (mp.getChildMpTermSynonym() != null) {
                            for (String s : mp.getChildMpTermSynonym()) {
                                mapKey = s;
                                if (mpTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpTermSynonym(s);
                                    asyn.setDocType("mp");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                documentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }

        }
    }

    private void populateDiseaseAutosuggestTerms() throws SolrServerException, IOException {

        List<String> diseaseFields = Arrays.asList(DiseaseDTO.DISEASE_ID, DiseaseDTO.DISEASE_TERM, DiseaseDTO.DISEASE_ALTS);

        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(diseaseFields, ","))
            .setRows(Integer.MAX_VALUE);

        List<DiseaseDTO> diseases = diseaseCore.query(query).getBeans(DiseaseDTO.class);
        for (DiseaseDTO disease : diseases) {

            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : diseaseFields) {

                AutosuggestBean a = new AutosuggestBean();
                a.setDocType("disease");

                switch (field) {
                    case DiseaseDTO.DISEASE_ID:
                        mapKey = disease.getDiseaseId();
                        if (diseaseIdSet.add(mapKey)) {
                            a.setDiseaseId(disease.getDiseaseId());
                            beans.add(a);
                        }
                        break;
                    case DiseaseDTO.DISEASE_TERM:
                        mapKey = disease.getDiseaseTerm();
                        if (diseaseTermSet.add(mapKey)) {
                            a.setDiseaseTerm(disease.getDiseaseTerm());
                            beans.add(a);
                        }
                        break;
                    case DiseaseDTO.DISEASE_ALTS:
                        if (disease.getDiseaseAlts() != null) {
                            for (String s : disease.getDiseaseAlts()) {
                                mapKey = s;
                                if (diseaseAltsSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setDiseaseAlts(s);
                                    asyn.setDocType("disease");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                documentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }

        }
    }

    private void populateAnatomyAutosuggestTerms() throws SolrServerException, IOException {

        // since we use anatomyIdSet, anatomyTermSet and anatomyTermSynonymSet to get only unique data,
        // there is no need to distinguish hierarchy of terms (child, parent, top_level) as a term which gets added to eg, anatomyIdSet
        // can be parent, child, top_level... etc.
        // so the setter needed are just setAnatomyId, setAnatomyTerm, setAnatomyTerm_synonym

        List<String> anatomyFields = Arrays.asList(
                AnatomyDTO.ANATOMY_ID, AnatomyDTO.ANATOMY_TERM, AnatomyDTO.ANATOMY_TERM_SYNONYM, AnatomyDTO.ALT_ANATOMY_ID, AnatomyDTO.CHILD_ANATOMY_ID, AnatomyDTO.CHILD_ANATOMY_TERM,
                AnatomyDTO.CHILD_ANATOMY_TERM_SYNONYM, AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_ID,
                AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM, AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM);

        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(anatomyFields, ","))
            .setRows(Integer.MAX_VALUE);


        List<AnatomyDTO> anatomies = anatomyCore.query(query).getBeans(AnatomyDTO.class);

        for (AnatomyDTO anatomy : anatomies) {

            Set<AutosuggestBean> beans = new HashSet<>();

            for (String field : anatomyFields) {

                AutosuggestBean a = new AutosuggestBean();
                a.setDocType("anatomy");

                switch (field) {
                    case AnatomyDTO.ANATOMY_ID:
                        mapKey = anatomy.getAnatomyId();
                        if (anatomyIdSet.add(mapKey)) {
                            a.setAnatomyId(anatomy.getAnatomyId());
                            beans.add(a);
                        }
                        break;
                    case AnatomyDTO.ANATOMY_TERM:
                        mapKey = anatomy.getAnatomyTerm();
                        if (anatomyTermSet.add(mapKey)) {
                            a.setAnatomyTerm(anatomy.getAnatomyTerm());
                            beans.add(a);
                        }
                        break;
                    case AnatomyDTO.ANATOMY_TERM_SYNONYM:
                        if (anatomy.getAnatomyTermSynonym() != null) {
                            for (String s : anatomy.getAnatomyTermSynonym()) {
                                mapKey = s;
                                if (anatomyTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyTermSynonym(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.ALT_ANATOMY_ID:
                        if ( anatomy.getAltAnatomyIds() != null ) {
                            for (String s : anatomy.getAltAnatomyIds()) {
                                mapKey = s;

                                if (anatomyIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyId(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.CHILD_ANATOMY_ID:
                        if (anatomy.getChildAnatomyId() != null) {
                            for (String s : anatomy.getChildAnatomyId()) {
                                mapKey = s;
                                if (anatomyIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyId(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.CHILD_ANATOMY_TERM:
                        if (anatomy.getChildAnatomyTerm() != null) {
                            for (String s : anatomy.getChildAnatomyTerm()) {
                                mapKey = s;
                                if (anatomyTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyTerm(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.CHILD_ANATOMY_TERM_SYNONYM:
                        if (anatomy.getChildAnatomyTermSynonym() != null) {
                            for (String s : anatomy.getChildAnatomyTermSynonym()) {
                                mapKey = s;
                                if (anatomyTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyTermSynonym(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.PARENT_ANATOMY_ID:
                        if (anatomy.getChildAnatomyId() != null) {
                            for (String s : anatomy.getChildAnatomyId()) {
                                mapKey = s;
                                if (anatomyIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyId(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.PARENT_ANATOMY_TERM:
                        if (anatomy.getChildAnatomyTerm() != null) {
                            for (String s : anatomy.getChildAnatomyTerm()) {
                                mapKey = s;
                                if (anatomyTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyTerm(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.PARENT_ANATOMY_TERM_SYNONYM:
                        if (anatomy.getChildAnatomyTermSynonym() != null) {
                            for (String s : anatomy.getChildAnatomyTermSynonym()) {
                                mapKey = s;
                                if (anatomyTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyTermSynonym(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.INTERMEDIATE_ANATOMY_ID:
                        if (anatomy.getChildAnatomyId() != null) {
                            for (String s : anatomy.getChildAnatomyId()) {
                                mapKey = s;
                                if (anatomyIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyId(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.INTERMEDIATE_ANATOMY_TERM:
                        if (anatomy.getChildAnatomyTerm() != null) {
                            for (String s : anatomy.getChildAnatomyTerm()) {
                                mapKey = s;
                                if (anatomyTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyTerm(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.INTERMEDIATE_ANATOMY_TERM_SYNONYM:
                        if (anatomy.getChildAnatomyTermSynonym() != null) {
                            for (String s : anatomy.getChildAnatomyTermSynonym()) {
                                mapKey = s;
                                if (anatomyTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyTermSynonym(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.TOP_LEVEL_ANATOMY_ID:
                        if (anatomy.getSelectedTopLevelAnatomyId() != null) {
                            for (String s : anatomy.getSelectedTopLevelAnatomyId()) {
                                mapKey = s;
                                if (anatomyIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyId(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.TOP_LEVEL_ANATOMY_TERM:
                        if (anatomy.getSelectedTopLevelAnatomyTerm() != null) {
                            for (String s : anatomy.getSelectedTopLevelAnatomyTerm()) {
                                mapKey = s;
                                if (anatomyTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyTerm(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.TOP_LEVEL_ANATOMY_TERM_SYNONYM:
                        if (anatomy.getSelectedTopLevelAnatomyTermSynonym() != null) {
                            for (String s : anatomy.getSelectedTopLevelAnatomyTermSynonym()) {
                                mapKey = s;
                                if (anatomyTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyTermSynonym(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_ID:
                        if (anatomy.getSelectedTopLevelAnatomyId() != null) {
                            for (String s : anatomy.getSelectedTopLevelAnatomyId()) {
                                mapKey = s;
                                if (anatomyIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyId(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM:
                        if (anatomy.getSelectedTopLevelAnatomyTerm() != null) {
                            for (String s : anatomy.getSelectedTopLevelAnatomyTerm()) {
                                mapKey = s;
                                if (anatomyTermSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyTerm(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM:
                        if (anatomy.getSelectedTopLevelAnatomyTermSynonym() != null) {
                            for (String s : anatomy.getSelectedTopLevelAnatomyTermSynonym()) {
                                mapKey = s;
                                if (anatomyTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setAnatomyTermSynonym(s);
                                    asyn.setDocType("anatomy");
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                documentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }
        }
    }

    private void populateProductAutosuggestTerms() throws SolrServerException, IOException {

        List<String> productFields = Arrays.asList(AlleleDTO.MARKER_SYMBOL, AlleleDTO.ALLELE_NAME, AlleleDTO.IKMC_PROJECT, AlleleDTO.MGI_ACCESSION_ID, AlleleDTO.ALLELE_MGI_ACCESSION_ID);

        SolrQuery query = new SolrQuery()
                .setQuery("type:Allele")
                .setFields(StringUtils.join(productFields, ","))
                .setRows(Integer.MAX_VALUE);

        QueryResponse response = sangerAlleleCore.query(query);
        List<AlleleDTO> alleles = response.getBeans(AlleleDTO.class);

        String docType = "allele2";

        for (AlleleDTO allele : alleles) {

            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : productFields) {

                AutosuggestBean a = new AutosuggestBean();
                a.setDocType(docType);

                switch (field) {
                    case AlleleDTO.MARKER_SYMBOL:
                        mapKey = allele.getMarkerSymbol();
                        if (allele2MarkerSymbolSet.add(mapKey)) {
                            a.setMarkerSymbol(mapKey);
                            beans.add(a);

                            if ( markerSymbolSynonymsMap.get(mapKey) != null ) {
                                for (String syn : markerSymbolSynonymsMap.get(mapKey)) {
                                    AutosuggestBean syna = new AutosuggestBean();
                                    syna.setDocType(docType);
                                    syna.setMarkerSynonym(syn);
                                    beans.add(syna);
                                }
                            }
                        }
                        break;
                    case AlleleDTO.MGI_ACCESSION_ID:
                        mapKey = allele.getMgiAccessionId();
                        if (allele2MgiAccsSet.add(mapKey)) {
                            a.setMgiAccessionId(mapKey);
                            beans.add(a);
                        }
                        break;
                    case AlleleDTO.ALLELE_MGI_ACCESSION_ID:
                        mapKey = allele.getAlleleMgiAccessionId();
                        if (allele2MgiAccsSet.add(mapKey)) {
                            a.setAlleleMgiAccessionId(mapKey);
                            beans.add(a);
                        }
                        break;
                    case AlleleDTO.IKMC_PROJECT:
                        List<String> ikmcProjects = allele.getIkmcProject();
                        if ( ikmcProjects != null ) {
                            for (String ikmcProject : ikmcProjects) {
                                if (allele2IkmcProjectSet.add(mapKey)) {
                                    a.setIkmcProject(ikmcProject);
                                    beans.add(a);
                                }
                            }
                        }
                        break;
                    case AlleleDTO.ALLELE_NAME:
                        List<String> names = allele.getAlleleName();
                        for( String mapKey : names) {

                            String markerSymbol = allele.getMarkerSymbol();
                            if (allele2GeneAlleleSet.add(markerSymbol + " " + mapKey)) {
                                AutosuggestBean ga = new AutosuggestBean();
                                ga.setDocType(docType);
                                ga.setGeneAllele(markerSymbol + " " + mapKey);
                                beans.add(ga);
                            }

                            if (allele2AlleleNameSet.add(mapKey)) {
                                a.setAlleleName(mapKey);
                                beans.add(a);
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                documentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }

        }
    }

    private void populateGwasAutosuggestTerms() throws SolrServerException, IOException, SQLException {

        List<String> gwasFields = Arrays.asList(
        		GwasDTO.MGI_GENE_ID,
        		GwasDTO.MGI_GENE_SYMBOL,
        		GwasDTO.MP_TERM_ID,
        		GwasDTO.MP_TERM_NAME,
        		GwasDTO.DISEASE_TRAIT,
        		GwasDTO.SNP_ID,
        		GwasDTO.REPORTED_GENE,
        		GwasDTO.MAPPED_GENE,
        		GwasDTO.UPSTREAM_GENE,
        		GwasDTO.DOWNSTREAM_GENE
                );

        List<GwasDTO> gwasMappings = gwasDao.getGwasMappingRows();

        for (GwasDTO gw : gwasMappings) {

        	Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : gwasFields) {

                AutosuggestBean a = new AutosuggestBean();
                a.setDocType("gwas");

                switch (field) {
                	case GwasDTO.MGI_GENE_ID:
                		mapKey = gw.getMgiGeneId();
                		if (gwasMgiGeneIdSet.add(mapKey)) {
	                        a.setGwasMgiGeneId(mapKey);
	                        beans.add(a);
	                    }
	                    break;
                    case GwasDTO.MGI_GENE_SYMBOL:
                        mapKey = gw.getMgiGeneSymbol();
                        if (gwasMgiGeneSymbolSet.add(mapKey)) {
                            a.setGwasMgiGeneSymbol(mapKey);
                            beans.add(a);
                        }
                        break;
                    case GwasDTO.MP_TERM_ID:
                    	mapKey = gw.getMpTermId();
                        if (gwasMpIdSet.add(mapKey)) {
                            a.setGwasMpTermId(mapKey);
                            beans.add(a);
                        }
                        break;
                    case GwasDTO.MP_TERM_NAME:
                    	mapKey = gw.getMpTermName();
                        if (gwasMpTermSet.add(mapKey)) {
                            a.setGwasMpTermName(mapKey);
                            beans.add(a);
                        }
                        break;
                    case GwasDTO.DISEASE_TRAIT:
                        mapKey = gw.getDiseaseTrait();
                        if (gwasTraitSet.add(mapKey)) {
                            a.setGwasDiseaseTrait(mapKey);
                            beans.add(a);
                        }
                        break;
                    case GwasDTO.SNP_ID:
                    	mapKey = gw.getSnpId();
                        if (gwasSnipIdSet.add(mapKey)) {
                            a.setGwasSnpId(mapKey);
                            beans.add(a);
                        }
                        break;
                    case GwasDTO.REPORTED_GENE:
                        if ( !gw.getReportedGene().isEmpty()) {
                        	mapKey = gw.getReportedGene();
                            if (gwasReportedGeneSymbolSet.add(mapKey)) {
                            	a.setGwasReportedGene(mapKey);
                                beans.add(a);
                            }
                        }
                        break;

                    case GwasDTO.MAPPED_GENE:
                    	if ( !gw.getMappedGene().isEmpty()) {
	                    	mapKey = gw.getMappedGene();
	                        if (gwasMappedGeneSymbolSet.add(mapKey)) {
	                        	a.setGwasMappedGene(mapKey);
	                            beans.add(a);
	                        }
                    	}
                    	break;

                    case GwasDTO.DOWNSTREAM_GENE:
                    	if ( !gw.getDownstreamGene().isEmpty()) {
	                    	mapKey = gw.getDownstreamGene();
	                        if (gwasDownstreamGeneSymbolSet.add(mapKey)) {
	                        	a.setGwasDownstreamGene(mapKey);
	                            beans.add(a);
	                        }
                    	}
                    	break;
                    case GwasDTO.UPSTREAM_GENE:
                    	if ( !gw.getUpstreamGene().isEmpty()) {
	                    	mapKey = gw.getUpstreamGene();
	                        if (gwasUpstreamGeneSymbolSet.add(mapKey)) {
	                        	a.setGwasUpstreamGene(mapKey);
	                            beans.add(a);
	                        }
                    	}
                    	break;
                }
            }

            if ( ! beans.isEmpty()) {
                documentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }
        }
    }

    private void populateHpAutosuggestTerms() throws SolrServerException, IOException {

        List<String> hpFields = Arrays.asList(HpDTO.MP_ID, HpDTO.MP_TERM, HpDTO.HP_ID, HpDTO.HP_TERM, HpDTO.HP_SYNONYM);

        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(hpFields, ","))
            .addFilterQuery("type:hp_mp")
            .setRows(PHENODIGM_CORE_MAX_RESULTS);

        QueryResponse r = phenodigmCore.query(query);
        List<HpDTO> hps = phenodigmCore.query(query).getBeans(HpDTO.class);

        for (HpDTO hp : hps) {

            Set<AutosuggestBean> beans = new HashSet<>();

            if (hp.getHpSynonym() != null) {
                for (String s : hp.getHpSynonym()) {
                    mapKey = s;

                    if (hpSynonymSet.add(mapKey)) {
                        AutosuggestBean asyn = new AutosuggestBean();
                        asyn.setDocType("hp");
                        asyn.setHpId(hp.getHpId());
                        asyn.setHpTerm(hp.getHpTerm());
                        asyn.setHpSynonym(s);
                        asyn.setHpmpId(hp.getMpId());
                        asyn.setHpmpTerm(hp.getMpTerm());

                        beans.add(asyn);
                    }
                }
            }
            else {
                if (hpIdSet.add(mapKey)) {
                    AutosuggestBean a = new AutosuggestBean();
                    a.setDocType("hp");
                    a.setHpId(hp.getHpId());
                    a.setHpTerm(hp.getHpTerm());
                    a.setHpmpId(hp.getMpId());
                    a.setHpmpTerm(hp.getMpTerm());
                    beans.add(a);
                }
            }

            if ( ! beans.isEmpty()) {
                documentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }
        }
    }

    public static void main(String[] args) throws IndexerException, SQLException {
        SpringApplication.run(AutosuggestIndexer.class, args);
    }
}
