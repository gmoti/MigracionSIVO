package cl.gmo.pos.venta.controlador;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import cl.gmo.pos.venta.utils.Constantes;


public class ControllerIndex {
	
	Session sess;
	String sucursal="T002";
	String nombre_sucural="GMO Tienda Prueba";	
	
	//String sucursal="T004";
	//String nombre_sucural="GMO MALL PARQUE ARAUCO";
	
	
	int caja = 1201;
	String glprofile = "HARRINGTON";
	String gldescripcion = "CHRISTOPHER HARRINGTON";
	String agente="HARRINGTON";
	
	@Init
	public void inicio() {
		sess = Sessions.getCurrent();
		sess.setAttribute(Constantes.STRING_SUCURSAL, sucursal);
		sess.setAttribute(Constantes.STRING_NOMBRE_SUCURSAL, nombre_sucural);
		sess.setAttribute("caja", 1201);		
		sess.setAttribute("glprofile", "HARRINGTON");
		sess.setAttribute("gldescripcion", "CHRISTOPHER HARRINGTON");
		sess.setAttribute("agente", "HARRINGTON");
		sess.setAttribute("usuario", "HARRINGTON");
	}
	

}
