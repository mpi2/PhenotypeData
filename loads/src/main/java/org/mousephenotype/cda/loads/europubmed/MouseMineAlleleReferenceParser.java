package org.mousephenotype.cda.loads.europubmed;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
@Import(value = {MouseMineAlleleReferenceParserConfig.class})
public class MouseMineAlleleReferenceParser implements CommandLineRunner {

	private static final String ROOT = "http://www.mousemine.org/mousemine/service";
	private static final Logger logger = LoggerFactory.getLogger(MouseMineAlleleReferenceParser.class);
	private static Connection connection;

	private static Map<Integer, Pubmed> pubmed = new HashMap<>();
	private static Set<Integer> mouseminePmids = new HashSet<>();
	private static Map<Integer, Map<String, List<String>>> pmid2Alleles = new HashMap<>();
	private static Set<Integer> newEuropubmedPmids = new HashSet<>();
	private static Set<String> mouseminePmidQrys = new HashSet<>();
	private static Map<String, String> allele2GeneMapping = new HashMap<>();
	private static Set<Integer> newlyLoadedMouseMine = new HashSet<>();
	private static Set<Integer> updatedEuropubmedUsingMousemine = new HashSet<>();
	private static Set<Integer> newlyLoadedEuropubmed = new HashSet<>();

	private static Set<String> pmidsFromCitation = new HashSet<>();
	private static Set<Integer> newlyLoadedFromCitation = new HashSet<>();

	final String delimiter = "|||";

	private DataSource admintoolsDataSource;
	private DataSource komp2DataSource;
	private String meshTreeText;
	private String mousemine;

	private static PreparedStatement insertStatement;
	private static PreparedStatement updateStatement;
	private static Map<String, String> topMesh = new HashMap<>();
	private static Map<String, String> child2TopMesh = new HashMap<>();
	private static Map<String, String> nameId = new HashMap<>();

	@Inject
	public MouseMineAlleleReferenceParser(
			@Named("admintoolsDataSource") DataSource admintoolsDataSource,
			@Named("komp2DataSource") DataSource komp2DataSource,
            @Value("${mousemine}") String mousemine,
            @Value("${meshTreeText}") String meshTreeText) {

		Assert.notNull(admintoolsDataSource, "admintoolsDataSource cannot be null");
		Assert.notNull(komp2DataSource, "komp2DataSource cannot be null");
        Assert.notNull(meshTreeText, "meshTreeText cannot be null");
        Assert.notNull(mousemine, "mousemine cannot be null");

		this.admintoolsDataSource = admintoolsDataSource;
		this.komp2DataSource = komp2DataSource;
		this.meshTreeText = meshTreeText;
		this.mousemine = mousemine;
	}

