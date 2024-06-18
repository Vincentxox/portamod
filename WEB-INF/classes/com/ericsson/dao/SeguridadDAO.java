package com.ericsson.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

public class SeguridadDAO extends GenericDaoBase<String> {

	public SeguridadDAO() {
		logger = Logger.getLogger(getClass());
		setDefaultDataSourceName(SEGURIDAD);
	}

	private static final String SQL_GET_PERFIL_USUARIO = " SELECT SECPERFILID  FROM SEC_PERFIL A, SEC_PERMISO_USUARIO B WHERE A.SECPERFILID = "
			+ " B.SECPERFILID_PERMISO AND A.SECAPLICACIONID= ? AND B.SECUSUARIOID= ? ";

	public BigDecimal getPerfilUsuario(Connection conn, BigDecimal aplicacion, BigDecimal codUsuario)
			throws SQLException, NamingException {
		return query(conn, SQL_GET_PERFIL_USUARIO, new ScalarHandler<>(), aplicacion, codUsuario);
	}

	private static final String SQL_GET_PERMISOS_ELEMENTOS = " SELECT B.NOMBRE FROM SEC_PERMISO_PERFIL A, SEC_ELEMENTO B"
			+ " WHERE A.SECELEMENTOID=B.SECELEMENTOID AND A.SECPERFILID=? ORDER BY 1";

	public List<String> getPermisosElementos(Connection conn, String perfil) throws SQLException, NamingException {
		return queryScalarList(conn, SQL_GET_PERMISOS_ELEMENTOS, perfil);

	}

}
