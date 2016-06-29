/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.cdaloader.steps;

import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.cdaloader.support.BiologicalModelAggregator;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by mrelac on 26/04/16.
 */
public class BiologicalModelWriter implements ItemWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;

    private BioModelPSSetter bioModelPss = new BioModelPSSetter();
    private BioModelAllelePSSetter bioModelAllelePss = new BioModelAllelePSSetter();
    private BioModelGenomicFeaturePSSetter bioModelGenomicFeaturePss = new BioModelGenomicFeaturePSSetter();
    private BioModelPhenotypePSSetter bioModelPhenotypePss = new BioModelPhenotypePSSetter();


    /**
     * Process the supplied data element. Will not be called with any null items
     * in normal operation.
     *
     * @param items items to be written
     * @throws Exception if there are errors. The framework will catch the
     *                   exception and convert or rethrow it as appropriate.
     */
    @Override
    public void write(List items) throws Exception {
        for (Object bioModel1 : items) {
            BiologicalModelAggregator bioModel = (BiologicalModelAggregator) bioModel1;

            sqlLoaderUtils.updateBioModel(bioModel, bioModelPss, bioModelAllelePss, bioModelGenomicFeaturePss, bioModelPhenotypePss);
        }
    }

    public class BioModelPSSetter implements PreparedStatementSetter {
        private BiologicalModelAggregator bioModel;


        public void setBioModel(BiologicalModelAggregator bioModel) {
                this.bioModel = bioModel;
            }

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {

//          INSERT INTO biological_model (db_id, allelic_composition, genetic_background)
//          VALUES (?, ?, ?)

            ps.setInt(1, DbIdType.MGI.intValue());
            ps.setString(2, bioModel.getAllelicComposition());
            ps.setString(3, bioModel.getGeneticBackground());
        }
    }

    public class BioModelAllelePSSetter implements PreparedStatementSetter {
        private BiologicalModelAggregator bioModel;

        public void setBioModel(BiologicalModelAggregator bioModel) {
            this.bioModel = bioModel;
        }

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {

//          INSERT INTO biological_model_allele (biological_model_id, allele_acc, allele_db_id)
//          VALUES (?, ?, ?)

            ps.setInt(1, bioModel.getBiologicalModelId());
            ps.setString(2, bioModel.getAlleleAccessionId());
            ps.setInt(3, DbIdType.MGI.intValue());
        }
    }

    public class BioModelGenomicFeaturePSSetter implements PreparedStatementSetter {
        private BiologicalModelAggregator bioModel;

        public void setBioModel(BiologicalModelAggregator bioModel) {
            this.bioModel = bioModel;
        }

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {

//          INSERT INTO biological_model_genomic_feature (biological_model_id, gf_acc, gf_db_id)
//          VALUES (?, ?, ?)

            ps.setInt(1, bioModel.getBiologicalModelId());
            ps.setString(2, bioModel.getMarkerAccessionId());
            ps.setInt(3, DbIdType.MGI.intValue());
        }
    }

    public class BioModelPhenotypePSSetter implements PreparedStatementSetter {
        private BiologicalModelAggregator bioModel;

        public void setBioModel(BiologicalModelAggregator bioModel) {
            this.bioModel = bioModel;
        }

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {

//          INSERT INTO biological_model_phenotype (biological_model_id, phenotype_acc, phenotype_db_id)
//          VALUES (?, ?, ?)

            ps.setInt(1, bioModel.getBiologicalModelId());
            ps.setString(2, bioModel.getMpAccessionId());
            ps.setInt(3, DbIdType.MP.intValue());
        }
    }
}