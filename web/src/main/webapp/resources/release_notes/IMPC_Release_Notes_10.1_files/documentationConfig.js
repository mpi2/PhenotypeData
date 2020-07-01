
/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * searchAndFacetConfig: definition of variables for the search and facet 
 * see searchAndFacet directory
 * 
 * Author: Chao-Kung Chen
 * 
 */

if(typeof(window.MDOC) === 'undefined') {
    window.MDOC = {};
}

MDOC.search = {
		'facetPanel'        : '<div class="briefDocCap">Browse IMPC data with facets</div>'
							+ '<ul><li>Click on a facet/subfacet to open or hide it.</li>'
							+ '    <li>Ways to display facet result:'
							+ '        <ul>'
							+ '            <li>Click on <b>checkbox</b> on the left.</li>'
							+'         </ul></li>'
							+'     <li>Click on the <b>info button</b> for detailed description.</li>'
							+ '</ul>', 
			
		'facetPanelDocUrl'  : baseUrl + '/documentation/doc-search'
};

var docuBase = baseUrl + '/documentation';

MDOC.gene = {
		'summarySection'         			: '<p>Details about the gene including: Gene name, accession IDs, location, links and a genome browser.</p><p>Click the help icon for more detail.</p>',
		'summarySectionDocUrl'   			: docuBase + '/doc-explore#summarySection0',
		'phenoAssocSection'              	: '<p>Mammalian Phenotype (MP) associations made to this gene.</p><p>Click the help icon for more detail.</p>',
		'phenoAssocSectionDocUrl'        	: docuBase + '/doc-explore#phenoAssocSection0',
		'heatmapSection'           			: '<p>Analysis of the IMPC data displayed in a heatmap.</p><p>Click the help icon for more detail.</p>',
		'heatmapSectionDocUrl'     			: docuBase + '/doc-explore#heatmapSection0',
		'expressionSection'       		    : '<p>Expression of IMPC images associated to this gene.</p><p>Click the help icon for more detail.</p>',
		'expressionSectionDocUrl' 		    : docuBase + '/doc-explore#expressionSection0',
		'phenoAssocImgSection'      		: '<p>Image data used by the phenotyping centers to score the presence or absence of an abnormal phenotyp</p><p>Click the help icon for more detail.</p>',
		'phenoAssocImgSectionDocUrl'		: docuBase + '/doc-explore#phenoAssocImgSection0',
		'diseaseSection'           		    : '<p>Human disease models found to be associated with mouse phenotypes.</p><p>Click the help icon for more detail.</p>',
		'diseaseSectionDocUrl'     		    : docuBase + '/doc-explore#tab-3',
		'orderSection'          			: '<p>Ordering information about mutant mouse lines and IKMC-produced ES cells for this gene appear here, with links provided to the corresponding repository when available. Links to enquire about biobanked tissue directly from the IMPC production centers are also provided.</p><p>Click the help icon for more detail.</p>',
		'orderSectionDocUrl'    			: docuBase + '/doc-explore#orderSection0',
};
MDOC.phenotypes = {
		'summarySection'         			: "<p> Phenotype summary panel.<p> <p>Click the help icon for more detail.</p>",
		'summarySectionDocUrl'   			: docuBase + '/doc-explore#summarySection1',
		'phenotypeStatsSection'  			: "<p> Find out more about how we obtain the stats and associations presented in this panel. <p>",
		'phenotypeStatsSectionDocUrl'		: docuBase + '/doc-explore#phenotypeStatsSection1',
		'geneVariantSection'       			: "<p>Allele associated with current phenotype. You can filter the table using the dropdown checkbox filters over the table, sort by one column and export the data. <p>Click the help icon for more detail.</p>",
		'geneVariantSectionDocUrl' 			: docuBase + '/doc-explore#geneVariantSection1',

};

MDOC.experiments = {
		'phenoAssocSection'              	: '<p>Continuous and Categorical data associated to this gene</p><p>Click the help icon for more detail.</p>',	
		'phenoAssocSectionDocUrl'   			: docuBase + '/doc-explore#all-adult',
};

MDOC.diseases = {
	'summarySection'         				: "<p> Disease summary.<p> <p>Click the help icon for more detail.</p>",
	'summarySectionDocUrl'   				: docuBase + '/doc-explore#summarySection2',
	'orthologySection'  					: "<p>For details about how disease model association by gene orthology is calculated, please click the help icon.<p>",
	'orthologySectionDocUrl'				: docuBase + '/doc-explore#tab-3',
	'similaritySection'       				: "<p>For details about how disease model association by phenotype similarity is calculated, please click the help icon.</p>",
	'similaritySectionDocUrl' 				: docuBase + '/doc-explore#similaritySection2',

};

MDOC.images = {
		'generalPanel'         				: "<p>All images associated with current phenotype.</p> <p>Click the help icon for more detail.</p>",
		'generalPanelDocUrl'   				: docuBase + '/doc-search#images5',
};

MDOC.stats = {
		'generalPanel'         				: '<p>Details about the graphs.</p> <p>Click the help icon for more detail.</p>',
		'generalPanelDocUrl'   				: docuBase + '/doc-explore#4'
};

MDOC.alleles = {
		'generalPanel'         				: '<p>Details about the graphs.</p> <p>Click the help icon for more detail.</p>',
		'generalPanelDocUrl'   				: docuBase + '/doc-explore#tabs-5'
};

MDOC.phenome = {
		'phenomePanel'         				: '<p>Details about the phenotype calls by center.</p> <p>Click the help icon for more detail.</p>',
		'phenomePanelDocUrl'   				: baseUrl + '/documentation/phenome-help'
};

MDOC.parallel = {
		'parallelPanel'         			: '<p>Select one or more procedures to be displayed in a parallel coordinates chart. You can select ranges to filter the data for multiple parameters. The values displayed are the genotype effect for each strain.</p> <p>Click the help icon for more details.</p>',
		'parallelPanelDocUrl'   			: docuBase + '/doc-method#tools'
};


