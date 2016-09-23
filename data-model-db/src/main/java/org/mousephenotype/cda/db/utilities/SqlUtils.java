package org.mousephenotype.cda.db.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by jmason on 26/03/2014.
 */
@Component
public class SqlUtils {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Overloaded helper methods for preparing SQL statement
     * @param s statement to use to insert
     * @param var variable being inserted
     * @param index position in the statement to insert the variable
     * @throws java.sql.SQLException
     */
    static public void setSqlParameter(PreparedStatement s, Integer var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, java.sql.Types.INTEGER);
        } else {
            s.setInt(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, String var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, java.sql.Types.VARCHAR);
        } else {
            s.setString(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, Boolean var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, java.sql.Types.BOOLEAN);
        } else {
            s.setBoolean(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, Float var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, java.sql.Types.FLOAT);
        } else {
            s.setFloat(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, Double var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, java.sql.Types.DOUBLE);
        } else {
            s.setDouble(index, var);
        }
    }


    static public void setSqlParameter(PreparedStatement s, java.sql.Timestamp var, int index) throws SQLException {

        if (var == null) {
            s.setNull(index, Types.TIMESTAMP);
        } else {
            s.setTimestamp(index, var);
        }
    }

    /**
     * Given a date string, this method attempts to convert the date to a <code>
     * java.sql.Date</code> object and, if successful, returns the date. If
     * not successful, returns null.
     *
     * @param dateString The date string against which to attempt conversion
     * @return the <code>java.sql.Date</code> date, if successful; null otherwise
     */
    public java.sql.Date tryParseStringToDbDate(String dateString) {
        java.sql.Date retVal = null;

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date date = df.parse(dateString);
            retVal = new java.sql.Date(date.getTime());
        }
        catch(ParseException e) { }

        return retVal;
    }


	/**
	 * Determine if a column exists in a table specific for a MySQL database
	 *
	 * @param connection the connection to use to query the database
	 * @param tableName the table to
	 * @param columnName the column in the table
	 * @return true = column name exists in table, false = column missing from table
	 * @throws SQLException on db access error
	 */
	public Boolean columnInSchemaMysql(Connection connection, String tableName, String columnName) throws SQLException {

		Boolean found = Boolean.FALSE;
		String columnQuery = "SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=(SELECT database()) AND TABLE_NAME=? AND column_name=?";

		try (PreparedStatement p = connection.prepareStatement(columnQuery)) {
			p.setString(1, tableName);
			p.setString(2, columnName);
			ResultSet r = p.executeQuery();
			if (r.next()) {
				found = Boolean.TRUE;
			}
		}

		return found;
	}

    /**
     * Given two {@link JdbcTemplate} instances and a query, executes the query against {@code jdbcPrevious}, then
     * against {@code jdbcCurrent}, returning the set difference of the results found in {@code jdbcPrevious} but not
     * found in {@code jdbcCurrent}. If there are no differences, an empty list is returned. If results are returned,
     * the first row is the column headings. Subsequent rows are the data.
     *
     * @param jdbcPrevious the previous {@link JdbcTemplate} instance
     * @param jdbcCurrent the current {@link JdbcTemplate} instance
     * @param query the query to execute against both {@link JdbcTemplate} instances
     *
     * @return the set difference of the results found in jdbc1 but not found in jdbc2Q. If there are no differences,
     *         null is returned.
     *
     * @throws Exception
     */
    public List<String[]> queryDiff(JdbcTemplate jdbcPrevious, JdbcTemplate jdbcCurrent, String query) throws Exception {
        List<String[]> results = new ArrayList<>();

        Set<List<String>> results1 = new HashSet<>();
        SqlRowSet         rs1      = jdbcPrevious.queryForRowSet(query);
        while (rs1.next()) {
            results1.add(getData(rs1));
        }

        Set<List<String>> results2 = new HashSet<>();
        SqlRowSet rs2 = jdbcCurrent.queryForRowSet(query);
        while (rs2.next()) {
            results2.add(getData(rs2));
        }

        // Fill the results list with the rows found in results1 but not found in results2.
        results1.removeAll(results2);

        if ( ! results1.isEmpty()) {
            String[] columnNames = rs1.getMetaData().getColumnNames();
            results.add(columnNames);

            Iterator<List<String>> it = results1.iterator();
            while (it.hasNext()) {
                results.add(it.next().toArray(new String[0]));
            }
        }

        return results;
    }

    /**
     * Given two {@link JdbcTemplate} instances of the same schema type and a query that produces a single int/long
     * value, this method executes the query against {@code jdbcPrevious}, then against {@code jdbcCurrent}. The status,
     * query, counts, ratio, delta, and a column indicating whether or not the ratio is below the threshold are returned
     * as row 2 of the results list.
     *
     * @param jdbcPrevious the previous {@link JdbcTemplate} instance
     * @param jdbcCurrent the current {@link JdbcTemplate} instance
     * @param delta the ratio of currentValue / previousValue beneath which the query should be marked as a warning
     * @param query the query to execute against both {@link JdbcTemplate} instances. The query should return a single
     *              int/long value.
     *
     * @return a {@link List} of strings with the results. The first list element is the heading. The second list
     * element is the results, encapsulated as a set of strings.
     */
    public List<String[]> queryForLongValue(JdbcTemplate jdbcPrevious, JdbcTemplate jdbcCurrent, Double delta, String query) {
        List<String[]> results = new ArrayList<>();

        long previousValue = jdbcPrevious.queryForObject(query, Long.class);
        long currentValue = jdbcCurrent.queryForObject(query, Long.class);

        double difference = ((double)currentValue) / previousValue;
        results.add(new String[] {"Status", "Query", "Previous count", "Current count", "Ratio", "Delta", "Below Threshold"});
        results.add(new String[] {
                (difference < delta ? "FAIL" : "SUCCESS"),
                query,
                Long.toString(previousValue),
                Long.toString(currentValue),
                String.format("%.5f", difference),
                String.format("%.5f", delta),
                (difference < delta ? "true" : "false")
        });

        return results;
    }

    public void createSpringBatchTables(DataSource datasource) {

        logger.info("Creating SPRING BATCH tables");
        org.springframework.core.io.Resource r = new ClassPathResource("org/springframework/batch/core/schema-mysql.sql");
        ResourceDatabasePopulator            p = new ResourceDatabasePopulator(r);
        p.execute(datasource);
    }

    /**
     * Given an {@link SqlRowSet}, extracts each column of data, converting to type {@link String} as necessary,
     * returning the row's cells in a {@link List<String>}
     *
     * @param rs the sql result containng a row of data
     *
     * @return the row's cells in a {@link List<String>}
     *
     * @throws Exception
     */
    private List<String> getData(SqlRowSet rs) throws Exception {
        List<String> newRow = new ArrayList<>();

        SqlRowSetMetaData md = rs.getMetaData();

        // Start index at 1, as column indexes are 1-relative.
        for (int i = 1; i <= md.getColumnCount(); i++) {
            int sqlType = md.getColumnType(i);
            switch (sqlType) {
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    newRow.add(rs.getString(i));
                    break;

                case Types.INTEGER:
                case Types.TINYINT:
                    newRow.add(Integer.toString(rs.getInt(i)));
                    break;

                case Types.BIT:
                    newRow.add(rs.getBoolean(i) ? "1" : "0");
                    break;

                case Types.BIGINT:
                    newRow.add(Long.toString(rs.getLong(i)));
                    break;

                default:
                    throw new Exception("No rule to handle sql type '" + md.getColumnTypeName(i) + "' (" + sqlType + ").");
            }
        }

        return newRow;
    }
}