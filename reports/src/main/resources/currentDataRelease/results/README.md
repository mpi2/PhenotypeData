# Description
This directory contains data results as a set of reports. _NOTE:
these results are created from a snapshot of the current
data for every new data release._

## Report descriptions
- `dataOverview` - Overview of data in the current data release
- `fertility` - Fertile or infertile phenotype for males and females, by mouse line
- `geneAndMPTermAssociation` - For each MP term, genes that are significant
- `genotype-phenotype-assertions-3I`   - MP calls for all current 3I lines
- `genotype-phenotype-assertions-ALL`  - MP calls for all current lines
- `genotype-phenotype-assertions-EUROPHENOME` - MP calls for all legacy
  EuroPhenome lines
- `genotype-phenotype-assertions-IMPC` - MP calls for all current IMPC lines
- `genotype-phenotype-assertions-MGP`  - MP calls for all legacy
  Wellcome Trust Sanger Institute lines
- `laczExpression` - lacZ expression
- `phenotypeHitsPerGene` - Phentoype hits per gene
- `phenotypeHitsPerLine` - Phentoype hits per line
- `phenotypeHitsPerParameterAndProcedure` - Phenotype hits per parameter and procedure
- `phenotypeHitsPerTopLevelMPTerm` - Phenotype hits per top level MP term
- `procedureCompletenessAndPhenotypeHits` - Procedure completeness, IMPC and legacy data
- `registrationOfInterest` - List by gene of user registration counts by registration date
   range (e.g. last 3 months, last 6 months, etc) NOTE: This report is updated frequently.
- `statistical-results-ALL`  - Statistical results snapshot of  all
  statistical results with status:Successful
- `viability` - Viability phenotypes, by mouse line

## Report filename mapping
Beginning with data release 12.0, the `reports` directory is
called `results` and the contents of the old `csv` directory
have been rolled into the new `results` directory. Some old reports
were removed owing to both expensive maintenance, long completion
times, of limited or no use, and/or ambiguous/misleading data. The
remaining files in the `report` directory were renamed to better
represent their content. Following is a map of reports removed, reports
renamed, and files merged from the old `csv` directory.

| Original Report Name               | Action     | New Report Name                       | 
| --------------------               | ------     | ---------------                       |
| 3I_genotype_phenotype              | from `.csv`| genotype-phenotype-assertions-3I      |
| ALL_genotype_phenotype             | from `.csv`| genotype-phenotype-assertions-ALL     |
| dataOverviewReport                 | renamed    | dataOverview                          |
| EuroPhenome_genotype_phenotype     | from `.csv`| genotype-phenotype-assertions-EUROPHENOME |
| fertilityReport                    | renamed    | fertility                             |
| hitsPerLineReport                  | renamed    | phenotypeHitsPerLine                  |
| hitsPerParameterAndProcedureReport | renamed    | phenotypeHitsPerParameterAndProcedure |
| IMPC_ALL_statistical_results       | from `.csv`| statistical-results-ALL               |
| IMPC_genotype_phenotype            | from `.csv`| genotype-phenotype-assertions-IMPC    |
| laczExpressionReport               | renamed    | laczExpression                        |
| MGP_genotype_phenotype             | from `.csv`| genotype-phenotype-assertions-MGP     |
| phenotypeHitsReport                | renamed    | phenotypeHitsPerTopLevelMPTerm        |
| phenotypeOverviewPerGeneReport     | renamed    | phenotypeHitsPerGene                  |
| procedureCompletenessAllReport     | renamed    | procedureCompletenessAndPhenotypeHits |
| viabilityReport                    | renamed    | viability                             |
| zygosityReport                     | renamed    | geneAndMPTermAssociation              |
| ALL_MPATH_genotype_phenotype       | removed    |                                       |
| bmdStatsGlucoseConcentrationReport | removed    |                                       |
| bmdStatsGlucoseResponseReport      | removed    |                                       |
| bmdStatsReport                     | removed    |                                       |
| impcGafReport                      | removed    |                                       |
| impcPValuesReport                  | removed    |                                       |
| metabolismCalorimetryReport        | removed    |                                       |
| metabolismCBCReport                | removed    |                                       |
| metabolismDEXAReport               | removed    |                                       |
| metabolismIPGTTReport              | removed    |                                       |
| procedureCompletenessImpcReport    | removed    |                                       |
