package org.mousephenotype.cda.loads.annotations;

import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.db.statistics.ResultDTO;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class StatisticalResultProcessorThreeI {

    private static final Logger logger = LoggerFactory.getLogger(StatisticalResultProcessorThreeI.class);

    private Float pValue = 0.0001f;

    private String query = "SELECT s.id as result_id, s.mp_acc, s.external_db_id, s.colony_id, s.project_id,"
            + " s.experimental_zygosity as zygosity, s.female_controls, s.female_mutants, s.male_controls, s.male_mutants,"
            + " s.null_test_significance, s.genotype_effect_pvalue, s.genotype_parameter_estimate, s.gender_female_ko_estimate,"
            + " s.gender_male_ko_estimate, s.gender_female_ko_pvalue, s.gender_male_ko_pvalue,"
            + " s.parameter_id as parameter_id, pparam.stable_id as parameter_stable_id, pproc.id as procedure_id, s.pipeline_id as pipeline_id,"
            + " bmgf.gf_acc, bmgf.gf_db_id, bma.allele_acc, bma.allele_db_id, bms.strain_acc, bms.strain_db_id, s.organisation_id as center_id "
            + " FROM stats_unidimensional_results s"
            + " INNER JOIN biological_model bmm ON bmm.id=s.experimental_id"
            + " INNER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id=s.experimental_id"
            + " INNER JOIN biological_model_strain bms ON bms.biological_model_id=s.experimental_id"
            + " INNER JOIN biological_model_allele bma ON bma.biological_model_id=s.experimental_id"
            + " INNER JOIN phenotype_procedure pproc ON pproc.id=s.procedure_id"
            + " INNER JOIN phenotype_parameter pparam ON pparam.id=s.parameter_id"
            + " WHERE s.statistical_method = 'Manual'"
            + " AND EXISTS (SELECT * from phenotype_parameter_lnk_ontology_annotation pont WHERE parameter_id = s.parameter_id)" // Filter in results that can generate MP terms
            + " AND s.null_test_significance <= ? ";


    private DataSource          komp2DataSource;
    private ParameterRepository parameterRepository;


    public StatisticalResultProcessorThreeI(
            @NotNull ParameterRepository parameterRepository,
            @NotNull DataSource komp2DataSource)
    {
        this.parameterRepository = parameterRepository;
        this.komp2DataSource = komp2DataSource;
    }


    Set<GenotypePhenotypeAssociationDTO> getPhenotypeAssociations() throws SQLException {
        Set<GenotypePhenotypeAssociationDTO> associations = new HashSet<>();

        Connection connection = komp2DataSource.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setFloat(1, pValue);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                Parameter parameter = parameterRepository.getById(resultSet.getLong("parameter_id"));

                GenotypePhenotypeAssociationDTO association = new GenotypePhenotypeAssociationDTO();
                association.setGf_acc(resultSet.getString("gf_acc"));
                association.setAllele_acc(resultSet.getString("allele_acc"));
                association.setZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));
                association.setParameterStableId(resultSet.getString("parameter_stable_id"));
                association.setColony_id(resultSet.getString("colony_id"));
                association.setExternal_db_id(resultSet.getInt("external_db_id"));

                ResultDTO result = new ResultDTO();

                result.setResultId(resultSet.getLong("result_id"));
                result.setDataSourceId(resultSet.getLong("external_db_id"));
                result.setColonyId(resultSet.getString("colony_id"));
                result.setMpTerm(resultSet.getString("mp_acc"));
                result.setProjectId(resultSet.getLong("project_id"));
                result.setCenterId(resultSet.getLong("center_id"));
                result.setPipelineId(resultSet.getLong("pipeline_id"));
                result.setProcedureId(resultSet.getLong("procedure_id"));
                result.setParameterId(resultSet.getLong("parameter_id"));
                result.setParameterStableId(resultSet.getString("parameter_stable_id"));
                result.setZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));
                result.setStrainAcc(resultSet.getString("strain_acc"));
                result.setStrainDbId(resultSet.getLong("strain_db_id"));
                result.setGeneAcc(resultSet.getString("gf_acc"));
                result.setGeneDbId(resultSet.getLong("gf_db_id"));
                result.setAlleleAcc(resultSet.getString("allele_acc"));
                result.setAlleleDbId(resultSet.getLong("allele_db_id"));
                result.setMaleControls(resultSet.getInt("male_controls"));
                result.setMaleMutants(resultSet.getInt("male_mutants"));
                result.setFemaleControls(resultSet.getInt("female_controls"));
                result.setFemaleMutants(resultSet.getInt("female_mutants"));

                // The wasNull() check is required because by default, ResultSet
                // will coerce the value to 0 if it's SQL counterpart is null
                // http://stackoverflow.com/questions/2920364/checking-for-a-null-int-value-from-a-java-resultset
                result.setNullTestPvalue(resultSet.getDouble("null_test_significance"));
                if (resultSet.wasNull()) {
                    result.setNullTestPvalue(null);
                }

                result.setGenotypeEffectPvalue(resultSet.getDouble("genotype_effect_pvalue"));
                if (resultSet.wasNull()) {
                    result.setGenotypeEffectPvalue(null);
                }

                result.setGenotypeEffectSize(resultSet.getDouble("genotype_parameter_estimate"));
                if (resultSet.wasNull()) {
                    result.setGenotypeEffectSize(null);
                }

                result.setFemaleEffectSize(resultSet.getDouble("gender_female_ko_estimate"));
                if (resultSet.wasNull()) {
                    result.setFemaleEffectSize(null);
                }

                result.setFemalePvalue(resultSet.getDouble("gender_female_ko_pvalue"));
                if (resultSet.wasNull()) {
                    result.setFemalePvalue(null);
                }

                result.setMaleEffectSize(resultSet.getDouble("gender_male_ko_estimate"));
                if (resultSet.wasNull()) {
                    result.setMaleEffectSize(null);
                }

                result.setMalePvalue(resultSet.getDouble("gender_male_ko_pvalue"));
                if (resultSet.wasNull()) {
                    result.setMalePvalue(null);
                }

                associations.add(association);
            }
        }

        logger.info("  Found " + associations.size() + " Three I results");

        return associations;
    }
}