-- database: cda

INSERT INTO `phenotype_pipeline` VALUES
  (1,'ESLIM_001',6,'EUMODIC Pipeline 1','EUMODIC Pipeline 1',1,0,1,NULL);

INSERT INTO `phenotype_pipeline` VALUES
  (2,'ESLIM_002',6,'EUMODIC Pipeline 2','EUMODIC Pipeline 2',2,0,1,NULL);


INSERT INTO phenotype_procedure VALUES
  (139,17,'ESLIM_014_001',6,'Slit Lamp','For viewing the eye structures in detail',1,0,0,'experiment','Adult','Week 13',0);


INSERT INTO `phenotype_parameter` VALUES
(4698,'ESLIM_014_001_001',6,'Eye','Eye',1,0,' ','','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',460),
(4699,'ESLIM_014_001_002',6,'Description','Description',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',461),
(4700,'ESLIM_014_001_003',6,'Eye size','Eye_size',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',462),
(4701,'ESLIM_014_001_004',6,'Eye (+) blood presence','Eye__blood_presence',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',463),
(4702,'ESLIM_014_001_005',6,'Eyelid closure','Eyelid_closure',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',464),
(4703,'ESLIM_014_001_006',6,'Cornea','Cornea',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',465),
(4704,'ESLIM_014_001_007',6,'Cornea opacity','Cornea_opacity',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',466),
(4705,'ESLIM_014_001_008',6,'Cornea vascularisation','Cornea_vascularisation',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',467),
(4706,'ESLIM_014_001_009',6,'Lens','Lens',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',468),
(4707,'ESLIM_014_001_010',6,'Lens opacity','Lens_opacity',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',469),
(4708,'ESLIM_014_001_011',6,'Iris/pupil','IrisPupil',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',470),
(4709,'ESLIM_014_001_012',6,'Iris/pupil position','IrisPupil_position',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',471),
(4710,'ESLIM_014_001_013',6,'Iris/pupil shape','IrisPupil_shape',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',472),
(4711,'ESLIM_014_001_014',6,'Iris/pupil light response','IrisPupil_light_response',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',473),
(4712,'ESLIM_014_001_015',6,'Iris pigment','Iris_pigment',1,0,' ','','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',474),
(4713,'ESLIM_014_001_016',6,'Digitial image(s)','Digitial_Image(s)',1,0,' ','','seriesMediaParameter',NULL,0,0,0,0,1,0,0,0,0,'',475),
(4714,'ESLIM_014_001_801',6,'Equipment name','Equipment_name',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',476),
(4715,'ESLIM_014_001_802',6,'Equipment manufacturer','Equipment_manufacturer',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',477),
(4716,'ESLIM_014_001_803',6,'Equipment model','Equipment_model',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',478);


INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);

