package cl.gmo.pos.venta.controlador;



import java.util.ArrayList;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.MessageboxDlg;

import cl.gmo.pos.venta.controlador.ventaDirecta.SeleccionPagoDispatchActions;
import cl.gmo.pos.venta.controlador.ventaDirecta.VentaDirectaDispatchActions;
import cl.gmo.pos.venta.utils.Constantes;
import cl.gmo.pos.venta.web.Integracion.DAO.DAOImpl.UtilesDAOImpl;
import cl.gmo.pos.venta.web.beans.ClienteBean;
import cl.gmo.pos.venta.web.beans.FormaPagoBean;
import cl.gmo.pos.venta.web.beans.ProductosBean;
import cl.gmo.pos.venta.web.forms.SeleccionPagoForm;
import cl.gmo.pos.venta.web.forms.VentaDirectaForm;


public class ControllerPagoVentaDirecta {
	
	Session sess = Sessions.getCurrent();
	
	@Wire("#winPagoVentaDirecta")
	private Window win;
	
	private SeleccionPagoForm seleccionPagoForm;
	private ClienteBean cliente;
	private VentaDirectaForm ventaDirectaForm;
	private SeleccionPagoDispatchActions seleccionPagoDispatchActions;
	private VentaDirectaDispatchActions ventaDirectaDispatchActions;
	
	
	private UtilesDAOImpl utilesDAOImpl;
	private FormaPagoBean formaPagoBean;
	private ArrayList<FormaPagoBean> listaFormasPago;
	
	@Init
	public void inicio(@ContextParam(ContextType.VIEW) Component view, 
					   @ExecutionArgParam("cliente")ClienteBean arg,
					   @ExecutionArgParam("pagoForm")SeleccionPagoForm arg2,
					   @ExecutionArgParam("ventaDirectaForm")VentaDirectaForm arg3) {
		
		
		Selectors.wireComponents(view, this, false);
		
		formaPagoBean = new FormaPagoBean();
		utilesDAOImpl = new UtilesDAOImpl();		
		cargaFormaPago();		
		
		seleccionPagoDispatchActions = new SeleccionPagoDispatchActions();
		ventaDirectaDispatchActions = new VentaDirectaDispatchActions(); 
		
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
		
		try {
			
			//verificaImpresion() ;
			//System.out.println("regreso");
			
			
			seleccionPagoForm = seleccionPagoDispatchActions.IngresaPago(seleccionPagoForm, sess);	
			
			String d = seleccionPagoForm.getSerie();
		
			//si el pago es exitoso
			
			ventaDirectaForm.setAccion(Constantes.STRING_PAGO_EXITOSO);			
			sess.setAttribute(Constantes.STRING_TICKET, ventaDirectaForm.getEncabezado_ticket() + "/" + ventaDirectaForm.getNumero_ticket());
			sess.setAttribute(Constantes.STRING_TIPO_DOCUMENTO, seleccionPagoForm.getTipo_doc());
			sess.setAttribute(Constantes.STRING_LISTA_PRODUCTOS_ADICIONALES, new ArrayList<ProductosBean>());
			sess.setAttribute(Constantes.STRING_DOCUMENTO, 0);
			sess.setAttribute("SeleccionPagoForm", seleccionPagoForm);
			sess.setAttribute(Constantes.STRING_TIPO_ALBARAN, ventaDirectaForm.getTipoAlbaran());
			
			
			
			//ventaDirectaForm.set
			ventaDirectaDispatchActions.IngresaVentaDirecta(ventaDirectaForm, sess);
			
			
			
			//Messagebox.show("pago efectuado");
			//win.detach();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void verificaImpresion() {		
		
		/*ArrayList<Button> botones = new ArrayList<Button>();
		
		botones.add(Messagebox.Button.OK);
		botones.add(Messagebox.Button.OK);
		
		Event w;
		
		Messagebox.show("pregunta", botones, new org.zkoss.zk.ui.event.EventListener<ClickEvent>());
		
		*/

				
        
	}
	
	
    
	
	//getter and setter
	
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
	
	
	
	
}
