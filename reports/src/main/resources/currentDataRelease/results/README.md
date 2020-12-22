# Description
This directory contains data results as a set of reports. _NOTE:
these results are created from a snapshot of the current
data for every new data release._

## Report descriptions
- `dataOverview` - Overview of data in the current data release
- `fertility` - Fertile or infertile phenotype for males and females, by mouse line
- `genotype-phenotype-assertions-3I`   - MP calls for all current 3I lines
- `genotype-phenotype-assertions-ALL`  - MP calls for all current lines
- `genotype-phenotype-assertions-EUROPHENOME` - MP calls for all legacy
     EuroPhenome lines
- `genotype-phenotype-assertions-IMPC` - MP calls for all current IMPC lines
- `genotype-phenotype-assertions-MGP`  - MP calls for all legacy
     Wellcome Trust Sanger Institute lines
- `laczExpression` - lacZ expression
- `phenotypeHitsPerLine` - Phentoype hits per gene
- `phenotypeHitsPerParameterAndProcedure` - Phenotype hits per parameter and procedure
- `phenotypeHitsPerTopLevelMPTerm` - Phenotype hits per top level MP term
- `procedureCompleteness` - Procedure completeness, IMPC and legacy data
- `statistical-results-ALL`  - Statistical results snapshot of  all
     statistical results with status:Successful
- `viability` - Viability phenotypes, by mouse line
- `zygosity` - For each MP term, genes that are significant

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
| dataOverviewReport                 | renamed    | dataOverview                          |
| fertilityReport                    | renamed    | fertility                             |
| 3I_genotype_phenotype              | from `.csv`| genotype-phenotype-assertions-3I      |
| ALL_genotype_phenotype             | from `.csv`| genotype-phenotype-assertions-ALL     |
| EuroPhenome_genotype_phenotype     | from `.csv`| genotype-phenotype-assertions-EUROPHENOME |
| IMPC_genotype_phenotype            | from `.csv`| genotype-phenotype-assertions-IMPC    |
| MGP_genotype_phenotype             | from `.csv`| genotype-phenotype-assertions-MGP     |
| laczExpressionReport               | renamed    | laczExpression                        |
| phenotypeOverviewPerGeneReport     | renamed    | phenotypeHitsPerLine                  |
| hitsPerParameterAndProcedureReport | renamed    | phenotypeHitsPerParameterAndProcedure |
| phenotypeHitsReport                | renamed    | phenotypeHitsPerTopLevelMPTerm        |
| procedureCompletenessAllReport     | renamed    | procedureCompleteness                 |
| IMPC_ALL_statistical_results       | from `.csv`| statistical-results-ALL               |
| viabilityReport                    | renamed    | viability                             |
| zygosityReport                     | renamed    | zygosity                              |
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
