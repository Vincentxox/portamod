package com.ericsson.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.log4j.Logger;

public class DaoBase {

	protected Logger logger = Logger.getLogger(DaoBase.class);

	//public static final String TELCASV = "jdbc/telcasv0";  //bk
	public static final String TELCASV = "jdbc/ttelcasv";
	//public static final String SEGURIDAD = "jdbc/seg"; //produccion
   public static final String SEGURIDAD = "jdbc/modsec";  //desarrollo

	// *********************
	// ***** Data part *****
	// *********************

	private String defaultDataSourceName;
	private DataSource defaultDataSource;
	private QueryRunner queryRunner;

	/**
	 * <p>
	 * Application's default data source name.
	 * </p>
	 */
	protected String getDefaultDataSourceName() {
		return defaultDataSourceName;
	}

	protected void setDefaultDataSourceName(String defaultDataSourceName) {
		this.defaultDataSourceName = defaultDataSourceName;
		this.defaultDataSource = null;
		this.queryRunner = null;
	}

	/**
	 * <p>
	 * Gets datasource from JNDI.
	 * </p>
	 * 
	 * @param dataSourceName
	 * @return
	 * @throws NamingException
	 */
	public DataSource getDataSource(String dataSourceName) throws NamingException {
		InitialContext ctx = new InitialContext();
		return (DataSource) ctx.lookup(dataSourceName);
	}

	/**
	 * <p>
	 * Gets the default data source.
	 * </p>
	 * 
	 * @return
	 * @throws NamingException
	 */
	public DataSource getDefaultDataSource() throws NamingException {
		if (defaultDataSource == null) {
			defaultDataSource = getDataSource(defaultDataSourceName);
		}
		return defaultDataSource;
	}

	/**
	 * <p>
	 * Gets connection from data source.
	 * </p>
	 * 
	 * @param dataSourceName
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	public Connection getConnection(String dataSourceName) throws SQLException, NamingException {
		return getDataSource(dataSourceName).getConnection();
	}

	/**
	 * <p>
	 * Gets connection from default data source.
	 * </p>
	 * 
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException, NamingException {
		return getDefaultDataSource().getConnection();
	}

	/**
	 * <p>
	 * Gets the default query runner object using default data source.
	 * </p>
	 * 
	 * @return
	 * @throws NamingException
	 */
	public QueryRunner getQueryRunner() throws NamingException {
		if (queryRunner == null) {
			queryRunner = new QueryRunner(getDefaultDataSource());
		}
		return queryRunner;
	}

