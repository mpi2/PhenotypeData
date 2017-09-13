# Package uk.ac.ebi.phenodigm2

The core of the package consists of the WebDao and WebDaoSolrImp classes. They
are designed to interact with a Solr core and produce objects with phenotype 
data. The core is assumed to contain documents in phenodigm2-format.

The other classes in the package are helper and container classes. For the most 
part, they consist of just fields that mirror solr documents, and standard
getters and setters. 


## Interaction with other modules and packages

The package relies or links to the following components: 

 - a DTO defined in the module data-model-solr
 - web-page controllers in package uk.ac.ebi.phenotype.web-controller 
(DiseaseController2, Phenodigm2RestController, Phenodigm2GridRestController)


## Version history

The code structure roughly mirrors classes in modules uk.ac.ebi.phenodigm.* .
The new code is, however, placed in a separate package because the underlying 
solr core documents have changed slightly. 

The phenodigm2 classes are meant to be simple. Their primary role (and 
arguably their only role) is to relay data from the solr code to the user. Thus,
they do not attempt to construct a deep logic around diseases, genes, etc.

