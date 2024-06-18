package com.ericsson.base;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Messagebox;

import com.consystec.seguridad.orm.SecUsuario;
import com.ericsson.utils.Utils;

public class SelectorComposerBase<T extends Component> extends SelectorComposer<T> {

	private static final long serialVersionUID = 1029790745899443531L;

	private transient SecUsuario activeUser;

	protected SecUsuario getActiveUser() {
		if (activeUser == null) {
			activeUser = (SecUsuario) Utils.getSessionAttribute("activeUser");
		}
		return activeUser;
	}

	protected boolean stringEmpty(String valor) {
		return (valor == null || valor.isEmpty());
	}

	protected boolean validateCmb(Object obj) {
		return obj != null && !obj.toString().isEmpty() && !obj.toString().equalsIgnoreCase(Utils.LABEL_SELECCIONE);
	}

	protected boolean validateNull(Object obj) {
		return obj != null && !obj.toString().isEmpty();
	}

	protected boolean isInt(String valor) {
		try {
			Integer.parseInt(valor);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	protected void loadAll() {
		org.zkoss.zkplus.databind.AnnotateDataBinder binder = (org.zkoss.zkplus.databind.AnnotateDataBinder) this
				.getSelf().getAttribute("binder");
		binder.loadAll();
	}

	protected void showError(String msg) {
		Messagebox.show(msg, "Error", Messagebox.OK, Messagebox.ERROR);
	}

	protected void showInfo(String msg) {
		Messagebox.show(msg, "Información", Messagebox.OK, Messagebox.INFORMATION);
	}

	protected void showSuccess(String msg) {
		Messagebox.show(msg, "Éxito", Messagebox.OK, Messagebox.INFORMATION);
	}

	protected void showQuestion(String msg) {
		Messagebox.show(msg, "Confirmación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION);
	}

}
