DROP TABLE IF EXISTS ontology_term;
CREATE TABLE ontology_term (
	acc                        VARCHAR(30) NOT NULL,
	db_id                      INT default 1 NULL,
	name                       TEXT NOT NULL,
	description                TEXT,
	is_obsolete                TINYINT DEFAULT 0,
	replacement_acc            VARCHAR(20) DEFAULT NULL,
	PRIMARY KEY (acc, db_id)

);

DROP TABLE IF EXISTS alternate_id;
CREATE TABLE alternate_id (
	ontology_term_acc          VARCHAR(30) NOT NULL,
	alternate_id_acc           VARCHAR(30) NOT NULL
);

DROP TABLE IF EXISTS consider_id;
CREATE TABLE consider_id (
	ontology_term_acc          VARCHAR(30) NOT NULL,
	consider_id_acc            VARCHAR(30) NOT NULL
);

DROP TABLE IF EXISTS ontology_term_anomaly;
CREATE TABLE ontology_term_anomaly (
  id                 INT          NOT NULL auto_increment,
  db_name            VARCHAR(128) NOT NULL,
  table_name         VARCHAR(128) NOT NULL,
  column_name        VARCHAR(128) NOT NULL,
  original_acc       VARCHAR(128) NOT NULL,
  replacement_acc    VARCHAR(128),
  reason             VARCHAR(128) NOT NULL,
  last_modified      DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id)
);

DROP TABLE IF EXISTS phenotype_parameter_ontology_association;
CREATE TABLE phenotype_parameter_ontology_association (
  id                 INTEGER NULL,
  event_type         VARCHAR(128) NULL,
  option_id          VARCHAR(128) NULL,
  ontology_acc       VARCHAR(128) NOT NULL,
  ontology_db_id     VARCHAR(128) NULL,
  sex                VARCHAR(128) NULL,
);




INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0001', 1, 'MP:0010', 'exists-isObsolete-hasReplacement');               										-- use replacement:   MP:0010
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0002', 1, null,      'exists-isObsolete-noReplacement-hasConsiderId');  										-- use considerId:    MP:0011
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0003', 1, null,      'exists-isObsolete-noReplacement-noConsiderId');   										-- Error: Obsolete term has no replacement
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0004', 0, null,      'exists-notObsolete');                             										-- use original term: MP:0004
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0005', 0, 'MP:0010', 'exists-notObsolete');                             										-- use original term: MP:0005

INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0010', 0, null,      'exists-notObsolete-hasConsiderId');                                   -- use original term: MP:0010
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0011', 0, null,      'exists-notObsolete-hasConsiderId2');                                  -- use original term: MP:0011
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0012', 0, null,      'exists-notObsolete-hasAlternateId');                                  -- use original term: MP:0012
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0013', 0, null,      'exists-notObsolete-hasAlternateId2');                                 -- use original term: MP:0013
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0014', 0, 'MP:0011', 'exists-notObsolete-hasReplacement-hasConsiderId-hasAlternateId');     -- use original term: MP:0014
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0015', 0, 'MP:0012', 'exists-notObsolete-hasReplacement-hasConsiderId-hasAlternateId2');    -- use original term: MP:0015
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0016', 1, 'MP:0001', 'replacement-isObsolete');                                             -- Error: Obsolete term has obsolete replacement term
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0017', 1, null,      'replacement-hasObsoleteConsiderId');                                  -- Error: Obsolete term has obsolete consider id
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0018', 1, null,      'replacement-multipleConsiderIds');                                    -- Error: Obsolete term has multiple consider ids


INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0002', 'MP:0011');
INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0300', 'MP:0010');
INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0011', 'MP:0300');
INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0400', 'MP:0014');
INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0015', 'MP:0300');
INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0017', 'MP:0002');
INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0018', 'MP:0001');
INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0018', 'MP:0002');


INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:0012', 'MP:9999');
INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:0400', 'MP:0012');
INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:0013', 'MP:0400');
INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:0500', 'MP:0014');
INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:0015', 'MP:0400');
INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:0001', 'MP:7777');
INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:5555', 'MP:6666');
INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:4444', 'MP:6666');

INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0001');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0002');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0003');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0004');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0005');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:9999');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:8888');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0010');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0011');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0012');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0013');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0014');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0015');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0016');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0017');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:0018');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:7777');
INSERT INTO phenotype_parameter_ontology_association (ontology_acc) VALUES ('MP:6666');