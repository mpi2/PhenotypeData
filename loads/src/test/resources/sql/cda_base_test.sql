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



INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0001', 1, 'MP:0010', 'exists-isObsolete-hasReplacement');               										-- use replacement:   MP:0010
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0002', 1, null,      'exists-isObsolete-noReplacement-hasConsiderId');  										-- use considerId:    MP:0011
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0003', 1, null,      'exists-isObsolete-noReplacement-noConsiderId');   										-- Error: Obsolete term has no replacement
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0004', 0, null,      'exists-notObsolete');                             										-- use original term: MP:0004
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0005', 0, 'MP:0010', 'exists-notObsolete');                             										-- use original term: MP:0005

INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0010', 0, null,      'exists-notObsolete-hasConsiderId');                                   -- use original term: MP:0010
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0011', 0, null,      'exists-notObsolete-hasConsiderId2');                                  -- use original term: MP:0011
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0012', 0, null,      'exists-notObsolete-hasAlternateId');                                  -- use original term: MP:0012
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0013', 0, null,      'exists-notObsolete-hasAlternateId2');                                 -- use original term: MP:0013
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0014', 0, null,      'exists-notObsolete-hasReplacement-hasConsiderId-hasAlternateId');     -- use original term: MP:0014
INSERT INTO ontology_term(acc, is_obsolete, replacement_acc, name) VALUES('MP:0015', 0, null,      'exists-notObsolete-hasReplacement-hasConsiderId-hasAlternateId2');    -- use original term: MP:0015


INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0002', 'MP:0011');
INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0300', 'MP:0010');
INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0011', 'MP:0300');
INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0400', 'MP:0014');
INSERT INTO consider_id(ontology_term_acc, consider_id_acc) VALUES ('MP:0015', 'MP:0300');


INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:0012', 'MP:9999');
INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:0400', 'MP:0012');
INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:0013', 'MP:0400');
INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:0500', 'MP:0014');
INSERT INTO alternate_id(ontology_term_acc, alternate_id_acc) VALUES ('MP:0015', 'MP:0400');