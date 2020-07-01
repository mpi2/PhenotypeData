/*******************************************************************************
 *  Copyright © 2018 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.ri.core.services;

import org.junit.Before;
import org.junit.Test;
import org.mousephenotype.cda.ri.core.entities.Gene;
import org.mousephenotype.cda.ri.core.entities.GeneSent;
import org.mousephenotype.cda.ri.core.entities.Summary;
import org.mousephenotype.cda.ri.core.entities.SummaryWithDecoration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Updated by mrelac on 06/12/2018.
 */

@SpringBootTest
public class GenerateServiceTest extends BaseTest {

    public static final String user1 = "user1@ebi.ac.uk";
    public static final String user2 = "user2@ebi.ac.uk";
    public static final String user3 = "user3@ebi.ac.uk";
    public static final String user4 = "user4@ebi.ac.uk";

    @Autowired
    private GenerateService generateService;


    @Before
    public void setUp() throws Exception {
    }


    @Test
    public void getAllSummariesByEmailAddress() {

        Set<String> expectedGenesUser1 = new HashSet<>(Arrays.asList(new String[]{"MGI:103576", "MGI:1919199", "MGI:2443658"}));
        Set<String> expectedGenesUser2 = new HashSet<>(Arrays.asList(new String[]{"MGI:3576659", "MGI:2444824"}));

        Map<String, Summary> summaries = generateService.getAllSummariesByEmailAddress();

        // user1
        Summary     summary     = summaries.get(user1);
        Set<String> actualGenes = new HashSet<>(summary.getGenes().stream().map(Gene::getMgiAccessionId).collect(Collectors.toList()));
        assertNotNull(summary);
        assertEquals(user1, summary.getEmailAddress());
        assertEquals(3, summary.getGenes().size());
        expectedGenesUser1.removeAll(actualGenes);
        assertEquals(0, expectedGenesUser1.size());

        // user2
        summary = summaries.get(user2);
        actualGenes = new HashSet<>(summary.getGenes().stream().map(Gene::getMgiAccessionId).collect(Collectors.toList()));
        assertNotNull(summary);
        assertEquals(user2, summary.getEmailAddress());
        assertEquals(2, summary.getGenes().size());
        expectedGenesUser2.removeAll(actualGenes);
        assertEquals(0, expectedGenesUser2.size());

        // user3
        summary = summaries.get(user3);
        assertEquals(user3, summary.getEmailAddress());
        assertEquals(0, summary.getGenes().size());

        // user4
        summary = summaries.get(user4);
        assertEquals(user4, summary.getEmailAddress());
        assertEquals(0, summary.getGenes().size());
    }


    @Test
    public void getGeneSentStatusByGeneAccessionId() {

        // user1
        Map<String, GeneSent> genesSent = generateService.getGeneSentStatusByGeneAccessionId(user1);
        assertEquals(3, genesSent.size());

        //      MGI:103576
        String   acc      = "MGI:103576";
        GeneSent geneSent = genesSent.get(acc);
        assertNotNull(geneSent);
        assertEquals(geneSent.getMgiAccessionId(), acc);

        //      MGI:1919199
        acc = "MGI:1919199";
        geneSent = genesSent.get(acc);
        assertNotNull(geneSent);
        assertEquals(geneSent.getMgiAccessionId(), acc);

        //      MGI:2443658
        acc = "MGI:2443658";
        geneSent = genesSent.get(acc);
        assertNotNull(geneSent);
        assertEquals(geneSent.getMgiAccessionId(), acc);


        // user2
        genesSent = generateService.getGeneSentStatusByGeneAccessionId(user2);
        assertEquals(2, genesSent.size());

        //      MGI:2444824
        acc = "MGI:2444824";
        geneSent = genesSent.get(acc);
        assertNotNull(geneSent);
        assertEquals(geneSent.getMgiAccessionId(), acc);

        //      MGI:3576659
        acc = "MGI:3576659";
        geneSent = genesSent.get(acc);
        assertNotNull(geneSent);
        assertEquals(geneSent.getMgiAccessionId(), acc);


        // user3
        genesSent = generateService.getGeneSentStatusByGeneAccessionId(user3);
        assertEquals(0, genesSent.size());


        // user4
        genesSent = generateService.getGeneSentStatusByGeneAccessionId(user4);
        assertEquals(0, genesSent.size());
    }


