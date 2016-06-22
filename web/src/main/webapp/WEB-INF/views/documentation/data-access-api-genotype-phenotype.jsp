<h1>REST API documentation for Genotype associated phenotype calls</h1>

<p>
    The International Mouse Phenotype Consortium (IMPC) is composed of several international research institutions.  At each institute, mutant mice are phenotyped and the data produced is sent to a Data Coordination Center (DCC).
    Once the data has passed a set of Quality Control (QC) checks, <a href="statistics-help.jsp">the data is statistically analyzed</a>.
</p>
<p>
    Each institute has unique operating standards and must track specimen and phenotype data
    to allow downstream analysis.  To facilitate this requirement, the DCC defined a method to uniquely identify a phenotyping effort.  One key data point used for this is the <strong>Colony ID</strong>. The Colony ID is used to
    encapsulate phenotyping mutants from a single allele on a background strain. The colony ID is associated to an allele/gene/background strain for display and dissemination.
    Each center can use a different scheme for generating the colony ID, and the colony IDs are unique across the IMPC effort.
</p>

<p>
    Internally, the IMPC uses the Colony ID (among other attributes) to assemble mutant and control data points into an appropriate data set used for analysis. If the analysis registers a significant change
    in the mutant due to the genotype effect, the IMPC generates an association between the mutant gene and a <a href="http://www.ebi.ac.uk/ols/ontologies/mp">mammalian phenotype ontology (MP)</a>
    term specific to the measured parameter as defined in <a href="http://www.mousephenotype.org/impress">IMPReSS</a>.
</p>


<p>The <strong>genotype-phenotype REST API</strong> provides programmatic access to the genotype to phenotype associations that the IMPC produces.  The API also includes access to data from legacy projects (EuroPhenome and MGP legacy) which have been
    analyzed using the IMPC statistical pipeline.
    There are many ways to get information about the MP terms associated to the different KO genes. You can select data per:
</p>

<ul class="subUl">
    <li> phenotyping center (e.g.: UCD, Wellcome Trust Sanger Institute, JAX, etc.) </li>
    <li> phenotyping procedure or parameter (e.g.: Procedure IMPC_VIA_001, Parameter IMPC_ABR_006_001, <a href="https://www.mousephenotype.org/impress/procedures/7">full IMPC procedure list available at IMPReSS)</a></li>
    <li> allele name or MGI allele ID</li>
    <li> background strain name or MGI strain ID</li>
    <li> gene symbol or MGI gene ID</li>
    <li> or a combination of all these fields</li>
</ul>

<p>
    The genotype-phenotype REST API provides the fields described in the table below.
    Each field may be used for restricting the set of data you wish to
    retreive. The full SOLR select syntax is available for use in querying the REST
    API. See <a href="http://wiki.apache.org/solr/SolrQuerySyntax">http://wiki.apache.org/solr/SolrQuerySyntax</a>
    and <a href="http://wiki.apache.org/solr/CommonQueryParameters">http://wiki.apache.org/solr/CommonQueryParameters</a>
    for a more complete list of query options.</p>


