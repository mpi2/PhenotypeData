package org.mousephenotype.cda.loads;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
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
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
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

import org.mousephenotype.cda.loads.MouseMineAlleleReferenceParser.Allele;

public class GwasImpcMappingImporter {

	private static ApplicationContext applicationContext;
	private static final Logger logger = LoggerFactory.getLogger(GwasImpcMappingImporter.class);
	private static Connection connection;
	static final String CONTEXT_ARG = "context";
	 
//	private static Map<Integer, Pubmed> pubmed = new HashMap<>();
//	private static Map<String, Allele> alleles = new HashMap<>();
//	private static Set<Integer> pmids = new HashSet<>();
//	private static Set<Integer> extraPmids = new HashSet<>();
//	private static Set<String> pmidQrys = new HashSet<>();
//	private static Map<String, String> allele2Gene = new HashMap<>();
	
	@Autowired
	@Qualifier("admintoolsDataSource")
	//@Qualifier("admintoolsDataSourceLocal")
	private DataSource admintoolsDataSource;
	
	@Autowired
	@Qualifier("komp2DataSource")
	//@Qualifier("komp2DataSourceLocal")
	private DataSource komp2DataSource;
	
	private static Map<String, String> allele2Gene = new HashMap<>();
	private static Map<String, String> symbol2Gene = new HashMap<>();
	private static Map<String, String> allele2Name = new HashMap<>();
	
	private static PreparedStatement insertStatement;
	  