    @Test
    public void getsummaryByEmailAddress() {

        Set<String> expectedGenesUser1 = new HashSet<>(Arrays.asList(new String[]{"MGI:103576", "MGI:1919199", "MGI:2443658"}));
        Set<String> expectedGenesUser2 = new HashSet<>(Arrays.asList(new String[]{"MGI:3576659", "MGI:2444824"}));
        Summary     summary;
        Set<String> actualGenes;

        // user1
        summary = generateService.getsummaryByEmailAddress(user1);
        actualGenes = new HashSet<>(summary.getGenes().stream().map(Gene::getMgiAccessionId).collect(Collectors.toList()));
        assertNotNull(summary);
        assertEquals(user1, summary.getEmailAddress());
        assertEquals(3, summary.getGenes().size());
        expectedGenesUser1.removeAll(actualGenes);
        assertEquals(0, expectedGenesUser1.size());

        // user2
        summary = generateService.getsummaryByEmailAddress(user2);
        actualGenes = new HashSet<>(summary.getGenes().stream().map(Gene::getMgiAccessionId).collect(Collectors.toList()));
        assertNotNull(summary);
        assertEquals(user2, summary.getEmailAddress());
        assertEquals(2, summary.getGenes().size());
        expectedGenesUser2.removeAll(actualGenes);
        assertEquals(0, expectedGenesUser2.size());

        // user3
        summary = generateService.getsummaryByEmailAddress(user3);
        assertEquals(user3, summary.getEmailAddress());
        assertEquals(0, summary.getGenes().size());

        // user4
        summary = generateService.getsummaryByEmailAddress(user4);
        assertEquals(user4, summary.getEmailAddress());
        assertEquals(0, summary.getGenes().size());
    }


    @Test
    public void getSummaryContentWithGenes() {

        String                content;
        Summary               summary;
        SummaryWithDecoration summaryWithDecoration;
        boolean               inHtml;

        // user1
        summary = generateService.getsummaryByEmailAddress(user1);
        Map<String, GeneSent> genesSent = generateService.getGeneSentStatusByGeneAccessionId(user1);
        summaryWithDecoration = new SummaryWithDecoration(summary, genesSent);
        inHtml = false;
        content = generateService.getSummaryContent(BaseTestConfig.PA_BASE_URL, summaryWithDecoration, inHtml);
        assertEquals(expectedSummaryContentNoHtml, content);

        inHtml = true;
        content = generateService.getSummaryContent(BaseTestConfig.PA_BASE_URL, summaryWithDecoration, inHtml);
        assertEquals(expectedSummaryContentInHtml, content);
    }


    @Test
    public void getSummaryContentNoGenes() {

        String  content;
        Summary summary;
        boolean inHtml;

        // user3
        summary = generateService.getsummaryByEmailAddress(user3);
        inHtml = false;
        content = generateService.getSummaryContent(BaseTestConfig.PA_BASE_URL, summary, inHtml);
        assertEquals(expectedSummaryContentNoHtmlNoGenes, content);

        inHtml = true;
        content = generateService.getSummaryContent(BaseTestConfig.PA_BASE_URL, summary, inHtml);
        assertEquals(expectedSummaryContentInHtmlNoGenes, content);
    }


    @Test
    public void getWelcomeContent() {

        boolean inHtml;
        String content;

        inHtml = false;
        content = generateService.getWelcomeContent(inHtml);
        assertEquals(expectedWelcomeContentNoHtml, content);

        inHtml = true;
        content = generateService.getWelcomeContent(inHtml);
        assertEquals(expectedWelcomeContentInHtml, content);

    }


    @Test
    public void getEmailEpilogue() {

        boolean inHtml;
        String content;

        inHtml = false;
        content = generateService.getEmailEpilogue(inHtml);
        assertEquals(expectedEmailEpilogueContentNoHtml, content);

        inHtml = true;
        content = generateService.getEmailEpilogue(inHtml);
        assertEquals(expectedEmailEpilogueContentInHtml, content);
    }


