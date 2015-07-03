package org.mousephenotype.cda;

import org.mousephenotype.cda.db.dao.BiologicalModelDAO;
import org.mousephenotype.cda.db.pojo.BiologicalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.validation.constraints.NotNull;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class DatabaseApplication {//implements CommandLineRunner {

	@NotNull
	@Autowired
	BiologicalModelDAO bmDao;

	private static final Logger log = LoggerFactory.getLogger(DatabaseApplication.class);

	public static void main(String args[]) {
		SpringApplication.run(DatabaseApplication.class, args);
	}


//	@Override
	public void run(String... strings) throws Exception {

		log.info("Getting biological model for Cdh19<tm1a(EUCOMM)Wtsi>/Cdh19<tm1a(EUCOMM)Wtsi>");
		BiologicalModel bm = bmDao.findByDbidAndAllelicCompositionAndGeneticBackgroundAndZygosity(23, "Cdh19<tm1a(EUCOMM)Wtsi>/Cdh19<tm1a(EUCOMM)Wtsi>", "involves: C57BL/6NTac", "homozygote");
		System.out.println(bm);

	}
}


