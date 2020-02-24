-- database: cda_test

INSERT INTO `phenotype_pipeline` VALUES (8,'MGP_001',6,'MGP Select Pipeline','',1,0,15,NULL);

INSERT INTO `phenotype_procedure` VALUES
  (109, 215, 'MGP_BCI_001', 6, 'Buffy coat peripheral blood leukocyte immunophenotyping', '', 1, 0, 0, 'experiment', 'Terminal', 'Week 16', 0);

INSERT INTO `phenotype_parameter`
  (`id`,`stable_id`,`db_id`,`name`,`description`,`major_version`,`minor_version`,`unit`,`datatype`,`parameter_type`,`formula`,`required`,`metadata`,`important`,`derived`,`annotate`,`increment`,`options`,`sequence`,`media`,`data_analysis_notes`,`stable_key`)
VALUES
  (3691,'MGP_BCI_001_001',6,'T cell CD3+ percentage','t_cell_cd3_percentage',1,0,'%','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',5553),
  (3692,'MGP_BCI_002_001',6,'T cell CD4+ percentage','t_cell_cd4_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5554),
  (3693,'MGP_BCI_003_001',6,'Treg cell CD25+ percentage','treg_cell_cd25_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5555),
  (3694,'MGP_BCI_004_001',6,'T cell CD8+ percentage','t_cell_cd8_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5556),
  (3695,'MGP_BCI_005_001',6,'NK cell percentage','nk_cell_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5557),
  (3696,'MGP_BCI_006_001',6,'NKT cell percentage','nkt_cell_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5558),
  (3697,'MGP_BCI_007_001',6,'CD4+CD44+CD62L- percentage','cd4_cd44_cd62l_percentage',1,0,'%','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',5559),
  (3698,'MGP_BCI_008_001',6,'CD8+CD44+CD62L- percentage','cd8_cd44_cd62l_percentage',1,0,'%','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',5560),
  (3699,'MGP_BCI_009_001',6,'B cell CD19+ percentage','b_cell_cd19_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5561),
  (3700,'MGP_BCI_010_001',6,'Mature B cell IgD+ percentage','mature_b_cell_igd_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5562),
  (3701,'MGP_BCI_011_001',6,'Granulocyte Gr1+ percentage','granulocyte_gr1_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5563),
  (3702,'MGP_BCI_012_001',6,'Monocyte percentage','monocyte_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5564),
  (3703,'MGP_BCI_013_001',6,'Equipment manufacturer','equipment_manufacturer',1,0,'','TEXT','procedureMetadata',NULL,1,1,1,0,0,0,1,0,0,'',5565),
  (3704,'MGP_BCI_014_001',6,'Equipment model','equipment_model',1,0,'','TEXT','procedureMetadata',NULL,1,1,1,0,0,0,1,0,0,'',5566),
  (3705,'MGP_BCI_015_001',6,'Anesthesia used for blood collection','anesthesia_used_for_blood_collection',1,0,'','TEXT','procedureMetadata',NULL,1,1,1,0,0,0,1,0,0,'',5567),
  (3706,'MGP_BCI_016_001',6,'Comment','comment',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',5568);

INSERT INTO `phenotyped_colony` VALUES (6049,'MAIL','EPD0027_4_B02','MGI:1890476',3,'Rad18<tm1a(EUCOMM)Wtsi>','B6Brd;B6Dnk;B6N-Tyr<c-Brd>','MGI:5446362',3,8,3,8);
INSERT INTO `phenotyped_colony` VALUES (6050,'MAIL2','EPD0027_4_B02','MGI:1890476',3,'Rad18<tm1a(EUCOMM)Wtsi>','B6Brd;B6Dnk;B6N-Tyr<c-Brd>','MGI:5446362',3,1,3,1);

INSERT INTO `genomic_feature` VALUES
  ('MGI:1890476', 3, 'Rad18', 'RAD18 E3 ubiquitin protein ligase', '1', 2, '2', 2, 6, 112619850, 112696686, -1, '52.42', 'active');

INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);