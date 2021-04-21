package org.mousephenotype.cda.ri.services;

import org.junit.Test;
import org.mousephenotype.cda.ri.exceptions.InterestException;
import org.mousephenotype.cda.ri.pojo.Summary;
import org.mousephenotype.cda.ri.pojo.SummaryDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileSystemUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.util.AssertionErrors.fail;

public class MailServiceTest extends BaseTest {
    public static final String user1 = "user1@ebi.ac.uk";
    public static final String user2 = "user2@ebi.ac.uk";
    public static final String user3 = "user3@ebi.ac.uk";
    public static final String user4 = "user4@ebi.ac.uk";
    public static final String user5 = "user5@ebi.ac.uk";

    @Autowired
    private MailService mailService;

    @Autowired
    private SummaryService summaryService;


    @Test(expected = InterestException.class)
    public void checkerOutputDirExistsAndIsNotADirectory() throws Exception {
        File outfile = null;
        try {
            outfile = new File("checkerOutputDirExistsAndIsNotADirectory");
            FileSystemUtils.deleteRecursively(outfile);
            if (outfile.exists()) {
                fail("Unable to delete output directory '" + outfile.getAbsolutePath() + "'");
            }
            if (outfile.exists()) {
                outfile.delete();
            }
            outfile.createNewFile();

            mailService.checker(outfile.getAbsolutePath());
            fail("Expected InterestException");
        } finally {
            FileSystemUtils.deleteRecursively(outfile);
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

    /*
     * NOTE: As there is no easy way to mock the geneService that provides the current gene statuses, there is a
     * possibility that this test will indicate failure when it shouldn't. The expected result strings were
     * captured with the geneService statuses as of 08-Apr-2021. If any of those statuses change, the comparison
     * will fail. In that case, after proving the test data should be updated, simply update the expected status string(s).
     *
     * For e-mails in HTML format, here is a great viewer that you can paste the HTML text into:
     * https://codebeautify.org/htmlviewer/#
     *
     * This test expects 3 files to be generated, with only the changed statuses as shown:
     * Filename          Format       Changed statuses (with " *" appended)
     * user2@ebi.ac.uk   Plain text   Sirpb1a -> Crispr status = "Genotype confirmed mice *      "
     * user3@ebi.ac.uk   HTML         Akt1    -> No statuses are changed. There should be no " *" appended.
     *                                Akt1s1  -> All statuses are changed and should have " *" appended to each.
     *                                Akt2    -> Conditional status = "Started *"
     *                                        -> Crispr status = "None *"
     *                                Aktip   -> Assignment = "Selected for production *"
     *                                        -> Null status = "Genotype confirmed mice *"
     *                                        -> Phenotyping = "Yes *"
     * user5@ebi.ac.uk   Plain text   Akt2    -> All statuses are changed and should have " *" appended to each.
     *
     * @throws Exception
     */
    @Test
    public void checkerOutputDirExistsAndIsEmpty() throws Exception {
        File outdir = null;
        try {
            outdir = new File("checkerOutputDirExistsAndIsEmpty");
            FileSystemUtils.deleteRecursively(outdir);
            if (outdir.exists()) {
                fail("Expected " + outdir.getAbsolutePath() + " to be empty.");
            }
            Files.createDirectories(outdir.toPath());
            mailService.checker(outdir.getAbsolutePath());

            // Did we get the expected files?
            Set<String> expectedFiles = new HashSet<>(Arrays.asList(user2, user3, user5));
            Set<String> actualFiles   = _getFilenames(outdir.getAbsolutePath());
            assertTrue("Expected " + expectedFiles.size() + " files", expectedFiles.size() == actualFiles.size());
            assertEquals(expectedFiles, actualFiles);
            assertEquals(user2Expected, _getFileContent(Paths.get(outdir.getAbsolutePath(), user2)));
            assertEquals(user3Expected, _getFileContent(Paths.get(outdir.getAbsolutePath(), user3)));
            assertEquals(user5Expected, _getFileContent(Paths.get(outdir.getAbsolutePath(), user5)));
        } finally {
            FileSystemUtils.deleteRecursively(outdir);
        }
    }

    /**
     * This test generates a real e-mail as a full-scale integration test. As such it is usually commented out.
     * Uncomment it and provide a valid e-mail address to which you have access to manually review the content.
     * The generated summary is based on user3 so content can be manually checked against user3Expected.
     */
//    @Test
//    public void generateAndSendInHtml() {
//        final String testEmailAddress = "xxx@ebi.ac.uk";
//        Summary summary = summaryService.getSummaryByContact(summaryService.getContact(user3));
//        Map<String, SummaryDetail> lastSdsByAcc = mailService.getLastSdsByAcc(user3);
//        summary.setEmailAddress(testEmailAddress);
//        mailService.generateAndSendSummary(summary, lastSdsByAcc);
//    }

    /**
     * This test generates a real e-mail as a full-scale integration test. As such it is usually commented out.
     * Uncomment it and provide a valid e-mail address to which you have access to manually review the content.
     * The generated summary is based on user2 (plain text e-mail) so content can be manually checked against
     * user2Expected.
     */
//    @Test
//    public void generateAndSendInPlain() {
//        final String testEmailAddress = "xxx@ebi.ac.uk";
//        Summary summary = summaryService.getSummaryByContact(summaryService.getContact(user2));
//        Map<String, SummaryDetail> lastSdsByAcc = mailService.getLastSdsByAcc(user2);
//        summary.setEmailAddress(testEmailAddress);
//        mailService.generateAndSendSummary(summary, lastSdsByAcc);
//    }

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
                "<br /><br />Below please find a summary of the IMPC genes for which you have registered interest.\n";
        Summary summary = summaryService.getSummaryByContact(summaryService.getContact(user1));
        // We're testing html content, not changes, so an empty HashMap should work fine.
        String actual = mailService.generateSummary(summary, new HashMap<>());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void generateSummaryPlain() {
        final String expected =
            "Dear colleague,\n" +
                "\n" +
                "\n" +
                "Below please find a summary of the IMPC genes for which you have registered interest.";
        Summary summary = summaryService.getSummaryByContact(summaryService.getContact(user2));
        // We're testing html content, not changes, so an empty HashMap should work fine.
        String actual = mailService.generateSummary(summary, new HashMap<>());
        assertTrue(actual.contains(expected));
    }

    @Test
    public void generateSummaryNonexistentContact() {
        String                     expected     = "No summary was found for that contact";
        Summary                    summary      = summaryService.getSummaryByContact(summaryService.getContact("xxx"));
        Map<String, SummaryDetail> lastSdsByAcc = mailService.getLastSdsByAcc("xxx");
        String                     actual       = mailService.generateSummary(summary, lastSdsByAcc);
        assertEquals(expected, actual);
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


    // PRIVATE METHODS


    private String _getFileContent(Path path) throws IOException {

        StringBuffer fileContent = new StringBuffer();
        String       line;
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toAbsolutePath().toString()))) {
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
            return fileContent.toString().substring(0, fileContent.length() - 1);  // Skip the trailing newline.
        }
    }

