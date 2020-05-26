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
package uk.ac.ebi.phenotype.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.exporter.Exporter;
import org.mousephenotype.cda.interfaces.Exportable;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.utilities.DisplayPager;
import org.mousephenotype.cda.utilities.DisplaySorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.phenotype.util.PublicationFetcher;
import uk.ac.ebi.phenotype.web.dao.ReferenceService;
import uk.ac.ebi.phenotype.web.dto.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
public class PublicationController implements Exportable<Publication> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public final String FULLY_QUALIFIED_GENE_BASE_URL = "https://www.mousephenotype.org/data/genes";

    private ReferenceService referenceService;


    @Inject
    public PublicationController(ReferenceService referenceService) {
        this.referenceService = referenceService;
    }


    @RequestMapping(value = "/fetchPaperStats", method = RequestMethod.GET)
    public @ResponseBody
    String paperStats(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws SQLException {

        return fetchPaperStats(); // json string
    }
    public String fetchPaperStats() throws SQLException {

        JSONObject  dataJson  = new JSONObject();

        try {
            dataJson.put("yearlyIncrease", new JSONObject(referenceService.getAddedCountByYear()));
            dataJson.put("paperMonthlyIncrementWeekDrilldown",  new JSONObject(referenceService.getAddedCountByYear()));
            dataJson.put("yearQuarterSum",  new JSONObject(referenceService.getCountByQuarter()));
            dataJson.put("agencyCount",  new JSONObject(referenceService.getCountByAgency()));

        } catch (Exception e) {

            e.printStackTrace();
        }

        return dataJson.toString();
    }

    @RequestMapping(value = "/publicationsDisplay", method = RequestMethod.GET)
    public ResponseEntity<String> publicationsDisplay(
            @RequestParam(value = "doAlleleRef", required = false) String params,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws JSONException {
        PublicationFetcher publicationFetcher = buildPublicationFetcher(request, params);
        String content = fetchPublicationContent(publicationFetcher);

        return new ResponseEntity<>(content, createResponseHeaders(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/publicationsExport", method = RequestMethod.GET)
    public void exportPublications(

            @RequestParam(value = "kw", required = true, defaultValue = "") String agency,
            @RequestParam(value = "id", required = true, defaultValue = "") String publicationTypeName,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "fileType", required = true) String fileType,
            @RequestParam(value = "fileName", required = true) String fileName,
            HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        PublicationFetcher publicationFetcher = buildPublicationFetcher(agency, filter, publicationTypeName);
        fileName = (publicationFetcher.getPublicationType() == PublicationFetcher.PublicationType.ACCEPTED_IMPC_PUBLICATION
                ? "all_impc_publications"
                : "impc_consortium_publications");
        List<Publication> publications = publicationFetcher.getAllPublications();
        List<List<String>> matrix = getRows(publications);
        Exporter.export(response, fileType, fileName, getHeading(), matrix);
    }


    // PRIVATE METHODS


    private PublicationFetcher buildPublicationFetcher(HttpServletRequest request, String params) throws JSONException {
        JSONObject jParams = new JSONObject(params);

        String agency = jParams.has("kw") ? jParams.getString("kw") : null;
        String filter = request.getParameter("sSearch");
        String publicationTypeName = jParams.getString("id");

        int startingDocumentOffset = request.getParameter("iDisplayStart") == null ? 0 : Integer.parseInt(request.getParameter("iDisplayStart"));
        int numDocumentsToDisplay = request.getParameter("iDisplayLength") == null ? 10 : Integer.parseInt(request.getParameter("iDisplayLength"));

        String orderByStr = jParams.getString("orderBy");
        String sortByFieldName = orderByStr.split(" ")[0];
        String sortByDirection = orderByStr.split(" ")[1];

        PublicationFetcher publicationFetcher = new PublicationFetcher(referenceService, getPublicationType(publicationTypeName));

        if ((agency != null) && ( ! agency.isEmpty())) {
            publicationFetcher.setAgency(agency);
        }
        publicationFetcher.setDisplayPager(new DisplayPager(startingDocumentOffset, numDocumentsToDisplay));
        if ((filter != null) && ( ! filter.isEmpty())) {
            publicationFetcher.setFilter(filter);
        }
        publicationFetcher.setDisplaySorter(new DisplaySorter(sortByFieldName, DisplaySorter.SortByDirection.valueOf(sortByDirection)));

        return publicationFetcher;
    }

    private PublicationFetcher buildPublicationFetcher(String agency, String filter, String publicationTypeName) {

        PublicationFetcher publicationFetcher = new PublicationFetcher(referenceService, getPublicationType(publicationTypeName));

        if ((agency != null) && ( ! agency.isEmpty())) {
            publicationFetcher.setAgency(agency);
        }

        if ((filter != null) && ( ! filter.isEmpty())) {
            publicationFetcher.setFilter(filter);
        }
        return publicationFetcher;
    }

    private HttpHeaders createResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    private String fetchPublicationContent(PublicationFetcher publicationFetcher) throws JSONException {

        final int DISPLAY_THRESHOLD = 5;

        JSONObject j = new JSONObject();
        j.put("data", new JSONArray());
        j.put("recordsTotal", publicationFetcher.getAllPublicationsCount());
        j.put("recordsFiltered", publicationFetcher.getDisplayPublicationsCount());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd");

        for (Publication publication : publicationFetcher.getDisplayPublications()) {

            List<String> rowData = new ArrayList<>();
            List<String> alleleSymbolinks = new ArrayList<>();

            int totalAlleleCount = 0;

            if (publication.getAlleles() != null) {

                totalAlleleCount = publication.getAlleles().size();
                // show max of 10 alleles for a paper
                int showCount = totalAlleleCount > DISPLAY_THRESHOLD ? DISPLAY_THRESHOLD : totalAlleleCount;

                for (int i=0; i < publication.getAlleles().size(); i++) {
                    AlleleRef allele = publication.getAlleles().get(i);
                    String symbol = Tools.superscriptify(allele.getAlleleSymbol());

                    if (symbol.equals("N/A")) {
                        continue;
                    }

                    String cssClass = "class='" + (i < DISPLAY_THRESHOLD ? "showMe" : "hideMe") + "'";

                    String alleleLink = null;

                    if(allele.getGeneAccessionId() != null) {
                        alleleLink = "<span " + cssClass + "><a target='_blank' href='//www.mousephenotype.org/data/genes/" + allele.getGeneAccessionId().toUpperCase() + "'>" + symbol + "</a></span>";
                    } else {
                        alleleLink = "<span " + cssClass + ">" + symbol + "</span>";
                    }

                    alleleSymbolinks.add(alleleLink);
                }
            }

            String paperLink = getFirstEuropePmcPublicationUrl(publication);

            if (paperLink == null || paperLink.isEmpty()) {
                rowData.add("<p>" + publication.getTitle() + "</p>");
            } else {
                rowData.add("<p><a href='" + paperLink + "'>" + publication.getTitle() + "</a></p>");
            }

            rowData.add("<p><i>" + publication.getJournalInfo().getJournal().getTitle() + "</i>, " + dateFormat.format(publication.getFirstPublicationDate()) + "</p>");

            // papers citing this paper
            if (publication.getCitedBy() != null && ! publication.getCitedBy().isEmpty()) {
                int len = publication.getCitedBy().size();
                String citations = "";
                for (CitingPaper paper : publication.getCitedBy()) {
                    citations += "<li><a target='_blank' href='" + paper.getUrl() +"'>" + paper.getTitle() + "</a> (" + dateFormat.format(paper.getPublicationDate()) + ")</li>";
                }
                rowData.add("<div id='citedBy' class='valToggle' rel=" + len + ">Cited by (" + len + ")</div><div class='valHide'><ul>" + citations + "</ul></div>");
            }

            rowData.add("<p class='author'>" + publication.getAuthorString() + "</p>");

            // hidden by default abstract, toggle to show/hide
            if (publication.getAbstractText() != null && !publication.getAbstractText().isEmpty()) {
                rowData.add("<div id='abstract' class='valToggle'>Show abstract</div><div class='valHide'>" + publication.getAbstractText() + "</div>");
            }

            rowData.add("<p>PMID: " + publication.getPmid() + "</p>");

            if (alleleSymbolinks.size() > 0) {
                if (totalAlleleCount > DISPLAY_THRESHOLD) {
                    alleleSymbolinks.add("<div class='alleleToggle' rel=" + alleleSymbolinks.size() + ">Show all " + alleleSymbolinks.size() + " alleles</div>");
                }
                rowData.add("<div class='alleles'>IMPC allele: " + StringUtils.join(alleleSymbolinks, "&nbsp;&nbsp;&nbsp;&nbsp;") + "</div>");
            }

            List<String> agencyList = new ArrayList();
            int agencyCount = publication.getGrantsList().size();

            // unique agency
            for (int i = 0; i < agencyCount; i++) {
                String grantAgency = publication.getGrantsList().get(i).getAgency();
                if (!grantAgency.isEmpty()) {
                    if (!agencyList.contains(grantAgency)) {
                        agencyList.add(grantAgency);
                    }
                }
            }

            if (agencyList.size() > 0) {
                rowData.add("<p>Grant agency: " + StringUtils.join(agencyList, ", ") + "</p>");
            }

            // hidden in datatable: mesh terms
            if (publication.getMeshHeadingList() != null && publication.getMeshHeadingList().size() > 0) {
                String meshTerms = StringUtils.join(publication.getMeshHeadingList(), ", ");
                rowData.add("<div id='meshTree' class='valToggle'>Show mesh terms</div><div class='valHide'>" + meshTerms + "</div>");
            }

            // single column
            String inOneRow = StringUtils.join(rowData, "");
            List<String> rowData2 = new ArrayList<>();
            rowData2.add("<div class='innerData'>" + inOneRow + "</div>");
            rowData = rowData2;

            j.getJSONArray("data").put(new JSONArray(rowData));

        }

        return j.toString();
    }

    private String getFirstEuropePmcPublicationUrl(Publication publication) {

        for(FullTextUrl url: publication.getFullTextUrlList()) {
            if (url.getSite().equals("Europe_PMC") && url.getDocumentStyle().equals("html"))
                return url.getUrl();
        }

        return null;
    }

    private PublicationFetcher.PublicationType getPublicationType(String publicationTypeName) {
        if (publicationTypeName != null && publicationTypeName.equals("agency")) {
            return PublicationFetcher.PublicationType.FUNDING_AGENCY;
        } else if (publicationTypeName != null && publicationTypeName.equals("consortiumPapers")) {
            return PublicationFetcher.PublicationType.IMPC_CONSORTIUM;
        } else if (publicationTypeName != null && publicationTypeName.equals("cardio")) {
            return PublicationFetcher.PublicationType.BIOSYSTEM;
        } else {
            return PublicationFetcher.PublicationType.ACCEPTED_IMPC_PUBLICATION;
        }
    }


    // IMPLEMENTATIONS


    @Override
    public List<String> getRow(Publication publication) {

        List<String> row = new ArrayList<>();

        List<String> symbols         = new ArrayList<>();
        List<String> accessionIds    = new ArrayList<>();
        String       geneAccessionId = null;
        if ((publication.getAlleles() != null) && ( ! publication.getAlleles().isEmpty())) {
            for (AlleleRef alleleRef : publication.getAlleles()) {
                symbols.add(alleleRef.getAlleleSymbol() == null ? Constants.NO_INFORMATION_AVAILABLE : alleleRef.getAlleleSymbol());
                accessionIds.add(alleleRef.getAlleleAccessionId() == null ? Constants.NO_INFORMATION_AVAILABLE : alleleRef.getAlleleAccessionId());
                if (geneAccessionId == null) {
                    geneAccessionId = alleleRef.getGeneAccessionId();
                }
            }
            row.add(StringUtils.join(symbols, "|"));            // MGI allele symbol
            row.add(StringUtils.join(accessionIds, "|"));       // MGI allele accession id
            row.add(geneAccessionId == null
                            ? Constants.NO_INFORMATION_AVAILABLE
                            : FULLY_QUALIFIED_GENE_BASE_URL + "/" + geneAccessionId);  // IMPC gene link
        } else {
            row.add(Constants.NO_INFORMATION_AVAILABLE);
            row.add(Constants.NO_INFORMATION_AVAILABLE);
            row.add(Constants.NO_INFORMATION_AVAILABLE);
        }
        row.add(publication.getTitle());                                 // Publication title
        row.add(publication.getJournalInfo().getJournal().getTitle());   // Journal
        row.add(publication.getPmid());                                  // PMID
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        row.add(f.format(publication.getFirstPublicationDate()));        // Publication date

        List<String> grantIds = new ArrayList<>();
        List<String> grantAgencies = new ArrayList<>();
        if ((publication.getGrantsList() != null) && ( ! publication.getGrantsList().isEmpty())) {
            for (Grant grant : publication.getGrantsList()) {
                grantIds.add(grant.getGrantId());
                grantAgencies.add(grant.getAgency());
            }
            row.add(StringUtils.join(grantIds, "|"));           // Grant Ids
            row.add(StringUtils.join(grantAgencies, "|"));      // Grant agencies
        } else {
            row.add(Constants.NO_INFORMATION_AVAILABLE);
            row.add(Constants.NO_INFORMATION_AVAILABLE);
        }
        String publicationLink = getFirstEuropePmcPublicationUrl(publication);
        row.add((publicationLink == null) || (publicationLink.isEmpty())
                        ? Constants.NO_INFORMATION_AVAILABLE
                        : publicationLink);
        row.add(publication.isConsortiumPaper() ? "Yes" : "No");          // Is consortium paper

        return row;
    }


    @Override
    public List<String> getHeading() {

        final List<String> headings = Arrays.asList(new String[] {
             "MGI allele name",
             "MGI allele accession id",
             "IMPC gene link",
             "Publication title",
             "Journal",
             "PMID",
             "Publication date",
             "Grant id(s)",
             "Grant agencies",
             "Publication link",
             "Is consortium paper"
        });

        return headings;
    }
}