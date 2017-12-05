/*******************************************************************************
 * Copyright © 2017 EMBL - European Bioinformatics Institute
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

import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BioModelResults {

    private static Logger logger = LoggerFactory.getLogger(BioModelResults.class);
    static NamedParameterJdbcTemplate jdbcCda;


    int    bm_db_id = 0;
    String bm_allelicComposition;
    String bm_geneticBackground;
    String bm_zygosity;

    String bm_strain_acc;
    int    bm_strain_db_id = 0;

    String  bm_gf_acc;
    int bm_gf_db_id = 0;

    String  bm_allele_acc;
    int bm_allele_db_id = 0;

    String strain_acc;
    int    strain_db_id = 0;

    String strain_biotype_acc;
    int    strain_biotype_db_id = 0;
    String strain_name;

    String  bs_external_id;
    int bs_db_id = 0;
    String  bs_sample_type_acc;
    int bs_sample_type_db_id = 0;
    String  bs_sample_group;
    int bs_organisation_id = 0;
    int bs_production_center_id = 0;


    public BioModelResults(NamedParameterJdbcTemplate jdbcCda) {
        this.jdbcCda = jdbcCda;
    }

    public BioModelResults(
            NamedParameterJdbcTemplate jdbcCda,
            int bm_db_id,
            String bm_allelicComposition,
            String bm_geneticBackground,
            String bm_zygosity,

            String bm_gf_acc,
            int bm_gf_db_id,

            String bm_allele_acc,
            int bm_allele_db_id,

            String bm_strain_acc,
            int bm_strain_db_id,

            String strain_acc,
            int strain_db_id,
            String strain_biotype_acc,
            int strain_biotype_db_id,
            String strain_name,

            String bs_external_id,
            int bs_db_id,
            String bs_sample_type_acc,
            int bs_sample_type_db_id,
            String bs_sample_group,
            int bs_organisation_id,
            int bs_production_center_id)
    {
        this.jdbcCda = jdbcCda;
        this.bm_db_id = bm_db_id;
        this.bm_allelicComposition = bm_allelicComposition;
        this.bm_geneticBackground = bm_geneticBackground;
        this.bm_zygosity = bm_zygosity;

        this.bm_strain_acc = bm_strain_acc;
        this.bm_strain_db_id = bm_strain_db_id;

        this.bm_gf_acc = bm_gf_acc;
        this.bm_gf_db_id = bm_gf_db_id;

        this.bm_allele_acc = bm_allele_acc;
        this.bm_allele_db_id = bm_allele_db_id;

        this.strain_acc = strain_acc;
        this.strain_db_id = strain_db_id;
        this.strain_biotype_acc = strain_biotype_acc;
        this.strain_biotype_db_id = strain_biotype_db_id;
        this.strain_name = strain_name;

        this.bs_external_id = bs_external_id;
        this.bs_db_id = bs_db_id;
        this.bs_sample_type_acc = bs_sample_type_acc;
        this.bs_sample_type_db_id = bs_sample_type_db_id;
        this.bs_sample_group = bs_sample_group;
        this.bs_organisation_id = bs_organisation_id;
        this.bs_production_center_id = bs_production_center_id;
    }

    public RunStatus diff(BioModelResults other) {
        RunStatus status = new RunStatus();
        if (bm_db_id != other.bm_db_id) { status.addWarning("bm_db_id mismatch: " + bm_db_id + "::" + other.bm_db_id); }
        if ( ! bm_allelicComposition.equals(other.bm_allelicComposition)) { status.addWarning("bm_allelicComposition mismatch: " + bm_allelicComposition + "::" + other.bm_allelicComposition); }
        if ( ! bm_geneticBackground.equals(other.bm_geneticBackground)) { status.addWarning("bm_geneticBackground mismatch: " + bm_geneticBackground + "::" + other.bm_geneticBackground); }
        if ( ! bm_zygosity.equals(other.bm_zygosity)) { status.addWarning("bm_zygosity mismatch: " + bm_zygosity + "::" + other.bm_zygosity); }
        if ( ! strain_acc.equals(other.strain_acc)) { status.addWarning("strain_acc mismatch: " + strain_acc + "::" + other.strain_acc); }
        if (strain_db_id != other.strain_db_id) { status.addWarning("strain_db_id mismatch: " + strain_db_id + "::" + other.strain_db_id); }
        if ( ! strain_biotype_acc.equals(other.strain_biotype_acc)) { status.addWarning("strain_biotype_acc mismatch: " + strain_biotype_acc + "::" + other.strain_biotype_acc); }
        if (strain_biotype_db_id != other.strain_biotype_db_id) { status.addWarning("strain_biotype_db_id mismatch: " + strain_biotype_db_id + "::" + other.strain_biotype_db_id); }
        if ( ! strain_name.equals(other.strain_name)) { status.addWarning("strain_name mismatch: " + strain_name + "::" + other.strain_name); }
        if ( ! bm_gf_acc.equals(other.bm_gf_acc)) { status.addWarning("gf_acc mismatch: " + bm_gf_acc + "::" + other.bm_gf_acc); }
        if (bm_gf_db_id != other.bm_gf_db_id) { status.addWarning("gf_db_id mismatch: " + bm_gf_db_id + "::" + other.bm_gf_db_id); }
        if ( ! bm_allele_acc.equals(other.bm_allele_acc)) { status.addWarning("allele_acc mismatch: " + bm_allele_acc + "::" + other.bm_allele_acc); }
        if (bm_allele_db_id != other.bm_allele_db_id) { status.addWarning("allele_db_id mismatch: " + bm_allele_db_id + "::" + other.bm_allele_db_id); }

        if (( bs_external_id == null) || (other.bs_external_id == null)) {
            if ((bs_external_id != null) && (other.bs_external_id != null)) {
                status.addWarning("Expected both sample external_ids to be null. this: " + bs_external_id + ". other: " + other.bs_external_id);
            }
        } else {
            if (!bs_external_id.equals(other.bs_external_id)) { status.addWarning("biosample_external_id mismatch: " + bs_external_id + "::" + other.bs_external_id); }
            if (bs_db_id != other.bs_db_id) { status.addWarning("biosample_db_id mismatch: " + bs_db_id + "::" + other.bs_db_id); }
            if (!bs_sample_type_acc.equals(other.bs_sample_type_acc)) { status.addWarning("biosample_sample_type_acc mismatch: " + bs_sample_type_acc + "::" + other.bs_sample_type_acc); }
            if (bs_sample_type_db_id != other.bs_sample_type_db_id) { status.addWarning("biosample_sample_type_db_id mismatch: " + bs_sample_type_db_id + "::" + other.bs_sample_type_db_id); }
            if (!bs_sample_group.equals(other.bs_sample_group)) { status.addWarning("biosample_sample_group mismatch: " + bs_sample_group + "::" + other.bs_sample_group); }
            if (bs_organisation_id != other.bs_organisation_id) { status.addWarning("biosample_organisation_id mismatch: " + bs_organisation_id + "::" + other.bs_organisation_id); }
        }

        return status;
    }

    public static BioModelResults query(int db_id, String allelicComposition, String geneticBackground, String zygosity) {

        BioModelResults result = new BioModelResults(jdbcCda);

        String query = "SELECT\n" +
                "  bm.db_id                  AS bm_db_id,\n" +
                "  bm.allelic_composition    AS bm_allelic_composition,\n" +
                "  bm.genetic_background     AS bm_genetic_background,\n" +
                "  bm.zygosity               AS bm_zygosity,\n" +
                "  \n" +
                "  bmgf.gf_acc               AS bm_gf_acc,\n" +
                "  bmgf.gf_db_id             AS bm_gf_db_id,\n" +
                "  \n" +
                "  bma.allele_acc            AS bm_allele_acc,\n" +
                "  bma.allele_db_id          AS bm_allele_db_id,\n" +
                "  \n" +
                "  bmstr.strain_acc          AS bm_strain_acc,\n" +
                "  bmstr.strain_db_id        AS bm_strain_db_id,\n" +
                "  \n" +
                "  s.acc                     AS strain_acc,\n" +
                "  s.db_id                   AS strain_db_id,\n" +
                "  s.biotype_acc             AS strain_biotype_acc,\n" +
                "  s.biotype_db_id           AS strain_biotype_db_id,\n" +
                "  s.name                    AS strain_name,\n" +
                "  \n" +
                "  bs.external_id            AS bs_external_id,\n" +
                "  bs.db_id                  AS bs_db_id,\n" +
                "  bs.sample_type_acc        AS bs_sample_type_acc,\n" +
                "  bs.sample_type_db_id      AS bs_sample_type_db_id,\n" +
                "  bs.sample_group           AS bs_sample_group,\n" +
                "  bs.organisation_id        AS bs_organisation_id,\n" +
                "  bs.production_center_id   AS bs_production_center_id\n" +
                "FROM biological_model bm\n" +
                "LEFT OUTER JOIN biological_model_strain bmstr ON bmstr.biological_model_id = bm.id\n" +
                "LEFT OUTER JOIN strain s ON s.acc = bmstr.strain_acc AND s.db_id = bmstr.strain_db_id\n" +
                "LEFT OUTER JOIN biological_model_sample bms ON bms.biological_model_id = bm.id\n" +
                "LEFT OUTER JOIN biological_sample bs ON bs.id = bms.biological_sample_id\n" +
                "LEFT OUTER JOIN biological_model_allele bma ON bma.biological_model_id = bm.id\n" +
                "LEFT OUTER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id = bm.id\n" +
                "WHERE\n" +
                "    bm.db_id               = :bm_db_id               AND\n" +
                "    bm.allelic_composition = :bm_allelic_composition AND\n" +
                "    bm.genetic_background  = :bm_genetic_background  AND\n" +
                "    bm.zygosity            = :bm_zygosity\n";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("bm_db_id", db_id);
        parameterMap.put("bm_allelic_composition", allelicComposition);
        parameterMap.put("bm_genetic_background", geneticBackground);
        parameterMap.put("bm_zygosity", zygosity);

        logger.debug(query);
        List<Map<String, Object>> listMap = jdbcCda.queryForList(query, parameterMap);

        for (Map<String, Object> map : listMap) {

            Object               o;

            // biological_model
            o = map.get("bm_db_id");
            result.bm_db_id = (o == null ? 0 : Integer.parseInt(o.toString()));

            o = map.get("bm_allelic_composition");
            result.bm_allelicComposition = (o == null ? "" : o.toString());

            o = map.get("bm_genetic_background");
            result.bm_geneticBackground = (o == null ? null : o.toString());

            o = map.get("bm_zygosity");
            result.bm_zygosity = (o == null ? null : o.toString());

            // biological_model_genomic_feature
            o = map.get("bm_gf_acc");
            result.bm_gf_acc = (o == null ? null : o.toString());

            o = map.get("bm_gf_db_id");
            result.bm_gf_db_id = (o == null ? 0 : Integer.parseInt(o.toString()));

            // biological_model_allele
            o = map.get("bm_allele_acc");
            result.bm_allele_acc = (o == null ? null : o.toString());

            o = map.get("bm_allele_db_id");
            result.bm_allele_db_id = (o == null ? 0 : Integer.parseInt(o.toString()));

            // biological_model_strain
            o = map.get("bm_strain_acc");
            result.bm_strain_acc = (o == null ? null : o.toString());

            o = map.get("bm_strain_db_id");
            result.bm_strain_db_id = (o == null ? 0 : Integer.parseInt(o.toString()));

            // strain
            o = map.get("strain_acc");
            result.strain_acc = (o == null ? null : o.toString());

            o = map.get("strain_db_id");
            result.strain_db_id = (o == null ? 0 : Integer.parseInt(o.toString()));

            o = map.get("strain_biotype_acc");
            result.strain_biotype_acc = (o == null ? null : o.toString());

            o = map.get("strain_biotype_db_id");
            result.strain_biotype_db_id = (o == null ? 0 : Integer.parseInt(o.toString()));

            o = map.get("strain_name");
            result.strain_name = (o == null ? null : o.toString());

            // biological_sample
            o = map.get("bs_external_id");
            result.bs_external_id = (o == null ? null :o.toString());

            o = map.get("bs_db_id");
            result.bs_db_id = (o == null ? 0 : Integer.parseInt(o.toString()));

            o = map.get("bs_sample_type_acc");
            result.bs_sample_type_acc = (o == null ? null : o.toString());

            o = map.get("bs_sample_type_db_id");
            result.bs_sample_type_db_id = (o == null ? 0 : Integer.parseInt(o.toString()));

            o = map.get("bs_sample_group");
            result.bs_sample_group = (o == null ? null : o.toString());

            o = map.get("bs_organisation_id");
            result.bs_organisation_id = (o == null ? 0 : Integer.parseInt(o.toString()));

            o = map.get("bs_production_center_id");
            result.bs_production_center_id = (o == null ? 0 : Integer.parseInt(o.toString()));
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BioModelResults that = (BioModelResults) o;

        if (bm_db_id != that.bm_db_id) return false;
        if (bm_strain_db_id != that.bm_strain_db_id) return false;
        if (bm_gf_db_id != that.bm_gf_db_id) return false;
        if (bm_allele_db_id != that.bm_allele_db_id) return false;
        if (strain_db_id != that.strain_db_id) return false;
        if (strain_biotype_db_id != that.strain_biotype_db_id) return false;
        if (bs_db_id != that.bs_db_id) return false;
        if (bs_sample_type_db_id != that.bs_sample_type_db_id) return false;
        if (bs_organisation_id != that.bs_organisation_id) return false;
        if (bs_production_center_id != that.bs_production_center_id) return false;
        if (bm_allelicComposition != null ? !bm_allelicComposition.equals(that.bm_allelicComposition) : that.bm_allelicComposition != null)
            return false;
        if (bm_geneticBackground != null ? !bm_geneticBackground.equals(that.bm_geneticBackground) : that.bm_geneticBackground != null)
            return false;
        if (bm_zygosity != null ? !bm_zygosity.equals(that.bm_zygosity) : that.bm_zygosity != null) return false;
        if (bm_strain_acc != null ? !bm_strain_acc.equals(that.bm_strain_acc) : that.bm_strain_acc != null)
            return false;
        if (bm_gf_acc != null ? !bm_gf_acc.equals(that.bm_gf_acc) : that.bm_gf_acc != null) return false;
        if (bm_allele_acc != null ? !bm_allele_acc.equals(that.bm_allele_acc) : that.bm_allele_acc != null)
            return false;
        if (strain_acc != null ? !strain_acc.equals(that.strain_acc) : that.strain_acc != null) return false;
        if (strain_biotype_acc != null ? !strain_biotype_acc.equals(that.strain_biotype_acc) : that.strain_biotype_acc != null)
            return false;
        if (strain_name != null ? !strain_name.equals(that.strain_name) : that.strain_name != null) return false;
        if (bs_external_id != null ? !bs_external_id.equals(that.bs_external_id) : that.bs_external_id != null)
            return false;
        if (bs_sample_type_acc != null ? !bs_sample_type_acc.equals(that.bs_sample_type_acc) : that.bs_sample_type_acc != null)
            return false;
        return bs_sample_group != null ? bs_sample_group.equals(that.bs_sample_group) : that.bs_sample_group == null;
    }

    @Override
    public int hashCode() {
        int result = bm_db_id;
        result = 31 * result + (bm_allelicComposition != null ? bm_allelicComposition.hashCode() : 0);
        result = 31 * result + (bm_geneticBackground != null ? bm_geneticBackground.hashCode() : 0);
        result = 31 * result + (bm_zygosity != null ? bm_zygosity.hashCode() : 0);
        result = 31 * result + (bm_strain_acc != null ? bm_strain_acc.hashCode() : 0);
        result = 31 * result + bm_strain_db_id;
        result = 31 * result + (bm_gf_acc != null ? bm_gf_acc.hashCode() : 0);
        result = 31 * result + bm_gf_db_id;
        result = 31 * result + (bm_allele_acc != null ? bm_allele_acc.hashCode() : 0);
        result = 31 * result + bm_allele_db_id;
        result = 31 * result + (strain_acc != null ? strain_acc.hashCode() : 0);
        result = 31 * result + strain_db_id;
        result = 31 * result + (strain_biotype_acc != null ? strain_biotype_acc.hashCode() : 0);
        result = 31 * result + strain_biotype_db_id;
        result = 31 * result + (strain_name != null ? strain_name.hashCode() : 0);
        result = 31 * result + (bs_external_id != null ? bs_external_id.hashCode() : 0);
        result = 31 * result + bs_db_id;
        result = 31 * result + (bs_sample_type_acc != null ? bs_sample_type_acc.hashCode() : 0);
        result = 31 * result + bs_sample_type_db_id;
        result = 31 * result + (bs_sample_group != null ? bs_sample_group.hashCode() : 0);
        result = 31 * result + bs_organisation_id;
        result = 31 * result + bs_production_center_id;
        return result;
    }

    public NamedParameterJdbcTemplate getJdbcCda() {
        return jdbcCda;
    }

    public void setJdbcCda(NamedParameterJdbcTemplate jdbcCda) {
        this.jdbcCda = jdbcCda;
    }

    public int getBm_db_id() {
        return bm_db_id;
    }

    public void setBm_db_id(int bm_db_id) {
        this.bm_db_id = bm_db_id;
    }

    public String getBm_allelicComposition() {
        return bm_allelicComposition;
    }

    public void setBm_allelicComposition(String bm_allelicComposition) {
        this.bm_allelicComposition = bm_allelicComposition;
    }

    public String getBm_geneticBackground() {
        return bm_geneticBackground;
    }

    public void setBm_geneticBackground(String bm_geneticBackground) {
        this.bm_geneticBackground = bm_geneticBackground;
    }

    public String getBm_zygosity() {
        return bm_zygosity;
    }

    public void setBm_zygosity(String bm_zygosity) {
        this.bm_zygosity = bm_zygosity;
    }

    public String getBm_strain_acc() {
        return bm_strain_acc;
    }

    public void setBm_strain_acc(String bm_strain_acc) {
        this.bm_strain_acc = bm_strain_acc;
    }

    public int getBm_strain_db_id() {
        return bm_strain_db_id;
    }

    public void setBm_strain_db_id(int bm_strain_db_id) {
        this.bm_strain_db_id = bm_strain_db_id;
    }

    public String getBm_gf_acc() {
        return bm_gf_acc;
    }

    public void setBm_gf_acc(String bm_gf_acc) {
        this.bm_gf_acc = bm_gf_acc;
    }

    public int getBm_gf_db_id() {
        return bm_gf_db_id;
    }

    public void setBm_gf_db_id(int bm_gf_db_id) {
        this.bm_gf_db_id = bm_gf_db_id;
    }

    public String getBm_allele_acc() {
        return bm_allele_acc;
    }

    public void setBm_allele_acc(String bm_allele_acc) {
        this.bm_allele_acc = bm_allele_acc;
    }

    public int getBm_allele_db_id() {
        return bm_allele_db_id;
    }

    public void setBm_allele_db_id(int bm_allele_db_id) {
        this.bm_allele_db_id = bm_allele_db_id;
    }

    public String getStrain_acc() {
        return strain_acc;
    }

    public void setStrain_acc(String strain_acc) {
        this.strain_acc = strain_acc;
    }

    public int getStrain_db_id() {
        return strain_db_id;
    }

    public void setStrain_db_id(int strain_db_id) {
        this.strain_db_id = strain_db_id;
    }

    public String getStrain_biotype_acc() {
        return strain_biotype_acc;
    }

    public void setStrain_biotype_acc(String strain_biotype_acc) {
        this.strain_biotype_acc = strain_biotype_acc;
    }

    public int getStrain_biotype_db_id() {
        return strain_biotype_db_id;
    }

    public void setStrain_biotype_db_id(int strain_biotype_db_id) {
        this.strain_biotype_db_id = strain_biotype_db_id;
    }

    public String getStrain_name() {
        return strain_name;
    }

    public void setStrain_name(String strain_name) {
        this.strain_name = strain_name;
    }

    public String getBs_external_id() {
        return bs_external_id;
    }

    public void setBs_external_id(String bs_external_id) {
        this.bs_external_id = bs_external_id;
    }

    public int getBs_db_id() {
        return bs_db_id;
    }

    public void setBs_db_id(int bs_db_id) {
        this.bs_db_id = bs_db_id;
    }

    public String getBs_sample_type_acc() {
        return bs_sample_type_acc;
    }

    public void setBs_sample_type_acc(String bs_sample_type_acc) {
        this.bs_sample_type_acc = bs_sample_type_acc;
    }

    public int getBs_sample_type_db_id() {
        return bs_sample_type_db_id;
    }

    public void setBs_sample_type_db_id(int bs_sample_type_db_id) {
        this.bs_sample_type_db_id = bs_sample_type_db_id;
    }

    public String getBs_sample_group() {
        return bs_sample_group;
    }

    public void setBs_sample_group(String bs_sample_group) {
        this.bs_sample_group = bs_sample_group;
    }

    public int getBs_organisation_id() {
        return bs_organisation_id;
    }

    public void setBs_organisation_id(int bs_organisation_id) {
        this.bs_organisation_id = bs_organisation_id;
    }

    public int getBs_production_center_id() {
        return bs_production_center_id;
    }

    public void setBs_production_center_id(int bs_production_center_id) {
        this.bs_production_center_id = bs_production_center_id;
    }
}