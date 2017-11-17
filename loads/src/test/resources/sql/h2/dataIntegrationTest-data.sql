-- database: cda_test

-- For testBackgroundStrainIsEqual
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

INSERT INTO `phenotype_parameter` VALUES
  ( 1,'ESLIM_001_001_001',6,'adult mouse weight','adult_mouse_weight',1,0,'g','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'',1),
  ( 2,'ESLIM_001_001_002',6,'adult mouse ','adult_mouse',1,0,' ','','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',2),
  ( 3,'ESLIM_001_001_003',6,'adult mouse abnormality description ','adult_mouse_abnormality_description',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',3),
  ( 4,'ESLIM_001_001_007',6,'coat hair ','coat_hair',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',7),
  ( 5,'ESLIM_001_001_008',6,'coat hair abnormality description ','coat_hair_abnormality_description',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',8),
  ( 6,'ESLIM_001_001_009',6,'coat hair color','coat_hair_color',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',9),
  ( 7,'ESLIM_001_001_011',6,'coat hair presence','coat_hair_presence',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',11),
  ( 8,'ESLIM_001_001_041',6,'skin ','skin',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',41),
  ( 9,'ESLIM_001_001_049',6,'skin color','skin_color',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',49),
  (10,'ESLIM_001_001_085',6,'head ','head',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',85),
  (11,'ESLIM_001_001_091',6,'muzzle/snout ','muzzlesnout',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',91),
  (12,'ESLIM_001_001_094',6,'vibrissa hair ','vibrissa_hair',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',94),
  (13,'ESLIM_001_001_101',6,'mouth ','mouth',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',101),
  (14,'ESLIM_001_001_102',6,'tooth ','tooth',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',102),
  (15,'ESLIM_001_001_122',6,'vagina ','vagina',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',122),
  (16,'ESLIM_001_001_127',6,'testis ','testis',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',127),
  (17,'ESLIM_001_001_175',6,'tail ','tail',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',175);
