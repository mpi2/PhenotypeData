package org.mousephenotype.cda.loads.graph;

import org.mousephenotype.cda.annotations.ComponentScanNonParticipant;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by ckchen on 17/03/2017.
 */

@SpringBootApplication(exclude = {
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        WebMvcAutoConfiguration.class})
@ComponentScanNonParticipant
public class Application {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(Application.class).web(false).run(args);
    }
}