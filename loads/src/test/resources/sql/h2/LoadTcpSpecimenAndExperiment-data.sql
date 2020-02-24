-- database: cda_test


INSERT INTO `phenotype_pipeline` VALUES
  (1,'TCP_001',6,'TCP Pipeline','Toronto Centre for Phenogenomics''s (TCP) pipeline for extra parameters and procedures',1,1,9,NULL);


INSERT INTO `phenotype_procedure` VALUES
(55,91,'IMPC_XRY_001',6,'X-ray','Construct and analyse digital X-ray images in immobilised mice using a Faxitron X-Ray system or NTB digital X-ray scanner.',1,2,1,'experiment','Adult','Week 14', 0);
INSERT INTO `phenotype_procedure` VALUES
(65,182,'IMPC_CBC_003',6,'Clinical Chemistry','Clinical chemistry determines biochemical parameters in plasma including enzymatic activity, specific substrates and electrolytes.&nbsp;Ontological description: MP:0001545 &ndash; blood physiology abnormalities.',3,2,1,'experiment','Terminal','Week 16', 0);


INSERT INTO `phenotype_parameter` VALUES (1793,'IMPC_XRY_001_001',6,'Skull shape','skull_shape',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1860);
INSERT INTO `phenotype_parameter` VALUES (1794,'IMPC_XRY_002_001',6,'Zygomatic bone','zygomatic_bone',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1861);
INSERT INTO `phenotype_parameter` VALUES (1795,'IMPC_XRY_003_001',6,'Maxilla/Pre-maxilla','maxilla',1,1,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1862);
INSERT INTO `phenotype_parameter` VALUES (1796,'IMPC_XRY_004_001',6,'Mandibles','mandibles',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1863);
INSERT INTO `phenotype_parameter` VALUES (1797,'IMPC_XRY_005_001',6,'Teeth','teeth',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1864);
INSERT INTO `phenotype_parameter` VALUES (1798,'IMPC_XRY_006_001',6,'Scapulae','scapulae',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1865);
INSERT INTO `phenotype_parameter` VALUES (1799,'IMPC_XRY_007_001',6,'Clavicle','clavicle',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1866);
INSERT INTO `phenotype_parameter` VALUES (1800,'IMPC_XRY_008_001',6,'Number of ribs right','number_of_ribs_right',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1867);
INSERT INTO `phenotype_parameter` VALUES (1801,'IMPC_XRY_009_001',6,'Number of ribs left','number_of_ribs_left',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1868);
INSERT INTO `phenotype_parameter` VALUES (1802,'IMPC_XRY_010_001',6,'Shape of ribs','shape_of_ribs',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1869);
INSERT INTO `phenotype_parameter` VALUES (1803,'IMPC_XRY_011_001',6,'Fusion of ribs','fusion_of_ribs',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1870);
INSERT INTO `phenotype_parameter` VALUES (1804,'IMPC_XRY_012_001',6,'Pelvis','pelvis',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1871);
INSERT INTO `phenotype_parameter` VALUES (1805,'IMPC_XRY_013_001',6,'Number of cervical vertebrae','number_of_cervical_vertebrae',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1872);
INSERT INTO `phenotype_parameter` VALUES (1806,'IMPC_XRY_014_001',6,'Number of thoracic vertebrae','number_of_thoracic_vertebrae',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1873);
INSERT INTO `phenotype_parameter` VALUES (1807,'IMPC_XRY_015_001',6,'Number of lumbar vertebrae','number_of_lumbar_vertebrae',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1874);
INSERT INTO `phenotype_parameter` VALUES (1808,'IMPC_XRY_016_001',6,'Number of pelvic vertebrae','number_of_pelvic_vertebrae',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1875);
INSERT INTO `phenotype_parameter` VALUES (1809,'IMPC_XRY_017_001',6,'Number of caudal vertebrae','number_of_caudal_vertebrae',1,1,' ','INT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1876);
INSERT INTO `phenotype_parameter` VALUES (1810,'IMPC_XRY_018_001',6,'Shape of vertebrae','shape_of_vertebrae',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1877);
INSERT INTO `phenotype_parameter` VALUES (1811,'IMPC_XRY_019_001',6,'Fusion of vertebrae','fusion_of_vertebrae',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1878);
INSERT INTO `phenotype_parameter` VALUES (1812,'IMPC_XRY_020_001',6,'Processes on vertebrae','processes_on_vertebrae',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1879);
INSERT INTO `phenotype_parameter` VALUES (1813,'IMPC_XRY_021_001',6,'Humerus','humerus',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1880);
INSERT INTO `phenotype_parameter` VALUES (1814,'IMPC_XRY_022_001',6,'Radius','radius',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1881);
INSERT INTO `phenotype_parameter` VALUES (1815,'IMPC_XRY_023_001',6,'Ulna','ulna',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1882);
INSERT INTO `phenotype_parameter` VALUES (1816,'IMPC_XRY_024_001',6,'Femur','femur',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1883);
INSERT INTO `phenotype_parameter` VALUES (1817,'IMPC_XRY_025_001',6,'Tibia','tibia',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1884);
INSERT INTO `phenotype_parameter` VALUES (1818,'IMPC_XRY_026_001',6,'Fibula','fibula',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1885);
INSERT INTO `phenotype_parameter` VALUES (1819,'IMPC_XRY_027_001',6,'Joints','joints',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1886);
INSERT INTO `phenotype_parameter` VALUES (1820,'IMPC_XRY_028_001',6,'Number of digits','number_of_digits',1,0,' ','INT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1887);
INSERT INTO `phenotype_parameter` VALUES (1821,'IMPC_XRY_029_001',6,'Syndactylism','syndactylism',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1888);
INSERT INTO `phenotype_parameter` VALUES (1822,'IMPC_XRY_030_001',6,'Brachydactyly','completeness_of_digits',1,1,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1889);
INSERT INTO `phenotype_parameter` VALUES (1823,'IMPC_XRY_031_001',6,'Digit integrity','digit_integrity',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,1,0,1,0,0,'',1890);
INSERT INTO `phenotype_parameter` VALUES (1824,'IMPC_XRY_032_001',6,'Comment on XRay image','comment_on_xray_image',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',1891);
INSERT INTO `phenotype_parameter` VALUES (1825,'IMPC_XRY_033_001',6,'Tibia length','tibia_length',1,1,'mm','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1892);
INSERT INTO `phenotype_parameter` VALUES (1826,'IMPC_XRY_034_001',6,'XRay Images Dorso Ventral','xray_images',1,3,' ','IMAGE','seriesMediaParameter',NULL,1,0,0,0,0,1,0,0,0,'',1893);
INSERT INTO `phenotype_parameter` VALUES (1827,'IMPC_XRY_048_001',6,'XRay Images Lateral Orientation','',1,2,' ','IMAGE','seriesMediaParameter',NULL,1,0,0,0,0,1,0,0,0,'',2172);
INSERT INTO `phenotype_parameter` VALUES (1828,'IMPC_XRY_035_001',6,'Alive','alive',1,0,' ','TEXT','simpleParameter',NULL,1,0,0,0,0,0,0,0,0,'',1894);
INSERT INTO `phenotype_parameter` VALUES (1829,'IMPC_XRY_036_001',6,'Xray equipment ID','xray_equipment_name',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',1895);
INSERT INTO `phenotype_parameter` VALUES (1830,'IMPC_XRY_037_001',6,'Xray equipment manufacturer','xray_equipment_manufacturer',1,4,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,'',1896);
INSERT INTO `phenotype_parameter` VALUES (1831,'IMPC_XRY_038_001',6,'Xray equipment model','xray_equipment_model',1,4,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,'',1897);
INSERT INTO `phenotype_parameter` VALUES (1832,'IMPC_XRY_039_001',6,'Settings time of exposure','settings_time_of_exposure',1,1,'s','FLOAT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',1898);
INSERT INTO `phenotype_parameter` VALUES (1833,'IMPC_XRY_040_001',6,'Settings level','settings_level',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',1899);
INSERT INTO `phenotype_parameter` VALUES (1834,'IMPC_XRY_041_001',6,'Experimenter ID (scan)','experimenter_scan_',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',1900);
INSERT INTO `phenotype_parameter` VALUES (1835,'IMPC_XRY_042_001',6,'Experimenter ID (analysis)','experimenter_analysis_',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',1901);
INSERT INTO `phenotype_parameter` VALUES (1836,'IMPC_XRY_043_001',6,'Voltage settings','voltage_settings',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',1902);
INSERT INTO `phenotype_parameter` VALUES (1837,'IMPC_XRY_044_001',6,'Scanner equipment ID','scanner_equipment_name',1,1,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',1903);
INSERT INTO `phenotype_parameter` VALUES (1838,'IMPC_XRY_045_001',6,'Scanner equipment manufacturer','scanner_equipment_manufacturer',1,1,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',1904);
INSERT INTO `phenotype_parameter` VALUES (1839,'IMPC_XRY_046_001',6,'Scanner equipment model','scanner_equipment_model',1,1,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',1905);
INSERT INTO `phenotype_parameter` VALUES (1840,'IMPC_XRY_047_001',6,'Date Xray equipment last calibrated','',1,3,' ','DATE','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',2165);
INSERT INTO `phenotype_parameter` VALUES (1841,'IMPC_XRY_049_001',6,'XRay Images Forepaw','',1,1,' ','IMAGE','seriesMediaParameter',NULL,0,0,0,0,0,1,0,0,0,'',2173);
INSERT INTO `phenotype_parameter` VALUES (1842,'IMPC_XRY_050_001',6,'XRay Images Skull Lateral Orientation','',1,2,' ','IMAGE','seriesMediaParameter',NULL,0,0,0,0,0,1,0,0,0,'',2174);
INSERT INTO `phenotype_parameter` VALUES (1843,'IMPC_XRY_051_001',6,'XRay Images Skull Dorso Ventral Orientation','',1,2,' ','IMAGE','seriesMediaParameter',NULL,0,0,0,0,0,1,0,0,0,'',2175);
INSERT INTO `phenotype_parameter` VALUES (1844,'IMPC_XRY_052_001',6,'XRay Images Hind Leg and Hip','',1,1,' ','IMAGE','seriesMediaParameter',NULL,0,0,0,0,0,1,0,0,0,'',2176);
INSERT INTO `phenotype_parameter` VALUES (1845,'IMPC_XRY_053_001',6,'Date Scanner equipment last calibrated','',1,1,' ','DATE','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',2352);
INSERT INTO `phenotype_parameter` VALUES (1846,'IMPC_XRY_054_001',6,'Free Ontology','',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',2383);
INSERT INTO `phenotype_parameter` VALUES (1847,'IMPC_XRY_055_001',6,'Shape of spine','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3778);
INSERT INTO `phenotype_parameter` VALUES (1848,'IMPC_XRY_056_001',6,'Scoliosis','',1,2,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3779);
INSERT INTO `phenotype_parameter` VALUES (1849,'IMPC_XRY_057_001',6,'Kyphosis','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3780);
INSERT INTO `phenotype_parameter` VALUES (1850,'IMPC_XRY_058_001',6,'Lordosis','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3781);
INSERT INTO `phenotype_parameter` VALUES (1851,'IMPC_XRY_059_001',6,'Shape of ribcage','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3782);
INSERT INTO `phenotype_parameter` VALUES (1852,'IMPC_XRY_060_001',6,'Transitional vertebrae','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3783);
INSERT INTO `phenotype_parameter` VALUES (1853,'IMPC_XRY_061_001',6,'Fusion of processes','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3784);
INSERT INTO `phenotype_parameter` VALUES (1854,'IMPC_XRY_062_001',6,'Polysyndactylism','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3785);
INSERT INTO `phenotype_parameter` VALUES (1855,'IMPC_XRY_063_001',6,'Cervical processes','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3786);
INSERT INTO `phenotype_parameter` VALUES (1856,'IMPC_XRY_064_001',6,'Thoracic processes','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3787);
INSERT INTO `phenotype_parameter` VALUES (1857,'IMPC_XRY_065_001',6,'Lumbar processes','',1,1,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3788);
INSERT INTO `phenotype_parameter` VALUES (1858,'IMPC_XRY_066_001',6,'Sacral processes','',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3789);
INSERT INTO `phenotype_parameter` VALUES (1859,'IMPC_XRY_067_001',6,'Caudal processes','',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,1,0,1,0,0,'',3790);
INSERT INTO `phenotype_parameter` VALUES (1860,'IMPC_XRY_068_001',6,'Missing cranial rib','',1,0,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,1,0,0,'',5379);

INSERT INTO `phenotype_parameter` VALUES (2149,'IMPC_CBC_001_001',6,'Sodium','sodium',1,3,'mmol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1944);
INSERT INTO `phenotype_parameter` VALUES (2150,'IMPC_CBC_002_001',6,'Potassium','potassium',1,3,'mmol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1945);
INSERT INTO `phenotype_parameter` VALUES (2151,'IMPC_CBC_003_001',6,'Chloride','chloride',1,4,'mmol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1946);
INSERT INTO `phenotype_parameter` VALUES (2152,'IMPC_CBC_004_001',6,'Urea (Blood Urea Nitrogen - BUN)','urea',1,5,'mg/dl','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1947);
INSERT INTO `phenotype_parameter` VALUES (2153,'IMPC_CBC_005_001',6,'Creatinine','creatinine_enzymatic_method_preferred_',1,5,'mg/dl','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1948);
INSERT INTO `phenotype_parameter` VALUES (2154,'IMPC_CBC_006_001',6,'Total protein','total_protein',1,2,'g/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1949);
INSERT INTO `phenotype_parameter` VALUES (2155,'IMPC_CBC_007_001',6,'Albumin','albumin',1,2,'g/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1950);
INSERT INTO `phenotype_parameter` VALUES (2156,'IMPC_CBC_008_001',6,'Total bilirubin','total_bilirubin',1,4,'mg/dl','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1951);
INSERT INTO `phenotype_parameter` VALUES (2157,'IMPC_CBC_009_001',6,'Calcium','calcium',1,5,'mg/dl','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1952);
INSERT INTO `phenotype_parameter` VALUES (2158,'IMPC_CBC_010_001',6,'Phosphorus','phosphate',1,6,'mg/dl','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1953);
INSERT INTO `phenotype_parameter` VALUES (2159,'IMPC_CBC_011_001',6,'Iron','iron',1,5,'mg/dl','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1954);
INSERT INTO `phenotype_parameter` VALUES (2160,'IMPC_CBC_012_001',6,'Aspartate aminotransferase','aspartate_aminotransferase',1,2,'U/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1955);
INSERT INTO `phenotype_parameter` VALUES (2161,'IMPC_CBC_013_001',6,'Alanine aminotransferase','alanine_aminotransferase',1,2,'U/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1956);
INSERT INTO `phenotype_parameter` VALUES (2162,'IMPC_CBC_014_001',6,'Alkaline phosphatase','alkaline_phosphatase',1,2,'U/l','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1957);
INSERT INTO `phenotype_parameter` VALUES (2163,'IMPC_CBC_015_001',6,'Total cholesterol','total_cholesterol',1,4,'mg/dl','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1958);
INSERT INTO `phenotype_parameter` VALUES (2164,'IMPC_CBC_016_001',6,'HDL-cholesterol','hdl_cholesterol',1,4,'mg/dl','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1959);
INSERT INTO `phenotype_parameter` VALUES (2165,'IMPC_CBC_017_001',6,'Triglycerides','triglycerides',1,4,'mg/dl','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1960);
INSERT INTO `phenotype_parameter` VALUES (2166,'IMPC_CBC_018_001',6,'Glucose','glucose',1,5,'mg/dl','FLOAT','simpleParameter',NULL,1,0,0,0,1,0,0,0,0,'',1961);
INSERT INTO `phenotype_parameter` VALUES (2167,'IMPC_CBC_019_001',6,'LIH (Hemolysis Severity - available on AU analysers)','lih_hemolysis_severity_available_on_au_analysers_',1,3,' ','TEXT','simpleParameter',NULL,0,0,0,0,0,0,0,0,0,'',1962);
INSERT INTO `phenotype_parameter` VALUES (2168,'IMPC_CBC_020_001',6,'Fructosamine','fructosamine',1,2,'umol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1963);
INSERT INTO `phenotype_parameter` VALUES (2169,'IMPC_CBC_021_001',6,'Lipase','lipase',1,1,'U/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1964);
INSERT INTO `phenotype_parameter` VALUES (2170,'IMPC_CBC_022_001',6,'Lactate dehydrogenase','lactate_dehydrogenase',1,2,'U/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1965);
INSERT INTO `phenotype_parameter` VALUES (2171,'IMPC_CBC_023_001',6,'Alpha-amylase','alpha_amylase',1,2,'U/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1966);
INSERT INTO `phenotype_parameter` VALUES (2172,'IMPC_CBC_024_001',6,'UIBC (unsaturated iron binding capacity)','uibc_unsaturated_iron_binding_capacity_',1,0,'umol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1967);
INSERT INTO `phenotype_parameter` VALUES (2173,'IMPC_CBC_025_001',6,'LDL-cholesterol','ldl_cholesterol',1,3,'mg/dl','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1968);
INSERT INTO `phenotype_parameter` VALUES (2174,'IMPC_CBC_026_001',6,'Free fatty acids','free_fatty_acids',1,4,'mmol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1969);
INSERT INTO `phenotype_parameter` VALUES (2175,'IMPC_CBC_027_001',6,'Glycerol','glycerol',1,4,'mmol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1970);
INSERT INTO `phenotype_parameter` VALUES (2176,'IMPC_CBC_028_001',6,'Creatine kinase','creatine_kinase',1,2,'U/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1971);
INSERT INTO `phenotype_parameter` VALUES (2177,'IMPC_CBC_029_001',6,'Uric acid','uric_acid',1,2,'umol/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1972);
INSERT INTO `phenotype_parameter` VALUES (2178,'IMPC_CBC_030_001',6,'Ferritin','ferritin',1,3,'ng/ml','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1973);
INSERT INTO `phenotype_parameter` VALUES (2179,'IMPC_CBC_031_001',6,'Transferrin','transferrin',1,2,'mg/dl','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1974);
INSERT INTO `phenotype_parameter` VALUES (2180,'IMPC_CBC_032_001',6,'C-reactive protein','c_reactive_protein',1,0,'mg/l','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',1975);
INSERT INTO `phenotype_parameter` VALUES (2181,'IMPC_CBC_033_001',6,'Equipment ID','equipment_name',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',1976);
INSERT INTO `phenotype_parameter` VALUES (2182,'IMPC_CBC_034_001',6,'Equipment manufacturer','equipment_manufacturer',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',1977);
INSERT INTO `phenotype_parameter` VALUES (2183,'IMPC_CBC_035_001',6,'Equipment model','equipment_model',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',1978);
INSERT INTO `phenotype_parameter` VALUES (2184,'IMPC_CBC_036_001',6,'Anesthesia used for blood collection','anesthesia_used_for_blood_collection',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',1979);
INSERT INTO `phenotype_parameter` VALUES (2185,'IMPC_CBC_038_001',6,'Anticoagulant','anticoagulant',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',1981);
INSERT INTO `phenotype_parameter` VALUES (2186,'IMPC_CBC_037_001',6,'Method of blood collection','method_of_blood_collection',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',1980);
INSERT INTO `phenotype_parameter` VALUES (2187,'IMPC_CBC_042_001',6,'Samples kept on ice between collection and analysis','samples_kept_on_ice_between_collection_and_analysis',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',1985);
INSERT INTO `phenotype_parameter` VALUES (2188,'IMPC_CBC_043_001',6,'Sample status','sample_status',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',1986);
INSERT INTO `phenotype_parameter` VALUES (2189,'IMPC_CBC_044_001',6,'Sample dilution','plasma_dilution',1,2,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',1987);
INSERT INTO `phenotype_parameter` VALUES (2190,'IMPC_CBC_045_001',6,'ID of blood collection SOP','id_of_blood_collection_sop',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',1988);
INSERT INTO `phenotype_parameter` VALUES (2191,'IMPC_CBC_046_001',6,'Date and time of blood collection','date_and_time_of_blood_collection',1,2,' ','DATETIME','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',1989);
INSERT INTO `phenotype_parameter` VALUES (2192,'IMPC_CBC_047_001',6,'Date of measurement','date_of_measurement',1,3,' ','DATE','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',1990);
INSERT INTO `phenotype_parameter` VALUES (2193,'IMPC_CBC_048_001',6,'Hemolysis status','hemolysis_status',1,1,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,'',1991);
INSERT INTO `phenotype_parameter` VALUES (2194,'IMPC_CBC_049_001',6,'Blood collection experimenter ID','',1,1,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',2157);
INSERT INTO `phenotype_parameter` VALUES (2195,'IMPC_CBC_039_001',6,'Blood collection tubes','',1,1,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,'',2388);
INSERT INTO `phenotype_parameter` VALUES (2196,'IMPC_CBC_050_001',6,'Date equipment last calibrated','',1,2,' ','DATE','procedureMetadata',NULL,0,1,0,0,0,0,0,0,0,'',2167);
INSERT INTO `phenotype_parameter` VALUES (2197,'IMPC_CBC_040_001',6,'Date and time of sacrifice','',1,1,' ','DATETIME','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',2389);
INSERT INTO `phenotype_parameter` VALUES (2198,'IMPC_CBC_041_001',6,'Storage temperature from blood collection till measurement','',1,3,'C','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',2398);
INSERT INTO `phenotype_parameter` VALUES (2199,'IMPC_CBC_051_001',6,'Blood analysis experimenter ID','',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,0,0,0,'',2402);
INSERT INTO `phenotype_parameter` VALUES (2200,'IMPC_CBC_052_001',6,'Glycosilated hemoglobin A1c (HbA1c)','',1,3,'%','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',3467);
INSERT INTO `phenotype_parameter` VALUES (2201,'IMPC_CBC_053_001',6,'Thyroxine','',1,2,'ug/dl','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',3468);
INSERT INTO `phenotype_parameter` VALUES (2202,'IMPC_CBC_054_001',6,'Magnesium','',1,5,'mg/dl','FLOAT','simpleParameter',NULL,0,0,0,0,1,0,0,0,0,'',3469);
INSERT INTO `phenotype_parameter` VALUES (2203,'IMPC_CBC_060_001',6,'Reagent manufacturer','',1,0,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,'',5969);
INSERT INTO `phenotype_parameter` VALUES (2204,'IMPC_CBC_055_001',6,'Difficult bleed','',1,0,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,'',3771);
INSERT INTO `phenotype_parameter` VALUES (2205,'IMPC_CBC_056_001',6,'Sample type','',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',4709);
INSERT INTO `phenotype_parameter` VALUES (2206,'IMPC_CBC_057_001',6,'Fasting','',1,0,' ','TEXT','procedureMetadata',NULL,1,1,0,0,0,0,1,0,0,'',4710);
INSERT INTO `phenotype_parameter` VALUES (2207,'IMPC_CBC_058_001',6,'Cholesterol ratio','TC/HDLC ratio',1,0,' ','FLOAT','simpleParameter','IMPC_CBC_015_001 IMPC_CBC_016_001 /',0,0,0,1,1,0,0,0,0,'',4713);
INSERT INTO `phenotype_parameter` VALUES (2208,'IMPC_CBC_059_001',6,'Reagent manufacturer','',1,0,' ','TEXT','procedureMetadata',NULL,0,1,0,0,0,0,1,0,0,'',4855);

INSERT INTO `phenotyped_colony` VALUES (7205,'TCPR0445_ACXM','','MGI:1924833',3,'Notum<em2(IMPC)Tcp>','C57BL/6NCrl','MGI:2683688',27,12,27,12);
INSERT INTO `genomic_feature` VALUES ('MGI:1924833',3,'Notum','notum palmitoleoyl-protein carboxylesterase','1',2,'2',2,11,120653788,120661175,-1,'84.38','active');

INSERT INTO ontology_term (acc, db_id, name, is_obsolete, replacement_acc) VALUES
  ('EFO:0002948', 15, 'postnatal',          0, null),
  ('EFO:0005857', 15, 'mouse embryo stage', 0, null),
  ('MA:0002405',   8, 'postnatal mouse',    0, null);