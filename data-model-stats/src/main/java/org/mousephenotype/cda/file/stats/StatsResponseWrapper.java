package org.mousephenotype.cda.file.stats;

import java.util.ArrayList;
import java.util.List;

/**
 * used to store more information about the list returned e.g. how many potential restuls of the pagination
 * @author jwarren
 *
 */
public class StatsResponseWrapper {

	
	private List<Stats> stats;
	
	public StatsResponseWrapper() {
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
