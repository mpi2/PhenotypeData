
INSERT INTO `phenotype_pipeline` VALUES (8,'MGP_001',6,'MGP Select Pipeline','',1,0,15,NULL);

INSERT INTO `phenotype_procedure` VALUES (116,217,'MGP_ANA_001',6,'Anti-nuclear antibody assay','',1,0,0,'experiment','Terminal','Week 16');
INSERT INTO `phenotype_procedure` VALUES (121,229,'MGP_IMM_001',6,'Spleen Immunophenotyping','3i Spleen Immunophenotyping',1,0,0,'experiment','Terminal','Week 16');
INSERT INTO `phenotype_procedure` VALUES (120,223,'MGP_MLN_001',6,'Mesenteric Lymph Node Immunophenotyping','3i Mesenteric Lymph Node Immunophenotyping',1,0,0,'experiment','Terminal','Week 16');
INSERT INTO `phenotype_procedure` VALUES (115,216,'MGP_PBI_001',6,'Whole blood peripheral blood leukocyte immunophenotyping','',1,0,0,'experiment','Terminal','Week 16');

INSERT INTO `phenotype_parameter` VALUES (3580,'MGP_PBI_009_001',6,'CD4+ CD44+ CD62L- alpha beta effector T cell percentage','cd4_cd44_cd62l_alpha_beta_effector_t_cell_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',5577);
INSERT INTO `phenotype_parameter` VALUES (3586,'MGP_PBI_015_001',6,'IgD+ mature B cell percentage','igd_mature_b_cell_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',5583);
INSERT INTO `phenotype_parameter` VALUES (3803,'MGP_MLN_010_001',6,'Resting Treg cells - % of Treg','resting_treg_cells_of_treg',1,1,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',5979);
INSERT INTO `phenotype_parameter` VALUES (3819,'MGP_MLN_026_001',6,'KLRG1+ CD4- NKT cells - % of CD4- NKT cells','klrg1_cd4_nkt_cells_of_cd4_nkt_cells',1,1,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',5995);
INSERT INTO `phenotype_parameter` VALUES (3851,'MGP_MLN_058_001',6,'Plasmacytoid DC - % of DC','plasmacytoid_dc_of_dc',1,1,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',6027);
INSERT INTO `phenotype_parameter` VALUES (3864,'MGP_MLN_071_001',6,'Resting Treg cells - % of CD45+','resting_treg_cells_of_cd45',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'',6040);
INSERT INTO `phenotype_parameter` VALUES (3895,'MGP_MLN_102_001',6,'Early germinal centre B cells - % of CD45+','early_germinal_centre_b_cells_of_cd45',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'',6071);
INSERT INTO `phenotype_parameter` VALUES (3904,'MGP_MLN_111_001',6,'Granulocytes - % of CD45+','granulocytes_of_cd45',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'',6080);
INSERT INTO `phenotype_parameter` VALUES (3908,'MGP_MLN_115_001',6,'Plasmacytoid DC - % of CD45+','plasmacytoid_dc_of_cd45',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'',6084);
INSERT INTO `phenotype_parameter` VALUES (4045,'MGP_IMM_047_001',6,'Transitional 2 B cells - % of B2 cells','transitional_2_b_cells_of_b2_cells',1,1,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',6326);
INSERT INTO `phenotype_parameter` VALUES (4115,'MGP_IMM_117_001',6,'Memory B cells - % of CD45+','memory_b_cells_of_cd45',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'',6396);
INSERT INTO `phenotype_parameter` VALUES (3616,'MGP_ANA_001_001',6,'ANA score','ana_score',1,0,'','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'This is the raw score data that is then classified into positive or negative',5613);
INSERT INTO `phenotype_parameter` VALUES (3617,'MGP_ANA_002_001',6,'ANA classification','ana_classification',1,1,'','TEXT','simpleParameter','ANA score >= 2 -> Positive; Negative',1,0,0,1,1,0,1,0,0,0,'This is the parameter we would like analysing like any other categorical data',5614);

INSERT INTO `genomic_feature` VALUES
	('MGI:99495',3,'Brd2','bromodomain containing 2','1',2,'2',2,17,34112023,34122634,-1,'17.98','active');

INSERT INTO `genomic_feature` VALUES
	('MGI:1915276',3,'Pclaf','PCNA clamp associated factor','1',2,'2',2,9,65890237,65903266,1,'35.58','active');

INSERT INTO `genomic_feature` VALUES
	('MGI:1917747',3,'Arhgap17','Rho GTPase activating protein 17','1',2,'2',2,7,123279218,123369915,-1,'67.42','active');

INSERT INTO `genomic_feature` VALUES
	('MGI:1919247',3,'Smg9','smg-9 homolog, nonsense mediated mRNA decay factor (C. elegans)','1',2,'2',2,7,24399619,24422778,1,'10.54','active');

INSERT INTO `genomic_feature` VALUES
	('MGI:102784',3,'Tgfb1i1','transforming growth factor beta 1 induced transcript 1','1',2,'2',2,7,128246812,128255699,1,'70.07','active');

INSERT INTO `genomic_feature` VALUES
	('MGI:2384409',3,'Il27','interleukin 27','1',2,'2',2,7,126589010,126594941,-1,'69.18','active');

INSERT INTO `genomic_feature` VALUES
	('MGI:3036236',3,'Tceal5','transcription elongation factor A (SII)-like 5','1',2,'2',2,20,136200948,136203876,-1,'57.40','active');

INSERT INTO `genomic_feature` VALUES
	('MGI:105303',3,'Cxcr2','chemokine (C-X-C motif) receptor 2','1',2,'2',2,1,74153991,74161246,1,'38.41','active');

SET @DB_ID_MGI=3;
SET @ORG_ID_WTSI=3;
SET @PROJECT_ID_THREEI=2;

INSERT INTO `phenotyped_colony` (`colony_name`, `es_cell_name`, `gf_acc`, `gf_db_id`, `allele_symbol`, `background_strain_name`, `production_centre_organisation_id`, `production_consortium_project_id`, `phenotyping_centre_organisation_id`, `phenotyping_consortium_project_id`)
VALUES
	('DAAE', NULL, 'MGI:99495', (SELECT @DB_ID_MGI), 'Brd2<em1(IMPC)Wtsi>', 'C57BL/6N', (@ORG_ID_WTSI),
	 (SELECT @PROJECT_ID_THREEI), (SELECT @ORG_ID_WTSI), (SELECT @PROJECT_ID_THREEI)),
	('MGRK', NULL, 'MGI:1915276', (SELECT @DB_ID_MGI), 'Pclaf<tm1a(EUCOMM)Wtsi>', 'C57BL/6N', (@ORG_ID_WTSI),
	 (SELECT @PROJECT_ID_THREEI), (SELECT @ORG_ID_WTSI), (SELECT @PROJECT_ID_THREEI)),
	('PMAP', NULL, 'MGI:1917747', (SELECT @DB_ID_MGI), 'Arhgap17<tm1b(EUCOMM)Wtsi>', 'C57BL/6N', (@ORG_ID_WTSI),
	 (SELECT @PROJECT_ID_THREEI), (SELECT @ORG_ID_WTSI), (SELECT @PROJECT_ID_THREEI)),
	('PMDC', NULL, 'MGI:1919247', (SELECT @DB_ID_MGI), 'Smg9<tm1b(EUCOMM)Wtsi>', 'C57BL/6N', (@ORG_ID_WTSI),
	 (SELECT @PROJECT_ID_THREEI), (SELECT @ORG_ID_WTSI), (SELECT @PROJECT_ID_THREEI)),
	('MUDL', NULL, 'MGI:102784', (SELECT @DB_ID_MGI), 'Tgfb1i1<tm1b(KOMP)Wtsi>', 'C57BL/6N', (@ORG_ID_WTSI),
	 (SELECT @PROJECT_ID_THREEI), (SELECT @ORG_ID_WTSI), (SELECT @PROJECT_ID_THREEI)),
	('PMCO', NULL, 'MGI:2384409', (SELECT @DB_ID_MGI), 'Il27<tm1b(EUCOMM)Wtsi>', 'C57BL/6N', (@ORG_ID_WTSI),
	 (SELECT @PROJECT_ID_THREEI), (SELECT @ORG_ID_WTSI), (SELECT @PROJECT_ID_THREEI)),
	('MUAM', NULL, 'MGI:3036236', (SELECT @DB_ID_MGI), 'Tceal5<tm1b(KOMP)Wtsi>', 'C57BL/6N', (@ORG_ID_WTSI),
	 (SELECT @PROJECT_ID_THREEI), (SELECT @ORG_ID_WTSI), (SELECT @PROJECT_ID_THREEI)),
	('MFFS', NULL, 'MGI:105303', (SELECT @DB_ID_MGI), 'Cxcr2<tm1a(EUCOMM)Wtsi>', 'C57BL/6N', (@ORG_ID_WTSI),
	 (SELECT @PROJECT_ID_THREEI), (SELECT @ORG_ID_WTSI), (SELECT @PROJECT_ID_THREEI))
;

