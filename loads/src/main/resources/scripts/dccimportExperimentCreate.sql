SET @@FOREIGN_KEY_CHECKS = 0;

/****************/
/*  experiment  */
/****************/
DROP TABLE IF EXISTS `experiment`;
CREATE TABLE `experiment` (
`pk` bigint(20) NOT NULL AUTO_INCREMENT,
`dateOfExperiment` date NOT NULL,
`experimentId` varchar(255) NOT NULL,
`sequenceId` VARCHAR(255) DEFAULT NULL,
`center_procedure_fk` bigint(20) NOT NULL,
PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `experiment_statuscode`;
CREATE TABLE `experiment_statuscode` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `experiment_fk` bigint(20) NOT NULL,
 `statuscode_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY experimentFk_statuscodeFk_uk (experiment_fk, statuscode_fk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `experiment_specimen`;
CREATE TABLE `experiment_specimen` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `experiment_fk` bigint(20) NOT NULL,
 `specimen_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY experimentFk_specimenFk_uk (experiment_fk, specimen_fk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `housing`;
CREATE TABLE `housing` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `fromLims` tinyint NOT NULL,
 `lastUpdated` date DEFAULT NULL,
 `center_procedure_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `line`;
CREATE TABLE `line` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `colonyId` varchar(255) NOT NULL,
 `center_procedure_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY colonyId_centerProcedureFk_uk (colonyId, center_procedure_fk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `procedure_`;
CREATE TABLE `procedure_` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `procedureId` varchar(255) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `center_procedure`;
CREATE TABLE `center_procedure` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `center_fk` bigint(20) NOT NULL,
 `procedure_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY centerFk_procedureFk_uk (center_fk, procedure_fk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `line_statuscode`;
CREATE TABLE `line_statuscode` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `line_fk` bigint(20) NOT NULL,
 `statuscode_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `simpleParameter`;
CREATE TABLE `simpleParameter` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `sequenceId` INTEGER DEFAULT NULL,
 `unit` varchar(255) DEFAULT NULL,
 `value` varchar(255) DEFAULT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `procedure_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ontologyParameter`;
CREATE TABLE `ontologyParameter` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `sequenceId` INTEGER DEFAULT NULL,
 `procedure_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `seriesParameter`;
CREATE TABLE `seriesParameter` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `sequenceId` INTEGER DEFAULT NULL,
 `procedure_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaParameter`;
CREATE TABLE `mediaParameter` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `filetype` varchar(255) NOT NULL,
 `URI` varchar(255) NOT NULL,
 `procedure_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ontologyParameterTerm`;
CREATE TABLE `ontologyParameterTerm` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `term` varchar(255) NOT NULL,
 `ontologyParameter_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY ontologyParameterFk_term_uk (ontologyParameter_fk, term)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `seriesParameterValue`;
CREATE TABLE `seriesParameterValue` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `value` varchar(255) DEFAULT NULL,
 `incrementValue` varchar(255) NOT NULL,
 `incrementStatus` varchar(255) DEFAULT NULL,
 `seriesParameter_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaParameter_parameterAssociation`;
CREATE TABLE `mediaParameter_parameterAssociation` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `mediaParameter_fk` bigint(20) NOT NULL,
 `parameterAssociation_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaParameter_procedureMetadata`;
CREATE TABLE `mediaParameter_procedureMetadata` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `mediaParameter_fk` bigint(20) NOT NULL,
 `procedureMetadata_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `parameterAssociation`;
CREATE TABLE `parameterAssociation` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `sequenceId` INTEGER DEFAULT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `procedureMetadata`;
CREATE TABLE `procedureMetadata` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `sequenceId` INTEGER DEFAULT NULL,
 `value` varchar(255) DEFAULT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dimension`;
CREATE TABLE `dimension` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `value` varchar(255) NOT NULL,
 `id` varchar(255) NOT NULL,
 `origin` varchar(255) NOT NULL,
 `unit` varchar(255) DEFAULT NULL,
 `parameterAssociation_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaSampleParameter`;
CREATE TABLE `mediaSampleParameter` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `procedure_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaSample`;
CREATE TABLE `mediaSample` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `localId` varchar(255) NOT NULL,
 `mediaSampleParameter_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaSection`;
CREATE TABLE `mediaSection` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `localId` varchar(255) NOT NULL,
 `mediaSample_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaFile`;
CREATE TABLE `mediaFile` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `localId` varchar(255) NOT NULL,
 `fileType` varchar(255) NOT NULL,
 `URI` varchar(255) NOT NULL,
 `mediaSection_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaFile_parameterAssociation`;
CREATE TABLE `mediaFile_parameterAssociation` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `mediaFile_fk` bigint(20) NOT NULL,
 `parameterAssociation_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaFile_procedureMetadata`;
CREATE TABLE `mediaFile_procedureMetadata` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `mediaFile_fk` bigint(20) NOT NULL,
 `procedureMetadata_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `seriesMediaParameter`;
CREATE TABLE `seriesMediaParameter` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `procedure_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `procedure_procedureMetadata`;
CREATE TABLE `procedure_procedureMetadata` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `procedure_fk` bigint(20) NOT NULL,
 `procedureMetadata_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `seriesMediaParameterValue`;
CREATE TABLE `seriesMediaParameterValue` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `incrementValue` varchar(255) NOT NULL,
 `URI` varchar(255) NOT NULL,
 `fileType` varchar(255) DEFAULT NULL,
 `seriesMediaParameter_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `seriesMediaParameterValue_parameterAssociation`;
CREATE TABLE `seriesMediaParameterValue_parameterAssociation` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `seriesMediaParameterValue_fk` bigint(20) NOT NULL,
 `parameterAssociation_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY smpvFk_paFk_uk (seriesMediaParameterValue_fk, parameterAssociation_fk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `seriesMediaParameterValue_procedureMetadata`;
CREATE TABLE `seriesMediaParameterValue_procedureMetadata` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `seriesMediaParameterValue_fk` bigint(20) NOT NULL,
 `procedureMetadata_fk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY smpvFk_pmFk_uk (seriesMediaParameterValue_fk, procedureMetadata_fk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/**********************************************/
/* ALTER TABLE STATEMENTS TO ADD FOREIGN KEYS */
/**********************************************/
ALTER TABLE experiment
ADD CONSTRAINT `experiment_center_procedure_fk` FOREIGN KEY (`center_procedure_fk`) REFERENCES `center_procedure` (`pk`);

ALTER TABLE experiment_statuscode
ADD CONSTRAINT `experiment_statuscode_experiment_fk` FOREIGN KEY (`experiment_fk`) REFERENCES `experiment` (`pk`),
ADD CONSTRAINT `experiment_statuscode_statuscode_fk` FOREIGN KEY (`statuscode_fk`) REFERENCES `statuscode` (`pk`);

ALTER TABLE experiment_specimen
ADD CONSTRAINT `experiment_specimen_experiment_fk` FOREIGN KEY (`experiment_fk`) REFERENCES `experiment` (`pk`),
ADD CONSTRAINT `experiment_specimen_specimen_fk` FOREIGN KEY (`specimen_fk`) REFERENCES `specimen` (`pk`);

ALTER TABLE housing
ADD CONSTRAINT `housing_center_procedure_fk` FOREIGN KEY (`center_procedure_fk`) REFERENCES `center_procedure` (`pk`);

ALTER TABLE line
ADD CONSTRAINT `line_center_procedure_fk` FOREIGN KEY (`center_procedure_fk`) REFERENCES `center_procedure` (`pk`);

ALTER TABLE center_procedure
ADD CONSTRAINT `center_procedure_center_fk` FOREIGN KEY (`center_fk`) REFERENCES `center` (`pk`),
ADD CONSTRAINT `center_procedure_procedure_fk` FOREIGN KEY (`procedure_fk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE line_statuscode
ADD CONSTRAINT `line_statuscode_line_fk` FOREIGN KEY (`line_fk`) REFERENCES `line` (`pk`),
ADD CONSTRAINT `line_statuscode_statuscode_fk` FOREIGN KEY (`statuscode_fk`) REFERENCES `statuscode` (`pk`);

ALTER TABLE simpleParameter
ADD CONSTRAINT `simpleParameter_procedure_fk` FOREIGN KEY (`procedure_fk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE ontologyParameter
ADD CONSTRAINT `ontologyParameter_procedure_fk` FOREIGN KEY (`procedure_fk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE seriesParameter
ADD CONSTRAINT `seriesParameter_procedure_fk` FOREIGN KEY (`procedure_fk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE mediaParameter
ADD CONSTRAINT `mediaParameter_procedure_fk` FOREIGN KEY (`procedure_fk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE ontologyParameterTerm
ADD CONSTRAINT `ontologyParameterTerm_ontologyParameter_fk` FOREIGN KEY (`ontologyParameter_fk`) REFERENCES `ontologyParameter` (`pk`);

ALTER TABLE seriesParameterValue
ADD CONSTRAINT `seriesParameterValue_seriesParameter_fk` FOREIGN KEY (`seriesParameter_fk`) REFERENCES `seriesParameter` (`pk`);

ALTER TABLE mediaParameter_parameterAssociation
ADD CONSTRAINT `mediaParameter_parameterAssociation_mediaParameter_fk` FOREIGN KEY (`mediaParameter_fk`) REFERENCES `mediaParameter` (`pk`),
ADD CONSTRAINT `mediaParameter_parameterAssociation_parameterAssociation_fk` FOREIGN KEY (`parameterAssociation_fk`) REFERENCES `parameterAssociation` (`pk`);

ALTER TABLE mediaParameter_procedureMetadata
ADD CONSTRAINT `mediaParameter_procedureMetadata_mediaParameter_fk` FOREIGN KEY (`mediaParameter_fk`) REFERENCES `mediaParameter` (`pk`),
ADD CONSTRAINT `mediaParameter_procedureMetadata_procedureMetadata_fk` FOREIGN KEY (`procedureMetadata_fk`) REFERENCES `procedureMetadata` (`pk`);

ALTER TABLE dimension
ADD CONSTRAINT `dimension_parameterAssociation_fk` FOREIGN KEY (`parameterAssociation_fk`) REFERENCES `parameterAssociation` (`pk`);

ALTER TABLE mediaSampleParameter
ADD CONSTRAINT `mediaSampleParameter_procedure_fk` FOREIGN KEY (`procedure_fk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE mediaSample
ADD CONSTRAINT `mediaSample_mediaSampleParameter_fk` FOREIGN KEY (`mediaSampleParameter_fk`) REFERENCES `mediaSampleParameter` (`pk`);

ALTER TABLE mediaSection
ADD CONSTRAINT `mediaSection_mediaSample_fk` FOREIGN KEY (`mediaSample_fk`) REFERENCES `mediaSample` (`pk`);

ALTER TABLE mediaFile
ADD CONSTRAINT `mediaFile_mediaSection_fk` FOREIGN KEY (`mediaSection_fk`) REFERENCES `mediaSection` (`pk`);

ALTER TABLE mediaFile_parameterAssociation
ADD CONSTRAINT `mediaFile_parameterAssociation_mediaFile_fk` FOREIGN KEY (`mediaFile_fk`) REFERENCES `mediaFile` (`pk`),
ADD CONSTRAINT `mediaFile_parameterAssociation_parameterAssociation_fk` FOREIGN KEY (`parameterAssociation_fk`) REFERENCES `parameterAssociation` (`pk`);

ALTER TABLE mediaFile_procedureMetadata
ADD CONSTRAINT `mediaFile_procedureMetadata_mediaFile_fk` FOREIGN KEY (`mediaFile_fk`) REFERENCES `mediaFile` (`pk`),
ADD CONSTRAINT `mediaFile_procedureMetadata_procedureMetadata_fk` FOREIGN KEY (`procedureMetadata_fk`) REFERENCES `procedureMetadata` (`pk`);

ALTER TABLE seriesMediaParameter
ADD CONSTRAINT `seriesMediaParameter_procedure_fk` FOREIGN KEY (`procedure_fk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE procedure_procedureMetadata
ADD CONSTRAINT `procedure_procedureMetadata_procedure_fk` FOREIGN KEY (`procedure_fk`) REFERENCES `procedure_` (`pk`),
ADD CONSTRAINT `procedure_procedureMetadata_procedureMetadata_fk` FOREIGN KEY (`procedureMetadata_fk`) REFERENCES `procedureMetadata` (`pk`);

ALTER TABLE seriesMediaParameterValue
ADD CONSTRAINT `seriesMediaParameterValue_seriesMediaParameter_fk` FOREIGN KEY (`seriesMediaParameter_fk`) REFERENCES `seriesMediaParameter` (`pk`);

ALTER TABLE seriesMediaParameterValue_parameterAssociation
ADD CONSTRAINT `seriesMediaParameterValue_PA_seriesMediaParameterValue_fk` FOREIGN KEY (`seriesMediaParameterValue_fk`) REFERENCES `seriesMediaParameterValue` (`pk`),
ADD CONSTRAINT `seriesMediaParameterValue_PA_parameterAssociation_fk` FOREIGN KEY (`parameterAssociation_fk`) REFERENCES `parameterAssociation` (`pk`);

ALTER TABLE seriesMediaParameterValue_procedureMetadata
ADD CONSTRAINT `seriesMediaParameterValue_PM_seriesMediaParameterValue_fk` FOREIGN KEY (`seriesMediaParameterValue_fk`) REFERENCES `seriesMediaParameterValue` (`pk`),
ADD CONSTRAINT `seriesMediaParameterValue_PM_procedureMetadata` FOREIGN KEY (`procedureMetadata_fk`) REFERENCES `procedureMetadata` (`pk`);

SET @@FOREIGN_KEY_CHECKS = 1;
