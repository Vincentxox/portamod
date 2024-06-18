package com.ericsson.dto;

import java.math.BigDecimal;
import java.util.Date;

public class ConfiguracionDTO {

	private BigDecimal TCPORTPARAMRETRIESID;
	private String AGRUPACION;
	private String NOMBRE;
	private String DESCRIPCION;
	private String VALOR;
	private Date CREADO_EL;
	private String CREADO_POR;
	private String VALOR2;
	private BigDecimal REINICIAR;
	private String REINICIARSTR;

	public BigDecimal getTCPORTPARAMRETRIESID() {
		return TCPORTPARAMRETRIESID;
	}

	public void setTCPORTPARAMRETRIESID(BigDecimal tCPORTPARAMRETRIESID) {
		TCPORTPARAMRETRIESID = tCPORTPARAMRETRIESID;
	}

	public String getAGRUPACION() {
		return AGRUPACION;
	}

	public void setAGRUPACION(String aGRUPACION) {
		AGRUPACION = aGRUPACION;
	}

	public String getNOMBRE() {
		return NOMBRE;
	}

	public void setNOMBRE(String nOMBRE) {
		NOMBRE = nOMBRE;
	}

	public String getDESCRIPCION() {
		return DESCRIPCION;
	}

	public void setDESCRIPCION(String dESCRIPCION) {
		DESCRIPCION = dESCRIPCION;
	}

	public String getVALOR() {
		return VALOR;
	}

	public void setVALOR(String vALOR) {
		VALOR = vALOR;
	}

	public Date getCREADO_EL() {
		return CREADO_EL;
	}

	public void setCREADO_EL(Date cREADO_EL) {
		CREADO_EL = cREADO_EL;
	}

	public String getCREADO_POR() {
		return CREADO_POR;
	}

	public void setCREADO_POR(String cREADO_POR) {
		CREADO_POR = cREADO_POR;
	}

	public String getVALOR2() {
		return VALOR2;
	}

	public void setVALOR2(String vALOR2) {
		VALOR2 = vALOR2;
	}

	public BigDecimal getREINICIAR() {
		return REINICIAR;
	}

	public void setREINICIAR(BigDecimal rEINICIAR) {
		REINICIAR = rEINICIAR;
	}

	public String getREINICIARSTR() {
		return REINICIARSTR;
	}

	public void setREINICIARSTR(String rEINICIARSTR) {
		REINICIARSTR = rEINICIARSTR;
	}

	@Override
	public String toString() {
		return "ConfiguracionDTO [TCPORTPARAMRETRIESID=" + TCPORTPARAMRETRIESID + ", AGRUPACION=" + AGRUPACION
				+ ", NOMBRE=" + NOMBRE + ", DESCRIPCION=" + DESCRIPCION + ", VALOR=" + VALOR + ", CREADO_EL="
				+ CREADO_EL + ", CREADO_POR=" + CREADO_POR + ", VALOR2=" + VALOR2 + ", REINICIAR=" + REINICIAR
				+ ", REINICIARSTR=" + REINICIARSTR + "]";
	}

}
