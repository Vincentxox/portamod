package com.ericsson.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;

public class Utils {

	private Utils() {
	}

	private static final Logger log = Logger.getLogger(Utils.class);

	public static final String LABEL_SELECCIONE = "-- Seleccione --";

	public static boolean isEmpty(String value) {
		return value == null || value.trim().equals("");
	}

	/**
	 * gets value of session attribute
	 * 
	 * @param attribute
	 * @return
	 */
	public static Object getSessionAttribute(String attribute) {
		return Executions.getCurrent().getSession().getAttribute(attribute);
	}

	public static void setSessionAttribute(String attribute, Object value) {
		Executions.getCurrent().getSession().setAttribute(attribute, value);
	}

	public static List<String> convertToModel(String conf) {
		log.debug("Inicia convertToModel. Conf: " + conf);
		List<String> model = new ArrayList<>();
		try {
			if (conf.contains("#"))
				model = Arrays.asList(conf.split("#"));
			else
				model.add(conf);
		} catch (Exception e) {
			log.error("Error en convertToModel. ", e);
		} finally {
			if (model == null || model.isEmpty()) {
				log.debug("Finaliza convertToModel. No se pudo concertir el modelo");
			} else {
				log.debug("Finaliza convertToModel. Cantidad de configuraciones: " + model.size());
			}
		}
		return model;
	}

	public static List<String> convertToCode(List<String> codigos) {
		log.debug("Inicia convertToCode. Codigos: " + codigos);
		List<String> codes = new ArrayList<>();
		try {
			for (String code : codigos) {
				codes.add(code.split("-")[0].trim());
			}
		} catch (Exception e) {
			log.error("Error en convertToCode. ", e);
		}
		log.debug(codes);
		return codes;
	}

}
