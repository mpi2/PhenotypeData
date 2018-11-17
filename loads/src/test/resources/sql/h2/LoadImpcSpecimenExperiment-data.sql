-- database: cda

INSERT INTO `phenotype_pipeline` VALUES (5,'JAX_001',6,'JAX Pipeline','',1,0,12,0);
INSERT INTO `phenotype_pipeline` VALUES (8,'MGP_001',6,'MGP Select Pipeline','',1,0,15,NULL);


INSERT INTO `phenotype_procedure` VALUES
  (1,103,'IMPC_BWT_001',6,'Body Weight','The body weight test measures the weight of the mouse in a time series, allowing monitoring of its evolution; also, it is required in many other procedures.',1,3,1,'experiment','Adult','Unrestricted');

INSERT INTO phenotype_procedure VALUES
  (67,183,'IMPC_INS_003',6,'Insulin Blood Level','The insulin concentration in the blood is an important indicator of diabetes.Ontological description: abnormal circulating insulin level [MP:0001560]; increased circulating insulin level [MP:0002079]; decreased circulating insulin level [MP:0002727].',3,1,0,'experiment','Terminal','Week 16');

INSERT INTO `phenotype_procedure` VALUES
  (57,90,'IMPC_DXA_001',6,'Body Composition (DEXA lean/fat)','Measure bone mineral content and density as well as body composition in mice using the DEXA (Dual Energy X-ray Absorptiometry) analyser.',1,8,1,'experiment','Adult','Week 14');

INSERT INTO `phenotype_parameter` VALUES (1,'IMPC_BWT_001_001',6,'Body weight','body_weight',1,3,'g','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'',2100);
INSERT INTO `phenotype_parameter` VALUES (2,'IMPC_BWT_002_001',6,'General comments about the mouse','general_comments_about_the_mouse',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2101);
INSERT INTO `phenotype_parameter` VALUES (3,'IMPC_BWT_003_001',6,'Equipment ID','equipment_name',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',2102);
INSERT INTO `phenotype_parameter` VALUES (4,'IMPC_BWT_004_001',6,'Equipment manufacturer','equipment_manufacturer',1,2,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,0,'',2103);
INSERT INTO `phenotype_parameter` VALUES (5,'IMPC_BWT_005_001',6,'Experimenter ID','experimenter_id',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',2104);
INSERT INTO `phenotype_parameter` VALUES (6,'IMPC_BWT_006_001',6,'Date equipment last calibrated','',1,2,' ','DATE','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',2171);
INSERT INTO `phenotype_parameter` VALUES (7,'IMPC_BWT_007_001',6,'Equipment model','',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,0,'',2356);
INSERT INTO `phenotype_parameter` VALUES (8,'IMPC_BWT_008_001',6,'Body weight curve','Derivation to collect all body weights measured (when available) and plot them correctly as time series.',1,1,'g','FLOAT','seriesMediaParameter','IMPC_GRS_003_001 IMPC_CAL_001_001 IMPC_DXA_001_001 IMPC_HWT_007_001 IMPC_PAT_049_001 IMPC_BWT_001_001 IMPC_ABR_001_001 IMPC_CHL_001_001 TCP_CHL_001_001 HMGU_ROT_004_001 PLOT_ALL_PARAMETERS_AS_TIMESERIES',0,0,0,1,0,0,0,0,0,0,'',4276);

INSERT INTO `phenotype_parameter` VALUES (2026,'IMPC_DXA_001_001',6,'Body weight','body_weight',1,1,'g','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'',1906);
INSERT INTO `phenotype_parameter` VALUES (2027,'IMPC_DXA_002_001',6,'Fat mass','fat_mass',1,1,'g','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',1907);

INSERT INTO `phenotype_parameter` VALUES
  (2157,'IMPC_INS_001_001',6,'Insulin','insulin',1,3,'pg/ml','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',2610),
  (2158,'IMPC_INS_002_001',6,'Type of kit','type_of_kit',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,0,'',2611),
  (2159,'IMPC_INS_003_001',6,'Kit manufacturer','kit_manufacturer',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,0,'',2612),
  (2160,'IMPC_INS_004_001',6,'Kit lot number','kit_lot_number',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',2613),
  (2161,'IMPC_INS_005_001',6,'Equipment ID','equipment_id',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',2614),
  (2162,'IMPC_INS_006_001',6,'Equipment manufacturer','equipment_manufacturer',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,1,'',2615),
  (2163,'IMPC_INS_007_001',6,'Equipment model','equipment_model',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,1,'',2616),
  (2164,'IMPC_INS_008_001',6,'Blood collection tubes','blood_collection_tubes',1,1,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,0,'',2617),
  (2165,'IMPC_INS_009_001',6,'Anesthesia used for blood collection','anesthesia_used_for_blood_collection',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,1,'',2618),
  (2166,'IMPC_INS_010_001',6,'Method of blood collection','method_of_blood_collection',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,1,'',2619),
  (2167,'IMPC_INS_011_001',6,'Anticoagulant','anticoagulant',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,0,'',2620),
  (2168,'IMPC_INS_012_001',6,'Date and time of blood collection','date_and_time_of_blood_collection',1,3,' ','DATETIME','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',2621),
  (2169,'IMPC_INS_013_001',6,'Date of measurement','date_of_measurement',1,3,' ','DATE','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',2622),
  (2170,'IMPC_INS_014_001',6,'Sample status','sample_status',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,0,'',2623),
  (2171,'IMPC_INS_015_001',6,'Samples kept on ice between collection and analysis','samples_kept_on_ice_between_collection_and_analysis',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,1,'',2624),
  (2172,'IMPC_INS_016_001',6,'Sample dilution','sample_dilution',1,2,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,0,'',2625),
  (2173,'IMPC_INS_017_001',6,'Replicates','replicates',1,0,' ','INT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,0,'',2626),
  (2174,'IMPC_INS_018_001',6,'ID of blood collection SOP','id_of_blood_collection_sop',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,0,'',2627),
  (2175,'IMPC_INS_019_001',6,'Hemolysis status','hemolysis_status',1,1,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,0,'',2628),
  (2176,'IMPC_INS_020_001',6,'Blood collection experimenter ID','blood_collection_experimenter_id',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',2629),
  (2177,'IMPC_INS_021_001',6,'Blood analysis experimenter ID','blood_analysis_experimenter_id',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',2630),
  (2178,'IMPC_INS_022_001',6,'Date equipment last calibrated','date_equipment_last_calibrated',1,1,' ','DATE','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',2631),
  (2179,'IMPC_INS_023_001',6,'Sample type','',1,0,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,0,'',4711);


INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);