-- database: cda_test

INSERT INTO `phenotype_pipeline` VALUES
  (1,'ESLIM_001',6,'EUMODIC Pipeline 1','EUMODIC Pipeline 1',1,0,1,NULL);

INSERT INTO `phenotype_pipeline` VALUES
  (2,'ESLIM_002',6,'EUMODIC Pipeline 2','EUMODIC Pipeline 2',2,0,1,NULL);

INSERT INTO `phenotype_procedure` VALUES (143,11,'ESLIM_008_001',6,'Modified SHIRPA','Use modified SHIRPA for a simple first-line phenotyping screen.',1,0,0,'experiment','Adult','Week 9');
INSERT INTO `phenotype_procedure` VALUES (145,13,'ESLIM_010_001',6,'Rotarod','The rotarod test is used to assess motor coordination and balance in rodents. Mice have to keep their balance on a rotating rod. It is measured the time (latency) ittakes the mouse to fall off the rod rotating at different speeds or under continuous acceleration (e.g. from 4 to 40rpm).',1,0,0,'experiment','Adult','Week 10');
INSERT INTO `phenotype_procedure` VALUES (146,14,'ESLIM_011_001',6,'Acoustic Startle&PPI','An acoustic startle model of sensorimotor gating, in which a weak acoustic stimulus (the pre-pulse) is used to decrease the reflex response (startle) producedby a second, more intense, stimulus (the pulse) in mice. Pre-pulse inhibition (PPI) provides an operational measure of sensorimotor gating which reflects the abilityof an animal to inhibit sensory information properly. Several clinical studies have shown that schizophrenic patients have a reduced PPI. The lack of sufficient sensory gating mechanism is thought to lead to an overflow of the sensory stimulation and disintegration of the cognitive functions. The startle reflex paradigm is therefore largely used to assess the effects of putative anti-psychotics and to explore genetic and neurobiological mechanisms underlying behaviours of relevance to psychosis (Geyer, 1999; Ouagazzal et al., 2001).',1,0,0,'experiment','Adult','Week 11');
INSERT INTO `phenotype_procedure` VALUES (150,18,'ESLIM_015_001',6,'Clinical Chemistry','For the determination of biochemical parameters in plasma including enzymatic activities, specific substrates and electrolytes using an Olympus AU400 analyser (Olympus Diagnostics).',1,0,0,'experiment','Adult','Week 13');

