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
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.constants.ParameterConstants;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.enumerations.LifeStage;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.utilities.PercentChangeStringParser;
import org.mousephenotype.cda.utilities.RunStatus;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Populate the Genotype-Phenotype core
 */
@EnableAutoConfiguration
public class GenotypePhenotypeIndexer extends AbstractIndexer {

    private final Logger logger = LoggerFactory.getLogger(GenotypePhenotypeIndexer.class);

    private final int         MAX_MISSING_LIFE_STAGE_ERRORS_TO_LOG = 100;
    private final Set<String> SKIP_PROCEDURES                      = new HashSet<>(Arrays.asList(       // Do not process parameters from these procecures
            "IMPC_ELZ", "IMPC_EOL", "IMPC_EMO", "IMPC_MAA", "IMPC_EMA"
    ));

    private SolrClient                genotypePhenotypeCore;
    private int                       missingLifeStageCount = 0;
    private Map<Long, ImpressBaseDTO> pipelineMap           = new HashMap<>();
    private Map<Long, ImpressBaseDTO> procedureMap          = new HashMap<>();
    private Map<Long, ParameterDTO>   parameterMap          = new HashMap<>();
    private OntologyParser            mpParser;
    private OntologyParser            mpMaParser;
    private OntologyParser            maParser;
    private OntologyParserFactory     ontologyParserFactory;

    protected GenotypePhenotypeIndexer() {

    }

