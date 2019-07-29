package uk.ac.ebi.phenotype.stats.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Resources;

import uk.ac.ebi.phenotype.stats.model.Statistics;

public class StatsResources extends Resources<Statistics> {

private List<Statistics> stats;
	
	public StatsResources() {
		stats=new ArrayList<>();
	}

	public List<Statistics> getStats() {
		return stats;
	}

	public void setStats(List<Statistics> stats) {
		this.stats = stats;
	}

	@Override
	public String toString() {
		return "StatsList [stats=" + stats + "]";
	}
	
}
