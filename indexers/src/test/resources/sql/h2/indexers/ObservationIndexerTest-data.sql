DROP TABLE IF EXISTS phenotype_parameter_ontology_annotation;
CREATE TABLE phenotype_parameter_ontology_annotation (
                                                         id                 INTEGER NULL,
                                                         event_type         VARCHAR(128) NULL,
                                                         option_id          VARCHAR(128) NULL,
                                                         ontology_acc       VARCHAR(128) NOT NULL,
                                                         ontology_db_id     VARCHAR(128) NULL,
                                                         sex                VARCHAR(128) NULL,
);