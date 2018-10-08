DROP TABLE IF EXISTS contact;
CREATE TABLE contact (
  pk                INT          NOT NULL         AUTO_INCREMENT PRIMARY KEY,

  address           VARCHAR(255) NOT NULL UNIQUE,
  password          VARCHAR(256) NOT NULL         DEFAULT '',
  password_expired  INT          NOT NULL         DEFAULT 1,                          -- 1 = expired; 0 = not expired
  account_locked    INT          NOT NULL         DEFAULT 0,                          -- 1 = locked; 0 = not locked

  created_at        DATETIME     NOT NULL,
  updated_at        TIMESTAMP    NOT NULL         DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS contact_gene;
CREATE TABLE contact_gene (
  pk             INT          NOT NULL      AUTO_INCREMENT PRIMARY KEY,

  contact_pk     INT          NOT NULL,
  gene_pk        INT          NOT NULL,

  created_at     DATETIME     NOT NULL,
  updated_at     TIMESTAMP    NOT NULL        DEFAULT CURRENT_TIMESTAMP,

  UNIQUE (contact_pk, gene_pk)

);


DROP TABLE IF EXISTS contact_role;
CREATE TABLE contact_role (
  pk             INT          NOT NULL      AUTO_INCREMENT PRIMARY KEY,

  contact_pk     INT          NOT NULL,
  role           VARCHAR(64)  NOT NULL      DEFAULT 'USER',

  created_at     DATETIME     NOT NULL,
  updated_at     TIMESTAMP    NOT NULL      DEFAULT CURRENT_TIMESTAMP,

  UNIQUE (contact_pk, role)

);


DROP TABLE IF EXISTS gene;
CREATE TABLE gene (
  pk                                        INT          NOT NULL   AUTO_INCREMENT PRIMARY KEY,

  mgi_accession_id                          VARCHAR(32)  NOT NULL           UNIQUE,
  symbol                                    VARCHAR(128) NOT NULL,
  assigned_to                               VARCHAR(128)            DEFAULT NULL,
  assignment_status                         VARCHAR(128)            DEFAULT NULL,
  assignment_status_date                    DATETIME                DEFAULT NULL,
  ri_assignment_status                      VARCHAR(64)             DEFAULT NULL,

  conditional_allele_production_centre      VARCHAR(128)            DEFAULT NULL,
  conditional_allele_production_status      VARCHAR(128)            DEFAULT NULL,
  ri_conditional_allele_production_status   VARCHAR(64)             DEFAULT NULL,
  conditional_allele_production_status_date DATETIME                DEFAULT NULL,
  conditional_allele_production_start_date  DATETIME                DEFAULT NULL,

  null_allele_production_centre             VARCHAR(128)            DEFAULT NULL,
  null_allele_production_status             VARCHAR(128)            DEFAULT NULL,
  ri_null_allele_production_status          VARCHAR(64)             DEFAULT NULL,
  null_allele_production_status_date        DATETIME                DEFAULT NULL,
  null_allele_production_start_date         DATETIME                DEFAULT NULL,

  phenotyping_centre                        VARCHAR(128)            DEFAULT NULL,
  phenotyping_status                        VARCHAR(128)            DEFAULT NULL,
  phenotyping_status_date                   DATETIME                DEFAULT NULL,
  ri_phenotyping_status                     VARCHAR(64)             DEFAULT NULL,

  number_of_significant_phenotypes          INT                     DEFAULT 0,

  created_at                                DATETIME     NOT NULL,
  updated_at                                TIMESTAMP    NOT NULL   DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS gene_sent;
CREATE TABLE gene_sent (
  pk                                   INT          NOT NULL        AUTO_INCREMENT PRIMARY KEY,

  address                              VARCHAR(255) NOT NULL,
  mgi_accession_id                     VARCHAR(32)  NOT NULL,
  assignment_status                    VARCHAR(64)                  DEFAULT NULL,
  conditional_allele_production_status VARCHAR(64)                  DEFAULT NULL,
  null_allele_production_status        VARCHAR(64)                  DEFAULT NULL,
  phenotyping_status                   VARCHAR(64)                  DEFAULT NULL,

  created_at                           DATETIME     NOT NULL,
  sent_at                              DATETIME, -- a null value means 'generated but not geneSent yet'.
  updated_at                           TIMESTAMP    NOT NULL        DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS gene_status;
CREATE TABLE gene_status (
  pk          INT          NOT NULL           AUTO_INCREMENT PRIMARY KEY,

  status      VARCHAR(64)  NOT NULL UNIQUE,

  created_at  DATETIME     NOT NULL,
  updated_at  TIMESTAMP    NOT NULL           DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS imits_status;
CREATE TABLE imits_status (
  pk              INT          NOT NULL           AUTO_INCREMENT PRIMARY KEY,

  gene_status     VARCHAR(64)                     DEFAULT NULL,
  status          VARCHAR(64)  NOT NULL UNIQUE,

  created_at      DATETIME     NOT NULL,
  updated_at      TIMESTAMP    NOT NULL           DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS reset_credentials;
CREATE TABLE reset_credentials (
  email_address  VARCHAR(255) NOT NULL PRIMARY KEY,
  token          TEXT         NOT NULL,
  created_at     DATETIME     NOT NULL

);


-- POPULATE STATIC TABLES


SET @now = NOW();

INSERT INTO gene_status (status, created_at) VALUES
  ('More phenotyping data available', @now),
  ('Genotype confirmed mice', @now),
  ('Started', @now),
  ('Not planned', @now),
  ('Phenotyping data available', @now),
  ('Selected for production and phenotyping', @now),
  ('register', @now),
  ('unregister', @now),
  ('Withdrawn', @now);

INSERT INTO imits_status (status, gene_status, created_at) VALUES
  ('Aborted - ES Cell QC Failed', 'Selected for production and phenotyping', @now),
  ('Assigned - ES Cell QC Complete', 'Selected for production and phenotyping', @now),
  ('Assigned - ES Cell QC In Progress', 'Selected for production and phenotyping', @now),
  ('Assigned', 'Selected for production and phenotyping', @now),
  ('Assigned for phenotyping', 'Selected for production and phenotyping', @now),
  ('Chimeras obtained', 'Started', @now),
  ('Chimeras/Founder obtained', 'Started', @now),
  ('Conflict', 'Selected for production and phenotyping', @now),
  ('Cre Excision Complete', 'Genotype confirmed mice', @now),
  ('Cre Excision Started', 'Started', @now),
  ('Founder obtained', 'Started', @now),
  ('Genotype confirmed', 'Genotype confirmed mice', @now),
  ('Inactive', 'Withdrawn', @now),
  ('Inspect - Conflict', 'Selected for production and phenotyping', @now),
  ('Inspect - GLT Mouse', 'Selected for production and phenotyping', @now),
  ('Inspect - MI Attempt', 'Selected for production and phenotyping', @now),
  ('Interest', 'Selected for production and phenotyping', @now),
  ('Micro-injection aborted', 'Started', @now),
  ('Micro-injection in progress', 'Started', @now),
  ('Mouse Allele Modification Registered', 'Started', @now),
  ('Phenotype Attempt Registered', NULL, @now),
  ('Phenotype Production Aborted', NULL, @now),
  ('Phenotyping Complete', 'Phenotyping data available', @now),
  ('Phenotyping Production Registered', NULL, @now),
  ('Phenotyping Started', NULL, @now),
  ('Rederivation Complete', 'Started', @now),
  ('Rederivation Started', 'Started', @now),
  ('Withdrawn', 'Withdrawn', @now);