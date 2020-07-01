-- database: cda_test

INSERT INTO `phenotype_pipeline` VALUES
  (1,'MGP_001',6,'MGP Select Pipeline','',1,0,1,NULL);

INSERT INTO phenotype_pipeline VALUES
  (7, 'HMGU_001', 6, 'German Mouse Clinic', 'German Mouse Clinic Pipeline for IMPC Project _ Rotarod as an additional center-specific Procedure', 1, 0, 14, 0);

INSERT INTO `phenotype_procedure` VALUES
  (1,1,'IMPC_FER_001',6,'Fertility of Homozygous Knock-out Mice','',1,1,1,'line','Adult','Unrestricted', 0),
  (14,154,'IMPC_VIA_001',6,'Viability Primary Screen','',1,0,0,'line','Adult','Unrestricted', 0),
  (102,249,'MGP_ALZ_001',6,'Adult LacZ','',1,0,0,'experiment','Adult','Unrestricted', 0);

INSERT INTO genomic_feature VALUES
  ('MGI:2152453', 3, 'Gsk3a', 'glycogen synthase kinase 3 alpha', 1, 2, 2, 2, 7, 25228258, 25237851, -1, 13.73, 'active'),
  ('MGI:1919912',3,'Dis3','DIS3 homolog, exosome endoribonuclease and 3''-5'' exoribonuclease',1,2,2,2,14,99075206,99099770,-1,49.25,'active');

INSERT INTO allele VALUES
  ('MGI:5548623',3,'MGI:1919912',3,'CV:000000101',3,'Dis3<tm1e.1(EUCOMM)Hmgu>','targeted mutation 1e.1, Helmholtz Zentrum Muenchen GmbH'),
  ('MGI:4434136', 3, 'MGI:2152453', 3, 'CV:000000101', 3, 'Gsk3a<tm1a(EUCOMM)Wtsi>', 'targeted mutation 1a, Wellcome Trust Sanger Institute');

INSERT INTO phenotyped_colony VALUES
  (3458,'HMGU-HEPD0659_5_E08-1-1','HEPD0659_5_E08','MGI:1919912',3,'Dis3<tm1e.1(EUCOMM)Hmgu>','C57BL/6NTac','MGI:2164831',9,15,9,15),
  (301,'MCCU','EPD0051_1_H11','MGI:2152453',3,'Gsk3a<tm1a(EUCOMM)Wtsi>','C57BL/6Brd-Tyr<c-Brd>;C57BL/6N;C57BL/6NTac','IMPC-CURATE-C44BE',3,8,3,8);

INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);

INSERT INTO strain VALUES ('MGI:5446362', 3, 'CV:00000005', 3, 'B6Brd;B6Dnk;B6N-Tyr<c-Brd>');
INSERT INTO strain VALUES ('IMPC-CURATE-C44BE', 22, 'CV:00000051', 3, 'C57BL/6Brd-Tyr<c-Brd>;C57BL/6N;C57BL/6NTac');

