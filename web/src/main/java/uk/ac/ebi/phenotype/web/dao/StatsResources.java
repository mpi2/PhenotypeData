package uk.ac.ebi.phenotype.web.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Resources;

import uk.ac.ebi.phenotype.web.dao.Stats;

public class StatsResources extends Resources<Stats> {

private List<Stats> stats;
	
	public StatsResources() {
		stats=new ArrayList<>();
	}

	public List<Stats> getStats() {
		return stats;
	}

	public void setStats(List<Stats> stats) {
		this.stats = stats;
	}

	@Override
	public String toString() {
		return "StatsList [stats=" + stats + "]";
	}
	
}
