package org.mousephenotype.cda;

import org.mousephenotype.cda.db.dao.BiologicalModelDAO;
import org.mousephenotype.cda.db.pojo.BiologicalModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.validation.constraints.NotNull;


/**
 * DatabaseApplication is a test to ensure that the configuration specified in the user defined
 * application.properties file is wiring up all the spring managed beans and hibernate correctly.
 *
 * In order to run this, you must add a file named application.properties to the classpath and
 * in that file provide the following database connection details:

datasource.komp2.url=xxxx
datasource.komp2.username=xxxx
datasource.komp2.password=xxxx

datasource.admintools.url=xxxx
datasource.admintools.username=xxxx
datasource.admintools.password=xxxx

 * and uncomment the implements CommandLineRunner below.
 *
 */

@SpringBootApplication
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


