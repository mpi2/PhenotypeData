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

import org.mousephenotype.cda.db.pojo.BiologicalModel;
import org.mousephenotype.cda.db.pojo.DiscreteTimePoint;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ChartData {
    @Override
    public String toString() {
        return "ChartData [expBiologicalModel=" + expBiologicalModel + ", experiment=" + experiment + ", chart=" + chart
                + ", organisation=" + organisation + ", min=" + min + ", max=" + max + ", id=" + id + ", lines=" + lines
                + "]";
    }

    
private String title;
public String getTitle() {
	return title;
}

public void setTitle(String title) {
	this.title = title;
}

public String getSubtitle() {
	return subtitle;
}

public void setSubTitle(String subtitle) {
	this.subtitle = subtitle;
}


private String subtitle;
    BiologicalModel expBiologicalModel;
    private ExperimentDTO experiment;
    private String chart;
    String organisation = "";
    private Float min = new Float(0);
    private Float max = new Float(1000000000);
    private String id;
    private Map<String, List<DiscreteTimePoint>> lines;

    public Map<String, List<DiscreteTimePoint>> getLines() {
        return lines;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public BiologicalModel getExpBiologicalModel() {
        return expBiologicalModel;
    }

    public void setExpBiologicalModel(BiologicalModel expBiologicalModel) {
        this.expBiologicalModel = expBiologicalModel;
    }

    public Float getMin() {
        return min;
    }

    public void setMin(Float min) {
        this.min = min;
    }

    public void alterMinMax(double d, double e) {
        String chartString = getChart();
        String newChartString = chartString.replace("min: 0", "min: " + d);
        newChartString = newChartString.replace("max: 2", "max: " + e);
        setChart(newChartString);
    }

    public Float getMax() {
        return max;
    }

    public void setMax(Float max) {
        this.max = max;
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

    /**
     * @return the experiment
     */
    public ExperimentDTO getExperiment() {
        return experiment;
    }

    /**
     * @param experiment the experiment to set
     */
    public void setExperiment(ExperimentDTO experiment) {
        this.experiment = experiment;
    }

    public void setLines(Map<String, List<DiscreteTimePoint>> lines) {
        this.lines = lines;

    }

    public Set<Float> getUniqueTimePoints() {
        Set timeSet = new TreeSet();
        for (String key : this.lines.keySet()) {
            List<DiscreteTimePoint> line = this.lines.get(key);
            for (DiscreteTimePoint point : line) {
                Float time = point.getDiscreteTime();
                if (!timeSet.contains(time)) {
                    timeSet.add(time);
                }

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

}