	public static void main(String[] args) throws IOException, SQLException {
		
		applicationContext = new ClassPathXmlApplicationContext("conf/jenkins/app-config.xml");

		// SPRING: auto-wire the instance with all its dependencies:
		GwasImpcMappingImporter generate = new GwasImpcMappingImporter();
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(generate, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

		generate.run();
		
		System.out.println("");
		logger.info("Job done");
		
		System.exit(0);
	}
	 
	public void run() throws SQLException, IOException{
		
		connection = admintoolsDataSource.getConnection();
		connection.setAutoCommit(false); // start transaction
		
		createGwasAnnotationTable();
		alleleGeneAccMapping();
		parseGwasPhenotypeMapping();
		
	}
	
	public void createGwasAnnotationTable() throws SQLException{
		 
		String[] statements = {
				"DROP TABLE IF EXISTS impc2gwas",
				"CREATE TABLE impc2gwas ("
			   +	    "mgi_gene_id varchar(20) NOT NULL,"
			   +     	"mgi_gene_symbol varchar(255) NOT NULL,"
			   +	    "mgi_allele_id varchar(20) NOT NULL,"
			   +        "mgi_allele_name varchar(255) NOT NULL,"
			   +		"pheno_mapping_category varchar(25),"
			   +        "gwas_disease_trait varchar(255),"
			   +        "gwas_p_value float(2),"
			   +        "gwas_reported_gene varchar(255) NOT NULL,"
			   +        "gwas_mapped_gene varchar(255) NOT NULL,"
			   +        "gwas_upstream_gene varchar(255) NOT NULL,"
			   +        "gwas_downstream_gene varchar(255) NOT NULL,"
			   +        "mp_term_id varchar(255) NOT NULL,"
			   +        "mp_term_name varchar(255) NOT NULL,"
			   +        "impc_mouse_gender enum('male', 'female', 'both') default NULL,"
			   +		"gwas_snp_id varchar(255) NOT NULL)",
	           "ALTER TABLE impc2gwas ADD UNIQUE KEY (mgi_gene_id, "
			   + "mgi_allele_id, "
	           + "pheno_mapping_category, "
			   + "gwas_disease_trait, "
	           + "gwas_p_value, "
			   + "gwas_reported_gene, "
	           + "gwas_mapped_gene, "
			   + "gwas_upstream_gene, "
	           + "gwas_downstream_gene, "
			   + "mp_term_id, "
	           + "impc_mouse_gender, "
	           + "gwas_snp_id)",
	           "ALTER TABLE impc2gwas ADD INDEX (mgi_gene_id)"
		};

		Statement currentStatement = null;
		
		for (String stmt: statements) {
			System.out.println("statement: "+ stmt);
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
	 
	public void alleleGeneAccMapping() throws SQLException {
		
		Connection komp2Connection = komp2DataSource.getConnection();
		String query = "select a.acc as allele_acc, a.name as allele_name, a.gf_acc as gene_acc, gf.symbol as gene_symbol from allele a, genomic_feature gf where a.gf_acc=gf.acc";
		
		try (PreparedStatement p = komp2Connection.prepareStatement(query)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				// allele id to gene id mapping
				//System.out.println("allele: " + resultSet.getString("acc") + " => gene: " + resultSet.getString("gf_acc"));
				String allele_acc = resultSet.getString("allele_acc");
				String gene_symbol = resultSet.getString("gene_symbol").toUpperCase();  // GWAS gene symbol is all capitalized
				
				if ( ! allele2Gene.containsKey(allele_acc) ){
					allele2Gene.put(allele_acc, resultSet.getString("gene_acc"));
					allele2Name.put(allele_acc, resultSet.getString("allele_name"));
				}
				if ( ! symbol2Gene.containsKey(gene_symbol) ){
					//System.out.println(gene_symbol + " ---- " + resultSet.getString("gene_acc"));
					symbol2Gene.put(gene_symbol, resultSet.getString("gene_acc"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			komp2Connection.close();
			logger.info("ALLELE ID TO GENE ID MAPPING: ");
			logger.info("Mapped " + allele2Gene.size() + " allele ids");
			logger.info("Mapped " + allele2Name.size() + " allele names");
			logger.info("Mapped " + symbol2Gene.size() + " symbols");
			
			System.out.println("");
		}
	}
	public void parseGwasPhenotypeMapping() throws IOException, SQLException{
		
		File file = new File("/Users/ckc/Documents/work/impc/gwas/impc2gwas.tsv");
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;

	    Set<String> symbols = new HashSet<>();
	    
	    fis = new FileInputStream(file);

	    // Here BufferedInputStream is added for fast reading.
	    bis = new BufferedInputStream(fis);
	    dis = new DataInputStream(bis);

	    List<String> rowData = new ArrayList();
	 	 	
	    System.out.println("PARSING IMPC PHENOTYPE TO GWAS DISEASE TRAIT MAPPING:");

	    insertStatement = connection.prepareStatement("INSERT IGNORE INTO impc2gwas (mgi_gene_id, mgi_gene_symbol, mgi_allele_id, mgi_allele_name, pheno_mapping_category, gwas_disease_trait, gwas_p_value, gwas_reported_gene, gwas_mapped_gene, gwas_upstream_gene, gwas_downstream_gene, mp_term_id, mp_term_name, impc_mouse_gender, gwas_snp_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		
	    // dis.available() returns 0 if the file does not have more lines.
		while (dis.available() != 0) {
			String line = dis.readLine();
			if ( line.contains("IMPC_GENE")){
				continue;
			}
			//System.out.println(line);
			List<String> fd = Arrays.asList(line.split("\t"));
			
			String mgi_allele_id = fd.get(14);
			String mgi_allele_name = allele2Name.get(mgi_allele_id); 
			String mgi_gene_id = allele2Gene.containsKey(mgi_allele_id) ? allele2Gene.get(mgi_allele_id) : "";
			String mgi_gene_symbol = fd.get(0).toUpperCase().trim();
			
			symbols.add(mgi_gene_symbol.toUpperCase());
			
			//System.out.println("mgi_gene_id: "+ mgi_gene_id);
			
			insertStatement.setString(1, mgi_gene_id);
			insertStatement.setString(2, mgi_gene_symbol);
			insertStatement.setString(3, mgi_allele_id);
			insertStatement.setString(4, mgi_allele_name);
			
			String impc_to_gwas_phenotype_mapping_category = fd.get(13);
			insertStatement.setString(5, impc_to_gwas_phenotype_mapping_category);
			
			String gwas_disease_trait = fd.get(2).replaceAll("\"", "");
    		insertStatement.setString(6, gwas_disease_trait);
			
    		int gwas_p_value_mantissa = Integer.parseInt(fd.get(3));
    		float gwas_p_value_exp = Float.parseFloat(fd.get(4));
    		String sciNote = (gwas_p_value_mantissa + "E" + gwas_p_value_exp).replace(".0","");
    		BigDecimal bd = new BigDecimal(sciNote);
    		float pVal = bd.floatValue();
			insertStatement.setFloat(7, pVal);
			
			String gwas_reported_gene = fd.get(9).trim();
			insertStatement.setString(8, gwas_reported_gene);
			
			String gwas_mapped_gene = fd.get(10).trim();
			insertStatement.setString(9, gwas_mapped_gene);
			
			String gwas_upstream_gene = fd.get(11).trim();
			insertStatement.setString(10, gwas_upstream_gene);
			
			String gwas_downstream_gene = fd.get(12).trim();
			insertStatement.setString(11, gwas_downstream_gene);
			
			String mp_term_id = fd.get(17);
			insertStatement.setString(12, mp_term_id);
			
			String mp_term_name = fd.get(16).replaceAll("\"","");
			insertStatement.setString(13, mp_term_name);
			
			String impc_mouse_gender = fd.get(15);
			insertStatement.setString(14, impc_mouse_gender);
			
			String gwas_snp_id = fd.get(5);
			insertStatement.setString(15, gwas_snp_id);
			
			insertStatement.executeUpdate();
			
			//System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%d", mgi_gene_symbol, impc_mouse_gender, gwas_disease_trait, mgi_gene_id, mgi_allele_id, impc_to_gwas_phenotype_mapping_category, mp_term_id, gwas_p_value)); 
		}
		
		connection.commit();
		System.out.println("FINISHED LOADING IMPC PHENOTYPE TO GWAS DISEASE TRAIT MAPPING TO DATABASE");		
		System.out.println("");
		
		//createGwasGeneTable();
		parseGwasCatalog(symbols, insertStatement);
	}
	
	public void parseGwasCatalog(Set<String> symbols, PreparedStatement insertStatement) throws IOException, SQLException{
		
		File file = new File("/Users/ckc/Documents/work/impc/gwas/gwas_catalog_v1.0-downloaded_2015-05-25.tsv");
	    FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    DataInputStream dis = null;
		   
	    fis = new FileInputStream(file);

	    // Here BufferedInputStream is added for fast reading.
	    bis = new BufferedInputStream(fis);
	    dis = new DataInputStream(bis);

	    List<String> rowData = new ArrayList();
	 	 	
	    System.out.println("PARSING GWAS CATALOG GENES:");
		
	    String pattern = "\\s*-\\s*"; // zero or more space
	   
	    // dis.available() returns 0 if the file does not have more lines.
		while (dis.available() != 0) {
			String line = dis.readLine();
			if ( line.contains("DATE ADDED")){
				continue;
			}
			//System.out.println(line);
			List<String> fd = Arrays.asList(line.split("\t"));
			
			String mapped_gene = fd.get(14);
				
			if ( ! mapped_gene.isEmpty() ){
				
				String[] gwasSymbols = mapped_gene.split(pattern);
				for ( int i=0; i<gwasSymbols.length; i++){
					String gwasSymbol = gwasSymbols[i].trim();
					
					if ( !symbols.contains(gwasSymbol) ){

						if ( gwasSymbol.startsWith(" STXB") ){
							System.out.println("FOUND " + gwasSymbol);
						}
						
						String mgi_gene_id = symbol2Gene.get(gwasSymbol);
						//System.out.println("mapped gene: "+ gwasSymbol + " --> " +  mgi_gene_id);
						insertStatement.setString(1, mgi_gene_id);
						insertStatement.setString(2, gwasSymbol);
						insertStatement.setString(3, "");
						insertStatement.setString(4, "");
						insertStatement.setString(5, "no mapping");
						insertStatement.setString(6, "");
						insertStatement.setFloat(7, 0);
						insertStatement.setString(8, "");
						insertStatement.setString(9, "");
						insertStatement.setString(10, "");
						insertStatement.setString(11, "");
						insertStatement.setString(12, "");
						insertStatement.setString(13, "");
						insertStatement.setString(14, "");
						insertStatement.setString(15, "");
						
						insertStatement.executeUpdate();
						
					}
				}
			}
		}
		
		connection.commit();
		System.out.println("FINISHED LOADING GWAS CATALOG GENES TO DATABASE");		
		System.out.println("");
	
}
	
}
