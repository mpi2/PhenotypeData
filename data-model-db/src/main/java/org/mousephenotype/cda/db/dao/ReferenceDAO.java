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

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.ReferenceDTO;
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
import java.util.Arrays;
import java.util.List;


@Repository
@Transactional
public class ReferenceDAO {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    public final String heading =
            "MGI allele symbol"
        + "\tMGI allele id"
        + "\tIMPC gene link"
        + "\tMGI allele name"
        + "\tTitle"
        + "\tjournal"
        + "\tPMID"
        + "\tDate of publication"
        + "\tGrant id"
        + "\tGrant agency"
        + "\tPaper link";



    @Autowired
    @Qualifier("admintoolsDataSource")
    private DataSource admintoolsDataSource;

    public ReferenceDAO() {

    }

    /**
     * Given a list of <code>ReferenceDTO</code> and a <code>pubMedId</code>,
     * returns the first matching ReferenceDTO matching pubMedId, if found; null
     * otherwise.
     *
     * @param references a list of <code>ReferenceDTO</code>
     *
     * @param pubMedId pub med ID
     *
     * @return the first matching ReferenceDTO matching pubMedId, if found; null
     * otherwise.
     *
     * @throws SQLException
     */
    public ReferenceDTO getReferenceByPmid(List<ReferenceDTO> references, Integer pubMedId
    ) throws SQLException {
        for (ReferenceDTO reference : references) {
            if (reference.getPmid() == pubMedId) {
                return reference;
            }
        }

        return null;
    }

    /**
     * Fetch all reference rows.
     *
     * @return all reference rows.
     *
     * @throws SQLException
     */
    public List<ReferenceDTO> getReferenceRows() throws SQLException {
        return getReferenceRows("");
    }

    /**
     * Fetch the reference rows, optionally filtered.
     *
     * @param filter Filter string, which may be null or empty, indicating no
     * filtering is desired. If supplied, a WHERE clause of the form "LIKE
     * '%<i>filter</i>%' is used in the query to query all fields for
     * <code>filter</code>.
     *
     * @return the reference rows, optionally filtered.
     *
     * @throws SQLException
     *
     */

