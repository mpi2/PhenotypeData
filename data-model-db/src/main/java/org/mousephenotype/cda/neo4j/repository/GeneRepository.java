package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.DiseaseModelGeneResult;
import org.mousephenotype.cda.neo4j.entity.Gene;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
 public interface GeneRepository extends Neo4jRepository<Gene, Long> {

    Gene findByMgiAccessionId(String mgiAccessionId);
    Gene findByMarkerSymbol(String markerSymbol);

    // diseaseModels know about MouseModel and Allele

    @Query("match (g:Gene) where g.markerSymbol={markerSymbol} with g, "
        + "[(g)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
        + "[(g)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
        + "[(g)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
        + "[(g)<-[:GENE]-(d:DiseaseModel) | d] as diseaseModel, "
        + "[(g)<-[:GENE]-(d:DiseaseModel)-[:MOUSE_MODEL]->(mm:MouseModel) | mm] as mouseModel, "
        + "[(g)<-[:GENE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
        + "[(g)<-[:GENE]-(d:DiseaseModel)-[:HUMAN_PHENOTYPE]->(h:Hp) | h] as hp, "
        + "[(g)<-[:GENE]-(d:DiseaseModel)-[:MOUSE_PHENOTYPE]->(m:Mp) | m] as mp, "
        + "[(g)<-[:GENE]-(d:DiseaseModel)-[:MOUSE_PHENOTYPE]->(m:Mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
        + "return g, ensemblGeneId, markerSynonym, humanGeneSymbol, diseaseModel, mouseModel, allele, hp, mp, mpSynonym")
    List<Object> findDataByMarkerSymbol(@Param( "markerSymbol" ) String markerSymbol);

    @Query("match (g:Gene) where g.mgiAccessionId={mgiAccessionId} with g, "
            + "[(g)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(g)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(g)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(g)<-[:GENE]-(d:DiseaseModel) | d] as diseaseModel, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:MOUSE_MODEL]->(mm:MouseModel) | mm] as mouseModel, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:HUMAN_PHENOTYPE]->(h:Hp) | h] as hp, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:MOUSE_PHENOTYPE]->(m:Mp) | m] as mp, "
            + "[(g)<-[:GENE]-(d:DiseaseModel)-[:MOUSE_PHENOTYPE]->(m:Mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "return g, ensemblGeneId, markerSynonym, humanGeneSymbol, diseaseModel, mouseModel, allele, hp, mp, mpSynonym")
    List<Object> findDataByMgiId(@Param( "mgiAccessionId" ) String mgiAccessionId);


    @Query("MATCH (g:Gene) WHERE g.chrId = {chrId} AND g.chrStart >= {chrStart} AND g.chrEnd <= {chrEnd} with g, "
    + "[(g)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
    + "[(g)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
    + "[(g)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
    + "[(g)<-[:MOUSE_MODEL]-(mm:MouseModel) | mm] as mouseModel, "
    + "[(g)<-[:MOUSE_MODEL]-(mm:MouseModel)-[:MOUSE_PHENOTYPE]->(m:Mp) | m] as mp, "
    + "[(g)<-[:MOUSE_MODEL]-(mm:MouseModel)-[:MOUSE_PHENOTYPE]->(m:Mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym, "
    + "[(g)-[:ALLELE]->(a:Allele)<-[:ALLELE]-(d:DiseaseModel) | d] as diseaseModel, "
    + "[(g)-[:ALLELE]->(a:Allele) | a] as allele, "
    + "[(g)-[:ALLELE]->(a:Allele)<-[:ALLELE]-(d:DiseaseModel)-[:HUMAN_PHENOTYPE]->(h:Hp) | h] as hp "
    + "RETURN g, markerSynonym, humanGeneSymbol, ensemblGeneId, mouseModel, mp, mpSynonym, diseaseModel, allele, hp")
    List<Object> findDataByChrRange(@Param( "chrId" ) String chrId,
                                    @Param( "chrStart" ) int chrStart,
                                    @Param( "chrEnd" ) int chrEnd);



//
//    Gene findByMarkerSymbolAndDiseaseName(String symbol, String ensemblGeneId);
//
//    @Query("CYPHER asdfasdf afes adg ?1  ?2 ")
//    Gene findByMarkerSymbolAndDiseaseName(String symbol, String ensemblGeneId);


}
