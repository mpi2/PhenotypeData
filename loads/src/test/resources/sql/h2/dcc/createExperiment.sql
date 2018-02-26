/****************/
/*  experiment  */
/****************/
DROP TABLE IF EXISTS experiment;
CREATE TABLE experiment (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  dateOfExperiment DATE NOT NULL,
  experimentId VARCHAR(255) NOT NULL,
  sequenceId VARCHAR(255) DEFAULT NULL,
  datasourceShortName VARCHAR(40) NOT NULL,
  center_procedure_pk INT NOT NULL

);

DROP TABLE IF EXISTS experiment_statuscode;
CREATE TABLE experiment_statuscode (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  experiment_pk INT NOT NULL,
  statuscode_pk INT NOT NULL,

  UNIQUE (experiment_pk, statuscode_pk)

);

DROP TABLE IF EXISTS experiment_specimen;
CREATE TABLE experiment_specimen (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  experiment_pk INT NOT NULL,
  specimen_pk INT NOT NULL,

  UNIQUE(experiment_pk, specimen_pk)

);

DROP TABLE IF EXISTS housing;
CREATE TABLE housing (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  fromLims TINYINT NOT NULL,
  lastUpdated DATE DEFAULT NULL,
  center_procedure_pk INT NOT NULL

);

DROP TABLE IF EXISTS line;
CREATE TABLE line (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  colonyId VARCHAR(255) NOT NULL,
  datasourceShortName VARCHAR(40) NOT NULL,
  center_procedure_pk INT NOT NULL,

  UNIQUE (colonyId, center_procedure_pk)

);

DROP TABLE IF EXISTS procedure_;
CREATE TABLE procedure_ (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  procedureId VARCHAR(255) NOT NULL

);

DROP TABLE IF EXISTS center_procedure;
CREATE TABLE center_procedure (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  center_pk INT NOT NULL,
  procedure_pk INT NOT NULL,

  UNIQUE (center_pk, procedure_pk)

);

DROP TABLE IF EXISTS line_statuscode;
CREATE TABLE line_statuscode (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  line_pk INT NOT NULL,
  statuscode_pk INT NOT NULL

);

DROP TABLE IF EXISTS simpleParameter;
CREATE TABLE simpleParameter (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  sequenceId INTEGER DEFAULT NULL,
  unit VARCHAR(255) DEFAULT NULL,
  value VARCHAR(4096) DEFAULT NULL,
  procedure_pk INT NOT NULL

);

DROP TABLE IF EXISTS ontologyParameter;
CREATE TABLE ontologyParameter (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  sequenceId INTEGER DEFAULT NULL,
  procedure_pk INT NOT NULL

);

DROP TABLE IF EXISTS seriesParameter;
CREATE TABLE seriesParameter (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  sequenceId INTEGER DEFAULT NULL,
  procedure_pk INT NOT NULL

);

DROP TABLE IF EXISTS mediaParameter;
CREATE TABLE mediaParameter (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  filetype VARCHAR(255) NOT NULL,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  URI VARCHAR(255) NOT NULL,
  link VARCHAR(255) DEFAULT NULL,
  procedure_pk INT NOT NULL

);

DROP TABLE IF EXISTS ontologyParameterTerm;
CREATE TABLE ontologyParameterTerm (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  term VARCHAR(255) NOT NULL,
  ontologyParameter_pk INT NOT NULL,

  UNIQUE (ontologyParameter_pk, term)

);

DROP TABLE IF EXISTS seriesParameterValue;
CREATE TABLE seriesParameterValue (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  incrementStatus VARCHAR(255) DEFAULT NULL,
  incrementValue VARCHAR(255) NOT NULL,
  value VARCHAR(4096) DEFAULT NULL,
  seriesParameter_pk INT NOT NULL

);

DROP TABLE IF EXISTS mediaParameter_parameterAssociation;
CREATE TABLE mediaParameter_parameterAssociation (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  mediaParameter_pk INT NOT NULL,
  parameterAssociation_pk INT NOT NULL

);

