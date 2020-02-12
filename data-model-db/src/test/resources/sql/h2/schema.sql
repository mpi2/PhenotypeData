/**
 * Copyright © 2017 EMBL - European Bioinformatics Institute
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


/**
 * Contains information about the loading of specimen files
 */
DROP TABLE IF EXISTS analytics_specimen_load;
CREATE TABLE analytics_specimen_load (
	id                         INT(10)      NOT NULL AUTO_INCREMENT,
	filename                   VARCHAR(255) NOT NULL DEFAULT '',
	colony_id                  VARCHAR(255) NOT NULL DEFAULT '',
	dob                        VARCHAR(50)  NOT NULL DEFAULT '',
	baseline                   BOOLEAN,
	strain                     VARCHAR(255) NOT NULL DEFAULT '',
	specimen_id                VARCHAR(255) NOT NULL DEFAULT '',
	sex                        VARCHAR(10)  NOT NULL DEFAULT '',
	zygosity                   VARCHAR(20)  NOT NULL DEFAULT '',
	litter_id                  VARCHAR(255) NOT NULL DEFAULT '',
	impress_pipeline           VARCHAR(50)  NOT NULL DEFAULT '',
	production_center          VARCHAR(100) NOT NULL DEFAULT '',
	phenotyping_center         VARCHAR(100) NOT NULL DEFAULT '',
	project                    VARCHAR(100) NOT NULL DEFAULT '',
	mapped_project             VARCHAR(100) NOT NULL DEFAULT '',
	status                     VARCHAR(100) NOT NULL DEFAULT '',
	message                    TEXT,
	additional_information     TEXT,

	PRIMARY KEY (id)

);


/**
 * Contains information about the loading of experiment files
 */
DROP TABLE IF EXISTS analytics_experiment_load;
CREATE TABLE analytics_experiment_load (
	id                       INT(10)      NOT NULL AUTO_INCREMENT,
	filename                 VARCHAR(255) NOT NULL DEFAULT '',
	center_id                VARCHAR(255) NOT NULL DEFAULT '',
	date_of_experiment       VARCHAR(50)      NULL,
	sequence_id              VARCHAR(255)     NULL,
	experiment_id            VARCHAR(255) NOT NULL DEFAULT '',
	specimen_id              VARCHAR(255) NULL,
	impress_pipeline         VARCHAR(50)  NOT NULL DEFAULT '',
	impress_procedure        VARCHAR(100) NOT NULL DEFAULT '',
	impress_parameters       TEXT,
	parameter_types          VARCHAR(255) NOT NULL DEFAULT '',
	data_values              INT(11)      NOT NULL DEFAULT 0,
	metadata_values          INT(11)      NOT NULL DEFAULT 0,
	missing_data_values      INT(11)      NOT NULL DEFAULT 0,
	status                   VARCHAR(100) NOT NULL DEFAULT '',
	message                  TEXT,
	additional_information   TEXT,

	PRIMARY KEY (id)

);


/**
 * Contains information about the loading of experiment files
 */
DROP TABLE IF EXISTS analytics_statistics_load;
CREATE TABLE analytics_statistics_load (
	id                       INT(10)      NOT NULL AUTO_INCREMENT,
	observation_type         VARCHAR(255) NOT NULL DEFAULT '',
	zygosity                 VARCHAR(255) NOT NULL DEFAULT '',
	center_id                VARCHAR(255) NOT NULL DEFAULT '',
	allele_accession_id      VARCHAR(255) NULL,
	impress_pipeline         VARCHAR(50)  NOT NULL DEFAULT '',
	impress_procedure        VARCHAR(100) NOT NULL DEFAULT '',
	impress_parameter        TEXT,
	male_controls            INT(10),
	male_mutants             INT(10),
	female_controls          INT(10),
	female_mutants           INT(10),
	status_date              DATETIME,
	status                   VARCHAR(100)  NOT NULL DEFAULT '',

	PRIMARY KEY (id)

);


/**
 * Contains meta information about the database like
 * the version of the code that can run safely on the data
 * the mouse assembly version of the data
 * the different phenodeviant calls made for this version
 * the version of the database schema
 */
DROP TABLE IF EXISTS meta_info;
CREATE TABLE meta_info (
	id                       INT(10)       NOT NULL AUTO_INCREMENT,
	property_key             VARCHAR(255)  NOT NULL DEFAULT '',
	property_value           VARCHAR(2000) NOT NULL DEFAULT '',
	description              TEXT,

	PRIMARY KEY (id),
	UNIQUE (property_key)

);


/**
 * Contains meta information from release to release to keep track of
 * Numbers through time
 */
DROP TABLE IF EXISTS meta_history;
CREATE TABLE meta_history (
	id                       INT(10) NOT NULL AUTO_INCREMENT,
	property_key             VARCHAR(255) NOT NULL DEFAULT '',
	property_value           VARCHAR(2000) NOT NULL DEFAULT '',
	data_release_version     VARCHAR(10) NOT NULL,

	PRIMARY KEY (id)

);


DROP TABLE IF EXISTS analytics_mp_calls;
CREATE TABLE analytics_mp_calls (
	id                       INT(10) NOT NULL AUTO_INCREMENT,
	phenotyping_center       VARCHAR(255) NOT NULL DEFAULT '',
	marker_symbol            VARCHAR(255) NOT NULL DEFAULT '',
	marker_accession_id      VARCHAR(255) NOT NULL DEFAULT '',
	colony_id                VARCHAR(255) NOT NULL DEFAULT '',
	mp_term_id               VARCHAR(255) NOT NULL DEFAULT '',
	mp_term_name             VARCHAR(255) NOT NULL DEFAULT '',
-- 	mp_term_level            ENUM('top', 'intermediate', 'leaf'),
	mp_term_level            VARCHAR(40),
	    CHECK (mp_term_level in ('top', 'intermediate', 'leaf')),

	PRIMARY KEY (id)

);


/**
@table project
@desc This table stores information about each phenotyping project
We made this table generic enough to store legacy project data
*/
DROP TABLE IF EXISTS project;
CREATE TABLE project (
	id                       INT(10) NOT NULL,
	name                     VARCHAR(255) NOT NULL DEFAULT '',
	fullname                 VARCHAR(255) NOT NULL DEFAULT '',
	description              TEXT,

	PRIMARY KEY (id),
	UNIQUE (name)

);


DROP TABLE IF EXISTS organisation;
CREATE TABLE organisation (
	id                       INT(10) NOT NULL AUTO_INCREMENT,
	name                     VARCHAR(255) NOT NULL DEFAULT '',
	fullname                 VARCHAR(255) NOT NULL DEFAULT '',
	country                  VARCHAR(50),

	PRIMARY KEY (id),
	UNIQUE (name)

);


DROP TABLE IF EXISTS participant;
CREATE TABLE participant (

	project_id               INT(10) NOT NULL,
	organisation_id          INT(10) NOT NULL,
	role                     VARCHAR(255) NOT NULL DEFAULT ''

);


/**
 * This table holds all the external datasources (MGI, Ensembl)
 */
-- this table holds all the external/ASTD database names
DROP TABLE IF EXISTS external_db;
CREATE TABLE external_db (
	id                       INT(10) NOT NULL,
	name                     VARCHAR(100) NOT NULL,
	short_name               VARCHAR(40) NOT NULL,
	version                  VARCHAR(15) NOT NULL DEFAULT '',
	version_date             DATE not NULL,

	PRIMARY KEY (id),
	UNIQUE  (name, version, version_date)

);


/**
 * This table will store the ontological terms we need for controlled vocabulary
 */
DROP TABLE IF EXISTS ontology_term;
CREATE TABLE ontology_term (
	acc                        VARCHAR(30) NOT NULL,
	db_id                      INT(10) NOT NULL,
	name                       TEXT NOT NULL,
	description                TEXT,
	is_obsolete                TINYINT(1) DEFAULT 0,
	replacement_acc            VARCHAR(20) DEFAULT NULL,
	PRIMARY KEY (acc, db_id)

);


DROP TABLE IF EXISTS ontology_relationship;
CREATE TABLE ontology_relationship (
	a_acc                      VARCHAR(20) NOT NULL,
	a_db_id                    INT(10) NOT NULL,
	b_acc                      VARCHAR(20) NOT NULL,
	b_db_id                    INT(10) NOT NULL,
	relationship               VARCHAR(30) NOT NULL

);


/**
 * The sequence and genomic information goes there.
 * We picked up some of the table from Ensembl to build our sequence information
 * At the moment, we don't plan to have multiple coordinate system for the same
 * genome.
 */
DROP TABLE IF EXISTS coord_system;
CREATE TABLE coord_system (
	id                         INT(10) NOT NULL AUTO_INCREMENT,
	name                       VARCHAR(40) NOT NULL,
	strain_acc                 VARCHAR(20) DEFAULT NULL,
	strain_db_id               INT(10) DEFAULT NULL,
	db_id                      INT(10) NOT NULL,

	PRIMARY KEY (id),
	UNIQUE (db_id, strain_db_id)

);


DROP TABLE IF EXISTS seq_region;
CREATE TABLE seq_region (
	id                         INT(10) NOT NULL AUTO_INCREMENT,
	name                       VARCHAR(40) NOT NULL,
	coord_system_id            INT(10) NOT NULL,
	length                     INT(10) NOT NULL,

	PRIMARY KEY (id),
	UNIQUE (name, coord_system_id)

);


/**
 * Genomic feature table
 * Contains any genomic site, whether functional or not
 * that can be mapped through formal genetic analysis
 */
DROP TABLE IF EXISTS genomic_feature;
CREATE TABLE genomic_feature (
	acc                        VARCHAR(30) NOT NULL,
	db_id                      INT(10) NOT NULL,
	symbol                     VARCHAR(100) NOT NULL,
	name                       VARCHAR(200) NOT NULL,
	biotype_acc                VARCHAR(20) NOT NULL,
	biotype_db_id              INT(10) NOT NULL,
	subtype_acc                VARCHAR(20),
	subtype_db_id              INT(10),
	seq_region_id              INT(10),
	seq_region_start           INT(10) DEFAULT 0,
	seq_region_end             INT(10) DEFAULT 0,
	seq_region_strand          TINYINT(2) DEFAULT 0,
	cm_position                VARCHAR(40),
-- 	status                     ENUM('active', 'withdrawn') NOT NULL DEFAULT 'active',
	status                     VARCHAR(24) NOT NULL DEFAULT 'active',
	    CHECK (status in ('active', 'withdrawn')),

		PRIMARY   KEY (acc, db_id)

);


DROP TABLE IF EXISTS synonym;
CREATE TABLE synonym (
	id                         INT(10) NOT NULL AUTO_INCREMENT,
	acc                        VARCHAR(30) NOT NULL,
	db_id                      INT(10) NOT NULL,
	symbol                     VARCHAR(8192) NOT NULL,

	PRIMARY KEY(id)

);


/**
 * Genomic feature cross-reference from other datasources.
 */
DROP TABLE IF EXISTS xref;
CREATE TABLE xref (
	id                         INT(10) NOT NULL AUTO_INCREMENT,
	acc                        VARCHAR(30) NOT NULL,
	db_id                      INT(10) NOT NULL,
	xref_acc                   VARCHAR(20) NOT NULL,
	xref_db_id                 INT(10) NOT NULL,

	PRIMARY KEY (id)

);


/**
 * Allele table
 * Contains sequence variant of a gene recognized by a DNA assay (polymorphic)
 * or a variant phenotype (mutant)
 */
DROP TABLE IF EXISTS allele;
CREATE TABLE allele (
	acc                       VARCHAR(30) NOT NULL,
	db_id                     INT(10) NOT NULL,
	gf_acc                    VARCHAR(20),
	gf_db_id                  INT(10),
	biotype_acc               VARCHAR(20)  NOT NULL,
	biotype_db_id             INT(10)      NOT NULL,
	symbol                    VARCHAR(100) NOT NULL,
	name                      VARCHAR(200) NOT NULL,

	PRIMARY KEY (acc, db_id)

);


DROP TABLE IF EXISTS strain;
CREATE TABLE strain (
	acc                       VARCHAR(30) NOT NULL,   -- There really is a strain 'CSD1918305-1b-(EUCOMM)Wtsi'.
	db_id                     INT(10) NOT NULL,
	biotype_acc               VARCHAR(20),
	biotype_db_id             INT(10),
	name                      VARCHAR(200) NOT NULL,

	PRIMARY KEY (acc, db_id),
	UNIQUE (name),
	UNIQUE (acc)

);

DROP TABLE IF EXISTS biological_model;
CREATE TABLE biological_model (
	id                        INT(10) NOT NULL AUTO_INCREMENT,
	db_id                     INT(10) NOT NULL,
	allelic_composition       VARCHAR(300) NOT NULL,
	genetic_background        VARCHAR(300) NOT NULL,
-- 	zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable') DEFAULT NULL,
	zygosity                  VARCHAR(24) DEFAULT NULL,
	    CHECK (zygosity IN ('homozygote', 'heterozygote', 'hemizygote', 'not_applicable')),

		PRIMARY KEY (id),
	UNIQUE (db_id, allelic_composition, genetic_background, zygosity)

);


DROP TABLE IF EXISTS biological_model_allele;
CREATE TABLE biological_model_allele (
	biological_model_id       INT(10) NOT NULL,
	allele_acc                VARCHAR(20) NOT NULL,
	allele_db_id              INT(10) NOT NULL,

	UNIQUE (biological_model_id, allele_acc)

);


DROP TABLE IF EXISTS biological_model_strain;
CREATE TABLE biological_model_strain (
	biological_model_id       INT(10) NOT NULL,
	strain_acc                VARCHAR(20) NOT NULL,
	strain_db_id              INT(10) NOT NULL,

	UNIQUE (biological_model_id, strain_acc)

);


/**
 * This table is an association table between the
 * allele table, the phenotype information and biological model
 */
DROP TABLE IF EXISTS biological_model_phenotype;
CREATE TABLE biological_model_phenotype (
	biological_model_id       INT(10) NOT NULL,
	phenotype_acc             VARCHAR(20) NOT NULL,
	phenotype_db_id           INT(10) NOT NULL,

	UNIQUE (biological_model_id, phenotype_acc)

);


DROP TABLE IF EXISTS biological_model_genomic_feature;
CREATE TABLE biological_model_genomic_feature (
	biological_model_id       INT(10) NOT NULL,
	gf_acc                    VARCHAR(20) NOT NULL,
	gf_db_id                  INT(10) NOT NULL,

	UNIQUE (biological_model_id, gf_acc)

);


/**
 * Links a sample to a biological model
 */
DROP TABLE IF EXISTS biological_model_sample;
CREATE TABLE biological_model_sample (
	biological_model_id       INT(10) NOT NULL,
	biological_sample_id      INT(10) NOT NULL,

	UNIQUE (biological_model_id, biological_sample_id)

);


/**
 * Experimental sample
 * Contains information about a sample
 * A sample can be an animal specimen, an organ, cell, sperm, etc.
 * The EFO ontology can be used to reference 'whole organism', 'animal fluid', 'animal body part'
 * A sample group defines what role or experimental group the sample belongs to. It can be 'control' / 'experimental'
 */
DROP TABLE IF EXISTS biological_sample;
CREATE TABLE biological_sample (
	id                        INT(10) NOT NULL AUTO_INCREMENT,
	external_id               VARCHAR(100)     NOT NULL,
	db_id                     INT(10)          NOT NULL,
	sample_type_acc           VARCHAR(20)      NOT NULL,
	sample_type_db_id         INT(10)          NOT NULL,
	sample_group              VARCHAR(100)     NOT NULL,
	organisation_id           INT(10) NOT NULL,
	production_center_id      INT(10) NOT NULL,
    project_id                INT(10) UNSIGNED NOT NULL,

	PRIMARY KEY (id),
	UNIQUE (external_id, organisation_id)

);


/**
 * An animal sample is a type of sample
 * The discriminative value is on the sample type
 */
DROP TABLE IF EXISTS live_sample;
CREATE TABLE live_sample (
	id                        INT(10) NOT NULL,
	colony_id                 VARCHAR(100) NOT NULL,
	developmental_stage_acc   VARCHAR(20) NOT NULL,
	developmental_stage_db_id INT(10) NOT NULL,
-- 	sex                       ENUM('female', 'hermaphrodite', 'male', 'not_applicable', 'no_data', 'both'),
-- 	zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote'),

	sex                       VARCHAR(24),
	    CHECK (sex IN ('female', 'hermaphrodite', 'male', 'not_applicable', 'no_data', 'both')),
	zygosity                  VARCHAR(24),
	    CHECK (zygosity IN ('homozygote', 'heterozygote', 'hemizygote')),
	date_of_birth             TIMESTAMP NULL,
	litter_id                 VARCHAR(200) NULL,

	PRIMARY KEY (id)

);


/**
 * One sample can refer to another sample
 * Example one: organ to whole organism as a part_of relationship
 */
DROP TABLE IF EXISTS biological_sample_relationship;
CREATE TABLE biological_sample_relationship (
	biological_sample_a_id     INT(10),
	biological_sample_b_id     INT(10),
	relationship               VARCHAR(30) NOT NULL

);


/**
 * experiment
 * A scientific procedure undertaken to make a discovery, test a hypothesis, or
 * demonstrate a known fact.
 * An experiment has several observation associated to it.
 * See table observation
 */
DROP TABLE IF EXISTS experiment;
CREATE TABLE experiment (
	id                         INT(10) NOT NULL AUTO_INCREMENT,
	db_id                      INT(10) NOT NULL,
	external_id                VARCHAR(100),
	sequence_id                VARCHAR(100) NULL DEFAULT NULL,
	date_of_experiment         TIMESTAMP NULL DEFAULT NULL,
	organisation_id            INT(10) NOT NULL,
	project_id                 INT(10) NULL DEFAULT NULL,
	pipeline_id                INT(10) NOT NULL,
	pipeline_stable_id         VARCHAR(30) NOT NULL,
	procedure_id               INT(10) NOT NULL,
	procedure_stable_id        VARCHAR(30) NOT NULL,
	biological_model_id        INT(10) NULL,
	colony_id                  VARCHAR(100) NULL,
	metadata_combined          TEXT,
	metadata_group             VARCHAR(50) DEFAULT '',
	procedure_status           VARCHAR(50) DEFAULT NULL,
	procedure_status_message   VARCHAR(450) DEFAULT NULL,

	PRIMARY KEY(id)

);


/**
 * Links multiple observations to experiment
 */
