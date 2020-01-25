-- database: cda

INSERT INTO genomic_feature
  (acc, db_id, symbol, name, biotype_acc, biotype_db_id, subtype_acc, subtype_db_id, seq_region_id, seq_region_start, seq_region_end, seq_region_strand, cm_position, status)
VALUES
  ('MGI:96794', 3, 'Lmna', 'lamin A', 1, 2, 2, 2, 3, '88481148', '88509932', -1, 38.84, 'active');


INSERT INTO `phenotype_pipeline` VALUES
  (19, 'DSS_001', 6, 'DSS challenge', '', 1, 0, 19, 0);


INSERT INTO phenotyped_colony
  (id, colony_name, es_cell_name, gf_acc, gf_db_id, allele_symbol, background_strain_name, background_strain_acc, phenotyping_centre_organisation_id, phenotyping_consortium_project_id, production_centre_organisation_id, production_consortium_project_id)
VALUES
  (7723, 'MMNA', 'EPD0419_6_A02', 'MGI:96794', 3, 'Lmna<tm1a(EUCOMM)Wtsi>', 'C57BL/6N', 'MGI:2159965', 3, 8, 8, 14);


INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);


INSERT INTO strain(acc, db_id, biotype_acc, biotype_db_id, name) VALUES ('IMPC-CURATE-F9887', 22, 'CV:00000051', 3, 'NonStringNonNumericValue');


INSERT INTO phenotype_procedure
  (id, stable_key, stable_id, db_id, name, description, major_version, minor_version, is_mandatory, level, stage, stage_label, schedule_key)
VALUES
  (735, 235, 'DSS_DSS_001', 6, 'DSS Histology', '', 1, 0, 0, 'experiment', 'Terminal', 'Week 16', 523);


INSERT INTO phenotype_parameter
  (id, stable_id, db_id, name, description, major_version, minor_version, unit, datatype, parameter_type, formula, required, metadata, important, derived, annotate, increment, options, sequence, media, data_analysis_notes, stable_key)
VALUES
  (38960, 'DSS_DSS_001_001', '6', 'Section 1 Epithelium', 'section_1_epithelium', 1, 0, ' ', 'FLOAT', 'simpleParameter', NULL, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 6780);