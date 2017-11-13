DROP TABLE IF EXISTS mike;
CREATE TABLE mike (c1 integer);



/**************/
/*  specimen  */
/**************/
/*
DROP TABLE IF EXISTS center;
CREATE TABLE center (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  centerId VARCHAR(255) NOT NULL,
  pipeline VARCHAR(255) NOT NULL,
  project VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS embryo;
CREATE TABLE embryo (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  stage VARCHAR(255) NOT NULL,
  stageUnit VARCHAR(255) NOT NULL,
  specimen_pk INT UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS mouse;
CREATE TABLE mouse (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  DOB DATE NOT NULL,
  specimen_pk INT UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS center_specimen;
CREATE TABLE center_specimen (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  center_pk INT UNSIGNED NOT NULL,
  specimen_pk INT UNSIGNED NOT NULL,
  UNIQUE KEY centerPk_specimenPk_uk (center_pk, specimen_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS relatedSpecimen;
CREATE TABLE relatedSpecimen (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  centerId VARCHAR(255) NOT NULL,
  specimenIdPk INT UNSIGNED NOT NULL,
  specimenId VARCHAR(255) NOT NULL,
  relationship VARCHAR(255) NOT NULL,
  relatedSpecimenId VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS specimen;
CREATE TABLE specimen (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  colonyId VARCHAR(255) DEFAULT NULL,
  datasourceShortName VARCHAR(40) NOT NULL,
  gender VARCHAR(255) NOT NULL,
  isBaseline tinyint(1) NOT NULL,
  litterId VARCHAR(255) NOT NULL,
  phenotypingCenter VARCHAR(255) NOT NULL,
  pipeline VARCHAR(255) NOT NULL,
  productionCenter VARCHAR(255) DEFAULT NULL,
  project VARCHAR(255) NOT NULL,
  specimenId VARCHAR(255) NOT NULL,
  strainId VARCHAR(255) DEFAULT NULL,
  zygosity VARCHAR(255) NOT NULL,
  statuscode_pk INT UNSIGNED DEFAULT NULL,
  KEY colonyIdIndex (colonyId),
  KEY specimenIdIndex (specimenId),
  KEY strainIdIndex (strainId),
  KEY statuscode_pk (statuscode_pk),
  UNIQUE KEY phenotypingCenter_specimenId_uk (phenotypingCenter, specimenId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS genotype;
CREATE TABLE genotype (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  fatherZygosity VARCHAR(255) DEFAULT NULL,
  geneSymbol VARCHAR(255) NOT NULL,
  mgiAlleleId VARCHAR(255) NOT NULL,
  mgiGeneId VARCHAR(255) NOT NULL,
  motherZygosity VARCHAR(255) DEFAULT NULL,
  specimen_pk INT UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS parentalStrain;
CREATE TABLE parentalStrain (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  gender VARCHAR(255) NOT NULL,
  level INTEGER NOT NULL,
  mgiStrainId VARCHAR(255) NOT NULL,
  percentage DOUBLE NOT NULL,
  specimen_pk INT UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS statuscode;
CREATE TABLE statuscode (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  dateOfStatuscode DATE DEFAULT NULL,
  value VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


*/



/*******************************************************/
/* specimen ALTER TABLE STATEMENTS TO ADD FOREIGN KEYS */
/*******************************************************/

/*
ALTER TABLE embryo
  ADD CONSTRAINT embryo_specimen_pk FOREIGN KEY (specimen_pk) REFERENCES specimen (pk);

ALTER TABLE mouse
  ADD CONSTRAINT mouse_specimen_pk FOREIGN KEY (specimen_pk) REFERENCES specimen (pk);

ALTER TABLE center_specimen
  ADD CONSTRAINT center_specimen_center_pk FOREIGN KEY (center_pk) REFERENCES center (pk),
  ADD CONSTRAINT center_specimen_specimen_pk FOREIGN KEY (specimen_pk) REFERENCES specimen (pk);

ALTER TABLE specimen
  ADD CONSTRAINT specimen_statuscode_pk FOREIGN KEY (statuscode_pk) REFERENCES statuscode (pk);

ALTER TABLE genotype
  ADD CONSTRAINT genotype_specimen_pk FOREIGN KEY (specimen_pk) REFERENCES specimen (pk);

ALTER TABLE parentalStrain
  ADD CONSTRAINT parentalStrain_specimen_pk FOREIGN KEY (specimen_pk) REFERENCES specimen (pk);
*/



