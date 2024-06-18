package com.ericsson.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ericsson.base.SelectorComposerBase;
import com.ericsson.dao.ListaBlancaDAO;
import com.ericsson.dto.ListaBlancaDTO;

import jxl.Sheet;
import jxl.Workbook;

public class ListaBlancaController extends SelectorComposerBase<Window> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ListaBlancaController.class);

	/* VARIABLES */
	/*****************************/
	private List<ListaBlancaDTO> listNumeros = new ArrayList<>();
	private List<ListaBlancaDTO> listaBlanca;
	private transient ListaBlancaDAO listaBlancaDAO = new ListaBlancaDAO();
	private ListaBlancaDTO serarchLB;
	private ListaBlancaDTO selectedNumber;
	private static final String INTENTELO = ".\nInténtelo de nuevo.";
	/*****************************/
	/* WIRE */
	/*****************************/
	@Wire
	private Textbox txtNombreArchivo;
	@Wire
	private Textbox txtFiltroNumero;
	@Wire
	private Intbox txtNumero;

	private transient Media media = null;

	private String mensaje;

	@Wire
	private Div divListaBlanca;

	/*****************************/
	/* GETTERS AND SETTERS */

	public List<ListaBlancaDTO> getListaBlanca() {
		return listaBlanca;
	}

	public void setListaBlanca(List<ListaBlancaDTO> listaBlanca) {
		this.listaBlanca = listaBlanca;
	}

	public ListaBlancaDTO getSelectedNumber() {
		return selectedNumber;
	}

	public void setSelectedNumber(ListaBlancaDTO selectedNumber) {
		this.selectedNumber = selectedNumber;
	}

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		setListaBlanca(listaBlancaDAO.getListaBlanca());
		comp.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				throw new UnsupportedOperationException();
			}
		});
	}

	@Listen("onClick = #btnProcesarMasivo")
	public void btnProcesarMasivo() {
		try {
			upload();
			if (mensaje != null) {
				showError(mensaje);
				txtNombreArchivo.setValue(null);
				media = null;
				mensaje = null;
				loadAll();
				return;
			}
			if (listNumeros.isEmpty()) {
				showInfo("Archivo vacio.");
			} else {
				if (procesarListaBlanca()) {
					showSuccess("Registros procesados con éxito");
					setListaBlanca(listaBlancaDAO.getListaBlanca());
				} else {
					showError("Error al procesar, por favor intente de nuevo");
				}
			}
		} catch (Exception e) {
			log.error("Error: ", e);
			showInfo("No se pudo procesar el archivo, por favor intente de nuevo");
		} finally {
			txtNombreArchivo.setValue(null);
			media = null;
			mensaje = null;
			loadAll();
		}
	}

	private boolean procesarListaBlanca() {
		for (ListaBlancaDTO registro : listNumeros) {
			try {
				listaBlancaDAO.crearRegistro(registro);
			} catch (Exception e) {
				showError("No se creó el registro porque " + e.getMessage() + INTENTELO);
				log.error("No se creó el registro porque  ", e);
				return false;
			}
		}
		return true;
	}

	@Listen("onClick = #btnGuardar")
	public void guardar(Event event) throws SQLException, NamingException {
		ListaBlancaDTO registro = new ListaBlancaDTO();
		try {
			Integer telefono = txtNumero.getValue();
			if (!validateNull(telefono)) {
				showInfo("Es necesario el ingreso del campo Número");
			}
			registro.setCreadoPor(getActiveUser().getUsuario());
			registro.setTelefono(new BigDecimal(telefono));
			if (!listaBlancaDAO.crearRegistro(registro)) {
				showInfo("No se creó el registro, por favor intente de nuevo");
			} else {
				showSuccess("Registro creado con éxito");
				txtNumero.setValue(null);
				txtFiltroNumero.setValue(null);
				setListaBlanca(listaBlancaDAO.getListaBlanca());
			}
		} catch (Exception e) {
			showError("No se creó el registro porque " + e.getMessage() + INTENTELO);
			log.error("No se creó el registro porque  ", e);
			return;
		}
		loadAll();
	}

	@Listen("onClick = #btnSearch")
	public void search(Event event) throws SQLException, NamingException {
		serarchLB = new ListaBlancaDTO();
		try {
			String telefono = txtFiltroNumero.getValue();
			if (validateNull(telefono)) {
				serarchLB.setTelefono(new BigDecimal(telefono));
			}
			setListaBlanca(listaBlancaDAO.getListaBlancaWithParameters(serarchLB));
			if (listEmpty())
				showInfo("No se encontraron registros con los filtros ingresados");
			else {
				log.debug("Lista: " + getListaBlanca().size());
				divListaBlanca.setVisible(true);
			}
		} catch (Exception e) {
			showError("La búsqueda no se puede completar porque " + e.getMessage() + INTENTELO);
			log.error("La búsqueda no se puede completar porque ", e);
			return;
		}
		loadAll();
	}

	@Listen("onUpload = #btnSeleccionar")
	public void btnSeleccionar(UploadEvent even) {
		try {
			mensaje = null;
			if (even.getMedia() != null) {
				if (even.getMedia().getName().replace(" ", "_").replace("xlsx", "#").matches("[\\S]+.[(xls)]{3}")) {
					log.debug("Tamaño del archivo: " + even.getMedia().getByteData().length);
					if (even.getMedia().getByteData().length <= 2000000) {
						media = even.getMedia();
						txtNombreArchivo.setText(media.getName());
					} else {
						showError(
								"El tamaño máximo aceptado es 2MB, por favor seleccione otro archivo que cumpla con este tamaño: ");
						loadAll();
					}
				} else {
					showError("Únicamente es posible cargar archivos del tipo xls.");
					loadAll();
					log.error("Se intento cargar un archivo que no es xls.");
				}
			}
		} catch (NullPointerException e) {
			showError("No se encontró ningun documento seleccionado: ");
			loadAll();
			log.error("No se encontró ningún documento seleccionado.", e);
		} catch (Exception e) {
			showError("Ha ocurrido un error al cargar el archivo: ");
			loadAll();
			log.error("Error en btnSeleccionar. ", e);
		}
	}

	private void upload() throws IOException {
		try {
			mensaje= null;
			Workbook excArch = Workbook.getWorkbook(media.getStreamData());
			int nrows;
			int ncells;
			Sheet hoja = excArch.getSheet(0);
			nrows = hoja.getRows();
			ncells = hoja.getColumns();
			log.debug("ROWS-->" + nrows);
			log.debug("ncells-->" + ncells);
			if (ncells >= 1 && nrows >= 2 && hoja.getCell(0, 0).getContents().equals("NUMERO")) {
				listNumeros.clear();
				for (int contR = 1; contR < nrows; contR++) {
					ListaBlancaDTO registro = new ListaBlancaDTO();
					for (int contC = 0; contC < 1; contC++) {
						String nameColumn = hoja.getCell(contC, 0).getContents();
						if (nameColumn.equals("NUMERO")) {
							String numero = hoja.getCell(contC, contR).getContents();
							if (numero != null && !numero.trim().isEmpty()) {
								registro.setTelefono(new BigDecimal(numero));
								registro.setCreadoPor(getActiveUser().getUsuario());
							}
						}
					}
					if (validateNull(registro.getTelefono())) {
						listNumeros.add(registro);
					}
				}
			} else {
				mensaje = "El formato del archivo no es el correcto : debe de contener la columna NUMERO y los números";
			}
		} catch (Exception e) {
			
			mensaje = "El formato del archivo no es el correcto : debe de contener la columna NUMERO y los números";
			log.error("No selecciono ninguna archivo: ", e);
		}
	}

	@Listen("onClick = #btnCambioEstado")
	public void operaCambio(Event event) {
		log.debug("Cambiar estado de " + getSelectedNumber().getTelefono() + "]");
		try {
			if (getSelectedNumber().getEstado().equalsIgnoreCase("ALTA")) {
				getSelectedNumber().setEstado("BAJA");
			} else {
				getSelectedNumber().setEstado("ALTA");
			}
			getSelectedNumber().setModificadoPor(getActiveUser().getUsuario());
			if (!listaBlancaDAO.updateRegistro(getSelectedNumber())) {
				showInfo("No se pudo actualizar el registro, por favor intente de nuevo");
			} else {
				showSuccess("Registro dado de " + getSelectedNumber().getEstado() + " con éxito");
				setListaBlanca(listaBlancaDAO.getListaBlanca());
				loadAll();
			}
		} catch (Exception e) {
			log.error("[No se pudo actualizar el registro]", e);
			showInfo("No se pudo actualizar el registro, por favor intente de nuevo");
		}
	}

	private boolean listEmpty() {
		if (listaBlanca == null)
			return true;
		else if (listaBlanca.isEmpty())
			return true;
		return false;
	}

}
