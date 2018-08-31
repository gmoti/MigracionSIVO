package cl.gmo.pos.venta.controlador;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zul.Window;

import cl.gmo.pos.venta.web.forms.GraduacionesForm;

public class ControllerGraduacionCliente extends GraduacionesForm{
	@Command
	public void cerrar(@BindingParam("arg1")  Window x) {
	    x.detach();
	}
}
