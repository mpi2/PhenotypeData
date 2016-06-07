/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissiopns and
 * limitations under the License.
 */

-- KOMP2 database definition

-- CHARACTER SET utf8 COLLATE utf8_general_ci;
set NAMES utf8;
SET collation_connection = utf8_general_ci;

--
-- Drop all the tables if they exist
--
DROP TABLE IF EXISTS analytics_specimen_load;
DROP TABLE IF EXISTS analytics_experiment_load;
DROP TABLE IF EXISTS anatomy_call_summary;
DROP TABLE IF EXISTS meta_info;
DROP TABLE IF EXISTS meta_history;
DROP TABLE IF EXISTS analytics_mp_calls;
DROP TABLE IF EXISTS allele;
DROP TABLE IF EXISTS biological_model;
DROP TABLE IF EXISTS biological_model_allele;
DROP TABLE IF EXISTS biological_model_strain;
DROP TABLE IF EXISTS biological_model_genomic_feature;
DROP TABLE IF EXISTS biological_model_phenotype;
DROP TABLE IF EXISTS biological_model_sample;
DROP TABLE IF EXISTS biological_sample;
DROP TABLE IF EXISTS biological_sample_relationship;
DROP TABLE IF EXISTS categorical_observation;
DROP TABLE IF EXISTS consider_id;
DROP TABLE IF EXISTS coord_system;
DROP TABLE IF EXISTS datetime_observation;
DROP TABLE IF EXISTS experiment;
DROP TABLE IF EXISTS experiment_observation;
DROP TABLE IF EXISTS external_db;
DROP TABLE IF EXISTS genomic_feature;
DROP TABLE IF EXISTS ilar;
DROP TABLE IF EXISTS image_record_observation;
DROP TABLE IF EXISTS live_sample;
DROP TABLE IF EXISTS metadata_observation;
DROP TABLE IF EXISTS multidimensional_observation;
DROP TABLE IF EXISTS observation;
DROP TABLE IF EXISTS observation_population;
DROP TABLE IF EXISTS population;
DROP TABLE IF EXISTS ontology_entity;
DROP TABLE IF EXISTS ontology_observation;
DROP TABLE IF EXISTS ontology_relationship;
DROP TABLE IF EXISTS ontology_term;
DROP TABLE IF EXISTS organisation;
DROP TABLE IF EXISTS participant;
DROP TABLE IF EXISTS phenotype_annotation_type;
DROP TABLE IF EXISTS phenotype_call_summary;
DROP TABLE IF EXISTS phenotype_annotation;
DROP TABLE IF EXISTS phenotype_parameter;
DROP TABLE IF EXISTS phenotype_parameter_lnk_eq_annotation;
DROP TABLE IF EXISTS phenotype_parameter_eq_annotation;
DROP TABLE IF EXISTS phenotype_parameter_lnk_ontology_annotation;
DROP TABLE IF EXISTS phenotype_parameter_lnk_increment;
DROP TABLE IF EXISTS phenotype_parameter_lnk_option;
DROP TABLE IF EXISTS phenotype_parameter_ontology_annotation;
DROP TABLE IF EXISTS phenotype_parameter_increment;
DROP TABLE IF EXISTS phenotype_parameter_option;
DROP TABLE IF EXISTS phenotype_pipeline;
DROP TABLE IF EXISTS phenotype_pipeline_procedure;
DROP TABLE IF EXISTS phenotype_procedure;
DROP TABLE IF EXISTS phenotype_procedure_meta_data;
DROP TABLE IF EXISTS phenotype_procedure_parameter;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS seq_region;
DROP TABLE IF EXISTS strain;
DROP TABLE IF EXISTS synonym;
DROP TABLE IF EXISTS text_observation;
DROP TABLE IF EXISTS time_series_observation;
DROP TABLE IF EXISTS unidimensional_observation;
DROP TABLE IF EXISTS xref;
DROP TABLE IF EXISTS image_record_observation;
DROP TABLE IF EXISTS dimension;
DROP TABLE IF EXISTS parameter_association;
DROP TABLE IF EXISTS procedure_meta_data;
DROP TABLE IF EXISTS genes_secondary_project;

/**
 * Contains information about the loading of specimen files
 */
