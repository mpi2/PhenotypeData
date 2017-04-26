package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.Hp;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
public interface HpRepository extends Neo4jRepository<Hp, Long> {

    Hp findByHpId(String hpId);
    Hp findByHpTerm(String hpTerm);


    //---------------------------------------
    // FIND BY HP ID (INCLUDES SELF)
    //---------------------------------------

    @Query("MATCH (hp:Hp) WHERE hp.hpId={hpId} WITH hp, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene) | g] as gene, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:MOUSE_MODEL]->(mm:MouseModel)| mm] as mouseModel, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)| d] as diseaseModel , "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(hp)<-[:HUMAN]-(mp:Mp) | mp] as mp, "
            + "[(hp)<-[:HUMAN]-(mp:Mp)-[:MpSynonym]->(mps:OntoSynonym) | mps] as mpSynonym, "
            + "[(hp)->[:HpSynonym]->(hps:OntoSynonym) | hps] as hpSynonym "
            + "RETURN hp, gene, markerSynonym, humanGeneSymbol, ensemblGeneId, mouseModel, diseaseModel, allele, mp, mpSynonym, hpSynonym")
    List<Object> findDataByHpId(@Param( "hpId" ) String hpId);

    @Query("MATCH (hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene) WHERE hp.hpId={hpId} "
            + " AND g.chrId = {chrId} with hp, g, "
            + "[(g)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(g)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(g)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:MOUSE_MODEL]->(mm:MouseModel)| mm] as mouseModel, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)| d] as diseaseModel , "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(hp)<-[:HUMAN]-(mp:Mp) | mp] as mp, "
            + "[(hp)<-[:HUMAN]-(mp:Mp)-[:MpSynonym]->(mps:OntoSynonym) | mps] as mpSynonym, "
            + "[(hp)->[:HpSynonym]->(hps:OntoSynonym) | hps] as hpSynonym "
            + "RETURN hp, g, markerSynonym, humanGeneSymbol, ensemblGeneId, mouseModel, diseaseModel, allele, mp, mpSynonym, hpSynonym")
    List<Object> findDataByHpIdChr(@Param( "hpId" ) String hpId,
                                   @Param( "chrId" ) String chrId
//                                        @Param( "chrStart" ) int chrStart,
//                                        @Param( "chrEnd" ) int chrEnd
    );

    @Query("MATCH (hp:Hp)<-[:PARENT*0..3]-(chp) WHERE hp.hpId={hpId} RETURN collect(distinct chp)")
    List<Object> findChildrenHpsByHpId(@Param( "hpId" ) String hpId, @Param( "childLevel" ) int childLevel);

    @Query("MATCH (hp:Hp)<-[:PARENT*0..]-(chp) WHERE hp.hpId={hpId} RETURN collect(distinct chp)")
    List<Object> findAllChildrenHpsByHpId(@Param( "hpId" ) String hpId);


    @Query("MATCH (chp)-[:PARENT*0..]->(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene) "
            + "WHERE g.chrId = {chrId} "
            + "AND hp.hpId={hpId} "
            + "RETURN collect(distinct chp)")
    List<Object> findAllChildrenHpsByHpIdChr(@Param( "hpId" ) String hpId,
                                             @Param( "chrId" ) String chrId
//                                               @Param( "chrStart" ) int chrStart,
//                                               @Param( "chrEnd" ) int chrEnd
    );

    @Query("MATCH (chp)-[:PARENT*0..3]->(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene) "
            + "WHERE g.chrId = {chrId} "
            + "AND hp.hpId={hpId} "
            + "RETURN collect(distinct chp)")
    List<Object> findChildrenHpsByHpIdChr(@Param( "hpId" ) String hpId,
                                          @Param( "chrId" ) String chrId,
//                                               @Param( "chrStart" ) int chrStart,
//                                               @Param( "chrEnd" ) int chrEnd,
                                          @Param( "childLevel" ) int childLevel);

    //---------------------------------------
    // FIND BY MP TERM (INCLUDES SELF)
    //---------------------------------------

    @Query("MATCH (hp:Hp) WHERE hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*')  WITH hp, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene) | g] as gene, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:MOUSE_MODEL]->(mm:MouseModel)| mm] as mouseModel, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)| d] as diseaseModel , "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(hp)<-[:HUMAN]-(mp:Mp) | mp] as mp, "
            + "[(hp)<-[:HUMAN]-(mp:Mp)-[:MpSynonym]->(mps:OntoSynonym) | mps] as mpSynonym, "
            + "[(hp)->[:HpSynonym]->(hps:OntoSynonym) | hps] as hpSynonym "
            + "RETURN hp, gene, markerSynonym, humanGeneSymbol, ensemblGeneId, mouseModel, diseaseModel, allele, mp, mpSynonym, hpSynonym")
    List<Object> findDataByHpTerm(@Param( "hpTerm" ) String hpTerm);

    @Query("MATCH (hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene) WHERE hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*') "
            + "AND g.chrId = {chrId} with hp, g, "
            + "[(g)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(g)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(g)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:MOUSE_MODEL]->(mm:MouseModel)| mm] as mouseModel, "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)| d] as diseaseModel , "
            + "[(hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(hp)<-[:HUMAN]-(mp:Mp) | mp] as mp, "
            + "[(hp)<-[:HUMAN]-(mp:Mp)-[:MpSynonym]->(mps:OntoSynonym) | mps] as mpSynonym, "
            + "[(hp)->[:HpSynonym]->(hps:OntoSynonym) | hps] as hpSynonym "
            + "RETURN hp, g, markerSynonym, humanGeneSymbol, ensemblGeneId, mouseModel, diseaseModel, allele, mp, mpSynonym, hpSynonym")
    List<Object> findDataByHpTermChr(@Param( "hpTerm" ) String hpTerm,
                                     @Param( "chrId" ) String chrId
//                                        @Param( "chrStart" ) int chrStart,
//                                        @Param( "chrEnd" ) int chrEnd
    );

    @Query("MATCH (hp:Hp)<-[:PARENT*0..3]-(chp) WHERE hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*') RETURN collect(distinct chp)")
    List<Object> findChildrenHpsByHpTerm(@Param( "hpTerm" ) String hpTerm, @Param( "childLevel" ) int childLevel);

    @Query("MATCH (hp:Hp)<-[:PARENT*0..]-(chp) WHERE hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*') RETURN collect(distinct chp)")
    List<Object> findAllChildrenHpsByHpTerm(@Param( "hpTerm" ) String hpTerm);

    @Query("MATCH (chp)-[:PARENT*0..]->(hp:Hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene) "
            + "WHERE g.chrId = {chrId} "
            + "AND hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*') "
            + "RETURN collect(distinct chp)")
    List<Object> findAllChildrenHpsByHpTermChr(@Param( "hpTerm" ) String hpTerm,
                                               @Param( "chrId" ) String chrId
//                                                    @Param( "chrStart" ) int chrStart,
//                                                    @Param( "chrEnd" ) int chrEnd
    );

    @Query("MATCH (chp)-[:PARENT*0..3]->(hp:Hp)<-[:HUMAN_PHENOTYPE]-(d:DiseaseModel)-[:GENE]->(g:Gene) "
            + "WHERE g.chrId = {chrId} "
            + "AND hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*') "
            + "RETURN collect(distinct cmh)")
    List<Object> findChildrenHpsByHpTermChr(@Param( "hpTerm" ) String hpTerm,
                                            @Param( "chrId" ) String chrId,
//                                                 @Param( "chrStart" ) int chrStart,
//                                                 @Param( "chrEnd" ) int chrEnd,
                                            @Param( "childLevel" ) int childLevel);

}
