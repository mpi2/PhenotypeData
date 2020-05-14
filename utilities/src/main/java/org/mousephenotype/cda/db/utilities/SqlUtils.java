package org.mousephenotype.cda.db.utilities;

import com.zaxxer.hikari.HikariDataSource;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;


/**
 * Created by jmason on 26/03/2014.
 */
@Component
public class SqlUtils {

    private static final Logger      logger      = LoggerFactory.getLogger(SqlUtils.class);
    private              CommonUtils commonUtils = new CommonUtils();

    // Hikari parameters:
    //    https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby
    private final static Integer INITIAL_POOL_CONNECTIONS = 1;
    private final static Integer MAXIMUM_POOL_SIZE        = 100;            // Default is 10. OntologyParserTest times out with 10.


    /**
     * Overloaded helper methods for preparing SQL statement
     * @param s statement to use to insert
     * @param var variable being inserted
     * @param index position in the statement to insert the variable
     * @throws SQLException
     */
    static public void setSqlParameter(PreparedStatement s, Integer var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, Types.INTEGER);
        } else {
            s.setInt(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, Long var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, Types.INTEGER);
        } else {
            s.setLong(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, String var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, Types.VARCHAR);
        } else {
            s.setString(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, Boolean var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, Types.BOOLEAN);
        } else {
            s.setBoolean(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, Float var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, Types.FLOAT);
        } else {
            s.setFloat(index, var);
        }
    }
    static public void setSqlParameter(PreparedStatement s, Double var, int index) throws SQLException {
        if(var==null) {
            s.setNull(index, Types.DOUBLE);
        } else {
            s.setDouble(index, var);
        }
    }


    static public void setSqlParameter(PreparedStatement s, Timestamp var, int index) throws SQLException {

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
            Date date = df.parse(dateString);
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
     * Given two {@link JdbcTemplate} instances and a query, executes the query against {@code jdbc1}, then
     * against {@code jdbc2}, returning the set difference of the results found in {@code jdbc1} but not
     * found in {@code jdbc2}. If there are no differences, an empty list is returned. If results are returned,
     * the first row is the column headings. Subsequent rows are the data.. Strict comparison rules are used for Strings:
     * i.e., no mapping of null to empty string, no trimming of strings, no lowercasing of strings before comparison.
     *
     * @param jdbc1 the first {@link JdbcTemplate} instance
     * @param jdbc2 the second {@link JdbcTemplate} instance
     * @param query the query to execute against both {@link JdbcTemplate} instances
     *
     * @return the set difference of the results found in jdbc1 but not found in jdbc2. If there are no differences,
     *         an empty list is returned.
     *
     * @throws Exception
     */
    public List<String[]> queryDiff(NamedParameterJdbcTemplate jdbc1, NamedParameterJdbcTemplate jdbc2, String query) throws Exception {
        return queryDiff(jdbc1, jdbc2, query, false);
    }

    /**
     * Given two {@link JdbcTemplate} instances and a query, executes the query against {@code jdbc1}, then
     * against {@code jdbc2}, returning the set difference of the results found in {@code jdbc1} but not
     * found in {@code jdbc2}. If there are no differences, an empty list is returned. If results are returned,
     * the first row is the column headings. Subsequent rows are the data.
     *
     * @param jdbc1 the first {@link JdbcTemplate} instance
     * @param jdbc2 the second {@link JdbcTemplate} instance
     * @param query the query to execute against both {@link JdbcTemplate} instances
     * @param useLenient if true, for String types, trim leading and trailing spaces, lowercase the strings, and convert NULLs to empty strings first.
     *                   if false, use exact comparison.
     *
     * @return the set difference of the results found in jdbc1 but not found in jdbc2. If there are no differences,
     *         an empty list is returned.
     *
     * @throws Exception
     */
    public List<String[]> queryDiff(NamedParameterJdbcTemplate jdbc1, NamedParameterJdbcTemplate jdbc2, String query, boolean useLenient) throws Exception {
        List<String[]> results = new ArrayList<>();

        Set<List<String>> results1 = new HashSet<>();
        SqlRowSet         rs1      = jdbc1.queryForRowSet(query, new HashMap<>());
        while (rs1.next()) {
            results1.add(getData(rs1, useLenient));
        }

        Set<List<String>> results2 = new HashSet<>();
        SqlRowSet rs2 = jdbc2.queryForRowSet(query, new HashMap<>());
        while (rs2.next()) {
            results2.add(getData(rs2, useLenient));
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
     * Given a valid {@link NamedParameterJdbcTemplate} instance, returns that connection's database name, database
     * server name, and database port number in a {@link Map}:
     * <ul>
     *     <li>key: dbname</li>
     *     <li>key: dbserver</li>
     *     <li>key: dbport</li>
     * </ul>
     *
     * @param jdbc the {@link NamedParameterJdbcTemplate} instance to query
     *
     * @return the connection's database name, database server name, and database port number
     */
    public Map<String, String> getDbInfo(NamedParameterJdbcTemplate jdbc) {

        Map<String, String> results = new HashMap<>();

        String s = jdbc.getJdbcOperations().queryForObject("SELECT DATABASE()", String.class);
        results.put("dbname", s);

        s = jdbc.getJdbcOperations().queryForObject("SELECT @@HOSTNAME", String.class);
        results.put("dbserver", s);

        s = jdbc.getJdbcOperations().queryForObject("SELECT @@PORT", String.class);
        results.put("dbport", s);

        return results;
    }

    /**
     * Given a valid {@link NamedParameterJdbcTemplate} instance, returns that connection's database name, database
     * server name, and database port number in a {@link String} in the format <i>dbserver:dbport/dbname</i>
     *
     * @param jdbc the {@link NamedParameterJdbcTemplate} instance to query
     *
     * @return the connection's database name, database server name, and database port number in a {@link String} in the
     * format <i>dbserver:dbport/dbname</i>
     */
    public String getDbInfoString(NamedParameterJdbcTemplate jdbc) {
        Map<String, String> info = getDbInfo(jdbc);

        return info.get("dbserver") + ":" + info.get("dbport") + "/" + info.get("dbname");
    }

    /**
     *
     * @param jdbc valid {@link NamedParameterJdbcTemplate} instance
     *
     * @return the database name
     */
    public String getDatabaseName(NamedParameterJdbcTemplate jdbc) {
        Map<String, String> dbinfo = getDbInfo(jdbc);

        return dbinfo.get("dbname");
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
    public List<String[]> queryForLongValue(NamedParameterJdbcTemplate jdbcPrevious, NamedParameterJdbcTemplate jdbcCurrent, Double delta, String query) {
        List<String[]> results = new ArrayList<>();

        long previousValue = jdbcPrevious.queryForObject(query, new HashMap<>(), Long.class);
        long currentValue = jdbcCurrent.queryForObject(query, new HashMap<>(), Long.class);

        // Let's not divide by zero.
        double ratio = 1;
        if (previousValue > 0)
            ratio = ((double)currentValue / previousValue);

        results.add(new String[] {"Status", "Query", "Previous count", "Current count", "Ratio", "Delta", "Below Delta"});
        results.add(new String[] {
                (ratio < delta ? "FAIL" : "SUCCESS"),
                query,
                Long.toString(previousValue),
                Long.toString(currentValue),
                String.format("%.5f", ratio),
                String.format("%.5f", delta),
                (ratio < delta ? "true" : "false")
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
     * returning the row's cells in a {@link List<String>}. Strict comparison rules are used for Strings: i.e., no
     * mapping of null to empty string, no trimming of strings, no lowercasing of strings before comparison.
     *
     * @param rs the sql result containng a row of data
     *
     * @return the row's cells in a {@link List<String>}
     *
     * @throws Exception
     */
    public List<String> getData(SqlRowSet rs) throws Exception {
        return getData(rs, false);
    }

    /**
     * Given an {@link SqlRowSet}, extracts each column of data, converting to type {@link String} as necessary,
     * returning the row's cells in a {@link List<String>}.
     * NOTE: for String types, trim leading and trailing spaces, lowercase the strings, and convert NULLs to empty strings first.
     *
     * @param rs the sql result containng a row of data
     * @param useLenient if true, for String types, trim leading and trailing spaces, lowercase the strings, and convert NULLs to empty strings first.
     *                   if false, use exact comparison.
     *
     * @return the row's cells in a {@link List<String>}
     *
     * @throws Exception
     */
    public List<String> getData(SqlRowSet rs, boolean useLenient) throws Exception {
        List<String> newRow = new ArrayList<>();

        SqlRowSetMetaData md = rs.getMetaData();

        // Start index at 1, as column indexes are 1-relative.
        for (int i = 1; i <= md.getColumnCount(); i++) {
            int sqlType = md.getColumnType(i);
            switch (sqlType) {
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    String s = rs.getString(i);
                    if (useLenient) {
                        if (s == null) {
                            s = "";                                     // remap null strings to empty string
                        }
                        newRow.add(s.trim().toLowerCase());             // trim spaces and convert to lowercase
                    } else {
                        newRow.add(s);
                    }
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

                case Types.TIMESTAMP:
                case Types.DATE:
                    Timestamp ts = rs.getTimestamp(i);
                    String formattedDate = "";
                    if (ts != null)  {
                        Date date = new Date(ts.getTime());
                        if (date != null) {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            formattedDate = dateFormat.format(date);
                        }
                    }
                    newRow.add(formattedDate);
                    break;

                case Types.REAL:
                case Types.FLOAT:
                    newRow.add(Double.toString(rs.getDouble(i)));
                    break;

                default:
                    throw new Exception("No rule to handle sql type '" + md.getColumnTypeName(i) + "' (" + sqlType + ").");
            }
        }

        return newRow;
    }

    /**
     * From https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby
     * maxLifetime:
     * This property controls the maximum lifetime of a connection in the pool. An in-use connection will never be retired,
     * only when it is closed will it then be removed. On a connection-by-connection basis, minor negative attenuation is
     * applied to avoid mass-extinction in the pool. We strongly recommend setting this value, and it should be several
     * seconds shorter than any database or infrastructure imposed connection time limit. A value of 0 indicates no maximum
     * lifetime (infinite lifetime), subject of course to the idleTimeout setting. Default: 1800000 (30 minutes)
     * @param url
     * @param username
     * @param password
     * @return
     */

    public static DataSource getConfiguredDatasource(String url, String username, String password) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setConnectionInitSql("SELECT 1");
        ds.setMinimumIdle(INITIAL_POOL_CONNECTIONS);
        ds.setMaximumPoolSize(MAXIMUM_POOL_SIZE);

        try {
            logger.info("Using database {} with URL: {}", ds.getConnection().getCatalog(), url);

        } catch (Exception e) {

            System.err.println(e.getLocalizedMessage());
            e.printStackTrace();
        }

        return ds;
    }
}