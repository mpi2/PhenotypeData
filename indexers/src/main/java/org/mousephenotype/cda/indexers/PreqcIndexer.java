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
package org.mousephenotype.cda.indexers;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.imits.EncodedOrganisationConversionMap;
import org.mousephenotype.cda.solr.service.Allele2Service;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@EnableAutoConfiguration
public class PreqcIndexer extends AbstractIndexer implements CommandLineRunner {

	private final Logger logger = LoggerFactory.getLogger(PreqcIndexer.class);

	@NotNull
    @Value("${preqcXmlFilename}")
    private String preqcXmlFilename;

    @Autowired
    @Qualifier("preqcCore")
    SolrClient preqcCore;

    @Autowired
    @Qualifier("ontodbDataSource")
    DataSource ontodbDataSource;

    @Autowired
    Allele2Service allele2Service;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    @Autowired
    EncodedOrganisationConversionMap dccMapping;

    private static Map<String, String> geneSymbol2IdMapping = new HashMap<>();
    private static Map<String, AlleleDTO> alleleSymbol2NameIdMapping = new HashMap<>();
    private static Map<String, String> strainId2NameMapping = new HashMap<>();
    private static Map<String, String> pipelineSid2NameMapping = new HashMap<>();
    private static Map<String, String> procedureSid2NameMapping = new HashMap<>();
    private static Map<String, String> parameterSid2NameMapping = new HashMap<>();
    private Set<Integer> missingPhenotypeTerm = new HashSet<>();

    private static Map<String, String> projectMap = new HashMap<>();
    private static Map<String, String> resourceMap = new HashMap<>();

    private static Map<String, String> mpId2TermMapping = new HashMap<>();
    private static Map<Integer, String> mpNodeId2MpIdMapping = new HashMap<>();

    private static Map<String, String> mpId2NodeIdsMapping = new HashMap<>();
    private static Map<Integer, Node2TopDTO> mpNodeId2TopLevelMapping = new HashMap<>();

    private static Map<Integer, String> mpNodeId2IntermediateNodeIdsMapping = new HashMap<>();

    private static Map<String, List<MpTermDTO>> intermediateMpTerms = new HashMap<>();
    private static Map<String, List<MpTermDTO>> topMpTerms = new HashMap<>();

    private static Map<String, String> zygosityMapping = new HashMap<>();
    private static Set<String> postQcData = new HashSet<>();

    private Connection conn_komp2 = null;
    private Connection conn_ontodb = null;


