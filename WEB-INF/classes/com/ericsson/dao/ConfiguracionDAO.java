package com.ericsson.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.ericsson.dto.ConfiguracionDTO;

public class ConfiguracionDAO extends GenericDaoBase<ConfiguracionDTO> {

	public ConfiguracionDAO() {
		logger = Logger.getLogger(getClass());
		setDefaultDataSourceName(TELCASV);
	}

	private static final String SQL_GET_TC_PORT_PARAM_RETRIES = ""
			+ "SELECT TCPORTPARAMRETRIESID, AGRUPACION, NOMBRE, DESCRIPCION, VALOR, CREADO_EL, CREADO_POR, VALOR2, REINICIAR, DECODE(REINICIAR, 0, 'NO', 1, 'SI') REINICIARSTR"
			+ " FROM TC_PORT_PARAM_RETRIES WHERE NOMBRE IN ('DIAS_ALTA','RETRIES', 'EMAIL','POSTPAGO_GESTIONADO_RETRIES', 'POSTPAGO_AUTOGESTIONADO_RETRIES') AND 1 = ? " + WHERE_WILDCARD;

	public List<ConfiguracionDTO> getConfWithParameters(ConfiguracionDTO conf) throws SQLException, NamingException {
		List<Object> parameters = new ArrayList<>();
		StringBuilder where = new StringBuilder();
		where.append(" ");
		parameters.add(1);
		if (conf != null) {
			if (conf.getAGRUPACION() != null)
				where.append(" AND UPPER(AGRUPACION) LIKE '%" + conf.getAGRUPACION() + "%'");
			if (conf.getNOMBRE() != null)
				where.append(" AND UPPER(NOMBRE) LIKE '%" + conf.getNOMBRE() + "%'");
		}
		Object[] params = parameters.toArray(new Object[parameters.size()]);
		String sql = SQL_GET_TC_PORT_PARAM_RETRIES.replace(WHERE_WILDCARD, where.toString());
		return getBeanListStatement(sql + " ORDER BY CREADO_EL DESC", params);
	}

	private static final String SQL_UPDATE_TC_PORT_PARAM_RETRIES = "UPDATE TC_PORT_PARAM_RETRIES SET" + WHERE_WILDCARD;

	public void modificarConfiguracion(String valor, String valor2, BigDecimal TCPORTPARAMRETRIESID)
			throws SQLException, NamingException {
		logger.debug(
				"Se modificará el registro: " + TCPORTPARAMRETRIESID + ". Valor: " + valor + ". Valor2: " + valor2);
		List<Object> parameters = new ArrayList<>();
		StringBuilder where = new StringBuilder();
		boolean valorNoNull = false;
		if (valor != null && !valor.isEmpty()) {
			where.append(" VALOR = ?");
			parameters.add(valor);
			valorNoNull = true;
		}
		if (valor2 != null && !valor2.isEmpty()) {
			if (valorNoNull)
				where.append(" , VALOR2 = ?");
			else
				where.append(" VALOR2 = ?");
			parameters.add(valor2);
		}
		where.append(" WHERE TCPORTPARAMRETRIESID = ?");
		parameters.add(TCPORTPARAMRETRIESID);
		String sql = SQL_UPDATE_TC_PORT_PARAM_RETRIES.replace(WHERE_WILDCARD, where.toString());
		update(sql, parameters.toArray(new Object[parameters.size()]));
	}

}
