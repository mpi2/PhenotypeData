<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage-landing>

    <jsp:attribute name="title">Histopath Landing Page</jsp:attribute>
    <jsp:attribute name="header">
   <script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/modules/heatmap.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
    </jsp:attribute>

    <jsp:attribute name="addToFooter">

        <script>
            $(document).ready(function () {
                Highcharts.chart('heatmap-container', {

                    chart: {
                        type: 'heatmap',
                        marginTop: 220,
                        marginBottom: 80,
                        plotBorderWidth: 1,
                        height: 11000,
                        width: 1100,
                    },
                    title: {
                        text: ''
                    },
                    colorAxis: {

                        dataClasses: [{
                            from: 0,
                            to: 1,
                            color: '#fff',
                            name: 'No Data'
                        }, {
                            from: 1,
                            to: 2,
                            color: '#808080',
                            name: 'Not Applicable'
                        }, {
                            from: 2,
                            to: 3,
                            color: '#17a2b8',
                            name: 'Not Significantly Different WT vs KO'
                        }, {
                            from: 3,
                            to: 4,
                            color: '#ce6211',
                            name: 'Significantly Different WT vs KO'
                        }
                        ],
                        min: 0,
                        max: 4,
                    },
                    legend: {
                        align: 'left',
                        // layout: 'vertical',
                        margin: 3,
                        verticalAlign: 'top',
                        backgroundColor: 'whitesmoke',
                        itemStyle: {
                            fontSize: '16px',
                            // font: '20pt Trebuchet MS, Verdana, sans-serif',
                            // color: '#A0A0A0'
                        },

                    },

                    xAxis: {
                        opposite: true,
                        categories: ${anatomyHeaders}, //['Alexander', 'Marie', 'Maximilian', 'Sophia', 'Lukas', 'Maria', 'Leon', 'Anna', 'Tim', 'Laura'],
                        labels: {
                            // useHTML: true,
                            rotation: 90
                        },
                        reserveSpace: true,
                    },

                    yAxis:
                    // [
                        {
                            categories: ${geneSymbols},
                            title: 'gene'
                        },
                    // {
                    //  linkedTo : 0,
                    //   title: 'construct',
                    //   lineWidth: 2,
                    //   categories: this.constructs
                    // }
                    // ],

                    tooltip: {
                        // shadow: false,
                        useHTML: true,
                        formatter: function () {
                            return '<b>' + this.series.xAxis.categories[this.point.x] + '</b><br/>' +
                                this.series.colorAxis.dataClasses[this.point.dataClass].name + '</b><br>' +
                                '<b>' + this.series.yAxis.categories[this.point.y] + '</b>';
                        }
                    },

                    plotOptions: {
                        series: {
                            cursor: 'pointer',
                            events: {
                                click: function (e) {
                                    const gene = e.point.series.yAxis.categories[e.point.y];

                                    const procedure = e.point.series.xAxis.categories[e.point.x];

                                    const text = 'gene: ' + gene +
                                        ' Procedure: ' + procedure + ' significance=' + e.point.value;

                                    // may have to use routerLink like for menus to link to our new not created yet parameter page
                                    //const routerLink = 'details?' + 'procedure=' + procedure + '&gene=' + gene;
                                    const routerLink = 'histopathsum/' + gene;
                                    window.open(routerLink, '_blank');
                                }
                            },
                        },
                    },

                    series: [{
                        name: 'Cell types with significant parameters',
                        borderWidth: 1,
                        // data: this.data,
                        data:  ${data},
                        dataLabels: {
                            enabled: false,
                            color: '#000000'
                        }
                    }],
                });

            });
        </script>

    </jsp:attribute>

    <jsp:body>
        <div class="container single single--no-side">

            <div class="breadcrumbs" style="box-shadow: none; margin-top: auto; margin: auto; padding: auto">

                <div class="row">
                    <div class="col-md-12">
                        <p><a href="/">Home</a>
                            <span class="fal fa-angle-right"></span> Histopathology
                        </p>
                    </div>
                </div>
            </div>

            <div class="row row-over-shadow">
                <div class="col-md-12 white-bg">
                    <div class="page-content">

                        <h2>Histopathology for ${gene.markerSymbol}</h2>
                        <p>Gene name: ${gene.markerName}</p>

                        <div class="card">
                            <div class="card-header">Score Definitions</div>
                            <div class="card-body">
                                <p class="my-0"><b>Severity Score:</b></p>
                                <ul class="my-0">
                                    <li>0 = Normal,</li>
                                    <li>1 = Mild (observation barely perceptible and not believed to have clinical
                                        significance),
                                    </li>
                                    <li>2 = Moderate (observation visible but involves minor proportion of tissue and
                                        clinical consequences of observation are most likely subclinical),
                                    </li>
                                    <li>3 = Marked (observation clearly visible involves a significant proportion of
                                        tissue and is likely to have some clinical manifestations generally expected to
                                        be minor),
                                    </li>
                                    <li>4 = Severe (observation clearly visible involves a major proportion of tissue
                                        and clinical manifestations are likely associated with significant tissue
                                        dysfunction or damage)
                                    </li>
                                </ul>

                                <p class="my-0"><b>Significance Score:</b></p>
                                <ul class="my-0">
                                    <li>0 = Not significant (histopathology finding that is interpreted by the
                                        histopathologist to be within normal limits of background strain-related
                                        findings or an incidental finding not related to genotype)
                                    </li>
                                    <li>1 = Significant (histopathology finding that is interpreted by the
                                        histopathologist to not be a background strain-related finding or an incidental
                                        finding)
                                    </li>
                                </ul>
                            </div>
                        </div>



                        <div id="heatmap-container">
                        </div>

                    </div>
                </div>
            </div>





        </div>

    </jsp:body>


    </t:genericpage-landing>

