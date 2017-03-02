package uk.ac.ebi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;


/**
 * Created by ilinca on 24/02/2017.
 */

@SpringBootApplication
@ImportResource("file:/Users/ilinca/IdeaProjects/PhenotypeData/web/src/main/webapp/WEB-INF/app-config.xml")

public class PhenotypeArchive extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PhenotypeArchive.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PhenotypeArchive.class, args);
    }

}
