<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

    <jsp:attribute name="title">Histopath</jsp:attribute>
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
                    credits: {
                        enabled: false
                    },
                    colorAxis: {
                        events: {
                            legendItemClick: function() {
                                return false;
                            }
                        },

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
                            name: 'Not Significant'
                        }, {
                            from: 3,
                            to: 4,
                            color: '#ce6211',
                            name: 'Significant'
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

                                    legendItemClick: function () {
                                        var visibility = this.visible ? 'visible' : 'hidden';
                                        if (!confirm('The series is currently ' +
                                            visibility + '. Do you want to change that?')) {
                                            return false;
                                        }
                                    },

                                click: function (e) {
                                    const gene = e.point.series.yAxis.categories[e.point.y];

                                    const anatomy = e.point.series.xAxis.categories[e.point.x];

                                    const text = 'gene: ' + gene +
                                        ' Procedure: ' + anatomy + ' significance=' + e.point.value;

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
                        },
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

                        <div class="card">
                            <div class="card-header">Histopathology for every gene tested</div>
                            <div class="card-body">

                                <p class="my-0"><b>Significance Score:</b></p>
                                <ul class="my-0">
                                    <li><b>Not significant</b> (histopathology finding that is interpreted by the
                                        histopathologist to be within normal limits of background strain-related
                                        findings or an incidental finding not related to genotype)
                                    </li>
                                    <li><b>Significant</b> (histopathology finding that is interpreted by the
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


    </t:genericpage>