INSERT INTO `phenotype_parameter` VALUES (506,'IMPC_FER_001_001',6,'Gross Findings Male','gross_findings',1,4,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',2365);
INSERT INTO `phenotype_parameter` VALUES (507,'IMPC_FER_019_001',6,'Gross Findings Female','',1,3,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',2680);
INSERT INTO `phenotype_parameter` VALUES (508,'IMPC_FER_002_001',6,'Pups born (primary)','pups_born_primary_',1,3,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2366);
INSERT INTO `phenotype_parameter` VALUES (509,'IMPC_FER_003_001',6,'Total matings (primary)','total_matings_primary_',1,2,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2367);
INSERT INTO `phenotype_parameter` VALUES (510,'IMPC_FER_004_001',6,'Total litters (primary)','total_litters_primary_',1,3,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2368);
INSERT INTO `phenotype_parameter` VALUES (511,'IMPC_FER_005_001',6,'Total pups with dissection (primary)','total_pups_primary_',1,5,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2369);
INSERT INTO `phenotype_parameter` VALUES (512,'IMPC_FER_006_001',6,'Pups born (Male screen)','pups_born_male_screen_',1,5,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2370);
INSERT INTO `phenotype_parameter` VALUES (513,'IMPC_FER_007_001',6,'Total matings (Male screen)','total_matings_male_screen_',1,4,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2371);
INSERT INTO `phenotype_parameter` VALUES (514,'IMPC_FER_008_001',6,'Total litters (Male screen)','total_litters_male_screen_',1,1,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2372);
INSERT INTO `phenotype_parameter` VALUES (515,'IMPC_FER_009_001',6,'Total pups/embryos (Male Screen)','total_pups_embryos_male_screen_',1,2,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2373);
INSERT INTO `phenotype_parameter` VALUES (516,'IMPC_FER_010_001',6,'Pups born (Female Screen)','pups_born_female_screen_',1,5,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2374);
INSERT INTO `phenotype_parameter` VALUES (517,'IMPC_FER_011_001',6,'Total matings (Female Screen)','total_matings_female_screen_',1,4,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2375);
INSERT INTO `phenotype_parameter` VALUES (518,'IMPC_FER_012_001',6,'Total litters (Female Screen)','total_litters_female_screen_',1,1,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2376);
INSERT INTO `phenotype_parameter` VALUES (519,'IMPC_FER_013_001',6,'Total pups/embryos (Female Screen)','total_pups_embryos_female_screen_',1,2,'count','INT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2377);
INSERT INTO `phenotype_parameter` VALUES (520,'IMPC_FER_014_001',6,'Age of set up','age_of_set_up',1,0,'Weeks','INT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',2378);
INSERT INTO `phenotype_parameter` VALUES (521,'IMPC_FER_015_001',6,'Time spent in breeding','time_breeding',1,2,'days','INT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',2379);
INSERT INTO `phenotype_parameter` VALUES (522,'IMPC_FER_016_001',6,'Test strain genotype','test_strain_genotype',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',2380);
INSERT INTO `phenotype_parameter` VALUES (523,'IMPC_FER_017_001',6,'Test strain background secondary  (MGI ID)','test_strain_background',1,2,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',2381);
INSERT INTO `phenotype_parameter` VALUES (524,'IMPC_FER_018_001',6,'Date of matings','date_of_matings',1,2,' ','DATE','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',2382);
INSERT INTO `phenotype_parameter` VALUES (525,'IMPC_FER_020_001',6,'Age of set up (Male screen)','',1,1,'Weeks','INT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',4272);
INSERT INTO `phenotype_parameter` VALUES (526,'IMPC_FER_021_001',6,'Age of set up (Female screen)','',1,0,'Weeks','INT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',4273);
INSERT INTO `phenotype_parameter` VALUES (527,'IMPC_FER_022_001',6,'Time spent in  breeding (Male screen)','',1,0,'days','INT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',4274);
INSERT INTO `phenotype_parameter` VALUES (528,'IMPC_FER_023_001',6,'Time spent in breeding (Female screen)','',1,0,'days','INT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',4275);



INSERT INTO `phenotype_parameter` VALUES (3322,'MGP_ALZ_082_001',6,'Adrenal Gland','adrenal_gland',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6859);
INSERT INTO `phenotype_parameter` VALUES (3323,'MGP_ALZ_083_001',6,'Lower Urinary Tract','lower_urinary_tract',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6860);
INSERT INTO `phenotype_parameter` VALUES (3324,'MGP_ALZ_084_001',6,'Brain','brain',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6861);
INSERT INTO `phenotype_parameter` VALUES (3325,'MGP_ALZ_085_001',6,'Eye','eye',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6862);
INSERT INTO `phenotype_parameter` VALUES (3326,'MGP_ALZ_086_001',6,'Heart','heart',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6863);
INSERT INTO `phenotype_parameter` VALUES (3327,'MGP_ALZ_087_001',6,'Kidney','kidney',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6864);
INSERT INTO `phenotype_parameter` VALUES (3328,'MGP_ALZ_088_001',6,'Liver','liver',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6865);
INSERT INTO `phenotype_parameter` VALUES (3329,'MGP_ALZ_089_001',6,'Lymph Node','lymph_node',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6866);
INSERT INTO `phenotype_parameter` VALUES (3330,'MGP_ALZ_090_001',6,'Mammary gland','mammary_gland',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6867);
INSERT INTO `phenotype_parameter` VALUES (3331,'MGP_ALZ_091_001',6,'Ovary','ovary',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6868);
INSERT INTO `phenotype_parameter` VALUES (3332,'MGP_ALZ_092_001',6,'Oviduct','oviduct',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6869);
INSERT INTO `phenotype_parameter` VALUES (3333,'MGP_ALZ_093_001',6,'Skin','skin',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6870);
INSERT INTO `phenotype_parameter` VALUES (3334,'MGP_ALZ_094_001',6,'Stomach','stomach',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6871);
INSERT INTO `phenotype_parameter` VALUES (3335,'MGP_ALZ_095_001',6,'Testis','testis',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6872);
INSERT INTO `phenotype_parameter` VALUES (3336,'MGP_ALZ_096_001',6,'Thymus','thymus',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6873);
INSERT INTO `phenotype_parameter` VALUES (3337,'MGP_ALZ_097_001',6,'Trachea','trachea',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6874);
INSERT INTO `phenotype_parameter` VALUES (3338,'MGP_ALZ_098_001',6,'Uterus','uterus',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6875);
INSERT INTO `phenotype_parameter` VALUES (3339,'MGP_ALZ_099_001',6,'Large Intestine','large_intestine',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6876);
INSERT INTO `phenotype_parameter` VALUES (3340,'MGP_ALZ_100_001',6,'Oesophagus','oesophagus',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6877);
INSERT INTO `phenotype_parameter` VALUES (3341,'MGP_ALZ_101_001',6,'Spinal cord','spinal_cord',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6878);
INSERT INTO `phenotype_parameter` VALUES (3342,'MGP_ALZ_102_001',6,'Thyroid gland','thyroid_gland',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6879);
INSERT INTO `phenotype_parameter` VALUES (3343,'MGP_ALZ_103_001',6,'Pituitary gland','pituitary_gland',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6880);
INSERT INTO `phenotype_parameter` VALUES (3344,'MGP_ALZ_104_001',6,'Skeletal muscle tissue','skeletal_muscle_tissue',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6881);
INSERT INTO `phenotype_parameter` VALUES (3345,'MGP_ALZ_105_001',6,'Small Intestine','small_intestine',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6882);
INSERT INTO `phenotype_parameter` VALUES (3346,'MGP_ALZ_106_001',6,'White Adipose Tissue','white_adipose_tissue',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6883);
INSERT INTO `phenotype_parameter` VALUES (3347,'MGP_ALZ_107_001',6,'Lung','lung',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6884);
INSERT INTO `phenotype_parameter` VALUES (3348,'MGP_ALZ_108_001',6,'Aorta','aorta',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6885);
INSERT INTO `phenotype_parameter` VALUES (3349,'MGP_ALZ_109_001',6,'Pancreas','pancreas',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6886);
INSERT INTO `phenotype_parameter` VALUES (3350,'MGP_ALZ_110_001',6,'Prostate gland','prostate_gland',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6887);
INSERT INTO `phenotype_parameter` VALUES (3351,'MGP_ALZ_111_001',6,'Spleen','spleen',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6888);
INSERT INTO `phenotype_parameter` VALUES (3352,'MGP_ALZ_112_001',6,'Brainstem','brainstem',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6889);
INSERT INTO `phenotype_parameter` VALUES (3353,'MGP_ALZ_113_001',6,'Cartilage tissue','cartilage_tissue',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6890);
INSERT INTO `phenotype_parameter` VALUES (3354,'MGP_ALZ_114_001',6,'Cerebral Cortex','cerebral_cortex',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6891);
INSERT INTO `phenotype_parameter` VALUES (3355,'MGP_ALZ_115_001',6,'Cerebellum','cerebellum',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6892);
INSERT INTO `phenotype_parameter` VALUES (3356,'MGP_ALZ_116_001',6,'Hippocampus','hippocampus',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6893);
INSERT INTO `phenotype_parameter` VALUES (3357,'MGP_ALZ_117_001',6,'Hypothalamus','hypothalamus',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6894);
INSERT INTO `phenotype_parameter` VALUES (3358,'MGP_ALZ_118_001',6,'Peripheral Nervous System','peripheral_nervous_system',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6895);
INSERT INTO `phenotype_parameter` VALUES (3359,'MGP_ALZ_119_001',6,'Peyers Patch','peyer_s_patch',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6896);
INSERT INTO `phenotype_parameter` VALUES (3360,'MGP_ALZ_120_001',6,'Striatum','striatum',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6897);
INSERT INTO `phenotype_parameter` VALUES (3361,'MGP_ALZ_121_001',6,'Blood vessel','blood_vessel',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6898);
INSERT INTO `phenotype_parameter` VALUES (3362,'MGP_ALZ_122_001',6,'Bone','bone',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6899);
INSERT INTO `phenotype_parameter` VALUES (3363,'MGP_ALZ_123_001',6,'Brown Adipose Tissue','brown_adipose_tissue',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6900);
INSERT INTO `phenotype_parameter` VALUES (3364,'MGP_ALZ_124_001',6,'Gall Bladder','gall_bladder',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6901);
INSERT INTO `phenotype_parameter` VALUES (3365,'MGP_ALZ_125_001',6,'Parathyroid gland','parathyroid_gland',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6902);
INSERT INTO `phenotype_parameter` VALUES (3366,'MGP_ALZ_126_001',6,'Olfactory lobe','olfactory_lobe',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',6903);
INSERT INTO `phenotype_parameter` VALUES (3367,'MGP_ALZ_127_001',6,'LacZ Images Wholemount','lacz_images_wholemount',1,0,'','IMAGE','seriesMediaParameter',NULL,0,0,0,0,0,1,0,0,0,'',6904);
INSERT INTO `phenotype_parameter` VALUES (3368,'MGP_ALZ_128_001',6,'Free Ontology','free_ontology',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',6905);
INSERT INTO `phenotype_parameter` VALUES (3369,'MGP_ALZ_129_001',6,'Free text comment','free_text_comment',1,0,'','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',6906);
INSERT INTO `phenotype_parameter` VALUES (3370,'MGP_ALZ_130_001',6,'Microscope equipment manufacturer','microscope_equipment_manufacturer',1,0,'','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',6907);
INSERT INTO `phenotype_parameter` VALUES (3371,'MGP_ALZ_131_001',6,'Microscope equipment model','microscope_equipment_model',1,0,'','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',6908);
INSERT INTO `phenotype_parameter` VALUES (3372,'MGP_ALZ_132_001',6,'Camera equipment model','camera_equipment_model',1,0,'','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',6909);
INSERT INTO `phenotype_parameter` VALUES (3373,'MGP_ALZ_133_001',6,'Sample preparation stain name','sample_preparation_stain_name',1,0,'','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',6910);
INSERT INTO `phenotype_parameter` VALUES (3374,'MGP_ALZ_134_001',6,'Sample preparation reference impc','sample_preparation_reference_impc',1,0,'','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',6911);
INSERT INTO `phenotype_parameter` VALUES (3375,'MGP_ALZ_135_001',6,'Date of Sacrifice','date_of_sacrifice',1,0,'','DATE','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',6912);

INSERT INTO phenotype_parameter VALUES (634, 'IMPC_VIA_001_001', 6, 'Outcome', '', 1, 1, ' ', 'TEXT', 'simpleParameter', null, 1, 0, 0, 0, 1, 0, 1, 0, 0, '', 3794);
INSERT INTO phenotype_parameter VALUES (635, 'IMPC_VIA_002_001', 6, 'Additional Outcome', '', 1, 1, ' ', 'TEXT', 'simpleParameter', null, 0, 0, 0, 0, 1, 0, 1, 0, 0, '', 3795);
INSERT INTO phenotype_parameter VALUES (637, 'IMPC_VIA_003_001', 6, 'Total pups', '', 1, 1, 'count', 'INT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 3796);
INSERT INTO phenotype_parameter VALUES (638, 'IMPC_VIA_004_001', 6, 'Total pups WT', '', 1, 1, 'count', 'INT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 3797);
INSERT INTO phenotype_parameter VALUES (639, 'IMPC_VIA_005_001', 6, 'Total pups heterozygous', '', 1, 0, 'count', 'INT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 3798);
INSERT INTO phenotype_parameter VALUES (640, 'IMPC_VIA_006_001', 6, 'Total pups homozygous', '', 1, 0, 'count', 'INT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 3799);
INSERT INTO phenotype_parameter VALUES (641, 'IMPC_VIA_007_001', 6, 'Total male WT', '', 1, 0, 'count', 'INT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 3800);
INSERT INTO phenotype_parameter VALUES (642, 'IMPC_VIA_008_001', 6, 'Total male heterozygous', '', 1, 0, 'count', 'INT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 3801);
INSERT INTO phenotype_parameter VALUES (643, 'IMPC_VIA_009_001', 6, 'Total male homozygous', '', 1, 1, 'count', 'INT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 3802);
INSERT INTO phenotype_parameter VALUES (644, 'IMPC_VIA_010_001', 6, 'Total male pups', '', 1, 0, 'count', 'INT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 3803);
INSERT INTO phenotype_parameter VALUES (645, 'IMPC_VIA_011_001', 6, 'Total female WT', '', 1, 0, 'count', 'INT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 3804);
INSERT INTO phenotype_parameter VALUES (646, 'IMPC_VIA_012_001', 6, 'Total female heterozygous', '', 1, 0, 'count', 'INT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 3805);
INSERT INTO phenotype_parameter VALUES (647, 'IMPC_VIA_013_001', 6, 'Total female homozygous', '', 1, 0, 'count', 'INT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 3806);
INSERT INTO phenotype_parameter VALUES (648, 'IMPC_VIA_014_001', 6, 'Total female pups', '', 1, 1, 'count', 'INT', 'simpleParameter', null, 1, 0, 0, 0, 0, 0, 0, 0, 0, '', 3807);
INSERT INTO phenotype_parameter VALUES (652, 'IMPC_VIA_015_001', 6, '% pups WT', '', 1, 3, '%', 'FLOAT', 'simpleParameter', 'IMPC_VIA_004_001 IMPC_VIA_003_001 /', 0, 0, 0, 1, 0, 0, 0, 0, 0, '', 3808);
INSERT INTO phenotype_parameter VALUES (649, 'IMPC_VIA_016_001', 6, 'Free Comment', '', 1, 0, ' ', 'TEXT', 'simpleParameter', null, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', 3809);
INSERT INTO phenotype_parameter VALUES (650, 'IMPC_VIA_017_001', 6, 'Average litter size', '', 1, 0, ' ', 'FLOAT', 'simpleParameter', null, 0, 0, 0, 0, 0, 0, 0, 0, 0, '', 3810);
INSERT INTO phenotype_parameter VALUES (653, 'IMPC_VIA_018_001', 6, '% pups heterozygous', '', 1, 2, '%', 'FLOAT', 'simpleParameter', 'IMPC_VIA_005_001 IMPC_VIA_003_001 /', 0, 0, 0, 1, 0, 0, 0, 0, 0, '', 3811);
INSERT INTO phenotype_parameter VALUES (654, 'IMPC_VIA_019_001', 6, '% pups homozygous', '', 1, 1, '%', 'FLOAT', 'simpleParameter', 'IMPC_VIA_006_001 IMPC_VIA_003_001 /', 0, 0, 0, 1, 0, 0, 0, 0, 0, '', 3812);
INSERT INTO phenotype_parameter VALUES (655, 'IMPC_VIA_020_001', 6, '% male WT', '', 1, 1, '%', 'FLOAT', 'simpleParameter', 'IMPC_VIA_007_001 IMPC_VIA_010_001 /', 0, 0, 0, 1, 0, 0, 0, 0, 0, '', 3813);
INSERT INTO phenotype_parameter VALUES (656, 'IMPC_VIA_021_001', 6, '% male heterozygous', '', 1, 1, '%', 'FLOAT', 'simpleParameter', 'IMPC_VIA_008_001 IMPC_VIA_010_001 /', 0, 0, 0, 1, 0, 0, 0, 0, 0, '', 3814);
INSERT INTO phenotype_parameter VALUES (657, 'IMPC_VIA_022_001', 6, '% male homozygous', '', 1, 1, '%', 'FLOAT', 'simpleParameter', 'IMPC_VIA_009_001 IMPC_VIA_010_001 /', 0, 0, 0, 1, 0, 0, 0, 0, 0, '', 3815);
INSERT INTO phenotype_parameter VALUES (658, 'IMPC_VIA_023_001', 6, '% female WT', '', 1, 1, '%', 'FLOAT', 'simpleParameter', 'IMPC_VIA_011_001 IMPC_VIA_014_001 /', 0, 0, 0, 1, 0, 0, 0, 0, 0, '', 3816);
INSERT INTO phenotype_parameter VALUES (659, 'IMPC_VIA_024_001', 6, '% female heterozygous', '', 1, 1, '%', 'FLOAT', 'simpleParameter', 'IMPC_VIA_012_001 IMPC_VIA_014_001 /', 0, 0, 0, 1, 0, 0, 0, 0, 0, '', 3817);
INSERT INTO phenotype_parameter VALUES (660, 'IMPC_VIA_025_001', 6, '% female homozygous', '', 1, 1, '%', 'FLOAT', 'simpleParameter', 'IMPC_VIA_013_001 IMPC_VIA_014_001 /', 0, 0, 0, 1, 0, 0, 0, 0, 0, '', 3818);
INSERT INTO phenotype_parameter VALUES (661, 'IMPC_VIA_026_001', 6, 'Female age earliest start', '', 1, 1, 'Weeks', 'INT', 'procedureMetadata', null, 0, 1, 0, 0, 0, 0, 0, 0, 0, '', 3819);
INSERT INTO phenotype_parameter VALUES (662, 'IMPC_VIA_027_001', 6, 'Female age oldest end', '', 1, 1, 'Weeks', 'INT', 'procedureMetadata', null, 0, 1, 0, 0, 0, 0, 0, 0, 0, '', 3820);
INSERT INTO phenotype_parameter VALUES (663, 'IMPC_VIA_028_001', 6, 'Time of dark cycle start', '', 1, 1, ' ', 'TIME', 'procedureMetadata', null, 1, 1, 0, 0, 0, 0, 0, 0, 0, '', 3821);
INSERT INTO phenotype_parameter VALUES (664, 'IMPC_VIA_029_001', 6, 'Time of dark cycle end', '', 1, 0, ' ', 'TIME', 'procedureMetadata', null, 1, 1, 0, 0, 0, 0, 0, 0, 0, '', 3822);
INSERT INTO phenotype_parameter VALUES (665, 'IMPC_VIA_030_001', 6, 'Age of pups at genotype', '', 1, 1, 'Weeks', 'INT', 'procedureMetadata', null, 1, 1, 0, 0, 0, 0, 0, 0, 0, '', 3823);
INSERT INTO phenotype_parameter VALUES (666, 'IMPC_VIA_031_001', 6, 'Breeding Strategy', '', 1, 0, ' ', 'TEXT', 'procedureMetadata', null, 1, 1, 0, 0, 0, 0, 1, 0, 0, '', 3824);
INSERT INTO phenotype_parameter VALUES (651, 'IMPC_VIA_032_001', 6, 'P-value for outcome call', '', 1, 2, ' ', 'FLOAT', 'simpleParameter', 'IMPC_VIA_006_001 IMPC_VIA_003_001 IMPC_VIA_031_001 get_binomial_distribution_p_value', 0, 0, 0, 1, 0, 0, 0, 0, 0, '', 3826);
INSERT INTO phenotype_parameter VALUES (636, 'IMPC_VIA_033_001', 6, 'Additional Subviable Outcome', '', 1, 1, ' ', 'TEXT', 'simpleParameter', null, 0, 0, 0, 0, 0, 0, 1, 0, 0, '', 4860);