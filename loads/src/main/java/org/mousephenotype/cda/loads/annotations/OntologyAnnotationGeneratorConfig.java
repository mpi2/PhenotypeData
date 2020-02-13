package org.mousephenotype.cda.loads.annotations;

import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.config.DataSourceCdaConfig;
import org.mousephenotype.cda.loads.common.config.DataSourceCdabaseConfig;
import org.mousephenotype.cda.loads.common.config.DataSourceDccConfig;
import org.mousephenotype.cda.loads.statistics.generate.StatisticalDatasetGeneratorConfig;
import org.mousephenotype.cda.loads.statistics.load.impc.StatisticalResultLoaderConfig;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = {"org.mousephenotype.cda.loads.annotations", "org.mousephenotype.cda.db"},
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        MpTermService.class
                })
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
                        DataSourceCdabaseConfig.class,
                        DataSourceCdaConfig.class,
                        DataSourceDccConfig.class,
                        StatisticalResultLoaderConfig.class,
                        StatisticalDatasetGeneratorConfig.class,
                        CdaSqlUtils.class})})
public class OntologyAnnotationGeneratorConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

}