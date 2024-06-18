package com.ericsson.base;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.consystec.seguridad.corecliente.SeguridadWeb;
import com.consystec.seguridad.orm.SecAplicacion;
import com.consystec.seguridad.orm.SecUsuario;
import com.ericsson.dao.DaoBase;
import com.ericsson.dao.ReporteReintentosPortaDAO;
import com.ericsson.dao.SeguridadDAO;
import com.ericsson.utils.Utils;

public abstract class ComposerBase extends GenericForwardComposer<Window> {

	private static final Logger log = Logger.getLogger(ComposerBase.class);
	private static final long serialVersionUID = 1L;

	private static final String ACTIVE_USER = "activeUser";

	protected String defaultDataSourceName;
	protected String aplicationName;

	private Label lblModuleTitle;
	private Label lblSectionTitleStatic;
	private Label lblSectionTitleDynamic;

	private transient ReporteReintentosPortaDAO parameterDAO = new ReporteReintentosPortaDAO();

	protected abstract String getModuleTitle();

	public DataSource getDataSource(String dataSourceName) throws NamingException {
		InitialContext ctx = new InitialContext();
		return (DataSource) ctx.lookup(dataSourceName);
	}

	public DataSource getDefaultDataSource() throws NamingException {
		InitialContext ctx = new InitialContext();
		return (DataSource) ctx.lookup(defaultDataSourceName);
	}

	public Connection getConnection() throws NamingException, SQLException {
		return getConnection(defaultDataSourceName);
	}

	public Connection getConnection(String dataSourceName) throws SQLException, NamingException {
		return getDataSource(dataSourceName).getConnection();
	}

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		SecUsuario usr = null;
		String securityActivate = "Y";//parameterDAO.getGeneralApplicationParameter("SECURITY_ACTIVATE");
		if (securityActivate != null && securityActivate.equals("N")) {
			log.debug("PRUEBA DESARROLLO");
			usr = new SecUsuario();
			usr.setApellido("Reintentos");
			usr.setNombre("Test");
			usr.setUsuario("test.reintentos");
			Utils.setSessionAttribute(ACTIVE_USER, usr);
			Utils.setSessionAttribute("pruebas", "S");
		} else {
			log.debug("No es testing");
			String url = "";
			String autoriza = Executions.getCurrent().getParameter("autoriza");
			String urlRetorno = Executions.getCurrent().getParameter("urlRetorno");
			String applicationName = "REINTENTOSPORTA";//parameterDAO.getGeneralApplicationParameter("APPLICATION_NAME");
			Connection connSeg = getConexionSeguridad();
			try {
				if (Utils.getSessionAttribute(ACTIVE_USER) == null && (autoriza == null)) {
					url = "";
					if (urlRetorno != null)
						url = urlRetorno;
					else
						url = new SeguridadWeb().obtenerUrlPortal(connSeg, applicationName);
					execution.sendRedirect(url);
				} else {

					PreparedStatement ps = null;
					ResultSet rs = null;
					try {
						ps = connSeg.prepareStatement("SELECT SYSDATE FROM DUAL WHERE 1 = ?");
						ps.setInt(1, 1);
						rs = ps.executeQuery();
						if (rs.next())
							log.debug("Fecha: " + rs.getString(1));
					} catch (Exception e) {
						log.error("Error aqui... ", e);
					} finally {
						DbUtils.closeQuietly(null, ps, rs);
					}
					if (Utils.getSessionAttribute(ACTIVE_USER) == null) {
						SeguridadWeb seg = new SeguridadWeb();
						usr = seg.validaAutorizacion(obtenerFuenteSeguridad(), autoriza, applicationName);
						if (usr == null) {
							Messagebox.show("No se ha podido obtener el usuario autenticado, favor intente de nuevo",
									"Error", Messagebox.OK, Messagebox.ERROR);
							return;
						}
						log.debug("Se obtendrá información de aplicación: " + applicationName);
						SecAplicacion app = seg.obtenerAplicacion(obtenerFuenteSeguridad(), applicationName);
						desktop.getSession().setAttribute("idAplicacion", app.getSecaplicacionid());
						if (desktop.getSession().getAttribute("permisosPerfil") == null) {
							log.debug("Se obtendrán permisos");
							SeguridadDAO dao = new SeguridadDAO();
							BigDecimal perfilResult = dao.getPerfilUsuario(connSeg, app.getSecaplicacionid(),
									usr.getSecusuarioid());
							log.debug("PerfilResult: " + perfilResult);
							String perfil = "0";
							if (perfilResult != null) {
								perfil = perfilResult.toString();
							}
							List<String> permisosPerfil = dao.getPermisosElementos(connSeg, perfil);
							desktop.getSession().setAttribute("permisosPerfil", permisosPerfil);
						}
						if (urlRetorno != null)
							Utils.setSessionAttribute("url", urlRetorno);
						else
							Utils.setSessionAttribute("url", seg.obtenerUrlPortal(connSeg, applicationName));
						Utils.setSessionAttribute(ACTIVE_USER, usr);
					}

				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				DbUtils.closeQuietly(connSeg);
			}
		}
		if (usr != null)
			Utils.setSessionAttribute("user", usr.getUsuario());
		if (desktop.getAttribute("isFirstTime") == null) {
			lblModuleTitle.setValue(getModuleTitle());
			desktop.setAttribute("isFirstTime", "false");
		}
	}

