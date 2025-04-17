# Description
This directory contains data release information 
for the IMPC data release DATA_RELEASE_NUMBER. Included are: 
all the genotype to phenotype associations 
using the Mammalian Phenotype (MP) ontology 
calculated from the statistical analysis
with OpenStats R package, as well as general-use 
and purpose-built reports for each data release. 
_NOTE: these results are created from a snapshot 
of the current data for every new data release._

# Subdirectories
- cores - This directory contains a unix tar `.tar` file
  for each core.

- mongo - This directory contains a gzipped `.gz` file
  containing a MongoDB dump of the IMPC database, created
  at release time.

- results - This directory contains a set of gzipped `.gz`
  report files containing statistically processed IMPC data,
  generated at release time. As of data release 12.0, this
  directory is a combination of the prior `csv` and `reports`
  directories.
