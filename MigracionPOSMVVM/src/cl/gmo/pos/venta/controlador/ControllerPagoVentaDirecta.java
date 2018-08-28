package cl.gmo.pos.venta.controlador;

import java.io.Serializable;
import java.util.HashMap;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import cl.gmo.pos.venta.controlador.ventaDirecta.SeleccionPagoDispatchActions;
import cl.gmo.pos.venta.utils.Constantes;
import cl.gmo.pos.venta.web.beans.ClienteBean;
import cl.gmo.pos.venta.web.beans.FormaPagoBean;
import cl.gmo.pos.venta.web.beans.PagoBean;
import cl.gmo.pos.venta.web.forms.SeleccionPagoForm;



public class ControllerPagoVentaDirecta implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3283893161345500968L;

	Session sess = Sessions.getCurrent();
	
	@Wire("#winPagoVentaDirecta")
	private Window win;
	
	HashMap<String,Object> objetos;
	
	private SeleccionPagoForm seleccionPagoForm;
	private ClienteBean cliente;	
	private SeleccionPagoDispatchActions seleccionPagoDispatchActions;
	private Integer diferencia_total;
	
	private FormaPagoBean formaPagoBean;
	private String disableDescuento;
	private PagoBean pagoBeanAux;
	private String origen;
	private BeanControlBotones controlBotones;
	
	@Init
	public void inicio(@ContextParam(ContextType.VIEW) Component view, 
					   @ExecutionArgParam("cliente")ClienteBean arg,
					   @ExecutionArgParam("pagoForm")SeleccionPagoForm arg2,
					   @ExecutionArgParam("ventaOrigenForm")Object arg3,
					   @ExecutionArgParam("origen")String arg4) {
		
		
		Selectors.wireComponents(view, this, false);
		
		cliente           = null;		
		seleccionPagoForm = null;
		controlBotones = new BeanControlBotones();
		controlBotones.setEnableGenerico1("false");
		
		formaPagoBean = new FormaPagoBean();
		pagoBeanAux   = new PagoBean();					
		
		seleccionPagoDispatchActions = new SeleccionPagoDispatchActions();		
		
		cliente           = (ClienteBean)arg;		
		seleccionPagoForm = (SeleccionPagoForm)arg2;
		origen            = (String)arg4;
		
		seleccionPagoDispatchActions.carga_formulario(seleccionPagoForm, sess, seleccionPagoForm.getFecha());
		seleccionPagoForm.setOrigen(origen);
		this.setDiferencia_total(seleccionPagoForm.getV_total());
		
		if (origen.equals("G"))
		   controlBotones.setEnableGenerico1("true");
		
		System.out.println("en init");
	}

	
	@NotifyChange({"seleccionPagoForm","formaPagoBean"})
	@Command
	public void pagarVenta() {
		
		int correcto = 1;	
		
		//grabar variables de sesion para el pago
		sess.setAttribute(Constantes.STRING_LISTA_PAGOS, seleccionPagoForm.getListaPagos());
		sess.setAttribute(Constantes.STRING_TOTAL, seleccionPagoForm.getV_a_pagar());
		sess.setAttribute(Constantes.STRING_LISTA_FORMAS_PAGOS, seleccionPagoForm.getListaFormasPago());
		sess.setAttribute(Constantes.STRING_FECHA, seleccionPagoForm.getFecha());			
		
		if(seleccionPagoForm.getEstado().equals("PAGADO_TOTAL")) {			
			Messagebox.show("No hay saldos pendientes por pagar");
			correcto = 0;			
		}else {
			
			if(seleccionPagoForm.getV_a_pagar()==0) {				
				if (seleccionPagoForm.getDescuento() != 100) {
					correcto = 0;					
					Messagebox.show("El monto a pagar debe ser mayor a cero");
				}
			}else {
				if (formaPagoBean.getId().equals("")) {					
					Messagebox.show("Debe ingresar una forma de pago");
					correcto = 0;
				}
				else
				{
					if(getDiferencia_total() == 0)
					{						
						Messagebox.show("No hay saldos pendientes por pagar");
						correcto = 0;
					}
				}			
			}		
		}
		
		
		if (correcto == 1) {			
			try {
				
				//asigno el tipo de pago seleccionado
				seleccionPagoForm.setForma_pago(formaPagoBean.getId());
				//seleccionPagoForm.setTipo_doc('B');				
				//seleccionPagoForm.setOrigen(origen);
				
				seleccionPagoForm.setAccion("pagar");
				seleccionPagoForm= seleccionPagoDispatchActions.IngresaPago(seleccionPagoForm, sess);				
				
			} catch (Exception e) {				
				e.printStackTrace();
			}				
		}
		
		
		//formaPagoBean = null;	
		
		//en caso de ser completamente pagado				    
		    
	    if (seleccionPagoForm.getV_a_pagar() == 0) {		    	
	    	Messagebox.show("Boleta ?","Seleccion Documento", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
				
				@Override
				public void onEvent(Event e) throws Exception {
					
					if( ((Integer) e.getData()).intValue() == Messagebox.YES ) {
						
						seleccionPagoForm.setAccion("valida_boleta");
						seleccionPagoForm.setTipo_doc('B');
						seleccionPagoForm = seleccionPagoDispatchActions.IngresaPago(seleccionPagoForm, sess);	
						BindUtils.postGlobalCommand(null, null, "creaPagoExitoso", null);						
						
						win.detach();
						
						
					}						
				}
			});		    	
	    } 			
	}
	
	
		
	
	
	//validaciones sobre el pago
	@NotifyChange({"seleccionPagoForm","diferencia_total"})
	@Command
	public void validaPago() {		
		System.out.println("validando pago");
		
		int diferencia;		
		
		diferencia= this.diferencia_total - seleccionPagoForm.getV_a_pagar();
		seleccionPagoForm.setDiferencia(diferencia);		
		
		if (seleccionPagoForm.getDiferencia() < 0) {			
			seleccionPagoForm.setV_a_pagar(0);
			seleccionPagoForm.setDiferencia(this.diferencia_total);
			Messagebox.show("La diferencia no puede ser menor a 0");		
			
		}
		
		this.setDiferencia_total(seleccionPagoForm.getDiferencia());
	}
	
	
	@NotifyChange({"seleccionPagoForm","disableDescuento"})
	@Command
	public void aplicaDescuento() {
		
		
		/*if (seleccionPagoForm.getDescuento() != ventaDirectaForm.getDescuentoTotal()) {
			
			Double descuento_max = Double.parseDouble(String.valueOf(ventaDirectaForm.getPorcentaje_descuento_max()));
			Double dto = ventaDirectaForm.getDescuentoTotal();
			
			if (dto <= descuento_max) {
				
				seleccionPagoForm.setDescuento(ventaDirectaForm.getDescuentoTotal());
				seleccionPagoForm.setAccion("descuento_directa");
				
				try {
					seleccionPagoForm =seleccionPagoDispatchActions.IngresaPago(seleccionPagoForm, sess);
					
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
			}else {
				
				
			}			
		}	*/	
		
	}
	
	
	/*function calculaTotalvtaDirecta()
	{
		if (document.getElementById('dto').value != document.getElementById('descuentoTotal').value) {
			
			var descuento_max = parseFloat(document.getElementById('descuento_max').value);
			var dto = parseFloat(document.getElementById('descuentoTotal').value);
			
			if (parseInt(dto) <= parseInt(descuento_max)) {
				document.getElementById('accion').value="descuento_directa";
				document.getElementById('dto').value = document.getElementById('descuentoTotal').value;
				document.seleccionPagoForm.submit();
			} 
			else 
			{
				
					alert("El descuento debe ser menor o igual al " + descuento_max + "%");
					document.getElementById('descuentoTotal').value = document.getElementById('dto').value;
			}
		}
		
		
	}*/
	
	
	
	
	@NotifyChange({"seleccionPagoForm"})	
	@Command
	public void deleteItemAutoriza(@BindingParam("arg")PagoBean b){
		
		objetos = new HashMap<String,Object>();		
		objetos.put("seleccionPagoForm",seleccionPagoForm);
		pagoBeanAux = new PagoBean();
		pagoBeanAux = b;
		
		Window win = (Window)Executions.createComponents(
                "/zul/venta_directa/AutorizaBorrarPago.zul", null, objetos);
		
		win.doModal();			
	}
	
	
	@NotifyChange({"seleccionPagoForm"})	
	@GlobalCommand
	public void deleteItem() {
		
		seleccionPagoForm.getListaPagos().remove(pagoBeanAux);
		pagoBeanAux=null;
	}
	
	
    
	
	//getter and setter
	//==============================
	
	public SeleccionPagoForm getSeleccionPagoForm() {
		return seleccionPagoForm;
	}

	public void setSeleccionPagoForm(SeleccionPagoForm seleccionPagoForm) {
		this.seleccionPagoForm = seleccionPagoForm;
	}

	public ClienteBean getCliente() {
		return cliente;
	}

	public void setCliente(ClienteBean cliente) {
		this.cliente = cliente;
	}

	public FormaPagoBean getFormaPagoBean() {
		return formaPagoBean;
	}

	public void setFormaPagoBean(FormaPagoBean formaPagoBean) {
		this.formaPagoBean = formaPagoBean;
	}	
	
	public Integer getDiferencia_total() {
		return diferencia_total;
	}

	public void setDiferencia_total(Integer diferencia_total) {
		this.diferencia_total = diferencia_total;
	}

	public String getDisableDescuento() {
		return disableDescuento;
	}

	public void setDisableDescuento(String disableDescuento) {
		this.disableDescuento = disableDescuento;
	}

	public BeanControlBotones getControlBotones() {
		return controlBotones;
	}

	public void setControlBotones(BeanControlBotones controlBotones) {
		this.controlBotones = controlBotones;
	}	
	
}
