DROP TABLE IF EXISTS analytics_significant_calls_procedures;
CREATE TABLE analytics_significant_calls_procedures (
    id                  INT(10) PRIMARY KEY NOT NULL,
    significant_calls   BIGINT(21)          NOT NULL,
    phenotyping_center  VARCHAR(255),
    procedure_stable_id VARCHAR(40),
    procedure_name      VARCHAR(200)
);
INSERT INTO analytics_significant_calls_procedures
    (id, significant_calls, phenotyping_center, procedure_stable_id, procedure_name)
VALUES
   (1, 5,    'BCM',  'BCM_CSD_001',  'Combined SHIRPA and Dysmorphology'),
   (4, 2,    'BCM',  'IMPC_CAL_003', 'Indirect Calorimetry'),
   (23, 223, 'HMGU', 'IMPC_CBC_002', 'Clinical Chemistry'),
   (71, 274, 'JAX',  'IMPC_HEM_002', 'Hematology'),
   (74, 143, 'JAX',  'IMPC_IPG_001', 'Intraperitoneal glucose tolerance test (IPGTT)')
;