    // static strings
    private static String expectedSummaryContentNoHtml =
            "Dear colleague,\n" +
                    "\n" +
                    "\n" +
                    "Below please find a summary of the IMPC genes for which you have registered interest.\n" +
                    "\n" +
                    "\n" +
                    "You have previously joined the IMPC 'Register Interest' list, which records your email address and genes for which you would like updates on mouse knockout, production, and phenotyping.\n" +
                    "\n" +
                    "\n" +
                    "You may manage the list of genes for which you have registered interest by visiting the IMPC 'summary' page at https://dev.mousephenotype.org/data/summary.\n" +
                    "\n" +
                    "\n" +
                    "Gene Symbol\tGene MGI Accession Id\tAssignment Status\tNull Allele Production\tConditional Allele Production\tPhenotyping Data Available\n" +
                    "Ccl11\tMGI:103576\tWithdrawn *\tNone\tNone *\tNo\t\n" +
                    "Cers5\tMGI:1919199\tSelected for production and phenotyping\tGenotype confirmed mice\tGenotype confirmed mice\tYes\t\n" +
                    "Prr14l\tMGI:2443658\tSelected for production and phenotyping\tGenotype confirmed mice *\tNone\tNo *\t\n" +
                    "\n" +
                    "* Gene assignment status has changed since the last e-mail sent to you.\n" +
                    "\n" +
                    "You may review our e-mail list privacy policy at:\n" +
                    "\n" +
                    "https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\n" +
                    "\n" +
                    "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
                    "\n" +
                    "Best Regards,\n" +
                    "\n" +
                    "\n" +
                    "The IMPC team";


    private static String expectedSummaryContentInHtml        =
            "Dear colleague,\n" +
                    "<br /><br />Below please find a summary of the IMPC genes for which you have registered interest.\n" +
                    "<br /><br />You have previously joined the IMPC <i>Register Interest</i> list, which records your email address and genes for which you would like updates on mouse knockout, production, and phenotyping.\n" +
                    "<br /><br />You may manage the list of genes for which you have registered interest by visiting the IMPC <a href=\"https://dev.mousephenotype.org/data/summary\">summary</a> page at https://dev.mousephenotype.org/data/summary.\n" +
                    "<br /><br /><style>  table {    font-family: arial, sans-serif;    border-collapse: collapse;    width: 100%;}td, th {    border: 1px solid #dddddd;    text-align: left;    padding: 8px;}tr:nth-child(even) {    background-color: #dddddd;}</style><table id=\"genesTable\"><tr><th>Gene Symbol</th><th>Gene MGI Accession Id</th><th>Assignment Status</th><th>Null Allele Production</th><th>Conditional Allele Production</th><th>Phenotyping Data Available</th></tr><tr><td><a href=\"https://dev.mousephenotype.org/data/genes/MGI:103576\" alt =\"https://dev.mousephenotype.org/data/genes/MGI:103576\">Ccl11</a></td><td><a href=\"http://www.informatics.jax.org/marker/MGI:103576\" alt =\"http://www.informatics.jax.org/marker/MGI:103576\">MGI:103576</a></td><td>Withdrawn *</td><td>None</td><td>None *</td><td>No</td><tr><td><a href=\"https://dev.mousephenotype.org/data/genes/MGI:1919199\" alt =\"https://dev.mousephenotype.org/data/genes/MGI:1919199\">Cers5</a></td><td><a href=\"http://www.informatics.jax.org/marker/MGI:1919199\" alt =\"http://www.informatics.jax.org/marker/MGI:1919199\">MGI:1919199</a></td><td>Selected for production and phenotyping</td><td><a href=\"https://dev.mousephenotype.org/data/search/allele2?kw=&quot;MGI:1919199&quot;\" alt =\"https://dev.mousephenotype.org/data/search/allele2?kw=&quot;MGI:1919199&quot;\">Genotype confirmed mice</a></td><td><a href=\"https://dev.mousephenotype.org/data/search/allele2?kw=&quot;MGI:1919199&quot;\" alt =\"https://dev.mousephenotype.org/data/search/allele2?kw=&quot;MGI:1919199&quot;\">Genotype confirmed mice</a></td><td><a href=\"https://dev.mousephenotype.org/data/genes/MGI:1919199#section-associations\" alt =\"https://dev.mousephenotype.org/data/genes/MGI:1919199#section-associations\">Yes</a></td><tr><td><a href=\"https://dev.mousephenotype.org/data/genes/MGI:2443658\" alt =\"https://dev.mousephenotype.org/data/genes/MGI:2443658\">Prr14l</a></td><td><a href=\"http://www.informatics.jax.org/marker/MGI:2443658\" alt =\"http://www.informatics.jax.org/marker/MGI:2443658\">MGI:2443658</a></td><td>Selected for production and phenotyping</td><td><a href=\"https://dev.mousephenotype.org/data/search/allele2?kw=&quot;MGI:2443658&quot;\" alt =\"https://dev.mousephenotype.org/data/search/allele2?kw=&quot;MGI:2443658&quot;\">Genotype confirmed mice *</a></td><td>None</td><td>No *</td></table><br />* Gene assignment status has changed since the last e-mail sent to you.<br /><br />You may review our e-mail list privacy policy at:<br /><br /><div><a href=\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\" alt =\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\">https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices</a></div><br />For further information / enquiries please write to <a href=\"mailto: mouse-helpdesk@ebi.ac.uk\">mouse-helpdesk@ebi.ac.uk</a>.<br /><br />Best Regards,\n" +
                    "<br /><br />The IMPC team";

