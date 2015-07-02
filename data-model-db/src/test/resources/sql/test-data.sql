

--
--  Insert test data to the biological_model table
--
INSERT INTO external_db (id, name, short_name, version, version_date) VALUES (12,'EuroPhenome', 'EuroPhenome', 'February 2012', '2012-04-26');


--
--  Insert test data to the biological_model table
--
INSERT INTO biological_model (db_id, allelic_composition, genetic_background, zygosity) VALUES (12, '', 'involves: C57BL/6', 'homozygote')

