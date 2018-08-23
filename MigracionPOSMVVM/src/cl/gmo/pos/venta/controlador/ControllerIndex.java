package cl.gmo.pos.venta.controlador;

import java.io.Serializable;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import cl.gmo.pos.venta.controlador.general.LoginActions;
import cl.gmo.pos.venta.utils.Constantes;
import cl.gmo.pos.venta.web.forms.UsuarioForm;


public class ControllerIndex implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5375817743395813578L;
	
	
	Session sess;
	String sucursal="T002";
	String nombre_sucural="GMO Tienda Prueba";	
	
	int caja = 1201;
	String glprofile = "HARRINGTON";
	String gldescripcion = "CHRISTOPHER HARRINGTON";
	String agente="HARRINGTON";
	
	private UsuarioForm usuarioForm;
	private LoginActions loginActions;
	
	
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
		
		usuarioForm = new UsuarioForm();
		loginActions= new LoginActions();
		
	}


	public UsuarioForm getUsuarioForm() {
		return usuarioForm;
	}


	public void setUsuarioForm(UsuarioForm usuarioForm) {
		this.usuarioForm = usuarioForm;
	}
	
	
	
	
	
	

}
