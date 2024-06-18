package com.ericsson.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import com.ericsson.dto.ReintentoDTO;

public class ReiniciarReintentosDAO extends GenericDaoBase<ReintentoDTO> {

	public ReiniciarReintentosDAO() {
		logger = Logger.getLogger(getClass());
		setDefaultDataSourceName(TELCASV);
	}

	private static final String SQL_GET_APPLICATION_PARAMETER_VALUE = "SELECT VALOR FROM TC_PORT_PARAM_RETRIES WHERE NOMBRE = ?";
	
	
	public String getGeneralApplicationParameter(String parameterName) throws SQLException, NamingException {
		String result = query(SQL_GET_APPLICATION_PARAMETER_VALUE, new ScalarHandler<>(), parameterName);
		logger.debug("Result: " + result);
		return result;
	}

	private static final String SQL_LIST_HIST = ""
			+ " SELECT PT.TCPORTTRANSACID TCPORTTRANSACID, PT.NUMERO_PORTAR NUMEROPORTAR, DECODE(PT.PORT_IN_OUT, 'I', 'PORTIN', 'O', 'PORTOUT') PORTINOUT, PT.NUMERO_TEMPORAL NUMEROTEMPORAL, PT.TIPO_PORT TIPOPORT, " 
			+ " NVL(PTR.NUEVO_CIP,PT.CIP) CIP, PT.TIPO_PRODUCTO TIPOPRODUCTO, PT.OPERADOR_DONANTE OPERADORDONANTE, PT.TIPO_PRODUCTO_DONANTE TIPOPRODUCTODONANTE, "
			+ " (select SPN_OBSERVACION from TC_PORT_TRANSAC where  TCPORTTRANSACID = (select max(TCPORTTRANSACID) from TC_PORT_TRANSAC where NUMERO_PORTAR = PT.NUMERO_PORTAR))  SPNOBSERVACION,  "
			+ " PT.COD_VENDEDOR CODVENDEDOR, "
			+ " (select ESTADO from TC_PORT_TRANSAC where  TCPORTTRANSACID = (select max(TCPORTTRANSACID) from TC_PORT_TRANSAC where NUMERO_PORTAR = PT.NUMERO_PORTAR)) ESTADO, PT.IMEI IMEI, PT.SWCUSTOMERID SWCUSTOMER, PT.TIP_CLIENTE TIPCLIENTE, " 
			+ " REPLACE(TRIM(PT.DES_NOMBRE || ' ' || PT.DES_NOMBRE2 || ' ' || PT.DES_APELLIDOS || ' ' || PT.DES_APELLIDOS2 || ' ' || PT.DES_APELLIDO3), '  ', ' ')  DESNOMBRE, "
			+ " PT.TIP_DOCUMENTO TIPDOCUMENTO, PT.COD_DOCUMENTO CODDOCUMENTO, PT.ORIGEN ORIGEN, PTR.ESTADO_RETRIES ESTADORETRIES, PTR.CREADO_EL, "
			+ " (select FECHA_PORTACION from TC_PORT_TRANSAC where  TCPORTTRANSACID = (select max(TCPORTTRANSACID) from TC_PORT_TRANSAC where NUMERO_PORTAR = PT.NUMERO_PORTAR)) FECHAPORTACION, "
			+ " PTR.RETRIES RETRIES, PTR.TIPO_CANAL TIPOCANAL, "
			+ " (select NVL(TO_CHAR(FECHA_PORTACION,'MM/DD/YYYY'), 'N') from TC_PORT_TRANSAC where  TCPORTTRANSACID = (select max(TCPORTTRANSACID) from TC_PORT_TRANSAC where NUMERO_PORTAR = PT.NUMERO_PORTAR))  EXITO, "
			+ " PTR.DISTRIBUIDOR, "
			+ " PTR.EJECUTIVO, "
			+ " PTR.PORTID IDPORTABILIDAD "
			+ " FROM TC_PORT_TRANSAC PT INNER JOIN TC_PORT_TRANSAC_RETRIES PTR ON PT.TCPORTTRANSACID = PTR.TCPORTTRANSACID WHERE PTR.ESTADO_RETRIES='PENDIENTE' AND 1 = ? ";

