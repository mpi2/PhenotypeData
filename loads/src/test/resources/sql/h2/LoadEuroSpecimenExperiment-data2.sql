-- database: cda

INSERT INTO `phenotype_pipeline` VALUES
  (1,'ESLIM_001',6,'EUMODIC Pipeline 1','EUMODIC Pipeline 1',1,0,1,NULL);

INSERT INTO `phenotype_procedure` VALUES (138,6,'ESLIM_005_001',6,'DEXA','Measure bone mineral content and density as well as body composition in mice using the pDEXA analyser.',1,0,0,'experiment','Adult','Week 14',0);

INSERT INTO `phenotype_parameter` VALUES (4590,'ESLIM_022_001_701',6,'Body Weight Curve Pipeline One','Body_Weight',1,0,'g','FLOAT','seriesParameter','plot_parameters_as_time_series(ESLIM_001_001_001, ESLIM_002_001_001, ESLIM_003_001_001, ESLIM_004_001_001, ESLIM_005_001_001, ESLIM_020_001_001, ESLIM_022_001_001)',0,0,0,1,0,1,0,0,0,'',315);
INSERT INTO `phenotype_parameter` VALUES (4596,'ESLIM_022_001_707',6,'Dexa Body Weight','Dexa_Body_Weight',1,0,'g','','simpleParameter','ESLIM_005_001_001',0,0,0,1,1,0,0,0,0,'',321);
INSERT INTO `phenotype_parameter` VALUES (4638,'ESLIM_005_001_001',6,'Body weight','Weight',1,0,'g','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'',217);
INSERT INTO `phenotype_parameter` VALUES (4639,'ESLIM_005_001_002',6,'Fat mass','Fat_Mass',1,0,'g','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',218);
INSERT INTO `phenotype_parameter` VALUES (4640,'ESLIM_005_001_003',6,'Lean mass','Lean_Mass',1,0,'g','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',219);
INSERT INTO `phenotype_parameter` VALUES (4641,'ESLIM_005_001_004',6,'Bone Mineral Density (excluding skull)','Bone_Mineral_Density_Excluding_skull',1,0,'g/cm^2','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',220);
INSERT INTO `phenotype_parameter` VALUES (4642,'ESLIM_005_001_005',6,'Bone Mineral Content','Bone_Mineral_Content',1,0,'g','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',221);
INSERT INTO `phenotype_parameter` VALUES (4643,'ESLIM_005_001_006',6,'Body length','Body_Length',1,0,'cm','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',222);
INSERT INTO `phenotype_parameter` VALUES (4644,'ESLIM_005_001_801',6,'Equipment name','Equipment_name',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',227);
INSERT INTO `phenotype_parameter` VALUES (4645,'ESLIM_005_001_802',6,'Equipment manufacturer','Equipment_manufacturer',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',228);
INSERT INTO `phenotype_parameter` VALUES (4646,'ESLIM_005_001_803',6,'Equipment model','Equipment_model',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',229);
INSERT INTO `phenotype_parameter` VALUES (4647,'ESLIM_005_001_804',6,'Mouse status','Mouse_Status',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',230);
INSERT INTO `phenotype_parameter` VALUES (4648,'ESLIM_005_001_701',6,'BMC/Body weight','BMC/Weight',1,0,'g bone/g mouse','FLOAT','simpleParameter','(ESLIM_005_001_005/ESLIM_005_001_001)',0,0,0,1,1,0,0,0,0,'',223);
INSERT INTO `phenotype_parameter` VALUES (4649,'ESLIM_005_001_702',6,'Lean/Body weight','Lean/Weight',1,0,'g lean/g mouse','FLOAT','simpleParameter','(ESLIM_005_001_003/ESLIM_005_001_001)',0,0,0,1,1,0,0,0,0,'',224);
INSERT INTO `phenotype_parameter` VALUES (4650,'ESLIM_005_001_703',6,'Fat/Body weight','Fat/Weight',1,0,'g fat/g mouse','FLOAT','simpleParameter','(ESLIM_005_001_002/ESLIM_005_001_001)',0,0,0,1,1,0,0,0,0,'',225);
INSERT INTO `phenotype_parameter` VALUES (4651,'ESLIM_005_001_704',6,'Bone area (BMC/BMD)','Bone_area_BMC/BMD',1,0,'cm^2','FLOAT','simpleParameter','(ESLIM_005_001_005/ESLIM_005_001_004)',0,0,0,1,1,0,0,0,0,'',226);


INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);

INSERT INTO `genomic_feature` VALUES
  ('MGI:1890156',3,'Stx8','syntaxin 8','1',2,'2',2,11,67966193,68207148,1,'41.20','active');