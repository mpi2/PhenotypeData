-- database: cda_test

INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);

INSERT INTO genomic_feature VALUES
  ('MGI:104874', 3, 'Akt2', 'thymoma viral proto-oncogene 2', 1, 2, 2, 2, 7, 27591552, 27640826, 1, 15.94, 'active');

INSERT INTO allele VALUES
  ('MGI:5760356', 3, 'MGI:104874', 3, 'CV:000000101', 3, 'Akt2<tm1Wcs>', 'targeted mutation 1, William C Skarnes');

INSERT INTO `phenotype_pipeline` VALUES
  (1,'ESLIM_001',6,'EUMODIC Pipeline 1','EUMODIC Pipeline 1',1,0,1,NULL);

INSERT INTO `phenotype_procedure` VALUES
  (1,1,'ESLIM_001_001',6,'Dysmorphology','A simple method to examine mice for morphological abnormalities.',1,0,0,'experiment','Adult','Week 9');
