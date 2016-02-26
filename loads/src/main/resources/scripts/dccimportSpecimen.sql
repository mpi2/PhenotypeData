SET @@FOREIGN_KEY_CHECKS = 0;

/**************/
/*  specimen  */
/**************/
DROP TABLE IF EXISTS `center`;
CREATE TABLE `center` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `centerId` varchar(255) NOT NULL,
  `pipeline` varchar(255) NOT NULL,
  `project` varchar(255) NOT NULL,
  PRIMARY KEY (`pk`),
  UNIQUE KEY (centerId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `embryo`;
CREATE TABLE `embryo` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `stage` varchar(255) NOT NULL,
  `stageUnit` varchar(255) NOT NULL,
  `specimen_fk` bigint(20) NOT NULL,
  PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mouse`;
CREATE TABLE `mouse` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `DOB` date NOT NULL,
  `specimen_fk` bigint(20) NOT NULL,
  PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `center_specimen`;
CREATE TABLE `center_specimen` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `center_fk` bigint(20) NOT NULL,
  `specimen_fk` bigint(20) NOT NULL,
  PRIMARY KEY (`pk`),
  UNIQUE KEY centerFk_specimenFk_uk (center_fk, specimen_fk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `chromosome`;
/*
CREATE TABLE `chromosome` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `start` varchar(255) NOT NULL,
  `end` varchar(255) NOT NULL,
  `species` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
*/

DROP TABLE IF EXISTS `relatedSpecimen`;
CREATE TABLE `relatedSpecimen` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `relationship` varchar(255) NOT NULL,
  `specimenIdMine` varchar(255) NOT NULL,
  `specimen_mine_fk` bigint(20) DEFAULT NULL,
  `specimen_theirs_fk` bigint(20) NOT NULL,
  PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `specimen`;
CREATE TABLE `specimen` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `colonyId` varchar(255) DEFAULT NULL,
  `gender` varchar(255) NOT NULL,
  `isBaseline` tinyint(1) NOT NULL,
  `litterId` varchar(255) NOT NULL,
  `phenotypingCenter` varchar(255) NOT NULL,
  `pipeline` varchar(255) NOT NULL,
  `productionCenter` varchar(255) DEFAULT NULL,
  `project` varchar(255) NOT NULL,
  `specimenId` varchar(255) NOT NULL,
  `strainId` varchar(255) DEFAULT NULL,
  `zygosity` varchar(255) NOT NULL,
  `statuscode_fk` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`pk`),
  KEY `colonyIdIndex` (`colonyId`),
  KEY `specimenIdIndex` (`specimenId`),
  KEY `strainIdIndex` (`strainId`),
  KEY `statuscode_fk` (`statuscode_fk`),
  UNIQUE KEY specimenId_uk (specimenId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `chromosomalAlteration`;
/*
CREATE TABLE `chromosomalAlteration` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `chromosome_added_fk` bigint(20) DEFAULT NULL,
  `chromosome_removed_fk` bigint(20) DEFAULT NULL,
  `specimen_fk` bigint(20) NOT NULL,
  PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
*/

DROP TABLE IF EXISTS `genotype`;
CREATE TABLE `genotype` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `mgiAlleleId` varchar(255) NOT NULL,
  `mgiGeneId` varchar(255) NOT NULL,
  `fatherZygosity` varchar(255) DEFAULT NULL,
  `geneSymbol` varchar(255) NOT NULL,
  `motherZygosity` varchar(255) DEFAULT NULL,
  `specimen_fk` bigint(20) NOT NULL,
  PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `parentalStrain`;
CREATE TABLE `parentalStrain` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `percentage` DOUBLE NOT NULL,
  `mgiStrainId` varchar(255) NOT NULL,
  `gender` varchar(255) NOT NULL,
  `level` INTEGER NOT NULL,
  `specimen_fk` bigint(20) NOT NULL,
  PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `statuscode`;
CREATE TABLE `statuscode` (
  `pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `dateOfStatuscode` date DEFAULT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*******************************************************/
/* specimen ALTER TABLE STATEMENTS TO ADD FOREIGN KEYS */
/*******************************************************/
ALTER TABLE embryo
ADD CONSTRAINT `embryo_specimen_fk` FOREIGN KEY (`specimen_fk`) REFERENCES `specimen` (`pk`);

ALTER TABLE mouse
ADD CONSTRAINT `mouse_specimen_fk` FOREIGN KEY (`specimen_fk`) REFERENCES `specimen` (`pk`);

ALTER TABLE center_specimen
ADD CONSTRAINT `center_specimen_center_fk` FOREIGN KEY (`center_fk`) REFERENCES `center` (`pk`),
ADD CONSTRAINT `center_specimen_specimen_fk` FOREIGN KEY (`specimen_fk`) REFERENCES `specimen` (`pk`);

ALTER TABLE relatedSpecimen
ADD CONSTRAINT `relatedSpecimen_specimen_mine_fk` FOREIGN KEY (`specimen_mine_fk`) REFERENCES `specimen` (`pk`),
ADD CONSTRAINT `relatedSpecimen_specimen_theirs_fk` FOREIGN KEY (`specimen_theirs_fk`) REFERENCES specimen (`pk`);

ALTER TABLE specimen
ADD CONSTRAINT `specimen_statuscode_fk` FOREIGN KEY (`statuscode_fk`) REFERENCES `statuscode` (`pk`);

/*
ALTER TABLE chromosomalAlteration
ADD CONSTRAINT `chromosomalAlteration_chromosome_added_fk` FOREIGN KEY (`chromosome_added_fk`) REFERENCES `chromosome` (`pk`),
ADD CONSTRAINT `chromosomalAlteration_chromosome_removed_fk` FOREIGN KEY (`chromosome_removed_fk`) REFERENCES `chromosome` (`pk`),
ADD CONSTRAINT `chromosomalAlteration_specimen_fk` FOREIGN KEY (`specimen_fk`) REFERENCES `specimen` (`pk`);
*/

ALTER TABLE genotype
ADD CONSTRAINT `genotype_specimen_fk` FOREIGN KEY (`specimen_fk`) REFERENCES `specimen` (`pk`);

ALTER TABLE parentalStrain
ADD CONSTRAINT `parentalStrain_specimen_fk` FOREIGN KEY (`specimen_fk`) REFERENCES `specimen` (`pk`);

SET @@FOREIGN_KEY_CHECKS = 1;