<table>
    <thead>
    <tr>
        <th>Field name</th>
        <th>Datatype</th>
        <th>Description</th>
    </tr>
    </thead>
    <tbody>
    <tr><td>doc_id</td><td>int</td><td>the unique ID of the document</td></tr>
    <tr><td>mp_term_id</td><td>string</td><td>the term ID of the associated mammalian phenotype term</td></tr>
    <tr><td>mp_term_name</td><td>string</td><td>the term name of the associated mammalian phenotype term</td></tr>
    <tr><td>top_level_mp_term_id</td><td>string</td><td>a list of the top level term ids of the associated mammalian phenotype term</td></tr>
    <tr><td>top_level_mp_term_name</td><td>string</td><td>a list of the top level term names of the associated mammalian phenotype term</td></tr>
    <tr><td>top_level_mp_term_definition</td><td>string</td><td>a list of the top level term definitions of the associated mammalian phenotype term</td></tr>
    <tr><td>top_level_mp_term_synonym</td><td>string</td><td>a list of alternate strings for the top level term name of the associated mammalian phenotype term</td></tr>
    <tr><td>intermediate_mp_term_id</td><td>string</td><td>a list of the intermediate level term ids of the associated mammalian phenotype term</td></tr>
    <tr><td>intermediate_mp_term_name</td><td>string</td><td>a list of the intermediate level term names of the associated mammalian phenotype term</td></tr>
    <tr><td>intermediate_mp_term_definition</td><td>string</td><td>a list of the intermediate level term definitions of the associated mammalian phenotype term</td></tr>
    <tr><td>intermediate_mp_term_synonym</td><td>string</td><td>a list of alternate strings for the intermediate level term name of the associated mammalian phenotype term</td></tr>
    <tr><td>marker_symbol</td><td>string</td><td>the associated marker symbol</td></tr>
    <tr><td>marker_accession_id</td><td>string</td><td>the associated marker accession ID</td></tr>
    <tr><td>colony_id</td><td>string</td><td>the colony ID</td></tr>
    <tr><td>allele_name</td><td>string</td><td>the name of the allele</td></tr>
    <tr><td>allele_symbol</td><td>string</td><td>the allele symbol</td></tr>
    <tr><td>allele_accession_id</td><td>string</td><td>the allele accession ID</td></tr>
    <tr><td><strike>strain_name</strike></td><td>string</td><td>Deprecated. Please see genetic_background description</td></tr>
    <tr><td>strain_accession_id</td><td>string</td><td>The background strain MGI accession ID (or IMPC ID when MGI accession is not available)</td></tr>
    <tr><td>genetic_background</td><td>string</td><td>The background strain name of the specimen</td></tr>
    <tr><td>phenotyping_center</td><td>string</td><td>the center at which the phenotyping was performed</td></tr>
    <tr><td>project_external_id</td><td>string</td><td>(legacy) the identifier of the project at the phenotyping center at which the work was performed</td></tr>
    <tr><td>project_name</td><td>string</td><td>the shortname of the project for which the phenotyping was performed</td></tr>
    <tr><td>project_fullname</td><td>string</td><td>the full name of the project for which the phenotyping was performed</td></tr>
    <tr><td>resource_name</td><td>string</td><td>the resource for which the phenotyping was performed</td></tr>
    <tr><td>resource_fullname</td><td>string</td><td>the full name of the resource for which the phenotyping was performed</td></tr>
    <tr><td>sex</td><td>string</td><td>the sex of the mutant specimens on which the association was made</td></tr>
    <tr><td>zygosity</td><td>string</td><td>the zygosity of the mutant specimens on which the association was made</td></tr>
    <tr><td>pipeline_name</td><td>string</td><td>the name of the IMPReSS pipeline</td></tr>
    <tr><td>pipeline_stable_id</td><td>string</td><td>the stable ID of the IMPReSS pipeline</td></tr>
    <tr><td>pipeline_stable_key</td><td>string</td><td>the stable key of the IMPReSS pipeline</td></tr>
    <tr><td>procedure_name</td><td>string</td><td>the name of the IMPReSS procedure performed</td></tr>
    <tr><td>procedure_stable_id</td><td>string</td><td>the stable ID of the IMPReSS procedure performed</td></tr>
    <tr><td>procedure_stable_key</td><td>string</td><td>the stable key of the IMPReSS procedure performed</td></tr>
    <tr><td>parameter_name</td><td>string</td><td>the name of the IMPReSS parameter measured</td></tr>
    <tr><td>parameter_stable_id</td><td>string</td><td>the stable ID of the IMPReSS parameter measured</td></tr>
    <tr><td>parameter_stable_key</td><td>string</td><td>the stable key of the IMPReSS parameter measured</td></tr>
    <tr><td>statistical_method</td><td>string</td><td>the statistical method used to determine the P value</td></tr>
    <tr><td>percentage_change</td><td>string</td><td>for continuous data, a standardized effect measure</td></tr>
    <tr><td>p_value</td><td>double</td><td>the statistical significance of the association</td></tr>
    <tr><td>effect_size</td><td>double</td><td>the size of the effect</td></tr>
    <tr><td>external_id</td><td>string</td><td>(legacy) internal ID of the association at the phenotyping center</td></tr>
    </tbody>
