package cl.gmo.pos.venta.controlador;

import java.io.Serializable;
import java.util.HashMap;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

import cl.gmo.pos.venta.controlador.presupuesto.BusquedaConveniosDispatchActions;
import cl.gmo.pos.venta.web.beans.ConvenioBean;
import cl.gmo.pos.venta.web.forms.BusquedaConveniosForm;


public class ControllerBusquedaConvenio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8233031087916039002L;
	Session sess = Sessions.getCurrent();
	
	private BusquedaConveniosForm busquedaConveniosForm;
	private BusquedaConveniosDispatchActions busquedaConveniosDispatchActions;
	
	
	HashMap<String,Object> objetos;
	
	@Init
	public void inicial() {
            
		busquedaConveniosForm = new BusquedaConveniosForm(); 
		busquedaConveniosDispatchActions = new BusquedaConveniosDispatchActions();		
		busquedaConveniosDispatchActions.cargaBusquedaConvenios(busquedaConveniosForm, sess);           
	}	
	
	
	@NotifyChange({"busquedaConveniosForm"})
	@Command
	public void buscarConvenios() {
		
		busquedaConveniosForm.setAccion("buscar");
		busquedaConveniosDispatchActions.buscar(busquedaConveniosForm, sess);		
	}
	
	
	@NotifyChange({"busquedaConveniosForm"})
	@Command
	public void seleccionaConvenio(@BindingParam("arg")ConvenioBean arg) {
		
		int index=0;
		objetos = new HashMap<String,Object>();		
		
		
		for(ConvenioBean cb : busquedaConveniosForm.getLista_convenios()) {			
			if(cb.getId().equals(arg.getId())) {
				break;
			}
			index++;
		}
		
		sess.setAttribute("indice", index);	
		//busquedaConveniosDispatchActions.selecciona_convenio(busquedaConveniosForm, sess);
		objetos.put("busquedaConvenios",busquedaConveniosForm);
		objetos.put("origen","convenio");
		
		//se llama ventana convenio
		Window window = (Window)Executions.createComponents(
                "/zul/presupuestos/SeleccionaConvenio.zul", null, objetos);		
        window.doModal();	
		
	}
	
	
	
	
	//=============== getter and setter =================
	
	public BusquedaConveniosForm getBusquedaConveniosForm() {
		return busquedaConveniosForm;
	}

	public void setBusquedaConveniosForm(BusquedaConveniosForm busquedaConveniosForm) {
		this.busquedaConveniosForm = busquedaConveniosForm;
	}        
	
	

}
