package org.mousephenotype.cda.loads.legacy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MouseMineAlleleReferenceParser {

	private static ApplicationContext applicationContext;
	private static final String ROOT = "http://www.mousemine.org/mousemine/service";
	private static final Logger logger = LoggerFactory.getLogger(MouseMineAlleleReferenceParser.class);
	private static Connection connection;
	static final String CONTEXT_ARG = "context";
	 
	private static Map<Integer, Pubmed> pubmed = new HashMap<>();
	private static Map<String, Allele> alleles = new HashMap<>();
	private static Set<Integer> pmids = new HashSet<>();
	private static Set<Integer> extraPmids = new HashSet<>();
	private static Set<String> pmidQrys = new HashSet<>();
	private static Map<String, String> allele2Gene = new HashMap<>();
	
	@Autowired
	@Qualifier("admintoolsDataSource")
	//@Qualifier("admintoolsDataSourceLocal")
	private DataSource admintoolsDataSource;
	
	@Autowired
	@Qualifier("komp2DataSource")
	//@Qualifier("komp2DataSourceLocal")
	private DataSource komp2DataSource;
	
	
	private static PreparedStatement insertStatement;
	  
	public static void main(String[] args) throws IOException, SQLException {
		
		applicationContext = new ClassPathXmlApplicationContext("conf/jenkins/app-config.xml");

		// SPRING: auto-wire the instance with all its dependencies:
		MouseMineAlleleReferenceParser generate = new MouseMineAlleleReferenceParser();
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(generate, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

		generate.run();
		
		System.out.println("LOADING DATABASE:");
		logger.info("Total unique pmids added " + pmids.size());
		logger.info("Total Unique EUROPE PUBMED pmids added " + extraPmids.size());
		System.out.println("");
		logger.info("Job done");
		
		System.exit(0);
	}
	 
	public void run() throws SQLException, IOException{
		
		connection = admintoolsDataSource.getConnection();
		connection.setAutoCommit(false); // start transaction
		
		//parseIntermine();
		//createTable();
		parseIntermine1();
		fetchEuropePubmedData();
		alleleGeneAccMapping();
		loadReferenceData();
		
	}
	 
	public void alleleGeneAccMapping() throws SQLException {
		
		Connection komp2Connection = komp2DataSource.getConnection();
		String query = "select acc, gf_acc, symbol from allele";
		
		try (PreparedStatement p = komp2Connection.prepareStatement(query)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				// allele id to gene id mapping
				//System.out.println("allele: " + resultSet.getString("acc") + " => gene: " + resultSet.getString("gf_acc"));
				String acc = resultSet.getString("acc");
				if ( ! allele2Gene.containsKey(acc) ){
					allele2Gene.put(acc, resultSet.getString("gf_acc"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			komp2Connection.close();
			logger.info("ALLELE ID TO GENE ID MAPPING: ");
			logger.info("Populated " + allele2Gene.size() + " allele ids");
			System.out.println("");
		}
	}
	
	public void createTable() throws SQLException{
		 
		String[] statements = {
				"DROP TABLE IF EXISTS allele_ref",
				"CREATE TABLE allele_ref ("
			   +        "dbid int(12) UNSIGNED NOT NULL AUTO_INCREMENT,"
	           +     	"acc varchar(20) NOT NULL,"
	           +     	"gacc varchar(20) NOT NULL,"
	           +     	"symbol varchar(255) NOT NULL,"
	           +     	"name varchar (255) NOT NULL,"
	           +     	"pmid int(10) NOT NULL,"
	           +     	"date_of_publication varchar(30) NOT NULL,"
	           +     	"reviewed enum('yes', 'no') default 'no',"
	           +     	"grant_id varchar(50) NOT NULL,"
	           +     	"agency varchar(100) NOT NULL,"
	           +     	"acronym varchar(20) NOT NULL,"
	           +        "title text NOT NULL,"
	           +        "journal text NOT NULL,"
	           +		" datasource varchar(10) NOT NULL,"
	           +        "paper_url text NOT NULL,"
	           + 		"PRIMARY KEY (dbid) )",
	             "ALTER TABLE allele_ref add unique key (acc, pmid, agency)",
	             "ALTER TABLE allele_ref ADD INDEX (acc)",
	             "ALTER TABLE allele_ref ADD INDEX (pmid)",
	             "ALTER TABLE allele_ref ADD INDEX (grant_id)",
	             "ALTER TABLE allele_ref ADD INDEX (acc, pmid, reviewed)"
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
	 
	public void loadReferenceData() throws SQLException {
		  
		//alleleAcc, symbol, name, pmid, date_of_publicaton, reviewed, grant_id, agency, acronym, paper_url
		insertStatement = connection.prepareStatement("INSERT IGNORE INTO allele_ref (acc, symbol, name, pmid, date_of_publication, reviewed, grant_id, agency, acronym, title, journal, gacc, paper_url, datasource) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		
		// these alleles from mouse mine all have pmid
		Iterator it = alleles.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            String alleleAcc = (String) pairs.getKey();
            Allele al = (Allele) pairs.getValue();

            saveAlleleData(al);
        }
        
        // these pmids found via "EUCOMM" or "KOMP" do not have alleles associated and need to be curated by hand
        for ( int pmid : extraPmids ){
        	
        	// check if this pmid is already in admintools.allele_ref table
            String sqla = "SELECT count(*) as count FROM allele_ref WHERE pmid=" + pmid;
            int found = 0;
            try (PreparedStatement p = connection.prepareStatement(sqla)) {
    			
    			ResultSet resultSet = p.executeQuery();

    			while (resultSet.next()) {
    				found = resultSet.getInt("count");
    				if ( found == 0 ){
    					System.out.println("NEW pmid to add");
    					saveNonAlleleData(pmid);
    				}
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
        
        connection.commit();
	}
	//1acc, 2symbol, 3name, 4pmid, 5date_of_publication, 6reviewed, 7grant_id, 8agency, 9acronym, 10title, 11journal, 12paper_url
	public void saveNonAlleleData(int pmid) throws SQLException{
		
        Pubmed pub = pubmed.get(pmid);
        
        // we can have multiple grants for a paper
        if ( pub.grants.size() > 0 ){
        	for ( int i=0; i<pub.grants.size(); i++ ){
        		Grant g = pub.grants.get(i);
        		
        		//System.out.println("MULTI Grant Non allele: " + pmid);
        		
        		insertStatement.setString(1, "");
        		insertStatement.setString(2, "");
        		insertStatement.setString(3, "");
        		insertStatement.setInt(4, pmid);
        		insertStatement.setString(5, pub.dateOfPublication);
        		insertStatement.setString(6, "no");
        		insertStatement.setString(7, g.id);
        		insertStatement.setString(8, g.agency);
        		insertStatement.setString(9, g.acronym);
        		insertStatement.setString(10, pub.title);
        		insertStatement.setString(11, pub.journal);
        		insertStatement.setString(12, "");
        		
        		// we can have multiple links to a paper
        		// concat all paper urls into one column
        		if ( pub.paperurls.size() > 0 ){
        			List<String> paper_urls = new ArrayList<>();
        			
        			for ( int j=0; j<pub.paperurls.size(); j++){
        				Paperurl p = (Paperurl) pub.paperurls.get(j);
        				paper_urls.add(p.url);
        			}
        			insertStatement.setString(13, StringUtils.join(paper_urls, ","));
        		}
        		else {
        			insertStatement.setString(13, "");
        		}
        		
        		insertStatement.setString(14, "europubmed");
        		
        		insertStatement.executeUpdate();
        	}
        }
        else {
        	
        	insertStatement.setString(1, "");
    		insertStatement.setString(2, "");
    		insertStatement.setString(3, "");
    		insertStatement.setInt(4, pmid);
    		insertStatement.setString(5, pub.dateOfPublication);
    		insertStatement.setString(6, "no");
    		insertStatement.setString(7, "");
    		insertStatement.setString(8, "");
    		insertStatement.setString(9, "");
    		insertStatement.setString(10, pub.title);
    		insertStatement.setString(11, pub.journal);
    		insertStatement.setString(12, "");
    		
    		// we can have multiple links to a paper
    		// concat all paper urls into one column
    		if ( pub.paperurls.size() > 0 ){
    			List<String> paper_urls = new ArrayList<>();
    			
    			for ( int j=0; j<pub.paperurls.size(); j++){
    				Paperurl p = (Paperurl) pub.paperurls.get(j);
    				paper_urls.add(p.url);
    			}
    			insertStatement.setString(13, StringUtils.join(paper_urls, ","));
    		}
    		else {
    			insertStatement.setString(13, "");
    		}
    		
    		insertStatement.setString(14, "europubmed");
    		
    		insertStatement.executeUpdate();
        }
	}
	
	public void saveAlleleData(Allele al) throws SQLException{

		Pubmed pub = pubmed.get(al.pmid);
        
        // we can have multiple grants for a paper
        if ( pub.grants.size() > 0 ){
        	for ( int i=0; i<pub.grants.size(); i++ ){
        		Grant g = pub.grants.get(i);
        		
        		//System.out.println("MULTI Grant allele: " + al.pmid + " -- " + al.acc);
        		insertStatement.setString(1, al.acc);
        		insertStatement.setString(2, al.symbol);
        		insertStatement.setString(3, al.name);
        		insertStatement.setInt(4, al.pmid);
        		insertStatement.setString(5, pub.dateOfPublication);
        		insertStatement.setString(6, "yes");
        		//System.out.println("grant id: " + g.id);
        		insertStatement.setString(7, g.id);
        		insertStatement.setString(8, g.agency);
        		insertStatement.setString(9, g.acronym);
        		insertStatement.setString(10, pub.title);
        		insertStatement.setString(11, pub.journal);
        		insertStatement.setString(12, allele2Gene.get(al.acc));
        		//System.out.println("mapped "+ al.acc + " to " + allele2Gene.get(al.acc));
        		// we can have multiple links to a paper
        		// concat all paper urls into one column
        		if ( pub.paperurls.size() > 0 ){
        			List<String> paper_urls = new ArrayList<>();
        			
        			for ( int j=0; j<pub.paperurls.size(); j++){
        				Paperurl p = (Paperurl) pub.paperurls.get(j);
        				paper_urls.add(p.url);
        			}
        			insertStatement.setString(13, StringUtils.join(paper_urls, ","));
        		}
        		else {
        			insertStatement.setString(13, "");
        		}
        		
        		insertStatement.setString(14, "mousemine");
        		
        		insertStatement.executeUpdate();
        	}
        }
        else {
        	
        	insertStatement.setString(1, al.acc);
    		insertStatement.setString(2, al.symbol);
    		insertStatement.setString(3, al.name);
    		insertStatement.setInt(4, al.pmid);
    		insertStatement.setString(5, pub.dateOfPublication);
    		insertStatement.setString(6, "yes");
    		insertStatement.setString(7, "");
    		insertStatement.setString(8, "");
    		insertStatement.setString(9, "");
    		insertStatement.setString(10, pub.title);
    		insertStatement.setString(11, pub.journal);
    		insertStatement.setString(12, allele2Gene.get(al.acc));
    		
    		// we can have multiple links to a paper
    		// concat all paper urls into one column
    		if ( pub.paperurls.size() > 0 ){
    			List<String> paper_urls = new ArrayList<>();
    			
    			for ( int j=0; j<pub.paperurls.size(); j++){
    				Paperurl p = (Paperurl) pub.paperurls.get(j);
    				paper_urls.add(p.url);
    			}
    			insertStatement.setString(13, StringUtils.join(paper_urls, ","));
    		}
    		else {
    			insertStatement.setString(13, "");
    		}
    		
    		insertStatement.setString(14, "mousemine");
    		
    		insertStatement.executeUpdate();
        }
	}
	
	public class Pubmed {
		String title;
		String journal;
		String dateOfPublication;
		List<Grant> grants;
		List<Paperurl> paperurls;
		
	}
	public class Grant {
		public String id;
		public String acronym;
		public String agency;
	}
	public class Paperurl {
		public String url;
	}
	
	public class Allele {
		public String acc;
		public String symbol;
		public String name;
		public int pmid;
	}
	
	public void fetchEuropePubmedData(){
		
		// IMPC papers 
		List<String> impcPmids = new ArrayList<>();
		impcPmids.add(Integer.toString(21677750));
		impcPmids.add(Integer.toString(22211970));
		impcPmids.add(Integer.toString(25093073));
		impcPmids.add(Integer.toString(25343444));
		impcPmids.add(Integer.toString(24652767));
		impcPmids.add(Integer.toString(24197666));
		impcPmids.add(Integer.toString(24046361));
		impcPmids.add(Integer.toString(24194600));
		impcPmids.add(Integer.toString(23519032));
		impcPmids.add(Integer.toString(22968824));
		impcPmids.add(Integer.toString(22926223));
		impcPmids.add(Integer.toString(22566555));
		
		
		List<String> filters = new ArrayList<>();
		filters.add("eucomm");
		filters.add("komp");
		filters.addAll(impcPmids);
		filters.addAll(pmidQrys);
		
		for( int i=0; i<filters.size(); i++ ){
			//System.out.println("Working on filter: "+ filters.get(i));
			//String dbfetchUrl = "http://www.ebi.ac.uk/europepmc/webservices/rest/search/query=" + filters.get(i) + "%20and%20src:MED&format=json&resulttype=core";
			String dbfetchUrl = "http://www.ebi.ac.uk/europepmc/webservices/rest/search/query=" + filters.get(i) + "&format=json&resulttype=core";
			 
			// Get the page and print it.
			JSONObject json = fetchHttpUrlJson(dbfetchUrl);
			
			//System.out.println(json);
			JSONArray results = json.getJSONObject("resultList").getJSONArray("result");
			
			//test
			List<Integer> testInts = new ArrayList<>();
			testInts.add(25485098);
			testInts.add(25393878);
			testInts.add(25373905);
			testInts.add(25356849);
			testInts.add(25347065);
			testInts.add(25343444);
			testInts.add(25340345);
			testInts.add(25299188);
			testInts.add(25263220);
			testInts.add(25251243);
			testInts.add(25211221);
			testInts.add(25184786);
			testInts.add(25170954);
			testInts.add(25161872);
			
			for ( int j=0; j<results.size(); j++ ) {
				
				JSONObject r = results.getJSONObject(j);
				int pmid = r.getInt("pmid");
				
				if ( testInts.contains(pmid) ){
					System.out.println("MISSING " + pmid + " is found"  );
				}
				
				if ( ! pmids.contains(pmid) ){
					//System.out.println(pmid + " NOT in MOUSEMINE");
					// keep the one obtained from europe pubmed query, ie, this id is not from mousemine
					extraPmids.add(pmid);
				}
				
				if ( ! pubmed.containsKey(pmid) ){
					pubmed.put(pmid, new Pubmed());
				}
				
				Pubmed pub = pubmed.get(pmid);
				
				//System.out.println("dateOfPublication: " + r.getString("dateOfPublication"));
				
				if ( r.containsKey("title")  ){
					pub.title = r.getString("title");
				}
				else {
					pub.title = "";
				}
				
				
				if ( r.containsKey("journalInfo") && r.getJSONObject("journalInfo").containsKey("dateOfPublication") ){
					String dateOfPublication = r.getJSONObject("journalInfo").getString("dateOfPublication");
					//System.out.println("dateOfPublication: " + dateOfPublication);
					pub.dateOfPublication = dateOfPublication;
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
			}
		}
		
		System.out.println("PARSING EUROPE PUBMED:");
		logger.info("Found " + pubmed.size() + " pmids");
		logger.info("Found " + extraPmids.size() + " pmids NOT overlapping with MOUSEMINE");
		System.out.println("");
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
	
	public void parseIntermine1() throws IOException{
	
		File file = new File("/Users/ckc/Downloads/mousemine_allele_ref.tsv");
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
			if ( line.contains("Allele > Symbol")){
				continue;
			}
			//System.out.println(line);
			List<String> fd = Arrays.asList(line.split("\t"));
			String alleleSymbol = fd.get(0);
			String alleleAcc = fd.get(1);
			String alleleName = fd.get(2);
			
			//System.out.println("Allele acc: " + alleleAcc);
			
			int pmid = Integer.parseInt(fd.get(3));
			
			//System.out.println(String.format("%s\t%s\t%s\t%d", alleleAcc, alleleSymbol, alleleName, pmid)); 
			
			pmids.add(pmid);
			//pmidQrys.add("ext_id:"+pmid);
			pmidQrys.add(Integer.toString(pmid));
			
			if ( ! alleles.containsKey(alleleAcc) ){
				alleles.put(alleleAcc,  new Allele());
			}
			Allele al = alleles.get(alleleAcc);
			al.acc = alleleAcc;
			al.symbol = alleleSymbol;
			al.name = alleleName;
			al.pmid = pmid;
		}
		
		System.out.println("PARSING MOUSEMINE:");
		logger.info("Populated " + alleles.size() + " alleles refs");
		logger.info("Collected " + pmids.size() + " unique pmids");
		System.out.println("");
		
	}
	
	 public void parseIntermine(){
		 
		 /*String PROXY_HOST = "hx-wwwcache.ebi.ac.uk";
		 int PROXY_PORT = 3128;
		 HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
         DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
         CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build();

         logger.info("Using Proxy Settings: " + PROXY_HOST + " on port: " + PROXY_PORT);
         */
		 ServiceFactory factory = new ServiceFactory(ROOT);
		 Model model = factory.getModel();
		 PathQuery query = new PathQuery(model);
		 
		 // Select the output columns:
		 query.addViews("Allele.name",
                "Allele.primaryIdentifier",
                "Allele.projectCollection",
                "Allele.symbol",
                "Allele.publications.pubMedId");

		 // Add orderby
		 query.addOrderBy("Allele.name", OrderDirection.ASC);

		 // Filter the results with the following constraints:
		 query.addConstraint(Constraints.oneOfValues("Allele.projectCollection", Arrays.asList("EUCOMM")), "A");
		 query.addConstraint(Constraints.oneOfValues("Allele.projectCollection", Arrays.asList("KOMP-CSD")), "B");
		 query.addConstraint(Constraints.oneOfValues("Allele.projectCollection", Arrays.asList("KOMP-Regeneron")), "C");
		 query.addConstraint(Constraints.neq("Allele.publications.pubMedId", "NULL"), "D");
		 // Specify how these constraints should be combined.
		 query.setConstraintLogic("(A or B or C) and D");

		 QueryService service = factory.getQueryService();
		 PrintStream out = System.out;
		 String format = "%-17.17s | %-17.17s | %-17.17s | %-17.17s | %-17.17s\n";
		 out.printf(format, query.getView().toArray());
		 System.out.println("check query: " + query.getJson());
		 Iterator<List<Object>> rows = service.getRowListIterator(query);
		 while (rows.hasNext()) {
			 out.printf(format, rows.next().toArray());
		 }
		 out.printf("%d rows\n", service.getCount(query));
	 }

	
}