	@Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(preqcCore);
    }

    @Override
    public RunStatus run() throws IndexerException, SQLException, IOException, SolrServerException {
        int count = 1;
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();

        zygosityMapping.put("Heterozygous", "heterozygote");
        zygosityMapping.put("Homozygous", "homozygote");
        zygosityMapping.put("Hemizygous", "hemizygote");

        Set<String> bad = new HashSet<>();

        try {

            conn_komp2 = komp2DataSource.getConnection();
            conn_ontodb = ontodbDataSource.getConnection();

            logger.info("  Populate Gene Symbol to Id Map");
            doGeneSymbol2IdMapping();

            logger.info("  Populate Allele Symbol to Name ID Map");
            doAlleleSymbol2NameIdMapping();

            logger.info("  Populate Strain ID to Name Map");
            doStrainId2NameMapping();

            logger.info("  Populate IMPReSS Map");
            doImpressSid2NameMapping();

            logger.info("  Populate ontology Map");
            doOntologyMapping();

            logger.info("  Populate post QC data Map");
            populatePostQcData();

            logger.info("  Populate resource Map");
            populateResourceMap();


            logger.info("  Truncating existing PreQC index");
            preqcCore.deleteByQuery("*:*");
            preqcCore.commit();

            // Read file using StAX parser
            logger.info("  Start reading the file");
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new BufferedInputStream(new FileInputStream(preqcXmlFilename)));

            int numberProcessed = 0;

            Integer id = null;
            String colonyId = null;
            Double pValue = null;
            String sex = null;
            String phenotypeTerm = null;
            String externalId = null;
            String zygosity = null;
            String datasource = null;
            String project = null;
            String gene = null;
            Double effectSize = null;
            String strain = null;
            String allele = null;
            String pipeline = null;
            String procedure = null;
            String parameter = null;
            String phenotypingCenter = null;

            while(xmlEventReader.hasNext()) {

                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();

                    if (startElement.getName().getLocalPart().equals("uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary")) {

                        // Start of PhenotypeCallSummary element, reset the document values

                        numberProcessed += 1;

                        if (numberProcessed % 100000 == 0) {
                            logger.info("    Processed {} documents", numberProcessed);
                        }

                        id = null;
                        colonyId = null;
                        pValue = null;
                        sex = null;
                        phenotypeTerm = null;
                        externalId = null;
                        zygosity = null;
                        datasource = null;
                        project = null;
                        gene = null;
                        effectSize = null;
                        strain = null;
                        allele = null;
                        pipeline = null;
                        procedure = null;
                        parameter = null;
                        phenotypingCenter = null;

                    } else {

                        // Currently processing PhenotypeCallSummary element, gather the data values

                        // nextEvent() gives the text of the child element
                        xmlEvent = xmlEventReader.nextEvent();
                        String data = xmlEvent.asCharacters().getData();

                        switch (startElement.getName().getLocalPart()) {
                            case "id":
                                id = Integer.parseInt(data);
                                break;
                            case "colonyId":
                                colonyId = data;
                                break;
                            case "pValue":
                                pValue = Double.parseDouble(data);
                                break;
                            case "sex":
                                sex = data;
                                break;
                            case "phenotypeTerm":
                                phenotypeTerm = data;
                                break;
                            case "externalId":
                                externalId = data;
                                break;
                            case "zygosity":
                                zygosity = data;
                                break;
                            case "datasource":
                                datasource = data;
                                break;
                            case "project":
                                project = data;
                                break;
                            case "gene":
                                gene = data;
                                break;
                            case "effectSize":
                                effectSize = Double.parseDouble(data);
                                break;
                            case "strain":
                                strain = data;
                                break;
                            case "allele":
                                allele = data.replace("<sup>", "<").replace("</sup>", ">");
                                break;
                            case "pipeline":
                                pipeline = data;
                                break;
                            case "procedure":
                                procedure = data;
                                break;
                            case "parameter":
                                parameter = data;
                                break;
                            case "phenotypingCenter":
                                phenotypingCenter = data.toUpperCase();
                                break;
                        }
                    }
                }

                // if PhenotypeCallSumary end element is reached, generate GenotypePhenotypeDTO object (when appropriate)
                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary")) {

                        if (numberProcessed % 100000 == 0) {
                            logger.debug("Processing document {} {} {} {}", id, colonyId, parameter, phenotypingCenter);
                        }

                        // Skip this one: phenotypeTerm is null
                        if (phenotypeTerm == null) {
                            missingPhenotypeTerm.add(id);
                            continue;
                        }

                        // Skip this one: pValue not significant OR phenotypeTerm is an anatomy term (MA)
                        // OR phenotype term is an embryonic anatomy term (EMAP)
                        if ((pValue != null && pValue >= 0.0001) || phenotypeTerm.startsWith("MA:") || phenotypeTerm.startsWith("EMAP:")) {
                            continue;
                        }

                        if (mpId2TermMapping.get(phenotypeTerm) == null) {
                            bad.add(phenotypeTerm);
                            continue;
                        }

                        if (allele == null || allele.isEmpty()){
                            // 27-Mar-2017 (mrelac) Make this an info. See MPII-2527.
                            logger.info("Empty allele symbol for id::colonyId::parameter::phenotypingCenter {}::{}::{}::{}", id, colonyId, parameter, phenotypingCenter);
                            continue;
                        }

                        // Skip if we already have this data postQC
                        phenotypingCenter = dccMapping.dccCenterMap.containsKey(phenotypingCenter) ? dccMapping.dccCenterMap.get(phenotypingCenter) : phenotypingCenter;
                        if ((phenotypingCenter == null) || (phenotypingCenter.trim().isEmpty())) {
                            throw new IndexerException("phenotypingCenter is missing!");
                        }

                        if (postQcData.contains(StringUtils.join(Arrays.asList(new String[]{colonyId, parameter, phenotypingCenter.toUpperCase()}), "_"))) {
                            continue;
                        }


                        // Generate and save the document
                        GenotypePhenotypeDTO o = new GenotypePhenotypeDTO();

                        // Procedure prefix is the first two strings of the parameter after splitting on underscore
                        // i.e. IMPC_BWT_001_001 => IMPC_BWT
                        String procedurePrefix = StringUtils.join(Arrays.asList(parameter.split("_")).subList(0, 2), "_");
                        if (GenotypePhenotypeIndexer.source3iProcedurePrefixes.contains(procedurePrefix)) {
                            o.setResourceName(StatisticalResultsIndexer.RESOURCE_3I.toUpperCase());
                            o.setResourceFullname(resourceMap.get(StatisticalResultsIndexer.RESOURCE_3I.toUpperCase()));

                        } else {
                            o.setResourceName(datasource);
                            if (resourceMap.containsKey(project.toUpperCase())) {
                                o.setResourceFullname(resourceMap.get(project.toUpperCase()));
                            }
                        }

                        o.setProjectName(project);
                        if (projectMap.containsKey(project.toUpperCase())) {
                            o.setProjectFullname(projectMap.get(project.toUpperCase()));
                        }

                        o.setColonyId(colonyId);
                        o.setExternalId(externalId);
                        o.setStrainAccessionId(strain);
                        o.setStrainName(strainId2NameMapping.get(strain));
                        o.setMarkerSymbol(gene);
                        o.setMarkerAccessionId(geneSymbol2IdMapping.get(gene));
                        o.setPipelineName(pipelineSid2NameMapping.get(pipeline));
                        o.setPipelineStableId(pipeline);
                        o.setProcedureName(procedureSid2NameMapping.get(procedure));
                        o.setProcedureStableId(procedure);
                        o.setParameterName(parameterSid2NameMapping.get(parameter));
                        o.setParameterStableId(parameter);
                        o.setMpTermId(phenotypeTerm);
                        o.setMpTermName(mpId2TermMapping.get(phenotypeTerm));
                        o.setP_value(pValue);
                        o.setEffectSize(effectSize);

                        if (!zygosityMapping.containsKey(zygosity)) {
                            runStatus.addWarning(" Zygosity " + zygosity + " not found for record id " + id);
                            continue;
                        }
                        o.setZygosity(zygosityMapping.get(zygosity));

                if (alleleSymbol2NameIdMapping.get(allele) == null || alleleSymbol2NameIdMapping.get(allele).acc == null) {
                    // use fake id if we cannot find the symbol from komp2
                    o.setAlleleAccessionId(createFakeIdFromSymbol(allele));
                } else {
                    o.setAlleleAccessionId(alleleSymbol2NameIdMapping.get(allele).acc);
                }
                o.setAlleleSymbol(allele);

                        if (dccMapping.dccCenterMap.containsKey(phenotypingCenter)) {
                            o.setPhenotypingCenter(dccMapping.dccCenterMap.get(phenotypingCenter));
                        } else {
                            o.setPhenotypingCenter(phenotypingCenter);
                        }

                        // Set the intermediate terms
                        List<String> ids = new ArrayList<>();
                        List<String> names = new ArrayList<>();

                        for (MpTermDTO mp : getIntermediateMpTerms(phenotypeTerm)) {
                            ids.add(mp.id);
                            names.add(mp.name);
                        }

                        o.setIntermediateMpTermId(ids);
                        o.setIntermediateMpTermName(names);

                        // Set the top level terms
                        ids = new ArrayList<>();
                        names = new ArrayList<>();

                        for (MpTermDTO mp : getTopMpTerms(phenotypeTerm)) {
                            ids.add(mp.id);
                            names.add(mp.name);
                        }

                        o.setTopLevelMpTermId(ids);
                        o.setTopLevelMpTermName(names);

                        if (sex.equals("Both")) {

                            // use incremental id instead of id field from Harwell
                            o.setId(count++);
                            o.setSex(SexType.female.getName());
                            documentCount++;
                            preqcCore.addBean(o, 5000);

                            o.setId(count++);
                            o.setSex(SexType.male.getName());
                            documentCount++;
                            preqcCore.addBean(o, 5000);

                        } else {

                            o.setId(count++);

                            try {

                                SexType.valueOf(sex.toLowerCase());

                            } catch (IllegalArgumentException se) {
                                runStatus.addError(" Got unexpected sex value '" + sex.toLowerCase() + "' from PreQC file. Not loading");
                                continue;
                            }

                            o.setSex(sex.toLowerCase());
                            documentCount++;
                            preqcCore.addBean(o, 5000);
                        }
                    }
                }
            }

            logger.info("    Processed {} documents", numberProcessed);

            preqcCore.commit();

        } catch (Exception e) {
            e.printStackTrace();
            throw new IndexerException(e);
        }

        logger.info("  Done reading the file");

        if (missingPhenotypeTerm.size() > 0) {
            logger.info(" Phenotype terms are missing for " + missingPhenotypeTerm.size() + " record(s).");
        }

        if (bad.size() > 0) {
            runStatus.addWarning(" Found " + bad.size() + " unique mps not in ontodb");
            runStatus.addWarning(" MP terms not found: " + StringUtils.join(bad, ","));
        }

        logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));

        return runStatus;
    }

    public String createFakeIdFromSymbol(String alleleSymbol) {

        return "NULL-" + DigestUtils.md5Hex(alleleSymbol).substring(0,10).toUpperCase();

    }

    public void doGeneSymbol2IdMapping() {

        try {
            List<Allele2DTO> allele2Docs = allele2Service.getAllDocuments("Gene", Allele2DTO.MARKER_SYMBOL, Allele2DTO.MGI_ACCESSION_ID);
            for (Allele2DTO allele: allele2Docs){
                geneSymbol2IdMapping.put(allele.getMarkerSymbol(), allele.getMgiAccessionId());
            }
        } catch (IOException | SolrServerException e) {
            e.printStackTrace();
        }
    }


    public void populateResourceMap() throws SQLException {

        String projQuery = "SELECT p.name as name, p.fullname as fullname FROM project p";
        String resQuery = "SELECT db.short_name as name, db.name as fullname FROM external_db db ";

        try (PreparedStatement p = conn_komp2.prepareStatement(projQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            p.setFetchSize(Integer.MIN_VALUE);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                projectMap.put(r.getString("name").toUpperCase(), r.getString("fullname"));
            }
        }
        try (PreparedStatement p = conn_komp2.prepareStatement(resQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            p.setFetchSize(Integer.MIN_VALUE);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                resourceMap.put(r.getString("name").toUpperCase(), r.getString("fullname"));
            }
        }
    }

    public void doImpressSid2NameMapping() {

        Map<String, Map> impressMapping = new HashMap<>();
        impressMapping.put("phenotype_pipeline", pipelineSid2NameMapping);
        impressMapping.put("phenotype_procedure", procedureSid2NameMapping);
        impressMapping.put("phenotype_parameter", parameterSid2NameMapping);

        ResultSet rs;
        Statement statement;

        for (Map.Entry entry : impressMapping.entrySet()) {
            String tableName = entry.getKey().toString();
            Map<String, String> mapping = (Map) entry.getValue();

            String query = "select name, stable_id from " + tableName;
            try {
                statement = conn_komp2.createStatement();
                rs = statement.executeQuery(query);

                while (rs.next()) {
                    // Retrieve by column name
                    String sid = rs.getString("stable_id");
                    String name = rs.getString("name");

                    if (tableName.equals("phenotype_procedure")) {

                        //if (sid.contains("_")) {

                            // Harwell does not include version in
                            // procedure_stable_id
                           // sid = sid.replaceAll("_\\d+$", "");
                       // } else {

                            // Harwell now does not include version OR pipeline in
                            // procedure_stable_id
                            String[] pieces = sid.split("_");
                            sid = pieces[1];
                       // }
                    }

                    mapping.put(sid, name);
                    
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void doAlleleSymbol2NameIdMapping() {


        try {
            List<Allele2DTO> allele2Docs = allele2Service.getAllDocuments("Allele", Allele2DTO.ALLELE_NAME, Allele2DTO.ALLELE_MGI_ACCESSION_ID, Allele2DTO.ALLELE_SYMBOL);
            for (Allele2DTO allele: allele2Docs){
                AlleleDTO al = new AlleleDTO();
                al.acc = allele.getAlleleMgiAccessionId();
                al.name = allele.getAlleleName();
                alleleSymbol2NameIdMapping.put(allele.getAlleleSymbol(), al);
            }
        } catch (IOException | SolrServerException e) {
            e.printStackTrace();
        }

    }

    public void doStrainId2NameMapping() {

        ResultSet rs;
        Statement statement;

        String query = "select acc, name from strain";
        try {
            statement = conn_komp2.createStatement();
            rs = statement.executeQuery(query);

            while (rs.next()) {
                // Retrieve by column name
                String acc = rs.getString("acc");
                String name = rs.getString("name");
                //logger.error(acc + " -- "+ name);
                strainId2NameMapping.put(acc, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void doOntologyMapping() {

        ResultSet rs1, rs2, rs3, rs4;
        Statement statement;

        // all MPs
        String query1 = "select ti.term_id, ti.name, nt.node_id from mp_term_infos ti, mp_node2term nt where ti.term_id=nt.term_id and ti.term_id !='MP:0000001'";

        // all top_level nodes of MP
        String query2 = "select lv.node_id as mp_node_id, ti.term_id, ti.name from mp_node_top_level lv"
                + " inner join mp_node2term nt on lv.top_level_node_id=nt.node_id" + " inner join mp_term_infos ti on nt.term_id=ti.term_id"
                + " and ti.term_id!='MP:0000001'";

        // all nodes of MPs
        String query3 = "select ti.term_id, group_concat(nt.node_id) as nodeIds from mp_term_infos ti, mp_node2term nt where ti.term_id=nt.term_id and ti.term_id !='MP:0000001' group by ti.term_id order by ti.term_id";

        // all intermediate nodes of MP nodes
        String query4 = "select child_node_id, group_concat(node_id) as intermediate_nodeIds from mp_node_subsumption_fullpath group by child_node_id";

        try {

			// we need to create a new state for each query
            // otherwise we get "Operation not allowed after ResultSet closed"
            // error
            statement = conn_ontodb.createStatement();
            rs1 = statement.executeQuery(query1);

            statement = conn_ontodb.createStatement();
            rs2 = statement.executeQuery(query2);

            statement = conn_ontodb.createStatement();
            rs3 = statement.executeQuery(query3);

            statement = conn_ontodb.createStatement();
            rs4 = statement.executeQuery(query4);

            while (rs1.next()) {
                String mp_term_id = rs1.getString("term_id");
                String mp_term_name = rs1.getString("name");
                int mp_node_id = rs1.getInt("node_id");
				// logger.error("rs1: " + mp_term_id + " -- "+ mp_term_name);
                // logger.error("rs1: " + mp_node_id + " -- "+ mp_term_name);
                mpId2TermMapping.put(mp_term_id, mp_term_name);
                mpNodeId2MpIdMapping.put(mp_node_id, mp_term_id);
            }

            // top level MPs
            while (rs2.next()) {
                int top_level_mp_node_id = rs2.getInt("mp_node_id");
                String top_level_mp_term_id = rs2.getString("term_id");
                String top_level_mp_term_name = rs2.getString("name");
				// logger.error("rs2: " + top_level_mp_node_id + " --> " +

                Node2TopDTO n2t = new Node2TopDTO();
                n2t.topLevelMpTermId = top_level_mp_term_id;
                n2t.topLevelMpTermName = top_level_mp_term_name;
                mpNodeId2TopLevelMapping.put(top_level_mp_node_id, n2t);
            }

            while (rs3.next()) {
                String mp_node_ids = rs3.getString("nodeIds");
                String mp_term_id = rs3.getString("term_id");
                // logger.error("rs3: " + mp_term_id + " -- > " + mp_node_ids);
                mpId2NodeIdsMapping.put(mp_term_id, mp_node_ids);
            }

            // intermediate nodeId mapping
            while (rs4.next()) {
                int child_node_id = rs4.getInt("child_node_id");
                String intermediate_nodeIds = rs4.getString("intermediate_nodeIds");
				// logger.error("rs4: " + child_node_id + " -- > " +
                // intermediate_nodeIds);
                mpNodeId2IntermediateNodeIdsMapping.put(child_node_id, intermediate_nodeIds);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<MpTermDTO> getIntermediateMpTerms(String mpId) {

        if ( ! intermediateMpTerms.containsKey(mpId)) {

            // default to empty list
            intermediateMpTerms.put(mpId, new ArrayList<>());

            // MP:0012441 -- > 618,732,741,971,1090,1204,1213
            if (mpId2NodeIdsMapping.containsKey(mpId)) {
                String[] nodeIdsStr = mpId2NodeIdsMapping.get(mpId).split(",");

                List<MpTermDTO> mps = new ArrayList<>();

	            for (String aNodeIdsStr : nodeIdsStr) {
		            int childNodeId = Integer.parseInt(aNodeIdsStr);
		            List<MpTermDTO> top = getTopMpTerms(mpId);

		            // top level mp do not have intermediate mp
		            if (mpNodeId2IntermediateNodeIdsMapping.get(childNodeId) != null) {
			            String[] intermediateNodeIdsStr = mpNodeId2IntermediateNodeIdsMapping.get(childNodeId).split(",");

			            for (String anIntermediateNodeIdsStr : intermediateNodeIdsStr) {
				            int intermediateNodeId = Integer.parseInt(anIntermediateNodeIdsStr);

				            MpTermDTO mp = new MpTermDTO();
				            mp.id = mpNodeId2MpIdMapping.get(intermediateNodeId);
				            mp.name = mpId2TermMapping.get(mp.id);

				            // don't want to include self as intermediate parent
				            if (childNodeId != intermediateNodeId && !top.contains(mp)) {
					            mps.add(mp);
				            }
			            }
		            }
	            }

                // added only we got intermediates
                if (mps.size() != 0) {
                    intermediateMpTerms.put(mpId, mps);
                }
            }
        }

        return intermediateMpTerms.get(mpId);
    }

    public List<MpTermDTO> getTopMpTerms(String mpId) {

        if ( ! topMpTerms.containsKey(mpId)) {

            // default to empty list
            topMpTerms.put(mpId, new ArrayList<>());

            // MP:0012441 -- > 618,732,741,971,1090,1204,1213
            if (mpId2NodeIdsMapping.containsKey(mpId)) {

                String[] nodeIdsStr = mpId2NodeIdsMapping.get(mpId).split(",");

                List<MpTermDTO> mps = new ArrayList<>();

	            for (String aNodeIdsStr : nodeIdsStr) {

		            int topLevelMpNodeId = Integer.parseInt(aNodeIdsStr);
		            // System.out.println(mpId + " - top_level_node_id: " +
		            // topLevelMpNodeId);

		            if (mpNodeId2TopLevelMapping.containsKey(topLevelMpNodeId)) {

			            MpTermDTO mp = new MpTermDTO();
			            mp.id = mpNodeId2TopLevelMapping.get(topLevelMpNodeId).topLevelMpTermId;
			            mp.name = mpNodeId2TopLevelMapping.get(topLevelMpNodeId).topLevelMpTermName;
			            mps.add(mp);
		            }

	            }

                topMpTerms.put(mpId, mps);

            }
        }

        return topMpTerms.get(mpId);
    }

    public void populatePostQcData() {

        List<String> queries = new ArrayList<>();

        // Gather all line level data
        queries.add("SELECT CONCAT(e.colony_id, '_', o.parameter_stable_id, '_', UPPER(org.name)) AS data_value " +
            "FROM observation o " +
            "INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
            "INNER JOIN experiment e ON e.id=eo.experiment_id " +
            "INNER JOIN organisation org ON org.id=e.organisation_id " +
            "WHERE e.colony_id IS NOT NULL ");

        // Gather all specimen level data
        queries.add("SELECT CONCAT(ls.colony_id, '_', o.parameter_stable_id, '_', UPPER(org.name)) AS data_value " +
            "FROM observation o " +
            "INNER JOIN live_sample ls ON ls.id=o.biological_sample_id " +
            "INNER JOIN biological_sample bs ON bs.id=o.biological_sample_id " +
            "INNER JOIN organisation org ON org.id=bs.organisation_id " +
            "WHERE bs.sample_group='experimental' ");

        for (String query : queries){

            try (PreparedStatement p = conn_komp2.prepareStatement(query)) {
                ResultSet resultSet = p.executeQuery();

                while (resultSet.next()) {
                    postQcData.add(resultSet.getString("data_value"));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    private class Node2TopDTO {

        String topLevelMpTermId;
        String topLevelMpTermName;
    }

    private class AlleleDTO {

        String acc;
        String name;
    }

    public class MpTermDTO {

        String id;
        String name;

        @Override
        public int hashCode() {

            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            MpTermDTO other = (MpTermDTO) obj;
            if ( ! getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (id == null) {
                if (other.id != null) {
                    return false;
                }
            } else if ( ! id.equals(other.id)) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if ( ! name.equals(other.name)) {
                return false;
            }
            return true;
        }

        private PreqcIndexer getOuterType() {

            return PreqcIndexer.this;
        }
    }


    public static void main(String[] args) throws IndexerException {
        SpringApplication.run(PreqcIndexer.class, args);
    }
}
