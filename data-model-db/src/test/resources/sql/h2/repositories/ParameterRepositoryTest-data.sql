INSERT INTO phenotype_parameter
    (id, stable_id, db_id, name, description, major_version, minor_version, unit, datatype, parameter_type, formula, required, metadata, important, derived, annotate, increment, options, sequence, media, data_analysis_notes, stable_key)
VALUES
    (8,   'IMPC_BWT_008_001', 6, 'Body weight curve', 'Body weight curve DESCRIPTION', 1, 1, 'g', 'FLOAT', 'seriesMediaParameter', 'IMPC_GRS_003_001 IMPC_CAL_001_001 IMPC_DXA_001_001 IMPC_HWT_007_001 IMPC_PAT_049_001 IMPC_BWT_001_001 IMPC_ABR_001_001 IMPC_CHL_001_001 TCP_CHL_001_001 HMGU_ROT_004_001 PLOT_ALL_PARAMETERS_AS_TIMESERIES', 0, 0, 0, 1, 0, 0, 0, 0, 0, '', 4276),
    (111, 'IMPC_EXD_009_001', 6, 'Number female controls', 'number_female_controls', 1, 0, ' ', 'INT', 'simpleParameter', NULL,                1, 0, 0, 0, 0, 0, 0, 0, 0, '', 4894),
    (171, 'IMPC_EXD_069_001', 6, 'Operator effects - Body weight', 'operator_effects_body_weight', 1, 0, ' ', 'TEXT', 'simpleParameter', NULL, 1, 0, 0, 0, 0, 0, 1, 0, 0, '', 4954),
    (313, 'IMPC_HEL_056_001', 6, 'Branchial arch 3 (EMAP:1038) MP terms', '', 1, 0, ' ', 'TEXT', 'ontologyParameter', NULL,                    0, 0, 0, 0, 0, 0, 0, 0, 0, '', 5845),
    (661, 'IMPC_VIA_026_001', 6, 'Female age earliest start', '', 1, 1, 'Weeks', 'INT', 'procedureMetadata', NULL,                             0, 1, 0, 0, 0, 0, 0, 0, 0, '', 3819),
    (758, 'IMPC_ELZ_093_001', 6, 'Placenta', '', 1, 0, '', 'TEXT', 'simpleParameter', NULL,                                                    0, 0, 0, 0, 1, 0, 1, 0, 0, '', 7291)
;


INSERT INTO `phenotype_pipeline` VALUES (13, 'UCD_001', 6, 'UCD Pipeline', '', 1, 0, 13, 0);
INSERT INTO `phenotype_procedure` VALUES (10,103,'IMPC_PAT_002',6,'Gross Pathology and Tissue Collection','description',2, 9, 0,'experiment','Terminal','Week 16', 893);
INSERT INTO phenotype_parameter VALUES (48075,'IMPC_PAT_049_002',6,'Body Weight',NULL,2,0,'g','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'',65155);
INSERT INTO phenotype_procedure_parameter VALUES (10, 48075);
INSERT INTO phenotype_pipeline_procedure VALUES (13, 10);

