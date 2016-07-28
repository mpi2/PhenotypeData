package org.mousephenotype.cda.loads.legacy.dccimport;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.mousephenotype.cda.db.dao.*;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.StageUnitType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.loads.legacy.dccimport.imits.FileBasedImitsClient;
import org.mousephenotype.cda.loads.legacy.dccimport.imits.ImitsClient;
import org.mousephenotype.cda.loads.legacy.dccimport.imits.ImitsRestClient;
import org.mousephenotype.cda.solr.imits.EncodedOrganisationConversionMap;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.mousephenotype.dcc.exportlibrary.xmlserialization.exceptions.XMLloadingException;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mousephenotype.cda.db.utilities.SqlUtils.setSqlParameter;

/**
 * Load specimen data that was encoded using the IMPC XML format
 */
//@Component
public class ImpcXmlFormatSpecimenLoader {

	// Required by the Harwell DCC export utilities
	public static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";
	private static final Logger logger = LoggerFactory.getLogger(ImpcXmlFormatSpecimenLoader.class);
	private static final String analyticsQuery = "INSERT INTO analytics_specimen_load (filename, colony_id, dob, baseline, strain, specimen_id, sex, zygosity, litter_id, impress_pipeline, production_center, phenotyping_center, project, mapped_project, status, message, additional_information) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String alleleInsert = "INSERT INTO allele(acc, symbol, name, gf_acc, db_id, gf_db_id, biotype_acc, biotype_db_id) VALUES (?, ?, ?, ?, 3, 3, 'CV:00000013', 3)";


	@Autowired
	org.mousephenotype.cda.db.impress.Utilities impressUtilities;
	private String filename;
	private String datasourceName;
	private Datasource datasource;
	private Connection connection;
	private ImitsClient imits;
	private ImitsRestClient imitsRest;
	private Map<DatasourceEntityId, LiveSample> liveSamples = new HashMap<>();
	private Map<BiologicalModelKey, BiologicalModel> biologicalModels = new HashMap<>();
	private Map<String, String> geneticBackgrounds = new HashMap<>();
	private Map<String, OntologyTerm> efoTerms = new HashMap<>();
	private Map<String, GenomicFeature> colonyIdToGene = new HashMap<>();
	private Map<String, Allele> colonyIdToAllele = new HashMap<>();
	private Set<String> esCellNotFound = new HashSet<>();
	private Set<String> alleleNotFound = new HashSet<>();
	private Map<String, Set<String>> colonyNotFound = new HashMap<>();
	private Map<String, Set<String>> sexNotFound = new HashMap<>();
	private EscellToGeneMap escellMap;
	@Autowired
	private DatasourceDAO dsDAO;
	@Autowired
	private OrganisationDAO orgDAO;
	@Autowired
	private StrainDAO strainDAO;
	@Autowired
	private GenomicFeatureDAO genomicFeatureDAO;
	@Autowired
	private AlleleDAO alleleDAO;
	@Autowired
	private OntologyTermDAO ontologyTermDAO;
	@Autowired
	private BiologicalModelDAO biologicalModelDAO;
	@Autowired
	private EncodedOrganisationConversionMap dccMapping;
	@Autowired
	@Qualifier("komp2DataSource")
	private DataSource ds;
	@Resource
	private List<String> COLONIES_TO_SKIP;


	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, XMLloadingException, KeyManagementException, SQLException, JAXBException {

		// Wire up spring support for this application
		ImpcXmlFormatSpecimenLoader main = new ImpcXmlFormatSpecimenLoader();
		main.initialize(args);
		main.run();

		logger.info("Process finished.  Exiting.");

	}

