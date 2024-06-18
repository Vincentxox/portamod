package com.ericsson.controller;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ericsson.base.SelectorComposerBase;
import com.ericsson.dao.ReporteReintentosPortaDAO;
import com.ericsson.dto.ReintentoDTO;
import com.ericsson.utils.Report;

public class ReporteListadoController extends SelectorComposerBase<Window> {

	private static final long serialVersionUID = 1488097107170583111L;

	private static final Logger log = Logger.getLogger(ReporteListadoController.class);

	/* SERVICES */
	/*****************************/
	private transient ReporteReintentosPortaDAO reporteReintentosPortaDao = new ReporteReintentosPortaDAO();
	private transient Report report = new Report();
	/*****************************/

	/* OBJECTS */
	/*****************************/
	private List<ReintentoDTO> listHist;
	private ReintentoDTO searchHist;
	/*****************************/

	/* WIRE */
	/*****************************/
	@Wire
	private Textbox txtNumeroPortar;
	@Wire
	private Textbox txtNumeroTemporal;
	@Wire
	private Datebox dbIni;
	@Wire
	private Datebox dbFin;
	@Wire
	private Div divHist;
	@Wire
	private Combobox cbTipoProducto;
	/*****************************/

	/* GETTERS AND SETTERS */
	/*****************************/
	public List<ReintentoDTO> getListHist() {
		return listHist;
	}

	public void setListHist(List<ReintentoDTO> listHist) {
		this.listHist = listHist;
	}

	public ReintentoDTO getSearchHist() {
		return searchHist;
	}

	public void setSearchHist(ReintentoDTO searchHist) {
		this.searchHist = searchHist;
	}

	/*****************************/

	/* COMPOSER */
	/*****************************/
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		log.debug("doAfterCompose en RepDepGarantiaController");
		super.doAfterCompose(comp);
		//setListHist(reporteReintentosPortaDao.getHist());
		//divHist.setVisible(true);
		cbTipoProducto.setSelectedIndex(0);
		comp.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				throw new UnsupportedOperationException();
			}
		});
	}

	/*****************************/

	@Listen("onClick = #btnClear")
	public void clean(Event event) {
		txtNumeroPortar.setText("");
		dbIni.setText("");
		dbFin.setText("");
		cbTipoProducto.setSelectedIndex(0);
		searchHist = new ReintentoDTO();
		listHist = null;
		divHist.setVisible(false);
		loadAll();
	}

	@Listen("onClick = #btnSearch")
	public void search(Event event) throws SQLException, NamingException {
		searchHist = new ReintentoDTO();
		boolean verificaNumeroPortar = false;
		boolean verificaNumeroTemporal = false;
		boolean verificainicio = true;
		boolean verificaFin = true;
		boolean verficarTipoProducto=false;
		try {
			
			String tipoProduct = cbTipoProducto.getSelectedItem().getValue();
			log.info("cbTipoProducto es: " + tipoProduct);
			if(validateNull(tipoProduct)) {
				if(!tipoProduct.toUpperCase().equals("TODAS")) {
					searchHist.setTIPOPRODUCTO(tipoProduct);
					verficarTipoProducto=true;
				}
				
			}
			
			String numeroPortar = txtNumeroPortar.getValue();
			if (validateNull(numeroPortar)) {
				searchHist.setNUMEROPORTAR(numeroPortar);
				verificaNumeroPortar = true;
			}
			String numeroTemporal = txtNumeroTemporal.getValue();
			if (validateNull(numeroTemporal)) {
				searchHist.setNUMEROTEMPORAL(numeroTemporal);
				verificaNumeroTemporal = true;
			}
			Date inicio = dbIni.getValue();
			if (inicio == null)
				verificainicio = false;
			Date fin = dbFin.getValue();
			if (fin == null)
				verificaFin = false;
			if (verificainicio && verificaFin) {
				if (inicio.after(fin)) {
					showInfo(
							"La búsqueda no se puede completar porque la fecha de inicio es mayor a la fecha de final");
					return;
				}
				fin = new Date(fin.getTime() + TimeUnit.DAYS.toMillis(1));
				log.debug("[INICIO]-[" + inicio + "]--[FIN]-[" + fin + "]");
				searchHist.setFECHA_INICIO(inicio);
				searchHist.setFECHA_FIN(fin);
			}
			if (verificaNumeroPortar || verificaNumeroTemporal || (verificainicio && verificaFin)) {
				setListHist(reporteReintentosPortaDao.getHistWithParameters(searchHist));
			} else {
				showInfo("Debe ingresar al menos un filtro para realizar la búsqueda");
				return;
			}
			if (listEmpty())
				showInfo("No se encontraron registros con los filtros ingresados");
			else {
				log.debug("Lista: " + getListHist().size());
				divHist.setVisible(true);
			}
		} catch (Exception e) {
			showError("La búsqueda no se puede completar porque " + e.getMessage() + ".\nInténtelo de nuevo.");
			log.error("La búsqueda no se puede completar porque ", e);
			return;
		}
		loadAll();
	}

	private boolean listEmpty() {
		if (listHist == null)
			return true;
		else if (listHist.isEmpty())
			return true;
		return false;
	}

	@Listen("onClick = #btnCreate")
	public void report(Event event) {
		if (listEmpty()) {
			showError("No hay datos para poder crear un reporte");
			return;
		}
		try {
			report.createReportHistory(listHist);
		} catch (Exception e) {
			showError("No se pudo crear el reporte debido a " + e.getMessage() + ".\nInténtelo de nuevo.");
			log.error("No se pudo crear el reporte debido a: ", e);
		}
	}

}
