DROP TABLE IF EXISTS analytics_pvalue_distribution;
CREATE TABLE analytics_pvalue_distribution (
    id                 INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    datatype           varchar(50)  NOT NULL,
    statistical_method varchar(200) NOT NULL,
    pvalue_bin         float        NOT NULL,
    interval_scale     float        NOT NULL,
    pvalue_count       int(10)      NOT NULL
);


INSERT INTO analytics_pvalue_distribution
    (id, datatype, statistical_method, pvalue_bin, interval_scale, pvalue_count)
VALUES
    (1,   'categorical',    'Fisher''s exact test', 0.06, 0.02, 0),
    (2,   'categorical',    'Fisher''s exact test', 0.02, 0.02, 0),
    (3,   'categorical',    'Fisher''s exact test', 0.04, 0.02, 0),
    (51,  'unidimensional', 'Wilcoxon rank sum test with continuity correction', 0.02, 0.02, 0),
    (52,  'unidimensional', 'Wilcoxon rank sum test with continuity correction', 0.04, 0.02, 0),
    (101, 'unidimensional', 'Mixed Model framework, generalized least squares, equation withoutWeight', 0.06, 0.02, 1588),
    (102, 'unidimensional', 'Mixed Model framework, generalized least squares, equation withoutWeight', 0.04, 0.02, 2078),
    (103, 'unidimensional', 'Mixed Model framework, generalized least squares, equation withoutWeight', 0.02, 0.02, 6065)
;