DROP TABLE IF EXISTS experiment_observation;
CREATE TABLE experiment_observation (
	experiment_id              INT(10) NOT NULL,
	observation_id             INT(10) NOT NULL

);


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
DROP TABLE IF EXISTS observation;
CREATE TABLE observation (
	id                         INT(10) NOT NULL AUTO_INCREMENT,
	db_id                      INT(10) NOT NULL,
	biological_sample_id       INT(10) NULL,
	parameter_id               INT(10) NOT NULL,
	parameter_stable_id        VARCHAR(30) NOT NULL,
	sequence_id                VARCHAR(30) DEFAULT NULL,
	population_id              INT(10) NOT NULL,
-- 	observation_type           ENUM('categorical', 'datetime', 'ontological', 'image_record', 'unidimensional', 'multidimensional', 'time_series', 'metadata', 'text'),

	observation_type           VARCHAR(24),
	    CHECK ( observation_type IN ('categorical', 'datetime', 'ontological', 'image_record', 'unidimensional', 'multidimensional', 'time_series', 'metadata', 'text', 'text_series')),
	missing                    TINYINT(1) DEFAULT 0,
	parameter_status           VARCHAR(50) DEFAULT NULL,
	parameter_status_message   VARCHAR(450) DEFAULT NULL,

	PRIMARY KEY(id)

);


/**
 * text_observation
 * Free text to annotate a phenotype
 */
DROP TABLE IF EXISTS text_observation;
CREATE TABLE text_observation (
	id                         INT(10) NOT NULL,
	text_value                 TEXT,

	PRIMARY KEY(id)

);


/**
 * categorical_observation
 * Categorical phenotypic observation like
 * coat hair pattern: mono-colored, multi-colored, spotted, etc.
 */
DROP TABLE IF EXISTS categorical_observation;
CREATE TABLE categorical_observation (
	id                         INT(10) NOT NULL,
	category                   VARCHAR(200) NOT NULL,

	PRIMARY KEY(id)

);


/**
 * This table will store the ontology 'alternate id's.
 */
DROP TABLE IF EXISTS alternate_id;
CREATE TABLE alternate_id (
	ontology_term_acc          VARCHAR(60) NOT NULL,
	alternate_id_acc           VARCHAR(60) NOT NULL,

	UNIQUE (ontology_term_acc, alternate_id_acc)

);


/**
 * This table will store the ontology 'consider id's.
 */
DROP TABLE IF EXISTS consider_id;
CREATE TABLE consider_id (
	ontology_term_acc          VARCHAR(60) NOT NULL,
	consider_id_acc            VARCHAR(60) NOT NULL,

	UNIQUE (ontology_term_acc, consider_id_acc)

);


/**
 * unidimensional_observation
 * Unidimensional data point measurement
 */
DROP TABLE IF EXISTS unidimensional_observation;
CREATE TABLE unidimensional_observation (
	id                        INT(10) NOT NULL,
	data_point                FLOAT NOT NULL,

	PRIMARY KEY(id)

);


/**
 * ontology_observation
 * ontology data measurement/observation
 */
DROP TABLE IF EXISTS ontology_observation;
CREATE TABLE ontology_observation (
	id           INT(10) NOT NULL,
	parameter_id VARCHAR(255)     NOT NULL, /**not necessary to store this as in main parameter when store observation, we should remove it but in for dev testing**/
	sequence_id  INT(10)          NULL,

	PRIMARY KEY (id)
);


/**
 * unidimensional_observation
 * Unidimensional data point measurement
 */
DROP TABLE IF EXISTS ontology_entity;
CREATE TABLE ontology_entity (
	id                      INT(10) NOT NULL AUTO_INCREMENT,
	ontology_observation_id INT(10) NOT NULL,
	term                    VARCHAR(255)     NULL,
	term_value              VARCHAR(255)     NULL,
	PRIMARY KEY (id)
);


/**
 * multidimensional_observation
 * multidimensional data point measurement
 * data_value: store the value of the observation in dimension 'dimension'
 * order_index: useful for time series or series of values. Keep the order
 * of observations in multiple dimension
 * dimension: dimension definition (x, y, z, t, etc.). It can also be used to
 * store multiple series of observations if needed.
 */
DROP TABLE IF EXISTS multidimensional_observation;
CREATE TABLE multidimensional_observation (
	id                        INT(10) NOT NULL,
	data_point                FLOAT NOT NULL,
	order_index               INT(10) NOT NULL,
	dimension                 VARCHAR(40) NOT NULL,

	PRIMARY KEY(id)

);


/**
 * time_series_observation
 * A time series is a sequence of observations which are ordered in time
 * (or space).
 */
DROP TABLE IF EXISTS time_series_observation;
CREATE TABLE time_series_observation (
	id                        INT(10) NOT NULL,
	data_point                FLOAT NOT NULL,
	time_point                DATETIME,
	discrete_point            FLOAT,

	PRIMARY KEY(id)

);

/**
 * text_series_observation
 * A text series is a sequence of TEXT observations which are segregated by some increment.
 */
DROP TABLE IF EXISTS text_series_observation;
CREATE TABLE text_series_observation (
    id         INT(10) NOT NULL,
    text_value TEXT NOT NULL,
    increment  TEXT NOT NULL,

    PRIMARY KEY(id)

);


/**
 * datetime_observation
 * A datetime observation is a point in time observation
 * (or space).
 */
DROP TABLE IF EXISTS datetime_observation;
CREATE TABLE datetime_observation (
	id                        INT(10) NOT NULL,
	datetime_point            datetime,

	PRIMARY KEY(id)

);


/**
 * metadata_observation
 * Some experimental settings can change from one experiment to another.
 * This table stores the meta information associated to the observation
 * as value property
 */
DROP TABLE IF EXISTS metadata_observation;
CREATE TABLE metadata_observation (
	id                         INT(10) NOT NULL,
	property_value             VARCHAR(100) NOT NULL,

	PRIMARY KEY(id)

);


/*
 * Phenotype to genotype association when analyzed without weight
 */
DROP TABLE IF EXISTS phenotype_call_summary;
CREATE TABLE phenotype_call_summary (
	id                        INT(10) NOT NULL AUTO_INCREMENT,
	external_id               VARCHAR(20) NULL,
	external_db_id            INT(10),
	project_id                INT(10) NOT NULL,
	organisation_id           INT(10) NOT NULL,
	gf_acc                    VARCHAR(20),
	gf_db_id                  INT(10),
	strain_acc                VARCHAR(20),
	strain_db_id              INT(10),
	allele_acc                VARCHAR(20),
	allele_db_id              INT(10),
	colony_id                 VARCHAR(200) NULL,
-- 	sex                       ENUM('female', 'hermaphrodite', 'male', 'both', 'not_applicable', 'no_data'),
-- 	zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	sex                       VARCHAR(24),
	    CHECK (sex IN ('female', 'hermaphrodite', 'male', 'both', 'not_applicable', 'no_data')),
	zygosity                  VARCHAR(24),
	    CHECK (zygosity IN ('homozygote', 'heterozygote', 'hemizygote', 'not_applicable')),
	parameter_id              INT(10) NOT NULL,
	procedure_id              INT(10) NOT NULL,
	pipeline_id               INT(10) NOT NULL,

	mp_acc                    VARCHAR(20) NOT NULL,
	mp_db_id                  INT(10) NOT NULL,

	p_value                   DOUBLE NULL DEFAULT 1,
	effect_size               DOUBLE NULL DEFAULT 0,

	PRIMARY KEY (id)

);


/*
 * Phenotype to genotype association when analyzed with weight
 */
DROP TABLE IF EXISTS phenotype_call_summary_withWeight;
CREATE TABLE phenotype_call_summary_withWeight (
	id                        INT(10) NOT NULL AUTO_INCREMENT,
	external_id               VARCHAR(20) NULL,
	external_db_id            INT(10),
	project_id                INT(10) NOT NULL,
	organisation_id           INT(10) NOT NULL,
	gf_acc                    VARCHAR(20),
	gf_db_id                  INT(10),
	strain_acc                VARCHAR(20),
	strain_db_id              INT(10),
	allele_acc                VARCHAR(20),
	allele_db_id              INT(10),
	colony_id                 VARCHAR(200) NULL,
-- 	sex                       ENUM('female', 'hermaphrodite', 'male', 'not_applicable', 'no_data'),
-- 	zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	sex                       VARCHAR(24),
	    CHECK (sex IN ('female', 'hermaphrodite', 'male', 'not_applicable', 'no_data')),
	zygosity                  VARCHAR(24),
	    CHECK (zygosity IN ('homozygote', 'heterozygote', 'hemizygote', 'not_applicable')),
	parameter_id              INT(10) NOT NULL,
	procedure_id              INT(10) NOT NULL,
	pipeline_id               INT(10) NOT NULL,

	mp_acc                    VARCHAR(20) NOT NULL,
	mp_db_id                  INT(10) NOT NULL,

	p_value                   DOUBLE NULL DEFAULT 1,
	effect_size               DOUBLE NULL DEFAULT 0,

	PRIMARY KEY (id)

);


DROP TABLE IF EXISTS anatomy_call_summary;
CREATE TABLE anatomy_call_summary (
	id                        INT(10) NOT NULL AUTO_INCREMENT,
	external_id               VARCHAR(20) NULL,
	external_db_id            INT(10),
	project_id                INT(10) NOT NULL,
	organisation_id           INT(10) NOT NULL,
	gf_acc                    VARCHAR(20),
	gf_db_id                  INT(10),
	background_strain_acc     VARCHAR(20),
	background_strain_db_id   INT(10),
	allele_acc                VARCHAR(20),
	allele_db_id              INT(10),
	colony_id                 VARCHAR(200) NULL,
-- 	sex                       ENUM('female', 'hermaphrodite', 'male', 'not_applicable', 'no_data'),
-- 	zygosity                  ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	sex                       VARCHAR(24),
	    CHECK (sex IN ('female', 'hermaphrodite', 'male', 'not_applicable', 'no_data')),
	zygosity                  VARCHAR(24),
	    CHECK (zygosity IN ('homozygote', 'heterozygote', 'hemizygote', 'not_applicable')),
	parameter_id              INT(10) NOT NULL,
	procedure_id              INT(10) NOT NULL,
	pipeline_id               INT(10) NOT NULL,

	anatomy_acc               VARCHAR(20) NOT NULL,
	anatomy_db_id             INT(10) NOT NULL,

	expression                VARCHAR(200),

	PRIMARY KEY (id)

);


/*
 * Tables below are for the storage of media/image information
 */
DROP TABLE IF EXISTS image_record_observation;
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
    `omero_dataset_id` int(11) DEFAULT NULL,
	PRIMARY KEY (`id`)
) ;


DROP TABLE IF EXISTS dimension;
CREATE TABLE dimension (
	dim_id                     INT(11) NOT NULL AUTO_INCREMENT,
	parameter_association_id   INT(11) NOT NULL,
	id                         VARCHAR(45) NOT NULL,
	origin                     VARCHAR(45) NOT NULL,
	unit                       VARCHAR(45) DEFAULT NULL,
	value                      DECIMAL(65,10) DEFAULT NULL,

	PRIMARY KEY (dim_id, parameter_association_id)

) ;


DROP TABLE IF EXISTS parameter_association;
CREATE TABLE parameter_association (
	id                         		INT(11) NOT NULL AUTO_INCREMENT,
	observation_id             		INT(11) NOT NULL,
	parameter_id               		VARCHAR(45) NOT NULL,
	sequence_id                		INT(11) DEFAULT NULL,
	dim_id                     		VARCHAR(45) DEFAULT NULL,
	parameter_association_value		VARCHAR(2048) DEFAULT NULL,

	PRIMARY KEY (id),
	UNIQUE (observation_id, parameter_id, parameter_association_value)	-- 287 is the maximum key length. Anything greater throws a 'key too long' error.

) ;


DROP TABLE IF EXISTS procedure_meta_data;
CREATE TABLE procedure_meta_data (
	id                         INT(11) NOT NULL AUTO_INCREMENT,
	parameter_id               VARCHAR(45) NOT NULL,
	sequence_id                VARCHAR(45) DEFAULT NULL,
	parameter_status           VARCHAR(450) DEFAULT NULL,
	value                      VARCHAR(450) DEFAULT NULL,
	procedure_id               VARCHAR(45) NOT NULL,
	experiment_id              INT(11) NOT NULL,
	observation_id             INT(11) DEFAULT '0',

	PRIMARY KEY (id)

) ;


DROP TABLE IF EXISTS genes_secondary_project;
CREATE TABLE genes_secondary_project (
	acc                        VARCHAR(30) NOT NULL,
	secondary_project_id       VARCHAR(20) NOT NULL

);

-- The imits report that populates this table has two production center columns: production_centre_organisation_id,
-- and cohort_production_centre_organisation_id. Since phenotype archive is only interested in the cohort production
-- centre, for convenience we call it 'production center'. We load imits' 'cohort production centre' to the
-- production_centre_organisation_id' column below. We are not interested idn imits' 'production centre'.
DROP TABLE IF EXISTS phenotyped_colony;
CREATE TABLE phenotyped_colony (
	id                                          INT(11)     NOT NULL AUTO_INCREMENT,
	colony_name                                 VARCHAR(64) NOT NULL,
	es_cell_name                                VARCHAR(128),
	gf_acc                                      VARCHAR(20) NOT NULL,
	gf_db_id                                    INT(11)     NOT NULL,
	allele_symbol                               VARCHAR(64) NOT NULL,
	background_strain_name                      VARCHAR(64) NOT NULL,
    background_strain_acc                       VARCHAR(20)           DEFAULT NULL,
	phenotyping_centre_organisation_id          INT(11)     NOT NULL,
	phenotyping_consortium_project_id           INT(11)     NOT NULL,
	production_centre_organisation_id           INT(11)     NOT NULL,
	production_consortium_project_id            INT(11)     NOT NULL,

	PRIMARY KEY (id),
	UNIQUE (colony_name)

) ;

-- -----------------------------------------------------------
-- Tables to store statistical results
-- -----------------------------------------------------------

/*
 * store the result of a fishers test calculation
 */
DROP TABLE IF EXISTS stats_categorical_results;
CREATE TABLE stats_categorical_results (
	id                         INT(10) NOT NULL AUTO_INCREMENT,
	control_id                 INT(10),
-- 	control_sex                ENUM('female', 'hermaphrodite', 'male', 'not_applicable', 'both', 'no_data'),
	control_sex                VARCHAR(24),
	    CHECK ( control_sex IN ('female', 'hermaphrodite', 'male', 'not_applicable', 'both', 'no_data')),
	experimental_id            INT(10),
-- 	experimental_sex           ENUM('female', 'hermaphrodite', 'male', 'not_applicable', 'both', 'no_data'),
-- 	experimental_zygosity      ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	experimental_sex           VARCHAR(24),
	    CHECK (experimental_sex IN ('female', 'hermaphrodite', 'male', 'not_applicable', 'both', 'no_data')),
	experimental_zygosity      VARCHAR(24),
	    CHECK (experimental_zygosity IN ('homozygote', 'heterozygote', 'hemizygote', 'not_applicable')),
	external_db_id             INT(10),
	project_id                 INT(10),
	organisation_id            INT(10),
	pipeline_id                INT(10),
	procedure_id               INT(10),
	parameter_id               INT(10),
	colony_id                  VARCHAR(200),
	dependent_variable         VARCHAR(200),
	mp_acc                     VARCHAR(20)      NULL,
	mp_db_id                   INT(10)          NULL,
	control_selection_strategy VARCHAR(100),
	male_controls              INT(10),
	male_mutants               INT(10),
	female_controls            INT(10),
	female_mutants             INT(10),
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
	female_p_value             DOUBLE,
	female_effect_size         DOUBLE,
	male_p_value               DOUBLE,
	male_effect_size           DOUBLE,
	classification_tag         VARCHAR(200),

	PRIMARY KEY (id)

);


/*
 * store the result of a PhenStat calculation
 */
DROP TABLE IF EXISTS stats_unidimensional_results;
CREATE TABLE stats_unidimensional_results (
	id                               INT(10) NOT NULL AUTO_INCREMENT,
	control_id                       INT(10),
	experimental_id                  INT(10),
-- 	experimental_zygosity            ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	experimental_zygosity            VARCHAR(24),
	    CHECK (experimental_zygosity IN ('homozygote', 'heterozygote', 'hemizygote', 'not_applicable')),
	external_db_id                   INT(10),
	project_id                       INT(10),
	organisation_id                  INT(10),
	pipeline_id                      INT(10),
	procedure_id                     INT(10),
	parameter_id                     INT(10),
	colony_id                        VARCHAR(200),
	dependent_variable               VARCHAR(200),
	control_selection_strategy       VARCHAR(100),
	mp_acc                           VARCHAR(20)      NULL,
	mp_db_id                         INT(10)          NULL,
	male_mp_acc                      VARCHAR(20)      NULL,
	female_mp_acc                    VARCHAR(20)      NULL,
	male_controls                    INT(10),
	male_mutants                     INT(10),
	female_controls                  INT(10),
	female_mutants                   INT(10),
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

	PRIMARY KEY (id)

);


/*
 * store the result of a PhenStat calculation when analyzed with weight
 */
DROP TABLE IF EXISTS stats_unidimensional_results_withWeight;
CREATE TABLE stats_unidimensional_results_withWeight (
	id                               INT(10) NOT NULL AUTO_INCREMENT,
	control_id                       INT(10),
	experimental_id                  INT(10),
-- 	experimental_zygosity            ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	experimental_zygosity            VARCHAR(24),
	    CHECK (experimental_zygosity IN ('homozygote', 'heterozygote', 'hemizygote', 'not_applicable')),
	external_db_id                   INT(10),
	project_id                       INT(10),
	organisation_id                  INT(10),
	pipeline_id                      INT(10),
	procedure_id                     INT(10),
	parameter_id                     INT(10),
	colony_id                        VARCHAR(200),
	dependent_variable               VARCHAR(200),
	control_selection_strategy       VARCHAR(100),
	mp_acc                           VARCHAR(20)      NULL,
	mp_db_id                         INT(10)          NULL,
	male_mp_acc                      VARCHAR(20)      NULL,
	female_mp_acc                    VARCHAR(20)      NULL,
	male_controls                    INT(10),
	male_mutants                     INT(10),
	female_controls                  INT(10),
	female_mutants                   INT(10),
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

	PRIMARY KEY (id)

);


/*
 * store the result of a PhenStat calculation
 */
DROP TABLE IF EXISTS stats_rrplus_results;
CREATE TABLE stats_rrplus_results (
	id                               INT(10) NOT NULL AUTO_INCREMENT,
	control_id                       INT(10),
	experimental_id                  INT(10),
-- 	experimental_zygosity            ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	experimental_zygosity            VARCHAR(24),
	    CHECK (experimental_zygosity IN ('homozygote', 'heterozygote', 'hemizygote', 'not_applicable')),
	external_db_id                   INT(10),
	project_id                       INT(10),
	organisation_id                  INT(10),
	pipeline_id                      INT(10),
	procedure_id                     INT(10),
	parameter_id                     INT(10),
	colony_id                        VARCHAR(200),
	dependent_variable               VARCHAR(200),
	control_selection_strategy       VARCHAR(100),
	mp_acc                           VARCHAR(20)      NULL,
	mp_db_id                         INT(10)          NULL,
	male_mp_acc                      VARCHAR(20)      NULL,
	female_mp_acc                    VARCHAR(20)      NULL,
	male_controls                    INT(10),
	male_mutants                     INT(10),
	female_controls                  INT(10),
	female_mutants                   INT(10),
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

	PRIMARY KEY (id)

);


