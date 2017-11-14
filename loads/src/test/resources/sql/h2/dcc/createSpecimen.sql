/**************/
/*  specimen  */
/**************/
DROP TABLE IF EXISTS center;
CREATE TABLE center (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  centerId VARCHAR(255) NOT NULL,
  pipeline VARCHAR(255) NOT NULL,
  project VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS embryo;
CREATE TABLE embryo (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  stage VARCHAR(255) NOT NULL,
  stageUnit VARCHAR(255) NOT NULL,
  specimen_pk INT NOT NULL
);

DROP TABLE IF EXISTS mouse;
CREATE TABLE mouse (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  DOB DATE NOT NULL,
  specimen_pk INT NOT NULL
);

DROP TABLE IF EXISTS center_specimen;
CREATE TABLE center_specimen (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  center_pk INT NOT NULL,
  specimen_pk INT NOT NULL,

  UNIQUE (center_pk, specimen_pk)
);

DROP TABLE IF EXISTS relatedSpecimen;
CREATE TABLE relatedSpecimen (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  centerId VARCHAR(255) NOT NULL,
  specimenIdPk INT NOT NULL,
  specimenId VARCHAR(255) NOT NULL,
  relationship VARCHAR(255) NOT NULL,
  relatedSpecimenId VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS specimen;
CREATE TABLE specimen (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
  statuscode_pk INT DEFAULT NULL,

  UNIQUE (phenotypingCenter, specimenId)
);

DROP TABLE IF EXISTS genotype;
CREATE TABLE genotype (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  fatherZygosity VARCHAR(255) DEFAULT NULL,
  geneSymbol VARCHAR(255) NOT NULL,
  mgiAlleleId VARCHAR(255) NOT NULL,
  mgiGeneId VARCHAR(255) NOT NULL,
  motherZygosity VARCHAR(255) DEFAULT NULL,
  specimen_pk INT NOT NULL
);

DROP TABLE IF EXISTS parentalStrain;
CREATE TABLE parentalStrain (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  gender VARCHAR(255) NOT NULL,
  level INTEGER NOT NULL,
  mgiStrainId VARCHAR(255) NOT NULL,
  percentage DOUBLE NOT NULL,
  specimen_pk INT NOT NULL
);

DROP TABLE IF EXISTS statuscode;
CREATE TABLE statuscode (
  pk INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  dateOfStatuscode DATE DEFAULT NULL,
  value VARCHAR(255) NOT NULL
);