    private static String expectedSummaryContentNoHtmlNoGenes =
            "Dear colleague,\n" +
                    "\n" +
                    "\n" +
                    "Below please find a summary of the IMPC genes for which you have registered interest.\n" +
                    "\n" +
                    "\n" +
                    "You have previously joined the IMPC 'Register Interest' list, which records your email address and genes for which you would like updates on mouse knockout, production, and phenotyping.\n" +
                    "\n" +
                    "\n" +
                    "You may manage the list of genes for which you have registered interest by visiting the IMPC 'summary' page at https://dev.mousephenotype.org/data/summary.\n" +
                    "\n" +
                    "\n" +
                    "Gene Symbol\tGene MGI Accession Id\tAssignment Status\tNull Allele Production\tConditional Allele Production\tPhenotyping Data Available\n" +
                    "\n" +
                    "You may review our e-mail list privacy policy at:\n" +
                    "\n" +
                    "https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\n" +
                    "\n" +
                    "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
                    "\n" +
                    "Best Regards,\n" +
                    "\n" +
                    "\n" +
                    "The IMPC team";


    private static String expectedSummaryContentInHtmlNoGenes =
            "Dear colleague,\n" +
                    "<br /><br />Below please find a summary of the IMPC genes for which you have registered interest.\n" +
                    "<br /><br />You have previously joined the IMPC <i>Register Interest</i> list, which records your email address and genes for which you would like updates on mouse knockout, production, and phenotyping.\n" +
                    "<br /><br />You may manage the list of genes for which you have registered interest by visiting the IMPC <a href=\"https://dev.mousephenotype.org/data/summary\">summary</a> page at https://dev.mousephenotype.org/data/summary.\n" +
                    "<br /><br /><style>  table {    font-family: arial, sans-serif;    border-collapse: collapse;    width: 100%;}td, th {    border: 1px solid #dddddd;    text-align: left;    padding: 8px;}tr:nth-child(even) {    background-color: #dddddd;}</style><table id=\"genesTable\"><tr><th>Gene Symbol</th><th>Gene MGI Accession Id</th><th>Assignment Status</th><th>Null Allele Production</th><th>Conditional Allele Production</th><th>Phenotyping Data Available</th></tr></table><br />You may review our e-mail list privacy policy at:<br /><br /><div><a href=\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\" alt =\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\">https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices</a></div><br />For further information / enquiries please write to <a href=\"mailto: mouse-helpdesk@ebi.ac.uk\">mouse-helpdesk@ebi.ac.uk</a>.<br /><br />Best Regards,\n" +
                    "<br /><br />The IMPC team";

