package com.ericsson.viewModel;


import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;

public class TipoProductoVM {
	
	private String tipoProducto;
	
	
	@Init
	public void init() {
		Session session = Executions.getCurrent().getSession();
		tipoProducto = (String) session.getAttribute("tipoProducto");
		setTipoProducto("todas");
	}
	
	@Command
	public void seleccionarTipoProducto() {
		Session session = Executions.getCurrent().getSession();
		session.setAttribute("tipoProducto", tipoProducto);
	}

	public TipoProductoVM(String tipoProducto) {
		super();
		this.tipoProducto = "todas";
	}

	public String getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(String tipoProducto) {
		this.tipoProducto = tipoProducto;
	}
	
	
}
