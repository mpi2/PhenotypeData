-- database: cda

INSERT INTO genomic_feature
  (acc, db_id, symbol, name, biotype_acc, biotype_db_id, subtype_acc, subtype_db_id, seq_region_id, seq_region_start, seq_region_end, seq_region_strand, cm_position, status)
VALUES
  ('MGI:1891915', 3, 'Foxo4', 'forkhead box O4', 1, 2, 2, 2, 20, '101254528', '101260873', 1, 43.89, 'active');


INSERT INTO `phenotype_pipeline` VALUES (13, 'UCD_001', 6, 'UCD Pipeline', '', 1, 0, 13, 0);


INSERT INTO phenotyped_colony
  (id, colony_name, es_cell_name, gf_acc, gf_db_id, allele_symbol, background_strain_name, background_strain_acc, phenotyping_centre_organisation_id, phenotyping_consortium_project_id, production_centre_organisation_id, production_consortium_project_id)
VALUES
  (1072, 'BL5274', '14072A-G7', 'MGI:1891915', 3, 'Foxo4<tm1.1(KOMP)Vlcg>', 'C57BL/6NCrl', 'MGI:2683688', 2, 12, 32, 12);


INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);


INSERT INTO strain(acc, db_id, biotype_acc, biotype_db_id, name) VALUES ('MGI:2683688', 3, 'CV:00000025', 3, 'C57BL/6NCrl');


INSERT INTO phenotype_procedure
  (id, stable_key, stable_id, db_id, name, description, major_version, minor_version, is_mandatory, level, stage, stage_label, schedule_key)
VALUES
  (109, 852, 'IMPC_CAL_003', 6, 'Indirect Calorimetry', 'Indirect calorimetry description...', 3, 5, 0, 'experiment', 'Adult', 'Week 11', 238);


INSERT INTO phenotype_parameter
  (id, stable_id, db_id, name, description, major_version, minor_version, unit, datatype, parameter_type, formula, required, metadata, important, derived, annotate, increment, options, sequence, media, data_analysis_notes, stable_key)
VALUES
  (3018, 'IMPC_CAL_001_001', 6, 'Body weight before experiment', 'body_weight_before_experiment', 1, 3, 'g', 'FLOAT', 'simpleParameter', NULL, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 18100),
  (3019, 'IMPC_CAL_002_001', 6, 'Body weight after experiment', 'body_weight_after_experiment', 1, 3, 'g', 'FLOAT', 'simpleParameter', NULL,   1, 0, 0, 0, 0, 0, 0, 0, 0, '', 18101),
  (3012, 'IMPC_CAL_017_001', 6, 'Respiratory Exchange Ratio', 'respiratory_exchange_ratio', 1, 2, ' ', 'FLOAT', 'simpleParameter', NULL, 0, 0, 0, 1, 1, 0, 0, 0, 0, '', 18116),
  (3022, 'IMPC_CAL_005_001', 6, 'Heat production (metabolic rate)', 'heat_production_metabolic_rate_', 1, 2, 'kJ/h/animal', 'FLOAT', 'seriesParameter', NULL, 1, 0, 0, 0, 1, 1, 0, 0, 0, '', 18104)
;