/****************/
/*  experiment  */
/****************/
/*
DROP TABLE IF EXISTS experiment;
CREATE TABLE experiment (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  dateOfExperiment DATE NOT NULL,
  experimentId VARCHAR(255) NOT NULL,
  sequenceId VARCHAR(255) DEFAULT NULL,
  datasourceShortName VARCHAR(40) NOT NULL,
  center_procedure_pk INT UNSIGNED NOT NULL,

  KEY dateOfExperiment_idx(dateOfExperiment),
  KEY experimentId_idx(experimentId),
  KEY center_procedure_pk_idx(center_procedure_pk)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS experiment_statuscode;
CREATE TABLE experiment_statuscode (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  experiment_pk INT UNSIGNED NOT NULL,
  statuscode_pk INT UNSIGNED NOT NULL,

  UNIQUE KEY experimentPk_statuscodePk_uk (experiment_pk, statuscode_pk)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS experiment_specimen;
CREATE TABLE experiment_specimen (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  experiment_pk INT UNSIGNED NOT NULL,
  specimen_pk INT UNSIGNED NOT NULL,

  UNIQUE KEY experimentPk_specimenPk_uk (experiment_pk, specimen_pk)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS housing;
CREATE TABLE housing (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  fromLims TINYINT UNSIGNED NOT NULL,
  lastUpdated DATE DEFAULT NULL,
  center_procedure_pk INT UNSIGNED NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS line;
CREATE TABLE line (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  colonyId VARCHAR(255) NOT NULL,
  datasourceShortName VARCHAR(40) NOT NULL,
  center_procedure_pk INT UNSIGNED NOT NULL,

  UNIQUE KEY colonyId_centerProcedurePk_uk (colonyId, center_procedure_pk),
  KEY colonyId_idx(colonyId),
  KEY center_procedure_pk_idx(center_procedure_pk)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS procedure_;
CREATE TABLE procedure_ (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  procedureId VARCHAR(255) NOT NULL,

  KEY procedureId_idx(procedureId)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS center_procedure;
CREATE TABLE center_procedure (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  center_pk INT UNSIGNED NOT NULL,
  procedure_pk INT UNSIGNED NOT NULL,

  UNIQUE KEY centerPk_procedurePk_uk (center_pk, procedure_pk),
  KEY center_pk_idx(center_pk),
  KEY procedure_pk_idx(procedure_pk)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS line_statuscode;
CREATE TABLE line_statuscode (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  line_pk INT UNSIGNED NOT NULL,
  statuscode_pk INT UNSIGNED NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS simpleParameter;
CREATE TABLE simpleParameter (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  sequenceId INTEGER DEFAULT NULL,
  unit VARCHAR(255) DEFAULT NULL,
  value VARCHAR(4096) DEFAULT NULL,
  procedure_pk INT UNSIGNED NOT NULL,

  KEY parameterId_idx(parameterId),
  KEY parameterStatus_idx(parameterStatus)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS ontologyParameter;
CREATE TABLE ontologyParameter (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  sequenceId INTEGER DEFAULT NULL,
  procedure_pk INT UNSIGNED NOT NULL,

  KEY parameterId_idx(parameterId),
  KEY parameterStatus_idx(parameterStatus)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS seriesParameter;
CREATE TABLE seriesParameter (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  sequenceId INTEGER DEFAULT NULL,
  procedure_pk INT UNSIGNED NOT NULL,

  KEY parameterId_idx(parameterId),
  KEY parameterStatus_idx(parameterStatus)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS mediaParameter;
CREATE TABLE mediaParameter (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  filetype VARCHAR(255) NOT NULL,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  URI VARCHAR(255) NOT NULL,
  procedure_pk INT UNSIGNED NOT NULL,

  KEY parameterId_idx(parameterId),
  KEY parameterStatus_idx(parameterStatus)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS ontologyParameterTerm;
CREATE TABLE ontologyParameterTerm (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  term VARCHAR(255) NOT NULL,
  ontologyParameter_pk INT UNSIGNED NOT NULL,
  UNIQUE KEY ontologyParameterPk_term_uk (ontologyParameter_pk, term)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS seriesParameterValue;
CREATE TABLE seriesParameterValue (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  incrementStatus VARCHAR(255) DEFAULT NULL,
  incrementValue VARCHAR(255) NOT NULL,
  value VARCHAR(4096) DEFAULT NULL,
  seriesParameter_pk INT UNSIGNED NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS mediaParameter_parameterAssociation;
CREATE TABLE mediaParameter_parameterAssociation (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  mediaParameter_pk INT UNSIGNED NOT NULL,
  parameterAssociation_pk INT UNSIGNED NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS mediaParameter_procedureMetadata;
CREATE TABLE mediaParameter_procedureMetadata (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  mediaParameter_pk INT UNSIGNED NOT NULL,
  procedureMetadata_pk INT UNSIGNED NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS parameterAssociation;
CREATE TABLE parameterAssociation (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  sequenceId INTEGER DEFAULT NULL,

  KEY parameterId_idx(parameterId),
  KEY sequenceId_idx(sequenceId)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS procedureMetadata;
CREATE TABLE procedureMetadata (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  sequenceId INTEGER DEFAULT NULL,
  value VARCHAR(4096) DEFAULT NULL,

  KEY parameterId_idx(parameterId),
  KEY parameterStatus_idx(parameterStatus)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS dimension;
CREATE TABLE dimension (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  id VARCHAR(255) NOT NULL,
  origin VARCHAR(255) NOT NULL,
  unit VARCHAR(255) DEFAULT NULL,
  value decimal(15, 10) NOT NULL,
  parameterAssociation_pk INT UNSIGNED NOT NULL,

  KEY id_idx(id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS mediaSampleParameter;
CREATE TABLE mediaSampleParameter (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  procedure_pk INT UNSIGNED NOT NULL,

  KEY parameterId_idx(parameterId),
  KEY parameterStatus_idx(parameterStatus)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS mediaSample;
CREATE TABLE mediaSample (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  localId VARCHAR(255) NOT NULL,
  mediaSampleParameter_pk INT UNSIGNED NOT NULL,

  KEY localId_idx(localId)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS mediaSection;
CREATE TABLE mediaSection (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  localId VARCHAR(255) NOT NULL,
  mediaSample_pk INT UNSIGNED NOT NULL,

  KEY localId_idx(localId)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS mediaFile;
CREATE TABLE mediaFile (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  fileType VARCHAR(255) NOT NULL,
  localId VARCHAR(255) NOT NULL,
  URI VARCHAR(255) NOT NULL,
  mediaSection_pk INT UNSIGNED NOT NULL,

  KEY localId_idx(localId)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS mediaFile_parameterAssociation;
CREATE TABLE mediaFile_parameterAssociation (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  mediaFile_pk INT UNSIGNED NOT NULL,
  parameterAssociation_pk INT UNSIGNED NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS mediaFile_procedureMetadata;
CREATE TABLE mediaFile_procedureMetadata (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  mediaFile_pk INT UNSIGNED NOT NULL,
  procedureMetadata_pk INT UNSIGNED NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS seriesMediaParameter;
CREATE TABLE seriesMediaParameter (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parameterId VARCHAR(255) NOT NULL,
  parameterStatus VARCHAR(255) DEFAULT NULL,
  procedure_pk INT UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS procedure_procedureMetadata;
CREATE TABLE procedure_procedureMetadata (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  procedure_pk INT UNSIGNED NOT NULL,
  procedureMetadata_pk INT UNSIGNED NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS seriesMediaParameterValue;
CREATE TABLE seriesMediaParameterValue (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  fileType VARCHAR(255) DEFAULT NULL,
  incrementValue VARCHAR(255) NOT NULL,
  URI VARCHAR(255) NOT NULL,
  seriesMediaParameter_pk INT UNSIGNED NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS seriesMediaParameterValue_parameterAssociation;
CREATE TABLE seriesMediaParameterValue_parameterAssociation (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  seriesMediaParameterValue_pk INT UNSIGNED NOT NULL,
  parameterAssociation_pk INT UNSIGNED NOT NULL,

  UNIQUE KEY smpvPk_paPk_uk (seriesMediaParameterValue_pk, parameterAssociation_pk)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS seriesMediaParameterValue_procedureMetadata;
CREATE TABLE seriesMediaParameterValue_procedureMetadata (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  seriesMediaParameterValue_pk INT UNSIGNED NOT NULL,
  procedureMetadata_pk INT UNSIGNED NOT NULL,

  UNIQUE KEY smpvPk_pmPk_uk (seriesMediaParameterValue_pk, procedureMetadata_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



*/



