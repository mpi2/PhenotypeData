INSERT INTO phenotype_pipeline
    (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated)
VALUES
    (2, 'HRWL_001', 6, 'Harwell', 'Harwell extra parameters', 1, 0, 8, 0)
;

INSERT INTO phenotype_procedure
    (id, stable_key, stable_id, db_id, name, description, major_version, minor_version, is_mandatory, level, stage, stage_label, schedule_key)
VALUES
    (1, 103, 'IMPC_BWT_001', 6, 'Body Weight', 'The body weight test measures the weight of the mouse in a time series, allowing monitoring of its evolution; also, it is required in many other procedures.', 1, 3, 1, 'experiment', 'Adult', 'Unrestricted', 0)
;


INSERT INTO biological_model
    (id, db_id, allelic_composition, genetic_background, zygosity)
VALUES
    (39787, 22, 'Gnb4<em1(IMPC)H>/Gnb4<em1(IMPC)H>', 'involves: C57BL/6NTac', 'homozygote')
;