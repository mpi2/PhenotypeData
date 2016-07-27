package org.mousephenotype.cda.loads.legacy;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
	private static Map<String, String> trait2EfoId = new HashMap<>();
	
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
		assignEfoIdToDiseaseTrait();
		alleleGeneAccMapping();
		parseGwasPhenotypeMapping();
		
	}
	
	public void assignEfoIdToDiseaseTrait(){
		
		String eboBaseUrl = "http://www.ebi.ac.uk/efo/";
		
		trait2EfoId.put("Aging (facial)", "http://purl.obolibrary.org/obo/GO_0007568");
		trait2EfoId.put("Antibody status in Tripanosoma cruzi seropositivity", eboBaseUrl + "EFO_0004556");  
		
		trait2EfoId.put("Behavioural disinhibition (generation interaction)", eboBaseUrl + "EFO_0005430");
		trait2EfoId.put("Bilirubin levels", eboBaseUrl + "EFO_0004570");
		trait2EfoId.put("Body mass index", eboBaseUrl + "EFO_0004340");
		trait2EfoId.put("Bone mineral density", eboBaseUrl + "EFO_0003923");
		trait2EfoId.put("Bone mineral density (paediatric, lower limb)", eboBaseUrl + "EFO_0003923");
		trait2EfoId.put("Bone mineral density (paediatric, total body less head)", eboBaseUrl + "EFO_0003923");
		
		trait2EfoId.put("Corneal structure", eboBaseUrl + "EFO_0004345");
		trait2EfoId.put("Coronary heart disease", eboBaseUrl + "EFO_0001645");
		trait2EfoId.put("Crohn's disease", eboBaseUrl + "EFO_0000384");
		                   
		trait2EfoId.put("Digit length ratio", eboBaseUrl + "EFO_0004841");
		
		trait2EfoId.put("Electrocardiographic traits", eboBaseUrl + "EFO_0004682");
		
		trait2EfoId.put("HDL cholesterol", eboBaseUrl + "EFO_0004612");
		trait2EfoId.put("Height", eboBaseUrl + "EFO_0004339");
		trait2EfoId.put("Hematological parameters", eboBaseUrl + "EFO_0004526");
		trait2EfoId.put("Hypertriglyceridemia", eboBaseUrl + "EFO_0004530");
		
		trait2EfoId.put("Immune response to smallpox vaccine (IL-6)", eboBaseUrl + "EFO_0004645");
		trait2EfoId.put("Inflammatory bowel disease", eboBaseUrl + "EFO_0003767");
		
		trait2EfoId.put("Longevity", eboBaseUrl + "EFO_0004300");
		
		trait2EfoId.put("Major depressive disorder", eboBaseUrl + "EFO_0003761");
		
		trait2EfoId.put("Obesity-related traits", eboBaseUrl + "EFO_0004627");
		
		trait2EfoId.put("Plasma homocysteine levels (post-methionine load test)", eboBaseUrl + "EFO_0004578");
		trait2EfoId.put("Platelet counts", eboBaseUrl + "EFO_0004309");
		trait2EfoId.put("Primary biliary cirrhosis", eboBaseUrl + "EFO_0004267");
		
		trait2EfoId.put("QT interval", eboBaseUrl + "EFO_0004682");
		
		trait2EfoId.put("Red blood cell traits", eboBaseUrl + "EFO_0004527");
		trait2EfoId.put("Rheumatoid arthritis", eboBaseUrl + "EFO_0000685");
		
		trait2EfoId.put("Schizophrenia", eboBaseUrl + "EFO_0000692");
		trait2EfoId.put("Serum dimethylarginine levels (asymmetric/symetric ratio)", eboBaseUrl + "EFO_0005418");
		trait2EfoId.put("Smoking quantity", eboBaseUrl + "EFO_0005671"); 
		
		trait2EfoId.put("Type 1 diabetes nephropathy", eboBaseUrl + "EFO_0004996");
		
		trait2EfoId.put("Waist circumference", eboBaseUrl + "EFO_0004342");
		
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
			   +        "gwas_disease_trait_id_url varchar(255),"
			   +        "gwas_p_value float(2),"
			   +        "gwas_reported_gene varchar(255) NOT NULL,"
			   +        "gwas_mapped_gene varchar(255) NOT NULL,"
			   +        "gwas_upstream_gene varchar(255) NOT NULL,"
			   +        "gwas_downstream_gene varchar(255) NOT NULL,"
			   +        "mp_term_id varchar(255) NOT NULL,"
			   +        "mp_term_id_url varchar(255) NOT NULL,"
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

	    insertStatement = connection.prepareStatement("INSERT IGNORE INTO impc2gwas (mgi_gene_id, mgi_gene_symbol, mgi_allele_id, mgi_allele_name, pheno_mapping_category, gwas_disease_trait, gwas_disease_trait_id_url, gwas_p_value, gwas_reported_gene, gwas_mapped_gene, gwas_upstream_gene, gwas_downstream_gene, mp_term_id, mp_term_id_url, mp_term_name, impc_mouse_gender, gwas_snp_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		
	    String mpTermIdOwlBaseUrl = "http://purl.obolibrary.org/obo/";
	    
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
			
    		// hash for gwas trait to EFO id
    		insertStatement.setString(7, trait2EfoId.containsKey(gwas_disease_trait) ? trait2EfoId.get(gwas_disease_trait) : "");
			
    		int gwas_p_value_mantissa = Integer.parseInt(fd.get(3));
    		float gwas_p_value_exp = Float.parseFloat(fd.get(4));
    		String sciNote = (gwas_p_value_mantissa + "E" + gwas_p_value_exp).replace(".0","");
    		BigDecimal bd = new BigDecimal(sciNote);
    		float pVal = bd.floatValue();
			insertStatement.setFloat(8, pVal);
			
			String gwas_reported_gene = fd.get(9).trim();
			insertStatement.setString(9, gwas_reported_gene);
			
			String gwas_mapped_gene = fd.get(10).trim();
			insertStatement.setString(10, gwas_mapped_gene);
			
			String gwas_upstream_gene = fd.get(11).trim();
			insertStatement.setString(11, gwas_upstream_gene);
			
			String gwas_downstream_gene = fd.get(12).trim();
			insertStatement.setString(12, gwas_downstream_gene);
			
			String mp_term_id = fd.get(17);
			insertStatement.setString(13, mp_term_id);
			
			String mpTermIdEfo = mp_term_id.replace(":",  "_");
			insertStatement.setString(14, mpTermIdOwlBaseUrl + mpTermIdEfo);
			
			String mp_term_name = fd.get(16).replaceAll("\"","");
			insertStatement.setString(15, mp_term_name);
			
			String impc_mouse_gender = fd.get(15);
			insertStatement.setString(16, impc_mouse_gender);
			
			String gwas_snp_id = fd.get(5);
			insertStatement.setString(17, gwas_snp_id);
			
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
						insertStatement.setString(16, "");
						insertStatement.setString(17, "");
						
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