/**********************************************/
/* ALTER TABLE STATEMENTS TO ADD FOREIGN KEYS */
/**********************************************/


/*
ALTER TABLE experiment
  ADD CONSTRAINT experiment_center_procedure_pk FOREIGN KEY (center_procedure_pk) REFERENCES center_procedure (pk);

ALTER TABLE experiment_statuscode
  ADD CONSTRAINT experiment_statuscode_experiment_pk FOREIGN KEY (experiment_pk) REFERENCES experiment (pk),
  ADD CONSTRAINT experiment_statuscode_statuscode_pk FOREIGN KEY (statuscode_pk) REFERENCES statuscode (pk);

ALTER TABLE experiment_specimen
  ADD CONSTRAINT experiment_specimen_experiment_pk FOREIGN KEY (experiment_pk) REFERENCES experiment (pk),
  ADD CONSTRAINT experiment_specimen_specimen_pk FOREIGN KEY (specimen_pk) REFERENCES specimen (pk);

ALTER TABLE housing
  ADD CONSTRAINT housing_center_procedure_pk FOREIGN KEY (center_procedure_pk) REFERENCES center_procedure (pk);

ALTER TABLE line
  ADD CONSTRAINT line_center_procedure_pk FOREIGN KEY (center_procedure_pk) REFERENCES center_procedure (pk);

ALTER TABLE center_procedure
  ADD CONSTRAINT center_procedure_center_pk FOREIGN KEY (center_pk) REFERENCES center (pk),
  ADD CONSTRAINT center_procedure_procedure_pk FOREIGN KEY (procedure_pk) REFERENCES procedure_ (pk);

ALTER TABLE line_statuscode
  ADD CONSTRAINT line_statuscode_line_pk FOREIGN KEY (line_pk) REFERENCES line (pk),
  ADD CONSTRAINT line_statuscode_statuscode_pk FOREIGN KEY (statuscode_pk) REFERENCES statuscode (pk);

ALTER TABLE simpleParameter
  ADD CONSTRAINT simpleParameter_procedure_pk FOREIGN KEY (procedure_pk) REFERENCES procedure_ (pk);

ALTER TABLE ontologyParameter
  ADD CONSTRAINT ontologyParameter_procedure_pk FOREIGN KEY (procedure_pk) REFERENCES procedure_ (pk);

ALTER TABLE seriesParameter
  ADD CONSTRAINT seriesParameter_procedure_pk FOREIGN KEY (procedure_pk) REFERENCES procedure_ (pk);

ALTER TABLE mediaParameter
  ADD CONSTRAINT mediaParameter_procedure_pk FOREIGN KEY (procedure_pk) REFERENCES procedure_ (pk);

ALTER TABLE ontologyParameterTerm
  ADD CONSTRAINT ontologyParameterTerm_ontologyParameter_pk FOREIGN KEY (ontologyParameter_pk) REFERENCES ontologyParameter (pk);

ALTER TABLE seriesParameterValue
  ADD CONSTRAINT seriesParameterValue_seriesParameter_pk FOREIGN KEY (seriesParameter_pk) REFERENCES seriesParameter (pk);

ALTER TABLE mediaParameter_parameterAssociation
  ADD CONSTRAINT mediaParameter_parameterAssociation_mediaParameter_pk FOREIGN KEY (mediaParameter_pk) REFERENCES mediaParameter (pk),
  ADD CONSTRAINT mediaParameter_parameterAssociation_parameterAssociation_pk FOREIGN KEY (parameterAssociation_pk) REFERENCES parameterAssociation (pk);

ALTER TABLE mediaParameter_procedureMetadata
  ADD CONSTRAINT mediaParameter_procedureMetadata_mediaParameter_pk FOREIGN KEY (mediaParameter_pk) REFERENCES mediaParameter (pk),
  ADD CONSTRAINT mediaParameter_procedureMetadata_procedureMetadata_pk FOREIGN KEY (procedureMetadata_pk) REFERENCES procedureMetadata (pk);

ALTER TABLE dimension
  ADD CONSTRAINT dimension_parameterAssociation_pk FOREIGN KEY (parameterAssociation_pk) REFERENCES parameterAssociation (pk);

ALTER TABLE mediaSampleParameter
  ADD CONSTRAINT mediaSampleParameter_procedure_pk FOREIGN KEY (procedure_pk) REFERENCES procedure_ (pk);

ALTER TABLE mediaSample
  ADD CONSTRAINT mediaSample_mediaSampleParameter_pk FOREIGN KEY (mediaSampleParameter_pk) REFERENCES mediaSampleParameter (pk);

ALTER TABLE mediaSection
  ADD CONSTRAINT mediaSection_mediaSample_pk FOREIGN KEY (mediaSample_pk) REFERENCES mediaSample (pk);

ALTER TABLE mediaFile
  ADD CONSTRAINT mediaFile_mediaSection_pk FOREIGN KEY (mediaSection_pk) REFERENCES mediaSection (pk);

ALTER TABLE mediaFile_parameterAssociation
  ADD CONSTRAINT mediaFile_parameterAssociation_mediaFile_pk FOREIGN KEY (mediaFile_pk) REFERENCES mediaFile (pk),
  ADD CONSTRAINT mediaFile_parameterAssociation_parameterAssociation_pk FOREIGN KEY (parameterAssociation_pk) REFERENCES parameterAssociation (pk);

ALTER TABLE mediaFile_procedureMetadata
  ADD CONSTRAINT mediaFile_procedureMetadata_mediaFile_pk FOREIGN KEY (mediaFile_pk) REFERENCES mediaFile (pk),
  ADD CONSTRAINT mediaFile_procedureMetadata_procedureMetadata_pk FOREIGN KEY (procedureMetadata_pk) REFERENCES procedureMetadata (pk);

ALTER TABLE seriesMediaParameter
  ADD CONSTRAINT seriesMediaParameter_procedure_pk FOREIGN KEY (procedure_pk) REFERENCES procedure_ (pk);

ALTER TABLE procedure_procedureMetadata
  ADD CONSTRAINT procedure_procedureMetadata_procedure_pk FOREIGN KEY (procedure_pk) REFERENCES procedure_ (pk),
  ADD CONSTRAINT procedure_procedureMetadata_procedureMetadata_pk FOREIGN KEY (procedureMetadata_pk) REFERENCES procedureMetadata (pk);

ALTER TABLE seriesMediaParameterValue
  ADD CONSTRAINT seriesMediaParameterValue_seriesMediaParameter_pk FOREIGN KEY (seriesMediaParameter_pk) REFERENCES seriesMediaParameter (pk);

ALTER TABLE seriesMediaParameterValue_parameterAssociation
  ADD CONSTRAINT seriesMediaParameterValue_PA_seriesMediaParameterValue_pk FOREIGN KEY (seriesMediaParameterValue_pk) REFERENCES seriesMediaParameterValue (pk),
  ADD CONSTRAINT seriesMediaParameterValue_PA_parameterAssociation_pk FOREIGN KEY (parameterAssociation_pk) REFERENCES parameterAssociation (pk);

ALTER TABLE seriesMediaParameterValue_procedureMetadata
  ADD CONSTRAINT seriesMediaParameterValue_PM_seriesMediaParameterValue_pk FOREIGN KEY (seriesMediaParameterValue_pk) REFERENCES seriesMediaParameterValue (pk),
  ADD CONSTRAINT seriesMediaParameterValue_PM_procedureMetadata FOREIGN KEY (procedureMetadata_pk) REFERENCES procedureMetadata (pk);

SET @@FOREIGN_KEY_CHECKS = 1;

*/