    private Set<String> _getFilenames(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            Set<String> files = stream
                .filter(file -> !Files.isDirectory(file))
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toSet());
            return files;
        }
    }


    // EXPECTED STRINGS


    final String user2Expected =
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
            "\"Gene Symbol\"  \"Gene Accession Id\"  \"Gene Assignment               \"  \"ES Cell Null Allele Production Status\"  \"ES Cell Conditional Allele Production Status\"  \"Crispr Allele Production Status\"  \"Phenotyping Data Available\"\n" +
            " Ano5           MGI:3576659          None                              None                                     Started                                         Withdrawn                          No                        \n" +
            " Sirpb1a        MGI:2444824          Selected for production           None                                     None                                            Genotype confirmed mice *          Yes                       \n" +
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

    final String user3Expected =
        "Dear colleague,\n" +
            "<br /><br />Below please find a summary of the IMPC genes for which you have registered interest.\n" +
            "<br /><br />You have previously joined the IMPC <i>Register Interest</i> list, which records your email address and genes for which you would like updates on mouse knockout, production, and phenotyping.\n" +
            "<br /><br />You may manage the list of genes for which you have registered interest by visiting the IMPC <a href=\"https://dev.mousephenotype.org/data/summary\">summary</a> page at https://dev.mousephenotype.org/data/summary.\n" +
            "<br /><br /><style>  table {    font-family: arial, sans-serif;    border-collapse: collapse;    width: 100%;}td, th {    border: 1px solid #dddddd;    text-align: left;    padding: 8px;}tr:nth-child(even) {    background-color: #dddddd;}</style><table id=\"genesTable\"><tr><th>Gene Symbol</th><th>Gene Accession Id</th><th>Gene Assignment</th><th>ES Cell Null Allele Production Status</th><th>ES Cell Conditional Allele Production Status</th><th>Crispr Allele Production Status</th><th>Phenotyping Data Available</th></tr><td>Akt1</td><td>MGI:87986</td><td>Selected for production</td><td>None</td><td>None</td><td>None</td><td>No</td></tr><td>Akt1s1</td><td>MGI:1914855</td><td>Selected for production *</td><td>None *</td><td>Genotype confirmed mice *</td><td>Withdrawn *</td><td>No *</td></tr><td>Akt2</td><td>MGI:104874</td><td>Selected for production</td><td>None</td><td>Started *</td><td>None *</td><td>Yes</td></tr><td>Aktip</td><td>MGI:3693832</td><td>Selected for production *</td><td>Genotype confirmed mice *</td><td>Genotype confirmed mice</td><td>None</td><td>Yes *</td></tr></table><br />* Indicates a status has changed since the last e-mail sent to you.<br /><br />You may review our e-mail list privacy policy at:<br /><br /><div><a href=\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\" alt=\"https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices\">https://www.ebi.ac.uk/data-protection/privacy-notice/impc-mailservices</a></div><br />For further information / enquiries please write to <a href=\"mailto: mouse-helpdesk@ebi.ac.uk\">mouse-helpdesk@ebi.ac.uk</a>.<br /><br />Best Regards,\n" +
            "<br /><br />The IMPC team";

    final String user5Expected =
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
            "\"Gene Symbol\"  \"Gene Accession Id\"  \"Gene Assignment               \"  \"ES Cell Null Allele Production Status\"  \"ES Cell Conditional Allele Production Status\"  \"Crispr Allele Production Status\"  \"Phenotyping Data Available\"\n" +
            " Akt2           MGI:104874           Selected for production *         None *                                   Started *                                       None *                             Yes *                     \n" +
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
}