</table>

<h3><a name="user-content-retrieve-all-genotype-phenotype-associations"
       class="anchor" href="#retrieve-all-genotype-phenotype-associations">
    <span class="octicon octicon-link"></span></a>
    Retrieve all genotype-phenotype associations
</h3>
<p>This is the basic request to get all the results from the Solr service in JSON format (open this link in browser)</p>
<div class="highlight highlight-bash">
                                        <pre>
<a href="http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=*:*&amp;rows=10&amp;wt=json&indent=1" target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=*:*&amp;rows=10&amp;wt=json&indent=1</a>
                                        </pre>
</div>

<p>A bit of explanation:</p>
<ul class="task-list">
    <li><strong>genotype-phenotype</strong> is the name of the Solr core service to query</li>
    <li><strong>select</strong> is the method used to query the Solr REST interface</li>
    <li><strong>q=<em>*:*</em></strong> queries everything without any filtering on any field</li>
    <li><strong>rows</strong> limits the number of results returned</li>
    <li><strong>wt=json</strong> is the response format (try "csv" or "xml" instead of "json")</li>
    <li><strong>indent=1</strong> or <strong>indent=true</strong> indents the output into a more human-readable form</li>
</ul>

<h3><a name="user-content-retrieve-all-genotype-phenotype-associations-for-a-specific-marker"
       class="anchor"
       href="#retrieve-all-genotype-phenotype-associations-for-a-specific-marker">
    <span class="octicon octicon-link"></span></a>
    Retrieve all genotype-phenotype associations for a specific marker using a command line tool, curl
</h3>
<p>We'll now constrain the results by adding a condition to the <strong>q</strong> (query) parameter using the specific
    <strong>marker_symbol</strong> field. For example, for Akt2, simply specify <strong>q=marker_symbol:Akt2</strong></p>

<div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=marker_symbol:Akt2&amp;wt=json'</span>
                                        </pre>
</div>
<h3><a name="user-content-retrieve-all-genotype-phenotype-associations-for-a-specific-mp-term"
       class="anchor"
       href="#retrieve-all-genotype-phenotype-associations-for-a-specific-mp-term">
    <span class="octicon octicon-link"></span></a>
    Retrieve all genotype-phenotype associations for a specific MP term
</h3>
<p>Now we constrain the results by adding a condition to the <strong>q</strong> (query) parameter using the specific
    <strong>mp_term_name</strong> field. To retrieve the genotype associated to "decreased body weight",
    simply specify <strong>q=mp_term_name:"decreased%20body%20weight"</strong><i>Note the use of &nbsp;</i><strong>%20</strong><i>
        replacing the spaces between the words.</i>
</p>
<div class="highlight highlight-bash">
                                        <pre>
    <a href='http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=mp_term_name:"decreased%20body%20weight"&wt=json&indent=1' target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=mp_term_name:"decreased%20body%20weight"&wt=json&indent=1</a>
                                        </pre>
</div>
<p>Alternatively, we may filter by the MP term identifier by specifying the <strong>mp_term_id</strong>:</p>

<div class="highlight highlight-bash">
                                        <pre>
    <a href='http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=mp_term_id:"MP:0001262"&amp;wt=json&indent=1' target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=mp_term_id:"MP:0001262"&amp;wt=json&indent=1</a>
                                        </pre>
</div>
<h3><a name="user-content-retrieve-all-genotype-phenotype-associations-for-a-top-level-mp-term"
       class="anchor"
       href="#retrieve-all-genotype-phenotype-associations-for-a-top-level-mp-term">
    <span class="octicon octicon-link"></span></a>
    Retrieve all genotype-phenotype associations for a top level MP term</h3>
