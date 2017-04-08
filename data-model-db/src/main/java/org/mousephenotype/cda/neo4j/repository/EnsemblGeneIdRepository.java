package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.EnsemblGeneId;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ckchen on 17/03/2017.
 */
@Repository
 public interface EnsemblGeneIdRepository extends GraphRepository<EnsemblGeneId> {

    EnsemblGeneId findByEnsemblGeneId(String ensemblGeneId);


    @Query("match (ensg:EnsemblGeneId)-[:GENE]->(g:Gene) where ensg.ensemblGeneId={ensemblGeneId} with g, ensg, "
            + "[(g)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(g)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(g)<-[:GENE]-(d:DiseaseModel) | d] as diseaseModel, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:MOUSE_MODEL]->(mm:MouseModel) | mm] as mouseModel, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:HUMAN_PHENOTYPE]->(h:Hp) | h] as hp, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:MOUSE_PHENOTYPE]->(m:Mp) | m] as mp, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:MOUSE_PHENOTYPE]->(m:Mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "return g, ensg, markerSynonym, humanGeneSymbol, diseaseModel, mouseModel, allele, hp, mp, mpSynonym")
    List<Object> findDataByEnsemblGeneId(@Param( "ensemblGeneId" ) String ensemblGeneId);

}
