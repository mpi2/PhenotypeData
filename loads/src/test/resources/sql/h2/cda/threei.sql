
INSERT INTO `phenotype_pipeline` VALUES (8,'MGP_001',6,'MGP Select Pipeline','',1,0,15,NULL);

INSERT INTO `phenotype_procedure` VALUES (120,223,'MGP_MLN_001',6,'Mesenteric Lymph Node Immunophenotyping','3i Mesenteric Lymph Node Immunophenotyping',1,0,0,'experiment','Terminal','Week 16');

INSERT INTO `phenotype_parameter` VALUES (3904,'MGP_MLN_111_001',6,'Granulocytes - % of CD45+','granulocytes_of_cd45',1,0,'%','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'',6080);
INSERT INTO `phenotype_parameter` VALUES (3819,'MGP_MLN_026_001',6,'KLRG1+ CD4- NKT cells - % of CD4- NKT cells','klrg1_cd4_nkt_cells_of_cd4_nkt_cells',1,1,'%','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',5995);

INSERT INTO `genomic_feature` VALUES
	('MGI:99495',3,'Brd2','bromodomain containing 2','1',2,'2',2,17,34112023,34122634,-1,'17.98','active');

SET @DB_ID_MGI=3;
SET @ORG_ID_WTSI=3;
SET @PROJECT_ID_THREEI=2;

INSERT INTO `phenotyped_colony` (`colony_name`, `es_cell_name`, `gf_acc`, `gf_db_id`, `allele_symbol`, `background_strain_name`, `production_centre_organisation_id`, `production_consortium_project_id`, `phenotyping_centre_organisation_id`, `phenotyping_consortium_project_id`)
VALUES
	('DAAE', NULL, 'MGI:99495', (SELECT @DB_ID_MGI), 'Brd2<em1(IMPC)Wtsi>', 'C57BL/6N', (@ORG_ID_WTSI),
	 (SELECT @PROJECT_ID_THREEI), (SELECT @ORG_ID_WTSI), (SELECT @PROJECT_ID_THREEI))

;