<p>Now we constrain the results by adding a condition to the <strong>q</strong> (query) parameter using the specific
    <strong>top_level_mp_term_name</strong> field. This also works with <strong>top_level_mp_term_id</strong>
    if you pass an identifier instead of the MP term name. To retrieve the genotype associated to "decreased
    body weight", simply specify <strong>q=top_level_mp_term_name:"nervous system phenotype"</strong>
</p>
<p><i>Note the use of &nbsp;</i><strong>%20</strong><i> replacing the space between "body" and "phenotype"</i>.</p>

<div class="highlight highlight-bash">
                                        <pre>
    <a href='http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=top_level_mp_term_name:"growth/size/body%20region%20phenotype"&amp;wt=json&indent=1' target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=top_level_mp_term_name:"growth/size/body%20region%20phenotype"&amp;wt=json&indent=1</a>
                                        </pre>
</div>

<h3>
    <a name="user-content-retrieve-all-genotype-phenotype-associations-with-a-p-value-cut-off"
       class="anchor"
       href="#retrieve-all-genotype-phenotype-associations-with-a-p-value-cut-off">
        <span class="octicon octicon-link"></span></a>
    Retrieve all genotype-phenotype associations with a P value cut-off
</h3>
<p>In this example, we apply a cut-off to the previous query and add a condition to the <strong>q</strong> (query)
    command. In Solr, you can specify a range to retrieve results. For instance, if you want P values below 0.0001,
    you can add the condition <strong>p_value:[0 TO 0.0001]</strong> to retrieve the genotype associated
    to a nervous system phenotype with a P value cut-off of 0.00005.
</p>
<p><i>Note the use of &nbsp;</i><strong>%20</strong> <i>replacing the spaces between the words, and </i><strong>%5b</strong>
    <i>and </i><strong>%5d</strong><i> replacing the </i>"<strong>[</strong>"<i> and </i>"<strong>]</strong>"<i> characters.
        Alternately, you could instead use the <strong>-g</strong></i> flag in the curl command

</p>
<div class="highlight highlight-bash">
                                        <pre>
    <a href='http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=top_level_mp_term_name:%22growth/size/body%20region%20phenotype%22%20AND%20p_value:%5b0%20TO%200.00005%5d&wt=json&indent=1' target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=top_level_mp_term_name:%22growth/size/body%20region%20phenotype%22%20AND%20p_value:%5b0%20TO%200.00005%5d&wt=json&indent=1</a>
                                        </pre></div>

or, alternatively, you could replace the <strong>[</strong> (%5b) and <strong>]</strong> (%5d) encoding characters
with the <strong>-g</strong> flag in the curl command:
<div class="highlight highlight-bash">
                                        <pre>
    <a href='http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=top_level_mp_term_name:%22growth/size/body%20region%20phenotype%22%20AND%20p_value:[0%20TO%200.00005]&wt=json&indent=1' target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=top_level_mp_term_name:%22growth/size/body%20region%20phenotype%22%20AND%20p_value:[0%20TO%200.00005]&wt=json&indent=1</a>
                                        </pre></div>

<h3>
    <a name="user-content-retrieve-all-genotype-phenotype-associations-for-a-specific-phenotyping-center"
       class="anchor"
       href="#retrieve-all-genotype-phenotype-associations-for-a-specific-phenotyping-center">
        <span class="octicon octicon-link"></span></a>
    Retrieve all genotype-phenotype associations for a specific phenotyping center</h3>
<p>Now we constrain the results by adding a condition to the <strong>q</strong> (query) parameter using the specific
    <strong>phenotyping_center</strong> field. To retrieve all MP associations to the "WTSI" (Wellcome Trust Sanger Institute)
    phenotyping center, specify <strong>q=phenotyping_center:"WTSI"</strong></p>
<div class="highlight highlight-bash">
                                        <pre>
    <a href='http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=phenotyping_center:"WTSI"&amp;wt=json&indent=1' target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=phenotyping_center:"WTSI"&amp;wt=json&indent=1</a>
                                        </pre>
</div>