	/**
	 * <p>
	 * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected boolean batch(Connection conn, String sql, Object[][] params) throws SQLException, NamingException {
		logger.debug(log(sql, params));
		int[] statement = getQueryRunner().batch(conn, sql, params);
		int updated = 0;
		for (int i = 0; i < statement.length; i++) {
			if (statement[i] == Statement.SUCCESS_NO_INFO)
				updated += 1;
			else if (statement[i] == Statement.EXECUTE_FAILED)
				updated += 0;
			else
				updated += statement[i];
		}
		return updated >= statement.length;
	}

	/**
	 * <p>
	 * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
	 * </p>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected boolean batch(String sql, Object[][] params) throws SQLException, NamingException {
		logger.debug(log(sql, params));
		int[] statement = getQueryRunner().batch(sql, params);
		int updated = 0;
		for (int i = 0; i < statement.length; i++) {
			if (statement[i] == Statement.SUCCESS_NO_INFO)
				updated += 1;
			else if (statement[i] == Statement.EXECUTE_FAILED)
				updated += 0;
			else
				updated += statement[i];
		}
		return updated >= statement.length;
	}

	/**
	 * <p>
	 * Execute an SQL SELECT query without any replacement parameters.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @param rsh
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh) throws SQLException, NamingException {
		logger.debug(log(sql));
		return getQueryRunner().query(conn, sql, rsh);
	}

	/**
	 * <p>
	 * Execute an SQL SELECT query with replacement parameters.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @param rsh
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params)
			throws SQLException, NamingException {
		logger.debug(log(sql, params));
		return getQueryRunner().query(conn, sql, rsh, params);
	}

	/**
	 * <p>
	 * Executes the given SELECT SQL without any replacement parameters.
	 * </p>
	 * 
	 * @param sql
	 * @param rsh
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected <T> T query(String sql, ResultSetHandler<T> rsh) throws SQLException, NamingException {
		logger.debug(log(sql));
		return getQueryRunner().query(sql, rsh);
	}

	/**
	 * <p>
	 * Executes the given SELECT SQL query and returns a result object.
	 * </p>
	 * 
	 * @param sql
	 * @param rsh
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected <T> T query(String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException, NamingException {
		logger.debug(log(sql, params));
		return getQueryRunner().query(sql, rsh, params);
	}

	/**
	 * <p>
	 * Execute an SQL INSERT, UPDATE, or DELETE query without replacement
	 * parameters.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected boolean update(Connection conn, String sql) throws SQLException, NamingException {
		logger.debug(log(sql));
		return getQueryRunner().update(conn, sql) > 0;
	}

	/**
	 * <p>
	 * Execute an SQL INSERT, UPDATE, or DELETE query.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected boolean update(Connection conn, String sql, Object... params) throws SQLException, NamingException {
		logger.debug(log(sql, params));
		return getQueryRunner().update(conn, sql, params) > 0;
	}

	/**
	 * <p>
	 * Executes the given INSERT, UPDATE, or DELETE SQL statement without any
	 * replacement parameters.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected boolean update(String sql) throws SQLException, NamingException {
		logger.debug(log(sql));
		return getQueryRunner().update(sql) > 0;
	}

	/**
	 * <p>
	 * Executes the given INSERT, UPDATE, or DELETE SQL statement.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected boolean update(String sql, Object... params) throws SQLException, NamingException {
		logger.debug(log(sql, params));
		return getQueryRunner().update(sql, params) > 0;
	}

	protected boolean updateStatement(Connection conn, String sql) throws SQLException {
		logger.debug(log(sql));
		try (Statement stm = conn.createStatement()) {
			return stm.executeUpdate(sql) > 0;
		}
	}

	protected boolean updateStatement(Connection conn, String sql, Object... params) throws SQLException {
		logger.debug(log(sql, params));
		try (PreparedStatement stm = conn.prepareStatement(sql)) {
			fillStatement(stm, params);
			return stm.executeUpdate() > 0;
		}
	}

	protected boolean updateStatement(String sql) throws SQLException, NamingException {
		logger.debug(log(sql));
		try (Connection conn = getConnection()) {
			try (Statement stm = conn.createStatement()) {
				return stm.executeUpdate(sql) > 0;
			}
		}
	}

	protected boolean updateStatement(String sql, Object... params) throws SQLException, NamingException {
		logger.debug(log(sql, params));
		try (Connection conn = getConnection()) {
			try (PreparedStatement stm = conn.prepareStatement(sql)) {
				if (params != null)
					fillStatement(stm, params);
				return stm.executeUpdate() > 0;
			}
		}
	}

	protected void fillStatement(PreparedStatement stm, Object... params) throws SQLException {
		for (int i = 0; i < params.length; i++) {
			logger.debug("param = " + params[i]);
			if (params[i] == null) {
				logger.debug("null");
				stm.setObject(i + 1, null);
			} else if (params[i] instanceof String) {
				logger.debug("String");
				stm.setString(i + 1, (String) params[i]);
			} else if (params[i] instanceof Integer) {
				logger.debug("Integer");
				stm.setInt(i + 1, (int) params[i]);
			} else if (params[i] instanceof Long) {
				logger.debug("Long");
				stm.setLong(i + 1, (long) params[i]);
			} else if (params[i] instanceof Date) {
				logger.debug("Date");
				stm.setDate(i + 1, new java.sql.Date(((Date) params[i]).getTime()));
			} else if (params[i] instanceof BigDecimal) {
				logger.debug("BigDecimal");
				stm.setBigDecimal(i + 1, (BigDecimal) params[i]);
			} else {
				logger.debug("setObject");
				stm.setObject(i + 1, params[i]);
			}
		}
	}

	// ******************
	// ***** Utility ****
	// ******************
	public static final String WHERE_WILDCARD = "%WHERE";

	protected String log(String sql, Object[][] params) {
		StringBuilder msg = new StringBuilder();

		msg.append(" Query: ");
		msg.append(sql);
		msg.append(" Parameters: ");

		for (int i = 0; i < params.length; i++) {
			if (params[i] == null) {
				msg.append("[]");
			} else {
				msg.append(Arrays.deepToString(params[i]));
			}
		}
		return msg.toString();
	}

	protected String log(String sql, Object... params) {
		StringBuilder msg = new StringBuilder();

		msg.append(" Query: ");
		msg.append(sql);
		msg.append(" Parameters: ");

		if (params == null) {
			msg.append("[]");
		} else {
			msg.append(Arrays.deepToString(params));
		}
		return msg.toString();
	}

	public final <T> List<T> queryScalarList(Connection conn, String sql, Object... params)
			throws SQLException, NamingException {
		return query(conn, sql, new ColumnListHandler<T>(), params);
	}

}