/*
 * Store the relationship between a phenotype call and the result that
 * produced the call.  Could refer to either the stats_categorical_result
 * or a stats_unidimensional_result or a stats_rrplus_result
 */
DROP TABLE IF EXISTS stat_result_phenotype_call_summary;
CREATE TABLE stat_result_phenotype_call_summary (
	categorical_result_id     INT(10) DEFAULT NULL,
	unidimensional_result_id  INT(10) DEFAULT NULL,
	rrplus_result_id          INT(10) DEFAULT NULL,
	phenotype_call_summary_id INT(10) NOT NULL,

	PRIMARY KEY (phenotype_call_summary_id)

);


--
-- Discrete statistical result schema
--
DROP TABLE IF EXISTS statistical_result;
CREATE TABLE statistical_result (
	id                               INT(10) NOT NULL AUTO_INCREMENT,
	control_id                       INT(10),
	experimental_id                  INT(10),
-- 	experimental_zygosity            ENUM('homozygote', 'heterozygote', 'hemizygote', 'not_applicable'),
	experimental_zygosity            VARCHAR(24),
	    CHECK (experimental_zygosity IN ('homozygote', 'heterozygote', 'hemizygote', 'not_applicable')),
	external_db_id                   INT(10),
	project_id                       INT(10),
	organisation_id                  INT(10),
	pipeline_id                      INT(10),
	procedure_id                     INT(10),
	parameter_id                     INT(10),
	pipeline_stable_id               VARCHAR(50),
	procedure_stable_id              VARCHAR(50),
	parameter_stable_id              VARCHAR(50),
	gene_acc                         VARCHAR(20)      NULL,
	colony_id                        VARCHAR(200),
	control_selection_strategy       VARCHAR(100),
	male_mp_acc                      VARCHAR(20)      NULL,
	female_mp_acc                    VARCHAR(20)      NULL,
	male_controls                    INT(10),
	male_mutants                     INT(10),
	female_controls                  INT(10),
	female_mutants                   INT(10),
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

	PRIMARY KEY (id)

);


DROP TABLE IF EXISTS statistical_result_phenotype_call_summary;
CREATE TABLE statistical_result_phenotype_call_summary (
	phenotype_call_summary_id INT(10) NOT NULL,
	result_id                 INT(10),

	PRIMARY KEY (phenotype_call_summary_id),
	FOREIGN KEY (result_id) REFERENCES statistical_result (id)

);


DROP TABLE IF EXISTS statistical_result_additional;
CREATE TABLE statistical_result_additional (
	id                         INT(10) NOT NULL,
	raw_output                 MEDIUMTEXT,
	dataset                    MEDIUMTEXT,

	PRIMARY KEY (id),
	FOREIGN KEY (id) REFERENCES statistical_result (id)

);


DROP TABLE IF EXISTS statistical_result_phenstat;
CREATE TABLE statistical_result_phenstat (
	id                               INT(10) NOT NULL,
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
	FOREIGN KEY (id) REFERENCES statistical_result (id)
);


DROP TABLE IF EXISTS statistical_result_fisher_exact;
CREATE TABLE statistical_result_fisher_exact (
	id         INT(10) NOT NULL,
	category_a TEXT,
	category_b TEXT,

	PRIMARY KEY (id),
	FOREIGN KEY (id) REFERENCES statistical_result (id)

);


DROP TABLE IF EXISTS statistical_result_manual;
CREATE TABLE statistical_result_manual (
	id     INT(10) NOT NULL,
	method VARCHAR(200),

	PRIMARY KEY (id),
	FOREIGN KEY (id) REFERENCES statistical_result (id)

);


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
);


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
);


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
);


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
);


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
	FOREIGN_KEY_ID     INT(11)      DEFAULT NULL
);

DROP TABLE IF EXISTS ANN_ONTOLOGY_DICT;
CREATE TABLE ANN_ONTOLOGY_DICT (
	ID          INT(11) NOT NULL,
	NAME        VARCHAR(256)  DEFAULT NULL,
	DESCRIPTION VARCHAR(2048) DEFAULT NULL,
	ACTIVE      TINYINT(4)    DEFAULT NULL,
	ORDER_BY    INT(11)       DEFAULT NULL,
	PRIMARY KEY (ID)
);


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
	QC_STATUS                 VARCHAR(255) DEFAULT NULL
);


DROP TABLE IF EXISTS IMA_EXPERIMENT_DICT;
CREATE TABLE IMA_EXPERIMENT_DICT (
	ID          INT(11) NOT NULL,
	NAME        VARCHAR(128)  DEFAULT NULL,
	DESCRIPTION VARCHAR(4000) DEFAULT NULL,
	ORDER_BY    INT(11)       DEFAULT NULL,
	ACTIVE      TINYINT(4)    DEFAULT NULL,

	PRIMARY KEY (ID)
);


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
	PRIMARY KEY (ID)
);


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
	Y_END           FLOAT         DEFAULT NULL
);


DROP TABLE IF EXISTS IMA_IMAGE_TAG_TYPE;
CREATE TABLE IMA_IMAGE_TAG_TYPE (
	NAME VARCHAR(128) DEFAULT NULL,
	ID   INT(11) NOT NULL,

	PRIMARY KEY (ID)
);


DROP TABLE IF EXISTS IMA_IMPORT_CONTEXT;
CREATE TABLE IMA_IMPORT_CONTEXT (
	ID   INT(11) NOT NULL,
	NAME VARCHAR(64) DEFAULT NULL,

	PRIMARY KEY (ID)
);


DROP TABLE IF EXISTS IMA_IMPORT_LOG;
CREATE TABLE IMA_IMPORT_LOG (
	LOG_ID        VARCHAR(4000) DEFAULT NULL,
	LOG_MESSAGE   VARCHAR(4000) DEFAULT NULL,
	LOG_TIMESTAMP TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	LOG_STATUS    VARCHAR(128) DEFAULT NULL,
	LOG_URL       VARCHAR(4000) DEFAULT NULL,
	ID            INT(10) NOT NULL AUTO_INCREMENT,

	PRIMARY KEY (ID)
);


DROP TABLE IF EXISTS IMA_PREDEFINED_TAG;
CREATE TABLE IMA_PREDEFINED_TAG (
	ID                 INT(11)      DEFAULT NULL,
	TAG_TYPE_ID        INT(11)      DEFAULT NULL,
	TAG_NAME           VARCHAR(256) DEFAULT NULL,
	EXPERIMENT_DICT_ID INT(11)      DEFAULT NULL,
	ORDER_BY           INT(11)      DEFAULT NULL,
	ALLOW_MULTIPLE     TINYINT(4)   DEFAULT '0',
	ALLOW_IN_ROI       TINYINT(4)   DEFAULT NULL
);


DROP TABLE IF EXISTS IMA_PREDEFINED_TAG_VALUE;
CREATE TABLE IMA_PREDEFINED_TAG_VALUE (
	ID                INT(11)       DEFAULT NULL,
	PREDEFINED_TAG_ID INT(11)       DEFAULT NULL,
	TAG_VALUE         VARCHAR(4000) DEFAULT NULL,
	ORDER_BY          INT(11)       DEFAULT NULL
);


DROP TABLE IF EXISTS IMA_PUBLISHED_DICT;
CREATE TABLE IMA_PUBLISHED_DICT (
	ID          INT(11) NOT NULL,
	NAME        VARCHAR(512) DEFAULT NULL,
	DESCRIPTION VARCHAR(512) DEFAULT NULL,
	ORDER_BY    INT(11)      DEFAULT NULL,
	ACTIVE      TINYINT(4)   DEFAULT NULL,

	PRIMARY KEY (ID)
);


DROP TABLE IF EXISTS IMA_QC_DICT;
CREATE TABLE IMA_QC_DICT (
	ID          INT(11) NOT NULL,
	NAME        VARCHAR(512) DEFAULT NULL,
	DESCRIPTION VARCHAR(512) DEFAULT NULL,
	ORDER_BY    INT(11)      DEFAULT NULL,
	ACTIVE      TINYINT(4)   DEFAULT NULL,
	PRIMARY KEY (ID)
);


DROP TABLE IF EXISTS IMA_SUBCONTEXT;
CREATE TABLE IMA_SUBCONTEXT (
	ID                 INT(11) NOT NULL,
	IMPORT_CONTEXT_ID  INT(11)    DEFAULT NULL,
	EXPERIMENT_DICT_ID INT(11)    DEFAULT NULL,
	IS_DEFAULT         TINYINT(4) DEFAULT '0',

	PRIMARY KEY (ID)
);


DROP TABLE IF EXISTS MTS_GENOTYPE_DICT;
CREATE TABLE MTS_GENOTYPE_DICT (
	ID          INT(11)     NOT NULL,
	NAME        VARCHAR(50) NOT NULL,
	ORDER_BY    INT(11)     NOT NULL,
	DESCRIPTION VARCHAR(25) NOT NULL,
	ACTIVE      INT(11)     NOT NULL
);


DROP TABLE IF EXISTS MTS_MOUSE_ALLELE;
CREATE TABLE MTS_MOUSE_ALLELE (
	ID               INT(11) NOT NULL,
	MOUSE_ID         INT(11) NOT NULL,
	ALLELE_ID        INT(11) NOT NULL,
	GENOTYPE_DICT_ID INT(11) NOT NULL,

	PRIMARY KEY (ID)
);


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

	PRIMARY KEY (ID)
);


DROP TABLE IF EXISTS ima_image_record_annotation_vw;
CREATE TABLE ima_image_record_annotation_vw (
	IMAGE_RECORD_ID INT(11)      DEFAULT NULL,
	TERM_ID         VARCHAR(128) DEFAULT NULL,
	TERM_NAME       VARCHAR(256) DEFAULT NULL
);



DROP TABLE IF EXISTS higher_level_annotation;
CREATE TABLE higher_level_annotation (
	term_id varchar(128) NOT NULL DEFAULT '',

	PRIMARY KEY    (term_id)
);


DROP TABLE IF EXISTS ontology_term_anomaly;
CREATE TABLE ontology_term_anomaly (
	id                 INT(11)      NOT NULL AUTO_INCREMENT,
	db_name            VARCHAR(128) NOT NULL,
	table_name         VARCHAR(128) NOT NULL,
	column_name        VARCHAR(128) NOT NULL,
	original_acc       VARCHAR(128) NOT NULL,
	replacement_acc    VARCHAR(128),
	reason             VARCHAR(128) NOT NULL,
	last_modified      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

	PRIMARY KEY(id)
);


DROP TABLE IF EXISTS missing_colony_id;
CREATE TABLE missing_colony_id (
	id                 INT(11)      NOT NULL AUTO_INCREMENT,
	colony_id          VARCHAR(255) NOT NULL,
	log_level          TINYINT(1)   DEFAULT 0,    --   WARN = 1,  INFO = 0,  DEBUG = -1,
	reason						 VARCHAR(512) NOT NULL,

	PRIMARY KEY(id)
);

INSERT INTO missing_colony_id (colony_id, log_level, reason) VALUES
	('(Deluca)<Deluca>', -1, 'We were never able to obtain the minimum set of data required to add this colony id'),
	('EPD0038_2_A04',    -1, 'We were never able to obtain the minimum set of data required to add this colony id'),
	('internal',         -1, 'We were never able to obtain the minimum set of data required to add this colony id'),
	('Trm1',             -1, 'We were never able to obtain the minimum set of data required to add this colony id'),
	('MAG',              -1, 'We were never able to obtain the minimum set of data required to add this colony id'),
	('EUCJ0019_C12',     -1, 'We were never able to obtain the minimum set of data required to add this colony id'),
	('EPD0130_2_C06',    -1, 'Even though this colonyId is in Hugh''s list, Jeremy''s research has shown there is newer data submitted under colonyId MEYN supporting the data in EPD00130_2_C06, which was an aborted experiment'),
    ('EPD0025_2_A04',    -1, 'Even though this colonyId has data, the microinjection was aborted and any data is invalid');


/**
 * External resources / database to populate
 */

SET @DB_ID_MGI=3;

insert into external_db(id, name, short_name, version, version_date) values(1,                            'Mouse Genome Assembly', 'NCBI m38', 'GRCm38', '2012-01-09');
insert into external_db(id, name, short_name, version, version_date) values(2,                            'MGI Genome Feature Type', 'Genome Feature Type', 'JAX', '2011-12-22');
insert into external_db(id, name, short_name, version, version_date) values(@DB_ID_MGI,                   'Mouse Genome Informatics', 'MGI', 'JAX', '2011-12-22');
insert into external_db(id, name, short_name, version, version_date) values(4,                            'murine X/Y pseudoautosomal region', 'X/Y', 'unknown', '2012-01-06');
insert into external_db(id, name, short_name, version, version_date) values(5,                            'Mammalian Phenotype', 'MP', 'JAX', '2012-01-09');
insert into external_db(id, name, short_name, version, version_date) values(6,                            'IMPReSS', 'IMPReSS', 'unknown', '2012-01-26');
insert into external_db(id, name, short_name, version, version_date) values(7,                            'Phenotypic qualities (properties)', 'PATO', 'unknown', '2011-11-23');
insert into external_db(id, name, short_name, version, version_date) values(8,                            'Mouse adult gross anatomy', 'MA', 'unknown', '2011-07-22');
insert into external_db(id, name, short_name, version, version_date) values(9,                            'Chemical entities of biological interest', 'CHEBI', '87', '2012-01-10');
insert into external_db(id, name, short_name, version, version_date) values(10,                           'Environment Ontology', 'EnvO', 'unknown', '2011-03-24');
insert into external_db(id, name, short_name, version, version_date) values(11,                           'Gene Ontology', 'GO', '1.1.2551', '2012-01-26');
insert into external_db(id, name, short_name, version, version_date) values(12,                           'EuroPhenome', 'EuroPhenome', 'February 2012', '2012-04-26');
insert into external_db(id, name, short_name, version, version_date) values(13,                           'Evidence codes', 'ECO', 'July 2012', '2012-07-30');
insert into external_db(id, name, short_name, version, version_date) values(14,                           'Mouse gross anatomy and development', 'EMAP', 'July 2012', '2012-07-30');
insert into external_db(id, name, short_name, version, version_date) values(15,                           'Experimental Factor Ontology', 'EFO', 'July 2012', '2012-07-31');
insert into external_db(id, name, short_name, version, version_date) values(16,                           'International Mouse Strain Resource', 'IMSR', 'August 2012', '2012-08-13');
insert into external_db(id, name, short_name, version, version_date) values(17,                           'Vertebrate Genome Annotation', 'VEGA', '50', '2012-12-19');
insert into external_db(id, name, short_name, version, version_date) values(18,                           'Ensembl', 'Ensembl', '70', '2013-01-17');
insert into external_db(id, name, short_name, version, version_date) values(19,                           'NCBI EntrezGene', 'EntrezGene', 'unknown', '2013-02-19');
insert into external_db(id, name, short_name, version, version_date) values(20,                           'WTSI Mouse Genetics Project', 'MGP', 'unknown', '2013-02-19');
insert into external_db(id, name, short_name, version, version_date) values(21,                           'Consensus CDS', 'cCDS', '2012-08-14', '2012-08-14');
insert into external_db(id, name, short_name, version, version_date) values(22,                           'International Mouse Phenotyping Consortium', 'IMPC', '2010-11-15', '2010-11-15');
insert into external_db(id, name, short_name, version, version_date) values(23,                           'Immunology', '3i', '2014-11-07', '2014-11-07');
insert into external_db(id, name, short_name, version, version_date) values(24,                           'Mouse pathology', 'MPATH', '2013-08-12', '2013-08-12');
insert into external_db(id, name, short_name, version, version_date) values(25,                           'Mouse Developmental Stages', 'MMUSDV', '2016-05-13', '2016-05-13');
insert into external_db(id, name, short_name, version, version_date) values(26,                           'Mouse gross anatomy and development, timed', 'EMAPA', '2016-05-13', '2016-05-13');



/**
Different Mouse Mutant related projects
*/

SET @PROJECT_ID_EUMODIC=1;