<h3>
    <a name="user-content-get-the-phenotyping-resource-names"
       class="anchor"
       href="#get-the-phenotyping-resource-names">
        <span class="octicon octicon-link"></span></a>
    Get the phenotyping resource names</h3>
<p>Start by a simple request to get the different phenotyping resource names (EuroPhenome, MGP, IMPC).
    This will be the basis to filter historical phenotyping resources like EuroPhenome or active resources
    like the IMPC project.</p>

<p>Solr queries are based on filters and facets. Using facets enables the retrieval of distinct values
    from a specific field. Using filters enables us to sub-select specific fields to retrieve, or,
    alternatively, all the fields from a Solr document. In this example we want to retrieve the distinct
    phenotyping resource names. The fields we are interested in are <strong>resource_name</strong> and <strong>resource_fullname</strong>.</p>
<div class="highlight highlight-bash">
                                        <pre>
    <a href='http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select/?q=*:*&amp;version=2.2&amp;start=0&amp;rows=0&amp;indent=on&amp;wt=json&amp;fl=resource_name&amp;fl=resource_fullname&amp;facet=on&amp;facet.field=resource_fullname&amp;facet.field=resource_name' target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select/?q=*:*&amp;version=2.2&amp;start=0&amp;rows=0&amp;indent=on&amp;wt=json&amp;fl=resource_name&amp;fl=resource_fullname&amp;facet=on&amp;facet.field=resource_fullname&amp;facet.field=resource_name</a>
                                        </pre>
</div>
<p>If you look carefully at the request:</p>
<ul class="task-list">
    <li>parameter <strong>fl</strong> means 'filter': we want to filter the results and keep only the <strong>resource_fullname</strong> and <strong>resource-name</strong> fields</li>
    <li>parameter <strong>facet=on</strong> means we want to have faceted results</li>
    <li>parameter <strong>facet.field</strong> means we are looking at all the possible combinations of <strong>resource_name</strong> and <strong>resource_fullname</strong></li>
    <li>parameter <strong>q</strong> is the query parameter. <strong>q=*</strong> means we don't want any text matching and want to get all
        the <strong>resource_name</strong> and <strong>resource_fullname</strong> results.</li>
</ul>
<p>Next, we look at more advanced query parameter examples.</p>

<h3>
    <a name="user-content-retrieve-all-the-phenotyping-projects"
       class="anchor"
       href="#retrieve-all-the-phenotyping-projects">
        <span class="octicon octicon-link"></span>
    </a>
    Retrieve all the phenotyping projects
</h3>
<p>In this example, only the selected field changes. Use the <strong>project_name</strong> and/or  <strong>project_fullname </strong> fields.</p>
<div class="highlight highlight-bash">
                                        <pre>
    <a href='http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select/?q=*:*&amp;version=2.2&amp;start=0&amp;rows=0&amp;indent=on&amp;wt=json&amp;fl=project_name&amp;facet=on&amp;facet.field=project_name' target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select/?q=*:*&amp;version=2.2&amp;start=0&amp;rows=0&amp;indent=on&amp;wt=json&amp;fl=project_name&amp;facet=on&amp;facet.field=project_name</a>
                                        </pre>
</div>

<h3>
    <a name="user-content-retrieve-all-pipelines-from-a-specific-project"
       class="anchor"
       href="#retrieve-all-pipelines-from-a-specific-project">
        <span class="octicon octicon-link"></span></a>
    Retrieve all pipelines from a specific project
</h3>
<p>To retrieve all the phenotyping pipelines from EUMODIC, we'll use the <strong>fq</strong> (filter query)
    parameter to filter the query on project_name:EUMODIC.
    As we are only interested at the distinct pipeline names, we'll use the <strong>facet.field</strong>
    parameter to facet on <strong>pipeline_name</strong>.</p>
