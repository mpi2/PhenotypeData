-- database: cda_test


INSERT INTO `phenotype_pipeline` VALUES
  (1,'TCP_001',6,'TCP Pipeline','Toronto Centre for Phenogenomics''s (TCP) pipeline for extra parameters and procedures',1,1,9,NULL);


INSERT INTO `phenotype_procedure` VALUES
(55,91,'IMPC_XRY_001',6,'X-ray','Construct and analyse digital X-ray images in immobilised mice using a Faxitron X-Ray system or NTB digital X-ray scanner.',1,2,1,'experiment','Adult','Week 14');


INSERT INTO `phenotype_parameter` VALUES (1793,'IMPC_XRY_001_001',6,'Skull shape','skull_shape',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1860);
INSERT INTO `phenotype_parameter` VALUES (1794,'IMPC_XRY_002_001',6,'Zygomatic bone','zygomatic_bone',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1861);
INSERT INTO `phenotype_parameter` VALUES (1795,'IMPC_XRY_003_001',6,'Maxilla/Pre-maxilla','maxilla',1,1,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1862);
INSERT INTO `phenotype_parameter` VALUES (1796,'IMPC_XRY_004_001',6,'Mandibles','mandibles',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1863);
INSERT INTO `phenotype_parameter` VALUES (1797,'IMPC_XRY_005_001',6,'Teeth','teeth',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1864);
INSERT INTO `phenotype_parameter` VALUES (1798,'IMPC_XRY_006_001',6,'Scapulae','scapulae',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1865);
INSERT INTO `phenotype_parameter` VALUES (1799,'IMPC_XRY_007_001',6,'Clavicle','clavicle',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1866);
INSERT INTO `phenotype_parameter` VALUES (1800,'IMPC_XRY_008_001',6,'Number of ribs right','number_of_ribs_right',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',1867);
INSERT INTO `phenotype_parameter` VALUES (1801,'IMPC_XRY_009_001',6,'Number of ribs left','number_of_ribs_left',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',1868);
INSERT INTO `phenotype_parameter` VALUES (1802,'IMPC_XRY_010_001',6,'Shape of ribs','shape_of_ribs',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1869);
INSERT INTO `phenotype_parameter` VALUES (1803,'IMPC_XRY_011_001',6,'Fusion of ribs','fusion_of_ribs',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1870);
INSERT INTO `phenotype_parameter` VALUES (1804,'IMPC_XRY_012_001',6,'Pelvis','pelvis',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1871);
INSERT INTO `phenotype_parameter` VALUES (1805,'IMPC_XRY_013_001',6,'Number of cervical vertebrae','number_of_cervical_vertebrae',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',1872);
INSERT INTO `phenotype_parameter` VALUES (1806,'IMPC_XRY_014_001',6,'Number of thoracic vertebrae','number_of_thoracic_vertebrae',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',1873);
INSERT INTO `phenotype_parameter` VALUES (1807,'IMPC_XRY_015_001',6,'Number of lumbar vertebrae','number_of_lumbar_vertebrae',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',1874);
INSERT INTO `phenotype_parameter` VALUES (1808,'IMPC_XRY_016_001',6,'Number of pelvic vertebrae','number_of_pelvic_vertebrae',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',1875);
INSERT INTO `phenotype_parameter` VALUES (1809,'IMPC_XRY_017_001',6,'Number of caudal vertebrae','number_of_caudal_vertebrae',1,1,' ','INT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',1876);
INSERT INTO `phenotype_parameter` VALUES (1810,'IMPC_XRY_018_001',6,'Shape of vertebrae','shape_of_vertebrae',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1877);
INSERT INTO `phenotype_parameter` VALUES (1811,'IMPC_XRY_019_001',6,'Fusion of vertebrae','fusion_of_vertebrae',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1878);
INSERT INTO `phenotype_parameter` VALUES (1812,'IMPC_XRY_020_001',6,'Processes on vertebrae','processes_on_vertebrae',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1879);
INSERT INTO `phenotype_parameter` VALUES (1813,'IMPC_XRY_021_001',6,'Humerus','humerus',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1880);
INSERT INTO `phenotype_parameter` VALUES (1814,'IMPC_XRY_022_001',6,'Radius','radius',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1881);
INSERT INTO `phenotype_parameter` VALUES (1815,'IMPC_XRY_023_001',6,'Ulna','ulna',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1882);
INSERT INTO `phenotype_parameter` VALUES (1816,'IMPC_XRY_024_001',6,'Femur','femur',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1883);
INSERT INTO `phenotype_parameter` VALUES (1817,'IMPC_XRY_025_001',6,'Tibia','tibia',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1884);
INSERT INTO `phenotype_parameter` VALUES (1818,'IMPC_XRY_026_001',6,'Fibula','fibula',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1885);
INSERT INTO `phenotype_parameter` VALUES (1819,'IMPC_XRY_027_001',6,'Joints','joints',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1886);
INSERT INTO `phenotype_parameter` VALUES (1820,'IMPC_XRY_028_001',6,'Number of digits','number_of_digits',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,0,'',1887);
INSERT INTO `phenotype_parameter` VALUES (1821,'IMPC_XRY_029_001',6,'Syndactylism','syndactylism',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1888);
INSERT INTO `phenotype_parameter` VALUES (1822,'IMPC_XRY_030_001',6,'Brachydactyly','completeness_of_digits',1,1,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1889);
INSERT INTO `phenotype_parameter` VALUES (1823,'IMPC_XRY_031_001',6,'Digit integrity','digit_integrity',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,0,'',1890);
INSERT INTO `phenotype_parameter` VALUES (1824,'IMPC_XRY_032_001',6,'Comment on XRay image','comment_on_xray_image',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',1891);
INSERT INTO `phenotype_parameter` VALUES (1825,'IMPC_XRY_033_001',6,'Tibia length','tibia_length',1,1,'mm','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,0,'',1892);
INSERT INTO `phenotype_parameter` VALUES (1826,'IMPC_XRY_034_001',6,'XRay Images Dorso Ventral','xray_images',1,3,' ','IMAGE','seriesMediaParameter',NULL,1,0,0,0,0,1,0,0,0,0,'',1893);
INSERT INTO `phenotype_parameter` VALUES (1827,'IMPC_XRY_048_001',6,'XRay Images Lateral Orientation','',1,2,' ','IMAGE','seriesMediaParameter',NULL,1,0,0,0,0,1,0,0,0,0,'',2172);
INSERT INTO `phenotype_parameter` VALUES (1828,'IMPC_XRY_035_001',6,'Alive','alive',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,0,'',1894);
INSERT INTO `phenotype_parameter` VALUES (1829,'IMPC_XRY_036_001',6,'Xray equipment ID','xray_equipment_name',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',1895);
INSERT INTO `phenotype_parameter` VALUES (1830,'IMPC_XRY_037_001',6,'Xray equipment manufacturer','xray_equipment_manufacturer',1,4,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,1,'',1896);
INSERT INTO `phenotype_parameter` VALUES (1831,'IMPC_XRY_038_001',6,'Xray equipment model','xray_equipment_model',1,4,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,1,'',1897);
INSERT INTO `phenotype_parameter` VALUES (1832,'IMPC_XRY_039_001',6,'Settings time of exposure','settings_time_of_exposure',1,1,'s','FLOAT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',1898);
INSERT INTO `phenotype_parameter` VALUES (1833,'IMPC_XRY_040_001',6,'Settings level','settings_level',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',1899);
INSERT INTO `phenotype_parameter` VALUES (1834,'IMPC_XRY_041_001',6,'Experimenter ID (scan)','experimenter_scan_',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',1900);
INSERT INTO `phenotype_parameter` VALUES (1835,'IMPC_XRY_042_001',6,'Experimenter ID (analysis)','experimenter_analysis_',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',1901);
INSERT INTO `phenotype_parameter` VALUES (1836,'IMPC_XRY_043_001',6,'Voltage settings','voltage_settings',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,0,'',1902);
INSERT INTO `phenotype_parameter` VALUES (1837,'IMPC_XRY_044_001',6,'Scanner equipment ID','scanner_equipment_name',1,1,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',1903);
INSERT INTO `phenotype_parameter` VALUES (1838,'IMPC_XRY_045_001',6,'Scanner equipment manufacturer','scanner_equipment_manufacturer',1,1,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',1904);
INSERT INTO `phenotype_parameter` VALUES (1839,'IMPC_XRY_046_001',6,'Scanner equipment model','scanner_equipment_model',1,1,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',1905);
INSERT INTO `phenotype_parameter` VALUES (1840,'IMPC_XRY_047_001',6,'Date Xray equipment last calibrated','',1,3,' ','DATE','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',2165);
INSERT INTO `phenotype_parameter` VALUES (1841,'IMPC_XRY_049_001',6,'XRay Images Forepaw','',1,1,' ','IMAGE','seriesMediaParameter',NULL,0,0,0,0,0,1,0,0,0,0,'',2173);
INSERT INTO `phenotype_parameter` VALUES (1842,'IMPC_XRY_050_001',6,'XRay Images Skull Lateral Orientation','',1,2,' ','IMAGE','seriesMediaParameter',NULL,0,0,0,0,0,1,0,0,0,0,'',2174);
INSERT INTO `phenotype_parameter` VALUES (1843,'IMPC_XRY_051_001',6,'XRay Images Skull Dorso Ventral Orientation','',1,2,' ','IMAGE','seriesMediaParameter',NULL,0,0,0,0,0,1,0,0,0,0,'',2175);
INSERT INTO `phenotype_parameter` VALUES (1844,'IMPC_XRY_052_001',6,'XRay Images Hind Leg and Hip','',1,1,' ','IMAGE','seriesMediaParameter',NULL,0,0,0,0,0,1,0,0,0,0,'',2176);
INSERT INTO `phenotype_parameter` VALUES (1845,'IMPC_XRY_053_001',6,'Date Scanner equipment last calibrated','',1,1,' ','DATE','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,0,'',2352);
INSERT INTO `phenotype_parameter` VALUES (1846,'IMPC_XRY_054_001',6,'Free Ontology','',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,0,'',2383);
INSERT INTO `phenotype_parameter` VALUES (1847,'IMPC_XRY_055_001',6,'Shape of spine','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3778);
INSERT INTO `phenotype_parameter` VALUES (1848,'IMPC_XRY_056_001',6,'Scoliosis','',1,2,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3779);
INSERT INTO `phenotype_parameter` VALUES (1849,'IMPC_XRY_057_001',6,'Kyphosis','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3780);
INSERT INTO `phenotype_parameter` VALUES (1850,'IMPC_XRY_058_001',6,'Lordosis','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3781);
INSERT INTO `phenotype_parameter` VALUES (1851,'IMPC_XRY_059_001',6,'Shape of ribcage','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3782);
INSERT INTO `phenotype_parameter` VALUES (1852,'IMPC_XRY_060_001',6,'Transitional vertebrae','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3783);
INSERT INTO `phenotype_parameter` VALUES (1853,'IMPC_XRY_061_001',6,'Fusion of processes','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3784);
INSERT INTO `phenotype_parameter` VALUES (1854,'IMPC_XRY_062_001',6,'Polysyndactylism','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3785);
INSERT INTO `phenotype_parameter` VALUES (1855,'IMPC_XRY_063_001',6,'Cervical processes','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3786);
INSERT INTO `phenotype_parameter` VALUES (1856,'IMPC_XRY_064_001',6,'Thoracic processes','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3787);
INSERT INTO `phenotype_parameter` VALUES (1857,'IMPC_XRY_065_001',6,'Lumbar processes','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3788);
INSERT INTO `phenotype_parameter` VALUES (1858,'IMPC_XRY_066_001',6,'Sacral processes','',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3789);
INSERT INTO `phenotype_parameter` VALUES (1859,'IMPC_XRY_067_001',6,'Caudal processes','',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,0,'',3790);
INSERT INTO `phenotype_parameter` VALUES (1860,'IMPC_XRY_068_001',6,'Missing cranial rib','',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,1,0,0,0,'',5379);


INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);

