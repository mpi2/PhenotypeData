INSERT INTO ontology_term
    (acc, db_id, name, description, is_obsolete, replacement_acc)
VALUES
    ('EFO:0002948', 15, 'postnatal', 'The time period after birth.', 0, NULL),
    ('MA:0002405',  8,  'postnatal mouse', NULL, '0', NULL)
;


INSERT INTO biological_sample
    (id, external_id, db_id, sample_type_acc, sample_type_db_id, sample_group, organisation_id, production_center_id, project_id)
VALUES
       (1  ,    '129SVEVM/70.8a_3763764', 12, 'MA:0002405', 8, 'control',      7,   7,  3),
       (3,      '129SVEVM/85.2e_3766817', 12, 'MA:0002405', 8, 'control',      7,   7,  3),
       (32703,  '338766',                 12, 'MA:0002405', 8, 'experimental', 3,   3,  3),
       (117523, '1000548247',             22, 'MA:0002405', 8, 'control',      35,  35, 3),
       (117552, '1000593657',             22, 'MA:0002405', 8, 'control',      35,  35, 3)
;


INSERT INTO live_sample
    (id,     colony_id,  developmental_stage_acc, developmental_stage_db_id, sex,      zygosity,     date_of_birth,         litter_id)
VALUES
    (3,      'baseline', 'EFO:0002948',           15,                        'male',   'homozygote', '2007-04-24 00:00:00', 'unknown'),
    (1,      'baseline', 'EFO:0002948',           15,                        'female', 'homozygote', '2007-04-26 00:00:00', 'unknown'),
    (117523, '',         'EFO:0002948',           15,                        'male',   'homozygote', '2015-04-08 00:00:00', ''),
    (117552, '',         'EFO:0002948',           15,                        'male',   'homozygote', '2015-05-25 00:00:00', ''),
    (32703,  '#4',       'EFO:0002948',           15,                        'male',   'homozygote', '2009-09-01 00:00:00', 'unknown')
;