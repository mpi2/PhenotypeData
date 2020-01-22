-- database: cda

INSERT INTO genomic_feature
  (acc, db_id, symbol, name, biotype_acc, biotype_db_id, subtype_acc, subtype_db_id, seq_region_id, seq_region_start, seq_region_end, seq_region_strand, cm_position, status)
VALUES
  ('MGI:1915094', 3, 'Rab32', 'RAB32, member RAS oncogene family', 1, 2, 2, 2, 10, '10545039', '10558207', -1, 3.44, 'active');


INSERT INTO `phenotype_pipeline` VALUES (13, 'UCD_001', 6, 'UCD Pipeline', '', 1, 0, 13, 0);


INSERT INTO phenotyped_colony
  (id, colony_name, es_cell_name, gf_acc, gf_db_id, allele_symbol, background_strain_name, phenotyping_centre_organisation_id, phenotyping_consortium_project_id, production_centre_organisation_id, production_consortium_project_id)
VALUES
  (19999, 'ET8295', 'EPD0095_2_C12', 'MGI:1915094', 3, 'Rab32<tm1b(KOMP)Wtsi>', 'strain_4', 2, 12, 32, 12);


INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);


INSERT INTO strain(acc, db_id, biotype_acc, biotype_db_id, name) VALUES ('MGI:2683688', 3, 'CV:00000025', 3, 'strain_4');


INSERT INTO `phenotype_procedure` VALUES
  (10,103,'IMPC_PAT_002',6,'Gross Pathology and Tissue Collection','description',2, 9, 0,'experiment','Terminal','Week 16', 893);


INSERT INTO phenotype_parameter VALUES
  (37850,'IMPC_PAT_032_002',6,'Comments (in English)','General comments, in English',2,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',5023),
  (48075,'IMPC_PAT_049_002',6,'Body Weight',NULL,2,0,'g','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'',65155);