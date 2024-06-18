package com.ericsson.dto;

import java.util.Date;

public class PortoutDTO {

	private String TCPORTPORTARECHAZADALOGID;
	private String NUMERO_PORTAR;
	private String OBSERVACIONES;
	private String CREADO_POR;
	private String CREADO_EL;
	private Date FECHA_INICIO;
	private Date FECHA_FIN;

	public String getTCPORTPORTARECHAZADALOGID() {
		return TCPORTPORTARECHAZADALOGID;
	}

	public void setTCPORTPORTARECHAZADALOGID(String tCPORTPORTARECHAZADALOGID) {
		TCPORTPORTARECHAZADALOGID = tCPORTPORTARECHAZADALOGID;
	}

	public String getNUMERO_PORTAR() {
		return NUMERO_PORTAR;
	}

	public void setNUMERO_PORTAR(String nUMERO_PORTAR) {
		NUMERO_PORTAR = nUMERO_PORTAR;
	}

	public String getOBSERVACIONES() {
		return OBSERVACIONES;
	}

	public void setOBSERVACIONES(String oBSERVACIONES) {
		OBSERVACIONES = oBSERVACIONES;
	}

	public String getCREADO_POR() {
		return CREADO_POR;
	}

	public void setCREADO_POR(String cREADO_POR) {
		CREADO_POR = cREADO_POR;
	}

	public String getCREADO_EL() {
		return CREADO_EL;
	}

	public void setCREADO_EL(String cREADO_EL) {
		CREADO_EL = cREADO_EL;
	}

	public Date getFECHA_INICIO() {
		return FECHA_INICIO;
	}

	public void setFECHA_INICIO(Date fECHA_INICIO) {
		FECHA_INICIO = fECHA_INICIO;
	}

	public Date getFECHA_FIN() {
		return FECHA_FIN;
	}

	public void setFECHA_FIN(Date fECHA_FIN) {
		FECHA_FIN = fECHA_FIN;
	}

	@Override
	public String toString() {
		return "PortoutDTO [TCPORTPORTARECHAZADALOGID=" + TCPORTPORTARECHAZADALOGID + ", NUMERO_PORTAR=" + NUMERO_PORTAR
				+ ", OBSERVACIONES=" + OBSERVACIONES + ", CREADO_POR=" + CREADO_POR + ", CREADO_EL=" + CREADO_EL
				+ ", FECHA_INICIO=" + FECHA_INICIO + ", FECHA_FIN=" + FECHA_FIN + "]";
	}

}
