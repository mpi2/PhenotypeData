package org.mousephenotype.cda.ri.services;

import org.junit.Ignore;
import org.junit.Test;
import org.mousephenotype.cda.ri.entities.Contact;
import org.mousephenotype.cda.ri.exceptions.InterestException;
import org.mousephenotype.cda.ri.pojo.SmtpParameters;
import org.mousephenotype.cda.ri.pojo.Summary;
import org.mousephenotype.cda.ri.pojo.SummaryDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class MailServiceTest extends BaseTest {
    public static final String user1 = "user1@ebi.ac.uk";
    public static final String user2 = "user2@ebi.ac.uk";
    public static final String user3 = "user3@ebi.ac.uk";
    public static final String user4 = "user4@ebi.ac.uk";

    @Autowired
    private MailService mailService;

    @Autowired
    private SummaryService summaryService;

    @Autowired
    private SmtpParameters smtpParameters;


    @Test(expected = InterestException.class)
    public void checkerOutputDirExistsAndIsNotADirectory() throws Exception {
        File outfile = null;
        try {
            outfile = new File("checkerWithOutputDirWithChanges");
            if (outfile.exists()) {
                outfile.delete();
            }
            outfile.createNewFile();

            mailService.checker(outfile.getAbsolutePath());
            fail("Expected InterestException");
        } finally {
            if (outfile != null) outfile.delete();
        }
    }

    @Test(expected = InterestException.class)
    public void checkerOutputDirExistsAndIsNotEmpty() throws Exception {
        File outdir = null;
        try {
            outdir = new File("checkerOutputDirExistsAndIsNotEmpty");
            FileSystemUtils.deleteRecursively(outdir);
            if (outdir.exists()) {
                fail("Unable to delete output directory '" + outdir.getAbsolutePath() + "'");
            }
            Files.createDirectories(outdir.toPath());
            Path p       = Paths.get(outdir.getName(), "xxx");
            File outfile = p.toFile();
            outfile.createNewFile();
            mailService.checker(outdir.getAbsolutePath());
            fail("Expected InterestException");
        } finally {
            FileSystemUtils.deleteRecursively(outdir);
        }
    }

    @Ignore
    @Test
    public void checkerOutputDirExistsAndIsEmpty() throws Exception {
        File outdir  = null;
        File outfile = null;
        try {
            outdir = new File("checkerOutputDirExistsAndIsEmpty");
            Files.deleteIfExists(Paths.get(outdir.getPath()));
            if (outdir.exists()) {
                fail("Expected " + outdir.getAbsolutePath() + " to be empty.");
            }
            Files.createDirectories(outdir.toPath());
//            Path p = Paths.get(outdir.getName(), "xxx");
//            outfile = p.toFile();
//            outfile.createNewFile();
            mailService.checker(outdir.getAbsolutePath());
            fail("Expected InterestException");
        } finally {
            if (outfile != null) outfile.delete();
            if (outdir != null) outdir.delete();
        }
    }

    @Ignore
    @Test
    public void checkerOutputDirDoesNotExist() {

    }

    @Ignore
    @Test
    public void checkerWithOutputDirSomeChanges() throws Exception {
        File outfile = new File("checkerWithOutputDirWithChanges");
        if (outfile.exists()) {
            outfile.delete();
        }
        outfile.createNewFile();
        mailService.checker(outfile.getAbsolutePath());

        // Delete outfile when done.
    }

    @Ignore
    @Test
    public void checkerNoOutputDirSomeChanges() throws Exception {
        mailService.checker(null);
    }

    @Test
    public void generateEmailEpilogueInHtml() {
        final boolean inHtml = true;
        final String expected =
            "You may review our e-mail list privacy policy at:<br /><br /><div><a href=\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\" alt=\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\">https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices</a></div><br />For further information / enquiries please write to <a href=\"mailto: mouse-helpdesk@ebi.ac.uk\">mouse-helpdesk@ebi.ac.uk</a>.<br /><br />Best Regards,\n" +
                "<br /><br />The IMPC team";
        final String actual = MailService.generateEmailEpilogue(inHtml);
        assertEquals(expected, actual);
    }

    @Test
    public void generateEmailEpiloguePlain() {
        final boolean inHtml = false;
        final String expected =
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
        final String actual = MailService.generateEmailEpilogue(inHtml);
        assertEquals(expected, actual);
    }

    @Test
    public void generateSummaryInHtml() {
        final String expected =
            "Dear colleague,\n" +
                "<br /><br />Below please find a summary of the IMPC genes for which you have registered interest.\n" +
                "<br /><br />You have previously joined the IMPC <i>Register Interest</i> list, which records your email address and genes for which you would like updates on mouse knockout, production, and phenotyping.\n" +
                "<br /><br />You may manage the list of genes for which you have registered interest by visiting the IMPC <a href=\"https://dev.mousephenotype.org/data/summary\">summary</a> page at https://dev.mousephenotype.org/data/summary.\n" +
                "<br /><br /><style>  table {    font-family: arial, sans-serif;    border-collapse: collapse;    width: 100%;}td, th {    border: 1px solid #dddddd;    text-align: left;    padding: 8px;}tr:nth-child(even) {    background-color: #dddddd;}</style><table id=\"genesTable\"><tr><th>Gene Symbol</th><th>Gene Accession Id</th><th>Assignment Status</th><th>Conditional Allele Production Status</th><th>Null Allele Production Status</th><th>Crispr Allele Production Status</th><th>Phenotyping Data Available</th></tr><td>Ccl11</td><td>MGI:103576</td><td>Withdrawn</td><td>None</td><td>None</td><td>None</td><td>No</td></tr><td>Cers5</td><td>MGI:1919199</td><td>Selected for production</td><td>Genotype confirmed mice</td><td>Genotype confirmed mice</td><td>None</td><td>Yes</td></tr><td>Prr14l</td><td>MGI:2443658</td><td>Selected for production</td><td>None</td><td>None</td><td>Genotype confirmed mice</td><td>No</td></tr></table><br />* Indicates a status has changed since the last e-mail sent to you.<br /><br />You may review our e-mail list privacy policy at:<br /><br /><div><a href=\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\" alt=\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\">https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices</a></div><br />For further information / enquiries please write to <a href=\"mailto: mouse-helpdesk@ebi.ac.uk\">mouse-helpdesk@ebi.ac.uk</a>.<br /><br />Best Regards,\n" +
                "<br /><br />The IMPC team";
        Summary summary = summaryService.getSummaryByContact(summaryService.getContact(user1));
        String  actual  = mailService.generateSummary(summary);
        assertEquals(expected, actual);
    }

    @Test
    public void generateSummaryPlain() {
        final String expected =
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
                "\"Gene Symbol\"  \"Gene Accession Id\"  \"Assignment Status          \"  \"Conditional Allele Production Status\"  \"Null Allele Production Status\"  \"Crispr Allele Production Status\"  \"Phenotyping Data Available\"\n" +
                "\"Sirpb1a    \"  \"MGI:2444824      \"  \"Selected for production    \"  \"None                                \"  \"None                         \"  \"Genotype confirmed mice        \"  \"Yes                       \"\n" +
                "\"Ano5       \"  \"MGI:3576659      \"  \"None                       \"  \"Started                             \"  \"None                         \"  \"Withdrawn                      \"  \"No                        \"\n" +
                "\n" +
                "\n" +
                "* Indicates a status has changed since the last e-mail sent to you.\n" +
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
        Summary      summary = summaryService.getSummaryByContact(summaryService.getContact(user2));
        final String actual  = mailService.generateSummary(summary);
        assertEquals(expected, actual);
    }

    @Test
    public void generateSummaryNonexistentContact() {
        String  expected = "No summary was found for that contact";
        Summary summary  = summaryService.getSummaryByContact(summaryService.getContact("xxx"));
        String  actual   = mailService.generateSummary(summary);
        assertEquals(expected, actual);
    }

    @Test
    public void getAndUpdateSummaryIfChanged() {
        Contact contact = summaryService.getContact(user3);
        boolean inHtml  = true;
        List<String> expected = Arrays.asList(
            "<td>Akt1</td><td>MGI:87986</td><td>Selected for production</td><td>None</td><td>None</td><td>None</td><td>No</td>",
            "<td>Akt1s1</td><td>MGI:1914855</td><td>Selected for production *</td><td>Genotype confirmed mice *</td><td>None *</td><td>Withdrawn *</td><td>No *</td>",
            "<td>Akt2</td><td>MGI:104874</td><td>Selected for production</td><td>Started *</td><td>None</td><td>None *</td><td>Yes</td>",
            "<td>Aktip</td><td>MGI:3693832</td><td>Selected for production *</td><td>Genotype confirmed mice</td><td>Genotype confirmed mice *</td><td>None</td><td>Yes *</td>"
        );

        Summary actual = mailService.getAndUpdateSummaryIfChanged(contact);
        assertTrue("Expected non-null result", actual != null);
        assertEquals(expected.size(), actual.getDetails().size());
        actual.getDetails().sort(Comparator.comparing(SummaryDetail::getSymbol));
        for (int i = 0; i < actual.getDetails().size(); i++) {
            assertEquals(expected.get(i), actual.getDetails().get(i).toStringDecorated(inHtml));
        }
    }

    @Test
    public void generateWelcomeInHtml() {
        boolean inHtml = true;
        final String expected =
            "Dear colleague,<br /><br />Thank you for registering your interest in genes with the IMPC. As a benefit of having registered, you will receive an e-mail notification whenever the status of the gene(s) for which you have registered interest changes.\n" +
                "\n" +
                "You may register or unregister for any genes of your choice by either:\n" +
                "<ul><li>clicking the link in the <b>Search</b> page's <i>Register</i> column corresponding to the gene of interest, or </li><li>clicking on the button on the <b>Gene</b> page just below the gene name</li></ul>\n" +
                "Clicking the <i>My genes</i> link on the <b>Search</b> page will take you to a page showing the genes for which you have already registered. On this page you may:<ul><li>unregister by clicking the <i>Unregister</i> button</li><li>reset your password by clicking the <i>Reset password</i> button</li><li>delete your account by clicking the<i>Delete account</i> button <b><i>Warning: deleting your account will delete all genes for which you have registered interest, as well as any history. This action is permanent and cannot be undone, so please use with caution.</i></b></ul>\n" +
                "\n" +
                "You may review our e-mail list privacy policy at:<br /><br /><div><a href=\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\" alt=\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\">https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices</a></div><br />For further information / enquiries please write to <a href=\"mailto: mouse-helpdesk@ebi.ac.uk\">mouse-helpdesk@ebi.ac.uk</a>.<br /><br />Best Regards,\n" +
                "<br /><br />The IMPC team";
        String actual = "";
        try {
            actual = mailService.generateWelcome(inHtml);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        assertEquals(expected, actual);
    }

    @Test
    public void generateWelcomePlain() {
        boolean inHtml = false;
        final String expected =
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
        String actual = "";
        try {
            actual = mailService.generateWelcome(inHtml);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        assertEquals(expected, actual);
    }
}