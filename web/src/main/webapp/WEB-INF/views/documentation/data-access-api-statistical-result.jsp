<h1>REST API documentation for IMPC and Legacy statistical result access</h1>

<p>The statistical-result REST API provides access to the detailed output generated as part of the statistical analysis process.</p>

<p>The API may be queried using </p>
<ul class="task-list">
    <li> phenotyping center (UCD, Wellcome Trust Sanger Institute, JAX, etc.)</li>
    <li> phenotyping program (legacy MGP, EUMODIC, etc.)</li>
    <li> phenotyping resource (EuroPhenome, MGP, IMPC)</li>
    <li> phenotyping pipeline (EUMODIC1, EUMODIC2, MGP, IMPC adult, IMPC embryonic, etc.)</li>
    <li> phenotyping procedure or parameter</li>
    <li> allele name or MGI allele ID</li>
    <li> strain name or MGI strain ID</li>
    <li> gene symbol or MGI gene ID</li>
    <li> or a combination of all these fields</li>
</ul>

<p>The statistical-result REST API provides the fields described in the table below. Each field may be used for restricting the set
    of statistical-results you wish to receive. The full SOLR select syntax is available for use in querying the REST API. See <a
            href="http://wiki.apache.org/solr/SolrQuerySyntax">http://wiki.apache.org/solr/SolrQuerySyntax</a> and <a
            href="http://wiki.apache.org/solr/CommonQueryParameters">http://wiki.apache.org/solr/CommonQueryParameters</a> for a
    more complete list of query options.</p>

