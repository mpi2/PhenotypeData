package org.mousephenotype.cda.ri.services;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.ri.entities.Contact;
import org.mousephenotype.cda.ri.entities.ContactGene;
import org.mousephenotype.cda.ri.entities.ResetCredentials;
import org.mousephenotype.cda.ri.enums.EmailFormat;
import org.mousephenotype.cda.ri.exceptions.InterestException;
import org.mousephenotype.cda.ri.pojo.Summary;
import org.mousephenotype.cda.ri.pojo.SummaryDetail;
import org.mousephenotype.cda.ri.utils.RiSqlUtils;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SummaryService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // This is the interval, in minutes, between reloading the summary details from the gene core.
    public static int SUMMARY_DETAILS_RELOAD_INTERVAL_IN_MINUTES = 240;

    private GeneService geneService;
    private RiSqlUtils  riSqlUtils;
    private SummaryDetailsProvider detailsProvider;

    @Inject
    public SummaryService(GeneService geneService, RiSqlUtils riSqlUtils) {
        this.geneService = geneService;
        this.riSqlUtils = riSqlUtils;
        this.detailsProvider = new SummaryDetailsProvider();
    }

    public void createAccount(String emailAddress, String newPassword) throws InterestException {
        riSqlUtils.createAccount(emailAddress, newPassword);
    }

    public void changeEmailFormat(String emailAddress, EmailFormat emailFormat) throws InterestException {
        riSqlUtils.updateInHtml(emailAddress, emailFormat == EmailFormat.HTML ? 1 : 0);
    }

    public void deleteContact(String emailAddress) throws InterestException {
        riSqlUtils.deleteContact(emailAddress);
    }

    public List<Contact> getContacts() {
        return riSqlUtils.getContacts();
    }

    public Contact getContact(String emailAddress) {
        return getContacts().stream().filter((Contact c)
                -> c.getEmailAddress().equalsIgnoreCase(emailAddress))
                .findFirst().orElse(null);
    }

    public void deleteResetCredentialsByEmailAddress(String emailAddress) {
        riSqlUtils.deleteResetCredentialsByEmailAddress(emailAddress);
    }

    public ResetCredentials getResetCredentialsByToken(String token) {
        return riSqlUtils.getResetCredentials(token);
    }

    public ResetCredentials getResetCredentialsByEmailAddress(String emailAddress) {
        return riSqlUtils.getResetCredentialsByEmail(emailAddress);
    }

    public void updateResetCredentials(ResetCredentials resetCredentials) {
        riSqlUtils.updateResetCredentials(resetCredentials);
    }

    // This gets called by Spring with emailAddress 'anonymousUser', which has no contact entry.
    public List<String> getGeneAccessionIds(String emailAddress) {
        Contact contact = getContact(emailAddress);
        if (contact == null) {
            return new ArrayList<>();
        }
        return getSummaryByContact(getContact(emailAddress))
                .getDetails()
                .stream()
                .map((SummaryDetail::getGeneAccessionId)).collect(Collectors.toList());
    }

    public Map<String, SummaryDetail> getSummaryDetailsByAcc() {
        return detailsProvider.summaryDetailsByAcc();
    }

    public Summary getSummaryByContact(Contact contact) {
        if (contact == null) {
            return null;
        }
        return new Summary(
                contact.getEmailAddress(),
                contact.isInHtml(),
                riSqlUtils.getContactGenes(contact.getEmailAddress())
                        .stream()
                        .map((ContactGene cg) -> detailsProvider.summaryDetailsByAcc().get(cg.getGeneAccessionId()))
                        .collect(Collectors.toList()));
    }

    public boolean isRegisteredForGene(String emailAddress, String geneAccessionId) {
        return getSummaryByContact(getContact(emailAddress))
                .getDetails()
                .stream()
                .anyMatch((SummaryDetail sd) ->
                        sd.getGeneAccessionId().equalsIgnoreCase(geneAccessionId));
    }

    public void registerGene(String emailAddress, String geneAccessionId) {
        riSqlUtils.registerGene(emailAddress, detailsProvider.summaryDetailsByAcc().get(geneAccessionId));
    }

    public void unregisterGene(String emailAddress, String geneAccessionId) {
        riSqlUtils.unregisterGene(emailAddress, geneAccessionId);
    }

    public void updatePassword(String emailAddress, String newPassword) throws InterestException {
        riSqlUtils.updatePassword(emailAddress, newPassword);
    }

    /**
     * This class encapsulates the code that returns the complete list of SummaryDetail instances for each
     * gene in the gene core. It is thread-safe. So as to lighten the burden on the gene core, it caches
     * the results and only refreshes itself every SUMMARY_DETAILS_RELOAD_INTERVAL_IN_MINUTES.
     */
    private class SummaryDetailsProvider {
        private final ConcurrentHashMap<String, SummaryDetail> _summaryDetails = new ConcurrentHashMap<>();

        private LocalDateTime lastAccess = LocalDateTime.now();

        SummaryDetailsProvider() {
            reload();
        }

        public Map<String, SummaryDetail> summaryDetailsByAcc() {
            long diffInMinutes = ChronoUnit.MINUTES.between(lastAccess, LocalDateTime.now());
            if (diffInMinutes > SUMMARY_DETAILS_RELOAD_INTERVAL_IN_MINUTES) {
                reload();
            }
            return _summaryDetails;
        }

        public void reload() {
            logger.info("Begin: Reloading SummaryDetails from gene core");
            synchronized (_summaryDetails) {    // Make it thread safe
                try {
                    _summaryDetails.clear();
                    _summaryDetails.putAll(
                            geneService.getRegisterInterestGeneDetails()
                                    .stream()
                                    .collect(Collectors.toMap(
                                            GeneDTO::getMgiAccessionId,
                                            SummaryDetail::new)));
                } catch (SolrServerException | IOException e) {
                    e.printStackTrace();
                }
            }
            lastAccess = LocalDateTime.now();
            logger.info("End: Reloading SummaryDetails from gene core");
        }
    }
}