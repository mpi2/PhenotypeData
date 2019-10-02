-- MySQL dump 10.13  Distrib 5.1.73, for redhat-linux-gnu (x86_64)
--
-- Host: mysql-mi-dev    Database: impress_6_0
-- ------------------------------------------------------
-- Server version	5.5.36-log

--
-- Table structure for table `phenotype_parameter`
--

DROP TABLE IF EXISTS `phenotype_parameter`;
CREATE TABLE `phenotype_parameter` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `stable_id` varchar(30) NOT NULL,
  `db_id` int(10) NOT NULL,
  `name` varchar(200) NOT NULL,
  `description` text,
  `major_version` int(10) NOT NULL DEFAULT '1',
  `minor_version` int(10) NOT NULL DEFAULT '0',
  `unit` varchar(40) NOT NULL,
  `datatype` varchar(20) NOT NULL,
  `parameter_type` varchar(30) NOT NULL,
  `formula` text,
  `required` tinyint(1) DEFAULT '0',
  `metadata` tinyint(1) DEFAULT '0',
  `important` tinyint(1) DEFAULT '0',
  `derived` tinyint(1) DEFAULT '0',
  `annotate` tinyint(1) DEFAULT '0',
  `increment` tinyint(1) DEFAULT '0',
  `options` tinyint(1) DEFAULT '0',
  `sequence` int(10) NOT NULL,
  `media` tinyint(1) DEFAULT '0',
  `data_analysis_notes` text,
  `stable_key` int(10) DEFAULT '0',

  PRIMARY KEY (`id`)
);


--
-- Table structure for table `phenotype_parameter_eq_annotation`
--

DROP TABLE IF EXISTS `phenotype_parameter_eq_annotation`;
CREATE TABLE `phenotype_parameter_eq_annotation` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
--   `event_type` enum('abnormal','abnormal_specific','increased','decreased','inferred','trait') DEFAULT NULL,
  `event_type` VARCHAR(40) DEFAULT NULL,
      CHECK (event_type IN ('abnormal','abnormal_specific','increased','decreased','inferred','trait')),
  `option_id` int(10) DEFAULT NULL,
--   `sex` enum('female','hermaphrodite','male','not_applicable','no_data') DEFAULT NULL,
  `sex` VARCHAR(40) DEFAULT NULL,
      CHECK (sex IN ('female','hermaphrodite','male','not_applicable','no_data')),

  `ontology_acc` varchar(20) DEFAULT NULL,
  `ontology_db_id` int(10) DEFAULT NULL,
  `quality_acc` varchar(20) DEFAULT NULL,
  `quality_db_id` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
);


--
-- Table structure for table `phenotype_parameter_increment`
--

DROP TABLE IF EXISTS `phenotype_parameter_increment`;
CREATE TABLE `phenotype_parameter_increment` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `increment_value` varchar(200) NOT NULL,
  `increment_datatype` varchar(20) NOT NULL,
  `increment_unit` varchar(40) NOT NULL,
  `increment_minimum` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
);


--
-- Table structure for table `phenotype_parameter_lnk_eq_annotation`
--

DROP TABLE IF EXISTS `phenotype_parameter_lnk_eq_annotation`;
CREATE TABLE `phenotype_parameter_lnk_eq_annotation` (
  `annotation_id` int(10) NOT NULL,
  `parameter_id` int(10) NOT NULL
);


--
-- Table structure for table `phenotype_parameter_lnk_increment`
--

DROP TABLE IF EXISTS `phenotype_parameter_lnk_increment`;
CREATE TABLE `phenotype_parameter_lnk_increment` (
  `parameter_id` int(10) NOT NULL,
  `increment_id` int(10) NOT NULL
);


--
-- Table structure for table `phenotype_parameter_lnk_ontology_annotation`
--

DROP TABLE IF EXISTS `phenotype_parameter_lnk_ontology_annotation`;
CREATE TABLE `phenotype_parameter_lnk_ontology_annotation` (
  `annotation_id` int(10) NOT NULL,
  `parameter_id` int(10) NOT NULL
);


--
-- Table structure for table `phenotype_parameter_lnk_option`
--

