package cl.gmo.pos.venta.controlador;

import java.io.Serializable;
import org.zkoss.bind.annotation.Init;


import cl.gmo.pos.venta.web.forms.SuplementosForm;



public class ControllerAgregaSuplemento implements Serializable {

	private static final long serialVersionUID = 9155273456336143454L;
	
	private SuplementosForm suplementosForm;
	private suplemen
	
	
	@Init
	public void inicial() {	
		
		suplementosForm = new SuplementosForm();

	
	}
	
	
	
	


	//============ Setter and Getter ===============
	
	public SuplementosForm getSuplementosForm() {
		return suplementosForm;
	}

	public void setSuplementosForm(SuplementosForm suplementosForm) {
		this.suplementosForm = suplementosForm;
	}	
	
}
