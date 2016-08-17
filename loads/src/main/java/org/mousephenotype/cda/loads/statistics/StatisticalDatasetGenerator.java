package org.mousephenotype.cda.loads.statistics;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.BasicService;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

/**
 *
 * Generate input files for batch stats processing with PhenStat
 * Read from solr cores and produce files for all parameters to be analyzed
 *
 * @author Jeremy Mason
 * @author Andrew Tikhonov
 *
 */
@Import(StatisticalDatasetGeneratorConfig.class)
public class StatisticalDatasetGenerator extends BasicService implements CommandLineRunner{

    final private Logger logger = LoggerFactory.getLogger(getClass());
    final private HttpSolrClient experimentCore;
    final private HttpSolrClient pipelineCore;

    @Inject
    public StatisticalDatasetGenerator(
            @Named("experimentCore") HttpSolrClient experimentCore,
            @Named("pipelineCore") HttpSolrClient pipelineCore)
    {
        Assert.notNull(experimentCore);
        Assert.notNull(pipelineCore);

        this.experimentCore = experimentCore;
        this.pipelineCore = pipelineCore;
    }


    public final static String PIVOT = "phenotyping_center,pipeline_stable_id,procedure_group,strain_accession_id";

    @Override
    public void run(String... strings) throws Exception {

        try {

            SolrQuery query = new SolrQuery();
            query.setQuery("*:*")
                    .addFilterQuery("-annotate:true")
                    .setFields(ImpressDTO.PROCEDURE_STABLE_ID, ImpressDTO.PARAMETER_STABLE_ID, ImpressDTO.PARAMETER_NAME)
                    .setRows(Integer.MAX_VALUE);
            QueryResponse response = pipelineCore.query(query);
            List<ImpressDTO> impressDTOs = response.getBeans(ImpressDTO.class);

            List<Map<String, String>> parametersByProcedure = getFacetPivotResults(response, false);


            query = new SolrQuery();
            query.setQuery("*:*")
                    .addFilterQuery("-procedure_group:IMPC_VIA")
                    .addFilterQuery("-procedure_group:IMPC_FER")
                    .addFilterQuery("observation_type:(categorical%20OR%20unidimensional)")
                    .addFilterQuery("biological_sample_group:experimental")
                    .setRows(0)
                    .setFacet(true)
                    .addFacetPivotField(PIVOT);
            response = experimentCore.query(query);
            List<Map<String, String>> results = getFacetPivotResults(response, false);


//
//            JSONObject jsonObj1 = requestAndParseJSON(SOLR_ENDPOINT + "/experiment/select?q=*:*&fq=-procedure_group:IMPC_VIA&fq=-procedure_group:IMPC_FER&fq=observation_type:(categorical%20OR%20unidimensional)&fq=biological_sample_group:experimental&rows=0&wt=json&facet=true&facet.pivot=" + PIVOT);
//            JSONArray procedure_groups = jsonObj1.getJSONObject("facet_counts").getJSONObject("facet_pivot").getJSONArray(PIVOT);
//
//            // Prepare the parameter index for writing the report
//
//            String parameter_solr_query = SOLR_ENDPOINT + "/pipeline/select?q=annotate:true&fl=procedure_stable_id,parameter_stable_id,parameter_name&rows=40000&wt=json";
//            JSONObject jsonObj2 = requestAndParseJSON(parameter_solr_query);
//
//            JSONArray solr_parameters = jsonObj2.getJSONObject("response").getJSONArray("docs");
//
//            Map<String, HashSet> parameters = new HashMap<String, HashSet>();
//
//            for (int index = 0 ; index < solr_parameters.length();index++) {
//                JSONObject o = solr_parameters.getJSONObject(index);
//
//                String procedure_group = o.getString("procedure_stable_id");
//                procedure_group = procedure_group.substring(0, procedure_group.lastIndexOf("_"));
//
//                String parameter_stable_id = o.getString("parameter_stable_id");
//                HashSet<String> set2 = parameters.get(procedure_group);
//
//                if (set2 == null) {
//                    set2 = new HashSet<String>();
//                    parameters.put(procedure_group, set2);
//                }
//
//                if (!set2.contains(parameter_stable_id)) {
//                    set2.add(parameter_stable_id);
//                }
//
//            }
//
//            logger.info("Prepared " + parameters.keySet().size() + " procedure groups");
//
//            String[] starting_headers = { "specimen_id", "group", "background_strain_name", "colony_id",
//                    "marker_accession_id", "allele_accession_id", "Batch", "metadata_group", "zygosity", "sex", "::" };
//
//            int i = 0;
//
//            // 4 loops to unwrap the pivot facets
//
//            for (int group_index = 0; group_index < procedure_groups.length();group_index ++) {
//                JSONObject center = procedure_groups.getJSONObject(group_index);
//
//                String phenotyping_center = center.getString("value");
//                JSONArray pipelines       = center.getJSONArray("pivot");
//
//                for (int pipeline_index = 0; pipeline_index < pipelines.length();pipeline_index ++) {
//                    JSONObject pipeline = pipelines.getJSONObject(pipeline_index);
//
//                    String pipeline_stable_id = pipeline.getString("value");
//                    JSONArray procedures      = pipeline.getJSONArray("pivot");
//
//
//                    for (int procedure_index = 0; procedure_index < procedures.length();procedure_index ++) {
//                        JSONObject procedure = procedures.getJSONObject(procedure_index);
//
//                        String procedure_group = procedure.getString("value");
//                        JSONArray strains      = procedure.getJSONArray("pivot");
//
//
//                        for (int strain_index = 0; strain_index < strains.length();strain_index ++) {
//                            JSONObject strain = strains.getJSONObject(strain_index);
//
//                            String strain_accession_id = strain.getString("value");
//
//                            HashSet parameter_set = parameters.get(procedure_group);
//
//                            if (parameter_set == null) {
//                                logger.info("procedure group is null ", procedure_group);
//                                continue;
//                            }
//
//                            if (parameter_set.size() < 1) {
//                                logger.info("No parameters to process for ", procedure_group);
//                                continue;
//                            }
//
//                            logger.info("Processing " + i + " " + phenotyping_center + " " + pipeline_stable_id + " " +
//                                    procedure_group + " " + strain_accession_id);
//
//                            String fields = "date_of_experiment,external_sample_id,strain_name,allele_accession_id,gene_accession_id,gene_symbol,sex,zygosity,biological_sample_group,colony_id,metadata_group,parameter_stable_id,parameter_name,data_point,category";
//
//                            String filter_queries =
//                                    "fq=parameter_stable_id:(" + join("%20OR%20", parameter_set.toArray()) + ")" + "&" +
//                                            "fq=phenotyping_center:\"" + phenotyping_center + "\"" + "&" +
//                                            "fq=pipeline_stable_id:"+ pipeline_stable_id + "&" +
//                                            "fq=procedure_group:" + procedure_group + "&" +
//                                            "fq=strain_accession_id:\"" + strain_accession_id + "\"";
//
//                            String experiment_data_solr_query = SOLR_ENDPOINT +
//                                    "/experiment/select?q=*:*&rows=2000000&wt=json&fl="+fields+"&"+filter_queries;
//
//                            i++;
//
//                            //logger.info("experiment_data_solr_query = " + experiment_data_solr_query);
//
//                            JSONObject jsonObj3 = requestAndParseJSON(experiment_data_solr_query);
//                            JSONArray experimental_data = jsonObj3.getJSONObject("response").getJSONArray("docs");
//
//
//                            // process_specimen_data
//                            //
//                            HashMap<String, HashMap<String, String>> specimen_data =
//                                    new HashMap<String, HashMap<String, String>>();
//
//                            for (int exp_index = 0;exp_index< experimental_data.length();exp_index++) {
//                                JSONObject exp = experimental_data.getJSONObject(exp_index);
//
//                                String colony_id = readJsonValue(exp, "colony_id", "+/+");
//                                if ("baseline".equals(colony_id)) colony_id = "+/+";
//
//                                String zygosity  = readJsonValue(exp, "zygosity", "");
//                                String allele    = readJsonValue(exp, "allele_accession_id", "");
//                                String gene_acc  = readJsonValue(exp, "gene_accession_id", "");
//                                String gene      = readJsonValue(exp, "gene_symbol", ""); // not used
//                                String specimen_id         = readJsonValue(exp, "external_sample_id", "");
//                                String date_of_experiment  = readJsonValue(exp, "date_of_experiment", "");
//                                String sex                 = readJsonValue(exp, "sex", "");
//                                String biological_sample_group = readJsonValue(exp, "biological_sample_group", "");
//                                String strain_name            = readJsonValue(exp, "strain_name", "");
//                                String metadata_group         = readJsonValue(exp, "metadata_group", "");
//                                String parameter_stable_id    = readJsonValue(exp, "parameter_stable_id", "");
//
//                                String value = readJsonValue(exp, "category", null);
//                                if (value == null) {
//                                    value = readJsonValue(exp, "data_point", "");
//                                }
//
//                                String key = specimen_id + "\t" + biological_sample_group + "\t" + strain_name + "\t" +
//                                        colony_id + "\t" + gene_acc + "\t" + allele + "\t" + date_of_experiment + "\t" +
//                                        metadata_group + "\t" + zygosity + "\t" + sex;
//
//                                HashMap<String, String> specimen_parameter_map = specimen_data.get(key);
//
//                                if (specimen_parameter_map == null) {
//                                    specimen_parameter_map = new HashMap<String, String>();
//                                    specimen_data.put(key, specimen_parameter_map);
//                                }
//
//                                specimen_parameter_map.put(parameter_stable_id, value);
//                            }
//
//
//                            // get_specimen_data
//                            //
//
//                            StringBuilder output = new StringBuilder();
//
//                            Object[] parameter_set_array = parameter_set.toArray(); // it copies the array, no problem
//                            Arrays.sort(parameter_set_array);                       // for the set after sorting
//
//                            // metadata header + parameters header
//                            String headerLine = join("\t", starting_headers) + "\t" + join("\t", parameter_set_array);
//
//                            output.append(headerLine);
//                            output.append("\n");
//
//                            for(String key : specimen_data.keySet()) {
//                                HashMap<String, String> data = specimen_data.get(key);
//
//                                output.append(key); // metadata
//
//                                // make sure the order of fields is correct
//                                //
//                                for (Object param : parameter_set_array) {
//                                    String value0 = data.get(param);
//
//                                    if (value0 == null) value0 = "";
//                                    output.append("\t");
//                                    output.append(value0);
//                                }
//                                output.append("\n");
//                            }
//
//
//                            String filename = "/Users/andrew/project/PhenGen/tsvs/"+ phenotyping_center.replace(" ","")
//                                    + "-" + pipeline_stable_id + "-" + procedure_group + "-" +
//                                    strain_accession_id.replace(":","")+ ".tsv";
//
//                            writeToFile(filename, output.toString());
//                        }
//                    }
//                }
//            }
//
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(StatisticalDatasetGenerator.class, args);
    }


}
