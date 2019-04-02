package uk.ac.ebi.phenotype.web.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * used to store more information about the list returned e.g. how many potential restuls of the pagination
 * @author jwarren
 *
 */
public class StatsResponseWrapper {

	
	private List<Statistics> stats;
	
	public StatsResponseWrapper() {
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
