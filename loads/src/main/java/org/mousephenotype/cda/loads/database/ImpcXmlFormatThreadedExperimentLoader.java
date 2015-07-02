package org.mousephenotype.cda.loads.database;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.h2.store.fs.FileUtils;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StatusCode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import uk.ac.ebi.phenotype.admin.dao.LoadImagesDao;
import uk.ac.ebi.phenotype.admin.dao.ParameterStatusMap;
import uk.ac.ebi.phenotype.dao.*;
import uk.ac.ebi.phenotype.data.europhenome.exceptions.BiologicalModelNotFoundException;
import uk.ac.ebi.phenotype.data.imits.EncodedOrganisationConversionMap;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.data.stats.Broker;
import uk.ac.ebi.phenotype.data.stats.MpTermService;
import uk.ac.ebi.phenotype.pojo.*;
import uk.ac.ebi.phenotype.service.ExperimentService;
import uk.ac.ebi.phenotype.service.ObservationService;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static uk.ac.ebi.phenotype.data.stats.SqlUtils.setSqlParameter;


/**
 * Uses a producer/consumer model to handle loading experiment data
 *
 * @author jmason
 */

@Component
public class ImpcXmlFormatThreadedExperimentLoader {


	private static final Logger logger = LoggerFactory.getLogger(ImpcXmlFormatThreadedExperimentLoader.class);
	private static final String CONTEXT_ARG = "context";
	private static final String DIRECTORY_ARG = "dir";
	private static final String DATASOURCE_ARG = "datasource";

	private static final int numThreads = 4;
	protected ApplicationContext applicationContext;
	protected String experimentFilePath;
	protected List<File> files;

	/**
	 * Spring auto wired dependencies
	 */
	@Autowired
	private DatasourceDAO dsDAO;
	@Autowired
	private OrganisationDAO orgDAO;
	@Autowired
	private BiologicalModelDAO biologicalModelDAO;
	@Autowired
	private PhenotypePipelineDAO pipelineDAO;
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private ObservationDAO observationDAO;
	@Autowired
	private LoadImagesDao loadImagesDao;

	@Autowired
	private ObservationService os;

	@Autowired
	private OrganisationDAO organisationDAO;

	@Autowired
	private ExperimentService es;

	@Autowired
	private PhenotypePipelineDAO phenotypePipelineDAO;

	@Autowired
	@Qualifier("komp2DataSource")
	private DataSource komp2DataSource;

	@Autowired
	private PhenotypePipelineDAO ppDAO;

	@Autowired
	private OntologyTermDAO ontologyTermDAO;

	@Autowired
	private MpTermService mpTermService;

	@Autowired
	private Utilities impressUtilities;

	@Autowired
	private EncodedOrganisationConversionMap dccMapping;

	/**
	 * Local variables
	 */
	private Map<String, Project> colonyProjectMap = new HashMap<>();
	private Map<String, Project> specimenProjectMap = new HashMap<>();
	private Map<String, Project> projects = new HashMap<>();
	private Map<String, Pipeline> pipelines = new HashMap<>();
	private Map<String, uk.ac.ebi.phenotype.pojo.Procedure> procedures = new HashMap<>();
	private Map<String, uk.ac.ebi.phenotype.pojo.Parameter> parameters = new HashMap<>();
	private Map<String, String> parameterIncrementUnit = new HashMap<>();
	private Map<String, LiveSample> specimens = new HashMap<>();
	private boolean bSkipExperiment = false;
	private Integer unknownAnimals = 0;
	private Integer parsed = 0;
	private Integer skipped = 0;


	private static final String analyticsQuery = "INSERT INTO analytics_experiment_load (filename, center_id, date_of_experiment, sequence_id, experiment_id, specimen_id, impress_pipeline, impress_procedure, impress_parameters, parameter_types, data_values, metadata_values, missing_data_values, status, message, additional_information) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String colonyMapQuery = "SELECT DISTINCT specimen_id, colony_id, mapped_project FROM analytics_specimen_load WHERE mapped_project != ''";

	private static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";

	private static Datasource globalDatasource;
	Datasource mgpDatasource;

	private static Connection connection;
	private static String datasourceName;
	private static Organisation phenotypingCenter;
	private static Pipeline pipeline;

