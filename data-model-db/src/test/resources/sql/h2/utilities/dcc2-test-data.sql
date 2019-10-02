DROP SCHEMA IF EXISTS dcc2test;
CREATE SCHEMA dcc2test;
DROP TABLE IF EXISTS test;
CREATE TABLE test (id INTEGER, message VARCHAR(64));

--
--  Insert 7 rows of test data to the dcc2.test. table. The first 5 rows should match exactly dcc1.test.
--
INSERT INTO test (id, message) VALUES (1, 'dcc line 1');
INSERT INTO test (id, message) VALUES (2, 'dcc line 2');
INSERT INTO test (id, message) VALUES (3, 'dcc line 3');
INSERT INTO test (id, message) VALUES (4, 'dcc line 4');
INSERT INTO test (id, message) VALUES (5, 'dcc line 5');
INSERT INTO test (id, message) VALUES (6, 'dcc line 6');
INSERT INTO test (id, message) VALUES (7, 'dcc line 7');
