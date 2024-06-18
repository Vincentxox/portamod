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
import org.zkoss.util.logging.Log;

import com.ericsson.dto.ReintentoDTO;

public class ReporteReintentosPortaDAO extends GenericDaoBase<ReintentoDTO> {

	public ReporteReintentosPortaDAO() {
		logger = Logger.getLogger(getClass());
		setDefaultDataSourceName(TELCASV);
	}

	private static final String SQL_GET_APPLICATION_PARAMETER_VALUE = "SELECT VALOR FROM TC_IB_PARAMETROS_CONFIGURACION WHERE NOMBRE = ?";

	public String getGeneralApplicationParameter(String parameterName) throws SQLException, NamingException {
		String result = query(SQL_GET_APPLICATION_PARAMETER_VALUE, new ScalarHandler<>(), parameterName);
		logger.debug("Result: " + result);
		return result;
	}

	private static final String SQL_GET_CONSEC_TRANS_IB = "SELECT DG_CONS_TRANS_SEQ.NEXTVAL FROM DUAL";

	public BigDecimal getConsecutivoTransaccion() throws SQLException, NamingException {
		BigDecimal result = query(SQL_GET_CONSEC_TRANS_IB, new ScalarHandler<>());
		logger.debug("getTransaccionId: " + result);
		return result;
	}

	private static final String SQL_LIST_HIST = ""
			+ " SELECT PT.TCPORTTRANSACID TCPORTTRANSACID, PT.NUMERO_PORTAR NUMEROPORTAR, DECODE(PT.PORT_IN_OUT, 'I', 'PORTIN', 'O', 'PORTOUT') PORTINOUT, PT.NUMERO_TEMPORAL NUMEROTEMPORAL, PT.TIPO_PORT TIPOPORT, " 
			+ " NVL(PTR.NUEVO_CIP,PT.CIP) CIP, PT.TIPO_PRODUCTO TIPOPRODUCTO, PT.OPERADOR_DONANTE OPERADORDONANTE, PT.TIPO_PRODUCTO_DONANTE TIPOPRODUCTODONANTE, "
			+ " (select SPN_OBSERVACION from TC_PORT_TRANSAC where  TCPORTTRANSACID = (select max(TCPORTTRANSACID) from TC_PORT_TRANSAC where NUMERO_PORTAR = PT.NUMERO_PORTAR))  SPNOBSERVACION,  "
			+ " PT.COD_VENDEDOR CODVENDEDOR, "
			+ " (select ESTADO from TC_PORT_TRANSAC where  TCPORTTRANSACID = (select max(TCPORTTRANSACID) from TC_PORT_TRANSAC where NUMERO_PORTAR = PT.NUMERO_PORTAR)) ESTADO, PT.IMEI IMEI, PT.SWCUSTOMERID SWCUSTOMER, PT.TIP_CLIENTE TIPCLIENTE, " 
			+ " REPLACE(TRIM(PT.DES_NOMBRE || ' ' || PT.DES_NOMBRE2 || ' ' || PT.DES_APELLIDOS || ' ' || PT.DES_APELLIDOS2 || ' ' || PT.DES_APELLIDO3), '  ', ' ')  DESNOMBRE, "
			+ " PT.TIP_DOCUMENTO TIPDOCUMENTO, PT.COD_DOCUMENTO CODDOCUMENTO, PT.ORIGEN ORIGEN, PTR.ESTADO_RETRIES ESTADORETRIES, PTR.CREADO_EL, PTR.DISTRIBUIDOR DISTRIBUIDOR, PTR.EJECUTIVO EJECUTIVO, PTR.PORTID IDPORTABILIDAD, "
			+ " (select FECHA_PORTACION from TC_PORT_TRANSAC where  TCPORTTRANSACID = (select max(TCPORTTRANSACID) from TC_PORT_TRANSAC where NUMERO_PORTAR = PT.NUMERO_PORTAR)) FECHAPORTACION, "
			+ " PTR.RETRIES RETRIES, "
			+ " (select NVL(TO_CHAR(FECHA_PORTACION,'MM/DD/YYYY'), 'N') from TC_PORT_TRANSAC where  TCPORTTRANSACID = (select max(TCPORTTRANSACID) from TC_PORT_TRANSAC where NUMERO_PORTAR = PT.NUMERO_PORTAR))  EXITO " 
			+ " FROM TC_PORT_TRANSAC PT INNER JOIN TC_PORT_TRANSAC_RETRIES PTR ON PT.TCPORTTRANSACID = PTR.TCPORTTRANSACID WHERE 1 = ? ";

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
			if (hist.getESTADO() != null) {
				where.append(" AND ESTADO = ?");
				parameters.add(hist.getESTADO());
			}
			if (hist.getFECHA_INICIO() != null) {
				where.append(" AND PTR.CREADO_EL BETWEEN ?");
				parameters.add(new java.sql.Date(hist.getFECHA_INICIO().getTime()));
			}
			if (hist.getFECHA_FIN() != null) {
				where.append(" AND ?");
				parameters.add(new java.sql.Date(hist.getFECHA_FIN().getTime()));
			}
			logger.debug("Tipo Producto es en hist es: "+hist.getTIPOPRODUCTO());
			if (hist.getTIPOPRODUCTO() != null) {
				where.append(" AND PT.TIPO_PRODUCTO = ?");
				parameters.add(hist.getTIPOPRODUCTO().toUpperCase());
			}
		}
		Object[] params = parameters.toArray(new Object[parameters.size()]);
		String sql = SQL_LIST_HIST_PARAMS.replace(WHERE_WILDCARD, where.toString());
		return getBeanListStatement(sql + " ORDER BY PTR.CREADO_EL DESC", params);
	}

	private static final String SQL_CALCELAR_REINTENTOS = "UPDATE TC_PORT_TRANSAC_RETRIES SET ESTADO_RETRIES = 'CANCELADO', MODIFICADO_EL = SYSDATE,"
			+ " MODIFICADO_POR = ? WHERE TCPORTTRANSACID = (SELECT TCPORTTRANSACID FROM (SELECT TCPORTTRANSACID FROM TC_PORT_TRANSAC_RETRIES"
			+ " WHERE NUMERO_PORTAR = ? ORDER BY CREADO_EL DESC) WHERE ROWNUM = 1)";

	public boolean cancelarReintento(String usuario, String numeroPortar) {
		logger.debug("Inicia cancelarReintento. Usuario: " + usuario + ". numeroPortar: " + numeroPortar + ". Query: "
				+ SQL_CALCELAR_REINTENTOS);
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection(TELCASV);
			ps = conn.prepareStatement(SQL_CALCELAR_REINTENTOS);
			ps.setString(1, usuario);
			ps.setBigDecimal(2, new BigDecimal(numeroPortar));
			ps.execute();
			logger.debug("Reintento cancelado exitosamente");
			return true;
		} catch (Exception e) {
			logger.error("Error cancelando reintento. ", e);
		} finally {
			DbUtils.closeQuietly(conn, ps, null);
		}
		return false;
	}

	private static final String SQL_OBTENER_ESTADO_REINTENTO = "SELECT ESTADO_RETRIES FROM (SELECT ESTADO_RETRIES FROM TC_PORT_TRANSAC_RETRIES"
			+ " WHERE NUMERO_PORTAR = ? ORDER BY CREADO_EL DESC) WHERE ROWNUM = 1";

	public String obtenerEstadoReintento(String numeroPortar) throws SQLException, NamingException {
		String result = query(SQL_OBTENER_ESTADO_REINTENTO, new ScalarHandler<>(), numeroPortar);
		logger.debug("Result: " + result);
		return result;
	}

}
