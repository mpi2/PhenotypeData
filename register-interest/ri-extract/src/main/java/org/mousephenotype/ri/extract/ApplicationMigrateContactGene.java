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

package org.mousephenotype.ri.extract;

import org.mousephenotype.cda.ri.core.entities.Contact;
import org.mousephenotype.cda.ri.core.entities.ContactGene;
import org.mousephenotype.cda.ri.core.entities.Gene;
import org.mousephenotype.cda.ri.core.exceptions.InterestException;
import org.mousephenotype.cda.ri.core.utils.DateUtils;
import org.mousephenotype.cda.ri.core.utils.PasswordUtils;
import org.mousephenotype.cda.ri.core.utils.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This class implements the migration and extraction of the ContactGene report from iMits.
 *
 * 2018-07-24 (mrelac) NOTE: This class will be obsolete once we unplug Harwell from Register Interest management.
 *
 * Created by mrelac on 19/07/2017.
 */
@EnableBatchProcessing
@ComponentScan({"org.mousephenotype.ri.extract"})
@Deprecated
public class ApplicationMigrateContactGene implements CommandLineRunner {

    @NotNull
    @Value("${download.workspace}")
    protected String downloadWorkspace;

    @NotNull
    @Value("${ContactGeneUrl}")
    protected String sourceUrl;

    private DateUtils dateUtils = new DateUtils();
    private Logger logger      = LoggerFactory.getLogger(this.getClass());
    private String targetFilename;
    private SqlUtils sqlUtils;