    @Inject
    public GenotypePhenotypeIndexer(
            @NotNull DataSource komp2DataSource,
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull SolrClient genotypePhenotypeCore) {
        super(komp2DataSource, ontologyTermRepository);
        this.genotypePhenotypeCore = genotypePhenotypeCore;


    }


    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(genotypePhenotypeCore);
    }

    @Override
    public RunStatus run() throws IndexerException {
        int count = 0;
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();

        try (Connection connection = komp2DataSource.getConnection()) {

            ontologyParserFactory = new OntologyParserFactory(komp2DataSource, owlpath);

            mpParser = ontologyParserFactory.getMpParser();
            maParser = ontologyParserFactory.getMaParser();
            mpMaParser = ontologyParserFactory.getMpMaParser();

            pipelineMap = IndexerMap.getImpressPipelines(connection);
            procedureMap = IndexerMap.getImpressProcedures(connection);
            parameterMap = IndexerMap.getImpressParameters(connection);

            count = populateGenotypePhenotypeSolrCore(connection, runStatus);

        } catch (SQLException | IOException | SolrServerException | OWLOntologyCreationException | OWLOntologyStorageException ex) {
        	ex.printStackTrace();
            throw new IndexerException(ex);
        }

        logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));
        return runStatus;
    }

    // Returns document count.
    public int populateGenotypePhenotypeSolrCore(Connection connection, RunStatus runStatus) throws SQLException, IOException, SolrServerException {

        int count = 0;

        genotypePhenotypeCore.deleteByQuery("*:*");

        // conditions of WHERE clauses
        /*
        - the first for normal lines
        - the 2nd for viability and fertility
        - the last s.p_value IS NULL is to pick up MPATH in the mp_acc column
         */


        String query = "SELECT s.id AS id, CASE WHEN sur.statistical_method IS NOT NULL THEN sur.statistical_method WHEN scr.statistical_method IS NOT NULL THEN scr.statistical_method ELSE 'Unknown' END AS statistical_method, " +
                "  sur.genotype_percentage_change, o.name AS phenotyping_center, s.external_id, s.parameter_id AS parameter_id, s.procedure_id AS procedure_id, s.pipeline_id AS pipeline_id, s.gf_acc AS marker_accession_id, " +
                "  gf.symbol AS marker_symbol, s.allele_acc AS allele_accession_id, al.name AS allele_name, al.symbol AS allele_symbol, s.strain_acc AS strain_accession_id, st.name AS strain_name, " +
                "  s.sex AS sex, s.zygosity AS zygosity, p.name AS project_name, p.fullname AS project_fullname, s.mp_acc AS ontology_term_id, ot.name AS ontology_term_name, " +
                "  CASE WHEN s.p_value IS NOT NULL THEN s.p_value WHEN s.sex='female' THEN sur.gender_female_ko_pvalue WHEN s.sex='male' THEN sur.gender_male_ko_pvalue END AS p_value, " +
                "  s.effect_size AS effect_size, " +
                "  s.colony_id, db.name AS resource_fullname, db.short_name AS resource_name, s.life_stage, s.life_stage_acc " +
                "FROM phenotype_call_summary s " +
                "  LEFT OUTER JOIN stat_result_phenotype_call_summary srpcs ON srpcs.phenotype_call_summary_id = s.id " +
                "  LEFT OUTER JOIN stats_unidimensional_results sur ON sur.id = srpcs.unidimensional_result_id " +
                "  LEFT OUTER JOIN stats_categorical_results scr ON scr.id = srpcs.categorical_result_id " +
                "  LEFT OUTER JOIN stats_rrplus_results srr ON srr.id = srpcs.rrplus_result_id " +
                "  INNER JOIN organisation o ON s.organisation_id = o.id " +
                "  INNER JOIN project p ON s.project_id = p.id " +
                "  INNER JOIN ontology_term ot ON ot.acc = s.mp_acc " +
                "  INNER JOIN genomic_feature gf ON s.gf_acc = gf.acc " +
                "  LEFT OUTER JOIN strain st ON s.strain_acc = st.acc " +
                "  LEFT OUTER JOIN allele al ON s.allele_acc = al.acc " +
                "  INNER JOIN external_db db ON s.external_db_id = db.id " +
                "WHERE (0.0001 >= s.p_value " +
                "  OR (s.p_value IS NULL AND s.sex='male' AND sur.gender_male_ko_pvalue <= 0.0001) " +
                "  OR (s.p_value IS NULL AND s.sex='female' AND sur.gender_female_ko_pvalue <= 0.0001)) " +
                "OR (s.parameter_id IN (SELECT id FROM phenotype_parameter WHERE stable_id like 'IMPC_VIA%' OR stable_id LIKE 'IMPC_FER%')) " +
                "OR s.p_value IS NULL ";

        try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

            p.setFetchSize(Integer.MIN_VALUE);

            ResultSet r = p.executeQuery();
            Map<String, Integer> skippedNotWarned = new HashMap<>();
            while (r.next()) {

                GenotypePhenotypeDTO doc = new GenotypePhenotypeDTO();

                doc.setId(r.getString("id"));
                doc.setSex(r.getString("sex"));
                doc.setZygosity(r.getString("zygosity"));
                doc.setPhenotypingCenter(r.getString("phenotyping_center"));
                doc.setProjectName(r.getString("project_name"));
                doc.setProjectFullname(r.getString("project_fullname"));

                String percentageChangeDb = r.getString("genotype_percentage_change");
                if (!r.wasNull()) {

                    // Default female, override if male
                    Double percentageChange = PercentChangeStringParser.getFemalePercentageChange(percentageChangeDb);

                    if (doc.getSex().equals(SexType.male.getName())) {
                        percentageChange = PercentChangeStringParser.getMalePercentageChange(percentageChangeDb);
                    }

                    if (percentageChange != null) {
                        doc.setPercentageChange(percentageChange.toString() + "%");
                    }

                }

                doc.setStatisticalMethod(r.getString("statistical_method"));

                // Only set the p_value and effect size if they are not null in the phenotype call summary table
                Double d = r.getDouble("p_value");
                if (!r.wasNull()) {
                    doc.setP_value(r.getDouble("p_value"));
                }

                d = r.getDouble("effect_size");
                if (!r.wasNull()) {
                    doc.setEffectSize(r.getDouble("effect_size"));
                }

                doc.setMarkerAccessionId(r.getString("marker_accession_id"));
                doc.setMarkerSymbol(r.getString("marker_symbol"));

                String colonyId = r.getString("colony_id");
                doc.setColonyId(colonyId);
                doc.setAlleleAccessionId(r.getString("allele_accession_id"));
                doc.setAlleleName(r.getString("allele_name"));
                doc.setAlleleSymbol(r.getString("allele_symbol"));
                doc.setStrainAccessionId(r.getString("strain_accession_id"));
                doc.setStrainName(r.getString("strain_name"));

                // Procedure prefix is the first two strings of the parameter after splitting on underscore
                // i.e. IMPC_BWT_001_001 => IMPC_BWT
                String procedurePrefix = StringUtils.join(Arrays.asList(parameterMap.get(r.getLong("parameter_id")).getStableId().split("_")).subList(0, 2), "_");
                if (ParameterConstants.source3iProcedurePrefixes.contains(procedurePrefix)) {
                    doc.setResourceName("3i");
                    doc.setResourceFullname("Infection, Immunity and Immunophenotyping consortium");
                } else {
                    doc.setResourceFullname(r.getString("resource_fullname"));
                    doc.setResourceName(r.getString("resource_name"));
                }

                doc.setExternalId(r.getString("external_id"));

                String pipelineStableId = pipelineMap.get(r.getLong("pipeline_id")).getStableId();
                doc.setPipelineStableKey("" + pipelineMap.get(r.getLong("pipeline_id")).getStableKey());
                doc.setPipelineName(pipelineMap.get(r.getLong("pipeline_id")).getName());
                doc.setPipelineStableId(pipelineStableId);

                String procedureStableId = procedureMap.get(r.getLong("procedure_id")).getStableId();
                doc.setProcedureStableKey("" + procedureMap.get(r.getLong("procedure_id")).getStableKey());
                doc.setProcedureName(procedureMap.get(r.getLong("procedure_id")).getName());
                doc.setProcedureStableId(procedureStableId);

                if (SKIP_PROCEDURES.contains(procedurePrefix)) {
                    // Do not store phenotype associations for these parameters
                    // if somehow they make it into the database
                    continue;
                }

                doc.setParameterStableKey("" + parameterMap.get(r.getLong("parameter_id")).getStableKey());
                doc.setParameterName(parameterMap.get(r.getLong("parameter_id")).getName());
                doc.setParameterStableId(parameterMap.get(r.getLong("parameter_id")).getStableId());

                doc.setLifeStageName(r.getString("life_stage"));
                doc.setLifeStageAcc(r.getString("life_stage_acc"));


                // MP association
                if (r.getString("ontology_term_id").startsWith("MP:")) {
                    // some hard-coded stuff
                    doc.setOntologyDbId(5L);

                    if (doc.getP_value() != null) {
                        doc.setAssertionType("automatic");
                        doc.setAssertionTypeId("ECO:0000203");
                    } else {
                        doc.setAssertionType("manual");
                        doc.setAssertionTypeId("ECO:0000218");

                    }

                    String mpId = r.getString("ontology_term_id");
                    String mpName = r.getString("ontology_term_name");

                    doc.setMpTermId(mpId);
                    doc.setMpTermName(mpName);

                    // mp-ma mappings, only add to adult (POSTPARTUM_STAGE) (MmusDv:0000092) as mapping if to MA

                    if (doc.getLifeStageAcc() == null) {
                        if (missingLifeStageCount < MAX_MISSING_LIFE_STAGE_ERRORS_TO_LOG) {
                            logger.warn("life stage is NULL for " +
                                                "mpTermId " + mpId +
                                                ", mpTermName " + mpName +
                                                ", external_id " + doc.getExternalId() +
                                                ", resourceName " + doc.getResourceName() +
                                                ", pipelineStableId " + doc.getPipelineStableId() +
                                                ", procedureStableId " + doc.getProcedureStableId() +
                                                ", parameterStableId " + doc.getParameterStableId() +
                                                ". Skipping...");
                        };

                        missingLifeStageCount++;
                        continue;
                    }


                    if (doc.getLifeStageName().equalsIgnoreCase(LifeStage.EARLY_ADULT.getName())) {

                        getMaTermsForMp(doc, mpId, true);

                        // Also check mappings up the tree, as a leaf term might not have a mapping, but the parents might.
                        for (String mpAncestorId : mpParser.getOntologyTerm(mpId).getIntermediateIds()) {
                            getMaTermsForMp(doc, mpAncestorId, false);
                        }

                    }

                    OntologyTermDTO mpDto = mpParser.getOntologyTerm(mpId);

                    if (mpDto == null) {
                        logger.warn(" Skipping missing mp term '" + mpId + "'");
                        continue;
                    }

                    if (mpDto.getTopLevelIds() == null || mpDto.getTopLevelIds().size() == 0 ){
                        // if the mpId itself is a top level, add itself as a top level
                        List<String> ids = new ArrayList<>(); ids.add(mpId);
                        List<String> names = new ArrayList<>(); names.add(doc.getMpTermName());

                        doc.setTopLevelMpTermId(ids);
                        doc.setTopLevelMpTermName(names);
                    }
                    else {
                        doc.setTopLevelMpTermId(mpDto.getTopLevelIds());
                        doc.setTopLevelMpTermName(mpDto.getTopLevelNames());
                    }
                    doc.setIntermediateMpTermId(mpDto.getIntermediateIds());
                    doc.setIntermediateMpTermName(mpDto.getIntermediateNames());
                }
                // MPATH association
                else if (r.getString("ontology_term_id").startsWith("MPATH:")) {
                    // some hard-coded stuff
                    doc.setOntologyDbId(24L);
                    doc.setAssertionType("manual");
                    doc.setAssertionTypeId("ECO:0000218");

                    doc.setMpathTermId(r.getString("ontology_term_id"));
                    doc.setMpathTermName(r.getString("ontology_term_name"));
                }

                // EMAP association
                else if (r.getString("ontology_term_id").startsWith("EMAP:")) {
                    // some hard-coded stuff
                    doc.setOntologyDbId(14L);
                    doc.setAssertionType("manual");
                    doc.setAssertionTypeId("ECO:0000218");

                    doc.setMpathTermId(r.getString("ontology_term_id"));
                    doc.setMpathTermName(r.getString("ontology_term_name"));
                } else {
                    runStatus.addError(" Found unknown ontology term: " + r.getString("ontology_term_id"));
                }

                expectedDocumentCount++;
                genotypePhenotypeCore.addBean(doc, 30000);

                count++;
            }

            if (skippedNotWarned.size() > 0) {
                logger.info("Skipped phenotypes for derived parametersId ");
                for (String key : skippedNotWarned.keySet()) {
                    System.out.println("  " + key + " : " + skippedNotWarned.get(key));
                }
            }

            // Final commit to save the rest of the docs
            genotypePhenotypeCore.commit();

        } catch (Exception e) {
            runStatus.addError(" Big error " + e.getMessage());
            e.printStackTrace();
        }

        if (missingLifeStageCount > 0) {
            logger.warn("missingLifeStageCount = " + missingLifeStageCount);
        }

        return count;
    }


    /**
     *
     * @param dto
     * @param mpId
     * @param direct direct term or ancestors
     */
    protected void getMaTermsForMp(GenotypePhenotypeDTO dto, String mpId, Boolean direct) {

        // get MA ids referenced from MP
        Set<String> maTerms = mpMaParser.getReferencedClasses(mpId, ontologyParserFactory.VIA_PROPERTIES, "MA");
        for (String maId : maTerms) {
            // get info about these MA terms. In the mp-ma file the MA classes have no details but the id. For example the labels or synonyms are not there.
            OntologyTermDTO ma = maParser.getOntologyTerm(maId);
            if (ma != null) {
                if (direct) {
                    dto.addAnatomyTermId(ma.getAccessionId());
                    dto.addAnatomyTermName(ma.getName());
                } else {
                    dto.addIntermediateAnatomyTermId(ma.getAccessionId());
                    dto.addIntermediateAnatomyTermName(ma.getName());
                }
                if (ma.getTopLevelIds() != null) {
                    dto.addTopLevelAnatomyTermId(ma.getTopLevelIds());
                    dto.addTopLevelAnatomyTermName(ma.getTopLevelNames());
                }
                dto.addIntermediateAnatomyTermId(ma.getIntermediateIds());
                dto.addIntermediateAnatomyTermName(ma.getIntermediateNames());
            } else {
                //System.out.println("Term not found in MA : " + maId+ " for mpid:"+mpId);
            }
        }
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(GenotypePhenotypeIndexer.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);

        context.close();
    }
}
