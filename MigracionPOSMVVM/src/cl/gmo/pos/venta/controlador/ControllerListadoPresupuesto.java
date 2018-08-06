package cl.gmo.pos.venta.controlador;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Window;

import cl.gmo.pos.venta.reporte.dispatch.ListadoPresupuestosDispatchActions;
import cl.gmo.pos.venta.reporte.nuevo.ReportesHelper;
import cl.gmo.pos.venta.utils.Constantes;
import cl.gmo.pos.venta.web.beans.DivisaBean;
import cl.gmo.pos.venta.web.beans.FormaPagoBean;
import cl.gmo.pos.venta.web.forms.ListadoPresupuestosForm;




public class ControllerListadoPresupuesto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 984154573804131507L;

	Session sess = Sessions.getCurrent();
	
	@Wire("#reporte4")
	private Window win;
	
	private AMedia fileContent;	
	private String nif;
	private String local;
	private String nombre_sucursal;
	private byte[] bytes;
	private Date fechaInicio;
	private Date fechaFin;
	
	private DivisaBean divisaBean;
	private FormaPagoBean formaPagoBean;

	private ListadoPresupuestosForm listadoPresupuestosForm;
	private ReportesHelper reportesHelper;
	private ListadoPresupuestosDispatchActions listadoPresupuestosDispatchActions;
	
	
	@AfterCompose
	public void initSetup(@ContextParam(ContextType.VIEW) Component view)	             {
	     Selectors.wireComponents(view, this, false);
	     
	}
	
	@Init
	public void inicial()  { 		
		
		local = (String) sess.getAttribute("sucursal");	
		nombre_sucursal = (String)sess.getAttribute("nombre_sucural");
		
		divisaBean = new DivisaBean() ;
		formaPagoBean = new FormaPagoBean();		
		
		listadoPresupuestosForm = new ListadoPresupuestosForm() ;		
		listadoPresupuestosDispatchActions = new ListadoPresupuestosDispatchActions() ;
		reportesHelper = new ReportesHelper(); 		
		
	}
	
	@NotifyChange({"fileContent"})
	@Command
	public void reporte() {	
		
		SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
		String fechaI = dt.format(fechaInicio);
		String fechaF = dt.format(fechaFin);
		
		sess.setAttribute(Constantes.STRING_REPORTER_NOMBRE_SUCURSAL, sess.getAttribute(Constantes.STRING_NOMBRE_SUCURSAL));
		listadoPresupuestosForm.setCerrado("N");
		
		listadoPresupuestosForm.setFechaInicio(fechaI);
		listadoPresupuestosForm.setFechaTermino(fechaF);
		
		
		listadoPresupuestosDispatchActions.buscar(listadoPresupuestosForm, sess);
		bytes =  reportesHelper.creaListadoPresupuestos(sess);
		
		final AMedia media = new AMedia("prueba.pdf", "pdf", "application/pdf", bytes);		
		fileContent = media;
		
	}

	public AMedia getFileContent() {
		return fileContent;
	}

	public void setFileContent(AMedia fileContent) {
		this.fileContent = fileContent;
	}

	public ListadoPresupuestosForm getListadoPresupuestosForm() {
		return listadoPresupuestosForm;
	}

	public void setListadoPresupuestosForm(ListadoPresupuestosForm listadoPresupuestosForm) {
		this.listadoPresupuestosForm = listadoPresupuestosForm;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public DivisaBean getDivisaBean() {
		return divisaBean;
	}

	public void setDivisaBean(DivisaBean divisaBean) {
		this.divisaBean = divisaBean;
	}

	public FormaPagoBean getFormaPagoBean() {
		return formaPagoBean;
	}

	public void setFormaPagoBean(FormaPagoBean formaPagoBean) {
		this.formaPagoBean = formaPagoBean;
	}
	
	
	
	
	

}
