
INSERT INTO `phenotype_pipeline` VALUES (8,'MGP_001',6,'MGP Select Pipeline','',1,0,15,NULL);

INSERT INTO `phenotype_procedure` VALUES (116,217,'MGP_ANA_001',6,'Anti-nuclear antibody assay','',1,0,0,'experiment','Terminal','Week 16',0);
INSERT INTO `phenotype_procedure` VALUES (121,229,'MGP_IMM_001',6,'Spleen Immunophenotyping','3i Spleen Immunophenotyping',1,0,0,'experiment','Terminal','Week 16',0);
INSERT INTO `phenotype_procedure` VALUES (120,223,'MGP_MLN_001',6,'Mesenteric Lymph Node Immunophenotyping','3i Mesenteric Lymph Node Immunophenotyping',1,0,0,'experiment','Terminal','Week 16',0);
INSERT INTO `phenotype_procedure` VALUES (115,216,'MGP_PBI_001',6,'Whole blood peripheral blood leukocyte immunophenotyping','',1,0,0,'experiment','Terminal','Week 16',0);
INSERT INTO `phenotype_procedure` VALUES (113,219,'MGP_EEI_001',6,'Ear epidermis immunophenotyping','',1,0,0,'experiment','Terminal','Week 16',0);

INSERT INTO `phenotype_parameter` VALUES (3580,'MGP_PBI_009_001',6,'CD4+ CD44+ CD62L- alpha beta effector T cell percentage','cd4_cd44_cd62l_alpha_beta_effector_t_cell_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5577);
INSERT INTO `phenotype_parameter` VALUES (3586,'MGP_PBI_015_001',6,'IgD+ mature B cell percentage','igd_mature_b_cell_percentage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5583);
INSERT INTO `phenotype_parameter` VALUES (3803,'MGP_MLN_010_001',6,'Resting Treg cells - % of Treg','resting_treg_cells_of_treg',1,1,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5979);
INSERT INTO `phenotype_parameter` VALUES (3819,'MGP_MLN_026_001',6,'KLRG1+ CD4- NKT cells - % of CD4- NKT cells','klrg1_cd4_nkt_cells_of_cd4_nkt_cells',1,1,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',5995);
INSERT INTO `phenotype_parameter` VALUES (3851,'MGP_MLN_058_001',6,'Plasmacytoid DC - % of DC','plasmacytoid_dc_of_dc',1,1,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',6027);
INSERT INTO `phenotype_parameter` VALUES (3864,'MGP_MLN_071_001',6,'Resting Treg cells - % of CD45+','resting_treg_cells_of_cd45',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'',6040);
INSERT INTO `phenotype_parameter` VALUES (3895,'MGP_MLN_102_001',6,'Early germinal centre B cells - % of CD45+','early_germinal_centre_b_cells_of_cd45',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'',6071);
INSERT INTO `phenotype_parameter` VALUES (3904,'MGP_MLN_111_001',6,'Granulocytes - % of CD45+','granulocytes_of_cd45',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'',6080);
INSERT INTO `phenotype_parameter` VALUES (3908,'MGP_MLN_115_001',6,'Plasmacytoid DC - % of CD45+','plasmacytoid_dc_of_cd45',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'',6084);
INSERT INTO `phenotype_parameter` VALUES (4045,'MGP_IMM_047_001',6,'Transitional 2 B cells - % of B2 cells','transitional_2_b_cells_of_b2_cells',1,1,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',6326);
INSERT INTO `phenotype_parameter` VALUES (4115,'MGP_IMM_117_001',6,'Memory B cells - % of CD45+','memory_b_cells_of_cd45',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'',6396);
INSERT INTO `phenotype_parameter` VALUES (3616,'MGP_ANA_001_001',6,'ANA score','ana_score',1,0,'','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'This is the raw score data that is then classified into positive or negative',5613);
INSERT INTO `phenotype_parameter` VALUES (3617,'MGP_ANA_002_001',6,'ANA classification','ana_classification',1,1,'','TEXT','simpleParameter','ANA score >= 2 -> Positive; Negative',1,0,0,1,1,0,1,0,0,'This is the parameter we would like analysing like any other categorical data',5614);
INSERT INTO `phenotype_parameter` VALUES (4140,'MGP_IMM_007_001',6,'Resting CD4+ T helper cells - % of CD4+ T helper cells','resting_cd4_t_helper_cells_of_cd4_t_helper_cells',1,1,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',6286);
INSERT INTO `phenotype_parameter` VALUES (3772,'MGP_EEI_002_001',6,'DETC coverage','detc_coverage',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'',5633);

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

INSERT INTO `genomic_feature` VALUES ('MGI:894679',3,'Bach2','BTB and CNC homology, basic leucine zipper transcription factor 2','1',2,'2',2,4,32238804,32586104,1,'14.02','active');


INSERT INTO `allele` VALUES ('MGI:5637047',3,'MGI:1917747',3,'CV:000000101',3,'Arhgap17<tm1b(EUCOMM)Wtsi>','targeted mutation 1b, Wellcome Trust Sanger Institute');
INSERT INTO `allele` VALUES ('MGI:4842593',3,'MGI:105303',3,'CV:000000101',3,'Cxcr2<tm1a(EUCOMM)Wtsi>','targeted mutation 1a, Wellcome Trust Sanger Institute');
INSERT INTO `allele` VALUES ('MGI:5694183',3,'MGI:2384409',3,'CV:000000101',3,'Il27<tm1b(EUCOMM)Wtsi>','targeted mutation 1b, Wellcome Trust Sanger Institute');
INSERT INTO `allele` VALUES ('MGI:4461695',3,'MGI:1915276',3,'CV:000000101',3,'Pclaf<tm1a(EUCOMM)Wtsi>','targeted mutation 1a, Wellcome Trust Sanger Institute');
INSERT INTO `allele` VALUES ('MGI:5692712',3,'MGI:1919247',3,'CV:000000101',3,'Smg9<tm1b(EUCOMM)Wtsi>','targeted mutation 1b, Wellcome Trust Sanger Institute');
INSERT INTO `allele` VALUES ('MGI:5548883',3,'MGI:3036236',3,'CV:000000101',3,'Tceal5<tm1b(KOMP)Wtsi>','targeted mutation 1b, Wellcome Trust Sanger Institute');
INSERT INTO `allele` VALUES ('MGI:5636887',3,'MGI:102784',3,'CV:000000101',3,'Tgfb1i1<tm1b(KOMP)Wtsi>','targeted mutation 1b, Wellcome Trust Sanger Institute');
INSERT INTO `allele` VALUES ('NULL-B4B97ABCD',22,'MGI:99495',3,'CV:000000101',3,'Brd2<em1(IMPC)Wtsi>','Brd2<em1(IMPC)Wtsi>');
INSERT INTO `allele` VALUES ('MGI:4882014',3,'MGI:894679',3,'CV:000000101',3,'Bach2<tm1a(EUCOMM)Wtsi>','targeted mutation 1a, Wellcome Trust Sanger Institute');

INSERT INTO `strain` VALUES ('MGI:2159965',3,'CV:00000025',3,'C57BL/6N');


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

INSERT INTO `phenotyped_colony` VALUES (6049,'MFZQ','EPD0689_1_H02','MGI:894679',3,'Bach2<tm1a(EUCOMM)Wtsi>','C57BL/6N','MGI:2159965',3,8,3,8);

INSERT INTO `biological_model` VALUES (21960,3,'Arhgap17<tm1b(EUCOMM)Wtsi>/Arhgap17<tm1b(EUCOMM)Wtsi>','C57BL/6N-Arhgap17<tm1b(EUCOMM)Wtsi>/Wtsi',NULL);
INSERT INTO `biological_model` VALUES (24855,3,'Cxcr2<tm1a(EUCOMM)Wtsi>/Cxcr2<tm1a(EUCOMM)Wtsi>','C57BL/6N-Cxcr2<tm1a(EUCOMM)Wtsi>/Wtsi',NULL);
INSERT INTO `biological_model` VALUES (28633,3,'Pclaf<tm1a(EUCOMM)Wtsi>/Pclaf<tm1a(EUCOMM)Wtsi>','C57BL/6N-Pclaf<tm1a(EUCOMM)Wtsi>/Wtsi',NULL);
INSERT INTO `biological_model` VALUES (29100,3,'Smg9<tm1b(EUCOMM)Wtsi>/Smg9<tm1b(EUCOMM)Wtsi>','C57BL/6N-Smg9<tm1b(EUCOMM)Wtsi>/Wtsi',NULL);
INSERT INTO `biological_model` VALUES (33387,3,'Tgfb1i1<tm1b(KOMP)Wtsi>/Tgfb1i1<tm1b(KOMP)Wtsi>','C57BL/6N-Tgfb1i1<tm1b(KOMP)Wtsi>/Wtsi',NULL);
INSERT INTO `biological_model` VALUES (39064,22,'Brd2<em1(IMPC)Wtsi>/Brd2<+>','involves: C57BL/6N','heterozygote');
INSERT INTO `biological_model` VALUES (39160,22,'Pclaf<tm1a(EUCOMM)Wtsi>/Pclaf<tm1a(EUCOMM)Wtsi>','involves: C57BL/6N','homozygote');
INSERT INTO `biological_model` VALUES (39430,22,'Arhgap17<tm1b(EUCOMM)Wtsi>/Arhgap17<tm1b(EUCOMM)Wtsi>','involves: C57BL/6N','homozygote');
INSERT INTO `biological_model` VALUES (39716,22,'Tceal5<tm1b(KOMP)Wtsi>/0','involves: C57BL/6N','hemizygote');
INSERT INTO `biological_model` VALUES (39718,22,'Tceal5<tm1b(KOMP)Wtsi>/Tceal5<+>','involves: C57BL/6N','heterozygote');
INSERT INTO `biological_model` VALUES (39719,22,'Tceal5<tm1b(KOMP)Wtsi>/Tceal5<tm1b(KOMP)Wtsi>','involves: C57BL/6N','homozygote');
INSERT INTO `biological_model` VALUES (40084,22,'Cxcr2<tm1a(EUCOMM)Wtsi>/Cxcr2<tm1a(EUCOMM)Wtsi>','involves: C57BL/6N','homozygote');
INSERT INTO `biological_model` VALUES (40091,22,'Cxcr2<tm1a(EUCOMM)Wtsi>/Cxcr2<+>','involves: C57BL/6N','heterozygote');
INSERT INTO `biological_model` VALUES (40541,22,'Tgfb1i1<tm1b(KOMP)Wtsi>/Tgfb1i1<tm1b(KOMP)Wtsi>','involves: C57BL/6N','homozygote');
INSERT INTO `biological_model` VALUES (40556,22,'Tgfb1i1<tm1b(KOMP)Wtsi>/Tgfb1i1<+>','involves: C57BL/6N','heterozygote');
INSERT INTO `biological_model` VALUES (44162,23,'Il27<tm1b(EUCOMM)Wtsi>/Il27<tm1b(EUCOMM)Wtsi>','involves: C57BL/6N','homozygote');
INSERT INTO `biological_model` VALUES (44207,23,'Smg9<tm1b(EUCOMM)Wtsi>/Smg9<+>','involves: C57BL/6N','heterozygote');
INSERT INTO `biological_model` VALUES (44274,23,'Tgfb1i1<tm1b(KOMP)Wtsi>/Tgfb1i1<tm1b(KOMP)Wtsi>','involves: C57BL/6N','homozygote');
INSERT INTO `biological_model` VALUES (44307,23,'Brd2<em1(IMPC)Wtsi>/Brd2<+>','involves: C57BL/6N','heterozygote');
INSERT INTO `biological_model` VALUES (44360,23,'Arhgap17<tm1b(EUCOMM)Wtsi>/Arhgap17<tm1b(EUCOMM)Wtsi>','involves: C57BL/6N','homozygote');
INSERT INTO `biological_model` VALUES (44467,23,'Tceal5<tm1b(KOMP)Wtsi>/0','involves: C57BL/6N','hemizygote');
INSERT INTO `biological_model` VALUES (44476,23,'Tceal5<tm1b(KOMP)Wtsi>/Tceal5<tm1b(KOMP)Wtsi>','involves: C57BL/6N','homozygote');
INSERT INTO `biological_model` VALUES (44483,23,'Pclaf<tm1a(EUCOMM)Wtsi>/Pclaf<tm1a(EUCOMM)Wtsi>','involves: C57BL/6N','homozygote');
INSERT INTO `biological_model` VALUES (44617,23,'Cxcr2<tm1a(EUCOMM)Wtsi>/Cxcr2<tm1a(EUCOMM)Wtsi>','involves: C57BL/6N','homozygote');
INSERT INTO `biological_model` VALUES (45706,22,'Brd2<em1(IMPC)Wtsi>/Brd2<em1(IMPC)Wtsi>','involves: C57BL/6N','homozygote');
INSERT INTO `biological_model` VALUES (45007,23,'Bach2<tm1a(EUCOMM)Wtsi>/Bach2<tm1a(EUCOMM)Wtsi>','involves: C57BL/6N','homozygote');

INSERT INTO `biological_model_strain` VALUES (39064,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (39160,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (39430,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (39716,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (39718,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (39719,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (40084,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (40091,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (40541,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (40556,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (44162,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (44207,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (44274,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (44307,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (44360,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (44467,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (44476,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (44483,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (44617,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (45706,'MGI:2159965',3);
INSERT INTO `biological_model_strain` VALUES (45007,'MGI:2159965',3);

INSERT INTO `biological_model_allele` VALUES (1088,'MGI:4436065',3);
INSERT INTO `biological_model_allele` VALUES (1116,'MGI:4461798',3);
INSERT INTO `biological_model_allele` VALUES (1683,'MGI:3045706',3);
INSERT INTO `biological_model_allele` VALUES (1816,'MGI:3916086',3);
INSERT INTO `biological_model_allele` VALUES (2101,'MGI:4461745',3);
INSERT INTO `biological_model_allele` VALUES (2557,'MGI:3840084',3);
INSERT INTO `biological_model_allele` VALUES (3281,'MGI:4461705',3);
INSERT INTO `biological_model_allele` VALUES (3402,'MGI:4461713',3);
INSERT INTO `biological_model_allele` VALUES (3689,'MGI:5544207',3);
INSERT INTO `biological_model_allele` VALUES (6025,'MGI:3639430',3);
INSERT INTO `biological_model_allele` VALUES (6504,'MGI:4436032',3);
INSERT INTO `biological_model_allele` VALUES (7873,'MGI:4436072',3);
INSERT INTO `biological_model_allele` VALUES (15270,'MGI:4430733',3);
INSERT INTO `biological_model_allele` VALUES (16392,'MGI:4461777',3);
INSERT INTO `biological_model_allele` VALUES (16402,'MGI:4461729',3);
INSERT INTO `biological_model_allele` VALUES (16409,'MGI:4440556',3);
INSERT INTO `biological_model_allele` VALUES (19665,'MGI:4461737',3);
INSERT INTO `biological_model_allele` VALUES (21960,'MGI:5637047',3);
INSERT INTO `biological_model_allele` VALUES (24855,'MGI:4842593',3);
INSERT INTO `biological_model_allele` VALUES (25392,'MGI:5446173',3);
INSERT INTO `biological_model_allele` VALUES (26406,'MGI:4443071',3);
INSERT INTO `biological_model_allele` VALUES (27818,'MGI:4436082',3);
INSERT INTO `biological_model_allele` VALUES (28633,'MGI:4461695',3);
INSERT INTO `biological_model_allele` VALUES (29100,'MGI:5692712',3);
INSERT INTO `biological_model_allele` VALUES (33387,'MGI:5636887',3);
INSERT INTO `biological_model_allele` VALUES (35551,'MGI:4436065',3);
INSERT INTO `biological_model_allele` VALUES (35654,'MGI:4436072',3);
INSERT INTO `biological_model_allele` VALUES (35737,'MGI:4436032',3);
INSERT INTO `biological_model_allele` VALUES (36245,'MGI:4441628',3);
INSERT INTO `biological_model_allele` VALUES (36352,'MGI:4436082',3);
INSERT INTO `biological_model_allele` VALUES (39064,'NULL-B4B97ABCD',22);
INSERT INTO `biological_model_allele` VALUES (39160,'MGI:4461695',3);
INSERT INTO `biological_model_allele` VALUES (39430,'MGI:5637047',3);
INSERT INTO `biological_model_allele` VALUES (39516,'MGI:4461745',3);
INSERT INTO `biological_model_allele` VALUES (39534,'MGI:4436065',3);
INSERT INTO `biological_model_allele` VALUES (39536,'MGI:4436065',3);
INSERT INTO `biological_model_allele` VALUES (39716,'MGI:5548883',3);
INSERT INTO `biological_model_allele` VALUES (39718,'MGI:5548883',3);
INSERT INTO `biological_model_allele` VALUES (39719,'MGI:5548883',3);
INSERT INTO `biological_model_allele` VALUES (39824,'MGI:4461705',3);
INSERT INTO `biological_model_allele` VALUES (39825,'MGI:4461737',3);
INSERT INTO `biological_model_allele` VALUES (40084,'MGI:4842593',3);
INSERT INTO `biological_model_allele` VALUES (40091,'MGI:4842593',3);
INSERT INTO `biological_model_allele` VALUES (40512,'MGI:4461713',3);
INSERT INTO `biological_model_allele` VALUES (40541,'MGI:5636887',3);
INSERT INTO `biological_model_allele` VALUES (40556,'MGI:5636887',3);
INSERT INTO `biological_model_allele` VALUES (40720,'MGI:4441628',3);
INSERT INTO `biological_model_allele` VALUES (42277,'MGI:5446173',3);
INSERT INTO `biological_model_allele` VALUES (44154,'MGI:4461729',3);
INSERT INTO `biological_model_allele` VALUES (44162,'MGI:5694183',3);
INSERT INTO `biological_model_allele` VALUES (44207,'MGI:5692712',3);
INSERT INTO `biological_model_allele` VALUES (44274,'MGI:5636887',3);
INSERT INTO `biological_model_allele` VALUES (44307,'NULL-B4B97ABCD',22);
INSERT INTO `biological_model_allele` VALUES (44360,'MGI:5637047',3);
INSERT INTO `biological_model_allele` VALUES (44467,'MGI:5548883',3);
INSERT INTO `biological_model_allele` VALUES (44476,'MGI:5548883',3);
INSERT INTO `biological_model_allele` VALUES (44479,'MGI:4436065',3);
INSERT INTO `biological_model_allele` VALUES (44483,'MGI:4461695',3);
INSERT INTO `biological_model_allele` VALUES (44617,'MGI:4842593',3);
INSERT INTO `biological_model_allele` VALUES (44632,'MGI:4461713',3);
INSERT INTO `biological_model_allele` VALUES (45230,'MGI:5446173',3);
INSERT INTO `biological_model_allele` VALUES (45427,'MGI:4461745',3);
INSERT INTO `biological_model_allele` VALUES (45706,'NULL-B4B97ABCD',22);
INSERT INTO `biological_model_allele` VALUES (45007,'MGI:4882014',3);

INSERT INTO `biological_model_genomic_feature` VALUES (950,'MGI:3833387',3);
INSERT INTO `biological_model_genomic_feature` VALUES (1093,'MGI:1333874',3);
INSERT INTO `biological_model_genomic_feature` VALUES (1379,'MGI:1333877',3);
INSERT INTO `biological_model_genomic_feature` VALUES (1430,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (1432,'MGI:2444672',3);
INSERT INTO `biological_model_genomic_feature` VALUES (1898,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (1903,'MGI:1344360',3);
INSERT INTO `biological_model_genomic_feature` VALUES (3367,'MGI:2144467',3);
INSERT INTO `biological_model_genomic_feature` VALUES (4114,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (4598,'MGI:1333878',3);
INSERT INTO `biological_model_genomic_feature` VALUES (5114,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (6776,'MGI:2144467',3);
INSERT INTO `biological_model_genomic_feature` VALUES (7883,'MGI:2444672',3);
INSERT INTO `biological_model_genomic_feature` VALUES (8677,'MGI:2442746',3);
INSERT INTO `biological_model_genomic_feature` VALUES (8992,'MGI:2444207',3);
INSERT INTO `biological_model_genomic_feature` VALUES (10577,'MGI:1344360',3);
INSERT INTO `biological_model_genomic_feature` VALUES (10694,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (12095,'MGI:1333877',3);
INSERT INTO `biological_model_genomic_feature` VALUES (13255,'MGI:2443078',3);
INSERT INTO `biological_model_genomic_feature` VALUES (13517,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (13720,'MGI:1333878',3);
INSERT INTO `biological_model_genomic_feature` VALUES (15270,'MGI:4430736',3);
INSERT INTO `biological_model_genomic_feature` VALUES (16887,'MGI:2443076',3);
INSERT INTO `biological_model_genomic_feature` VALUES (16960,'MGI:1929100',3);
INSERT INTO `biological_model_genomic_feature` VALUES (17147,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (17428,'MGI:1333870',3);
INSERT INTO `biological_model_genomic_feature` VALUES (18984,'MGI:3833387',3);
INSERT INTO `biological_model_genomic_feature` VALUES (19349,'MGI:1344360',3);
INSERT INTO `biological_model_genomic_feature` VALUES (19961,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (21636,'MGI:1333875',3);
INSERT INTO `biological_model_genomic_feature` VALUES (21670,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (21797,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (21960,'MGI:1917747',3);
INSERT INTO `biological_model_genomic_feature` VALUES (22347,'MGI:2446176',3);
INSERT INTO `biological_model_genomic_feature` VALUES (22637,'MGI:2446175',3);
INSERT INTO `biological_model_genomic_feature` VALUES (23193,'MGI:2446176',3);
INSERT INTO `biological_model_genomic_feature` VALUES (24294,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (24855,'MGI:105303',3);
INSERT INTO `biological_model_genomic_feature` VALUES (26027,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (26964,'MGI:2446176',3);
INSERT INTO `biological_model_genomic_feature` VALUES (28612,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (28633,'MGI:1915276',3);
INSERT INTO `biological_model_genomic_feature` VALUES (29100,'MGI:1919247',3);
INSERT INTO `biological_model_genomic_feature` VALUES (29409,'MGI:1333870',3);
INSERT INTO `biological_model_genomic_feature` VALUES (30785,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (31586,'MGI:2444207',3);
INSERT INTO `biological_model_genomic_feature` VALUES (31949,'MGI:2443075',3);
INSERT INTO `biological_model_genomic_feature` VALUES (32362,'MGI:1333875',3);
INSERT INTO `biological_model_genomic_feature` VALUES (32719,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (32898,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (32936,'MGI:1929100',3);
INSERT INTO `biological_model_genomic_feature` VALUES (33064,'MGI:1333879',3);
INSERT INTO `biological_model_genomic_feature` VALUES (33387,'MGI:102784',3);
INSERT INTO `biological_model_genomic_feature` VALUES (35357,'MGI:1344360',3);
INSERT INTO `biological_model_genomic_feature` VALUES (35500,'MGI:1333875',3);
INSERT INTO `biological_model_genomic_feature` VALUES (35640,'MGI:2446176',3);
INSERT INTO `biological_model_genomic_feature` VALUES (35758,'MGI:2443076',3);
INSERT INTO `biological_model_genomic_feature` VALUES (35759,'MGI:2443076',3);
INSERT INTO `biological_model_genomic_feature` VALUES (36283,'MGI:2446175',3);
INSERT INTO `biological_model_genomic_feature` VALUES (37042,'MGI:2444207',3);
INSERT INTO `biological_model_genomic_feature` VALUES (37139,'MGI:2444207',3);
INSERT INTO `biological_model_genomic_feature` VALUES (37958,'MGI:2446175',3);
INSERT INTO `biological_model_genomic_feature` VALUES (39064,'MGI:99495',3);
INSERT INTO `biological_model_genomic_feature` VALUES (39160,'MGI:1915276',3);
INSERT INTO `biological_model_genomic_feature` VALUES (39430,'MGI:1917747',3);
INSERT INTO `biological_model_genomic_feature` VALUES (39716,'MGI:3036236',3);
INSERT INTO `biological_model_genomic_feature` VALUES (39718,'MGI:3036236',3);
INSERT INTO `biological_model_genomic_feature` VALUES (39719,'MGI:3036236',3);
INSERT INTO `biological_model_genomic_feature` VALUES (40084,'MGI:105303',3);
INSERT INTO `biological_model_genomic_feature` VALUES (40091,'MGI:105303',3);
INSERT INTO `biological_model_genomic_feature` VALUES (40541,'MGI:102784',3);
INSERT INTO `biological_model_genomic_feature` VALUES (40556,'MGI:102784',3);
INSERT INTO `biological_model_genomic_feature` VALUES (41385,'MGI:1344360',3);
INSERT INTO `biological_model_genomic_feature` VALUES (41393,'MGI:1344360',3);
INSERT INTO `biological_model_genomic_feature` VALUES (43094,'MGI:1333877',3);
INSERT INTO `biological_model_genomic_feature` VALUES (43095,'MGI:1333877',3);
INSERT INTO `biological_model_genomic_feature` VALUES (43100,'MGI:1333877',3);
INSERT INTO `biological_model_genomic_feature` VALUES (44162,'MGI:2384409',3);
INSERT INTO `biological_model_genomic_feature` VALUES (44207,'MGI:1919247',3);
INSERT INTO `biological_model_genomic_feature` VALUES (44274,'MGI:102784',3);
INSERT INTO `biological_model_genomic_feature` VALUES (44307,'MGI:99495',3);
INSERT INTO `biological_model_genomic_feature` VALUES (44360,'MGI:1917747',3);
INSERT INTO `biological_model_genomic_feature` VALUES (44467,'MGI:3036236',3);
INSERT INTO `biological_model_genomic_feature` VALUES (44476,'MGI:3036236',3);
INSERT INTO `biological_model_genomic_feature` VALUES (44483,'MGI:1915276',3);
INSERT INTO `biological_model_genomic_feature` VALUES (44617,'MGI:105303',3);
INSERT INTO `biological_model_genomic_feature` VALUES (45706,'MGI:99495',3);
INSERT INTO `biological_model_genomic_feature` VALUES (45007,'MGI:894679',3);

