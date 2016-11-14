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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.db.dao.GenomicFeatureDAO;
import org.mousephenotype.cda.db.dao.ReferenceDAO;
import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.solr.generic.util.JSONImageUtils;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.mousephenotype.cda.solr.service.SolrIndex.AnnotNameValCount;
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.web.dto.SimpleOntoTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.phenodigm.dao.PhenoDigmWebDao;
import uk.ac.ebi.phenodigm.model.GeneIdentifier;
import uk.ac.ebi.phenodigm.web.AssociationSummary;
import uk.ac.ebi.phenodigm.web.DiseaseAssociationSummary;
import uk.ac.ebi.phenotype.generic.util.RegisterInterestDrupalSolr;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.util.*;


@Controller
public class PaperController {

    private static final int ArrayList = 0;

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());


    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @Autowired
    @Qualifier("admintoolsDataSource")
    private DataSource admintoolsDataSource;

    @Autowired
    @Qualifier("komp2DataSource")
    private DataSource komp2DataSource;

    @Autowired
    private ReferenceDAO referenceDAO;

    @Autowired
	private GenomicFeatureDAO genesDao;


	// allele reference stuff
	@RequestMapping(value = "/alleleRefLogin", method = RequestMethod.POST)
	public @ResponseBody
	boolean checkPassCode(
			@RequestParam(value = "passcode", required = true) String passcode,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException {

		return checkPassCode(passcode);
	}

	public boolean checkPassCode(String passcode) throws SQLException {

		Connection conn = admintoolsDataSource.getConnection();

		// prevent sql injection
		String query = "select password = md5(?) as status from users where name='ebi'";
		boolean match = false;

		try (PreparedStatement p = conn.prepareStatement(query)) {
			p.setString(1, passcode);
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				match = resultSet.getBoolean("status");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

		return match;
	}

	@RequestMapping(value = "/addpmid", method = RequestMethod.POST)
	public @ResponseBody
	String addPaper(
			@RequestParam(value = "idStr", required = true) String idStr,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException {

		List<String> pmidQrys = new ArrayList<>();
		List<String> pmidStrs = Arrays.asList(idStr.split(","));

		System.out.println("Got paper id str: "+idStr);
		for( String pmidStr : pmidStrs ){
			pmidQrys.add("ext_id:" + pmidStr);
		}

		Connection conn = admintoolsDataSource.getConnection();
		PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO allele_ref "
				+ "(gacc, acc, symbol, name, pmid, date_of_publication, reviewed, grant_id, agency, acronym, title, journal, paper_url, datasource, timestamp, falsepositive, mesh) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)");

		String status = "";
		String failStatus = "";
		String successStatus = "";
		String notFoundStatus = "";

		Map<Integer, Pubmed> pubmeds = fetchEuropePubmedData(pmidQrys);

		if (pubmeds.size() == 0) {
			status = "Your paper ids are not found in pubmed database";
		}
		else {
			//System.out.println("found " + pubmeds.size() + " papers");
			List<String> failedPmids = new ArrayList<>();
			List<String> failedPmidsMsg = new ArrayList<>();
			List<String> foundPmids = new ArrayList<>();

			Iterator it = pubmeds.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				String pmidStr = pair.getKey().toString();

				foundPmids.add(pmidStr);

				Pubmed pub = (Pubmed) pair.getValue();

				//System.out.println("found paper: "+pmidStr);

				String msg = savePmidData(pub, insertStatement);

				//System.out.println("insert status: "+msg);
				if ( ! msg.equals("success") ){
					//System.out.println("failed: "+ msg);
					msg = msg.replace(" for key 'pmid'", "");
					failedPmids.add(pmidStr);
					failedPmidsMsg.add(msg);
				}
				else {
					successStatus += pmidStr + " added to database\n";
				}


				it.remove(); // avoids a ConcurrentModificationException
			}
			if ( failedPmids.size() == 0 && foundPmids.size() == pmidStrs.size()) {
				status = "Pmid(s) added successfully";
			}
			else {
				failStatus += fetchNotFoundMsg(pmidStrs, failedPmids);
				if ( !successStatus.equals("")){
					status += "Success:\n" + successStatus;
				}

				notFoundStatus = fetchNotFoundMsg(pmidStrs, foundPmids);
				if ( !notFoundStatus.equals("") ){
					status += "Not Found:\n" + notFoundStatus;
				}

				status += "Error:\n" + StringUtils.join(failedPmidsMsg, "\n");

			}
		}
		conn.close();
		return status;
	}

	public String fetchNotFoundMsg(List<String> pmidStrs, List<String> failedPmids ){
		String msg = "";
		for (String pmid : pmidStrs){
			if ( !failedPmids.contains(pmid)){
				msg += pmid + " not found in pubmed\n";
			}
		}
		return msg;
	}

	public String savePmidData(Pubmed pub, PreparedStatement insertStatement) throws SQLException{

		// (1.gacc, 2.acc, 3.symbol, 4.name, 5.pmid, 6.date_of_publication,
		// 7.reviewed, 8.grant_id, 9.agency, 10.acronym, 11.title,
		// 12.journal, 13.paper_url, 14.datasource, 15.timestamp, 16.falsepositive, 17.mesh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");

		final String delimiter = "|||";

		insertStatement.setString(1, "");
		insertStatement.setString(2, "");
		insertStatement.setString(3, "");
		insertStatement.setString(4, "");
		insertStatement.setInt(5, pub.getPmid());
		insertStatement.setString(6, pub.getDateOfPublication());
		insertStatement.setString(7, "no"); // reviewed, default is no

		// grant info: we can have multiple grants for a paper

		List<String> grantIds = new ArrayList<>();
		List<String> grantAgencies = new ArrayList<>();
		List<String> grantAcronyms = new ArrayList<>();

		List<Grant> grants = pub.getGrants();
		for (int i = 0; i < grants.size(); i++) {
			Grant g = grants.get(i);
			if (!g.getId().equals("")) {
				grantIds.add(g.getId());
			}
			if (!g.getAgency().equals("")) {
				grantAgencies.add(g.getAgency());
			}
			if (!g.getAcronym().equals("")) {
				grantAcronyms.add(g.getAcronym());
			}
		}

		insertStatement.setString(8, grantIds.size() > 0 ? StringUtils.join(grantIds, delimiter) : "");
		insertStatement.setString(9, grantAgencies.size() > 0 ? StringUtils.join(grantAgencies, delimiter) : "");
		insertStatement.setString(10, grantAcronyms.size() > 0 ? StringUtils.join(grantAcronyms, delimiter) : "");

		insertStatement.setString(11, pub.getTitle());
		insertStatement.setString(12, pub.getJournal());

		// we can have multiple links to a paper
		List<String> paper_urls = new ArrayList<>();
		List<Paperurl> paperUrls = pub.getPaperurls();
		for (int j = 0; j < paperUrls.size(); j++) {
			Paperurl p = paperUrls.get(j);
			paper_urls.add(p.getUrl());
		}
		insertStatement.setString(13, paper_urls.size() > 0 ? StringUtils.join(paper_urls, delimiter) : "");

		insertStatement.setString(14, "europubmed");
		insertStatement.setTimestamp(15, new Timestamp(System.currentTimeMillis()));
		insertStatement.setString(16, "no");

		// fetch mesh terms: heading pulus mesh heading+mesh qualifier
		List<String> mterms = new ArrayList<>();
		for ( int k=0; k<pub.meshTerms.size(); k++ ) {
			MeshTerm mt = pub.meshTerms.get(k);
			mterms.add(mt.meshHeading);
			for (String mq : mt.meshQualifiers) {
				mterms.add(mt.meshHeading + " " + mq);
			}
		}
		insertStatement.setString(17, mterms.size() > 0 ? StringUtils.join(mterms, delimiter) : "");


		try {
			int count = insertStatement.executeUpdate();
			return "success";
		}
		catch(SQLException se){
			return se.getMessage();
		}

	}

	public Map<Integer, Pubmed> fetchEuropePubmedData(List<String> pmidQrys){

		Map<Integer, Pubmed> pubmeds = new HashMap<>();

		// attach pubmed info to pmid
		for( String q : pmidQrys ){
			//System.out.println("Working on filter: "+ q);
			String dbfetchUrl = "http://www.ebi.ac.uk/europepmc/webservices/rest/search/query=" + q + "%20and%20src:MED&format=json&resulttype=core";
			//System.out.println(dbfetchUrl);

			JSONObject json = fetchHttpUrlJson(dbfetchUrl);

			JSONArray results = json.getJSONObject("resultList").getJSONArray("result");

			for (int j = 0; j < results.size(); j++) {

				JSONObject r = results.getJSONObject(j);

				int pmid = r.getInt("pmid");

				if (!pubmeds.containsKey(q)) {
					pubmeds.put(pmid, new Pubmed());
				}
				Pubmed pub = pubmeds.get(pmid);

				pub.setPmid(pmid);

				if (r.containsKey("title")) {
					pub.setTitle(r.getString("title"));
				} else {
					pub.setTitle("");
				}

				if (r.containsKey("journalInfo") && r.getJSONObject("journalInfo").containsKey("dateOfPublication")) {
					String dateOfPublication = r.getJSONObject("journalInfo").getString("dateOfPublication");
					//System.out.println("dateOfPublication: " + dateOfPublication);
					pub.setDateOfPublication(dateOfPublication);
				} else {
					pub.setDateOfPublication("");
				}


				if (r.containsKey("journalInfo") && r.getJSONObject("journalInfo").containsKey("journal")) {
					String journal = r.getJSONObject("journalInfo").getJSONObject("journal").getString("title");
					pub.setJournal(journal);
				} else {
					pub.setJournal("");
				}


				List<Grant> grantList = new ArrayList<>();

				if (r.containsKey("grantsList")) {
					JSONArray grants = r.getJSONObject("grantsList").getJSONArray("grant");
					for (int k = 0; k < grants.size(); k++) {
						JSONObject thisG = (JSONObject) grants.get(k);
						Grant g = new Grant();
						//System.out.println(thisG.toString());

						if (thisG.containsKey("grantId")) {
							g.setId(thisG.getString("grantId"));
						} else {
							g.setId("");
						}

						if (thisG.containsKey("agency")) {
							g.setAgency(thisG.getString("agency"));
						} else {
							g.setAgency("");
						}

						if (thisG.containsKey("acronym")) {
							g.setAcronym(thisG.getString("acronym"));
						} else {
							g.setAcronym("");
						}

						grantList.add(g);

						//System.out.println(g.toString());
					}
				}
				pub.setGrants(grantList);

				List<Paperurl> paperurls = new ArrayList<>();

				if (r.containsKey("fullTextUrlList")) {
					JSONArray textUrl = r.getJSONObject("fullTextUrlList").getJSONArray("fullTextUrl");
					for (int l = 0; l < textUrl.size(); l++) {
						Paperurl p = new Paperurl();
						JSONObject thisT = (JSONObject) textUrl.get(l);
						if (thisT.containsKey("url")) {
							p.setUrl(thisT.getString("url"));
							paperurls.add(p);
							//System.out.println("URL: "+ p.url);
						}
					}
				}

				pub.setPaperurls(paperurls);

				// mesh terms
				List<MeshTerm> meshTerms = new ArrayList<>();

				if ( r.containsKey("meshHeadingList") ){
					JSONArray meshHeadings = r.getJSONObject("meshHeadingList").getJSONArray("meshHeading");
					for ( int mh=0; mh<meshHeadings.size(); mh++ ){
						JSONObject thisMeshHeading = (JSONObject) meshHeadings.get(mh);
						MeshTerm mt  = new MeshTerm();
						//System.out.println(thisMeshHeading.toString());

						// mesh heading
						mt.setMeshHeading("");
						if ( thisMeshHeading.containsKey("descriptorName") ){
							mt.setMeshHeading(thisMeshHeading.getString("descriptorName"));
						}

						// mesh subheading
						mt.setMeshQualifiers(new ArrayList<>());
						if ( thisMeshHeading.containsKey("meshQualifierList") ) {
							JSONArray meshQualifiers= thisMeshHeading.getJSONObject("meshQualifierList").getJSONArray("meshQualifier");
							for (int mq = 0; mq < meshQualifiers.size(); mq++) {
								JSONObject thisMeshQualifier = (JSONObject) meshQualifiers.get(mq);
								if ( thisMeshQualifier.containsKey("qualifierName") ){
									String qf = thisMeshQualifier.getString("qualifierName");
									if ( ! mt.getMeshQualifiers().contains(qf)) {
										mt.getMeshQualifiers().add(qf);
									}
								}
							}
						}

						meshTerms.add(mt);
					}
				}
				pub.setMeshTerms(meshTerms);

			}
		}

		System.out.println("PARSING EUROPE PUBMED:");
		logger.info("Found " + pubmeds.size() + " pmids");
		System.out.println("");

		return pubmeds;
	}

	public JSONObject fetchHttpUrlJson(String dbfetchUrl) {
		// Data obtained from service, to be returned
		String jsonStr = null;
		// Get data using HTTP GET
		try {
			URL url = new URL(dbfetchUrl);
			BufferedReader inBuf = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer strBuf = new StringBuffer();
			while(inBuf.ready()) {
				strBuf.append(inBuf.readLine() + System.getProperty("line.separator"));
			}
			jsonStr = strBuf.toString();
		}
		catch(IOException ex) {
			System.out.println(ex.getMessage());
		}
		// Return the response data as JSON object
		JSONObject json = JSONObject.fromObject(jsonStr);
		return json;
	}

	public class Pubmed {
		Integer pmid;
		String title;
		String journal;
		String dateOfPublication;
		List<Grant> grants;
		List<Paperurl> paperurls;
		List<MeshTerm> meshTerms;

		public Integer getPmid() {
			return pmid;
		}

		public void setPmid(Integer pmid) {
			this.pmid = pmid;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getJournal() {
			return journal;
		}

		public void setJournal(String journal) {
			this.journal = journal;
		}

		public String getDateOfPublication() {
			return dateOfPublication;
		}

		public void setDateOfPublication(String dateOfPublication) {
			this.dateOfPublication = dateOfPublication;
		}

		public List<Grant> getGrants() {
			return grants;
		}

		public void setGrants(List<Grant> grants) {
			this.grants = grants;
		}

		public List<Paperurl> getPaperurls() {
			return paperurls;
		}

		public void setPaperurls(List<Paperurl> paperurls) {
			this.paperurls = paperurls;
		}

		public List<MeshTerm> getMeshTerms() {
			return meshTerms;
		}

		public void setMeshTerms(List<MeshTerm> meshTerms) {
			this.meshTerms = meshTerms;
		}

		@Override
		public String toString() {
			return "Pubmed{" +
					"pmid=" + pmid +
					", title='" + title + '\'' +
					", journal='" + journal + '\'' +
					", dateOfPublication='" + dateOfPublication + '\'' +
					", grants=" + grants +
					", paperurls=" + paperurls +
					'}';
		}
	}
	public class Grant {
		public String id;
		public String acronym;
		public String agency;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getAcronym() {
			return acronym;
		}

		public void setAcronym(String acronym) {
			this.acronym = acronym;
		}

		public String getAgency() {
			return agency;
		}

		public void setAgency(String agency) {
			this.agency = agency;
		}
	}
	public class Paperurl {
		public String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
	public class MeshTerm {
		public String meshHeading;
		public List<String> meshQualifiers; // same as mes subheading

		public String getMeshHeading() {
			return meshHeading;
		}

		public void setMeshHeading(String meshHeading) {
			this.meshHeading = meshHeading;
		}

		public List<String> getMeshQualifiers() {
			return meshQualifiers;
		}

		public void setMeshQualifiers(List<String> meshQualifiers) {
			this.meshQualifiers = meshQualifiers;
		}
	}

	private HttpHeaders createResponseHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}
}
