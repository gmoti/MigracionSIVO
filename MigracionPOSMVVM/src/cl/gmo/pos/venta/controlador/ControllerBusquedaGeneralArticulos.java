package cl.gmo.pos.venta.controlador;

import java.io.Serializable;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import cl.gmo.pos.venta.web.forms.InformeBusquedaProductoForm;


public class ControllerBusquedaGeneralArticulos implements Serializable {

	
	private static final long serialVersionUID = -7019940900649604869L;
	Session sess = Sessions.getCurrent();
	
	private InformeBusquedaProductoForm informeBusquedaProductoForm;
	
	
	@Init
	public void inicial() {
		
		informeBusquedaProductoForm = new InformeBusquedaProductoForm();
           
	
	}
	
	
	@NotifyChange
	@Command
	public void reporte() {
		
		
	}	
	
	
	//============== Getter and Setter ======================
	
	public InformeBusquedaProductoForm getInformeBusquedaProductoForm() {
		return informeBusquedaProductoForm;
	}


	public void setInformeBusquedaProductoForm(InformeBusquedaProductoForm informeBusquedaProductoForm) {
		this.informeBusquedaProductoForm = informeBusquedaProductoForm;
	}
	
	
	

}
