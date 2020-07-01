package uk.ac.ebi.phenotype.stats.model;

import org.springframework.hateoas.CollectionModel;

import java.util.ArrayList;
import java.util.List;

public class StatsResources extends CollectionModel<Statistics> {

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
