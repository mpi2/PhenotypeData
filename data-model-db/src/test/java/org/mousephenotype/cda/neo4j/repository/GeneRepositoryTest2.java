package org.mousephenotype.cda.neo4j.repository;

//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.mousephenotype.cda.neo4j.entity.Gene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * Created by ckchen on 14/03/2017.
 */

//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@ContextConfiguration(classes = {Neo4jTestConfig.class})
////@TestPropertySource(locations = {"classpath:ogm.properties"})
//@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SpringBootApplication
@EnableNeo4jRepositories
public class GeneRepositoryTest2 {

    private final static Logger log = LoggerFactory.getLogger(GeneRepositoryTest2.class);

    public static final String geneSymbol = "Nxn";

    @Autowired
    GeneRepository geneRepository;


    public static void main(String[] args) throws Exception {
        SpringApplication.run(GeneRepositoryTest2.class, args);
    }

    @Ignore
    @Test
    public void testGeneRepository() {

//    CommandLineRunner demo(GeneRepository geneRepository) {
//        return args -> {
//
//            // geneRepository.deleteAll();


//            Gene gene = geneRepository.findByMarkerSymbol(geneSymbol);
//            if (gene == null) {
//                log.debug("Gene {} not found. Creating", geneSymbol);
//                gene = new Gene();
//                gene.setMarkerSymbol(geneSymbol);
//                geneRepository.save(gene);
//            }
        Gene g = geneRepository.findByMarkerSymbol(geneSymbol);
        System.out.println(g.toString());



    }
}