CREATE TABLE analytics_specimen_load (
	id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	filename                   VARCHAR(255) NOT NULL DEFAULT '',
	colony_id                  VARCHAR(255) NOT NULL DEFAULT '',
	dob                        VARCHAR(50) NOT NULL DEFAULT '',
	baseline                   BOOLEAN,
	strain                     VARCHAR(255) NOT NULL DEFAULT '',
	specimen_id                VARCHAR(255) NOT NULL DEFAULT '',
	sex                        VARCHAR(10) NOT NULL DEFAULT '',
	zygosity                   VARCHAR(20) NOT NULL DEFAULT '',
	litter_id                  VARCHAR(255) NOT NULL DEFAULT '',
	impress_pipeline           VARCHAR(50) NOT NULL DEFAULT '',
	production_center          VARCHAR(100) NOT NULL DEFAULT '',
	phenotyping_center         VARCHAR(100) NOT NULL DEFAULT '',
	project                    VARCHAR(100) NOT NULL DEFAULT '',
	mapped_project             VARCHAR(100) NOT NULL DEFAULT '',
	status                     VARCHAR(100) NOT NULL DEFAULT '',
	message                    TEXT,
	additional_information     TEXT,

	PRIMARY KEY (id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Contains information about the loading of experiment files
 */
CREATE TABLE analytics_experiment_load (
	id                       INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	filename                 VARCHAR(255) NOT NULL DEFAULT '',
	center_id                VARCHAR(255) NOT NULL DEFAULT '',
	date_of_experiment       VARCHAR(50) NULL,
	sequence_id              VARCHAR(255) NULL,
	experiment_id            VARCHAR(255) NOT NULL DEFAULT '',
	specimen_id              VARCHAR(255) NULL,
	impress_pipeline         VARCHAR(50) NOT NULL DEFAULT '',
	impress_procedure        VARCHAR(100) NOT NULL DEFAULT '',
	impress_parameters       TEXT,
	parameter_types          VARCHAR(255) NOT NULL DEFAULT '',
	data_values              INT(11) NOT NULL DEFAULT 0,
	metadata_values          INT(11) NOT NULL DEFAULT 0,
	missing_data_values      INT(11) NOT NULL DEFAULT 0,
	status                   VARCHAR(100) NOT NULL DEFAULT '',
	message                  TEXT,
	additional_information   TEXT,

	PRIMARY KEY (id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Contains information about the loading of experiment files
 */
DROP TABLE IF EXISTS analytics_statistics_load;
CREATE TABLE analytics_statistics_load (
	id                       INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	observation_type         VARCHAR(255) NOT NULL DEFAULT '',
	zygosity                 VARCHAR(255) NOT NULL DEFAULT '',
	center_id                VARCHAR(255) NOT NULL DEFAULT '',
	allele_accession_id      VARCHAR(255) NULL,
	impress_pipeline         VARCHAR(50) NOT NULL DEFAULT '',
	impress_procedure        VARCHAR(100) NOT NULL DEFAULT '',
	impress_parameter        TEXT,
	male_controls            INT(10) UNSIGNED,
	male_mutants             INT(10) UNSIGNED,
	female_controls          INT(10) UNSIGNED,
	female_mutants           INT(10) UNSIGNED,
	status_date              DATETIME,
	status                   VARCHAR(100) NOT NULL DEFAULT '',

	PRIMARY KEY (id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Contains meta information about the database like
 * the version of the code that can run safely on the data
 * the mouse assembly version of the data
 * the different phenodeviant calls made for this version
 * the version of the database schema
 */
CREATE TABLE meta_info (
	id                       INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	property_key             VARCHAR(255) NOT NULL DEFAULT '',
	property_value           VARCHAR(2000) NOT NULL DEFAULT '',
	description              TEXT,

	PRIMARY KEY (id),
	UNIQUE KEY key_idx (property_key),
	KEY value_idx (property_value)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Contains meta information from release to release to keep track of
 * Numbers through time
 */
CREATE TABLE meta_history (
	id                       INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	property_key             VARCHAR(255) NOT NULL DEFAULT '',
	property_value           VARCHAR(2000) NOT NULL DEFAULT '',
	data_release_version     VARCHAR(10) NOT NULL,

	PRIMARY KEY (id),
	KEY value_idx (property_value),
	KEY version_idx (data_release_version)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE analytics_mp_calls (
	id                       INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	phenotyping_center       VARCHAR(255) NOT NULL DEFAULT '',
	marker_symbol            VARCHAR(255) NOT NULL DEFAULT '',
	marker_accession_id      VARCHAR(255) NOT NULL DEFAULT '',
	colony_id                VARCHAR(255) NOT NULL DEFAULT '',
	mp_term_id               VARCHAR(255) NOT NULL DEFAULT '',
	mp_term_name             VARCHAR(255) NOT NULL DEFAULT '',
	mp_term_level            ENUM('top', 'intermediate', 'leaf'),

	PRIMARY KEY (id),
	KEY center_idx (phenotyping_center),
	KEY marker_idx (marker_accession_id),
	KEY colony_idx (colony_id),
	KEY mp_idx (mp_term_id, mp_term_level)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
@table project
@desc This table stores information about each phenotyping project
We made this table generic enough to store legacy project data
*/
CREATE TABLE project (
	id                       INT(10) UNSIGNED NOT NULL,
	name                     VARCHAR(255) NOT NULL DEFAULT '',
	fullname                 VARCHAR(255) NOT NULL DEFAULT '',
	description              TEXT,

	PRIMARY KEY (id),
	UNIQUE KEY name_idx (name)

	) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE organisation (

	id                       INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	name                     VARCHAR(255) NOT NULL DEFAULT '',
	fullname                 VARCHAR(255) NOT NULL DEFAULT '',
	country                  VARCHAR(50),

	PRIMARY KEY (id),
	UNIQUE KEY name_idx (name)

	) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE participant (

	project_id               INT(10) UNSIGNED NOT NULL,
	organisation_id          INT(10) UNSIGNED NOT NULL,
	role                     VARCHAR(255) NOT NULL DEFAULT '',

	KEY project (project_id),
	KEY organisation (organisation_id)

	) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Very mouse specific . What about Zebrafish? ZFIN
 */
CREATE TABLE ilar (

	labcode                  VARCHAR(50) NOT NULL,
	status                   ENUM('active', 'pending', 'retired'),
	investigator             VARCHAR(255) NOT NULL DEFAULT '',
	organisation             VARCHAR(255) NOT NULL DEFAULT '',

	PRIMARY KEY (labcode)

	) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * This table holds all the external datasources (MGI, Ensembl)
 */

-- this table holds all the external/ASTD database names
CREATE TABLE external_db (

	id                       INT(10) UNSIGNED NOT NULL,
	name                     VARCHAR(100) NOT NULL,
	short_name               VARCHAR(40) NOT NULL,
	version                  VARCHAR(15) NOT NULL DEFAULT '',
	version_date             DATE not NULL,

	PRIMARY   KEY (id),
	UNIQUE    KEY name_idx (name, version, version_date)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * This table will store the ontological terms we need for controlled vocabulary
 */
CREATE TABLE ontology_term (
	acc                        VARCHAR(20) NOT NULL,
	db_id                      INT(10) NOT NULL,
	name                       TEXT NOT NULL,
	description                TEXT,
	is_obsolete                TINYINT(1) DEFAULT 0,
	replacement_acc            VARCHAR(20) DEFAULT NULL,
	PRIMARY KEY (acc, db_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE ontology_relationship (
	a_acc                      VARCHAR(20) NOT NULL,
	a_db_id                    INT(10) NOT NULL,
	b_acc                      VARCHAR(20) NOT NULL,
	b_db_id                    INT(10) NOT NULL,
	relationship               VARCHAR(30) NOT NULL,

	KEY (a_acc, a_db_id),
	KEY (b_acc, b_db_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * The sequence and genomic information goes there.
 * We picked up some of the table from Ensembl to build our sequence information
 * At the moment, we don't plan to have multiple coordinate system for the same
 * genome.
 */

CREATE TABLE coord_system (

	id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	name                       VARCHAR(40) NOT NULL,
	strain_acc                 VARCHAR(20) DEFAULT NULL,
	strain_db_id               INT(10) UNSIGNED DEFAULT NULL,
	db_id                      INT(10) NOT NULL,

	PRIMARY KEY (id),
	UNIQUE KEY name_idx (db_id, strain_db_id),
	KEY db_idx (db_id),
	KEY strain_idx (strain_db_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE seq_region (

	id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	name                       VARCHAR(40) NOT NULL,
	coord_system_id            INT(10) UNSIGNED NOT NULL,
	length                     INT(10) UNSIGNED NOT NULL,

	PRIMARY KEY (id),
	UNIQUE KEY name_cs_idx (name, coord_system_id),
	KEY cs_idx (coord_system_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Genomic feature table
 * Contains any genomic site, whether functional or not
 * that can be mapped through formal genetic analysis
 */
CREATE TABLE genomic_feature (
	acc                        VARCHAR(20) NOT NULL,
	db_id                      INT(10) NOT NULL,
	symbol                     VARCHAR(100) NOT NULL,
	name                       VARCHAR(200) NOT NULL,
	biotype_acc                VARCHAR(20) NOT NULL,
	biotype_db_id              INT(10) NOT NULL,
	subtype_acc                VARCHAR(20),
	subtype_db_id              INT(10),
	seq_region_id              INT(10) UNSIGNED,
	seq_region_start           INT(10) UNSIGNED DEFAULT 0,
	seq_region_end             INT(10) UNSIGNED DEFAULT 0,
	seq_region_strand          TINYINT(2) DEFAULT 0,
	cm_position                VARCHAR(40),
	status                     ENUM('active', 'withdrawn') NOT NULL DEFAULT 'active',

	PRIMARY   KEY (acc, db_id),
	KEY genomic_feature_symbol_idx (symbol),
	KEY genomic_feature_acc_idx (acc),
	KEY seq_region_idx (seq_region_id),
	KEY biotype_idx (biotype_acc, biotype_db_id),
	KEY subtype_idx (subtype_acc, subtype_db_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE synonym (

	acc                        VARCHAR(20) NOT NULL,
	db_id                      INT(10) NOT NULL,
	symbol                     VARCHAR(8192) NOT NULL,

	PRIMARY KEY (acc, db_id),
	UNIQUE KEY genomic_feature_acc_idx (acc),
	KEY synonym_symbol_idx (symbol(333))

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Genomic feature cross-reference from other datasources.
 */
CREATE TABLE xref (

	id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	acc                        VARCHAR(20) NOT NULL,
	db_id                      INT(10) NOT NULL,
	xref_acc                   VARCHAR(20) NOT NULL,
	xref_db_id                 INT(10) NOT NULL,

	PRIMARY KEY (id),
	KEY genomic_feature_idx (acc, db_id),
	KEY xref_idx (xref_acc, xref_db_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Allele table
 * Contains sequence variant of a gene recognized by a DNA assay (polymorphic)
 * or a variant phenotype (mutant)
 */
CREATE TABLE allele (

	acc                       VARCHAR(20) NOT NULL,
	db_id                     INT(10) NOT NULL,
	gf_acc                    VARCHAR(20),
	gf_db_id                  INT(10),
	biotype_acc               VARCHAR(20),
	biotype_db_id             INT(10),
	symbol                      VARCHAR(100) NOT NULL,
	name                      VARCHAR(200) NOT NULL,

	PRIMARY KEY (acc, db_id),
	KEY genomic_feature_idx (gf_acc, gf_db_id),
	KEY biotype_idx (biotype_acc, biotype_db_id),
	KEY symbol_idx (symbol)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Strain table
 */
CREATE TABLE strain (

	acc                       VARCHAR(20) NOT NULL,
	db_id                     INT(10) NOT NULL,
	biotype_acc               VARCHAR(20),
	biotype_db_id             INT(10),
	name                      VARCHAR(200) NOT NULL,

	PRIMARY KEY (acc, db_id),
	KEY biotype_idx (biotype_acc, biotype_db_id),
	UNIQUE KEY name_idx (name),
	UNIQUE KEY (acc)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE biological_model (

	id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	db_id                     INT(10) NOT NULL,
	allelic_composition       VARCHAR(300) NOT NULL,
	genetic_background        VARCHAR(300) NOT NULL,
	zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable') DEFAULT NULL,
	PRIMARY KEY (id),
	KEY allelic_composition_idx (allelic_composition),
	KEY genetic_background_idx (genetic_background),
	UNIQUE KEY unique_biomodels_idx (db_id, allelic_composition(100), genetic_background(100), zygosity)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE biological_model_allele (

	biological_model_id       INT(10) UNSIGNED NOT NULL,
	allele_acc                VARCHAR(20) NOT NULL,
	allele_db_id              INT(10) NOT NULL,

	KEY biological_model_idx (biological_model_id),
	KEY allele_idx (allele_acc, allele_db_id),
  UNIQUE KEY unique_biomodels_idx (biological_model_id, allele_acc)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE biological_model_strain (

	biological_model_id       INT(10) UNSIGNED NOT NULL,
	strain_acc                VARCHAR(20) NOT NULL,
	strain_db_id              INT(10) NOT NULL,

	KEY biological_model_idx (biological_model_id),
	KEY strain_idx (strain_acc, strain_db_id),
  UNIQUE KEY unique_biomodels_idx (biological_model_id, strain_acc)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * This table is an association table between the
 * allele table, the phenotype information and biological model
 */
CREATE TABLE biological_model_phenotype (

	biological_model_id       INT(10) UNSIGNED NOT NULL,
	phenotype_acc             VARCHAR(20) NOT NULL,
	phenotype_db_id           INT(10) NOT NULL,

	KEY biological_model_idx (biological_model_id),
	KEY phenotype_idx (phenotype_acc, phenotype_db_id),
  UNIQUE KEY unique_biomodels_idx (biological_model_id, phenotype_acc)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

CREATE TABLE biological_model_genomic_feature (

	biological_model_id       INT(10) UNSIGNED NOT NULL,
	gf_acc                    VARCHAR(20) NOT NULL,
	gf_db_id                  INT(10) NOT NULL,

	KEY biological_model_idx (biological_model_id),
	KEY genomic_feature_idx (gf_acc, gf_db_id),
  UNIQUE KEY unique_biomodels_idx (biological_model_id, gf_acc)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Links a sample to a biological model
 */
CREATE TABLE biological_model_sample (

	biological_model_id       INT(10) UNSIGNED NOT NULL,
	biological_sample_id      INT(10) UNSIGNED NOT NULL,

	KEY biological_model_idx (biological_model_id),
	KEY biological_sample_idx (biological_sample_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Experimental sample
 * Contains information about a sample
 * A sample can be an animal specimen, an organ, cell, sperm, etc.
 * The EFO ontology can be used to reference 'whole organism', 'animal fluid', 'animal body part'
 * A sample group defines what role or experimental group the sample belongs to. It can be 'control' / 'experimental'
 */
CREATE TABLE biological_sample (

	id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	external_id               VARCHAR(100),
	db_id                     INT(10),
	sample_type_acc           VARCHAR(20) NOT NULL,
	sample_type_db_id         INT(10) NOT NULL,
	sample_group              VARCHAR(100) NOT NULL,
	organisation_id           INT(10) UNSIGNED NOT NULL,
  production_center_id      INT(10) UNSIGNED NULL,
  litter_id                 VARCHAR(200) NULL,

	PRIMARY KEY (id),
	KEY external_id_idx(external_id),
	KEY external_db_idx(db_id),
	KEY group_idx (sample_group),
	KEY sample_type_idx (sample_type_acc, sample_type_db_id),

	KEY organisation_idx (organisation_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * An animal sample is a type of sample
 * The discriminative value is on the sample type
 */

CREATE TABLE live_sample (

	id                        INT(10) UNSIGNED NOT NULL,
	colony_id                 VARCHAR(100) NOT NULL,
	developmental_stage_acc   VARCHAR(20) NOT NULL,
	developmental_stage_db_id INT(10) NOT NULL,
	sex                       ENUM('female', 'hermaphrodite', 'male', 'not_applicable', 'no_data'),
	zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote'),
	date_of_birth             TIMESTAMP NULL,

	PRIMARY KEY (id),
	KEY colony_idx (colony_id),
	KEY gender_idx (sex),
	KEY zygosity_idx (zygosity),
	KEY developmental_stage_idx (developmental_stage_acc, developmental_stage_db_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * One sample can refer to another sample
 * Example one: organ to whole organism as a part_of relationship
 */
CREATE TABLE biological_sample_relationship (

	biological_sample_a_id     INT(10),
	biological_sample_b_id     INT(10),
	relationship               VARCHAR(30) NOT NULL,

	KEY sample_a_idx (biological_sample_a_id),
	KEY sample_b_idx (biological_sample_b_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * experiment
 * A scientific procedure undertaken to make a discovery, test a hypothesis, or
 * demonstrate a known fact.
 * An experiment has several observation associated to it.
 * See table observation
 */
CREATE TABLE experiment (

	id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	db_id                      INT(10) UNSIGNED NOT NULL,
	external_id                VARCHAR(100),
	sequence_id                VARCHAR(100) NULL DEFAULT NULL,
	date_of_experiment         TIMESTAMP NULL DEFAULT NULL,
	organisation_id            INT(10) UNSIGNED NOT NULL,
	project_id                 INT(10) UNSIGNED NULL DEFAULT NULL,
	pipeline_id                INT(10) UNSIGNED NOT NULL,
	pipeline_stable_id         VARCHAR(30) NOT NULL,
	procedure_id               INT(10) UNSIGNED NOT NULL,
	procedure_stable_id        VARCHAR(30) NOT NULL,
	biological_model_id        INT(10) UNSIGNED NULL,
	colony_id                  VARCHAR(100) NULL,
	metadata_combined          TEXT,
	metadata_group             VARCHAR(50) DEFAULT '',
	procedure_status           VARCHAR(50) DEFAULT NULL,
	procedure_status_message   VARCHAR(450) DEFAULT NULL,

	PRIMARY KEY(id),
	KEY external_db_idx(db_id),
	KEY organisation_idx(organisation_id),
	KEY pipeline_idx(pipeline_id),
	KEY pipeline_stable_idx(pipeline_stable_id),
	KEY procedure_idx(procedure_id),
  KEY procedure_stable_idx(procedure_stable_id),
  KEY biological_model_idx(biological_model_id),
  KEY colony_idx(colony_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * Links multiple observations to experiment
 */

CREATE TABLE experiment_observation (
	experiment_id              INT(10) UNSIGNED NOT NULL,
	observation_id             INT(10) UNSIGNED NOT NULL,

	KEY experiment_idx(experiment_id),
	KEY observation_idx(observation_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * observation
 * An observation is a experimental parameter measurement (data point, image
 * record, etc.)
 * of a phenotype of a given control/experimental biological sample.
 * Measurement are diverse. observation_type is the discriminator in this table.
 * Children observation tables represent the diversity of the type of
 * observations.
 * db_id: indicates where the data are coming from. Convenient when selecting or
 * deleting data from this table
 * missing: tells if there was no data for this observation.
 */
CREATE TABLE observation (

	id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	db_id                      INT(10) UNSIGNED NOT NULL,
	biological_sample_id       INT(10) UNSIGNED NULL,
	parameter_id               INT(10) UNSIGNED NOT NULL,
  parameter_stable_id        VARCHAR(30) NOT NULL,
  sequence_id                VARCHAR(30) DEFAULT NULL,
	population_id              INT(10) UNSIGNED NOT NULL,
	observation_type           ENUM('categorical', 'datetime', 'ontological', 'image_record', 'unidimensional', 'multidimensional', 'time_series', 'metadata', 'text'),
	missing                    TINYINT(1) DEFAULT 0,
	parameter_status           VARCHAR(50) DEFAULT NULL,
	parameter_status_message   VARCHAR(450) DEFAULT NULL,

	PRIMARY KEY(id),
	KEY biological_sample_idx(biological_sample_id),
	KEY parameter_idx(parameter_id),
	KEY parameter_stable_idx(parameter_stable_id),
	KEY population_idx(population_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


/**
 * text_observation
 * Free text to annotate a phenotype
 */
CREATE TABLE text_observation (

	id                         INT(10) UNSIGNED NOT NULL,
	text                       TEXT,

	PRIMARY KEY(id),
	KEY text_idx(text(255))

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * categorical_observation
 * Categorical phenotypic observation like
 * coat hair pattern: mono-colored, multi-colored, spotted, etc.
 */
CREATE TABLE categorical_observation (

	id                         INT(10) UNSIGNED NOT NULL,
	category                   VARCHAR(200) NOT NULL,

	PRIMARY KEY(id),
	KEY category_idx(category)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * This table will store the ontology 'consider id's.
 */
CREATE TABLE consider_id (

	ontology_term_acc          VARCHAR(20) NOT NULL,
	acc                        VARCHAR(20) NOT NULL,

  FOREIGN KEY ontology_term_acc_fk (ontology_term_acc) REFERENCES ontology_term (acc)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * unidimensional_observation
 * Unidimensional data point measurement
 */
CREATE TABLE unidimensional_observation (

	id                        INT(10) UNSIGNED NOT NULL,
	data_point                FLOAT NOT NULL,

	PRIMARY KEY(id),
	KEY data_point_idx(data_point)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * ontology_observation
 * ontology data measurement/observation
 */
CREATE TABLE ontology_observation (

	id           INT(10) UNSIGNED NOT NULL,
	parameter_id VARCHAR(255)     NOT NULL, /**not necessary to store this as in main parameter when store observation, we should remove it but in for dev testing**/
	sequence_id  INT(10)          NULL,

	PRIMARY KEY (id)

)
	COLLATE = utf8_general_ci
	ENGINE = MyISAM;

/**
 * unidimensional_observation
 * Unidimensional data point measurement
 */
CREATE TABLE ontology_entity (

	id                      INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	ontology_observation_id INT(10) UNSIGNED NOT NULL,
	term                    VARCHAR(255)     NULL,
	term_value              VARCHAR(255)     NULL,
	PRIMARY KEY (id),
	KEY `idx_ontology_entity_ontology_observation_id` (`ontology_observation_id`)
)
	COLLATE = utf8_general_ci
	ENGINE = MyISAM;

/**
 * multidimensional_observation
 * multidimensional data point measurement
 * data_value: store the value of the observation in dimension 'dimension'
 * order_index: useful for time series or series of values. Keep the order
 * of observations in multiple dimension
 * dimension: dimension definition (x, y, z, t, etc.). It can also be used to
 * store multiple series of observations if needed.
 */
CREATE TABLE multidimensional_observation (

	id                        INT(10) UNSIGNED NOT NULL,
	data_point                FLOAT NOT NULL,
	order_index               INT(10) NOT NULL,
	dimension                 VARCHAR(40) NOT NULL,

	PRIMARY KEY(id),
	KEY data_point_idx(data_point, order_index),
	KEY dimension_idx(dimension)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * time_series_observation
 * A time series is a sequence of observations which are ordered in time
 * (or space).
 */
CREATE TABLE time_series_observation (

	id                        INT(10) UNSIGNED NOT NULL,
	data_point                FLOAT NOT NULL,
	time_point                TIMESTAMP,
	discrete_point            FLOAT,

	PRIMARY KEY(id),
	KEY data_point_idx(data_point, time_point)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
 * datetime_observation
 * A datetime observation is a point in time observation
 * (or space).
 */
CREATE TABLE datetime_observation (

	id                        INT(10) UNSIGNED NOT NULL,
	datetime_point            datetime,

	PRIMARY KEY(id),
	KEY datetime_point_idx(datetime_point)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


/**
 * metadata_observation
 * Some experimental settings can change from one experiment to another.
 * This table stores the meta information associated to the observation
 * as value property
 */
CREATE TABLE metadata_observation (

	id                         INT(10) UNSIGNED NOT NULL,
	property_value             VARCHAR(100) NOT NULL,

	PRIMARY KEY(id),
	KEY property_value_idx(property_value)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


/**
 * --------------------------
 * Phenotype pipeline tables
 * --------------------------
 */

/**
@table phenotype_pipeline
@desc This table stores information about each phenotyping pipeline
At the moment, this information is managed by MRC Harwell in 2 central
resources called EMPReSS and IMPReSS
The Phenotype archive stores this information for convenience

@column id                    internal id of the pipeline. Primary key.
@column stable_id             stable id from external resource
@column db_id                 external db id
@column name                  pipeline name.

@see external_db
*/
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

/**
@table phenotype_pipeline_procedure
@desc This table joins associations between procedures and pipelines

@column pipeline_id           pipeline internal id. Foreign key references to the @link phenotype_pipeline table.
@column procedure_id          procedure internal id. Foreign key references to the @link phenotype_procedure table.

@see phenotype_pipeline
@see phenotype_procedure
*/
CREATE TABLE phenotype_pipeline_procedure (

	pipeline_id                INT(10) UNSIGNED NOT NULL,
	procedure_id               INT(10) UNSIGNED NOT NULL,

	KEY pipeline_idx (pipeline_id),
	KEY procedure_idx (procedure_id),
	UNIQUE (pipeline_id, procedure_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
@table phenotype_procedure
@desc This table stores information about each phenotyping procedure

@column id                    internal id of the procedure. Primary key
@column stable_id             stable id from external resource
@column db_id                 external db id
@column name                  procedure name.

@see phenotype_pipeline
@see external_db

*/
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

	PRIMARY KEY (id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
@table phenotype_procedure_meta_data
@desc This table stores information about each phenotyping procedure meta data
In EMPReSS, currently we store information about aging only.

@column procedure_id          The procedure this annotation belongs too. Foreign key reference to the @link phenotype_procedure table.
@column meta_name             name of the meta data
@column value                 value of the meta data as a string

@see phenotype_procedure

*/
CREATE TABLE phenotype_procedure_meta_data (

	procedure_id               INT(10) UNSIGNED NOT NULL,
	meta_name                  VARCHAR(40) NOT NULL,
	meta_value                 VARCHAR(40) NOT NULL,

	KEY procedure_meta_data_idx (procedure_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/**
@table phenotype_procedure_parameter
@desc This table joins associations between procedures and parameters

@column procedure_id          procedure internal id. Foreign key references to the @link phenotype_procedure table.
@column parameter_id          parameter internal id. Foreign key references to the @link phenotype_parameter table.

@see phenotype_procedure
@see phenotype_parameter
*/
CREATE TABLE phenotype_procedure_parameter (

	procedure_id               INT(10) UNSIGNED NOT NULL,
	parameter_id               INT(10) UNSIGNED NOT NULL,

	KEY procedure_idx (procedure_id),
	KEY parameter_idx (parameter_id),
	UNIQUE (procedure_id, parameter_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


-- because of IMPReSS we have a different length for stable_id
-- description for parameter is TEXT

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
	data_analysis             TINYINT(1) DEFAULT 0,
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

-- truncate table phenotype_parameter; truncate table phenotype_pipeline; truncate table phenotype_procedure; truncate table phenotype_pipeline_procedure; truncate table phenotype_procedure_meta_data; truncate table phenotype_parameter_option; truncate table phenotype_parameter_increment;

CREATE TABLE phenotype_parameter_lnk_ontology_annotation (

	annotation_id             INT(10) UNSIGNED NOT NULL,
	parameter_id              INT(10) UNSIGNED NOT NULL,

	KEY parameter_idx (parameter_id),
	KEY annotation_idx (annotation_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/*
 * We compute the significance of the occurence of some observation
 * by comparison with WT/control animals.
 *
 */
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

/*
 * We compute the significance of the occurence of some observation
 * by comparison with WT/control animals.
 *
 */
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

/*
 * Phenotype to genotype association when analyzed without weight
 */

CREATE TABLE phenotype_call_summary (

	id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	external_id               VARCHAR(20) NULL,
	external_db_id            INT(10),
	project_id                INT(10) UNSIGNED NOT NULL,
	organisation_id           INT(10) UNSIGNED NOT NULL,
	gf_acc                    VARCHAR(20),
	gf_db_id                  INT(10),
	strain_acc                VARCHAR(20),
	strain_db_id              INT(10),
	allele_acc                VARCHAR(20),
	allele_db_id              INT(10),
	colony_id                 VARCHAR(200) NULL,
	sex                       ENUM('female', 'hermaphrodite', 'male', 'both', 'not_applicable', 'no_data'),
	zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	parameter_id              INT(10) UNSIGNED NOT NULL,
	procedure_id              INT(10) UNSIGNED NOT NULL,
	pipeline_id               INT(10) UNSIGNED NOT NULL,



	mp_acc                    VARCHAR(20) NOT NULL,
	mp_db_id                  INT(10) NOT NULL,

	p_value                   DOUBLE NULL DEFAULT 1,
	effect_size               DOUBLE NULL DEFAULT 0,

	PRIMARY KEY (id),
	KEY parameter_call_idx (parameter_id),
	KEY procedure_call_idx (procedure_id),
	KEY pipeline_call_idx (pipeline_id),
	KEY organisation_idx (pipeline_id, organisation_id),
	KEY allele_idx (allele_acc, allele_db_id),
	KEY mp_call_idx (mp_acc)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


/*
 * Phenotype to genotype association when analyzed with weight
 */
DROP TABLE IF EXISTS phenotype_call_summary_withWeight;
CREATE TABLE phenotype_call_summary_withWeight (

  id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  external_id               VARCHAR(20) NULL,
  external_db_id            INT(10),
  project_id                INT(10) UNSIGNED NOT NULL,
  organisation_id           INT(10) UNSIGNED NOT NULL,
  gf_acc                    VARCHAR(20),
  gf_db_id                  INT(10),
  strain_acc                VARCHAR(20),
  strain_db_id              INT(10),
  allele_acc                VARCHAR(20),
  allele_db_id              INT(10),
  colony_id                 VARCHAR(200) NULL,
  sex                       ENUM('female', 'hermaphrodite', 'male', 'not_applicable', 'no_data'),
  zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
  parameter_id              INT(10) UNSIGNED NOT NULL,
  procedure_id              INT(10) UNSIGNED NOT NULL,
  pipeline_id               INT(10) UNSIGNED NOT NULL,

  mp_acc                    VARCHAR(20) NOT NULL,
  mp_db_id                  INT(10) NOT NULL,

  p_value                   DOUBLE NULL DEFAULT 1,
  effect_size               DOUBLE NULL DEFAULT 0,

  PRIMARY KEY (id),
  KEY parameter_call_idx (parameter_id),
  KEY procedure_call_idx (procedure_id),
  KEY pipeline_call_idx (pipeline_id),
  KEY organisation_idx (pipeline_id, organisation_id),
  KEY allele_idx (allele_acc, allele_db_id),
  KEY mp_call_idx (mp_acc)

) COLLATE=utf8_general_ci ENGINE=MyISAM;


CREATE TABLE anatomy_call_summary (

	id                        INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	external_id               VARCHAR(20) NULL,
	external_db_id            INT(10),
	project_id                INT(10) UNSIGNED NOT NULL,
	organisation_id           INT(10) UNSIGNED NOT NULL,
	gf_acc                    VARCHAR(20),
	gf_db_id                  INT(10),
	background_strain_acc     VARCHAR(20),
	background_strain_db_id   INT(10),
	allele_acc                VARCHAR(20),
	allele_db_id              INT(10),
	colony_id                 VARCHAR(200) NULL,
	sex                       ENUM('female', 'hermaphrodite', 'male', 'not_applicable', 'no_data'),
	zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	parameter_id              INT(10) UNSIGNED NOT NULL,
	procedure_id              INT(10) UNSIGNED NOT NULL,
	pipeline_id               INT(10) UNSIGNED NOT NULL,

	anatomy_acc               VARCHAR(20) NOT NULL,
	anatomy_db_id             INT(10) NOT NULL,

	expression                VARCHAR(200),

	PRIMARY KEY (id),
	KEY parameter_call_idx (parameter_id),
	KEY procedure_call_idx (procedure_id),
	KEY pipeline_call_idx (pipeline_id),
	KEY organisation_idx (organisation_id),
	KEY allele_idx (allele_acc, allele_db_id),
	KEY anatomy_acc_idx (anatomy_acc, anatomy_db_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM;

/*
 * Tables below are for the storage of media/image information
 */
CREATE TABLE `image_record_observation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sample_id` int(11) DEFAULT NULL,
  `original_file_name` varchar(1024) DEFAULT NULL,
  `creator_id` int(11) DEFAULT NULL,
  `full_resolution_file_path` varchar(256) DEFAULT NULL,
  `small_thumbnail_file_path` varchar(256) DEFAULT NULL,
  `large_thumbnail_file_path` varchar(256) DEFAULT NULL,
  `download_file_path` varchar(256) DEFAULT NULL,
  `image_link` varchar(256) DEFAULT NULL,
  `organisation_id` int(11) NOT NULL DEFAULT '0',
  `increment_value` varchar(45) DEFAULT NULL,
  `file_type` varchar(45) DEFAULT NULL,
  `media_sample_local_id` varchar(45) DEFAULT NULL,
  `media_section_id` varchar(45) DEFAULT NULL,
  `omero_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `OMERO_ID` (`omero_id`),
  KEY `FULL_RES_FILE_PATH` (`full_resolution_file_path`)
) COLLATE=utf8_general_ci ENGINE=MyISAM ;


CREATE TABLE dimension (

	dim_id                     INT(11) NOT NULL AUTO_INCREMENT,
	parameter_association_id   INT(11) NOT NULL,
	id                         VARCHAR(45) NOT NULL,
	origin                     VARCHAR(45) NOT NULL,
	unit                       VARCHAR(45) DEFAULT NULL,
	value                      DECIMAL(65,10) DEFAULT NULL,

	PRIMARY KEY (dim_id, parameter_association_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM ;


CREATE TABLE parameter_association (

	id                         INT(11) NOT NULL AUTO_INCREMENT,
	observation_id             VARCHAR(45) NOT NULL,
	parameter_id               VARCHAR(45) NOT NULL,
	sequence_id                INT(11) DEFAULT NULL,
	dim_id                     VARCHAR(45) DEFAULT NULL,
	parameter_association_value					VARCHAR(45) DEFAULT NULL,

	PRIMARY KEY (id)

) COLLATE=utf8_general_ci ENGINE=MyISAM ;


CREATE TABLE procedure_meta_data (

	id                         INT(11) NOT NULL AUTO_INCREMENT,
	parameter_id               VARCHAR(45) NOT NULL,
	sequence_id                VARCHAR(45) DEFAULT NULL,
	parameter_status           VARCHAR(450) DEFAULT NULL,
	value                      VARCHAR(450) DEFAULT NULL,
	procedure_id               VARCHAR(45) NOT NULL,
	experiment_id              INT(11) NOT NULL,
	observation_id             INT(11) DEFAULT '0',

	PRIMARY KEY (id),
	KEY procedure_meta_data_experiment_idx (experiment_id),
	KEY procedure_meta_data_parameter_idx (parameter_id)

) COLLATE=utf8_general_ci ENGINE=MyISAM ;


CREATE TABLE genes_secondary_project (

  acc                        VARCHAR(20) NOT NULL,
  secondary_project_id       VARCHAR(20) NOT NULL

) COLLATE=utf8_general_ci ENGINE=MyISAM;

-- -----------------------------------------------------------
-- Tables to store statistical results
-- -----------------------------------------------------------

/*
 * store the result of a fishers test calculation
 */
DROP TABLE IF EXISTS stats_categorical_results;
CREATE TABLE stats_categorical_results (

	id                         INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	control_id                 INT(10) UNSIGNED,
	control_sex                ENUM('female', 'hermaphrodite', 'male', 'not_applicable', 'both', 'no_data'),
	experimental_id            INT(10) UNSIGNED,
	experimental_sex           ENUM('female', 'hermaphrodite', 'male', 'not_applicable', 'both', 'no_data'),
	experimental_zygosity      ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	external_db_id             INT(10),
	project_id                 INT(10) UNSIGNED,
	organisation_id            INT(10) UNSIGNED,
	pipeline_id                INT(10) UNSIGNED,
	procedure_id               INT(10) UNSIGNED,
	parameter_id               INT(10) UNSIGNED,
	colony_id                  VARCHAR(200),
	dependent_variable         VARCHAR(200),
	mp_acc                     VARCHAR(20)      NULL,
	mp_db_id                   INT(10)          NULL,
	control_selection_strategy VARCHAR(100),
	male_controls              INT(10) UNSIGNED,
	male_mutants               INT(10) UNSIGNED,
	female_controls            INT(10) UNSIGNED,
	female_mutants             INT(10) UNSIGNED,
	metadata_group             VARCHAR(50) DEFAULT '',
	statistical_method         VARCHAR(50),
	workflow                   VARCHAR(50),
	weight_available           VARCHAR(50),
	status                     VARCHAR(200),
	category_a                 TEXT,
	category_b                 TEXT,
	p_value                    DOUBLE,
	effect_size                DOUBLE,
	raw_output                 MEDIUMTEXT,
	authoritative              BOOLEAN,

	PRIMARY KEY (id),
	KEY control_idx (control_id),
	KEY experimental_idx (experimental_id),
	KEY organisation_idx (organisation_id),
	KEY pipeline_idx (pipeline_id),
	KEY parameter_idx (parameter_id)

) COLLATE =utf8_general_ci ENGINE =MyISAM;

/*
 * store the result of a PhenStat calculation
 */
DROP TABLE IF EXISTS stats_unidimensional_results;
CREATE TABLE stats_unidimensional_results (

	id                               INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	control_id                       INT(10) UNSIGNED,
	experimental_id                  INT(10) UNSIGNED,
	experimental_zygosity            ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	external_db_id                   INT(10),
	project_id                       INT(10) UNSIGNED,
	organisation_id                  INT(10) UNSIGNED,
	pipeline_id                      INT(10) UNSIGNED,
	procedure_id                     INT(10) UNSIGNED,
	parameter_id                     INT(10) UNSIGNED,
	colony_id                        VARCHAR(200),
	dependent_variable               VARCHAR(200),
	control_selection_strategy       VARCHAR(100),
	mp_acc                           VARCHAR(20)      NULL,
	mp_db_id                         INT(10)          NULL,
	male_mp_acc                      VARCHAR(20)      NULL,
	female_mp_acc                    VARCHAR(20)      NULL,
	male_controls                    INT(10) UNSIGNED,
	male_mutants                     INT(10) UNSIGNED,
	female_controls                  INT(10) UNSIGNED,
	female_mutants                   INT(10) UNSIGNED,
	female_control_mean              DOUBLE NULL,
	male_control_mean                DOUBLE NULL,
	female_experimental_mean         DOUBLE NULL,
	male_experimental_mean           DOUBLE NULL,
	metadata_group                   VARCHAR(50) DEFAULT '',
	statistical_method               VARCHAR(200),
	workflow                         VARCHAR(50),
	weight_available                 VARCHAR(50),
	status                           VARCHAR(200),
	batch_significance               BOOLEAN,
	variance_significance            BOOLEAN,
	null_test_significance           DOUBLE,
	genotype_parameter_estimate      DOUBLE,
	genotype_stderr_estimate         DOUBLE,
	genotype_effect_pvalue           DOUBLE,
	genotype_percentage_change       VARCHAR(200),
	gender_parameter_estimate        DOUBLE,
	gender_stderr_estimate           DOUBLE,
	gender_effect_pvalue             DOUBLE,
	weight_parameter_estimate        DOUBLE,
	weight_stderr_estimate           DOUBLE,
	weight_effect_pvalue             DOUBLE,
	gp1_genotype                     VARCHAR(200),
	gp1_residuals_normality_test     DOUBLE,
	gp2_genotype                     VARCHAR(200),
	gp2_residuals_normality_test     DOUBLE,
	blups_test                       DOUBLE,
	rotated_residuals_normality_test DOUBLE,
	intercept_estimate               DOUBLE,
	intercept_stderr_estimate        DOUBLE,
	interaction_significance         BOOLEAN,
	interaction_effect_pvalue        DOUBLE,
	gender_female_ko_estimate        DOUBLE,
	gender_female_ko_stderr_estimate DOUBLE,
	gender_female_ko_pvalue          DOUBLE,
	gender_male_ko_estimate          DOUBLE,
	gender_male_ko_stderr_estimate   DOUBLE,
	gender_male_ko_pvalue            DOUBLE,
	classification_tag               VARCHAR(200),
	additional_information           TEXT,
	raw_output                       MEDIUMTEXT,
	authoritative                    BOOLEAN,

	PRIMARY KEY (id),
	KEY organisation_idx (organisation_id),
	KEY pipeline_idx (pipeline_id),
	KEY parameter_idx (parameter_id)

) COLLATE =utf8_general_ci ENGINE =MyISAM;


/*
 * store the result of a PhenStat calculation when analyzed with weight
 */
DROP TABLE IF EXISTS stats_unidimensional_results_withWeight;
CREATE TABLE stats_unidimensional_results_withWeight (

  id                               INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  control_id                       INT(10) UNSIGNED,
  experimental_id                  INT(10) UNSIGNED,
  experimental_zygosity            ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
  external_db_id                   INT(10),
  project_id                       INT(10) UNSIGNED,
  organisation_id                  INT(10) UNSIGNED,
  pipeline_id                      INT(10) UNSIGNED,
  procedure_id                     INT(10) UNSIGNED,
  parameter_id                     INT(10) UNSIGNED,
  colony_id                        VARCHAR(200),
  dependent_variable               VARCHAR(200),
  control_selection_strategy       VARCHAR(100),
  mp_acc                           VARCHAR(20)      NULL,
  mp_db_id                         INT(10)          NULL,
  male_mp_acc                      VARCHAR(20)      NULL,
  female_mp_acc                    VARCHAR(20)      NULL,
  male_controls                    INT(10) UNSIGNED,
  male_mutants                     INT(10) UNSIGNED,
  female_controls                  INT(10) UNSIGNED,
  female_mutants                   INT(10) UNSIGNED,
  female_control_mean              DOUBLE NULL,
  male_control_mean                DOUBLE NULL,
  female_experimental_mean         DOUBLE NULL,
  male_experimental_mean           DOUBLE NULL,
  metadata_group                   VARCHAR(50) DEFAULT '',
  statistical_method               VARCHAR(200),
  workflow                         VARCHAR(50),
  weight_available                 VARCHAR(50),
  status                           VARCHAR(200),
  batch_significance               BOOLEAN,
  variance_significance            BOOLEAN,
  null_test_significance           DOUBLE,
  genotype_parameter_estimate      DOUBLE,
  genotype_stderr_estimate         DOUBLE,
  genotype_effect_pvalue           DOUBLE,
  genotype_percentage_change       VARCHAR(200),
  gender_parameter_estimate        DOUBLE,
  gender_stderr_estimate           DOUBLE,
  gender_effect_pvalue             DOUBLE,
  weight_parameter_estimate        DOUBLE,
  weight_stderr_estimate           DOUBLE,
  weight_effect_pvalue             DOUBLE,
  gp1_genotype                     VARCHAR(200),
  gp1_residuals_normality_test     DOUBLE,
  gp2_genotype                     VARCHAR(200),
  gp2_residuals_normality_test     DOUBLE,
  blups_test                       DOUBLE,
  rotated_residuals_normality_test DOUBLE,
  intercept_estimate               DOUBLE,
  intercept_stderr_estimate        DOUBLE,
  interaction_significance         BOOLEAN,
  interaction_effect_pvalue        DOUBLE,
  gender_female_ko_estimate        DOUBLE,
  gender_female_ko_stderr_estimate DOUBLE,
  gender_female_ko_pvalue          DOUBLE,
  gender_male_ko_estimate          DOUBLE,
  gender_male_ko_stderr_estimate   DOUBLE,
  gender_male_ko_pvalue            DOUBLE,
  classification_tag               VARCHAR(200),
  additional_information           TEXT,
  raw_output                       MEDIUMTEXT,
  authoritative                    BOOLEAN,

  PRIMARY KEY (id),
  KEY organisation_idx (organisation_id),
  KEY pipeline_idx (pipeline_id),
  KEY parameter_idx (parameter_id)

)
  COLLATE =utf8_general_ci
  ENGINE =MyISAM;


/*
 * store the result of a PhenStat calculation
 */
DROP TABLE IF EXISTS stats_rrplus_results;
CREATE TABLE stats_rrplus_results (

  id                               INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  control_id                       INT(10) UNSIGNED,
  experimental_id                  INT(10) UNSIGNED,
  experimental_zygosity            ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
  external_db_id                   INT(10),
  project_id                       INT(10) UNSIGNED,
  organisation_id                  INT(10) UNSIGNED,
  pipeline_id                      INT(10) UNSIGNED,
  procedure_id                     INT(10) UNSIGNED,
  parameter_id                     INT(10) UNSIGNED,
  colony_id                        VARCHAR(200),
  dependent_variable               VARCHAR(200),
  control_selection_strategy       VARCHAR(100),
  mp_acc                           VARCHAR(20)      NULL,
  mp_db_id                         INT(10)          NULL,
  male_mp_acc                      VARCHAR(20)      NULL,
  female_mp_acc                    VARCHAR(20)      NULL,
  male_controls                    INT(10) UNSIGNED,
  male_mutants                     INT(10) UNSIGNED,
  female_controls                  INT(10) UNSIGNED,
  female_mutants                   INT(10) UNSIGNED,
  female_control_mean              DOUBLE NULL,
  male_control_mean                DOUBLE NULL,
  female_experimental_mean         DOUBLE NULL,
  male_experimental_mean           DOUBLE NULL,
  metadata_group                   VARCHAR(50) DEFAULT '',
  statistical_method               VARCHAR(200),
  workflow                         VARCHAR(50),
  weight_available                 VARCHAR(50),
  status                           VARCHAR(200),
  batch_significance               BOOLEAN,
  variance_significance            BOOLEAN,
  null_test_significance           DOUBLE,
  genotype_parameter_estimate      VARCHAR(200),
  genotype_stderr_estimate         DOUBLE,
  genotype_effect_pvalue           VARCHAR(200),
  genotype_percentage_change       VARCHAR(200),
  gender_parameter_estimate        VARCHAR(200),
  gender_stderr_estimate           DOUBLE,
  gender_effect_pvalue             VARCHAR(200),
  weight_parameter_estimate        DOUBLE,
  weight_stderr_estimate           DOUBLE,
  weight_effect_pvalue             DOUBLE,
  gp1_genotype                     VARCHAR(200),
  gp1_residuals_normality_test     DOUBLE,
  gp2_genotype                     VARCHAR(200),
  gp2_residuals_normality_test     DOUBLE,
  blups_test                       DOUBLE,
  rotated_residuals_normality_test DOUBLE,
  intercept_estimate               DOUBLE,
  intercept_stderr_estimate        DOUBLE,
  interaction_significance         BOOLEAN,
  interaction_effect_pvalue        DOUBLE,
  gender_female_ko_estimate        VARCHAR(200),
  gender_female_ko_stderr_estimate DOUBLE,
  gender_female_ko_pvalue          VARCHAR(200),
  gender_male_ko_estimate          VARCHAR(200),
  gender_male_ko_stderr_estimate   DOUBLE,
  gender_male_ko_pvalue            VARCHAR(200),
  classification_tag               VARCHAR(200),
  additional_information           TEXT,
  raw_output                       MEDIUMTEXT,
  authoritative                    BOOLEAN,

  PRIMARY KEY (id),
  KEY organisation_idx (organisation_id),
  KEY pipeline_idx (pipeline_id),
  KEY parameter_idx (parameter_id)

)
  COLLATE =utf8_general_ci
  ENGINE =MyISAM;


/*
 * Store the relationship between a phenotype call and the result that
 * produced the call.  Could refer to either the stats_categorical_result
 * or a stats_unidimensional_result or a stats_rrplus_result
 */
DROP TABLE IF EXISTS stat_result_phenotype_call_summary;
CREATE TABLE stat_result_phenotype_call_summary (

	categorical_result_id     INT(10) UNSIGNED DEFAULT NULL,
	unidimensional_result_id  INT(10) UNSIGNED DEFAULT NULL,
  rrplus_result_id          INT(10) UNSIGNED DEFAULT NULL,
	phenotype_call_summary_id INT(10) UNSIGNED NOT NULL,

	PRIMARY KEY (phenotype_call_summary_id),
	INDEX srpcs_categorical_result_id_idx (categorical_result_id),
	Index srpcs_unidimensional_result_id_idx (unidimensional_result_id)

)
	COLLATE =utf8_general_ci
	ENGINE =MyISAM;


--
-- Discrete statistical result schema
--

DROP TABLE IF EXISTS statistical_result;
CREATE TABLE statistical_result (

	id                               INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	control_id                       INT(10) UNSIGNED,
	experimental_id                  INT(10) UNSIGNED,
	experimental_zygosity            ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	external_db_id                   INT(10),
	project_id                       INT(10) UNSIGNED,
	organisation_id                  INT(10) UNSIGNED,
	pipeline_id                      INT(10) UNSIGNED,
	procedure_id                     INT(10) UNSIGNED,
	parameter_id                     INT(10) UNSIGNED,
	pipeline_stable_id               VARCHAR(50),
	procedure_stable_id              VARCHAR(50),
	parameter_stable_id              VARCHAR(50),
	gene_acc                         VARCHAR(20)      NULL,
	colony_id                        VARCHAR(200),
	control_selection_strategy       VARCHAR(100),
	male_mp_acc                      VARCHAR(20)      NULL,
	female_mp_acc                    VARCHAR(20)      NULL,
	male_controls                    INT(10) UNSIGNED,
	male_mutants                     INT(10) UNSIGNED,
	female_controls                  INT(10) UNSIGNED,
	female_mutants                   INT(10) UNSIGNED,
	metadata_group                   VARCHAR(50) DEFAULT '',
	statistical_method               VARCHAR(200),
	workflow                         VARCHAR(50),
	weight_available                 VARCHAR(50),
	status                           VARCHAR(200),
	type                             VARCHAR(200),
	classification_tag               VARCHAR(200),
	male_pvalue                      DOUBLE,
	male_effect_size                 DOUBLE,
	male_stderr                      DOUBLE,
	female_pvalue                    DOUBLE,
	female_effect_size               DOUBLE,
	female_stderr                    DOUBLE,
	pvalue                           DOUBLE,
	effect_size                      DOUBLE,
	stderr                           DOUBLE,
	authoritative                    BOOLEAN,

	PRIMARY KEY (id),
	KEY organisation_idx (organisation_id),
	KEY pipeline_idx (pipeline_id),
	KEY parameter_idx (parameter_id)

)
	COLLATE =utf8_general_ci
	ENGINE =MyISAM;


DROP TABLE IF EXISTS statistical_result_phenotype_call_summary;
CREATE TABLE statistical_result_phenotype_call_summary (

  phenotype_call_summary_id INT(10) UNSIGNED NOT NULL,
  result_id                 INT(10) UNSIGNED,

  PRIMARY KEY (phenotype_call_summary_id),
  FOREIGN KEY result_idx (result_id) REFERENCES statistical_result (id)

)
  COLLATE =utf8_general_ci
  ENGINE =MyISAM;

DROP TABLE IF EXISTS statistical_result_additional;
CREATE TABLE statistical_result_additional (
	id                         INT(10) UNSIGNED NOT NULL,
	raw_output                 MEDIUMTEXT,
	dataset                    MEDIUMTEXT,

	PRIMARY KEY (id),
	FOREIGN KEY result_idx (id) REFERENCES statistical_result (id)

)
	COLLATE =utf8_general_ci
	ENGINE =MyISAM;


DROP TABLE IF EXISTS statistical_result_phenstat;
CREATE TABLE statistical_result_phenstat (

	id                               INT(10) UNSIGNED NOT NULL,
	batch_significance               BOOLEAN,
	interaction_significance         BOOLEAN,
	variance_significance            BOOLEAN,
	genotype_contribution_pvalue     DOUBLE,
	genotype_effect_pvalue           DOUBLE,
	genotype_parameter_estimate      DOUBLE,
	genotype_stderr_estimate         DOUBLE,
	genotype_percentage_change       VARCHAR(200),
	gender_effect_pvalue             DOUBLE,
	gender_parameter_estimate        DOUBLE,
	gender_stderr_estimate           DOUBLE,
	gender_male_effect_pvalue        DOUBLE,
	gender_male_parameter_estimate   DOUBLE,
	gender_male_stderr_estimate      DOUBLE,
	gender_female_effect_pvalue      DOUBLE,
	gender_female_parameter_estimate DOUBLE,
	gender_female_stderr_estimate    DOUBLE,
	weight_effect_pvalue             DOUBLE,
	weight_parameter_estimate        DOUBLE,
	weight_stderr_estimate           DOUBLE,
	group1_genotype                  VARCHAR(200),
	group1_residuals_normality_test  DOUBLE,
	group2_genotype                  VARCHAR(200),
	group2_residuals_normality_test  DOUBLE,
	blups_test                       DOUBLE,
	rotated_residuals_normality_test DOUBLE,
	intercept_estimate               DOUBLE,
	intercept_stderr_estimate        DOUBLE,
	interaction_effect_pvalue        DOUBLE,
	classification_tag               VARCHAR(200),

	PRIMARY KEY (id),
	FOREIGN KEY result_idx (id) REFERENCES statistical_result (id)

)
	COLLATE =utf8_general_ci
	ENGINE =MyISAM;


DROP TABLE IF EXISTS statistical_result_fisher_exact;
CREATE TABLE statistical_result_fisher_exact (

	id         INT(10) UNSIGNED NOT NULL,
	category_a TEXT,
	category_b TEXT,

	PRIMARY KEY (id),
	FOREIGN KEY result_idx (id) REFERENCES statistical_result (id)

)
	COLLATE =utf8_general_ci
	ENGINE =MyISAM;


DROP TABLE IF EXISTS statistical_result_manual;
CREATE TABLE statistical_result_manual (

	id     INT(10) UNSIGNED NOT NULL,
	method VARCHAR(200),

	PRIMARY KEY (id),
	FOREIGN KEY result_idx (id) REFERENCES statistical_result (id)

)
	COLLATE =utf8_general_ci
	ENGINE =MyISAM;


--
-- Sanger Imaging tables schema
--
DROP TABLE IF EXISTS PHN_REQUIRED_OBSERVATION;
CREATE TABLE PHN_REQUIRED_OBSERVATION (
  ID                    INT(11)      NOT NULL,
  OBSERVATION_TYPE_ID   INT(11)      NOT NULL,
  ORDER_BY              INT(11)      NOT NULL,
  DISPLAY_NAME          VARCHAR(256) NOT NULL,
  ACTIVE                INT(11)      NOT NULL,
  CREATED_DATE          DATE         DEFAULT NULL,
  CREATOR_ID            INT(11)      DEFAULT NULL,
  EDIT_DATE             DATE         DEFAULT NULL,
  EDITED_BY             VARCHAR(256) DEFAULT NULL,
  CHECK_NUMBER          INT(11)      DEFAULT NULL,
  SOP_ID                INT(10)      DEFAULT NULL,
  DEFAULT_VALUE         VARCHAR(256) DEFAULT NULL,
  MANDATORY_OBSERVATION INT(10)      DEFAULT 1,
  SECTION_TITLE         VARCHAR(256),
  IS_MACHINE_DATA       INT(10)      DEFAULT 0,
  IS_SERIES_DATA        INT(10)      DEFAULT 0,
  VIEW_COLUMN_NAME      VARCHAR(30)  DEFAULT NULL,
  PARENT_REQ_OBS_ID     INT(10)      DEFAULT NULL,
  IS_VIEWABLE           INT(10)      DEFAULT 1,
  UNIQUE_NAME           VARCHAR(256),
  PRIMARY KEY (ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS PHN_EXPERIMENT;
CREATE TABLE PHN_EXPERIMENT (
  ID                   INT(11) NOT NULL,
  SOP_ID               INT(11) NOT NULL,
  MOUSE_ID             INT(11) NOT NULL,
  IS_COMPLETE          VARCHAR(256) DEFAULT NULL,
  CREATED_DATE         DATE         DEFAULT NULL,
  CREATOR_ID           INT(11) NOT NULL,
  EDIT_DATE            DATE         DEFAULT NULL,
  EDITED_BY            VARCHAR(256) DEFAULT NULL,
  CHECK_NUMBER         INT(11) NOT NULL,
  EXPERIMENT_SET_ID    INT(11) NOT NULL,
  QC_COMMENTS          VARCHAR(256) DEFAULT NULL,
  QC_FAILURE_REASON_ID INT(11) NOT NULL,
  COMMENTS             VARCHAR(256) DEFAULT NULL,
  PRIMARY KEY (ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS PHN_OBSERVATION;
CREATE TABLE PHN_OBSERVATION (
  ID                    INT(11) NOT NULL,
  EXPERIMENT_ID         INT(11) NOT NULL,
  REQUIRED_OBSERVATION  INT(11) NOT NULL,
  VALUE                 VARCHAR(256) DEFAULT NULL,
  INSTRUMENT_ID         INT(11) NOT NULL,
  OBSERVATION_TIME      TIMESTAMP,
  CREATED_DATE          DATE         DEFAULT NULL,
  CREATOR_ID            INT(11) NOT NULL,
  EDIT_DATE             DATE         DEFAULT NULL,
  EDITED_BY             VARCHAR(256) DEFAULT NULL,
  CHECK_NUMBER          INT(11) NOT NULL,
  QC_COMMENTS           VARCHAR(256) DEFAULT NULL,
  IS_FAILED_VALIDATION  INT(11)      DEFAULT 0,
  QC_FAILED             INT(11)      DEFAULT 0,
  PARENT_OBSERVATION_ID INT(11) NOT NULL,
  PRIMARY KEY (ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS IMPC_MOUSE_ALLELE_MV;
CREATE TABLE IMPC_MOUSE_ALLELE_MV (
  MOUSE_ID      INT(11) NOT NULL,
  MOUSE_NAME    VARCHAR(256)  DEFAULT NULL,
  GENDER        VARCHAR(256)  DEFAULT NULL,
  AGE_IN_WEEKS  VARCHAR(256)  DEFAULT NULL,
  GENE          VARCHAR(256)  DEFAULT NULL,
  COLONY_PREFIX VARCHAR(1024) DEFAULT NULL,
  COLONY_ID     INT(11) NOT NULL,
  ALLELE        VARCHAR(1024) DEFAULT NULL,
  GENOTYPE      VARCHAR(256)  DEFAULT NULL,
  FULL_GENOTYPE VARCHAR(256)  DEFAULT NULL,
  PRIMARY KEY (MOUSE_ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS ANN_ANNOTATION;
CREATE TABLE ANN_ANNOTATION (
  ID                 INT(11)      DEFAULT NULL,
  TERM_NAME          VARCHAR(256) DEFAULT NULL,
  TERM_ID            VARCHAR(128) DEFAULT NULL,
  ONTOLOGY_DICT_ID   INT(11)      DEFAULT NULL,
  EDIT_DATE          DATE         DEFAULT NULL,
  EDITED_BY          VARCHAR(128) DEFAULT NULL,
  CREATED_DATE       DATE         DEFAULT NULL,
  CREATOR_ID         INT(11)      DEFAULT NULL,
  CHECK_NUMBER       INT(11)      DEFAULT NULL,
  FOREIGN_TABLE_NAME VARCHAR(30)  DEFAULT NULL,
  FOREIGN_KEY_ID     INT(11)      DEFAULT NULL,
  KEY TERM_ID (TERM_ID),
  KEY ID (ID),
  KEY FOREIGN_KEY_ID (FOREIGN_KEY_ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS ANN_ONTOLOGY_DICT;
CREATE TABLE ANN_ONTOLOGY_DICT (
  ID          INT(11) NOT NULL,
  NAME        VARCHAR(256)  DEFAULT NULL,
  DESCRIPTION VARCHAR(2048) DEFAULT NULL,
  ACTIVE      TINYINT(4)    DEFAULT NULL,
  ORDER_BY    INT(11)       DEFAULT NULL,
  PRIMARY KEY (ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS IMA_DCF_IMAGE_VW;
CREATE TABLE IMA_DCF_IMAGE_VW (
  ID                        INT(11)      DEFAULT NULL,
  DOWNLOAD_FILE_PATH        VARCHAR(255) DEFAULT NULL,
  DCF_ID                    INT(11)      DEFAULT NULL,
  MOUSE_ID                  INT(11)      DEFAULT NULL,
  EXPERIMENT_ID             INT(11)      DEFAULT NULL,
  SMALL_THUMBNAIL_FILE_PATH VARCHAR(255) DEFAULT NULL,
  PUBLISHED_STATUS_ID       VARCHAR(255) DEFAULT NULL,
  FULL_RESOLUTION_FILE_PATH VARCHAR(255) DEFAULT NULL,
  LARGE_THUMBNAIL_FILE_PATH VARCHAR(255) DEFAULT NULL,
  PUBLISHED_STATUS          VARCHAR(255) DEFAULT NULL,
  QC_STATUS_ID              INT(11)      DEFAULT NULL,
  QC_STATUS                 VARCHAR(255) DEFAULT NULL,
  KEY ID (ID),
  KEY DCF_ID (DCF_ID),
  KEY MOUSE_ID (MOUSE_ID),
  KEY EXPERIMENT_ID (EXPERIMENT_ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS IMA_EXPERIMENT_DICT;
CREATE TABLE IMA_EXPERIMENT_DICT (
  ID          INT(11) NOT NULL,
  NAME        VARCHAR(128)  DEFAULT NULL,
  DESCRIPTION VARCHAR(4000) DEFAULT NULL,
  ORDER_BY    INT(11)       DEFAULT NULL,
  ACTIVE      TINYINT(4)    DEFAULT NULL,
  PRIMARY KEY (ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS IMA_IMAGE_RECORD;
CREATE TABLE IMA_IMAGE_RECORD (
  ID                        INT(11) NOT NULL,
  FOREIGN_TABLE_NAME        VARCHAR(256)     DEFAULT NULL,
  FOREIGN_KEY_ID            INT(11)          DEFAULT NULL,
  ORIGINAL_FILE_NAME        VARCHAR(1024)    DEFAULT NULL,
  CREATOR_ID                INT(11)          DEFAULT NULL,
  CREATED_DATE              DATE             DEFAULT NULL,
  EDITED_BY                 VARCHAR(64)      DEFAULT NULL,
  EDIT_DATE                 DATE             DEFAULT NULL,
  CHECK_NUMBER              INT(11)          DEFAULT NULL,
  FULL_RESOLUTION_FILE_PATH VARCHAR(256)     DEFAULT NULL,
  SMALL_THUMBNAIL_FILE_PATH VARCHAR(256)     DEFAULT NULL,
  LARGE_THUMBNAIL_FILE_PATH VARCHAR(256)     DEFAULT NULL,
  DOWNLOAD_FILE_PATH        VARCHAR(256)     DEFAULT NULL,
  SUBCONTEXT_ID             INT(11)          DEFAULT NULL,
  QC_STATUS_ID              INT(11)          DEFAULT NULL,
  PUBLISHED_STATUS_ID       INT(11)          DEFAULT NULL,
  organisation              INT(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (ID),
  KEY FOREIGN_KEY_ID (FOREIGN_KEY_ID),
  KEY SUBCONTEXT_ID (SUBCONTEXT_ID),
  KEY FOREIGN_TABLE_NAME (FOREIGN_TABLE_NAME),
  KEY organisation (organisation)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS IMA_IMAGE_TAG;
CREATE TABLE IMA_IMAGE_TAG (
  ID              INT(11)       DEFAULT NULL,
  IMAGE_RECORD_ID INT(11)       DEFAULT NULL,
  TAG_TYPE_ID     INT(11)       DEFAULT NULL,
  TAG_NAME        VARCHAR(256)  DEFAULT NULL,
  TAG_VALUE       VARCHAR(4000) DEFAULT NULL,
  CREATOR_ID      INT(11)       DEFAULT NULL,
  CREATED_DATE    DATE          DEFAULT NULL,
  EDITED_BY       VARCHAR(64)   DEFAULT NULL,
  EDIT_DATE       DATE          DEFAULT NULL,
  CHECK_NUMBER    INT(11)       DEFAULT NULL,
  X_START         FLOAT         DEFAULT NULL,
  Y_START         FLOAT         DEFAULT NULL,
  X_END           FLOAT         DEFAULT NULL,
  Y_END           FLOAT         DEFAULT NULL,
  KEY IMAGE_RECORD_ID (IMAGE_RECORD_ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS IMA_IMAGE_TAG_TYPE;
CREATE TABLE IMA_IMAGE_TAG_TYPE (
  NAME VARCHAR(128) DEFAULT NULL,
  ID   INT(11) NOT NULL,
  PRIMARY KEY (ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS IMA_IMPORT_CONTEXT;
CREATE TABLE IMA_IMPORT_CONTEXT (
  ID   INT(11) NOT NULL,
  NAME VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS IMA_IMPORT_LOG;
CREATE TABLE IMA_IMPORT_LOG (
  LOG_ID        VARCHAR(4000) DEFAULT NULL,
  LOG_MESSAGE   VARCHAR(4000) DEFAULT NULL,
  LOG_TIMESTAMP TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  LOG_STATUS    VARCHAR(128) DEFAULT NULL,
  LOG_URL       VARCHAR(4000) DEFAULT NULL,
  ID            INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS IMA_PREDEFINED_TAG;
CREATE TABLE IMA_PREDEFINED_TAG (
  ID                 INT(11)      DEFAULT NULL,
  TAG_TYPE_ID        INT(11)      DEFAULT NULL,
  TAG_NAME           VARCHAR(256) DEFAULT NULL,
  EXPERIMENT_DICT_ID INT(11)      DEFAULT NULL,
  ORDER_BY           INT(11)      DEFAULT NULL,
  ALLOW_MULTIPLE     TINYINT(4)   DEFAULT '0',
  ALLOW_IN_ROI       TINYINT(4)   DEFAULT NULL
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS IMA_PREDEFINED_TAG_VALUE;
CREATE TABLE IMA_PREDEFINED_TAG_VALUE (
  ID                INT(11)       DEFAULT NULL,
  PREDEFINED_TAG_ID INT(11)       DEFAULT NULL,
  TAG_VALUE         VARCHAR(4000) DEFAULT NULL,
  ORDER_BY          INT(11)       DEFAULT NULL
) ENGINE = MyISAM DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS IMA_PUBLISHED_DICT;
CREATE TABLE IMA_PUBLISHED_DICT (
  ID          INT(11) NOT NULL,
  NAME        VARCHAR(512) DEFAULT NULL,
  DESCRIPTION VARCHAR(512) DEFAULT NULL,
  ORDER_BY    INT(11)      DEFAULT NULL,
  ACTIVE      TINYINT(4)   DEFAULT NULL,
  PRIMARY KEY (ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS IMA_QC_DICT;
CREATE TABLE IMA_QC_DICT (
  ID          INT(11) NOT NULL,
  NAME        VARCHAR(512) DEFAULT NULL,
  DESCRIPTION VARCHAR(512) DEFAULT NULL,
  ORDER_BY    INT(11)      DEFAULT NULL,
  ACTIVE      TINYINT(4)   DEFAULT NULL,
  PRIMARY KEY (ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS IMA_SUBCONTEXT;
CREATE TABLE IMA_SUBCONTEXT (
  ID                 INT(11) NOT NULL,
  IMPORT_CONTEXT_ID  INT(11)    DEFAULT NULL,
  EXPERIMENT_DICT_ID INT(11)    DEFAULT NULL,
  IS_DEFAULT         TINYINT(4) DEFAULT '0',
  PRIMARY KEY (ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS MTS_GENOTYPE_DICT;
CREATE TABLE MTS_GENOTYPE_DICT (
  ID          INT(11)     NOT NULL,
  NAME        VARCHAR(50) NOT NULL,
  ORDER_BY    INT(11)     NOT NULL,
  DESCRIPTION VARCHAR(25) NOT NULL,
  ACTIVE      INT(11)     NOT NULL
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS MTS_MOUSE_ALLELE;
CREATE TABLE MTS_MOUSE_ALLELE (
  ID               INT(11) NOT NULL,
  MOUSE_ID         INT(11) NOT NULL,
  ALLELE_ID        INT(11) NOT NULL,
  GENOTYPE_DICT_ID INT(11) NOT NULL,
  PRIMARY KEY (ID),
  KEY MOUSE_ID (MOUSE_ID, ALLELE_ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS PHN_STD_OPERATING_PROCEDURE;
CREATE TABLE PHN_STD_OPERATING_PROCEDURE (
  ID                 INT(11)      NOT NULL,
  PROCEDURE_ID       INT(11)      NOT NULL,
  NAME               VARCHAR(256) NOT NULL,
  ACTIVE             INT(11)      NOT NULL,
  CREATED_DATE       DATE         DEFAULT NULL,
  CREATOR_ID         INT(11)      DEFAULT NULL,
  EDIT_DATE          DATE         DEFAULT NULL,
  EDITED_BY          VARCHAR(256) DEFAULT NULL,
  CHECK_INT          INT(11)      DEFAULT NULL,
  TERMINAL_PROCEDURE INT(10)      DEFAULT NULL,
  IS_HIDDEN          INT(1)       DEFAULT NULL,
  VIEW_NAME          VARCHAR(30)  DEFAULT NULL,
  PRIMARY KEY (ID),
  KEY PROCEDURE_ID (PROCEDURE_ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS ima_image_record_annotation_vw;
CREATE TABLE ima_image_record_annotation_vw (
  IMAGE_RECORD_ID INT(11)      DEFAULT NULL,
  TERM_ID         VARCHAR(128) DEFAULT NULL,
  TERM_NAME       VARCHAR(256) DEFAULT NULL
) ENGINE = MyISAM DEFAULT CHARSET = utf8;


DROP TABLE IF EXISTS mts_mouse_allele_mv;
CREATE TABLE mts_mouse_allele_mv (
  MOUSE_ID INT(11)      NOT NULL,
  ALLELE   VARCHAR(250) NOT NULL,
  PRIMARY KEY (MOUSE_ID)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;



/**
 * External resources / database to populate
 */
insert into external_db(id, name, short_name, version, version_date) values(1, 'Mouse Genome Assembly', 'NCBI m38', 'GRCm38', '2012-01-09');
insert into external_db(id, name, short_name, version, version_date) values(2, 'MGI Genome Feature Type', 'Genome Feature Type', 'JAX', '2011-12-22');
insert into external_db(id, name, short_name, version, version_date) values(3, 'Mouse Genome Informatics', 'MGI', 'JAX', '2011-12-22');
insert into external_db(id, name, short_name, version, version_date) values(4, 'murine X/Y pseudoautosomal region', 'X/Y', 'unknown', '2012-01-06');
insert into external_db(id, name, short_name, version, version_date) values(5, 'Mammalian Phenotype', 'MP', 'JAX', '2012-01-09');
insert into external_db(id, name, short_name, version, version_date) values(6, 'IMPReSS', 'IMPReSS', 'unknown', '2012-01-26');
insert into external_db(id, name, short_name, version, version_date) values(7, 'Phenotypic qualities (properties)', 'PATO', 'unknown', '2011-11-23');
insert into external_db(id, name, short_name, version, version_date) values(8, 'Mouse adult gross anatomy', 'MA', 'unknown', '2011-07-22');
insert into external_db(id, name, short_name, version, version_date) values(9, 'Chemical entities of biological interest', 'CHEBI', '87', '2012-01-10');
insert into external_db(id, name, short_name, version, version_date) values(10, 'Environment Ontology', 'EnvO', 'unknown', '2011-03-24');
insert into external_db(id, name, short_name, version, version_date) values(11, 'Gene Ontology', 'GO', '1.1.2551', '2012-01-26');
insert into external_db(id, name, short_name, version, version_date) values(12, 'EuroPhenome', 'EuroPhenome', 'February 2012', '2012-04-26');
insert into external_db(id, name, short_name, version, version_date) values(13, 'Evidence codes', 'ECO', 'July 2012', '2012-07-30');
insert into external_db(id, name, short_name, version, version_date) values(14, 'Mouse gross anatomy and development', 'EMAP', 'July 2012', '2012-07-30');
insert into external_db(id, name, short_name, version, version_date) values(15, 'Experimental Factor Ontology', 'EFO', 'July 2012', '2012-07-31');
insert into external_db(id, name, short_name, version, version_date) values(16, 'International Mouse Strain Resource', 'IMSR', 'August 2012', '2012-08-13');
insert into external_db(id, name, short_name, version, version_date) values(17, 'Vertebrate Genome Annotation', 'VEGA', '50', '2012-12-19');
insert into external_db(id, name, short_name, version, version_date) values(18, 'Ensembl', 'Ensembl', '70', '2013-01-17');
insert into external_db(id, name, short_name, version, version_date) values(19, 'NCBI EntrezGene', 'EntrezGene', 'unknown', '2013-02-19');
insert into external_db(id, name, short_name, version, version_date) values(20, 'WTSI Mouse Genetics Project', 'MGP', 'unknown', '2013-02-19');
insert into external_db(id, name, short_name, version, version_date) values(21, 'Consensus CDS', 'cCDS', '2012-08-14', '2012-08-14');
insert into external_db(id, name, short_name, version, version_date) values(22, 'International Mouse Phenotyping Consortium', 'IMPC', '2010-11-15', '2010-11-15');
insert into external_db(id, name, short_name, version, version_date) values(23, 'Immunology', '3i', '2014-11-07', '2014-11-07');
insert into external_db(id, name, short_name, version, version_date) values(24, 'Mouse pathology', 'MPATH', '2013-08-12', '2013-08-12');
insert into external_db(id, name, short_name, version, version_date) values(25, 'Mouse gross anatomy and development, timed', 'EMAPA', '2016-05-13', '2016-05-13');



/**
Different Mouse Mutant related projects
*/
INSERT INTO project(id, name, fullname, description) VALUES(1, 'EUMODIC', 'European Mouse Disease Clinic', 'Consortium of 18 research institutes in 8 European countries who are experts in the field of mouse functional genomics and phenotyping');
INSERT INTO project(id, name, fullname, description) VALUES(2, 'KOMP', 'NIH Knockout Mouse Project', "Trans-NIH initiative that aims to generate a comprehensive and public resource comprised of mouse embryonic stem (ES) cells containing a null mutation in every gene in the mouse genome");
INSERT INTO project(id, name, fullname, description) VALUES(3, 'KOMP2', 'Knockout Mouse Phenotyping Program', "The Common Fund's Knockout Mouse Phenotyping Program (KOMP2) provides broad, standardized phenotyping of a genome-wide collection of mouse knockouts generated by the International Knockout Mouse Consortium (IKMC), funded by the NIH, European Union, Wellcome Trust, Canada, and the Texas Enterprise Fund");
INSERT INTO project(id, name, fullname, description) VALUES(4, 'IMPC', 'International Mouse Phenotyping Consortium', 'Group of major mouse genetics research institutions along with national funding organisations formed to address the challenge of developing an encyclopedia of mammalian gene function');
INSERT INTO project(id, name, fullname, description) VALUES(5, 'IKMC', 'International Knock-out Mouse Consortium', 'Mutate all protein-coding genes in the mouse using a combination of gene trapping and gene targeting in C57BL/6 mouse embryonic stem (ES) cells');
INSERT INTO project(id, name, fullname, description) VALUES(6, 'EUCOMM', 'European Conditional Mouse Mutagenesis Program', 'Generation, archiving, and world-wide distribution of up to 12.000 conditional mutations across the mouse genome in mouse embryonic stem (ES) cells. Establishment of a limited number of mouse mutants from this resource');
INSERT INTO project(id, name, fullname, description) VALUES(7, 'NorCOMM', 'North American Conditional Mouse Mutagenesis Project', 'Large-scale research initiative focused on developing and distributing a library of mouse embryonic stem (ES) cell lines carrying single gene trapped or targeted mutations across the mouse genome');
INSERT INTO project(id, name, fullname, description) VALUES(8, 'MGP', 'Wellcome Trust Sanger Institute Mouse Genetics Project', 'MGP Phenotyping pipeline (funded by WTSI & EUMODIC)');
INSERT INTO project(id, name, fullname, description) VALUES(9, 'GMC', 'HMGU GMC pipeline', 'German Mouse Clinic Phenotype Pipeline');
INSERT INTO project(id, name, fullname, description) VALUES(10, 'BaSH', 'KOMP2 BaSH consortium', 'The BaSH consortium is a cooperative effort between Baylor College of Medicine, the Wellcome Trust Sanger Institute (WTSI) and MRC Harwell Mary Lyon Centre. The BaSH consortium is a member of the International Mouse Phenotyping Consortium (IMPC) where the status of KOMP alleles may be found. Interest may also be registered for specific KOMP alleles.');
INSERT INTO project(id, name, fullname, description) VALUES(11, 'NorCOMM2', 'North American Conditional Mouse Mutagenesis project 2', 'In NorCOMM2, our Genome Canada-funded project has partnered with MRC Harwell to use the IKMC mouse ES cell resource to generate and phenotype >1,000 mutant mouse lines');
INSERT INTO project(id, name, fullname, description) VALUES(12, 'DTCC', 'DTCC consortium', 'University of California, Davis (UC Davis), Toronto Centre for Phenogenomics (TCP), Children''s Hospital Oakland Research Institute (CHORI) and Charles River have come together to form the DTCC Consortium for this 5-year research effort.');
INSERT INTO project(id, name, fullname, description) VALUES(13, 'JAX', 'Jackson Laboratory', 'JAXs objective is to phenotype 833 of the KOMP2 strains. To do this, it is using a powerful, efficient, high-throughput phenotyping pipeline that integrates recommendations from the IMPC and includes disease-relevant phenotypes recommended by a panel of internal and external KOMP2 domain experts.');
INSERT INTO project(id, name, fullname, description) VALUES(14, 'Phenomin', 'Phenomin', 'PHENOMIN will participate to the International Mouse Phenotyping Consortium (IMPC) and, owing to its tight connection with the Institut de Génétique et de Biologie Moléculaire et Cellulaire (IGBMC) at Illkirch and the Centre d''Immunologie de Marseille-Luminy, will be able to provide a major contribution to the goals of the IMPC.');
INSERT INTO project(id, name, fullname, description) VALUES(15, 'Helmholtz GMC', 'Helmholtz German Mouse Clinic', "Characterisation of mouse models for human diseases to understand molecular mechanisms of human disorders and for the development of new therapies");
INSERT INTO project(id, name, fullname, description) VALUES(16, 'MRC', 'MRC project', '-');
INSERT INTO project(id, name, fullname, description) VALUES(17, 'MARC', 'Model Animal Research Center', 'Nanjing University - Model Animal Research Center');
INSERT INTO project(id, name, fullname, description) VALUES(18, 'RIKEN BRC', 'RIKEN BioResource Center', 'RIKEN BioResource Center - Japan');


/**
 * Some project names are inherited from
 */
/**
Organizations participating to the projects
 */
INSERT INTO organisation(id, name, fullname, country) VALUES(1, 'CHORI', "Children's Hospital Oakland Research Institute", 'USA');
INSERT INTO organisation(id, name, fullname, country) VALUES(2, 'UC Davis', 'University of California at Davis School of Veterinary Medicine', 'USA');
INSERT INTO organisation(id, name, fullname, country) VALUES(3, 'WTSI', 'Wellcome Trust Sanger Institute', 'UK');
INSERT INTO organisation(id, name, fullname, country) VALUES(4, 'Regeneron', 'Regeneron Pharmaceuticals, Inc.', 'USA');
INSERT INTO organisation(id, name, fullname, country) VALUES(6, 'JAX', 'Jackson Laboratory', 'USA');
INSERT INTO organisation(id, name, fullname, country) VALUES(7, 'MRC Harwell', 'Medical Research Council centre for mouse genetics', 'UK');
INSERT INTO organisation(id, name, fullname, country) VALUES(8, 'ICS', 'Institut Clinique de la Souris', 'France');
INSERT INTO organisation(id, name, fullname, country) VALUES(9, 'HMGU', 'Helmholtz Zentrum München Deutsches Forschungszentrum für Gesundheit und Umwelt', 'Germany');
INSERT INTO organisation(id, name, fullname, country) VALUES(10, 'HZI', 'Das Helmholtz Zentrum für Infektionsforschung', 'Germany');
INSERT INTO organisation(id, name, fullname, country) VALUES(11, 'CNR', 'National Research Council', 'Italy');
INSERT INTO organisation(id, name, fullname, country) VALUES(12, 'U.Manchester', 'University of Manchester', 'UK');
INSERT INTO organisation(id, name, fullname, country) VALUES(13, 'EMBL Monterotondo', 'European Molecular Biology Laboratory Monterotondo', 'Italy');
INSERT INTO organisation(id, name, fullname, country) VALUES(14, 'CNIO', 'Centro Nacional de Investigaciones Oncológicas', 'Spain');
INSERT INTO organisation(id, name, fullname, country) VALUES(15, 'AniRA', 'Technical facilities within the UMS3444 Biosciences Gerland-Lyon Sud', 'France');
INSERT INTO organisation(id, name, fullname, country) VALUES(16, 'TAU', 'Tel Aviv University', 'Israel');
INSERT INTO organisation(id, name, fullname, country) VALUES(17, 'UAB', 'Autonomous University of Barcelona', 'Spain');
INSERT INTO organisation(id, name, fullname, country) VALUES(18, 'CIG', 'Center for Integrative Genomics', 'Switzerland');
INSERT INTO organisation(id, name, fullname, country) VALUES(19, 'Transgenose CNRS', 'Institut De Transgenose CNRS Orléans', 'France');
INSERT INTO organisation(id, name, fullname, country) VALUES(20, 'U.Cambridge', 'University of Cambridge', 'UK');
INSERT INTO organisation(id, name, fullname, country) VALUES(21, 'TIGEM', 'Telethon Institute of Genetics and Medicine', 'Italy');
INSERT INTO organisation(id, name, fullname, country) VALUES(22, 'Fleming', 'Research Centre "Alexander Fleming"', 'Greece');
INSERT INTO organisation(id, name, fullname, country) VALUES(23, 'CMHD', 'Centre for Modeling Human Disease', 'Canada');
INSERT INTO organisation(id, name, fullname, country) VALUES(24, 'CIPHE', "Centre d'ImmunoPhenomique", 'France');
INSERT INTO organisation(id, name, fullname, country) VALUES(25, 'BCM', 'Baylor College of Medicine', 'USA');
INSERT INTO organisation(id, name, fullname, country) VALUES(26, 'RBRC', 'RIKEN BioResource Center', 'Japan');
INSERT INTO organisation(id, name, fullname, country) VALUES(27, 'TCP', 'The Centre for Phenogenomics', 'Canada');
INSERT INTO organisation(id, name, fullname, country) VALUES(28, 'NING', 'Nanjing University Model Animal Research Center', 'China');
INSERT INTO organisation(id, name, fullname, country) VALUES(29, 'CDTA', 'Institut de Transgenose (CDTA Orleans)', 'France');
INSERT INTO organisation(id, name, fullname, country) VALUES(30, 'IMG', 'Institute of Molecular Genetics of the ASCR, v. v. i.', 'Czech Republic');


/**
Link organisations to projects
*/

/**
List the participants to EUMODIC
 */
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'MRC Harwell';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'ICS';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'Helmholtz Zentrum München';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'HZI';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'CNR';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'U.Manchester';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'EMBL Monterotondo';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'CNIO';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'AniRA';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'TAU';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'UAB';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'CIG';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'Transgenose CNRS';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'U.Cambridge';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'TIGEM';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'Fleming';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'EUMODIC' AND o.name = 'WTSI';

-- bash participants
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'BaSH' AND o.name = 'MRC Harwell';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'BaSH' AND o.name = 'WTSI';
INSERT INTO participant(project_id, organisation_id, role)
  SELECT p.id, o.id, 'part_of'
  FROM project p, organisation o WHERE p.name = 'BaSH' AND o.name = 'BCM';

  /**
   * MGI allele types
   */
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000001', 3, 'Chemically and radiation induced', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000002', 3, 'Chemically induced (ENU)', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000003', 3, 'Chemically induced (other)', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000004', 3, 'Gene trapped', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000005', 3, 'Not Applicable', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000006', 3, 'Not Specified', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000007', 3, 'QTL', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000008', 3, 'Radiation induced', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000009', 3, 'Spontaneous', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000010', 3, 'Targeted (Floxed/Frt)', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000011', 3, 'Targeted (Reporter)', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000012', 3, 'Targeted (knock-in)', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000013', 3, 'Targeted (knock-out)', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000014', 3, 'Targeted (other)', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000015', 3, 'Transgenic (Cre/Flp)', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000016', 3, 'Transgenic (Reporter)', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000017', 3, 'Transgenic (Transposase)', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000018', 3, 'Transgenic (random, expressed)', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000019', 3, 'Transgenic (random, gene disruption)', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000020', 3, 'Transposon induced', '');

  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:000000101', 3, 'Targeted', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:000000102', 3, 'Transgenic', '');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:000000103', 3, 'Endonuclease-mediated', '');


  /**
   * MGI strain classification
   */

  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000021', 3, 'coisogenic', 'coisogenic strain');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000022', 3, 'congenic', 'congenic strain');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000023', 3, 'conplastic', 'conplastic strain');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000024', 3, 'consomic', 'consomic strain');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000025', 3, 'inbred strain', 'inbred strain');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000026', 3, 'recombinant congenic (RC)', 'recombinant congenic (RC)');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000027', 3, 'recombinant inbred (RI)', 'recombinant inbred (RI)');
  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000028', 3, 'mutant strain', 'mutant strain as defined in IMSR');

  INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000050', 3, 'EuroPhenome mutant strain', 'mutant strain as defined in EuroPhenome');
  INSERT INTO ontology_term (acc, db_id, name, description)
  VALUES ('CV:00000051', 3, 'IMPC uncharacterized background strain', 'background strain used in IMPC');