INSERT INTO `phenotype_parameter` VALUES (4591,'ESLIM_022_001_702',6,'Body Weight Curve Pipeline Two','Body_Weight_Curve_Pipeline_Two',1,0,'g','FLOAT','seriesParameter','plot_parameters_as_time_series(ESLIM_009_001_003, ESLIM_010_001_003, ESLIM_011_001_011, ESLIM_012_001_005, ESLIM_013_001_018, ESLIM_022_001_001)',0,0,0,1,0,1,0,0,0,0,'',316);
INSERT INTO `phenotype_parameter` VALUES (4598,'ESLIM_022_001_710',6,'Rotarod Body Weight','Rotarod_Body_Weight',1,0,'g','','simpleParameter','ESLIM_010_001_003',0,0,0,1,1,0,0,0,0,0,'',324);
INSERT INTO `phenotype_parameter` VALUES (4599,'ESLIM_022_001_711',6,'Acoustic Startle & PPI Body Weight','Acoustic_Startle_and_PPI_Body_Weight',1,0,'g','','simpleParameter','ESLIM_011_001_011',0,0,0,1,1,0,0,0,0,0,'',325);
INSERT INTO `phenotype_parameter` VALUES (4758,'ESLIM_008_001_001',6,'Body position','Body_Position',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',352);
INSERT INTO `phenotype_parameter` VALUES (4759,'ESLIM_008_001_002',6,'Tremor ','Tremor',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',353);
INSERT INTO `phenotype_parameter` VALUES (4760,'ESLIM_008_001_003',6,'Defecation','Defecation',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',354);
INSERT INTO `phenotype_parameter` VALUES (4761,'ESLIM_008_001_004',6,'Urination','Urination',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',355);
INSERT INTO `phenotype_parameter` VALUES (4762,'ESLIM_008_001_005',6,'Palpebral closure','Palpebral_Closure',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',356);
INSERT INTO `phenotype_parameter` VALUES (4763,'ESLIM_008_001_006',6,'Lacrimation','Lacrimation',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',357);
INSERT INTO `phenotype_parameter` VALUES (4764,'ESLIM_008_001_007',6,'Transfer arousal','Transfer_Arousal',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',358);
INSERT INTO `phenotype_parameter` VALUES (4765,'ESLIM_008_001_008',6,'Locomotor activity','Locomotor_activity',1,0,'Squares crossed','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',359);
INSERT INTO `phenotype_parameter` VALUES (4766,'ESLIM_008_001_009',6,'Gait','Gait',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',360);
INSERT INTO `phenotype_parameter` VALUES (4767,'ESLIM_008_001_010',6,'Comment on gait','Comment_on_Gait',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',361);
INSERT INTO `phenotype_parameter` VALUES (4768,'ESLIM_008_001_011',6,'Pelvic elevation','Pelvic_elevation',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',362);
INSERT INTO `phenotype_parameter` VALUES (4769,'ESLIM_008_001_012',6,'Tail elevation','Tail_elevation',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',363);
INSERT INTO `phenotype_parameter` VALUES (4770,'ESLIM_008_001_013',6,'Startle response','Startle_Response',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',364);
INSERT INTO `phenotype_parameter` VALUES (4771,'ESLIM_008_001_014',6,'Touch escape ','Touch_Escape',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',365);
INSERT INTO `phenotype_parameter` VALUES (4772,'ESLIM_008_001_015',6,'Positional passivity','Positional_Passivity',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',366);
INSERT INTO `phenotype_parameter` VALUES (4773,'ESLIM_008_001_016',6,'Trunk curl','Trunk_Curl',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',367);
INSERT INTO `phenotype_parameter` VALUES (4774,'ESLIM_008_001_017',6,'Limb grasping ','Limb_Grasping',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',368);
INSERT INTO `phenotype_parameter` VALUES (4775,'ESLIM_008_001_018',6,'Pinna reflex','Pinna_Reflex',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',369);
INSERT INTO `phenotype_parameter` VALUES (4776,'ESLIM_008_001_019',6,'Corneal reflex','Corneal_Reflex',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',370);
INSERT INTO `phenotype_parameter` VALUES (4777,'ESLIM_008_001_020',6,'Contact righting reflex','Contact_Righting_Reflex',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',371);
INSERT INTO `phenotype_parameter` VALUES (4778,'ESLIM_008_001_021',6,'Evidence of biting','Evidence_of_Biting',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',372);
INSERT INTO `phenotype_parameter` VALUES (4779,'ESLIM_008_001_022',6,'Vocalisation','Vocalization',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',373);
INSERT INTO `phenotype_parameter` VALUES (4780,'ESLIM_008_001_023',6,'General comments about the mouse','General_comments_about_the_mouse',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',374);
INSERT INTO `phenotype_parameter` VALUES (4781,'ESLIM_008_001_801',6,'Number of animals per cage','Number_of_animals_per_cage',1,0,' ','INT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',375);
INSERT INTO `phenotype_parameter` VALUES (4782,'ESLIM_008_001_802',6,'Location of test','Location_of_test',1,0,' ','','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,0,'',376);
INSERT INTO `phenotype_parameter` VALUES (4783,'ESLIM_008_001_803',6,'Number of days since animals were cleaned','Number_of_days_since_animals_were_cleaned',1,0,'number','FLOAT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',377);
INSERT INTO `phenotype_parameter` VALUES (4796,'ESLIM_010_001_001',6,'Latency to fall','Latency_to_fall',1,0,'s','FLOAT','seriesParameter',NULL,1,0,0,0,1,1,0,0,0,0,'',390);
INSERT INTO `phenotype_parameter` VALUES (4797,'ESLIM_010_001_002',6,'Passive rotation','Passive_rotation',1,0,' ','','seriesParameter',NULL,1,0,0,0,1,1,1,0,0,0,'',391);
INSERT INTO `phenotype_parameter` VALUES (4798,'ESLIM_010_001_003',6,'Body weight ','Body_weight',1,0,'g','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'',392);
INSERT INTO `phenotype_parameter` VALUES (4799,'ESLIM_010_001_004',6,'General comments about the mouse','General_comments_about_the_mouse',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',393);
INSERT INTO `phenotype_parameter` VALUES (4800,'ESLIM_010_001_801',6,'Equipment name','Equipment_name',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',395);
INSERT INTO `phenotype_parameter` VALUES (4801,'ESLIM_010_001_802',6,'Equipment manufacturer','Equipment_manufacturer',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,1,'',396);
INSERT INTO `phenotype_parameter` VALUES (4802,'ESLIM_010_001_803',6,'Equipment model','Equipment_model',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,1,'',397);
INSERT INTO `phenotype_parameter` VALUES (4803,'ESLIM_010_001_804',6,'Surface of the rod','Surface_of_the_rod',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',398);
INSERT INTO `phenotype_parameter` VALUES (4804,'ESLIM_010_001_805',6,'Diameter of the rod','Diameter_of_the_rod',1,0,'cm','INT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,1,'',399);
INSERT INTO `phenotype_parameter` VALUES (4805,'ESLIM_010_001_806',6,'Acceleration mode','Acceleration_mode',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',400);
INSERT INTO `phenotype_parameter` VALUES (4806,'ESLIM_010_001_807',6,'Number of mice on the rod per run','Number_of_mice_on_the_rod_per_run',1,0,' ','INT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',401);
INSERT INTO `phenotype_parameter` VALUES (4807,'ESLIM_010_001_808',6,'First inter-trial interval','First_inter_trial_interval',1,0,'min','','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',402);
INSERT INTO `phenotype_parameter` VALUES (4808,'ESLIM_010_001_809',6,'Second inter-trial interval','Second_inter_trial_interval',1,0,'min','','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',403);
INSERT INTO `phenotype_parameter` VALUES (4809,'ESLIM_010_001_701',6,'Latency to fall mean','Latency_to_fall_mean',1,0,'s','FLOAT','simpleParameter','sum_of_increments(ESLIM_010_001_001)/number_of_increments(ESLIM_010_001_001)',0,0,0,1,0,0,0,0,0,0,'',394);
INSERT INTO `phenotype_parameter` VALUES (4810,'ESLIM_011_001_001',6,'BN startle magnitude ','BN_startle_magnitude ',1,0,' ','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',404);
INSERT INTO `phenotype_parameter` VALUES (4811,'ESLIM_011_001_002',6,'PP1 startle magnitude','PP1_startle_magnitude',1,0,' ','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',405);
INSERT INTO `phenotype_parameter` VALUES (4812,'ESLIM_011_001_003',6,'PP2 startle magnitude','PP2_startle_magnitude',1,0,' ','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',406);
INSERT INTO `phenotype_parameter` VALUES (4813,'ESLIM_011_001_004',6,'PP3 startle magnitude','PP3_startle_magnitude',1,0,' ','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',407);
INSERT INTO `phenotype_parameter` VALUES (4814,'ESLIM_011_001_005',6,'PP4 startle magnitude','PP4_startle_magnitude',1,0,' ','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',408);
INSERT INTO `phenotype_parameter` VALUES (4815,'ESLIM_011_001_006',6,'110dB startle magnitude','110dB_startle_magnitude',1,0,' ','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',409);
INSERT INTO `phenotype_parameter` VALUES (4816,'ESLIM_011_001_007',6,'PP1 + pulse startle magnitude','PP1__pulse_startle_magnitude',1,0,' ','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',410);
INSERT INTO `phenotype_parameter` VALUES (4817,'ESLIM_011_001_008',6,'PP2 + pulse startle magnitude','PP2__pulse_startle_magnitude',1,0,' ','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',411);
INSERT INTO `phenotype_parameter` VALUES (4818,'ESLIM_011_001_009',6,'PP3 + pulse startle magnitude','PP3__pulse_startle_magnitude',1,0,' ','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',412);
INSERT INTO `phenotype_parameter` VALUES (4819,'ESLIM_011_001_010',6,'PP4 + pulse startle magnitude','PP4__pulse_startle_magnitude',1,0,' ','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',413);
INSERT INTO `phenotype_parameter` VALUES (4820,'ESLIM_011_001_011',6,'Body weight','Body_weight',1,0,' ','FLOAT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'',414);
INSERT INTO `phenotype_parameter` VALUES (4821,'ESLIM_011_001_801',6,'Equipment name','Equipment_name',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',420);
INSERT INTO `phenotype_parameter` VALUES (4822,'ESLIM_011_001_802',6,'Equipment manufacturer','Equipment_manufacturer',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,1,'',421);
INSERT INTO `phenotype_parameter` VALUES (4823,'ESLIM_011_001_803',6,'Equipment model','Equipment_model',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,1,'',422);
INSERT INTO `phenotype_parameter` VALUES (4824,'ESLIM_011_001_804',6,'Startle pulse (110dB/40ms white noise)','startle_pulse_110dB40ms_white_noise',1,0,'dB','FLOAT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',423);
INSERT INTO `phenotype_parameter` VALUES (4825,'ESLIM_011_001_805',6,'Background noise','Background_noise',1,0,'dB','FLOAT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',424);
INSERT INTO `phenotype_parameter` VALUES (4826,'ESLIM_011_001_806',6,'PP1 pulse (P/10 ms white noise)','PP1_pulse_P10_ms_white_noise',1,0,'dB','FLOAT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',425);
INSERT INTO `phenotype_parameter` VALUES (4827,'ESLIM_011_001_807',6,'PP2 pulse (P/10 ms white noise)','PP2_pulse_P10_ms_white_noise',1,0,'dB','FLOAT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',426);
INSERT INTO `phenotype_parameter` VALUES (4828,'ESLIM_011_001_808',6,'PP3 pulse (P/10 ms white noise)','PP3_pulse_P10_ms_white_noise',1,0,'dB','FLOAT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',427);
INSERT INTO `phenotype_parameter` VALUES (4829,'ESLIM_011_001_809',6,'PP4 pulse (P/10 ms white noise)','PP4_pulse_P10_ms_white_noise',1,0,'dB','FLOAT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',428);
INSERT INTO `phenotype_parameter` VALUES (4830,'ESLIM_011_001_810',6,'Inter prepulse-pulse interval','Inter_prepulsepulse_interval',1,0,'ms','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,1,'',429);
INSERT INTO `phenotype_parameter` VALUES (4831,'ESLIM_011_001_701',6,'Prepulse inhibition - PP1','Prepulse_inhibition_-_PP1',1,0,'%','FLOAT','simpleParameter','100x((ESLIM_011_001_006-ESLIM_011_001_007)/ESLIM_011_001_006)',0,0,0,1,1,0,0,0,0,0,'',415);
INSERT INTO `phenotype_parameter` VALUES (4832,'ESLIM_011_001_702',6,'Prepulse inhibition - PP2','Prepulse_inhibition_-_PP2',1,0,'%','FLOAT','simpleParameter','100x((ESLIM_011_001_006-ESLIM_011_001_008)/ESLIM_011_001_006)',0,0,0,1,1,0,0,0,0,0,'',416);
INSERT INTO `phenotype_parameter` VALUES (4833,'ESLIM_011_001_703',6,'Prepulse inhibition - PP3','Prepulse_inhibition_-_PP3',1,0,'%','FLOAT','simpleParameter','100x((ESLIM_011_001_006-ESLIM_011_001_009)/ESLIM_011_001_006)',0,0,0,1,1,0,0,0,0,0,'',417);
INSERT INTO `phenotype_parameter` VALUES (4834,'ESLIM_011_001_704',6,'Prepulse inhibition - PP4','Prepulse_inhibition_-_PP4',1,0,'%','FLOAT','simpleParameter','100x((ESLIM_011_001_006-ESLIM_011_001_010)/ESLIM_011_001_006)',0,0,0,1,1,0,0,0,0,0,'',418);
INSERT INTO `phenotype_parameter` VALUES (4835,'ESLIM_011_001_705',6,'Global prepulse inhibition ','Global_prepulse_inhibition',1,0,'ratio','FLOAT','simpleParameter','100x((ESLIM_011_001_006-((ESLIM_011_001_007+ESLIM_011_001_008+ESLIM_011_001_009+ESLIM_011_001_010)/4))/ESLIM_011_001_006)',0,0,0,1,1,0,0,0,0,0,'',419);
INSERT INTO `phenotype_parameter` VALUES (4885,'ESLIM_015_001_001',6,'Glucose','Glucose',1,0,'mmol/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',479);
INSERT INTO `phenotype_parameter` VALUES (4886,'ESLIM_015_001_002',6,'Urea','Urea',1,0,'mmol/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',480);
INSERT INTO `phenotype_parameter` VALUES (4887,'ESLIM_015_001_003',6,'Creatinine ','Creatinine',1,0,'umol/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',481);
INSERT INTO `phenotype_parameter` VALUES (4888,'ESLIM_015_001_004',6,'Sodium','Sodium',1,0,'mmol/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',482);
INSERT INTO `phenotype_parameter` VALUES (4889,'ESLIM_015_001_005',6,'Potassium','Potassium',1,0,'mmol/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',483);
INSERT INTO `phenotype_parameter` VALUES (4890,'ESLIM_015_001_006',6,'Chloride','Chloride',1,0,'mmol/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',484);
INSERT INTO `phenotype_parameter` VALUES (4891,'ESLIM_015_001_007',6,'Total protein ','Total_protein',1,0,'g/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',485);
INSERT INTO `phenotype_parameter` VALUES (4892,'ESLIM_015_001_008',6,'Albumin','Albumin',1,0,'g/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',486);
INSERT INTO `phenotype_parameter` VALUES (4893,'ESLIM_015_001_009',6,'Calcium  ','Calcium',1,0,'mmol/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',487);
INSERT INTO `phenotype_parameter` VALUES (4894,'ESLIM_015_001_010',6,'Phosphorus    ','Phosphorus',1,0,'mmol/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',488);
INSERT INTO `phenotype_parameter` VALUES (4895,'ESLIM_015_001_011',6,'Iron    ','Iron',1,0,'umol/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',489);
INSERT INTO `phenotype_parameter` VALUES (4896,'ESLIM_015_001_012',6,'Lactate dehydrogenase','Lactate_dehydrogenase',1,0,'U/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',490);
INSERT INTO `phenotype_parameter` VALUES (4897,'ESLIM_015_001_013',6,'Aspartate aminotransferase','Aspartate_aminotransferase',1,0,'U/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',491);
INSERT INTO `phenotype_parameter` VALUES (4898,'ESLIM_015_001_014',6,'Alanine aminotransferase','Alanine_aminotransferase',1,0,'U/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',492);
INSERT INTO `phenotype_parameter` VALUES (4899,'ESLIM_015_001_015',6,'Alkaline phosphatase    ','Alkaline_phosphatase',1,0,'U/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',493);
INSERT INTO `phenotype_parameter` VALUES (4900,'ESLIM_015_001_016',6,'Alpha-amylase    ','Alphaamylase',1,0,'U/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',494);
INSERT INTO `phenotype_parameter` VALUES (4901,'ESLIM_015_001_017',6,'Total cholesterol   ','Total_cholesterol',1,0,'mmol/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',495);
INSERT INTO `phenotype_parameter` VALUES (4902,'ESLIM_015_001_018',6,'Triglyceride    ','Triglyceride',1,0,'mmol/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',496);
INSERT INTO `phenotype_parameter` VALUES (4903,'ESLIM_015_001_019',6,'Free fatty acid    ','Free_fatty_acid',1,0,'mmol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',497);
INSERT INTO `phenotype_parameter` VALUES (4904,'ESLIM_015_001_020',6,'Creatine kinase    ','Creatine_kinase',1,0,'U/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',498);
INSERT INTO `phenotype_parameter` VALUES (4905,'ESLIM_015_001_021',6,'Uric acid','Uric_acid',1,0,'umol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',499);
INSERT INTO `phenotype_parameter` VALUES (4906,'ESLIM_015_001_022',6,'Total bilirubin ','Total_bilirubin',1,0,'umol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',500);
INSERT INTO `phenotype_parameter` VALUES (4907,'ESLIM_015_001_023',6,'HDL-cholesterol    ','HDLcholesterol',1,0,'mmol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',501);
INSERT INTO `phenotype_parameter` VALUES (4908,'ESLIM_015_001_024',6,'LDL-cholesterol   ','LDLcholesterol',1,0,'mmol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',502);
INSERT INTO `phenotype_parameter` VALUES (4909,'ESLIM_015_001_025',6,'Ferretin   ','Ferretin',1,0,'mg/ml','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',503);
INSERT INTO `phenotype_parameter` VALUES (4910,'ESLIM_015_001_026',6,'Transferrin   ','Transferrin',1,0,'mg/dl','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',504);
INSERT INTO `phenotype_parameter` VALUES (4911,'ESLIM_015_001_027',6,'C-reactive protein','CReactive_protein',1,0,'mg/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',505);
INSERT INTO `phenotype_parameter` VALUES (4912,'ESLIM_015_001_801',6,'Equipment name','Equipment_name',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',506);
INSERT INTO `phenotype_parameter` VALUES (4913,'ESLIM_015_001_802',6,'Equipment manufacturer','Equipment_manufacturer',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,1,'',507);
INSERT INTO `phenotype_parameter` VALUES (4914,'ESLIM_015_001_803',6,'Equipment model','Equipment_model',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,1,'',508);
INSERT INTO `phenotype_parameter` VALUES (4915,'ESLIM_015_001_804',6,'Method of blood collection','Method_of_blood_collection',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,1,'',509);
INSERT INTO `phenotype_parameter` VALUES (4916,'ESLIM_015_001_805',6,'EMPReSSID for blood collection SOP','EMPReSSID_for_blood_collection_SOP',1,0,' ','','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',510);
INSERT INTO `phenotype_parameter` VALUES (4917,'ESLIM_015_001_806',6,'Date/time of blood collection','DateTime_of_blood_collection',1,0,' ','','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',511);
INSERT INTO `phenotype_parameter` VALUES (4918,'ESLIM_015_001_807',6,'Fasting prior to experiment','Fasting_prior_to_experiment',1,0,' ','','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,1,'',512);
INSERT INTO `phenotype_parameter` VALUES (4919,'ESLIM_015_001_808',6,'Approximate period of fasting','Approximate_period_of_fasting',1,0,'Hours','','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',513);
INSERT INTO `phenotype_parameter` VALUES (4920,'ESLIM_015_001_809',6,'Moved from cage for fasting','Moved_from_cage_for_fasting',1,0,' ','','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,0,'',514);
INSERT INTO `phenotype_parameter` VALUES (4921,'ESLIM_015_001_810',6,'Plasma dilution','Plasma_dilution',1,0,' ','','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,1,'',515);
INSERT INTO `phenotype_parameter` VALUES (4922,'ESLIM_015_001_811',6,'Sample status','Sample_status',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,1,'',516);
INSERT INTO `phenotype_parameter` VALUES (4923,'ESLIM_015_001_812',6,'Anaesthesia used for blood collection','Anaesthesia_used_for_blood_collection',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,1,'',517);
INSERT INTO `phenotype_parameter` VALUES (4924,'ESLIM_015_001_813',6,'Date of measurement','Date_of_measurement',1,0,' ','','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',518);
INSERT INTO `phenotype_parameter` VALUES (4925,'ESLIM_015_001_814',6,'Haemolysis status','Haemolysis_Status',1,0,' ','','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,1,'',519);


INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);


INSERT INTO genomic_feature VALUES
  ('MGI:88255', 3, 'Anxa6', 'annexin A6', 1, 2, 2, 2, 11, 54979108, 55033445, -1, 32.13, 'active');

