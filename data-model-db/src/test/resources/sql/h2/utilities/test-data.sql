
--
--  Insert test data to the biological_model table
--
INSERT INTO biological_model (db_id, allelic_composition, genetic_background, zygosity) VALUES (12, '', 'involves: C57BL/6', 'homozygote');

--  Insert test data to the phenotype_parameter table
--
INSERT INTO phenotype_parameter (stable_id, db_id, name, description, major_version, minor_version, unit, datatype, parameter_type, formula, required, metadata, important, derived, annotate, increment, options, sequence, media, data_analysis_notes, stable_key) VALUES ('ESLIM_003_001_006', 6, 'Heat production (metabolic rate)', 'Heat_Production_Metabolic_Rate', 1, 0, 'kJ/h/animal', '', 'seriesParameter', null, 1, 0, 0, 0, 1, 1, 0, 0, 0, '', 196);
INSERT INTO phenotype_parameter (stable_id, db_id, name, description, major_version, minor_version, unit, datatype, parameter_type, formula, required, metadata, important, derived, annotate, increment, options, sequence, media, data_analysis_notes, stable_key) VALUES ('TCP_VFR_001_001', 6, 'Baseline: tabulation', '', 1, 0, ' ', 'TEXT', 'seriesParameter', null, 1, 0, 0, 0, 0, 1, 0, 0, 0, '', 67158)

