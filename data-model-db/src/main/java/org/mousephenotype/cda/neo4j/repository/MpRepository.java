package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.Mp;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
public interface MpRepository extends Neo4jRepository<Mp, Long> {

    Mp findByMpId(String mpId);
    Mp findByMpTerm (String mpTerm);


    @Query("match (mp:Mp) where mp.mpId={mpId} with mp, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) | g] as gene, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel) | mm] as mouseModel, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel) | d] as diseaseModel , "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(mp)-[:HUMAN]->(h:Hp) | h] as hp, "
            + "[(mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "return mp, markerSynonym, humanGeneSymbol, ensemblGeneId, gene, mouseModel, diseaseModel, allele, hp, mpSynonym")
    List<Object> findDataByMpId(@Param( "mpId" ) String mpId);

    @Query("match (mp:Mp) where mp.mptTerm={mptTerm} with mp, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) | g] as gene, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel) | mm] as mouseModel, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel) | d] as diseaseModel , "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(mp)-[:HUMAN]->(h:Hp) | h] as hp, "
            + "[(mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "return mp, markerSynonym, humanGeneSymbol, ensemblGeneId, gene, mouseModel, diseaseModel, allele, hp, mpSynonym")
    List<Object> findDataByMpTerm(@Param( "mptTerm" ) String mptTerm);


    @Query("match (mp:Mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) where mp.mpId={mpId} "
            + " AND g.chrId = {chrId} AND g.chrStart >= {chrStart} AND g.chrEnd <= {chrEnd} with mp, g, "
            + "[(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel) | mm] as mouseModel, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel) | d] as diseaseModel , "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(mp)-[:HUMAN]->(h:Hp) | h] as hp, "
            + "[(mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "return mp, markerSynonym, humanGeneSymbol, ensemblGeneId, g, mouseModel, diseaseModel, allele, hp, mpSynonym")
    List<Object> findDataByMpIdChrRange(@Param( "mpId" ) String mpId,
                                           @Param( "chrId" ) String chrId,
                                           @Param( "chrStart" ) int chrStart,
                                           @Param( "chrEnd" ) int chrEnd
                                          );


    @Query("match(m:Mp)-[:MP_PARENT_ID*2..]->(p) where m.mpId={mpId} and exists(p.topLevelStatus) with m, p return m, p")
    List<Object> findDataByMpTermWithTopLevel(@Param( "mpId" ) String mpId);

}
