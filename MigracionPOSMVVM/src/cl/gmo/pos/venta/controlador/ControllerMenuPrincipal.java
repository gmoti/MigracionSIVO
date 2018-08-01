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
		
			//transacciones
		case "M1_1":
			window = (Window)Executions.createComponents(
	                "/zul/venta_directa/VentaDirecta.zul", null, null);
			
		case "M1_2":
			window = (Window)Executions.createComponents(
	                "/zul/presupuestos/presupuesto.zul", null, null);	
			
	        window.doModal();
	        break;
			
	        //mantenedores
		case "M2_1":	
			window = (Window)Executions.createComponents(
	                "/zul/Cliente.zul", null, null);
			
	        window.doModal();
	        break;
		
	        //reportes
		case "M3_1":			
			window = (Window)Executions.createComponents(
	                "/zul/reportes/ListadoInformeOptico.zul", null, null);
			
	        window.doModal();			
			break;
			
		case "M3_2":
			
			window = (Window)Executions.createComponents(
	                "/zul/reportes/ListadoTotalDia.zul", null, null);
			
	        window.doModal();			
			break;
			
		case "M3_3":
			
			window = (Window)Executions.createComponents(
	                "/zul/reportes/ListadoBoletas.zul", null, null);
			
	        window.doModal();			
			break;
			
		case "M3_4":
			
			window = (Window)Executions.createComponents(
	                "/zul/reportes/ListadoPresupuestos.zul", null, null);
			
	        window.doModal();			
			break;	

		default:
			break;
		}
		
		
	}

}
