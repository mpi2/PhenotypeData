package org.mousephenotype.cda.reports;

import org.mousephenotype.cda.db.repositories.ObservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * Created by jmason on 23/06/2015.
 */

@ComponentScan("org.mousephenotype.org")
@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired(required=true)
	ObservationRepository observations;

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}


	@Override
	public void run(String... strings) throws Exception {

		log.info("test");
		observations.findById(1L);

	}
}


