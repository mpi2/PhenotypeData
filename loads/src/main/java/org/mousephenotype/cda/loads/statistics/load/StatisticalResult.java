package org.mousephenotype.cda.loads.statistics.load;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Interface for statistical result for continuous parameters
 */
public interface StatisticalResult {

	PreparedStatement getSaveResultStatement(Connection connection, LightweightResult result) throws SQLException;

}
