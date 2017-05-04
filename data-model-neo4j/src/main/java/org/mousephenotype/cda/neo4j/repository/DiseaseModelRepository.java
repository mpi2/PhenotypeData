package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.DiseaseModel;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
public interface DiseaseModelRepository extends Neo4jRepository<DiseaseModel, Long> {

    @Depth(2)
    DiseaseModel findByDiseaseId(String diseaseId);
    DiseaseModel findByDiseaseTerm(String diseaseTerm);

    @Depth(3)
    List<DiseaseModel> findByAlleleAlleleSymbol(String alleleSymbol);

//    @Depth(2)
//    List<DiseaseModel> fiindByGeneMarkerSymbol(String markerSymbol);

    @Query("MATCH (dm:DiseaseModel)--(a:Allele)--(g:Gene) WHERE g.markerSymbol = {markerSymbol} return dm")
    List<DiseaseModel> findByAllele_Gene_MarkerSymbol(@Param("markerSymbol") String markerSymbol);


    // DISEASE MODEL ID
    @Query("MATCH (d:DiseaseModel) WHERE d.diseaseId={diseaseId} WITH d, "
            + "[(d)-[:GENE]->(g:Gene) | g] as gene, "
            + "[(d)-[:GENE]->(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(d)-[:GENE]->(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(d)-[:GENE]->(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(d)-[:MOUSE_MODEL]->(mm:MouseModel) | mm] as mouseModel, "
            + "[(d)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(d)-[:HUMAN_PHENOTYPE]->(h:Hp) | h] as hp, "
            + "[(d)-[:HUMAN_PHENOTYPE]->(h:Hp)-[:HP_SYNONYM]->(hps:OntoSynonym) | hps] as hpSynonym, "
            + "[(d)-[:MOUSE_PHENOTYPE]->(m:Mp) | m] as mp, "
            + "[(d)-[:MOUSE_PHENOTYPE]->(m:Mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "RETURN d, gene, markerSynonym, humanGeneSymbol, ensemblGeneId, mouseModel, allele, hp, hpSynonym, mp, mpSynonym")
    List<Object> findDataByDiseaseId(@Param( "diseaseId" ) String diseaseId);

    @Query("MATCH (d:DiseaseModel)-[:GENE]->(g:Gene) WHERE d.diseaseId={diseaseId} "
            + "AND g.chrId = {chrId} WITH d, g, "
            + "[(g)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(g)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(g)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(d)-[:MOUSE_MODEL]->(mm:MouseModel) | mm] as mouseModel, "
            + "[(d)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(d)-[:HUMAN_PHENOTYPE]->(h:Hp) | h] as hp, "
            + "[(d)-[:HUMAN_PHENOTYPE]->(h:Hp)-[:HP_SYNONYM]->(hps:OntoSynonym) | hps] as hpSynonym, "
            + "[(d)-[:MOUSE_PHENOTYPE]->(m:Mp) | m] as mp, "
            + "[(d)-[:MOUSE_PHENOTYPE]->(m:Mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "RETURN d, g, markerSynonym, humanGeneSymbol, ensemblGeneId, mouseModel, allele, hp, hpSynonym, mp, mpSynonym")
    List<Object> findDataByDiseaseIdChr(@Param( "diseaseId" ) String diseaseId,
                                   @Param( "chrId" ) String chrId
//                                        @Param( "chrStart" ) int chrStart,
//                                        @Param( "chrEnd" ) int chrEnd
    );

    // DISEASE MODEL TERM
    @Query("MATCH (d:DiseaseModel) WHERE d.diseaseTerm =~ ('(?i)'+'.*'+{diseaseTerm}+'.*') WITH d, "
            + "[(d)-[:GENE]->(g:Gene) | g] as gene, "
            + "[(d)-[:GENE]->(g:Gene)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(d)-[:GENE]->(g:Gene)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(d)-[:GENE]->(g:Gene)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(d)-[:MOUSE_MODEL]->(mm:MouseModel) | mm] as mouseModel, "
            + "[(d)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(d)-[:HUMAN_PHENOTYPE]->(h:Hp) | h] as hp, "
            + "[(d)-[:HUMAN_PHENOTYPE]->(h:Hp)-[:HP_SYNONYM]->(hps:OntoSynonym) | hps] as hpSynonym, "
            + "[(d)-[:MOUSE_PHENOTYPE]->(m:Mp) | m] as mp, "
            + "[(d)-[:MOUSE_PHENOTYPE]->(m:Mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "RETURN d, gene, markerSynonym, humanGeneSymbol, ensemblGeneId, mouseModel, allele, hp, hpSynonym, mp, mpSynonym")
    List<Object> findDataByDiseaseTerm(@Param( "diseaseTerm" ) String diseaseTerm);

    @Query("MATCH (d:DiseaseModel)-[:GENE]->(g:Gene) WHERE d.diseaseTerm =~ ('(?i)'+'.*'+{diseaseTerm}+'.*') "
            + " AND g.chrId = {chrId} WITH d, g, "
            + "[(g)-[:MARKER_SYNONYM]->(ms:MarkerSynonym) | ms] as markerSynonym, "
            + "[(g)-[:HUMAN_GENE_SYMBOL]->(hgs:HumanGeneSymbol) | hgs] as humanGeneSymbol, "
            + "[(g)-[:ENSEMBL_GENE_ID]->(ensg:EnsemblGeneId) | ensg] as ensemblGeneId, "
            + "[(d)-[:MOUSE_MODEL]->(mm:MouseModel) | mm] as mouseModel, "
            + "[(d)-[:ALLELE]->(a:Allele) | a] as allele, "
            + "[(d)-[:HUMAN_PHENOTYPE]->(h:Hp) | h] as hp, "
            + "[(d)-[:HUMAN_PHENOTYPE]->(h:Hp)-[:HP_SYNONYM]->(hps:OntoSynonym) | hps] as hpSynonym, "
            + "[(d)-[:MOUSE_PHENOTYPE]->(m:Mp) | m] as mp, "
            + "[(d)-[:MOUSE_PHENOTYPE]->(m:Mp)-[:MP_SYNONYM]->(mps:OntoSynonym) | mps] as mpSynonym "
            + "RETURN d, g, markerSynonym, humanGeneSymbol, ensemblGeneId, mouseModel, allele, hp, hpSynonym, mp, mpSynonym")
    List<Object> findDataByDiseaseTermChr(@Param( "diseaseTerm" ) String diseaseTerm,
                                        @Param( "chrId" ) String chrId
//                                        @Param( "chrStart" ) int chrStart,
//                                        @Param( "chrEnd" ) int chrEnd
    );

}
