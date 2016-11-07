
        <h1>REST API documentation for IMPC images access</h1>

        <p>The experimental data set contains images from experiments conducted for the Europhenome project
           (see <a href="http://www.europhenome.org">http://www.europhenome.org</a>) and for the International
           Mouse Phenotype Consortium (IMPC) project (see <a href="http://www.mousephenotype.org">http://www.mousephenotype.org</a>).
           A record in this resource represents a single data point for an experiment. The list of experiments performed is documented
           in the International Mouse Phenotyping Resource of Standardised Screens (IMPREeSS,
           see <a href="http://www.mousephenotype.org/impress">http://www.mousephenotype.org/impress</a>).
           Individual data points are associated to an IMPReSS <strong>Parameter</strong>. Parameters are organised
           into Procedures. Procedures are organised into Pipelines.</p>

        <p>There are many ways to select and filter image records, e.g.:</p>

        <ul>
            <li>all images for a parameter</li>
            <li>all images for a gene for one experiment</li>
            <li>all images for a specific pipeline</li>
        </ul>

        <p>The impc images data REST API provides the fields described in the table below which are mostly the same as for
        the experiment API with some additions. Each field may be used to restrict the set of impc image data you wish to
        receive. The full SOLR select syntax is available for use in querying the REST API.
        See <a href="http://wiki.apache.org/solr/SolrQuerySyntax">http://wiki.apache.org/solr/SolrQuerySyntax</a>
        and <a href="http://wiki.apache.org/solr/CommonQueryParameters">http://wiki.apache.org/solr/CommonQueryParameters</a>
        for a more complete list of query options.</p>

		<h3>Image specific parameters:</h3>

 			<table>
            <thead>
                <tr>
                    <th>Field name</th>
                    <th>Datatype</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>omero_id</td>
                    <td>int</td>
                    <td>the omero image id which uniquely signifies the image in the omero image system </td>
                </tr>
                <tr>
                    <td>download_url</td>
                    <td>string</td>
                    <td>Url from where the original image can be downloaded - the format can be one of many including dicom, tiff, jpeg</td>
                </tr>
                <tr>
                    <td>jpeg_url</td>
                    <td>string</td>
                    <td>Url where a high resolution jpeg of the image can be obtained</td>
                </tr>
                </tbody>
                </table>


		<h3>General experimental parameters:</h3>



        <table>
            <thead>
                <tr>
                    <th>Field name</th>
                    <th>Datatype</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>phenotyping_center</td>
                    <td>string</td>
                    <td>the name of the organisation that performed the experiment</td>
                </tr>
                <tr>
                    <td>gene_accession_id</td>
                    <td>string</td>
                    <td>the gene MGI ID (<a href="http://www.informatics.jax.org">http://www.informatics.jax.org</a>) of the mutant specimen used for the experiment</td>
                </tr>
                <tr>
                    <td>gene_symbol</td>
                    <td>string</td>
                    <td>the gene symbol of the mutant specimen used for the experiment</td>
                </tr>
                <tr>
                    <td>allele_accession_id</td>
                    <td>string</td>
                    <td>the allele MGI ID (<a href="http://www.informatics.jax.org">http://www.informatics.jax.org</a>) of the mutant specimen used for the experiment</td>
                </tr>
                <tr>
                    <td>allele_symbol</td>
                    <td>string</td>
                    <td>the allele symbol of the mutant specimen used for the experiment</td>
                </tr>
                <tr>
                    <td>zygosity</td>
                    <td>string</td>
                    <td>indicating the zygosity of the specimen</td>
                </tr>
                <tr>
                    <td>sex</td>
                    <td>string</td>
                    <td>indicating the sex of the specimen</td>
                </tr>
                <tr>
                    <td>biological_sample_group</td>
                    <td>string</td>
                    <td>indicating if the specimen was a member of the control group or the experimental group (also see metadata_group)</td>
                </tr>
                <tr>
                    <td>metadata_group</td>
                    <td>string</td>
                    <td>a string indicating a group of experimental and control mice that have the same metadata (see also biological_sample_group)</td>
                </tr>
                <tr>
                    <td>metadata</td>
                    <td>list of strings</td>
                    <td>list showing all relevant metadata in effect when the data was collected</td>
                </tr>
                <tr>
                    <td><strike>strain_name</strike></td>
                    <td>string</td>
                    <td>Deprecated. Please see genetic_background description</td></tr>
                <tr>
                <tr>
                    <td>strain_accession_id</td>
                    <td>string</td>
                    <td>Background strain MGI accession ID (or IMPC ID when MGI accession is not available)</td>
                </tr>
                <tr>
                    <td>genetic_background</td>
                    <td>string</td>
                    <td>indicating the background strain name of the specimen</td>
                </tr>
                <tr>
                    <td>pipeline_name</td>
                    <td>string</td>
                    <td>indicating the name of the pipeline where the experiment was conducted</td>
                </tr>
                <tr>
                    <td>pipeline_stable_id</td>
                    <td>string</td>
                    <td>indicating the IMPReSS ID of the pipeline</td>
                </tr>
                <tr>
                    <td>procedure_stable_id</td>
                    <td>string</td>
                    <td>indicating the IMPReSS ID of the procedure</td>
                </tr>
                <tr>
                    <td>procedure_name</td>
                    <td>string</td>
                    <td>indicating the full name of the procedure</td>
                </tr>
                <tr>
                    <td>parameter_stable_id</td>
                    <td>string</td>
                    <td>indicating the IMPReSS ID of the parameter</td>
                </tr>
                <tr>
                    <td>parameter_name</td>
                    <td>string</td>
                    <td>indicating the full name of the parameter</td>
                </tr>
                <tr>
                    <td>experiment_source_id</td>
                    <td>string</td>
                    <td>indicating the experiment identifier at the center that performed it</td>
                </tr>
                <tr>
                    <td>observation_type</td>
                    <td>string</td>
                    <td>indicating the type of parameter (categorical, unidimensional, multidimensional, time series, metadata)</td>
                </tr>
                <tr>
                    <td>colony_id</td>
                    <td>string</td>
                    <td>indicating the name of the colony of the specimen</td>
                </tr>
                <tr>
                    <td>date_of_birth</td>
                    <td>date</td>
                    <td>indicating the date the specimen was born</td>
                </tr>
                <tr>
                    <td>date_of_experiment</td>
                    <td>date</td>
                    <td>indicating the date the data was collected</td>
                </tr>
                <tr>
                    <td>weight</td>
                    <td>float</td>
                    <td>indicating the weight of the specimen observed</td>
                </tr>
                <tr>
                    <td>weight_parameter_stable_id</td>
                    <td>string</td>
                    <td>indicating the particular weight parameter</td>
                </tr>
                <tr>
                    <td>weight_date</td>
                    <td>date</td>
                    <td>indicating the actual date the weight was observed</td>
                </tr>
                <tr>
                    <td>weight_days_old</td>
                    <td>int</td>
                    <td>indicating the age in days of the specimen when the weight was observed</td>
                </tr>
                <tr>
                    <td>data_point</td>
                    <td>float</td>
                    <td>indicates the measured data value <sup>[1][2][3]</sup></td>
                </tr>
                <tr>
                    <td>order_index</td>
                    <td>int</td>
                    <td>indicating the order <sup>[2]</sup></td>
                </tr>
                <tr>
                    <td>dimension</td>
                    <td>string</td>
                    <td>indicating the dimension <sup>[2]</sup></td>
                </tr>
                <tr>
                    <td>time_point</td>
                    <td>string</td>
                    <td>indicating the time the data value was measured <sup>[3]</sup></td>
                </tr>
                <tr>
                    <td>discrete_point</td>
                    <td>float</td>
                    <td>indicating the discrete point <sup>[3]</sup></td>
                </tr>
                <tr>
                    <td>category</td>
                    <td>string</td>
                    <td>indicating the category to which the specimen has been classified <sup>[4]</sup></td>
                </tr>
                <tr>
                    <td>value</td>
                    <td>string</td>
                    <td>the value of the metadata <sup>[5]</sup></td>
                </tr>
            </tbody>
        </table>

        <blockquote>
            <sub>[1] - For unidimensional parameters [2] - For multidimensional parameters [3] - For time series parameters [4] - For categorical parameters [5] - For metadata parameters</sub>
        </blockquote>
        
        <h4>Examples</h4>

        <p><i>NOTE: Certain characters, most notably spaces and the "&lt;" and "&gt;" characters, must be url encoded (space = %20, &lt;&nbsp;=&nbsp;%3c, &gt;&nbsp;=&nbsp;%3e) for command line usage</i>.</p>

        <p>Retrieve the first 10 image results for parameter IMPC_XRY_034_001 "XRay Images Dorso Ventral", for colony MEAW</p>
        <div class="highlight highlight-bash">
            <pre>
curl <span class="se">\</span>
<span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/impc_images/select?q=parameter_stable_id:IMPC_XRY_034_001%20AND%20colony_id:MEAW&amp;wt=json&amp;indent=true'</span>
            </pre>
        </div>

        <p>Retrieve the first 1000 image results for parameter IMPC_XRY_034_001 "XRay Images Dorso Ventral", for colony MEAW
           (Same as above, except append <strong>&rows=1000</strong>)</p>
        <div class="highlight highlight-bash">
            <pre>
curl <span class="se">\</span>
<span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/impc_images/select?q=parameter_stable_id:IMPC_XRY_034_001%20AND%20colony_id:MEAW&amp;wt=json&amp;indent=true&amp;rows=1000'</span>
            </pre>

        </div>
        <p>Retrieve impc image results for organisation WTSI</p>
        <div class="highlight highlight-bash"><pre>curl <span class="se">\</span>
<span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/impc_images/select?q=phenotyping_center:WTSI&amp;wt=json&amp;indent=true'</span>
</pre>

        </div>
        <p>Retrieve impc image results for MRC Harwell for procedure name Echo </p>
        <div class="highlight highlight-bash"><pre>curl <span class="se">\</span>
            <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/impc_images/select?q=phenotyping_center:%22MRC%20Harwell%22%20AND%20procedure_name:%20Echo%20&amp;wt=json&amp;indent=true'</span>
            </pre>
        </div><%-- end of content div--%>
