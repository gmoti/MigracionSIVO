package cl.gmo.pos.venta.controlador;

import java.io.Serializable;
import java.util.HashMap;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

public class ControllerMenuPrincipal implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7591341824630787025L;
	private Window window;
	HashMap<String,Object> objetos;
	
	
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
			window.doModal();
	        break;
			
		case "M1_2":
			window = (Window)Executions.createComponents(
	                "/zul/presupuestos/presupuesto.zul", null, null);			
	        window.doModal();
	        break;
	        
		case "M1_3":
			objetos = new HashMap<String,Object>();
			objetos.put("origen", "menu");
			window = (Window)Executions.createComponents(
	                "/zul/encargos/encargos.zul", null, objetos);			
	        window.doModal();
	        break;    
			
	        //mantenedores
		case "M2_1":	
			window = (Window)Executions.createComponents(
	                "/zul/mantenedores/Cliente.zul", null, null);
			
	        window.doModal();
	        break;
		
		case "M2_2":	
			window = (Window)Executions.createComponents(
	                "/zul/mantenedores/GraduacionCliente.zul", null, null);
			
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
