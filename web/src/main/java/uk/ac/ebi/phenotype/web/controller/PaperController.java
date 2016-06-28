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
				+ "(gacc, acc, symbol, name, pmid, date_of_publication, reviewed, grant_id, agency, acronym, title, journal, paper_url, datasource, timestamp, falsepositive) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");

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
			if ( failedPmids.size() == 0 ) {
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
		// 12.journal, 13.paper_url, 14.datasource, 15.timestamp, 16.falsepositive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)");

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

		insertStatement.setString(8, grantIds.size() > 0 ? StringUtils.join(grantIds, "|||") : "");
		insertStatement.setString(9, grantAgencies.size() > 0 ? StringUtils.join(grantAgencies, "|||") : "");
		insertStatement.setString(10, grantAcronyms.size() > 0 ? StringUtils.join(grantAcronyms, "|||") : "");

		insertStatement.setString(11, pub.getTitle());
		insertStatement.setString(12, pub.getJournal());

		// we can have multiple links to a paper
		List<String> paper_urls = new ArrayList<>();
		List<Paperurl> paperUrls = pub.getPaperurls();
		for (int j = 0; j < paperUrls.size(); j++) {
			Paperurl p = paperUrls.get(j);
			paper_urls.add(p.getUrl());
		}
		insertStatement.setString(13, paper_urls.size() > 0 ? StringUtils.join(paper_urls, ",") : "");

		insertStatement.setString(14, "europubmed");
		insertStatement.setString(15, "00-00-00 00:00:00");
		insertStatement.setString(16, "no");

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

//
//
//	// allele reference stuff
//    @RequestMapping(value = "/dataTableAlleleRefCount", method = RequestMethod.GET)
//    public @ResponseBody
//    int updateReviewed(
//            @RequestParam(value = "filterStr", required = true) String sSearch,
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Model model) throws IOException, URISyntaxException, SQLException {
//
//        return fetchAlleleRefCount(sSearch);
//    }
//
//    public int fetchAlleleRefCount(String sSearch) throws SQLException {
//
//        Connection conn = admintoolsDataSource.getConnection();
//
//        String like = "%" + sSearch + "%";
//        String query = null;
//
//        if (sSearch != "") {
//            query = "select count(*) as count from allele_ref where "
//					+ " reviewed='no' and"
//                    + " acc like ?"
//                    + " or symbol like ?"
//                    + " or pmid like ?"
//                    + " or date_of_publication like ?"
//                    + " or grant_id like ?"
//                    + " or agency like ?"
//                    + " or acronym like ?";
//        } else {
//            query = "select count(*) as count from allele_ref where reviewed='no'";
//        }
//		System.out.println("DataTableController: query: "+query);
//		int rowCount = 0;
//        try (PreparedStatement p1 = conn.prepareStatement(query)) {
//            if (sSearch != "") {
//                for (int i = 1; i < 8; i ++) {
//                    p1.setString(i, like);
//                }
//            }
//            ResultSet resultSet = p1.executeQuery();
//
//            while (resultSet.next()) {
//                rowCount = Integer.parseInt(resultSet.getString("count"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return rowCount;
//    }
//
//    @RequestMapping(value = "/dataTableAlleleRef", method = RequestMethod.POST)
//	 public @ResponseBody
//	 String updateReviewed(
//					@RequestParam(value = "value", required = true) String value,
//					@RequestParam(value = "id", required = true) String dbidStr,
//					HttpServletRequest request,
//					HttpServletResponse response,
//					Model model) throws IOException, URISyntaxException, SQLException {
//
//		// store new value to database
//		value = value.trim();
//		System.out.println("***** check dbid: "+dbidStr);
//		List<Integer> dbids = getDbIds(dbidStr);
//		return setAlleleSymbol(dbids, value);
//	}
//	@RequestMapping(value = "/dataTableAlleleRefSetFalsePositive", method = RequestMethod.GET)
//	public @ResponseBody
//	Boolean updateFalsePositive(
//			@RequestParam(value = "value", required = true) String falsePositive,
//			@RequestParam(value = "id", required = true) String dbidStr,
//			HttpServletRequest request,
//			HttpServletResponse response,
//			Model model) throws IOException, URISyntaxException, SQLException {
//
//		// store new value to database
//		System.out.println("***** check set falsepositive dbid: "+dbidStr);
//		List<Integer> dbids = getDbIds(dbidStr);
//		return setFalsePositive(dbids, falsePositive);
//	}
//
//	public Boolean setFalsePositive(List<Integer> dbids, String falsePositive) throws SQLException {
//		Connection conn = admintoolsDataSource.getConnection();
//
//		for( int dbid : dbids) {
//
//			String uptSql = "UPDATE allele_ref SET falsepositive=?, reviewed='no', acc='', gacc='', timestamp=? WHERE dbid=?";
//			PreparedStatement stmt = conn.prepareStatement(uptSql);
//			stmt.setString(1, falsePositive);
//			stmt.setString(2, String.valueOf(new Timestamp(System.currentTimeMillis())));
//			stmt.setInt(3, dbid);
//			stmt.executeUpdate();
//		}
//		return true;
//	}
//
//	private List<Integer> getDbIds(String dbidStr) {
//		List<String> dbids = Arrays.asList(dbidStr.split(","));
//		List<Integer> dbidsInt = new ArrayList<>();
//		for (String strDbid : dbids) {
//			dbidsInt.add(Integer.parseInt(strDbid));
//		}
//		return dbidsInt;
//	}
//
//    public String setAlleleSymbol(List<Integer> dbids, String alleleSymbol) throws SQLException {
//
//		Connection connKomp2 = komp2DataSource.getConnection();
//		Connection conn = admintoolsDataSource.getConnection();
//
//		List<String> alleleSymbols = new ArrayList<>();
//		JSONObject j = new JSONObject();
//
//		String sqla = "SELECT acc, gf_acc FROM allele WHERE symbol=?";
//
//		// when symbol is set to be empty, change reviewed status, too
//		if (alleleSymbol.equals("")) {
//
//			j.put("reviewed", "no");
//			j.put("symbol", "");
//
//		}
//		else if (!alleleSymbol.contains(",")) {
//			// single allele symbol
//
//			String alleleAcc = null;
//			String geneAcc = null;
//
//			// find matching allele symbol from komp2 database and use its allele acc to populate allele_ref table
//			try (PreparedStatement p = connKomp2.prepareStatement(sqla)) {
//				p.setString(1, alleleSymbol);
//				ResultSet resultSet = p.executeQuery();
//
//				while (resultSet.next()) {
//					alleleAcc = resultSet.getString("acc");
//					geneAcc = resultSet.getString("gf_acc");
//					//System.out.println(alleleSymbol + ": " + alleleAcc + " --- " + geneAcc);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			try {
//				if (alleleAcc != null && geneAcc != null) {
//
//					for ( int dbid : dbids ) {
//
//						String uptSql = "UPDATE allele_ref SET acc=?, gacc=?, symbol=?, reviewed=?, timestamp=? WHERE dbid=?";
//						PreparedStatement stmt = conn.prepareStatement(uptSql);
//						stmt.setString(1, alleleAcc);
//						stmt.setString(2, geneAcc);
//						stmt.setString(3, alleleSymbol);
//						stmt.setString(4, "yes");
//						stmt.setString(5, String.valueOf(new Timestamp(System.currentTimeMillis())));
//						stmt.setInt(6, dbid);
//						stmt.executeUpdate();
//					}
//					j.put("reviewed", "yes");
//					j.put("symbol", alleleSymbol);
//				} else {
//					j.put("reviewed", "no");
//					j.put("symbol", alleleSymbol);
//					j.put("allAllelesNotFound", true);
//				}
//
//			} catch (SQLException se) {
//				//Handle errors for JDBC
//				se.printStackTrace();
//				j.put("reviewed", "no");
//				j.put("symbol", "ERROR: setting symbol failed");
//
//			}
//
//		} else if (alleleSymbol.contains(",")) {
//			// if there are multiple allele symbols, it should have been separated by comma
//
//			alleleSymbols = Arrays.asList(alleleSymbol.split(","));
//
//			int alleleCounter = 0;
//			List<String> nonMatchedAlleleSymbols = new ArrayList<>();
//			List<String> matchedAlleleSymbols = new ArrayList<>();
//
//			for (String thisAlleleSymbol : alleleSymbols) {
//
//				thisAlleleSymbol = thisAlleleSymbol.trim();
//
//				// fetch allele id, gene id of this allele symbol
//				// and update acc and gacc fields of allele_ref table
//				//System.out.println("set allele: " + sqla);
//
//				String alleleAcc = null;
//				String geneAcc = null;
//
//				// find matching allele symbol from komp2 database and use its allele acc to populate allele_ref table
//				try (PreparedStatement p = connKomp2.prepareStatement(sqla)) {
//					p.setString(1, thisAlleleSymbol);
//					ResultSet resultSet = p.executeQuery();
//
//					while (resultSet.next()) {
//						alleleAcc = resultSet.getString("acc");
//						geneAcc = resultSet.getString("gf_acc");
//						//System.out.println(alleleSymbol + ": " + alleleAcc + " --- " + geneAcc);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//				//System.out.println("setting acc and gacc -> " + alleleAcc + " --- " + geneAcc);
//
//				try {
//					if (alleleAcc != null && geneAcc != null) {
//						alleleCounter++;
//
//						if (alleleCounter == 1) {
//
//							for (int dbid : dbids) {
//
//								String uptSql = "UPDATE allele_ref SET acc=?, gacc=?, symbol=?, reviewed=?, timestamp=? WHERE dbid=?";
//								PreparedStatement stmt = conn.prepareStatement(uptSql);
//								stmt.setString(1, alleleAcc);
//								stmt.setString(2, geneAcc);
//								stmt.setString(3, thisAlleleSymbol);
//								stmt.setString(4, "yes");
//								stmt.setString(5, String.valueOf(new Timestamp(System.currentTimeMillis())));
//								stmt.setInt(6, dbid);
//								stmt.executeUpdate();
//							}
//						}
//						else {
//							for (int dbid : dbids) {
//								String insertSql = "INSERT INTO allele_ref ("
//										+ "acc,gacc,symbol,name,pmid,date_of_publication,reviewed,grant_id,agency,acronym,title,journal,datasource,paper_url,timestamp,falsepositive) "
//										+ "SELECT '" + alleleAcc + "','" + geneAcc + "','" + thisAlleleSymbol + "',name,pmid,date_of_publication,'yes',grant_id,agency,acronym,title,journal,datasource,paper_url,'"
//										+ String.valueOf(new Timestamp(System.currentTimeMillis())) + "','no'"
//										+ " FROM allele_ref"
//										+ " WHERE dbid=" + dbid;
//
//								PreparedStatement stmt = conn.prepareStatement(insertSql);
//								stmt.executeUpdate();
//							}
//						}
//						matchedAlleleSymbols.add(thisAlleleSymbol);
//					}
//					else {
//						nonMatchedAlleleSymbols.add(thisAlleleSymbol);
//					}
//				}
//				catch (SQLException se) {
//					//Handle errors for JDBC
//					se.printStackTrace();
//					j.put("reviewed", "no");
//					j.put("symbol", "ERROR: setting symbol failed");
//
//				}
//			}
//
//			if ( nonMatchedAlleleSymbols.size() == alleleSymbols.size() ) {
//				// all symbols not found in KOMP2
//				j.put("reviewed", "no");
//				j.put("symbol", alleleSymbol);
//				j.put("allAllelesNotFound", true);
//			}
//			else {
//				if ( matchedAlleleSymbols.size() == alleleSymbols.size() ){
//					// all matched
//					j.put("reviewed", "yes");
//					j.put("symbol", alleleSymbol);
//				}
//				else {
//					// displays only the matched ones
//					j.put("reviewed", "yes");
//					j.put("symbol", StringUtils.join(matchedAlleleSymbols, ","));
//
//					j.put("someAllelesNotFound", StringUtils.join(nonMatchedAlleleSymbols, ","));
//				}
//			}
//		}
//
//		conn.close();
//		connKomp2.close();
//
//		return j.toString();
//	}
//
//    // allele reference stuff
//    @RequestMapping(value = "/dataTableAlleleRefEdit", method = RequestMethod.GET)
//    public ResponseEntity<String> dataTableAlleleRefEditJson(
//            @RequestParam(value = "iDisplayStart", required = false) Integer iDisplayStart,
//            @RequestParam(value = "iDisplayLength", required = false) Integer iDisplayLength,
//            @RequestParam(value = "sSearch", required = false) String sSearch,
//			@RequestParam(value = "doAlleleRefEdit", required = false) String editParams,
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Model model) throws IOException, URISyntaxException, SQLException {
//
//		JSONObject jParams = (JSONObject) JSONSerializer.toJSON(editParams);
//		Boolean editMode = jParams.getString("editMode").equals("true") ? true : false;
//
//		String content = fetch_allele_ref_edit(iDisplayLength, iDisplayStart, sSearch, editMode);
//        return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
//
//    }
//
//    @RequestMapping(value = "/dataTableAlleleRef", method = RequestMethod.GET)
//    public ResponseEntity<String> dataTableAlleleRefJson(
//            @RequestParam(value = "iDisplayStart", required = false, defaultValue = "0") int iDisplayStart,
//            @RequestParam(value = "iDisplayLength", required = false, defaultValue = "-1") int iDisplayLength,
//            @RequestParam(value = "sSearch", required = false) String sSearch,
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Model model) throws IOException, URISyntaxException, SQLException {
//
//        String content = fetch_allele_ref(iDisplayLength, iDisplayStart, sSearch);
//        return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
//
//    }
//
//    // allele reference stuff
//    @RequestMapping(value = "/alleleRefLogin", method = RequestMethod.POST)
//    public @ResponseBody
//    boolean checkPassCode(
//            @RequestParam(value = "passcode", required = true) String passcode,
//            HttpServletRequest request,
//            HttpServletResponse response,
//            Model model) throws IOException, URISyntaxException, SQLException {
//
//        return checkPassCode(passcode);
//    }
//
//    public boolean checkPassCode(String passcode) throws SQLException {
//
//        Connection conn = admintoolsDataSource.getConnection();
//
//        // prevent sql injection
//        String query = "select password = md5(?) as status from users where name='ebi'";
//        boolean match = false;
//
//        try (PreparedStatement p = conn.prepareStatement(query)) {
//            p.setString(1, passcode);
//            ResultSet resultSet = p.executeQuery();
//
//            while (resultSet.next()) {
//                match = resultSet.getBoolean("status");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            conn.close();
//        }
//
//        return match;
//    }
//
//    public String fetch_allele_ref_edit(int iDisplayLength, int iDisplayStart, String sSearch, Boolean editMode) throws SQLException {
//
//        Connection conn = admintoolsDataSource.getConnection();
//
//        //String likeClause = " like '%" + sSearch + "%'";
//        String like = "%" + sSearch + "%";
//        String query = null;
//
//        if (sSearch != "") {
////            query = "select * as count from allele_ref where "
////					+ " (reviewed = 'no' and"
////					+ " falsepositive = 'no') and"
////                    + " acc like ?"
////                    + " or symbol like ?"
////                    + " or pmid like ?"
////                    + " or date_of_publication like ?"
////                    + " or grant_id like ?"
////                    + " or agency like ?";
////                  //  + " or acronym like ?";
//			query = "SELECT "
//					+ "GROUP_CONCAT(distinct symbol) AS symbol,"
//					+ "pmid,"
//					+ "date_of_publication,"
//					+ "GROUP_CONCAT(grant_id) AS grant_id,"
//					+ "GROUP_CONCAT(agency) AS agency "
//					+ "FROM allele_ref "
//					+ "WHERE (reviewed='no' AND falsepositive='no') "
//					+ "AND (symbol LIKE ? "
//					+ "OR pmid LIKE ? "
//					+ "OR date_of_publication LIKE ? "
//					+ "OR grant_id LIKE ? "
//					+ "OR agency LIKE ?) "
//					+ "GROUP BY pmid ";
//        } else {
//            //query = "select count(distinct pmid) as count from allele_ref";
//			query = "SELECT COUNT(DISTINCT pmid) AS count FROM allele_ref WHERE reviewed='no' AND falsepositive='no'";
//        }
//		//System.out.println("count query: "+query);
//		int rowCount = 0;
//        try (PreparedStatement p1 = conn.prepareStatement(query)) {
//            if (sSearch != "") {
//                for (int i = 1; i < 6; i ++) {
//					p1.setString(i, like);
//                }
//				ResultSet resultSet = p1.executeQuery();
//				while (resultSet.next()) {
//					rowCount++;
//				}
//            }
//			else {
//				ResultSet resultSet = p1.executeQuery();
//				while (resultSet.next()) {
//					rowCount = Integer.parseInt(resultSet.getString("count"));
//				}
//			}
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//		//System.out.println("Got " + rowCount + " rows");
//        JSONObject j = new JSONObject();
//        j.put("aaData", new Object[0]);
//		j.put("iTotalRecords", rowCount);
//		j.put("iTotalDisplayRecords", rowCount);
//
//        String query2 = null;
//
//        if (sSearch != "") {
////            query2 = "select * from allele_ref where"
////					+ " (reviewed='no' and"
////					+ " falsepositive='no') and"
////                    + " (acc like ?"
////                    + " or symbol like ?"
////                    + " or pmid like ?"
////                    + " or date_of_publication like ?"
////                    + " or grant_id like ?"
////                    + " or agency like ?"
////                    + " or acronym like ?)"
////                    + " order by reviewed"
////                    + " limit ?, ?";
//			query2 = "SELECT GROUP_CONCAT(dbid) AS dbid,"
//				+ "reviewed,"
//				+ "GROUP_CONCAT(distinct gacc) AS gacc,"
//				+ "GROUP_CONCAT(distinct symbol) AS symbol,"
//				+ "pmid,"
//				+ "date_of_publication,"
//				+ "GROUP_CONCAT(grant_id) AS grant_id,"
//				+ "GROUP_CONCAT(agency) AS agency,"
//				+ "GROUP_CONCAT(acronym) AS acronym,"
//				+ "paper_url "
//				+ "FROM allele_ref "
//				+ "WHERE (reviewed='no' AND falsepositive='no') "
//				+ "AND (symbol LIKE ? "
//				+ "OR pmid LIKE ? "
//				+ "OR date_of_publication LIKE ? "
//				+ "OR grant_id LIKE ? "
//				+ "OR agency LIKE ? ) "
//				+ "GROUP BY pmid "
//				+ "ORDER BY reviewed DESC "
//				+ "LIMIT ?, ?";
//        } else {
//            //query2 = "select * from allele_ref where reviewed='no' and falsepositive='no' limit ?,?";
//			query2 = "SELECT GROUP_CONCAT(dbid) AS dbid,"
//				+ "reviewed,"
//				+ "GROUP_CONCAT(distinct gacc) AS gacc,"
//				+ "GROUP_CONCAT(distinct symbol) AS symbol,"
//				+ "pmid,"
//				+ "date_of_publication,"
//				+ "GROUP_CONCAT(grant_id) AS grant_id,"
//				+ "GROUP_CONCAT(agency) AS agency,"
//				//+ "GROUP_CONCAT(acronym) AS acronym,"
//				+ "paper_url "
//				+ "FROM allele_ref "
//				+ "WHERE (reviewed='no' AND falsepositive='no') "
//				+ "GROUP BY pmid "
//				+ "ORDER BY reviewed DESC limit ?,?";
//        }
//
////		System.out.println("query: "+ query);
////        System.out.println("query2: "+ query2);
////		System.out.println("start: " + iDisplayStart + " end: " + iDisplayLength);
//		String impcGeneBaseUrl = "http://www.mousephenotype.org/data/genes/";
//
//        try (PreparedStatement p2 = conn.prepareStatement(query2)) {
//            if (sSearch != "") {
//                for (int i = 1; i < 8; i ++) {
//                    p2.setString(i, like);
//                    if (i == 6) {
//                        p2.setInt(i, iDisplayStart);
//                    } else if (i == 7) {
//                        p2.setInt(i, iDisplayLength);
//                    }
//				}
//            } else {
//                p2.setInt(1, iDisplayStart);
//                p2.setInt(2, iDisplayLength);
//            }
//
//            ResultSet resultSet = p2.executeQuery();
//
//
//            while (resultSet.next()) {
//
//				List<String> rowData = new ArrayList<String>();
//
//				// dbid has been concatanated so becomes a string
//				String dbidStr = resultSet.getString("dbid");
//
//				//int dbid = resultSet.getInt("dbid");
//
//                String gacc = resultSet.getString("gacc");
//
//				rowData.add("<input type='checkbox'>");
//                rowData.add(resultSet.getString("reviewed"));
//
//                //rowData.add(resultSet.getString("acc"));
//				String alleleSymbol = Tools.superscriptify(resultSet.getString("symbol"));
//				//String alLink = alleleSymbol.equals("") ? "" : "<a target='_blank' href='" + impcGeneBaseUrl + resultSet.getString("gacc") + "'>" + alleleSymbol + "</a>";
//				rowData.add(alleleSymbol);
//
//                //rowData.add(resultSet.getString("name"));
//                //String pmid = "<span id=" + dbid + ">" + resultSet.getString("pmid") + "</span>";
//				String pmid = "<span class='pmid' id=" + dbidStr + ">" + resultSet.getString("pmid") + "</span>";
//                rowData.add(pmid);
//
//                rowData.add(resultSet.getString("date_of_publication"));
//
//                rowData.add(resultSet.getString("grant_id"));
//                rowData.add(resultSet.getString("agency"));
//                //rowData.add(resultSet.getString("acronym").replaceAll("\\s,|,\\s|\\s,\\s|,$", ""));
//                String[] urls = resultSet.getString("paper_url").split(",");
//                List<String> links = new ArrayList<>();
////                for (int i = 0; i < urls.length; i ++) {
////                    links.add("<a target='_blank' href='" + urls[i] + "'>paper</a>");
////                }
//
//				// just show one paper: although they are from different sources, but are actually the same paper
//				links.add("<a target='_blank' href='" + urls[0] + "'>paper</a>");
//                rowData.add(StringUtils.join(links, "<br>"));
//
//                j.getJSONArray("aaData").add(rowData);
//            }
//
//        }
//		catch (Exception e) {
//            e.printStackTrace();
//        }
//		finally {
//            conn.close();
//        }
//        return j.toString();
//    }
//
//    public String fetch_allele_ref(int iDisplayLength, int iDisplayStart, String sSearch) throws SQLException {
//        final int DISPLAY_THRESHOLD = 4;
//        List<org.mousephenotype.cda.db.pojo.ReferenceDTO> references = referenceDAO.getReferenceRows(sSearch);
//
//        JSONObject j = new JSONObject();
//        j.put("aaData", new Object[0]);
//        j.put("iTotalRecords", references.size());
//        j.put("iTotalDisplayRecords", references.size());
//
//        // MMM to digit conversion
//        Map<String, String> m2d = new HashMap<>();
//        // the digit part is set as such to work with the default non-natural sort behavior so that
//        // 9 will not be sorted after 10
//        m2d.put("Jan","11");
//        m2d.put("Feb","12");
//        m2d.put("Mar","13");
//        m2d.put("Apr","14");
//        m2d.put("May","15");
//        m2d.put("Jun","16");
//        m2d.put("Jul","17");
//        m2d.put("Aug","18");
//        m2d.put("Sep","19");
//        m2d.put("Oct","20");
//        m2d.put("Nov","21");
//        m2d.put("Dec","22");
//
//        for (org.mousephenotype.cda.db.pojo.ReferenceDTO reference : references) {
//
//        	List<String> rowData = new ArrayList<>();
//        	Map<String,String> alleleSymbolinks = new LinkedHashMap<String,String>();
//
//            int alleleAccessionIdCount = reference.getAlleleAccessionIds().size();
//            for (int i = 0; i < alleleAccessionIdCount; i++) {
//
//                String symbol = Tools.superscriptify(reference.getAlleleSymbols().get(i));
//                String alleleLink;
//                String cssClass = "class='" +  (alleleSymbolinks.size() < DISPLAY_THRESHOLD ? "showMe" : "hideMe") + "'";
//
//				if (i < reference.getImpcGeneLinks().size()) {
//                		alleleLink = "<div " + cssClass + "><a target='_blank' href='" + reference.getImpcGeneLinks().get(i) + "'>" + symbol + "</a></div>";
//                } else {
//                    if (i > 0) {
//                    	alleleLink = "<div " + cssClass + "><a target='_blank' href='" + reference.getImpcGeneLinks().get(0) + "'>" + symbol + "</a></div>";
//                    } else {
//                    	alleleLink = alleleLink = "<div " + cssClass + ">" + symbol + "</div>";
//                    }
//                }
//                alleleSymbolinks.put(symbol, alleleLink);
//            }
//
//            if (alleleSymbolinks.size() > 5){
//            	int num = alleleSymbolinks.size();
//            	alleleSymbolinks.put("toggle", "<div class='alleleToggle' rel='" + num + "'>Show all " + num + " alleles ...</div>");
//            }
//
//            List<String> alLinks = new ArrayList<>();
//            Iterator it = alleleSymbolinks.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry pair = (Map.Entry)it.next();
//                alLinks.add(pair.getValue().toString());
//                it.remove(); // avoids a ConcurrentModificationException
//            }
//
//            rowData.add(StringUtils.join(alLinks, ""));
//
//            rowData.add(reference.getTitle());
//            rowData.add(reference.getJournal());
//
//            String oriPubDate = reference.getDateOfPublication();
//
//            String altStr = null;
//            oriPubDate = oriPubDate.trim();
//            if ( oriPubDate.matches("^\\d+$") ){
//            	altStr = oriPubDate + "-23"; // so that YYYY will be sorted after YYYY MMM
//            }
//            else {
//            	String[] parts = oriPubDate.split(" ");
//            	altStr = parts[0] + "-" + m2d.get(parts[1]);
//            }
//
//            // alt is for alt-string sorting in dataTable for date_of_publication field
//            // The format is either YYYY or YYYY Mmm (2012 Jul, eg)
//            // I could not get sorting to work with this column using dataTable datetime-moment plugin (which supports self-defined format)
//            // but I managed to get it to work with alt-string
//            rowData.add("<span alt='" + altStr + "'>" + oriPubDate + "</span>");
//
//            List<String> agencyList = new ArrayList();
//            int agencyCount = reference.getGrantAgencies().size();
//
//            for (int i = 0; i < agencyCount; i++) {
//                String cssClass = "class='" +  (i < DISPLAY_THRESHOLD ? "showMe" : "hideMe") + "'";
//                String grantAgency = reference.getGrantAgencies().get(i);
//                if ( ! grantAgency.isEmpty()) {
//                    agencyList.add("<li " + cssClass + ">" + grantAgency + "</li>");
//                }
//            }
//            rowData.add("<ul>" + StringUtils.join(agencyList, "") + "</ul>");
//
//            int pmid = Integer.parseInt(reference.getPmid());
//            List<String> paperLinks = new ArrayList<>();
//            List<String> paperLinksOther = new ArrayList<>();
//            List<String> paperLinksPubmed = new ArrayList<>();
//            List<String> paperLinksEuroPubmed = new ArrayList<>();
//            String[] urlList = (reference.getPaperUrls() != null) ? reference.getPaperUrls().toArray(new String[0]) : new String[0];
//
//            for (int i = 0; i < urlList.length; i ++) {
//                String[] urls = urlList[i].split(",");
//
//                int pubmedSeen = 0;
//                int eupubmedSeen = 0;
//                int otherSeen = 0;
//
//                for (int k = 0; k < urls.length; k ++) {
//                    String url = urls[k];
//
//                    if (pubmedSeen != 1) {
//                        if (url.startsWith("http://www.pubmedcentral.nih.gov") && url.endsWith("pdf")) {
//                            paperLinksPubmed.add("<li><a target='_blank' href='" + url + "'>Pubmed Central</a></li>");
//                            pubmedSeen ++;
//                        } else if (url.startsWith("http://www.pubmedcentral.nih.gov") && url.endsWith(Integer.toString(pmid))) {
//                            paperLinksPubmed.add("<li><a target='_blank' href='" + url + "'>Pubmed Central</a></li>");
//                            pubmedSeen ++;
//                        }
//                    }
//                    if (eupubmedSeen != 1) {
//                        if (url.startsWith("http://europepmc.org/") && url.endsWith("pdf=render")) {
//                            paperLinksEuroPubmed.add("<li><a target='_blank' href='" + url + "'>Europe Pubmed Central</a></li>");
//                            eupubmedSeen ++;
//                        } else if (url.startsWith("http://europepmc.org/")) {
//                            paperLinksEuroPubmed.add("<li><a target='_blank' href='" + url + "'>Europe Pubmed Central</a></li>");
//                            eupubmedSeen ++;
//                        }
//                    }
//                    if (otherSeen != 1 &&  ! url.startsWith("http://www.pubmedcentral.nih.gov") &&  ! url.startsWith("http://europepmc.org/")) {
//                        paperLinksOther.add("<li><a target='_blank' href='" + url + "'>Non-pubmed source</a></li>");
//                        otherSeen ++;
//                    }
//                }
//            }
//
//            // ordered
//            paperLinks.addAll(paperLinksEuroPubmed);
//            paperLinks.addAll(paperLinksPubmed);
//            paperLinks.addAll(paperLinksOther);
//            rowData.add(StringUtils.join(paperLinks, ""));
//
//            j.getJSONArray("aaData").add(rowData);
//        }
//
//        //System.out.println("Got " + rowCount + " rows");
//        return j.toString();
//    }

	private HttpHeaders createResponseHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}
}