	public List<ReintentoDTO> getHist() throws SQLException, NamingException {
		List<Object> parameters = new ArrayList<>();
		StringBuilder where = new StringBuilder();
		where.append(" ");
		where.append(" AND PTR.CREADO_EL BETWEEN TRUNC(SYSDATE)");
		where.append(" AND TRUNC(SYSDATE + 1)");
		parameters.add(1);
		Object[] params = parameters.toArray(new Object[parameters.size()]);
		String sql = SQL_LIST_HIST_PARAMS.replace(WHERE_WILDCARD, where.toString());
		return getBeanListStatement(sql + " ORDER BY PTR.CREADO_EL DESC", params);
	}

	private static final String SQL_LIST_HIST_PARAMS = SQL_LIST_HIST + WHERE_WILDCARD;

	public List<ReintentoDTO> getHistWithParameters(ReintentoDTO hist) throws SQLException, NamingException {
		List<Object> parameters = new ArrayList<>();
		StringBuilder where = new StringBuilder();
		where.append(" ");
		parameters.add(1);
		if (hist != null) {
			if (hist.getNUMEROPORTAR() != null) {
				where.append(" AND PT.NUMERO_PORTAR = ?");
				parameters.add(hist.getNUMEROPORTAR());
			}			
			if (hist.getFECHA_INICIO() != null) {
				where.append(" AND PTR.CREADO_EL BETWEEN ?");
				parameters.add(new java.sql.Date(hist.getFECHA_INICIO().getTime()));
			}
			if (hist.getFECHA_FIN() != null) {
				where.append(" AND ?");
				parameters.add(new java.sql.Date(hist.getFECHA_FIN().getTime()));
			}
			System.out.println("valor hasta cas---------_>"+hist.getTIPOPRODUCTO());
			if (!"TODOS".equals(hist.getTIPOPRODUCTO())) {
				where.append(" AND PT.TIPO_PRODUCTO = ?");
				parameters.add(hist.getTIPOPRODUCTO());
			}
		}
		Object[] params = parameters.toArray(new Object[parameters.size()]);
		String sql = SQL_LIST_HIST_PARAMS.replace(WHERE_WILDCARD, where.toString());
		return getBeanListStatement(sql + " ORDER BY PTR.CREADO_EL DESC", params);
	}

	private static final String SQL_REINICIAR_REINTENTOS = "UPDATE TC_PORT_TRANSAC_RETRIES SET RETRIES = 0, tipo_reintento ='Manual', MODIFICADO_EL = SYSDATE, CREADO_EL = SYSDATE, FEC_PROXIMA_EJEC = SYSDATE , modificado_Por = ?  "
			+ " WHERE TCPORTTRANSACID = ?";

	public boolean reiniciarReintento(String id,String modificadoPor) {
		logger.debug("Inicia Reinciar Reintento.  numeroPortar: " + id + ". Query: "
				+ SQL_REINICIAR_REINTENTOS);
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection(TELCASV);
			ps = conn.prepareStatement(SQL_REINICIAR_REINTENTOS);
			ps.setString(1, modificadoPor);
			ps.setBigDecimal(2, new BigDecimal(id));
			ps.execute();
			logger.debug("Reintento reiniciado exitosamente");
			return true;
		} catch (Exception e) {
			logger.error("Error reiniciado reintento. ", e);
		} finally {
			DbUtils.closeQuietly(conn, ps, null);
		}
		return false;
	}
	
	
	private static final String SQL_GET_NUMBER_DATA = "SELECT ptr.TCPORTTRANSACID TCPORTTRANSACID, ptr.NUMERO_PORTAR NUMEROPORTAR FROM TC_PORT_TRANSAC_RETRIES ptr WHERE ptr.NUMERO_PORTAR = ?";
	
	public ReintentoDTO getNumberData(String number) {
		logger.debug("getNumberData(), Procesando Number: "+ number);
		try {
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(number);
			String sql = SQL_GET_NUMBER_DATA;
			
			logger.debug("SQL is : " +sql);
			return getBeanStatement(sql, parameters.toArray());
		}catch(Exception e) {
			logger.error("Hubo un error al obtener datos del numero "+ number +" mesage: " + e.getMessage());
			return null;
		}
	}


}
