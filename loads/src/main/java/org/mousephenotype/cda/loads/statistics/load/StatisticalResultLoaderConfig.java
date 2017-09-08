package org.mousephenotype.cda.loads.statistics.load;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@PropertySource(value="file:${user.home}/configfiles/${profile:dev}/application.properties")
public class StatisticalResultLoaderConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.komp2")
    public DataSource komp2DataSource() {
        return DataSourceBuilder.create().build();
    }

}