	private static Map<String, Integer> centerMap = new HashMap<>();
	static {
		// Provided by MRC Harwell
		centerMap.put("HMGU", 1);
		centerMap.put("MRC Harwell", 2);
		centerMap.put("WTSI", 3);
		centerMap.put("ICS", 4);
	}


	public static void main(String[] args) throws InstantiationException, InterruptedException, ExecutionException, IOException, SQLException {

		ImpcXmlFormatThreadedExperimentLoader main = new ImpcXmlFormatThreadedExperimentLoader();
		main.initialise(args);
		main.run();

		logger.info("Process finished.  Exiting.");
	}


	public void run() throws InstantiationException, SQLException, ExecutionException, InterruptedException {

		File folder = new File(experimentFilePath);
		if(folder.listFiles()==null || folder.length()==0) {
			logger.info("No files found to process at filepath {}.", experimentFilePath);
			return;
		}

		File[] fileArray = folder.listFiles();
		List<File> files;
		if(fileArray!=null) {
			files = Collections.synchronizedList(new ArrayList<>(Arrays.asList(fileArray)));

			// Only process experiment files
			for (File file : Arrays.asList(fileArray)) {
				if( ! file.getName().toLowerCase().contains("experiment")) {
					files.remove(file);
				}
			}

			// Disperse large files randomly throughout the load process
			// There is a tendency for time series parameters, which are large individually, to be
			// exported into contiguous files, loading these files into memory at the same time
			// could exhaust the available mem.  Randomly sorting these into the rest (~700 files) gives
			// a better memory performance profile
			Collections.shuffle(files);

		} else {
			logger.info("No files found to process at filepath {}.  Exiting.", experimentFilePath);
			return;
		}

		Connection connection = komp2DataSource.getConnection();
		connection.setAutoCommit(true);

		logger.info("Establishing thread pool");
		ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
		ExecutorService saverThreadPool = Executors.newFixedThreadPool(1);

		logger.info("Establishing shared queue");
		Broker<Observation> broker = new Broker<>(1000, "Experiment queue");

		logger.info("Starting saver thread");
		ImpcXmlObservationSaver producer = new ImpcXmlObservationSaver(broker);
		Future<?> saverThreadStatus = saverThreadPool.submit(producer);

		//=============================================================================================================
		// MAIN PROCESS
		//=============================================================================================================
		long start = System.currentTimeMillis();
		loadAllPipelinesByStableIds();
		logger.info("Preparing pipeline/procedure/parameter maps - complete (took {}s)", ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		for (Project p : projectDAO.getAllProjects()) {

			// Translate all project names to upper case for easy mapping
			projects.put(p.getName().toUpperCase(), p);

			// Also add to the map translations from legacy iMits project names
			if (p.getName().equals("MGP")) {
				projects.put("MGP Legacy", p);
			}

			if (p.getName().equals("EUMODIC")) {
				projects.put("EUCOMM-EUMODIC", p);
			}
		}
		logger.info("Preparing project map - complete (took {}s)", ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		loadColonyIdToProjectMapping();
		logger.info("Preparing colony to project re-map - complete (took {}s)", ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		// Cache the MGP datasource to support remapping projects indicated in imits
		// as MGP projects to the correct datasource
		mgpDatasource =  dsDAO.getDatasourceByShortName("MGP");
		globalDatasource = dsDAO.getDatasourceByShortName(datasourceName);
		logger.info("Loading data for datasources ('MGP', {}) - complete (took {}s)", datasourceName, ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		loadAllAnimals();
		logger.info("Loading all specimens ({}) - complete (took {}s)", specimens.size(), ((System.currentTimeMillis() - start) / 1000.0));



		// Start up the consumers
		logger.info("Starting consumer threads");
		for (File file : files) {

			try {
				ImpcXmlFileConsumer consumer = new ImpcXmlFileConsumer(file, broker);
				threadPool.execute(consumer);
			} catch (Exception e) {
				logger.error("Error instantiating consumer", e);
			}

			broker.moreData=false;

		}

		// Indicate that the threadpool should shutdown after
		threadPool.shutdown();

		// Wait for all the tasks to finish executing
		logger.info("Waiting for producer threads to finish");
		while(!threadPool.isTerminated()) {
			Thread.sleep(1000);
		}

		// No more data will be enqueued
		broker.moreData = false;

		// Finish saving the queue
		// force saverThreadStatus to complete
		logger.info("Saver thread status ('null' is good): " + saverThreadStatus.get());
		saverThreadPool.shutdown();

		logger.info("Loading experiments finished");

	}


	//=================================================================================================================
	// SUPPORT METHODS
	//=================================================================================================================

	public void initialise(String[] args) throws SQLException {

		OptionSet options = parseCommandLine(args);
		if (options != null) {
			experimentFilePath = (String) options.valuesOf(DIRECTORY_ARG).get(0);

			applicationContext = loadApplicationContext((String) options.valuesOf(CONTEXT_ARG).get(0));
			applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
			initialiseHibernateSession(applicationContext);
			connection = komp2DataSource.getConnection();
		} else {
			throw new RuntimeException("Failed to parse command-line options.");
		}
	}


	protected OptionSet parseCommandLine(String[] args) {

		OptionParser parser = new OptionParser();
		OptionSet options = null;

		// parameter to indicate which spring context file to use
		parser.accepts(CONTEXT_ARG).withRequiredArg().ofType(String.class)
			.describedAs("Spring context file, such as 'index-app-config.xml'");

		parser.accepts(DIRECTORY_ARG).withRequiredArg().ofType(String.class)
			.describedAs("Directory path to the experiment files, such as '/nfs/komp2/web/phenotype_data/impc/latest/'");

		parser.accepts(DATASOURCE_ARG).withRequiredArg().ofType(String.class)
			.describedAs("Datasource to associate to the observations loaded, such as 'IMPC' or 'EuroPhenome'");


		try {
			options = parser.parse(args);
		} catch (OptionException uoe) {
			if (args.length < 1) {
				System.out.println("Expected required context file parameter and directory parameter.");
			} else {
				System.out.println("Bad context file '" + CONTEXT_ARG + " or missing directory.\n\nUsage:\n");
			}
			try {
				parser.printHelpOn(System.out);
			} catch (IOException e) {
			}
			throw uoe;
		}

		return options;
	}


	protected ApplicationContext loadApplicationContext(String context) {

		ApplicationContext applicationContext = null;

		if (FileUtils.exists(context)) {
			try {
				// Try context as a file resource
				logger.info("Trying to load context from file system...");
				applicationContext = new FileSystemXmlApplicationContext("file:" + context);
				logger.info("Context loaded from file system");
			} catch (BeansException e) {
				logger.info("Unable to load the context file: {}", e.getMessage());
			}
		} else {

			// Try context as a class path resource
			applicationContext = new ClassPathXmlApplicationContext(context);
			logger.info("Using classpath app-config file: {}", context);
		}

		return applicationContext;
	}


	protected void initialiseHibernateSession(ApplicationContext applicationContext) {
		// allow hibernate session to stay open the whole execution
		PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
		DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
		transactionAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
		transactionManager.getTransaction(transactionAttribute);
	}


	/**
	 * Convert a time unit from absolute date to "time since lights out" as required
	 * by the SOP.
	 *
	 * input comes in as a full time stamp string like 2009-01-22 14:05:00
	 *
	 * @param input the absolute date/time to convert
	 * @param phenotypingCenter location the time was recorded
	 * @return float representing the converted time between -5 hours and +23 hours
	 */
	private float convertTimepoint(String input, Organisation phenotypingCenter) {

		String[] dateTime = input.split(" ");
		String[] time = dateTime[1].split(":");
		Integer hour = Integer.parseInt(time[0]);
		Integer mins = Integer.parseInt(time[1]);

		if (hour < 13) { // Morning times are the next day
			hour += 24;
		}

		float retFloat;

		switch (centerMap.get(phenotypingCenter.getName())) {
			case 1: // HMGU
				retFloat = (float) hour - 18;
				break;
			case 2: // MRC
				retFloat = (float) hour - 19;
				break;
			case 3: // WTSI
				retFloat = (float) hour - 19;
				if (mins > 29) {
					retFloat += 1;
				}
				break;
			case 4: // ICS
				retFloat = (float) hour - 19;
				break;
			default: // CMHD never sent calorimetry, so this should never happen
				retFloat = hour;
				break;
		}
		return retFloat;
	}

	/**
	 * Pre-load the IMPReSS maps for faster look-ups
	 */
	protected void loadAllPipelinesByStableIds() {

		List<Pipeline> pipelineList = pipelineDAO.getAllPhenotypePipelines();

		for (Pipeline p: pipelineList) {
			pipelines.put(p.getStableId(), p);
			for (uk.ac.ebi.phenotype.pojo.Procedure proc: p.getProcedures()) {
				procedures.put(proc.getStableId(), proc);
				for (uk.ac.ebi.phenotype.pojo.Parameter param: proc.getParameters()) {
					parameters.put(param.getStableId(), param);
					if (param.isIncrementFlag()) {
						for (ParameterIncrement increment: param.getIncrement()) {
							if (increment.getValue().length() > 0) {
								parameterIncrementUnit.put(param.getStableId(), increment.getDataType());
							}
						}
					}
				}
			}
		}
	}

	protected void loadColonyIdToProjectMapping() throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(colonyMapQuery)){

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				colonyProjectMap.put(resultSet.getString("colony_id"), projects.get(resultSet.getString("mapped_project")));
				specimenProjectMap.put(resultSet.getString("specimen_id"), projects.get(resultSet.getString("mapped_project")));
			}

		}
	}

	/**
	 * Map the supplied organisation name to the CDA approved name
	 *
	 * @param orgName the organisation name to map
	 * @return the mapped organisation name
	 */
	public String getCdaOrganisationName(String orgName) {

		String upperOrgName = orgName.toUpperCase();

		if (dccMapping.dccCenterMap.containsKey(upperOrgName)) {
			upperOrgName = dccMapping.dccCenterMap.get(upperOrgName).toUpperCase();
		}

		return upperOrgName;
	}

	/**
	 * Populate the specimen map for fast lookup of biological samples to attach the data.
	 */
	private void loadAllAnimals() {
		if (specimens.size() < 1) {
			List<LiveSample> animalList = biologicalModelDAO.getAllLiveSamples();
			for (LiveSample animal : animalList) {
				specimens.put(animal.getStableId(), animal);
			}
		}
	}
	private void logParameterError(String centreID, String experimentID, String parameterID,
	                               String parameterStatusString) {
		String errorString="center="+centreID+"experimentId="+experimentID+"parameterId="+parameterID+" parameterStatus="+parameterStatusString+" paramStatusDescription="+ ParameterStatusMap.getParameterStatusDescription(parameterStatusString);
		logger.error(errorString);
	}


	/**
	 * Record the attempt to load the data into the experiment analytics tables
	 *
	 * @param experimentOrLine either and experiment or a line could trigger the analytics
	 * @param status the status of the insert
	 * @param message a brief message to provide more information about the status of the data load
	 * @param additionalInformation large messages (i.e. stack traces) about the status of the data load
	 * @throws SQLException when the database could not insert the data point
	 */
	private void recordAnalytics(String filename, Object experimentOrLine, String status, String message, String additionalInformation)
		throws SQLException {

		org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure procedure;
		String dateOfExperiment;
		String sequenceId;
		String experimentId;
		String specimenId;

		if(experimentOrLine.getClass().equals(org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment.class)) {
			org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment eol = (org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment)experimentOrLine;
			procedure = eol.getProcedure();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			dateOfExperiment = sdf.format(eol.getDateOfExperiment().getTime());
			sequenceId = eol.getSequenceID();
			experimentId = eol.getExperimentID();
			specimenId = eol.getSpecimenID().get(0);
		} else {
			Line eol = (Line)experimentOrLine;
			procedure = eol.getProcedure();
			dateOfExperiment = null;
			sequenceId = eol.getSequenceID();
			experimentId = String.format("%s-%s", eol.getProcedure().getProcedureID(), eol.getColonyID());
			specimenId = null;
		}

		if(sequenceId == null) {
			sequenceId = "";
		}


		List<Object> parameters = new ArrayList<>();
		parameters.addAll(procedure.getSimpleParameter());
		parameters.addAll(procedure.getSeriesParameter());
		parameters.addAll(procedure.getSeriesMediaParameter());
		parameters.addAll(procedure.getMediaParameter());
		parameters.addAll(procedure.getMediaSampleParameter());
		parameters.addAll(procedure.getOntologyParameter());
		parameters.addAll(procedure.getProcedureMetadata());

		Set<String> parameterTypes = new HashSet<>();
		Set<String> parameterIds = new TreeSet<>();
		Integer parameterValues = 0;
		Integer metadataValues = 0;
		Integer missing = 0;

		for(Object parameter : parameters) {

			switch (parameter.getClass().getName()) {
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SimpleParameter":
					parameterTypes.add("SimpleParameter");
					parameterIds.add(((SimpleParameter) parameter).getParameterID());
					if(((SimpleParameter) parameter).getParameterStatus() == null) {
						parameterValues += 1;
					} else {
						missing += 1;
					}
					break;
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesParameter":
					parameterTypes.add("SeriesParameter");
					parameterIds.add(((SeriesParameter) parameter).getParameterID());
					if(((SeriesParameter) parameter).getParameterStatus() == null) {
						parameterValues += ((SeriesParameter) parameter).getValue().size();
					} else {
						missing += 1;
					}
					break;
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesMediaParameter":
					parameterTypes.add("SeriesMediaParameter");
					parameterIds.add(((SeriesMediaParameter) parameter).getParameterID());
					if(((SeriesMediaParameter) parameter).getParameterStatus() == null) {
						parameterValues += ((SeriesMediaParameter) parameter).getValue().size();
					} else {
						missing += 1;
					}
					break;
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaParameter":
					parameterTypes.add("MediaParameter");
					parameterIds.add(((MediaParameter) parameter).getParameterID());
					if(((MediaParameter) parameter).getParameterStatus() == null) {
						parameterValues += 1;
					} else {
						missing += 1;
					}
					break;
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaSampleParameter":
					parameterTypes.add("MediaSampleParameter");
					parameterIds.add(((MediaSampleParameter) parameter).getParameterID());
					if(((MediaSampleParameter) parameter).getParameterStatus() == null) {
						parameterValues += ((MediaSampleParameter) parameter).getMediaSample().size();
					} else {
						missing += 1;
					}
					break;
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.OntologyParameter":
					parameterTypes.add("OntologyParameter");
					parameterIds.add(((OntologyParameter) parameter).getParameterID());
					if(((OntologyParameter) parameter).getParameterStatus() == null) {
						parameterValues += ((OntologyParameter) parameter).getTerm().size();
					} else {
						missing += 1;
					}
					break;
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ProcedureMetadata":
					parameterTypes.add("ProcedureMetadata");
					parameterIds.add(((ProcedureMetadata) parameter).getParameterID());
					if(((ProcedureMetadata) parameter).getParameterStatus() == null) {
						metadataValues += 1;
					}
					break;
				default:
					break;
			}

		}

		try (PreparedStatement p = connection.prepareStatement(analyticsQuery)) {
			// filename, center_id, date_of_experiment,
			// sequence_id, experiment_id, specimen_id,
			// impress_pipeline, impress_procedure, impress_parameter,
			// parameter_type, data_values, missing, status,
			// message, additional_information
			Integer i = 1;
			setSqlParameter(p, filename, i++);
			setSqlParameter(p, phenotypingCenter.getName(), i++);
			setSqlParameter(p, dateOfExperiment, i++);
			setSqlParameter(p, sequenceId, i++);
			setSqlParameter(p, experimentId, i++);
			setSqlParameter(p, specimenId, i++);
			setSqlParameter(p, pipeline.getName(), i++);
			setSqlParameter(p, procedure.getProcedureID(), i++);
			setSqlParameter(p, StringUtils.join(parameterIds, ", "), i++);
			setSqlParameter(p, StringUtils.abbreviate(StringUtils.join(parameterTypes, ", "), 255), i++);
			setSqlParameter(p, parameterValues, i++);
			setSqlParameter(p, metadataValues, i++);
			setSqlParameter(p, missing, i++);
			setSqlParameter(p, status, i++);
			setSqlParameter(p, message, i++);
			setSqlParameter(p, additionalInformation, i); // last field
//TODO:			p.executeUpdate();
		}

	}


	/**
	 * Gets or creates a biological model without an allelic composition and a not applicable zygosity
	 * for use with line level parameters
	 *
	 * @param colonyID the colony for which to retrieve the biological model
	 * @return a biological model object
	 * @throws SQLException
	 */
	private BiologicalModel getBaseBiologicalModelByColonyId(String colonyID, List<SimpleParameter> simpleParameters) throws SQLException, BiologicalModelNotFoundException {

		// Default zygosity is homozygous since most of the time this will be the case
		ZygosityType zygosity = ZygosityType.homozygote;

		// Check if Hemizygote
		for (SimpleParameter param : simpleParameters) {

			// Find the associated "Outcome" parameter
			if (param.getParameterID().equals("IMPC_VIA_001_001")) {

				// Found the outcome parameter, check zygosity
				String category = param.getValue();

				if (category != null && category.contains("Hemizygous")) {
					zygosity = ZygosityType.hemizygote;
				}

				break;
			}

		}

		String query = "SELECT bm.id FROM biological_model bm " +
			"INNER JOIN experiment e ON bm.id=e.biological_model_id " +
			"WHERE e.colony_id = ? AND bm.zygosity = ? LIMIT 1";


		try (PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, colonyID);
			statement.setString(2, zygosity.getName());

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {

				BiologicalModel bm = biologicalModelDAO.getBiologicalModelById(resultSet.getInt("id"));

				if (bm != null) {
					// Base Biological model found

					logger.info("Using existing line level biological model for colony {}", colonyID);

					return bm;
				}
			}

		}

		// Create an appropriate biological model based on an existing one

		// Query out an existing biological model for this colony
		// Any biological model will do since we are interested in the strain, gene, allele
		// associations, not the zygosity
		query = "SELECT bm.id FROM biological_model bm " +
			"INNER JOIN biological_model_sample bms ON bm.id=bms.biological_model_id " +
			"INNER JOIN live_sample ls ON bms.biological_sample_id=ls.id " +
			"WHERE colony_id = ? LIMIT 1";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, colonyID);

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {

				BiologicalModel bm = biologicalModelDAO.getBiologicalModelById(resultSet.getInt("id"));

				if (bm == null) {
					// Something wrong... there is no existing biological model for this colony ID!
					throw new BiologicalModelNotFoundException("Biological model not found");
				}

				logger.info("Creating new line level biological model for colony {}", colonyID);

				// Copy associations from the existing biological model when appropriate.
				// Save and return the new model
				BiologicalModel newBm = new BiologicalModel();
				newBm.setAllelicComposition(bm.getAllelicComposition());
				newBm.setGeneticBackground(bm.getGeneticBackground());

				// Set the appropriate zygosity for the line
				newBm.setZygosity(zygosity.getName());

				newBm.setDatasource(bm.getDatasource());

				for (Allele a : bm.getAlleles()) {
					newBm.addAllele(a);
				}

				for (Strain s : bm.getStrains()) {
					newBm.addStrain(s);
				}

				for (GenomicFeature gf : bm.getGenomicFeatures()) {
					newBm.addGenomicFeature(gf);
				}

//TODO:				biologicalModelDAO.saveBiologicalModel(newBm);

				// Return on the first valid result set since we don't care which
				// existing model we're copying -- they're all the same gene/allele/strain!
				return newBm;
			}

		}

		logger.error("Cannot find existing biological model for line level parameter for colony {}", colonyID);
		return null;
	}


	//=================================================================================================================
	// INTERNAL CLASSES
	//=================================================================================================================

	/**
	 * Consume the xml file and put the created experiments on the experiment queue
	 */
	public class ImpcXmlFileConsumer implements Runnable {

		private Broker<Observation> broker;
		private String name;
		private File file;
		private String filename;

		public ImpcXmlFileConsumer(File file, Broker<Observation> broker) {
			this.broker=broker;
			this.file = file;
			this.filename = file.getName();
			this.name = "ImpcXmlFileConsumer("+filename+")";
		}

		@Override
		public void run() {

			int count=0;

			try {

				List<CentreProcedure> centreProcedures = null;

				try {

					// XMLUtils is not threadsave, synchronize access
					synchronized (XMLUtils.CONTEXTPATH) {
						centreProcedures = XMLUtils.unmarshal(CONTEXT_PATH, CentreProcedureSet.class, file.getAbsolutePath()).getCentre();
					}

					// Persist errors to display at the end
					List<String> possibleErrorsList = new ArrayList<>();

					for (CentreProcedure centreProcedure : centreProcedures) {

						// Default datasource is the one passed in on the command line
						Datasource datasource = globalDatasource;

						// get the project name
						String projectName = centreProcedure.getProject().toUpperCase();

						// get the phenotypingCenter
						String centreID = centreProcedure.getCentreID().name();

						// Map the DCC center name to the CDA name
						centreID = getCdaOrganisationName(centreID);

						logger.debug("Loading data for phenotypingCenter {}", centreID);
						phenotypingCenter = orgDAO.getOrganisationByName(centreID);
						if (phenotypingCenter == null) {
							logger.error("phenotypingCenter can't be found. Skipping %s", centreID);
							continue;
						}

						// get the pipeline
						String pipelineName = centreProcedure.getPipeline();
						logger.debug("Loading data for pipeline {}", pipelineName);
						pipeline = pipelines.get(pipelineName);
						if (pipeline == null) {
							logger.error("Pipeline can't be found. Skipping {}", pipelineName);
							continue;
						}

						//
						// Line level procedures
						//

						try {
							for (Line line : centreProcedure.getLine()) {

								logger.debug("Loading data for project {}", projectName);
								Project project = projects.get(projectName);
								if (project == null) {
									logger.error("project can't be found. Skipping line colony id {} for project {}", line.getColonyID(), projectName);
									continue;
								}

								if (colonyProjectMap.containsKey(line.getColonyID()) && !project.equals(colonyProjectMap.get(line.getColonyID()))) {

									logger.info("Remapping colony " + line.getColonyID() + " project association from " + projectName + " to " + (colonyProjectMap.containsKey(line.getColonyID()) ? colonyProjectMap.get(line.getColonyID()).getName() : "(colony not found)"));

									if (!colonyProjectMap.containsKey(line.getColonyID())) {
										String errMsg = String.format("Cannot find the project for: %s", line.getColonyID());
										logger.warn(errMsg);
										recordAnalytics(filename, line, "Skipped - Missing line level project", errMsg, null);
										continue;
									}

									// Remap this line to MGP from EUMODIC/EuroPhenome
									project = colonyProjectMap.get(line.getColonyID());
									datasource = mgpDatasource;

								}


								org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure impressProcedure = line.getProcedure();
								String procedureName = impressProcedure.getProcedureID();
								uk.ac.ebi.phenotype.pojo.Procedure procedure = procedures.get(procedureName);//pipelineDAO.getProcedureByStableId(procedureName);

								// Fail fast on parameter types we do not import yet
								// Only type we currently import are simple parameters
								if (!impressProcedure.getMediaParameter().isEmpty()) {
									String errMsg = String.format("We don't process line level MediaParameter: %s", procedureName);
									logger.warn(errMsg);
									recordAnalytics(filename, line, "Skipped - Inappropriate line level parameter type", errMsg, null);
									bSkipExperiment = true;
								}
								if (!impressProcedure.getMediaSampleParameter().isEmpty()) {
									String errMsg = String.format("We don't process line level MediaSampleParameter: %s", procedureName);
									logger.warn(errMsg);
									recordAnalytics(filename, line, "Skipped - Inappropriate line level parameter type", errMsg, null);
									bSkipExperiment = true;
								}
								if (!impressProcedure.getOntologyParameter().isEmpty()) {
									String errMsg = String.format("We don't process line level OntologyParameter: %s", procedureName);
									logger.warn(errMsg);
									recordAnalytics(filename, line, "Skipped - Inappropriate line level parameter type", errMsg, null);
									bSkipExperiment = true;
								}
								if (!impressProcedure.getSeriesMediaParameter().isEmpty()) {
									String errMsg = String.format("We don't process line level SeriesMediaParameter: %s", procedureName);
									logger.warn(errMsg);
									recordAnalytics(filename, line, "Skipped - Inappropriate line level parameter type", errMsg, null);
									bSkipExperiment = true;
								}
								if (!impressProcedure.getSeriesParameter().isEmpty()) {
									String errMsg = String.format("We don't process line level SeriesParameter: %s", procedureName);
									logger.warn(errMsg);
									recordAnalytics(filename, line, "Skipped - Inappropriate line level parameter type", errMsg, null);
									bSkipExperiment = true;
								}


								if (bSkipExperiment) {

									skipped++;

								} else {

									logger.debug("fertility and viability data procedure {}", procedureName);

									String experimentID = String.format("%s-%s", line.getProcedure().getProcedureID(), line.getColonyID());
									logger.debug("constructed experiment ID {}", experimentID);

									BiologicalModel associatedModel;
									try {
										associatedModel = getBaseBiologicalModelByColonyId(line.getColonyID(), impressProcedure.getSimpleParameter());
									} catch (BiologicalModelNotFoundException | SQLException e) {
										logger.error("Cannot find or create biological model for line level parameter for colony {}", line.getColonyID());
										recordAnalytics(filename, line, "Skipped - No biological model", null, ExceptionUtils.getFullStackTrace(e));
										continue;
									}

									// Create a CDA experiment to collect this data
									uk.ac.ebi.phenotype.pojo.Experiment currentExperiment = new uk.ac.ebi.phenotype.pojo.Experiment();
									currentExperiment.setDatasource(datasource);
									currentExperiment.setDateOfExperiment(null);
									currentExperiment.setExternalId(experimentID);
									currentExperiment.setModel(associatedModel);
									currentExperiment.setColonyId(line.getColonyID());
									currentExperiment.setOrganisation(phenotypingCenter);
									currentExperiment.setProject(project);
									currentExperiment.setPipeline(pipeline);
									currentExperiment.setPipelineStableId(pipeline.getStableId());
									currentExperiment.setProcedure(procedure);
									currentExperiment.setProcedureStableId(procedure.getStableId());

									// Add status codes to the experiment
									for (StatusCode statusCode : line.getStatusCode()) {

										String code = statusCode.getValue();

										if (code.contains(":")) {

											String message = code.substring(code.indexOf(":") + 1, code.length()).trim();
											currentExperiment.setProcedureStatusMessage(message);

											code = code.substring(0, code.indexOf(":"));

										}

										currentExperiment.setProcedureStatus(code);

									}

//TODO:							observationDAO.saveExperiment(currentExperiment);

//TODO:							loadImagesDao.setProcedure(impressProcedure);
//TODO:							loadImagesDao.setExperiment(currentExperiment);

									List<SimpleParameter> simpleParameters = impressProcedure.getSimpleParameter();

									// Save the parameter information
									for (SimpleParameter simpleParameter : simpleParameters) {

										// get the parameter ID fo a simple parameter
										String parameterID = simpleParameter.getParameterID();

										//need suffix here but not on other params?
										Parameter parameter = parameters.get(parameterID);
										if (parameter == null) {
											logger.error("Parameter object for id {} cannot be found", parameterID);
											continue;
										}

										String simpleValue = simpleParameter.getValue();
										if (simpleValue == null) {
											simpleValue = "null";//done this to mimick the behaviour of the sax parser when this is null! check this ok - doesn't throw null pointer error anymore
										}
										ObservationType observationType = impressUtilities.checkType(parameter, simpleValue);

										// then depending on the type, create the relevant information
										Observation observation = observationDAO.createSimpleObservation(observationType, simpleValue, parameter, null, datasource, currentExperiment, simpleParameter.getParameterStatus());
										broker.put(observation);


									}

									// Save the metadata for this experiment
									for (ProcedureMetadata procedureMetadata : impressProcedure.getProcedureMetadata()) {
										try {

											loadImagesDao.loadProcedureMetaData(connection, procedureMetadata, 0);

										} catch (SQLException e) {
											logger.error(ExceptionUtils.getFullStackTrace(e));

											recordAnalytics(filename, line, "Error - metadata", null, ExceptionUtils.getFullStackTrace(e));
											break;

										}
									}

									recordAnalytics(filename, line, "Success", null, null);

								} // end of skip experiment
								bSkipExperiment = false;

							}

						} catch (SQLException | InterruptedException e) {
							e.printStackTrace();

						}
					}

				} catch (JAXBException | IOException e) {
					e.printStackTrace();

				}

			} catch (Exception e) {
				logger.info("Exception cought while processing file {}.", filename, e);

			}

			logger.info("Experiment file consumer thread {} complete. Enqueued {} experiments", name, count);
		}
	}

	/**
	 * Save experiments as they are created
	 */
	public class ImpcXmlObservationSaver implements Runnable {

		private Broker<Observation> broker;

		public ImpcXmlObservationSaver(Broker<Observation> broker) {
			this.broker=broker;
		}

		@Override
		public void run() {

			int count = 0;
			while (broker.size()<0 || broker.hasMoreData()) {

				// Save the observation
				try {
					count += 1;
					Observation observation = broker.get();

//					observationDAO.saveObservation(observation);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			logger.info("Experiment saver thread complete. Saved {} experiments", count);
		}
	}

}
