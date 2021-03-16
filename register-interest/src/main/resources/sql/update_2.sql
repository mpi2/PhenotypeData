
-- Add table to hold contact roles.

DROP TABLE IF EXISTS contact_role;
CREATE TABLE contact_role (
  pk             INT          NOT NULL      AUTO_INCREMENT PRIMARY KEY,
  contact_pk     INT          NOT NULL,
  role           VARCHAR(64)  NOT NULL      DEFAULT 'USER',

  created_at     DATETIME     NOT NULL,
  updated_at     TIMESTAMP    NOT NULL      DEFAULT CURRENT_TIMESTAMP
                   ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY contact_pk_fk   (contact_pk) REFERENCES contact(pk),
  UNIQUE KEY  contact_role_uk (contact_pk, role)

) COLLATE=utf8_general_ci ENGINE=InnoDb;

-- Add table to hold password reset data

DROP TABLE IF EXISTS reset_credentials;
CREATE TABLE reset_credentials (
  email_address  VARCHAR(255) NOT NULL PRIMARY KEY,
  token          TEXT         NOT NULL,
  created_at     DATETIME     NOT NULL

) COLLATE=utf8_general_ci ENGINE=InnoDb;


-- Add password and password_expired columns to contact.
ALTER TABLE `ri`.`contact`
  ADD COLUMN `password`         VARCHAR(256) NOT NULL DEFAULT '' AFTER `address`,
  ADD COLUMN `password_expired` INT          NOT NULL DEFAULT 1  AFTER `password`,
  ADD COLUMN `account_locked`   INT          NOT NULL DEFAULT 0  AFTER `password_expired`;

-- Populate the contact_role table with a 'USER' role for every contact.
INSERT INTO contact_role(contact_pk, role, created_at)
  SELECT c.pk, 'USER', NOW() FROM contact c;


-- Rename contact_gene table to contact_gene.
RENAME TABLE gene_contact TO contact_gene;

-- DROP active flags.
ALTER TABLE contact
DROP COLUMN `active`;

ALTER TABLE contact_gene
  DROP COLUMN active;

ALTER TABLE gene_status
  DROP COLUMN active;

ALTER TABLE imits_status
  DROP COLUMN active;

-- Rename gene_sent.contact_gene_pk to contact_gene_pk.
ALTER TABLE `ri`.`gene_sent`
  DROP FOREIGN KEY `gene_sent_ibfk_1`;
ALTER TABLE `ri`.`gene_sent`
  CHANGE COLUMN `gene_contact_pk` `contact_gene_pk` INT(11) NOT NULL ;
ALTER TABLE `ri`.`gene_sent`
  ADD CONSTRAINT `gene_sent_ibfk_1`
FOREIGN KEY (`contact_gene_pk`)
REFERENCES `ri`.`contact_gene` (`pk`);

-- Drop the log table
DROP TABLE IF EXISTS log;