	private void initialize(String[] args)
		throws IOException, SQLException, KeyManagementException, NoSuchAlgorithmException {

		OptionParser parser = new OptionParser();

		// parameter to indicate the name of the file to process
		parser.accepts("filename").withRequiredArg().ofType(String.class);

		// parameter to indicate the short name of the datasource
		parser.accepts("datasource").withRequiredArg().ofType(String.class);

		// parameter to indicate which spring context file to use
		parser.accepts("context").withRequiredArg().ofType(String.class);

		// parameter to indicate the name of the file to process
		parser.accepts("imits-report").withOptionalArg().ofType(String.class);

		OptionSet options = parser.parse(args);
		filename = (String) options.valuesOf("filename").get(0);
		logger.info("Loading specimens file {}", filename);

		datasourceName = (String) options.valuesOf("datasource").get(0);
		logger.info("Associating with datasource {}", datasourceName);

		if (options.hasArgument("imits-report")) {
			logger.info("Using imits-report {} for gene and allele lookups", datasourceName);
			logger.info("Initializing file based iMits client");
			imits = new FileBasedImitsClient((String) options.valueOf("imits-report"));
		} else {
			logger.info("Initializing HTTP based iMits client");
			imits = new ImitsRestClient();
		}

		imitsRest = new ImitsRestClient();

		// Wire up spring support for this application
		ApplicationContext applicationContext;
		String context = (String) options.valuesOf("context").get(0);
		logger.info("Using application context file {}", context);
		if (new File(context).exists()) {
			applicationContext = new FileSystemXmlApplicationContext("file:" + context);
		} else {
			applicationContext = new ClassPathXmlApplicationContext(context);
		}

		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
		DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
		transactionAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
		transactionManager.getTransaction(transactionAttribute);

		logger.info("Loading ES cell to gene map");
		escellMap = new EscellToGeneMap(genomicFeatureDAO, alleleDAO, applicationContext);

		connection = ds.getConnection();

	}


