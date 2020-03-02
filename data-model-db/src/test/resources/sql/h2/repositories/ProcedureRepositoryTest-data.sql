INSERT INTO phenotype_pipeline
    (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated)
VALUES
    (1, 'IMPC_001', 6, 'IMPC pipeline', '', 1, 0, 1, 0),
    (14, 'TRC_001', 6, 'Trichuris challenge', '', 1, 0, 18, 0),
    (15, 'DSS_001', 6, 'DSS challenge', '', 1, 0, 19, 0),
    (17, 'HRWLLA_001', 6, 'Harwell late adult', 'Harwell late adult', 1, 0, 30, 0)
;

INSERT INTO phenotype_procedure
    (id, stable_key, stable_id, db_id, name, description, major_version, minor_version, is_mandatory, level, stage, stage_label, schedule_key)
VALUES
    (15, 172, 'IMPC_ELZ_001', 6, 'Embryo LacZ', '', 1, 1, 0, 'experiment', 'Embryonic', 'E12.5', 4),
    (2, 173, 'IMPC_HOU_001', 6, 'Housing and Husbandry', '', 1, 0, 0, 'housing', 'Adult', 'Unrestricted', 3),
    (8, 222, 'IMPC_HPL_001', 6, 'Histopathology Placenta E9.5', '', 1, 0, 1, 'experiment', 'Embryonic', 'E9.5 and Younger', 0)
;

INSERT INTO phenotype_pipeline_procedure
    (pipeline_id, procedure_id)
VALUES
    (1, 2),
    (17, 15),
    (15, 8);