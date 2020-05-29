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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.owl.AnatomogramMapper;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Populate the Anatomy core
 *
 * This includes MA and EMAPA and both top_levels and selected_top_levels are indexed for each core
 *
 *
 * @author ckchen based on the old MAIndexer
 */

@EnableAutoConfiguration
public class AnatomyIndexer extends AbstractIndexer implements CommandLineRunner {

    @Value("classpath:uberonEfoMaAnatomogram_mapping.txt")
    Resource anatomogramResource;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SolrClient               anatomyCore;
    private Map<String, Set<String>> maUberonEfoByTermId = new HashMap<>();

    private OntologyParser maParser;
    private OntologyParser emapaParser;
    private OntologyParser uberonParser;

    protected AnatomyIndexer() {

    }

    @Inject
    public AnatomyIndexer(
            @NotNull DataSource komp2DataSource,
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull SolrClient anatomyCore)
    {
        super(komp2DataSource, ontologyTermRepository);
        this.anatomyCore = anatomyCore;
    }

    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(anatomyCore);
    }


    @Override
    public RunStatus run() throws IndexerException {
        
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();

    	try {

            // Delete the documents in the core if there are any.
            anatomyCore.deleteByQuery("*:*");
            anatomyCore.commit();

            initialiseSupportingBeans();

            Set<String> maIds = maParser.getTermIdsInSlim();
            Set<String> emapaIds = emapaParser.getTermIdsInSlim();

            // Add all ma terms to the index.
            for (String maId : maIds ) {

                AnatomyDTO anatomyTerm = new AnatomyDTO();
                OntologyTermDTO maTerm = maParser.getOntologyTerm(maId);

                // Set scalars.
                anatomyTerm.setDataType("ma");
                anatomyTerm.setStage("adult");

                // Set fields in common with EMAPA documents
                addBasicFields(anatomyTerm, maTerm);

                // index UBERON/EFO id for MA id

                if (maUberonEfoByTermId.containsKey(maId)) {
                    for (String id : maUberonEfoByTermId.get(maId)){
                        if (id.startsWith("UBERON")){
                            anatomyTerm.addUberonIds(id);
                        } else if (id.startsWith("EFO")){
                            anatomyTerm.addEfoIds(id);
                        }
                    }
                }

                expectedDocumentCount++;
                anatomyCore.addBean(anatomyTerm, 60000);

            }

            // Add all emapa terms to the index.
            for (String emapaId  : emapaIds) {

                AnatomyDTO emapa = new AnatomyDTO();
                OntologyTermDTO emapaDTO = emapaParser.getOntologyTerm(emapaId);

                emapa.setDataType("emapa");
                emapa.setStage("embryo");

                addBasicFields(emapa, emapaDTO);

                expectedDocumentCount++;
                anatomyCore.addBean(emapa, 60000);

            }


            // Send a final commit
            anatomyCore.commit();

        } catch (SolrServerException | IOException e) {
            throw new IndexerException(e);
        }

        logger.info(" Added {} total beans in {}", expectedDocumentCount, commonUtils.msToHms(System.currentTimeMillis() - start));
        return runStatus;
    }


    private void addBasicFields(AnatomyDTO anatomyDTO, OntologyTermDTO termDTO){

        anatomyDTO.setAnatomyId(termDTO.getAccessionId());
        anatomyDTO.setAnatomyTerm(termDTO.getName());

        if (termDTO.getAlternateIds() != null && termDTO.getAlternateIds().size() > 0) {
            anatomyDTO.setAltAnatomyIds(termDTO.getAlternateIds());
        }

        anatomyDTO.setAnatomyTermSynonym(termDTO.getSynonyms());

        // top level for anatomy are NOT selected top levels, but organ, organ sytem, anatomic region, postnatal mouse, ... (not including nodeId 0). Not sure they're needed though.
        anatomyDTO.setTopLevelAnatomyId(termDTO.getTopLevelIds());
        anatomyDTO.setTopLevelAnatomyTerm(termDTO.getTopLevelNames());
        anatomyDTO.setTopLevelAnatomyTermSynonym(termDTO.getTopLevelSynonyms());

        anatomyDTO.setSelectedTopLevelAnatomyId(termDTO.getTopLevelIds());
        anatomyDTO.setSelectedTopLevelAnatomyTerm(termDTO.getTopLevelNames());
        anatomyDTO.setSelectedTopLevelAnatomyTermSynonym(termDTO.getTopLevelSynonyms());

        anatomyDTO.setIntermediateAnatomyId(termDTO.getIntermediateIds());
        anatomyDTO.setIntermediateAnatomyTerm(termDTO.getIntermediateNames());
        anatomyDTO.setIntermediateAnatomyTermSynonym(termDTO.getIntermediateSynonyms());

        anatomyDTO.setParentAnatomyId(termDTO.getParentIds());
        anatomyDTO.setParentAnatomyTerm(termDTO.getParentNames());

        anatomyDTO.setChildAnatomyId(termDTO.getChildIds());
        anatomyDTO.setChildAnatomyTerm(termDTO.getChildNames());

        anatomyDTO.setAnatomyNodeId(termDTO.getNodeIds());

    }

    private void initialiseSupportingBeans() {

        try {
            OntologyParserFactory ontologyParserFactory = new OntologyParserFactory(komp2DataSource, owlpath);
            emapaParser = ontologyParserFactory.getEmapaParser();
            maParser = ontologyParserFactory.getMaParser();
            uberonParser = ontologyParserFactory.getUberonParser();
            maUberonEfoByTermId = AnatomogramMapper.getMapping(maParser, uberonParser, "UBERON", "MA");

        } catch (SQLException | IOException | OWLOntologyCreationException | OWLOntologyStorageException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(AnatomyIndexer.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);

        context.close();
    }
}