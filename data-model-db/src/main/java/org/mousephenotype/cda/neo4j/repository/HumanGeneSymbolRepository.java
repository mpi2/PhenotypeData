package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.Allele;
import org.mousephenotype.cda.neo4j.entity.HumanGeneSymbol;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
 public interface HumanGeneSymbolRepository extends GraphRepository<HumanGeneSymbol> {

    HumanGeneSymbol findByHumanGeneSymbol(String humanGeneSymbol);

    @Query("MATCH (hgs:HumanGeneSymbol)-[:GENE*0..1]->(g:Gene) WHERE hgs.humanGeneSymbol={humanGeneSymbol} WITH hgs, g, "
            + "[(g)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(g)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(g)<-[:GENE]-(d:DiseaseModel) | d] as diseaseModel, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:MOUSE_MODEL]->(mm:MouseModel) | mm] as mouseModel, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:HUMAN_PHENOTYPE]->(h:Hp) | h] as hp, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:MOUSE_PHENOTYPE]->(m:Mp) | m] as mp, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:MOUSE_PHENOTYPE]->(m:Mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "return g, ensemblGeneId, markerSynonym, hgs, diseaseModel, mouseModel, allele, hp, mp, mpSynonym")
    List<Object> findDataByHumanGeneSymbol(@Param( "humanGeneSymbol" ) String humanGeneSymbol);


}
