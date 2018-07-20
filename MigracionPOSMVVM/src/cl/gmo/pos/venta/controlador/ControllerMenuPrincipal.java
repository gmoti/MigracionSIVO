package cl.gmo.pos.venta.controlador;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

public class ControllerMenuPrincipal {
	
	private Window window;
	
	
	@Init
	public void inicial() {
		window=null;
	}
	
	
	@Command
	public void seleccionMenu(@BindingParam("arg")String arg ) {
		
		switch (arg) {
		
		case "M1_1":
			window = (Window)Executions.createComponents(
	                "/zul/VentaDirecta.zul", null, null);
			
	        window.doModal();
	        break;
			
		case "M2_1":	
			window = (Window)Executions.createComponents(
	                "/zul/Cliente.zul", null, null);
			
	        window.doModal();
	        break;
		
		case "M3_2":
			
			window = (Window)Executions.createComponents(
	                "/zul/ListadoTotalDia.zul", null, null);
			
	        window.doModal();			
			break;

		default:
			break;
		}
		
		
	}

}
