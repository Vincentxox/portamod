package com.ericsson.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.ericsson.dto.PortoutDTO;

public class ReportePortoutDAO extends GenericDaoBase<PortoutDTO> {

	public ReportePortoutDAO() {
		logger = Logger.getLogger(getClass());
		setDefaultDataSourceName(TELCASV);
	}

	private static final String SQL_LIST_HIST = ""
			+ " SELECT TCPORTPORTARECHAZADALOGID, NUMERO_PORTAR, OBSERVACIONES, CREADO_POR,"
			+ " TO_CHAR(CREADO_EL, 'DD/MM/YYYY HH24:MI:SS') CREADO_EL FROM TC_PORT_PORTA_RECHAZADA_LOG WHERE 1 = ?";

	public List<PortoutDTO> getHist() throws SQLException, NamingException {
		List<Object> parameters = new ArrayList<>();
		StringBuilder where = new StringBuilder();
		where.append(" AND CREADO_EL BETWEEN TRUNC(SYSDATE)");
		where.append(" AND TRUNC(SYSDATE + 1)");
		parameters.add(1);
		Object[] params = parameters.toArray(new Object[parameters.size()]);
		String sql = SQL_LIST_HIST_PARAMS.replace(WHERE_WILDCARD, where.toString());
		return getBeanListStatement(sql + " ORDER BY CREADO_EL DESC", params);
	}

	private static final String SQL_LIST_HIST_PARAMS = SQL_LIST_HIST + WHERE_WILDCARD;

	public List<PortoutDTO> getHistWithParameters(PortoutDTO dto) throws SQLException, NamingException {
		List<Object> parameters = new ArrayList<>();
		StringBuilder where = new StringBuilder();
		parameters.add(1);
		if (dto != null) {
			if (dto.getNUMERO_PORTAR() != null) {
				where.append(" AND NUMERO_PORTAR = ?");
				parameters.add(dto.getNUMERO_PORTAR());
			}
			if (dto.getCREADO_POR() != null) {
				where.append(" AND CREADO_POR = ?");
				parameters.add(dto.getCREADO_POR());
			}
			if (dto.getFECHA_INICIO() != null) {
				where.append(" AND CREADO_EL BETWEEN ?");
				parameters.add(new java.sql.Date(dto.getFECHA_INICIO().getTime()));
			}
			if (dto.getFECHA_FIN() != null) {
				where.append(" AND ?");
				parameters.add(new java.sql.Date(dto.getFECHA_FIN().getTime()));
			}
		}
		Object[] params = parameters.toArray(new Object[parameters.size()]);
		String sql = SQL_LIST_HIST_PARAMS.replace(WHERE_WILDCARD, where.toString());
		return getBeanListStatement(sql + " ORDER BY CREADO_EL DESC", params);
	}

}
