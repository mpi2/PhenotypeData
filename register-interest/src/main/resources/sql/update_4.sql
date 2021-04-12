-- This sql update, driven by ticket #512
-- (https://github.com/mpi2/PhenotypeData/issues/512), adds
-- crispr allele production status to the register interest
-- summary and vastly simplifies the code by sourcing gene
-- status information from the gene core (enhanced to provide
-- all required register interest gene summary status info)
-- instead of the old imits report that was loaded into the
-- gene table.
-- With this change, the gene, gene_status, and imits_status
-- tables are deleted, replaced by gene core queries.

ALTER TABLE contact
  ADD COLUMN in_html INT(11) NULL DEFAULT '1' AFTER `address`;

-- contact_gene:
-- 1. add gene_accession_id field. It replaces gene_pk.
-- 2. migrate gene_accession_id field.
-- 3. delete gene_pk constraints and column.
ALTER TABLE contact_gene
  DROP FOREIGN KEY contact_gene_ibfk_1,
  DROP FOREIGN KEY contact_gene_ibfk_2,
  DROP INDEX contact_gene_uk,
  DROP INDEX gene_pk_fk,
  ADD COLUMN gene_accession_id VARCHAR(32) NOT NULL AFTER contact_pk;

UPDATE contact_gene cg
  JOIN gene g ON g.pk = cg.gene_pk
  SET cg.gene_accession_id = g.mgi_accession_id;

ALTER TABLE contact_gene
  ADD UNIQUE INDEX contact_gene_uk (contact_pk ASC, gene_accession_id ASC),
  DROP COLUMN gene_pk;


-- gene_sent:
-- 1. standardise mgi_accession_id by renaming to gene_accession_id
-- 2. add crispr_allele_production_status.
ALTER TABLE gene_sent
  DROP FOREIGN KEY gene_sent_ibfk_1,
  DROP FOREIGN KEY gene_sent_ibfk_2,
  DROP FOREIGN KEY gene_sent_ibfk_3,
  DROP FOREIGN KEY gene_sent_ibfk_4,
  DROP INDEX assignment_status_fk,
  DROP INDEX conditional_allele_production_status_fk,
  DROP INDEX null_allele_production_status_fk,
  DROP INDEX phenotyping_status_fk,
  CHANGE COLUMN mgi_accession_id gene_accession_id VARCHAR(32) NOT NULL,
  ADD COLUMN symbol VARCHAR(100) NOT NULL
    AFTER gene_accession_id,
  ADD COLUMN crispr_allele_production_status VARCHAR(64) NULL DEFAULT NULL
    AFTER conditional_allele_production_status,
  ADD COLUMN phenotyping_data_available INT(11) NULL DEFAULT NULL
    AFTER null_allele_production_status;

UPDATE gene_sent
  SET phenotyping_data_available = 1 WHERE phenotyping_status = 'Phenotyping data available';

ALTER TABLE gene_sent
  DROP COLUMN phenotyping_status;

UPDATE gene_sent gs
  JOIN gene g ON g.mgi_accession_id = gs.gene_accession_id
  SET gs.symbol = g.symbol;

SET FOREIGN_KEY_CHECKS=0;

-- Delete unneeded tables
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_CONTEXT;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_PARAMS;
DROP TABLE IF EXISTS BATCH_JOB_EXECUTION_SEQ;
DROP TABLE IF EXISTS BATCH_JOB_INSTANCE;
DROP TABLE IF EXISTS BATCH_JOB_SEQ;
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION;
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION_CONTEXT;
DROP TABLE IF EXISTS BATCH_STEP_EXECUTION_SEQ;

DROP TABLE IF EXISTS gene;
DROP TABLE IF EXISTS gene_status;
DROP TABLE IF EXISTS imits_status;

SET FOREIGN_KEY_CHECKS=1;