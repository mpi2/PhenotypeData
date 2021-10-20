/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.chart;

import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.web.dto.ExperimentsDataTableRow;
import org.mousephenotype.cda.solr.web.dto.PhenotypeCallSummaryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class PhenomeChartProvider {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    public String createPvaluesOverviewChart(double minimalPValue, String pointFormat, JSONArray series, JSONArray categories, Map<String, Map<String, Integer >> procedureParameterMap)
            throws JSONException {


        String chartString = null;

        if (series.length() > 0) {
            JSONObject jsProcParamMap = new JSONObject();
            for (String proc : procedureParameterMap.keySet()) {
                jsProcParamMap.put(proc, new JSONArray(Arrays.asList(procedureParameterMap.get(proc).get("start"), procedureParameterMap.get(proc).get("end"))));
            }
            chartString = "\nvar jsProcParamMap = " + jsProcParamMap + ";\n\n\n";
            chartString +=
                    "(function (H) {\n" +
                    "  // Position highcharts checkboxes to the left of the label in the legend\n" +
                    "  H.wrap(H.Legend.prototype, 'positionCheckboxes', function (p, scrollOffset) {\n" +
                    "    var alignAttr = this.group.alignAttr, translateY, clipHeight = this.clipHeight || this.legendHeight;\n" +
                    "    if (alignAttr) {\n" +
                    "      translateY = alignAttr.translateY;\n" +
                    "      H.each(this.allItems, function (item) {\n" +
                    "        var checkbox = item.checkbox,\n" +
                    "        bBox = item.legendItem.getBBox(true), top;\n\n" +
                    "        if (checkbox) {\n" +
                    "          top = (translateY + checkbox.y + (scrollOffset || 0) + 3);\n" +
                    "          H.css(checkbox, {\n" +
                    "            left: (alignAttr.translateX + item.checkboxOffset + checkbox.x - 60 - bBox.width) + 'px',\n" +
                    "            top: top + 'px',\n" +
                    "            display: top > translateY - 6 && top < translateY + clipHeight - 6 ? '' : 'none'\n" +
                    "          });\n" +
                    "        }\n" +
                    "      });\n" +
                    "    }\n" +
                    "  });\n" +
                    "  \n" +
                    "  //beforeSetTickPositions\n" +
                    "  H.wrap(H.Axis.prototype, 'beforeSetTickPositions', function(proceed) {\n" +
                    "    var axis = this, breaks = axis.options.breaks;\n" +
                    "    axis.options.breaks = false;\n" +
                    "    proceed.apply(this, [].slice.call(arguments, 1));\n" +
                    "    axis.options.breaks = breaks;\n" +
                    "  });\n" +
                    "})(Highcharts);\n" +
                    "$(function () { \n"
                    + "  pvaluesOverviewChart = new Highcharts.Chart({ \n"
                    + "     chart: {\n"
                    + "       events: {\n"
                    + "         load: function() {\n"
                    + "           Highcharts.each(this.series, function() {\n"
                    + "             breaks.push({});\n"
                    + "           });\n"
                    + "         },\n"
                    + "       },"
                    + "       inverted: true,\n"
                    + "       ignoreHiddenSeries: true,\n"
                    + "       style: {\n"
                    + "         fontFamily: '\"Roboto\", sans-serif;'\n"
                    + "       },"
                    + "       renderTo: 'chartDiv',\n"
                    + "       type: 'scatter',\n"
                    + "       zoomType: 'xy',\n"
                    + "       height: 800\n"
                    + "     },\n"
                    + "     title: { text: '' },\n"
                    + "     subtitle: { text: '' },\n"
                    + "     xAxis: {\n"
                    + "       startOnTick: true," +
                            "tickPositioner: function () {\n" +
                            "    let suppressed = 0;\n" +
                            "    if (typeof this.brokenAxis !== 'undefined' && typeof this.brokenAxis.breakArray !== 'undefined') {\n" +
                            "        for (let i = 0; i < this.brokenAxis.breakArray.length; i++) {\n" +
                            "            suppressed += this.brokenAxis.breakArray[i].len;\n" +
                            "        }\n" +
                            "    }\n" +
                            "    var positions = [],\n" +
                            "        tick = Math.floor(this.dataMin),\n" +
                            "        increment = Math.ceil((this.dataMax - this.dataMin - suppressed) / 50);\n" +
                            "    if (this.dataMax !== null && this.dataMin !== null) {\n" +
                            "        for (tick; tick - increment <= this.dataMax; tick += increment) {\n" +
                            "            // If this tick falls within a broken axis section, skip it\n" +
                            "            if (typeof this.brokenAxis !== 'undefined' && typeof this.brokenAxis.breakArray !== 'undefined') {\n" +
                            "                let cont = true;\n" +
                            "                for (let i = 0; i < this.brokenAxis.breakArray.length; i++) {\n" +
                            "                    if (tick > this.brokenAxis.breakArray[i].from && tick < this.brokenAxis.breakArray[i].to) {\n" +
                            "                        cont = false;\n" +
                            "                        break\n" +
                            "                    }\n" +
                            "                }\n" +
                            "                if (!cont) continue;\n" +
                            "            }\n" +
                            "            positions.push(tick);\n" +
                            "        }\n" +
                            "    }\n" +
                            "    return positions;\n" +
                            "}\n,\n"
                    + "       gridLineWidth: 1,"
                    + "       categories: " + categories.toString() + ",\n"
                    + "       title: {\n"
                    + "         enabled: true,\n"
                    + "         text: 'Parameters' \n"
                    + "       }, \n"
                    + "     }, \n"
                    + "    yAxis: { floor: 0, minRange: 6, ceiling: 22, \n"
                            + "       gridLineWidth: 0,"
                    + "      title: { \n"
                    + "        text: '" + Constants.MINUS_LOG10_HTML + "(p-value)" + "' \n"
                    + "      }, \n"
                    + "      plotLines : [{\n"
                    + "	       value : " + -Math.log10(minimalPValue) + ",\n"
                    + "	       color : 'green', \n"
                    + "	       dashStyle : 'shortdash',\n"
                    + "	       width : 2,\n"
                    + "	       label : { text : 'Significance threshold " + minimalPValue + "' }\n"
                    + "	     }]"
                    + "    }, \n"
                    + "    credits: { \n"
                    + "      enabled: false \n"
                    + "    }, \n"
                    + "    legend: { symbolPadding: 25, symbolWidth: 10, layout: 'horizontal', align: 'left',	verticalAlign: 'top', borderWidth: 0, maxHeight: 200 },\n"
                    + "    tooltip: {\n"
                    + "      headerFormat: '<div class=\"card\"><div class=\"card-body\"><dl>',\n"
                    + "      pointFormat: '" + pointFormat + "',\n"
                    + "      footerFormat: '</dl></div></div>',\n"
                    + "      shared: 'true',\n"
                    + "      style: { opacity: 1, background: 'rgba(246, 246, 246, 1)' },\n"
                    + "      outside: false,\n"
                    + "      useHTML: 'true',\n"
                    + "    }, \n"
                    + "    plotOptions: { \n"
                    + "        scatter: { "
                    + "            showCheckbox: true,\n"
                    + "            cursor: 'pointer',\n"
                    + "            marker: { \n"
                    + "                radius: 5, \n"
                    + "                states: { \n"
                    + "                   hover: { \n"
                    + "                      enabled: true, \n"
                    + "                      lineColor: 'rgb(100,100,100)' \n"
                    + "                   } \n"
                    + "                } \n"
                    + "            }, \n"
                    + "            states: { \n"
                    + "               hover: { \n"
                    + "                  marker: { \n"
                    + "                     enabled: false \n"
                    + "                  } \n"
                    + "              } \n"
                    + "            }, \n\n"
                    + "            events: { \n"
                    + "              legendItemClick: function() { \n"
                    + "                if (this.visible) {\n"
                    + "                  this.selected = false; \n"
                    + "                  this.checkbox.checked = false;\n"
                    + "                  breaks[this.index] = {\n"
                    + "                    from: jsProcParamMap[this.name][0]-0.5,\n"
                    + "                    to: jsProcParamMap[this.name][1]+0.5,\n"
                    + "                    breakSize: 0\n"
                    + "                  };\n"
                    + "                  this.chart.xAxis[0].update({breaks: breaks});\n"
                    + "                } else {\n"
                    + "                  this.checkbox.checked = true;\n"
                    + "                  this.selected = true; \n"
                    + "                  breaks[this.index] = {}\n"
                    + "                  this.chart.xAxis[0].update({ breaks: breaks });\n"
                    + "                }\n"
                    + "              },\n"
                    + "              checkboxClick: function(event) {\n"
                    + "                if(this.checkbox.checked) {\n"
                    + "                  this.show();\n"
                    + "                  this.selected = false; \n"
                    + "                  this.checkbox.checked = false;\n"
                    + "                  breaks[this.index] = {}\n"
                    + "                  this.chart.xAxis[0].update({ breaks: breaks });\n"
                    + "                } else {\n"
                    + "                  this.hide();\n"
                    + "                  this.checkbox.checked = true;\n"
                    + "                  this.selected = true; \n"
                    + "                  breaks[this.index] = {\n"
                    + "                    from: jsProcParamMap[this.name][0]-0.5,\n"
                    + "                    to: jsProcParamMap[this.name][1]+0.5,\n"
                    + "                    breakSize: 0\n"
                    + "                  };\n"
                    + "                  this.chart.xAxis[0].update({breaks: breaks});\n"
                    + "                }\n"
                    + "              },\n"
                    + "              click: function(event) { \n"
                    + "     window.open(" + "base_url + '/charts?accession=' + event.point.geneAccession + "
                    + "'&parameter_stable_id=' + event.point.parameterStableId + '&allele_accession=' + event.point.alleleAccession + "
                    + "'&zygosity=' + event.point.zygosity + '&phenotyping_center=' + event.point.phenotypingCenter + "
                    + "'&bare=true'" + ", '_blank'); \n"
                    + "               } \n"
                    + "           } \n" // events
                    + "       } \n"
                    + "   }, \n"
                    + "     series: " + series.toString() + "\n"
                    + "    }); \n"
                    + ChartUtils.getSelectAllButtonJs("pvaluesOverviewChart", "checkAll", "uncheckAll")
                    + "	}); \n";
        }

        return chartString;
    }


    /**
     * Creates a highCharts Phenome summary view plotting p-values for every
     * significant call for every strain from every IMPReSS parameter for a
     * specific phenotyping center
     *
     * @param phenotypingCenter the specific phenotyping center
     * @param minimalPValue     set the minimal threshold
     * @param series            series of categories to plot
     * @param categories        list of categories (one for every MP term)
     * @return the chart to be displayed
     * @throws JSONException
     */
    public String createPhenomeChart(String phenotypingCenter, double minimalPValue, String pointFormat, JSONArray series, JSONArray categories)
            throws JSONException {

        String chartString = "	$(function () { \n"
                + "  phenomeChart = new Highcharts.Chart({ \n"
                + "     chart: {\n"
                + "			renderTo: 'phenomeChart',\n"
                + "         type: 'scatter',\n"
                + "         zoomType: 'xy',\n"
                + "         height: 800\n"
                + "     },\n"
                + "   title: {\n"
                + "       text: 'Significant MP calls'\n"
                + "    },\n"
                + "     subtitle: {\n"
                + "        text: 'by Top Level MP Categories'\n"
                + "    },\n"
                + "     xAxis: {\n"
                + "     categories: " + categories.toString() + ",\n"
                + "       labels: { \n"
                + "           rotation: -90, \n"
                + "           align: 'right', \n"
                + "           style: { \n"
                + "              fontSize: '10px', \n"
                + "              fontFamily: '\"Roboto\", sans-serif' \n"
                + "         } \n"
                + "     }, \n"
                + "      showLastLabel: true \n"
                + "  	}, \n"
                + "   	 yAxis: { \n"
                + "			min: 0,\n"
                + "			max: " + -Math.log10(1E-21) + ",\n"
                + "         title: { \n"
                + "             text: '" + Constants.MINUS_LOG10_HTML + "(p-value)" + "' \n"
                + "           }, \n"
                + "       }, \n"
                + "      credits: { \n"
                + "         enabled: false \n"
                + "      }, \n"
                + "     tooltip: {\n"
                + "        headerFormat: '<span style=\"font-size:10px\">{point.name}</span><table>',\n"
                + "        pointFormat: '" + pointFormat + "',\n"
                + "        footerFormat: '</table>',\n"
                + "        shared: 'true',\n"
                + "        useHTML: 'true',\n"
                + "     }, \n"
                + "      plotOptions: { \n"
                + "        scatter: { \n"
                + " cursor: 'pointer',"
                + "            marker: { \n"
                + "                radius: 5, \n"
                + "                states: { \n"
                + "                   hover: { \n"
                + "                      enabled: true, \n"
                + "                      lineColor: 'rgb(100,100,100)' \n"
                + "                   } \n"
                + "                } \n"
                + "            }, \n"
                + "            states: { \n"
                + "               hover: { \n"
                + "                  marker: { \n"
                + "                     enabled: false \n"
                + "                  } \n"
                + "              } \n"
                + "            }, \n"
                + "            events: { \n"
                + "               click: function(event) { \n"
                + "                   //var sexString = (event.point.sex == \"both\") ? '&gender=male&gender=female' : '&gender=' + event.point.sex; \n"
                + "                   $.fancybox.open([ \n"
                + "                  {\n"
                + "                     href : base_url + '/charts?accession=' + event.point.geneAccession +"
                + "								'&parameter_stable_id=' + event.point.parameter_stable_id + '&allele_accession=' + event.point.alleleAccession + "
                + "								'&zygosity=' + event.point.zygosity + '&phenotyping_center=' + event.point.phenotyping_center + "
                + "								'&pipeline_stable_id=' + event.point.pipeline_stable_id + '&bare=true', \n"
                + "                     title : event.point.geneAccession \n"
                + "                  } \n"
                + "                  ], \n"
                + "                   { \n"
                + "                     'maxWidth'          : 1000, \n" // 980 too
                // narrow
                + "                     'maxHeight'         : 900, \n"
                + "                     'fitToView'         : false, \n"
                + "                     'width'             : '100%',  \n"
                + "                     'height'            : '85%',  \n"
                + "                     'autoSize'          : false,  \n"
                + "                     'transitionIn'      : 'none', \n"
                + "                     'transitionOut'     : 'none', \n"
                + "                     'type'              : 'iframe', \n"
                + "                     scrolling           : 'auto' \n"
                + "                  }); \n"
                + "               } \n"
                + "           } \n" // events
                + "       } \n"
                + "   }, \n"
                + "     series: " + series.toString() + "\n"
                + "    }); \n"
                + "	}); \n";


        return chartString;
    }


    public String generatePhenomeChartByPhenotype(
            List<PhenotypeCallSummaryDTO> calls,
            String phenotypingCenter,
            double minimalPvalue)
            throws IOException,
            URISyntaxException {

        String chartString = null;

        JSONArray series = new JSONArray();

        JSONArray categories = new JSONArray();
        List<String> categoryGroupList = new ArrayList<String>();
        Map<String, List<String>> specificTermMatrix = new HashMap<String, List<String>>();

        Map<String, JSONObject> seriesMap = new HashMap<String, JSONObject>();

        try {

            // build tooltip
            StringBuilder pointFormat = new StringBuilder();

            pointFormat.append("<tr><td style=\"color:{series.color};padding:0\">Top Level MP: {series.name}</td></tr>");
            pointFormat.append("<tr><td style=\"padding:0\">MP Term: {point.mp_term}</td></tr>");
            pointFormat.append("<tr><td style=\"padding:0\">Gene: {point.geneSymbol}</td></tr>");
            pointFormat.append("<tr><td style=\"padding:0\">Zygosity: {point.zygosity}</td></tr>");
            pointFormat.append("<tr><td style=\"padding:0\">P-value: {point.pValue}</td></tr>");
            pointFormat.append("<tr><td style=\"padding:0\">Effect size: {point.effectSize}</td></tr>");

            // first grab all categories and associated terms

            for (PhenotypeCallSummaryDTO call : calls) {

                for (BasicBean topLevel : call.getTopLevelPhenotypeTerms()) {

                    String topLevelName = topLevel.getName();
                    if (!categoryGroupList.contains(topLevelName)) {

                        specificTermMatrix.put(topLevelName, new ArrayList<String>());
                        categoryGroupList.add(topLevelName);

                        JSONObject scatterJsonObject = new JSONObject();
                        seriesMap.put(topLevelName, scatterJsonObject);

                        scatterJsonObject.put("type", "scatter");
                        scatterJsonObject.put("name", topLevelName);

                        JSONArray dataArray = new JSONArray();

                        scatterJsonObject.put("data", dataArray);

                        series.put(scatterJsonObject);

                    }

                    if (!specificTermMatrix.get(topLevelName).contains(call.getPhenotypeTerm().getName())) {
                        specificTermMatrix.get(topLevelName).add(call.getPhenotypeTerm().getName());
                    }
                }
            }

            // Then generate categories for all of them
            int categoriesDim = 0;
            for (String categoryName : categoryGroupList) {
                for (String specificTerm : specificTermMatrix.get(categoryName)) {
                    categories.put((categoriesDim + 1) + ". " + specificTerm);
                }
                categoriesDim++;
            }
            // finally extract the data points and generate a point for every
            // top level categories associated
            for (PhenotypeCallSummaryDTO call : calls) {
                for (BasicBean topLevel : call.getTopLevelPhenotypeTerms()) {

                    String topLevelName = topLevel.getName();
                    int firstDim = categoryGroupList.indexOf(topLevelName);

                    // convert to position on x axis
                    int index = 0;
                    for (int i = 0; i <= firstDim; i++) {
                        index += (i != firstDim) ?
                                specificTermMatrix.get(categoryGroupList.get(i)).size() :
                                specificTermMatrix.get(topLevelName).indexOf(call.getPhenotypeTerm().getName());
                    }

                    JSONObject dataPoint = new JSONObject();
                    dataPoint.put("name", (firstDim + 1) + ". " + call.getPhenotypeTerm().getName());
                    dataPoint.put("mp_term", call.getPhenotypeTerm().getName());
                    dataPoint.put("geneSymbol", call.getGene().getSymbol());
                    dataPoint.put("geneAccession", call.getGene().getAccessionId());
                    dataPoint.put("alleleAccession", call.getAllele().getAccessionId());
                    dataPoint.put("parameter_stable_id", call.getParameter().getStableId());
                    dataPoint.put("pipeline_stable_id", call.getPipeline().getStableId());
                    dataPoint.put("phenotyping_center", call.getPhenotypingCenter());
                    dataPoint.put("x", index);
                    dataPoint.put("y", call.getLogValue() + addJitter(call.getEffectSize()));
                    dataPoint.put("pValue", call.getpValue());
                    dataPoint.put("effectSize", call.getEffectSize());
                    dataPoint.put("sex", call.getSex());
                    dataPoint.put("zygosity", call.getZygosity());

                    ((JSONArray) seriesMap.get(topLevelName).get("data")).put(dataPoint);

                }
            }

            // finally sort by index
            for (String topLevelName : categoryGroupList) {

                JSONArray array = ((JSONArray) seriesMap.get(topLevelName).get("data"));
                seriesMap.get(topLevelName).put("data", this.getSortedList(array));
            }

            chartString = createPhenomeChart(phenotypingCenter, minimalPvalue, pointFormat.toString(), series, categories);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return chartString;
    }

    public String generatePhenomeChartByGenes(
            List<PhenotypeCallSummaryDTO> calls,
            String phenotypingCenter,
            double minimalPvalue)
            throws IOException,
            URISyntaxException {

        String chartString = null;

        JSONArray series = new JSONArray();

        JSONArray categories = new JSONArray();
        List<String> categoryGroupList = new ArrayList<String>();
        List<String> specificTerms = new ArrayList<String>();

        Map<String, JSONObject> seriesMap = new HashMap<String, JSONObject>();

        try {

            // build tooltip
            StringBuilder pointFormat = new StringBuilder();

            pointFormat.append("<tr><td style=\"color:{series.color};padding:0\">Top Level MP: {series.name}</td></tr>");
            pointFormat.append("<tr><td style=\"padding:0\">MP Term: {point.mp_term}</td></tr>");
            pointFormat.append("<tr><td style=\"padding:0\">Gene: {point.geneSymbol}</td></tr>");
            pointFormat.append("<tr><td style=\"padding:0\">Zygosity: {point.zygosity}</td></tr>");
            pointFormat.append("<tr><td style=\"padding:0\">P-value: {point.pValue}</td></tr>");
            pointFormat.append("<tr><td style=\"padding:0\">Effect size: {point.effectSize}</td></tr>");

            // first grab all categories and associated terms

            Map<String, List<String>> phenotypeGroups = new HashMap<String, List<String>>();


            // Set phenotype order to show (x-axis)
            for (PhenotypeCallSummaryDTO call : calls) {
                if (call.getTopLevelPhenotypeTerms() != null) {
                    String topLevelName = call.getTopLevelPhenotypeTerms().get(0).getName();
                    if (!phenotypeGroups.containsKey(topLevelName)) {
                        phenotypeGroups.put(topLevelName, new ArrayList<String>());

                    }

                    if (!phenotypeGroups.get(topLevelName).contains(call.getPhenotypeTerm().getName())) {
                        phenotypeGroups.get(topLevelName).add(call.getPhenotypeTerm().getName());
                    }
                }
            }

            for (String topLevelMP : phenotypeGroups.keySet()) {
                for (String mp : phenotypeGroups.get(topLevelMP)) {
                    categoryGroupList.add(mp);
                    categories.put(mp);
                }
            }

            // get genes
            for (PhenotypeCallSummaryDTO call : calls) {

                String gene = call.getGene().getSymbol();
                if (!specificTerms.contains(gene)) {
                    specificTerms.add(gene);
                    JSONObject scatterJsonObject = new JSONObject();
                    seriesMap.put(gene, scatterJsonObject);
                    scatterJsonObject.put("name", gene);
                    JSONArray dataArray = new JSONArray();
                    scatterJsonObject.put("data", dataArray);
                    series.put(scatterJsonObject);
                }
            }


            // finally extract the data points and generate a point for every
            // top level categories associated.
            for (PhenotypeCallSummaryDTO call : calls) {

                String gene = call.getGene().getSymbol();
                int firstDim = categoryGroupList.indexOf(gene);

                // convert to position on x axis

                JSONObject dataPoint = new JSONObject();
                dataPoint.put("name", (firstDim + 1) + ". " + call.getPhenotypeTerm().getName());
                dataPoint.put("mp_term", call.getPhenotypeTerm().getName());
                dataPoint.put("geneSymbol", call.getGene().getSymbol());
                dataPoint.put("geneAccession", call.getGene().getAccessionId());
                dataPoint.put("alleleAccession", call.getAllele().getAccessionId());
                dataPoint.put("parameter_stable_id", call.getParameter().getStableId());
                dataPoint.put("pipeline_stable_id", call.getPipeline().getStableId());
                dataPoint.put("phenotyping_center", call.getPhenotypingCenter());
                dataPoint.put("x", categoryGroupList.indexOf(call.getPhenotypeTerm().getName()));
                dataPoint.put("y", call.getLogValue() + addJitter(call.getEffectSize()));
                dataPoint.put("pValue", call.getpValue());
                dataPoint.put("effectSize", call.getEffectSize());
                dataPoint.put("sex", call.getSex());
                dataPoint.put("zygosity", call.getZygosity());
                ((JSONArray) seriesMap.get(gene).get("data")).put(dataPoint);
            }

            // finally sort by index
            for (String geneSymbol : seriesMap.keySet()) {

                JSONArray array = ((JSONArray) seriesMap.get(geneSymbol).get("data"));
                seriesMap.get(geneSymbol).put("data", this.getSortedList(array));
            }

            chartString = createPhenomeChart(phenotypingCenter, minimalPvalue, pointFormat.toString(), series, categories);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return chartString;
    }


    /**
     * @param statisticalResults
     * @param minimalPvalue
     * @param parametersByProcedure
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public Map<String, Object> generatePvaluesOverviewChart(Map<String, List<ExperimentsDataTableRow>> statisticalResults, double minimalPvalue, Map<String, List<String>> parametersByProcedure)
            throws IOException, URISyntaxException {

        String chartString = null;
        int index = 0;
        JSONArray series = new JSONArray();
        ArrayList<String> categories = new ArrayList<String>();

        try {

            StringBuilder pointFormat = new StringBuilder();

            pointFormat.append("<dt style=\"color:{series.color};\">Parameter</dt><dd> {point.name}</dd>");
            pointFormat.append("<dt>Procedure</dt><dd> {series.name}</dd>");
            pointFormat.append("<dt>Zygosity</dt><dd> {point.zygosity}</dd>");
            pointFormat.append("<dt>Mutants</dt><dd> {point.femaleMutants}females / {point.maleMutants}males</dd>");
            pointFormat.append("<dt>Metadata group</dt><dd> {point.metadataGroup}</dd>");
            pointFormat.append("<dt>P-value</dt><dd> {point.pValue}</dd>");
            pointFormat.append("<dt>Effect size</dt><dd> {point.effectSize}</dd>");


            // Create a statistical series for every procedure in the pipeline
            // Start from the pipeline so that there is no need to keep this
            // information from the caller side
            // get All procedures and generate a Map Parameter => Procedure
            Map<String, Map<String, Integer>> procedureParameterMap = new HashMap<>();
            for (String procedure : parametersByProcedure.keySet()) {

                // A JSON version of procedureParameterMap will be used by the highcharts legend functions
                // to define categories to exclude from the axis labels when the series is deselected
                procedureParameterMap.put(procedure, new HashMap<>());
                procedureParameterMap.get(procedure).put("start", index);

                JSONObject scatterJsonObject = new JSONObject();
                JSONArray dataArray = new JSONArray();

                scatterJsonObject.put("type", "scatter");
                scatterJsonObject.put("name", procedure);
                scatterJsonObject.put("selected", Boolean.TRUE);

                // create a series here
                for (String parameterStableId : parametersByProcedure.get(procedure)) {

                    if (statisticalResults.containsKey(parameterStableId)) {
                        // Solr sorts empty fields first when ASC so we might need to loop over the results
                        ExperimentsDataTableRow statsResult = statisticalResults.get(parameterStableId).get(0);
                        int i = 1;
                        while (statsResult.getpValue() == null && i < statisticalResults.get(parameterStableId).size()) {
                            statsResult = statisticalResults.get(parameterStableId).get(i);
                            i++;
                        }

                        // smallest p-value is the first (solr docs are sorted)
                        if (!categories.contains(statsResult.getParameter().getName()) && statsResult.getpValue() != null) {

                            // create the point first
                            JSONObject dataPoint = new JSONObject();
                            dataPoint.put("name", statsResult.getParameter().getName());
                            dataPoint.put("parameterStableId", parameterStableId);
                            dataPoint.put("parameterName", statsResult.getParameter().getName());
                            dataPoint.put("geneAccession", statsResult.getGene().getAccessionId());
                            dataPoint.put("alleleAccession", statsResult.getAllele().getAccessionId());
                            dataPoint.put("phenotypingCenter", statsResult.getPhenotypingCenter());
                            dataPoint.put("x", index);
                            dataPoint.put("y", getLogValue(statsResult.getpValue()));
                            dataPoint.put("pValue", statsResult.getpValue());
                            dataPoint.put("effectSize", statsResult.getEffectSize());
                            dataPoint.put("zygosity", statsResult.getZygosity());
                            dataPoint.put("femaleMutants", statsResult.getFemaleMutantCount());
                            dataPoint.put("maleMutants", statsResult.getMaleMutantCount());
                            dataPoint.put("metadataGroup", statsResult.getMetadataGroup());

                            categories.add(statsResult.getParameter().getName());
                            dataArray.put(dataPoint);
                            procedureParameterMap.get(procedure).put("end", index);
                            index++;
                        }
                    }
                }

                if (dataArray.length() > 0) {
                    scatterJsonObject.put("data", dataArray);
                    series.put(scatterJsonObject);
                }
            }
            for (String key : new HashSet<>(procedureParameterMap.keySet())) {
                if ( ! procedureParameterMap.get(key).containsKey("end") ) {
                    procedureParameterMap.remove(key);
                }
            }

            chartString = createPvaluesOverviewChart(minimalPvalue, pointFormat.toString(), series, new JSONArray(categories), procedureParameterMap);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("chart", chartString);
        result.put("count", index);
        return result;
    }


    /**
     * Return a -Log10 value to generate a scale
     *
     * @return -Math.log10(pValue)
     */
    public double getLogValue(Double pValue) {
        if (pValue < 1E-20) {
            return -Math.log10(1E-20);
        }
        return -Math.log10(pValue);
    }

    /**
     * Add jitter to a data point by capping the effect size between 0 and 0.8
     *
     * @param effectSize the effect size associated to a MP call
     * @return a jittered value based on the effect size
     */
    double addJitter(double effectSize) {

        // cap to 0.8 max otherwise this means nothing
        // 2 decimals
        double scale = 8E-2;
        boolean neg = (effectSize < 0);
        return (((Math.abs(effectSize) >= 10) ? ((neg) ? -10 : 10) : effectSize) * scale);
    }


    private JSONArray getSortedList(JSONArray array)
            throws JSONException {

        List<JSONObject> list = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getJSONObject(i));
        }
        Collections.sort(list, new JSONSortBasedonXAxisIndexComparator());

        JSONArray resultArray = new JSONArray(list);

        return resultArray;

    }

    protected class JSONSortBasedonXAxisIndexComparator implements Comparator<JSONObject> {

        public int compare(JSONObject a, JSONObject b) {

            try {
                int valA = a.getInt("x");
                int valB = b.getInt("x");

                if (valA > valB)
                    return 1;
                if (valA < valB)
                    return -1;

                return 0;

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return 0;
            }
        }
    }

}