DROP TABLE IF EXISTS `phenotype_parameter_lnk_option`;
CREATE TABLE `phenotype_parameter_lnk_option` (
  `parameter_id` int(10) NOT NULL,
  `option_id` int(10) NOT NULL
);


--
-- Table structure for table `phenotype_parameter_ontology_annotation`
--

DROP TABLE IF EXISTS `phenotype_parameter_ontology_annotation`;
CREATE TABLE `phenotype_parameter_ontology_annotation` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
--   `event_type` enum('abnormal','abnormal_specific','increased','decreased','inferred','trait') DEFAULT NULL,
  `event_type` VARCHAR(40) DEFAULT NULL,
      CHECK (event_type IN ('abnormal','abnormal_specific','increased','decreased','inferred','trait')),
  `option_id` int(10) DEFAULT NULL,
  `ontology_acc` varchar(20) DEFAULT NULL,
  `ontology_db_id` int(10) DEFAULT NULL,
  `sex` varchar(8) DEFAULT '',
  PRIMARY KEY (`id`)
);


--
-- Table structure for table `phenotype_parameter_option`
--

DROP TABLE IF EXISTS `phenotype_parameter_option`;
CREATE TABLE `phenotype_parameter_option` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `normal` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
);


--
-- Table structure for table `phenotype_pipeline`
--

DROP TABLE IF EXISTS `phenotype_pipeline`;
CREATE TABLE `phenotype_pipeline` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `stable_id` varchar(40) NOT NULL,
  `db_id` int(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(2000) DEFAULT NULL,
  `major_version` int(10) NOT NULL DEFAULT '1',
  `minor_version` int(10) NOT NULL DEFAULT '0',
  `stable_key` int(10) DEFAULT '0',
  `is_deprecated` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
);


--
-- Table structure for table `phenotype_pipeline_procedure`
--

DROP TABLE IF EXISTS `phenotype_pipeline_procedure`;
CREATE TABLE `phenotype_pipeline_procedure` (
  `pipeline_id` int(10) NOT NULL,
  `procedure_id` int(10) NOT NULL,
  UNIQUE (`pipeline_id`,`procedure_id`)
);


--
-- Table structure for table `phenotype_procedure`
--

DROP TABLE IF EXISTS `phenotype_procedure`;
CREATE TABLE `phenotype_procedure` (
  id                        INT(10)      NOT NULL AUTO_INCREMENT,
  stable_key                INT(10)      DEFAULT '0',
  stable_id                 VARCHAR(40)  NOT NULL,
  db_id                     INT(10)      NOT NULL,
  name                      VARCHAR(200) NOT NULL,
  description               TEXT,
  major_version             INT(10)      NOT NULL DEFAULT '1',
  minor_version             INT(10)      NOT NULL DEFAULT '0',
  is_mandatory              TINYINT(1)   DEFAULT '0',
  level                     VARCHAR(20)  DEFAULT NULL,
  stage                     VARCHAR(20)  DEFAULT 'Adult',
  stage_label               VARCHAR(20)  DEFAULT NULL,
  schedule_key              INT(10)      DEFAULT 0,

  PRIMARY KEY (`id`)
);


--
-- Table structure for table `phenotype_procedure_meta_data`
--

DROP TABLE IF EXISTS `phenotype_procedure_meta_data`;
CREATE TABLE `phenotype_procedure_meta_data` (
  `procedure_id` int(10) NOT NULL,
  `meta_name` varchar(40) NOT NULL,
  `meta_value` varchar(40) NOT NULL
);


--
-- Table structure for table `phenotype_procedure_parameter`
--

DROP TABLE IF EXISTS `phenotype_procedure_parameter`;
CREATE TABLE `phenotype_procedure_parameter` (
  `procedure_id` int(10) NOT NULL,
  `parameter_id` int(10) NOT NULL,
  UNIQUE (`procedure_id`,`parameter_id`)
);

--
-- Table structure for table `phenotype_parameter_type`
-- Taken from last line of http://ves-ebi-d9:8080/jenkins/job/DataRelease_GenerateDerivedParameters/configure
--

DROP TABLE IF EXISTS `phenotype_parameter_type`;
CREATE TABLE IF NOT EXISTS phenotype_parameter_type AS SELECT DISTINCT parameter_stable_id, observation_type FROM observation;