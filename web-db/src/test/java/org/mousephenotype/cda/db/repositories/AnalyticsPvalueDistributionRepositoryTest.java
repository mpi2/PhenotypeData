package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.AnalyticsPvalueDistribution;
import org.mousephenotype.cda.dto.UniqueDatatypeAndStatisticalMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class AnalyticsPvalueDistributionRepositoryTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    AnalyticsPvalueDistributionRepository analyticsPvalueDistributionRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList("sql/h2/repositories/org.mousephenotype.cda.db.repositories.AnalyticsPvalueDistributionRepositoryTest-data.sql");

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }

    @Test
    public void getAllUniqueDatatypeAndStatisticalMethod() {

        List<UniqueDatatypeAndStatisticalMethod> expectedList = Arrays.asList(
                new UniqueDatatypeAndStatisticalMethod("categorical", "Fisher's exact test"),
                new UniqueDatatypeAndStatisticalMethod("unidimensional", "Wilcoxon rank sum test with continuity correction"),
                new UniqueDatatypeAndStatisticalMethod("unidimensional", "Mixed Model framework, generalized least squares, equation withoutWeight")
        );

        List<UniqueDatatypeAndStatisticalMethod> actualList = analyticsPvalueDistributionRepository.getAllStatisticalMethods(UniqueDatatypeAndStatisticalMethod.class);

        assertTrue(actualList.size() == 3);

        for (UniqueDatatypeAndStatisticalMethod expected : expectedList) {
            assertTrue(actualList.contains(expected));
        }
    }

    @Test
    public void getAllByDatatypeAndStatisticalMethodOrderByPvalueBinAsc() {

        List<AnalyticsPvalueDistribution> expectedCategorical = Arrays.asList(
                new AnalyticsPvalueDistribution(2L, "categorical", "Fisher's exact test", 0.02, 0.02, 0),
                new AnalyticsPvalueDistribution(3L, "categorical", "Fisher's exact test", 0.04, 0.02, 0),
                new AnalyticsPvalueDistribution(1L, "categorical", "Fisher's exact test", 0.06, 0.02, 0));
        List<AnalyticsPvalueDistribution> actualCategorical = analyticsPvalueDistributionRepository
                .getAllByDatatypeAndStatisticalMethodOrderByPvalueBinAsc("categorical", "Fisher's exact test");
        assertArrayEquals(expectedCategorical.toArray(), actualCategorical.toArray());


        List<AnalyticsPvalueDistribution> expectedUniWilcox = Arrays.asList(
                new AnalyticsPvalueDistribution(51L, "unidimensional", "Wilcoxon rank sum test with continuity correction", 0.02, 0.02, 0),
                new AnalyticsPvalueDistribution(52L, "unidimensional", "Wilcoxon rank sum test with continuity correction", 0.04, 0.02, 0));
        List<AnalyticsPvalueDistribution> actualUniWilcox = analyticsPvalueDistributionRepository
                .getAllByDatatypeAndStatisticalMethodOrderByPvalueBinAsc("unidimensional", "Wilcoxon rank sum test with continuity correction");
        assertArrayEquals(expectedUniWilcox.toArray(), actualUniWilcox.toArray());


        List<AnalyticsPvalueDistribution> expectedUniMixed = Arrays.asList(
                new AnalyticsPvalueDistribution(103L, "unidimensional", "Mixed Model framework, generalized least squares, equation withoutWeight", 0.02, 0.02, 6065),
                new AnalyticsPvalueDistribution(102L, "unidimensional", "Mixed Model framework, generalized least squares, equation withoutWeight", 0.04, 0.02, 2078),
                new AnalyticsPvalueDistribution(101L, "unidimensional", "Mixed Model framework, generalized least squares, equation withoutWeight", 0.06, 0.02, 1588));
        List<AnalyticsPvalueDistribution> actualUniMixed = analyticsPvalueDistributionRepository
                .getAllByDatatypeAndStatisticalMethodOrderByPvalueBinAsc("unidimensional", "Mixed Model framework, generalized least squares, equation withoutWeight");
        assertArrayEquals(expectedUniMixed.toArray(), actualUniMixed.toArray());
    }
}