INSERT INTO project(id, name, fullname, description) VALUES(@PROJECT_ID_EUMODIC, 'EUMODIC', 'European Mouse Disease Clinic', 'Consortium of 18 research institutes in 8 European countries who are experts in the field of mouse functional genomics and phenotyping');
INSERT INTO project(id, name, fullname, description) VALUES(2,                   'KOMP', 'NIH Knockout Mouse Project', 'Trans-NIH initiative that aims to generate a comprehensive and public resource comprised of mouse embryonic stem (ES) cells containing a null mutation in every gene in the mouse genome');
INSERT INTO project(id, name, fullname, description) VALUES(3,                   'KOMP2', 'Knockout Mouse Phenotyping Program', 'The Common Fund''s Knockout Mouse Phenotyping Program (KOMP2) provides broad, standardized phenotyping of a genome-wide collection of mouse knockouts generated by the International Knockout Mouse Consortium (IKMC), funded by the NIH, European Union, Wellcome Trust, Canada, and the Texas Enterprise Fund');
INSERT INTO project(id, name, fullname, description) VALUES(4,                   'IMPC', 'International Mouse Phenotyping Consortium', 'Group of major mouse genetics research institutions along with national funding organisations formed to address the challenge of developing an encyclopedia of mammalian gene function');
INSERT INTO project(id, name, fullname, description) VALUES(5,                   'IKMC', 'International Knock-out Mouse Consortium', 'Mutate all protein-coding genes in the mouse using a combination of gene trapping and gene targeting in C57BL/6 mouse embryonic stem (ES) cells');
INSERT INTO project(id, name, fullname, description) VALUES(6,                   'EUCOMM', 'European Conditional Mouse Mutagenesis Program', 'Generation, archiving, and world-wide distribution of up to 12.000 conditional mutations across the mouse genome in mouse embryonic stem (ES) cells. Establishment of a limited number of mouse mutants from this resource');
INSERT INTO project(id, name, fullname, description) VALUES(7,                   'NorCOMM', 'North American Conditional Mouse Mutagenesis Project', 'Large-scale research initiative focused on developing and distributing a library of mouse embryonic stem (ES) cell lines carrying single gene trapped or targeted mutations across the mouse genome');
INSERT INTO project(id, name, fullname, description) VALUES(8,                   'MGP', 'Wellcome Trust Sanger Institute Mouse Genetics Project', 'MGP Phenotyping pipeline (funded by WTSI & EUMODIC)');
INSERT INTO project(id, name, fullname, description) VALUES(9,                   'GMC', 'HMGU GMC pipeline', 'German Mouse Clinic Phenotype Pipeline');
INSERT INTO project(id, name, fullname, description) VALUES(10,                  'BaSH', 'KOMP2 BaSH consortium', 'The BaSH consortium is a cooperative effort between Baylor College of Medicine, the Wellcome Trust Sanger Institute (WTSI) and MRC Harwell Mary Lyon Centre. The BaSH consortium is a member of the International Mouse Phenotyping Consortium (IMPC) where the status of KOMP alleles may be found. Interest may also be registered for specific KOMP alleles.');
INSERT INTO project(id, name, fullname, description) VALUES(11,                  'NorCOMM2', 'North American Conditional Mouse Mutagenesis project 2', 'In NorCOMM2, our Genome Canada-funded project has partnered with MRC Harwell to use the IKMC mouse ES cell resource to generate and phenotype >1,000 mutant mouse lines');
INSERT INTO project(id, name, fullname, description) VALUES(12,                  'DTCC', 'DTCC consortium', 'University of California, Davis (UC Davis), The Centre for Phenogenomics (TCP), Children''s Hospital Oakland Research Institute (CHORI) and Charles River have come together to form the DTCC Consortium for this 5-year research effort.');
INSERT INTO project(id, name, fullname, description) VALUES(13,                  'JAX', 'Jackson Laboratory', 'JAXs objective is to phenotype 833 of the KOMP2 strains. To do this, it is using a powerful, efficient, high-throughput phenotyping pipeline that integrates recommendations from the IMPC and includes disease-relevant phenotypes recommended by a panel of internal and external KOMP2 domain experts.');
INSERT INTO project(id, name, fullname, description) VALUES(14,                  'Phenomin', 'Phenomin', 'PHENOMIN will participate to the International Mouse Phenotyping Consortium (IMPC) and, owing to its tight connection with the Institut de Génétique et de Biologie Moléculaire et Cellulaire (IGBMC) at Illkirch and the Centre d''Immunologie de Marseille-Luminy, will be able to provide a major contribution to the goals of the IMPC.');
INSERT INTO project(id, name, fullname, description) VALUES(15,                  'Helmholtz GMC', 'Helmholtz German Mouse Clinic', 'Characterisation of mouse models for human diseases to understand molecular mechanisms of human disorders and for the development of new therapies');
INSERT INTO project(id, name, fullname, description) VALUES(16,                  'MRC', 'MRC project', '-');
INSERT INTO project(id, name, fullname, description) VALUES(17,                  'MARC', 'Model Animal Research Center', 'Nanjing University - Model Animal Research Center');
INSERT INTO project(id, name, fullname, description) VALUES(18,                  'RBRC', 'RIKEN BioResource Center', 'RIKEN BioResource Center - Japan');
INSERT INTO project(id, name, fullname, description) VALUES(19,                  'CAM-SU GRC', 'Cambrige-Soochow Genomic Resource Center', 'Cambrige-Soochow Genomic Resource Center');
INSERT INTO project(id, name, fullname, description) VALUES(20,                  'EMBL Monterotondo', 'European Molecular Biology Laboratory Monterotondo', 'European Molecular Biology Laboratory Monterotondo');
INSERT INTO project(id, name, fullname, description) VALUES(21,                  'Infrafrontier-I3', 'Infrafrontier-I3 Consortium', 'Infrafrontier-I3 Consortium');
INSERT INTO project(id, name, fullname, description) VALUES(22,                  'KMPC', 'Korea Mouse Phenotyping Center', 'Korea Mouse Phenotyping Center');
INSERT INTO project(id, name, fullname, description) VALUES(23,                  'UC Davis', 'University of California at Davis School of Veterinary Medicine', 'UC Davis Veterinary Medicine');
INSERT INTO project(id, name, fullname, description) VALUES(24,                  'NARLabs', 'National Applied Research Laboratories', 'National Applied Research Laboratories');
INSERT INTO project(id, name, fullname, description) VALUES(25,                  'CCP', 'Czech Centre for Phenogenomics', 'Czech Centre for Phenogenomics');
INSERT INTO project(id, name, fullname, description) VALUES(26,                  'IMG', 'Institute of Molecular Genetics of the ASCR, v.v.i', 'Institute of Molecular Genetics of the ASCR, v.v.i');
INSERT INTO project(id, name, fullname, description) VALUES(27, '3i', 'Infection and Immunity Immunophenotyping (3i) consortium', 'The Infection and Immunity Immunophenotyping (3i) consortium conducts a high-throughput immunological phenotyping of approximately 550 knockout mouse lines generated by the Wellcome Trust Sanger Institute (WTSI).');
INSERT INTO project(id, name, fullname, description) VALUES(28, 'CCP-IMG', 'Institute of Molecular Genetics of the ASCR, v.v.i', 'Institute of Molecular Genetics of the ASCR, v.v.i');


/**
 * Some project names are inherited from
 */
/**
Organizations participating to the projects
 */

SET @ORG_ID_WTSI=3;
SET @ORG_ID_MRC_HARWELL=7;
SET @ORG_ID_ICS=8;
SET @ORG_ID_HMGU=9;
SET @ORG_ID_CMHD=23;

INSERT INTO organisation(id, name, fullname, country) VALUES(1,                               'CHORI', 'Children''s Hospital Oakland Research Institute', 'USA');
INSERT INTO organisation(id, name, fullname, country) VALUES(2,                               'UC Davis', 'University of California at Davis School of Veterinary Medicine', 'USA');
INSERT INTO organisation(id, name, fullname, country) VALUES(@ORG_ID_WTSI,                    'WTSI', 'Wellcome Trust Sanger Institute', 'UK');
INSERT INTO organisation(id, name, fullname, country) VALUES(4,                               'Regeneron', 'Regeneron Pharmaceuticals, Inc.', 'USA');
INSERT INTO organisation(id, name, fullname, country) VALUES(5,                               'EBI', 'European Bioinformatics Institute', 'UK');
INSERT INTO organisation(id, name, fullname, country) VALUES(6,                               'JAX', 'Jackson Laboratory', 'USA');
INSERT INTO organisation(id, name, fullname, country) VALUES(@ORG_ID_MRC_HARWELL,             'MRC Harwell', 'Medical Research Council centre for mouse genetics', 'UK');
INSERT INTO organisation(id, name, fullname, country) VALUES(@ORG_ID_ICS,                     'ICS', 'Institut Clinique de la Souris', 'France');
INSERT INTO organisation(id, name, fullname, country) VALUES(@ORG_ID_HMGU,                    'HMGU', 'Helmholtz Zentrum München Deutsches Forschungszentrum für Gesundheit und Umwelt', 'Germany');
INSERT INTO organisation(id, name, fullname, country) VALUES(10,                              'HZI', 'Das Helmholtz Zentrum für Infektionsforschung', 'Germany');
INSERT INTO organisation(id, name, fullname, country) VALUES(11,                              'CNR', 'National Research Council', 'Italy');
INSERT INTO organisation(id, name, fullname, country) VALUES(12,                              'U.Manchester', 'University of Manchester', 'UK');
INSERT INTO organisation(id, name, fullname, country) VALUES(13,                              'EMBL Monterotondo', 'European Molecular Biology Laboratory Monterotondo', 'Italy');
INSERT INTO organisation(id, name, fullname, country) VALUES(14,                              'CNIO', 'Centro Nacional de Investigaciones Oncológicas', 'Spain');
INSERT INTO organisation(id, name, fullname, country) VALUES(15,                              'AniRA', 'Technical facilities within the UMS3444 Biosciences Gerland-Lyon Sud', 'France');
INSERT INTO organisation(id, name, fullname, country) VALUES(16,                              'TAU', 'Tel Aviv University', 'Israel');
INSERT INTO organisation(id, name, fullname, country) VALUES(17,                              'UAB', 'Autonomous University of Barcelona', 'Spain');
INSERT INTO organisation(id, name, fullname, country) VALUES(18,                              'CIG', 'Center for Integrative Genomics', 'Switzerland');
INSERT INTO organisation(id, name, fullname, country) VALUES(19,                              'Transgenose CNRS', 'Institut De Transgenose CNRS Orléans', 'France');
INSERT INTO organisation(id, name, fullname, country) VALUES(20,                              'U.Cambridge', 'University of Cambridge', 'UK');
INSERT INTO organisation(id, name, fullname, country) VALUES(21,                              'TIGEM', 'Telethon Institute of Genetics and Medicine', 'Italy');
INSERT INTO organisation(id, name, fullname, country) VALUES(22,                              'Fleming', 'Research Centre "Alexander Fleming"', 'Greece');
INSERT INTO organisation(id, name, fullname, country) VALUES(@ORG_ID_CMHD,                    'CMHD', 'Centre for Modeling Human Disease', 'Canada');
INSERT INTO organisation(id, name, fullname, country) VALUES(24,                              'CIPHE', 'Centre d''ImmunoPhenomique', 'France');
INSERT INTO organisation(id, name, fullname, country) VALUES(25,                              'BCM', 'Baylor College of Medicine', 'USA');
INSERT INTO organisation(id, name, fullname, country) VALUES(26,                              'RBRC', 'RIKEN BioResource Center', 'Japan');
INSERT INTO organisation(id, name, fullname, country) VALUES(27,                              'TCP', 'The Centre for Phenogenomics', 'Canada');
INSERT INTO organisation(id, name, fullname, country) VALUES(28,                              'NING', 'Nanjing University Model Animal Research Center', 'China');
INSERT INTO organisation(id, name, fullname, country) VALUES(29,                              'CDTA', 'Institut de Transgenose (CDTA Orleans)', 'France');
INSERT INTO organisation(id, name, fullname, country) VALUES(30,                              'CCP-IMG', 'Institute of Molecular Genetics of the ASCR, v. v. i.', 'Czech Republic');
INSERT INTO organisation(id, name, fullname, country) VALUES(31,                              'CAM-SU GRC', 'Cambrige-Soochow Genomic Resource Center', 'China');
INSERT INTO organisation(id, name, fullname, country) VALUES(32,                              'CRL', 'Charles River Laboratory', 'USA');
INSERT INTO organisation(id, name, fullname, country) VALUES(33,                              'INFRAFRONTIER-VETMEDUNI', 'University of Veterinary Medicine Vienna', 'Austria');
INSERT INTO organisation(id, name, fullname, country) VALUES(34,                              'KMPC', 'Korea Mouse Phenotyping Center', 'Korea');
INSERT INTO organisation(id, name, fullname, country) VALUES(35,                              'MARC', 'Model Animal Research Center', 'Japan');
INSERT INTO organisation(id, name, fullname, country) VALUES(36,                              'NARLabs', 'National Applied Research Laboratories', 'Taiwan');
INSERT INTO organisation(id, name, fullname, country) VALUES(37,                              'BIAT', 'University of Veterinary Medicine Vienna', 'Austria');
INSERT INTO organisation(id, name, fullname, country) VALUES(38,                              'PH', 'Academy of Sciences of the Czech Republic', 'Czech Republic');
INSERT INTO organisation(id, name, fullname, country) VALUES(39,                              'VETMEDUNI', 'University of Veterinary Medicine, Vienna', 'Austria');


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



/*
** FOR MARKER REGION LOADING (from AdminTools seq_region.sql
 */
insert into coord_system(name, db_id, strain_db_id, strain_acc) values('chromosome', 1, 3, 'MGI:3028467');
insert into coord_system(name, db_id, strain_db_id, strain_acc) values('par', 4, NULL, NULL);

insert into seq_region(name, coord_system_id, length) values('1', 1, 195471971);
insert into seq_region(name, coord_system_id, length) values('2', 1, 182113224);
insert into seq_region(name, coord_system_id, length) values('3', 1, 160039680);
insert into seq_region(name, coord_system_id, length) values('4', 1, 156508116);
insert into seq_region(name, coord_system_id, length) values('5', 1, 151834684);
insert into seq_region(name, coord_system_id, length) values('6', 1, 149736546);
insert into seq_region(name, coord_system_id, length) values('7', 1, 145441459);
insert into seq_region(name, coord_system_id, length) values('8', 1, 129401213);
insert into seq_region(name, coord_system_id, length) values('9', 1, 124595110);
insert into seq_region(name, coord_system_id, length) values('10', 1, 130694993);
insert into seq_region(name, coord_system_id, length) values('11', 1, 122082543);
insert into seq_region(name, coord_system_id, length) values('12', 1, 120129022);
insert into seq_region(name, coord_system_id, length) values('13', 1, 120421639);
insert into seq_region(name, coord_system_id, length) values('14', 1, 124902244);
insert into seq_region(name, coord_system_id, length) values('15', 1, 104043685);
insert into seq_region(name, coord_system_id, length) values('16', 1, 98207768);
insert into seq_region(name, coord_system_id, length) values('17', 1, 94987271);
insert into seq_region(name, coord_system_id, length) values('18', 1, 90702639);
insert into seq_region(name, coord_system_id, length) values('19', 1, 61431566);
insert into seq_region(name, coord_system_id, length) values('X', 1, 171031299);
insert into seq_region(name, coord_system_id, length) values('Y', 1, 91744698);
insert into seq_region(name, coord_system_id, length) values('MT', 1, 16299);
insert into seq_region(name, coord_system_id, length) values('XY', 2, 0);
insert into seq_region(name, coord_system_id, length) values('MG153_PATCH', 1, 61431565);
insert into seq_region(name, coord_system_id, length) values('MG3835_PATCH', 1, 90835696);
insert into seq_region(name, coord_system_id, length) values('MG4136_PATCH', 1, 156508116);
insert into seq_region(name, coord_system_id, length) values('MG4151_PATCH', 1, 145439975);
insert into seq_region(name, coord_system_id, length) values('MG4209_PATCH', 1, 91793962);
insert into seq_region(name, coord_system_id, length) values('MG4211_PATCH', 1, 91797447);
insert into seq_region(name, coord_system_id, length) values('MG4212_PATCH', 1, 151862668);
insert into seq_region(name, coord_system_id, length) values('MG4213_PATCH', 1, 91736668);
insert into seq_region(name, coord_system_id, length) values('MG4214_PATCH', 1, 171031749);


/*
** FOR MARKER TYPES LOADING (from AdminTools mgi_marker_types.sql)
*/
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '1', db.id, 'gene', 'A region (or regions) that include all of the sequence elements necessary to encode a functional transcript. A gene may include regulatory regions, transcribed regions, and/or other functional sequence regions.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '2', db.id, 'protein coding gene', 'A gene that produces at least one transcript that is translated into a protein.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '3', db.id, 'non-coding RNA gene', 'A gene that produces an RNA transcript that functions as the gene product.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '4', db.id, 'rRNA gene', 'A gene that encodes ribosomal RNA.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '5', db.id, 'tRNA gene', 'A gene that encodes Transfer RNA.  ' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '6', db.id, 'snRNA gene', 'A gene that encodes a Small Nuclear RNA.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '7', db.id, 'snoRNA gene', 'A gene that encodes for Small Nucleolar RNA.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '8', db.id, 'miRNA gene', 'A gene that encodes for microRNA.  ' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '9', db.id, 'scRNA gene', 'A gene that encodes for Small Cytoplasmic RNA.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '10', db.id, 'lincRNA gene', 'A gene that encodes large intervening non-coding RNA.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '11', db.id, 'RNase P RNA gene', 'A gene that encodes RNase P RNA, the RNA component of Ribonuclease P (RNase P).' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '12', db.id, 'RNase MRP RNA gene', 'A gene that encodes RNase MRP RNA.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '13', db.id, 'telomerase RNA gene', 'A non-coding RNA gene, the RNA product of which is a component of telomerase.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '14', db.id, 'unclassified non-coding RNA gene', 'A non-coding RNA gene not classified The RNA product of this non-coding is a component of telomerase.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '15', db.id, 'heritable phenotypic marker', 'A biological region characterized as a single heritable trait in a phenotype screen. The heritable phenotype may be mapped to a chromosome but generally has not been characterized to a specific gene locus.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '16', db.id, 'gene segment', 'A gene component region which acts as a recombinational unit of a gene whose functional form is generated through somatic recombination.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '17', db.id, 'unclassified gene', 'A region of the genome associated with transcript and/or prediction evidence but where feature classification is imprecise.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '18', db.id, 'other feature types', 'MGI markers that are not classified as gene including pseudogenes, QTL, transgenes, gene clusters, cytogenetic markers, & unclassified genome features.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '19', db.id, 'QTL', 'A quantitative trait locus (QTL) is a polymorphic locus which contains alleles that differentially affect the expression of a continuously distributed phenotypic trait. Usually it is a marker described by statistical association to quantitative variation in the particular phenotypic trait that is thought to be controlled by the cumulative action of alleles at multiple loci.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '20', db.id, 'transgene', 'A gene that has been transferred naturally or by any of a number of genetic engineering techniques from one organism to another.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '21', db.id, 'complex/cluster/region', 'A group of linked markers characterized by related sequence and/or function where the precise location or identity of the individual components is obscure.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '22', db.id, 'cytogenetic marker', 'A structure within a chromosome or a chromosomal rearrangement that is visible by microscopic examination.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '23', db.id, 'chromosomal deletion', 'An incomplete chromosome.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '24', db.id, 'insertion', 'The sequence of one or more nucleotides added between two adjacent nucleotides in the sequence.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '25', db.id, 'chromosomal inversion', 'An interchromosomal mutation where a region of the chromosome is inverted with respect to wild type.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '26', db.id, 'Robertsonian fusion', 'A non reciprocal translocation whereby the participating chromosomes break at their centromeres and the long arms fuse to form a single chromosome with a single centromere.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '27', db.id, 'reciprocal chromosomal translocation', 'A chromosomal translocation with two breaks; two chromosome segments have simply been exchanged.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '28', db.id, 'chromosomal translocation', 'An interchromosomal mutation. Rearrangements that alter the pairing of telomeres are classified as translocations.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '29', db.id, 'chromosomal duplication', 'An extra chromosome.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '30', db.id, 'chromosomal transposition', 'A chromosome structure variant whereby a region of a chromosome has been transferred to another position. Among interchromosomal rearrangements, the term transposition is reserved for that class in which the telomeres of the chromosomes involved are coupled (that is to say, form the two ends of a single DNA molecule) as in wild-type.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '31', db.id, 'unclassified cytogenetic marker', 'A cytogenetic marker not classifiable within current cytogenetic subcategories.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '32', db.id, 'BAC/YAC end', 'A region of sequence from the end of a BAC or YAC clone used as a reagent in mapping and genome assembly.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '33', db.id, 'other genome feature', 'A region of the genome associated with biological interest (includes regulatory regions, conserved regions and related sequences, repetitive sequences, and viral integrations).' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '34', db.id, 'DNA segment', 'A region of the genome associated with experimental interest, often used as a reagent for genetic mapping. Includes RFLP and other hybridization probes, sequence-tagged sites (STS), and regions defined by PCR primer pairs such as microsatellite markers).' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '35', db.id, 'pseudogenic region', 'A non-functional descendant of a functional entity.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '36', db.id, 'pseudogene', 'A sequence that closely resembles a known functional gene, at another locus within the genome, that is non-functional a consequence of (usually several) mutations that prevent either its transcription or translation (or both). In general, pseudogenes result from either reverse transcription of a transcript of their normal paralog, in which case the pseudogene typically lacks introns and includes a poly(A) tail, or from recombination, in which case the pseudogene is typically a tandem duplication of its normal paralog.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '37', db.id, 'pseudogenic gene segment', 'A recombinational unit of a gene which when incorporated by somatic recombination in the final gene transcript results in a nonfunctional product.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '38', db.id, 'polymorphic pseudogene', 'Pseudogene owing to a SNP/DIP but in other individuals/haplotypes/strains the gene is translated.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '39', db.id, 'SRP RNA gene', 'A gene that encodes signal recognition particle RNA.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';


