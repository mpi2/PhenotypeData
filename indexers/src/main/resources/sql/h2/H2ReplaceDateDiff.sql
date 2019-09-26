-- Replace the H2 default DATEDIFF function to be compatible with MySQL DATEDIFF syntax
CREATE ALIAS IF NOT EXISTS REMOVE_DATE_DIFF FOR "org.mousephenotype.cda.indexers.H2Function.removeDateDifference";
CALL REMOVE_DATE_DIFF();
DROP ALIAS IF EXISTS DATEDIFF;
CREATE ALIAS DATEDIFF FOR "org.mousephenotype.cda.indexers.H2Function.dateDifference";