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

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.web.ChartType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChartUtils {

    private static final Logger logger = LoggerFactory.getLogger(ChartUtils.class);

    /**
     * method that changes the javascript of the chart to have a new max yAxis,
     * currently relies on replacing a string "max: 2" to another value
     *
     * @param chartsAndTablesForParameter
     * @param min
     * @param max
     * @return a list of <code>ChartData</code>
     */
    public static List<ChartData> alterMinAndMaxYAxisOfCharts(List<ChartData> chartsAndTablesForParameter, Float min, Float max) {

        for (ChartData chartNTable : chartsAndTablesForParameter) {
            //for each chart replace the strings that set the min and max values
            String chartString = chartNTable.getChart();
            String newChartString = chartString.replace("min: 0", "min: " + min);
            newChartString = newChartString.replace("max: 2", "max: " + max);
            logger.debug("altering chart string=" + newChartString);
            chartNTable.setChart(newChartString);
        }
        return chartsAndTablesForParameter;
    }
    
    public static String getSelectAllButtonJs(String chartVarName, String checkButtonId, String uncheckButtonId){
    
    	String code = "";
    	if (chartVarName != null && checkButtonId != null && uncheckButtonId != null){
    	
    		code =  "\n$('#" + checkButtonId + "').click(function(){ "
                    + " " + chartVarName + ".showLoading();"
    			+ " for(i=0; i < " + chartVarName + ".series.length; i++) {"
    			+ " if(" + chartVarName + ".series[i].visible == false){ "
                + " " + chartVarName + ".series[i].setVisible(true, false);\n"
                + "}"
                + " " + chartVarName + ".series[i].checkbox.checked = true; \n"
                + " " + chartVarName + ".series[i].selected = true; breaks[i] = {};}\n"
                + " " + chartVarName  + ".hideLoading(); "
                + " " + chartVarName  + ".redraw(); "
                + "});\n"
                +"$('#" + uncheckButtonId + "').click(function(){ "
                + " " + chartVarName + ".showLoading();"
    			+ " for(i=0; i < " + chartVarName + ".series.length; i++) { "
    			+ " if(" + chartVarName + ".series[i].visible == true){ "
                + " " + chartVarName + ".series[i].setVisible(false, false);\n"
                + "}\n"
                    + " " + chartVarName + ".series[i].checkbox.checked = false; \n"
                    + " " + chartVarName + ".series[i].selected = false; breaks[i] = {};}\n"
                    + " " + chartVarName  + ".xAxis[0].update({ breaks: breaks });"
                    + " " + chartVarName  + ".hideLoading();"
                    + " " + chartVarName  + ".redraw(); "
                    + "});\n";
    	}
    	return code;
    }


    /**
     * Return decimal places as an int.
     *
     * @param experiment
     *
     * @return decimal places as an int.
     */
    public static int getDecimalPlaces(ExperimentDTO experiment) {
 
        Set<ObservationDTO> observations=experiment.getControls();
        
        return getDecimalPlacesFromObservations(observations);
    }

	public static int getDecimalPlacesFromObservations(Set<ObservationDTO> observations) {
		int numberOfDecimalPlaces = 0;
        int i = 0;
        for (ObservationDTO control : observations) {
            Float dataPoint = control.getDataPoint();
            String dString = dataPoint.toString();
            int pointIndex = dString.indexOf(".");
            int length = dString.length();
            int tempNumber = length - (pointIndex + 1);
            if (tempNumber > numberOfDecimalPlaces) {
                numberOfDecimalPlaces = tempNumber;
            }
            i ++;
            if (i > 100) {
                break;//only sample the first 100 hopefully representative
            }
        }
        return numberOfDecimalPlaces;
	}


    /**
     * Return <code>number</code> to specified number of decimals.
     *
     * @param number
     * @param numberOfDecimals
     *
     * @return <code>number</code> to specified number of decimals.
     */
    public static Float getDecimalAdjustedFloat(Float number, int numberOfDecimals) {
        //1 decimal #.#
        String decimalFormatString = "#.";
        for (int i = 0; i < numberOfDecimals; i ++) {
            decimalFormatString += "#";
        }

        DecimalFormat df = new DecimalFormat(decimalFormatString);
        String decimalAdjustedMean = df.format(number);
        Float decFloat = new Float(decimalAdjustedMean);
        return decFloat;
    }
    
    /**
     * Return <code>number</code> to specified number of decimals.
     *
     * @param number
     * @param numberOfDecimals
     *
     * @return <code>number</code> to specified number of decimals.
     */
    public static Double getDecimalAdjustedDouble(Double number, int numberOfDecimals) {
        //1 decimal #.#
        String decimalFormatString = "#.";
        for (int i = 0; i < numberOfDecimals; i ++) {
            decimalFormatString += "#";
        }

        DecimalFormat df = new DecimalFormat(decimalFormatString);
        String decimalAdjustedMean = df.format(number);
        Double decFloat = new Double(decimalAdjustedMean);
        return decFloat;
    }


    public static String getChartPageUrlPostQc(String baseUrl, String geneAcc, String alleleAcc, ZygosityType zygosity, String parameterStableId, String pipelineStableId, String phenotypingCenter) {
        String url = baseUrl;
        url += "/charts?accession=" + geneAcc;
        url += "&allele_accession_id=" + alleleAcc;
        if (zygosity != null) {
            url += "&zygosity=" + zygosity.name();
        }
        if (parameterStableId != null) {
            url += "&parameter_stable_id=" + parameterStableId;
        }
        if (pipelineStableId != null) {
            url += "&pipeline_stable_id=" + pipelineStableId;
        }
        if (phenotypingCenter != null) {
            url += "&phenotyping_center=" + phenotypingCenter;
        }
        return url;
    }


    public static Map<String, Float> getMinMaxXAxis(List<ChartsSeriesElement> chartsSeriesElementsList, ExperimentDTO experiment) {

        Float min = new Float(Integer.MAX_VALUE);
        Float max = new Float(Integer.MIN_VALUE);
        Map<String, Float> res = new HashMap<>();

        int decimalPlaces = ChartUtils.getDecimalPlaces(experiment);

        for (ChartsSeriesElement chartsSeriesElement : chartsSeriesElementsList) {

            List<Float> listOfFloats = chartsSeriesElement.getOriginalData();
            PercentileComputation pc = new PercentileComputation(listOfFloats);

            for (Float point : listOfFloats) {
                if (point > max) {
                    max = point;
                }
                if (point < min) {
                    min = point;
                }
            }

            if (listOfFloats.size() > 0) {

                double Q1 = ChartUtils.getDecimalAdjustedFloat(new Float(pc.getLowerQuartile()), decimalPlaces);
                double Q3 = ChartUtils.getDecimalAdjustedFloat(new Float(pc.getUpperQuartile()), decimalPlaces);
                double IQR = Q3 - Q1;

                Float minIQR = ChartUtils.getDecimalAdjustedFloat(new Float(Q1 - (1.5 * IQR)), decimalPlaces);
                if (minIQR < min) {
                    min = minIQR;
                }

                Float maxIQR = ChartUtils.getDecimalAdjustedFloat(new Float(Q3 + (1.5 * IQR)), decimalPlaces);
                if (maxIQR > max) {
                    max = maxIQR;
                }

            }
        }

        res.put("min", min);
        res.put("max", max);

        return res;
    }


    public static String getLabel(ZygosityType zyg, SexType sex) {

        return StringUtils.capitalize(sex.getName()) + " " + (zyg == null ? "WT" : StringUtils.capitalize(zyg.getName().substring(0, 3) + "."));
    }


    public static String getPlotParameter(String parameter) {

        if (Constants.ESLIM_702.contains(parameter)) {
            return "ESLIM_022_001_702";
        } else if (Constants.ESLIM_701.contains(parameter)) {
            return "ESLIM_022_001_701";
        } else if (Constants.IMPC_BWT.contains(parameter)) {
            return "IMPC_BWT_008_001";
        } else if (Constants.IMPC_IPG_002_001.contains(parameter)){
        	return "IMPC_IPG_002_001";

        }

        return parameter;
    }


    public static ChartType getPlotType(String parameter) {

        if (Constants.ESLIM_702.contains(parameter) || parameter.equals("ESLIM_022_001_702") || Constants.ESLIM_701.contains(parameter) || parameter.equals("ESLIM_022_001_701")
                || Constants.IMPC_BWT.contains(parameter) || parameter.equals("IMPC_BWT_008_001") || parameter.equals("IMPC_IPG_002_001")) {
            return ChartType.TIME_SERIES_LINE;
        }

        return null;
    }

	public static int getDecimalPlacesFromStrings(List<String> means) {
		int numberOfDecimalPlaces = 0;
        int i = 0;
        for (String numberString  : means) {
            Float dataPoint = new Float(numberString);
            String dString = dataPoint.toString();
            int pointIndex = dString.indexOf(".");
            int length = dString.length();
            int tempNumber = length - (pointIndex + 1);
            if (tempNumber > numberOfDecimalPlaces) {
                numberOfDecimalPlaces = tempNumber;
            }
            i ++;
            if (i > 100) {
                break;//only sample the first 100 hopefully representative
            }
        }
        return numberOfDecimalPlaces;
	}

}
