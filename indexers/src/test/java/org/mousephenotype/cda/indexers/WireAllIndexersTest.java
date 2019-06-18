/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * /**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mrelac
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {IndexersTestConfig.class})
public class WireAllIndexersTest implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    final String OBSERVATION_CORE = IndexerManager.OBSERVATION_CORE;
    final String GENOTYPE_PHENOTYPE_CORE = IndexerManager.GENOTYPE_PHENOTYPE_CORE;
    final String STATSTICAL_RESULT_CORE = IndexerManager.STATSTICAL_RESULT_CORE;
    final String MGI_PHENOTYPE_CORE = IndexerManager.MGI_PHENOTYPE_CORE;
    final String PRODUCT_CORE = IndexerManager.PRODUCT_CORE;
    final String ALLELE2_CORE = IndexerManager.ALLELE2_CORE;
    final String ALLELE_CORE = IndexerManager.ALLELE_CORE;
    final String IMAGES_CORE = IndexerManager.IMAGES_CORE;
    final String IMPC_IMAGES_CORE = IndexerManager.IMPC_IMAGES_CORE;
    final String MP_CORE = IndexerManager.MP_CORE;
    final String ANATOMY_CORE = IndexerManager.ANATOMY_CORE;
    final String PIPELINE_CORE = IndexerManager.PIPELINE_CORE;
    final String GENE_CORE = IndexerManager.GENE_CORE;
    final String AUTOSUGGEST_CORE = IndexerManager.AUTOSUGGEST_CORE;

    final Class observationClass = ObservationIndexer.class;
    final Class genotypePhenotypeClass = GenotypePhenotypeIndexer.class;
    final Class statisticalResultClass = StatisticalResultsIndexer.class;
    final Class mgiPhenotypeClass = MGIPhenotypeIndexer.class;
    final Class alleleClass = AlleleIndexer.class;
    final Class imagesClass = SangerImagesIndexer.class;
    final Class impcImagesClass = ImpcImagesIndexer.class;
    final Class mpClass = MPIndexer.class;
    final Class anatomyClass = AnatomyIndexer.class;
    final Class pipelineClass = PipelineIndexer.class;
    final Class geneClass = GeneIndexer.class;
    final Class autosuggestClass = AutosuggestIndexer.class;
    final Class allele2Class = Allele2Indexer.class;
    final Class productClass = ProductIndexer.class;

    @Test
    public void testStaticNoArgs() throws IOException, SolrServerException, SQLException, URISyntaxException, IllegalAccessException, InstantiationException {

        List<IndexerItem> indexerItemList = new ArrayList();

        indexerItemList.add(new IndexerItem(OBSERVATION_CORE, observationClass));
        indexerItemList.add(new IndexerItem(GENOTYPE_PHENOTYPE_CORE, genotypePhenotypeClass));
        indexerItemList.add(new IndexerItem(STATSTICAL_RESULT_CORE, statisticalResultClass));
        indexerItemList.add(new IndexerItem(MGI_PHENOTYPE_CORE, mgiPhenotypeClass));
        indexerItemList.add(new IndexerItem(ALLELE2_CORE, allele2Class));
        indexerItemList.add(new IndexerItem(PRODUCT_CORE, productClass));
        indexerItemList.add(new IndexerItem(ALLELE_CORE, alleleClass));
        indexerItemList.add(new IndexerItem(IMAGES_CORE, imagesClass));
        indexerItemList.add(new IndexerItem(IMPC_IMAGES_CORE, impcImagesClass));
        indexerItemList.add(new IndexerItem(MP_CORE, mpClass));
        indexerItemList.add(new IndexerItem(ANATOMY_CORE, anatomyClass));
        indexerItemList.add(new IndexerItem(PIPELINE_CORE, pipelineClass));
        indexerItemList.add(new IndexerItem(GENE_CORE, geneClass));
        indexerItemList.add(new IndexerItem(AUTOSUGGEST_CORE, autosuggestClass));

        IndexerItem[] indexerItems = indexerItemList.toArray(new IndexerItem[0]);

        for (IndexerItem indexerItem : indexerItems) {

            AbstractIndexer idx = (AbstractIndexer) indexerItem.indexerClass.newInstance();
            applicationContext.getAutowireCapableBeanFactory().autowireBean(idx);
            applicationContext.getAutowireCapableBeanFactory().initializeBean(idx, "IndexBean" + idx.getClass().toGenericString());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    class IndexerItem {
        final String name;
        final Class indexerClass;

        IndexerItem(String name, Class indexerClass) {
            this.name = name;
            this.indexerClass = indexerClass;
        }
    }
}