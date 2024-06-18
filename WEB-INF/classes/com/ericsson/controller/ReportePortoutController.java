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
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.ericsson.base.SelectorComposerBase;
import com.ericsson.dao.ReportePortoutDAO;
import com.ericsson.dto.PortoutDTO;
import com.ericsson.utils.Report;

public class ReportePortoutController extends SelectorComposerBase<Window> {

	private static final long serialVersionUID = 1488097107170583111L;

	private static final Logger log = Logger.getLogger(ReportePortoutController.class);

	/* SERVICES */
	/*****************************/
	private transient ReportePortoutDAO reporteReintentosPortaDao = new ReportePortoutDAO();
	private transient Report report = new Report();
	/*****************************/

	/* OBJECTS */
	/*****************************/
	private List<PortoutDTO> listHist;
	private PortoutDTO searchHist;
	/*****************************/

	/* WIRE */
	/*****************************/
	@Wire
	private Textbox txtNumeroPortar;
	@Wire
	private Textbox txtCreadoPor;
	@Wire
	private Datebox dbIni;
	@Wire
	private Datebox dbFin;
	@Wire
	private Div divHist;
	/*****************************/

	/* GETTERS AND SETTERS */
	/*****************************/
	public List<PortoutDTO> getListHist() {
		return listHist;
	}

	public void setListHist(List<PortoutDTO> listHist) {
		this.listHist = listHist;
	}

	public PortoutDTO getSearchHist() {
		return searchHist;
	}

	public void setSearchHist(PortoutDTO searchHist) {
		this.searchHist = searchHist;
	}

	/*****************************/

	/* COMPOSER */
	/*****************************/
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		log.debug("doAfterCompose en RepDepGarantiaController");
		super.doAfterCompose(comp);
		setListHist(reporteReintentosPortaDao.getHist());
		divHist.setVisible(true);
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
		searchHist = new PortoutDTO();
		listHist = null;
		divHist.setVisible(false);
		loadAll();
	}

	@Listen("onClick = #btnSearch")
	public void search(Event event) throws SQLException, NamingException {
		searchHist = new PortoutDTO();
		boolean verificaNumeroPortar = false;
		boolean verificaCreadoPor = false;
		boolean verificainicio = true;
		boolean verificaFin = true;
		try {
			String numeroPortar = txtNumeroPortar.getValue();
			if (validateNull(numeroPortar)) {
				searchHist.setNUMERO_PORTAR(numeroPortar);
				verificaNumeroPortar = true;
			}
			String creadoPor = txtCreadoPor.getValue();
			if (validateNull(creadoPor)) {
				searchHist.setCREADO_POR(creadoPor);
				verificaCreadoPor = true;
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
			if (verificaNumeroPortar || verificaCreadoPor || (verificainicio && verificaFin)) {
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
			report.createReportHistoryPortoutDTO(listHist);
		} catch (Exception e) {
			showError("No se pudo crear el reporte debido a " + e.getMessage() + ".\nInténtelo de nuevo.");
			log.error("No se pudo crear el reporte debido a: ", e);
		}
	}

}
