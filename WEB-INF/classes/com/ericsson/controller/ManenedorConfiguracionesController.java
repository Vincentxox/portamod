package com.ericsson.controller;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ericsson.base.SelectorComposerBase;
import com.ericsson.dao.ConfiguracionDAO;
import com.ericsson.dto.ConfiguracionDTO;

public class ManenedorConfiguracionesController extends SelectorComposerBase<Window> {

	private static final long serialVersionUID = 1488097107170583111L;

	private static final Logger log = Logger.getLogger(ManenedorConfiguracionesController.class);

	/* SERVICES */
	/*****************************/
	private transient ConfiguracionDAO configuracionDao = new ConfiguracionDAO();
	/*****************************/

	/* OBJECTS */
	/*****************************/
	private List<ConfiguracionDTO> listConfs;
	private ConfiguracionDTO searchConf;
	private ConfiguracionDTO selectedConf;
	/*****************************/

	/* WIRE */
	/*****************************/
	@Wire
	private Textbox txtAgrupacion;
	@Wire
	private Textbox txtNombre;
	@Wire
	private Textbox txtAgrupacionPopup;
	@Wire
	private Textbox txtNombrePopup;
	@Wire
	private Textbox txtDescripcionPopup;
	@Wire
	private Textbox txtValorPopup;
	@Wire
	private Textbox txtValor2Popup;
	@Wire
	private Textbox txtReinicioPopup;
	@Wire
	private Textbox txtValorNuevoPopup;
	@Wire
	private Textbox txtValor2NuevoPopup;
	@Wire
	private Popup popupModificacion;
	@Wire
	private Button btnModificar;
	@Wire
	private Label errorTxtValorNuevoPopup;
	@Wire
	private Label errorTxtValor2NuevoPopup;

	/*****************************/

	public List<ConfiguracionDTO> getListConfs() {
		return listConfs;
	}

	public void setListConfs(List<ConfiguracionDTO> listConfs) {
		this.listConfs = listConfs;
	}

	public ConfiguracionDTO getSearchConf() {
		return searchConf;
	}

	public void setSearchConf(ConfiguracionDTO searchConf) {
		this.searchConf = searchConf;
	}

	public ConfiguracionDTO getSelectedConf() {
		return selectedConf;
	}

	public void setSelectedConf(ConfiguracionDTO selectedConf) {
		this.selectedConf = selectedConf;
	}

	/*****************************/