-- it's a hack for SOLR indexing
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '40', db.id, 'unknown', 'A gene with no subtype.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';

-- Additional subtypes from MGI feature list
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '41', db.id, 'antisense lncRNA gene', 'A gene that encodes a non-coding RNA transcribed from the opposite DNA strand compared with other transcripts and overlap in part with sense RNA.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '42', db.id, 'BAC end', 'A region of sequence from the end of a BAC clone that may provide a highly specific marker.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '43', db.id, 'CpG island', 'Regions of a few hundred to a few thousand bases in vertebrate genomes that are relatively GC and CpG rich; they are typically unmethylated and often found near the 5'' ends of genes.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '44', db.id, 'endogenous retroviral region', 'A region derived from viral infection of germ cells that has been stably integrated into the host genome and is passed on from generation to generation.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '45', db.id, 'intronic lncRNA gene', 'A gene that encodes a lncRNA totally contained within an intron.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '46', db.id, 'lncRNA gene', 'A gene that encodes a non-coding RNA over 200 nucleotides in length' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '47', db.id, 'minisatellite', 'A repeat region containing tandemly repeated sequences having a unit length of 10 to 40 bp.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '48', db.id, 'mutation defined region', 'A genomic region, containing multiple genes/genome features, within which a mutation event resulted in complex genomic changes affecting multiple features (e.g. not a simple regional deletion).' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '49', db.id, 'PAC end', 'A region of sequence from the end of a PAC clone that may provide a highly specific marker.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '50', db.id, 'promoter', 'A regulatory_region composed of the TSS(s) and binding sites for TF_complexes of the basal transcription machinery.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '51', db.id, 'retrotransposon', 'A transposable element that is incorporated into a chromosome by a mechanism that requires reverse transcriptase.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '52', db.id, 'telomere', 'A specific structure at the end of a linear chromosome, required for the integrity and maintenance of the end.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '53', db.id, 'unclassified other genome feature', 'A genome feature that cannot be classified in any currently recognized genome category.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '54', db.id, 'YAC end', 'A region of sequence from the end of a YAC clone that may provide a highly specific marker.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';
INSERT INTO ontology_term(acc, db_id, name, description) SELECT '55', db.id, 'ribozyme gene', 'A gene that encodes an RNA with catalytic activity.' FROM external_db db WHERE db.name = 'MGI Genome Feature Type' AND version = 'JAX';



/*
** FOR ALLELE TYPES LOADING (from AdminTools mgi_allele_types.sql)
*/
/**
 * MGI allele type
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
 ** MGI strain classification
 */
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000021', 3, 'coisogenic', 'coisogenic strain');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000022', 3, 'congenic', 'congenic strain');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000023', 3, 'conplastic', 'conplastic strain');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000024', 3, 'consomic', 'consomic strain');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000025', 3, 'inbred strain', 'inbred strain');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000026', 3, 'recombinant congenic (RC)', 'recombinant congenic (RC)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000027', 3, 'recombinant inbred (RI)', 'recombinant inbred (RI)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000028', 3, 'mutant strain', 'mutant strain as defined in IMSR');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000029', 3, 'unclassified', 'unclassified');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000030', 3, 'segregating inbred', 'segregating inbred as defined in IMSR');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000031', 3, 'mutant stock', 'mutant stock as defined in IMSR');

INSERT INTO ontology_term(acc, db_id, name, description) VALUES('CV:00000050', 3, 'EuroPhenome mutant strain', 'mutant strain as defined in EuroPhenome');
INSERT INTO ontology_term (acc, db_id, name, description)
VALUES ('CV:00000051', 3, 'IMPC uncharacterized background strain', 'background strain used in IMPC');



/*
** FOR META INFO LOADING
*/
insert into meta_info(property_key, property_value, description) values('code_version', 'beta-release-0_1_4', '');
insert into meta_info(property_key, property_value, description) values('assembly_version', 'GRCm38', 'Genome Reference Consortium GRCm38');
insert into meta_info(property_key, property_value, description) values('species', 'Mus musculus', 'Mus musculus phenotype database');


/*
** FOR HIGHER LEVEL ANNOTATION LOADING
 */
INSERT INTO higher_level_annotation VALUES
	('MA:0000004')
	,('MA:0000007')
	,('MA:0000009')
	,('MA:0000010')
	,('MA:0002418')
	,('MA:0000012')
	,('MA:0002711')
	,('MA:0002411')
	,('MA:0000014')
	,('MA:0000016')
	,('MA:0000327')
	,('MA:0002431')
	,('MA:0000325')
	,('MA:0000326')
	,('MA:0000017')
	,('MA:0002887') 				-- set of connective tissues
;

INSERT INTO higher_level_annotation VALUES
	('EMAPA:16104')        -- cardiovascular system
	,('EMAPA:16192')        -- sensory organ system
	,('EMAPA:16246')        -- alimentary system
	,('EMAPA:16405')        -- limb
	,('EMAPA:16469')        -- nervous system
	,('EMAPA:16727')        -- respiratory system
	,('EMAPA:16748')        -- tail
	,('EMAPA:16840')        -- liver and biliary system
	,('EMAPA:17524')        -- integumental system
	,('EMAPA:31858')        -- head
;



/*
** FOR IMPC STATUS CODE LOADING
 */
/**
 * status code information about experiments, procedures or parameters from the DCC
 */
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PSC_001', 22, 'Mouse died', 'Found dead');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_001', 22, 'Mouse died', 'Found dead');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PSC_002', 22, 'Mouse culled', 'Culled for welfare reasons');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_002', 22, 'Mouse culled', 'Culled for welfare reasons');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PSC_003', 22, 'Single procedure not performed - welfare', 'Because of animal welfare');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_003', 22, 'Single procedure not performed - welfare', 'Because of animal welfare');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PSC_004', 22, 'Single procedure not performed - schedule', 'Mouse missed test date, e.g. no phenotyper available');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_004', 22, 'Single procedure not performed - schedule', 'Mouse missed test date, e.g. no phenotyper available');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PSC_005', 22, 'Pipeline stopped - welfare', 'Because of animal welfare');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_005', 22, 'Pipeline stopped - welfare', 'Because of animal welfare');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PSC_006', 22, 'Pipeline stopped - scheduling', 'Mouse missed more than one test so pipeline stopped');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_006', 22, 'Pipeline stopped - scheduling', 'Mouse missed more than one test so pipeline stopped');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PSC_007', 22, 'Procedure Failed - Equipment Failed', 'Issue with the phenotyping equipment, hardware/software (but not LIMS)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_007', 22, 'Procedure Failed - Equipment Failed', 'Issue with the phenotyping equipment, hardware/software (but not LIMS)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PSC_008', 22, 'Procedure Failed - Sample Lost', 'Procedure Failed - Sample Lost');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_008', 22, 'Procedure Failed - Sample Lost', 'Procedure Failed - Sample Lost');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PSC_009', 22, 'Procedure Failed - Insufficient Sample', 'Error/failure in any step of the procedure (e.g.: glucose injection in IPGTT gone wrong)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_009', 22, 'Procedure Failed - Insufficient Sample', 'Error/failure in any step of the procedure (e.g.: glucose injection in IPGTT gone wrong)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PSC_010', 22, 'Procedure Failed - Process Failed', 'Error/failure in any step of the procedure (e.g.: glucose injection in IPGTT gone wrong)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_010', 22, 'Procedure Failed - Process Failed', 'Error/failure in any step of the procedure (e.g.: glucose injection in IPGTT gone wrong)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_011', 22, 'Procedure QC Failed', 'Procedure QC Failed');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_012', 22, 'LIMS not ready yet', 'LIMS is undergoing updates and is unable to collect the data, for the moment; e.g.: manage a mandatory procedure still not yet added to the LIMS');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_013', 22, 'Software failure', 'LIMS says no');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_014', 22, 'Uncooperative mouse', 'Mouse is refusing to comply to the test (e.g.: not griping the grid in Grip Strength)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PSC_015', 22, 'Free Text of Issues', 'Info about a procedure that could not be completed for any other reason (submitted like IMPC_PSC_015:laboratory destroyed by flood)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PARAMSC_001', 22, 'Parameter not measured - Equipment Failed', 'Issue with the phenotyping equipment, hardware/software (but not LIMS)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_001', 22, 'Parameter not measured - Equipment Failed', 'Issue with the phenotyping equipment, hardware/software (but not LIMS)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PARAMSC_002', 22, 'Parameter not measured - Sample Lost', 'Parameter not measured - Sample Lost');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_002', 22, 'Parameter not measured - Sample Lost', 'Parameter not measured - Sample Lost');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PARAMSC_003', 22, 'Parameter not measured - Insufficient sample', 'Blood sample was enough for one Clinical chemistry but not for Hematology');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_003', 22, 'Parameter not measured - Insufficient sample', 'Blood sample was enough for one Clinical chemistry but not for Hematology');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PARAMSC_004', 22, 'Parameter not recorded - welfare issue', 'Single parameter not recorded because of mouse welfare reasons');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_004', 22, 'Parameter not recorded - welfare issue', 'Single parameter not recorded because of mouse welfare reasons');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PARAMSC_005', 22, 'Free Text of Issues', 'Info about a parameter e.g the mouse moved, the power went off (submitted like IMPC_PARAMSC_004:the mouse moved)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_005', 22, 'Free Text of Issues', 'Info about a parameter e.g the mouse moved, the power went off (submitted like IMPC_PARAMSC_004:the mouse moved)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PARAMSC_006', 22, 'Extra Information', 'e.g. For media parameters a link to the parameter associated with the picture (submitted like IMPC_PARAMSC_006: IMPC_013_001_014)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_006', 22,  'Extra Information', 'e.g. For media parameters a link to the parameter associated with the picture (submitted like IMPC_PARAMSC_006: IMPC_013_001_014)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('ESLIM_PARAMSC_007', 22,'Parameter not measured - not in SOP', 'Not in center specific SOP at time of measurement');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_007', 22, 'Parameter not measured - not in SOP', 'Not in center specific SOP at time of measurement');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_008', 22, 'Above upper limit of quantitation', 'Above upper limit of quantitation');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_009', 22, 'Below lower limit of quantitation', 'Below lower limit of quantitation');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_010', 22, 'Parameter QC Failed', 'Parameter QC Failed');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_011', 22, 'LIMS not ready yet', 'LIMS is undergoing updates and is unable to collect the data, for the moment; e.g.: manage mandatory parameters added to procedure after experiment performed but before data exported');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_012', 22, 'Software failure', 'LIMS says no');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_013', 22, 'Uncooperative mouse', 'Mouse is refusing to comply to the test (e.g.: not griping the grid in Grip Strength)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_014', 22, 'Parameter not measured - Equipment Incompatible', 'The phenotyping equipment can not be used to measure a mandatory parameter');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_PARAMSC_016', 22, 'LIMS error', 'LIMS error resulted in erroneous data submission');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_SSC_001', 22, 'Genotyping failed', 'Genotyping failed');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_SSC_002', 22, 'Health Issue', 'Mouse withdrawn because of health issue not related to the targeted mutation; all data to be removed; (ex: health issue discovered during gross pathology, justifying rare values throughout the pipeline)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_SSC_003', 22, 'Free Text of Issues', 'Info about a specimen that was removed, not covered by the other specimen status codes; e.g.: duplicated specimen entries (submitted like IMPC_SSC_003:duplicated specimen entry)');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPC_SSC_004', 22, 'LIMS error', 'LIMS error resulted in erroneous data submission');



/*
 ** LIFE STAGE
 */
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPCLS:0001', 22, 'E9.5', 'Embryonic day 9.5');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPCLS:0002', 22, 'E12.5', 'Embryonic day 12.5');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPCLS:0003', 22, 'E15.5', 'Embryonic day 15.5');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPCLS:0004', 22, 'E18.5', 'Embryonic day 18.5');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPCLS:0005', 22, 'Early adult', 'Time period less than 16 weeks of age');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPCLS:0006', 22, 'Middle aged adult', 'Time period between 16 and 48 weeks of age');
INSERT INTO ontology_term(acc, db_id, name, description) VALUES('IMPCLS:0007', 22, 'Late adult', 'Time period greater than 48 weeks of age');


/*
** Add hand-curated Europhenome colony ids to phenotyped_colony.
 */

SET @MGI_DB_ID=3;

SET @ORGANISATION_ID_WTSI=3;
SET @ORGANISATION_ID_MRCHARWELL=7;
SET @ORGANISATION_ID_ICS=8;
SET @ORGANISATION_ID_HMGU=9;
SET @ORGANISATION_ID_CMHD=23;

SET @EUMODIC_PROJECT_ID=1;
SET @IMPC_PROJECT_ID=2;
SET @MGP_PROJECT_ID=3;
SET @THREEI_PROJECT_ID=4;


