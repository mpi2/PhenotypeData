-- database: cda

INSERT INTO genomic_feature
    (acc, db_id, symbol, name, biotype_acc, biotype_db_id, subtype_acc, subtype_db_id, seq_region_id, seq_region_start, seq_region_end, seq_region_strand, cm_position, status)
VALUES
    ('MGI:1923544', 3, 'Asb5', 'ankyrin repeat and SOCs box-containing 5', 1, 2, 2, 2, 8, '54550331', '54587842', 1, 29.2, 'active');


INSERT INTO `phenotype_pipeline` VALUES (30, 'CCP_001', 6, 'CCP Pipeline', 'Czech Centre for Phenogenomics (CCP) Pipeline', 1, 0, 44, 0);


INSERT INTO phenotyped_colony
  (id, colony_name, es_cell_name, gf_acc, gf_db_id, allele_symbol, background_strain_name, background_strain_acc, phenotyping_centre_organisation_id, phenotyping_consortium_project_id, production_centre_organisation_id, production_consortium_project_id)
VALUES
  (1193, 'CCP-Asb5-del839-EM1-B6N', '', 'MGI:1923544', 3, 'Asb5<em1(IMPC)Ccpcz>', 'C57BL/6NCrl', 'MGI:2683688', 30, 28, 30, 28);


INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);


INSERT INTO strain(acc, db_id, biotype_acc, biotype_db_id, name) VALUES ('MGI:2683688', 3, 'CV:00000025', 3, 'C57BL/6NCrl');


INSERT INTO `phenotype_procedure` VALUES
  (109, 852, 'IMPC_CAL_003', 6, 'Indirect Calorimetry', 'Description', 2, 9, 0, 'experiment', 'Adult', 'Week 11', 893);


INSERT INTO phenotype_parameter VALUES
  (3019, 'IMPC_CAL_002_001', 6, 'Body weight after experiment', 'body_weight_after_experiment', 1, 3, 'g', 'FLOAT', 'simpleParameter',   NULL, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 18101 ),
  (3018, 'IMPC_CAL_001_001', 6, 'Body weight before experiment', 'body_weight_before_experiment', 1, 3, 'g', 'FLOAT', 'simpleParameter', NULL, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 18100 ),
  (3026, 'IMPC_CAL_009_001', 6, 'Cumulative food intake', 'cumulative_food_intake', 1, 2, 'g', 'FLOAT', 'seriesParameter',               NULL, 0, 0, 0, 0, 1, 1, 0, 0, 0, '', 18108 );