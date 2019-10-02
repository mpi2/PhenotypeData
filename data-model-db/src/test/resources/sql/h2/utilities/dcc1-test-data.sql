DROP SCHEMA IF EXISTS dcc1test;
CREATE SCHEMA dcc1test;
DROP TABLE IF EXISTS test;
CREATE TABLE test (id INTEGER, message VARCHAR(64));

--
--  Insert 5 rows of test data to the dcc1.test. table.
--
INSERT INTO test (id, message) VALUES (1, 'dcc line 1');
INSERT INTO test (id, message) VALUES (2, 'dcc line 2');
INSERT INTO test (id, message) VALUES (3, 'dcc line 3');
INSERT INTO test (id, message) VALUES (4, 'dcc line 4');
INSERT INTO test (id, message) VALUES (5, 'dcc line 5');
