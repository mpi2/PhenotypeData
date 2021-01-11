# Description
This directory contains unix `.tar` files
   of all of the SOLR cores that were used
   to make the data release.  _NOTE: these
   results are created from a snapshot of
   the current data for every new data release._

## Core descriptions
- `allele` - Allele details including associated gene,
  latest statuses, and chromosome information
- `allele2` - Further allele details including feature
  information and gene model ids
- `anatomy` - Associated anatomy terms such as top-level
  and intermediate anatomy terms and ids
- `essentialgenes` - Batch query support mappings for
  mouse to human orthologs
- `experiment` - Experiment details and experiment
  observations
- `gene` - Gene details
- `genotype-phenotype` - Genotype to phenotype information
- `images` - Legacy image information
- `impc_images` - Current image information
- `mgi-phenotype` - MGI allele to gene information
- `mp` - Mammalian phenotype term information
- `ortholog_mapping` - Further batch query support for
  filtering by support count
- `phenodigm` - Mouse to human disease mapping
- `pipeline` - Pipeline information
- `product` - Product information
- `statistical-raw-data` - Statistical result raw data, a string,
   keyed by the statistical-result doc_id, containing only the
   raw data
 _NOTE: this core is very large._
- `statistical-result` - Statistical result analysis details
