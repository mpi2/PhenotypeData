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

import org.mousephenotype.cda.dto.DiscreteTimePoint;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;

import java.util.*;

public class ChartData {

    private String title;
    private ParameterDTO parameter;
    private String subTitle;
    private ExperimentDTO experiment;
    private String chart;
    private String organisation = "";
    private Float min = 0f;
    private Float max = 1000000000f;
    private String id;
    private Map<String, List<DiscreteTimePoint>> lines;


    public void alterMinMax(double d, double e) {
        String chartString = getChart();
        String newChartString = chartString.replace("min: 0", "min: " + d);
        newChartString = newChartString.replace("max: 2", "max: " + e);
        setChart(newChartString);
    }


    public Set<Float> getUniqueTimePoints() {
        Set<Float> timeSet = new TreeSet<>();
        for (String key : this.lines.keySet()) {
            List<DiscreteTimePoint> line = this.lines.get(key);
            for (DiscreteTimePoint point : line) {
                Float time = point.getDiscreteTime();
                timeSet.add(time);

            }
        }
        return timeSet;
    }

    public Float getMinWeek() {
        Float minWeek = 99.0f;

        for (String weekString : lines.keySet()) {
            for (DiscreteTimePoint values : lines.get(weekString)) {
                Float checkWeek = values.getDiscreteTime();
                if (checkWeek < minWeek) {
                    minWeek = checkWeek;
                }
            }
        }

        return minWeek;
    }

    public Float getMaxWeek() {
        Float maxWeek = 0.0f;

        for (String weekString : lines.keySet()) {
            for (DiscreteTimePoint values : lines.get(weekString)) {
                Float checkWeek = values.getDiscreteTime();
                if (checkWeek > maxWeek) {
                    maxWeek = checkWeek;
                }
            }
        }

        return maxWeek;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ParameterDTO getParameter() {
        return parameter;
    }

    public void setParameter(ParameterDTO parameter) {
        this.parameter = parameter;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public ExperimentDTO getExperiment() {
        return experiment;
    }

    public void setExperiment(ExperimentDTO experiment) {
        this.experiment = experiment;
    }

    public String getChart() {
        return chart;
    }

    public void setChart(String chart) {
        this.chart = chart;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public Float getMin() {
        return min;
    }

    public void setMin(Float min) {
        this.min = min;
    }

    public Float getMax() {
        return max;
    }

    public void setMax(Float max) {
        this.max = max;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, List<DiscreteTimePoint>> getLines() {
        return lines;
    }

    public void setLines(Map<String, List<DiscreteTimePoint>> lines) {
        this.lines = lines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChartData chartData = (ChartData) o;
        return Objects.equals(title, chartData.title) &&
                Objects.equals(parameter, chartData.parameter) &&
                Objects.equals(subTitle, chartData.subTitle) &&
                Objects.equals(experiment, chartData.experiment) &&
                Objects.equals(chart, chartData.chart) &&
                Objects.equals(organisation, chartData.organisation) &&
                Objects.equals(min, chartData.min) &&
                Objects.equals(max, chartData.max) &&
                Objects.equals(id, chartData.id) &&
                Objects.equals(lines, chartData.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, parameter, subTitle, experiment, chart, organisation, min, max, id, lines);
    }

    @Override
    public String toString() {
        return "ChartData [experiment=" + experiment + ", chart=" + chart
                + ", organisation=" + organisation + ", min=" + min + ", max=" + max + ", id=" + id + ", lines=" + lines
                + "]";
    }


}
