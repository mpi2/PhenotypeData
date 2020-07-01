INSERT INTO experiment
  (id, db_id, external_id, sequence_id, date_of_experiment, organisation_id, project_id, pipeline_id, pipeline_stable_id, procedure_id,
   procedure_stable_id, biological_model_id, colony_id, metadata_combined, metadata_group, procedure_status, procedure_status_message)
VALUES
  (418722, 12, 'ESLIM_024_001-B6.129P2-Vnn1<tm1Pna>/H',   NULL, NULL, 7, 1, 6, 'ESLIM_003', 80, 'ESLIM_024_001', 39076, 'B6.129P2-Vnn1<tm1Pna>/H', '', 'd41d8cd98f00b204e9800998ecf8427e', NULL, NULL),
  (418724, 12, 'ESLIM_024_001-B6.Cg-Sfrp2<C50F>/H',     NULL, NULL, 7, 1, 6, 'ESLIM_003', 80, 'ESLIM_024_001', 39081, 'B6.Cg-Sfrp2<C50F>/H', '', 'd41d8cd98f00b204e9800998ecf8427e', NULL, NULL),
  (418742, 12, 'ESLIM_024_001-C.Cg-Ostes/H',            NULL, NULL, 7, 1, 6, 'ESLIM_003', 80, 'ESLIM_024_001', 39075, 'C.Cg-Ostes/H', '', 'd41d8cd98f00b204e9800998ecf8427e', NULL, NULL),
  (418754, 12, 'ESLIM_024_001-B6.Cg-Sfrp5<Q27STOP>/H',  NULL, NULL, 7, 1, 6, 'ESLIM_003', 80, 'ESLIM_024_001', 39078, 'B6.Cg-Sfrp5<Q27STOP>/H', '', 'd41d8cd98f00b204e9800998ecf8427e', NULL, NULL),
  (418756, 12, 'ESLIM_024_001-129/SvEv-Gnas<tm1Jop>/H', NULL, NULL, 7, 1, 6, 'ESLIM_003', 80, 'ESLIM_024_001', 39083, '129/SvEv-Gnas<tm1Jop>/H', '', 'd41d8cd98f00b204e9800998ecf8427e', NULL, NULL)
;

INSERT INTO biological_model
  (id, db_id, allelic_composition, genetic_background, zygosity)
VALUES
  (39075, 12, 'Ostes/Ostes', 'involves: BALB/cAnNCrl', 'homozygote'),
  (39076, 12, 'Vnn1<tm1Pna>/Vnn1<tm1Pna>', 'involves: C57BL/6J', 'homozygote'),
  (39078, 12, 'Sfrp5<Q27STOP>/Sfrp5<Q27STOP>', 'involves: C57BL/6J', 'homozygote'),
  (39081, 12, 'Sfrp2<C50F>/Sfrp2<C50F>', 'involves: C57BL/6J', 'homozygote'),
  (39083, 12, 'Gnas<tm1Jop>/Gnas<tm1Jop>', 'involves: 129S9/SvEvH', 'homozygote')
;




-- INSERT INTO strain


-- INSERT INTO biological_model_strain


DROP TABLE IF EXISTS phenotype_parameter_ontology_annotation;
CREATE TABLE phenotype_parameter_ontology_annotation (
     id                 INTEGER NULL,
     event_type         VARCHAR(128) NULL,
     option_id          VARCHAR(128) NULL,
     ontology_acc       VARCHAR(128) NOT NULL,
     ontology_db_id     VARCHAR(128) NULL,
     sex                VARCHAR(128) NULL
);