	private DataSource obtenerFuenteSeguridad() throws NamingException {
		return getDataSource(DaoBase.SEGURIDAD);
	}

	private Connection getConexionSeguridad() throws SQLException, NamingException {
		return getConnection(DaoBase.SEGURIDAD);
	}

	protected void showMenu() {
		Clients.evalJavaScript("show_menu()");
	}

	public void reloadPage() {
		Clients.evalJavaScript("reloadPage()");
	}

	protected void setLabelHeaders(String lbHeaders) {
		lblSectionTitleStatic.setValue(lbHeaders);
		lblSectionTitleDynamic.setValue(lbHeaders);
	}

//	public SecUsuario validaAutorizacion(DataSource dts, String param, String app) throws Exception {
//	    SecUsuario usr = null;
//	    String desc = "";
//	    String usuario = "";
//	    String fechaHora = "";
//	    String appParam = "";
//	    boolean tienePermiso = false;
//	    try {
//	      desc = Cifrado.decrypt(param, "PCAORNASMYESTTREOC");
//	      StringTokenizer tokens = new StringTokenizer(desc, "||");
//	      if (tokens.countTokens() != 3)
//	        throw new ExcepcionSeguridad("Error en información de parde autorización"); 
//	      usuario = tokens.nextToken();
//	      fechaHora = tokens.nextToken();
//	      appParam = tokens.nextToken();
//	      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//	      Date fecha = null;
//	      try {
//	        fecha = sdf.parse(fechaHora);
//	      } catch (ParseException e) {}
//	      Date fecSys = null;
//	      try {
//	        fecSys = obtenerFechaSistema(dts);
//	      } catch (Exception e) {
//	        e.printStackTrace();
//	        throw new ExcepcionSeguridad("Ocurriun error al tratar de obtener la fecha del sistema, comuncon su administrador");
//	      } 
//	      if (fecha == null)
//	        throw new ExcepcionSeguridad("La fecha definida en pares nula"); 
//	      if (fecSys.getTime() - fecha.getTime() > 120000L || fecSys.getTime() - fecha.getTime() < 0L)
//	        throw new ExcepcionSeguridad("El parametro de autorizacion ha expirado"); 
//	      usr = (new UsuariosDAO()).obtenerUsuario(dts, usuario);
//	      if (!appParam.equals(app))
//	        throw new ExcepcionSeguridad("La aplicación definida en parámetro no es igual a la definida en el argumento del método"); 
//	      SecAplicacion aplicacion = (new AplicacionesDAO()).obtenerAplicacion(dts, app);
//	      if (aplicacion == null)
//	        throw new ExcepcionSeguridad("La aplicacicon el nombre indicado no existe."); 
//	      tienePermiso = Autorizar.tienePermisoAplicacion(dts, usr, app);
//	      if (!tienePermiso)
//	        throw new ExcepcionSeguridad("El usuario definida en parámetro no tiene permiso para acceder a esta aplicación"); 
//	    } catch (GeneralSecurityException e) {
//	      throw new ExcepcionSeguridad("Ocurrió un error en el cifrado de parámetro", e);
//	    } 
//	    return usr;
//	  }
//	
//	
//	public Date obtenerFechaSistema(DataSource dts) throws Exception {
//	    Date ret = null;
//	    Connection conn = null;
//	    try {
//	      conn = dts.getConnection();
//	      ret = obtenerFechaSistema(conn);
//	    } finally {
//	      if (conn != null)
//	        conn.close(); 
//	      conn = null;
//	    } 
//	    return ret;
//	  }

}