<table>
    <thead>
    <tr>

        <th>Field name</th>
        <th>Datatype</th>
        <th>Description</th>
    </tr>
    </thead>
    <tbody>

    <!-- Information about the SOLR document -->
    <tr>
        <td>doc_id</td>
        <td>string</td>
        <td>The ID of the solr document</td>
    </tr>
    <tr>
        <td>db_id</td>
        <td>int</td>
        <td>The IMPC internal database identifier</td>
    </tr>
    <tr>
        <td>data_type</td>
        <td>string</td>
        <td>The type of the underlying data for which the statistic was calculated</td>
    </tr>

    <!-- Information about the MP term -->
    <tr>
        <td>mp_term_id</td>
        <td>string</td>
        <td>The accession ID of the MP term associated to this result</td>
    </tr>
    <tr>
        <td>mp_term_name</td>
        <td>string</td>
        <td>The name of the MP term associated to this result</td>
    </tr>
    <tr>
        <td>top_level_mp_term_id</td>
        <td>string</td>
        <td>The accession ID of the top level MP term obtained by interrogating the MP ontology</td>
    </tr>
    <tr>
        <td>top_level_mp_term_name</td>
        <td>string</td>
        <td>The name of the top level MP term obtained by interrogating the MP ontology</td>
    </tr>
    <tr>
        <td>intermediate_mp_term_id</td>
        <td>string</td>
        <td>The accession ID of all intermediate level MP terms obtained by interrogating the MP ontology</td>
    </tr>
    <tr>
        <td>intermediate_mp_term_name</td>
        <td>string</td>
        <td>The name of all intermediate level MP terms obtained by interrogating the MP ontology</td>
    </tr>
    <tr>
        <td>phenotype_sex</td>
        <td>list of strings</td>
        <td>If the result produced a phenotype call, this lists all the sexes for which the call is significant</td>
    </tr>

    <!-- Information about the data being processed -->
    <tr>
        <td>resource_name</td>
        <td>string</td>
        <td>The short name of the resource responsible for producing the data</td>
    </tr>
    <tr>
        <td>resource_fullname</td>
        <td>string</td>
        <td>The full name of the resource responsible for producing the data</td>
    </tr>
    <tr>
        <td>resource_id</td>
        <td>int</td>
        <td>The IMPC internal identifier of the resource responsible for producing the data</td>
    </tr>
    <tr>
        <td>project_name</td>
        <td>string</td>
        <td>The consortium/project that produced the data</td>
    </tr>
    <tr>
        <td>phenotyping_center</td>
        <td>string</td>
        <td>The center where the data was generated</td>
    </tr>

    <tr>
        <td>pipeline_stable_id</td>
        <td>string</td>
        <td>IMPReSS pipeline identifier</td>
    </tr>
    <tr>
        <td>pipeline_stable_key</td>
        <td>string</td>
        <td>IMPReSS pipeline stable key</td>
    </tr>
    <tr>
        <td>pipeline_name</td>
        <td>string</td>
        <td>IMPReSS pipeline name</td>
    </tr>
    <tr>
        <td>pipeline_id</td>
        <td>int</td>
        <td>IMPC internal ID representing the IMPReSS pipeline</td>
    </tr>

    <tr>
        <td>procedure_stable_id</td>
        <td>string</td>
        <td>IMPReSS procedure stable ID</td>
    </tr>
    <tr>
        <td>procedure_stable_key</td>
        <td>string</td>
        <td>IMPReSS procedure stable key</td>
    </tr>
    <tr>
        <td>procedure_name</td>
        <td>string</td>
        <td>IMPReSS procedure name</td>
    </tr>
    <tr>
        <td>procedure_id</td>
        <td>int</td>
        <td>IMPC internal ID representing the IMPReSS procedure</td>
    </tr>

    <tr>
        <td>parameter_stable_id</td>
        <td>string</td>
        <td>IMPReSS parameter stable ID</td>
    </tr>
    <tr>
        <td>parameter_stable_key</td>
        <td>string</td>
        <td>IMPReSS parameter stable key</td>
    </tr>
    <tr>
        <td>parameter_name</td>
        <td>string</td>
        <td>IMPReSS parameter name</td>
    </tr>
    <tr>
        <td>parameter_id</td>
        <td>int</td>
        <td>IMPC internal ID representing the IMPReSS parameter</td>
    </tr>

    <tr>
        <td>colony_id</td>
        <td>string</td>
        <td>Phenotyping center specific colony name of the line used to generate the data</td>
    </tr>
    <tr>
        <td>marker_symbol</td>
        <td>string</td>
        <td>Gene symbol</td>
    </tr>
    <tr>
        <td>marker_accession_id</td>
        <td>string</td>
        <td>MGI ID of the gene</td>
    </tr>
    <tr>
        <td>allele_symbol</td>
        <td>string</td>
        <td>Allele symbol</td>
    </tr>
    <tr>
        <td>allele_name</td>
        <td>string</td>
        <td>Allele name</td>
    </tr>
    <tr>
        <td>allele_accession_id</td>
        <td>string</td>
        <td>MGI ID of the allele</td>
    </tr>
    <tr>
        <td><strike>strain_name</strike></td>
        <td>string</td>
        <td>Deprecated. Please see genetic_background description</td></tr>
    <tr>
    <tr>
        <td>strain_accession_id</td>
        <td>string</td>
        <td>The background strain MGI accession ID (or IMPC ID when MGI accession is not available)</td>
    </tr>
    <tr>
        <td>genetic_background</td>
        <td>string</td>
        <td>The background strain name of the specimen</td>
    </tr>
    <tr>
        <td>sex</td>
        <td>string</td>
        <td>The sex of the specimen</td>
    </tr>
    <tr>
        <td>zygosity</td>
        <td>string</td>
        <td>The zygosity of the mutant specimen</td>
    </tr>
    <tr>
        <td>control_selection_method</td>
        <td>string</td>
        <td>The strategy used to select the control set (options are baseline_all or concurrent)</td>
    </tr>
    <tr>
        <td>dependent_variable</td>
        <td>string</td>
        <td>The variable being tested</td>
    </tr>
    <tr>
        <td>metadata</td>
        <td>list of strings</td>
        <td>Metadata is data that describes the conditions under which the data was collected (e.g. machine calibration date)</td>
    </tr>
    <tr>
        <td>metadata_group</td>
        <td>string</td>
        <td>A collection of biological specimens that were all tested under the same experimental conditions. The experimental conditions
            are identified by the metadata_group tag. For more information, see the <a href="https://www.mousephenotype.org/parameters">IMPReSS parameter</a>
            documentation section <strong>Required For Data Analysis</strong>. </td>
    </tr>

    <!-- Information about the raw data -->
    <tr>
        <td>control_biological_model_id</td>
        <td>int</td>
        <td>IMPC internal ID of the biological model of the control group</td>
    </tr>
    <tr>
        <td>mutant_biological_model_id</td>
        <td>int</td>
        <td>IMPC internal ID of the biological model of the experimental group</td>
    </tr>
    <tr>
        <td>male_control_count</td>
        <td>int</td>
        <td>Count of male specimens in the control group</td>
    </tr>
    <tr>
        <td>male_mutant_count</td>
        <td>int</td>
        <td>Count of male specimens in the experimental group</td>
    </tr>
    <tr>
        <td>female_control_count</td>
        <td>int</td>
        <td>Count of female specimens in the control group</td>
    </tr>
    <tr>
        <td>female_mutant_count</td>
        <td>int</td>
        <td>Count of female specimens in the experimental group</td>
    </tr>

    <!-- Information about the calculation -->
    <tr>
        <td>statistical_method</td>
        <td>string</td>
        <td>The statistical method used to calculate the P value</td>
    </tr>
    <tr>
        <td>status</td>
        <td>string</td>
        <td>The status of the statistical calculation</td>
    </tr>
    <tr>
        <td>additional_information</td>
        <td>string</td>
        <td>Any additional information about the calculation</td>
    </tr>
    <tr>
        <td>raw_output</td>
        <td>string</td>
        <td>The actual R output produced while performing the calculation</td>
    </tr>
    <tr>
        <td>p_value</td>
        <td>float</td>
        <td>The P value of the data</td>
    </tr>
    <tr>
        <td>effect_size</td>
        <td>float</td>
        <td>The effect size of the data</td>
    </tr>

    <!-- Categorical statistics  -->
    <tr>
        <td>categories</td>
        <td>string</td>
        <td>Categories of data <sup>[4]</sup></td>
    </tr>
    <tr>
        <td>categorical_p_value</td>
        <td>float</td>
        <td>The P value for categorical data <sup>[3]</sup></td>
    </tr>
    <tr>
        <td>categorical_effect_size</td>
        <td>float</td>
        <td>The effect size (max percentage change) for categorical data <sup>[3]</sup></td>
    </tr>

    <!-- Continuous statistics -->
    <tr>
        <td>batch_significant</td>
        <td>boolean</td>
        <td>True/false if random variable "batch" is significant or not <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>variance_significant</td>
        <td>boolean</td>
        <td>True/false if variance is significant <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>null_test_p_value</td>
        <td>float</td>
        <td>The overall significance result of the calculation <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>genotype_effect_p_value</td>
        <td>float</td>
        <td>The significance of the genotype effect to describe variation in the data <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>genotype_effect_stderr_estimate</td>
        <td>float</td>
        <td>The estimate of the standard error of the genotype effect <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>genotype_effect_parameter_estimate</td>
        <td>float</td>
        <td>The effect size estimate of the genotype effect <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>sex_effect_p_value</td>
        <td>float</td>
        <td>The significance of sex to describe variation in the data <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>sex_effect_stderr_estimate</td>
        <td>float</td>
        <td>The estimate of the standard error of the sex effect <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>sex_effect_parameter_estimate</td>
        <td>float</td>
        <td>The effect size estimate of the sex effect <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>weight_effect_p_value</td>
        <td>float</td>
        <td>The significance of weight to describe variation in the data <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>weight_effect_stderr_estimate</td>
        <td>float</td>
        <td>The estimate of the standard error of the weight effect <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>weight_effect_parameter_estimate</td>
        <td>float</td>
        <td>The effect size estimate of the weight effect <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>group_1_genotype</td>
        <td>string</td>
        <td>The genotype of the first group (usually +/+)</td>
    </tr>
    <tr>
        <td>group_1_residuals_normality_test</td>
        <td>float</td>
        <td>Significance that group 1 conforms to normal distribution</td>
    </tr>
    <tr>
        <td>group_2_genotype</td>
        <td>string</td>
        <td>The genotype of the second group (usually the colony ID)</td>
    </tr>
    <tr>
        <td>group_2_residuals_normality_test</td>
        <td>float</td>
        <td>Significance that group 2 conforms to normal distribution</td>
    </tr>
    <tr>
        <td>blups_test</td>
        <td>float</td>
        <td>Best Linear Unbiased Prediction test</td>
    </tr>
    <tr>
        <td>rotated_residuals_test</td>
        <td>float</td>
        <td>Additional statistical model details. See
            <a href="http://www.bioconductor.org/packages/release/bioc/html/PhenStat.html">PhenStat</a>
            documentation for more information</td>
    </tr>
    <tr>
        <td>intercept_estimate</td>
        <td>float</td>
        <td>Additional statistical model details. See
            <a href="http://www.bioconductor.org/packages/release/bioc/html/PhenStat.html">PhenStat</a>
            documentation for more information</td>
    </tr>
    <tr>
        <td>intercept_estimate_stderr_estimate</td>
        <td>float</td>
        <td>Additional statistical model details. See
            <a href="http://www.bioconductor.org/packages/release/bioc/html/PhenStat.html">PhenStat</a>
            documentation for more information</td>
    </tr>
    <tr>
        <td>interaction_significant</td>
        <td>boolean</td>
        <td>True/false if the significance of sex*genotype interaction is significant <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>interaction_effect_p_value</td>
        <td>float</td>
        <td>The significance of sex*genotype interaction to describe variation in the data <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>female_ko_effect_p_value</td>
        <td>float</td>
        <td>If sex is significant, the significance of the female genotype <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>female_ko_effect_stderr_estimate</td>
        <td>float</td>
        <td>If sex is significant, the standard error estimate of the female genotype <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>female_ko_parameter_estimate</td>
        <td>float</td>
        <td>If sex is significant, the effect size estimate of the female genotype <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>male_ko_effect_p_value</td>
        <td>float</td>
        <td>If sex is significant, the significance of the male genotype <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>male_ko_effect_stderr_estimate</td>
        <td>float</td>
        <td>If sex is significant, the standard error estimate of the male genotype <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>male_ko_parameter_estimate</td>
        <td>float</td>
        <td>If sex is significant, the effect size estimate of the male genotype <sup>[1]</sup></td>
    </tr>
    <tr>
        <td>classification_tag</td>
        <td>string</td>
        <td>A summary of the result <sup>[1]</sup></td>
    </tr>
    </tbody>
