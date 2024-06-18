package com.ericsson.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

public class GenericDaoBase<T> extends DaoBase {

	public GenericDaoBase() {
		logger = Logger.getLogger(getClass());
	}

	private Class<T> recordClass;
	private BeanHandler<T> beanHandler;
	private BeanListHandler<T> beanListHandler;
	private MapHandler mapHandler;
	private MapListHandler mapListHandler;
	private ScalarHandler<BigDecimal> bigDecimalHandler;

	@SuppressWarnings("unchecked")
	private Class<T> getRecordClass() {
		if (recordClass == null) {
			Type type = getClass().getGenericSuperclass();
			if (type instanceof ParameterizedType) {
				ParameterizedType paramType = (ParameterizedType) type;
				recordClass = (Class<T>) paramType.getActualTypeArguments()[0];
			} else {
				logger.error("Could not guess record class by reflection");
			}
		}
		return recordClass;
	}

	protected void setConvert(RowProcessor convert) {
		this.beanHandler = new BeanHandler<>(getRecordClass(), convert);
		this.beanListHandler = new BeanListHandler<>(getRecordClass(), convert);
	}

	private BeanHandler<T> getBeanHandler() {
		if (beanHandler == null) {
			beanHandler = new BeanHandler<>(getRecordClass());
		}
		return beanHandler;
	}

	private BeanListHandler<T> getBeanListHandler() {
		if (beanListHandler == null) {
			beanListHandler = new BeanListHandler<>(getRecordClass());
		}
		return beanListHandler;
	}

	private MapHandler getMapHandler() {
		if (mapHandler == null) {
			mapHandler = new MapHandler();
		}
		return mapHandler;
	}

	private MapListHandler getMapListHandler() {
		if (mapListHandler == null) {
			mapListHandler = new MapListHandler();
		}
		return mapListHandler;
	}

	private ScalarHandler<BigDecimal> getBigDecimalHandler() {
		if (bigDecimalHandler == null) {
			bigDecimalHandler = new ScalarHandler<>();
		}
		return bigDecimalHandler;
	}

