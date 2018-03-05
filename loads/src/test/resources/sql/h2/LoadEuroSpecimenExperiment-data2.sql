-- database: cda

INSERT INTO `phenotype_pipeline` VALUES
  (1,'ESLIM_005',6,'EUMODIC Pipeline 1','EUMODIC Pipeline 1',1,0,1,NULL);


INSERT INTO phenotype_procedure VALUES
  (139,17,'ESLIM_005_001',6,'xxx','For viewing the eye structures in detail',1,0,0,'experiment','Adult','Week 13');


INSERT INTO `phenotype_parameter` VALUES
(4698,'ESLIM_005_001_004',6,'AAA','Eye',1,0,' ','','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',460);


INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);