DROP TABLE IF EXISTS contact;
CREATE TABLE contact (
  pk                INT          NOT NULL         AUTO_INCREMENT PRIMARY KEY,
  address           VARCHAR(255) NOT NULL UNIQUE,
  in_html           INT          NULL             DEFAULT 1,
  password          VARCHAR(256) NOT NULL         DEFAULT '',
  password_expired  INT          NOT NULL         DEFAULT 1,                          -- 1 = expired; 0 = not expired
  account_locked    INT          NOT NULL         DEFAULT 0,                          -- 1 = locked; 0 = not locked
  created_at        DATETIME     NOT NULL,
  updated_at        TIMESTAMP    NOT NULL         DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS contact_gene;
CREATE TABLE contact_gene (
  pk                 INT          NOT NULL      AUTO_INCREMENT PRIMARY KEY,
  contact_pk         INT          NOT NULL,
  gene_accession_id  VARCHAR(32)  NOT NULL,
  created_at         DATETIME     NOT NULL,
  updated_at         TIMESTAMP    NOT NULL      DEFAULT CURRENT_TIMESTAMP,

  UNIQUE (contact_pk, gene_accession_id)

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

DROP TABLE IF EXISTS gene_sent;
CREATE TABLE gene_sent (
  pk                                   INT          NOT NULL        AUTO_INCREMENT PRIMARY KEY,
  address                              VARCHAR(255) NOT NULL,
  gene_accession_id                    VARCHAR(32)  NOT NULL,
  symbol                               VARCHAR(100) NOT NULL,
  assignment_status                    VARCHAR(64)                  DEFAULT NULL,
  conditional_allele_production_status VARCHAR(64)                  DEFAULT NULL,
  crispr_allele_production_status      VARCHAR(64)                  DEFAULT NULL,
  null_allele_production_status        VARCHAR(64)                  DEFAULT NULL,
  phenotyping_data_available           INTEGER                      DEFAULT NULL,
  created_at                           DATETIME     NOT NULL,
  sent_at                              DATETIME, -- a null value means 'generated but not geneSent yet'.
  updated_at                           TIMESTAMP    NOT NULL        DEFAULT CURRENT_TIMESTAMP

);


DROP TABLE IF EXISTS reset_credentials;
CREATE TABLE reset_credentials (
  email_address  VARCHAR(255) NOT NULL PRIMARY KEY,
  token          TEXT         NOT NULL,
  created_at     DATETIME     NOT NULL

);