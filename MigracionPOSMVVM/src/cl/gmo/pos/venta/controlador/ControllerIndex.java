package cl.gmo.pos.venta.controlador;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;


public class ControllerIndex {
	
	Session sess;
	String sucursal="T004";
	String nombreSucural="GMO Parque Arauco";
	String fechaBusquedaTotal = "17/07/2018";
	String venta_fecha = "17/07/2018";
	
	@Init
	public void inicio() {
		sess = Sessions.getCurrent();
		sess.setAttribute("sucursal", sucursal);
		sess.setAttribute("nombreSucural", nombreSucural);
		sess.setAttribute("fechaBusquedaTotal", fechaBusquedaTotal);
		sess.setAttribute("venta_fecha", venta_fecha);
	}
	

}
