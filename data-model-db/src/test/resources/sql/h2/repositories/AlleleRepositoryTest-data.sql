DROP TABLE IF EXISTS allele;
CREATE TABLE allele (
    acc                       VARCHAR(30) NOT NULL,
    db_id                     INT(10)     NOT NULL,
    gf_acc                    VARCHAR(20),
    gf_db_id                  INT(10),
    biotype_acc               VARCHAR(20)  NOT NULL,
    biotype_db_id             INT(10)      NOT NULL,
    symbol                    VARCHAR(100) NOT NULL,
    name                      VARCHAR(200) NOT NULL,

    PRIMARY KEY (acc, db_id)

);

DROP TABLE IF EXISTS ontology_term;
CREATE TABLE ontology_term (
    acc                       VARCHAR(30) NOT NULL,
    db_id                     INT(10) NOT NULL,
    name                      TEXT NOT NULL,
    description               TEXT,
    is_obsolete               TINYINT(1) DEFAULT 0,
    replacement_acc           VARCHAR(20) DEFAULT NULL,

    PRIMARY KEY (acc, db_id)
);

DROP TABLE IF EXISTS synonym;
CREATE TABLE synonym (
    id                        INT(10) NOT NULL AUTO_INCREMENT,
    acc                       VARCHAR(30) NOT NULL,
    db_id                     INT(10) NOT NULL,
    symbol                    VARCHAR(8192) NOT NULL,

    PRIMARY KEY(id)
);

INSERT INTO ontology_term VALUES ('CV:000000101', '3', 'Targeted', '', '0', NULL);
INSERT INTO allele VALUES ('MGI:5013777', '3', NULL, NULL, 'CV:000000101', '3', '0610009B22Rik<tm1a(EUCOMM)Hmgu>', 'targeted mutation 1a, Helmholtz Zentrum Muenchen GmbH');