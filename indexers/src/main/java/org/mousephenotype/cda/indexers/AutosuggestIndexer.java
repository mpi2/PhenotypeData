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

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.indexers.beans.AutosuggestBean;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.service.dto.*;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**  
 * pdsimplify: This class references deprecated PhenodigmDTO
 */
// Autocomplete is no longer a requirement in the newly skinned BZ PA app. Refactor this class if autocomplete becomes a requirement again.
@Deprecated
@EnableAutoConfiguration
public class AutosuggestIndexer extends AbstractIndexer implements CommandLineRunner {

    private final Logger logger              = LoggerFactory.getLogger(this.getClass());
    private final int    MP_CORE_MAX_RESULTS = 350000;

    private SolrClient allele2Core;
    private SolrClient anatomyCore;
    private SolrClient autosuggestCore;
    private SolrClient geneCore;
    private SolrClient mpCore;
    private SolrClient phenodigmCore;
    private SolrClient pipelineCore;


    protected AutosuggestIndexer() {

    }

    @Inject
    public AutosuggestIndexer(
            @NotNull DataSource komp2DataSource,
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull SolrClient allele2Core,
            @NotNull SolrClient anatomyCore,
            @NotNull SolrClient autosuggestCore,
            @NotNull SolrClient geneCore,
            @NotNull SolrClient mpCore,
            @NotNull SolrClient phenodigmCore,
            @NotNull SolrClient pipelineCore) {
        super(komp2DataSource, ontologyTermRepository);
        this.allele2Core = allele2Core;
        this.anatomyCore = anatomyCore;
        this.autosuggestCore = autosuggestCore;
        this.geneCore = geneCore;
        this.mpCore = mpCore;
        this.phenodigmCore = phenodigmCore;
        this.pipelineCore = pipelineCore;
    }


// Sets used to insure uniqueness when loading core components.

    // gene
    Set<String> mgiAccessionIdSet = new HashSet();
    Set<String> mgiAlleleAccessionIdSet = new HashSet();
    Set<String> markerSymbolSet = new HashSet();
    Set<String> markerNameSet = new HashSet();
    Set<String> markerSynonymSet = new HashSet();
    Set<String> humanGeneSymbolSet = new HashSet();
    Map<String, List<String>> markerSymbolSynonymsMap = new HashMap<>();

    // mp
    Set<String> mpIdSet = new HashSet();
    Set<String> mpTermSet = new HashSet();
    Set<String> mpTermSynonymSet = new HashSet();
    Set<String> mpNarrowSynonymSet = new HashSet();
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

    // pipeline (parameter_name only)
    Set<String> parameterNameSet = new HashSet();

    // hp
    Set<String> hpIdSet = new HashSet();
    Set<String> hpTermSet = new HashSet();
    Set<String> hpTermSynonymSet = new HashSet();

    // mp
    Set<String> hpIdSet2 = new HashSet();
    Set<String> hpTermSet2 = new HashSet();
    Set<String> hpTermSynonymSet2 = new HashSet();

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
    Set<String> allele2MarkerNameSet  = new HashSet();
    Set<String> allele2MarkerSynonymSet = new HashSet();
    Set<String> allele2IkmcProjectSet = new HashSet();
    Set<String> allele2GeneAlleleSet = new HashSet();
    Map<String, List<String>> allele2MarkerSymbolSynonymsMap = new HashMap<>();
    Set<String> allele2HumanGeneSymbolSet = new HashSet();


