package org.mousephenotype.cda.loads.create.releasenotes;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Created by ilinca on 12/10/2016.
 */
public class DataSourceConfig {

//
//    // database connections
//    @Bean
//    @Primary
//    @ConfigurationProperties(prefix = "datasource.komp2")
//    public DataSource komp2DataSource() {
//        return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
//    }

}