	public static void main(String[] args) {
		SpringApplication.run(MouseMineAlleleReferenceParser.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		logger.info("LOADING ALLELE PAPERS to DATABASE:");

		connection = admintoolsDataSource.getConnection();
		connection.setAutoCommit(false); // start transaction

		//createTable();

		//--------------------
		// REQUIRED TO RUN
		//--------------------
		//---------------------------------------------------------------
		// maps all child terms to top level terms (2017)
		// https://www.nlm.nih.gov/mesh/2017/download/2017MeshTree.txt
		//---------------------------------------------------------------
		meshTermMappings(); // stored in database: admintools.mesh_mapping

		alleleGeneAccMapping();
		parseIntermine1();

		Boolean doCitation = false;
		fetchEuropePubmedData(doCitation);
		loadReferenceDataToDatabase();
		paperMeshTopmesh(); // mesh heading to top level mesh term mapping

		//--------------------
		//     CITATIONS
		//--------------------
		loadPapersCitingConsortiumPapers();
		//http://www.ebi.ac.uk/europepmc/webservices/rest/search/query=CITES:27523608_MED&format=json&resulttype=core


		// do this once on 2017-02-10 for the first time, then add datasource found per week to this table: by method loadNewPaperByDatasourceWeekly
		loadNewPaperByDatasourceWeekly();  // a cronjob is run weekly

		 //RUN THIS EVERY FIRST DAY OF MONTH fron cron job
		// this is the data where the line chart is based on
		//String mSql = "INSERT INTO paper_by_year_monthly_increase " +
		//	"SELECT CURDATE(), LEFT(date_of_publication,4), COUNT(left(date_of_publication,4)) AS count " +
		//	"FROM allele_ref WHERE falsepositive = 'no' GROUP BY LEFT(date_of_publication,4)";

		//-------------------
		//   RUN ON DEMAND
		///------------------
		//updatePublicationGrantMeshTerms(); // can be run stand alone
		//updateDatasource();
		//updateAbstract();

		connection.close();

		logger.info("NEW MOUSEMINE pmids added " + newlyLoadedMouseMine.size());
		logger.info("EXISTING EUROPE PUBMED pmids updated using MOUSEMINE " + updatedEuropubmedUsingMousemine.size());
		logger.info("NEW EUROPE PUBMED pmids added " + newlyLoadedEuropubmed.size());
		System.out.println("");

		logger.info("Job done");


	}

	private void loadPapersCitingConsortiumPapers() throws SQLException {

		// fetch existing pmids in allele_ref
		String sql2 = "SELECT pmid FROM allele_ref";

		Set<Integer> pmidKnown = new HashSet<>();
		try (PreparedStatement p2 = connection.prepareStatement(sql2)) {
			ResultSet resultSet2 = p2.executeQuery();

			while (resultSet2.next()) {
				int pmid = resultSet2.getInt("pmid");
				pmidKnown.add(pmid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		String sql = "SELECT pmid FROM allele_ref WHERE consortium_paper ='yes'";
		//String sql = "SELECT pmid FROM allele_ref WHERE pmid =17218247"; // test

		Map<Integer, Set<Integer>> consortiumPmidCited = new HashMap<>();
		try (PreparedStatement p = connection.prepareStatement(sql)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				int pmid = resultSet.getInt("pmid");
				fetchCitingPmids(pmid, consortiumPmidCited);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		updateStatement = connection.prepareStatement("UPDATE allele_ref SET cites_consortium_paper='yes' WHERE pmid=?");

		List<Integer> newPmidsFromCitation = new ArrayList<>();
		Iterator cc = consortiumPmidCited.entrySet().iterator();
		while (cc.hasNext()) {

			Map.Entry pair = (Map.Entry) cc.next();
			int pmid = Integer.parseInt(pair.getKey().toString());
			Set<Integer> cites = (Set<Integer>)pair.getValue();
			//System.out.println(pmid + " cited by " + Arrays.toString(cites.toArray()));
			//System.out.println(pmid + " cited by "+ cites.size() + " citations");
			// fetch new paper from cited by
			for(Integer citing : cites){
				pmidsFromCitation.add("ext_id:" + citing);

				// add cites_consortium_paper to this pmid
				add_cites_consortium_paper(citing, updateStatement);

				if (! (pmidKnown.contains(citing) ||
						newlyLoadedMouseMine.contains(citing) ||
						newlyLoadedEuropubmed.contains(citing)) ){
					newPmidsFromCitation.add(citing);
				}
			}
		}
		connection.commit();

		logger.info("Found {} pmids from citations, {} of which are new", pmidsFromCitation.size(), newPmidsFromCitation.size());

		// load new pmids from citations
		Boolean doCitation = true;
		fetchEuropePubmedData(doCitation);

		// insert new mousemine pmid and ignore existing ones
		insertStatement = connection.prepareStatement("REPLACE INTO allele_ref "
				+ "(gacc, acc, symbol, name, pmid, date_of_publication, reviewed, grant_id, agency, acronym, title, journal, paper_url, datasource, timestamp, falsepositive, mesh, meshtree, author, abstract, cited_by) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		for(Integer p : newPmidsFromCitation){
			//System.out.println("check query pmid: "+p);
			saveNewEuropubmedPapers(p, "manual");
			newlyLoadedFromCitation.add(p);
		}

		// add cited_by to consortium papers
		updateConsortiumPmid(consortiumPmidCited);

		connection.commit();
		logger.info("SAVED {} NEW PMIDs FROM PAPER CITING CONSORTIUM PAPERS", newlyLoadedFromCitation.size());
	}

	private void add_cites_consortium_paper(Integer pmid, PreparedStatement updateStatement) throws SQLException {
		updateStatement.setInt(1, pmid);
		updateStatement.executeUpdate();
	}


	//private void updateConsortiumPmid(int pmid, Set<Integer> cites, PreparedStatement updateStatement) throws SQLException {
	private void updateConsortiumPmid(Map<Integer, Set<Integer>> consortiumPmidCited) throws SQLException {

		updateStatement = connection.prepareStatement("UPDATE allele_ref SET cited_by=?  WHERE pmid=?");

		Iterator cc = consortiumPmidCited.entrySet().iterator();
		while (cc.hasNext()) {

			Map<String, List<String>> dateLink = new HashMap<>();
			TreeSet<String> dateofpub = new TreeSet<>(Collections.<String>reverseOrder());
			List<String> links = new ArrayList<>();

			Map.Entry pair = (Map.Entry) cc.next();
			int pmid = Integer.parseInt(pair.getKey().toString());
			Set<Integer> cites = (Set<Integer>) pair.getValue();

			//System.out.println(pmid + " : found " + cites.size() + " citations");
			for (int cite : cites) {

				Pubmed pub = pubmed.get(cite);

				if (pub == null) {
					//System.out.println(cite + " has no data --> check");
					continue;
				}

				String title = pub.title;
				String dop = pub.dateOfPublication;
				dateofpub.add(dop);

				if (! dateLink.containsKey(dop)){
					dateLink.put(dop, new ArrayList<String>());
				}

				List<Paperurl> paperUrls = pub.paperurls;
				if (paperUrls.size() > 0) {
					String pUrl = paperUrls.get(0).url;
					dateLink.get(dop).add("<li><a target='_blank' href='" + pUrl + "'>" + title + "</a> (" + dop + ")</li>");

				} else {
					dateLink.get(dop).add("<li>" + title + " (" + dop + ")</li>");
				}
			}

			// added in the order of date of publication
			for (String dop : dateofpub){
				links.addAll(dateLink.get(dop));
			}
			//System.out.println(pmid + " : added " + links.size() + " citations");

			try {
				updateStatement.setString(1, StringUtils.join(links, delimiter));
				updateStatement.setInt(2, pmid);

				updateStatement.executeUpdate();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		connection.commit();
	}

	private void fetchCitingPmids(int pmid, Map<Integer, Set<Integer>> consortiumPmidCited) {

		String dbfetchUrl = "http://www.ebi.ac.uk/europepmc/webservices/rest/search/format=json&resulttype=idlist&pageSize=500&query=CITES:";

		JSONObject json = fetchHttpUrlJson(dbfetchUrl + pmid + "_MED");

		//System.out.println("check json: "+ json);
		JSONArray results = json.getJSONObject("resultList").getJSONArray("result");

		for (int j = 0; j < results.size(); j++) {

			JSONObject r = results.getJSONObject(j);

			if (r.containsKey("pmid")) {
				int thisPmid = Integer.parseInt(r.getString("pmid"));
				if (! consortiumPmidCited.containsKey(pmid)) {
					consortiumPmidCited.put(pmid, new HashSet<Integer>());
				}
				consortiumPmidCited.get(pmid).add(thisPmid);
			}
		}
	}

	private void updateDatasource() throws SQLException {

		// FIDDLE SANDBOX

		int ct = 0;
//		List<Integer> asManual = new ArrayList<>();
//		for ( Integer pmid : nih){
//			if ( ! mouseminePmids.contains(pmid)){
//				ct++;
//				System.out.println("NOT in mousemine: " + pmid);
//				asManual.add(pmid);
//			}
//
//		}
		System.out.println(ct + " not in mouse");
		//System.out.println(StringUtils.join(asManual, ","));
//

//		connection = admintoolsDataSource.getConnection();
//		String sql = "UPDATE allele_ref SET datasource='mousemine' where pmid = ?";
//		insertStatement = connection.prepareStatement(sql);
//
//		try {
//			for (Integer pmid : nih) {
//				insertStatement.setInt(1, pmid);
//				insertStatement.executeUpdate();
//			}
//		}
//		catch(Exception e){
//			System.out.println("Update datasource failed: " + e.getMessage());
//		}
//
//		System.out.println("successful");
	}


	private void loadNewPaperByDatasourceWeekly() throws SQLException {
        logger.info("START RUNNING loadNewPaperByDatasourceWeekly()");
        connection = admintoolsDataSource.getConnection();

		List<String> insertStrList = new ArrayList<>();

		Map<String, Set<Integer>> srcPmids = new HashMap<>();
		srcPmids.put("mousemine", newlyLoadedMouseMine);
		srcPmids.put("europubmed", newlyLoadedEuropubmed);
		srcPmids.put("manual", newlyLoadedFromCitation);

		Iterator itsrc = srcPmids.entrySet().iterator();
		while (itsrc.hasNext()) {

            Map.Entry pair = (Map.Entry)itsrc.next();
			String datasource = pair.getKey().toString();
			Set<Integer> srcpmids = (HashSet<Integer>) pair.getValue();

			Map<String, Integer> yearCount = new HashMap<>();
			for (Integer pmid : srcpmids) {
                Pubmed pub = pubmed.get(pmid);
				String year = pub.dateOfPublication.substring(0,4);
				if ( ! yearCount.containsKey(year)){
					yearCount.put(year, 1);
				}
				else {
					yearCount.put(year, yearCount.get(year) + 1);
				}
			}

			if (yearCount.size() > 0){
				Iterator ityr = yearCount.entrySet().iterator();
				while (ityr.hasNext()) {
					Map.Entry pair2 = (Map.Entry) ityr.next();
					String year = pair2.getKey().toString();
					Integer count = (Integer) pair2.getValue();

					insertStrList.add("(CURDATE(), '" + year + "', '" + datasource + "', " + count + ")");
                }
				ityr.remove();
			}

			itsrc.remove(); // avoids a ConcurrentModificationException
		}

		if (insertStrList.size() > 0) {
			String tablename = "datasource_by_year_weekly_increase";
			String sql = "INSERT INTO " + tablename + " VALUES " + StringUtils.join(insertStrList, ", ");
            insertStatement = connection.prepareStatement(sql);

			try {
				insertStatement.executeUpdate();

				String sql2= "select sum(count) as counts from datasource_by_year_weekly_increase group by date order by date desc limit 1";
				PreparedStatement p = connection.prepareStatement(sql2);
				ResultSet resultSet = p.executeQuery();

				while (resultSet.next()) {
					int counts = resultSet.getInt("counts");
					logger.info("ADDED {} PAPER(s) TO {} table successfully", counts, tablename);
				}

            } catch (Exception e) {
				logger.info("FAILED TO ADD NEW PAPER COUNTS TO {} table: {}", tablename, e.getMessage());
			}
		}

	}

	private void alleleGeneAccMapping() throws SQLException {

		Connection komp2Connection = komp2DataSource.getConnection();
		String query = "select acc, gf_acc, symbol from allele";

		try (PreparedStatement p = komp2Connection.prepareStatement(query)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				// allele id to gene id mapping
				//System.out.println("allele: " + resultSet.getString("acc") + " => gene: " + resultSet.getString("gf_acc"));
				String acc = resultSet.getString("acc");
				if ( ! allele2GeneMapping.containsKey(acc) ){
					allele2GeneMapping.put(acc, resultSet.getString("gf_acc"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			komp2Connection.close();
			System.out.println("ALLELE ID TO GENE ID MAPPING USING KOMP2 DATABASE: ");
			logger.info("Populated " + allele2GeneMapping.size() + " allele id to gene id mappings");
			System.out.println("");
		}
	}

	private void createTable() throws SQLException{
		// (1.gacc, 2.acc, 3.symbol, 4.name, 5.pmid, 6.date_of_publication,
		// 7.reviewed, 8.grant_id, 9.agency, 10.acronym, 11.title,
		// 12.journal, 13.paper_url, 14.datasource, 15.timestamp, 16.falsepositive, 17.mesh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)");

		String[] statements = {
				"DROP TABLE IF EXISTS allele_ref",
				"CREATE TABLE allele_ref ("
						+ "dbid int(12) UNSIGNED NOT NULL AUTO_INCREMENT,"
						+ "gacc text NOT NULL,"
						+ "acc text NOT NULL,"
						+ "symbol text NOT NULL,"
						+ "name text NOT NULL,"
						+ "pmid int(10) NOT NULL,"
						+ "date_of_publication varchar(30) NOT NULL,"
						+ "reviewed enum('yes', 'no') default 'no',"
						+ "grant_id text NOT NULL,"
						+ "agency text NOT NULL,"
						+ "acronym text NOT NULL,"
						+ "title text NOT NULL,"
						+ "journal text NOT NULL,"
						+ "paper_url text NOT NULL,"
						+ "datasource varchar(10) NOT NULL,"
						+ "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
						+ "falsepositive enum('yes','no') default 'no',"
						+ "mesh text NOT NULL,"
						+ "meshtree text NOT NULL,"
						+ "author text NOT NULL,"
                        + "consortium_paper enum('yes', 'no') default 'no',"
						+ "abstract text,"
						+ "cited_by longtext NOT NULL,"
						+ "cites_consortium_paper enum('yes', 'no') default 'no',"
						+ "PRIMARY KEY (dbid) )  DEFAULT CHARACTER SET utf8",
				"ALTER TABLE allele_ref add unique key (pmid)",
				"ALTER TABLE allele_ref ADD INDEX (grant_id(50))",
				"ALTER TABLE allele_ref ADD INDEX (datasource)",
				"ALTER TABLE allele_ref ADD INDEX (reviewed, falsepositive)"
		};

		Statement currentStatement = null;

		for (String stmt: statements) {
			try {
				// Execute statement
				currentStatement = connection.createStatement();
				currentStatement.execute(stmt);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// Release resources
				if (currentStatement != null) {
					try {
						currentStatement.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		connection.commit(); //transaction block end
	}

	private void meshTermMappings() throws IOException, SQLException {
		// maps mesh terms to their top levels

		// this file has utf-16 binary charset in it, so is seen as a binary file
		// use file -I filename to view it or hexdump -c to see the unwanted charset
		// command: curl https://www.nlm.nih.gov/mesh/2017/download/2017MeshTree.txt | iconv -f utf-16le -t utf-8 | awk '{if(NR==1)sub(/^\xef\xbb\xbf/,"");print}' > 2017MeshTree.txt
		File file = new File(meshTreeText);

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;

		fis = new FileInputStream(file);

		// Here BufferedInputStream is added for fast reading.
		bis = new BufferedInputStream(fis);
		dis = new DataInputStream(bis);

//		Map<String, String> topMesh = new HashMap<>();
//		Map<String, String> child2TopMesh = new HashMap<>();
//		Map<String, String> nameId = new HashMap<>();

		// dis.available() returns 0 if the file does not have more lines.
		Pattern topPat = Pattern.compile("([A-Z]\\d{2,2})\\s+(D\\d+)\\s+(.+)");
		Pattern childPat = Pattern.compile("(([A-Z]\\d{2,2})\\..*)\\s+(D\\d+)\\s+(.+)");

		List<String> values = new ArrayList<>();

		try {
			insertStatement = connection.prepareStatement("INSERT IGNORE INTO mesh_mapping "
					+ "(mesh_heading, mesh_heading_id, top_mesh, top_mesh_id) "
					+ "VALUES (?,?,?,?)");


			while (dis.available() != 0) {
				String line = dis.readLine();
				//Z01	D005842	Geographic Locations
				//System.out.println(line);
				Matcher topM = topPat.matcher(line);
				Matcher childM = childPat.matcher(line);
				if (topM.find()) {
					String id = topM.group(1);
					String meshId = topM.group(2);
					String name = topM.group(3);
					//System.out.println("----- "+ id);
					//System.out.println("----- "+ name);
					topMesh.put(id, name);
					nameId.put(name, meshId);
				} else if (childM.find()) {

					String id = childM.group(1);
					String topId = childM.group(2);
					String meshId = childM.group(3);
					String name = childM.group(4);
					//				System.out.println("----- "+ id);
					//				System.out.println("----- "+ topId);
					//				System.out.println("----- "+ name);
					//System.out.println( name + " -> " + topMesh.get(topId) );
					child2TopMesh.put(name, topMesh.get(topId));
					nameId.put(name, meshId);

					insertStatement.setString(1, name);
					insertStatement.setString(2, meshId);
					insertStatement.setString(3, topMesh.get(topId));
					insertStatement.setString(4, topId);
					insertStatement.executeUpdate();
				}
			}
			connection.commit();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	private void paperMeshTopmesh() throws SQLException, IOException {
        logger.info("START RUNNING paperMeshTopmesh()");
        Map<String, HashSet<String>> top2headings = new HashMap<>();
		Map<String, Set<Integer>> heading2pmid = new HashMap<>();
		//Map<String, List<String>> top2headings2 = new HashMap<>();

		PreparedStatement ps = connection.prepareStatement("SELECT pmid FROM allele_ref");

		ResultSet resultSet = ps.executeQuery();
		while (resultSet.next()) {
			Integer pmid = resultSet.getInt("pmid");

			String dbfetchUrl = "http://www.ebi.ac.uk/europepmc/webservices/rest/search/query=ext_id:" + pmid + "%20and%20src:MED&format=json&resulttype=core";
			//System.out.println(dbfetchUrl);
			// Get the page and print it.
			JSONObject json = fetchHttpUrlJson(dbfetchUrl);

			//System.out.println("check json: "+ json);
			JSONArray results = json.getJSONObject("resultList").getJSONArray("result");

			for ( int j=0; j<results.size(); j++ ) {

				JSONObject r = results.getJSONObject(j);

				List<String> meshTerms = new ArrayList<>();

				if ( r.containsKey("meshHeadingList") ){
					JSONArray meshHeadings = r.getJSONObject("meshHeadingList").getJSONArray("meshHeading");
					for ( int mh=0; mh<meshHeadings.size(); mh++ ){
						JSONObject thisMeshHeading = (JSONObject) meshHeadings.get(mh);

						String meshHeading = null;
						if ( thisMeshHeading.containsKey("descriptorName") ){
							meshHeading = thisMeshHeading.getString("descriptorName");
							meshTerms.add(meshHeading);

							// only mesh heading are mapped in the mapping source file
							String topMeshTerm = child2TopMesh.get(meshHeading);
//							System.out.println("Mesh heading: "+ meshHeading + " ---> " + topMeshTerm);
							if ( topMeshTerm != null ) {
								if (!top2headings.containsKey(topMeshTerm)) {
									top2headings.put(topMeshTerm, new HashSet<String>());
									//top2headings2.put(topMeshTerm, new ArrayList<String>());
								}

								top2headings.get(topMeshTerm).add(meshHeading);
								//top2headings2.get(topMeshTerm).add(meshHeading);

								if (! heading2pmid.containsKey(meshHeading)){
									heading2pmid.put(meshHeading, new HashSet<Integer>());
								}
								heading2pmid.get(meshHeading).add(pmid);

							}
						}

						// mesh subheading
						List<String>meshQuas = new ArrayList<>();
						if ( thisMeshHeading.containsKey("meshQualifierList") ) {
							JSONArray meshQualifiers= thisMeshHeading.getJSONObject("meshQualifierList").getJSONArray("meshQualifier");
							for (int mq = 0; mq < meshQualifiers.size(); mq++) {
								JSONObject thisMeshQualifier = (JSONObject) meshQualifiers.get(mq);
								if ( thisMeshQualifier.containsKey("qualifierName") ){
									String qf = thisMeshQualifier.getString("qualifierName");
									if ( ! meshQuas.contains(qf)) {
										meshTerms.add(meshHeading + " " + qf);
									}
								}
							}
						}
					}
				}

				//System.out.println(pmid + " ---> " + StringUtils.join(meshTerms, delimiter));

//				preparedStatement.setString(1, StringUtils.join(meshTerms, delimiter));
//				preparedStatement.setInt(2, pmid);
//				preparedStatement.executeUpdate();

			}
		}


		try {
			Map<String, HashSet<String>> map = new TreeMap<>(top2headings);

			String sql = "REPLACE INTO paperMeshTopmesh (top_mesh, number_mapped_mesh, number_mapped_pmid) VALUES (?,?,?)";
			insertStatement = connection.prepareStatement(sql);

			Iterator it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				String topMeshTerm = pair.getKey().toString();

				Set<String> headings = (HashSet<String>) pair.getValue();

				Set<Integer> uniqPmids = new HashSet<>();
				for (String heading : headings) {
					uniqPmids.addAll(heading2pmid.get(heading));
				}

				int papercount = uniqPmids.size();

				insertStatement.setString(1, topMeshTerm);
				insertStatement.setInt(2, headings.size());
				insertStatement.setInt(3, papercount);
				insertStatement.executeUpdate();

				//int numPaper = top2headings2.get(topMeshTerm).size();
				//System.out.println("<tr><td>" + topMeshTerm + " (" + nameId.get(topMeshTerm) + ")</td><td>" + headings.size() + " / " + papercount + "</td><td>" + headings + "</td></tr>");
				it.remove(); // avoids a ConcurrentModificationException
			}
			//connection.commit();
            logger.info("DONE RUNNING paperMeshTopmesh()");

			connection.commit();
        }
		catch(Exception e){
			logger.error(e.getMessage());
		}
	}

	private void updateAbstract() throws SQLException {

		PreparedStatement preparedStatement = connection.prepareStatement("UPDATE allele_ref SET abstract = ? WHERE pmid = ?");

		PreparedStatement ps = connection.prepareStatement("SELECT pmid FROM allele_ref");

		int counter = 0;
		ResultSet resultSet = ps.executeQuery();
		while (resultSet.next()) {
			Integer pmid = resultSet.getInt("pmid");

			String dbfetchUrl = "http://www.ebi.ac.uk/europepmc/webservices/rest/search/query=ext_id:" + pmid + "%20and%20src:MED&format=json&resulttype=core";
			//System.out.println(dbfetchUrl);
			// Get the page and print it.
			JSONObject json = fetchHttpUrlJson(dbfetchUrl);

			//System.out.println("check json: "+ json);
			JSONArray results = json.getJSONObject("resultList").getJSONArray("result");

			for ( int j=0; j<results.size(); j++ ) {

				counter++;
				JSONObject r = results.getJSONObject(j);

				//System.out.println(r.toString());

				String abstractTxt = "";
				if (r.containsKey("abstractText")){
					abstractTxt = r.getString("abstractText");
				}

				preparedStatement.setString(1, abstractTxt);
				preparedStatement.setInt(2, pmid);

				preparedStatement.executeUpdate();
			}
		}

		System.out.println("Done updating abstractTxt for " + counter + " PMIDs");
		connection.commit();
	}

	private void updatePublicationGrantMeshTerms() throws SQLException {

		PreparedStatement preparedStatement = connection.prepareStatement("UPDATE allele_ref SET date_of_publication = ?, mesh=?, grant_id=?, agency=?, acronym=?, meshtree=? WHERE pmid = ?");
		//PreparedStatement preparedStatement = connection.prepareStatement("UPDATE allele_ref SET date_of_publication = ? WHERE pmid = ?");

		PreparedStatement ps1 = connection.prepareStatement("SELECT pmid FROM allele_ref WHERE falsepositive = 'no' AND reviewed = 'yes'");

		TreeMap<String, List<String>> grantAgency = new TreeMap<>();

		int counter = 0;
		ResultSet resultSet = ps1.executeQuery();
		while (resultSet.next()) {
			Integer pmid = resultSet.getInt("pmid");

			String dbfetchUrl = "http://www.ebi.ac.uk/europepmc/webservices/rest/search/query=ext_id:" + pmid + "%20and%20src:MED&format=json&resulttype=core";
			System.out.println(dbfetchUrl);
			// Get the page and print it.
			JSONObject json = fetchHttpUrlJson(dbfetchUrl);

			//System.out.println("check json: "+ json);
			JSONArray results = json.getJSONObject("resultList").getJSONArray("result");

			for ( int j=0; j<results.size(); j++ ) {

				counter++;
				JSONObject r = results.getJSONObject(j);

				//System.out.println(r.toString());

				String dateOfPublication = null;
				if (r.containsKey("electronicPublicationDate")){
					dateOfPublication = r.getString("electronicPublicationDate");
				}
				else if (r.containsKey("firstPublicationDate")){
					dateOfPublication = r.getString("firstPublicationDate");
				}
				else if (r.containsKey("journalInfo") && r.getJSONObject("journalInfo").containsKey("printPublicationDate")) {
					dateOfPublication = r.getJSONObject("journalInfo").getString("printPublicationDate");
				}
				else if (r.containsKey("pubYear")){
					dateOfPublication = r.getString("pubYear") + "-00-00";
				}
				else {
					dateOfPublication = "";
				}

				// mesh terms
				List<String> meshTerms = new ArrayList<>();
				List<String> meshQfTerms = new ArrayList<>(); // mesh qualifers only (ie, sub terms, no major headings)
				JSONArray meshHeadings_modified = new JSONArray();

				if ( r.containsKey("meshHeadingList") ){
					JSONArray meshHeadings = r.getJSONObject("meshHeadingList").getJSONArray("meshHeading");

					for ( int mh=0; mh<meshHeadings.size(); mh++ ){
						JSONObject thisMeshHeading = (JSONObject) meshHeadings.get(mh);
						JSONObject thisMeshHeading_modified = new JSONObject();

						// mesh heading
						if ( thisMeshHeading.containsKey("descriptorName") ){
							String thisMeshTerm = thisMeshHeading.getString("descriptorName");
							meshTerms.add(thisMeshTerm);

							String topMeshTerm = child2TopMesh.get(thisMeshTerm);
							thisMeshHeading_modified.put("text", thisMeshTerm + "(<span class='topmesh'>" + topMeshTerm + "</span>)");

						}

						// mesh subheading
						//List<String> meshQs = new ArrayList<>();
						if ( thisMeshHeading.containsKey("meshQualifierList") ) {
							JSONArray meshQualifiers = thisMeshHeading.getJSONObject("meshQualifierList").getJSONArray("meshQualifier");
							JSONArray meshQualifiers_modified = new JSONArray();

							for (int mq = 0; mq < meshQualifiers.size(); mq++) {
								JSONObject thisMeshQualifier = (JSONObject) meshQualifiers.get(mq);
								if ( thisMeshQualifier.containsKey("qualifierName") ){
									String qf = thisMeshQualifier.getString("qualifierName");

									JSONObject thisMeshQualifier_modified = new JSONObject();
									thisMeshQualifier_modified.put("text", qf);
									meshQualifiers_modified.add(thisMeshQualifier_modified);

									if ( ! meshQfTerms.contains(qf)) {
										meshQfTerms.add(qf);
										meshTerms.add(qf);
									}
								}
							}
							thisMeshHeading_modified.put("children", meshQualifiers_modified);
						}

						meshHeadings_modified.add(thisMeshHeading_modified);
					}
				}

				List<String> grantIds = new ArrayList<>();
				List<String> grantAgencies = new ArrayList<>();
				List<String> grantAcronyms = new ArrayList<>();


				if ( r.containsKey("grantsList") ){
					JSONArray grants = r.getJSONObject("grantsList").getJSONArray("grant");
					for ( int k=0; k<grants.size(); k++ ){
						JSONObject thisG = (JSONObject) grants.get(k);
						Grant g = new Grant();
						//System.out.println(thisG.toString());

						if ( thisG.containsKey("grantId") ){
							g.id = thisG.getString("grantId").trim();
						}
						else {
							g.id = "";
						}
						grantIds.add(g.id);

						if ( thisG.containsKey("agency") ){
							g.agency = thisG.getString("agency").trim();
						}
						else {
							g.agency = "";
						}
						grantAgencies.add(g.agency);

						if ( thisG.containsKey("acronym") ){
							g.acronym = thisG.getString("acronym").trim();
						}
						else {
							g.acronym = "";
						}
						grantAcronyms.add(g.acronym);

						if (!g.id.isEmpty() && !g.agency.isEmpty()){
							if (!grantAgency.containsKey(g.agency)){
								grantAgency.put(g.agency, new ArrayList<String>());
							}
							grantAgency.get(g.agency).add(g.id);
						}

					}
				}


				preparedStatement.setString(1, dateOfPublication);
				preparedStatement.setString(2, StringUtils.join(meshTerms, "|||"));
				preparedStatement.setString(3, grantIds.size() > 0 ? StringUtils.join(grantIds, delimiter) : "");
				preparedStatement.setString(4, grantAgencies.size() > 0 ? StringUtils.join(grantAgencies, delimiter) : "");
				preparedStatement.setString(5, grantAcronyms.size() > 0 ? StringUtils.join(grantAcronyms, delimiter) : "");
				preparedStatement.setString(6, meshHeadings_modified.size() > 0 ? meshHeadings_modified.toString() : "");

				preparedStatement.setInt(7, pmid);
				preparedStatement.executeUpdate();
			}
		}

		System.out.println("Done updating date of publication and mesh terms for " + counter + " PMIDs");
		//connection.commit();

		for (Map.Entry<String, List<String>> entry : grantAgency.entrySet()) {
			for(String id : entry.getValue()) {
				System.out.println(entry.getKey() + "\t" + id);
			}

		}


	}
	private void loadReferenceDataToDatabase() throws SQLException {

		// check for reviewed pmids stored in our database
		List<Integer> reviewedPmidsInDb = new ArrayList<>();
		List<Integer> nonReviewedPmidsInDb = new ArrayList<>();

		PreparedStatement ps = connection.prepareStatement("SELECT pmid, reviewed FROM allele_ref");

		ResultSet resultSet = ps.executeQuery();
		while (resultSet.next()) {
			Integer thisPmid = resultSet.getInt("pmid");
			String reviewed = resultSet.getString("reviewed");
			if ( reviewed.equals("yes")) {
				reviewedPmidsInDb.add(thisPmid);
			}
			else {
				nonReviewedPmidsInDb.add(thisPmid);
			}
		}

		logger.info("EXISTING REVIEWED PMIDs IN OUR DATABASE: {}", reviewedPmidsInDb.size());
		logger.info("EXISTING NON-REVIEWED PMIDs IN OUR DATABASE: {}", nonReviewedPmidsInDb.size());


		// (1.gacc, 2.acc, 3.symbol, 4.name, 5.pmid, 6.date_of_publication,
		// 7.reviewed, 8.grant_id, 9.agency, 10.acronym, 11.title,
		// 12.journal, 13.paper_url, 14.datasource, 15.timestamp, 16.falsepositive, 17.mesh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)");
		// IGNORE takes care of existing ones don't get overriden

		// insert new mousemine pmid and ignore existing ones
		insertStatement = connection.prepareStatement("REPLACE INTO allele_ref "
			+ "(gacc, acc, symbol, name, pmid, date_of_publication, reviewed, grant_id, agency, acronym, title, journal, paper_url, datasource, timestamp, falsepositive, mesh, meshtree, author, abstract, cited_by) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		// update europubmed pmids with mousemine pmids
		updateStatement = connection.prepareStatement("UPDATE allele_ref "
				+ "SET gacc=?, acc=?, symbol=?, name=?, date_of_publication=?, reviewed=?, grant_id=?, agency=?, acronym=?, title=?, journal=?, paper_url=?, datasource=?, timestamp=?, falsepositive=?, mesh=?, meshtree=?, author=?, abstract=?, cited_by=?"
				+ " WHERE pmid=?");

		// papers with allele infos (from mousemine)
		Iterator it = pmid2Alleles.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry)it.next();
			Integer pmid = (Integer) pairs.getKey();
			Map<String, List<String>> alleleInfos = (Map<String, List<String>>)pairs.getValue();

			// update europubmed curation with mousemine data
			if ( nonReviewedPmidsInDb.contains(pmid) && alleleInfos.get("acc").size() > 0 ) {  // acc: allele must be in IMPC for automated process
				String src = "mousemine";
				updateEuropubmedPapers(pmid, alleleInfos, src);
				System.out.println("UPDATED europubmed via mousemine: " + pmid);
				updatedEuropubmedUsingMousemine.add(pmid);
			}
			else if (! reviewedPmidsInDb.contains(pmid) && alleleInfos.get("acc").size() > 0 ) {  // acc: allele must be in IMPC for automated process

			// insert new mousemine pmids
			//if ( ! reviewedPmidsInDb.contains(pmid) && ! updatedEuropubmedUsingMousemine.contains(pmid) ) {
				String src = "mousemine";
				saveMouseMinePapers(pmid, alleleInfos, src);
				System.out.println("NEW mousemine: " + pmid);
				newlyLoadedMouseMine.add(pmid);
			}

		}

		// these pmids found via "EUCOMM" or "KOMP" keywords
		// from Europubmed do not have alleles associated and need to be curated by hand
		for ( int pmidFromEuropubmedREST : newEuropubmedPmids ){
			if (! nonReviewedPmidsInDb.contains(pmidFromEuropubmedREST) && ! reviewedPmidsInDb.contains(pmidFromEuropubmedREST)) {
				saveNewEuropubmedPapers(pmidFromEuropubmedREST, "europubmed");
				newlyLoadedEuropubmed.add(pmidFromEuropubmedREST);
				System.out.println("NEW europubmed: " + pmidFromEuropubmedREST);
			}
		}

        logger.info("DONE RUNNING loadReferenceDataToDatabase");
        connection.commit();
	}

	private void saveNewEuropubmedPapers(int pmid, String datasource) throws SQLException{

		//"INSERT IGNORE INTO allele_ref
		// (1.gacc, 2.acc, 3.symbol, 4.name, 5.pmid, 6.date_of_publication,
		// 7.reviewed, 8.grant_id, 9.agency, 10.acronym, 11.title,
		// 12.journal, 13.paper_url, 14.datasource, 15.timestamp, 16.falsepositive, 17.mesh, 18.meshtree, 18.author, 19.abstract, 20 cited_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)");

		Pubmed pub = pubmed.get(pmid);

		// don't want these false positives
        //System.out.println("YEAR: " + pub.dateOfPublication.substring(0,4));
        if (Integer.parseInt(pub.dateOfPublication.substring(0,4)) > 2005) {


			try {
				insertStatement.setString(1, "");
				insertStatement.setString(2, "");
				insertStatement.setString(3, "");
				insertStatement.setString(4, "");
				insertStatement.setInt(5, pmid);
				insertStatement.setString(6, pub.dateOfPublication);
				insertStatement.setString(7, "no");

				// grant info: we can have multiple grants for a paper

				List<String> grantIds = new ArrayList<>();
				List<String> grantAgencies = new ArrayList<>();
				List<String> grantAcronyms = new ArrayList<>();

				for (int i = 0; i < pub.grants.size(); i++) {
					Grant g = pub.grants.get(i);
					if (!g.id.equals("")) {
						grantIds.add(g.id);
					}
					if (!g.agency.equals("")) {
						grantAgencies.add(g.agency);
					}
					if (!g.acronym.equals("")) {
						grantAcronyms.add(g.acronym);
					}
				}

				insertStatement.setString(8, grantIds.size() > 0 ? StringUtils.join(grantIds, delimiter) : "");
				insertStatement.setString(9, grantAgencies.size() > 0 ? StringUtils.join(grantAgencies, delimiter) : "");
				insertStatement.setString(10, grantAcronyms.size() > 0 ? StringUtils.join(grantAcronyms, delimiter) : "");

				insertStatement.setString(11, pub.title);
				insertStatement.setString(12, pub.journal);

				// we can have multiple links to a paper
				List<String> paper_urls = new ArrayList<>();
				for (int j = 0; j < pub.paperurls.size(); j++) {
					Paperurl p = (Paperurl) pub.paperurls.get(j);
					paper_urls.add(p.url);
				}
				insertStatement.setString(13, paper_urls.size() > 0 ? StringUtils.join(paper_urls, delimiter) : "");

				insertStatement.setString(14, datasource);
				insertStatement.setTimestamp(15, new Timestamp(System.currentTimeMillis()));
				insertStatement.setString(16, "no");

				// fetch mesh terms: heading pulus mesh heading+mesh qualifier
				List<String> mterms = new ArrayList<>();
				for (int k = 0; k < pub.meshTerms.size(); k++) {
					MeshTerm mt = pub.meshTerms.get(k);
					mterms.add(mt.meshHeading);
					for (String mq : mt.meshQualifiers) {
						mterms.add(mt.meshHeading + " " + mq);
					}
				}

				insertStatement.setString(17, mterms.size() > 0 ? StringUtils.join(mterms, delimiter) : "");
				insertStatement.setString(18, mterms.size() > 0 ? pub.meshJsonStr : "");
				insertStatement.setString(19, pub.author);
				insertStatement.setString(20, pub.abstractText);
				insertStatement.setString(21, pub.cited_by);

				insertStatement.executeUpdate();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private void updateEuropubmedPapers(Integer pmid, Map<String, List<String>> alleleInfos, String src) throws SQLException{

		Pubmed pub = pubmed.get(pmid);

//		updateStatement = connection.prepareStatement("UPDATE allele_ref "
//				+ "SET 1 gacc=?, 2 acc=?, 3 symbol=?, 4 name=?, 5 date_of_publication=?, 6 reviewed=?, 7 grant_id=?, 8 agency=?, 9acronym=?, 10 title=?, " +
//				"11 journal=?, 12 paper_url=?, 13 datasource=?, 14 timestamp=?, 15 falsepositive=?, 16 mesh=?, 17 meshtree=? 18 author=? 19 abstract=?
//				+ " WHERE 18 pmid=?");


		try {
			updateStatement.setString(1, StringUtils.join(alleleInfos.get("gacc"), delimiter));
			updateStatement.setString(2, StringUtils.join(alleleInfos.get("acc"), delimiter));
			updateStatement.setString(3, StringUtils.join(alleleInfos.get("symbol"), delimiter));
			updateStatement.setString(4, StringUtils.join(alleleInfos.get("name"), delimiter));

			updateStatement.setString(5, pub.dateOfPublication);
			updateStatement.setString(6, "yes");

			// grant info: we can have multiple grants for a paper

			List<String> grantIds = new ArrayList<>();
			List<String> grantAgencies = new ArrayList<>();
			List<String> grantAcronyms = new ArrayList<>();

			for (int i = 0; i < pub.grants.size(); i++) {
				Grant g = pub.grants.get(i);
				grantIds.add(g.id);
				grantAgencies.add(g.agency);
				grantAcronyms.add(g.acronym);
			}

			updateStatement.setString(7, grantIds.size() > 0 ? StringUtils.join(grantIds, delimiter) : "");
			updateStatement.setString(8, grantAgencies.size() > 0 ? StringUtils.join(grantAgencies, delimiter) : "");
			updateStatement.setString(9, grantAcronyms.size() > 0 ? StringUtils.join(grantAcronyms, delimiter) : "");

			updateStatement.setString(10, pub.title);
			updateStatement.setString(11, pub.journal);

			// we can have multiple links to a paper
			List<String> paper_urls = new ArrayList<>();
			for (int j = 0; j < pub.paperurls.size(); j++) {
				Paperurl p = (Paperurl) pub.paperurls.get(j);
				paper_urls.add(p.url);
			}
			updateStatement.setString(12, paper_urls.size() > 0 ? StringUtils.join(paper_urls, delimiter) : "");
			updateStatement.setString(13, src); // all papers having allele info come from MGI mousemine
			updateStatement.setTimestamp(14, new Timestamp(System.currentTimeMillis()));
			updateStatement.setString(15, "no"); // default

			// fetch mesh terms: heading pulus mesh heading+mesh qualifier
			List<String> mterms = new ArrayList<>();
			for (int k = 0; k < pub.meshTerms.size(); k++) {
				MeshTerm mt = pub.meshTerms.get(k);
				mterms.add(mt.meshHeading);
				for (String mq : mt.meshQualifiers) {
					mterms.add(mt.meshHeading + " " + mq);
				}
			}

			updateStatement.setString(16, mterms.size() > 0 ? StringUtils.join(mterms, delimiter) : "");
			updateStatement.setString(17, mterms.size() > 0 ? pub.meshJsonStr : "");
			updateStatement.setString(18, pub.author);
			updateStatement.setString(19, pub.abstractText);
			updateStatement.setString(20, pub.cited_by);
			updateStatement.setInt(21, pmid);

			updateStatement.executeUpdate();
		}
		catch(Exception e){
			System.out.println(e.getStackTrace());
		}
	}

	private void saveMouseMinePapers(Integer pmid, Map<String, List<String>> alleleInfos, String src) throws SQLException{

		Pubmed pub = pubmed.get(pmid);

		//"REPLACE INTO allele_ref
		// (1.gacc, 2.acc, 3.symbol, 4.name, 5.pmid, 6.date_of_publication,
		// 7.reviewed, 8.grant_id, 9.agency, 10.acronym, 11.title,
		// 12.journal, 13.paper_url, 14.datasource, 15.timestamp, 16.falsepositive, 17.mesh, 18.meshtree 19.author 20 abstract) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?)");


		try {
			insertStatement.setString(1, StringUtils.join(alleleInfos.get("gacc"), delimiter));
			insertStatement.setString(2, StringUtils.join(alleleInfos.get("acc"), delimiter));
			insertStatement.setString(3, StringUtils.join(alleleInfos.get("symbol"), delimiter));
			insertStatement.setString(4, StringUtils.join(alleleInfos.get("name"), delimiter));
			insertStatement.setInt(5, pmid);
			insertStatement.setString(6, pub.dateOfPublication);
			insertStatement.setString(7, "yes");

			// grant info: we can have multiple grants for a paper

			List<String> grantIds = new ArrayList<>();
			List<String> grantAgencies = new ArrayList<>();
			List<String> grantAcronyms = new ArrayList<>();

			for (int i = 0; i < pub.grants.size(); i++) {
				Grant g = pub.grants.get(i);
				grantIds.add(g.id);
				grantAgencies.add(g.agency);
				grantAcronyms.add(g.acronym);
			}

			insertStatement.setString(8, grantIds.size() > 0 ? StringUtils.join(grantIds, delimiter) : "");
			insertStatement.setString(9, grantAgencies.size() > 0 ? StringUtils.join(grantAgencies, delimiter) : "");
			insertStatement.setString(10, grantAcronyms.size() > 0 ? StringUtils.join(grantAcronyms, delimiter) : "");

			insertStatement.setString(11, pub.title);
			insertStatement.setString(12, pub.journal);

			// we can have multiple links to a paper
			List<String> paper_urls = new ArrayList<>();
			for (int j = 0; j < pub.paperurls.size(); j++) {
				Paperurl p = (Paperurl) pub.paperurls.get(j);
				paper_urls.add(p.url);
			}
			insertStatement.setString(13, paper_urls.size() > 0 ? StringUtils.join(paper_urls, delimiter) : "");
			insertStatement.setString(14, src); // all papers having allele info come from MGI mousemine
			insertStatement.setTimestamp(15, new Timestamp(System.currentTimeMillis()));
			insertStatement.setString(16, "no"); // default

			// fetch mesh terms: heading pulus mesh heading+mesh qualifier
			List<String> mterms = new ArrayList<>();
			for (int k = 0; k < pub.meshTerms.size(); k++) {
				MeshTerm mt = pub.meshTerms.get(k);
				mterms.add(mt.meshHeading);
				for (String mq : mt.meshQualifiers) {
					mterms.add(mt.meshHeading + " " + mq);
				}
			}
			insertStatement.setString(17, mterms.size() > 0 ? StringUtils.join(mterms, delimiter) : "");
			insertStatement.setString(18, mterms.size() > 0 ? pub.meshJsonStr : "");
			insertStatement.setString(19, pub.author);
			insertStatement.setString(20, pub.abstractText);
			insertStatement.setString(21, pub.cited_by);


			insertStatement.executeUpdate();
		}
		catch(Exception e){
			System.out.println(e.getStackTrace());
		}
	}

	public class Pubmed {
		String title;
		String journal;
		String dateOfPublication;
		String author;
		List<Grant> grants;
		List<Paperurl> paperurls;
		List<MeshTerm> meshTerms;
		String meshJsonStr;
		String abstractText;
		String cited_by;
	}
	public class Grant {
		public String id;
		public String acronym;
		public String agency;
	}
	public class Paperurl {
		public String url;
	}
	public class MeshTerm {
		public String meshHeading;
		public List<String> meshQualifiers; // same as mes subheading
	}

	private void fetchEuropePubmedData(Boolean doCitation){

		System.out.println("PARSING EUROPE PUBMED:");
		int currNum = pubmed.size();
		logger.info("Pubmed pmids currently in memory: {}", currNum);


		Set<Integer> newPmidFromCitation = new HashSet<>();
		List<String> filters = new ArrayList<>();

		if (! doCitation) {
			filters.add("EUCOMM");
			filters.add("KOMP");
			filters.addAll(mouseminePmidQrys);
		}
		else {
			filters.addAll(pmidsFromCitation);
		}

		// attach pubmed info to pmid
		for( int i=0; i<filters.size(); i++ ){
			//System.out.println("Filter: "+filters.get(i));
			String qry = filters.get(i);
			if (qry.startsWith("ext_id")){
				int id = Integer.parseInt(qry.replace("ext_id:",""));
				if (pubmed.containsKey(id)){
					continue;
				}
			}

			String dbfetchUrl = "http://www.ebi.ac.uk/europepmc/webservices/rest/search/query=" + qry + "%20and%20src:MED&format=json&resulttype=core";
			//System.out.println(dbfetchUrl);
			// Get the page and print it.
			JSONObject json = fetchHttpUrlJson(dbfetchUrl);

			//System.out.println("check json: "+ json);
			JSONArray results = json.getJSONObject("resultList").getJSONArray("result");

			for ( int j=0; j<results.size(); j++ ) {

				JSONObject r = results.getJSONObject(j);
				int pmidFound = r.getInt("pmid");

				if (! doCitation) {
					if (!mouseminePmids.contains(pmidFound)) {
						// keep the one obtained from europe pubmed query, ie, this id is not from mousemine
						newEuropubmedPmids.add(pmidFound);
					}
				}

				// populate pubmed hashmap
				if ( ! pubmed.containsKey(pmidFound) ){
					//System.out.println("Need to fetch Europubmed data for " + pmidFound);
					pubmed.put(pmidFound, new Pubmed());
				}

				Pubmed pub = pubmed.get(pmidFound);

				//System.out.println("dateOfPublication: " + r.getString("dateOfPublication"));

				if ( r.containsKey("title")  ){
					pub.title = r.getString("title");
				}
				else {
					pub.title = "";
				}


				if (r.containsKey("abstractText")){
					pub.abstractText = r.getString("abstractText");
				}
				else {
					pub.abstractText = "";
				}

				if (r.containsKey("firstPublicationDate")){
					pub.dateOfPublication = r.getString("firstPublicationDate");
				}
				else if (r.containsKey("electronicPublicationDate")){
					pub.dateOfPublication = r.getString("electronicPublicationDate");
				}
				else if (r.containsKey("journalInfo") && r.getJSONObject("journalInfo").containsKey("printPublicationDate")) {
					pub.dateOfPublication = r.getJSONObject("journalInfo").getString("printPublicationDate");
				}
				else if (r.containsKey("pubYear")){
					pub.dateOfPublication = r.getString("pubYear") + "-00-00";
				}
				else {
					pub.dateOfPublication = "";
				}


				if ( r.containsKey("journalInfo") && r.getJSONObject("journalInfo").containsKey("journal") ){
					String journal = r.getJSONObject("journalInfo").getJSONObject("journal").getString("title");
					pub.journal = journal;
				}
				else {
					pub.journal = "";
				}


				List<Grant> grantList = new ArrayList<>();

				if ( r.containsKey("grantsList") ){
					JSONArray grants = r.getJSONObject("grantsList").getJSONArray("grant");
					for ( int k=0; k<grants.size(); k++ ){
						JSONObject thisG = (JSONObject) grants.get(k);
						Grant g = new Grant();
						//System.out.println(thisG.toString());

						if ( thisG.containsKey("grantId") ){
							g.id = thisG.getString("grantId");
						}
						else {
							g.id = "";
						}

						if ( thisG.containsKey("agency") ){
							g.agency = thisG.getString("agency");
						}
						else {
							g.agency = "";
						}

						if ( thisG.containsKey("acronym") ){
							g.acronym = thisG.getString("acronym");
						}
						else {
							g.acronym = "";
						}

						grantList.add(g);

						//System.out.println(g.toString());
					}
				}
				pub.grants = grantList;

				List<Paperurl> paperurls = new ArrayList<>();

				if ( r.containsKey("fullTextUrlList") ){
					JSONArray textUrl = r.getJSONObject("fullTextUrlList").getJSONArray("fullTextUrl");
					for ( int l=0; l<textUrl.size(); l++ ){
						Paperurl p = new Paperurl();
						JSONObject thisT = (JSONObject) textUrl.get(l);
						if ( thisT.containsKey("url") ){
							p.url = thisT.getString("url");
							paperurls.add(p);
							//System.out.println("URL: "+ p.url);
						}
					}
				}

				pub.paperurls = paperurls;

				// mesh terms
				List<MeshTerm> meshTerms = new ArrayList<>();
				JSONArray meshHeadings_modified = new JSONArray();

				if ( r.containsKey("meshHeadingList") ){
					JSONArray meshHeadings = r.getJSONObject("meshHeadingList").getJSONArray("meshHeading");
					for ( int mh=0; mh<meshHeadings.size(); mh++ ){
						JSONObject thisMeshHeading = (JSONObject) meshHeadings.get(mh);
						JSONObject thisMeshHeading_modified = new JSONObject();

						MeshTerm mt  = new MeshTerm();
						//System.out.println(thisMeshHeading.toString());

						// mesh heading
						mt.meshHeading = "";
						if ( thisMeshHeading.containsKey("descriptorName") ){
							mt.meshHeading = thisMeshHeading.getString("descriptorName");

							String topMeshTerm = child2TopMesh.get(mt.meshHeading);
							thisMeshHeading_modified.put("text", mt.meshHeading + "(<span class='topmesh'>" + topMeshTerm + "</span>)");
						}

						// mesh subheading
						mt.meshQualifiers = new ArrayList<>();
						if ( thisMeshHeading.containsKey("meshQualifierList") ) {
							JSONArray meshQualifiers= thisMeshHeading.getJSONObject("meshQualifierList").getJSONArray("meshQualifier");
							JSONArray meshQualifiers_modified = new JSONArray();

							for (int mq = 0; mq < meshQualifiers.size(); mq++) {
								JSONObject thisMeshQualifier = (JSONObject) meshQualifiers.get(mq);
								if ( thisMeshQualifier.containsKey("qualifierName") ){
									String qf = thisMeshQualifier.getString("qualifierName");

									JSONObject thisMeshQualifier_modified = new JSONObject();
									thisMeshQualifier_modified.put("text", qf);
									meshQualifiers_modified.add(thisMeshQualifier_modified);

									if ( ! mt.meshQualifiers.contains(qf)) {
										mt.meshQualifiers.add(qf);
									}
								}
							}
							thisMeshHeading_modified.put("children", meshQualifiers_modified);
						}

						meshHeadings_modified.add(thisMeshHeading_modified);
						meshTerms.add(mt);

						//System.out.println(mt.toString());
					}
				}
				pub.meshTerms = meshTerms;
				pub.meshJsonStr = meshHeadings_modified.toString();

				if ( r.containsKey("authorString") ){
					pub.author = r.getString("authorString");
				}
				else {
                    pub.author = "";
                }

                pub.cited_by = ""; // default

				//System.out.println("Done fetching Europubmed data for " + pmidFound + " --> " + pub.dateOfPublication);

			}
		}

		logger.info("Populated {} Pubmed pmids", pubmed.size());
		if (doCitation){
			logger.info("Found {} new PMIDs CITING CONSORTIUM PAPERS\n", pubmed.size() - currNum);
		}
		else {
			logger.info("Found {} pmids NOT overlapping with MOUSEMINE\n", newEuropubmedPmids.size());
		}
	}

	private JSONObject fetchHttpUrlJson(String dbfetchUrl) {
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

	private void parseIntermine1() throws IOException{

		File file = new File(mousemine);
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;

	    fis = new FileInputStream(file);

	    // Here BufferedInputStream is added for fast reading.
	    bis = new BufferedInputStream(fis);
	    dis = new DataInputStream(bis);

	    List<String> rowData = new ArrayList();

	    // dis.available() returns 0 if the file does not have more lines.
		while (dis.available() != 0) {
			String line = dis.readLine();

			//System.out.println(line);
			if ( line.contains("Allele.symbol")){
				continue;
			}

			List<String> fd = Arrays.asList(line.split("\t"));
			if (fd.size() < 5){
				continue;
			}
			String alleleSymbol = fd.get(2);
			String alleleAcc = fd.get(1);
			String alleleName = fd.get(0);
			//System.out.println("Allele acc: " q+ alleleAcc);

			int pmid = Integer.parseInt(fd.get(3));

			//System.out.println(String.format("%s\t%s\t%s\t%d", alleleAcc, alleleSymbol, alleleName, pmid));

			mouseminePmids.add(pmid);
			mouseminePmidQrys.add("ext_id:"+pmid);

			// one pmid can have many alleles
			if ( ! pmid2Alleles.containsKey(pmid) ){
				pmid2Alleles.put(pmid, new HashMap<String, List<String>>());
			}

			Map<String, List<String>> alleleInfos = pmid2Alleles.get(pmid);
			if ( !alleleInfos.containsKey("acc") ) {
				alleleInfos.put("acc", new ArrayList<String>());
				alleleInfos.put("gacc", new ArrayList<String>());
			}
			if ( !alleleInfos.containsKey("symbol") ) {
				alleleInfos.put("symbol", new ArrayList<String>());
			}
			if ( !alleleInfos.containsKey("name") ) {
				alleleInfos.put("name", new ArrayList<String>());
			}

			// limit to only 20 alleles. Eg. pmid 27626380 has way too many
			if ( ! alleleInfos.get("acc").contains(alleleAcc) && alleleInfos.get("acc").size() < 20 ) {
				// don't want duplicate alleles: although allele name would still be different (ignore for now)
				if (allele2GeneMapping.get(alleleAcc) != null) {
					alleleInfos.get("gacc").add(allele2GeneMapping.get(alleleAcc));
					alleleInfos.get("acc").add(alleleAcc);
					alleleInfos.get("symbol").add(alleleSymbol);
					alleleInfos.get("name").add(alleleName);

//					System.out.println(alleleAcc + " -- " + allele2GeneMapping.get(alleleAcc).toString());
				}
			}

		}

		System.out.println("PARSING MOUSEMINE:");
		logger.info("Found " + mouseminePmids.size() + " unique pmids");

		System.out.println("");
	}

//	 private void parseIntermine(){
//
//		 /*String PROXY_HOST = "hx-wwwcache.ebi.ac.uk";
//		 int PROXY_PORT = 3128;
//		 HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
//         DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
//         CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build();
//
//         logger.info("Using Proxy Settings: " + PROXY_HOST + " on port: " + PROXY_PORT);
//         */
//		 ServiceFactory factory = new ServiceFactory(ROOT);
//		 Model model = factory.getModel();
//		 PathQuery query = new PathQuery(model);
//
//		 // Select the output columns:
//		 query.addViews("Allele.name",
//                "Allele.primaryIdentifier",
//                "Allele.projectCollection",
//                "Allele.symbol",
//                "Allele.publications.pubMedId");
//
//		 // Add orderby
//		 query.addOrderBy("Allele.name", OrderDirection.ASC);
//
//		 // Filter the results with the following constraints:
//		 query.addConstraint(Constraints.oneOfValues("Allele.projectCollection", Arrays.asList("EUCOMM")), "A");
//		 query.addConstraint(Constraints.oneOfValues("Allele.projectCollection", Arrays.asList("KOMP-CSD")), "B");
//		 query.addConstraint(Constraints.oneOfValues("Allele.projectCollection", Arrays.asList("KOMP-Regeneron")), "C");
//		 query.addConstraint(Constraints.neq("Allele.publications.pubMedId", "NULL"), "D");
//		 // Specify how these constraints should be combined.
//		 query.setConstraintLogic("(A or B or C) and D");
//
//		 QueryService service = factory.getQueryService();
//		 PrintStream out = System.out;
//		 String format = "%-17.17s | %-17.17s | %-17.17s | %-17.17s | %-17.17s\n";
//		 out.printf(format, query.getView().toArray());
//		 System.out.println("check query: " + query.getJson());
//		 Iterator<List<Object>> rows = service.getRowListIterator(query);
//		 while (rows.hasNext()) {
//			 out.printf(format, rows.next().toArray());
//		 }
//		 out.printf("%d rows\n", service.getCount(query));
//	 }

}
