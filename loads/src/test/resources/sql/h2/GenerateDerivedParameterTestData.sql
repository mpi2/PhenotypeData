-- database: komp2 test data for generating derived parameter ACS_037

INSERT INTO phenotype_pipeline VALUES (1, 'IMPC_001', 6, 'IMPC Pipeline', 'The IMPC Pipeline is a core set of Procedures and Parameters to be collected by all phenotyping centers.', 1, 1, 7, 0);

INSERT INTO phenotype_parameter (id, stable_id, db_id, name, description, major_version, minor_version, unit, datatype, parameter_type, formula, required, metadata, important, derived, annotate, increment, options, sequence, media, data_analysis_notes, stable_key)
VALUES
       (2882,'IMPC_ACS_037_001',6,'% Pre-pulse inhibition - Global','_pre_pulse_inhibition_global',1,6,'%','FLOAT','simpleParameter','mul(div(sub(''IMPC_ACS_006_001'', div(sum(''IMPC_ACS_007_001'', ''IMPC_ACS_008_001'', ''IMPC_ACS_009_001'', ''IMPC_ACS_010_001''), 4)), ''IMPC_ACS_006_001''), 100)',0,0,0,1,1,0,0,0,0,'',17757);

INSERT INTO phenotype_procedure VALUES (43, 176, 'IMPC_ACS_003', 6, 'Acoustic Startle and Pre-pulse Inhibition (PPI)', 'The acoustic startle', 3, 4, 1, 'experiment', 'Adult', 'Week 10', 1);

INSERT INTO phenotype_procedure_parameter VALUES (43, 2882);

INSERT INTO experiment VALUES (1, 1, 'external_id', 'sequence_id', '2019-01-01', 1, 1, 1, 'IMPC_001', 1,  'IMPC_ACS_003', 1, 'colony_id', 'metadata_combined', 'metadata_group', null, null);
INSERT INTO experiment VALUES (2, 1, 'external_id2', 'sequence_id2', '2019-01-02', 1, 1, 1, 'IMPC_001', 1,  'IMPC_ACS_003', 1, 'colony_id2', 'metadata_combined', 'metadata_group', null, null);

INSERT INTO observation VALUES (6, 1, 1, 1, 'IMPC_ACS_006_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (7, 1, 1, 2, 'IMPC_ACS_007_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (8, 1, 1, 3, 'IMPC_ACS_008_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (9, 1, 1, 4, 'IMPC_ACS_009_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (10, 1, 1, 5, 'IMPC_ACS_010_001', 'sequence_id', 0, 'unidimensional', 0, null, null);

INSERT INTO observation VALUES (16, 1, 2, 1, 'IMPC_ACS_006_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (17, 1, 2, 2, 'IMPC_ACS_007_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (18, 1, 2, 3, 'IMPC_ACS_008_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (19, 1, 2, 4, 'IMPC_ACS_009_001', 'sequence_id', 0, 'unidimensional', 0, null, null);

INSERT INTO unidimensional_observation VALUES (6, 6);
INSERT INTO unidimensional_observation VALUES (7, 7);
INSERT INTO unidimensional_observation VALUES (8, 8);
INSERT INTO unidimensional_observation VALUES (9, 9);
INSERT INTO unidimensional_observation VALUES (10, 10);

INSERT INTO unidimensional_observation VALUES (16, 6);
INSERT INTO unidimensional_observation VALUES (17, 7);
INSERT INTO unidimensional_observation VALUES (18, 8);
INSERT INTO unidimensional_observation VALUES (19, 10);

INSERT INTO experiment_observation VALUES (1, 6);
INSERT INTO experiment_observation VALUES (1, 7);
INSERT INTO experiment_observation VALUES (1, 8);
INSERT INTO experiment_observation VALUES (1, 9);
INSERT INTO experiment_observation VALUES (1, 10);

INSERT INTO experiment_observation VALUES (2, 16);
INSERT INTO experiment_observation VALUES (2, 17);
INSERT INTO experiment_observation VALUES (2, 18);
INSERT INTO experiment_observation VALUES (2, 19);

INSERT INTO biological_sample VALUES (1, 'external_id', 1, 'sample_type_acc', 1, 'experimental', 1, 1);
INSERT INTO biological_sample VALUES (2, 'external_id2', 1, 'sample_type_acc', 1, 'experimental', 1, 1);

INSERT INTO live_sample VALUES (1, 'colony_id', 'devstage_acc', 1, 'male', 'homozygote', '2019-01-01', 1);
INSERT INTO live_sample VALUES (2, 'colony_id2', 'devstage_acc', 1, 'male', 'homozygote', '2019-02-02', 1);

INSERT INTO biological_model_sample VALUES (1, 1);
INSERT INTO biological_model_sample VALUES (1, 2);

INSERT INTO biological_model_strain VALUES (1, 'strain_acc', 1);

