package org.mousephenotype.cda.neo4jLoad.graph;

import org.mousephenotype.cda.annotations.ComponentScanNonParticipant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;

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

    public static void main(String[] args){
        try {
            SpringApplication.run(Application.class, args);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}