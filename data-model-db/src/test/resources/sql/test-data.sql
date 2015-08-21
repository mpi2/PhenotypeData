

--
--  Insert test data to the external_db table
--
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (12,'EuroPhenome', 'EuroPhenome', 'February 2012', '2012-04-26');


--
--  Insert test data to the biological_model table
--
INSERT INTO biological_model (db_id, allelic_composition, genetic_background, zygosity) VALUES (12, '', 'involves: C57BL/6', 'homozygote');

--
--  Insert test data to test the ontology_term table
--
insert into external_db(id, name, short_name, version, version_date) values(5, 'Mammalian Phenotype', 'MP', 'JAX', '2012-01-09');
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

