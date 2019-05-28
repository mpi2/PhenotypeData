DROP TABLE IF EXISTS phenotype_pipeline;
DROP TABLE IF EXISTS phenotype_pipeline_procedure;
DROP TABLE IF EXISTS phenotype_procedure;
DROP TABLE IF EXISTS phenotype_procedure_meta_data;
DROP TABLE IF EXISTS phenotype_procedure_parameter;
DROP TABLE IF EXISTS phenotype_parameter;
DROP TABLE IF EXISTS phenotype_parameter_lnk_option;
DROP TABLE IF EXISTS phenotype_parameter_option;
DROP TABLE IF EXISTS phenotype_parameter_lnk_increment;
DROP TABLE IF EXISTS phenotype_parameter_increment;
DROP TABLE IF EXISTS phenotype_parameter_lnk_ontology_annotation;
DROP TABLE IF EXISTS phenotype_parameter_ontology_annotation;
DROP TABLE IF EXISTS phenotype_parameter_lnk_eq_annotation;
DROP TABLE IF EXISTS phenotype_parameter_eq_annotation;


CREATE TABLE phenotype_pipeline (

  id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  stable_id                  VARCHAR(40) NOT NULL,
  db_id                      INT(10) NOT NULL,
  name                       VARCHAR(100) NOT NULL,
  description                VARCHAR(2000),
  major_version              INT(10) NOT NULL DEFAULT 1,
  minor_version              INT(10) NOT NULL DEFAULT 0,
  stable_key                 INT(10) DEFAULT 0,
  is_deprecated              BOOLEAN,
  PRIMARY KEY (id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_pipeline_procedure (

  pipeline_id                INT(10) UNSIGNED NOT NULL,
  procedure_id               INT(10) UNSIGNED NOT NULL,

  KEY pipeline_idx (pipeline_id),
  KEY procedure_idx (procedure_id),
  UNIQUE (pipeline_id, procedure_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_procedure (

  id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  stable_key                 INT(10) DEFAULT 0,
  stable_id                  VARCHAR(40) NOT NULL,
  db_id                      INT(10) NOT NULL,
  name                       VARCHAR(200) NOT NULL,
  description                TEXT,
  major_version              INT(10) NOT NULL DEFAULT 1,
  minor_version              INT(10) NOT NULL DEFAULT 0,
  is_mandatory               TINYINT(1) DEFAULT 0,
  level                      VARCHAR(20),
  stage                      VARCHAR(20) DEFAULT 'Adult',
  stage_label                VARCHAR(20),
  schedule_key               INT(10)     DEFAULT 0,

  PRIMARY KEY (id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_procedure_meta_data (

  procedure_id               INT(10) UNSIGNED NOT NULL,
  meta_name                  VARCHAR(40) NOT NULL,
  meta_value                 VARCHAR(40) NOT NULL,

  KEY procedure_meta_data_idx (procedure_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_procedure_parameter (

  procedure_id               INT(10) UNSIGNED NOT NULL,
  parameter_id               INT(10) UNSIGNED NOT NULL,

  KEY procedure_idx (procedure_id),
  KEY parameter_idx (parameter_id),
  UNIQUE (procedure_id, parameter_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_parameter (

  id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  stable_id                 VARCHAR(30) NOT NULL,
  db_id                     INT(10) NOT NULL,
  name                      VARCHAR(200) NOT NULL,
  description               TEXT,
  major_version             INT(10) NOT NULL DEFAULT 1,
  minor_version             INT(10) NOT NULL DEFAULT 0,
  unit                      VARCHAR(40) NOT NULL,
  datatype                  VARCHAR(20) NOT NULL,
  parameter_type            VARCHAR(30) NOT NULL,
  formula                   TEXT,
  required                  TINYINT(1) DEFAULT 0,
  metadata                  TINYINT(1) DEFAULT 0,
  important                 TINYINT(1) DEFAULT 0,
  derived                   TINYINT(1) DEFAULT 0,
  annotate                  TINYINT(1) DEFAULT 0,
  increment                 TINYINT(1) DEFAULT 0,
  options                   TINYINT(1) DEFAULT 0,
  sequence                  INT(10) UNSIGNED NOT NULL,
  media                     TINYINT(1) DEFAULT 0,
  data_analysis_notes       TEXT,
  stable_key                INT(10) DEFAULT 0,

  PRIMARY KEY (id),
  KEY parameter_stable_id_idx (stable_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_parameter_lnk_option (

  parameter_id              INT(10) UNSIGNED NOT NULL,
  option_id                 INT(10) UNSIGNED NOT NULL,

  KEY parameter_idx (parameter_id),
  KEY option_idx (option_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_parameter_option (

  id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  name                      VARCHAR(200) NOT NULL,
  description               VARCHAR(200),
  normal                    BOOLEAN,

  PRIMARY KEY (id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_parameter_lnk_increment (

  parameter_id              INT(10) UNSIGNED NOT NULL,
  increment_id              INT(10) UNSIGNED NOT NULL,

  KEY parameter_idx (parameter_id),
  KEY increment_idx (increment_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_parameter_increment (

  id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  increment_value           VARCHAR(200) NOT NULL,
  increment_datatype        VARCHAR(20) NOT NULL,
  increment_unit            VARCHAR(40) NOT NULL,
  increment_minimum         VARCHAR(20) NOT NULL,

  PRIMARY KEY (id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_parameter_lnk_ontology_annotation (

  annotation_id             INT(10) UNSIGNED NOT NULL,
  parameter_id              INT(10) UNSIGNED NOT NULL,

  KEY parameter_idx (parameter_id),
  KEY annotation_idx (annotation_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_parameter_ontology_annotation (

  id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  event_type                enum('abnormal', 'abnormal_specific', 'increased', 'decreased', 'inferred', 'trait'),
  option_id                 INT(10) UNSIGNED,
  ontology_acc              VARCHAR(20),
  ontology_db_id            INT(10),
  sex                       VARCHAR(8) DEFAULT '',

  PRIMARY KEY (id),
  KEY ontology_idx (ontology_acc, ontology_db_id),
  KEY option_idx (option_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_parameter_lnk_eq_annotation (

  annotation_id             INT(10) UNSIGNED NOT NULL,
  parameter_id              INT(10) UNSIGNED NOT NULL,

  KEY parameter_idx (parameter_id),
  KEY annotation_idx (annotation_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE phenotype_parameter_eq_annotation (

  id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  event_type                enum('abnormal', 'abnormal_specific', 'increased', 'decreased', 'inferred', 'trait'),
  option_id                 INT(10) UNSIGNED,
  sex                       ENUM('female', 'hermaphrodite', 'male', 'not_applicable', 'no_data'),
  ontology_acc              VARCHAR(20),
  ontology_db_id            INT(10),
  quality_acc               VARCHAR(20),
  quality_db_id             INT(10),

  PRIMARY KEY (id),
  KEY ontology_idx (ontology_acc, ontology_db_id),
  KEY quality_idx (quality_acc, quality_db_id),
  KEY option_idx (option_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;
