/*******************************************************************************
 *  Copyright © 2017 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.ri.utils;

import org.mousephenotype.cda.ri.entities.*;
import org.mousephenotype.cda.ri.entities.report.GenesOfInterestByPopularity;
import org.mousephenotype.cda.ri.exceptions.InterestException;
import org.mousephenotype.cda.ri.rowmappers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 12/05/2017.
 */
public class RiSqlUtils {
    private static final Logger logger = LoggerFactory.getLogger(RiSqlUtils.class);

    private static final Integer INITIAL_POOL_CONNECTIONS = 1;


    @NotNull
    private NamedParameterJdbcTemplate jdbcInterest;
    
    @Inject
    public RiSqlUtils(NamedParameterJdbcTemplate jdbcInterest) {
        this.jdbcInterest = jdbcInterest;
    }


    /**
     * Add {@code role} to {@code emailAddress}
     *
     * @param emailAddress The email addresss to be inserted
     * @param role The role to be added
     * @throws {@link InterestException}
     */
    public void addRole(String emailAddress, RIRole role) throws InterestException {

        String message;

        Contact contact = getContact(emailAddress);
        if (contact == null) {
            message = "addRole(): Invalid contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, InterestStatus.NOT_FOUND);
        }

        final String insert = "INSERT INTO contact_role (contact_pk, role, created_at)" +
                              "VALUES (:contactPk, :role, :createdAt)";

        Date createdAt = new Date();

        Map<String, Object> parameterMap = new HashMap<>();
        try {
            parameterMap.put("contactPk", contact.getPk());
            parameterMap.put("role", role.toString());
            parameterMap.put("createdAt", createdAt);

            int rowCount = jdbcInterest.update(insert, parameterMap);
            if (rowCount < 1) {
                message = "Unable to add role " + role.toString() + " for contact " + emailAddress + ".";
                logger.error(message);
                throw new InterestException(message, InterestStatus.INTERNAL_ERROR);
            }

        } catch (Exception e) {

            message = "Error inserting contact_role for " + emailAddress + ": " + e.getLocalizedMessage();
            logger.error(message);
            throw new InterestException(message, InterestStatus.INTERNAL_ERROR);
        }
    }

    /**
     * Determine if a column exists in a table specific for a MySQL database
     *
     * @param connection the connection to use to query the database
     * @param tableName the table to
     * @param columnName the column in the table
     * @return true = column name exists in table, false = column missing from table
     * @throws SQLException on db access error
     */
    public Boolean columnInSchemaMysql(Connection connection, String tableName, String columnName) throws SQLException {

        Boolean found = Boolean.FALSE;
        String columnQuery = "SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=(SELECT database()) AND TABLE_NAME=? AND column_name=?";

        try (PreparedStatement p = connection.prepareStatement(columnQuery)) {
            p.setString(1, tableName);
            p.setString(2, columnName);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                found = Boolean.TRUE;
            }
        }

