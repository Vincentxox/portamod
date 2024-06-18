package com.ericsson.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.ericsson.dto.ListaBlancaDTO;

public class ListaBlancaDAO extends GenericDaoBase<ListaBlancaDTO> {

	public ListaBlancaDAO() {
		logger = Logger.getLogger(getClass());
		setDefaultDataSourceName(TELCASV);
	}

	private static final String SQL_LISTA_BLANCA = " SELECT TCLISTABLANCAPORTAID  tcListaBlancaPortaId,    TELEFONO telefono,     ESTADO estado,CREADO_POR creadoPor,CREADO_EL creadoEl, "
			+ " MODIFICADO_POR modificadoPor, MODIFICADO_EL modificadoEl  FROM TC_LISTABLANCA_PORTA WHERE 1 = ? ";

	public List<ListaBlancaDTO> getListaBlanca() throws SQLException, NamingException {
		List<Object> parameters = new ArrayList<>();
		StringBuilder where = new StringBuilder();
		where.append(" ");
		where.append(" AND CREADO_EL BETWEEN TRUNC(SYSDATE)");
		where.append(" AND TRUNC(SYSDATE + 1)");
		parameters.add(1);
		Object[] params = parameters.toArray(new Object[parameters.size()]);
		String sql = SQL_LISTA_BLANCA.replace(WHERE_WILDCARD, where.toString());
		return getBeanListStatement(sql + " ORDER BY CREADO_EL DESC", params);
	}

	private static final String SQL_LISTA_BLANCA_PARAMS = SQL_LISTA_BLANCA + WHERE_WILDCARD;

	public List<ListaBlancaDTO> getListaBlancaWithParameters(ListaBlancaDTO objeto)
			throws SQLException, NamingException {
		List<Object> parameters = new ArrayList<>();
		StringBuilder where = new StringBuilder();
		where.append(" ");
		parameters.add(1);
		if (objeto != null) {
			if (objeto.getTelefono() != null) {
				where.append(" AND TELEFONO like ('%" + objeto.getTelefono() + "%')");
			}
		}
		Object[] params = parameters.toArray(new Object[parameters.size()]);
		String sql = SQL_LISTA_BLANCA_PARAMS.replace(WHERE_WILDCARD, where.toString());
		return getBeanListStatement(sql + " ORDER BY CREADO_EL DESC", params);
	}

	private static final String SQL_INSERT_LISTA_BLANCA = "INSERT INTO TC_LISTABLANCA_PORTA (TCLISTABLANCAPORTAID,TELEFONO,ESTADO,CREADO_POR,CREADO_EL) VALUES(TC_LISTABLANCA_PORTA_SQ.NEXTVAL,?,'ALTA',?,SYSDATE)";

	public boolean crearRegistro(ListaBlancaDTO objeto) throws NamingException, SQLException {
		logger.debug("Se creará el registro: " + objeto.getTelefono());

		Object[] params = new Object[] { objeto.getTelefono(), objeto.getCreadoPor() };
		try {
			return update(SQL_INSERT_LISTA_BLANCA, params);
		} catch (SQLException e) {
			//si el telefono ya existe en la tabla se cambia el estado a ALTA
			return updateRegistroTelefono(objeto);
		} 

	}
	
	private static final String SQL_UPDATE_LISTA_BLANCA = "UPDATE TC_LISTABLANCA_PORTA SET ESTADO = ?, MODIFICADO_POR = ? WHERE TCLISTABLANCAPORTAID = ?";

	public boolean updateRegistro(ListaBlancaDTO objeto) throws SQLException, NamingException {
		logger.debug("Se modificará  el registro: " + objeto.getTelefono());

		Object[] params = new Object[] { objeto.getEstado(), objeto.getModificadoPor(),objeto.getTcListaBlancaPortaId() };
		return update(SQL_UPDATE_LISTA_BLANCA, params);

	}
	
	private static final String SQL_UPDATE_LISTA_BLANCA_TELEFONO = "UPDATE TC_LISTABLANCA_PORTA SET ESTADO = 'ALTA', MODIFICADO_POR = ? WHERE TELEFONO = ?";

	public boolean updateRegistroTelefono(ListaBlancaDTO objeto) throws SQLException, NamingException {
		logger.debug("Se modificará  el registro: " + objeto.getTelefono());

		Object[] params = new Object[] { objeto.getCreadoPor(),objeto.getTelefono() };
		return update(SQL_UPDATE_LISTA_BLANCA_TELEFONO, params);

	}
	
}
