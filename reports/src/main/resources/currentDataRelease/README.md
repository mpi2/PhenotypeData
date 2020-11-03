# Description
This directory contains data release information for
data release DATA_RELEASE_NUMBER. Included are: 
all the genotype to phenotype associations using the
Mammalian Phenotype (MP) ontology calculated from
the statistical analysis (using the PhenStat R 
package), as well as general-use and purpose-built
reports for each data release. _NOTE: these
results are created from a snapshot of the current
data for every new data release._

## Subdirectories
- cores   - This directory contains a gzipped `.gz` file
  for each core.

- mysql   - This directory contains a gzipped `.gz` file
  containing a mysqldump of the IMPC database, created
  at release time. _NOTE: beginning with data release 12.0,
  the database is significantly smaller._

- results - This directory contains a set of gzipped `.gz`
  report files containing statistically processed IMPC data,
  generated at release time. As of data release 12.0, this
  directory is a combination of the prior `csv` and `reports`
  directories.
- docker  - For the past several releases, we have included a
  ready-to-run docker image  of the IMPC web portal and API.
  However, due to the large size of the cores, which are a
  part of the docker image, the docker file may not be created
  or available.
  