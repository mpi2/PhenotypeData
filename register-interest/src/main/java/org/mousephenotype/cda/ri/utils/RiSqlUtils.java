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
import org.mousephenotype.cda.ri.enums.InterestStatus;
import org.mousephenotype.cda.ri.exceptions.InterestException;
import org.mousephenotype.cda.ri.pojo.RIRole;
import org.mousephenotype.cda.ri.pojo.Summary;
import org.mousephenotype.cda.ri.pojo.SummaryDetail;
import org.mousephenotype.cda.ri.rowmappers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by mrelac on 12/05/2017.
 */
public class RiSqlUtils {
    private static final Logger logger = LoggerFactory.getLogger(RiSqlUtils.class);

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
     * @param role         The role to be added
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
     * @param tableName  the table to
     * @param columnName the column in the table
     * @return true = column name exists in table, false = column missing from table
     * @throws SQLException on db access error
     */
    public Boolean columnInSchemaMysql(Connection connection, String tableName, String columnName) throws SQLException {

        Boolean found       = Boolean.FALSE;
        String  columnQuery = "SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=(SELECT database()) AND TABLE_NAME=? AND column_name=?";

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
     * @param emailAddress      The email addresss to be inserted
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

    @Transactional
    public void deleteContact(String emailAddress) throws InterestException {

        String message;

        Contact contact = getContact(emailAddress);
        if (contact == null) {
            message = "deleteContact(): Invalid contact " + emailAddress + ".";
            logger.warn(message);
            throw new InterestException(message, InterestStatus.NOT_FOUND);
        }

        String              delete;
        Map<String, Object> parameterMap = new HashMap<>();

        // Delete all matching emailAddress from contact_gene.
        delete = "DELETE FROM contact_gene WHERE contact_pk = :contactPk";
        parameterMap.put("contactPk", contact.getPk());
        jdbcInterest.update(delete, parameterMap);

        // Delete all matching emailAddress from gene_sent (GDPR).
        deleteGeneSentByEmailAddress(emailAddress);

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
        String              delete       = "DELETE FROM gene_sent WHERE address = :address";
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("address", emailAddress);
        jdbcInterest.update(delete, parameterMap);
    }

    /**
     * Delete the row in reset_credentials identified by {@code emailAddress}. If no such email address exists, no
     * rows are deleted.
     *
     * @param emailAddress key to use for delete
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
     * Return the  {@link Contact}
     *
     * @param emailAddress the email address of the desired contact
     * @return the {@link Contact} matching the emailAddress if found; null otherwise
     */
    public Contact getContact(String emailAddress) {

        final String query = "SELECT * FROM contact WHERE address = :emailAddress";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", emailAddress);

        List<Contact> contacts = jdbcInterest.query(query, parameterMap, new ContactRowMapper());
        Contact       contact  = (contacts.isEmpty() ? null : contacts.get(0));
        if (contact != null) {
            contact.setRoles(getContactRoles(emailAddress));
        }

        return contact;
    }

    /**
     * @return a {@link List} of all {@link ContactGene} entries
     */
    public List<ContactGene> getContactGenes() {
        final String query = "SELECT * FROM contact_gene";

        Map<String, Object> parameterMap = new HashMap<>();

        return jdbcInterest.query(query, parameterMap, new ContactGeneRowMapper());
    }

    public List<ContactGene> getContactGenes(String emailAddress) {
        final String query =
            "SELECT cg.* FROM contact_gene cg\n" +
                "JOIN contact c ON c.pk = cg.contact_pk\n" +
                "WHERE c.address = :emailAddress";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", emailAddress);

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

        Map<Integer, Contact> contactMap  = new HashMap<>();
        List<Contact>         contactList = jdbcInterest.query(query, new HashMap<String, Object>(), new ContactRowMapper());

        return contactList;
    }

    public Map<Integer, Contact> getContactsByPk() {
        final String query = "SELECT * FROM contact";

        Map<Integer, Contact> contactMap  = new HashMap<>();
        List<Contact>         contactList = jdbcInterest.query(query, new HashMap<String, Object>(), new ContactRowMapper());
        for (Contact contact : contactList) {
            contactMap.put(contact.getPk(), contact);
        }

        return contactMap;
    }

    /**
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

    public List<GeneSent> getGeneSentByEmailAddress(String emailAddress) {
        final String        query        = "SELECT * FROM gene_sent WHERE address = :emailAddress";
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", emailAddress);
        return jdbcInterest.query(query, parameterMap, new GeneSentRowMapper());
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

    /**
     * Register {@code geneAccessionId} to {@code emailAddress}
     *
     * @param emailAddress  contact email address
     * @param summaryDetail summary detail info for gene of interest
     * @throws InterestException if either {@code emailAddress} or {@code geneAccessionId} doesn't exist, or if contact
     *                           is already registered for this gene.
     */
    @Transactional
    public void registerGene(String emailAddress, SummaryDetail summaryDetail) {
        Contact contact = getContact(emailAddress);
        Date    now     = new Date();
        String insert = "INSERT INTO contact_gene (contact_pk, gene_accession_id, created_at) VALUES " +
            "(:contactPk, :geneAccessionId, :createdAt)";
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("contactPk", contact.getPk());
        parameterMap.put("geneAccessionId", summaryDetail.getGeneAccessionId());
        parameterMap.put("createdAt", now);
        try {
            jdbcInterest.update(insert, parameterMap);
            _insertGeneSent(emailAddress, summaryDetail);
        } catch (DuplicateKeyException e) {
        }
    }

    /**
     * Unregister {@code geneAccessionId} from {@code emailAddress}
     *
     * @param emailAddress    contact email address
     * @param geneAccessionId gene accession id
     * @throws InterestException if either {@code emailAddress} or {@code geneAccessionId} doesn't exist, or if contact
     *                           is not registered for this gene.
     */
    @Transactional
    public void unregisterGene(String emailAddress, String geneAccessionId) {
        Contact contact = getContact(emailAddress);
        String delete = "DELETE FROM contact_gene" +
            " WHERE contact_pk = :contactPk AND gene_accession_id = :geneAccessionId";
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("contactPk", contact.getPk());
        parameterMap.put("geneAccessionId", geneAccessionId);
        jdbcInterest.update(delete, parameterMap);

        // Delete from gene_sent if a row exists.
        delete = "DELETE FROM gene_sent WHERE address = :emailAddress AND gene_accession_id = :geneAccessionId";
        parameterMap = new HashMap<>();
        parameterMap.put("emailAddress", contact.getEmailAddress());
        parameterMap.put("geneAccessionId", geneAccessionId);
        jdbcInterest.update(delete, parameterMap);
    }

    /**
     * Update the account_locked flag to the supplied value
     *
     * @param emailAddress  contact email address
     * @param accountLocked new value
     * @return {@link InterestStatus}.NOT_FOUND if {@code emailAddress} doesn't exist
     */
    public InterestStatus updateAccountLocked(String emailAddress, boolean accountLocked) throws InterestException {
        String              update       = "UPDATE contact SET account_locked = :accountLocked WHERE address = :address";
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
     * Deletes any existing SummaryDetail instances for this summary, then
     * INSERTs the SummaryDetail instances in Summary.
     */
    @Transactional
    public void updateGeneSent(Summary summary) {
        String emailAddress = summary.getEmailAddress();
        deleteGeneSentByEmailAddress(emailAddress);
        summary.getDetails()
            .stream()
            .forEach((SummaryDetail sd) -> _insertGeneSent(summary.getEmailAddress(), sd));
    }

    /**
     * Updates the gene_sent table for this contact emailAddress and gene.
     */
    private void _insertGeneSent(String emailAddress, SummaryDetail summaryDetail) {
        final String insert =
            "INSERT INTO gene_sent (address, gene_accession_id, symbol, assignment_status," +
                " conditional_allele_production_status, crispr_allele_production_status," +
                " null_allele_production_status, phenotyping_data_available, created_at, sent_at) " +
                "VALUES (:address, :geneAccessionId, :symbol, :assignmentStatus, :conditionalAlleleProductionStatus," +
                " :crisprAlleleProductionStatus, :nullAlleleProductionStatus, :phenotypingDataAvailable," +
                " :createdAt, :sentAt)";

        Date                now          = new Date();
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("address", emailAddress);
        parameterMap.put("geneAccessionId", summaryDetail.getGeneAccessionId());
        parameterMap.put("symbol", summaryDetail.getSymbol());
        parameterMap.put("assignmentStatus", summaryDetail.getAssignmentStatus());
        parameterMap.put("conditionalAlleleProductionStatus", summaryDetail.getConditionalAlleleProductionStatus());
        parameterMap.put("crisprAlleleProductionStatus", summaryDetail.getCrisprAlleleProductionStatus());
        parameterMap.put("nullAlleleProductionStatus", summaryDetail.getNullAlleleProductionStatus());
        parameterMap.put("phenotypingDataAvailable", summaryDetail.isPhenotypingDataAvailable() ? 1 : 0);
        parameterMap.put("createdAt", now);
        parameterMap.put("sentAt", now);
        jdbcInterest.update(insert, parameterMap);
    }

    /**
     * Update the encrypted password for {@code emailAddress}
     *
     * @param emailAddress      contact email address
     * @param encryptedPassword new, encrypted password
     * @throws {@link InterestException}.NOT_FOUND) if {@code emailAddress} doesn't exist
     */
    public void updatePassword(String emailAddress, String encryptedPassword) throws InterestException {
        String              message;
        final String        update       = "UPDATE contact SET password = :password, password_expired = 0 WHERE address = :address";
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

    public void updateInHtml(String emailAddress, int inHtml) throws InterestException {
        String              message;
        Map<String, Object> parameterMap = new HashMap<>();
        final String update = "UPDATE contact SET in_html = :inHtml WHERE address = :address";
        parameterMap.put("inHtml", inHtml);
        parameterMap.put("address", emailAddress);
        int rowCount = jdbcInterest.update(update, parameterMap);
        if (rowCount < 1) {
            message = "Unable to change in_html to " + inHtml + " for contact " + emailAddress + ".";
            logger.error(message);
            throw new InterestException(message, InterestStatus.INTERNAL_ERROR);
        }
    }

    /**
     * Update the password_expired flag to the supplied value
     *
     * @param emailAddress    contact email address
     * @param passwordExpired new value
     * @throws {@link InterestException}.NOT_FOUND) if {@code emailAddress} doesn't exist
     */
    public void updatePasswordExpired(String emailAddress, boolean passwordExpired) throws InterestException {
        String              message;
        String              update       = "UPDATE contact SET password_expired = :passwordExpired WHERE address = :address";
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
     *
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
}