    private static String expectedWelcomeContentNoHtml =
        "Dear colleague,\n" +
                "\n" +
                "Thank you for registering your interest in genes with the IMPC. As a benefit of having registered, you will receive an e-mail notification whenever the status of the gene(s) for which you have registered interest changes.\n" +
                "\n" +
                "You may register or unregister for any genes of your choice by either:\n" +
                "\t* clicking the link in the Search page's Register column corresponding to the gene of interest, or \n" +
                "\t* clicking on the button on the Gene page just below the gene name\n" +
                "\n" +
                "Clicking the My genes link on the Search page will take you to a page showing the genes for which you have already registered. On this page you may:\t* unregister by clicking the Unregister button\n" +
                "\t* reset your password by clicking the Reset password button\n" +
                "\t* delete your account by clicking theDelete account button Warning: deleting your account will delete all genes for which you have registered interest, as well as any history. This action is permanent and cannot be undone, so please use with caution.\n" +
                "\n" +
                "You may review our e-mail list privacy policy at:\n" +
                "\n" +
                "https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\n" +
                "\n" +
                "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
                "\n" +
                "Best Regards,\n" +
                "\n" +
                "\n" +
                "The IMPC team";

    private static String expectedWelcomeContentInHtml =
            "Dear colleague,<br /><br />Thank you for registering your interest in genes with the IMPC. As a benefit of having registered, you will receive an e-mail notification whenever the status of the gene(s) for which you have registered interest changes.\n" +
                    "\n" +
                    "You may register or unregister for any genes of your choice by either:\n" +
                    "<ul><li>clicking the link in the <b>Search</b> page's <i>Register</i> column corresponding to the gene of interest, or </li><li>clicking on the button on the <b>Gene</b> page just below the gene name</li></ul>\n" +
                    "Clicking the <i>My genes</i> link on the <b>Search</b> page will take you to a page showing the genes for which you have already registered. On this page you may:<ul><li>unregister by clicking the <i>Unregister</i> button</li><li>reset your password by clicking the <i>Reset password</i> button</li><li>delete your account by clicking the<i>Delete account</i> button <b><i>Warning: deleting your account will delete all genes for which you have registered interest, as well as any history. This action is permanent and cannot be undone, so please use with caution.</i></b></ul>\n" +
                    "\n" +
                    "You may review our e-mail list privacy policy at:<br /><br /><div><a href=\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\" alt =\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\">https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices</a></div><br />For further information / enquiries please write to <a href=\"mailto: mouse-helpdesk@ebi.ac.uk\">mouse-helpdesk@ebi.ac.uk</a>.<br /><br />Best Regards,\n" +
                    "<br /><br />The IMPC team";

    private static String expectedEmailEpilogueContentNoHtml =
            "You may review our e-mail list privacy policy at:\n" +
                    "\n" +
                    "https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\n" +
                    "\n" +
                    "For further information / enquiries please write to mouse-helpdesk@ebi.ac.uk.\n" +
                    "\n" +
                    "Best Regards,\n" +
                    "\n" +
                    "\n" +
                    "The IMPC team";

    private static String expectedEmailEpilogueContentInHtml =
            "You may review our e-mail list privacy policy at:<br /><br /><div><a href=\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\" alt =\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\">https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices</a></div><br />For further information / enquiries please write to <a href=\"mailto: mouse-helpdesk@ebi.ac.uk\">mouse-helpdesk@ebi.ac.uk</a>.<br /><br />Best Regards,\n" +
                    "<br /><br />The IMPC team";

    private static String expectedSummaryHtmlTableContentNoHtmlWithGenes =
            "Gene Symbol\tGene MGI Accession Id\tAssignment Status\tNull Allele Production\tConditional Allele Production\tPhenotyping Data Available\n" +
                    "Ccl11\tMGI:103576\tWithdrawn\tNone\tNone\tNo\t\n" +
                    "Cers5\tMGI:1919199\tSelected for production and phenotyping\tGenotype confirmed mice\tGenotype confirmed mice\tYes\t\n" +
                    "Prr14l\tMGI:2443658\tSelected for production and phenotyping\tGenotype confirmed mice\tNone\tNo\t\n";
}