DROP TABLE IF EXISTS mediaParameter_procedureMetadata;
CREATE TABLE mediaParameter_procedureMetadata (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  mediaParameter_pk INT NOT NULL,
  procedureMetadata_pk INT NOT NULL

);

DROP TABLE IF EXISTS parameterAssociation;
CREATE TABLE parameterAssociation (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  sequenceId INTEGER DEFAULT NULL,
  link VARCHAR(255) DEFAULT NULL

);

DROP TABLE IF EXISTS procedureMetadata;
CREATE TABLE procedureMetadata (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  sequenceId INTEGER DEFAULT NULL,
  value VARCHAR(4096) DEFAULT NULL

);

DROP TABLE IF EXISTS dimension;
CREATE TABLE dimension (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  id VARCHAR(255) NOT NULL,
  origin VARCHAR(255) NOT NULL,
  unit VARCHAR(255) DEFAULT NULL,
  value decimal(15, 10) NOT NULL,
  parameterAssociation_pk INT NOT NULL

);

DROP TABLE IF EXISTS mediaSampleParameter;
CREATE TABLE mediaSampleParameter (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  procedure_pk INT NOT NULL

);

DROP TABLE IF EXISTS mediaSample;
CREATE TABLE mediaSample (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  localId VARCHAR(255) NOT NULL,
  mediaSampleParameter_pk INT NOT NULL

);

DROP TABLE IF EXISTS mediaSection;
CREATE TABLE mediaSection (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  localId VARCHAR(255) NOT NULL,
  mediaSample_pk INT NOT NULL

);

DROP TABLE IF EXISTS mediaFile;
CREATE TABLE mediaFile (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  fileType VARCHAR(255) NOT NULL,
  localId VARCHAR(255) NOT NULL,
  URI VARCHAR(255) NOT NULL,
  link VARCHAR(255) DEFAULT NULL,
  mediaSection_pk INT NOT NULL

);

DROP TABLE IF EXISTS mediaFile_parameterAssociation;
CREATE TABLE mediaFile_parameterAssociation (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  mediaFile_pk INT NOT NULL,
  parameterAssociation_pk INT NOT NULL

);

DROP TABLE IF EXISTS mediaFile_procedureMetadata;
CREATE TABLE mediaFile_procedureMetadata (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  mediaFile_pk INT NOT NULL,
  procedureMetadata_pk INT NOT NULL

);

DROP TABLE IF EXISTS seriesMediaParameter;
CREATE TABLE seriesMediaParameter (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  procedure_pk INT NOT NULL
);

DROP TABLE IF EXISTS procedure_procedureMetadata;
CREATE TABLE procedure_procedureMetadata (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  procedure_pk INT NOT NULL,
  procedureMetadata_pk INT NOT NULL

);

DROP TABLE IF EXISTS seriesMediaParameterValue;
CREATE TABLE seriesMediaParameterValue (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  fileType VARCHAR(255) DEFAULT NULL,
  incrementValue VARCHAR(255) NOT NULL,
  URI VARCHAR(255) NOT NULL,
  link VARCHAR(255) DEFAULT NULL,
  seriesMediaParameter_pk INT NOT NULL

);

DROP TABLE IF EXISTS seriesMediaParameterValue_parameterAssociation;
CREATE TABLE seriesMediaParameterValue_parameterAssociation (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  seriesMediaParameterValue_pk INT NOT NULL,
  parameterAssociation_pk INT NOT NULL,

  UNIQUE (seriesMediaParameterValue_pk, parameterAssociation_pk)

);

DROP TABLE IF EXISTS seriesMediaParameterValue_procedureMetadata;
CREATE TABLE seriesMediaParameterValue_procedureMetadata (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  seriesMediaParameterValue_pk INT NOT NULL,
  procedureMetadata_pk INT NOT NULL,

  UNIQUE (seriesMediaParameterValue_pk, procedureMetadata_pk)
);