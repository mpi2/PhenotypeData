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

package org.mousephenotype.cda.loads.common;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * Created by mrelac on 16/04/2018.
 */
@RunWith(SpringRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test2.properties")
@Import(ImpressTestConfig.class)
public class ImpressDataValidationTest extends TestCase {

	@Autowired
	private NamedParameterJdbcTemplate jdbcImpress;

	@Autowired
	private NamedParameterJdbcTemplate jdbcCdabase;

	@Test
	public void comparePipelines() {

	}

	@Test
	public void compareProcedures() {

	}

	@Test
	public void compareParameters() {

	}

	@Test
	public void compareOption() {

	}

	@Test
	public void compareIncrement() {

	}

	@Test
	public void compareOntologyAnnotation() {

	}

   	@Test
   	public void compareDBDetails() {

   		List<ImpressData> impressOldWay = getImpressData(jdbcImpress);
   		List<ImpressData> impressNewWay = getImpressData(jdbcCdabase);
   		
		Assert.assertEquals(impressOldWay.size(), impressNewWay.size());
   		
   		Set<ImpressData>  impressOldSet = new HashSet<>(impressOldWay);
   		Set<ImpressData> impressNewSet = new HashSet<>(impressNewWay);
   		
   		impressOldSet.removeAll(impressNewSet);

   		int count = 0;
   		for (ImpressData nonMatchingData : impressOldSet) {
   			System.out.println("MISMATCH: " + nonMatchingData.toString());
   			count++;
		}
		
		System.out.println(count + " MISMATCHES");
		
//		for (int i = 0; i < impressOldWay.size(); i++) {
//			Object oImpressOld = impressOldWay.get(i);
//			Object oImpressNew = impressNewWay.get(i);
//			if ((oImpressOld == null) || (oImpressNew == null)) {
//				Assert.assertNull(oImpressOld);
//				Assert.assertNull(oImpressNew);
//			} else {
//				Assert.assertEquals(oImpressOld.toString(), oImpressNew.toString());
//			}
//		}
	}

	private List<ImpressData> getImpressData(NamedParameterJdbcTemplate jdbc) {

		List<ImpressData> list = jdbc.query(impressQuery, new ImpressDataRowMapper());

		return list;
	}

	String impressQuery =
	"SELECT\n" +
			"  pi.stable_id                  AS pi_stable_id,\n" +
			"  pi.db_id                      AS pi_db_id,\n" +
			"  pi.name                       AS pi_name,\n" +
			"  pi.description                AS pi_description,\n" +
			"  pi.major_version              AS pi_major_version,\n" +
			"  pi.minor_version              AS pi_minor_version,\n" +
			"  pi.is_deprecated              AS pi_is_deprecated,\n" +
			"  \n" +
			"  pr.stable_id                  AS pr_stable_id,\n" +
			"  pr.db_id                      AS pr_db_id,\n" +
			"  pr.name                       AS pr_name,\n" +
			"  pr.description                AS pr_description,\n" +
			"  pr.major_version              AS pr_major_version,\n" +
			"  pr.minor_version              AS pr_minor_version,\n" +
			"  pr.is_mandatory               AS pr_is_mandatory,\n" +
			"  pr.level                      AS pr_level,\n" +
			"  pr.stage                      AS pr_stage,\n" +
			"  pr.stage_label                AS pr_stage_label,\n" +
			"  \n" +
			"  pa.stable_id                  AS pa_stable_id,\n" +
			"  pa.db_id                      AS pa_db_id,\n" +
			"  pa.name                       AS pa_name,\n" +
			"  pa.description                AS pa_description,\n" +
			"  pa.major_version              AS pa_major_version,\n" +
			"  pa.minor_version              AS pa_minor_version,\n" +
			"  pa.unit                       AS pa_unit,\n" +
			"  pa.datatype                   AS pa_datatype,\n" +
			"  pa.parameter_type             AS pa_parameter_type,\n" +
			"  pa.formula                    AS pa_formula,\n" +
			"  pa.required                   AS pa_required,\n" +
			"  pa.metadata                   AS pa_metadata,\n" +
			"  pa.important                  AS pa_important,\n" +
			"  pa.derived                    AS pa_derived,\n" +
			"  pa.annotate                   AS pa_annotate,\n" +
			"  pa.increment                  AS pa_increment,\n" +
			"  pa.options                    AS pa_options,\n" +
			"  pa.sequence                   AS pa_sequence,\n" +
			"  pa.media                      AS pa_media,\n" +
			"  pa.data_analysis              AS pa_data_analysis,\n" +
			"  pa.data_analysis_notes        AS pa_data_analysis_notes,\n" +
			"  \n" +
			"  pao.name                      AS pao_name,\n" +
			"  pao.description               AS pao_description,\n" +
			"  pao.normal                    AS pao_normal,\n" +
			"  \n" +
			"  pai.increment_value           AS pai_increment_value,\n" +
			"  pai.increment_datatype        AS pai_increment_datatype,\n" +
			"  pai.increment_unit            AS pai_increment_unit,\n" +
			"  pai.increment_minimum         AS pai_increment_minimum,\n" +
			"  \n" +
			"  paoa.event_type               AS paoa_event_type,\n" +
			"  paoa.option_id                AS paoa_option_id,\n" +
			"  paoa.ontology_acc             AS paoa_ontology_acc,\n" +
			"  paoa.ontology_db_id           AS paoa_ontology_db_id,\n" +
			"  paoa.sex                      AS paoa_sex\n" +
			"  \n" +
			"FROM phenotype_pipeline                             pi\n" +
			"JOIN phenotype_pipeline_procedure                   piprocedure         ON piprocedure.         pipeline_id     = pi.               id\n" +
			"JOIN phenotype_procedure                            pr                  ON pr.                  id              = piprocedure.      procedure_id\n" +
			"JOIN phenotype_procedure_parameter                  prparameter         ON prparameter.         procedure_id    = pr.               id\n" +
			"JOIN phenotype_parameter                            pa                  ON pa.                  id              = prparameter.      parameter_id\n" +
			"JOIN phenotype_parameter_lnk_option                 palo                ON palo.                parameter_id    = pa.               id\n" +
			"JOIN phenotype_parameter_option                     pao                 ON pao.                 id              = palo.             option_id\n" +
			"JOIN phenotype_parameter_lnk_increment              pali                ON pali.                parameter_id    = pa.               id\n" +
			"JOIN phenotype_parameter_increment                  pai                 ON pai.                 id              = pali.             increment_id\n" +
			"JOIN phenotype_parameter_lnk_ontology_annotation    paloa               ON paloa.               parameter_id    = pa.               id\n" +
			"JOIN phenotype_parameter_ontology_annotation        paoa                ON paoa.                id              = paloa.            annotation_id\n" +
			"\n" +
			"ORDER BY pa_stable_id, pr.stable_id, pa.stable_id, pao.name, pao.normal, pai.increment_value, pai.increment_datatype, pai.increment_unit, pai.increment_minimum,\n" +
			"         paoa.event_type, paoa.option_id, paoa.ontology_acc, paoa.ontology_db_id, paoa.sex";

   	public class ImpressData {
		String pi_stable_id;
		Integer pi_db_id;
		String pi_name;
		String pi_description;
		Integer pi_major_version;
		Integer pi_minor_version;
		Integer pi_is_deprecated;
		String pr_stable_id;
		Integer pr_db_id;
		String pr_name;
		String pr_description;
		Integer pr_major_version;
		Integer pr_minor_version;
		Integer pr_is_mandatory;
		String pr_level;
		String pr_stage;
		String pr_stage_label;
		String pa_stable_id;
		Integer pa_db_id;
		String pa_name;
		String pa_description;
		Integer pa_major_version;
		Integer pa_minor_version;
		String pa_unit;
		String pa_datatype;
		String pa_parameter_type;
		String pa_formula;
		Integer pa_required;
		Integer pa_metadata;
		Integer pa_important;
		Integer pa_derived;
		Integer pa_annotate;
		Integer pa_increment;
		Integer pa_options;
		Integer pa_sequence;
		Integer pa_media;
		Integer pa_data_analysis;
		String pa_data_analysis_notes;
		String pao_name;
		String pao_description;
		Integer pao_normal;
		String pai_increment_value;
		String pai_increment_datatype;
		String pai_increment_unit;
		String pai_increment_minimum;
		String paoa_event_type;
		Integer paoa_option_id;
		String paoa_ontology_acc;
		Integer paoa_ontology_db_id;
		String paoa_sex;

		@Override
		public String toString() {
			return "ImpressData{" +
					"pi_stable_id='" + pi_stable_id + '\'' +
					", pi_db_id=" + pi_db_id +
					", pi_name='" + pi_name + '\'' +
					", pi_description='" + pi_description + '\'' +
					", pi_major_version=" + pi_major_version +
					", pi_minor_version=" + pi_minor_version +
					", pi_is_deprecated=" + pi_is_deprecated +
					", pr_stable_id='" + pr_stable_id + '\'' +
					", pr_db_id=" + pr_db_id +
					", pr_name='" + pr_name + '\'' +
					", pr_description='" + pr_description + '\'' +
					", pr_major_version=" + pr_major_version +
					", pr_minor_version=" + pr_minor_version +
					", pr_is_mandatory=" + pr_is_mandatory +
					", pr_level='" + pr_level + '\'' +
					", pr_stage='" + pr_stage + '\'' +
					", pr_stage_label='" + pr_stage_label + '\'' +
					", pa_stable_id='" + pa_stable_id + '\'' +
					", pa_db_id=" + pa_db_id +
					", pa_name='" + pa_name + '\'' +
					", pa_description='" + pa_description + '\'' +
					", pa_major_version=" + pa_major_version +
					", pa_minor_version=" + pa_minor_version +
					", pa_unit='" + pa_unit + '\'' +
					", pa_datatype='" + pa_datatype + '\'' +
					", pa_parameter_type='" + pa_parameter_type + '\'' +
					", pa_formula='" + pa_formula + '\'' +
					", pa_required=" + pa_required +
					", pa_metadata=" + pa_metadata +
					", pa_important=" + pa_important +
					", pa_derived=" + pa_derived +
					", pa_annotate=" + pa_annotate +
					", pa_increment=" + pa_increment +
					", pa_options=" + pa_options +
					", pa_sequence=" + pa_sequence +
					", pa_media=" + pa_media +
					", pa_data_analysis=" + pa_data_analysis +
					", pa_data_analysis_notes='" + pa_data_analysis_notes + '\'' +
					", pao_name='" + pao_name + '\'' +
					", pao_description='" + pao_description + '\'' +
					", pao_normal=" + pao_normal +
					", pai_increment_value='" + pai_increment_value + '\'' +
					", pai_increment_datatype='" + pai_increment_datatype + '\'' +
					", pai_increment_unit='" + pai_increment_unit + '\'' +
					", pai_increment_minimum='" + pai_increment_minimum + '\'' +
					", paoa_event_type='" + paoa_event_type + '\'' +
					", paoa_option_id=" + paoa_option_id +
					", paoa_ontology_acc='" + paoa_ontology_acc + '\'' +
					", paoa_ontology_db_id=" + paoa_ontology_db_id +
					", paoa_sex='" + paoa_sex + '\'' +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ImpressData that = (ImpressData) o;
			return Objects.equals(pi_stable_id, that.pi_stable_id) &&
					Objects.equals(pi_db_id, that.pi_db_id) &&
					Objects.equals(pi_name, that.pi_name) &&
					Objects.equals(pi_description, that.pi_description) &&
					Objects.equals(pi_major_version, that.pi_major_version) &&
					Objects.equals(pi_minor_version, that.pi_minor_version) &&
					Objects.equals(pi_is_deprecated, that.pi_is_deprecated) &&
					Objects.equals(pr_stable_id, that.pr_stable_id) &&
					Objects.equals(pr_db_id, that.pr_db_id) &&
					Objects.equals(pr_name, that.pr_name) &&
					Objects.equals(pr_description, that.pr_description) &&
					Objects.equals(pr_major_version, that.pr_major_version) &&
					Objects.equals(pr_minor_version, that.pr_minor_version) &&
					Objects.equals(pr_is_mandatory, that.pr_is_mandatory) &&
					Objects.equals(pr_level, that.pr_level) &&
					Objects.equals(pr_stage, that.pr_stage) &&
					Objects.equals(pr_stage_label, that.pr_stage_label) &&
					Objects.equals(pa_stable_id, that.pa_stable_id) &&
					Objects.equals(pa_db_id, that.pa_db_id) &&
					Objects.equals(pa_name, that.pa_name) &&
					Objects.equals(pa_description, that.pa_description) &&
					Objects.equals(pa_major_version, that.pa_major_version) &&
					Objects.equals(pa_minor_version, that.pa_minor_version) &&
					Objects.equals(pa_unit, that.pa_unit) &&
					Objects.equals(pa_datatype, that.pa_datatype) &&
					Objects.equals(pa_parameter_type, that.pa_parameter_type) &&
					Objects.equals(pa_formula, that.pa_formula) &&
					Objects.equals(pa_required, that.pa_required) &&
					Objects.equals(pa_metadata, that.pa_metadata) &&
					Objects.equals(pa_important, that.pa_important) &&
					Objects.equals(pa_derived, that.pa_derived) &&
					Objects.equals(pa_annotate, that.pa_annotate) &&
					Objects.equals(pa_increment, that.pa_increment) &&
					Objects.equals(pa_options, that.pa_options) &&
					Objects.equals(pa_sequence, that.pa_sequence) &&
					Objects.equals(pa_media, that.pa_media) &&
					Objects.equals(pa_data_analysis, that.pa_data_analysis) &&
					Objects.equals(pa_data_analysis_notes, that.pa_data_analysis_notes) &&
					Objects.equals(pao_name, that.pao_name) &&
					Objects.equals(pao_description, that.pao_description) &&
					Objects.equals(pao_normal, that.pao_normal) &&
					Objects.equals(pai_increment_value, that.pai_increment_value) &&
					Objects.equals(pai_increment_datatype, that.pai_increment_datatype) &&
					Objects.equals(pai_increment_unit, that.pai_increment_unit) &&
					Objects.equals(pai_increment_minimum, that.pai_increment_minimum) &&
					Objects.equals(paoa_event_type, that.paoa_event_type) &&
					Objects.equals(paoa_option_id, that.paoa_option_id) &&
					Objects.equals(paoa_ontology_acc, that.paoa_ontology_acc) &&
					Objects.equals(paoa_ontology_db_id, that.paoa_ontology_db_id) &&
					Objects.equals(paoa_sex, that.paoa_sex);


		}

		@Override
		public int hashCode() {

			return Objects.hash(pi_stable_id, pi_db_id, pi_name, pi_description, pi_major_version, pi_minor_version, pi_is_deprecated, pr_stable_id, pr_db_id, pr_name, pr_description, pr_major_version, pr_minor_version, pr_is_mandatory, pr_level, pr_stage, pr_stage_label, pa_stable_id, pa_db_id, pa_name, pa_description, pa_major_version, pa_minor_version, pa_unit, pa_datatype, pa_parameter_type, pa_formula, pa_required, pa_metadata, pa_important, pa_derived, pa_annotate, pa_increment, pa_options, pa_sequence, pa_media, pa_data_analysis, pa_data_analysis_notes, pao_name, pao_description, pao_normal, pai_increment_value, pai_increment_datatype, pai_increment_unit, pai_increment_minimum, paoa_event_type, paoa_option_id, paoa_ontology_acc, paoa_ontology_db_id, paoa_sex);
		}
	}

	public class ImpressDataRowMapper implements RowMapper<ImpressData> {

		/**
		 * Implementations must implement this method to map each row of data
		 * in the ResultSet. This method should not call {@code next()} on
		 * the ResultSet; it is only supposed to map values of the current row.
		 *
		 * @param rs     the ResultSet to map (pre-initialized for the current row)
		 * @param rowNum the number of the current row
		 * @return the result object for the current row
		 * @throws SQLException if a SQLException is encountered getting
		 *                      column values (that is, there's no need to catch SQLException)
		 */
		@Override
		public ImpressData mapRow(ResultSet rs, int rowNum) throws SQLException {
			ImpressData data = new ImpressData();

			data.pi_stable_id = rs.getString("pi_stable_id");
			data.pi_db_id = rs.getInt("pi_db_id");
			data.pi_name = rs.getString("pi_name");
			data.pi_description = rs.getString("pi_description");
			data.pi_major_version = rs.getInt("pi_major_version");
			data.pi_minor_version = rs.getInt("pi_minor_version");
			data.pi_is_deprecated = rs.getInt("pi_is_deprecated");
			data.pr_stable_id = rs.getString("pr_stable_id");
			data.pr_db_id = rs.getInt("pr_db_id");
			data.pr_name = rs.getString("pr_name");
			data.pr_description = rs.getString("pr_description");
			data.pr_major_version = rs.getInt("pr_major_version");
			data.pr_minor_version = rs.getInt("pr_minor_version");
			data.pr_is_mandatory = rs.getInt("pr_is_mandatory");
			data.pr_level = rs.getString("pr_level");
			data.pr_stage = rs.getString("pr_stage");
			data.pr_stage_label = rs.getString("pr_stage_label");
			data.pa_stable_id = rs.getString("pa_stable_id");
			data.pa_db_id = rs.getInt("pa_db_id");
			data.pa_name = rs.getString("pa_name");
			data.pa_description = rs.getString("pa_description");
			data.pa_major_version = rs.getInt("pa_major_version");
			data.pa_minor_version = rs.getInt("pa_minor_version");
			data.pa_unit = rs.getString("pa_unit");
			data.pa_datatype = rs.getString("pa_datatype");
			data.pa_parameter_type = rs.getString("pa_parameter_type");
			data.pa_formula = rs.getString("pa_formula");
			data.pa_required = rs.getInt("pa_required");
			data.pa_metadata = rs.getInt("pa_metadata");
			data.pa_important = rs.getInt("pa_important");
			data.pa_derived = rs.getInt("pa_derived");
			data.pa_annotate = rs.getInt("pa_annotate");
			data.pa_increment = rs.getInt("pa_increment");
			data.pa_options = rs.getInt("pa_options");
			data.pa_sequence = rs.getInt("pa_sequence");
			data.pa_media = rs.getInt("pa_media");
			data.pa_data_analysis = rs.getInt("pa_data_analysis");
			data.pa_data_analysis_notes = rs.getString("pa_data_analysis_notes");
			data.pao_name = rs.getString("pao_name");
			data.pao_description = rs.getString("pao_description");
			data.pao_normal = rs.getInt("pao_normal");
			data.pai_increment_value = rs.getString("pai_increment_value");
			data.pai_increment_datatype = rs.getString("pai_increment_datatype");
			data.pai_increment_unit = rs.getString("pai_increment_unit");
			data.pai_increment_minimum = rs.getString("pai_increment_minimum");
			data.paoa_event_type = rs.getString("paoa_event_type");
			data.paoa_option_id = rs.getInt("paoa_option_id");
			data.paoa_ontology_acc = rs.getString("paoa_ontology_acc");
			data.paoa_ontology_db_id = rs.getInt("paoa_ontology_db_id");
			data.paoa_sex = rs.getString("paoa_sex");
			if ((data.paoa_sex != null) && (data.paoa_sex.trim().isEmpty()))
				data.paoa_sex = null;
			
			return data;
		}
	}


}