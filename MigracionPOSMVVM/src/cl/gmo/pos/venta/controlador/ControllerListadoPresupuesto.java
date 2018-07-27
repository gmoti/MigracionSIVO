package cl.gmo.pos.venta.controlador;

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
import cl.gmo.pos.venta.web.forms.ListadoPresupuestosForm;
import cl.gmo.pos.venta.web.helper.ListadoPresupuestosHelper;
import cl.gmo.pos.venta.web.helper.ReportesHelper;


public class ControllerListadoPresupuesto {
	
	Session sess = Sessions.getCurrent();
	
	@Wire("#reporte4")
	private Window win;
	
	private AMedia fileContent;	
	private String nif;
	private String local;
	private String nombre_sucursal;
	private byte[] bytes;
	
	
	private ListadoPresupuestosForm listadoPresupuestosForm;
	private ListadoPresupuestosHelper listadoPresupuestosHelper;
	private ListadoPresupuestosDispatchActions listadoPresupuestosDispatchActions;
	
	
	@AfterCompose
	public void initSetup(@ContextParam(ContextType.VIEW) Component view)	             {
	     Selectors.wireComponents(view, this, false);
	     
	}
	
	@Init
	public void inicial()  { 		
		
		local = (String) sess.getAttribute("sucursal");	
		nombre_sucursal = (String)sess.getAttribute("nombre_sucural");
		
		
		listadoPresupuestosForm = new ListadoPresupuestosForm() ;
		//listadoPresupuestosHelper = new ListadoPresupuestosHelper() ;
		listadoPresupuestosDispatchActions = new ListadoPresupuestosDispatchActions() ;
		
		
		
		
	}
	
	@NotifyChange({"fileContent"})
	@Command
	public void reporte() {
		
		
		
		
		//new ReportesHelper().creaListadoPresupuestos(session, response);
		
		
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
	
	
	
	
	

}
