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
- cores   - This directory contains a single gzipped `.gz` file
            containing all of all of the SOLR cores that were
             used to make the data release

- mysql   - This is a mysqldump of the IMPC database, created
    at release time. _NOTE: beginning with data release 12.0,
     the database is significantly smaller_

- results - This is collection of reports containing
  statistically processed IMPC data, generated at release
  time. As of data release 12.0, this directory is an
   amalgamation of the prior `csv` and `reports` directories.
- docker  - A ready to run docker image of the IMPC web portal
  and API
  