	/* COMPOSER */
	/*****************************/
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		log.debug("doAfterCompose en RepDepGarantiaController");
		super.doAfterCompose(comp);
		setListConfs(configuracionDao.getConfWithParameters(null));
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
		txtAgrupacion.setText("");
		txtNombre.setText("");
		loadAll();
	}

	@Listen("onClick = #btnSearch")
	public void search(Event event) throws SQLException, NamingException {
		searchConf = new ConfiguracionDTO();
		boolean validAgrupacion = false;
		boolean validNombre = false;
		try {
			if (validateNull(txtAgrupacion.getText())) {
				searchConf.setAGRUPACION(txtAgrupacion.getText().toUpperCase().trim());
				validAgrupacion = true;
			}
			if (validateNull(txtNombre.getText())) {
				searchConf.setNOMBRE(txtNombre.getText().toUpperCase().trim());
				validNombre = true;
			}
			if (validAgrupacion || validNombre)
				setListConfs(configuracionDao.getConfWithParameters(searchConf));
			else {
				showInfo("Debe seleccionar por lo menos un filtro para realizar la búsqueda");
				return;
			}
			if (listConfEmpty())
				showInfo("No se encontraron registros con los filtros ingresados");
		} catch (Exception e) {
			showError("La búsqueda no se puede completar porque " + e.getMessage() + ".\nInténtelo de nuevo.");
			log.error("La búsqueda no se puede completar porque ", e);
			return;
		}
		loadAll();
	}

	private boolean listConfEmpty() {
		if (listConfs == null)
			return true;
		else if (listConfs.isEmpty())
			return true;
		return false;
	}

	@Listen("onClick = #btnModificar")
	public void modificar(Event event) {
		log.debug("[Event btnModificar]-[" + getSelectedConf().toString() + "]");
		try {
			cleanValues();
			cleanMessages();
			txtValor2NuevoPopup.setDisabled(false);
			txtAgrupacionPopup.setValue(getSelectedConf().getAGRUPACION());
			txtNombrePopup.setValue(getSelectedConf().getNOMBRE());
			txtDescripcionPopup.setValue(getSelectedConf().getDESCRIPCION());
			txtValorPopup.setValue(getSelectedConf().getVALOR());
			txtValor2Popup.setValue(getSelectedConf().getVALOR2());
			txtReinicioPopup.setValue(getSelectedConf().getREINICIARSTR());
			if (!"SPN_OBSERVACION".equalsIgnoreCase(getSelectedConf().getAGRUPACION()))
				txtValor2NuevoPopup.setDisabled(true);
			popupModificacion.open(btnModificar);
		} catch (Exception e) {
			showError("No se pudo crear el reporte debido a: \n" + e.getMessage()
					+ ".\nInténtelo de nuevo en caso de existir información.");
			log.error("No se pudo crear el reporte debido a: ", e);
		}
	}

	/*****************************/

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Listen("onClick = #btnOperarCambio")
	public void operaCambio(Event event) {
		log.debug("[Event btnOperarCambio TCPORTPARAMRETRIESID]-[" + getSelectedConf().getTCPORTPARAMRETRIESID() + "]");
		cleanMessages();
		String valorNuevo = null;
		String valor2Nuevo = null;
		try {
			valorNuevo = txtValorNuevoPopup.getValue();
			if (!txtValor2NuevoPopup.isDisabled())
				valor2Nuevo = txtValor2NuevoPopup.getValue();
			log.debug("valorNuevo: " + valorNuevo + "valor2Nuevo: " + valor2Nuevo);
			log.debug("stringEmpty(valorNuevo): " + stringEmpty(valorNuevo) + " stringEmpty(valor2Nuevo): "
					+ stringEmpty(valor2Nuevo));
			if ((stringEmpty(valorNuevo) && stringEmpty(valor2Nuevo))) {
				log.error("[Debe ingresar al menos un valor nuevo]");
				errorTxtValorNuevoPopup.setValue("Debe ingresar al menos un valor nuevo");
				errorTxtValorNuevoPopup.setVisible(true);
				return;
			}
			if (!stringEmpty(valor2Nuevo) && !isInt(valor2Nuevo)) {
				log.error("[El VALOR2 debe ser numérico]");
				errorTxtValor2NuevoPopup.setValue("El VALOR2 debe ser numérico");
				errorTxtValor2NuevoPopup.setVisible(true);
				return;
			}
			final String valorNuevoEnviar = valorNuevo;
			final String valorNuevo2Enviar = valor2Nuevo;
			Messagebox.show("¿Desea actualizar la configuración " + txtNombrePopup.getValue() + "?", "Confirmación",
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
						public void onEvent(Event evt) {
							switch (((Integer) evt.getData()).intValue()) {
							case Messagebox.YES:
								try {
									configuracionDao.modificarConfiguracion(valorNuevoEnviar, valorNuevo2Enviar,
											getSelectedConf().getTCPORTPARAMRETRIESID());
									Thread.sleep(1000);
									setListConfs(configuracionDao.getConfWithParameters(null));
									loadAll();
									cleanMessages();
									popupModificacion.close();
									String mensaje = "Se actualizó la configuración correctamente.";
									if ("SI".equalsIgnoreCase(txtReinicioPopup.getValue()))
										mensaje = mensaje
												+ " Para que la configuración tenga efecto es necesario reiniciar el proceso.";
									showInfo(mensaje);
								} catch (Exception e) {
									log.error("No se pudo actualizar la configuración. ", e);
									showInfo("No se pudo actualizar la configuración, por favor intente de nuevo");
								}
								break;
							case Messagebox.NO:
								popupModificacion.close();
								break;
							default:
								break;
							}
						}
					});
		} catch (Exception e) {
			log.error("[No se pudo actualizar la configuración]", e);
			showInfo("No se pudo actualizar la configuración, por favor intente de nuevo");
		}
	}
	/*****************************/

	/* METHODS */
	/*****************************/
	private void cleanMessages() {
		errorTxtValorNuevoPopup.setValue("");
		errorTxtValorNuevoPopup.setVisible(false);
		errorTxtValor2NuevoPopup.setValue("");
		errorTxtValor2NuevoPopup.setVisible(false);
	}

	private void cleanValues() {
		txtValorNuevoPopup.setValue(null);
		txtValor2NuevoPopup.setValue(null);
	}

}
