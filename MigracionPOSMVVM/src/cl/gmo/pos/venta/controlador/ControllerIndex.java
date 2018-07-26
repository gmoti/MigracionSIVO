package cl.gmo.pos.venta.controlador;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;


public class ControllerIndex {
	
	Session sess;
	String sucursal="T002";
	String nombreSucural="GMO Tienda Prueba";	
	int caja = 1201;
	String glprofile = "HARRINGTON";
	String gldescripcion = "CHRISTOPHER HARRINGTON";
	String agente="HARRINGTON";
	
	@Init
	public void inicio() {
		sess = Sessions.getCurrent();
		sess.setAttribute("sucursal", sucursal);
		sess.setAttribute("nombreSucural", nombreSucural);
		sess.setAttribute("caja", 1201);		
		sess.setAttribute("glprofile", "HARRINGTON");
		sess.setAttribute("gldescripcion", "CHRISTOPHER HARRINGTON");
		sess.setAttribute("agente", "HARRINGTON");
		sess.setAttribute("usuario", "HARRINGTON");
	}
	

}