    public static final int COL_MGI_ACCESSION_ID                     = 0;
    public static final int COL_MARKER_SYMBOL                        = 1;
    public static final int COL_EMAIL                                = 2;
    public static final int COL_GENE_CONTACT_CREATED_AT              = 3;
    public static final int COL_CONTACT_CREATED_AT                   = 4;


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ApplicationMigrateContactGene.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }


    @Inject
    public ApplicationMigrateContactGene(SqlUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }


    @Override
    public void run(String... args) throws Exception {
        targetFilename = downloadWorkspace + "/ContactGene.tsv";
        long start;

        try {

            start = new Date().getTime();
            download();
            logger.info("Downloaded " + sourceUrl + " to " + targetFilename + " in " + dateUtils.msToHms(new Date().getTime() - start));

        } catch (InterestException e) {

            logger.warn(e.getLocalizedMessage());
        }

        try {

            start = new Date().getTime();
            int count = migrateContactGenes();
            logger.info("Extracted " + count + " contact-gene associations in " + dateUtils.msToHms(new Date().getTime() - start));

        } catch (InterestException e) {

            logger.warn(e.getLocalizedMessage());
        }
    }

    public void download() throws InterestException {

        FileOutputStream fos;
        ReadableByteChannel rbc;
        final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
        String outputAppender = DATE_FORMAT.format(new Date());
        String source;
        String target;
        String targetTemp;
        URL url;

        try

        {
            Files.createDirectories(Paths.get(targetFilename).getParent());

        } catch (IOException e) {

            logger.error("Create download directory '" + targetFilename + "' failed. Reason: " + e.getLocalizedMessage());
        }

        target = targetFilename;
        targetTemp = target + "." + outputAppender;
        source = sourceUrl;
        try

        {
            url = new URL(source);
            rbc = Channels.newChannel(url.openStream());
            fos = new FileOutputStream(targetTemp);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            Files.move(Paths.get(targetTemp), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {

            String message = "Download of " + source + " -> " + target + " failed. Reason: Bad URL: " + e.getLocalizedMessage();
            throw new InterestException(message);
        }
    }


    @Transactional
    public int migrateContactGenes() throws InterestException {

        int               count            = 0;
        String            line;
        String[]          parts;
        List<Contact>     impcContacts     = sqlUtils.getContacts();
        List<ContactGene> impcContactGenes = sqlUtils.getContactGenes();

        Map<String, Gene>  genesByGeneAccessionId = sqlUtils.getGenesByGeneAccessionId();
        Map<Integer, Gene> genesByPk              = sqlUtils.getGenesByPk();

        Map<String, Contact>  impcContactsByAddress = new HashMap<>();
        Map<Integer, Contact> contactsByPk          = new HashMap<>();
        for (Contact contact : impcContacts) {
            impcContactsByAddress.put(contact.getEmailAddress(), contact);
            contactsByPk.put(contact.getPk(), contact);
        }

        Map<String, ContactGene> impcContactGenesByCustomKey = new HashMap<>();
        for (ContactGene contactGene : impcContactGenes) {
            Contact contact = contactsByPk.get(contactGene.getContactPk());
            Gene gene = genesByPk.get(contactGene.getGenePk());
            String key = contact.getEmailAddress() + "::" + gene.getMgiAccessionId();
            impcContactGenesByCustomKey.put(key, contactGene);
        }

        Set<String> imitsContactGenes = new HashSet<>();
        Set<String> impcContactsCreated = new HashSet<>();
        Set<String> impcContactGenesCreated = new HashSet<>();


        try {
            BufferedReader br = new BufferedReader(new FileReader(targetFilename));
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty())                     // Skip the newline. Thank you, java.
                    continue;

                if (lineNumber++ == 1)
                    continue;                           // Skip the heading.

                parts = line.split(Pattern.quote("\t"));

                if (parts.length != 5) {

                    logger.error("FATAL ERROR: Input file '{}' contains {} fields. Expected 5.", targetFilename, parts.length);

                    return count;
                }

                String geneAccessionId = parts[COL_MGI_ACCESSION_ID];
                String emailAddress = parts[COL_EMAIL];

                // Get the gene. It is an error if the gene does not exist.
                Gene gene = genesByGeneAccessionId.get(geneAccessionId);
                if (gene == null) {

                    logger.error("ERROR: createAccount for contact '{}' failed because gene accession id {} doesn't exist.", emailAddress, geneAccessionId);
                    continue;
                }

                imitsContactGenes.add(emailAddress + "::" + geneAccessionId);

                // If the contact doesn't exist in impc, create it.
                Contact contact = impcContactsByAddress.get(emailAddress);
                if (contact == null) {
                    sqlUtils.createAccount(emailAddress, PasswordUtils.generateSecureRandomPassword());
                    contact = sqlUtils.getContact(emailAddress);

                    impcContactsCreated.add(emailAddress);
                    impcContactsByAddress.put(emailAddress, contact);
                }

                // If the contactGene doesn't exist in impc, create it.
                String key = emailAddress + "::" + geneAccessionId;
                ContactGene contactGene = impcContactGenesByCustomKey.get(key);
                if (contactGene == null) {
                    sqlUtils.registerGene(emailAddress, geneAccessionId);
                    contactGene = sqlUtils.getContactGene(emailAddress, geneAccessionId);

                    impcContactGenesCreated.add(key);
                    impcContactGenesByCustomKey.put(key, contactGene);
                }

                count++;
            }

            if (br != null) {
                br.close();
            }

        } catch (IOException e) {

            String message = "Open file '" + targetFilename + "' failed. Reason: " + e.getLocalizedMessage();
            throw new InterestException(message);
        }

        ArrayList<String> impcContactsCreatedList = new ArrayList<>(impcContactsCreated);
        ArrayList<String> impcContactGenesCreatedList = new ArrayList<>(impcContactGenesCreated);

        Collections.sort(impcContactsCreatedList);
        Collections.sort(impcContactGenesCreatedList);

        // Print statistics.
        if (impcContactsCreatedList.isEmpty()) {
            logger.info("Created no new accounts.");
        } else {
            logger.info("Created new accounts for:");
            for (String s : impcContactsCreatedList) {
                logger.info("  {}", s);
            }
        }

        System.out.println();

        if (impcContactGenesCreatedList.isEmpty()) {
            logger.info("Created no new contact-gene registrations.");
        } else {
            logger.info("Created new contact-gene registrations for:");
            for (String s : impcContactGenesCreatedList) {
                logger.info("  {}", s);
            }
        }

        impcContactGenesCreated.removeAll(imitsContactGenes);
        if (impcContactGenesCreated.isEmpty()) {
            logger.info("No contact-gene unregistrations.");
        } else {
            logger.info("Unregistrations:");
            for (String s : impcContactGenesCreated) {
                logger.info("  {}", s);
            }
        }

        return count;
    }


    // PRIVATE METHODS


    private Date parseDate(String dateString) throws InterestException {

        Date date = dateUtils.convertToDate(dateString);
        
        if (date == null) {
            throw new InterestException( "Invalid date: '" + dateString + "'");
        }
        
        return date;
    }
}