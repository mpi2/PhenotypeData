INSERT INTO biological_sample
    (id, external_id, db_id, sample_type_acc, sample_type_db_id, sample_group, organisation_id, production_center_id, project_id)
VALUES
       (40, 'RAB18-129P/25.2e_4771763', 12, 'MA:0002405', 8, 'control', 7, 7, 1)
;


INSERT INTO phenotype_pipeline
    (id, stable_id, db_id, name, description, major_version, minor_version, stable_key, is_deprecated)
VALUES
    (30, 'ESLIM_001', 6, 'EUMODIC Pipeline 1', 'EUMODIC Pipeline 1', 1, 0, 1, 0)
;


INSERT INTO phenotype_procedure
    (id, stable_key, stable_id, db_id, name, description, major_version, minor_version, is_mandatory, level, stage, stage_label, schedule_key)
VALUES
    (219, 1, 'ESLIM_001_001', 6, 'Dysmorphology', 'A simple method to examine mice for morphological abnormalities.', 1, 0, 0, 'experiment', 'Adult', 'Week 9', 0)
;


INSERT INTO biological_model
    (id, db_id, allelic_composition, genetic_background, zygosity)
VALUES
    (37941, 12, 'Vangl2<Lp>/Vangl2<+>', 'involves: C3H/HeH', 'heterozygote')
;


INSERT INTO experiment
    (id, db_id, external_id, sequence_id, date_of_experiment, organisation_id, project_id, pipeline_id, pipeline_stable_id, procedure_id, procedure_stable_id, biological_model_id, colony_id, metadata_combined, metadata_group, procedure_status, procedure_status_message)
VALUES
    (1, 12, '4535_14344', '4535_14344', '2009-06-19 00:00:00', 7, 1, 30, 'ESLIM_001', 219, 'ESLIM_001_001', 37941, NULL, '', 'd41d8cd98f00b204e9800998ecf8427e', NULL, NULL)
;