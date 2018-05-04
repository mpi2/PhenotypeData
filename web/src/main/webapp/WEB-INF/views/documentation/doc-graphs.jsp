
        <h1>More information about the way IMPC uses graphs.</h1>
        <br/>
        <h3 id="data">Where is the data coming from?</h3>
        <p>Data for the charts are obtained from the IMPC phenotyping centers and from the <a
                href="http://www.europhenome.org/">
            Europhenome</a> legacy project. The table of genotype to phenotype associations on the gene and phenotype pages
            includes
            links to charts of the data and more information about the processing used to determine statistical significance.
        </p>

        <p> The P values and other model fitting estimates are calculated using the <a href="${baseUrl}/documentation/doc-method">IMPC statistical
            methods</a>.
            <img src="img/graph-links.png">
        </p>


        <h3 id="interactions">Interacting with Graphs</h3>
        <p>Charts are interactive so that you can adjust the view to your liking. Click on a legend to remove a set of data from
            the graph.
            This is especially useful if you wish to remove "noise" from a chart and focus on the control or experimental data.
            <br/><br/>
            <img src="img/graph-legends.png">
        </p>

        <p>After clicking on the control legend, the male homozygote data has disappeared:</p>
        <img src="img/graph-no-control.png" alt="chart with no control here">

        <p>Hovering over a data point or error bars displays extra information about the data point: </p>
        <img src="img/graph-hover.png" alt="chart with a hovering over mouse label here">

        <p>If appropriate, the chart will allow you to zoom in on a data set by clicking and dragging to create a
            square/zoomable area:</p>
        <img src="img/graph-before-zoom.png" alt="Scatter chart with no zoom shown here">

        <p>Once zoomed, a "Reset zoom" button appears at the right of the chart to enable the chart to be reset to original
            position:</p>
        <img src="img/graph-after-zoom.png" alt="Zoomed in chart here">

        <p> Parts of the chart header are also interactive. Clicking on the pipeline and procedure links will take you to the
            IMPReSS page describing them. </p>
        <img src="img/graph-header.png" alt="Chart header"/>
        <br/><br/><br/><br/>


        <h3 id="export">Exporting Chart Data</h3>
        <p>An export button is always visible on the right hand side of the charts where the chart picture can be exported in
            png, jpeg, pdf or svg format which will be downloaded to your computer:
            <img src="img/graph-export.png" alt="export button in chart here">

        <p>The data used to generate the charts can be downloaded in TSV or XLS formats from the buttons on the top of the page
            (<img src="img/graph-data-export.png"
                  alt="export button in chart here">). This will export data for all charts shown on the page. Data for males
            and females from the same experiment will be shown
            in the same table while for different zygosities, organizations or strains there will be separate tables exported in
            the same file. Following we present a list of
            exported values accompanied by a short description when needed.
        </p>

        <table>
            <tr>
                <th width="20%">Column</th>
                <th> Description</th>
            </tr>
            <tr>
                <td> pipelineName</td>
                <td> Pipeline through which the phenotyping was done, e.g. EUMODIC pipelines</td>
            </tr>
            <tr>
                <td> pipelineStableId</td>
                <td> Pipeline id</td>
            </tr>
            <tr>
                <td> procedureStableId</td>
                <td> Procedure id</td>
            </tr>
            <tr>
                <td> procedureName</td>
                <td> Procedure name</td>
            </tr>
            <tr>
                <td> parameterStableId</td>
                <td> Parameter id</td>
            </tr>
            <tr>
                <td> parameterName</td>
                <td> Parameter name</td>
            </tr>
            <tr>
                <td> strain</td>
                <td> Mouse strain used</td>
            </tr>
            <tr>
                <td> geneSymbol</td>
                <td> Gene symbol</td>
            </tr>
            <tr>
                <td> geneAccession</td>
                <td> Gene MGI accession id</td>
            </tr>
            <tr>
                <td> organisation</td>
                <td> Organization name</td>
            </tr>
            <tr>
                <td> colonyId</td>
                <td> Colony id</td>
            </tr>
            <tr>
                <td> dateOfExperiment</td>
                <td> Date of experiment</td>
            </tr>
            <tr>
                <td> externalSampleId</td>
                <td> Animal id</td>
            </tr>
            <tr>
                <td> zygosity</td>
                <td> Zygosity</td>
            </tr>
            <tr>
                <td> sex</td>
                <td> Sex</td>
            </tr>
            <tr>
                <td> group</td>
                <td> This field has only 2 values: control or experiment</td>
            </tr>
            <tr>
                <td> category</td>
                <td> Column is exported only for categorical data. It contains the label of the category assigned.</td>
            </tr>
            <tr>
                <td> meta data</td>
                <td> Shows any meta data in respect of equipment used which can be used to seperate the data sets used for
                    statistical analysis
                </td>
            </tr>
            <tr>
                <td> dataPoint</td>
                <td> Coulmn is exported for unidimensional or time series data. It contains the value resulted from the
                    measurment decribed by the current procedure.
                </td>
            </tr>
            <tr>
                <td> discretePoint</td>
                <td> Column exported for time series data. It contains the relative timepoints at which the measurments were
                    made.
                </td>
            </tr>
        </table>
        <br/>
        <p> A <b>restful web service</b> (with <a href="${baseUrl}/documentation/data-access"> Documentation</a>) is also available for retrieving
            information
            pertaining to experiments via a web browser or a programming language of your choice.
        </p>


        <h3 id="types">Types of Charts and Equations</h3>
        <p>The following types of chart exist in the IMPC portal from the IMPC:</p>
        <ol>
            <li><a href="#categorical-graphs">Categorical Bar Graphs</a></li>
            <li><a href="#undimensional-graphs">Unidimensional Scatter and Box Plot Graphs</a></li>
            <li><a href="#time-graphs">Time Series Graphs</a></li>
            <li><a href="#pie-graphs">Pie Graphs graphing viability</a></li>
            <li><a href="#abr-graphs">ABR graphs showing auditory brain stem response</a></li>
        </ol>
        <br/><br/>


        <h3 id="categorical-graphs">Categorical Bar Graphs</h3>
        <img src="img/graph-categorical-normal.png">
        <p>Categorical charts contain data where an observation can be categorised into one of two or more groups e.g. Abnormal
            Eye or Normal Eye.
            Charts are presented as bar charts with a table underneath. If IMPC data is available this will be displayed (see <a
                    href="documentation/doc-method#statistics">
                statistics help</a> for more information).
        </p>


        <h3 id="undimensional-graphs">Unidimensional Scatter and Box Plot Graphs</h3>

        <img src="img/graph-box-normal.png">
        <br/><br/>
        <p>Where an observation can be measured on a continuous basis (e.g. red blood cell counts or tail length), we display
            them in a mixed box and scatter plot.
            The first columns contain box plots for Wild Type (control), Homozygote then Heterozygote mutants for female, then
            the columns representing males.
            The second set of columns contains scatter plots for the same sets of data. Hover over the box in the chart to see
            the basic statistics for that set of
            data.
        </p>
        <p>When the data for a parameter is collected at various points in time, a scatter plot shows each data value on the
            y-axis
            and the date/time the data was collected on the x-axis.
        </p>

        <p>Below the chart is a table showing the output of the statistical test used to determine if this data is statistically
            significant:</p>
        <br/>
        <img src="img/stats-mm-table.png">

        <p>In the top left of the table is the global P value used to determine if this is a statistically significant
            result.<br/>
            The Classification column displays the effect of sexual dimorphism on this association
            (<a href="http://en.wikipedia.org/wiki/Sexual_dimorphism">sexual dimorphism</a> is the phenomenon when the genotype
            affects the sexes differently).
            The possible results are:
        </p>

        <dl>
            <dt>No significant change</dt>
            <dd>The effect is not statistically significant</dd>
            <dt>Cannot differentiate genders</dt>
            <dd>For the measured parameter, there is a significant effect, but there is not enough statistical power to
                determine sexual dimorphism
            </dd>
            <dt>Both genders equally</dt>
            <dd>For the measured parameter, both sexes are affected equally</dd>
            <dt>Female only</dt>
            <dd>For the measured parameter, only the females are affected</dd>
            <dt>Male only</dt>
            <dd>For the measured parameter, only the males are affected</dd>
            <dt>Different effect size, females greater</dt>
            <dd>For the measured parameter, there is an effect on males but the size of the effect on females is greater.</dd>
            <dt>Different effect size, males greater</dt>
            <dd>For the measured parameter, there is an effect on females but the size of the effect on males is greater.</dd>
            <dt>Female and male different directions</dt>
            <dd>For the measured parameter, the effect on one sex is increased while the other sex is decreased.</dd>
            <dt>If phenotype is significant it is for the one genotype tested</dt>
            <dd>For the measured parameter, there was no data for one of the sexes, so sexual dimorphism cannot be determined.
            </dd>
        </dl>

        <p> The <strong>more statistics</strong> link at the bottom of the table displays additional output related to the
            statistical method. </p>

        <h3 id="time-graphs">Time Series Graphs</h3>
        <img src="img/graph-time-series.png">
        <p>Where an observation can be measured as a time series (e.g. mean blood glucose concentration), we display the data in
            a line chart and
            scatter plot. The line chart will contain lines for wild-type and mutant data for female and for males. The data
            points displayed are
            the mean of all data collected at that timepoint and the whiskers indicate standard deviation. Hover over the points
            to see basic
            statistics about the data.
        </p>

        <h3 id="pie-graphs">Pie Graphs</h3>
        <img src="img/graph-pie.png">
        <p>Pie graphs are used to display the results of the primary viability screen. The screen is used to assess the
            postnatal viability, sub-viability, and lethality of homozygous mice during cohort production.
        </p>
        <h3 id="abr-graphs">ABR Charts</h3>

        <img src="img/graph-abr.png">
        <p>Auditory Brain Stem Responce data is plotted in the context of the whole serie of ABR measurements, as opposed to
            plotting each parameter
            separately. We plot data from the following parameters: click, 6kHz, 12kHz, 18kHz, 24kHz, 30kHz, where the click is
            separated from the rest of the data.
            For each parameter we plot the mean of it's respective measurements. If one parameter does not have any points it
            means no data is available.
        </p>
