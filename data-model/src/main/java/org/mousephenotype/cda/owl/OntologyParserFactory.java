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

package org.mousephenotype.cda.owl;

import org.apache.solr.client.solrj.SolrClient;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.configurationprocessor.json.JSONException;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by ilinca on 29/03/2017.
 */
/**
 * pdsimplify: This class refers to old Phenodigm objects or db
 */
public class OntologyParserFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String owlpath;
    private DataSource komp2DataSource;

    @Autowired
    @Qualifier("phenodigmCore")
    SolrClient phenodigmCore;


    public OntologyParserFactory(DataSource komp2DataSource, String owlpath){

        this.komp2DataSource = komp2DataSource;
        this.owlpath = owlpath;

    }

    // Properties we want to follow to get MA terms form MP
    public static final Set<OWLObjectPropertyImpl> VIA_PROPERTIES = new HashSet<>(Arrays.asList(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/BFO_0000052")),
            new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/BFO_0000070")),
            new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/mp/mp-logical-definitions#inheres_in_part_of"))));


    public static final List<String> TOP_LEVEL_MP_TERMS = new ArrayList<>(Arrays.asList("MP:0010768", "MP:0002873", "MP:0001186", "MP:0003631",
            "MP:0005367",  "MP:0005369", "MP:0005370", "MP:0005371", "MP:0005377", "MP:0005378", "MP:0005375", "MP:0005376",
            "MP:0005379", "MP:0005380",  "MP:0005381", "MP:0005384", "MP:0005385", "MP:0005382", "MP:0005388", "MP:0005389", "MP:0005386",
            "MP:0005387", "MP:0005391",  "MP:0005390", "MP:0005394", "MP:0005397", "MP:0010771"));

    public static final Set<String> TOP_LEVEL_MA_TERMS = new HashSet<>(Arrays.asList("MA:0000004", "MA:0000007", "MA:0000009",
            "MA:0000010", "MA:0000012", "MA:0000014", "MA:0000016", "MA:0000017", "MA:0000325", "MA:0000326", "MA:0000327",
            "MA:0002411", "MA:0002418", "MA:0002431", "MA:0002711", "MA:0002887", "MA:0002405"));

    public static final List<String> TREE_TOP_LEVEL_MA_TERMS = new ArrayList<>(Arrays.asList("MA:0002433", "MA:0002450", "MA:0000003",
            "MA:0003001", "MA:0003002"));

    public static final Set<String> TOP_LEVEL_EMAPA_TERMS = new HashSet<>(Arrays.asList("EMAPA:16104", "EMAPA:16192", "EMAPA:16246",
            "EMAPA:16405", "EMAPA:16469", "EMAPA:16727", "EMAPA:16748", "EMAPA:16840", "EMAPA:17524", "EMAPA:31858"));

    public static final List<String> TREE_TOP_LEVEL_EMAPA_TERMS = new ArrayList<>(Arrays.asList("EMAPA:16039", "EMAPA:36040", "EMAPA:36037",
            "EMAPA:36031", "EMAPA:16042", "EMAPA:35949", "EMAPA:16103", "EMAPA:35868"));

    public static final Set<String> TOP_LEVEL_HP_TERMS = new HashSet<>(Arrays.asList( "HP:0002086","HP:0045027","HP:0001871","HP:0001939","HP:0001574","HP:0001608",
            "HP:0001626","HP:0025354","HP:0001507","HP:0025142","HP:0001197","HP:0003549",
            "HP:0025031","HP:0003011","HP:0040064","HP:0000924","HP:0000769","HP:0000707",
            "HP:0000818","HP:0000478","HP:0000598","HP:0002664","HP:0002715","HP:0000119",
            "HP:0000152"));

    // These parsers are used by several indexers so it makes sense to initialize them in one place, so that they don't get out of synch.
    public OntologyParser getMpParser() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, SQLException {
        return  new OntologyParser(owlpath + "/mp.owl", "MP", TOP_LEVEL_MP_TERMS, getWantedMPIds());
    }

    public OntologyParser getMpMaParser() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
        return new OntologyParser(owlpath + "/mp-ext-merged.owl", "MP", null, null);
    }

    public OntologyParser getMaParser() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, SQLException {
        return new OntologyParser(owlpath + "/ma.owl", "MA", TOP_LEVEL_MA_TERMS, getMaWantedIds());
    }

    public OntologyParser getEmapaParser() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, SQLException {
        return new OntologyParser(owlpath + "/emapa.owl", "EMAPA", TOP_LEVEL_EMAPA_TERMS, getEmapaWantedIds());
    }

    public OntologyParser getUberonParser() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, SQLException {
        return new OntologyParser(owlpath + "/uberon.owl", "UBERON", null, null);
    }

    public OntologyParser getMaParserWithTreeJson() throws OWLOntologyStorageException, IOException, SQLException, OWLOntologyCreationException, JSONException {

        OntologyParser parser = getMaParser();
        parser.fillJsonTreePath("MA:0002405", "/data/anatomy/", null, TREE_TOP_LEVEL_MA_TERMS, true); // postnatal mouse
        return parser;
    }

    public OntologyParser getEmapaParserWithTreeJson() throws OWLOntologyStorageException, IOException, SQLException, OWLOntologyCreationException, JSONException {

        OntologyParser parser = getEmapaParser();
        parser.fillJsonTreePath("EMAPA:25765", "/data/anatomy/", null, TREE_TOP_LEVEL_EMAPA_TERMS, true); // mouse
        return parser;
    }

    protected Set<String> getMaWantedIds() throws SQLException, OWLOntologyCreationException, OWLOntologyStorageException, IOException {

        Set<String> wantedIds = new HashSet<>();

        // Get MA terms from Sanger images
        PreparedStatement statement = komp2DataSource.getConnection().prepareStatement("SELECT DISTINCT (UPPER(TERM_ID)) AS TERM_ID FROM ANN_ANNOTATION");
        ResultSet res = statement.executeQuery();
        while (res.next()) {
            String r= res.getString("TERM_ID");
            if (r != null && r.startsWith("MA:")){
                wantedIds.add(r);
            }
        }
        // Add MA ids from IMPRESS
        wantedIds.addAll(getOntologyIds(8, komp2DataSource));

        // Get MA terms referenced by MP terms in the slim
        Set<String> wantedMp = getWantedMPIds();
        OntologyParser mpMaParser = getMpMaParser();

        for (String mpId: wantedMp){
            wantedIds.addAll( mpMaParser.getReferencedClasses(mpId, VIA_PROPERTIES, "MA"));
        }

        return wantedIds;

    }



    protected Set<String> getEmapaWantedIds() throws SQLException, OWLOntologyCreationException, OWLOntologyStorageException, IOException {

        Set<String> wantedIds = new HashSet<>();
        Set<String> emapIds = new HashSet<>();

        // In IMPRESS we have only EMAP ids
        emapIds.addAll(getOntologyIds(14, komp2DataSource));


        // Add EMAP terms from image annotations
        PreparedStatement statement = komp2DataSource.getConnection().prepareStatement("SELECT DISTINCT(UPPER(ontology_acc)) as TERM_ID FROM phenotype_parameter_ontology_annotation ppoa WHERE ppoa.ontology_db_id=?");
        statement.setInt(1, 14);
        ResultSet res = statement.executeQuery();
        while (res.next()) {
            String r = res.getString("TERM_ID");
            if (r.startsWith("EMAP:")){
                emapIds.add(r);
            }
        }
        emapIds.addAll(getOntologyIds(14, komp2DataSource));


        //Get EMAPA list by parsing EMAP list and check mapping txt ftp://ftp.hgu.mrc.ac.uk/pub/MouseAtlas/Anatomy/EMAP-EMAPA.txt
        Map<String,String> emapMap = getEmapToEmapaMap();
        for (String emapTerm: emapIds){
            wantedIds.add(emapMap.get(emapTerm));
        }

        // Get EMAPA terms from Sanger images
        statement = komp2DataSource.getConnection().prepareStatement("SELECT DISTINCT (UPPER(TERM_ID)) AS TERM_ID FROM ANN_ANNOTATION");
        res = statement.executeQuery();
        while (res.next()) {
            String r= res.getString("TERM_ID");
            if (r != null && r.startsWith("EMAPA:")){
                wantedIds.add(r);
            }
        }

        // We have no EMAPA terms referenced by MP terms in the slim

        return wantedIds;

    }


    /**
     *
     * @return all MP ids that we want in the slim
     * @throws SQLException
     */
    protected Set<String> getWantedMPIds() throws SQLException {

        // Select MP terms from images too
        Set<String> wantedIds = new HashSet<>();

        // Get mp terms from Sanger images
        PreparedStatement statement = komp2DataSource.getConnection().prepareStatement("SELECT DISTINCT (UPPER(TERM_ID)) AS TERM_ID, (UPPER(TERM_NAME)) as TERM_NAME FROM  IMA_IMAGE_TAG iit INNER JOIN ANN_ANNOTATION aa ON aa.FOREIGN_KEY_ID=iit.ID");
        ResultSet res = statement.executeQuery();
        while (res.next()) {
            String r = res.getString("TERM_ID");
            if (r.startsWith("MP:")) {
                wantedIds.add(r);
            }
        }

        //All MP terms we can have annotations to (from IMPRESS)
        wantedIds.addAll(getOntologyIds(5, komp2DataSource));

        return wantedIds;
    }

    /**
     * @author tudose
     * @param ontologyId database id. [14=emap, 5=mp, 8=ma]
     * @return
     * @throws SQLException
     */
    public List<String> getOntologyIds(Integer ontologyId, DataSource ds) throws SQLException{

        PreparedStatement statement = ds.getConnection().prepareStatement(
                "SELECT DISTINCT(ontology_acc) FROM phenotype_parameter_ontology_annotation ppoa WHERE ppoa.ontology_db_id=" + ontologyId);
        ResultSet res = statement.executeQuery();
        List<String> terms = new ArrayList<>();

        while (res.next()) {
            terms.add(res.getString("ontology_acc"));
        }

        return terms;
    }


    // EMAP to EMAPA mapping obtained by CK. Richard Baldock confirmed this is not maintained any more so we maintain it in our repo. Can add terms as needed.
    // Another way would be to use UBERON and get the EMAP and EMAPA cross refs from there.
    public Map<String,String> getEmapToEmapaMap()
            throws IOException {


        File emapEmapa = new File (owlpath + "/EMAP-EMAPA.txt") ;
        Scanner scan = new Scanner(emapEmapa);
        String line="";
        Map<String,String> emapMap = new HashMap<>();

        while (scan.hasNextLine())		{
            line = scan.nextLine();
            String[] split=line.split("\t");
            emapMap.put(split[0], split[2]);
        }

        return emapMap;
    }
}
