
-- Update gene status strings to something that is appropriate to display if desired.
UPDATE gene_status SET status = 'More phenotyping data available' WHERE status = 'more_phenotyping_data_available';
UPDATE gene_status SET status = 'Genotype confirmed mice' WHERE status = 'mouse_produced';
UPDATE gene_status SET status = 'Started' WHERE status = 'mouse_production_started';
UPDATE gene_status SET status = 'Not planned' WHERE status = 'not_planned';
UPDATE gene_status SET status = 'Phenotyping data available' WHERE status = 'phenotyping_data_available';
UPDATE gene_status SET status = 'Selected for production and phenotyping' WHERE status = 'production_and_phenotyping_planned';
UPDATE gene_status SET status = 'Withdrawn' WHERE status = 'withdrawn';

-- Add ri status strings after their corresponding primary keys.
ALTER TABLE `ri`.`gene`
  ADD COLUMN `ri_assignment_status` VARCHAR(64) NULL DEFAULT NULL AFTER `assignment_status_pk`,
  ADD COLUMN `ri_conditional_allele_production_status` VARCHAR(64) NULL DEFAULT NULL AFTER `conditional_allele_production_status_pk`,
  ADD COLUMN `ri_null_allele_production_status` VARCHAR(64) NULL DEFAULT NULL AFTER `null_allele_production_status_pk`,
  ADD COLUMN `ri_phenotyping_status` VARCHAR(64) NULL DEFAULT NULL AFTER `phenotyping_status_pk`;

-- Update the newly-added ri strings above using primary key.
UPDATE gene g SET
  ri_assignment_status = (SELECT status FROM gene_status WHERE pk = g.assignment_status_pk),
  ri_conditional_allele_production_status = (SELECT status FROM gene_status WHERE pk = g.conditional_allele_production_status_pk),
  ri_null_allele_production_status = (SELECT status FROM gene_status WHERE pk = g.null_allele_production_status_pk),
  ri_phenotyping_status = (SELECT status FROM gene_status WHERE pk = g.phenotyping_status_pk)
;