INSERT INTO `phenotyped_colony` (`colony_name`, `es_cell_name`, `gf_acc`, `gf_db_id`, `allele_symbol`, `background_strain_name`, `production_centre_organisation_id`, `production_consortium_project_id`, `phenotyping_centre_organisation_id`, `phenotyping_consortium_project_id`)
VALUES

	-- MGP (THREE I) manually curated colonyIds from MNG 20181011

	('MAGG','EPD0027_4_C08','MGI:2443514',(SELECT @MGI_DB_ID),'Wdhd1<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MBEK','EPD0025_2_A04','MGI:1930751',(SELECT @MGI_DB_ID),'Trpc4ap<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MBKQ','EPD0025_6_B04','MGI:1347045',(SELECT @MGI_DB_ID),'Psmb2<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MBQF','EPD0095_2_E05','MGI:2384590',(SELECT @MGI_DB_ID),'Ndrg4<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MBRK','EPD0126_4_F03','MGI:1194910',(SELECT @MGI_DB_ID),'Rbbp7<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MBTQ','EPD0130_2_C06','MGI:1202879',(SELECT @MGI_DB_ID),'Tcf7l2<tm2a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCBG','EPD0116_2_B07','MGI:2155959',(SELECT @MGI_DB_ID),'Lnx2<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCDB','EPD0129_4_D04','info not available',(SELECT @MGI_DB_ID),'6530401N04Rik<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCFF','EPD0157_6_B09','MGI:1918779',(SELECT @MGI_DB_ID),'Kazn<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCGY','EPD0102_1_D08','MGI:1858696',(SELECT @MGI_DB_ID),'Copg1<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MAPB','EPD0162_2_H08','MGI:1928396',(SELECT @MGI_DB_ID),'Pdcd10<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MARW','EPD0197_3_C08','MGI:1921593',(SELECT @MGI_DB_ID),'Crtc2<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('CRET','1','MGI:96217',(SELECT @MGI_DB_ID),'Hprt<Tg(CMV-Cre)Brd>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MHHK','DC0348','MGI:1333803',(SELECT @MGI_DB_ID),'Top3b<gt(DC0348)SIGTR>','129P2/OlaHsdWtsi',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MAXH','EPD0200_5_G01','MGI:894689',(SELECT @MGI_DB_ID),'Ywhae<tm1e(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MAYT','EPD0027_4_H10','MGI:2446244',(SELECT @MGI_DB_ID),'Setd1a<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCHV','EPD0187_2_A09','MGI:1859854',(SELECT @MGI_DB_ID),'Rcor2<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCJB','EPD0169_4_D02','MGI:1858416',(SELECT @MGI_DB_ID),'Stk39<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCLB','EPD0203_5_C05','MGI:107170',(SELECT @MGI_DB_ID),'Ptpn22<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCLK','EPD0377_5_A10','MGI:96113',(SELECT @MGI_DB_ID),'Hmgb1<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCLQ','EPD0369_5_A05','MGI:1353495',(SELECT @MGI_DB_ID),'Slc25a4<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCQW','EPD0412_2_C09','MGI:103099',(SELECT @MGI_DB_ID),'Cox6a1<tm1(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCSE','EPD0424_5_A05','MGI:88385',(SELECT @MGI_DB_ID),'Cfh<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCSF','EPD0389_5_A05','MGI:2384892',(SELECT @MGI_DB_ID),'Rhot2<tm1(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCSV','EPD0051_2_A09','MGI:2154239',(SELECT @MGI_DB_ID),'Plxnb2<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCTH','EPD0381_2_B07','MGI:104510',(SELECT @MGI_DB_ID),'Myo7a<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCUD','EPD0105_5_C02','MGI:2442191',(SELECT @MGI_DB_ID),'Dusp4<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCUL','EPD0413_2_A01','MGI:1925055',(SELECT @MGI_DB_ID),'Esco1<tm1(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCVX','EPD0147_4_G08','MGI:2159711',(SELECT @MGI_DB_ID),'Usp33<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCWA','EPD0157_3_B05','MGI:1915399',(SELECT @MGI_DB_ID),'Otub2<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCWG','EPD0156_3_A07','info not available',(SELECT @MGI_DB_ID),'Rqcd1<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCXG','EPD0144_3_A09','MGI:1913491',(SELECT @MGI_DB_ID),'Tmem9<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCXW','EPD0164_1_G10','MGI:3035141',(SELECT @MGI_DB_ID),'Slc44a5<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCYN','EPD0554_4_A09','MGI:2151045',(SELECT @MGI_DB_ID),'Lsm10<tm2a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCYQ','EPD0553_2_A09','MGI:1345150',(SELECT @MGI_DB_ID),'Cdc6<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCYY','EPD0553_3_A01','MGI:1919563',(SELECT @MGI_DB_ID),'Fryl<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCYZ','EPD0553_3_A07','MGI:103073',(SELECT @MGI_DB_ID),'Zp1<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCZC','EPD0552_1_A11','MGI:2685918',(SELECT @MGI_DB_ID),'Lpar5<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MJBA','B15-5','MGI:104735',(SELECT @MGI_DB_ID),'Gt(ROSA)26Sor<CreERT2>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MCZW','EPD0489_1_C06','MGI:1201607',(SELECT @MGI_DB_ID),'Blzf1<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDAR','EPD0463_1_A11','MGI:1921257',(SELECT @MGI_DB_ID),'Btbd11<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDAW','EPD0547_5_C04','MGI:1346322',(SELECT @MGI_DB_ID),'Gpc6<tm2a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDAY','EPD0147_2_E01','MGI:1914485',(SELECT @MGI_DB_ID),'Zkscan14<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDBV','EPD0076_1_F07','MGI:109234',(SELECT @MGI_DB_ID),'Kif21b<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDCC','EPD0555_1_E10','MGI:2385957',(SELECT @MGI_DB_ID),'Mfrp<tm2a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDCU','EPD0452_5_E06','MGI:2442403',(SELECT @MGI_DB_ID),'Stk32a<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDCW','EPD0079_5_G07','MGI:3611446',(SELECT @MGI_DB_ID),'Rapgefl1<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDDA','EPD0073_2_C09','info not available',(SELECT @MGI_DB_ID),'A430084P05Rik<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDDE','EPD0538_1_D06','MGI:2445077',(SELECT @MGI_DB_ID),'Lacc1<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDDF','EPD0176_4_A01','MGI:1921263',(SELECT @MGI_DB_ID),'Rftn2<tm1e(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDDM','EPD0272_2_D07','MGI:102518',(SELECT @MGI_DB_ID),'Adam3<tm1(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDDT','EPD0177_4_C08','MGI:2385900',(SELECT @MGI_DB_ID),'Ccdc106<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDDZ','EPD0300_3_C05','MGI:2156774',(SELECT @MGI_DB_ID),'Stard6<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDEK','EPD0319_2_E12','info not available',(SELECT @MGI_DB_ID),'1190002H23Rik<tm1(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDFG','EPD0188_1_C02','MGI:1922973',(SELECT @MGI_DB_ID),'Amotl1<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDFL','EPD0175_1_A02','MGI:2676551',(SELECT @MGI_DB_ID),'Plekhg1<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDFQ','EPD0188_2_A02','MGI:2684854',(SELECT @MGI_DB_ID),'Slc25a43<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDJP','EPD0453_4_B09','MGI:98905',(SELECT @MGI_DB_ID),'Usp4<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDKR','EPD0409_6_A03','MGI:1919238',(SELECT @MGI_DB_ID),'Esco2<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MDLM','EPD0156_6_F07','MGI:2685873',(SELECT @MGI_DB_ID),'Mroh7<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MEHN','EPD0370_4_C06','MGI:107605',(SELECT @MGI_DB_ID),'Npat<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MEQQ','EPD0657_1_D01','MGI:1924876',(SELECT @MGI_DB_ID),'Smpd4<tm2a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MFQN','EPD0816_5_E09','MGI:1097696',(SELECT @MGI_DB_ID),'Rem1<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MFRY','EPD0431_5_B07','MGI:109327',(SELECT @MGI_DB_ID),'Bnip2<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MGCM','EPD0530_6_A08','MGI:109433',(SELECT @MGI_DB_ID),'Oaz1<tm2e(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MGHP','EPD0925_2_A10','MGI:1916632',(SELECT @MGI_DB_ID),'1700024P04Rik<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MGKV','EPD0199_1_A12','MGI:1889850',(SELECT @MGI_DB_ID),'Gsdme<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MGRB','EPD0208_6_A02','MGI:1097714',(SELECT @MGI_DB_ID),'Bhlhe40<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MGWG','HEPD0948_5_A10','MGI:88582',(SELECT @MGI_DB_ID),'Cyp11a1<tm1a(EUCOMM)Hmgu>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MHDC','EPD0688_2_B06','MGI:1918764',(SELECT @MGI_DB_ID),'Sfpq<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MHFK','HEPD0724_3_H06','MGI:88516',(SELECT @MGI_DB_ID),'Cryab<tm1a(EUCOMM)Hmgu>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),
	('MHFV','HEPD0572_8_F01','MGI:2384974',(SELECT @MGI_DB_ID),'Flvcr2<tm1a(EUCOMM)Hmgu>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @THREEI_PROJECT_ID)),


	-- EuroPhenome manually curated colonyIDs
	('EPD0038_2_B10_10026',NULL,'MGI:98970',(SELECT @MGI_DB_ID),'Xbp1<tm1a(EUCOMM)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('DNase-X',NULL,'MGI:109628',(SELECT @MGI_DB_ID),'Dnase1l1<tm1Dkfz>','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('CMHD-TM_N00324P0_T20D4',NULL,'MGI:2158736',(SELECT @MGI_DB_ID),'Senp3<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('Wrnip1tm1a KO',NULL,'MGI:1926153',(SELECT @MGI_DB_ID),'Wrnip1<tm1a(EUCOMM)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Vwde',NULL,'MGI:2685313',(SELECT @MGI_DB_ID),'Vwde<tm1Icmb>','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EUC0050E03',NULL,'MGI:87880',(SELECT @MGI_DB_ID),'Aco2<Gt(EUC0050e03)Hmgu>','129P2/OlaHsd',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0038_2_B10_10557',NULL,'MGI:98970',(SELECT @MGI_DB_ID),'Xbp1<tm1a(EUCOMM)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Tmc1Tmc1<dn>',NULL,'MGI:2151016',(SELECT @MGI_DB_ID),'Tmc1<dn>','STOCK Tmc1<dn>',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0002_2_B07',NULL,'MGI:2150037',(SELECT @MGI_DB_ID),'Mta1<tm1a(EUCOMM)Wtsi>','129/SvEv',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Magi2Magi2<tm1Grnt>',NULL,'MGI:1354953',(SELECT @MGI_DB_ID),'Magi2<tm1Grnt>','129S5;129P2',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('#4',NULL,'MGI:104735',(SELECT @MGI_DB_ID),'Gt(ROSA)26Sor<tm1(FLP1)Dym>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Prkab1Prkab1<Gt(RRR454)Byg>',NULL,'MGI:1336167',(SELECT @MGI_DB_ID),'Prkab1<Gt(RRR454)Byg>','CBA/Ca;129P2',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Arl4',NULL,'MGI:99437',(SELECT @MGI_DB_ID),'Arl4a<tm1Asch>','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EUC0027G03',NULL,'MGI:2142567',(SELECT @MGI_DB_ID),'Arhgef18<Gt(EUC0027g03)Hmgu>','129P2/OlaHsd',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Nox4',NULL,'MGI:1354184',(SELECT @MGI_DB_ID),'Nox4<tm1.1Hwsc>','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Asxl tm1a KO',NULL,'MGI:2684063',(SELECT @MGI_DB_ID),'Asxl1<tm1a(EUCOMM)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Matn1Matn1<tm1Goe>',NULL,'MGI:106591',(SELECT @MGI_DB_ID),'Matn1<tm1Goe>','C57BL/6JTyr;C57BL/6;129S5',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0059_3_A06',NULL,'MGI:1914084',(SELECT @MGI_DB_ID),'Acot13<tm1a(EUCOMM)Wtsi>','C57BL/6NTacDen',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('B6.Cg-Sfrp2<I153N>/H',NULL,'MGI:108078',(SELECT @MGI_DB_ID),'Sfrp2<I153N>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Elk4Elk4<tm1a(EUCOMM)Wtsi/H>',NULL,'MGI:102853',(SELECT @MGI_DB_ID),'Elk4<tm1a(EUCOMM)Wtsi>','C57BL/6',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Ren1Ren1<Ren-1c Enhancer KO>',NULL,'MGI:97898',(SELECT @MGI_DB_ID),'Ren1<Ren-1c Enhancer KO>','C57BL/6JIco',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('CMHD-TM_N00020P0_T5C11',NULL,'MGI:2652885',(SELECT @MGI_DB_ID),'Tbc1d2<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('EUCE0233_A03',NULL,'MGI:102790',(SELECT @MGI_DB_ID),'Rab18<Gt(EUCE0233a03)Hmgu>','129P2/OlaHsd',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('HEPD0634_1_D03',NULL,'MGI:97005',(SELECT @MGI_DB_ID),'Mmp12<tm1a(EUCOMM)Hmgu>','C57BL/6NTac',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('C3H.Cg-Fbxo11<Jf>/H',NULL,'MGI:2147134',(SELECT @MGI_DB_ID),'Fbxo11<Jf>','C3H/HeH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('B6Dnk;129P2-HBP1<Gt(EUCJ0053g09)>/H',NULL,'MGI:894659',(SELECT @MGI_DB_ID),'Hbp1<Gt(EUCJ0053g09)>Hmgu','C57BL/6Dnk',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('CMHD-TM_N01174P0_T28D11',NULL,'MGI:1915987',(SELECT @MGI_DB_ID),'Angel1<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('HST012',NULL,'MGI:102674',(SELECT @MGI_DB_ID),'Umod<urehr4>','C3HeB/FeJ',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EUC0054A05',NULL,'MGI:2137706',(SELECT @MGI_DB_ID),'Actn1<Gt(EUC0054a05)Hmgu>','129P2/OlaHsd',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('129-Smo<tm1Amc>/H',NULL,'MGI:108075',(SELECT @MGI_DB_ID),'Smo<tm1Amc>','129S9/SvEvH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Myo7aMyo7a<Hdb>',NULL,'MGI:104510',(SELECT @MGI_DB_ID),'Myo7a<Hdb>','C3HeB/FeJ',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Ankrd13aAnkrd13a<Gt(RRH308)Byg>',NULL,'MGI:1915670',(SELECT @MGI_DB_ID),'Ankrd13a<Gt(RRH308)Byg>','CBA/Ca;129P2',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('C57BL/6J-Ucp1<m1H>/H',NULL,'MGI:98894',(SELECT @MGI_DB_ID),'Ucp1<m1H>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('EUCJ0004_F10',NULL,'MGI:97316',(SELECT @MGI_DB_ID),'Nfya<Gt(EUCJ0004f10)Hmgu>','C57BL/6NTacDen',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('HST009',NULL,'MGI:103150',(SELECT @MGI_DB_ID),'Slc12a1<urehr3>','C3HeB/FeJ',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('CMHD-TM_N00006P0_T6F5',NULL,'MGI:1917274',(SELECT @MGI_DB_ID),'Mcm10<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('DLG3',NULL,'MGI:1353651',(SELECT @MGI_DB_ID),'Gnl3<Gt(W062C05)Wrst>','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0001_3_E04',NULL,'MGI:1349766',(SELECT @MGI_DB_ID),'Brd7<tm3a(EUCOMM)Wtsi>','129/SvEv',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('EUCJ0079D10',NULL,'MGI:1201409',(SELECT @MGI_DB_ID),'Pknox1<Gt(EUCJ0079d10)Hmgu>','C57BL/6NTacDen',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('C3H.Cg-Ankrd11<Yod>/H',NULL,'MGI:1924337',(SELECT @MGI_DB_ID),'Ankrd11<Yod>','C3H/HeH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('HEPD0527_5_A04',NULL,'MGI:2444491',(SELECT @MGI_DB_ID),'Heatr3<tm1a(EUCOMM)Hmgu>','C57BL/6NTac',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Ptdsr',NULL,'MGI:1858910',(SELECT @MGI_DB_ID),'Jmjd6<tm1.1Gbf>','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Rassf1Rassf1<tm1.2Brd>',NULL,'MGI:1928386',(SELECT @MGI_DB_ID),'Rassf1<tm1.2Brd>','C57BL/6JTyr;129S5',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('clone 1',NULL,'MGI:104874',(SELECT @MGI_DB_ID),'Akt2<tm1Wcs>','129/SvEv',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('129/SvEv-Gnas<tm1Jop>/H',NULL,'MGI:95777',(SELECT @MGI_DB_ID),'Gnas<tm1Jop>','129S9/SvEvH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('ALI14',NULL,'MGI:97616',(SELECT @MGI_DB_ID),'Plcg2<Ali14>','C3HeB/FeJ',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Miz1',NULL,'MGI:1096566',(SELECT @MGI_DB_ID),'Pias2<Gt(pT1Betageo)1Ruiz>','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0019_1_D07',NULL,'MGI:2444748',(SELECT @MGI_DB_ID),'Chd7<tm2a(EUCOMM)Wtsi>','129S5;C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Ube3bUbe3b<Gt(RRJ142)Byg>',NULL,'MGI:1891295',(SELECT @MGI_DB_ID),'Ube3b<Gt(RRJ142)Byg>','CBA/Ca;129P2',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('B6.129P2-Mro<tm1H>/H',NULL,'MGI:2152817',(SELECT @MGI_DB_ID),'Mro<tm1H>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Cidec tm1a KO',NULL,'MGI:95585',(SELECT @MGI_DB_ID),'Cidec<tm1a(EUCOMM)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('CMHD-TM_N00135P0_T22G9',NULL,'MGI:2654703',(SELECT @MGI_DB_ID),'Otud7b<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('C3H.Cg-Ptk7<chz>/H',NULL,'MGI:1918711',(SELECT @MGI_DB_ID),'Ptk7<chz>','C3H/HeH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('CMHD-TM_N00016P0_T5D5',NULL,'MGI:1916960',(SELECT @MGI_DB_ID),'Arap1<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('B6.Cg-Sfrp5<Q27STOP>/H',NULL,'MGI:1860298',(SELECT @MGI_DB_ID),'Sfrp5<Q27STOP>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('ApoeApoe<tm1Unc>',NULL,'MGI:88057',(SELECT @MGI_DB_ID),'Apoe<tm1Unc>','C57BL/6',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('HEPD0527_4_D08',NULL,'MGI:1350921',(SELECT @MGI_DB_ID),'Fkbp9<tm1a(EUCOMM)Hmgu>','C57BL/6NTac',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('B6.129-Ptch1<tm1Mps>/JH',NULL,'MGI:105373',(SELECT @MGI_DB_ID),'Ptch1<tm1Mps>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('129S9/SvEvH-Nespas<tm3Jop>/H',NULL,'MGI:1861674',(SELECT @MGI_DB_ID),'Gnas/Nespas<tm2.1Jop>','129S9/SvEvH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('C.Cg-Bfc/H',NULL,'MGI:88276',(SELECT @MGI_DB_ID),'Ctnnb1<Bfc>','BALB/cAnNCrl',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('C.Cg-Celsr1<Crsh>/H',NULL,'MGI:1100883',(SELECT @MGI_DB_ID),'Celsr1<Crsh>','BALB/cAnNCrl',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('EUC0047A08',NULL,'MGI:1917329',(SELECT @MGI_DB_ID),'Golm1<Gt(EUC0047a08)Hmgu>','129P2/OlaHsd',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0002_3_G03',NULL,'MGI:1278322',(SELECT @MGI_DB_ID),'Epc1<tm1a(EUCOMM)Wtsi>','129/SvEv',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('C3H.A-Vangl2<Lp>/H',NULL,'MGI:2135272',(SELECT @MGI_DB_ID),'Vangl2<Lp>','C3H/HeH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('NCAM1',NULL,'MGI:97281',(SELECT @MGI_DB_ID),'Ncam1<tm1Cgn>','C57BL/6JIco',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Deltagen_407',NULL,'MGI:2449205',(SELECT @MGI_DB_ID),'Nr1d2<tm1Dgen>','B6J.129S2',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('N00007P0_T5B3',NULL,'MGI:88255',(SELECT @MGI_DB_ID),'Anxa6<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('CMHD-TM_N01605P1_T53C4',NULL,'MGI:2685003',(SELECT @MGI_DB_ID),'Zbtb45<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('Grin2aGrin2a<tm1Rsp>',NULL,'MGI:95820',(SELECT @MGI_DB_ID),'Grin2a<tm1Rsp>','C57BL/6JIco;129P2',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('CMHD-TM_N01539P1_T51C7',NULL,'MGI:1922075',(SELECT @MGI_DB_ID),'Senp6<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('Eya3',NULL,'MGI:109339',(SELECT @MGI_DB_ID),'Eya3<Gt(W096D02)Wrst>','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('CMHD-TM_N00086P0_T24C7',NULL,'MGI:1914737',(SELECT @MGI_DB_ID),'Dhx40<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0001_3_G07',NULL,'MGI:98809',(SELECT @MGI_DB_ID),'Tpm1<tm1a(EUCOMM)Wtsi>','129/SvEv',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Aldh2Aldh2<tm1a(EUCOMM)Wtsi/Hmgu>',NULL,'MGI:99600',(SELECT @MGI_DB_ID),'Aldh2<tm1a(EUCOMM)Wtsi>','C57BL/6',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Nr1i2',NULL,'MGI:1337040',(SELECT @MGI_DB_ID),'Nr1i2','B6J.129S2',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Bin1_mut1',NULL,'MGI:108092',(SELECT @MGI_DB_ID),'Bin1_mut1','B6N.B6J.129S2',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Bin1_mut2',NULL,'MGI:108092',(SELECT @MGI_DB_ID),'Bin1_mut2','B6N.129S2.B6J',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Nr1h4',NULL,'MGI:1352464',(SELECT @MGI_DB_ID),'Nr1h4','B6J.129S2',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Nr6a1',NULL,'MGI:1352459',(SELECT @MGI_DB_ID),'Nr6a1','B6J.129S2',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('EUCJ004_F10',NULL,'MGI:97316',(SELECT @MGI_DB_ID),'Nfya','C57BL/6NTac',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Snx5',NULL,'MGI:1916428',(SELECT @MGI_DB_ID),'Snx5','C57BL/6NTac',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('NR1D1',NULL,'MGI:2444210',(SELECT @MGI_DB_ID),'Nr1d1','B6J.129S2',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Stard3',NULL,'MGI:1929618',(SELECT @MGI_DB_ID),'Stard3','B6N.129S2.B6J',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('STARD3NL',NULL,'MGI:1923455',(SELECT @MGI_DB_ID),'STARD3NL','B6N.129S2.B6J',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Emp3',NULL,'MGI:1098729',(SELECT @MGI_DB_ID),'Emp3','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Ncor2',NULL,'MGI:1337080',(SELECT @MGI_DB_ID),'Ncor2','B6J.129S2',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Strap',NULL,'MGI:1329037',(SELECT @MGI_DB_ID),'Strap','B6J.B6N',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Gbx1',NULL,'MGI:95667',(SELECT @MGI_DB_ID),'Gbx1','C57BL/6J',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Tbp2',NULL,'MGI:2684058',(SELECT @MGI_DB_ID),'Tbp2','B6J.129S2.B6N',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('March 7 targeted gene trap Cross 1',NULL,'MGI:1931053',(SELECT @MGI_DB_ID),'March7','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Nr2b3',NULL,'MGI:98216',(SELECT @MGI_DB_ID),'Rxrg','B6J.129S2',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Gpt2',NULL,'MGI:1915391',(SELECT @MGI_DB_ID),'Gpt2','C57BL/6NTac',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Sighted C3H',NULL,'MGI:97525',(SELECT @MGI_DB_ID),'Pde6b','C3H/HeH',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Nr3c2',NULL,'MGI:99459',(SELECT @MGI_DB_ID),'Nr3c2','B6J.129S2',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Net',NULL,'MGI:101762',(SELECT @MGI_DB_ID),'Elk3','B6J.B6N',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Aste1',NULL,'MGI:1913845',(SELECT @MGI_DB_ID),'Aste1','C57BL/6NTac',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Nr0b2',NULL,'MGI:1346344',(SELECT @MGI_DB_ID),'Nr0b2','B6J.129S2',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Ttll12',NULL,'MGI:3039573',(SELECT @MGI_DB_ID),'Ttll12','B6J.B6N',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('PJA1',NULL,'MGI:1101765',(SELECT @MGI_DB_ID),'PJA1','C57BL/6J',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Dnm2',NULL,'MGI:109547',(SELECT @MGI_DB_ID),'Dnm2','Balb/c.129S2',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Insl5',NULL,'MGI:1346085',(SELECT @MGI_DB_ID),'Insl5','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0518_6_B02',NULL,'MGI:95405',(SELECT @MGI_DB_ID),'Ephx1','C57BL/6NTac',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Rere',NULL,'MGI:2683486',(SELECT @MGI_DB_ID),'Rere','B6J.B6N',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('B6129S8-Tc(Hsa21)1TybEmcf/H',NULL,'MGI:3814702',(SELECT @MGI_DB_ID),'Tc(Hsa21)1TybEmcf','Stock',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('B6.Cg-cub/H',NULL,'MGI:1890156',(SELECT @MGI_DB_ID),'Stx8<tm2a(EUCOMM)Wtsi>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Dll1_C3H_113',NULL,'MGI:104659',(SELECT @MGI_DB_ID),'Dll1<tm1Gos>','C3HeB/FeJ',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Dll1_C3H_10333',NULL,'MGI:104659',(SELECT @MGI_DB_ID),'Dll1<tm1Gos>','129SvJ-Iso',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Gsk3a',NULL,'MGI:2152453',(SELECT @MGI_DB_ID),'Gsk3a<not yet available>','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Kcnip4',NULL,'MGI:1933131',(SELECT @MGI_DB_ID),'Kcnip4<not yet available>','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('March 7 KO (Cross 2)',NULL,'MGI:1931053',(SELECT @MGI_DB_ID),'MARCH7<not yet available>','C57BL''C3H.C-Mecom<Jbo>/H ''/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('C.Cg-Ostes/H',NULL,'MGI:3688249',(SELECT @MGI_DB_ID),'Ostes','BALB/cAnNCrl',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Phex',NULL,'MGI:107489',(SELECT @MGI_DB_ID),'Phex<not yet available>','C3HeB/FeJ',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('VMP1',NULL,'MGI:1923159',(SELECT @MGI_DB_ID),'Vmp1<not yet available>','C57BL/6NTacDen',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Ifitm1_1F4',NULL,'MGI:1915963',(SELECT @MGI_DB_ID),'Ifitm1<not yet assigned>','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('RHBDL5-AG2',NULL,'MGI:2442473',(SELECT @MGI_DB_ID),'Rhbdf2<not yet available>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Myo7aMyo7a<sh1-6J>',NULL,'MGI:104510',(SELECT @MGI_DB_ID),'Myo7a<sh1-6J>','AKR/J;C57BLKS/J',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Atp2b2Atp2b2<Obv>',NULL,'MGI:105368',(SELECT @MGI_DB_ID),'Atp2b2<Obv>','C3HeB/FeJ',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Dlg4Dlg4<tm1Grnt>',NULL,'MGI:1277959',(SELECT @MGI_DB_ID),'Dlg4<tm1Grnt>','C57BL/6JIco;129P2',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Ttll4Ttll4<tm1a(EUCOMM)Wtsi/H>',NULL,'MGI:1914784',(SELECT @MGI_DB_ID),'Ttll4<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('WhrnWhrn<wi>',NULL,'MGI:2682003',(SELECT @MGI_DB_ID),'Whrn<wi>','STOCK Whrn<wi>',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Git2Git2<Gt(XG510)Byg>',NULL,'MGI:1347053',(SELECT @MGI_DB_ID),'Git2<Gt(XG510)Byg>','CBA/Ca;129P2',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Secisbp2Secisbp2<tm1a(EUCOMM)Wtsi/H>',NULL,'MGI:1922670',(SELECT @MGI_DB_ID),'Secisbp2<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Chd7Chd7<Whi>',NULL,'MGI:2444748',(SELECT @MGI_DB_ID),'Chd7<Whi>','C3HeB/FeJ',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Clk1Clk1<tm1a(EUCOMM)Wtsi/Ics>',NULL,'MGI:107403',(SELECT @MGI_DB_ID),'Clk1<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Dlg2Dlg2<tm1Dsb>',NULL,'MGI:1344351',(SELECT @MGI_DB_ID),'Dlg2<tm1Dsb>','C57BL/6JIco',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Ifitm3Ifitm3<tm1Masu>',NULL,'MGI:1913391',(SELECT @MGI_DB_ID),'Ifitm3<tm1Masu>','C57BL/6JIco;C57BL/6JTyr;129P2',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Entpd1Entpd1<tm1a(EUCOMM)Wtsi/Hmgu>',NULL,'MGI:102805',(SELECT @MGI_DB_ID),'Entpd1<tm1a(EUCOMM)Wtsi>','C57BL/6',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('NfyaNfya<Gt(EUCJ0004f10)Hmgu>',NULL,'MGI:97316',(SELECT @MGI_DB_ID),'Nfya<Gt(EUCJ0004f10)Hmgu>','C57BL/6',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('129S9/SvEvH-Ablim1<tm1H>/H',NULL,'MGI:1194500',(SELECT @MGI_DB_ID),'Ablim1<tm1H>','129S9/SvEvH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('129P2/OlaHsd-Fbxl3<Gt(CB0226)Wtsi>/H',NULL,'MGI:1354702',(SELECT @MGI_DB_ID),'Fbxl3<Gt(CB0226)Wtsi>','129P2/OlaHsd',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('C3H.C-Mecom<Jbo>/H',NULL,'MGI:95457',(SELECT @MGI_DB_ID),'Mecom<Jbo>','C3H/HeH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('C3H.B6-Tulp3<hhkr>/H',NULL,'MGI:1329045',(SELECT @MGI_DB_ID),'Tulp3<hhkr>','C3H/HeH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('C3H.C-Trpc3<Mwk>/H',NULL,'MGI:109526',(SELECT @MGI_DB_ID),'Trpc3<Mwk>','C3H/HeH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('129P2/OlaHsd-Tpcn1<Gt(XG716)Byg>/H',NULL,'MGI:2182472',(SELECT @MGI_DB_ID),'Tpcn1<Gt(XG716)Byg>','129P2/OlaHsd',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('B6.Cg-Sfrp2<C50F>/H',NULL,'MGI:108078',(SELECT @MGI_DB_ID),'Sfrp2<C50F>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('129P2/OlaHsd-Map3k1<Gt(YTC001)Byg>/H',NULL,'MGI:1346872',(SELECT @MGI_DB_ID),'Map3k1<Gt(YTC001)Byg>','129P2/OlaHsd',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('B6.129-Kcnj16<tm1Sjtu>/H',NULL,'MGI:1314842',(SELECT @MGI_DB_ID),'Kcnj16<tm1Sjtu>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('mPtpg',NULL,'MGI:1921802',(SELECT @MGI_DB_ID),'Nipal3<tm1Pbfd>','129SV',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPS8',NULL,'MGI:104684',(SELECT @MGI_DB_ID),'Eps8<tm1Ppdf>','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('FoxP2 delta exon7',NULL,'MGI:2148705',(SELECT @MGI_DB_ID),'Foxp2<tm2.1Woen>','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Abcb4',NULL,'MGI:97569',(SELECT @MGI_DB_ID),'Abcb4<tm1Bor>','FVB/NJ',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Fam63aFam63a<tm1a(EUCOMM)Wtsi/Ics>',NULL,'MGI:1922257',(SELECT @MGI_DB_ID),'Fam63a<tm1a(EUCOMM)Wtsi>','C57BL/6',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Eyl',NULL,'MGI:1100498',(SELECT @MGI_DB_ID),'Pitx3<eyl>','C3H/NHG',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('B6.129-Sfrp1<tm1Aksh>',NULL,'MGI:892014',(SELECT @MGI_DB_ID),'Sfrp1<tm1Aksh>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Tp53',NULL,'MGI:1926609',(SELECT @MGI_DB_ID),'Trp53inp1<tm1Acar>','129/Sv_C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('HEPD0546_1_F01',NULL,'MGI:2685397',(SELECT @MGI_DB_ID),'Rc3h1<tm1a(EUCOMM)Hmgu>','C57BL/6NTac',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('IFIT-2',NULL,'MGI:99449',(SELECT @MGI_DB_ID),'Ifit2<tm1.1Ebsb>','C57BL/6N',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('CMHD-TM_N00105P0_T28A8',NULL,'MGI:1202301',(SELECT @MGI_DB_ID),'Itch<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('Tmem45b',NULL,'MGI:2384574',(SELECT @MGI_DB_ID),'Tmem45b<tm1.1Hsue>','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('DKK3',NULL,'MGI:1354952',(SELECT @MGI_DB_ID),'Dkk3<tm1Cni>','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('T181',NULL,'MGI:1924104',(SELECT @MGI_DB_ID),'Gper1<tm1Dgen>','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('1',NULL,'MGI:1336167',(SELECT @MGI_DB_ID),'Prkab1<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('CMHD-TM_N00005P0_T11G1',NULL,'MGI:1933401',(SELECT @MGI_DB_ID),'Zfp202<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD060_1_B12',NULL,'MGI:104681',(SELECT @MGI_DB_ID),'Hgs<tm1a(EUCOMM)Wtsi>','C57BL/6NTacDen',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('B6.129P2-Vnn1<tm1Pna>/H',NULL,'MGI:108395',(SELECT @MGI_DB_ID),'Vnn1<tm1Pna>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('MARCH9-KO_NOT_OSN',NULL,'MGI:2446144',(SELECT @MGI_DB_ID),'March9<tm1a(EUCOMM)Wtsi>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Myo6Myo6<sv>',NULL,'MGI:104785',(SELECT @MGI_DB_ID),'Myo6<sv>','C57BL/6JIco;C57BL/10',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('129-Nespas<tm1Jop>/H',NULL,'MGI:1861674',(SELECT @MGI_DB_ID),'Nespas<tm1Jop>','129S8/SvEv-Gpi1<c>/NimrH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Vimentin',NULL,'MGI:98932',(SELECT @MGI_DB_ID),'Vim<tm2Cba>','129/SvPas',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('CMHD-TM_N00343P0_T21F5',NULL,'MGI:1916221',(SELECT @MGI_DB_ID),'Tamm41<tm1(NCOM)Cmhd>','C57BL/6N',(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_CMHD),(SELECT @EUMODIC_PROJECT_ID)),
	('Cin85',NULL,'MGI:1889583',(SELECT @MGI_DB_ID),'Sh3kbp1<tm1Ivdi>','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0028_5_G01',NULL,'MGI:108086',(SELECT @MGI_DB_ID),'Hdac1<tm1a(EUCOMM)Wtsi>','C57BL/6NTacDen',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('DEA3',NULL,'MGI:2151016',(SELECT @MGI_DB_ID),'Tmc1<Bth>','C3HeB/FeJ',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Glut8',NULL,'MGI:1860103',(SELECT @MGI_DB_ID),'Slc2a8<tm1.1Asch>','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('C3H.Cg-Celsr1<Scy>/H',NULL,'MGI:1100883',(SELECT @MGI_DB_ID),'Celsr1<Scy>','C3H/HeH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Usp6nl',NULL,'MGI:2138893',(SELECT @MGI_DB_ID),'Usp6nl<tm1Ppdf>','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Cadm1Cadm1<tm1.2Brd>',NULL,'MGI:1889272',(SELECT @MGI_DB_ID),'Cadm1<tm1.2Brd>','C57BL/6JIco;C57BL/6JTyr;129S5',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Rhbdl4-/-',NULL,'MGI:1924117',(SELECT @MGI_DB_ID),'Rhbdd1<tm1.1Mfm>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Abcb4_Balb/cJ',NULL,'MGI:97569',(SELECT @MGI_DB_ID),'Abcb4<tm1Bor>','BALB/cByJ',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('129P2/OlaHsd-Tpcn2<Gt(YHD437)Byg>/H',NULL,'MGI:2385297',(SELECT @MGI_DB_ID),'Tpcn2<Gt(YHD437)Byg>','129P2/OlaHsd',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0033_3_C09',NULL,'MGI:1336167',(SELECT @MGI_DB_ID),'Prkab1<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('EUCJ0019C12',NULL,'MGI:104662',(SELECT @MGI_DB_ID),'Pml<Gt(EUCJ0019c12)Hmgu>','C57BL/6NTacDen',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EUC BL6-FP00042B07',NULL,'MGI:1934229',(SELECT @MGI_DB_ID),'Setdb1<tm1a(EUCOMM)Wtsi>','C57BL/6NTacDen',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('NADH',NULL,'MGI:1918611',(SELECT @MGI_DB_ID),'Aifm2<tm1Avm>','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Matn3Matn3<tm1Brd>',NULL,'MGI:1328350',(SELECT @MGI_DB_ID),'Matn3<tm1Brd>','C57BL/6JTyr;C57BL/6;129S5',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Kctd10Kctd10<Gt(RRG305)Byg>',NULL,'MGI:2141207',(SELECT @MGI_DB_ID),'Kctd10<Gt(RRG305)Byg>','CBA/Ca;129P2',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Grxcr1Grxcr1<tde>',NULL,'MGI:3577767',(SELECT @MGI_DB_ID),'Grxcr1<pi-tde>','STOCK Grxcr1<tde>',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('ENSMUSG00000065586ENSMUSG00000065586<Dmdo>',NULL,'MGI:3619440',(SELECT @MGI_DB_ID),'Mir96<Dmdo>','C3HeB/FeJ',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Stx8tm3a KO',NULL,'MGI:1890156',(SELECT @MGI_DB_ID),'Stx8<tm2a(EUCOMM)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Ino80e tm1a KO',NULL,'MGI:2141881',(SELECT @MGI_DB_ID),'Ino80e<tm1a(EUCOMM)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Dmbt1',NULL,'MGI:106210',(SELECT @MGI_DB_ID),'Dmbt1<tm1Janm>','C57BL/6',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('B6.129-Sfrp2<tm1Aksh>',NULL,'MGI:108078',(SELECT @MGI_DB_ID),'Sfrp2<tm1Aksh>','C57BL/6J',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('M076C04',NULL,'MGI:1915372',(SELECT @MGI_DB_ID),'Nkain4<Gt(M076C04)Vmel>','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('C1orf37',NULL,'MGI:1914729',(SELECT @MGI_DB_ID),'Tg(TMEM183B)1Pbo','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0288_3_A08',NULL,'MGI:2429409',(SELECT @MGI_DB_ID),'Afm<tm1a(KOMP)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('Dll1<C413Y>',NULL,'MGI:104659',(SELECT @MGI_DB_ID),'Dll1<m1Mhda>','C3HeB/FeJ',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('FoxP2',NULL,'MGI:2148705',(SELECT @MGI_DB_ID),'Foxp2<tm1Woen>','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('129-Shh<tm1Chg>/H',NULL,'MGI:98297',(SELECT @MGI_DB_ID),'Shh<tm1Chg>','129S9/SvEvH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('HEPD0527_2_D03',NULL,'MGI:2685493',(SELECT @MGI_DB_ID),'Pla2g4f<tm1a(EUCOMM)Hmgu>','C57BL/6NTac',(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_ICS),(SELECT @EUMODIC_PROJECT_ID)),
	('129S9/SvEvH-Nodal<tm1Rob>/H',NULL,'MGI:97359',(SELECT @MGI_DB_ID),'Nodal<tm1Rob>','129S9/SvEvH',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('Hmx3Hmx3<hx>',NULL,'MGI:107160',(SELECT @MGI_DB_ID),'Hmx3<tm1Ebo>','STOCK Hmx3<hx>',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('ABE1',NULL,'MGI:104785',(SELECT @MGI_DB_ID),'Myo6<Tlc>','C3HeB/FeJ',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Tnfaip1Tnfaip1<tm1a(EUCOMM)Wtsi/H>',NULL,'MGI:104961',(SELECT @MGI_DB_ID),'Tnfaip1<tm1a(EUCOMM)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('Hhip',NULL,'MGI:1341847',(SELECT @MGI_DB_ID),'Hhip<tm1Icmb>','C57BL/6J',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('Cdh23Cdh23<v>',NULL,'MGI:1890219',(SELECT @MGI_DB_ID),'Cdh23<v>','STOCK Cdh23<v>',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0013_1_G11_10560',NULL,'MGI:1927664',(SELECT @MGI_DB_ID),'Sirt2<tm1a(EUCOMM)Wtsi>','C57BL/6JTyr;C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0013_1_G11_10613',NULL,'MGI:1927664',(SELECT @MGI_DB_ID),'Sirt2<tm1a(EUCOMM)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0033_3_F04_10232',NULL,'MGI:1924054',(SELECT @MGI_DB_ID),'Kdm4c<tm1a(KOMP)Wtsi>','C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0033_3_F04_10514',NULL,'MGI:1924054',(SELECT @MGI_DB_ID),'Kdm4c<tm1a(KOMP)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0033_3_F04_10955',NULL,'MGI:1924054',(SELECT @MGI_DB_ID),'Kdm4c<tm1a(KOMP)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0039_1_B01_10470',NULL,'MGI:2156003',(SELECT @MGI_DB_ID),'Snip1<tm1a(EUCOMM)Wtsi>','C57BL/6Dnk',(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_MRCHARWELL),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0039_1_B01_157',NULL,'MGI:2156003',(SELECT @MGI_DB_ID),'Snip1<tm1a(EUCOMM)Wtsi>','C57BL/6JTyr;C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0156_1_B01_10099',NULL,'MGI:102805',(SELECT @MGI_DB_ID),'Entpd1<tm1a(EUCOMM)Wtsi>','C57BL/6NTacDen',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),

	-- line-based hand-curated colonyIds
	('EPD0080_1_B11',NULL,'MGI:2684063',(SELECT @MGI_DB_ID),'Asxl1<tm1a(EUCOMM)Wtsi>', 'C57BL/6NTac',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0059_2_G04',NULL,'MGI:1926153',(SELECT @MGI_DB_ID),'Wrnip1<tm1a(EUCOMM)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0060_1_B12',NULL,'MGI:104681', (SELECT @MGI_DB_ID),'Hgs<tm1a(EUCOMM)Wtsi>',   'C57BL/6NTac',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0177_2_H03',NULL,'MGI:2141881',(SELECT @MGI_DB_ID),'Ino80e<tm1a(EUCOMM)Wtsi>','C57BL/6NTac',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),
	('EPD0116_3_D06',NULL,'MGI:95585',  (SELECT @MGI_DB_ID),'Cidec<tm1a(EUCOMM)Wtsi>', 'C57BL/6NTac',(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID),(SELECT @ORGANISATION_ID_HMGU),(SELECT @EUMODIC_PROJECT_ID)),


	-- IMPC hand curated colonyIds
	('PMDJ',NULL,'MGI:2677270',  (SELECT @MGI_DB_ID),'Pdzd8<tm1b(EUCOMM)Wtsi>', 'C57BL/6NTac',(SELECT @ORGANISATION_ID_WTSI),(SELECT @IMPC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @IMPC_PROJECT_ID)),
	('MEXY',NULL,'MGI:97812',  (SELECT @MGI_DB_ID),'Ptprd<tm1b(EUCOMM)Wtsi>', 'C57BL/6N',(SELECT @ORGANISATION_ID_WTSI),(SELECT @IMPC_PROJECT_ID),(SELECT @ORGANISATION_ID_WTSI),(SELECT @IMPC_PROJECT_ID))
;


/*
** FOR SECONDARY PROJECT GENES LOADING
 */

DROP TABLE IF EXISTS `genes_secondary_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `genes_secondary_project` (
	`acc` varchar(30) NOT NULL,
	`secondary_project_id` varchar(20) NOT NULL,
	`group_label` varchar(40) DEFAULT NULL
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genes_secondary_project`
--

INSERT INTO `genes_secondary_project` VALUES ('MGI:1202299','idg','GPCRs'),('MGI:1917605','idg','GPCRs'),('MGI:2685341','idg','GPCRs'),('MGI:2441837','idg','GPCRs'),('MGI:2681280','idg','GPCRs'),('MGI:2681259','idg','GPCRs'),('MGI:1926076','idg','GPCRs'),('MGI:108418','idg','GPCRs'),('MGI:2159637','idg','GPCRs'),('MGI:3645524','idg','GPCRs'),('MGI:2441719','idg','GPCRs'),('MGI:2441732','idg','GPCRs'),('MGI:106912','idg','GPCRs'),('MGI:2676315','idg','GPCRs'),('MGI:1858235','idg','GPCRs'),('MGI:1333809','idg','GPCRs'),('MGI:109248','idg','GPCRs'),('MGI:2685211','idg','GPCRs'),('MGI:3643278','idg','GPCRs'),('MGI:1346334','idg','GPCRs'),('MGI:2443628','idg','GPCRs'),('MGI:3041203','idg','GPCRs'),('MGI:96283','idg','GPCRs'),('MGI:2441734','idg','GPCRs'),('MGI:2681298','idg','GPCRs'),('MGI:2681278','idg','GPCRs'),('MGI:2681273','idg','GPCRs'),('MGI:2672983','idg','GPCRs'),('MGI:1097709','idg','GPCRs'),('MGI:1277167','idg','GPCRs'),('MGI:102967','idg','GPCRs'),('MGI:1933113','idg','GPCRs'),('MGI:2136761','idg','GPCRs'),('MGI:108082','idg','GPCRs'),('MGI:2681308','idg','GPCRs'),('MGI:2681304','idg','GPCRs'),('MGI:1935037','idg','GPCRs'),('MGI:1927851','idg','GPCRs'),('MGI:1196464','idg','GPCRs'),('MGI:2681256','idg','GPCRs'),('MGI:2182926','idg','GPCRs'),('MGI:1927596','idg','GPCRs'),('MGI:2451244','idg','GPCRs'),('MGI:2441827','idg','GPCRs'),('MGI:1925499','idg','GPCRs'),('MGI:101909','idg','GPCRs'),('MGI:1927653','idg','GPCRs'),('MGI:1916157','idg','GPCRs'),('MGI:1891250','idg','GPCRs'),('MGI:2155249','idg','GPCRs'),('MGI:1934133','idg','GPCRs'),('MGI:3525078','idg','GPCRs'),('MGI:2441992','idg','GPCRs'),('MGI:2135884','idg','GPCRs'),('MGI:1315214','idg','GPCRs'),('MGI:2147529','idg','GPCRs'),('MGI:2441872','idg','GPCRs'),('MGI:2441803','idg','GPCRs'),('MGI:2686146','idg','GPCRs'),('MGI:2441843','idg','GPCRs'),('MGI:2685222','idg','GPCRs'),('MGI:892973','idg','GPCRs'),('MGI:2685071','idg','GPCRs'),('MGI:2441950','idg','GPCRs'),('MGI:2681247','idg','GPCRs'),('MGI:1329003','idg','GPCRs'),('MGI:3527454','idg','GPCRs'),('MGI:2653880','idg','GPCRs'),('MGI:2681312','idg','GPCRs'),('MGI:2441887','idg','GPCRs'),('MGI:2681302','idg','GPCRs'),('MGI:2182728','idg','GPCRs'),('MGI:1354372','idg','GPCRs'),('MGI:2685213','idg','GPCRs'),('MGI:2681253','idg','GPCRs'),('MGI:105372','idg','GPCRs'),('MGI:2441884','idg','GPCRs'),('MGI:1277106','idg','GPCRs'),('MGI:2446854','idg','GPCRs'),('MGI:2685145','idg','GPCRs'),('MGI:3527427','idg','GPCRs'),('MGI:891989','idg','GPCRs'),('MGI:1918361','idg','GPCRs'),('MGI:1345190','idg','GPCRs'),('MGI:1347010','idg','GPCRs'),('MGI:1914418','idg','GPCRs'),('MGI:3606604','idg','GPCRs'),('MGI:1859670','idg','GPCRs'),('MGI:107859','idg','GPCRs'),('MGI:3033139','idg','GPCRs'),('MGI:1929676','idg','GPCRs'),('MGI:2681218','idg','GPCRs'),('MGI:2677633','idg','GPCRs'),('MGI:2135882','idg','GPCRs'),('MGI:1920260','idg','GPCRs'),('MGI:1919112','idg','GPCRs'),('MGI:3033145','idg','GPCRs'),('MGI:1934135','idg','GPCRs'),('MGI:1924846','idg','GPCRs'),('MGI:1918021','idg','GPCRs'),('MGI:2182928','idg','GPCRs'),('MGI:107193','idg','GPCRs'),('MGI:2685955','idg','GPCRs'),('MGI:2668437','idg','GPCRs'),('MGI:2685887','idg','GPCRs'),('MGI:2441758','idg','GPCRs'),('MGI:2442046','idg','GPCRs'),('MGI:2442043','idg','GPCRs'),('MGI:2685519','idg','GPCRs'),('MGI:2681210','idg','GPCRs'),('MGI:1933383','idg','GPCRs'),('MGI:2441671','idg','GPCRs'),('MGI:2441890','idg','GPCRs'),('MGI:3033115','idg','GPCRs'),('MGI:3588270','idg','GPCRs'),('MGI:2685076','idg','GPCRs'),('MGI:2685995','idg','GPCRs'),('MGI:3527452','idg','GPCRs'),('MGI:3033098','idg','GPCRs'),('MGI:3033095','idg','GPCRs'),('MGI:3821888','idg','GPCRs'),('MGI:3033148','idg','GPCRs'),('MGI:1914799','idg','Ion Channels'),('MGI:102965','idg','Ion Channels'),('MGI:1918882','idg','Ion Channels'),('MGI:1859165','idg','Ion Channels'),('MGI:2445160','idg','Ion Channels'),('MGI:1913983','idg','Ion Channels'),('MGI:1098718','idg','Ion Channels'),('MGI:103301','idg','Ion Channels'),('MGI:102522','idg','Ion Channels'),('MGI:2146607','idg','Ion Channels'),('MGI:2652846','idg','Ion Channels'),('MGI:1889006','idg','Ion Channels'),('MGI:1889007','idg','Ion Channels'),('MGI:1914748','idg','Ion Channels'),('MGI:1298234','idg','Ion Channels'),('MGI:1890615','idg','Ion Channels'),('MGI:3612244','idg','Ion Channels'),('MGI:104696','idg','Ion Channels'),('MGI:2385186','idg','Ion Channels'),('MGI:1310000','idg','Ion Channels'),('MGI:1917912','idg','Ion Channels'),('MGI:2685489','idg','Ion Channels'),('MGI:106921','idg','Ion Channels'),('MGI:2685197','idg','Ion Channels'),('MGI:1932376','idg','Ion Channels'),('MGI:99418','idg','Ion Channels'),('MGI:1859167','idg','Ion Channels'),('MGI:1859168','idg','Ion Channels'),('MGI:1933131','idg','Ion Channels'),('MGI:2442845','idg','Ion Channels'),('MGI:1859639','idg','Ion Channels'),('MGI:2385894','idg','Ion Channels'),('MGI:87895','idg','Ion Channels'),('MGI:3588203','idg','Ion Channels'),('MGI:1915051','idg','Ion Channels'),('MGI:2143897','idg','Ion Channels'),('MGI:1924118','idg','Ion Channels'),('MGI:2442632','idg','Ion Channels'),('MGI:2159566','idg','Ion Channels'),('MGI:1197019','idg','Ion Channels'),('MGI:2443317','idg','Ion Channels'),('MGI:95816','idg','Ion Channels'),('MGI:107497','idg','Ion Channels'),('MGI:95624','idg','Ion Channels'),('MGI:95749','idg','Ion Channels'),('MGI:1917607','idg','Ion Channels'),('MGI:1933993','idg','Ion Channels'),('MGI:103307','idg','Ion Channels'),('MGI:2387404','idg','Ion Channels'),('MGI:1861899','idg','Ion Channels'),('MGI:1918881','idg','Ion Channels'),('MGI:2387597','idg','Ion Channels'),('MGI:1197011','idg','Ion Channels'),('MGI:1932374','idg','Ion Channels'),('MGI:96670','idg','Ion Channels'),('MGI:1298211','idg','Ion Channels'),('MGI:3043288','idg','Ion Channels'),('MGI:2157091','idg','Ion Channels'),('MGI:2443082','idg','Ion Channels'),('MGI:2156184','idg','Ion Channels'),('MGI:96664','idg','Ion Channels'),('MGI:2384820','idg','Ion Channels'),('MGI:2663923','idg','Ion Channels'),('MGI:2139758','idg','Ion Channels'),('MGI:87891','idg','Ion Channels'),('MGI:2669035','idg','Ion Channels'),('MGI:2669033','idg','Ion Channels'),('MGI:95617','idg','Ion Channels'),('MGI:95618','idg','Ion Channels'),('MGI:95619','idg','Ion Channels'),('MGI:96663','idg','Ion Channels'),('MGI:2684043','idg','Ion Channels'),('MGI:96671','idg','Ion Channels'),('MGI:95812','idg','Ion Channels'),('MGI:3609260','idg','Ion Channels'),('MGI:894644','idg','Ion Channels'),('MGI:2684139','idg','Ion Channels'),('MGI:2664670','idg','Ion Channels'),('MGI:87886','idg','Ion Channels'),('MGI:1347049','idg','Ion Channels'),('MGI:1098804','idg','Ion Channels'),('MGI:1202403','idg','Ion Channels'),('MGI:109239','idg','Ion Channels'),('MGI:1929813','idg','Ion Channels'),('MGI:87890','idg','Ion Channels'),('MGI:87893','idg','Ion Channels'),('MGI:95625','idg','Ion Channels'),('MGI:1336208','idg','Ion Channels'),('MGI:1338890','idg','Ion Channels'),('MGI:1921821','idg','Ion Channels'),('MGI:1913272','idg','Ion Channels'),('MGI:95751','idg','Ion Channels'),('MGI:3694646','idg','Ion Channels'),('MGI:1921674','idg','Ion Channels'),('MGI:1924627','idg','Ion Channels'),('MGI:1916704','idg','Ion Channels'),('MGI:1858231','idg','Ion Channels'),('MGI:103156','idg','Ion Channels'),('MGI:1206582','idg','Ion Channels'),('MGI:2664668','idg','Ion Channels'),('MGI:1929259','idg','Ion Channels'),('MGI:88295','idg','Ion Channels'),('MGI:1341841','idg','Ion Channels'),('MGI:2157946','idg','Ion Channels'),('MGI:2664099','idg','Ion Channels'),('MGI:95750','idg','Ion Channels'),('MGI:1930643','idg','Ion Channels'),('MGI:1329026','idg','Ion Channels'),('MGI:2139790','idg','Ion Channels'),('MGI:2139744','idg','Ion Channels'),('MGI:1920831','idg','Kinases'),('MGI:3026984','idg','Kinases'),('MGI:2445052','idg','Kinases'),('MGI:1913266','idg','Kinases'),('MGI:1344404','idg','Kinases'),('MGI:1289230','idg','Kinases'),('MGI:2449492','idg','Kinases'),('MGI:1352500','idg','Kinases'),('MGI:1347557','idg','Kinases'),('MGI:1891338','idg','Kinases'),('MGI:1351326','idg','Kinases'),('MGI:1891638','idg','Kinases'),('MGI:1921385','idg','Kinases'),('MGI:1347357','idg','Kinases'),('MGI:2147036','idg','Kinases'),('MGI:2685008','idg','Kinases'),('MGI:2652869','idg','Kinases'),('MGI:1917172','idg','Kinases'),('MGI:1889336','idg','Kinases'),('MGI:2142824','idg','Kinases'),('MGI:2652894','idg','Kinases'),('MGI:1918590','idg','Kinases'),('MGI:2442403','idg','Kinases'),('MGI:1890645','idg','Kinases'),('MGI:2385213','idg','Kinases'),('MGI:2442399','idg','Kinases'),('MGI:1333822','idg','Kinases'),('MGI:1330302','idg','Kinases'),('MGI:1201675','idg','Kinases'),('MGI:1329027','idg','Kinases'),('MGI:2685045','idg','Kinases'),('MGI:2685925','idg','Kinases'),('MGI:1921903','idg','Kinases'),('MGI:2443419','idg','Kinases'),('MGI:1336167','idg','Kinases'),('MGI:1918341','idg','Kinases'),('MGI:1918294','idg','Kinases'),('MGI:2442190','idg','Kinases'),('MGI:2388285','idg','Kinases'),('MGI:1928487','idg','Kinases'),('MGI:1913837','idg','Kinases'),('MGI:2679420','idg','Kinases'),('MGI:2387464','idg','Kinases'),('MGI:1916211','idg','Kinases'),('MGI:1195261','idg','Kinases'),('MGI:1330300','idg','Kinases'),('MGI:2448549','idg','Kinases'),('MGI:2385336','idg','Kinases'),('MGI:97579','idg','Kinases'),('MGI:2448506','idg','Kinases'),('MGI:2441683','idg','Kinases'),('MGI:3039582','idg','Kinases'),('MGI:1858204','idg','Kinases'),('MGI:1920955','idg','Kinases'),('MGI:1918349','idg','Kinases'),('MGI:1917675','idg','Kinases'),('MGI:2384296','idg','Kinases'),('MGI:2152419','idg','Kinases'),('MGI:97601','idg','Kinases'),('MGI:96840','idg','Kinases'),('MGI:2152214','idg','Kinases'),('MGI:2442276','idg','Kinases'),('MGI:2385204','idg','Kinases'),('MGI:88547','idg','Kinases'),('MGI:2683541','idg','Kinases'),('MGI:98904','idg','Kinases'),('MGI:894318','idg','Kinases'),('MGI:2148775','idg','Kinases'),('MGI:1924735','idg','Kinases'),('MGI:97576','idg','Kinases'),('MGI:894279','idg','Kinases'),('MGI:894676','idg','Kinases'),('MGI:88353','idg','Kinases'),('MGI:1922857','idg','Kinases'),('MGI:1098670','idg','Kinases'),('MGI:97518','idg','Kinases'),('MGI:2660884','idg','Kinases'),('MGI:1918885','idg','Kinases'),('MGI:2444188','idg','Kinases'),('MGI:2685924','idg','Kinases'),('MGI:1276121','idg','Kinases'),('MGI:97517','idg','Kinases'),('MGI:1330292','idg','Kinases'),('MGI:1914128','idg','Kinases'),('MGI:1858227','idg','Kinases'),('MGI:3587025','idg','Kinases'),('MGI:1931744','idg','Kinases'),('MGI:1922250','idg','Kinases'),('MGI:2685557','idg','Kinases'),('MGI:2679274','idg','Kinases'),('MGI:2151224','idg','Kinases'),('MGI:2685128','idg','Kinases'),('MGI:1921428','idg','Kinases'),('MGI:109584','idg','Kinases'),('MGI:1289172','idg','Kinases'),('MGI:1891766','idg','Kinases'),('MGI:1349436','idg','Kinases'),('MGI:1339656','idg','Kinases'),('MGI:1927552','idg','Kinases'),('MGI:1346023','idg','Kinases'),('MGI:3027899','idg','Kinases'),('MGI:3583944','idg','Kinases'),('MGI:2137630','idg','Kinases'),('MGI:108411','idg','Kinases'),('MGI:1931787','idg','Kinases'),('MGI:2446159','idg','Kinases'),('MGI:2388268','idg','Kinases'),('MGI:2444559','idg','Kinases'),('MGI:2136459','idg','Kinases'),('MGI:1098551','idg','Kinases'),('MGI:1203730','idg','Kinases'),('MGI:1920014','idg','Kinases'),('MGI:2652845','idg','Kinases'),('MGI:1330301','idg','Kinases'),('MGI:107930','idg','Kinases'),('MGI:97594','idg','Kinases'),('MGI:2388073','idg','Kinases'),('MGI:2155779','idg','Kinases'),('MGI:1921622','idg','Kinases'),('MGI:1346879','idg','Kinases'),('MGI:3528383','idg','Kinases'),('MGI:2385017','idg','Kinases'),('MGI:1344375','idg','Kinases'),('MGI:2142227','idg','Kinases'),('MGI:1929914','idg','Kinases'),('MGI:107929','idg','Kinases'),('MGI:2443413','idg','Kinases');

/*!40000 ALTER TABLE `genes_secondary_project` ENABLE KEYS */;
