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
`center_procedure_pk` bigint(20) NOT NULL,
PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `experiment_statuscode`;
CREATE TABLE `experiment_statuscode` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `experiment_pk` bigint(20) NOT NULL,
 `statuscode_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY experimentPk_statuscodePk_uk (experiment_pk, statuscode_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `experiment_specimen`;
CREATE TABLE `experiment_specimen` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `experiment_pk` bigint(20) NOT NULL,
 `specimen_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY experimentPk_specimenPk_uk (experiment_pk, specimen_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `housing`;
CREATE TABLE `housing` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `fromLims` tinyint NOT NULL,
 `lastUpdated` date DEFAULT NULL,
 `center_procedure_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `line`;
CREATE TABLE `line` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `colonyId` varchar(255) NOT NULL,
 `center_procedure_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY colonyId_centerProcedurePk_uk (colonyId, center_procedure_pk)
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
 `center_pk` bigint(20) NOT NULL,
 `procedure_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY centerPk_procedurePk_uk (center_pk, procedure_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `line_statuscode`;
CREATE TABLE `line_statuscode` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `line_pk` bigint(20) NOT NULL,
 `statuscode_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `simpleParameter`;
CREATE TABLE `simpleParameter` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `sequenceId` INTEGER DEFAULT NULL,
 `unit` varchar(255) DEFAULT NULL,
 `value` varchar(4096) DEFAULT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `procedure_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ontologyParameter`;
CREATE TABLE `ontologyParameter` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `sequenceId` INTEGER DEFAULT NULL,
 `procedure_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `seriesParameter`;
CREATE TABLE `seriesParameter` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `sequenceId` INTEGER DEFAULT NULL,
 `procedure_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaParameter`;
CREATE TABLE `mediaParameter` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `filetype` varchar(255) NOT NULL,
 `URI` varchar(255) NOT NULL,
 `procedure_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ontologyParameterTerm`;
CREATE TABLE `ontologyParameterTerm` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `term` varchar(255) NOT NULL,
 `ontologyParameter_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY ontologyParameterPk_term_uk (ontologyParameter_pk, term)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `seriesParameterValue`;
CREATE TABLE `seriesParameterValue` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `value` varchar(4096) DEFAULT NULL,
 `incrementValue` varchar(255) NOT NULL,
 `incrementStatus` varchar(255) DEFAULT NULL,
 `seriesParameter_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaParameter_parameterAssociation`;
CREATE TABLE `mediaParameter_parameterAssociation` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `mediaParameter_pk` bigint(20) NOT NULL,
 `parameterAssociation_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaParameter_procedureMetadata`;
CREATE TABLE `mediaParameter_procedureMetadata` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `mediaParameter_pk` bigint(20) NOT NULL,
 `procedureMetadata_pk` bigint(20) NOT NULL,
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
 `value` varchar(4096) DEFAULT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `dimension`;
CREATE TABLE `dimension` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `id` varchar(255) NOT NULL,
 `origin` varchar(255) NOT NULL,
 `unit` varchar(255) DEFAULT NULL,
 `value` decimal(15, 10) NOT NULL,
 `parameterAssociation_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaSampleParameter`;
CREATE TABLE `mediaSampleParameter` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `procedure_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaSample`;
CREATE TABLE `mediaSample` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `localId` varchar(255) NOT NULL,
 `mediaSampleParameter_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaSection`;
CREATE TABLE `mediaSection` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `localId` varchar(255) NOT NULL,
 `mediaSample_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaFile`;
CREATE TABLE `mediaFile` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `localId` varchar(255) NOT NULL,
 `fileType` varchar(255) NOT NULL,
 `URI` varchar(255) NOT NULL,
 `mediaSection_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaFile_parameterAssociation`;
CREATE TABLE `mediaFile_parameterAssociation` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `mediaFile_pk` bigint(20) NOT NULL,
 `parameterAssociation_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mediaFile_procedureMetadata`;
CREATE TABLE `mediaFile_procedureMetadata` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `mediaFile_pk` bigint(20) NOT NULL,
 `procedureMetadata_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `seriesMediaParameter`;
CREATE TABLE `seriesMediaParameter` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `parameterId` varchar(255) NOT NULL,
 `parameterStatus` varchar(255) DEFAULT NULL,
 `procedure_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `procedure_procedureMetadata`;
CREATE TABLE `procedure_procedureMetadata` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `procedure_pk` bigint(20) NOT NULL,
 `procedureMetadata_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `seriesMediaParameterValue`;
CREATE TABLE `seriesMediaParameterValue` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `incrementValue` varchar(255) NOT NULL,
 `URI` varchar(255) NOT NULL,
 `fileType` varchar(255) DEFAULT NULL,
 `seriesMediaParameter_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `seriesMediaParameterValue_parameterAssociation`;
CREATE TABLE `seriesMediaParameterValue_parameterAssociation` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `seriesMediaParameterValue_pk` bigint(20) NOT NULL,
 `parameterAssociation_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY smpvPk_paPk_uk (seriesMediaParameterValue_pk, parameterAssociation_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `seriesMediaParameterValue_procedureMetadata`;
CREATE TABLE `seriesMediaParameterValue_procedureMetadata` (
 `pk` bigint(20) NOT NULL AUTO_INCREMENT,
 `seriesMediaParameterValue_pk` bigint(20) NOT NULL,
 `procedureMetadata_pk` bigint(20) NOT NULL,
 PRIMARY KEY (`pk`),
 UNIQUE KEY smpvPk_pmPk_uk (seriesMediaParameterValue_pk, procedureMetadata_pk)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/**********************************************/
/* ALTER TABLE STATEMENTS TO ADD FOREIGN KEYS */
/**********************************************/
ALTER TABLE experiment
ADD CONSTRAINT `experiment_center_procedure_pk` FOREIGN KEY (`center_procedure_pk`) REFERENCES `center_procedure` (`pk`);

ALTER TABLE experiment_statuscode
ADD CONSTRAINT `experiment_statuscode_experiment_pk` FOREIGN KEY (`experiment_pk`) REFERENCES `experiment` (`pk`),
ADD CONSTRAINT `experiment_statuscode_statuscode_pk` FOREIGN KEY (`statuscode_pk`) REFERENCES `statuscode` (`pk`);

ALTER TABLE experiment_specimen
ADD CONSTRAINT `experiment_specimen_experiment_pk` FOREIGN KEY (`experiment_pk`) REFERENCES `experiment` (`pk`),
ADD CONSTRAINT `experiment_specimen_specimen_pk` FOREIGN KEY (`specimen_pk`) REFERENCES `specimen` (`pk`);

ALTER TABLE housing
ADD CONSTRAINT `housing_center_procedure_pk` FOREIGN KEY (`center_procedure_pk`) REFERENCES `center_procedure` (`pk`);

ALTER TABLE line
ADD CONSTRAINT `line_center_procedure_pk` FOREIGN KEY (`center_procedure_pk`) REFERENCES `center_procedure` (`pk`);

ALTER TABLE center_procedure
ADD CONSTRAINT `center_procedure_center_pk` FOREIGN KEY (`center_pk`) REFERENCES `center` (`pk`),
ADD CONSTRAINT `center_procedure_procedure_pk` FOREIGN KEY (`procedure_pk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE line_statuscode
ADD CONSTRAINT `line_statuscode_line_pk` FOREIGN KEY (`line_pk`) REFERENCES `line` (`pk`),
ADD CONSTRAINT `line_statuscode_statuscode_pk` FOREIGN KEY (`statuscode_pk`) REFERENCES `statuscode` (`pk`);

ALTER TABLE simpleParameter
ADD CONSTRAINT `simpleParameter_procedure_pk` FOREIGN KEY (`procedure_pk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE ontologyParameter
ADD CONSTRAINT `ontologyParameter_procedure_pk` FOREIGN KEY (`procedure_pk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE seriesParameter
ADD CONSTRAINT `seriesParameter_procedure_pk` FOREIGN KEY (`procedure_pk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE mediaParameter
ADD CONSTRAINT `mediaParameter_procedure_pk` FOREIGN KEY (`procedure_pk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE ontologyParameterTerm
ADD CONSTRAINT `ontologyParameterTerm_ontologyParameter_pk` FOREIGN KEY (`ontologyParameter_pk`) REFERENCES `ontologyParameter` (`pk`);

ALTER TABLE seriesParameterValue
ADD CONSTRAINT `seriesParameterValue_seriesParameter_pk` FOREIGN KEY (`seriesParameter_pk`) REFERENCES `seriesParameter` (`pk`);

ALTER TABLE mediaParameter_parameterAssociation
ADD CONSTRAINT `mediaParameter_parameterAssociation_mediaParameter_pk` FOREIGN KEY (`mediaParameter_pk`) REFERENCES `mediaParameter` (`pk`),
ADD CONSTRAINT `mediaParameter_parameterAssociation_parameterAssociation_pk` FOREIGN KEY (`parameterAssociation_pk`) REFERENCES `parameterAssociation` (`pk`);

ALTER TABLE mediaParameter_procedureMetadata
ADD CONSTRAINT `mediaParameter_procedureMetadata_mediaParameter_pk` FOREIGN KEY (`mediaParameter_pk`) REFERENCES `mediaParameter` (`pk`),
ADD CONSTRAINT `mediaParameter_procedureMetadata_procedureMetadata_pk` FOREIGN KEY (`procedureMetadata_pk`) REFERENCES `procedureMetadata` (`pk`);

ALTER TABLE dimension
ADD CONSTRAINT `dimension_parameterAssociation_pk` FOREIGN KEY (`parameterAssociation_pk`) REFERENCES `parameterAssociation` (`pk`);

ALTER TABLE mediaSampleParameter
ADD CONSTRAINT `mediaSampleParameter_procedure_pk` FOREIGN KEY (`procedure_pk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE mediaSample
ADD CONSTRAINT `mediaSample_mediaSampleParameter_pk` FOREIGN KEY (`mediaSampleParameter_pk`) REFERENCES `mediaSampleParameter` (`pk`);

ALTER TABLE mediaSection
ADD CONSTRAINT `mediaSection_mediaSample_pk` FOREIGN KEY (`mediaSample_pk`) REFERENCES `mediaSample` (`pk`);

ALTER TABLE mediaFile
ADD CONSTRAINT `mediaFile_mediaSection_pk` FOREIGN KEY (`mediaSection_pk`) REFERENCES `mediaSection` (`pk`);

ALTER TABLE mediaFile_parameterAssociation
ADD CONSTRAINT `mediaFile_parameterAssociation_mediaFile_pk` FOREIGN KEY (`mediaFile_pk`) REFERENCES `mediaFile` (`pk`),
ADD CONSTRAINT `mediaFile_parameterAssociation_parameterAssociation_pk` FOREIGN KEY (`parameterAssociation_pk`) REFERENCES `parameterAssociation` (`pk`);

ALTER TABLE mediaFile_procedureMetadata
ADD CONSTRAINT `mediaFile_procedureMetadata_mediaFile_pk` FOREIGN KEY (`mediaFile_pk`) REFERENCES `mediaFile` (`pk`),
ADD CONSTRAINT `mediaFile_procedureMetadata_procedureMetadata_pk` FOREIGN KEY (`procedureMetadata_pk`) REFERENCES `procedureMetadata` (`pk`);

ALTER TABLE seriesMediaParameter
ADD CONSTRAINT `seriesMediaParameter_procedure_pk` FOREIGN KEY (`procedure_pk`) REFERENCES `procedure_` (`pk`);

ALTER TABLE procedure_procedureMetadata
ADD CONSTRAINT `procedure_procedureMetadata_procedure_pk` FOREIGN KEY (`procedure_pk`) REFERENCES `procedure_` (`pk`),
ADD CONSTRAINT `procedure_procedureMetadata_procedureMetadata_pk` FOREIGN KEY (`procedureMetadata_pk`) REFERENCES `procedureMetadata` (`pk`);

ALTER TABLE seriesMediaParameterValue
ADD CONSTRAINT `seriesMediaParameterValue_seriesMediaParameter_pk` FOREIGN KEY (`seriesMediaParameter_pk`) REFERENCES `seriesMediaParameter` (`pk`);

ALTER TABLE seriesMediaParameterValue_parameterAssociation
ADD CONSTRAINT `seriesMediaParameterValue_PA_seriesMediaParameterValue_pk` FOREIGN KEY (`seriesMediaParameterValue_pk`) REFERENCES `seriesMediaParameterValue` (`pk`),
ADD CONSTRAINT `seriesMediaParameterValue_PA_parameterAssociation_pk` FOREIGN KEY (`parameterAssociation_pk`) REFERENCES `parameterAssociation` (`pk`);

ALTER TABLE seriesMediaParameterValue_procedureMetadata
ADD CONSTRAINT `seriesMediaParameterValue_PM_seriesMediaParameterValue_pk` FOREIGN KEY (`seriesMediaParameterValue_pk`) REFERENCES `seriesMediaParameterValue` (`pk`),
ADD CONSTRAINT `seriesMediaParameterValue_PM_procedureMetadata` FOREIGN KEY (`procedureMetadata_pk`) REFERENCES `procedureMetadata` (`pk`);

SET @@FOREIGN_KEY_CHECKS = 1;
