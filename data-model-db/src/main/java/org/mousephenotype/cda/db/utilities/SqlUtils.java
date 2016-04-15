package org.mousephenotype.cda.db.utilities;

import org.springframework.stereotype.Component;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by jmason on 26/03/2014.
 */
@Component
public class SqlUtils {

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

}
