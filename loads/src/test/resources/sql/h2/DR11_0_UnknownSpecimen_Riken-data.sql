-- database: cda

INSERT INTO genomic_feature
  (acc, db_id, symbol, name, biotype_acc, biotype_db_id, subtype_acc, subtype_db_id, seq_region_id, seq_region_start, seq_region_end, seq_region_strand, cm_position, status)
VALUES
  ('MGI:1342291', 3, 'Rlim', 'ring finger protein, LIM domain interacting', 1, 2, 2, 2, 20, '103957163', '103981284', 1, 43.89, 'active');


INSERT INTO `phenotype_pipeline` VALUES (7, 'IMPC_001', 6, 'IMPC Pipeline', '', 1, 1, 7, 0);


INSERT INTO phenotyped_colony
  (id, colony_name, es_cell_name, gf_acc, gf_db_id, allele_symbol, background_strain_name, background_strain_acc, phenotyping_centre_organisation_id, phenotyping_consortium_project_id, production_centre_organisation_id, production_consortium_project_id)
VALUES
  (8336, 'RIKEN-Rlim-B02_', '', 'MGI:1342291', 3, 'Rlim<em1(IMPC)Rbrc>', 'C57BL/6NTac', 'MGI:2164831', 26, 18, 26, 18);


INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);


INSERT INTO strain(acc, db_id, biotype_acc, biotype_db_id, name) VALUES ('MGI:2164831', 3, 'CV:00000025', 3, 'C57BL/6NTac');


INSERT INTO phenotype_procedure
  (id, stable_key, stable_id, db_id, name, description, major_version, minor_version, is_mandatory, level, stage, stage_label, schedule_key)
VALUES
  (81, 623, 'IMPC_BWT_001', 6, 'Body Weight', '', 1, 3, 0, 'experiment', 'Adult', 'Unrestricted', 22);


INSERT INTO phenotype_parameter
  (id, stable_id, db_id, name, description, major_version, minor_version, unit, datatype, parameter_type, formula, required, metadata, important, derived, annotate, increment, options, sequence, media, data_analysis_notes, stable_key)
VALUES
  (1732, 'IMPC_BWT_001_001', 6, 'Body weight', 'body_weight', 1, 3, 'g', 'FLOAT', 'simpleParameter', NULL, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 27185);