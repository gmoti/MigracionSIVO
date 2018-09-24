package cl.gmo.pos.venta.controlador;

import java.io.Serializable;

import org.zkoss.bind.annotation.Init;

import cl.gmo.pos.venta.web.forms.BusquedaLiberacionesForm;

public class ControllerLiberacionEncargos implements Serializable {

	
	private static final long serialVersionUID = -648978358513467205L;
	
	private BusquedaLiberacionesForm busquedaLiberacionesForm;
	
	
	@Init	
	public void inicial() {
		
		busquedaLiberacionesForm = new BusquedaLiberacionesForm();
		
		
		
	}
	
	
	
	
	//=============== getter and setter ==============

	public BusquedaLiberacionesForm getBusquedaLiberacionesForm() {
		return busquedaLiberacionesForm;
	}


	public void setBusquedaLiberacionesForm(BusquedaLiberacionesForm busquedaLiberacionesForm) {
		this.busquedaLiberacionesForm = busquedaLiberacionesForm;
	}
	
	
	

}
