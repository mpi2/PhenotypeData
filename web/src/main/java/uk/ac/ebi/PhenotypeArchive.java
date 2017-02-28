package uk.ac.ebi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;


/**
 * Created by ilinca on 24/02/2017.
 */

@SpringBootApplication
@ComponentScan(value= "uk.ac.ebi")
@ImportResource("file:/Users/ilinca/IdeaProjects/PhenotypeData/web/src/main/webapp/WEB-INF/app-config.xml")
public class PhenotypeArchive {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PhenotypeArchive.class, args);
    }

}
