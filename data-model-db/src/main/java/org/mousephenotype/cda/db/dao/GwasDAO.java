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
package org.mousephenotype.cda.db.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class GwasDAO {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Autowired
    @Qualifier("admintoolsDataSource")
    private DataSource admintoolsDataSource;

    public GwasDAO() {

    }

    /**
     * Fetch all gwas mapping rows filtered by mgi gene sysmbol.
     *
     * @return all gwas mapping rows filtered by mgi gene symbol
     * @throws SQLException
     */
    public List<GwasDTO> getGwasMappingByGeneSymbol(List<GwasDTO> gwasMappings, String mgiGeneSymbol) throws SQLException {

    	List<GwasDTO> mappedList = new ArrayList<>();

        for (GwasDTO gwasMapping : gwasMappings) {
            if (gwasMapping.getMgiGeneSymbol().equals(mgiGeneSymbol)) {
                mappedList.add(gwasMapping);
            }

            return mappedList;
        }

        return null;
    }

    /**
     * Fetch all detailed gwas mapping rows filtered by query.
     * @return all detailed gwas mapping rows filtered by query
     *
     * @throws SQLException
     */

    public List<GwasDTO> getGwasMappingDetailRows(String sql) throws SQLException {
    	Connection connection = admintoolsDataSource.getConnection();
    	// need to set max length for group_concat() otherwise some values would get chopped off !!
    	String query = "SELECT * FROM impc2gwas WHERE pheno_mapping_category != 'no mapping' AND " + sql;
    	//System.out.println("gwas mapping detail rows query: " + query);

         List<GwasDTO> results = new ArrayList<>();

         try (PreparedStatement ps = connection.prepareStatement(query)) {

        	 ResultSet resultSet = ps.executeQuery();
             while (resultSet.next()) {

                 GwasDTO gwasMappingRow = new GwasDTO();

                 gwasMappingRow.setMgiAlleleName(resultSet.getString("mgi_allele_name"));
                 gwasMappingRow.setMgiAlleleId(resultSet.getString("mgi_allele_id"));
                 gwasMappingRow.setMouseGender(resultSet.getString("impc_mouse_gender"));
                 gwasMappingRow.setPhenoMappingCategory(resultSet.getString("pheno_mapping_category"));
                 gwasMappingRow.setSnpId(resultSet.getString("gwas_snp_id"));
                 gwasMappingRow.setPvalue(resultSet.getFloat("gwas_p_value"));
                 gwasMappingRow.setReportedGene(resultSet.getString("gwas_reported_gene"));
                 gwasMappingRow.setMappedGene(resultSet.getString("gwas_mapped_gene"));
                 gwasMappingRow.setUpstreamGene(resultSet.getString("gwas_upstream_gene"));
                 gwasMappingRow.setDownstreamGene(resultSet.getString("gwas_downstream_gene"));

                 results.add(gwasMappingRow);
             }
             resultSet.close();
             ps.close();
             connection.close();

         } catch (Exception e) {
             log.error("Fetch IMPC GWAS mapping data failed: " + e.getLocalizedMessage());
             e.printStackTrace();
         }

         return results;

    }

    /**
     * Fetch all overview gwas mapping rows filtered by query.
     * The columns are mgi gene symbol, IMPC MP term, GWAS trait
     * @return all overview gwas mapping rows filtered by query
     *
     * @throws SQLException
     */
    public List<GwasDTO> getGwasMappingOverviewByQueryStr(String field, String value) throws SQLException {

    	Connection connection = admintoolsDataSource.getConnection();
    	// need to set max length for group_concat() otherwise some values would get chopped off !!

    	String gcsql = "SET SESSION GROUP_CONCAT_MAX_LEN = 100000000";
    	PreparedStatement pst = connection.prepareStatement(gcsql);
    	pst.executeQuery();

    	String whereClause = null;
        String query = null;
        String selectClause = "SELECT GROUP_CONCAT(distinct mgi_gene_symbol) AS mgi_gene_symbol, "
        		+ "GROUP_CONCAT(distinct mgi_gene_id) AS mgi_gene_id, "
        		+ "GROUP_CONCAT(distinct mp_term_name) AS mp_term_name, "
        		+ "GROUP_CONCAT(distinct gwas_disease_trait) AS gwas_disease_trait "
        		+ "FROM impc2gwas ";

        String groupBy = " GROUP BY gwas_disease_trait, mp_term_name";
        String orderBy = " ORDER BY mgi_gene_symbol";
        String andClause = " AND pheno_mapping_category != 'no mapping' AND mgi_gene_symbol != ''";

    	if ( field.equals("keyword") ){
        	whereClause =
                  "  WHERE (mgi_gene_id         LIKE ?\n"
                + "  OR mgi_gene_symbol         LIKE ?\n"
                + "  OR mgi_allele_id         	LIKE ?\n"
                + "  OR mgi_allele_name        	LIKE ?\n"
                + "  OR pheno_mapping_category 	LIKE ?\n"
                + "  OR gwas_disease_trait      LIKE ?\n"
                + "  OR gwas_p_value	        LIKE ?\n"
                + "  OR gwas_reported_gene      LIKE ?\n"
                + "  OR gwas_mapped_gene        LIKE ?\n"
                + "  OR gwas_upstream_gene      LIKE ?\n"
                + "  OR gwas_downstream_gene    LIKE ?\n"
                + "  OR mp_term_id              LIKE ?\n"
                + "  OR mp_term_name            LIKE ?\n"
                + "  OR impc_mouse_gender       LIKE ?\n"
                + "  OR gwas_snp_id             LIKE ?\n)"
                + andClause;

        	query = selectClause + whereClause + groupBy;
        }
        else if ( value.equals("*") ) {
            query = selectClause + " WHERE " + field + " LIKE '%' " + andClause + groupBy + orderBy;
        }
        else if ( ! value.isEmpty() ) {
        	query = selectClause + " WHERE " + field + " = ? " + andClause + groupBy + orderBy;
        }
        else {
            query = selectClause + " WHERE " + andClause + groupBy + orderBy;
        }

        //System.out.println("gwas mapping query: " + query);

        List<GwasDTO> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
        	if ( field.equals("keyword") ){
        		value = "%" + value + "%";
                //System.out.println("VALUE: " + value);
                ps.setString(1, value); // mgi_gene_id
        		ps.setString(2, value); // mgi_gene_symbol
        		ps.setString(3, value); // mgi_allele_id
        		ps.setString(4, value); // mgi_allele_name
        		ps.setString(5, value); // pheno_mapping_category
        		ps.setString(6, value); // gwas_disease_trait
        		ps.setFloat(7, Float.valueOf("-1")); // gwas_p_value, simply set to something that does not exist, we don't need this field
        		ps.setString(8, value);  // gwas_reported_gene
        		ps.setString(9, value); // gwas_mapped_gene
        		ps.setString(10, value); // gwas_upstream_gene
        		ps.setString(11, value); // gwas_downstream_gene
        		ps.setString(12, value); // mp_term_id
        		ps.setString(13, value); // mp_term_name
        		ps.setString(14, value); // impc_mouse_gender
        		ps.setString(15, value); // gwas_snp_id
        	}
        	else if ( ! value.isEmpty() && ! value.equals("*") ) {
                // Replace parameter holder ? with the value.
            	ps.setString(1, value);
            }

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {

                GwasDTO gwasMappingRow = new GwasDTO();

                gwasMappingRow.setMgiGeneId(resultSet.getString("mgi_gene_id"));
                gwasMappingRow.setMgiGeneSymbol(resultSet.getString("mgi_gene_symbol"));
                gwasMappingRow.setDiseaseTrait(resultSet.getString("gwas_disease_trait"));
                gwasMappingRow.setMpTermName(resultSet.getString("mp_term_name"));

                results.add(gwasMappingRow);
            }
            resultSet.close();
            ps.close();
            connection.close();

        } catch (Exception e) {
            log.error("Fetch IMPC GWAS mapping data failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return results;

    }

    /**
     * Fetch all gwas mapping rows.
     *
     * @return all gwas mapping rows.
     *
     * @throws SQLException
     */
    public List<GwasDTO> getGwasMappingRows() throws SQLException {
        return getGwasMappingRows("", "");
    }


    /**
     * Fetch the GWAS Mapping rows, optionally filtered with mgi_gene_symbol.
     *
     * @param String mgiGeneSymbol, which may be null or empty, indicating no
     * filtering by gene symbol.
     *
     * @return the GWAS Mapping rows, optionally filtered with mgi_gene_symbol
     *
     * @throws SQLException
     */
    public List<GwasDTO> getGwasMappingRows(String field, String value) throws SQLException {

    	Connection connection = admintoolsDataSource.getConnection();
    	// need to set max length for group_concat() otherwise some values would get chopped off !!

    	//String gcsql = "SET SESSION GROUP_CONCAT_MAX_LEN = 100000000";
    	//PreparedStatement pst = connection.prepareStatement(gcsql);
    	//pst.executeQuery();

        String whereClause = null;
        String query = null;
        String andClause = " AND pheno_mapping_category != 'no mapping' AND mgi_gene_symbol != ''";

        // only want gwas mapping that are either direct or indirect
        // the ones without mappings are from GWAS catalog and
        // are not of interest here
        if ( field.equals("keyword") ){
        	whereClause =
                  "  WHERE (mgi_gene_id          LIKE ?\n"
                + "  OR mgi_gene_symbol         LIKE ?\n"
                + "  OR mgi_allele_id         	LIKE ?\n"
                + "  OR mgi_allele_name        	LIKE ?\n"
                + "  OR pheno_mapping_category 	LIKE ?\n"
                + "  OR gwas_disease_trait      LIKE ?\n"
                + "  OR gwas_p_value	        LIKE ?\n"
                + "  OR gwas_reported_gene      LIKE ?\n"
                + "  OR gwas_mapped_gene        LIKE ?\n"
                + "  OR gwas_upstream_gene      LIKE ?\n"
                + "  OR gwas_downstream_gene    LIKE ?\n"
                + "  OR mp_term_id              LIKE ?\n"
                + "  OR mp_term_name            LIKE ?\n"
                + "  OR impc_mouse_gender       LIKE ?\n"
                + "  OR gwas_snp_id             LIKE ?\n)"
                + andClause;

        	query = "SELECT * FROM impc2gwas" + whereClause;
        }
        else if ( ! value.isEmpty() ) {
        	query = "SELECT * FROM impc2gwas WHERE " + field + " = ? " + andClause;
        }
        else {
        	query = "SELECT * FROM impc2gwas WHERE " + andClause;
        }

        //System.out.println("gwas mapping query: " + query);

        List<GwasDTO> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
        	if ( field.equals("keyword") ){
        		value = "%" + value + "%";
        		ps.setString(1, value);
        		ps.setString(2, value);
        		ps.setString(3, value);
        		ps.setString(4, value);
        		ps.setString(5, value);
        		ps.setString(6, value);
        		ps.setString(7, value);
        		ps.setFloat(8, Float.valueOf("-1"));  // simply set to something that does not exist, we don't need this field
        		ps.setString(9, value);
        		ps.setString(10, value);
        		ps.setString(11, value);
        		ps.setString(12, value);
        		ps.setString(13, value);
        		ps.setString(14, value);
        		ps.setString(15, value);
        		ps.setString(16, value);
        		ps.setString(17, value);
        	}
        	else if ( ! value.isEmpty()) {
                // Replace parameter holder ? with the value.
            	ps.setString(1, value);
            }

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {

                GwasDTO gwasMappingRow = new GwasDTO();

                gwasMappingRow.setMgiGeneId(resultSet.getString("mgi_gene_id"));
                gwasMappingRow.setMgiGeneSymbol(resultSet.getString("mgi_gene_symbol"));
                gwasMappingRow.setMgiAlleleId(resultSet.getString("mgi_allele_id"));
                gwasMappingRow.setMgiAlleleName(resultSet.getString("mgi_allele_name"));
                gwasMappingRow.setPhenoMappingCategory(resultSet.getString("pheno_mapping_category"));
                gwasMappingRow.setDiseaseTrait(resultSet.getString("gwas_disease_trait"));
                gwasMappingRow.setDiseaseTraitIdUrl(resultSet.getString("gwas_disease_trait_id_url"));
                gwasMappingRow.setPvalue(resultSet.getFloat("gwas_p_value"));
                gwasMappingRow.setReportedGene(resultSet.getString("gwas_reported_gene"));
                gwasMappingRow.setMappedGene(resultSet.getString("gwas_mapped_gene"));
                gwasMappingRow.setUpstreamGene(resultSet.getString("gwas_upstream_gene"));
                gwasMappingRow.setDownstreamGene(resultSet.getString("gwas_downstream_gene"));
                gwasMappingRow.setMpTermId(resultSet.getString("mp_term_id"));
                gwasMappingRow.setMpTermIdUrl(resultSet.getString("mp_term_id_url"));
                gwasMappingRow.setMpTermName(resultSet.getString("mp_term_name"));
                gwasMappingRow.setMouseGender(resultSet.getString("impc_mouse_gender"));
                gwasMappingRow.setSnpId(resultSet.getString("gwas_snp_id"));

                results.add(gwasMappingRow);
            }
            resultSet.close();
            ps.close();
            connection.close();

        } catch (Exception e) {
            log.error("Fetch IMPC GWAS mapping data failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return results;
    }

}
