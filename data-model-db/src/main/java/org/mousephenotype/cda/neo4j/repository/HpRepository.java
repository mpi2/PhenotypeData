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
    // FIND BY MP ID (INCLUDES SELF)
    //---------------------------------------

    @Query("MATCH (mp:Mp) WHERE hp.hpId={hpId} WITH mp, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) | g] as gene, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel) | mm] as mouseModel, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel) | d] as diseaseModel , "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(mp)-[:HUMAN]->(h:Hp) | h] as hp, "
            + "[(mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "RETURN mp, markerSynonym, humanGeneSymbol, ensemblGeneId, gene, mouseModel, diseaseModel, allele, hp, mpSynonym")
    List<Object> findDataByHpId(@Param( "hpId" ) String hpId);


    //@Query("MATCH (mp:Mp)<-[:PARENT*0..{childLevel}]-(cmp) WHERE hp.hpId={hpId} with mp, cmp RETURN collect(distinct hp.hpId), collect(distinct chp.hpId)")
    @Query("MATCH (mp:Mp)<-[:PARENT*0..3]-(cmp) WHERE hp.hpId={hpId} RETURN cmp")
    List<Object> findChildrenHpsByHpId(@Param( "hpId" ) String hpId, @Param( "childLevel" ) int childLevel);

    @Query("MATCH (mp:Mp)<-[:PARENT*0..]-(cmp) WHERE hp.hpId={hpId} RETURN cmp")
    List<Object> findAllChildrenHpsByHpId(@Param( "hpId" ) String hpId);


    @Query("MATCH (cmp)-[:PARENT*0..]->(mp:Mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) "
            + "WHERE g.chrId = {chrId} "
            + "AND hp.hpId={hpId} "
            + "RETURN collect(distinct cmp)")
    List<Object> findAllChildrenHpsByHpIdChr(@Param( "hpId" ) String hpId,
                                             @Param( "chrId" ) String chrId
//                                               @Param( "chrStart" ) int chrStart,
//                                               @Param( "chrEnd" ) int chrEnd
    );

    @Query("MATCH (cmp)-[:PARENT*0..3]->(mp:Mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) "
            + "WHERE g.chrId = {chrId} "
            + "AND hp.hpId={hpId} "
            + "RETURN collect(distinct cmp)")
    List<Object> findChildrenHpsByHpIdChr(@Param( "hpId" ) String hpId,
                                          @Param( "chrId" ) String chrId,
//                                               @Param( "chrStart" ) int chrStart,
//                                               @Param( "chrEnd" ) int chrEnd,
                                          @Param( "childLevel" ) int childLevel);

    @Query("MATCH (mp:Mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) WHERE hp.hpId={hpId} "
            + " AND g.chrId = {chrId} with mp, g, "
            + "[(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel) | mm] as mouseModel, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel) | d] as diseaseModel , "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(mp)-[:HUMAN]->(h:Hp) | h] as hp, "
            + "[(mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "RETURN mp, markerSynonym, humanGeneSymbol, ensemblGeneId, g, mouseModel, diseaseModel, allele, hp, mpSynonym")
    List<Object> findDataByHpIdChr(@Param( "hpId" ) String hpId,
                                   @Param( "chrId" ) String chrId
//                                        @Param( "chrStart" ) int chrStart,
//                                        @Param( "chrEnd" ) int chrEnd
    );


    //---------------------------------------
    // FIND BY MP TERM (INCLUDES SELF)
    //---------------------------------------

    @Query("MATCH (mp:Mp) WHERE hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*')  WITH mp, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) | g] as gene, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel) | mm] as mouseModel, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel) | d] as diseaseModel , "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(mp)-[:HUMAN]->(h:Hp) | h] as hp, "
            + "[(mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "RETURN mp, markerSynonym, humanGeneSymbol, ensemblGeneId, gene, mouseModel, diseaseModel, allele, hp, mpSynonym")
    List<Object> findDataByHpTerm(@Param( "hpTerm" ) String hpTerm);

    //    @Query("MATCH (mp:Mp)<-[:PARENT*0..{childLevel: {childLevel}]-(cmp) WHERE hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*') with mp, cmp RETURN collect(distinct cmp)")
    @Query("MATCH (hp:Hp)<-[:PARENT*0..3]-(cmp) WHERE hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*') with hp, cmp RETURN collect(distinct cmp)")
    List<Object> findChildrenHpsByHpTerm(@Param( "hpTerm" ) String hpTerm, @Param( "childLevel" ) int childLevel);

    @Query("MATCH (mp:Mp)<-[:PARENT*0..]-(cmp) WHERE hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*') RETURN collect(distinct mp), collect(distinct cmp)")
    List<Object> findAllChildrenHpsByHpTerm(@Param( "hpTerm" ) String hpTerm);

    @Query("MATCH (cmp)-[:PARENT*0..]->(mp:Mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) "
            + "WHERE g.chrId = {chrId} "
            + "AND hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*') "
            + "RETURN collect(distinct cmp)")
    List<Object> findAllChildrenHpsByHpTermChr(@Param( "hpTerm" ) String hpTerm,
                                               @Param( "chrId" ) String chrId
//                                                    @Param( "chrStart" ) int chrStart,
//                                                    @Param( "chrEnd" ) int chrEnd
    );

    @Query("MATCH (cmp)-[:PARENT*0..3]->(hp:Hp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) "
            + "WHERE g.chrId = {chrId} "
            + "AND hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*') "
            + "RETURN collect(distinct cmp)")
    List<Object> findChildrenHpsByHpTermChr(@Param( "hpTerm" ) String hpTerm,
                                            @Param( "chrId" ) String chrId,
//                                                 @Param( "chrStart" ) int chrStart,
//                                                 @Param( "chrEnd" ) int chrEnd,
                                            @Param( "childLevel" ) int childLevel);

    @Query("MATCH (mp:Mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) WHERE hp.hpTerm =~ ('(?i)'+'.*'+{hpTerm}+'.*') "
            + "AND g.chrId = {chrId} with mp, g, "
            + "[(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel) | mm] as mouseModel, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel) | d] as diseaseModel , "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(mp)-[:HUMAN]->(h:Hp) | h] as hp, "
            + "[(mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "RETURN mp, markerSynonym, humanGeneSymbol, ensemblGeneId, g, mouseModel, diseaseModel, allele, hp, mpSynonym")
    List<Object> findDataByHpTermChr(@Param( "hpTerm" ) String hpTerm,
                                     @Param( "chrId" ) String chrId
//                                        @Param( "chrStart" ) int chrStart,
//                                        @Param( "chrEnd" ) int chrEnd
    );





}
