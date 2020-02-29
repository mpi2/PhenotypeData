package org.mousephenotype.cda.loads.derived;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.inference.BinomialTest;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.db.repositories.*;
import org.mousephenotype.cda.db.utilities.ObservationUtils;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.loads.common.CommandLineUtils;
import org.mousephenotype.cda.loads.common.ConcurrentHashMapAllowNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootApplication
@Import(value = {GenerateDerivedParametersConfig.class})
public class GenerateDerivedParameters implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ExecutorService       executor;
    private List<Future<Integer>> tasks = new ArrayList<>();

    private Map<Long, LiveSample>     animals         = new ConcurrentHashMapAllowNull<>();
    private Map<Long, Datasource>     datasources     = new ConcurrentHashMapAllowNull<>();
    private Map<Long, Project>        projects        = new ConcurrentHashMapAllowNull<>();
    private Map<String, Pipeline>     pipelines       = new ConcurrentHashMapAllowNull<>();
    private Map<String, Organisation> organisations   = new ConcurrentHashMapAllowNull<>();


    private BiologicalModelRepository biologicalModelRepository;
    private DatasourceRepository      datasourceRepository;
    private ExperimentRepository      experimentRepository;
    private DataSource                komp2DataSource;
    private LiveSampleRepository      liveSampleRepository;
    private ObservationRepository     observationRepository;
    private OrganisationRepository    organisationRepository;
    private ParameterRepository       parameterRepository;
    private PipelineRepository        pipelineRepository;
    private ProcedureRepository       procedureRepository;
    private ProjectRepository         projectRepository;


    @Inject
    public GenerateDerivedParameters(
            BiologicalModelRepository biologicalModelRepository,
            DatasourceRepository datasourceRepository,
            ExperimentRepository experimentRepository,
            DataSource komp2DataSource,
            LiveSampleRepository liveSampleRepository,
            ObservationRepository observationRepository,
            OrganisationRepository organisationRepository,
            ParameterRepository parameterRepository,
            PipelineRepository pipelineRepository,
            ProcedureRepository procedureRepository,
            ProjectRepository projectRepository)
    {
        this.biologicalModelRepository = biologicalModelRepository;
        this.datasourceRepository = datasourceRepository;
        this.experimentRepository = experimentRepository;
        this.komp2DataSource = komp2DataSource;
        this.liveSampleRepository = liveSampleRepository;
        this.observationRepository = observationRepository;
        this.organisationRepository = organisationRepository;
        this.parameterRepository = parameterRepository;
        this.pipelineRepository = pipelineRepository;
        this.procedureRepository = procedureRepository;
        this.projectRepository = projectRepository;
    }


    @Override
    public void run(String... args) throws Exception {

        executor = Executors.newFixedThreadPool(16);

        organisations = loadAllOrganisationsById();
        datasources = loadAllDatasourcesById();
        projects = loadAllProjectsById();
		animals = loadAllAnimalsById();
        pipelines = loadAllPipelinesByStableId();


        OptionParser parser = CommandLineUtils.getOptionParser();
        parser.accepts( "context" ).withRequiredArg();
        parser.accepts("parameters").withRequiredArg();
        OptionSet options = parser.parse( args );

        if (options.has("parameters")) {
            String paramString = (String) options.valueOf("parameters");

            List<String> parameters = Arrays.asList(paramString.split(","));
            generateSpecificDerivedParameters(parameters);

        } else {
            getAllDerivedParameters();
        }

        executor.shutdown();

    }


    private void generateSpecificDerivedParameters(List<String> parameters) {

        // Reusable task for adding unit of work to the queue
        Callable<Integer> task;

        for (String parameter : parameters) {

            logger.info("Processing parameter {}", parameter);

            switch (parameter) {

                // Only called via manually passing in the parameter
                case "IMPC_VIA_032_001":
                    task = () -> IMPC_VIA_032_001();
                    tasks.add(executor.submit(task));
                    break;

                case "GMC_914_001_704":
                    task = () -> copyDivisionResult("GMC_914_001_704", "GMC_914_001_015", "GMC_914_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "GMC_914_001_701":
                    task = () -> copyMultiplicationResult("GMC_914_001_701", "GMC_914_001_005", "GMC_914_001_021");    // Could not test, no data
                    tasks.add(executor.submit(task));
                    break;

                case "GMC_914_001_705":
                    task = () -> copyMultiplicationOfDivision("GMC_914_001_705", "GMC_914_001_015", "GMC_914_001_009", 100);    // Could not test, no data
                    tasks.add(executor.submit(task));
                    break;

                case "GMC_914_001_702":
                    task = () -> copyMultiplicationOfDivision("GMC_914_001_702", "GMC_914_001_015", "GMC_914_001_009", "GMC_914_001_005");    // Could not test, no data
                    tasks.add(executor.submit(task));
                    break;

                case "TCP_TFL_002_001":
                    task = () -> copyMeanOfIncrements("TCP_TFL_002_001", "IMPC_TFL_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_DXA_007_001":
                    task = () -> copyDivisionResult("IMPC_DXA_007_001", "IMPC_DXA_005_001", "IMPC_DXA_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_DXA_008_001":
                    task = () -> copyDivisionResult("IMPC_DXA_008_001", "IMPC_DXA_003_001", "IMPC_DXA_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_DXA_009_001":
                    task = () -> copyDivisionResult("IMPC_DXA_009_001", "IMPC_DXA_002_001", "IMPC_DXA_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_DXA_010_001":
                    task = () -> copyDivisionResult("IMPC_DXA_010_001", "IMPC_DXA_005_001", "IMPC_DXA_004_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_GRS_008_001":
                    task = () -> copyMeanOfIncrements("IMPC_GRS_008_001", "IMPC_GRS_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_GRS_009_001":
                    task = () -> copyMeanOfIncrements("IMPC_GRS_009_001", "IMPC_GRS_002_001");
                    tasks.add(executor.submit(task));
                    break;

                case "ICS_ROT_002_001":
                    task = () -> copyMeanOfIncrements("ICS_ROT_002_001", "ICS_ROT_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_HWT_012_001":
                    task = () -> copyDivisionResult("IMPC_HWT_012_001", "IMPC_HWT_008_001", "IMPC_HWT_007_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_OFD_020_001":
                    task = () -> copySumOfIncrements("IMPC_OFD_020_001", "IMPC_OFD_005_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_OFD_021_001":
                    task = () -> copySumOfIncrements("IMPC_OFD_021_001", "IMPC_OFD_006_001");
                    tasks.add(executor.submit(task));
                    break;

                case "MGP_IPG_011_001":
                    task = () -> copyDifferenceOfIncrements("MGP_IPG_011_001", "MGP_IPG_002_001", 15.0, 0.0);
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_IPG_012_001":
                    task = () -> copyAUC("IMPC_IPG_012_001", "IMPC_IPG_002_001");
                    tasks.add(executor.submit(task));
                    break;

                case "MGP_IPG_012_001":
                    task = () -> copyAUC("MGP_IPG_012_001", "MGP_IPG_002_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_EYE_092_001":
                    task = () -> IMPC_EYE_092_001();
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_ACS_033_001":
                    task = () -> copyPercentageOfDifference("IMPC_ACS_033_001", "IMPC_ACS_006_001", "IMPC_ACS_007_001", "IMPC_ACS_006_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_ACS_034_001":
                    task = () -> copyPercentageOfDifference("IMPC_ACS_034_001", "IMPC_ACS_006_001", "IMPC_ACS_008_001", "IMPC_ACS_006_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_ACS_035_001":
                    task = () -> copyPercentageOfDifference("IMPC_ACS_035_001", "IMPC_ACS_006_001", "IMPC_ACS_009_001", "IMPC_ACS_006_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_ACS_036_001":
                    task = () -> copyPercentageOfDifference("IMPC_ACS_036_001", "IMPC_ACS_006_001", "IMPC_ACS_010_001", "IMPC_ACS_006_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_OFD_022_001":
                    task = () -> copyPercentageOf("IMPC_OFD_022_001", "IMPC_OFD_016_001", "IMPC_OFD_008_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_ACS_037_001":
                    task = () -> IMPC_ACS_037_001();
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_IPG_010_001":
                    task = () -> copyIncrementValueAt("IMPC_IPG_010_001", "IMPC_IPG_002_001", 0);
                    tasks.add(executor.submit(task));
                    break;

                case "MGP_IPG_010_001":
                    task = () -> copyIncrementValueAt("MGP_IPG_010_001", "MGP_IPG_002_001", 0);
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_CAL_017_001":
                    task = () -> copyDivisionOfMeanOfIncrements("IMPC_CAL_017_001", "IMPC_CAL_004_001", "IMPC_CAL_003_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_IPG_011_001":
                    task = () -> IMPC_IPG_011_001();
                    tasks.add(executor.submit(task));
                    break;



                case "IMPC_FEA_003_001":
                    task = () -> copyMultiplicationOfDivision("IMPC_FEA_003_001", "IMPC_FEA_002_001", "IMPC_FEA_033_001", 100);
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_FEA_009_001":
                    task = () -> copyMultiplicationOfDivision("IMPC_FEA_009_001", "IMPC_FEA_008_001", "IMPC_FEA_049_001", 100);
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_FEA_015_001":
                    task = () -> copyMultiplicationOfDivision("IMPC_FEA_015_001", "IMPC_FEA_014_001", "IMPC_FEA_057_001", 100);
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_FEA_021_001":
                    task = () -> copyMultiplicationOfDivision("IMPC_FEA_021_001", "IMPC_FEA_020_001", "IMPC_FEA_060_001", 100);
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_FEA_091_001":
                    task = () -> copyMultiplicationOfDivision("IMPC_FEA_091_001", "IMPC_FEA_090_001", "IMPC_FEA_036_001", 100);
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_FEA_100_001":
                    task = () -> copyMultiplicationOfDivision("IMPC_FEA_100_001", "IMPC_FEA_099_001", "IMPC_FEA_039_001", 100);
                    tasks.add(executor.submit(task));
                    break;



                case "ESLIM_004_001_701":
                    task = () -> copyAUC("ESLIM_004_001_701", "ESLIM_004_001_002");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_009_001_703":
                    task = () -> copyDivisionOfSumOfIncrementsOverNumberOfIncrements("ESLIM_009_001_703", "ESLIM_009_001_001", "ESLIM_009_001_003");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_009_001_704":
                    task = () -> copyDivisionOfSumOfIncrementsOverNumberOfIncrements("ESLIM_009_001_704", "ESLIM_009_001_002", "ESLIM_009_001_003");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_009_001_701":
                    task = () -> copyMeanOfIncrements("ESLIM_009_001_701", "ESLIM_009_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_009_001_702":
                    task = () -> copyMeanOfIncrements("ESLIM_009_001_702", "ESLIM_009_001_002");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_011_001_701":
                    task = () -> copyPercentageOfDifference("ESLIM_011_001_701", "ESLIM_011_001_006", "ESLIM_011_001_007", "ESLIM_011_001_006");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_011_001_702":
                    task = () -> copyPercentageOfDifference("ESLIM_011_001_702", "ESLIM_011_001_006", "ESLIM_011_001_008", "ESLIM_011_001_006");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_011_001_703":
                    task = () -> copyPercentageOfDifference("ESLIM_011_001_703", "ESLIM_011_001_006", "ESLIM_011_001_009", "ESLIM_011_001_006");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_011_001_704":
                    task = () -> copyPercentageOfDifference("ESLIM_011_001_704", "ESLIM_011_001_006", "ESLIM_011_001_010", "ESLIM_011_001_006");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_011_001_705":
                    task = () -> ESLIM_011_001_705();
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_003_001_701":
                    task = () -> ESLIM_003_001_701();
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_005_001_701":
                    task = () -> copyDivisionResult("ESLIM_005_001_701", "ESLIM_005_001_005", "ESLIM_005_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_005_001_702":
                    task = () -> copyDivisionResult("ESLIM_005_001_702", "ESLIM_005_001_003", "ESLIM_005_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_005_001_703":
                    task = () -> copyDivisionResult("ESLIM_005_001_703", "ESLIM_005_001_002", "ESLIM_005_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_005_001_704":
                    task = () -> copyDivisionResult("ESLIM_005_001_704", "ESLIM_005_001_005", "ESLIM_005_001_004");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_703":
                    task = () -> copyParameter("ESLIM_022_001_703", "ESLIM_001_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_704":
                    task = () -> copyParameter("ESLIM_022_001_704", "ESLIM_002_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_705":
                    task = () -> copyParameter("ESLIM_022_001_705", "ESLIM_003_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_706":
                    task = () -> copyParameter("ESLIM_022_001_706", "ESLIM_004_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_707":
                    task = () -> copyParameter("ESLIM_022_001_707", "ESLIM_005_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_709":
                    task = () -> copyParameter("ESLIM_022_001_709", "ESLIM_009_001_003");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_710":
                    task = () -> copyParameter("ESLIM_022_001_710", "ESLIM_010_001_003");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_711":
                    task = () -> copyParameter("ESLIM_022_001_711", "ESLIM_011_001_011");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_712":
                    task = () -> copyParameter("ESLIM_022_001_712", "ESLIM_012_001_005");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_713":
                    task = () -> copyParameter("ESLIM_022_001_713", "ESLIM_013_001_018");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_708":
                    task = () -> copyParameter("ESLIM_022_001_708", "ESLIM_020_001_001");
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_701":
                    ArrayList<String> params = new ArrayList<>();
                    params.add("ESLIM_001_001_001");
                    params.add("ESLIM_002_001_001");
                    params.add("ESLIM_003_001_001");
                    params.add("ESLIM_004_001_001");
                    params.add("ESLIM_005_001_001");
                    params.add("ESLIM_020_001_001");
                    params.add("ESLIM_022_001_001");
                    task = () -> plotParametersAsTimeSeries("ESLIM_022_001_701", params);
                    tasks.add(executor.submit(task));
                    break;

                case "ESLIM_022_001_702":
                    params = new ArrayList<>();
                    params.add("ESLIM_009_001_003");
                    params.add("ESLIM_010_001_003");
                    params.add("ESLIM_011_001_011");
                    params.add("ESLIM_012_001_005");
                    params.add("ESLIM_013_001_018");
                    params.add("ESLIM_022_001_001");
                    task = () -> plotParametersAsTimeSeries("ESLIM_022_001_702", params);
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_BWT_008_001":
                    params = new ArrayList<>();
                    params.add("IMPC_GRS_003_001");
                    params.add("IMPC_CAL_001_001");
                    params.add("IMPC_DXA_001_001");
                    params.add("IMPC_HWT_007_001");
                    params.add("IMPC_PAT_049_001");
                    params.add("IMPC_BWT_001_001");
                    params.add("IMPC_ABR_001_001");
                    params.add("IMPC_CHL_001_001");
                    params.add("TCP_CHL_001_001");
                    params.add("HMGU_ROT_004_001");
                    task = () -> plotParametersAsTimeSeries("IMPC_BWT_008_001", params);
                    tasks.add(executor.submit(task));
                    break;

                default:
                    logger.info("Delayed processing parameter " + parameter);
                    break;
            }
        }


        //
        // These parameters have dependencies on other derived parameters
        //

        for (String parameter : parameters.stream().filter(x -> Arrays.asList("IMPC_GRS_010_001", "IMPC_GRS_011_001").contains(x)).collect(Collectors.toList())) {

            logger.info("Processing parameter {}", parameter);

            switch (parameter) {
                case "IMPC_GRS_010_001":
                    task = () -> copyDivisionResult("IMPC_GRS_010_001", "IMPC_GRS_008_001", "IMPC_GRS_003_001");
                    tasks.add(executor.submit(task));
                    break;

                case "IMPC_GRS_011_001":
                    task = () -> copyDivisionResult("IMPC_GRS_011_001", "IMPC_GRS_009_001", "IMPC_GRS_003_001");
                    tasks.add(executor.submit(task));
                    break;

                default:
                    logger.warn("Cannot find case to create parameter " + parameter);
                    break;
            }
        }


    }




    private void getAllDerivedParameters() {
        List<String> allParams = new ArrayList<>();
        allParams.add("GMC_914_001_704");
        allParams.add("GMC_914_001_701");
        allParams.add("GMC_914_001_705");
        allParams.add("GMC_914_001_705");
        allParams.add("TCP_TFL_002_001");
        allParams.add("IMPC_HWT_012_001");
        allParams.add("IMPC_DXA_007_001");
        allParams.add("IMPC_DXA_008_001");
        allParams.add("IMPC_DXA_009_001");
        allParams.add("IMPC_DXA_010_001");
        allParams.add("IMPC_GRS_008_001");
        allParams.add("IMPC_GRS_009_001");
        allParams.add("ICS_ROT_002_001");
        allParams.add("IMPC_GRS_010_001");
        allParams.add("IMPC_GRS_011_001");
        allParams.add("IMPC_OFD_020_001");
        allParams.add("IMPC_OFD_021_001");
        allParams.add("MGP_IPG_011_001");
        allParams.add("IMPC_IPG_012_001");
        allParams.add("MGP_IPG_012_001");
        allParams.add("IMPC_EYE_092_001");
        allParams.add("IMPC_IPG_011_001");
        allParams.add("ESLIM_011_001_705");
        allParams.add("ESLIM_003_001_701");
        allParams.add("IMPC_ACS_037_001");
        allParams.add("IMPC_ACS_033_001");
        allParams.add("IMPC_ACS_034_001");
        allParams.add("IMPC_ACS_035_001");
        allParams.add("IMPC_ACS_036_001");
        allParams.add("IMPC_OFD_022_001");
        allParams.add("IMPC_IPG_010_001");
        allParams.add("MGP_IPG_010_001");
        allParams.add("IMPC_CAL_017_001");
        allParams.add("ESLIM_004_001_701");
        allParams.add("ESLIM_009_001_703");
        allParams.add("ESLIM_009_001_704");
        allParams.add("ESLIM_009_001_701");
        allParams.add("ESLIM_009_001_702");
        allParams.add("ESLIM_011_001_701");
        allParams.add("ESLIM_011_001_702");
        allParams.add("ESLIM_011_001_703");
        allParams.add("ESLIM_011_001_704");
        allParams.add("ESLIM_005_001_701");
        allParams.add("ESLIM_005_001_702");
        allParams.add("ESLIM_005_001_703");
        allParams.add("ESLIM_005_001_704");
        allParams.add("ESLIM_022_001_703");
        allParams.add("ESLIM_022_001_704");
        allParams.add("ESLIM_022_001_705");
        allParams.add("ESLIM_022_001_706");
        allParams.add("ESLIM_022_001_707");
        allParams.add("ESLIM_022_001_709");
        allParams.add("ESLIM_022_001_710");
        allParams.add("ESLIM_022_001_711");
        allParams.add("ESLIM_022_001_712");
        allParams.add("ESLIM_022_001_713");
        allParams.add("ESLIM_022_001_708");
        allParams.add("ESLIM_022_001_701");
        allParams.add("ESLIM_022_001_702");
        allParams.add("IMPC_BWT_008_001");

        generateSpecificDerivedParameters(allParams);

    }


    /**
     * NOTE: updated on 2016/05/03 to 100 x [S―(PP1_S + PP2_S + PP3_S + PP4_S)/4]/S
     *
     * Formula :  IMPC_ACS_037_001	"IMPC_ACS_007_001 IMPC_ACS_008_001 + IMPC_ACS_009_001 + IMPC_ACS_010_001 + 4 / IMPC_ACS_006_001 - IMPC_ACS_006_001 / 100 *"
     * x = ( IMPC_ACS_007_001 + IMPC_ACS_008_001 + IMPC_ACS_009_001 + IMPC_ACS_010_001 ) / 4;
     * x -= IMPC_ACS_006_001 ;
     * x /= IMPC_ACS_006_001 ;
     * x *= 100 ;
     *
     * NOTE: updated on 2019/05/22
     *
     * Per Elissa Chessler @ JAX on behalf of the Behaviour working group 2019-05
     * If PPI_4 is provided (it's an optional parameter in IMPReSS), use all four PPI intervals to
     * compute the derived parameter, else use PPI_1, 2, and 3 only (they are all required parameters)
     *
     * When 3 values are provided, the formula is: 100 * ((S - ((PP1_S + PP2_S + PP3_S) / 3)) / S)
     * When 4 values are provided, the formula is: 100 * ((S - ((PP1_S + PP2_S + PP3_S + PP4_S) / 4)) / S)
     */
    protected int IMPC_ACS_037_001()
            throws SQLException{

        int i = 0;
        String parameterToCreate = "IMPC_ACS_037_001";
        deleteObservationsForParameterId(parameterToCreate);
        String IMPC_ACS_006_001 = "IMPC_ACS_006_001";
        String IMPC_ACS_007_001 = "IMPC_ACS_007_001";
        String IMPC_ACS_008_001 = "IMPC_ACS_008_001";
        String IMPC_ACS_009_001 = "IMPC_ACS_009_001";
        String IMPC_ACS_010_001 = "IMPC_ACS_010_001";

        // Gather set of data that contains all 4 PP parameters
        ArrayList<String> paramsOfInterest = new ArrayList<>();
        paramsOfInterest.add(IMPC_ACS_007_001);
        paramsOfInterest.add(IMPC_ACS_008_001);
        paramsOfInterest.add(IMPC_ACS_009_001);
        paramsOfInterest.add(IMPC_ACS_006_001);
        paramsOfInterest.add(IMPC_ACS_010_001);

        Map<String, Map<String, ObservationDTO>> parameterMap = new HashMap<>();
        Set<String> animalIds = new HashSet<>(getResultsIntersectionByParameter(paramsOfInterest, parameterMap));

        // Gather set of data that contains just 3 PP parameters
        paramsOfInterest = new ArrayList<>();
        paramsOfInterest.add(IMPC_ACS_006_001);
        paramsOfInterest.add(IMPC_ACS_007_001);
        paramsOfInterest.add(IMPC_ACS_008_001);
        paramsOfInterest.add(IMPC_ACS_009_001);

        Set<String> threeSet = getResultsIntersectionByParameter(paramsOfInterest, parameterMap);
        animalIds.addAll(threeSet);


        for (String id: animalIds){
            if (parameterMap.get(IMPC_ACS_006_001).get(id).getDataPoint() != 0){ // denominator
                // create an experiment
                ObservationDTO dto = parameterMap.get(IMPC_ACS_007_001).get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

                Datasource datasource = datasources.get(dto.getExternalDbId());
                Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);

                // Use the same metadata as the other parameters in this procedure
                currentExperiment.setMetadataCombined(dto.getMetadataCombined());
                currentExperiment.setMetadataGroup(dto.getMetadataGroup());

                experimentRepository.save(currentExperiment);

                // compute data point
                // When 3 values are provided, the formula is: 100 * ((S - ((PP1_S + PP2_S + PP3_S) / 3)) / S)
                // When 4 values are provided, the formula is: 100 * ((S - ((PP1_S + PP2_S + PP3_S + PP4_S) / 4)) / S)

                Float PP1_S = dto.getDataPoint();
                Float PP2_S = parameterMap.get(IMPC_ACS_008_001).get(id).getDataPoint();
                Float PP3_S = parameterMap.get(IMPC_ACS_009_001).get(id).getDataPoint();

                Float mean = (PP1_S + PP2_S + PP3_S) / 3;

                if (parameterMap.containsKey(IMPC_ACS_010_001) && parameterMap.get(IMPC_ACS_010_001).containsKey(id)) {
                    Float PP4_S = parameterMap.get(IMPC_ACS_010_001).get(id).getDataPoint();
                    mean = (PP1_S + PP2_S + PP3_S + PP4_S) / 4;
                }

                Float S = parameterMap.get(IMPC_ACS_006_001).get(id).getDataPoint();
                Float dataPoint = 100 * ((S - mean) / S);

                Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);
                observationRepository.save(observation);
            }

        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);

        return i;

    }


    /**
     *  ESLIM_003_001_701	<-	ESLIM_003_001_004/ESLIM_003_001_003
     *  actually means :
     *  ESLIM_003_001_701	<-	average(ESLIM_003_001_004)/average(ESLIM_003_001_003)
     *  ESLIM_003_001_004 is timeseries
     *  ESLIM_003_001_003 is timeseries
     *  ESLIM_003_001_701 is unidimensional
     * @return number of ESLIM_003_001_701 obs added
     */
    private int ESLIM_003_001_701()
            throws SQLException{

        int i = 0;
        String parameterToCreate = "ESLIM_003_001_701";
        String nominatorParam = "ESLIM_003_001_004";
        String denominatorParam = "ESLIM_003_001_003";
        deleteObservationsForParameterId(parameterToCreate);

        Map<String, ObservationDTO> nominators = getIncrementalDataMapByParameter(nominatorParam);
        Map<String, ObservationDTO> denominators = getIncrementalDataMapByParameter(denominatorParam);

        ArrayList<String> paramsOfInterest = new ArrayList<>();
        paramsOfInterest.add(nominatorParam);
        paramsOfInterest.add(denominatorParam);

        Map<String, Map<String, ObservationDTO>> parameterMap = new HashMap<>();
        Set<String> animalIds =  getResultsIntersectionByParameter(paramsOfInterest, parameterMap);

        for (String id: animalIds){
            if (nominators.get(id).getIncrementValues().size() != 0 && denominators.get(id).getIncrementValues().size() != 0){
                ObservationDTO dto = nominators.get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

                Datasource datasource = datasources.get(dto.getExternalDbId());
                Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);

//                // Get metadata group as combined nom and denom metadata groups
//                String denomMetadata = null;
//                if (parameterMap.get(denominatorParam)!=null && parameterMap.get(denominatorParam).get(id)!=null) {
//                    denomMetadata = parameterMap.get(denominatorParam).get(id).getMetadataGroup();
//                }
//                String numeratorMetadata = null;
//                if (parameterMap.get(nominatorParam)!=null && parameterMap.get(nominatorParam).get(id)!=null) {
//                    numeratorMetadata = parameterMap.get(nominatorParam).get(id).getMetadataGroup();
//                }
//
//                String metadataGroup = "";
//                if (denomMetadata != null || numeratorMetadata != null) {
//                    metadataGroup = StringUtils.join(Arrays.asList(denomMetadata, numeratorMetadata), "_");
//                }
//
//                // Get metadata string as combined nom and denom metadata strings
//                String denomMetadataCombined = null;
//                if (parameterMap.get(denominatorParam)!=null && parameterMap.get(denominatorParam).get(id)!=null){
//                    denomMetadataCombined = parameterMap.get(denominatorParam).get(id).getMetadataCombined();
//                }
//
//                String numeratorMetadataCombined = null;
//                if(parameterMap.get(nominatorParam)!=null && parameterMap.get(nominatorParam).get(id)!=null){
//                    numeratorMetadataCombined = parameterMap.get(nominatorParam).get(id).getMetadataCombined();
//                }
//
//                String metadata = "";
//                if (denomMetadataCombined != null || numeratorMetadataCombined != null) {
//                    metadata = StringUtils.join(Arrays.asList(denomMetadataCombined, numeratorMetadataCombined), "_");
//                }
//
//                currentExperiment.setMetadataGroup(DigestUtils.md5Hex(metadataGroup));
//                currentExperiment.setMetadataCombined(metadata);

                // Use the same metadata as the other parameters in this procedure
                currentExperiment.setMetadataCombined(dto.getMetadataCombined());
                currentExperiment.setMetadataGroup(dto.getMetadataGroup());


                // sum_of_increments(ESLIM_010_001_001)/number_of_increments(ESLIM_010_001_001)
                double nom = 0;
                for (int k = 0; k < dto.getIncrementValues().size(); k++){
                    if (dto.getDiscreteValues().get(k) > -6 && dto.getDiscreteValues().get(k) < 16)
                        nom += dto.getIncrementValues().get(k);
                }
                nom /= dto.getIncrementValues().size();

                double denom = 0;
                for (int k = 0; k < denominators.get(id).getIncrementValues().size(); k++){
                    if (denominators.get(id).getDiscreteValues().get(k) > -6 && denominators.get(id).getDiscreteValues().get(k) < 16)
                        denom += denominators.get(id).getIncrementValues().get(k);
                }
                denom /= denominators.get(id).getIncrementValues().size();

                if (denom > 0){
                    experimentRepository.save(currentExperiment);
                    Double dataPoint = nom/denom;
                    //createTimeSeriesObservationWithOriginalDate
                    Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);
                    observationRepository.save(observation);
                }
            }
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }

    // ++++ IMPC_IPG_011_001	IMPC_IPG_002_001 15 increment_value IMPC_IPG_002_001 0 increment_value -
    private int IMPC_IPG_011_001()
            throws SQLException{

        String parameterToCreate = "IMPC_IPG_011_001";
        String paramToUse = "IMPC_IPG_002_001";
        deleteObservationsForParameterId(parameterToCreate);

        Map<String, ObservationDTO> resultAt_t0 = getResultsAtIncrement(paramToUse, 0);
        Map<String, ObservationDTO> resultAt_t15 = getResultsAtIncrement(paramToUse, 15);

        Set <String> commonIds = resultAt_t0.keySet();
        commonIds.retainAll(resultAt_t15.keySet());

        int i = 0;
        for (String id :  commonIds){
            ObservationDTO dto = resultAt_t0.get(id);
            Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

            // then depending on the type, create the relevant information
            Datasource datasource = datasources.get(dto.getExternalDbId());
            Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);

            experimentRepository.save(currentExperiment);

            Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, "" + (resultAt_t15.get(id).getDataPoint() - dto.getDataPoint()), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

            observationRepository.save(observation);
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);

        return i;

    }


    /**
     * Calculates and stores all results for parameter IMPC_VIA_032_001
     * 	total pups homozygous | total pups | Breeding Strategy {HetXHet 0.25, HetXHom 0.5, HomXHet 0.5}
     * 	IMPC_VIA_032_001 = IMPC_VIA_006_001 IMPC_VIA_003_001 IMPC_VIA_031_001 get_binomial_distribution_p_value
     *
     * @return the number of results stored
     */
    public int IMPC_VIA_032_001()
            throws SQLException{

        String parameterToCreate = "IMPC_VIA_032_001";
        // "IMPC_VIA_006_001"; // Total homozygous pups
        // "IMPC_VIA_003_001"; // Total pups
        // "IMPC_VIA_031_001"; // Breeding Strategy

//		Map <String, Double> probabilityMap = new HashMap<>(); // prob to have hom
//		probabilityMap.put("HetXHet", 0.25);
//		probabilityMap.put("HetXHom", 0.5);
//		probabilityMap.put("HomXHet", 0.5);

        deleteObservationsForParameterId(parameterToCreate);

        int i = 0;


        // Get all possible colonies
        List<ColonyDTO> colonyIds = getColonyIdsForIMPC_VIA_032_001();

        // for each colony that has all the required parameters present
        for (ColonyDTO colony : colonyIds) {

            // determine pvalue
            Float pValue = getIMPC_VIA_032_001(colony.colonyId, colony.experimentId);

            // Save an observation
            ObservationDTO dto = getObservationDTOForColony(colony.colonyId, colony.experimentId);
            Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

            dto.setDataPoint(pValue);


            Datasource datasource = datasources.get(dto.getExternalDbId());

            Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);
            currentExperiment.setColonyId(colony.colonyId);
            currentExperiment.setModel(biologicalModelRepository.findById(colony.biologicalModelId).orElse(null));

            if(currentExperiment.getModel()==null) {
                logger.warn("Skipping loading IMPC_VIA_032_001 for colony {} missing biological model", colony.colonyId);
                continue;
            }

            experimentRepository.save(currentExperiment);

            // create derived parameter record
            Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, pValue.toString(), param, null, datasource, currentExperiment, null);
            observationRepository.save(observation);

        }

        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;

    }

    // IMPC_EYE_092_001,"IMPC_EYE_020_001 IMPC_EYE_021_001 IMPC_EYE_022_001 if_abnormal"
    /**
     * Create parameter IMPC_EYE_092_001 if any of the 3 is abnormal or contains abnormal int he name.
     *
     * @return # of parameters added
     */
    private int IMPC_EYE_092_001() throws SQLException {

        String parameterToCreate = "IMPC_EYE_092_001";
        Map<String, ObservationDTO> IMPC_EYE_020_001 = getCategoricalResultsMap("IMPC_EYE_020_001");
        Map<String, ObservationDTO> IMPC_EYE_021_001 = getCategoricalResultsMap("IMPC_EYE_021_001");
        Map<String, ObservationDTO> IMPC_EYE_022_001 = getCategoricalResultsMap("IMPC_EYE_022_001");

        Set<String> allIds = new HashSet<>();
        allIds.addAll(IMPC_EYE_020_001.keySet());
        allIds.addAll(IMPC_EYE_021_001.keySet());
        allIds.addAll(IMPC_EYE_022_001.keySet());

        int i = 0;

        deleteObservationsForParameterId(parameterToCreate);

        logger.info("Size of dataset to processes: {}", allIds.size());

        for (String id : allIds){

            ObservationDTO dto = null;

            dto = isAbnormal(dto,  IMPC_EYE_020_001.get(id));
            dto = isAbnormal(dto,  IMPC_EYE_021_001.get(id));
            dto = isAbnormal(dto,  IMPC_EYE_022_001.get(id));

            if (dto != null) {
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());


                Datasource datasource = datasources.get(dto.getExternalDbId());

                Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);
                experimentRepository.save(currentExperiment);

                Observation observation = ObservationUtils.createSimpleObservation(ObservationType.categorical, dto.getCategory(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);
                observationRepository.save(observation);
            } else {
                logger.info("Not processing data {} for id {}", dto.getCategory(), id);
            }

        }

        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);

        return i;
    }


    private ObservationDTO isAbnormal(ObservationDTO currentDTO, ObservationDTO newDTO) {

        // If the current DTO is null, use the passed in value
        if ( currentDTO == null ) {
            if ( newDTO != null && newDTO.getCategory().contains("abnormal")) {
                newDTO.setCategory("abnormal");
            }
            return newDTO;
        }

        // If We already have an abnormal hit, keep it
        if ( currentDTO != null && currentDTO.getCategory().contains("abnormal")) {
            return  currentDTO;
        }

        // If there is a new parameter and it's value is abnormal, recode and return
        if ( newDTO != null && newDTO.getCategory().contains("abnormal")) {
            newDTO.setCategory("abnormal");
            return newDTO;
        } else if ( newDTO != null && newDTO.getCategory().contains("no data for both eyes")) {
            newDTO.setCategory("no data");
            // Return the previous value if there was no data
            return currentDTO;
        } else if (newDTO != null) {
            // Otherwise, return the new value with a normal category since it wasn't abnormal or no data
            newDTO.setCategory("normal");
            return newDTO;
        }

        // Return the previous value if there was no new value
        return currentDTO;
    }


    private int copyIncrementValueAt(String parameterToCreate, String parameterToCopyFrom, float increment)
            throws SQLException{

        deleteObservationsForParameterId(parameterToCreate);
        List<ObservationDTO> res = new ArrayList<> (getResultsAtIncrement(parameterToCopyFrom, increment).values());
        int i = 0;
        for (ObservationDTO dto: res){
            Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

            // then depending on the type, create the relevant information
            Datasource datasource = datasources.get(dto.getExternalDbId());
            Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);

            experimentRepository.save(currentExperiment);

            Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dto.getDataPoint().toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

            observationRepository.save(observation);
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }

    private int copyParameter (String parameterToCreate, String parameterToCopy)
            throws SQLException{

        deleteObservationsForParameterId(parameterToCreate);
        List<ObservationDTO> res = getResultsByParameter(parameterToCopy);
        int i = 0;
        for (ObservationDTO dto: res){
            Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

            // then depending on the type, create the relevant information
            Datasource datasource = datasources.get(dto.getExternalDbId());
            Procedure proc = getProcedureFromObservation(param, dto);

            if (proc == null) {
                logger.warn("Skipping. Procedure is NULL for result- specimen ID: " + dto.getAnimalId() + ", parameter: "+parameterToCreate);
                continue;
            }

            Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, proc, true);

            experimentRepository.save(currentExperiment);

            Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dto.getDataPoint().toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

            observationRepository.save(observation);
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }

    // 100x((ESLIM_011_001_006-ESLIM_011_001_007)/ESLIM_011_001_006)
    // minuend − subtrahend
    private int copyPercentageOfDifference(String parameterToCreate, String minuend, String subtrahend, String divisorParameter)
            throws SQLException{

        deleteObservationsForParameterId(parameterToCreate);
        int i = 0;
        ArrayList<String> paramsOfInterest = new ArrayList<>();
        paramsOfInterest.add(minuend);
        paramsOfInterest.add(subtrahend);
        paramsOfInterest.add(divisorParameter);

        Map<String, Map<String, ObservationDTO>> parameterMap = new HashMap<>();
        Set<String> animalIds =  getResultsIntersectionByParameter(paramsOfInterest, parameterMap);

        for (String id: animalIds){
            try {
                if (parameterMap.get(divisorParameter).get(id).getDataPoint() != 0) { // denominator
                    ObservationDTO dto = parameterMap.get(minuend).get(id);
                    Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

                    Datasource datasource = datasources.get(dto.getExternalDbId());
                    Experiment currentExperiment = createNewExperiment(dto, "derived_" + parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);
//                    String metadataGroup = parameterMap.get(minuend).get(id).getMetadataGroup() + "_" +
//                            parameterMap.get(subtrahend).get(id).getMetadataGroup() + "_" +
//                            parameterMap.get(divisorParameter).get(id).getMetadataGroup();
//                    String metadata = parameterMap.get(minuend).get(id).getMetadataCombined() + "_" +
//                            parameterMap.get(subtrahend).get(id).getMetadataCombined()  + "_" +
//                            parameterMap.get(divisorParameter).get(id).getMetadataCombined();
//                    currentExperiment.setMetadataCombined(metadata);
//                    currentExperiment.setMetadataGroup(DigestUtils.md5Hex(metadataGroup));

                    // Use the same metadata as the other parameters in this procedure
                    currentExperiment.setMetadataCombined(dto.getMetadataCombined());
                    currentExperiment.setMetadataGroup(dto.getMetadataGroup());


                    experimentRepository.save(currentExperiment);

                    Float dataPoint = (dto.getDataPoint() - parameterMap.get(subtrahend).get(id).getDataPoint()) / parameterMap.get(divisorParameter).get(id).getDataPoint() * 100;
                    Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

                    observationRepository.save(observation);
                }
            } catch (Exception e) {

                System.out.println("animal id: " + id);
                System.out.println("min " + parameterMap.get(minuend).get(id));
                System.out.println("Sub " + parameterMap.get(subtrahend).get(id));
                System.out.println("divisor" + parameterMap.get(divisorParameter).get(id));

                String errorMsg = String.format("Error while trying to calculate %s for animal id %s, minuend: %s, subtrahend: %s, divisor: %s", parameterToCreate, id, minuend, subtrahend, divisorParameter);
                logger.error(errorMsg, e);

            }
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }

    private int copyPercentageOf(String parameterToCreate, String numeratorParameter, String divisorParameter)
            throws SQLException{

        deleteObservationsForParameterId(parameterToCreate);
        int i = 0;
        ArrayList<String> paramsOfInterest = new ArrayList<>();
        paramsOfInterest.add(numeratorParameter);
        paramsOfInterest.add(divisorParameter);

        Map<String, Map<String, ObservationDTO>> parameterMap = new HashMap<>();
        Set<String> animalIds =  getResultsIntersectionByParameter(paramsOfInterest, parameterMap);

        for (String id: animalIds){
            if (parameterMap.get(divisorParameter).get(id).getDataPoint() != 0){ // denominator
                ObservationDTO dto = parameterMap.get(numeratorParameter).get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());
                Datasource datasource = datasources.get(dto.getExternalDbId());
                Experiment currentExperiment = createNewExperiment(dto, "derived_" + parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);
//                String metadataGroup = parameterMap.get(numeratorParameter).get(id).getMetadataGroup() + "_" +
//                        parameterMap.get(divisorParameter).get(id).getMetadataGroup();
//                String metadata = parameterMap.get(numeratorParameter).get(id).getMetadataCombined() + "_" +
//                        parameterMap.get(divisorParameter).get(id).getMetadataCombined() ;
//                currentExperiment.setMetadataCombined(metadata);
//                currentExperiment.setMetadataGroup(DigestUtils.md5Hex(metadataGroup));

                // Use the same metadata as the other parameters in this procedure
                currentExperiment.setMetadataCombined(dto.getMetadataCombined());
                currentExperiment.setMetadataGroup(dto.getMetadataGroup());

                experimentRepository.save(currentExperiment);

                Float dataPoint = dto.getDataPoint()/parameterMap.get(divisorParameter).get(id).getDataPoint()*100;
                Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

                observationRepository.save(observation);
            }
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }


    /**
     * Create a percentage based off other parameters
     *
     * @param parameterToCreate the parameter to create
     * @param numerator the number on top
     * @param denominator the number on bottom
     * @return the number of parameters created
     */
    private int calculateLineLevelPercent(String parameterToCreate, String numerator, String denominator)
            throws SQLException{

        Long initialTime = System.currentTimeMillis();
        deleteObservationsForParameterId(parameterToCreate);

        Long time = System.currentTimeMillis();
        int i = 0;

        System.out.println("getting parameters took " + (System.currentTimeMillis() - time));


        ArrayList<String> paramsOfInterest = new ArrayList<>();

        paramsOfInterest.add(numerator);
        paramsOfInterest.add(denominator);
        time = System.currentTimeMillis();

        Map<String, Map<String, ObservationDTO>> lineLevelParameterMap = new HashMap<>();
        Set<String> colonyIds =  getLineLevelResultsIntersectionByParameter(paramsOfInterest, lineLevelParameterMap);

        System.out.println("Intersection took " + (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        System.out.println("# colony ids : " + colonyIds.size());
        for (String id: colonyIds){
            if (lineLevelParameterMap.get(denominator) != null &&
                    lineLevelParameterMap.get(denominator).get(id) != null &&
                    lineLevelParameterMap.get(denominator).get(id).getDataPoint() != 0){ // denominator

                ObservationDTO dto = lineLevelParameterMap.get(numerator).get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

                Datasource datasource = datasources.get(dto.getExternalDbId());
                Experiment currentExperiment = createNewExperiment(dto, "derived_" + parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);
//                String metadataGroup = lineLevelParameterMap.get(numerator).get(id).getMetadataGroup() + "_" +
//                        lineLevelParameterMap.get(denominator).get(id).getMetadataGroup();
//                String metadata = lineLevelParameterMap.get(numerator).get(id).getMetadataCombined() + "_" +
//                        lineLevelParameterMap.get(denominator).get(id).getMetadataCombined() ;
//                currentExperiment.setMetadataCombined(metadata);
//                currentExperiment.setMetadataGroup(DigestUtils.md5Hex(metadataGroup));

                // Use the same metadata as the other parameters in this procedure
                currentExperiment.setMetadataCombined(dto.getMetadataCombined());
                currentExperiment.setMetadataGroup(dto.getMetadataGroup());

                currentExperiment.setColonyId(dto.getColony());
                currentExperiment.setModel(biologicalModelRepository.findById(dto.getBiologicalModelId()).orElse(null));

                if(currentExperiment.getModel()==null) {
                    logger.warn("Skipping loading {} for colony {} missing biological model", parameterToCreate, id);
                    continue;
                }

                experimentRepository.save(currentExperiment);

                Float dataPoint = dto.getDataPoint()/lineLevelParameterMap.get(denominator).get(id).getDataPoint();
                Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

                observationRepository.save(observation);

            }
        }
        System.out.println("for loop took " + (System.currentTimeMillis() - time));
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate + " in total took " + (System.currentTimeMillis() - initialTime) + "\n");
        return i;
    }

    private int copyMultiplicationResult(String parameterToCreate, String firstParam, String secondParam)
            throws SQLException{

        deleteObservationsForParameterId(parameterToCreate);
        int i = 0;
        ArrayList<String> paramsOfInterest = new ArrayList<>();
        paramsOfInterest.add(firstParam);
        paramsOfInterest.add(secondParam);

        Map<String, Map<String, ObservationDTO>> parameterMap = new HashMap<>();
        Set<String> animalIds =  getResultsIntersectionByParameter(paramsOfInterest, parameterMap);

        for (String id: animalIds){
            if (parameterMap.get(secondParam) != null &&
                    parameterMap.get(secondParam).get(id) != null &&
                    parameterMap.get(secondParam).get(id).getDataPoint() != 0){ // denominator
                ObservationDTO dto = parameterMap.get(firstParam).get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

                Datasource datasource = datasources.get(dto.getExternalDbId());
                Experiment currentExperiment = createNewExperiment(dto, "derived_" + parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);
//                String metadataGroup = parameterMap.get(firstParam).get(id).getMetadataGroup() + "_" +
//                        parameterMap.get(secondParam).get(id).getMetadataGroup();
//                String metadata = parameterMap.get(firstParam).get(id).getMetadataCombined() + "_" +
//                        parameterMap.get(secondParam).get(id).getMetadataCombined() ;
//                currentExperiment.setMetadataCombined(metadata);
//                currentExperiment.setMetadataGroup(DigestUtils.md5Hex(metadataGroup));

                // Use the same metadata as the other parameters in this procedure
                currentExperiment.setMetadataCombined(dto.getMetadataCombined());
                currentExperiment.setMetadataGroup(dto.getMetadataGroup());

                experimentRepository.save(currentExperiment);

                Float dataPoint = dto.getDataPoint()*parameterMap.get(secondParam).get(id).getDataPoint();
                Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

                observationRepository.save(observation);

            }
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }

    private int copyMultiplicationOfDivision(String parameterToCreate, String numeratorParameter, String divisorParameter, Integer multiplicator)
            throws SQLException{

        deleteObservationsForParameterId(parameterToCreate);

        int i = 0;
        ArrayList<String> paramsOfInterest = new ArrayList<>();
        paramsOfInterest.add(numeratorParameter);
        paramsOfInterest.add(divisorParameter);

        Map<String, Map<String, ObservationDTO>> parameterMap = new HashMap<>();
        Set<String> animalIds =  getResultsIntersectionByParameter(paramsOfInterest, parameterMap);

        for (String id: animalIds){
            if (parameterMap.get(divisorParameter) != null &&
                    parameterMap.get(divisorParameter).get(id) != null &&
                    parameterMap.get(divisorParameter).get(id).getDataPoint() != 0){ // denominator

                ObservationDTO dto = parameterMap.get(numeratorParameter).get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

                Datasource datasource = datasources.get(dto.getExternalDbId());
                Experiment currentExperiment = createNewExperiment(dto, "derived_" + parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);

                // Use the same metadata as the other parameters in this procedure
                currentExperiment.setMetadataCombined(dto.getMetadataCombined());
                currentExperiment.setMetadataGroup(dto.getMetadataGroup());

                experimentRepository.save(currentExperiment);

                Float dataPoint = multiplicator * dto.getDataPoint() / parameterMap.get(divisorParameter).get(id).getDataPoint();
                Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

                observationRepository.save(observation);

            }
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }

    private int copyMultiplicationOfDivision(String parameterToCreate, String numeratorParameter, String divisorParameter, String multiplicator)
            throws SQLException{

        deleteObservationsForParameterId(parameterToCreate);

        int i = 0;
        ArrayList<String> paramsOfInterest = new ArrayList<>();
        paramsOfInterest.add(numeratorParameter);
        paramsOfInterest.add(divisorParameter);
        paramsOfInterest.add(multiplicator);

        Map<String, Map<String, ObservationDTO>> parameterMap = new HashMap<>();
        Set<String> animalIds =  getResultsIntersectionByParameter(paramsOfInterest, parameterMap);

        for (String id: animalIds){
            if (parameterMap.get(divisorParameter) != null &&
                    parameterMap.get(divisorParameter).get(id) != null &&
                    parameterMap.get(divisorParameter).get(id).getDataPoint() != 0){ // denominator

                ObservationDTO dto = parameterMap.get(numeratorParameter).get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());
                Datasource datasource = datasources.get(dto.getExternalDbId());
                Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);
//                String metadataGroup = parameterMap.get(divisorParameter).get(id).getMetadataGroup() + "_" +
//                        parameterMap.get(numeratorParameter).get(id).getMetadataGroup() + "_" +
//                        parameterMap.get(multiplicator).get(id).getMetadataGroup();
//                String metadata = parameterMap.get(divisorParameter).get(id).getMetadataCombined() + "_" +
//                        parameterMap.get(numeratorParameter).get(id).getMetadataCombined() + "_" +
//                        parameterMap.get(multiplicator).get(id).getMetadataCombined();
//                currentExperiment.setMetadataCombined(metadata);
//                currentExperiment.setMetadataGroup(DigestUtils.md5Hex(metadataGroup));

                // Use the same metadata as the other parameters in this procedure
                currentExperiment.setMetadataCombined(dto.getMetadataCombined());
                currentExperiment.setMetadataGroup(dto.getMetadataGroup());


                experimentRepository.save(currentExperiment);

                Float multiplicatorDataPoint = parameterMap.get(multiplicator).get(id).getDataPoint();
                Float dataPoint = multiplicatorDataPoint * dto.getDataPoint() / parameterMap.get(divisorParameter).get(id).getDataPoint();
                Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

                observationRepository.save(observation);

            }
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }

    private int copyDivisionResult(String parameterToCreate, String numeratorParameter, String divisorParameter)
            throws SQLException{

        deleteObservationsForParameterId(parameterToCreate);
        int i = 0;
        ArrayList<String> paramsOfInterest = new ArrayList<>();
        paramsOfInterest.add(numeratorParameter);
        paramsOfInterest.add(divisorParameter);

        Map<String, Map<String, ObservationDTO>> parameterMap = new HashMap<>();
        Set<String> animalIds =  getResultsIntersectionByParameter(paramsOfInterest,parameterMap);

        for (String id: animalIds){
            if (parameterMap.get(divisorParameter) != null &&
                    parameterMap.get(divisorParameter).get(id) != null &&
                    parameterMap.get(divisorParameter).get(id).getDataPoint() != 0){ // denominator

                try {
                    ObservationDTO dto = parameterMap.get(numeratorParameter).get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

                    // Filter out calculating derived parameter for HRWL_OWT procedures
                    if (dto.getProcedureStableId().startsWith("HRWL_OWT")) {
                        continue;
                    }

                    Procedure proc = getProcedureFromObservation(param, dto);
                    Datasource datasource = datasources.get(dto.getExternalDbId());
                    Experiment currentExperiment = createNewExperiment(dto, "derived_" + parameterToCreate + "_" + i++, proc, true);

//                    String metadataGroup = dto.getMetadataGroup() + "_" +
//                            parameterMap.get(divisorParameter).get(id).getMetadataGroup();
//                    String metadata = dto.getMetadataCombined() + "_" +
//                            parameterMap.get(divisorParameter).get(id).getMetadataCombined();
//                    currentExperiment.setMetadataCombined(metadata);
//                    currentExperiment.setMetadataGroup(DigestUtils.md5Hex(metadataGroup));

                    // Use the same metadata as the other parameters in this procedure
                    currentExperiment.setMetadataCombined(dto.getMetadataCombined());
                    currentExperiment.setMetadataGroup(dto.getMetadataGroup());

                    experimentRepository.save(currentExperiment);

                    Float dataPoint = dto.getDataPoint() / parameterMap.get(divisorParameter).get(id).getDataPoint();
                    Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);
                    observationRepository.save(observation);
                    
                } catch (Exception e) {

                    Map<String, ObservationDTO> m1 = parameterMap.get(numeratorParameter);
                    Map<String, ObservationDTO> m2 = parameterMap.get(divisorParameter);
                    Float n = null;
                    Float d = null;
                    if (m1!=null && m1.get(id)!=null) n = m1.get(id).getDataPoint();
                    if (m2!=null && m2.get(id)!=null) d = m2.get(id).getDataPoint();

                    String errorMsg = String.format("Error while trying to calculate %s for animal id %s, formula %s / %s (actual values %s / %s)", parameterToCreate, id, numeratorParameter, divisorParameter, n, d);
                    logger.error(errorMsg, e);
                    e.printStackTrace();

                }

            }
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }

    // ESLIM_011_001_705	100 x ( (ESLIM_011_001_006 - ( (ESLIM_011_001_007 + ESLIM_011_001_008 + ESLIM_011_001_009 + ESLIM_011_001_010 ) / 4 ) ) / ESLIM_011_001_006 )
    private int ESLIM_011_001_705()
            throws SQLException{

        String parameterToCreate = "ESLIM_011_001_705";
        deleteObservationsForParameterId(parameterToCreate);
        int i = 0;
        ArrayList<String> paramsOfInterest = new ArrayList<>();
        paramsOfInterest.add("ESLIM_011_001_006");
        paramsOfInterest.add("ESLIM_011_001_007");
        paramsOfInterest.add("ESLIM_011_001_008");
        paramsOfInterest.add("ESLIM_011_001_009");
        paramsOfInterest.add("ESLIM_011_001_010");

        Map<String, Map<String, ObservationDTO>> parameterMap = new HashMap<>();
        Set<String> animalIds =  getResultsIntersectionByParameter(paramsOfInterest, parameterMap);

        for (String id: animalIds){
            if (parameterMap.get("ESLIM_011_001_006").get(id).getDataPoint() != 0){ // denominator
                ObservationDTO dto = parameterMap.get("ESLIM_011_001_006").get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

                Datasource datasource = datasources.get(dto.getExternalDbId());
                Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);
//                String metadataGroup = parameterMap.get("ESLIM_011_001_006").get(id).getMetadataGroup() + "_" +
//                        parameterMap.get("ESLIM_011_001_007").get(id).getMetadataGroup() + "_" +
//                        parameterMap.get("ESLIM_011_001_008").get(id).getMetadataGroup() + "_" +
//                        parameterMap.get("ESLIM_011_001_009").get(id).getMetadataGroup() + "_" +
//                        parameterMap.get("ESLIM_011_001_010").get(id).getMetadataGroup() ;
//                String metadata = parameterMap.get("ESLIM_011_001_006").get(id).getMetadataCombined() + "_" +
//                        parameterMap.get("ESLIM_011_001_007").get(id).getMetadataCombined() + "_" +
//                        parameterMap.get("ESLIM_011_001_008").get(id).getMetadataCombined() + "_" +
//                        parameterMap.get("ESLIM_011_001_009").get(id).getMetadataCombined() + "_" +
//                        parameterMap.get("ESLIM_011_001_010").get(id).getMetadataCombined() ;
//                currentExperiment.setMetadataGroup(DigestUtils.md5Hex(metadataGroup));
//                currentExperiment.setMetadataCombined(metadata);

                // Use the same metadata as the other parameters in this procedure
                currentExperiment.setMetadataCombined(dto.getMetadataCombined());
                currentExperiment.setMetadataGroup(dto.getMetadataGroup());

                experimentRepository.save(currentExperiment);

                // (ESLIM_011_001_007 + ESLIM_011_001_008 + ESLIM_011_001_009 + ESLIM_011_001_010 ) / 4
                Float dataPoint = (parameterMap.get("ESLIM_011_001_007").get(id).getDataPoint() +
                        parameterMap.get("ESLIM_011_001_008").get(id).getDataPoint() +
                        parameterMap.get("ESLIM_011_001_009").get(id).getDataPoint() +
                        parameterMap.get("ESLIM_011_001_010").get(id).getDataPoint()) / 4
                        ;
                // (ESLIM_011_001_006 - ( (ESLIM_011_001_007 + ESLIM_011_001_008 + ESLIM_011_001_009 + ESLIM_011_001_010 ) / 4 ) )
                dataPoint = parameterMap.get("ESLIM_011_001_006").get(id).getDataPoint() - dataPoint;
                // 100 x ( (ESLIM_011_001_006 - ( (ESLIM_011_001_007 + ESLIM_011_001_008 + ESLIM_011_001_009 + ESLIM_011_001_010 ) / 4 ) ) / ESLIM_011_001_006 )
                dataPoint = 100 * dataPoint / parameterMap.get("ESLIM_011_001_006").get(id).getDataPoint();
                Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

                observationRepository.save(observation);
            }
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }


    //		ESLIM_004_001_701	area_under_curve(ESLIM_004_001_002)
    private int copyAUC(String parameterToCreate, String parameterId)
            throws SQLException{

        int i = 0;
        deleteObservationsForParameterId(parameterToCreate);
        Map<String, ObservationDTO> increments = getIncrementalDataMapByParameter(parameterId);
        Set<String> animalIds =  increments.keySet();
        for (String id: animalIds){
            if (increments.get(id).getIncrementValues().size() != 0){ // denominator, here should actually always be > 0
                ObservationDTO dto = increments.get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());


                Double dataPoint = getAUC(dto.getIncrementValues(), dto.getDiscreteValues());
                if (dataPoint > 0){
                    Datasource datasource = datasources.get(dto.getExternalDbId());
                    Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);
                    experimentRepository.save(currentExperiment);

                    //createTimeSeriesObservationWithOriginalDate
                    Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(),
                            param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

                    observationRepository.save(observation);
                }
            }
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }

    private double getAUC(List<Double> values, List<Double> timePoints){
        // |A| + |B|
        double auc = 0;
        if (values.size() != 5 || timePoints.size() != 5)
            return -1;
        double baseline = values.get(0);
        for (int i = 0; i < values.size() - 1; i++){
            if (values.get(i) - baseline < 0 && values.get(i+1) - baseline > 0 || values.get(i) - baseline > 0 && values.get(i+1) - baseline < 0){
                // compute each triangle separately
                double a = Math.abs(values.get(i) - baseline);
                double b = Math.abs(values.get(i+1) - baseline);
                double k = timePoints.get(i+1) - timePoints.get(i);
                double y = b * k / (a + b);
                double x = k - y;
                auc += y * b / 2; // we know it has a 90 degree angle between y and b
                auc += a * x / 2;
            }
            else {
                double height = timePoints.get(i+1) - timePoints.get(i);
                auc += Math.abs((values.get(i) + values.get(i+1) - 2 * baseline) * height) / 2;
            }
        }
        return auc;
    }


    private int copyDivisionOfMeanOfIncrements(String parameterToCreate, String numerator, String divisor)
            throws SQLException{

        deleteObservationsForParameterId(parameterToCreate);
        int i = 0;
        ArrayList<String> listOfParameters = new ArrayList<>();
        listOfParameters.add(numerator);
        listOfParameters.add(divisor);

        Double div = (double) 0;
        Double num = (double) 0;

        Map<String, Map<String, ObservationDTO>> parameterMap = new HashMap<>();
        Set<String> animalPipelineList = getTimeseriesResultsIntersection(listOfParameters, parameterMap);

        for (String id: animalPipelineList){

            try {
                ObservationDTO dto = parameterMap.get(numerator).get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

                Datasource datasource = datasources.get(dto.getExternalDbId());
                Experiment currentExperiment = createNewExperiment(dto, "derived_" + parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);

                // Use the same metadata as the other parameters in this procedure
                currentExperiment.setMetadataCombined(dto.getMetadataCombined());
                currentExperiment.setMetadataGroup(dto.getMetadataGroup());


                // sum_of_increments/number_of_increments
                ArrayList<Double> incrementValues = dto.getIncrementValues();
                num = (double) 0;
                for (Double incrementValue : incrementValues) {
                    num += incrementValue;
                }
                num = num / (incrementValues.size());

                // sum_of_increments/number_of_increments
                incrementValues = parameterMap.get(divisor).get(id).getIncrementValues();
                div = (double) 0;
                for (Double incrementValue : incrementValues) {
                    div += incrementValue;
                }
                div = div / (incrementValues.size());

                if (div != 0){

                    experimentRepository.save(currentExperiment);
                    Double dataPoint = num / div;
                    //	createTimeSeriesObservationWithOriginalDate
                    Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);
                    observationRepository.save(observation);
                }

            }catch (Exception e) {

                String errorMsg = String.format("Error while trying to calculate %s for animal id %s numerator parameter: %s, denominator parameter: %s", parameterToCreate, id, numerator, divisor);
                logger.error(errorMsg, e);
                logger.error("div " + div);
                logger.error("num  " + num);
                logger.error("id: " + id);

            }
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }


    // ESLIM_010_001_701	sum_of_increments(ESLIM_010_001_001)/number_of_increments(ESLIM_010_001_001)
    private int copyMeanOfIncrements(String parameterToCreate, String parameterId)
            throws SQLException{

        deleteObservationsForParameterId(parameterToCreate);
        int i = 0;

        Map<String, ObservationDTO> increments = getIncrementalDataMapByParameter(parameterId);
        Set<String> animalIds =  increments.keySet();

        for (String id: animalIds){
            if (increments.get(id).getIncrementValues().size() != 0){ // denominator, here should actually always be > 0
                ObservationDTO dto = increments.get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

                Datasource datasource = datasources.get(dto.getExternalDbId());
                Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);

                experimentRepository.save(currentExperiment);

                // sum_of_increments(ESLIM_010_001_001)/number_of_increments(ESLIM_010_001_001)
                ArrayList<Double> incrementValues = dto.getIncrementValues();
                Double dataPoint = (double) 0;
                for (Double incrementValue : incrementValues) {
                    dataPoint += incrementValue;
                }
                dataPoint = dataPoint/(incrementValues.size());
                //createTimeSeriesObservationWithOriginalDate
                Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

                observationRepository.save(observation);
            }
        }
        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }


    private int copyDifferenceOfIncrements(String parameterToCreate, String parameterId, Double incrementForMinued, Double incrementForSubtrahend) throws SQLException {

        deleteObservationsForParameterId(parameterToCreate);
        int i = 0;
        Map<String, ObservationDTO> increments = getIncrementalDataMapByParameter(parameterId);
        for (String id:  increments.keySet()){

            ObservationDTO dto = increments.get(id);
            Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

            int minuedPosition = dto.getDiscreteValues().indexOf(incrementForMinued);
            int subtrahendPosition = dto.getDiscreteValues().indexOf(incrementForSubtrahend);

            if (minuedPosition >= 0 && subtrahendPosition>= 0) {
                Datasource datasource = datasources.get(dto.getExternalDbId());
                Experiment currentExperiment = createNewExperiment(dto, "derived_" + parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);
                experimentRepository.save(currentExperiment);
                Double dataPoint = dto.getIncrementValues().get(minuedPosition) - dto.getIncrementValues().get(subtrahendPosition);
                Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);
                observationRepository.save(observation);

            }
        }

        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }

    private int copySumOfIncrements(String parameterToCreate, String parameterId)
            throws SQLException{

        deleteObservationsForParameterId(parameterToCreate);
        int i = 0;
        Map<String, ObservationDTO> increments = getIncrementalDataMapByParameter(parameterId);
        Set<String> animalIds =  increments.keySet();
        for (String id: animalIds){
            ObservationDTO dto = increments.get(id);
            Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());


            Datasource datasource = datasources.get(dto.getExternalDbId());

            Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);

            experimentRepository.save(currentExperiment);

            // sum_of_increments(ESLIM_010_001_001)/number_of_increments(ESLIM_010_001_001)
            ArrayList<Double> incrementValues = dto.getIncrementValues();
            Double dataPoint = 0.0;
            for (Double incrementValue : incrementValues) {
                dataPoint += incrementValue;
            }
            //createTimeSeriesObservationWithOriginalDate
            Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

            observationRepository.save(observation);
        }

        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;
    }

    private Procedure getProcedureFromObservation(Parameter param, ObservationDTO dto) {
        Procedure proc = null;

        //
        // EUMODIC bodyweight procedure is always ESLIM_022_001
        //
        if (param.getStableId().contains("ESLIM_022")) {
            return procedureRepository.getByStableId("ESLIM_022_001");
        }

        //
        // IMPC bodyweight procedure is always IMPC_BWT_001
        //
        if (param.getStableId().contains("IMPC_BWT")) {
            return procedureRepository.getByStableId("IMPC_BWT_001");
        }

        for (Procedure p : param.getProcedures()) {
            if (p.getStableId().equals(dto.getProcedureStableId())) {
                proc = p;
                break;
            }
        }

        return proc;
    }


    // ESLIM_009_001_704	(sum_of_increments(ESLIM_009_001_002)/number_of_increments(ESLIM_009_001_002))/ESLIM_009_001_003
    private int copyDivisionOfSumOfIncrementsOverNumberOfIncrements(String parameterToCreate, String parameterId, String denominator)
            throws SQLException{

        deleteObservationsForParameterId(parameterToCreate);
        int i = 0;
        Map<String, ObservationDTO> increments = getIncrementalDataMapByParameter(parameterId);
        Map<String, ObservationDTO> denominators = getResultsMapByParameter(denominator);

        Set <String> intersection = increments.keySet();
        Set<String> newSet = denominators.keySet();
        intersection.retainAll(newSet);

        for (String id: intersection){
            if (increments.get(id).getIncrementValues().size() != 0 && denominators.get(id).getDataPoint() != 0){
                ObservationDTO dto = increments.get(id);
                Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, dto.getProcedureStableId(), dto.getPipelineStableId());

                Datasource datasource = datasources.get(dto.getExternalDbId());
                Experiment currentExperiment = createNewExperiment(dto, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, dto), true);
//                String metadataGroup = denominators.get(id).getMetadataGroup() + "_" +
//                        dto.getMetadataGroup();
//                String metadata = denominators.get(id).getMetadataCombined() + "   " +
//                        dto.getMetadataCombined();
//                currentExperiment.setMetadataGroup(DigestUtils.md5Hex(metadataGroup));
//                currentExperiment.setMetadataCombined(metadata);

                // Use the same metadata as the other parameters in this procedure
                currentExperiment.setMetadataCombined(dto.getMetadataCombined());
                currentExperiment.setMetadataGroup(dto.getMetadataGroup());

                experimentRepository.save(currentExperiment);

                // sum_of_increments(ESLIM_010_001_001)/number_of_increments(ESLIM_010_001_001)
                ArrayList<Double> incrementValues = dto.getIncrementValues();
                Double dataPoint = (double) 0;
                for (Double incrementValue : incrementValues) {
                    dataPoint += incrementValue;
                }
                dataPoint = dataPoint/(incrementValues.size());
                dataPoint = dataPoint/denominators.get(id).getDataPoint();
                //createTimeSeriesObservationWithOriginalDate
                Observation observation = ObservationUtils.createSimpleObservation(ObservationType.unidimensional, dataPoint.toString(), param, animals.get(dto.getAnimalId()), datasource, currentExperiment, null);

                observationRepository.save(observation);
            }
        }

        logger.info("Added " + i + " observations for " + parameterToCreate);
        System.out.println("Added " + i + " observations for " + parameterToCreate);
        return i;

    }


    protected int plotParametersAsTimeSeries(String parameterToCreate, List<String> paramsOfInterest)
            throws SQLException{

        int i = 0;
        deleteObservationsForParameterId(parameterToCreate);
        Map<String, Set<ObservationDTO>> res = new HashMap<> (); // <animalid, Set<obsDTO>>
        Map<String, String> metadata = new HashMap<>();

        Map<String, Map<String, Set<ObservationDTO>>> parameterMap = new HashMap<>();
        Set<String> animalIds =  getTimeSeriesResultsUnionByParameter(paramsOfInterest, parameterMap);

        logger.info("  " + parameterToCreate + " Finished Getting Ids");

        for (String id: animalIds){
            metadata.put(id, "");
            for (String p: paramsOfInterest){

                // If the parameter has an entry for this specimen
                if (parameterMap.get(p).containsKey(id)) {

                    // Loop through the set of observations to set the discrete timepoint and value
                    // as well as collect all the underlying metadata
                    for (ObservationDTO obsDTO : parameterMap.get(p).get(id)) {

                        // The specimens have no specific order in the animalIds set, so if we have not yet seen
                        // this specimen, add an entry for it in the res Map
                        if ( ! res.containsKey(id)) {
                            res.put(id, new HashSet<>());
                        }

                        obsDTO.addIncrementValue((Double) 0.0 + obsDTO.getDataPoint());
                        obsDTO.addDiscreteValue(getAgeInWeeks(obsDTO.getDateOfExperiment(), obsDTO.getDateOfBirth()));
                        res.get(id).add(obsDTO);

                        metadata.put(id, metadata.get(id) + ", " + p + ":" + obsDTO.getMetadataCombined());
                    }
                }
            }
        }

        logger.info( "  " + parameterToCreate + " Finished parsing data and metadata, contains " + res.keySet().size() + " ids");
        for (String id: res.keySet()){
            int loop = 0;
            logger.info("  Every line loop counter:" + (loop++) + " id" + id);

            ObservationDTO exemplarObservation = res.get(id).stream().findFirst().orElse(null);
            logger.info("  Every line loop counter:" + (loop++) + " id" + id);
            if (exemplarObservation == null) {
                continue;
            }
            logger.info("  Every line loop counter:" + (loop++) + " id" + id);
//            Parameter param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, exemplarObservation.getProcedureStableId(), exemplarObservation.getPipelineStableId());
            String procedureId = exemplarObservation.getProcedureStableId();
            logger.info("  Every line loop counter:" + (loop++) + " id" + id);
            String pipelineId = exemplarObservation.getPipelineStableId();
            logger.info("  Every line loop counter:" + (loop++) + " id" + id);
            if (parameterToCreate.contains("BWT_008_001")) {
                procedureId = "IMPC_BWT_001";
            }
            logger.info("  Every line loop counter:" + loop);
            Parameter param;
            try {
                param = parameterRepository.getFirstByStableIdAndProcedures(parameterToCreate, procedureId, pipelineId);
            } catch (Exception e) {
                logger.error("Error when calling parameterRepository", e);
                return 0;
            }
                logger.info("  " + parameterToCreate + " Found parameter " + param);

            logger.info("  Every line loop counter:" + (loop++) + " id" + id);

            if (param==null) {
                logger.warn("Cannot find parameter for parameter: %s, procedure: %s, pipeline: %s", param, procedureId, pipelineId);
            }
            logger.info("  Every line loop counter:" + (loop++) + " id" + id);


            Datasource datasource = datasources.get(exemplarObservation.getExternalDbId());
                logger.info("  " + parameterToCreate + " Found datasource " + datasource);

            logger.info("  Every line loop counter:" + (loop++) + " id" + id);

            Experiment currentExperiment = createNewExperiment(exemplarObservation, "derived_" +parameterToCreate + "_" + i++, getProcedureFromObservation(param, exemplarObservation), false);
            currentExperiment.setMetadataCombined(metadata.get(id));
            logger.info("  Every line loop counter:" + (loop++) + " id" + id);

            // No metadata split for body weight curves
            currentExperiment.setMetadataGroup(DigestUtils.md5Hex(""));
                logger.info("  " + parameterToCreate + " setting up experiment " + currentExperiment.getExternalId());
            logger.info("  Every line loop counter:" + (loop++) + " id" + id);


            logger.info("  " + parameterToCreate + " Saving experiment " + currentExperiment.getExternalId());
            experimentRepository.save(currentExperiment);
            logger.info("  " + parameterToCreate + " Saved experiment " + currentExperiment.getExternalId());
            logger.info("  Every line loop counter:" + (loop++) + " id" + id);

            logger.info("    " + parameterToCreate + " Saving " + res.get(id).size() + " observations");
            for (ObservationDTO dto : res.get(id)) {
                for (int k = 0; k < dto.getDiscreteValues().size(); k++) {
                    Observation observation = ObservationUtils.createTimeSeriesObservationWithOriginalDate(ObservationType.time_series,
                            dto.getIncrementValues().get(k).toString(), // dataPoint
                            dto.getDiscreteValues().get(k).toString(), // discretePoint
                            dto.getDateOfExperiment().toString(), // timePoint
                            "weeks", //unit
                            param,
                            animals.get(dto.getAnimalId()),
                            datasource,
                            currentExperiment, null);
                    i++;
                    observationRepository.save(observation);
                }
            }
        }

        logger.info("Added " + i + " observations for " + parameterToCreate);
        return i;
    }


    private double getAgeInWeeks(Date current, Date birth){
        long weeks = (current.getTime() - birth.getTime()) / (24 * 60 * 60 * 1000 * 7);
        return  (double) weeks;
    }


    /**
     * Get a list of colony ids that have all the information required for calculating
     * the derived parameter
     *
     *  IMPC_VIA_006_001   // Total homozygous pups
     *  IMPC_VIA_003_001   // Total pups
     *
     * @return a list of colony ids to calculate the IMPC_VIA_032_001 parameter
     */
    private List<ColonyDTO> getColonyIdsForIMPC_VIA_032_001()
            throws SQLException {

        List<ColonyDTO> res = new ArrayList<>();

        String query = "SELECT DISTINCT colony_id, experiment_id, biological_model_id " +
                "FROM ( " +
                "  SELECT DISTINCT CONCAT_WS('_', e.colony_id, e.id) as colony_id_comb, e.colony_id, e.id as experiment_id, e.biological_model_id " +
                "  FROM experiment e " +
                "  INNER JOIN experiment_observation eo ON e.id=eo.experiment_id " +
                "  INNER JOIN observation o ON o.id=eo.observation_id " +
                "  WHERE parameter_stable_id='IMPC_VIA_006_001') a " +
                "WHERE a.colony_id_comb IN ( " +
                "  SELECT DISTINCT CONCAT_WS('_', e.colony_id, e.id) as colony_id " +
                "  FROM experiment e " +
                "  INNER JOIN experiment_observation eo ON e.id=eo.experiment_id " +
                "  INNER JOIN observation o ON o.id=eo.observation_id " +
                "  WHERE parameter_stable_id ='IMPC_VIA_003_001')";


        logger.trace("Executing query: " + query);

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ColonyDTO dto = new ColonyDTO();
                dto.colonyId = resultSet.getString("colony_id");
                dto.experimentId = resultSet.getLong("experiment_id");
                dto.biologicalModelId = resultSet.getLong("biological_model_id");
                res.add(dto);
            }
        }

        return res;
    }


    private Float getIMPC_VIA_032_001(String colonyId, Long experimentId)
            throws SQLException {

        Double pValue = 1.0;


        String IMPC_VIA_006_001_query = "SELECT o.*, uo.data_point, e.metadata_group, e.metadata_combined " +
                "  FROM experiment e " +
                "  INNER JOIN experiment_observation eo ON e.id=eo.experiment_id " +
                "  INNER JOIN observation o ON o.id=eo.observation_id " +
                "  INNER JOIN unidimensional_observation uo ON o.id=uo.id " +
                "  WHERE parameter_stable_id='IMPC_VIA_006_001' AND e.id=? AND e.colony_id=?";

        String IMPC_VIA_003_001_query = "SELECT o.*, uo.data_point, e.metadata_group, e.metadata_combined " +
                "  FROM experiment e " +
                "  INNER JOIN experiment_observation eo ON e.id=eo.experiment_id " +
                "  INNER JOIN observation o ON o.id=eo.observation_id " +
                "  INNER JOIN unidimensional_observation uo ON o.id=uo.id " +
                "  WHERE parameter_stable_id='IMPC_VIA_003_001' AND e.id=? AND e.colony_id=?";

        String IMPC_VIA_031_001_query = "SELECT m.value, e.metadata_group, e.metadata_combined" +
                "  FROM experiment e " +
                "  INNER JOIN procedure_meta_data m ON e.id=m.experiment_id " +
                "  WHERE m.parameter_id='IMPC_VIA_031_001' AND e.id=? AND e.colony_id=?";

        Integer totalPups = null;
        Integer homPups = null;
        String breedingStrategy = null;

        logger.trace("Executing query: {} (eid: {}, cid: {})", IMPC_VIA_003_001_query, experimentId, colonyId);
        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(IMPC_VIA_003_001_query)) {

            statement.setLong(1, experimentId);
            statement.setString(2, colonyId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                totalPups = resultSet.getInt("data_point");
                break;
            }
        }

        logger.trace("Executing query: {} (eid: {}, cid: {})", IMPC_VIA_006_001_query, experimentId, colonyId);
        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(IMPC_VIA_006_001_query)) {

            statement.setLong(1, experimentId);
            statement.setString(2, colonyId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                homPups = resultSet.getInt("data_point");
                break;
            }
        }

        logger.trace("Executing query: {} (eid: {}, cid: {})", IMPC_VIA_031_001_query, experimentId, colonyId);
        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(IMPC_VIA_031_001_query)) {

            statement.setLong(1, experimentId);
            statement.setString(2, colonyId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                breedingStrategy = resultSet.getString("value");
                break;
            }
        }

        pValue = calculateBinomialPvalue(totalPups, homPups, breedingStrategy);

        // If there are NO homozygous pups AND there are more than 28 pups total
        // Score this as lethal https://www.mousephenotype.org/impress/protocol/154/7
        if (totalPups >= 28 && homPups < 1) {
            pValue = 0.0;
        }

        return pValue.floatValue();

    }

    private Double calculateBinomialPvalue(Integer totalPups, Integer homPups, String breedingStrategy) {

        if(totalPups==null || homPups==null || breedingStrategy == null) {
            return null;
        }

        Float baseProbability = 0.25f;

        // A HetXHet breeding strategy should normally produce a Punnett square like this:
        //
        //      WT     KO
        // WT | 25% | 25% |
        // KO | 25% | 25% |
        //
        // Which would produce 25% WT, 50% Het, and 25% Hom
        //
        // In the case where the breeding strategy is HomXHet (Mother=Hom, Father=Het),
        // or HetXHom (Mother=Het, Father=Hom), the square would be:
        //
        //      WT     KO
        // KO | 25% | 25% |
        // KO | 25% | 25% |
        //
        // Or 50% Het, 50% Hom

        if ( ! breedingStrategy.equals("HetXHet")) {
            baseProbability = 0.50f;
        }

        Double pValue;
        BinomialTest bt = new BinomialTest();
        pValue = bt.binomialTest(totalPups, homPups, baseProbability, org.apache.commons.math3.stat.inference.AlternativeHypothesis.LESS_THAN);
        return pValue;

    }


    private ObservationDTO getObservationDTOForColony(String colonyId, Long experimentId)
            throws SQLException {

        String query = "SELECT e.db_id, e.project_id, org.name, e.metadata_group, e.metadata_combined, e.pipeline_id, e.pipeline_stable_id, e.procedure_id, e.procedure_stable_id " +
                "  FROM experiment e " +
                "  INNER JOIN organisation org ON org.id=e.organisation_id " +
                "  WHERE e.id=? AND e.colony_id=?";

        logger.trace("Executing query: " + query);

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, experimentId);
            statement.setString(2, colonyId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                return fillLineLevelObsDTO(resultSet);
            }
        }

        return null;

    }


    private Map<String, ObservationDTO> getLineLevelResultsMapByParameter (String parameterId)
            throws SQLException {

        Map<String, ObservationDTO> res = new HashMap<>();

        String query = "SELECT DISTINCT e.id, e.project_id,  e.procedure_id, e.procedure_stable_id, e.db_id, e.colony_id, e.biological_model_id, bmstrain.strain_acc, uo.data_point, "
                + "org.name, e.date_of_experiment, e.metadata_group, e.metadata_combined, e.pipeline_stable_id, e.pipeline_id  " +
                "FROM observation o " +
                "INNER JOIN unidimensional_observation uo ON uo.id=o.id " +
                "INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
                "INNER JOIN experiment e ON e.id=eo.experiment_id " +
                "INNER JOIN organisation org ON e.organisation_id=org.id " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=e.biological_model_id " +
                "WHERE o.parameter_stable_id=?" +
                "AND o.missing=0";


        logger.trace("Executing query: " + query);
        logger.trace("With parameters parameterId="+parameterId);

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, parameterId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ObservationDTO obsDTO = fillLineLevelObsDTO(resultSet);
                obsDTO.setDataPoint(resultSet.getFloat("data_point"));
                obsDTO.setBiologicalModelId(resultSet.getLong("biological_model_id"));
                obsDTO.setColony(resultSet.getString("colony_id"));
                res.put(resultSet.getString("colony_id") + "#" + obsDTO.getPipelineId(), obsDTO);
            }
        }
        return res;
    }

    /**
     * Returns a list of colony ids with all associated parameters.
     * Fills lineLevelParameterMap if needed
     *
     * @param listOfParameters the list of parameters to intersect
     * @return list of colony IDs that have enough data to support the parameters passed in
     */
    public  Set<String> getLineLevelResultsIntersectionByParameter(List<String> listOfParameters, Map<String, Map<String, ObservationDTO>> lineLevelParameterMap)
            throws SQLException {

        Map<Integer,String> auxMap = new HashMap<>();
        ArrayList<Integer> sortedList = new ArrayList<>();

        for (String paramId : listOfParameters) {

            if ( ! lineLevelParameterMap.containsKey(paramId)) {
                lineLevelParameterMap.put(paramId, getLineLevelResultsMapByParameter(paramId));
            }

            int size = lineLevelParameterMap.get(paramId).size();
            while (auxMap.containsKey(size)) {
                size++;
            }

            auxMap.put(size, paramId);
            sortedList.add(size);
        }

        // sort sets by size
        Collections.sort(sortedList);

        // start with smallest set
        Set<String> resParamId = lineLevelParameterMap.get(auxMap.get(sortedList.get(0))).keySet();
        for (int i = 1; i < sortedList.size()-1; i++){
            Set<String> newSet = lineLevelParameterMap.get(auxMap.get(sortedList.get(i))).keySet();
            resParamId.retainAll(newSet);
        }

        return resParamId;
    }

    /**
     * Line level results do not have animal IDs and hence, no date of birth, or zygosity or
     * any other individual specimen data
     *
     * @param resultSet database result set to fill the observationDTO
     * @return populated observationDTO
     */
    private ObservationDTO fillLineLevelObsDTO(ResultSet resultSet)
            throws SQLException{

        ObservationDTO obsDTO = new ObservationDTO();

        obsDTO.setExternalDbId(resultSet.getLong("db_id"));
        obsDTO.setProjectId(resultSet.getLong("project_id"));
        obsDTO.setProductionCenter(resultSet.getString("name"));
        obsDTO.setMetadataGroup(resultSet.getString("metadata_group"));
        obsDTO.setMetadataCombined(resultSet.getString("metadata_combined"));
        obsDTO.setPipelineId(resultSet.getString("pipeline_id"));
        obsDTO.setPipelineStableId(resultSet.getString("pipeline_stable_id"));
        obsDTO.setProcedureId(resultSet.getString("procedure_id"));
        obsDTO.setProcedureStableId(resultSet.getString("procedure_stable_id"));

        return obsDTO;
    }



    private List<ObservationDTO> getResultsByParameter (String parameterId)
            throws SQLException {

        ArrayList<ObservationDTO> res = new ArrayList<> ();

        String query = "SELECT DISTINCT e.project_id, e.procedure_id, e.procedure_stable_id,  e.db_id, ls.colony_id, bmstrain.strain_acc, ls.zygosity, ls.sex, ls.date_of_birth, uo.data_point, bs.id, org.name, "
                + "e.date_of_experiment, e.metadata_group, e.metadata_combined, pipeline_stable_id, pipeline_id  "
                + "FROM observation o " +
                "INNER JOIN unidimensional_observation uo ON uo.id=o.id " +
                "INNER JOIN biological_sample bs ON bs.id=o.biological_sample_id " +
                "INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
                "INNER JOIN experiment e ON e.id=eo.experiment_id " +
                "INNER JOIN organisation org ON e.organisation_id=org.id " +
                "INNER JOIN live_sample ls ON ls.id=bs.id " +
                "INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id " +
                "WHERE o.parameter_stable_id=?" +
                "AND o.missing=0";


        logger.trace("Executing query: " + query);
        logger.trace("With parameters parameterId="+parameterId);

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, parameterId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ObservationDTO obsDTO = fillUnidimensionalObsDTO(resultSet);
                res.add(obsDTO);
            }
        }
        return res;
    }


    private  Map<String, ObservationDTO> getCategoricalResultsMap(String parameterId)
            throws SQLException {

        Map<String, ObservationDTO> res = new HashMap<> ();

        String query = "SELECT DISTINCT e.project_id, e.procedure_id, e.procedure_stable_id,  e.db_id, ls.colony_id, bmstrain.strain_acc, ls.zygosity, ls.sex, ls.date_of_birth, " +
                "bs.id, org.name, e.date_of_experiment, e.metadata_group, e.metadata_combined, pipeline_stable_id, pipeline_id, category " +
                "FROM observation o " +
                "INNER JOIN categorical_observation co ON co.id=o.id " +
                "INNER JOIN biological_sample bs ON bs.id=o.biological_sample_id " +
                "INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
                "INNER JOIN experiment e ON e.id=eo.experiment_id " +
                "INNER JOIN organisation org ON e.organisation_id=org.id " +
                "INNER JOIN live_sample ls ON ls.id=bs.id " +
                "INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id " +
                "WHERE o.parameter_stable_id=?" +
                "AND o.missing=0 AND observation_type='categorical'";

        logger.trace("Executing query: " + query);
        logger.trace("With parameters parameterId="+parameterId);

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, parameterId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ObservationDTO obsDTO = fillCategoricalObsDTO(resultSet);
                res.put(resultSet.getString("id") + "#" + resultSet.getString("pipeline_stable_id") , obsDTO);
            }
        }
        return res;
    }


    /**
     * Gets a Map of key: SpecimenId#PipelineId, value: Set of Unidimensional Observations for the parameter
     * identified by the parameterId argument
     *
     * @param parameterId the String stable ID of the parameter to find all observations e.g., IMPC_EYE_092_001
     * @return Map of SpecimenId#PipelineId to Set of ObservationDTOs
     * @throws SQLException when the database is not working correctly
     */
    private Map<String, Set<ObservationDTO>> getSetOfResultsMapByParameter (String parameterId)
            throws SQLException {

        String query = "SELECT DISTINCT e.project_id,  e.procedure_id, e.procedure_stable_id, e.db_id, ls.colony_id, "
                + "bmstrain.strain_acc, ls.zygosity, ls.sex, ls.date_of_birth, uo.data_point, bs.id, "
                + "org.name, e.date_of_experiment, e.metadata_group, e.metadata_combined, pipeline_stable_id, pipeline_id  " +
                "FROM observation o " +
                "INNER JOIN unidimensional_observation uo ON uo.id=o.id " +
                "INNER JOIN biological_sample bs ON bs.id=o.biological_sample_id " +
                "INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
                "INNER JOIN experiment e ON e.id=eo.experiment_id " +
                "INNER JOIN organisation org ON e.organisation_id=org.id " +
                "INNER JOIN live_sample ls ON ls.id=bs.id " +
                "INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id " +
                "WHERE o.parameter_stable_id=?" +
                "AND o.missing=0";

        logger.trace("Executing query: " + query);
        logger.trace("With parameters parameterId=" + parameterId);

        logger.info("    " + parameterId + " Starting get Set of results map by parameter");

        Map<String, Set<ObservationDTO>> res = new HashMap<> ();
        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, parameterId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ObservationDTO obsDTO = fillUnidimensionalObsDTO(resultSet);
                String key = resultSet.getString("id") + "#" + obsDTO.getPipelineId();
                if ( ! res.containsKey(key)) {
                    res.put(key, new HashSet<>());
                }
                res.get(key).add(obsDTO);
            }

            logger.info("    " + parameterId + " Resulting size of query: " + res.keySet().size());

        }

        logger.info("    " + parameterId + " Finished get Set of results map by parameter");

        return res;
    }


    private Map<String, ObservationDTO> getResultsMapByParameter (String parameterId)
            throws SQLException {

        // Metadata parmeters required for processing several Fear Conditioning derived parameters
        Set<String> metadataParametersToInclude = new HashSet<>(Arrays.asList(
                "IMPC_FEA_033_001",
                "IMPC_FEA_049_001",
                "IMPC_FEA_057_001",
                "IMPC_FEA_060_001",
                "IMPC_FEA_036_001",
                "IMPC_FEA_039_001"));


        Map<String, ObservationDTO> res = new HashMap<> ();

        List<String> queries = new ArrayList<>();

        // Gather all data from unidimensional data type
        queries.add("SELECT DISTINCT e.project_id,  e.procedure_id, e.procedure_stable_id, e.db_id, ls.colony_id, "
                + "bmstrain.strain_acc, ls.zygosity, ls.sex, ls.date_of_birth, uo.data_point, bs.id, "
                + "org.name, e.date_of_experiment, e.metadata_group, e.metadata_combined, pipeline_stable_id, pipeline_id  " +
                "FROM observation o " +
                "INNER JOIN unidimensional_observation uo ON uo.id=o.id " +
                "INNER JOIN biological_sample bs ON bs.id=o.biological_sample_id " +
                "INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
                "INNER JOIN experiment e ON e.id=eo.experiment_id " +
                "INNER JOIN organisation org ON e.organisation_id=org.id " +
                "INNER JOIN live_sample ls ON ls.id=bs.id " +
                "INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id " +
                "WHERE o.parameter_stable_id=?" +
                "AND o.missing=0");

        // Gather all data from selected list of metadata parameters as these are used for calcualtions on the FEA
        // procedure
        if (metadataParametersToInclude.contains(parameterId)) {
            queries.add("SELECT DISTINCT e.project_id,  e.procedure_id, e.procedure_stable_id, e.db_id, ls.colony_id, \n" +
                    "bmstrain.strain_acc, ls.zygosity, ls.sex, ls.date_of_birth, mo.value as data_point, bs.id, \n" +
                    "org.name, e.date_of_experiment, e.metadata_group, e.metadata_combined, pipeline_stable_id, pipeline_id  \n" +
                    "FROM procedure_meta_data mo\n" +
                    "INNER JOIN experiment e ON e.id=mo.experiment_id \n" +
                    "INNER JOIN organisation org ON org.id=e.organisation_id\n" +
                    "INNER JOIN experiment_observation eo ON eo.experiment_id=e.id \n" +
                    "INNER JOIN observation o on o.id=eo.observation_id\n" +
                    "INNER JOIN biological_sample bs ON bs.id=o.biological_sample_id \n" +
                    "INNER JOIN live_sample ls ON ls.id=bs.id \n" +
                    "INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id \n" +
                    "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id \n" +
                    "WHERE mo.parameter_id=?" +
                    "AND o.missing=0");
        }


        logger.trace("Executing queries: " + StringUtils.join(queries, "\n"));
        logger.trace("With parameters parameterId=" + parameterId);

        for (String query : queries) {

            try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, parameterId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    ObservationDTO obsDTO = fillUnidimensionalObsDTO(resultSet);
                    res.put(resultSet.getString("id") + "#" + obsDTO.getPipelineId(), obsDTO);
                }
            }
        }

        return res;
    }



    private Map<String,ObservationDTO> getResultsAtIncrement (String parameterId, float increment)
            throws SQLException{

        String query = "SELECT DISTINCT e.project_id,  e.procedure_id, e.procedure_stable_id, e.db_id, ls.colony_id, bmstrain.strain_acc, ls.zygosity, ls.sex, ls.date_of_birth, tso.data_point, "
                + "tso.discrete_point, bs.id, org.name, e.date_of_experiment, e.metadata_group, e.metadata_combined, pipeline_stable_id, pipeline_id "+
                "FROM observation o " +
                "INNER JOIN time_series_observation tso ON tso.id=o.id " +
                "INNER JOIN biological_sample bs ON bs.id=o.biological_sample_id " +
                "INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
                "INNER JOIN experiment e ON e.id=eo.experiment_id " +
                "INNER JOIN organisation org ON e.organisation_id=org.id " +
                "INNER JOIN live_sample ls ON ls.id=bs.id " +
                "INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id " +
                "WHERE o.parameter_stable_id=? " +
                "AND o.missing=0 " +
                "AND discrete_point=? ";


        logger.trace("Executing query: " + query);
        logger.trace("With parameters parameterId="+parameterId);

        ObservationDTO obsDTO;
        Map<String, ObservationDTO> res = new HashMap<>();

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, parameterId);
            statement.setFloat(2, increment);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                obsDTO = fillUnidimensionalObsDTO (resultSet);
                res.put(obsDTO.getAnimalId() + "#" + obsDTO.getPipelineId() , obsDTO);
            }
        }
        return res;

    }


    private Map<String, ObservationDTO> getTimeseriesDataByParameter (String parameterId)
            throws SQLException {

        Map<String, ObservationDTO> temp = new HashMap<> ();

        String query = "SELECT DISTINCT e.project_id, e.procedure_id, e.procedure_stable_id,  e.db_id, ls.colony_id, bmstrain.strain_acc, ls.zygosity, ls.sex,"
                + " ls.date_of_birth, tso.data_point, tso.discrete_point, "
                + "bs.id, org.name, e.date_of_experiment, e.metadata_group, e.metadata_combined, pipeline_stable_id, pipeline_id "+
                "FROM observation o " +
                "INNER JOIN time_series_observation tso ON tso.id=o.id " +
                "INNER JOIN biological_sample bs ON bs.id=o.biological_sample_id " +
                "INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
                "INNER JOIN experiment e ON e.id=eo.experiment_id " +
                "INNER JOIN organisation org ON e.organisation_id=org.id " +
                "INNER JOIN live_sample ls ON ls.id=bs.id " +
                "INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id " +
                "WHERE o.parameter_stable_id=? " +
                "AND o.missing=0";


        logger.trace("Executing query: " + query);
        logger.trace("With parameters parameterId="+parameterId);

        ObservationDTO obsDTO;

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, parameterId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id") + "#" + resultSet.getString("pipeline_id");
                if (temp.containsKey(id)){
                    obsDTO = temp.get(id);
                }
                else{
                    obsDTO = fillUnidimensionalObsDTO(resultSet);
                }
                obsDTO.addIncrementValue(resultSet.getDouble("data_point"));
                obsDTO.addDiscreteValue(resultSet.getDouble("discrete_point"));
                temp.put(id, obsDTO);
            }
        }

        return temp;
    }


    /**
     * Get a map of incremental data (time series) specific for the parameterId passed in
     *
     * @param parameterId the parameter to get the map of data values
     * @return map of animalIds and observationDTO
     */
    private Map<String, ObservationDTO> getIncrementalDataMapByParameter (String parameterId)
            throws SQLException {

        Map<String, ObservationDTO> res = new HashMap<String, ObservationDTO> ();

        String query = "SELECT DISTINCT e.project_id, e.procedure_id, e.procedure_stable_id, e.db_id, ls.colony_id, bmstrain.strain_acc, ls.zygosity, ls.sex, ls.date_of_birth, ls.date_of_birth, tso.data_point, tso.discrete_point, "
                + "bs.id, org.name, e.date_of_experiment, e.metadata_group, e.metadata_combined, pipeline_stable_id, pipeline_id "+
                "FROM observation o " +
                "INNER JOIN time_series_observation tso ON tso.id=o.id " +
                "INNER JOIN biological_sample bs ON bs.id=o.biological_sample_id " +
                "INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
                "INNER JOIN experiment e ON e.id=eo.experiment_id " +
                "INNER JOIN organisation org ON e.organisation_id=org.id " +
                "INNER JOIN live_sample ls ON ls.id=bs.id " +
                "INNER JOIN biological_model_sample bms ON bms.biological_sample_id=bs.id " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id " +
                "WHERE o.parameter_stable_id=?" +
                "AND o.missing=0";


        logger.trace("Executing query: " + query);
        logger.trace("With parameters parameterId="+parameterId);

        ObservationDTO obsDTO;

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, parameterId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("id") + "#" + resultSet.getString("pipeline_id");
                if (res.containsKey(id)){
                    obsDTO = res.get(id);
                }
                else{
                    obsDTO = fillUnidimensionalObsDTO(resultSet);
                }
                obsDTO.addIncrementValue(resultSet.getDouble("data_point"));
                obsDTO.addDiscreteValue(resultSet.getDouble("discrete_point"));
                res.put(id, obsDTO);
            }
        }
        return res;
    }

    private  Set<String> getTimeseriesResultsIntersection(ArrayList<String> listOfParameters, Map<String, Map<String, ObservationDTO>> parameterMap)
            throws SQLException {

        Map<Integer,String> auxMap = new HashMap<>();
        ArrayList<Integer> sortedList = new ArrayList<>();

        for (String paramId : listOfParameters){
            Map<String,ObservationDTO> animalPipelineMap = new  HashMap<>();
            animalPipelineMap.putAll(getTimeseriesDataByParameter(paramId));
            parameterMap.put(paramId, animalPipelineMap);
            int size = parameterMap.get(paramId).size();
            while (auxMap.containsKey(size)){
                size++;
            }
            auxMap.put(size, paramId);
            sortedList.add(size);
        }

        // sort sets by size
        Collections.sort(sortedList);

        // start with smallest set
        Set<String> resParamId = parameterMap.get(auxMap.get(sortedList.get(0))).keySet();
        for (int i = 1; i < sortedList.size(); i++){
            Set<String> newSet = parameterMap.get(auxMap.get(sortedList.get(i))).keySet();
            resParamId.retainAll(newSet);
        }

        return resParamId;
    }



    /**
     * Fills parameterMap if needed
     * returns a list of animal ids with all parameters.
     * @param listOfParameters the list of parameters to interrogate
     * @return filled set of parameters that have data for all pararmeters in listOfParameters
     */
    private  Set<String> getResultsIntersectionByParameter(ArrayList<String> listOfParameters, Map<String, Map<String, ObservationDTO>> parameterMap)
            throws SQLException{

        Map<Integer,String> auxMap = new HashMap<>();
        ArrayList<Integer> sortedList = new ArrayList<>();

        for (String paramId : listOfParameters){
            if (!parameterMap.containsKey(paramId)){
                parameterMap.put(paramId, getResultsMapByParameter(paramId));
            }
            int size = parameterMap.get(paramId).size();
            while (auxMap.containsKey(size)){
                size++;
            }
            auxMap.put(size, paramId);
            sortedList.add(size);
        }

        // sort sets by size
        Collections.sort(sortedList);
        // start with smallest set

        Set<String> resParamId = parameterMap.get(auxMap.get(sortedList.get(0))).keySet();
        for (int i = 1; i < sortedList.size(); i++){
            Set<String> newSet = parameterMap.get(auxMap.get(sortedList.get(i))).keySet();
            resParamId.retainAll(newSet);
        }
        return resParamId;
    }

    private  Set<String> getTimeSeriesResultsUnionByParameter(List<String> listOfParameters, Map<String, Map<String, Set<ObservationDTO>>> parameterMap)
            throws SQLException{

        for (String paramId : listOfParameters){
            parameterMap.put(paramId, getSetOfResultsMapByParameter(paramId));
        }

        return parameterMap.values().stream()
                .flatMap(x -> x.keySet().stream())
                .collect(Collectors.toSet());
    }


    /**
     * Deletes all experiments and observations (all types) for a provided
     * organisation.
     *
     * @param parameterId the parameter id to delete all entries from the database
     */
    private void deleteObservationsForParameterId(String parameterId) throws SQLException {

        String query = " SELECT e.id FROM experiment e INNER JOIN experiment_observation eo ON eo.experiment_id=e.id"
                + " INNER JOIN observation o ON o.id=eo.observation_id WHERE o.parameter_stable_id=? AND e.external_id LIKE 'derived_%'";

        Connection dbConnection = komp2DataSource.getConnection();
        dbConnection.setAutoCommit(false);

        try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
            statement.setString(1, parameterId);
            ResultSet res = statement.executeQuery();
            ArrayList<String> deleteQueries = new ArrayList<>();
            deleteQueries.add("DELETE FROM experiment WHERE id=?" );
            deleteQueries.add("DELETE FROM experiment_observation WHERE experiment_id=?" );

            for (String q : deleteQueries){
                try (PreparedStatement stat = dbConnection.prepareStatement(q)) {
                    while (res.next()){
                        Integer id = res.getInt("id");
                        stat.setInt(1, id);
                        stat.addBatch();
                    }
                    stat.executeBatch();
                }
            }
        }

        query = " SELECT id from observation where parameter_stable_id=?";
        try (PreparedStatement statement = dbConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            statement.setString(1, parameterId);
            ResultSet res = statement.executeQuery();
            ArrayList<String> deleteQueries = new ArrayList<>();
            deleteQueries.add("DELETE FROM unidimensional_observation WHERE id=?" );
            deleteQueries.add("DELETE FROM time_series_observation WHERE id=?" );
            deleteQueries.add("DELETE FROM observation WHERE id=?" );
            deleteQueries.add("DELETE FROM experiment_observation WHERE observation_id=?" );

            for (String q : deleteQueries){

                try (PreparedStatement stat = dbConnection.prepareStatement(q)) {
                    while (res.next()){
                        Integer id = res.getInt("id");
                        stat.setInt(1, id);
                        stat.addBatch();
                    }
                    stat.executeBatch();
                    res.first();
                }
            }
        }

        query = " SELECT id FROM experiment WHERE external_id like ?";
        try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
            statement.setString(1, "derived_" + parameterId + "%");
            ResultSet res = statement.executeQuery();
            ArrayList<String> deleteQueries = new ArrayList<>();
            deleteQueries.add("DELETE FROM experiment WHERE id=?" );
            deleteQueries.add("DELETE FROM experiment_observation WHERE experiment_id=?" );

            for (String q : deleteQueries){
                try (PreparedStatement stat = dbConnection.prepareStatement(q)) {
                    while (res.next()){
                        Integer id = res.getInt("id");
                        stat.setInt(1, id);
                        stat.addBatch();
                    }
                    stat.executeBatch();
                }
            }
        }

        dbConnection.commit();
        dbConnection.close();

        logger.info("Delete finished for parameterId="+parameterId);

    }


    /**
     *
     * @param dto
     * @param experimentId
     * @param p
     * @param storeMetadata The metadata group should be stored for all formulas except plotAsTimeseries. The simple parameters may come from different procedures.
     * @return
     */
    private Experiment createNewExperiment(ObservationDTO dto, String experimentId, Procedure p, boolean storeMetadata){

        Experiment currentExperiment = new Experiment();
        Pipeline pipeline = pipelines.get(dto.getPipelineStableId());

        // JM 20170912
        // Override the pipeline to always be EUMODIC 1 when dealing with legacy body weight derived parameters
        if (p!= null && p.getStableId()!= null && p.getStableId().equals("ESLIM_022_001")) {
            pipeline = pipelines.get("ESLIM_001");
        }

        currentExperiment.setDatasource(datasources.get(dto.getExternalDbId()));
        currentExperiment.setProject(projects.get(dto.getProjectId()));
        currentExperiment.setDateOfExperiment(dto.getDateOfExperiment());
        currentExperiment.setExternalId(experimentId);
        currentExperiment.setOrganisation(organisations.get(dto.getProductionCenter().toUpperCase()));

        if (storeMetadata){
            currentExperiment.setMetadataCombined(dto.getMetadataCombined());
            currentExperiment.setMetadataGroup(dto.getMetadataGroup());
        }

        currentExperiment.setPipeline(pipeline);
        currentExperiment.setPipelineStableId(pipeline.getStableId());
        currentExperiment.setProcedureStableId("" + p.getStableId());
        currentExperiment.setProcedure(p);

        return currentExperiment;

    }


    private ObservationDTO fillCategoricalObsDTO(ResultSet resultSet)
            throws SQLException{

        ObservationDTO obsDTO = new ObservationDTO();
        obsDTO.setExternalDbId(resultSet.getLong("db_id"));
        obsDTO.setProjectId(resultSet.getLong("project_id"));
        obsDTO.setAnimalId(resultSet.getLong("id"));
        obsDTO.setColony(resultSet.getString("colony_id"));
        obsDTO.setSex(SexType.valueOf(resultSet.getString("sex")));
        if (resultSet.getString("zygosity") == null)
            obsDTO.setZygosity(null);
        else
            obsDTO.setZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));
        obsDTO.setStrain(resultSet.getString("strain_acc"));
        obsDTO.setProductionCenter(resultSet.getString("name"));
        obsDTO.setDateOfExperiment(resultSet.getDate("date_of_experiment"));
        obsDTO.setMetadataGroup(resultSet.getString("metadata_group"));
        obsDTO.setMetadataCombined(resultSet.getString("metadata_combined"));
        obsDTO.setCategory(resultSet.getString("category"));
        obsDTO.setPipelineId(resultSet.getString("pipeline_id"));
        obsDTO.setPipelineStableId(resultSet.getString("pipeline_stable_id"));
        obsDTO.setDateOfBirth(resultSet.getDate("date_of_birth"));
        obsDTO.setProcedureId(resultSet.getString("procedure_id"));
        obsDTO.setProcedureStableId(resultSet.getString("procedure_stable_id"));
        return obsDTO;
    }



    private ObservationDTO fillUnidimensionalObsDTO(ResultSet resultSet)
            throws SQLException{

        ObservationDTO obsDTO = new ObservationDTO();
        obsDTO.setExternalDbId(resultSet.getLong("db_id"));
        obsDTO.setProjectId(resultSet.getLong("project_id"));
        obsDTO.setAnimalId(resultSet.getLong("id"));
        obsDTO.setColony(resultSet.getString("colony_id"));
        obsDTO.setSex(SexType.valueOf(resultSet.getString("sex")));
        if (resultSet.getString("zygosity") == null)
            obsDTO.setZygosity(null);
        else
            obsDTO.setZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));
        obsDTO.setStrain(resultSet.getString("strain_acc"));
        obsDTO.setProductionCenter(resultSet.getString("name"));
        obsDTO.setDateOfExperiment(resultSet.getDate("date_of_experiment"));
        obsDTO.setMetadataGroup(resultSet.getString("metadata_group"));
        obsDTO.setMetadataCombined(resultSet.getString("metadata_combined"));
        obsDTO.setDataPoint(resultSet.getFloat("data_point"));
        obsDTO.setPipelineId(resultSet.getString("pipeline_id"));
        obsDTO.setPipelineStableId(resultSet.getString("pipeline_stable_id"));
        obsDTO.setDateOfBirth(resultSet.getDate("date_of_birth"));
        obsDTO.setProcedureId(resultSet.getString("procedure_id"));
        obsDTO.setProcedureStableId(resultSet.getString("procedure_stable_id"));

        return obsDTO;
    }


    public class ObservationDTO {

        private Long              animalId;
        private Long              biologicalModelId; //line level params only
        private String            colony;
        private Float             dataPoint;
        private Date              dateOfExperiment;
        private Date              dateOfBirth;
        private ArrayList<Double> discreteValues  = new ArrayList<>(); // discrete_point
        private ArrayList<Double> incrementValues = new ArrayList<>(); // data_point
        private Long              externalDbId;
        private String            metadataCombined;
        private String            metadataGroup;
        private String            pipelineStableId;
        private String            pipelineId;
        private String            procedureId;
        private String            procedureStableId;
        private String            productionCenter;
        private Long              projectId;
        private String            strain;
        private SexType           sex;
        private ZygosityType      zygosity;
        private String            category;

        Long getBiologicalModelId() { return biologicalModelId; }

        void setBiologicalModelId(Long biologicalModelId) { this.biologicalModelId = biologicalModelId; }

        String getProcedureId() {
            return procedureId;
        }
        void setProcedureId(String procedureId) {
            this.procedureId = procedureId;
        }
        String getProcedureStableId() {
            return procedureStableId;
        }
        void setProcedureStableId(String procedureStableId) {
            this.procedureStableId = procedureStableId;
        }
        String getCategory() {
            return category;
        }
        void setCategory(String category) {
            this.category = category;
        }
        String getPipelineId() {
            return pipelineId;
        }
        void setPipelineId(String pipelineId) {
            this.pipelineId = pipelineId;
        }
        String getPipelineStableId() {
            return pipelineStableId;
        }
        void setPipelineStableId(String pipelineStableId) {
            this.pipelineStableId = pipelineStableId;
        }

        String getMetadataCombined() {
            return metadataCombined;
        }
        void setMetadataCombined(String metadataCombined) {
            this.metadataCombined = metadataCombined;
        }
        String getMetadataGroup() {
            return metadataGroup;
        }
        void setMetadataGroup(String metadataGroup) {
            this.metadataGroup = metadataGroup;
        }
        Long getProjectId() {
            return projectId;
        }
        void setProjectId(Long projectId) {
            this.projectId = projectId;
        }
        Long getExternalDbId() {
            return externalDbId;
        }
        void setExternalDbId(Long externalDbId) {
            this.externalDbId = externalDbId;
        }

        String getStrain() {
            return strain;
        }
        void setStrain(String strain) {
            this.strain = strain;
        }
        ZygosityType getZygosity() {
            return zygosity;
        }
        void setZygosity(ZygosityType zygosity) {
            this.zygosity = zygosity;
        }
        SexType getSex() {
            return sex;
        }
        void setSex(SexType sex) {
            this.sex = sex;
        }
        String getColony() {
            return colony;
        }
        void setColony(String colony) {
            this.colony = colony;
        }
        Float getDataPoint() {
            return dataPoint;
        }
        void setDataPoint(Float dataPoint) {
            this.dataPoint = dataPoint;
        }
        Long getAnimalId() {
            return animalId;
        }
        void setAnimalId(Long animalId) {
            this.animalId = animalId;
        }
        Date getDateOfExperiment() {
            return dateOfExperiment;
        }
        void setDateOfExperiment(Date dateOfExperiment) {
            this.dateOfExperiment = dateOfExperiment;
        }
        String getProductionCenter() {
            return productionCenter;
        }
        void setProductionCenter(String productionCenter) {
            this.productionCenter = productionCenter;
        }
        ArrayList<Double> getIncrementValues(){
            return incrementValues;
        }
        void addIncrementValue(Double value){
            incrementValues.add(value);
        }
        ArrayList<Double> getDiscreteValues(){
            return discreteValues;
        }
        void addDiscreteValue(Double value){
            discreteValues.add(value);
        }
        Date getDateOfBirth() {
            return dateOfBirth;
        }
        void setDateOfBirth(Date dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        @Override
        public String toString() {
            return "ObservationDTO{" +
                    "animalId=" + animalId +
                    ", biologicalModelId=" + biologicalModelId +
                    ", colony='" + colony + '\'' +
                    ", dataPoint=" + dataPoint +
                    ", dateOfExperiment=" + dateOfExperiment +
                    ", dateOfBirth=" + dateOfBirth +
                    ", discreteValues=" + discreteValues +
                    ", incrementValues=" + incrementValues +
                    ", externalDbId=" + externalDbId +
                    ", metadataCombined='" + metadataCombined + '\'' +
                    ", metadataGroup='" + metadataGroup + '\'' +
                    ", pipelineStableId='" + pipelineStableId + '\'' +
                    ", pipelineId='" + pipelineId + '\'' +
                    ", procedureId='" + procedureId + '\'' +
                    ", procedureStableId='" + procedureStableId + '\'' +
                    ", productionCenter='" + productionCenter + '\'' +
                    ", projectId=" + projectId +
                    ", strain='" + strain + '\'' +
                    ", sex=" + sex +
                    ", zygosity=" + zygosity +
                    ", category='" + category + '\'' +
                    '}';
        }
    }


    private class ColonyDTO {
        String colonyId;
        Long experimentId;
        Long biologicalModelId;


        @Override
        public String toString() {

            return "ColonyDTO{" +
                    "colonyId='" + colonyId + '\'' +
                    ", experimentId=" + experimentId +
                    '}';
        }
    }


    // Load all animals by id
    protected Map<Long, LiveSample> loadAllAnimalsById() {

        return StreamSupport
                .stream(liveSampleRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(LiveSample::getId, Function.identity()));
    }

    // Load all datasources by id
    protected Map<Long, Datasource> loadAllDatasourcesById() {

        return StreamSupport
                .stream(datasourceRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(Datasource::getId, Function.identity()));
    }

   // Load all organisations by UPPERCASED short name
    protected Map<String, Organisation> loadAllOrganisationsById() {

        return StreamSupport
                .stream(organisationRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap( org -> org.getName().toUpperCase(), Function.identity()));
    }

    // Load all pipelines by stable id
    protected Map<String, Pipeline> loadAllPipelinesByStableId() {

        return StreamSupport
                .stream(pipelineRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(Pipeline::getStableId, Function.identity()));
    }

    // Load all projectsById
    protected Map<Long, Project> loadAllProjectsById() {

        return StreamSupport
                .stream(projectRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(Project::getId, Function.identity()));
    }


    // ------
    // Setters
    // ------
    public void setPipelines(Map<String, Pipeline> pipelines) {
        this.pipelines = pipelines;
    }

    public void setDatasources(Map<Long, Datasource> datasources) {
        this.datasources = datasources;
    }

    public void setProjects(Map<Long, Project> projects) {
        this.projects = projects;
    }

    public void setOrganisations(Map<String, Organisation> organisations) {
        this.organisations = organisations;
    }

    public void setAllAnimals(Map<Long, LiveSample> animals) {
        this.animals = animals;
    }
    /* ************************************************************************************** */
    /* NOT USED METHODS BELOW */
    /* ************************************************************************************** */



    /*
     * Kept for legacy reasons
     * not used
     */
//    private void getAllDerivedParameters_old() throws SQLException, ClassNotFoundException{
//
//        // ************** GMC Parameters **************** //
//        //	GMC_914_001_704	GMC_914_001_015/GMC_914_001_001
//        copyDivisionResult("GMC_914_001_704", "GMC_914_001_015", "GMC_914_001_001");
//        // GMC_914_001_701	GMC_914_001_005xGMC_914_001_021
//        copyMultiplicationResult("GMC_914_001_701", "GMC_914_001_005", "GMC_914_001_021");	// Could not test, no data
//        // ++++ GMC_914_001_705	100xGMC_914_001_015/GMC_914_001_009
//        copyMultiplicationOfDivision("GMC_914_001_705", "GMC_914_001_015", "GMC_914_001_009", 100);	// Could not test, no data
//        // ++++ GMC_914_001_702	GMC_914_001_005xGMC_914_001_021/GMC_914_001_001
//        copyMultiplicationOfDivision("GMC_914_001_705", "GMC_914_001_015", "GMC_914_001_009", "GMC_914_001_005");	// Could not test, no data
//
//
//        // ************** TCP Parameters **************** //
//        // 		TCP_TFL_002_001	" 	IMPC_TFL_001_001 mean_of_increments"
//        copyMeanOfIncrements("TCP_TFL_002_001", "IMPC_TFL_001_001");
//
//
//        // ************** IMPC Parameters *************** //
//        // 						IMPC_DXA_007_001	"IMPC_DXA_005_001 	IMPC_DXA_001_001 /"
//        copyDivisionResult("IMPC_DXA_007_001", "IMPC_DXA_005_001" , "IMPC_DXA_001_001");	//Checked, fine
//        // 						IMPC_DXA_008_001	"IMPC_DXA_003_001 	IMPC_DXA_001_001 /"
//        copyDivisionResult("IMPC_DXA_008_001", "IMPC_DXA_003_001" , "IMPC_DXA_001_001");
//        // 						IMPC_DXA_009_001	"IMPC_DXA_002_001 	IMPC_DXA_001_001 /"
//        copyDivisionResult("IMPC_DXA_009_001", "IMPC_DXA_002_001" , "IMPC_DXA_001_001");
//        // 						IMPC_DXA_010_001	"IMPC_DXA_005_001 	IMPC_DXA_004_001 /"
//        copyDivisionResult("IMPC_DXA_010_001", "IMPC_DXA_005_001" , "IMPC_DXA_004_001");
//        // 					IMPC_GRS_008_001	"IMPC_GRS_001_001 mean_of_increments"
//        copyMeanOfIncrements("IMPC_GRS_008_001", "IMPC_GRS_001_001");	// Checked, fine
//        // 					IMPC_GRS_009_001	"IMPC_GRS_002_001 mean_of_increments"
//        copyMeanOfIncrements("IMPC_GRS_009_001", "IMPC_GRS_002_001");	// Checked, fine
//        // 						IMPC_GRS_010_001	"IMPC_GRS_008_001 	IMPC_GRS_003_001 /"
//        copyMeanOfIncrements("ICS_ROT_002_001", "ICS_ROT_001_001");	// Checked, fine
//        // 						ICS_ROT_002_001,"ICS_ROT_001_001 mean_of_increments"
//        copyDivisionResult("IMPC_GRS_010_001", "IMPC_GRS_008_001" , "IMPC_GRS_003_001");	// Checked, fine
//        // 						IMPC_GRS_011_001	"IMPC_GRS_009_001 	IMPC_GRS_003_001 /"
//        copyDivisionResult("IMPC_GRS_011_001", "IMPC_GRS_009_001" , "IMPC_GRS_003_001");	// Checked, fine
//
//        // 					IMPC_OFD_020_001	"IMPC_OFD_005_001 sum_of_increments"
//        copySumOfIncrements("IMPC_OFD_020_001", "IMPC_OFD_005_001");	// Checked for ESLIM, fine
//        // 					IMPC_OFD_021_001	"IMPC_OFD_006_001 sum_of_increments"
//        copySumOfIncrements("IMPC_OFD_021_001", "IMPC_OFD_006_001");	// Checked for ESLIM, fine
//
//        // 	MGP_IPG_011_001,"MGP_IPG_002_001 15 increment_value MGP_IPG_002_001 0 increment_value -"
//        copyDifferenceOfIncrements("MGP_IPG_011_001", "MGP_IPG_002_001", 15.0, 0.0);
//
//        // IMPC_IPG_012_001	area_under_curve(IMPC_IPG_002_001)
//        copyAUC("IMPC_IPG_012_001", "IMPC_IPG_002_001");	// Checked for ESLIM, fine
//        // MGP_IPG_012_001,"MGP_IPG_002_001 area_under_curve"
//        copyAUC("MGP_IPG_012_001", "MGP_IPG_002_001");
//
//        // IMPC_EYE_092_001,"IMPC_EYE_020_001 IMPC_EYE_021_001 IMPC_EYE_022_001 if_abnormal"
//        IMPC_EYE_092_001(); 	//Checked by hand, values look fine
//
//        // IMPC_ACS_033_001	"IMPC_ACS_006_001 IMPC_ACS_007_001 - IMPC_ACS_006_001 / 100 *"
//        copyPercentageOfDifference("IMPC_ACS_033_001", "IMPC_ACS_006_001", "IMPC_ACS_007_001", "IMPC_ACS_006_001");		// Checked for ESLIM, fine
//        // IMPC_ACS_034_001	"IMPC_ACS_006_001 IMPC_ACS_008_001 - IMPC_ACS_006_001 / 100 *"
//        copyPercentageOfDifference("IMPC_ACS_034_001", "IMPC_ACS_006_001", "IMPC_ACS_008_001", "IMPC_ACS_006_001");		// Checked for ESLIM, fine
//        // IMPC_ACS_035_001	"IMPC_ACS_006_001 IMPC_ACS_009_001 - IMPC_ACS_006_001 / 100 *"
//        copyPercentageOfDifference("IMPC_ACS_035_001", "IMPC_ACS_006_001", "IMPC_ACS_009_001", "IMPC_ACS_006_001");		// Checked for ESLIM, fine
//        // IMPC_ACS_036_001	"IMPC_ACS_006_001 IMPC_ACS_010_001 - IMPC_ACS_006_001 / 100 *"
//        copyPercentageOfDifference("IMPC_ACS_036_001", "IMPC_ACS_006_001", "IMPC_ACS_010_001", "IMPC_ACS_006_001");		// Checked for ESLIM, fine
//
//
//        // IMPC_OFD_022_001	"IMPC_OFD_016_001 IMPC_OFD_008_001 / 100 *"
//        copyPercentageOf("IMPC_OFD_022_001", "IMPC_OFD_016_001", "IMPC_OFD_008_001"); 	// Checked for ESLIM, fine
//
//
//        // IMPC_ACS_037_001	"IMPC_ACS_007_001 IMPC_ACS_008_001 + IMPC_ACS_009_001 + IMPC_ACS_010_001 + 4 / IMPC_ACS_006_001 - IMPC_ACS_006_001 / 100 *"
//        IMPC_ACS_037_001(); // Checked & fine!
//
//        // IMPC_IPG_010_001	"increment_value IMPC_IPG_002_001 0"
//        copyIncrementValueAt("IMPC_IPG_010_001", "IMPC_IPG_002_001", 0);	// Checked & fine!
//        //MGP_IPG_010_001,"MGP_IPG_002_001 0 increment_value"
//        copyIncrementValueAt("MGP_IPG_010_001", "MGP_IPG_002_001", 0);
//
//
//        // IMPC_CAL_017_001	"IMPC_CAL_004_001 mean_of_increments IMPC_CAL_003_001 mean_of_increments /"
//        copyDivisionOfMeanOfIncrements("IMPC_CAL_017_001", "IMPC_CAL_004_001", "IMPC_CAL_003_001"); // Checked & fine!
//
//        // ++++ IMPC_IPG_011_001	IMPC_IPG_002_001 15 increment_value IMPC_IPG_002_001 0 increment_value -
//        IMPC_IPG_011_001(); 	// Checked, fine
//
//
//        // *
//        // * No ontology terms associated
//        // *
//        //////////////////////////////////////////////////////////////////////////////////////////////////
//        // CALCULATE IMPC LINE LEVEL DERIVED PARAMETERS
//        //////////////////////////////////////////////////////////////////////////////////////////////////
//
//        // % pups WT = Total pups WT / Total Pups
//        // IMPC_VIA_015_001 = IMPC_VIA_004_001 / IMPC_VIA_003_001
//        // System.out.println("GETTING DERIVED PARAMS");
//        //calculateLineLevelPercent("IMPC_VIA_015_001", "IMPC_VIA_004_001" , "IMPC_VIA_003_001");
//
//        // % pups heterozygous = Total pups heterozygous / Total Pups
//        // IMPC_VIA_018_001 = IMPC_VIA_005_001 / IMPC_VIA_003_001
//        // calculateLineLevelPercent("IMPC_VIA_018_001", "IMPC_VIA_005_001" , "IMPC_VIA_003_001");
//
//        // % pups homozygous = Total pups homozygous / Total pups
//        // IMPC_VIA_019_001 = IMPC_VIA_006_001 / IMPC_VIA_003_001
//        // calculateLineLevelPercent("IMPC_VIA_019_001", "IMPC_VIA_006_001" , "IMPC_VIA_003_001");
//
//        // % male WT = Total male WT/Total male pups
//        // IMPC_VIA_020_001 = IMPC_VIA_007_001 / IMPC_VIA_010_001
//        // calculateLineLevelPercent("IMPC_VIA_020_001", "IMPC_VIA_007_001" , "IMPC_VIA_010_001");
//
//        // % male heterozygous = Total male heterozygous / Total male pups
//        // IMPC_VIA_021_001 = IMPC_VIA_008_001 / IMPC_VIA_010_001
//        // calculateLineLevelPercent("IMPC_VIA_021_001", "IMPC_VIA_008_001" , "IMPC_VIA_010_001");
//
//        // % male homozygous = Total male homozygous / Total male pups
//        // IMPC_VIA_022_001 = IMPC_VIA_009_001 / IMPC_VIA_010_001
//        // calculateLineLevelPercent("IMPC_VIA_022_001", "IMPC_VIA_009_001" , "IMPC_VIA_010_001");
//
//        // % female WT = Total female WT / Total female pups
//        // IMPC_VIA_023_001 = IMPC_VIA_011_001 / IMPC_VIA_014_001
//        // calculateLineLevelPercent("IMPC_VIA_023_001", "IMPC_VIA_011_001" , "IMPC_VIA_014_001");
//
//        // % female heterozygous = Total female heterozygous / Total female pups
//        // IMPC_VIA_024_001 = IMPC_VIA_012_001 / IMPC_VIA_014_001
//        // calculateLineLevelPercent("IMPC_VIA_024_001", "IMPC_VIA_012_001" , "IMPC_VIA_014_001");
//
//        // % female homozygous = Total female homozygous / Total female pups
//        // IMPC_VIA_025_001 = IMPC_VIA_013_001 / IMPC_VIA_014_001
//        // calculateLineLevelPercent("IMPC_VIA_025_001", "IMPC_VIA_013_001" , "IMPC_VIA_014_001");
//
//        // If the breeding strategy is     HetXHet, then 25% of the offspring should be Hom, 50% Het, 25% WT
//        // If the breeding strategy is not HetXHet, then 50% of the offspring should be Hom, 50% Het
//        // Terry says there will be some effects depending on HetXHom or HomXHet, but the p value is not effected
//        // total pups homozygous | total pups | Breeding Strategy {HetXHet 0.25, HetXHom 0.5, HomXHet 0.5}
//        // IMPC_VIA_032_001	= IMPC_VIA_006_001 IMPC_VIA_003_001 IMPC_VIA_031_001 get_binomial_distribution_p_value
//        // IMPC_VIA_032_001();
//
//
//
//        // ************** ESLIM Parameters *************** //
//        // ESLIM_004_001_701	area_under_curve(ESLIM_004_001_002)
//        // Modiefied AUC computation to do A + |B| , Feb. 17 2014
//        copyAUC("ESLIM_004_001_701", "ESLIM_004_001_002");	// checked, fine
//
//        // ESLIM_009_001_703	(sum_of_increments(ESLIM_009_001_001)/number_of_increments(ESLIM_009_001_001))/ESLIM_009_001_003
//        // ESLIM_009_001_704	(sum_of_increments(ESLIM_009_001_002)/number_of_increments(ESLIM_009_001_002))/ESLIM_009_001_003
//        copyDivisionOfSumOfIncrementsOverNumberOfIncrements("ESLIM_009_001_703", "ESLIM_009_001_001", "ESLIM_009_001_003");	// checked, fine
//        copyDivisionOfSumOfIncrementsOverNumberOfIncrements("ESLIM_009_001_704", "ESLIM_009_001_002", "ESLIM_009_001_003");	// checked, fine
//
//        // *
//        // * No ontology terms associated
//        // *
//        // ESLIM_007_001_701	sum_of_increments(ESLIM_007_001_001)
//        // ESLIM_007_001_702	sum_of_increments(ESLIM_007_001_002)
//        // copySumOfIncrements("ESLIM_007_001_701", "ESLIM_007_001_001");	// checked, fine
//        // copySumOfIncrements("ESLIM_007_001_702", "ESLIM_007_001_002");	// checked, fine
//
//
//        // ESLIM_009_001_701	sum_of_increments(ESLIM_009_001_001)/number_of_increments(ESLIM_009_001_001)
//        // ESLIM_009_001_702	sum_of_increments(ESLIM_009_001_002)/number_of_increments(ESLIM_009_001_002)
//        // ESLIM_010_001_701	sum_of_increments(ESLIM_010_001_001)/number_of_increments(ESLIM_010_001_001)
//        copyMeanOfIncrements("ESLIM_009_001_701", "ESLIM_009_001_001");	// checked, fine
//        copyMeanOfIncrements("ESLIM_009_001_702", "ESLIM_009_001_002");	// checked, fine
//
//        // *
//        // * No ontology terms associated
//        // *
//        // copyMeanOfIncrements("ESLIM_010_001_701", "ESLIM_010_001_001");	// checked, fine
//
//        // ESLIM_011_001_701	100x((ESLIM_011_001_006-ESLIM_011_001_007)/ESLIM_011_001_006)
//        // ESLIM_011_001_702	100x((ESLIM_011_001_006-ESLIM_011_001_008)/ESLIM_011_001_006)
//        // ESLIM_011_001_703	100x((ESLIM_011_001_006-ESLIM_011_001_009)/ESLIM_011_001_006)
//        // ESLIM_011_001_704	100x((ESLIM_011_001_006-ESLIM_011_001_010)/ESLIM_011_001_006)
//        copyPercentageOfDifference("ESLIM_011_001_701", "ESLIM_011_001_006", "ESLIM_011_001_007", "ESLIM_011_001_006");	// checked, fine
//        copyPercentageOfDifference("ESLIM_011_001_702", "ESLIM_011_001_006", "ESLIM_011_001_008", "ESLIM_011_001_006");	// checked, fine
//        copyPercentageOfDifference("ESLIM_011_001_703", "ESLIM_011_001_006", "ESLIM_011_001_009", "ESLIM_011_001_006");	// checked, fine
//        copyPercentageOfDifference("ESLIM_011_001_704", "ESLIM_011_001_006", "ESLIM_011_001_010", "ESLIM_011_001_006");	// checked, fine
//
//        // ESLIM_011_001_705	<-	100 x ( (ESLIM_011_001_006 - ( (ESLIM_011_001_007 + ESLIM_011_001_008 + ESLIM_011_001_009 + ESLIM_011_001_010 ) / 4 ) ) / ESLIM_011_001_006 )
//        ESLIM_011_001_705();	// checked, fine
//
//        // *
//        // * No ontology terms associated
//        // *
//        // ESLIM_007_001_703	<-	(ESLIM_007_001_012/ESLIM_007_001_004)x100
//        // copyPercentageOf("ESLIM_007_001_703", "ESLIM_007_001_012", "ESLIM_007_001_004");	// checked, fine
//
//
//        // ESLIM_003_001_701	<-	ESLIM_003_001_004/ESLIM_003_001_003
//        ESLIM_003_001_701();	// checked, fine
//
//        // ESLIM_005_001_701	<-	(ESLIM_005_001_005/ESLIM_005_001_001)
//        copyDivisionResult("ESLIM_005_001_701", "ESLIM_005_001_005", "ESLIM_005_001_001");	// checked, fine
//        // ESLIM_005_001_702	<-	(ESLIM_005_001_003/ESLIM_005_001_001)
//        copyDivisionResult("ESLIM_005_001_702", "ESLIM_005_001_003", "ESLIM_005_001_001");	// checked, fine
//        // ESLIM_005_001_703	<-	(ESLIM_005_001_002/ESLIM_005_001_001)
//        copyDivisionResult("ESLIM_005_001_703", "ESLIM_005_001_002", "ESLIM_005_001_001");	// checked, fine
//        // ESLIM_005_001_704	<-	(ESLIM_005_001_005/ESLIM_005_001_004)
//        copyDivisionResult("ESLIM_005_001_704", "ESLIM_005_001_005", "ESLIM_005_001_004");	// checked, fine
//
//
//
//        copyParameter("ESLIM_022_001_703", "ESLIM_001_001_001");
//        copyParameter("ESLIM_022_001_704", "ESLIM_002_001_001");
//        copyParameter("ESLIM_022_001_705", "ESLIM_003_001_001");
//        copyParameter("ESLIM_022_001_706", "ESLIM_004_001_001");
//        copyParameter("ESLIM_022_001_707", "ESLIM_005_001_001");
//        copyParameter("ESLIM_022_001_709", "ESLIM_009_001_003");
//        copyParameter("ESLIM_022_001_710", "ESLIM_010_001_003");
//        copyParameter("ESLIM_022_001_711", "ESLIM_011_001_011");
//        copyParameter("ESLIM_022_001_712", "ESLIM_012_001_005");
//        copyParameter("ESLIM_022_001_713", "ESLIM_013_001_018");
//        copyParameter("ESLIM_022_001_708", "ESLIM_020_001_001");
//
//
//
//        //ESLIM_022_001_701	"plot_parameters_as_time_series(ESLIM_001_001_001, ESLIM_002_001_001, ESLIM_003_001_001, ESLIM_004_001_001, ESLIM_005_001_001, ESLIM_020_001_001, ESLIM_022_001_001)"
//        ArrayList<String> params = new ArrayList<>();
//        params.add("ESLIM_001_001_001");
//        params.add("ESLIM_002_001_001");
//        params.add("ESLIM_003_001_001");
//        params.add("ESLIM_004_001_001");
//        params.add("ESLIM_005_001_001");
//        params.add("ESLIM_020_001_001");
//        params.add("ESLIM_022_001_001");
//        plotParametersAsTimeSeries("ESLIM_022_001_701", params);
//        // ESLIM_022_001_702	"plot_parameters_as_time_series(ESLIM_009_001_003, ESLIM_010_001_003, ESLIM_011_001_011, ESLIM_012_001_005, ESLIM_013_001_018, ESLIM_022_001_001)"
//        params = new ArrayList<>();
//        params.add("ESLIM_009_001_003");
//        params.add("ESLIM_010_001_003");
//        params.add("ESLIM_011_001_011");
//        params.add("ESLIM_012_001_005");
//        params.add("ESLIM_013_001_018");
//        params.add("ESLIM_022_001_001");
//        plotParametersAsTimeSeries("ESLIM_022_001_702", params);
//        // IMPC_BWT_008_001		IMPC_GRS_003_001 IMPC_CAL_001_001 IMPC_DXA_001_001 IMPC_HWT_007_001 IMPC_PAT_049_001 IMPC_BWT_001_001 IMPC_ABR_001_001 IMPC_CHL_001_001 TCP_CHL_001_001 HMGU_ROT_004_001 PLOT_ALL_PARAMETERS_AS_TIMESERIES
//        params = new ArrayList<>();
//        params.add("IMPC_GRS_003_001");
//        params.add("IMPC_CAL_001_001");
//        params.add("IMPC_DXA_001_001");
//        params.add("IMPC_HWT_007_001");
//        params.add("IMPC_PAT_049_001");
//        params.add("IMPC_BWT_001_001");
//        params.add("IMPC_ABR_001_001");
//        params.add("IMPC_CHL_001_001");
//        params.add("TCP_CHL_001_001");
//        params.add("HMGU_ROT_004_001");
//        plotParametersAsTimeSeries("IMPC_BWT_008_001", params);
//
//
//    }


    public static void main(String[] args) {
        new SpringApplicationBuilder(GenerateDerivedParameters.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
