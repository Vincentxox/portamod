package com.ericsson.controller;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ericsson.base.SelectorComposerBase;
import com.ericsson.dao.ReporteReintentosPortaDAO;

public class CancelarReintentoController extends SelectorComposerBase<Window> {

	private static final long serialVersionUID = 1488097107170583111L;

	private static final Logger log = Logger.getLogger(CancelarReintentoController.class);

	/* SERVICES */
	/*****************************/
	private transient ReporteReintentosPortaDAO cancelarDao = new ReporteReintentosPortaDAO();
	/*****************************/

	/* WIRE */
	/*****************************/
	@Wire
	private Textbox txtNumero;
	@Wire
	private Textbox txtEstado;
	@Wire
	private Groupbox infoNumero;
	@Wire
	private Button btnCancelar;

	/*****************************/

	/*****************************/

	/* COMPOSER */
	/*****************************/
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		log.debug("doAfterCompose en RepDepGarantiaController");
		super.doAfterCompose(comp);
		comp.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				throw new UnsupportedOperationException();
			}
		});
	}

	/*****************************/

	/* BUTTONS */
	/*****************************/

	@Listen("onClick = #btnClear")
	public void clean(Event event) {
		txtNumero.setText("");
		txtEstado.setText("");
		infoNumero.setVisible(false);
		btnCancelar.setDisabled(true);
	}

	@Listen("onClick = #btnSearch")
	public void search(Event event) {
		try {
			cleanBusqueda();
			String numeroPortar = txtNumero.getText();
			if (validateNull(numeroPortar)) {
				if (!isInt(numeroPortar)) {
					showInfo("No ingresó un número de teléfono válido");
					return;
				}
				String estado = cancelarDao.obtenerEstadoReintento(numeroPortar);
				if (validateNull(estado)) {
					txtEstado.setText(estado);
					infoNumero.setVisible(true);
					if (!"CANCELADO".equalsIgnoreCase(estado))
						btnCancelar.setDisabled(false);
				} else
					showInfo("El número ingresado no existe en los registros");
			} else
				showInfo("Debe ingresar un número de teléfono");
		} catch (Exception e) {
			showError("La búsqueda no se puede completar porque " + e.getMessage() + ".\nInténtelo de nuevo.");
			log.error("La búsqueda no se puede completar porque ", e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Listen("onClick = #btnCancelar")
	public void cancelar(Event event) {
		log.debug("[Event btnCancelar. Numero a cancelar]-[" + txtNumero.getText() + "]");
		try {
			String numeroPortar = txtNumero.getText();
			if (validateNull(numeroPortar)) {
				if (!isInt(numeroPortar)) {
					showInfo("No ingresó un número de teléfono válido");
					return;
				}
				String estado = cancelarDao.obtenerEstadoReintento(numeroPortar);
				if (validateNull(estado)) {
					final String usuario = getActiveUser().getUsuario();
					Messagebox.show("¿Desea cancelar los reintentos para el número " + numeroPortar + "?",
							"Confirmación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
								public void onEvent(Event evt) {
									switch (((Integer) evt.getData()).intValue()) {
									case Messagebox.YES:
										try {
											if (cancelarDao.cancelarReintento(usuario, numeroPortar)) {
												showInfo("Se cancelaron correctamente los reintentos para el numero "
														+ numeroPortar);
												txtEstado.setText(cancelarDao.obtenerEstadoReintento(numeroPortar));
												btnCancelar.setDisabled(true);
											} else
												showInfo("No fue posible cancelar los reintentos para el numero "
														+ numeroPortar);
										} catch (Exception e) {
											log.error("No se pudo cancelar el reintento. ", e);
											showInfo("No fue posible cancelar los reintentos para el numero "
													+ numeroPortar);
										}
										break;
									case Messagebox.NO:
										break;
									default:
										break;
									}
								}
							});
				} else
					showInfo("El número ingresado no existe en los registros");
			} else
				showInfo("Debe ingresar un número de teléfono");
		} catch (Exception e) {
			showError("No se pudo crear el reporte debido a: \n" + e.getMessage()
					+ ".\nInténtelo de nuevo en caso de existir información.");
			log.error("No se pudo crear el reporte debido a: ", e);
		}
	}

	public void cleanBusqueda() {
		txtEstado.setText("");
		infoNumero.setVisible(false);
		btnCancelar.setDisabled(true);
	}

}
