

--
--  Insert test data to the external_db table
--
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (1, 'Mouse Genome Assembly', 'NCBI m38', 'GRCm38', '2012-01-09');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (2, 'MGI Genome Feature Type', 'Genome Feature Type', 'JAX', '2011-12-22');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (3, 'Mouse Genome Informatics', 'MGI', 'JAX', '2011-12-22');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (4, 'murine X/Y pseudoautosomal region', 'X/Y', 'unknown', '2012-01-06');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (5, 'Mammalian Phenotype', 'MP', 'JAX', '2012-01-09');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (6, 'IMPReSS', 'IMPReSS', 'unknown', '2012-01-26');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (7, 'Phenotypic qualities (properties)', 'PATO', 'unknown', '2011-11-23');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (8, 'Mouse adult gross anatomy', 'MA', 'unknown', '2011-07-22');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (9, 'Chemical entities of biological interest', 'CHEBI', '87', '2012-01-10');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (10, 'Environment Ontology', 'EnvO', 'unknown', '2011-03-24');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (11, 'Gene Ontology', 'GO', '1.1.2551', '2012-01-26');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (12, 'EuroPhenome', 'EuroPhenome', 'February 2012', '2012-04-26');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (13, 'Evidence codes', 'ECO', 'July 2012', '2012-07-30');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (14, 'Mouse gross anatomy and development', 'EMAP', 'July 2012', '2012-07-30');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (15, 'Experimental Factor Ontology', 'EFO', 'July 2012', '2012-07-31');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (16, 'International Mouse Strain Resource', 'IMSR', 'August 2012', '2012-08-13');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (20, 'WTSI Mouse Genetics Project', 'MGP', 'unknown', '2013-02-19');
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (22, 'International Mouse Phenotyping Consortium', 'IMPC', '2010-11-15', '2010-11-15');



--
--  Insert test data to the biological_model table
--
INSERT INTO biological_model (db_id, allelic_composition, genetic_background, zygosity) VALUES (12, '', 'involves: C57BL/6', 'homozygote');

--
--  Insert test data to test the ontology_term table
--
INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete) VALUES ('1', 2, 'Gene', 'A region (or regions) that include all of the sequence elements necessary to encode a functional transcript. A gene may include regulatory regions, transcribed regions, and/or other functional sequence regions.', 0);
INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete) VALUES ('2', 2, 'protein coding gene', 'A gene that produces at least one transcript that is translated into a protein.', 0);
INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete) VALUES ('3', 2, 'non-coding RNA gene', 'A gene that produces an RNA transcript that functions as the gene product.', 0);
INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete) VALUES ('4', 2, 'rRNA gene', 'A gene that encodes ribosomal RNA.', 0);
INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete) VALUES ('5', 2, 'tRNA gene', 'A gene that encodes Transfer RNA.  ', 0);
INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete) VALUES ('MP:0000001', 5, 'mammalian phenotype', 'the observable morphological, physiological, behavioral and other characteristics of mammalian organisms that are manifested through development and lifespan', 0);
INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete) VALUES ('MP:0000002', 5, 'obsolete Morphology', 'OBSOLETE.', 0);
INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete) VALUES ('MP:0000003', 5, 'abnormal adipose tissue morphology', 'any structural anomaly of the connective tissue composed of fat cells enmeshed in areolar tissue', 0);
INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete) VALUES ('MP:0000005', 5, 'increased brown adipose tissue amount', 'increased amount of the thermogenic form of adipose tissue that is composed of brown adipocytes', 0);
INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete) VALUES ('MP:0000008', 5, 'increased white adipose tissue amount', 'increased quantity of fat-storing cells/tissue', 0);
INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete) VALUES ('EFO:eday9.5', 15, 'embryonic day 9.5', 'embryonic day 9.5', 0);


--
--  Insert test data to the phenotype_parameter table
--
INSERT INTO phenotype_parameter (stable_id, db_id, name, description, major_version, minor_version, unit, datatype, parameter_type, formula, required, metadata, important, derived, annotate, increment, options, sequence, media, data_analysis, data_analysis_notes, stable_key) VALUES ('ESLIM_003_001_006', 6, 'Heat production (metabolic rate)', 'Heat_Production_Metabolic_Rate', 1, 0, 'kJ/h/animal', '', 'seriesParameter', null, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, '', 196);


