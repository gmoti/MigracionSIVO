package cl.gmo.pos.venta.controlador;

import java.io.Serializable;

//pendiente los productos gratis

import java.util.ArrayList;
import java.util.HashMap;

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
import cl.gmo.pos.venta.controlador.ventaDirecta.VentaDirectaDispatchActions;
import cl.gmo.pos.venta.utils.Constantes;
import cl.gmo.pos.venta.web.Integracion.DAO.DAOImpl.UtilesDAOImpl;
import cl.gmo.pos.venta.web.beans.ClienteBean;
import cl.gmo.pos.venta.web.beans.FormaPagoBean;
import cl.gmo.pos.venta.web.beans.PagoBean;
import cl.gmo.pos.venta.web.beans.ProductosBean;
import cl.gmo.pos.venta.web.forms.SeleccionPagoForm;
import cl.gmo.pos.venta.web.forms.VentaDirectaForm;


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
	private VentaDirectaForm ventaDirectaForm;
	private SeleccionPagoDispatchActions seleccionPagoDispatchActions;
	private VentaDirectaDispatchActions ventaDirectaDispatchActions;
	private Integer diferencia_total;
	
	
	private UtilesDAOImpl utilesDAOImpl;
	private FormaPagoBean formaPagoBean;
	private ArrayList<FormaPagoBean> listaFormasPago;
	private String disableDescuento;
	private PagoBean pagoBeanAux;
	
	
	@Init
	public void inicio(@ContextParam(ContextType.VIEW) Component view, 
					   @ExecutionArgParam("cliente")ClienteBean arg,
					   @ExecutionArgParam("pagoForm")SeleccionPagoForm arg2,
					   @ExecutionArgParam("ventaDirectaForm")VentaDirectaForm arg3) {
		
		
		Selectors.wireComponents(view, this, false);
		
		formaPagoBean = new FormaPagoBean();
		pagoBeanAux   = new PagoBean();
		utilesDAOImpl = new UtilesDAOImpl();		
		cargaFormaPago();		
		
		seleccionPagoDispatchActions = new SeleccionPagoDispatchActions();
		ventaDirectaDispatchActions = new VentaDirectaDispatchActions(); 
		
		cliente           = null;		
		seleccionPagoForm = null;	
		ventaDirectaForm  = null;	
		
		cliente           = (ClienteBean)arg;		
		seleccionPagoForm = (SeleccionPagoForm)arg2;
		ventaDirectaForm  = (VentaDirectaForm)arg3;
		
		seleccionPagoForm.setFecha(ventaDirectaForm.getFecha());
		seleccionPagoForm.setNif(cliente.getNif());
		seleccionPagoForm.setRazon(cliente.getRazon_social());
		seleccionPagoForm.setDireccion(cliente.getDireccion());
		seleccionPagoForm.setProvincia(cliente.getProvincia());
		seleccionPagoForm.setProvincia_descripcion(cliente.getProvincia_desc());
		seleccionPagoForm.setPoblacion(cliente.getPoblacion());
		seleccionPagoForm.setGiro(cliente.getGiro());
		
		seleccionPagoForm.setV_total_parcial(ventaDirectaForm.getSumaTotal());
		seleccionPagoForm.setV_total(ventaDirectaForm.getSumaTotal());
		seleccionPagoForm.setV_a_pagar(ventaDirectaForm.getSumaTotal());
		seleccionPagoForm.setDiferencia(0);	
		seleccionPagoForm.setSerie(ventaDirectaForm.getEncabezado_ticket() + "/" + ventaDirectaForm.getNumero_ticket());		
		
		seleccionPagoForm.setListaFormasPago(listaFormasPago);
		seleccionPagoForm.setFech_pago(ventaDirectaForm.getFecha());
		seleccionPagoForm.setFecha(ventaDirectaForm.getFecha());
		
		this.setDiferencia_total(ventaDirectaForm.getSumaTotal());
		this.setDisableDescuento("FALSE");
		
		
		try {
			seleccionPagoForm.setAccion("");
			seleccionPagoForm= seleccionPagoDispatchActions.IngresaPago(seleccionPagoForm, sess);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("en init");
	}


	public void cargaFormaPago() {
		
		try {
			listaFormasPago = utilesDAOImpl.traeFormasPago();	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	@NotifyChange({"seleccionPagoForm"})
	@Command
	public void pagarVenta() {	
		
		
		
		
		
		
		//grabar variables de sesion para el pago
		sess.setAttribute(Constantes.STRING_LISTA_PAGOS, seleccionPagoForm.getListaPagos());
		sess.setAttribute(Constantes.STRING_TOTAL, seleccionPagoForm.getV_a_pagar());
		sess.setAttribute(Constantes.STRING_LISTA_FORMAS_PAGOS, seleccionPagoForm.getListaFormasPago());
		sess.setAttribute(Constantes.STRING_FECHA, seleccionPagoForm.getFecha());	
		
		//asigno el tipo de pago seleccionado
		seleccionPagoForm.setForma_pago(formaPagoBean.getId());
		seleccionPagoForm.setTipo_doc('B');
		seleccionPagoForm.setAccion("pagar");
		seleccionPagoForm.setOrigen("DIRECTA");
		
		try {
			
			seleccionPagoForm = seleccionPagoDispatchActions.IngresaPago(seleccionPagoForm, sess);	
			
		    System.out.println("getTiene_pagos 		 " + seleccionPagoForm.getTiene_pagos());
		    System.out.println("getV_a_pagar  		 " + seleccionPagoForm.getV_a_pagar());
		    System.out.println("getV_total           " + seleccionPagoForm.getV_total());
		    System.out.println("getV_total_parcial   " + seleccionPagoForm.getV_total_parcial());
		    
		    
		    if (seleccionPagoForm.getV_a_pagar() == 0) {		    	
		    	Messagebox.show("Boleta ?","Seleccion Documento", Messagebox.OK | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
					
					@Override
					public void onEvent(Event e) throws Exception {
						
						if( ((Integer) e.getData()).intValue() == Messagebox.OK ) {
							
							seleccionPagoForm.setAccion("valida_boleta");
							seleccionPagoForm.setTipo_doc('B');
							seleccionPagoForm = seleccionPagoDispatchActions.IngresaPago(seleccionPagoForm, sess);
							creaPagoExitoso();
						}						
					}
				});		    	
		    } 
		    
		    
			
			
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
	}
	
	
	private void creaPagoExitoso() {		
		
		Messagebox.show("Desea Imprimir Ticket de cambio? ","Impresion de tecket", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
			
			@Override
			public void onEvent(Event e) throws Exception {
				
				if( ((Integer) e.getData()).intValue() == Messagebox.YES ) {
					
					//si el pago es exitoso
					
					ventaDirectaForm.setAccion(Constantes.STRING_PAGO_EXITOSO);			
					sess.setAttribute(Constantes.STRING_TICKET, ventaDirectaForm.getEncabezado_ticket() + "/" + ventaDirectaForm.getNumero_ticket());
					sess.setAttribute(Constantes.STRING_TIPO_DOCUMENTO, seleccionPagoForm.getTipo_doc());
					sess.setAttribute(Constantes.STRING_LISTA_PRODUCTOS_ADICIONALES, new ArrayList<ProductosBean>());
					sess.setAttribute(Constantes.STRING_DOCUMENTO, 0);
					sess.setAttribute("SeleccionPagoForm", seleccionPagoForm);
					sess.setAttribute(Constantes.STRING_TIPO_ALBARAN, ventaDirectaForm.getTipoAlbaran());					
					
					ventaDirectaDispatchActions.IngresaVentaDirecta(ventaDirectaForm, sess);		
					win.detach();				
					
				}						
			}
		});	
		
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
		
		
		
		
		/*
		if (seleccionPagoForm.getDescuento() > 0) {
			setDisableDescuento("TRUE");
			nuevoMonto = (int) (seleccionPagoForm.getV_total_parcial() - (seleccionPagoForm.getV_total_parcial() * seleccionPagoForm.getDescuento()/100));
			seleccionPagoForm.setV_total_parcial(nuevoMonto);
		}		*/
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


	public VentaDirectaForm getVentaDirectaForm() {
		return ventaDirectaForm;
	}


	public void setVentaDirectaForm(VentaDirectaForm ventaDirectaForm) {
		this.ventaDirectaForm = ventaDirectaForm;
	}


	public String getDisableDescuento() {
		return disableDescuento;
	}


	public void setDisableDescuento(String disableDescuento) {
		this.disableDescuento = disableDescuento;
	}
	
	
	
	
}
