package org.mousephenotype.cda.loads.legacy.dccimport.imits;

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Imits client that uses a report from iMits to get approprate data
 * <p>
 * Fields in the internal database are: symbol, accession, colony, escell, strain, production_center, project, center,
 * resource, allele
 */
public class FileBasedImitsClient implements ImitsClient {
	private static final Logger logger = LoggerFactory.getLogger(FileBasedImitsClient.class);
	private static boolean initialized = false;
	public Map<String, String> centerNameMap = new HashMap<>();
	private DataSource dataSource;
	private JdbcTemplate jdbc;


	public FileBasedImitsClient(String filename) throws IOException, SQLException {

		// Set up the database
		initDb();

		// Setup the center name map
		centerNameMap.put("HARWELL", "MRC HARWELL");
		centerNameMap.put("RIKEN BRC", "RBRC");

		if (filename == null || !Files.exists(Paths.get(filename))) {
			throw new RuntimeException("Filename must be defined and exist.");
		}

		String output = new String(Files.readAllBytes(Paths.get(filename)));

		// Parse the file into the embedded db
		for (String line : output.split("\n")) {

			insert(line);

		}

		String sql = "SELECT DISTINCT COUNT(*) FROM colony";
		Integer c = jdbc.queryForObject(sql, Integer.class);
		logger.debug("Loaded {} lines", c);

	}

	private DataSource dataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		return builder.setType(EmbeddedDatabaseType.HSQL).build();
	}

	public JdbcTemplate getJdbc() {
		dataSource = dataSource();
		return new JdbcTemplate(dataSource);
	}

	private void initDb() {

		// Set up the jdbc template
		jdbc = getJdbc();

		if (!initialized) {

			jdbc.execute("CREATE TABLE colony (symbol VARCHAR(255), accession VARCHAR(255), colony VARCHAR(255), escell VARCHAR(255), strain VARCHAR(255), production_center VARCHAR(255), project VARCHAR(255), center VARCHAR(255), resource VARCHAR(255), allele VARCHAR(255))");

			initialized = true;
		} else {
			jdbc.execute("DELETE FROM colony");
		}
	}

	private void insert(String s) throws SQLException {
		String sql = "INSERT INTO colony (symbol, accession, colony, escell, strain, production_center, project, center, resource, allele) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			int i = 1;

			for (String field : Arrays.asList(s.split("\t"))) {
				// Translate the organisation names from iMits names to CDA names
				if (i == 6 || i == 8) {
					SqlUtils.setSqlParameter(statement, getCenterName(field), i++);
				} else {
					SqlUtils.setSqlParameter(statement, field, i++);
				}
			}

			statement.executeUpdate();

		}
	}

	public String getCenterName(String orgName) {

		String upperOrgName = orgName.toUpperCase();
		if (centerNameMap.containsKey(upperOrgName)) {
			upperOrgName = centerNameMap.get(upperOrgName);
		}

		return upperOrgName;
	}

	@Override
	public String getGeneByColonyId(String colonyId, String center) {

		String sql = "SELECT DISTINCT accession FROM colony WHERE colony=? AND center=?";
		logger.debug("Executing sql " + sql);
		try {
			return jdbc.queryForObject(sql, new Object[]{colonyId, center.toUpperCase()}, String.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}


	@Override
	public String getAlleleSymbolByColonyId(String colonyId) {

		String sql = "SELECT DISTINCT allele FROM colony WHERE colony=?";//" '" + colonyId + "'";
		logger.debug("Executing sql " + sql);
		try {
			return jdbc.queryForObject(sql, new Object[]{colonyId}, String.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}


	@Override
	public String getStrainByColonyId(String colonyId, String center) {
		String sql = "SELECT DISTINCT strain FROM colony WHERE colony=? AND center=?";
		logger.debug("Executing sql " + sql);
		try {
			return jdbc.queryForObject(sql, new Object[]{colonyId, center.toUpperCase()}, String.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public String getStrainNameByColonyId(String colonyId, String center) {
		return this.getStrainByColonyId(colonyId, center);
	}

	@Override
	public String getColonyIdByEsCell(String escell, String center) {
		String sql = "SELECT DISTINCT colony FROM colony WHERE escell=? AND center=? ORDER BY colony LIMIT 1";
		logger.debug("Executing sql " + sql);
		try {
			return jdbc.queryForObject(sql, new Object[]{escell, center.toUpperCase()}, String.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}


	@Override
	public String getProjectByEsCell(String escell, String center) {
		String sql = "SELECT DISTINCT project FROM colony WHERE escell=? AND center=?";
		logger.debug("Executing sql " + sql);
		try {
			return jdbc.queryForObject(sql, new Object[]{escell, center.toUpperCase()}, String.class);
		} catch (IncorrectResultSizeDataAccessException e) {

			// If there are more than one project for this escell/center combination
			// and if the projects are "MGP" and "MGP Legacy".
			// Only in legacy data is project looked up by ESCell
			// Detect this and pass back appropriately
			List<String> projects = jdbc.query(sql, new RowMapper<String>() {
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString(1);
				}
			}, escell, center.toUpperCase());

			// This is a legacy line and so we will return "MGP Legacy"
			if (projects.size()>0 && projects.contains("MGP") && projects.contains("MGP Legacy")) {
				return "MGP Legacy";
			}

			return null;
		}

	}


	@Override
	public String getAlleleMGIIDByEscell(String esCell) {
		logger.debug("getAlleleMGIIDByEscell is not implemented for File Based Imits client.");
		return null;
	}


	@Override
	public String getGeneByEscell(String escell) {
		String sql = "SELECT DISTINCT accession FROM colony WHERE escell=?";
		logger.debug("Executing sql " + sql);
		try {
			return jdbc.queryForObject(sql, new Object[]{escell}, String.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}


}
