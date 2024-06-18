package com.ericsson.base;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Window;

import com.consystec.seguridad.orm.SecUsuario;
import com.ericsson.dao.DaoBase;

public class ApplicationComposer extends ComposerBase {

	private static final Logger log = Logger.getLogger(ApplicationComposer.class);

	private static final long serialVersionUID = 1L;
	private Label lblUser;
	private Label lblDate;
	private Menuitem menuItemListado;
	private Menuitem menuItemConfiguraciones;
	private Menuitem menuItemListaBlanca;
	private Menuitem menuItemPortOut;
	private Menuitem menuItemCancelar;
	private Menuitem menuItemReintentar;

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		menuItemsVisible();
	}

	public ApplicationComposer() {
		this.defaultDataSourceName = DaoBase.SEGURIDAD;
	}

	private void menuItemsVisible() {
		mostrarMenus(false);
		if (desktop.getSession().getAttribute("permisosPerfil") != null) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			List<String> permisos = (List) desktop.getSession().getAttribute("permisosPerfil");
			log.debug("[Permisos]-[" + permisos + "]");
			if (permisos.contains("menuItemListado"))
				mostrarMenuItem(menuItemListado);
			if (permisos.contains("menuItemConfiguraciones"))
				mostrarMenuItem(menuItemConfiguraciones);
			if (permisos.contains("menuItemListaBlanca"))
				mostrarMenuItem(menuItemListaBlanca);
			if (permisos.contains("menuItemPortOut"))
				mostrarMenuItem(menuItemPortOut);
			if (permisos.contains("menuItemCancelar"))
				mostrarMenuItem(menuItemCancelar);
			if (permisos.contains("menuItemReintentar"))
				mostrarMenuItem(menuItemReintentar);
		} else if (desktop.getSession().getAttribute("pruebas") != null
				&& "S".equals(desktop.getSession().getAttribute("pruebas"))) {
			mostrarMenus(true);
		}
	}

	private void mostrarMenuItem(Menuitem item) {
		item.setVisible(true);
	}

	private void ocultarMenuItem(Menuitem item) {
		item.setVisible(false);
	}

	private void mostrarMenus(boolean mostrar) {
		if (mostrar) {
			mostrarMenuItem(menuItemListado);
			mostrarMenuItem(menuItemConfiguraciones);
			mostrarMenuItem(menuItemListaBlanca);
			mostrarMenuItem(menuItemPortOut);
			mostrarMenuItem(menuItemCancelar);
			mostrarMenuItem(menuItemReintentar);
		} else {
			ocultarMenuItem(menuItemListado);
			ocultarMenuItem(menuItemConfiguraciones);
			ocultarMenuItem(menuItemListaBlanca);
			ocultarMenuItem(menuItemPortOut);
			ocultarMenuItem(menuItemCancelar);
			ocultarMenuItem(menuItemReintentar);
		}
	}

	@Override
	protected String getModuleTitle() {
		SecUsuario user = (SecUsuario) desktop.getSession().getAttribute("activeUser");
		Calendar calendar = Calendar.getInstance();
		lblDate.setValue(writeDate(calendar));
		if (user == null) {
			lblUser.setValue("NULL");
		} else {
			lblUser.setValue(user.getNombre() + " " + user.getApellido());
		}
		return "Reintento Automático";
	}

	private String writeDate(Calendar calendar) {
		String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		int intMont = calendar.get(Calendar.MONTH);
		intMont++;
		String month = "";
		switch (intMont) {
		case 1:
			month = "Enero";
			break;
		case 2:
			month = "Febrero";
			break;
		case 3:
			month = "Marzo";
			break;
		case 4:
			month = "Abril";
			break;
		case 5:
			month = "Mayo";
			break;
		case 6:
			month = "Junio";
			break;
		case 7:
			month = "Julio";
			break;
		case 8:
			month = "Agosto";
			break;
		case 9:
			month = "Septiembre";
			break;
		case 10:
			month = "Octubre";
			break;
		case 11:
			month = "Noviembre";
			break;
		case 12:
			month = "Diciembre";
			break;
		default:
			break;
		}
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		return day + " de " + month + " del " + year;
	}
}