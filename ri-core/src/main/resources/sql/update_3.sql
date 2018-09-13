
-- Replace gene_sent.contact_gene_pk (and its requisite foreign key and index) with unconstrained contact address and gene mgi_accession_id.
-- First, add the two columns. Then migrate existing data. Finally, drop the column, foreign key, and index.
ALTER TABLE gene_sent
  ADD COLUMN address VARCHAR(255) NOT NULL AFTER body,
  ADD COLUMN mgi_accession_id VARCHAR(32) NOT NULL AFTER address;


UPDATE gene_sent gs
  JOIN contact_gene cg ON cg.pk = gs.contact_gene_pk
  JOIN gene         g  ON g. pk = cg.gene_pk
  JOIN contact      c  ON c. pk = cg.contact_pk
SET gs.address = c.address, gs.mgi_accession_id = g.mgi_accession_id;


ALTER TABLE gene_sent
  DROP COLUMN contact_gene_pk,
  DROP FOREIGN KEY gene_sent_ibfk_1,
  DROP INDEX gene_contact_pk_fk;


-- Replace gene_sent_summary.contact_pk (and its requisite foreign key and index) with unconstrained contact address.
-- First, add the column. Then migrate existing data. Finally, drop the column, foreign key, and index.
ALTER TABLE gene_sent_summary
  ADD COLUMN address VARCHAR(255) NOT NULL after body;


UPDATE gene_sent_summary gss
  JOIN contact c ON c.pk = gss.contact_pk
SET gss.address = c.address;


ALTER TABLE gene_sent_summary
  DROP COLUMN contact_pk,
  DROP FOREIGN KEY gene_sent_summary_ibfk_1,
  DROP INDEX contact_pk_fk
;


-- Add ri status columns and drop old foreign key constraints.
ALTER TABLE `ri`.`gene_sent`
  DROP FOREIGN KEY `gene_sent_ibfk_5`,
  DROP FOREIGN KEY `gene_sent_ibfk_4`,
  DROP FOREIGN KEY `gene_sent_ibfk_3`,
  DROP FOREIGN KEY `gene_sent_ibfk_2`;

ALTER TABLE `ri`.`gene_sent`
  DROP INDEX `phenotyping_status_pk_fk` ,
  DROP INDEX `null_allele_production_status_pk_fk` ,
  DROP INDEX `conditional_allele_production_status_pk_fk` ,
  DROP INDEX `assignment_status_pk_fk` ;


ALTER TABLE `ri`.`gene_sent`
  ADD COLUMN `assignment_status` VARCHAR(64) NULL DEFAULT NULL AFTER `mgi_accession_id`,
  ADD COLUMN `conditional_allele_production_status` VARCHAR(64) NULL DEFAULT NULL AFTER `assignment_status`,
  ADD COLUMN `null_allele_production_status` VARCHAR(64) NULL DEFAULT NULL AFTER `conditional_allele_production_status`,
  ADD COLUMN `phenotyping_status` VARCHAR(64) NULL DEFAULT NULL AFTER `null_allele_production_status`
;

-- Migrate existing statuses.
UPDATE gene_sent gs
  JOIN gene_status gsas   ON gsas.pk   = gs.phenotyping_status_pk
  JOIN gene_status gscaps ON gscaps.pk = gs.conditional_allele_production_status_pk
  JOIN gene_status gsnaps ON gsnaps.pk = gs.null_allele_production_status_pk
  JOIN gene_status gsps   ON gsps.pk   = gs.phenotyping_status_pk
SET assignment_status                    = gsas.status,
    conditional_allele_production_status = gscaps.status,
    null_allele_production_status        = gsnaps.status,
    phenotyping_status                   = gsps.status
;

-- Drop old pk columns.
ALTER TABLE `ri`.`gene_sent`
  DROP COLUMN `phenotyping_status_pk`,
  DROP COLUMN `null_allele_production_status_pk`,
  DROP COLUMN `conditional_allele_production_status_pk`,
  DROP COLUMN `assignment_status_pk`
;

-- Add new column foreign key constraints to gene_sent table.
ALTER TABLE `ri`.`gene_sent`
  ADD INDEX `gene_sent_ibfk_2_idx` (`assignment_status` ASC),
  ADD INDEX `gene_sent_ibfk_3_idx` (`conditional_allele_production_status` ASC),
  ADD INDEX `gene_sent_ibfk_4_idx` (`null_allele_production_status` ASC),
  ADD INDEX `gene_sent_ibfk_5_idx` (`phenotyping_status` ASC);

ALTER TABLE `ri`.`gene_sent`
  ADD CONSTRAINT `gene_sent_ibfk_2`
FOREIGN KEY (`assignment_status`)
REFERENCES `ri`.`gene_status` (`status`)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT,
  ADD CONSTRAINT `gene_sent_ibfk_3`
FOREIGN KEY (`conditional_allele_production_status`)
REFERENCES `ri`.`gene_status` (`status`)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT,
  ADD CONSTRAINT `gene_sent_ibfk_4`
FOREIGN KEY (`null_allele_production_status`)
REFERENCES `ri`.`gene_status` (`status`)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT,
  ADD CONSTRAINT `gene_sent_ibfk_5`
FOREIGN KEY (`phenotyping_status`)
REFERENCES `ri`.`gene_status` (`status`)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT;

-- Remove unneeded ri pk columns from gene table.
ALTER TABLE `ri`.`gene`
  DROP FOREIGN KEY `gene_ibfk_4`,
  DROP FOREIGN KEY `gene_ibfk_3`,
  DROP FOREIGN KEY `gene_ibfk_2`,
  DROP FOREIGN KEY `gene_ibfk_1`;
ALTER TABLE `ri`.`gene`
  DROP COLUMN `phenotyping_status_pk`,
  DROP COLUMN `null_allele_production_status_pk`,
  DROP COLUMN `conditional_allele_production_status_pk`,
  DROP COLUMN `assignment_status_pk`,
  DROP INDEX `phenotyping_status_pk_fk` ,
  DROP INDEX `null_allele_production_status_pk_fk` ,
  DROP INDEX `conditional_allele_production_status_pk_fk` ,
  DROP INDEX `assignment_status_pk_fk` ;


-- Add new column foreign key constraints to gene table.
ALTER TABLE `ri`.`gene`
  ADD INDEX `gene_ibfk_2_idx` (`ri_assignment_status` ASC),
  ADD INDEX `gene_ibfk_3_idx` (`ri_conditional_allele_production_status` ASC),
  ADD INDEX `gene_ibfk_4_idx` (`ri_null_allele_production_status` ASC),
  ADD INDEX `gene_ibfk_5_idx` (`ri_phenotyping_status` ASC);

ALTER TABLE `ri`.`gene`
  ADD CONSTRAINT `gene_ibfk_2`
FOREIGN KEY (`ri_assignment_status`)
REFERENCES `ri`.`gene_status` (`status`)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT,
  ADD CONSTRAINT `gene_ibfk_3`
FOREIGN KEY (`ri_conditional_allele_production_status`)
REFERENCES `ri`.`gene_status` (`status`)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT,
  ADD CONSTRAINT `gene_ibfk_4`
FOREIGN KEY (`ri_null_allele_production_status`)
REFERENCES `ri`.`gene_status` (`status`)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT,
  ADD CONSTRAINT `gene_ibfk_5`
FOREIGN KEY (`ri_phenotyping_status`)
REFERENCES `ri`.`gene_status` (`status`)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT;

-- Delete subject and body fields from gene_sent.
ALTER TABLE gene_sent
  DROP COLUMN `body`,
  DROP COLUMN `subject`;