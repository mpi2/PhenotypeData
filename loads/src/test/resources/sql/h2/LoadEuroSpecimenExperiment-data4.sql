-- database: cda

INSERT INTO `phenotype_pipeline` VALUES (16,'M-G-P_001',6,'MGP Pipeline','MGP Pipeline',1,0,4,NULL);

INSERT INTO `phenotype_procedure` VALUES (182,49,'M-G-P_003_001',6,'Indirect Calorimetry','',1,0,0,'experiment','Adult','Week 12',0);

INSERT INTO `phenotype_parameter` VALUES (5428,'M-G-P_003_001_001',6,'Body mass before experiment','Body_mass_before_experiment',1,0,'g','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',924);
INSERT INTO `phenotype_parameter` VALUES (5429,'M-G-P_003_001_002',6,'Body mass after experiment','Body_mass_after_experiment',1,0,'g','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',925);
INSERT INTO `phenotype_parameter` VALUES (5430,'M-G-P_003_001_003',6,'Oxygen consumption ','Oxygen_consumption_',1,0,'ml/h/animal','','seriesParameter',NULL,1,0,0,0,1,1,0,0,0,'',926);
INSERT INTO `phenotype_parameter` VALUES (5431,'M-G-P_003_001_004',6,'Carbon dioxide production','Carbon_dioxide_production',1,0,'ml/h/animal','','seriesParameter',NULL,1,0,0,0,1,1,0,0,0,'',927);
INSERT INTO `phenotype_parameter` VALUES (5432,'M-G-P_003_001_006',6,'Heat Production (Metabolic Rate)','Heat_Production_(Metabolic_Rate)',1,0,'kJ/h/animal','','seriesParameter',NULL,1,0,0,0,1,1,0,0,0,'',928);
INSERT INTO `phenotype_parameter` VALUES (5433,'M-G-P_003_001_007',6,'Ambulatory activity  (no. of beam cuts)','Ambulatory_activity__(no_of_beam_cuts)',1,0,'count/hour','','seriesParameter',NULL,0,0,0,0,1,1,0,0,0,'',929);
INSERT INTO `phenotype_parameter` VALUES (5434,'M-G-P_003_001_008',6,'Total activity  (no. of fine movement + no. of beam cuts)','Total_activity__(no_of_fine_movement_+_no_of_beam_cuts)',1,0,'count/hour','','seriesParameter',NULL,0,0,0,0,1,1,0,0,0,'',930);
INSERT INTO `phenotype_parameter` VALUES (5435,'M-G-P_003_001_009',6,'Ambulatory activity  (no of beam cuts)','Ambulatory_activity__(no_of_beam_cuts)',1,0,'count/hour','','seriesParameter',NULL,0,0,0,0,1,1,0,0,0,'',931);
INSERT INTO `phenotype_parameter` VALUES (5436,'M-G-P_003_001_010',6,'Total activity  (no of fine movement + no of beam cuts)','Total_activity__(no_of_fine_movement_+_no_of_beam_cuts)',1,0,'count/hour','','seriesParameter',NULL,0,0,0,0,1,1,0,0,0,'',932);
INSERT INTO `phenotype_parameter` VALUES (5437,'M-G-P_003_001_011',6,'Total Food intake','Total_Food_intake',1,0,'g','','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',933);
INSERT INTO `phenotype_parameter` VALUES (5438,'M-G-P_003_001_012',6,'Cumulative food intake ','Cumulative_food_intake_',1,0,'g','','seriesParameter',NULL,0,0,0,0,1,1,0,0,0,'',934);
INSERT INTO `phenotype_parameter` VALUES (5439,'M-G-P_003_001_801',6,'Equipment name','Equipment_name',1,0,' ','','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',935);
INSERT INTO `phenotype_parameter` VALUES (5440,'M-G-P_003_001_802',6,'Equipment manufacturer','Equipment_manufacturer',1,0,' ','','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',936);
INSERT INTO `phenotype_parameter` VALUES (5441,'M-G-P_003_001_803',6,'Equipment model','Equipment_model',1,0,' ','','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',937);
INSERT INTO `phenotype_parameter` VALUES (5442,'M-G-P_003_001_804',6,'Normal room temperature span','Normal_room_temperature_span',1,0,' ','','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',938);
INSERT INTO `phenotype_parameter` VALUES (5443,'M-G-P_003_001_805',6,'Room Temperature','Room_Temperature',1,0,' ','','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',939);


INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);

INSERT INTO `genomic_feature` VALUES ('MGI:1345634',3,'Amfr','autocrine motility factor receptor','1',2,'2',2,8,93971588,94012842,-1,'45.96','active');

INSERT INTO `phenotyped_colony` VALUES (1942,'EPD0097_2_E01','EPD0097_2_E01','MGI:1345634',3,'Amfr<tm1a(KOMP)Wtsi>','C57BL/6JTyr;C57BL/6N','IMPC-CURATE-09A4A',3,8,3,8);