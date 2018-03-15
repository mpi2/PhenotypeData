-- database: cda_test

INSERT INTO `phenotype_pipeline` VALUES
  (1,'MGP_001',6,'MGP Select Pipeline','',1,0,1,NULL);

INSERT INTO `phenotype_procedure` VALUES
  (1,1,'IMPC_FER_001',6,'Fertility of Homozygous Knock-out Mice','',1,1,1,'line','Adult','Unrestricted');

INSERT INTO genomic_feature VALUES
  ('MGI:2152453', 3, 'Gsk3a', 'glycogen synthase kinase 3 alpha', 1, 2, 2, 2, 7, 25228258, 25237851, -1, 13.73, 'active');

INSERT INTO allele VALUES
  ('MGI:4434136', 3, 'MGI:2152453', 3, 'CV:000000101', 3, 'Gsk3a<tm1a(EUCOMM)Wtsi>', 'targeted mutation 1a, Wellcome Trust Sanger Institute');


INSERT INTO `phenotype_parameter` VALUES (506,'IMPC_FER_001_001',6,'Gross Findings Male','gross_findings',1,4,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',2365);
INSERT INTO `phenotype_parameter` VALUES (507,'IMPC_FER_019_001',6,'Gross Findings Female','',1,3,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',2680);
INSERT INTO `phenotype_parameter` VALUES (508,'IMPC_FER_002_001',6,'Pups born (primary)','pups_born_primary_',1,3,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2366);
INSERT INTO `phenotype_parameter` VALUES (509,'IMPC_FER_003_001',6,'Total matings (primary)','total_matings_primary_',1,2,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2367);
INSERT INTO `phenotype_parameter` VALUES (510,'IMPC_FER_004_001',6,'Total litters (primary)','total_litters_primary_',1,3,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2368);
INSERT INTO `phenotype_parameter` VALUES (511,'IMPC_FER_005_001',6,'Total pups with dissection (primary)','total_pups_primary_',1,5,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2369);
INSERT INTO `phenotype_parameter` VALUES (512,'IMPC_FER_006_001',6,'Pups born (Male screen)','pups_born_male_screen_',1,5,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2370);
INSERT INTO `phenotype_parameter` VALUES (513,'IMPC_FER_007_001',6,'Total matings (Male screen)','total_matings_male_screen_',1,4,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2371);
INSERT INTO `phenotype_parameter` VALUES (514,'IMPC_FER_008_001',6,'Total litters (Male screen)','total_litters_male_screen_',1,1,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2372);
INSERT INTO `phenotype_parameter` VALUES (515,'IMPC_FER_009_001',6,'Total pups/embryos (Male Screen)','total_pups_embryos_male_screen_',1,2,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2373);
INSERT INTO `phenotype_parameter` VALUES (516,'IMPC_FER_010_001',6,'Pups born (Female Screen)','pups_born_female_screen_',1,5,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2374);
INSERT INTO `phenotype_parameter` VALUES (517,'IMPC_FER_011_001',6,'Total matings (Female Screen)','total_matings_female_screen_',1,4,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2375);
INSERT INTO `phenotype_parameter` VALUES (518,'IMPC_FER_012_001',6,'Total litters (Female Screen)','total_litters_female_screen_',1,1,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2376);
INSERT INTO `phenotype_parameter` VALUES (519,'IMPC_FER_013_001',6,'Total pups/embryos (Female Screen)','total_pups_embryos_female_screen_',1,2,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2377);
INSERT INTO `phenotype_parameter` VALUES (520,'IMPC_FER_014_001',6,'Age of set up','age_of_set_up',1,0,'Weeks','INT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',2378);
INSERT INTO `phenotype_parameter` VALUES (521,'IMPC_FER_015_001',6,'Time spent in breeding','time_breeding',1,2,'days','INT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',2379);
INSERT INTO `phenotype_parameter` VALUES (522,'IMPC_FER_016_001',6,'Test strain genotype','test_strain_genotype',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,0,'',2380);
INSERT INTO `phenotype_parameter` VALUES (523,'IMPC_FER_017_001',6,'Test strain background secondary  (MGI ID)','test_strain_background',1,2,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',2381);
INSERT INTO `phenotype_parameter` VALUES (524,'IMPC_FER_018_001',6,'Date of matings','date_of_matings',1,2,' ','DATE','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',2382);
INSERT INTO `phenotype_parameter` VALUES (525,'IMPC_FER_020_001',6,'Age of set up (Male screen)','',1,1,'Weeks','INT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',4272);
INSERT INTO `phenotype_parameter` VALUES (526,'IMPC_FER_021_001',6,'Age of set up (Female screen)','',1,0,'Weeks','INT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',4273);
INSERT INTO `phenotype_parameter` VALUES (527,'IMPC_FER_022_001',6,'Time spent in  breeding (Male screen)','',1,0,'days','INT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',4274);
INSERT INTO `phenotype_parameter` VALUES (528,'IMPC_FER_023_001',6,'Time spent in breeding (Female screen)','',1,0,'days','INT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',4275);

INSERT INTO phenotyped_colony VALUES
  (301,'MCCU','EPD0051_1_H11','MGI:2152453',3,'Gsk3a<tm1a(EUCOMM)Wtsi>','C57BL/6Brd-Tyr<c-Brd>;C57BL/6N;C57BL/6NTac',3,8,3,8);