SET @@FOREIGN_KEY_CHECKS = 0;

/**************/
/*  specimen  */
/**************/
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

DROP TABLE IF EXISTS chromosome;
/*
CREATE TABLE chromosome (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  start VARCHAR(255) NOT NULL,
  end VARCHAR(255) NOT NULL,
  species VARCHAR(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
*/

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

DROP TABLE IF EXISTS chromosomalAlteration;
/*
CREATE TABLE chromosomalAlteration (
  pk INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  chromosome_added_pk INT UNSIGNED DEFAULT NULL,
  chromosome_removed_pk INT UNSIGNED DEFAULT NULL,
  specimen_pk INT UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
*/

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

/*******************************************************/
/* specimen ALTER TABLE STATEMENTS TO ADD FOREIGN KEYS */
/*******************************************************/
ALTER TABLE embryo
ADD CONSTRAINT embryo_specimen_pk FOREIGN KEY (specimen_pk) REFERENCES specimen (pk);

ALTER TABLE mouse
ADD CONSTRAINT mouse_specimen_pk FOREIGN KEY (specimen_pk) REFERENCES specimen (pk);

ALTER TABLE center_specimen
ADD CONSTRAINT center_specimen_center_pk FOREIGN KEY (center_pk) REFERENCES center (pk),
ADD CONSTRAINT center_specimen_specimen_pk FOREIGN KEY (specimen_pk) REFERENCES specimen (pk);

ALTER TABLE specimen
ADD CONSTRAINT specimen_statuscode_pk FOREIGN KEY (statuscode_pk) REFERENCES statuscode (pk);

ALTER TABLE center ADD INDEX center_pipeline_project (centerId, pipeline, project);
ALTER TABLE center ADD UNIQUE KEY center_pipeline_project_unique (centerId, pipeline, project);

/*
ALTER TABLE chromosomalAlteration
ADD CONSTRAINT chromosomalAlteration_chromosome_added_pk FOREIGN KEY (chromosome_added_pk) REFERENCES chromosome (pk),
ADD CONSTRAINT chromosomalAlteration_chromosome_removed_pk FOREIGN KEY (chromosome_removed_pk) REFERENCES chromosome (pk),
ADD CONSTRAINT chromosomalAlteration_specimen_pk FOREIGN KEY (specimen_pk) REFERENCES specimen (pk);
*/

ALTER TABLE genotype
ADD CONSTRAINT genotype_specimen_pk FOREIGN KEY (specimen_pk) REFERENCES specimen (pk);

ALTER TABLE parentalStrain
ADD CONSTRAINT parentalStrain_specimen_pk FOREIGN KEY (specimen_pk) REFERENCES specimen (pk);

SET @@FOREIGN_KEY_CHECKS = 1;
