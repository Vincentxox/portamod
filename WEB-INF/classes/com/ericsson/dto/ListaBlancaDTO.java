package com.ericsson.dto;

import java.math.BigDecimal;
import java.util.Date;

public class ListaBlancaDTO {
	
	
	private BigDecimal tcListaBlancaPortaId;
	private BigDecimal telefono;
	private String estado;
	private String creadoPor;
	private Date creadoEl;
	private String modificadoPor;
	private Date modificadoEl;
	public BigDecimal getTcListaBlancaPortaId() {
		return tcListaBlancaPortaId;
	}
	public void setTcListaBlancaPortaId(BigDecimal tcListaBlancaPortaId) {
		this.tcListaBlancaPortaId = tcListaBlancaPortaId;
	}
	public BigDecimal getTelefono() {
		return telefono;
	}
	public void setTelefono(BigDecimal telefono) {
		this.telefono = telefono;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getCreadoPor() {
		return creadoPor;
	}
	public void setCreadoPor(String creadoPor) {
		this.creadoPor = creadoPor;
	}
	public Date getCreadoEl() {
		return creadoEl;
	}
	public void setCreadoEl(Date creadoEl) {
		this.creadoEl = creadoEl;
	}
	public String getModificadoPor() {
		return modificadoPor;
	}
	public void setModificadoPor(String modificadoPor) {
		this.modificadoPor = modificadoPor;
	}
	public Date getModificadoEl() {
		return modificadoEl;
	}
	public void setModificadoEl(Date modificadoEl) {
		this.modificadoEl = modificadoEl;
	}
	
	
	

}