</table>

<blockquote>
    <sub>[1] - For unidimensional parameters [2] - For multidimensional parameters [3] - For time series parameters [4] - For categorical parameters [5] - For metadata parameters</sub>
</blockquote>

<h3>Command line examples</h3>

<h4><a name="user-content-retrieve-all-statistical-result-associations"
       class="anchor" href="#retrieve-all-statistical-result-associations">
    <span class="octicon octicon-link"></span></a>
    Retrieve all statistical-result calculations
</h4>

<p>This is the basic request to get the first 10 results from the Solr service in JSON</p>

<div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/statistical-result/select?q=*:*&amp;rows=10&amp;wt=json&indent=true'</span>
                                        </pre>
</div>

<p>A bit of explanation:</p>
<ul class="task-list">
    <li><strong>statistical-result</strong> is the name of the Solr core service to query</li>
    <li><strong>select</strong> is the method used to query the Solr REST interface</li>
    <li><strong>q=<em>*:*</em></strong> means querying everything without any filtering on any field</li>
    <li><strong>rows</strong> allows to limit the number of results returned. Note: if you do not specify the rows parameter, the default is 10 rows.</li>
    <li><strong>wt=json</strong> return the results in json format (csv is an alternate format</li>
    <li><strong>indent=1</strong> or <strong>indent=true</strong> indents the output into a more human-readable form</li>
</ul>

<h4><a name="user-content-retrieve-all-statistical-result-for-a-specific-marker"
       class="anchor"
       href="#retrieve-all-statistical-result-associations-for-a-specific-marker">
    <span class="octicon octicon-link"></span></a>
    Retrieve all IMPC statistical results for a specific marker
</h4>
<p>We will constrain the results by adding a condition to the <strong>q</strong> (query) parameter using the specific
    <strong>marker_symbol</strong> field. For Akt2, simply specify <strong>q=marker_symbol:Akt2</strong></p>

<div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/statistical-result/select?q=marker_symbol:Akt2&amp;wt=json&indent=true'</span>
                                        </pre>
</div>

<p>More examples of how to use Solr to query and filter are available on the
    <a href="${baseUrl}/documentation/data-access-api-genotype-phenotype">genotype-phenotype API</a> documentation page.</p>

<h3>URL examples</h3>

<p>The following  are example URLs that you can paste into your browsers location bar.  The resulting documents will display like a web page.</p>

<p><i>NOTE: Certain characters, most notably spaces and the "&lt;" and "&gt;" characters, must be url encoded (space = %20, &lt;&nbsp;=&nbsp;%3c, &gt;&nbsp;=&nbsp;%3e) for command line usage</i>.</p>

<p>
<h4>Get a maximum of 500 statistical results for gene Car4</h4>
<a href="http://www.ebi.ac.uk/mi/impc/solr/statistical-result/select?q=marker_symbol:Car4&rows=500">http://www.ebi.ac.uk/mi/impc/solr/statistical-result/select?q=marker_symbol:Car4&rows=500</a>
</p>
<p>
<h4>Get a maximum of 500 statistical results for gene Car4 in CSV format</h4>
<a href="http://www.ebi.ac.uk/mi/impc/solr/statistical-result/select?q=marker_symbol:Car4&wt=csv">http://www.ebi.ac.uk/mi/impc/solr/statistical-result/select?q=marker_symbol:Car4&rows=500&wt=csv</a>
</p>
<p>
<h4>Get a list of specific fields from statistical results for gene Car4 in CSV format</h4>
<a href="http://www.ebi.ac.uk/mi/impc/solr/statistical-result/select?q=marker_symbol:Car4&wt=csv&fl=parameter_stable_id,parameter_name,female_control_count,female_mutant_count,male_control_count,male_mutant_count,null_test_p_value,statistical_method,allele_name,status,marker_accession_id,marker_symbol,allele_accession_id,allele_symbol,colony_id,metadata_group,zygosity,strain_accession_id,strain_name,classification_tag,genotype_effect_p_value,effect_size,genotype_effect_stderr_estimate">http://www.ebi.ac.uk/mi/impc/solr/statistical-result/select?q=marker_symbol:Car4&wt=csv&fl=parameter_stable_id,parameter_name,female_control_count,female_mutant_count,male_control_count,male_mutant_count,null_test_p_value,statistical_method,allele_name,status,marker_accession_id,marker_symbol,allele_accession_id,allele_symbol,colony_id,metadata_group,zygosity,strain_accession_id,strain_name,classification_tag,genotype_effect_p_value,effect_size,genotype_effect_stderr_estimate</a>
</p>
