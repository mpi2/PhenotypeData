package org.mousephenotype.cda.web.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;




@ComponentScan
@SpringBootApplication
public class PhentoypeDataApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(PhentoypeDataApplication.class);
	}
    public static void main(String[] args) {
        SpringApplication.run(PhentoypeDataApplication.class, args);
    }
}
