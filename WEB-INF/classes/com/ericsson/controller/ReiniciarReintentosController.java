package com.ericsson.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import com.ericsson.base.SelectorComposerBase;
import com.ericsson.dao.ReiniciarReintentosDAO;
import com.ericsson.dto.ReintentoDTO;
import com.ericsson.utils.Report;

public class ReiniciarReintentosController extends SelectorComposerBase<Window> {

	private static final long serialVersionUID = 7989270336828836404L;

	private static final Logger log = Logger.getLogger(ReiniciarReintentosController.class);

	/* SERVICES */
	/*****************************/
	private transient ReiniciarReintentosDAO reiniciarReintentosDAO = new ReiniciarReintentosDAO();
	private transient Report report = new Report();
	private transient Media media = null;
	/*****************************/

	/* OBJECTS */
	/*****************************/
	private List<ReintentoDTO> listHist;
	private List<String> listNumeros = new ArrayList<>();
	private List<String> noNumberProcceded = new ArrayList<String>();
	private ReintentoDTO searchHist;
	
	ListModelList model;
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
	
	@Wire
	public boolean mostrarOperaciones=true;
	
	@Wire
	private Radiogroup radioGroup;	
	@Wire
	private Textbox txtCheck;
	
	@Wire
	private Listbox listHistorial;

	@Wire
	private Textbox txtNombreArchivo;

	
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

	
	public boolean isMostrarOperaciones() {
		return mostrarOperaciones;
	}

	public void setMostrarOperaciones(boolean mostrarOperaciones) {
		this.mostrarOperaciones = mostrarOperaciones;
	}

	/*****************************/

	public ListModelList getModel() {
		return model;
	}

	public void setModel(ListModelList model) {
		this.model = model;
	}

	/* COMPOSER */
	/*****************************/
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		log.debug("doAfterCompose en RepDepGarantiaController");
		super.doAfterCompose(comp);		
		List<ReintentoDTO> x = new ArrayList();
		
