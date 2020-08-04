package org.mousephenotype.cda.reports.support;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.db.PrimaryDataSource;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrDao;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * ReportType bean configuration
 */

@Configuration
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@ComponentScan(basePackages = {"org.mousephenotype.cda.db", "org.mousephenotype.cda.solr.service"},
		excludeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
						PrimaryDataSource.class
				})})
@EnableAutoConfiguration
public class ReportsConfig {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${internal_solr_url}")
	private String internalSolrUrl;


	////////////////////////////////
	// DataSources and JdbcTemplates
	////////////////////////////////


	// komp2
	@Value("${datasource.komp2.jdbc-url}")
	private String komp2Url;
	@Value("${datasource.komp2.username}")
	private String komp2Uername;
	@Value("${datasource.komp2.password}")
	private String komp2Password;
    @Bean
    @Primary
    public DataSource komp2DataSource() {
        return SqlUtils.getConfiguredDatasource(komp2Url, komp2Uername, komp2Password);
    }


	/////////////////////////
	// Read only solr servers
	/////////////////////////

	// allele
	@Bean(name = "alleleCore")
	public HttpSolrClient alleleCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/allele").build();
	}

	// allele2
	@Bean(name = "allele2Core")
	public HttpSolrClient allele2Core() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/allele2").build();
	}

	// anatomy
	@Bean(name = "anatomyCore")
	HttpSolrClient anatomyCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/anatomy").build();
	}

	// autosuggest
	@Bean(name = "autosuggestCore")
	HttpSolrClient autosuggestCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/autosuggest").build();
	}

	// experiment
	@Bean(name = "experimentCore")
	HttpSolrClient experimentCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/experiment").build();
	}

	// gene
	@Bean(name = "geneCore")
	HttpSolrClient geneCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/gene").build();
	}

	// genotype-phenotype
	@Bean(name = "genotypePhenotypeCore")
	HttpSolrClient genotypePhenotypeCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/genotype-phenotype").build();
	}

	// images
	@Bean(name = "sangerImagesCore")
	HttpSolrClient imagesCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/images").build();
	}

	// impc_images
	@Bean(name = "impcImagesCore")
	HttpSolrClient impcImagesCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/impc_images").build();
	}

	// mgi-phenotype
	@Bean(name = "mgiPhenotypeCore")
	HttpSolrClient mgiPhenotypeCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/mgi-phenotype").build();
	}

	// mp
	@Bean(name = "mpCore")
	HttpSolrClient mpCore() { return new HttpSolrClient.Builder(internalSolrUrl + "/mp").build(); }

	// phenodigm
	@Bean(name = "phenodigmCore")
	public HttpSolrClient phenodigmCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/phenodigm").build();
	}

	// pipeline
	@Bean(name = "pipelineCore")
	HttpSolrClient pipelineCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/pipeline").build();
	}

	// product
	@Bean(name = "productCore")
	HttpSolrClient productCore() { return new HttpSolrClient.Builder(internalSolrUrl + "/product").build(); }

	// statistical-result
	@Bean(name = "statisticalResultCore")
	HttpSolrClient statisticalResultCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/statistical-result").build();
	}


	///////
	// DAOs
	///////

	@Bean
	public ImagesSolrDao imagesSolrDao( HttpSolrClient sangerImagesCore) {
    	return new ImagesSolrJ(sangerImagesCore);
	}


	////////
	// Other
	////////


	@Bean
	public SolrClient solrClient() { return new HttpSolrClient.Builder(internalSolrUrl).build(); }

	@Bean
	public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }
}