package com.ericsson.utils;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.TypeConverter;

@SuppressWarnings("deprecation")
public class ShowConverter implements TypeConverter, Serializable {

	private static final long serialVersionUID = -8663357511543656287L;

	private static final Logger log = Logger.getLogger(ShowConverter.class);

	@Override
	public Object coerceToUi(Object val, Component comp) {
		log.debug("Valor original: " + val);
		if (val != null) {
			if (val.toString().equalsIgnoreCase("N"))
				return "No";
			else
				return "Si";
		}
		return "No";
	}

	@Override
	public Object coerceToBean(Object val, Component comp) {
		return val;
	}

}