    String mapKey;

    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(autosuggestCore);
    }


    @Override
    public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();

        try {

            autosuggestCore.deleteByQuery("*:*");

            //populateParameterNameAutosuggestTerms();  // no using pipeline for now
            populateGeneAutosuggestTerms();
            populateMpAutosuggestTerms();
            populateDiseaseAutosuggestTerms();
            populateAnatomyAutosuggestTerms();
            populateProductAutosuggestTerms(); // must run after populateGeneAutosuggestTerms to use the map markerSymbolSynonymsMap
            populateHpAutosuggestTerms();  // use mphp mapping from mphp ontology parser in mp core until the new phenodigm core has hp-mp mapping included
            //populateGwasAutosuggestTerms();


            // Final commit
            autosuggestCore.commit();

        } catch (SolrServerException | IOException e) {
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", expectedDocumentCount, commonUtils.msToHms(System.currentTimeMillis() - start));
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
                String docType = "gene";
                a.setDocType(docType);

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
                        mapKey = gene.getMarkerName();
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case GeneDTO.ALLELE_ACCESSION_ID:
                        if (gene.getAlleleAccessionIds() != null) {
                            for (String s : gene.getAlleleAccessionIds()) {
                                mapKey = s;
                                if (mgiAlleleAccessionIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setHumanGeneSymbol(s);
                                    asyn.setDocType(docType);
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                expectedDocumentCount += beans.size();
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

    private void populateParameterNameAutosuggestTerms() throws SolrServerException, IOException {

        List<String> pipelineFields = Arrays.asList("pipeline_name", "procedure_name", "parameter_name");

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .setFields(StringUtils.join(pipelineFields, ","))
                .setRows(Integer.MAX_VALUE);


        QueryResponse rsp = pipelineCore.query(query);
        SolrDocumentList pipelineDocs = rsp.getResults();

        Set<AutosuggestBean> beans = new HashSet<>();

        for(SolrDocument pd : pipelineDocs){

            AutosuggestBean a = new AutosuggestBean();
            a.setDocType("pipeline");
            a.setParameterName(pd.getFieldValue("parameter_name").toString());
            beans.add(a);
        }
        if ( ! beans.isEmpty()) {
            expectedDocumentCount += beans.size();
            autosuggestCore.addBeans(beans, 60000);
        }
    }


    private void populateMpAutosuggestTerms() throws SolrServerException, IOException {

        List<String> mpFields = Arrays.asList(
                MpDTO.MP_ID, MpDTO.MP_TERM, MpDTO.MP_TERM_SYNONYM, MpDTO.MP_NARROW_SYNONYM, MpDTO.ALT_MP_ID, MpDTO.TOP_LEVEL_MP_ID, MpDTO.TOP_LEVEL_MP_TERM,
                MpDTO.TOP_LEVEL_MP_TERM_SYNONYM, MpDTO.INTERMEDIATE_MP_ID, MpDTO.INTERMEDIATE_MP_TERM, MpDTO.PARENT_MP_ID, MpDTO.PARENT_MP_TERM, MpDTO.PARENT_MP_TERM_SYNONYM,
                MpDTO.MIX_SYN_QF, MpDTO.INTERMEDIATE_MP_TERM_SYNONYM, MpDTO.CHILD_MP_ID, MpDTO.CHILD_MP_TERM, MpDTO.CHILD_MP_TERM_SYNONYM, MpDTO.HP_ID, MpDTO.HP_TERM,
                MpDTO.HP_TERM_SYNONYM);

        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(mpFields, ","))
            .setRows(Integer.MAX_VALUE);
        //System.out.println("QRY: " + query);
        List<MpDTO> mps = mpCore.query(query).getBeans(MpDTO.class);
        for (MpDTO mp : mps) {

            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : mpFields) {

                AutosuggestBean a = new AutosuggestBean();
                String docType = "mp";
                a.setDocType(docType);

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
                                    asyn.setDocType(docType);
                                    asyn.setMpTerm(mp.getMpTerm());
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.MIX_SYN_QF:
                        if (mp.getMixedSynonyms() != null) {
                            for (String s : mp.getMixedSynonyms()) {
                                mapKey = s;
                                if (mpTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setMpTermSynonym(s);
                                    asyn.setDocType(docType);
                                    asyn.setMpTerm(mp.getMpTerm());
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;

                    case MpDTO.HP_ID:
                        if ( mp.getHpId() != null ) {
                            for ( String hpId : mp.getHpId() ) {
                                if (hpIdSet2.add(hpId)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setHpId(hpId);
                                    asyn.setDocType(docType);
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.HP_TERM:
                        if ( mp.getHpTerm() != null ) {
                            for ( String hpTerm : mp.getHpTerm() ) {
                                if (hpTermSet2.add(hpTerm)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setHpTerm(hpTerm);
                                    asyn.setDocType(docType);
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case MpDTO.HP_TERM_SYNONYM:
                        if (mp.getHpTermSynonym() != null) {
                            for (String hpTermSynonym : mp.getHpTermSynonym()) {

                                if (hpTermSynonymSet2.add(hpTermSynonym)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setHpTermSynonym(hpTermSynonym);
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                expectedDocumentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }

        }
    }

    private void populateDiseaseAutosuggestTerms() throws SolrServerException, IOException {

        List<String> diseaseFields = Arrays.asList(DiseaseDTO.DISEASE_ID, DiseaseDTO.DISEASE_TERM, DiseaseDTO.DISEASE_ALTS);

        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFilterQueries("type:disease_search")
            .setFields(StringUtils.join(diseaseFields, ","))
            .setRows(Integer.MAX_VALUE);

        List<PhenodigmDTO> diseases = phenodigmCore.query(query).getBeans(PhenodigmDTO.class);
        for (PhenodigmDTO disease : diseases) {

            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : diseaseFields) {

                AutosuggestBean a = new AutosuggestBean();
                String docType = "disease";
                a.setDocType(docType);

                switch (field) {
                    case PhenodigmDTO.DISEASE_ID:
                        mapKey = disease.getDiseaseId();
                        if (diseaseIdSet.add(mapKey)) {
                            a.setDiseaseId(disease.getDiseaseId());
                            beans.add(a);
                        }
                        break;
                    case PhenodigmDTO.DISEASE_TERM:
                        mapKey = disease.getDiseaseTerm();
                        if (diseaseTermSet.add(mapKey)) {
                            a.setDiseaseTerm(disease.getDiseaseTerm());
                            beans.add(a);
                        }
                        break;
                    case PhenodigmDTO.DISEASE_ALTS:
                        if (disease.getDiseaseAlts() != null) {
                            for (String s : disease.getDiseaseAlts()) {
                                mapKey = s;
                                if (diseaseAltsSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setDiseaseAlts(s);
                                    asyn.setDocType(docType);
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                expectedDocumentCount += beans.size();
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
                AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_ID,
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
                String docType = "anatomy";
                a.setDocType(docType);

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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
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
                                    asyn.setDocType(docType);
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                expectedDocumentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }
        }
    }

    private void populateProductAutosuggestTerms() throws SolrServerException, IOException {

        List<String> productFields = Arrays.asList(AlleleDTO.MARKER_SYMBOL, AlleleDTO.MARKER_NAME, AlleleDTO.MARKER_SYNONYM, AlleleDTO.HUMAN_GENE_SYMBOL, AlleleDTO.ALLELE_NAME, AlleleDTO.IKMC_PROJECT, AlleleDTO.MGI_ACCESSION_ID, AlleleDTO.ALLELE_MGI_ACCESSION_ID);

        SolrQuery query = new SolrQuery()
                .setQuery("type:Allele")
                .setFields(StringUtils.join(productFields, ","))
                .setRows(Integer.MAX_VALUE);

        QueryResponse response = allele2Core.query(query);
        List<Allele2DTO> alleles = response.getBeans(Allele2DTO.class);

        String docType = "allele2";

        for (Allele2DTO allele : alleles) {

            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : productFields) {

                AutosuggestBean a = new AutosuggestBean();
                a.setDocType(docType);

                switch (field) {
                    case Allele2DTO.MARKER_SYMBOL:
                        mapKey = allele.getMarkerSymbol();
                        if (allele2MarkerSymbolSet.add(mapKey)) {
                            a.setMarkerSymbol(mapKey);
                            beans.add(a);

                            if ( allele2MarkerSymbolSynonymsMap.get(mapKey) != null ) {
                                for (String syn : allele2MarkerSymbolSynonymsMap.get(mapKey)) {
                                    AutosuggestBean syna = new AutosuggestBean();
                                    syna.setDocType(docType);
                                    syna.setMarkerSynonym(syn);
                                    beans.add(syna);
                                }
                            }
                        }
                        break;
                    case Allele2DTO.MARKER_NAME:
                        mapKey = allele.getMarkerName();
                        if (allele2MarkerNameSet.add(mapKey)) {
                            a.setMarkerName(mapKey);
                            beans.add(a);
                        }
                        break;
                    case Allele2DTO.HUMAN_GENE_SYMBOL:
                        if (allele.getHumanGeneSymbol() != null) {
                            for (String s : allele.getHumanGeneSymbol()) {
                                mapKey = s;
                                if (allele2HumanGeneSymbolSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setHumanGeneSymbol(s);
                                    asyn.setDocType(docType);
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case Allele2DTO.MGI_ACCESSION_ID:
                        mapKey = allele.getMgiAccessionId();
                        if (allele2MgiAccsSet.add(mapKey)) {
                            a.setMgiAccessionId(mapKey);
                            beans.add(a);
                        }
                        break;
                    case Allele2DTO.ALLELE_MGI_ACCESSION_ID:
                        mapKey = allele.getAlleleMgiAccessionId();
                        if (allele2MgiAccsSet.add(mapKey)) {
                            a.setAlleleMgiAccessionId(mapKey);
                            beans.add(a);
                        }
                        break;
                    case Allele2DTO.IKMC_PROJECT:
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
                    case Allele2DTO.ALLELE_NAME:
                        String mapKey = allele.getAlleleName();

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

                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                expectedDocumentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }

        }
    }

    private void populateHpAutosuggestTerms() throws SolrServerException, IOException {
        // using old phenodigm - need to update this when new phenodigm has hp-mp apping

        List<String> hpFields = Arrays.asList(HpDTO.MP_ID, HpDTO.MP_TERM, HpDTO.HP_ID, HpDTO.HP_TERM, HpDTO.HP_TERM_SYNONYM);

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .setFields(StringUtils.join(hpFields, ","))
                .setRows(MP_CORE_MAX_RESULTS);

        List<MpDTO> mps = mpCore.query(query).getBeans(MpDTO.class);
        for (MpDTO mp : mps) {

            Set<AutosuggestBean> beans = new HashSet<>();
            for (String field : hpFields) {

                AutosuggestBean a = new AutosuggestBean();
                String docType = "hp";
                a.setDocType(docType);

                switch (field) {
                    case HpDTO.HP_ID:
                        if (mp.getHpId() != null) {
                            for (String s : mp.getHpId()) {
                                mapKey = s;
                                if (hpIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setDocType(docType);
                                    asyn.setHpId(s);
                                    asyn.setHpmpId(mp.getMpId());
                                    asyn.setHpmpTerm(mp.getMpTerm());
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case HpDTO.HP_TERM:
                        if (mp.getHpId() != null) {
                            for (String s : mp.getHpTerm()) {
                                mapKey = s;
                                if (hpIdSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setDocType(docType);
                                    asyn.setHpTerm(s);
                                    asyn.setHpmpId(mp.getMpId());
                                    asyn.setHpmpTerm(mp.getMpTerm());
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                    case HpDTO.HP_TERM_SYNONYM:
                        if (mp.getHpTermSynonym() != null) {
                            for (String s : mp.getHpTermSynonym()) {
                                mapKey = s;
                                if (hpTermSynonymSet.add(mapKey)) {
                                    AutosuggestBean asyn = new AutosuggestBean();
                                    asyn.setDocType(docType);
                                    asyn.setHpTermSynonym(s);
                                    asyn.setHpmpId(mp.getMpId());
                                    asyn.setHpmpTerm(mp.getMpTerm());
                                    beans.add(asyn);
                                }
                            }
                        }
                        break;
                }
            }

            if ( ! beans.isEmpty()) {
                expectedDocumentCount += beans.size();
                autosuggestCore.addBeans(beans, 60000);
            }
        }
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(AutosuggestIndexer.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);

        context.close();
    }
}