        return found;
    }

    /**
     * Within the scope of a single transaction, insert the contact, then the default USER role
     *
     * @param emailAddress The email addresss to be inserted
     * @param encryptedPassword The encrypted password to be inserted
     * @throws InterestException (HttpStatus.INTERNAL_SERVER_ERROR if either the email address or the role already exists
     */
    @Transactional
    public void createAccount(String emailAddress, String encryptedPassword) throws InterestException {

        final String ERROR_MESSAGE = "We were unable to register you with the specified e-mail address. Please contact the EBI mouse informatics helpdesk for assistance.";
        final String insert = "INSERT INTO contact(address, password, password_expired, account_locked, created_at) " +
                              "VALUES (:address, :password, :passwordExpired, :accountLocked, :createdAt)";
        int pk;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("address", emailAddress);
        parameterMap.put("password", encryptedPassword);
        parameterMap.put("passwordExpired", 0);
        parameterMap.put("accountLocked", 0);
        parameterMap.put("createdAt", new Date());

        try {

            // Insert the contact.
            KeyHolder          keyholder       = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            pk = jdbcInterest.update(insert, parameterSource, keyholder);
            if (pk < 1) {
                logger.error("Contact {} already exists.", emailAddress);
                throw new InterestException(ERROR_MESSAGE, InterestStatus.EXISTS);
            }

            // Add the USER role to the database.
            addRole(emailAddress, RIRole.USER);

        } catch (Exception e) {

            logger.error("Exception adding contact {}. Reason: {}", emailAddress, e.getLocalizedMessage());
            throw new InterestException(ERROR_MESSAGE, InterestStatus.INTERNAL_ERROR);
        }
    }

    public void createSpringBatchTables(DataSource datasource) {

        logger.info("Creating SPRING BATCH tables");
        org.springframework.core.io.Resource r = new ClassPathResource("org/springframework/batch/core/schema-mysql.sql");
        ResourceDatabasePopulator p = new ResourceDatabasePopulator(r);
        p.execute(datasource);
    }

    public void deleteAllContactGenes() {
        String delete = "DELETE FROM contact_gene";
        Map<String, Object> parameterMap = new HashMap<>();
        jdbcInterest.update(delete, parameterMap);
    }

    public void deleteAllContactRoles() {
        String delete = "DELETE FROM contact_role";
        Map<String, Object> parameterMap = new HashMap<>();
        jdbcInterest.update(delete, parameterMap);
    }

    public void deleteAllContacts() {
        String delete = "DELETE FROM contact";
        Map<String, Object> parameterMap = new HashMap<>();
        jdbcInterest.update(delete, parameterMap);
    }

    @Transactional
    public void deleteContact(String emailAddress) throws InterestException {

        String message;

        Contact contact = getContact(emailAddress);
        if (contact == null) {
            message = "deleteContact(): Invalid contact " + emailAddress + ".";
            logger.warn(message);
            throw new InterestException(message, InterestStatus.NOT_FOUND);
        }

        String delete;
        Map<String, Object> parameterMap = new HashMap<>();

        // Delete all matching emailAddress from contact_gene.
        delete = "DELETE FROM contact_gene WHERE contact_pk = :contactPk";
        parameterMap.put("contactPk", contact.getPk());
        jdbcInterest.update(delete, parameterMap);

        // Delete all matching emailAddress from gene_sent (GDPR).
        delete = "DELETE FROM gene_sent WHERE address = :address";
        parameterMap.put("address", contact.getEmailAddress());
        jdbcInterest.update(delete, parameterMap);

        // Delete all matching emailAddress from reset_credentials
        deleteResetCredentialsByEmailAddress(emailAddress);

        // Delete all matching emailAddress from contact_role.
        delete = "DELETE FROM contact_role WHERE contact_pk = :contactPk";
        jdbcInterest.update(delete, parameterMap);

        // Delete all matching emailAddress from contact
        delete = "DELETE FROM contact WHERE pk = :contactPk";
        jdbcInterest.update(delete, parameterMap);
    }


    public void deleteGeneSentByEmailAddress(String emailAddress) {
        String delete = "DELETE FROM gene_sent WHERE address = :address";
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("address", emailAddress);
        jdbcInterest.update(delete, parameterMap);
    }


    /**
     * Delete the row in reset_credentials identified by {@code emailAddress}. If no such email address exists, no
     * rows are deleted.
     *
     * @param emailAddress key to use for delete
     *
     * @return the number of rows deleted
     */
    public int deleteResetCredentialsByEmailAddress(String emailAddress) {

        int rowsDeleted = 0;

        String delete = "DELETE FROM reset_credentials WHERE email_address = :emailAddress";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", emailAddress);

        try {
            rowsDeleted = jdbcInterest.update(delete, parameterMap);
        } catch (Exception e) {
            // Ignore any errors
        }

        return rowsDeleted;
    }

    /**
     * Delete {@code role} from contact identified by {@code emailAddress}
     *
     * @param emailAddress The email address
     * @param role the role
     *
     * @throws {@link InterestException}.NOT_FOUND if either the email address doesn't exist or an internal error
     *         occurrs
     */
    public void deleteRole(String emailAddress, RIRole role) throws InterestException {

        String message;

        Contact contact = getContact(emailAddress);
        if (contact == null) {
            message = "deleteRole(): Invalid contact " + emailAddress + ".";
            logger.warn(message);
            throw new InterestException(message, InterestStatus.NOT_FOUND);
        }

        final String delete = "DELETE FROM contact_role WHERE contact_pk = :contactPk AND role = :role";

        Map<String, Object> parameterMap = new HashMap<>();

        try {
            parameterMap.put("contactPk", contact.getPk());
            parameterMap.put("role", role.toString());

            jdbcInterest.update(delete, parameterMap);

        } catch (Exception e) {

            message = "Error deleting contact_role for " + emailAddress + ": " + e.getLocalizedMessage();
            logger.error(message);
            throw new InterestException(message, InterestStatus.INTERNAL_ERROR);
        }
    }

    /**
     *
     * @return a list of all summaries, keyed by email address
     */
    public Map<String, Summary> getAllSummariesByEmailAddress() {

        Map<String, Summary> allSummaries = new HashMap<>();

        final String selectContact = "SELECT * FROM contact";

        Map<String, Object> parameterMap = new HashMap<>();

        List<Contact> contacts = jdbcInterest.query(selectContact, parameterMap, new ContactRowMapper());
        for (Contact contact : contacts) {

            parameterMap.put("address", contact.getEmailAddress());
            List<Gene> genes = getGenesByEmailAddress(contact.getEmailAddress());

            Summary summary = new Summary();
            summary.setEmailAddress(contact.getEmailAddress());
            summary.setGenes(genes);

            allSummaries.put(contact.getEmailAddress(), summary);
        }

        return allSummaries;
    }

    /**
     * Return the  {@link Contact}
     *
     * @param emailAddress the email address of the desired contact
     *
     * @return the {@link Contact} matching the emailAddress if found; null otherwise
     */
    public Contact getContact(String emailAddress) {

        final String query = "SELECT * FROM contact WHERE address = :emailAddress";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", emailAddress);

        List<Contact> contacts = jdbcInterest.query(query, parameterMap, new ContactRowMapper());
        Contact contact = (contacts.isEmpty() ? null : contacts.get(0));
        if (contact != null) {
            contact.setRoles(getContactRoles(emailAddress));
        }

        return contact;
    }

    /**
     *
     * @param address the email address of interest
     * @param gene The mgi accession id of the gene of interest
     * @return the {@link ContactGene} instance, if found; null otherwise
     */
    public ContactGene getContactGene(String address, String gene) {
        String query =
                "SELECT * FROM contact_gene cg\n" +
                        "JOIN gene g ON g.pk = cg.gene_pk\n" +
                        "JOIN contact c ON c.pk = cg.contact_pk\n" +
                        "WHERE g.mgi_accession_id = :gene AND c.address = :address";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("address", address);
        parameterMap.put("gene", gene);

        List<ContactGene> list = jdbcInterest.query(query, parameterMap, new ContactGeneRowMapper());

        return (list.isEmpty() ? null : list.get(0));
    }

    /**
     *
     * @return a list of {@link ContactGeneReportRow}. Needed by iMits.
     */
    public List<ContactGeneReportRow> getContactGeneReportRow() {
        List<ContactGeneReportRow> report;

        final String query = "SELECT\n" +
                "  c.address          AS contact_email,\n" +
                "  c.created_at       AS contact_created_at,\n" +
                "  g.symbol           AS marker_symbol,\n" +
                "  g.mgi_accession_id AS mgi_accession_id,\n" +
                "  cg.created_at      AS gene_interest_created_at\n" +
                "FROM contact c\n" +
                "JOIN contact_gene cg ON cg.contact_pk = c.pk\n" +
                "JOIN gene         g  ON g.pk = cg.gene_pk\n" +
                "ORDER BY c.address, g.symbol;";

        Map<String, Object> parameterMap = new HashMap<>();
        report = jdbcInterest.query(query, parameterMap, new ContactGeneReportRowRowMapper());

        return report;
    }

    /**
     *
     * @return a {@link List} of all {@link ContactGene} entries
     */
    public List<ContactGene> getContactGenes() {
        final String query = "SELECT * FROM contact_gene";

        Map<String, Object> parameterMap = new HashMap<>();

        return jdbcInterest.query(query, parameterMap, new ContactGeneRowMapper());
    }

    /**
     * @param emailAddress The contact for which the gene list is desired
     * @return a {@link List} of all {@link ContactGene} entries
     */
    public List<ContactGene> getContactGenes(String emailAddress) {
        final String query = "SELECT * FROM contact_gene";

        Map<String, Object> parameterMap = new HashMap<>();

        return jdbcInterest.query(query, parameterMap, new ContactGeneRowMapper());
    }

    public Collection<GrantedAuthority> getContactRoles(String emailAddress) {

        ArrayList<GrantedAuthority> roles = new ArrayList<>();

        final String query = "SELECT cr.* FROM contact_role cr " +
                "JOIN contact c ON c.pk = cr.contact_pk " +
                "WHERE c.address = :emailAddress";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", emailAddress);

        List<ContactRole> results = jdbcInterest.query(query, parameterMap, new ContactRoleRowMapper());
        for (ContactRole result : results) {
            roles.add(result.getAuthority());
        }

        return roles;
    }

    public List<Contact> getContacts() {
        final String query = "SELECT * FROM contact";

        Map<Integer, Contact> contactMap = new HashMap<>();
        List<Contact> contactList = jdbcInterest.query(query, new HashMap<String, Object>(), new ContactRowMapper());

        return contactList;
    }

    public Map<Integer, Contact> getContactsByPk() {
        final String query = "SELECT * FROM contact";

        Map<Integer, Contact> contactMap = new HashMap<>();
        List<Contact> contactList = jdbcInterest.query(query, new HashMap<String, Object>(), new ContactRowMapper());
        for (Contact contact : contactList) {
            contactMap.put(contact.getPk(), contact);
        }

        return contactMap;
    }

    /**
     *
     * @return a list of the contact primary keys for every contact who is registered for interest in one or more genes
     */
    public List<Integer> getContactsWithRegistrations() {

        String contactPkQuery =
                "SELECT DISTINCT\n" +
                        "  c.pk\n" +
                        "FROM contact_gene cg\n" +
                        "JOIN contact      c  ON c.pk = cg.contact_pk\n" +
                        "ORDER BY c.pk";

        List<Integer> contactPks = jdbcInterest.queryForList(contactPkQuery, new HashMap<String, Object>(), Integer.class);

        return contactPks;
    }

    public Map<Integer, String> getEmailAddressesByContactGenePk() {
        Map<Integer, String> results = new HashMap<>();
        final String query =
                "SELECT cg.pk, c.address\n" +
                "FROM contact c\n" +
                "JOIN contact_gene cg ON cg.contact_pk      = c. pk\n" +
                "JOIN gene_sent    gs ON gs.contact_gene_pk = cg.pk";

        List<Map<String, Object>> listMap = jdbcInterest.queryForList(query, new HashMap<>());
        for (Map<String, Object> map : listMap) {
            int pk = (Integer)map.get("pk");
            String address = map.get("address").toString();
            results.put(pk, address);
        }

        return results;
    }

    /**
     * Return the {@link Gene}
     *
     * @param mgiAccessionId the MGI accession id of the desired gene
     *
     * @return the {@link Gene} matching the mgiAccessionId if found; null otherwise
     */
    public Gene getGene(String mgiAccessionId) {

        final String query = "SELECT * FROM gene WHERE mgi_accession_id = :mgi_accession_id";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("mgi_accession_id", mgiAccessionId);

        List<Gene> genes = jdbcInterest.query(query, parameterMap, new GeneRowMapper());

        return (genes.isEmpty() ? null : genes.get(0));
    }

    /**
     * @return A map of all genes, indexed by mgi accession id.
     */
    public Map<String, Gene> getGenesByGeneAccessionId() {

        Map<String, Gene> genes = new HashMap<>();

        final String query = "SELECT * FROM gene";

        Map<String, Object> parameterMap = new HashMap<>();

        List<Gene> genesList = jdbcInterest.query(query, parameterMap, new GeneRowMapper());
        for (Gene gene : genesList) {
            genes.put(gene.getMgiAccessionId(), gene);
        }

        return genes;
    }

    /**
     *
     * @return A map, indexed by contact primary key, of all genes assigned to each contact
     */
    public Map<Integer, List<Gene>> getGenesByContactPk() {

        Map<Integer, List<Gene>> genesByContactMap = new ConcurrentHashMap<>();

        String query =
                "SELECT DISTINCT\n" +
                        "  g.*\n" +
                        "FROM contact_gene cg\n" +
                        "JOIN gene         g  ON g.pk = cg.gene_pk\n" +
                        "WHERE cg.contact_pk = :contactPk\n" +
                        "ORDER BY g.symbol";

        Map<String, Object> parameterMap = new HashMap<>();

        List<Integer> contactPks = getContactsWithRegistrations();
        for (Integer contactPk : contactPks) {

            parameterMap.put("contactPk", contactPk);
            List<Gene> genes = jdbcInterest.query(query, parameterMap, new GeneRowMapper());
            genesByContactMap.put(contactPk, genes);
        }

        return genesByContactMap;
    }

    /**
     * @return A map of all genes, indexed by primary key.
     */
    public Map<Integer, Gene> getGenesByPk() {

        Map<Integer, Gene> genes = new HashMap<>();

        final String query = "SELECT * FROM gene";

        Map<String, Object> parameterMap = new HashMap<>();

        List<Gene> genesList = jdbcInterest.query(query, parameterMap, new GeneRowMapper());
        for (Gene gene : genesList) {
            genes.put(gene.getPk(), gene);
        }

        return genes;
    }

    public GeneSent getGeneSent(String emailAddress, String mgiAccessionId) {
        GeneSent geneSent = null;
        final String query = "SELECT * FROM gene_sent WHERE address = :address AND mgi_accession_id = :mgiAccessionId";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("address", emailAddress);
        parameterMap.put("mgiAccessionId", mgiAccessionId);

        List<GeneSent> geneSentList = jdbcInterest.query(query, parameterMap, new GeneSentRowMapper());
        if ( ! geneSentList.isEmpty()) {
            geneSent = geneSentList.get(0);
        }

        return geneSent;
    }

    /**
     * Return a map of {@link GeneSent} statuses, indexed by mgi_accession_id, matching the given {@code address}
     * @param emailAddress the contact e-mail address
     * @return A map, indexed by mgi_accession_id, containing the {@link GeneSent} statuses ONLY, any or all of which
     * may be null:
     * <ul>
     *     <li>assignment_status</li>
     *     <li>conditional_allele_production_status</li>
     *     <li>null_allele_production_status</li>
     *     <li>phenotyping_status</li>
     * </ul>
     */
    public Map<String, GeneSent> getGeneSentStatusByGeneAccessionId(String emailAddress) {

        HashMap<String, GeneSent> results = new HashMap<>();

        final String query =
                "SELECT\n" +
                "  g.symbol,\n" +
                "  g.mgi_accession_id,\n" +
                "  gs.assignment_status,\n" +
                "  gs.null_allele_production_status,\n" +
                "  gs.conditional_allele_production_status,\n" +
                "  gs.phenotyping_status,\n" +
                "  gs.created_at,\n" +
                "  gs.sent_at,\n" +
                "  gs.updated_at\n" +
                "FROM contact c\n" +
                "JOIN contact_gene cg ON cg.contact_pk = c.pk\n" +
                "JOIN gene g ON g.pk = cg.gene_pk\n" +
                "JOIN gene_sent gs ON gs.address = c.address and gs.mgi_accession_id = g.mgi_accession_id\n" +
                "WHERE c.address = :address\n" +
                "ORDER BY symbol";


        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("address", emailAddress);


        List<Map<String, Object>> resultsMap = jdbcInterest.queryForList(query, parameterMap);
        for (Map<String, Object> result : resultsMap) {
            GeneSent geneSent = new GeneSent();
            geneSent.setMgiAccessionId(result.get("mgi_accession_id").toString());

            geneSent.setAssignmentStatus((String) result.get("assignment_status"));
            geneSent.setConditionalAlleleProductionStatus((String) result.get("conditional_allele_production_status"));
            geneSent.setNullAlleleProductionStatus((String) result.get("null_allele_production_status"));
            geneSent.setPhenotypingStatus((String) result.get("phenotyping_status"));
            geneSent.setCreatedAt((Date) result.get("created_at"));
            geneSent.setSentAt((Date) result.get("sent_at"));
            geneSent.setUpdatedAt((Date) result.get("updated_at"));
            results.put(geneSent.getMgiAccessionId(), geneSent);
        }

        return results;
    }

    /**
     *
     * @param emailAddress contact email address to filter by
     * @return The list of genes the contact has registered interest in
     */
    public List<Gene> getGenesByEmailAddress(String emailAddress) {

        String select =
               "SELECT\n" +
                       "  g.*\n" +
                       "FROM contact_gene cg\n" +
                       "JOIN contact c ON c.pk = cg.contact_pk\n" +
                       "JOIN gene    g ON g.pk = cg.gene_pk\n" +
                       "WHERE c.address = :address\n" +
                       "ORDER BY g.symbol";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("address", emailAddress);

        return jdbcInterest.query(select, parameterMap, new GeneRowMapper());
    }

    /**
     * @return A {@link List} of
     */
    public List<GenesOfInterestByPopularity> getGenesOfInterestByPopularity() {

        final String query =
                "SELECT\n" +
                        "  g.mgi_accession_id,\n" +
                        "  g.symbol,\n" +
                        "  g.assigned_to,\n" +
                        "  g.assignment_status,\n" +
                        "  g.assignment_status_date,\n" +
                        "  count(g.symbol) AS subscriber_count,\n" +
                        "  GROUP_CONCAT(c.address, \"::\", cg.created_at ORDER BY cg.created_at SEPARATOR ' | ') AS subscribers\n" +
                        "FROM gene g\n" +
                        "JOIN contact_gene cg ON cg.gene_pk = g. pk\n" +
                        "JOIN contact      c  ON c. pk      = cg.contact_pk\n" +
                        "GROUP BY g.symbol\n" +
                        "ORDER BY count(address) DESC, g.symbol";

        Map<String, Object> parameterMap = new HashMap<>();

        List<GenesOfInterestByPopularity> genes = jdbcInterest.query(query, parameterMap, new GenesOfInterestByPopularityRowMapper());

        return genes;
    }

    /**
     * @return a {@link Map} of all imits status, indexed by status (i.e. name)
     */
    public Map<String, ImitsStatus> getImitsStatusByStatus() {

        Map<String, ImitsStatus> imitsStatusByStatus = new HashMap<>();
        String query = "SELECT * FROM imits_status";

        Map<String, Object> parameterMap = new HashMap<>();

        List<ImitsStatus> imitsStatusList = jdbcInterest.query(query, parameterMap, new ImitsStatusRowMapper());
        for (ImitsStatus imitsStatus : imitsStatusList) {
            imitsStatusByStatus.put(imitsStatus.getStatus(), imitsStatus);
        }

        return imitsStatusByStatus;
    }

    /**
     * @param emailAddress the email address to match
     * @return the most recent {@link ResetCredentials} instance matching {@code emailAddress}, if found; null otherwise
     */
    public ResetCredentials getResetCredentialsByEmail(String emailAddress) {

        String query =
                "SELECT * FROM reset_credentials WHERE email_address = :emailAddress";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", emailAddress);

        List<ResetCredentials> list = jdbcInterest.query(query, parameterMap, new ResetCredentialsRowMapper());

        return (list.isEmpty() ? null : list.get(0));
    }

    /**
     * @param token the token to match
     * @return the most recent {@link ResetCredentials} instance matching {@code token}, if found; null otherwise
     */
    public ResetCredentials getResetCredentials(String token) {

        String query =
                "SELECT * FROM reset_credentials WHERE token = :token";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("token", token);

        List<ResetCredentials> list = jdbcInterest.query(query, parameterMap, new ResetCredentialsRowMapper());

        return (list.isEmpty() ? null : list.get(0));
    }

    public Summary getSummary(String emailAddress) {
        final String query =
                "SELECT g.* FROM gene g\n" +
                        "LEFT OUTER JOIN contact_gene cg ON cg.gene_pk = g.pk\n" +
                        "JOIN contact      c  ON c. pk      = cg.contact_pk\n" +
                        "WHERE c.address = :emailAddress\n" +
                        "ORDER BY g.symbol";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", emailAddress);

        Summary summary = new Summary();
        summary.setEmailAddress(emailAddress);
        List<Gene> genes = jdbcInterest.query(query, parameterMap, new GeneRowMapper());
        summary.setGenes(genes);

        return summary;
    }

    /**
     * Register {@code geneAccessionId} to {@code emailAddress}
     *
     * @param emailAddress contact email address
     * @param geneAccessionId gene accession id
     *
     * @throws InterestException if either {@code emailAddress} or {@code geneAccessionId} doesn't exist, or if contact
     *                           is already registered for this gene.
     */
    @Transactional
    public void registerGene(String emailAddress, String geneAccessionId) throws InterestException {

        String message;

        Gene gene = getGene(geneAccessionId);
        if (gene == null) {
            message = "Invalid gene " + geneAccessionId + ".";
            logger.error(message);
            throw new InterestException(message, InterestStatus.NOT_FOUND);
        }

        Contact contact = getContact(emailAddress);
        if (contact == null) {
            message = "Invalid contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, InterestStatus.NOT_FOUND);
        }

        Date now = new Date();
        String insert = "INSERT INTO contact_gene (contact_pk, gene_pk, created_at) VALUES " +
                "(:contactPk, :genePk, :createdAt)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("contactPk", contact.getPk());
        parameterMap.put("genePk", gene.getPk());
        parameterMap.put("createdAt", now);

        try {

            jdbcInterest.update(insert, parameterMap);
            updateGeneSent(emailAddress, gene);

        } catch (DuplicateKeyException e) {

            message = "Contact " + emailAddress + " is already registered for gene " + geneAccessionId + ".";
            logger.warn(message);
        }
    }

    /**
     * Unregister {@code geneAccessionId} from {@code emailAddress}
     *
     * @param emailAddress contact email address
     * @param geneAccessionId gene accession id
     *
     * @throws InterestException if either {@code emailAddress} or {@code geneAccessionId} doesn't exist, or if contact
     *                           is not registered for this gene.
     */
    @Transactional
    public void unregisterGene(String emailAddress, String geneAccessionId) throws InterestException {

        String message;

        Gene gene = getGene(geneAccessionId);
        if (gene == null) {
            message = "Invalid gene " + geneAccessionId + ".";
            logger.error(message);
            throw new InterestException(message, InterestStatus.NOT_FOUND);
        }

        Contact contact = getContact(emailAddress);
        if (contact == null) {
            message = "Invalid contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, InterestStatus.NOT_FOUND);
        }

        // If the contact has not registered for this gene, return appropriate HttpStatus.
        if (getContactGene(emailAddress, geneAccessionId) == null) {
            message = "Contact " + emailAddress + " is not registered for gene " + geneAccessionId + ".";
            logger.error(message);
            throw new InterestException(message, InterestStatus.NOT_FOUND);
        }

        String delete = "DELETE FROM contact_gene WHERE contact_pk = :contactPk AND gene_pk = :genePk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("contactPk", contact.getPk());
        parameterMap.put("genePk", gene.getPk());

        jdbcInterest.update(delete, parameterMap);

        // Delete from gene_sent if a row exists.
        delete = "DELETE FROM gene_sent WHERE address = :emailAddress AND mgi_accession_id = :mgiAccessionId";

        parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", contact.getEmailAddress());
        parameterMap.put("mgiAccessionId", gene.getMgiAccessionId());

       jdbcInterest.update(delete, parameterMap);
    }

    /**
     * Update the account_locked flag to the supplied value
     * @param emailAddress contact email address
     * @param accountLocked new value
     * @return {@link InterestStatus}.NOT_FOUND if {@code emailAddress} doesn't exist
     */
    public InterestStatus updateAccountLocked(String emailAddress, boolean accountLocked) throws InterestException {

        String message;
        String update = "UPDATE contact SET account_locked = :accountLocked WHERE address = :address";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("accountLocked", accountLocked ? 1 : 0);
        parameterMap.put("address", emailAddress);

        int rowCount = jdbcInterest.update(update, parameterMap);
        if (rowCount < 1) {
            return InterestStatus.NOT_FOUND;
        }

        return InterestStatus.OK;
    }

    /**
     * Updates the gene_sent table for this contact emailAddress. Any previous rows for this emailAddress are first
     * deleted, then the rows from the {@link Summary} instance are INSERTed.
     */
    @Transactional
    public void updateGeneSent(Summary summary) {

        String emailAddress = summary.getEmailAddress();
        deleteGeneSentByEmailAddress(emailAddress);
        Date now = new Date();

        String insert = "INSERT INTO gene_sent (address, mgi_accession_id, assignment_status, conditional_allele_production_status, null_allele_production_status, phenotyping_status, created_at, sent_at) " +
                        "VALUES (:address, :mgiAccessionId, :assignmentStatus, :conditionalAlleleProductionStatus, :nullAlleleProductionStatus, :phenotypingStatus, :createdAt, :sentAt)";
        Map<String, Object> parameterMap = new HashMap<>();
        for (Gene gene : summary.getGenes()) {
            parameterMap.put("address", emailAddress);
            parameterMap.put("mgiAccessionId", gene.getMgiAccessionId());
            parameterMap.put("assignmentStatus", gene.getRiAssignmentStatus());
            parameterMap.put("conditionalAlleleProductionStatus", gene.getRiConditionalAlleleProductionStatus());
            parameterMap.put("nullAlleleProductionStatus", gene.getRiNullAlleleProductionStatus());
            parameterMap.put("phenotypingStatus", gene.getRiPhenotypingStatus());
            parameterMap.put("createdAt", now);
            parameterMap.put("sentAt", now);

            jdbcInterest.update(insert, parameterMap);
        }
    }

    /**
     * Updates the gene_sent table for this contact emailAddress and gene.
     */
    @Transactional
    public void updateGeneSent(String emailAddress, Gene gene) {

        final String insert =
                "INSERT INTO gene_sent (address, mgi_accession_id, assignment_status, conditional_allele_production_status, null_allele_production_status, phenotyping_status, created_at, sent_at) " +
                "VALUES (:address, :mgiAccessionId, :assignmentStatus, :conditionalAlleleProductionStatus, :nullAlleleProductionStatus, :phenotypingStatus, :createdAt, :sentAt)";

        Date now = new Date();

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("address", emailAddress);
        parameterMap.put("mgiAccessionId", gene.getMgiAccessionId());
        parameterMap.put("assignmentStatus", gene.getRiAssignmentStatus());
        parameterMap.put("conditionalAlleleProductionStatus", gene.getRiConditionalAlleleProductionStatus());
        parameterMap.put("nullAlleleProductionStatus", gene.getRiNullAlleleProductionStatus());
        parameterMap.put("phenotypingStatus", gene.getRiPhenotypingStatus());
        parameterMap.put("createdAt", now);
        parameterMap.put("sentAt", now);

        if (getGeneSent(emailAddress, gene.getMgiAccessionId()) == null) {
            jdbcInterest.update(insert, parameterMap);
        }
    }

    /**
     * For every {@link Gene} in {@code genes, first attempt to update it. If the update fails because it's missing,
     * insert the record.
     *
     * @param genes the list of {@link Gene} instances to be used to update the database
     *
     * @return A {@link Map} containing two keyed results:
     * <ul>
     *     <li>
     *         key: "insertCount" ({@Link Integer}) - the number of rows inserted
     *         key: "updateCount" ({@link Integer}) - the number of rows updated
     *      </li>
     * </ul>
     *
     * @throws InterestException
     */
    public Map<String, Integer> updateOrInsertGene(List<Gene> genes) throws InterestException {
        Map<String, Integer> results = new HashMap<>();
        int insertCount = 0;
        int updateCount = 0;
        Date createdAt = new Date();

        for (Gene gene : genes) {

            gene.setCreatedAt(createdAt);

            try {

                Map<String, Object> parameterMap = loadGeneParameterMap(gene);

                // Except for the initial load, most of the time the gene will already exist.
                // Try to update. If that fails because it doesn't yet exist, insert.
                int count = updateGene(parameterMap);
                if (count > 0) {
                    updateCount += count;
                } else {
                    insertCount += insertGene(parameterMap);
                }

            } catch (Exception e) {
                logger.error(e.getLocalizedMessage());
            }
        }

        results.put("insertCount", insertCount);
        results.put("updateCount", updateCount);

        return results;
    }

    /**
     * Update the encrypted password for {@code emailAddress}
     *
     * @param emailAddress contact email address
     * @param encryptedPassword new, encrypted password
     * @throws {@link InterestException}.NOT_FOUND) if {@code emailAddress} doesn't exist
     */
    public void updatePassword(String emailAddress, String encryptedPassword) throws InterestException {

        String message;
        String update = "UPDATE contact SET password = :password, password_expired = 0 WHERE address = :address";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("password", encryptedPassword);
        parameterMap.put("address", emailAddress);

        int rowCount = jdbcInterest.update(update, parameterMap);
        if (rowCount < 1) {
            message = "Unable to update password for contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, InterestStatus.NOT_FOUND);
        }
    }

    /**
     * Update the password_expired flag to the supplied value
     *
     * @param emailAddress contact email address
     * @param passwordExpired new value
     * @throws {@link InterestException}.NOT_FOUND) if {@code emailAddress} doesn't exist
     */
    public void updatePasswordExpired(String emailAddress, boolean passwordExpired) throws InterestException {

        String message;
        String update = "UPDATE contact SET password_expired = :passwordExpired WHERE address = :address";

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("passwordExpired", passwordExpired ? 1 : 0);
        parameterMap.put("address", emailAddress);

        int rowCount = jdbcInterest.update(update, parameterMap);
        if (rowCount < 1) {
            message = "Unable to update passwordExpired for contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, InterestStatus.NOT_FOUND);
        }
    }

    /**
     * Try to INSERT the {@link ResetCredentials} instance. If the emailAddress already exists, update the instance.
     * @param resetCredentials the instance to INSERT or UPDATE.
     */
    public void updateResetCredentials(ResetCredentials resetCredentials) {

        String insert = "INSERT INTO reset_credentials (email_address, token, created_at) VALUES " +
                        "(:emailAddress, :token, :createdAt)";

        String update = "UPDATE reset_credentials SET token = :token, created_at = :createdAt " +
                        "WHERE email_address = :emailAddress";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", resetCredentials.getAddress());
        parameterMap.put("token", resetCredentials.getToken());
        parameterMap.put("createdAt", resetCredentials.getCreatedAt());

        try {

            jdbcInterest.update(insert, parameterMap);

        } catch (DuplicateKeyException e) {

            jdbcInterest.update(update, parameterMap);
        }
    }


    // PRIVATE METHODS


    private int insertGene(Map<String, Object> parameterMap) throws InterestException {
        final String columnNames =
                "mgi_accession_id, symbol, assigned_to, assignment_status, assignment_status_date, ri_assignment_status, " +
                "conditional_allele_production_centre, conditional_allele_production_status, ri_conditional_allele_production_status, " +
                "conditional_allele_production_status_date, conditional_allele_production_start_date, " +
                "null_allele_production_centre, null_allele_production_status, ri_null_allele_production_status, " +
                "null_allele_production_status_date, null_allele_production_start_date, " +
                "phenotyping_centre, phenotyping_status, phenotyping_status_date, ri_phenotyping_status, " +
                "number_of_significant_phenotypes, created_at";

        final String columnValues =
                ":mgi_accession_id, :symbol, :assigned_to, :assignment_status, :assignment_status_date, :ri_assignment_status, " +
                ":conditional_allele_production_centre, :conditional_allele_production_status, :ri_conditional_allele_production_status, " +
                ":conditional_allele_production_status_date, :conditional_allele_production_start_date, " +
                ":null_allele_production_centre, :null_allele_production_status, :ri_null_allele_production_status, " +
                ":null_allele_production_status_date, :null_allele_production_start_date, " +
                ":phenotyping_centre, :phenotyping_status, :phenotyping_status_date, :ri_phenotyping_status, " +
                ":number_of_significant_phenotypes, :created_at";

        final String query = "INSERT INTO gene(" + columnNames + ") VALUES (" + columnValues + ")";

        int count = jdbcInterest.update(query, parameterMap);

        return count;
    }

    private Map<String, Object> loadGeneParameterMap(Gene gene) {

        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("mgi_accession_id", gene.getMgiAccessionId());
        parameterMap.put("symbol", gene.getSymbol());
        parameterMap.put("assigned_to", gene.getAssignedTo());
        parameterMap.put("assignment_status", gene.getAssignmentStatus());
        parameterMap.put("assignment_status_date", gene.getAssignmentStatusDate());
        parameterMap.put("ri_assignment_status", gene.getRiAssignmentStatus());

        parameterMap.put("conditional_allele_production_centre", gene.getConditionalAlleleProductionCentre());
        parameterMap.put("conditional_allele_production_status", gene.getConditionalAlleleProductionStatus());
        parameterMap.put("ri_conditional_allele_production_status", gene.getRiConditionalAlleleProductionStatus());
        parameterMap.put("conditional_allele_production_status_date", gene.getConditionalAlleleProductionStatusDate());
        parameterMap.put("conditional_allele_production_start_date", gene.getConditionalAlleleProductionStartDate());

        parameterMap.put("null_allele_production_centre", gene.getNullAlleleProductionCentre());
        parameterMap.put("null_allele_production_status", gene.getNullAlleleProductionStatus());
        parameterMap.put("ri_null_allele_production_status", gene.getRiNullAlleleProductionStatus());
        parameterMap.put("null_allele_production_status_date", gene.getNullAlleleProductionStatusDate());
        parameterMap.put("null_allele_production_start_date", gene.getNullAlleleProductionStartDate());

        parameterMap.put("phenotyping_centre", gene.getPhenotypingCentre());
        parameterMap.put("phenotyping_status", gene.getPhenotypingStatus());
        parameterMap.put("phenotyping_status_date", gene.getPhenotypingStatusDate());
        parameterMap.put("ri_phenotyping_status", gene.getRiPhenotypingStatus());

        parameterMap.put("number_of_significant_phenotypes", gene.getNumberOfSignificantPhenotypes());

        parameterMap.put("created_at", gene.getCreatedAt());

        return parameterMap;
    }

    private int updateGene(Map<String, Object> parameterMap) {

        final String colData =
                // Omit mgi_accession_id in the UPDATE as it is used in the WHERE clause.

                "symbol = :symbol, " +
                "assigned_to = :assigned_to, " + 
                "assignment_status = :assignment_status, " + 
                "assignment_status_date = :assignment_status_date, " +
                "ri_assignment_status = :ri_assignment_status, " +

                "conditional_allele_production_centre = :conditional_allele_production_centre, " +
                "conditional_allele_production_status = :conditional_allele_production_status, " +
                "ri_conditional_allele_production_status = :ri_conditional_allele_production_status, " +
                "conditional_allele_production_status_date = :conditional_allele_production_status_date, " +
                "conditional_allele_production_start_date = :conditional_allele_production_start_date, " +

                "null_allele_production_centre = :null_allele_production_centre, " +
                "null_allele_production_status = :null_allele_production_status, " +
                "ri_null_allele_production_status = :ri_null_allele_production_status, " +
                "null_allele_production_status_date = :null_allele_production_status_date, " +
                "null_allele_production_start_date = :null_allele_production_start_date, " +

                "phenotyping_centre = :phenotyping_centre, " +
                "phenotyping_status = :phenotyping_status, " +
                "phenotyping_status_date = :phenotyping_status_date, " +
                "ri_phenotyping_status = :ri_phenotyping_status, " +

                "number_of_significant_phenotypes = :number_of_significant_phenotypes";

        final String query = "UPDATE gene SET " + colData + " WHERE mgi_accession_id = :mgi_accession_id";

        int count = jdbcInterest.update(query, parameterMap);

        return count;
    }
}