<div class="highlight highlight-bash">
                                        <pre>
    <a href='http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=*:*&amp;fq=project_name:EUMODIC&amp;rows=0&amp;fl=project_name,pipeline_name&amp;facet=on&amp;facet.field=pipeline_name&amp;facet.mincount=1&amp;wt=json&indent=1' target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=*:*&amp;fq=project_name:EUMODIC&amp;rows=0&amp;fl=project_name,pipeline_name&amp;facet=on&amp;facet.field=pipeline_name&amp;facet.mincount=1&amp;wt=json&indent=1</a>
                                        </pre>
</div>

<h3>
    <a name="user-content-retrieve-all-procedures-from-a-specific-pipeline"
       class="anchor"
       href="#retrieve-all-procedures-from-a-specific-pipeline">
        <span class="octicon octicon-link"></span></a>
    Retrieve all procedures from a specific pipeline
</h3>
<p>Again, we'll use the <strong>fq</strong> command to filter the query on pipeline_name using double-quotes
    and select the <strong>facet.field</strong> called <strong>procedure_name</strong>.</p><i>Note the use of
    &nbsp;</i><strong>%20</strong><i> replacing the spaces between the words.</i>
<div class="highlight highlight-bash">
                                        <pre>
    <a href='http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=*:*&amp;fq=pipeline_name:"EUMODIC%20Pipeline%201"&amp;rows=0&amp;fl=procedure_name,pipeline_name&amp;facet=on&amp;facet.field=procedure_name&amp;facet.mincount=1&amp;wt=json&indent=1' target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=*:*&amp;fq=pipeline_name:"EUMODIC%20Pipeline%201"&amp;rows=0&amp;fl=procedure_name,pipeline_name&amp;facet=on&amp;facet.field=procedure_name&amp;facet.mincount=1&amp;wt=json&indent=1</a>
                                        </pre>
</div>

<h3>
    <a name="user-content-retrieve-all-parameters-from-a-specific-procedure"
       class="anchor" href="#retrieve-all-parameters-from-a-specific-procedure">
        <span class="octicon octicon-link"></span></a>
    Retrieve all parameters from a specific procedure which produced an MP call
</h3>
<p><i>Note the use of &nbsp;</i><strong>%20</strong><i> replacing the spaces between the words.</i></p>
<div class="highlight highlight-bash">
                                        <pre>
    <a href='http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=*:*&amp;fq=pipeline_name:"EUMODIC%20Pipeline%201"&amp;fq=procedure_name:"Calorimetry"&amp;rows=0&amp;fl=procedure_name,parameter_name&amp;facet=on&amp;facet.field=parameter_name&amp;facet.mincount=1&facet.limit=-1&amp;wt=json&indent=1' target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=*:*&amp;fq=pipeline_name:"EUMODIC%20Pipeline%201"&amp;fq=procedure_name:"Calorimetry"&amp;rows=0&amp;fl=procedure_name,parameter_name&amp;facet=on&amp;facet.field=parameter_name&amp;facet.mincount=1&facet.limit=-1&amp;wt=json&indent=1</a>
                                        </pre>
</div>

<h3>
    <a name="user-content-retrieve-all-mp-calls-grouped-by-top-level-mp-terms-first-and-then-by-resources-mgp-europhenome" class="anchor" href="#retrieve-all-mp-calls-grouped-by-top-level-mp-terms-first-and-then-by-resources-mgp-europhenome"><span class="octicon octicon-link"></span></a>Retrieve all MP calls grouped by top level MP terms first and then by resources (MGP, EuroPhenome)</h3>

<div class="highlight highlight-bash">
                                        <pre>
    <a href='http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select/?q=*:*&amp;version=2.2&amp;start=0&amp;rows=0&amp;indent=on&amp;wt=json&amp;fq=-resource_name:%22IMPC%22&amp;fl=top_level_mp_term_name&amp;facet=on&amp;facet.pivot=top_level_mp_term_name,resource_name' target="_blank">http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select/?q=*:*&amp;version=2.2&amp;start=0&amp;rows=0&amp;indent=on&amp;wt=json&amp;fq=-resource_name:%22IMPC%22&amp;fl=top_level_mp_term_name&amp;facet=on&amp;facet.pivot=top_level_mp_term_name,resource_name</a>
                                        </pre>
</div>