	/**
	 * <p>
	 * Execute an SQL SELECT query without any replacement parameters and convert
	 * the first row of the <code>ResultSet</code> into a bean.
	 * <p>
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected T getBean(Connection conn, String sql) throws SQLException, NamingException {
		return query(conn, sql, getBeanHandler());
	}

	/**
	 * <p>
	 * Execute an SQL SELECT query with replacement parameters and convert the first
	 * row of the <code>ResultSet</code> into a bean.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected T getBean(Connection conn, String sql, Object... params) throws SQLException, NamingException {
		return query(conn, sql, getBeanHandler(), params);
	}

	/**
	 * <p>
	 * Executes the given SELECT SQL without any replacement parameters and convert
	 * the first row of the <code>ResultSet</code> into a bean.
	 * </p>
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected T getBean(String sql) throws SQLException, NamingException {
		return query(sql, getBeanHandler());
	}

	/**
	 * <p>
	 * Executes the given SELECT SQL query and convert the first row of the
	 * <code>ResultSet</code> into a bean.
	 * </p>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected T getBean(String sql, Object... params) throws SQLException, NamingException {
		return query(sql, getBeanHandler(), params);
	}

	/**
	 * <p>
	 * Execute an SQL SELECT query without any replacement parameters and convert
	 * the whole <code>ResultSet</code> into a <code>List</code> of beans.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected List<T> getBeanList(Connection conn, String sql) throws SQLException, NamingException {
		return query(conn, sql, getBeanListHandler());
	}

	/**
	 * <p>
	 * Execute an SQL SELECT query with replacement parameters and convert the whole
	 * <code>ResultSet</code> into a <code>List</code> of beans.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected List<T> getBeanList(Connection conn, String sql, Object... params) throws SQLException, NamingException {
		return query(conn, sql, getBeanListHandler(), params);
	}

	/**
	 * <p>
	 * Executes the given SELECT SQL without any replacement parameters and convert
	 * the whole <code>ResultSet</code> into a <code>List</code> of beans.
	 * </p>
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected List<T> getBeanList(String sql) throws SQLException, NamingException {
		return query(sql, getBeanListHandler());
	}

	/**
	 * <p>
	 * Executes the given SELECT SQL query and convert the whole
	 * <code>ResultSet</code> into a <code>List</code> of beans.
	 * </p>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected List<T> getBeanList(String sql, Object... params) throws SQLException, NamingException {
		return query(sql, getBeanListHandler(), params);
	}

	/**
	 * <p>
	 * Execute an SQL SELECT query without any replacement parameters and converts
	 * the first row in the <code>ResultSet</code> into a <code>Map</code>.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected Map<String, Object> getMap(Connection conn, String sql) throws SQLException, NamingException {
		return query(conn, sql, getMapHandler());
	}

	/**
	 * <p>
	 * Execute an SQL SELECT query with replacement parameters and converts the
	 * first row in the <code>ResultSet</code> into a <code>Map</code>.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected Map<String, Object> getMap(Connection conn, String sql, Object... params)
			throws SQLException, NamingException {
		return query(conn, sql, getMapHandler(), params);
	}

	/**
	 * <p>
	 * Executes the given SELECT SQL without any replacement parameters and converts
	 * the first row in the <code>ResultSet</code> into a <code>Map</code>.
	 * </p>
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected Map<String, Object> getMap(String sql) throws SQLException, NamingException {
		return query(sql, getMapHandler());
	}

	/**
	 * <p>
	 * Executes the given SELECT SQL query and converts the first row in the
	 * <code>ResultSet</code> into a <code>Map</code>.
	 * </p>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected Map<String, Object> getMap(String sql, Object... params) throws SQLException, NamingException {
		return query(sql, getMapHandler(), params);
	}

	/**
	 * <p>
	 * Execute an SQL SELECT query without any replacement parameters and converts a
	 * <code>ResultSet</code> into a <code>List</code> of <code>Maps</code>.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected List<Map<String, Object>> getMapList(Connection conn, String sql) throws SQLException, NamingException {
		return query(conn, sql, getMapListHandler());
	}

	/**
	 * <p>
	 * Execute an SQL SELECT query with replacement parameters and converts a
	 * <code>ResultSet</code> into a <code>List</code> of <code>Maps</code>.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected List<Map<String, Object>> getMapList(Connection conn, String sql, Object... params)
			throws SQLException, NamingException {
		return query(conn, sql, getMapListHandler(), params);
	}

	/**
	 * <p>
	 * Executes the given SELECT SQL without any replacement parameters and converts
	 * a <code>ResultSet</code> into a <code>List</code> of <code>Maps</code>.
	 * </p>
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected List<Map<String, Object>> getMapList(String sql) throws SQLException, NamingException {
		return query(sql, getMapListHandler());
	}

	/**
	 * <p>
	 * Executes the given SELECT SQL query and converts a <code>ResultSet</code>
	 * into a <code>List</code> of <code>Maps</code>.
	 * </p>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected List<Map<String, Object>> getMapList(String sql, Object... params) throws SQLException, NamingException {
		return query(sql, getMapListHandler(), params);
	}

	/**
	 * <p>
	 * Execute an SQL SELECT query without any replacement parameters and converts
	 * one <code>ResultSet</code> column into an <code>BigDecimal</code>.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected BigDecimal getBigDecimal(Connection conn, String sql) throws SQLException, NamingException {
		return query(conn, sql, getBigDecimalHandler());
	}

	/**
	 * <p>
	 * Execute an SQL SELECT query with replacement parameters and converts one
	 * <code>ResultSet</code> column into an <code>BigDecimal</code>.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected BigDecimal getBigDecimal(Connection conn, String sql, Object... params)
			throws SQLException, NamingException {
		return query(conn, sql, getBigDecimalHandler(), params);
	}

	/**
	 * <p>
	 * Executes the given SELECT SQL without any replacement parameters and converts
	 * one <code>ResultSet</code> column into an <code>BigDecimal</code>.
	 * </p>
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected BigDecimal getBigDecimal(String sql) throws SQLException, NamingException {
		return query(sql, getBigDecimalHandler());
	}

	/**
	 * <p>
	 * Executes the given SELECT SQL query and converts one <code>ResultSet</code>
	 * column into an <code>BigDecimal</code>.
	 * </p>
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	protected BigDecimal getBigDecimal(String sql, Object... params) throws SQLException, NamingException {
		return query(sql, getBigDecimalHandler(), params);
	}

	protected T getBeanStatement(Connection conn, String sql) throws SQLException {
		logger.debug(log(sql));
		try (Statement stm = conn.createStatement()) {
			try (ResultSet rs = stm.executeQuery(sql)) {
				return getBeanHandler().handle(rs);
			}
		}
	}

	protected T getBeanStatement(Connection conn, String sql, Object... params) throws SQLException {
		logger.debug(log(sql, params));
		try (PreparedStatement stm = conn.prepareStatement(sql)) {
			fillStatement(stm, params);
			try (ResultSet rs = stm.executeQuery()) {
				return getBeanHandler().handle(rs);
			}
		}
	}

	protected T getBeanStatement(String sql) throws SQLException, NamingException {
		logger.debug(log(sql));
		try (Connection conn = getConnection()) {
			try (Statement stm = conn.createStatement()) {
				try (ResultSet rs = stm.executeQuery(sql)) {
					return getBeanHandler().handle(rs);
				}
			}
		}
	}

	protected T getBeanStatement(String sql, Object... params) throws SQLException, NamingException {
		logger.debug(log(sql, params));
		try (Connection conn = getConnection()) {
			try (PreparedStatement stm = conn.prepareStatement(sql)) {
				fillStatement(stm, params);
				try (ResultSet rs = stm.executeQuery()) {
					return getBeanHandler().handle(rs);
				}
			}
		}
	}

	protected List<T> getBeanListStatement(Connection conn, String sql) throws SQLException {
		logger.debug(log(sql));
		try (Statement stm = conn.createStatement()) {
			try (ResultSet rs = stm.executeQuery(sql)) {
				return getBeanListHandler().handle(rs);
			}
		}
	}

	protected List<T> getBeanListStatement(Connection conn, String sql, Object... params) throws SQLException {
		logger.debug(log(sql, params));
		try (PreparedStatement stm = conn.prepareStatement(sql)) {
			fillStatement(stm, params);
			try (ResultSet rs = stm.executeQuery()) {
				return getBeanListHandler().handle(rs);
			}
		}
	}

	protected List<T> getBeanListStatement(String sql) throws SQLException, NamingException {
		logger.debug(log(sql));
		try (Connection conn = getConnection()) {
			try (Statement stm = conn.createStatement()) {
				try (ResultSet rs = stm.executeQuery(sql)) {
					return getBeanListHandler().handle(rs);
				}
			}
		}
	}

	protected List<T> getBeanListStatement(String sql, Object... params) throws SQLException, NamingException {
		logger.debug(log(sql, params));
		try (Connection conn = getConnection()) {
			try (PreparedStatement stm = conn.prepareStatement(sql)) {
				fillStatement(stm, params);
				try (ResultSet rs = stm.executeQuery()) {
					return getBeanListHandler().handle(rs);
				}
			}
		}
	}
}