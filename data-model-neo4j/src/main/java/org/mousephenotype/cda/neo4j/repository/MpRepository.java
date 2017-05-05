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


    //---------------------------------------
    // FIND BY MP ID (INCLUDES SELF)
    //---------------------------------------

    @Query("MATCH (mp:Mp) WHERE mp.mpId={mpId} WITH mp, "
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
    List<Object> findDataByMpId(@Param( "mpId" ) String mpId);

    @Query("MATCH (mp:Mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) WHERE mp.mpId={mpId} "
            + " AND g.chrId = {chrId} WITH mp, g, "
            + "[(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel) | mm] as mouseModel, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel) | d] as diseaseModel , "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(mp)-[:HUMAN]->(h:Hp) | h] as hp, "
            + "[(mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "RETURN mp, markerSynonym, humanGeneSymbol, ensemblGeneId, g, mouseModel, diseaseModel, allele, hp, mpSynonym")
    List<Object> findDataByMpIdChr(@Param( "mpId" ) String mpId,
                                   @Param( "chrId" ) String chrId
//                                        @Param( "chrStart" ) int chrStart,
//                                        @Param( "chrEnd" ) int chrEnd
    );

    //@Query("MATCH (mp:Mp)<-[:PARENT*0..{childLevel}]-(cmp) WHERE mp.mpId={mpId} WITH mp, cmp RETURN collect(distinct mp.mpId), collect(distinct cmp.mpId)")
    @Query("MATCH (mp:Mp)<-[:PARENT*0..3]-(cmp) WHERE mp.mpId={mpId} RETURN collect(distinct cmp)")
    List<Object> findChildrenMpsByMpId(@Param( "mpId" ) String mpId, @Param( "childLevel" ) int childLevel);

    @Query("MATCH (mp:Mp)<-[:PARENT*0..]-(cmp) WHERE mp.mpId={mpId} RETURN collect(distinct cmp)")
    List<Object> findAllChildrenMpsByMpId(@Param( "mpId" ) String mpId);


    @Query("MATCH (cmp)-[:PARENT*0..]->(mp:Mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) "
            + "WHERE g.chrId = {chrId} "
            + "AND mp.mpId={mpId} "
            + "RETURN collect(distinct cmp)")
    List<Object> findAllChildrenMpsByMpIdChr(@Param( "mpId" ) String mpId,
                                               @Param( "chrId" ) String chrId
//                                               @Param( "chrStart" ) int chrStart,
//                                               @Param( "chrEnd" ) int chrEnd
                                                );

    @Query("MATCH (cmp)-[:PARENT*0..3]->(mp:Mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) "
            + "WHERE g.chrId = {chrId} "
            + "AND mp.mpId={mpId} "
            + "RETURN collect(distinct cmp)")
    List<Object> findChildrenMpsByMpIdChr(@Param( "mpId" ) String mpId,
                                               @Param( "chrId" ) String chrId,
//                                               @Param( "chrStart" ) int chrStart,
//                                               @Param( "chrEnd" ) int chrEnd,
                                               @Param( "childLevel" ) int childLevel);

    //---------------------------------------
    // FIND BY MP TERM (INCLUDES SELF)
    //---------------------------------------

    @Query("MATCH (mp:Mp) WHERE mp.mpTerm =~ ('(?i)'+'.*'+{keyword}+'.*') return mp.mpTerm limit 10")
    List<String> findMpTermByKeyword(@Param( "keyword" ) String keyword);

    @Query("MATCH (mp:Mp) WHERE mp.mpTerm =~ ('(?i)'+'.*'+{mpTerm}+'.*')  WITH mp, "
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
    List<Object> findDataByMpTerm(@Param( "mpTerm" ) String mpTerm);

    @Query("MATCH (mp:Mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) WHERE mp.mpTerm =~ ('(?i)'+'.*'+{mpTerm}+'.*') "
            + "AND g.chrId = {chrId} WITH mp, g, "
            + "[(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel) | mm] as mouseModel, "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel) | d] as diseaseModel , "
            + "[(mp)<-[:MOUSE_PHENOTYPE]-(d:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(mp)-[:HUMAN]->(h:Hp) | h] as hp, "
            + "[(mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "RETURN mp, markerSynonym, humanGeneSymbol, ensemblGeneId, g, mouseModel, diseaseModel, allele, hp, mpSynonym")
    List<Object> findDataByMpTermChr(@Param( "mpTerm" ) String mpTerm,
                                     @Param( "chrId" ) String chrId
//                                        @Param( "chrStart" ) int chrStart,
//                                        @Param( "chrEnd" ) int chrEnd
    );

    @Query("MATCH (mp:Mp)<-[:PARENT*0..{childLevel:{childLevel}}]-(cmp) WHERE mp.mpTerm =~ ('(?i)'+'.*'+{mpTerm}+'.*') WITH cmp RETURN collect(distinct cmp)")
   // @Query("MATCH (mp:Mp)<-[:PARENT*0..3]-(cmp) WHERE mp.mpTerm =~ ('(?i)'+'.*'+{mpTerm}+'.*') WITH mp, cmp RETURN collect(distinct cmp)")
    List<Object> findChildrenMpsByMpTerm(@Param( "mpTerm" ) String mpTerm, @Param( "childLevel" ) int childLevel);

    @Query("MATCH (mp:Mp)<-[:PARENT*0..]-(cmp) WHERE mp.mpTerm =~ ('(?i)'+'.*'+{mpTerm}+'.*') RETURN collect(distinct mp), collect(distinct cmp)")
    List<Object> findAllChildrenMpsByMpTerm(@Param( "mpTerm" ) String mpTerm);

    @Query("MATCH (cmp)-[:PARENT*0..]->(mp:Mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) "
            + "WHERE g.chrId = {chrId} "
            + "AND mp.mpTerm =~ ('(?i)'+'.*'+{mpTerm}+'.*') "
            + "RETURN collect(distinct cmp)")
    List<Object> findAllChildrenMpsByMpTermChr(@Param( "mpTerm" ) String mpTerm,
                                                    @Param( "chrId" ) String chrId
//                                                    @Param( "chrStart" ) int chrStart,
//                                                    @Param( "chrEnd" ) int chrEnd
                                                    );

    @Query("MATCH (cmp)-[:PARENT*0..3]->(mp:Mp)<-[:MOUSE_PHENOTYPE]-(mm:MouseModel)-[:GENE]->(g:Gene) "
            + "WHERE g.chrId = {chrId} "
            + "AND mp.mpTerm =~ ('(?i)'+'.*'+{mpTerm}+'.*') "
            + "RETURN collect(distinct cmp)")
    List<Object> findChildrenMpsByMpTermChr(@Param( "mpTerm" ) String mpTerm,
                                                 @Param( "chrId" ) String chrId,
//                                                 @Param( "chrStart" ) int chrStart,
//                                                 @Param( "chrEnd" ) int chrEnd,
                                                 @Param( "childLevel" ) int childLevel);



}
