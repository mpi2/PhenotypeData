package uk.ac.ebi.phenotype.stats.graphs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.phenotype.chart.ChartColors;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ChartColorsTest {

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		List<String> colorStrings=ChartColors.getFemaleMaleColorsRgba(0.7);
		System.out.println(colorStrings);
		assertTrue(colorStrings.size()>3);
	}

}
