-- database: cda_test

INSERT INTO `phenotype_pipeline` VALUES
  (1,'MGP_001',6,'MGP Select Pipeline','',1,0,1,NULL);

INSERT INTO `phenotype_procedure` VALUES
  (1,1,'IMPC_FER_001',6,'Fertility of Homozygous Knock-out Mice','',1,1,1,'line','Adult','Unrestricted');

INSERT INTO genomic_feature VALUES
  ('MGI:2152453', 3, 'Gsk3a', 'glycogen synthase kinase 3 alpha', 1, 2, 2, 2, 7, 25228258, 25237851, -1, 13.73, 'active');

INSERT INTO allele VALUES
  ('MGI:4434136', 3, 'MGI:2152453', 3, 'CV:000000101', 3, 'Gsk3a<tm1a(EUCOMM)Wtsi>', 'targeted mutation 1a, Wellcome Trust Sanger Institute');

INSERT INTO `phenotype_parameter` VALUES
  ( 1,'IMPC_FER_001_001',6,'Gross Findings Male',                       '',1,4,'',     'TEXT','simpleParameter',  NULL,1,0,0,0,1,0,1,0,0,0,'',1),
  ( 2,'IMPC_FER_002_001',6,'Pups born (primary)',                       '',1,3,'count','INT','simpleParameter',   NULL,1,1,0,0,0,0,0,0,0,0,'',1),
  ( 3,'IMPC_FER_014_001',6,'Age of set up',                             '',1,0,'Weeks','INT', 'procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',1),
  ( 4,'IMPC_FER_017_001',6,'Test strain background secondary  (MGI ID)','',1,2,'',     'TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',1),
  ( 5,'IMPC_FER_019_001',6,'Gross Findings Female',                     '',1,3,'',     'TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',1);

INSERT INTO phenotyped_colony VALUES
  (301,'MCCU','EPD0051_1_H11','MGI:2152453',3,'Gsk3a<tm1a(EUCOMM)Wtsi>','C57BL/6Brd-Tyr<c-Brd>;C57BL/6N;C57BL/6NTac',3,8,3,8);