#Description
This directory contains data release information for
all data releases. Included are: all the genotype to
phenotype associations using the Mammalian Phenotype
(MP) ontology calculated from the statistical analysis
(using the PhenStat R package), as well as general-use
and purpose-built reports for each data release.
Please note that all associations are computed again
for every data release.

##Subdirectories
- cores   - This directory contains gzipped `.gz` files
            of all of the SOLR cores that were used to
             make the data release
- mp-calls - This directory contains gzipped `.gz`
             files of all the genotype to phenotype
             associations using the Mammalian Phenotype
             (MP) ontology calculated from the statistical
             analysis (using the PhenStat R package).
             _NOTE: This directory was originally named_
             `csv` _. It was renamed for clarity._

- mysql   - This is a mysqldump of the IMPC database, created at release time. _NOTE: 
beginning with data release 12.0, the database is significantly smaller_
- reports - This is the full set of statistical reports of the IMPC data,
 generated at release time. _NOTE: beginning with data release 12.0,
 several reports that were difficult to maintain and produce and that
 were deemed of limited or no use were dropped. The remaining
 reports were renamed for clarity. A map of old/retired reports
 to new report names is provided in the reports_ **README.md** _file._
- docker  - A ready to run docker image of the IMPC web portal and API
