package org.mousephenotype.cda;

import org.mousephenotype.cda.db.repositories.ObservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;
import java.util.List;


/**
 * Created by jmason on 23/06/2015.
 */

@ComponentScan("org.mousephenotype.org")
@SpringBootApplication
public class ReportsManager implements CommandLineRunner {

	@Autowired(required=true)
	ObservationRepository observations;

	private static final Logger log = LoggerFactory.getLogger(ReportsManager.class);
	private static final String REPORTS_ARG = "reports";
	private static final String TARGET_ARG = "targetDirectory";
	private static final String PROPERTIES_FILE_ARG = "propertiesFile";


	public static void main(String args[]) {
		SpringApplication.run(ReportsManager.class, args);
	}


	@Override
	public void run(String... strings) throws Exception {

		List<String> options = Arrays.asList(strings);
		if ( ! options.contains("spring.config.name")) {
			System.out.println("No configuration file.");
			return;
		}
	}


}