	private void run() throws JAXBException, XMLloadingException, IOException, SQLException, KeyManagementException, NoSuchAlgorithmException {

		logger.info("Populating EFO ontology map terms");
		Integer efoDBID = dsDAO.getDatasourceByShortName("EFO").getId();

		efoTerms.put("whole organism", ontologyTermDAO.getOntologyTermByNameAndDatabaseId("whole organism", efoDBID));
		efoTerms.put("embryo stage", ontologyTermDAO.getOntologyTermByAccession("EFO:0001367"));

		efoTerms.put("postnatal", ontologyTermDAO.getOntologyTermByNameAndDatabaseId("postnatal", efoDBID));
		efoTerms.put("TS20,embryo", ontologyTermDAO.getOntologyTermByAccession("EFO:3980"));

		efoTerms.put("male", ontologyTermDAO.getOntologyTermByNameAndDatabaseId("male", efoDBID));
		efoTerms.put("female", ontologyTermDAO.getOntologyTermByNameAndDatabaseId("female", efoDBID));

		datasource = dsDAO.getDatasourceByShortName(datasourceName);
		logger.info("Associating with datasource: {}", datasource.getName());

		List<CentreSpecimen> specimenSets = XMLUtils.unmarshal(ImpcXmlFormatSpecimenLoader.CONTEXT_PATH, CentreSpecimenSet.class, filename).getCentre();

		if (specimenSets.size() == 0) {
			logger.error("{} failed to unmarshall", filename);
			throw new XMLloadingException(filename + " failed to unserialize.");
		}

		logger.info("Specimen files has {} specimen sets", specimenSets.size());

		for (CentreSpecimen specimenSet : specimenSets) {

			logger.info("Parsing centre {}", specimenSet.getCentreID());

			// set the organisation there
			String centreID = specimenSet.getCentreID().value();

			// Map the DCC center name to the CDA name
			centreID = getCdaOrganisationName(centreID);
			Organisation phenotypingCenter = orgDAO.getOrganisationByName(centreID);

			for (Specimen specimen : specimenSet.getMouseOrEmbryo()) {

				if (specimen.getPhenotypingCentre() != null) {
					centreID = getCdaOrganisationName(centreID);
					phenotypingCenter = orgDAO.getOrganisationByName(centreID);
				}

				// Get production center
				Organisation productionCenter = null;
				if (specimen.getProductionCentre() != null) {
					String prodCentreID = getCdaOrganisationName(specimen.getProductionCentre().value());
					if (productionCenter != null) {
						productionCenter = orgDAO.getOrganisationByName(prodCentreID);
					}
				}



				try {

					String id = specimen.getSpecimenID();
					Boolean isBaseline = specimen.isIsBaseline();

					if (biologicalModelDAO.getLiveSampleBySampleIdAndOrganisationId(id, phenotypingCenter.getId()) != null) {
						logger.debug("This entry is a duplicate entry. Skipping");
						recordAnalytics(specimen, specimen.getProject(), "Duplicate entry", "This entry is a duplicate entry for specimen external ID: " + id + ". Skipping", null);
						continue;
					}

					String mappedProject = specimen.getProject();
					String mappedColony = specimen.getColonyID();

					String colonyID;
					try {
						colonyID = specimen.getColonyID();
						if (colonyID == null || colonyID.isEmpty()) {
							// Baseline animals may not have colonyID
							colonyID = "baseline";
						}
					} catch (NullPointerException e1) {
						// Baseline animals may not have colonyID
						colonyID = "baseline";
					}

					if (COLONIES_TO_SKIP.contains(colonyID)) {
						recordAnalytics(specimen, mappedProject, "Explicitly skipped", "Data has been withdrawn for specimen " + id + " (colonyId: " + colonyID + ", phenotyping center: " + centreID + ")", null);
						continue;
					}

					//					// Some TCP specimens are coming through with a dangling "_TCP"
					//					// Chop that bit right off and continue.
					//					if (centreID.equals("TCP")) {
					//						if (colonyID.endsWith("_TCP")) {
					//							colonyID = colonyID.replaceAll("_TCP", "");
					//						}
					//					}

					// The colony ID in the Europhenome XML file from May2013 has
					// the colonyID concatenated with and underscore and
					// the an internal DCC database ID
					// remove the underscore and ID from the
					// colonyID before proceeding, but only for non IMPC colonies
					// Similarly, re map the project and colony if required using iMits as the canonical source
					if (datasourceName.toUpperCase().equals("EUROPHENOME")) {
						String[] parts = colonyID.split("_");

						// Split the colonies that look like EPD0037_3_D11_197
						parts = (String[]) ArrayUtils.remove(parts, parts.length - 1);

						colonyID = StringUtils.join(parts, "_").trim();

						mappedProject = imits.getProjectByEsCell(colonyID, specimen.getPhenotypingCentre().value());

						// Override the colony ID to the imits version if the colony ID is different from that
						// supplied in the
						if (!colonyID.equals("baseline")) {
							mappedColony = imits.getColonyIdByEsCell(colonyID, specimen.getPhenotypingCentre().value());

							// Es cell was not found in iMits so the mapped colony is null, reset to colony ID
							if (mappedColony == null) {
								mappedColony = colonyID;
							}

						}

					}

					// default to project from XML file if project is not found in iMits
					if (mappedProject == null) {
						mappedProject = specimen.getProject();
					}

					if (phenotypingCenter == null) {
						logger.error("Cannot find organisation for " + phenotypingCenter);
						recordAnalytics(specimen, mappedProject, "Not found - phenotyping center", "Organisation not found for specimen " + id + " (colonyId: " + colonyID + ", phenotyping center: " + phenotypingCenter + ")", null);
						continue;
					}

					DatasourceEntityId lookupKey = new DatasourceEntityId(id, phenotypingCenter.getId());

					if (liveSamples.containsKey(lookupKey)) {
						logger.debug("This entry is a duplicated entry " + lookupKey + ". Skipping");
						recordAnalytics(specimen, mappedProject, "Duplicate entry", "This entry is a duplicate entry for specimen " + lookupKey + ". Skipping", null);
						continue;
					}

					String sampleZygosity = specimen.getZygosity().value();

					// Some centers are sending the +/+ set of specimens
					// from a cross as litter mate controls -- mark them
					// as control even if the baseline flag is false
					// by checking the zygosity == "wild type"
					if (!isBaseline && sampleZygosity.equals("wild type")) {

						isBaseline = Boolean.TRUE;

						logger.warn("Using specimen " + id + " from colony " + colonyID + " as a control, even thought it is baseline 'false' because it has zygosity: " + sampleZygosity);

					}

					// Attempt to get the background strain from imits (override the XML files)
					String backgroundStrainName = imits.getStrainByColonyId(colonyID, translateName(phenotypingCenter.getName()));

					// When imits doesn't have the background strain,
					// use the strain identified in the file
					if (backgroundStrainName == null) {

						if (specimen.getStrainID() != null) {
							backgroundStrainName = specimen.getStrainID();
						}

					}

					if (backgroundStrainName == null) {
						recordAnalytics(specimen, mappedProject, "Not found - strain", "Strain not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
						continue;
					}

					// what is the genetic background?
					String geneticBackground;

					try {

						geneticBackground = getGeneticBackground(backgroundStrainName);

					} catch (StrainNotFoundException e1) {

						// We could not find the strain,
						// mark it as missing
						geneticBackground = null;

					}

					if (geneticBackground == null) {
						recordAnalytics(specimen, mappedProject, "Not found - genetic background", "Genetic background not found for specimen " + id + " (colonyId: " + colonyID + ", backgroundStrain: " + backgroundStrainName + ")", null);
						continue;
					}

					// Using the ES cell line (i.e. colonyID), we can
					// query the MGI allele ID
					// then using the allele MGI ID we can query the
					// gene MGI ID....
					// If the ES CELL cannot be found, we need to fall
					// back to a lookup ESCell => gene

					// We can also look at phenotype attempts for a
					// "colony id" to find the gene
					// and allele

					GenomicFeature gene;
					Allele allele;

					try {
						if (!isBaseline && !colonyIdToGene.containsKey(colonyID)) {

							gene = escellMap.getGene(colonyID);
							allele = escellMap.getAllele(colonyID);

							if (gene == null) {

								if (esCellNotFound.contains(colonyID)) {
									if (colonyNotFound.get(colonyID) == null) {
										colonyNotFound.put(colonyID, new HashSet<String>());
									}
									colonyNotFound.get(colonyID).add(id);

									recordAnalytics(specimen, mappedProject, "Not found - colony ID", "Colony ID not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
									continue;
								}

								if (alleleNotFound.contains(colonyID)) {
									if (colonyNotFound.get(colonyID) == null) {
										colonyNotFound.put(colonyID, new HashSet<String>());
									}
									colonyNotFound.get(colonyID).add(id);

									recordAnalytics(specimen, mappedProject, "Not found - allele", "Allele not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
									continue;
								}

								String geneIdentifier = null;

								//
								// ES cell identifiers usually contain
								// an underscore
								//
								if (colonyID.contains("_")) {

									// Try to get the gene by looking at
									// es cells
									try {

										if (geneIdentifier == null) {
											geneIdentifier = imits.getGeneByEscell(colonyID);
										}

									} catch (Exception ex) {

										esCellNotFound.add(colonyID);
										if (colonyNotFound.get(colonyID) == null) {
											colonyNotFound.put(colonyID, new HashSet<String>());
										}
										colonyNotFound.get(colonyID).add(id);

										logger.error(ExceptionUtils.getFullStackTrace(ex));

										recordAnalytics(specimen, mappedProject, "Not found - colony ID", "Imits lookup gene by ES cell ID - Colony ID not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
										continue;

									}

								}

								if (geneIdentifier == null) {

									// Try to get the gene by looking at
									// phenotype attempts for this
									// colony
									try {

										geneIdentifier = imits.getGeneByColonyId(colonyID, translateName(phenotypingCenter.getName()));


									} catch (Exception ex) {

										if (colonyNotFound.get(colonyID) == null) {
											colonyNotFound.put(colonyID, new HashSet<String>());
										}
										colonyNotFound.get(colonyID).add(id);

										logger.error(String.format("Not loading animal %s. Cannot find gene for colony: %s", id, colonyID));

										recordAnalytics(specimen, mappedProject, "Not found - gene", "Imits lookup gene by Colony ID - Colony ID not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
										continue;

									}

								}

								// Did not find the gene
								if (geneIdentifier == null) {

									if (colonyNotFound.get(colonyID) == null) {
										colonyNotFound.put(colonyID, new HashSet<String>());
									}
									colonyNotFound.get(colonyID).add(id);
									logger.error(String.format("Not loading animal %s. Cannot find gene identifier for colony: %s", id, colonyID));

									recordAnalytics(specimen, mappedProject, "Not found - gene", "Gene identifier not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
									continue;
								}

								// Try to get gene object by MGI ID
								gene = genomicFeatureDAO.getGenomicFeatureByAccession(geneIdentifier);

								// Try to get gene object by symbol or
								// synonym
								if (gene == null) {
									gene = genomicFeatureDAO.getGenomicFeatureBySymbolOrSynonym(geneIdentifier);
								}

								if (gene == null) {
									logger.error("Not loading animal. Cannot find gene for symbol: " + geneIdentifier);

									if (colonyNotFound.get(colonyID) == null) {
										colonyNotFound.put(colonyID, new HashSet<String>());
									}
									colonyNotFound.get(colonyID).add(id);

									recordAnalytics(specimen, mappedProject, "Not found - gene", "Gene object not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
									continue;
								}

							}

							// populate the caches
							colonyIdToGene.put(colonyID, gene);

							// It's ok to set the allele here since if it's null it will get looked up and reset later
							colonyIdToAllele.put(colonyID, allele);

						} else {
							gene = colonyIdToGene.get(colonyID);
							allele = colonyIdToAllele.get(colonyID);
						}

						if (allele == null && !isBaseline) {

							if (alleleNotFound.contains(colonyID)) {
								if (colonyNotFound.get(colonyID) == null) {
									colonyNotFound.put(colonyID, new HashSet<String>());
								}
								colonyNotFound.get(colonyID).add(id);

								recordAnalytics(specimen, mappedProject, "Not found - allele", "Allele not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
								continue;
							}

							// Try to get the allele by looking at
							// phenotype attempts for this colony
							String alleleSymbol = imits.getAlleleSymbolByColonyId(colonyID);

							if (alleleSymbol != null) {

								allele = alleleDAO.getAlleleBySymbol(alleleSymbol);

							} else {

								// Try to get the allele MGI ID
								// directly from imits colony ID

								String alleleMgiid = imits.getAlleleMGIIDByEscell(colonyID);
								allele = alleleDAO.getAlleleByAccession(alleleMgiid);

								if (allele!=null) {
									logger.info("Allele {} found by getAlleleMGIIDByEscell", allele.getSymbol());
								}
							}

							// if we still cant find the allele, try to create the allele
							if (allele == null && alleleSymbol != null) {
								try {
									allele = getAllele(alleleSymbol);
								} catch (Exception e) {
									logger.warn("Failed to created allele {} for colony id {}", alleleSymbol, colonyID);
								}
							}

							// if we still cant find the allele, skip
							// this specimen
							if (allele == null) {
								alleleNotFound.add(colonyID);

								if (colonyNotFound.get(colonyID) == null) {
									colonyNotFound.put(colonyID, new HashSet<String>());
								}
								colonyNotFound.get(colonyID).add(id);

								recordAnalytics(specimen, mappedProject, "Not found - allele", "Allele not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
								continue;

							} else {
								colonyIdToAllele.put(colonyID, allele);
							}
						}
					} catch (Exception ex) {
						// some error processing this one
						logger.error("An error occurred while processing animal " + id + ": \n" + ExceptionUtils.getFullStackTrace(ex));

						if (colonyNotFound.get(colonyID) == null) {
							colonyNotFound.put(colonyID, new HashSet<String>());
						}
						colonyNotFound.get(colonyID).add(id);

						recordAnalytics(specimen, mappedProject, "Error", "An error occurred while processing specimen " + id + " (colonyId: " + colonyID + ")", null);
						continue;
					}

					if ((allele == null || allele.getSymbol() == null) && !isBaseline) {
						alleleNotFound.add(colonyID);

						if (colonyNotFound.get(colonyID) == null) {
							colonyNotFound.put(colonyID, new HashSet<String>());
						}
						colonyNotFound.get(colonyID).add(id);

						recordAnalytics(specimen, mappedProject, "Not found - allele", "Allele or allele symbol not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
						continue;
					}

					// 2014-12-04 Per Hugh's request, load all EuroPhenome alleles, not just the Gt and tm alleles
					//				if (!isBaseline && !allele.getSymbol().contains("<tm") && !allele.getSymbol().contains("<Gt")) {
					//					if (alleleNotAppropriate.get(allele.getSymbol()) == null) {
					//						alleleNotAppropriate.put(allele.getSymbol(), new HashSet<String>());
					//					} else {
					//						alleleNotAppropriate.get(allele.getSymbol()).add(id);
					//					}
					//
					//					if (colonyNotFound.get(colonyID) == null) {
					//						colonyNotFound.put(colonyID, new HashSet<String>());
					//					}
					//					colonyNotFound.get(colonyID).add(id);
					//
					//                    recordAnalytics(specimen, mappedProject, "Not appropriate", "Allele is not a targeted mutation or a gene trap for specimen " + id + " (colonyId: " + colonyID + ")", null);
					//					continue;
					//				}

					String sex = specimen.getGender().value();

					if (sex.equals("no data") && specimen instanceof Embryo) {
						sex = SexType.no_data.getName();
					}


					// Load all the embryos!
					//				if(specimen instanceof Embryo) {
					//					recordAnalytics(specimen, mappedProject, "Not loading - embryo", "Specimen " + id + " (colonyId: " + colonyID + ") is an embryo. Not loaded", null);
					//					continue;
					//				}

					// if the sex supplied is not in the enumeration
					// skip this record and report
					try {

						SexType.valueOf(sex);

					} catch (IllegalArgumentException e1) {

						if (sexNotFound.get(sex) == null) {
							sexNotFound.put(sex, new HashSet<String>());
						}

						sexNotFound.get(sex).add(id);

						recordAnalytics(specimen, mappedProject, "Not found - sex", "Sex not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
						continue;

					}
					Date dateOfBirth = getDateOfBirth(specimen);


					// what the allelic composition?
					String sampleGroup = (isBaseline) ? "control" : "experimental";
					String geneSymbol = (gene != null) ? gene.getSymbol() : "";
					String allelicName = (allele != null) ? allele.getSymbol() : colonyID;
					String allelicComposition = StrainNames.getAllelicComposition(sampleZygosity, allelicName, geneSymbol, sampleGroup);

					// Let's create a Live Sample:
					String zygosity = null;
					switch (sampleZygosity) {
						case "homozygous":
							zygosity = ZygosityType.homozygote.getName();
							break;
						case "heterozygous":
							zygosity = ZygosityType.heterozygote.getName();
							break;
						case "hemizygous":
							zygosity = ZygosityType.hemizygote.getName();
							break;
					}

					LiveSample animal = new LiveSample();
					animal.setDatasource(datasource);

					animal.setDateOfBirth(dateOfBirth);
					if (id.contains("_WTSI")) {
						id = id.replaceFirst("_WTSI", "");
					}
					animal.setStableId(id);
					animal.setSex(sex);
					animal.setOrganisation(phenotypingCenter);
					animal.setProductionCenter(productionCenter);
					animal.setLitterId(specimen.getLitterId());

					// Override the file colonyID with the colonyID from imits if appropriate
					if (mappedColony != null && !mappedColony.isEmpty() && !colonyID.equals(mappedColony)) {
						colonyID = mappedColony;
					}
					animal.setColonyID(colonyID);

					animal.setGroup((isBaseline) ? "control" : "experimental");
					animal.setZygosity(zygosity);

					if (specimen instanceof Mouse) {

						//Mouse
						animal.setType(efoTerms.get("whole organism"));
						animal.setDevelopmentalStage(efoTerms.get("postnatal"));
					} else {

						// Embryo
						animal.setType(efoTerms.get("embryo stage"));
						OntologyTerm efoTerm = impressUtilities.getStageTerm(((Embryo) specimen).getStage().replaceAll("E", ""), StageUnitType.valueOf(((Embryo) specimen).getStageUnit().value()));
						if (efoTerm == null) {
							logger.error("Could not get developmental stage EFO term for " + colonyID + " (animal ID: " + specimen.getSpecimenID() +") stage: " + ((Embryo) specimen).getStage() + " stageUnit: " + ((Embryo) specimen).getStageUnit());
							recordAnalytics(specimen, mappedProject, "Not found - Developmental stage", "Embryo stage not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
							continue;
						}
						animal.setDevelopmentalStage(efoTerm);
					}

					// link to biological model
					BiologicalModelKey bmKey = new BiologicalModelKey(zygosity, allelicComposition, backgroundStrainName);

					if (allelicComposition == null || geneticBackground == null || (allelicComposition.equals("") && geneticBackground.equals("involves: "))) {
						logger.error("Could not get allelic composition or genetic background for " + colonyID + " (animal ID: " + id + ") allelicComposition: " + allelicComposition + " geneticBackground: " + geneticBackground);
						recordAnalytics(specimen, mappedProject, "Not found - genetic background", "Allelic composition or genetic background not found for specimen " + id + " (colonyId: " + colonyID + ")", null);
						continue;
					}

					BiologicalModel biologicalModel = biologicalModelDAO.findByDbidAndAllelicCompositionAndGeneticBackgroundAndZygosity(datasource.getId(), allelicComposition, geneticBackground, zygosity);

					if (biologicalModel == null && biologicalModels.containsKey(bmKey)) {

						// We've seen this biological model already for this load
						biologicalModel = biologicalModels.get(bmKey);

					} else if (biologicalModel == null) {

						biologicalModel = new BiologicalModel();
						biologicalModel.setAllelicComposition(allelicComposition);
						biologicalModel.setGeneticBackground(geneticBackground);
						biologicalModel.setZygosity(zygosity);
						biologicalModel.setDatasource(datasource);

						// add gene and allele to non-control specimens
						if (!isBaseline) {
							biologicalModel.addGenomicFeature(gene);
							biologicalModel.addAllele(allele);
						}

						Strain strain = StrainNames.getStrain(backgroundStrainName, datasourceName, datasource.getId(), strainDAO);
						biologicalModel.addStrain(strain);

						logger.debug("Saving biological model {}, {}", biologicalModel.getAllelicComposition(), biologicalModel.getGeneticBackground());
						biologicalModelDAO.saveBiologicalModel(biologicalModel);
						biologicalModels.put(bmKey, biologicalModel);
					}

					animal.setBiologicalModel(biologicalModel);

					DatasourceEntityId sampleKey = new DatasourceEntityId(animal.getStableId(), animal.getOrganisation().getId());
					liveSamples.put(sampleKey, animal);

					// Record successfully loaded specimen
					recordAnalytics(specimen, mappedProject, "Success", null, null);

				} catch (Exception e1) {
					logger.error("Failed to load record for specimen {}", specimen.getSpecimenID(), e1);
					recordAnalytics(specimen, "Not determined", "Failed", null, null);
				}

			}

			// save all the animals

			for (LiveSample liveSample : liveSamples.values()) {
				biologicalModelDAO.saveLiveSample(liveSample);
			}
			logger.info("Saved {} specimens", liveSamples.size());

		}
	}

	// Parse date of birth, embryo has no date of birth so set to null as default and override if mouse
	public Date getDateOfBirth(Specimen specimen) {

		Date dateOfBirth;

		if (specimen instanceof Mouse) {
			dateOfBirth = ((Mouse) specimen).getDOB().getTime();
		} else {
			dateOfBirth = null;
		}

		return dateOfBirth;
	}


	/**
	 * Create an allele record in the database with the supplied allele symbol
	 *
	 * @param alleleSymbol the allele symbol
	 * @return an allele DAO object representing the newly created allele
	 */
	public Allele getAllele(String alleleSymbol) throws Exception {

		// Short circuit if the allele already exists
		if (alleleDAO.getAlleleBySymbol(alleleSymbol) != null) {
			return alleleDAO.getAlleleBySymbol(alleleSymbol);
		}

		// Create the allele based from the symbol
		// e.g. allele symbol Lama4<tm1.1(KOMP)Vlcg>

		// 1) Get the gene symbol
		String alleleGeneSymbol = alleleSymbol.substring(0, alleleSymbol.indexOf('<'));

		// 2) get the allele gene
		GenomicFeature alleleGene = genomicFeatureDAO.getGenomicFeatureBySymbol(alleleGeneSymbol);

		// 3) Determine the allele acc
		String alleleAccession = "NULL-" + DigestUtils.md5Hex(alleleSymbol).substring(0, 9).toUpperCase();

		// 4) insert the allele to the database
		insertAllele(alleleSymbol, alleleAccession, alleleGene.getId().getAccession());

		// 5) get the hibernate allele object
		Allele allele = alleleDAO.getAlleleByAccession(alleleAccession);

		logger.info("Created allele {} for gene {} with accession {}", alleleSymbol, alleleGeneSymbol, alleleAccession);

		return allele;
	}


	private String translateName(String name) {
		if (name.toUpperCase().equals("UC DAVIS")) {
			return "UCD";
		} else {
			return name;
		}
	}


	/**
	 * Attempt to create an allele in the database
	 *
	 * @param alleleSymbol    allele symbol to insert
	 * @param alleleAccession allele accession id to insert
	 * @param geneAccession   the accession id of the gene to which the allele is associated
	 * @throws SQLException
	 */
	private void insertAllele(String alleleSymbol, String alleleAccession, String geneAccession) throws SQLException {

		try (PreparedStatement p = connection.prepareStatement(alleleInsert)) {
			//INSERT INTO allele(acc, symbol, name, gf_acc, db_id, gf_db_id, biotype_acc, biotype_db_id) VALUES (?, ?, ?, ?, 3, 3, 'CV:00000013', 3)

			Integer i = 1;
			setSqlParameter(p, alleleAccession, i++);
			setSqlParameter(p, alleleSymbol, i++);
			setSqlParameter(p, alleleSymbol, i++);
			setSqlParameter(p, geneAccession, i);
			p.executeUpdate();
		}
	}


	private void recordAnalytics(Specimen specimen, String mappedProject, String status, String message, String additionalInformation)
		throws SQLException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		// Only remap MGP legacy projects
		if (!mappedProject.equals("MGP Legacy")) {
			mappedProject = "";
		}

		try (PreparedStatement p = connection.prepareStatement(analyticsQuery)) {
			//filename, colony_id, dob, baseline, strain, specimen_id, sex, zygosity, litter_id, impress_pipeline, production_center, phenotyping_center, project, mapped_project, status, message, additional_information
			Integer i = 1;
			setSqlParameter(p, filename, i++);
			setSqlParameter(p, (specimen.getColonyID() != null) ? specimen.getColonyID() : "", i++);

			try {
				if (specimen instanceof Mouse) {
					setSqlParameter(p, sdf.format(((Mouse) specimen).getDOB().getTime()), i++);
				} else {
					Embryo e = (Embryo) specimen;
					setSqlParameter(p, e.getStage() + " " + e.getStageUnit(), i++);
				}
			} catch (Exception e) {
				setSqlParameter(p, "Error getting date/stage", i++);
			}

			setSqlParameter(p, specimen.isIsBaseline(), i++);
			setSqlParameter(p, (specimen.getStrainID() != null) ? specimen.getStrainID() : "", i++);
			setSqlParameter(p, specimen.getSpecimenID(), i++);
			setSqlParameter(p, specimen.getGender().value(), i++);
			setSqlParameter(p, specimen.getZygosity().value(), i++);
			setSqlParameter(p, specimen.getLitterId(), i++);
			setSqlParameter(p, specimen.getPipeline(), i++);
			setSqlParameter(p, (specimen.getProductionCentre() != null) ? specimen.getProductionCentre().value() : "", i++);
			setSqlParameter(p, specimen.getPhenotypingCentre().value(), i++);
			setSqlParameter(p, specimen.getProject(), i++);
			setSqlParameter(p, mappedProject, i++);
			setSqlParameter(p, status, i++);
			setSqlParameter(p, message, i++);
			setSqlParameter(p, additionalInformation, i); // last field
			p.executeUpdate();
		}
	}

	public String getCdaOrganisationName(String orgName) {

		String upperOrgName = orgName.toUpperCase();

		if (dccMapping.dccCenterMap.containsKey(upperOrgName)) {
			upperOrgName = dccMapping.dccCenterMap.get(upperOrgName).toUpperCase();
		}

		return upperOrgName;
	}


	/**
	 * Retrieve the correct genetic background string from the EuroPhenome string.
	 * Use the list of strains derived from the genetic background string if need be
	 *
	 * @param background the string for which to determine the strain
	 * @return the correct genetic background string
	 * @throws StrainNotFoundException
	 */
	protected String getGeneticBackground(String background) throws StrainNotFoundException {
		String gb;
		if (geneticBackgrounds.containsKey(background)) {
			gb = geneticBackgrounds.get(background);
		} else {
			gb = StrainNames.getGeneticBackground(background, datasourceName, datasource.getId(), strainDAO);
			geneticBackgrounds.put(background, gb);

		}
		return gb;
	}


}
