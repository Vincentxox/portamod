package com.ericsson.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zul.Filedownload;

import com.ericsson.dto.PortoutDTO;
import com.ericsson.dto.ReintentoDTO;

public class Report {

	private static final Logger log = Logger.getLogger(Report.class);
	private DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss");
	private DateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
	private static final String NEXT_LINE = "\n";
	private static final String DELIM = ",";
	private static final String FILAS = " filas";
	private static final String PATH = "Path: ";
	private static final String FILE = ". File: ";
	private static final String APP_FILE = "application/file";

	public Report() {
		DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
		simbolos.setDecimalSeparator('.');
		simbolos.setGroupingSeparator(',');
	}

	public void createReportHistory(List<ReintentoDTO> history) {
		log.debug("Inicia createReportHistory. " + history.size() + FILAS);
		File tempFile = null;
		try {
			StringBuilder fw = new StringBuilder();
			fw.append(
					"TCPORTTRANSACID, NUMEROPORTAR, PORTIN/PORTOUT, NUMEROTEMPORAL, TIPOPORT, CIP, TIPOPRODUCTO, OPERADORDONANTE, TIPOPRODUCTODONANTE, SPNOBSERVACION, CODVENDEDOR, ESTADO, IMEI, SWCUSTOMER, TIPCLIENTE, "
							+ "DESNOMBRE, TIPDOCUMENTO, CODDOCUMENTO, ORIGEN, ESTADORETRIES, CREADO_EL, NO.REINTENTO, EXITOSA, FECHAPORTACION, OPERADOR, DISTRIBUIDOR, EJECUTIVO,  IDPORTABILIDAD,  ORIGEN")
					.append(NEXT_LINE);
			for (ReintentoDTO hDto : history) {
				fw.append(getString(hDto.getTCPORTTRANSACID()) + DELIM)
						.append(getString(hDto.getNUMEROPORTAR()) + DELIM)
						.append(getString(hDto.getPORTINOUT()) + DELIM)
						.append(getString(hDto.getNUMEROTEMPORAL()) + DELIM)
						.append(getString(hDto.getTIPOPORT()) + DELIM)
						.append(getString(hDto.getCIP()) + DELIM)
						.append(getString(hDto.getTIPOPRODUCTO()) + DELIM)
						.append(getString(hDto.getOPERADORDONANTE()) + DELIM)
						.append(getString(hDto.getTIPOPRODUCTODONANTE()) + DELIM)
						.append(getString(hDto.getSPNOBSERVACION()) + DELIM)
						.append(getString(hDto.getCODVENDEDOR()) + DELIM)
						.append(getString(hDto.getESTADO()) + DELIM)
						.append(getString(hDto.getIMEI()) + DELIM)
						.append(getString(hDto.getSWCUSTOMER()) + DELIM)
						.append(getString(hDto.getTIPCLIENTE()) + DELIM)
						.append(getString(hDto.getDESNOMBRE()) + DELIM)
						.append(getString(hDto.getTIPDOCUMENTO()) + DELIM)
						.append(getString(hDto.getCODDOCUMENTO()) + DELIM)
						.append(getString(hDto.getORIGEN()) + DELIM)
						.append(getString(hDto.getESTADORETRIES()) + DELIM)
						.append(getDateFormat(hDto.getCREADO_EL()) + DELIM)
						.append(getString(hDto.getRETRIES()) + DELIM)
						.append(getString(hDto.getEXITO().equalsIgnoreCase("N") ? "No" : "Si") + DELIM)
						.append(getDateFormat(hDto.getFECHAPORTACION()) + DELIM)
						.append(getString(hDto.getOPERADORDONANTE()) + DELIM)
						.append(getString(hDto.getDISTRIBUIDOR()) + DELIM)
						.append(getString(hDto.getEJECUTIVO()) + DELIM)
						.append(getString(hDto.getIDPORTABILIDAD()) + DELIM)
						.append(getString(hDto.getORIGEN()) + DELIM)
						.append(NEXT_LINE);
			}
			tempFile = new File(
					"reportes_creados/ReporteReintentosPortabilidad-" + dateFormat.format(new Date()) + ".csv");
			log.debug(PATH + tempFile.getAbsolutePath() + FILE + tempFile.getAbsoluteFile());
			try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
				outputStream.write(fw.toString().getBytes());
				outputStream.flush();
				Filedownload.save(tempFile, APP_FILE);
			}
		} catch (Exception e) {
			log.error("Error en createReportHistory. ", e);
		}
	}

	public void createReportHistoryPortoutDTO(List<PortoutDTO> history) {
		log.debug("Inicia createReportHistoryPortoutDTO. " + history.size() + FILAS);
		File tempFile = null;
		try {
			StringBuilder fw = new StringBuilder();
			fw.append("TCPORTPORTARECHAZADALOGID;NUMERO_PORTAR;OBSERVACIONES;CREADO_EL").append(NEXT_LINE);
			for (PortoutDTO hDto : history) {
				fw.append(getString(hDto.getTCPORTPORTARECHAZADALOGID())).append(DELIM)
						.append(getString(hDto.getNUMERO_PORTAR())).append(DELIM)
						.append(getString(hDto.getOBSERVACIONES())).append(DELIM)
						.append(getString(hDto.getCREADO_EL()))
						.append(NEXT_LINE);
			}
			tempFile = new File("reportes_creados/ReportePortout-" + dateFormat.format(new Date()) + ".csv");
			log.debug(PATH + tempFile.getAbsolutePath() + FILE + tempFile.getAbsoluteFile());
			try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
				outputStream.write(fw.toString().getBytes());
				outputStream.flush();
				Filedownload.save(tempFile, APP_FILE);
			}
		} catch (Exception e) {
			log.error("Error en createReportHistory. ", e);
		}
	}

	public void cleanUp(Path path) throws IOException {
		Files.delete(path);
	}

	private String getString(String st) {
		return st != null ? st.trim() : "";
	}

	private String getDateFormat(Date val) {
		return val != null ? dateFormat2.format(val) : "";
	}

}
