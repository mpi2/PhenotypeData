package uk.ac.ebi.phenotype.web.dao;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
//@RepositoryRestResource(collectionResourceRel = "stats", path = "stats")
public interface StatisticsRepository extends MongoRepository<Statistics, String> {

	List<Statistics> findAll();
	//real example chart request
	//http://www.mousephenotype.org/data/chart?accession=MGI:95568&parameter_stable_id=IMPC_HEM_038_001&chart_type=UNIDIMENSIONAL_BOX_PLOT&pipeline_stable_id=UCD_001&zygosity=heterozygote&phenotyping_center=UC%20Davis&strain_accession_id=MGI:2683688&allele_accession_id=MGI:5695973&metadata_group=d673a881e1b99112d08f1ee3b9057eda&&experimentNumber=divChart_1&_=1553270745303
	

	List<Statistics> findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup(@Param("geneAccession") String geneAccession, @Param("alleleAccession") String alleleAccession, @Param("parameterStableId") String parameterStableId,
			@Param("pipelineStableId") String pipelineStableId,  @Param("zygosity") String zygosity, @Param("phenotypingCenter") String phenotypingCenter, @Param("metaDataGroup") String metaDataGroup);
	
	//http://localhost:8080/stats/search/findByGeneSymbolAndParameterStableId?geneSymbol=Arel1&parameterStableId=IMPC_HEM_038_001
	List<Statistics> findByGeneSymbolAndParameterStableId(@Param("geneSymbol") String geneSymbol, @Param("parameterStableId") String parameterStableId);
	
	
	List<Statistics> findByGeneAccession(@Param("geneAccession") String geneAccession);
	//http://localhost:8080/stats/search/findByGeneSymbol?geneSymbol=Arel1
	List<Statistics> findByGeneSymbol(@Param("geneSymbol") String geneSymbol);
	
	
	//http://localhost:8080/stats/search/findByParameterStableId?parameterStableId=IMPC_HEM_038_001
	List<Statistics> findByParameterStableId(@Param("parameterStableId") String parameterStableId);
	

}
