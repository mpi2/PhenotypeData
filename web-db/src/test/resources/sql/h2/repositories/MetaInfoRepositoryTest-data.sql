DROP TABLE IF EXISTS meta_info;
CREATE TABLE meta_info (
    id                       INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    property_key             VARCHAR(255) NOT NULL DEFAULT '',
    property_value           VARCHAR(2000) NOT NULL DEFAULT '',
    description              TEXT,

    PRIMARY KEY (id),
    UNIQUE KEY key_idx (property_key)
);


INSERT INTO meta_info
  (id, property_key, property_value, description)
VALUES
    (1, 'data_release_version', '10.1', 'Major data release 10.1, released on 03 June 2019, analysed using PhenStat version 2.7.1'),
    (2, 'data_release_date', '26 June 2019', '26 June 2019'),
    (3, 'statistical_packages', 'PhenStat', '')