package cl.gmo.pos.venta.controlador;

import java.io.Serializable;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import cl.gmo.pos.venta.controlador.presupuesto.BusquedaConveniosDispatchActions;
import cl.gmo.pos.venta.web.beans.ConvenioBean;
import cl.gmo.pos.venta.web.beans.ConvenioLnBean;
import cl.gmo.pos.venta.web.forms.BusquedaConveniosForm;

public class ControllerSeleccionaConvenio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4293822807417186796L;
	Session sess = Sessions.getCurrent();
	
	private BusquedaConveniosForm busquedaConveniosForm;
	private BusquedaConveniosDispatchActions busquedaConveniosDispatchActions;
	private ConvenioLnBean convenioLnBean;
	
	
	@Init
	public void inicial(@ExecutionArgParam("busquedaConvenios")BusquedaConveniosForm arg,
						@ExecutionArgParam("origen")String arg2	) {
		
		busquedaConveniosForm = new BusquedaConveniosForm(); 
		busquedaConveniosForm = arg;
		convenioLnBean        = new ConvenioLnBean(); 
		
		if (arg2.equals("convenio")) {
			busquedaConveniosDispatchActions = new BusquedaConveniosDispatchActions();		
			busquedaConveniosDispatchActions.selecciona_convenio(busquedaConveniosForm, sess);
		}
	}
	
	@NotifyChange({"convenioLnBean"})
	@Command
	public void seleccionaConvenio(@BindingParam("arg")ConvenioLnBean arg) {
		
		convenioLnBean = arg;
		
	}

	
	//================ Getter ans Setter =============

	public BusquedaConveniosForm getBusquedaConveniosForm() {
		return busquedaConveniosForm;
	}

	public void setBusquedaConveniosForm(BusquedaConveniosForm busquedaConveniosForm) {
		this.busquedaConveniosForm = busquedaConveniosForm;
	}

	public ConvenioLnBean getConvenioLnBean() {
		return convenioLnBean;
	}

	public void setConvenioLnBean(ConvenioLnBean convenioLnBean) {
		this.convenioLnBean = convenioLnBean;
	}	
	
	
}