		setListHist(x);
		model = new ListModelList(getListHist());
		model.setMultiple(true);
		divHist.setVisible(false);
		comp.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				throw new UnsupportedOperationException();
			}
		});
	}

	/*****************************/
	@Listen("onUpload = #btnSeleccionarTextFile")
	public void btnSeleccionar(UploadEvent even) {
		try {
			if (even.getMedia() != null) {
				if (even.getMedia().getName().replace(" ", "_").toLowerCase().endsWith(".txt")) {
					log.debug("Tamaño del archivo: " + even.getMedia().getStringData().length() );
					if (even.getMedia().getStringData().length() <= 2000000) {
						media = even.getMedia();
						txtNombreArchivo.setText(media.getName());
					} else {
						showError(
								"El tamaño máximo aceptado es 2MB, por favor seleccione otro archivo que cumpla con este tamaño: ");
						loadAll();
					}
				} else {
					showError("Únicamente es posible cargar archivos del tipo txt.");
					loadAll();
					log.error("Se intento cargar un archivo que no es txt.");
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
	
	@Listen("onClick = #btnProcesarMasivo")
	public void btnProcesarMasivo() {
		try {
			setMostrarOperaciones(false);
			upload();
			log.debug("listNumero is:"+listNumeros);
			if (listNumeros.isEmpty()) {
				showInfo("Archivo vacio.");
			} else {
				noNumberProcceded.clear();
				for(String num: listNumeros) {
					log.debug("Procesando number: "+ num);
					ReintentoDTO reintento = reiniciarReintentosDAO.getNumberData(num);
					
					log.debug("Reintento es: "+ reintento);
					
					if(reintento != null && reintento.getTCPORTTRANSACID() != null) {
						log.debug("Reintneto id is: "+ reintento.getTCPORTTRANSACID());
						try {
							if (reiniciarReintentosDAO.reiniciarReintento(reintento.getTCPORTTRANSACID(),getActiveUser().getUsuario())) {
								
							}
						} catch (Exception e) {
							log.error("No se pudo reiniciar el reintento. ", e);
						}
						
					} 
					if(reintento == null) {
						log.debug("Reintento es: "+ reintento);
						noNumberProcceded.add(num);
					}
				}
				log.debug("Los siguientes numero no existen: " + noNumberProcceded.toString());
				showSuccess("Los siguientes numero no existen: " + noNumberProcceded.toString());
			}
		} catch (Exception e) {
			log.error("Error: ", e);
			showInfo("No se pudo procesar el archivo, por favor intente de nuevo");
		} finally {
			txtNombreArchivo.setValue("");
			media = null;
			loadAll();
		}
		mostrarOperaciones=false;
	}
	
	private void upload() throws IOException {
		try {
			listNumeros.clear();
			if(media != null) {
				
				try (BufferedReader reader = new BufferedReader(new StringReader(media.getStringData()))){
					String line;
					
					while((line = reader.readLine()) != null) {
						try {
							String number = line.trim().toString();
							listNumeros.add(number);
						}catch(NumberFormatException e) {
							log.error(e.getMessage());
						}
					}
				}
			}
				
		} catch (Exception e) {
			log.error("No selecciono ninguna archivo: ", e);
		}
	}
	

	@Listen("onClick = #btnClear")
	public void clean(Event event) {
		setMostrarOperaciones(true);
		txtNumeroPortar.setText("");
		dbIni.setText("");
		dbFin.setText("");
		searchHist = new ReintentoDTO();
		listHist = null;
		divHist.setVisible(false);		
		loadAll();
	}
	
//	@Listen("onUpload = #btnSeleccionar")
//	public void btnSeleccionar(UploadEvent even) {
//		try {
//			//mensaje = null;
//			if (even.getMedia() != null) {
//				System.out.println("file name"+even.getMedia().getName());
//				if (even.getMedia().getName().replace(" ", "_").matches("[\\S]+.[(txt)]{3}")) {
//				//	log.debug("Tamaño del archivo: " + even.getMedia().getByteData().length);
//					
//				} else {
//					showError("Únicamente es posible cargar archivos del tipo txt.");
//					loadAll();
//					log.error("Se intento cargar un archivo que no es txt.");
//				}
//			}
//		} catch (NullPointerException e) {
//			showError("No se encontró ningun documento seleccionado: ");
//			loadAll();
//			log.error("No se encontró ningún documento seleccionado.", e);
//		} catch (Exception e) {
//			showError("Ha ocurrido un error al cargar el archivo: ");
//			loadAll();
//			log.error("Error en btnSeleccionar. ", e);
//		}
//	}
//	
	@Listen("onClick = #btnCreate")
	public void reiniciar(Event event) {
		
		setMostrarOperaciones(true);
		txtNumeroPortar.setText("");
		dbIni.setText("");
		dbFin.setText("");
		searchHist = new ReintentoDTO();
		
		boolean estadoCorrecto = true;
		
		log.debug("tamaño lista " + listHistorial.getSelectedItems().size());
		if (listHistorial.getSelectedItems().size()<1) {
			showInfo("Debe seleccionar al menos un elemento");
			return;
		}
		
		log.debug("valor del ocult " +txtCheck.getValue());
		if ("true".equals(txtCheck.getValue())) {
			
			for (ReintentoDTO reintentoDTO : listHist) {
			      
				 
				
				
				try {
					if (reiniciarReintentosDAO.reiniciarReintento(reintentoDTO.getTCPORTTRANSACID(),getActiveUser().getUsuario())) {
					
					} else {
						estadoCorrecto = false;
						break;
					}
				} catch (Exception e) {
					log.error("No se pudo reiniciar el reintento. ", e);
					
					estadoCorrecto = false;
					break;	
				}
			}
			
		}else if ("false".equals(txtCheck.getValue())) {

			log.debug("parcial ");
			for (Listitem item :listHistorial.getSelectedItems()) {
			      
				ReintentoDTO reintentoDTO =  (ReintentoDTO)  item.getValue();
				
				
				try {
					if (reiniciarReintentosDAO.reiniciarReintento(reintentoDTO.getTCPORTTRANSACID(),getActiveUser().getUsuario())) {
					
					
					}else {
					
						estadoCorrecto = false;
						break;
					}
				} catch (Exception e) {
					log.error("No se pudo reiniciar el reintento. ", e);
					
					estadoCorrecto = false;
					break;
				}
			}
		}
		if (estadoCorrecto) {
			showSuccess("Se reiniciaron realizado correctamente");
		}else {
			showInfo("No se pudo realizar el reinicio");
		}
		
		listHist = null;
		divHist.setVisible(false);
	
		setListHist(null);
		
		loadAll();
	}

	@Listen("onClick = #btnSearch")
	public void search(Event event) throws SQLException, NamingException {
		searchHist = new ReintentoDTO();
		boolean verificaNumeroPortar = false;
		boolean verificaCreadoPor = false;
		boolean verificainicio = true;
		boolean verificaFin = true;
		boolean verificaTipo = true;
		Radio seleccion = radioGroup.getSelectedItem();
		setMostrarOperaciones(true);
		
		log.debug("se elegio " + seleccion.getLabel());
		log.debug("# de seleccionados: " + listHistorial.getSelectedItems().size());
		Integer maximoPrepago=0;
		Integer maximoPostPagoGestionado=0;
		Integer maximoPostPagoAutoGestionado=0;
		
		
		
		try {
			String numeroPortar = txtNumeroPortar.getValue();
			if (validateNull(numeroPortar)) {
				searchHist.setNUMEROPORTAR(numeroPortar);
				verificaNumeroPortar = true;
			}
			
			
			searchHist.setTIPOPRODUCTO(seleccion.getLabel().toUpperCase());
			
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
			if (verificaNumeroPortar || verificaCreadoPor || (verificainicio && verificaFin)||verificaTipo) {
				
				List<ReintentoDTO> listHistTemp=reiniciarReintentosDAO.getHistWithParameters(searchHist);
				List<ReintentoDTO> listHistTemp2 = new ArrayList<>();
				if (!listHistTemp.isEmpty()) {
					log.debug("size listTemp: " + listHistTemp.size());
					
					if ("TODOS".equals(searchHist.getTIPOPRODUCTO())||"PREPAGO".equals(searchHist.getTIPOPRODUCTO())) {
						maximoPrepago=Integer.valueOf(reiniciarReintentosDAO.getGeneralApplicationParameter("RETRIES"));
					}

					if ("TODOS".equals(searchHist.getTIPOPRODUCTO())||"POSTPAGO".equals(searchHist.getTIPOPRODUCTO())) {
						maximoPostPagoGestionado=Integer.valueOf(reiniciarReintentosDAO.getGeneralApplicationParameter("POSTPAGO_GESTIONADO_RETRIES"));
						maximoPostPagoAutoGestionado=Integer.valueOf(reiniciarReintentosDAO.getGeneralApplicationParameter("POSTPAGO_AUTOGESTIONADO_RETRIES"));
					}
					
					
					
					for (ReintentoDTO reintentoDTO : listHistTemp) {
						if ("TODOS".equals(searchHist.getTIPOPRODUCTO())||"PREPAGO".equals(searchHist.getTIPOPRODUCTO())) {
							if ("PREPAGO".equals(reintentoDTO.getTIPOPRODUCTO())) {
								if (Integer.valueOf(reintentoDTO.getRETRIES())>=maximoPrepago) {
									listHistTemp2.add(reintentoDTO);
								}
							}
						}

						if ("TODOS".equals(searchHist.getTIPOPRODUCTO())||"POSTPAGO".equals(searchHist.getTIPOPRODUCTO())) {
							if ("POSTPAGO".equals(reintentoDTO.getTIPOPRODUCTO())&&("GESTIONADO".equals(reintentoDTO.getTIPOCANAL()))) {
								if (Integer.valueOf(reintentoDTO.getRETRIES())>=maximoPostPagoGestionado) {
									listHistTemp2.add(reintentoDTO);
								}
							}else if ("POSTPAGO".equals(reintentoDTO.getTIPOPRODUCTO())&&("AUTOGESTIONADO".equals(reintentoDTO.getTIPOCANAL()))) {
								if (Integer.valueOf(reintentoDTO.getRETRIES())>=maximoPostPagoAutoGestionado) {
									listHistTemp2.add(reintentoDTO);
								}
							}
							
						}
					}
					
					
					
				}				
				log.debug("size listTemp2: " + listHistTemp2.size());
				setListHist(listHistTemp2);
				model = new ListModelList(getListHist());				
				model.setMultiple(true);
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



}
