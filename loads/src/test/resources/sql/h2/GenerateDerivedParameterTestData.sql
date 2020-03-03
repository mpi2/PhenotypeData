-- database: komp2 test data for generating derived parameter ACS_037

INSERT INTO phenotype_pipeline VALUES (1, 'IMPC_001', 6, 'IMPC Pipeline', 'The IMPC Pipeline is a core set of Procedures and Parameters to be collected by all phenotyping centers.', 1, 1, 7, 0);

INSERT INTO phenotype_parameter (id, stable_id, db_id, name, description, major_version, minor_version, unit, datatype, parameter_type, formula, required, metadata, important, derived, annotate, increment, options, sequence, media, data_analysis_notes, stable_key)
VALUES
       (2882,'IMPC_ACS_037_001',6,'% Pre-pulse inhibition - Global','_pre_pulse_inhibition_global',1,6,'%','FLOAT','simpleParameter','mul(div(sub(''IMPC_ACS_006_001'', div(sum(''IMPC_ACS_007_001'', ''IMPC_ACS_008_001'', ''IMPC_ACS_009_001'', ''IMPC_ACS_010_001''), 4)), ''IMPC_ACS_006_001''), 100)',0,0,0,1,1,0,0,0,0,'',17757),
       (3013,'IMPC_CAL_001_001',6,'Body weight before experiment','body_weight_before_experiment',1,3,'g','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'',18100),
       (1732,'IMPC_BWT_001_001',6,'Body weight','body_weight',1,3,'g','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'',27185),
       (1739, 'IMPC_BWT_008_001',6,'Body weight curve','Derivation to collect all body weights',1,1,'g','FLOAT','seriesMediaParameter','unimplemented()',0,0,0,1,0,0,0,0,1,'',27192);

INSERT INTO phenotype_parameter VALUES (1576, 'IMPC_ACS_006_001', 6, 'Response amplitude - S', 'response_amplitude_s', 1, 4, ' ', 'FLOAT', 'simpleParameter', null, 1, 0, 0, 0, 1, 0, 0, 0, 0, '', 2112);
INSERT INTO phenotype_parameter VALUES (1577, 'IMPC_ACS_007_001', 6, 'Response amplitude - PP1_S', 'response_amplitude_pp1_s', 1, 5, ' ', 'FLOAT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 2113);
INSERT INTO phenotype_parameter VALUES (1578, 'IMPC_ACS_008_001', 6, 'Response amplitude - PP2_S', 'response_amplitude_pp2_s', 1, 5, ' ', 'FLOAT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 2114);
INSERT INTO phenotype_parameter VALUES (1579, 'IMPC_ACS_009_001', 6, 'Response amplitude - PP3_S', 'response_amplitude_pp3_s', 1, 5, ' ', 'FLOAT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 2115);
INSERT INTO phenotype_parameter VALUES (1580, 'IMPC_ACS_010_001', 6, 'Response amplitude - PP4_S', 'response_amplitude_pp4_s', 1, 6, ' ', 'FLOAT', 'simpleParameter', null, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', 2116);

INSERT INTO phenotype_procedure VALUES (43, 176, 'IMPC_ACS_003', 6, 'Acoustic Startle and Pre-pulse Inhibition (PPI)', 'The acoustic startle', 3, 4, 1, 'experiment', 'Adult', 'Week 10', 1),
                                       (81, 623, 'IMPC_BWT_001', 6, 'Body Weight', 'The body weight test ', 1, 3, 0, 'experiment', 'Adult', 'Unrestricted', 22),
                                       (872, 859, 'IMPC_CAL_003', 6, 'Indirect Calorimetry', 'Indirect calorimetry .', 3, 5, 0, 'experiment', 'Adult', 'Week 11', 253);

-- Insert the procedure to parameter associations
INSERT INTO phenotype_procedure_parameter VALUES (43, 2882);
INSERT INTO phenotype_procedure_parameter VALUES (81, 1732);
INSERT INTO phenotype_procedure_parameter VALUES (81, 1739);
INSERT INTO phenotype_procedure_parameter VALUES (872, 3013);

-- Insert the pipeline to procedure associations
INSERT INTO phenotype_pipeline_procedure VALUES (1, 43);
INSERT INTO phenotype_pipeline_procedure VALUES (1, 81);
INSERT INTO phenotype_pipeline_procedure VALUES (1, 872);


 -- ACS EXPERIMENTS
INSERT INTO experiment VALUES (1, 1, 'external_id', 'sequence_id', '2019-01-01', 1, 1, 1, 'IMPC_001', 43,  'IMPC_ACS_003', 1, 'colony_id', 'metadata_combined', 'metadata_group', null, null);
INSERT INTO experiment VALUES (2, 1, 'external_id2', 'sequence_id2', '2019-01-02', 1, 1, 1, 'IMPC_001', 43,  'IMPC_ACS_003', 1, 'colony_id2', 'metadata_combined', 'metadata_group', null, null);

INSERT INTO observation VALUES (6, 1, 1, 1576, 'IMPC_ACS_006_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (7, 1, 1, 1577, 'IMPC_ACS_007_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (8, 1, 1, 1578, 'IMPC_ACS_008_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (9, 1, 1, 1579, 'IMPC_ACS_009_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (10, 1, 1, 1580, 'IMPC_ACS_010_001', 'sequence_id', 0, 'unidimensional', 0, null, null);

INSERT INTO observation VALUES (16, 1, 2, 1576, 'IMPC_ACS_006_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (17, 1, 2, 1577, 'IMPC_ACS_007_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (18, 1, 2, 1578, 'IMPC_ACS_008_001', 'sequence_id', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (19, 1, 2, 1578, 'IMPC_ACS_009_001', 'sequence_id', 0, 'unidimensional', 0, null, null);

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

-- BODYWEIGHT EXPERIMENTS

INSERT INTO experiment VALUES (3, 1, 'external_id3', 'sequence_id', '2012-09-03', 1, 1, 1, 'IMPC_001', 43,  'IMPC_CAL_003', 1, 'colony_id', 'metadata_combined', 'metadata_group', null, null);
INSERT INTO experiment VALUES (4, 1, 'external_id4', 'sequence_id', '2012-07-25', 1, 1, 1, 'IMPC_001', 81,  'IMPC_BWT_001', 1, 'colony_id', 'metadata_combined', 'metadata_group', null, null); -- different specimen
INSERT INTO experiment VALUES (5, 1, 'external_id5', 'sequence_id', '2012-07-11', 1, 1, 1, 'IMPC_001', 81,  'IMPC_BWT_001', 1, 'colony_id', 'metadata_combined', 'metadata_group', null, null);
INSERT INTO experiment VALUES (6, 1, 'external_id6', 'sequence_id', '2012-07-20', 1, 1, 1, 'IMPC_001', 81,  'IMPC_BWT_001', 1, 'colony_id', 'metadata_combined', 'metadata_group', null, null);
INSERT INTO experiment VALUES (7, 1, 'external_id7', 'sequence_id', '2012-07-27', 1, 1, 1, 'IMPC_001', 81,  'IMPC_BWT_001', 1, 'colony_id', 'metadata_combined', 'metadata_group', null, null);

INSERT INTO observation VALUES (20, 1, 3, 3013, 'IMPC_CAL_001_001', '', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (21, 1, 4, 1732, 'IMPC_BWT_001_001', '', 0, 'unidimensional', 0, null, null); -- different specimen
INSERT INTO observation VALUES (22, 1, 3, 1732, 'IMPC_BWT_001_001', '', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (23, 1, 3, 1732, 'IMPC_BWT_001_001', '', 0, 'unidimensional', 0, null, null);
INSERT INTO observation VALUES (24, 1, 3, 1732, 'IMPC_BWT_001_001', '', 0, 'unidimensional', 0, null, null);
INSERT INTO unidimensional_observation VALUES (20, 	22.7);
INSERT INTO unidimensional_observation VALUES (21, 	7.71); -- different specimen
INSERT INTO unidimensional_observation VALUES (22, 	11.61);
INSERT INTO unidimensional_observation VALUES (23, 	16.79);
INSERT INTO unidimensional_observation VALUES (24, 	19.08);

INSERT INTO experiment_observation VALUES (3, 20);
INSERT INTO experiment_observation VALUES (4, 21);
INSERT INTO experiment_observation VALUES (5, 22);
INSERT INTO experiment_observation VALUES (6, 23);
INSERT INTO experiment_observation VALUES (7, 24);


INSERT INTO biological_model VALUES (1, 3, 'Cdh15<tm1Hha>/Cdh15<tm1Hha>', 'involves: 129S4/SvJae * C57BL/6', 'homozygote');
INSERT INTO biological_model VALUES (2, 3, 'Cdh15<tm1Hha>/+', 'involves: 129S4/SvJae * C57BL/6', 'heterozygote');

INSERT INTO biological_model_strain VALUES (1, 'strain_acc', 1);
INSERT INTO biological_model_strain VALUES (2, 'strain_acc', 1);


INSERT INTO biological_sample VALUES (1, 'external_id', 1, 'sample_type_acc', 1, 'experimental', 1, 1, 1);
INSERT INTO live_sample VALUES (1, 'colony_id', 'devstage_acc', 1, 'male', 'homozygote', '2019-01-01', 1);
INSERT INTO biological_model_sample VALUES (1, 1);

INSERT INTO biological_sample VALUES (2, 'external_id2', 1, 'sample_type_acc', 1, 'experimental', 1, 1, 1);
INSERT INTO live_sample VALUES (2, 'colony_id2', 'devstage_acc', 1, 'male', 'homozygote', '2019-02-02', 1);
INSERT INTO biological_model_sample VALUES (1, 2);

INSERT INTO biological_sample VALUES (3, 'external_id3', 1, 'sample_type_acc', 1, 'experimental', 1, 1, 1);
INSERT INTO live_sample VALUES (3, 'colony_id3', 'devstage_acc', 1, 'male', 'heterozygote', '2012-06-18', 1);
INSERT INTO biological_model_sample VALUES (2, 3);

INSERT INTO biological_sample VALUES (4, 'external_id4', 1, 'sample_type_acc', 1, 'experimental', 1, 1, 1);
INSERT INTO live_sample VALUES (4, 'colony_id3', 'devstage_acc', 1, 'male', 'heterozygote', '2012-07-04', 1);
INSERT INTO biological_model_sample VALUES (2, 4);

INSERT INTO ontology_term VALUES ('sample_type_acc', 1, 'sample_type_acc', 'A region', 0, null);
INSERT INTO ontology_term VALUES ('devstage_acc', 1, 'devstage_acc', 'A region', 0, null);