    public List<ReferenceDTO> getReferenceRows(String filter, String orderBy) throws SQLException {
        Connection connection = admintoolsDataSource.getConnection();
        // need to set max length for group_concat() otherwise some values would get chopped off !!
//    	String gcsql = "SET SESSION GROUP_CONCAT_MAX_LEN = 100000000";
//
//    	PreparedStatement pst = connection.prepareStatement(gcsql);
//    	pst.executeQuery();

        if (filter == null)
            filter = "";

        String impcGeneBaseUrl = "http://www.mousephenotype.org/data/genes/";
        String pmidsToOmit = getPmidsToOmit();
        String notInClause = (pmidsToOmit.isEmpty() ? "" : "  AND pmid NOT IN (" + pmidsToOmit + ")\n");
        String searchClause = "";

        int colCount = 0;

        if ( ! filter.isEmpty()) {
            if ( filter.contains("|")){
                searchClause =
                        "  AND (\n"
                                + "(title LIKE ? or title LIKE ?)\n"
                                + " OR (mesh LIKE ? OR mesh LIKE ?))\n";
                colCount = 4;
            }
            else {
                colCount = 2;
                searchClause =
                        "  AND (\n"
                                + "     title               LIKE ?\n"
                                + " OR mesh                LIKE ?)\n";
            }
        }

        String whereClause =
                "WHERE\n"
                        + " reviewed = 'yes'\n"
                        + " AND falsepositive = 'no'"
                        + " AND symbol != ''\n"

                        // some paper are forced to be reviewed although no gacc and acc is known, but symbol will have been set as "Not available"
                        // + " AND gacc != ''\n"
                        // + " AND acc != ''\n"
                        + notInClause
                        + searchClause;

        //    + " AND pmid=24652767 "; // for test
        String query =
                "SELECT\n"
                        + "  symbol AS alleleSymbols\n"
                        + ", acc AS alleleAccessionIds\n"
                        + ", gacc AS geneAccessionIds\n"
                        + ", name AS alleleNames\n"
//              + "  GROUP_CONCAT( symbol    SEPARATOR \"|||\") AS alleleSymbols\n"
//              + ", GROUP_CONCAT( acc       SEPARATOR \"|||\") AS alleleAccessionIds\n"
//              + ", GROUP_CONCAT( gacc      SEPARATOR \"|||\") AS geneAccessionIds\n"
//              + ", GROUP_CONCAT( name      SEPARATOR \"|||\") AS alleleNames\n"
                        + ", title\n"
                        + ", journal\n"
                        + ", pmid\n"
                        + ", date_of_publication\n"
                        + ", grant_id AS grantIds\n"
                        + ", agency AS grantAgencies\n"
                        + ", paper_url AS paperUrls\n"
                        + ", mesh\n"
                        + ", author\n"
                        + "FROM allele_ref AS ar\n"
                        + whereClause
                        //+ "GROUP BY pmid\n"
                        + "ORDER BY " + orderBy + "\n";

        System.out.println("alleleRef query: " + query);
        List<ReferenceDTO> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if ( ! searchClause.isEmpty()) {
                // Replace the parameter holder ? with the values.

                String like1, like2 = null;
                if (filter.contains("|")){
                    String[] fltr = StringUtils.split(filter,"|");
                    like1 = "%" + fltr[0] + "%";
                    like2 = "%" + fltr[1] + "%";

                    for (int i = 0; i < colCount; i=i+2) {                                   // If a search clause was specified, load the parameters.
                        ps.setString(i + 1, like1);
                        ps.setString(i + 2, like2);
                    }
                }
                else {
                    like1 = "%" + filter + "%";
                    for (int i = 0; i < colCount; i++) {                                   // If a search clause was specified, load the parameters.
                        ps.setString(i + 1, like1);
                    }
                }
            }

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                final String delimeter = "\\|\\|\\|";
                ReferenceDTO referenceRow = new ReferenceDTO();

                referenceRow.setAlleleSymbols(Arrays.asList(resultSet.getString("alleleSymbols").split(delimeter)));
                referenceRow.setAlleleAccessionIds(Arrays.asList(resultSet.getString("alleleAccessionIds").split(delimeter)));
                String geneAccessionIds = resultSet.getString("geneAccessionIds").trim();
                List<String> geneLinks = new ArrayList();
                if ( ! geneAccessionIds.isEmpty()) {
                    referenceRow.setGeneAccessionIds(Arrays.asList(geneAccessionIds.split(delimeter)));
                    String[] parts = geneAccessionIds.split(delimeter);
                    for (String part : parts) {
                        geneLinks.add(impcGeneBaseUrl + part.trim());
                    }
                    referenceRow.setImpcGeneLinks(geneLinks);
                }
                referenceRow.setMgiAlleleNames(Arrays.asList(resultSet.getString("alleleNames").split(delimeter)));
                referenceRow.setTitle(resultSet.getString("title"));
                referenceRow.setJournal(resultSet.getString("journal"));
                referenceRow.setPmid(resultSet.getInt("pmid"));
                referenceRow.setDateOfPublication(resultSet.getString("date_of_publication"));
                referenceRow.setGrantIds(Arrays.asList(resultSet.getString("grantIds").split(delimeter)));
                referenceRow.setGrantAgencies(Arrays.asList(resultSet.getString("grantAgencies").split(delimeter)));
                referenceRow.setPaperUrls(Arrays.asList(resultSet.getString("paperUrls").split(delimeter)));
                referenceRow.setMeshTerms(Arrays.asList(resultSet.getString("mesh").split(delimeter)));
                referenceRow.setAuthor(resultSet.getString("author"));

                results.add(referenceRow);
            }
            resultSet.close();
            ps.close();
            connection.close();

        } catch (Exception e) {
            log.error("download rowData extract failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return results;
    }

     public List<ReferenceDTO> getReferenceRows(String filter) throws SQLException {

    	Connection connection = admintoolsDataSource.getConnection();
    	// need to set max length for group_concat() otherwise some values would get chopped off !!
//    	String gcsql = "SET SESSION GROUP_CONCAT_MAX_LEN = 100000000";
//
//    	PreparedStatement pst = connection.prepareStatement(gcsql);
//    	pst.executeQuery();

    	if (filter == null)
            filter = "";

        String impcGeneBaseUrl = "http://www.mousephenotype.org/data/genes/";
        String pmidsToOmit = getPmidsToOmit();
        String notInClause = (pmidsToOmit.isEmpty() ? "" : "  AND pmid NOT IN (" + pmidsToOmit + ")\n");
        String searchClause = "";

        int colCount = 0;

        if ( ! filter.isEmpty()) {
            if ( filter.contains("|")){
                searchClause =
                        "  AND (\n"
                                + "(title LIKE ? or title LIKE ?)\n"
                                + " OR (journal LIKE ? OR journal LIKE ?)\n"
                                + " OR (acc LIKE ? OR acc LIKE ?)\n"
                                + " OR (symbol LIKE ? OR symbol LIKE ?)\n"
                                + " OR (pmid LIKE ? OR pmid LIKE ?)\n"
                                + " OR (date_of_publication LIKE ? OR date_of_publication LIKE ?)\n"
                                + " OR (grant_id LIKE ? OR grant_id LIKE ?)\n"
                                + " OR (agency LIKE ? OR agency LIKE ?)\n"
                                + " OR (acronym LIKE ? OR acronym LIKE ?)\n"
                                + " OR (mesh LIKE ? OR mesh LIKE ?))\n";
                colCount = 20;
            }
            else {
                colCount = 10;
                searchClause =
                        "  AND (\n"
                                + "     title               LIKE ?\n"
                                + " OR journal             LIKE ?\n"
                                + " OR acc                 LIKE ?\n"
                                + " OR symbol              LIKE ?\n"
                                + " OR pmid                LIKE ?\n"
                                + " OR date_of_publication LIKE ?\n"
                                + " OR grant_id            LIKE ?\n"
                                + " OR agency              LIKE ?\n"
                                + " OR acronym             LIKE ?\n"
                                + " OR mesh                LIKE ?)\n";
            }
        }

        String whereClause =
                "WHERE\n"
              + " reviewed = 'yes'\n"
              + " AND falsepositive = 'no'"
              + " AND symbol != ''\n"

             // some paper are forced to be reviewed although no gacc and acc is known, but symbol will have been set as "Not available"
             // + " AND gacc != ''\n"
             // + " AND acc != ''\n"
              + notInClause
              + searchClause;

                    //    + " AND pmid=24652767 "; // for test
        String query =
                "SELECT\n"
              + "  symbol AS alleleSymbols\n"
              + ", acc AS alleleAccessionIds\n"
              + ", gacc AS geneAccessionIds\n"
              + ", name AS alleleNames\n"
//              + "  GROUP_CONCAT( symbol    SEPARATOR \"|||\") AS alleleSymbols\n"
//              + ", GROUP_CONCAT( acc       SEPARATOR \"|||\") AS alleleAccessionIds\n"
//              + ", GROUP_CONCAT( gacc      SEPARATOR \"|||\") AS geneAccessionIds\n"
//              + ", GROUP_CONCAT( name      SEPARATOR \"|||\") AS alleleNames\n"
              + ", title\n"
              + ", journal\n"
              + ", pmid\n"
              + ", date_of_publication\n"
              + ", grant_id AS grantIds\n"
              + ", agency AS grantAgencies\n"
              + ", paper_url AS paperUrls\n"
              + ", mesh\n"
              + "FROM allele_ref AS ar\n"
              + whereClause
              //+ "GROUP BY pmid\n"
              + "ORDER BY date_of_publication DESC\n";

        //System.out.println("alleleRef query: " + query);
        List<ReferenceDTO> results = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if ( ! searchClause.isEmpty()) {
                // Replace the parameter holder ? with the values.

                String like1, like2 = null;
                if (filter.contains("|")){
                    String[] fltr = StringUtils.split(filter,"|");
                    like1 = "%" + fltr[0] + "%";
                    like2 = "%" + fltr[1] + "%";

                    for (int i = 0; i < colCount; i=i+2) {                                   // If a search clause was specified, load the parameters.
                        ps.setString(i + 1, like1);
                        ps.setString(i + 2, like2);
                    }
                }
                else {
                    like1 = "%" + filter + "%";
                    for (int i = 0; i < colCount; i++) {                                   // If a search clause was specified, load the parameters.
                        ps.setString(i + 1, like1);
                    }
                }
            }

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                final String delimeter = "\\|\\|\\|";
                ReferenceDTO referenceRow = new ReferenceDTO();

                referenceRow.setAlleleSymbols(Arrays.asList(resultSet.getString("alleleSymbols").split(delimeter)));
                referenceRow.setAlleleAccessionIds(Arrays.asList(resultSet.getString("alleleAccessionIds").split(delimeter)));
                String geneAccessionIds = resultSet.getString("geneAccessionIds").trim();
                List<String> geneLinks = new ArrayList();
                if ( ! geneAccessionIds.isEmpty()) {
                    referenceRow.setGeneAccessionIds(Arrays.asList(geneAccessionIds.split(delimeter)));
                    String[] parts = geneAccessionIds.split(delimeter);
                    for (String part : parts) {
                        geneLinks.add(impcGeneBaseUrl + part.trim());
                    }
                    referenceRow.setImpcGeneLinks(geneLinks);
                }
                referenceRow.setMgiAlleleNames(Arrays.asList(resultSet.getString("alleleNames").split(delimeter)));
                referenceRow.setTitle(resultSet.getString("title"));
                referenceRow.setJournal(resultSet.getString("journal"));
                referenceRow.setPmid(resultSet.getInt("pmid"));
                referenceRow.setDateOfPublication(resultSet.getString("date_of_publication"));
                referenceRow.setGrantIds(Arrays.asList(resultSet.getString("grantIds").split(delimeter)));
                referenceRow.setGrantAgencies(Arrays.asList(resultSet.getString("grantAgencies").split(delimeter)));
                referenceRow.setPaperUrls(Arrays.asList(resultSet.getString("paperUrls").split(delimeter)));
                referenceRow.setMeshTerms(Arrays.asList(resultSet.getString("mesh").split(delimeter)));

                results.add(referenceRow);
            }
            resultSet.close();
            ps.close();
            connection.close();

        } catch (Exception e) {
            log.error("download rowData extract failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Returns a comma-separated, single-quoted list of pmid values to omit.
     * This is meant to be a filter to omit any common distinct papers. Rows
     * with more than MAX_ROWS distinct paper references are excluded from the
     * download row set.
     *
     * @return a comma-separated list of pmid values to omit.
     */
    public String getPmidsToOmit() throws SQLException {
        StringBuilder retVal = new StringBuilder();

        // Filter to omit any common distinct papers. Rows with more than MAX_ROWS distinct papers are excluded.
        final int MAX_PAPERS = 140;

        Connection connection = admintoolsDataSource.getConnection();
        String query = "SELECT pmid FROM allele_ref ar GROUP BY pmid HAVING (SELECT COUNT(symbol) FROM allele_ref ar2 WHERE ar2.pmid = ar.pmid) > " + MAX_PAPERS;

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (retVal.length() > 0) {
                    retVal.append(", ");
                }
                retVal.append("'").append(rs.getInt("pmid")).append("'");
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            log.error("getPmidsToOmit() call failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return retVal.toString();
    }
}
