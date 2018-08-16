package cl.gmo.pos.venta.controlador;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import cl.gmo.pos.venta.controlador.presupuesto.PresupuestoHelper;
import cl.gmo.pos.venta.controlador.ventaDirecta.VentaPedidoDispatchActions;
import cl.gmo.pos.venta.utils.Constantes;
import cl.gmo.pos.venta.web.beans.ClienteBean;
import cl.gmo.pos.venta.web.beans.GraduacionesBean;
import cl.gmo.pos.venta.web.beans.ProductosBean;
import cl.gmo.pos.venta.web.forms.VentaPedidoForm;

public class ControllerEncargos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3904397835765271540L;
	
	Session sess = Sessions.getCurrent();
	PresupuestoHelper helper = new PresupuestoHelper();
	HashMap<String,Object> objetos;
	private Window wBusqueda;
	private boolean bWin=true;
	
	SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat tt = new SimpleDateFormat("hh:mm:ss");		
	private VentaPedidoForm ventaPedidoForm;
	private VentaPedidoDispatchActions ventaPedidoDispatchActions;
	
	private ClienteBean cliente;
	private Double esfera;
	private Double cilindro;
	private Double diametro;
	private String fecha_grad;
	private String numero_grad;
	private String ojo2;
	
	private String fpagoDisable;
	private String agenteDisable;
	private Date fecha;
	private Date fechaEntrega;
	private String sucursal;
	
	
	@Init
	public void incio() {
		
		ventaPedidoForm            = new VentaPedidoForm();
		ventaPedidoDispatchActions = new VentaPedidoDispatchActions();
		cliente = new ClienteBean();
		esfera  =0.00;
		cilindro=0.00;
		diametro=0.00;
		fecha_grad ="";
		numero_grad="";
		ojo2="";
		
		fpagoDisable="True";
		agenteDisable="True";	
		
		fecha= new Date(System.currentTimeMillis());
		fechaEntrega= new Date(System.currentTimeMillis());		
		//ventaPedidoForm.setFecha(dt.format(new Date(System.currentTimeMillis())));
		//ventaPedidoForm.setHora(tt.format(new Date(System.currentTimeMillis())));
		sucursal = sess.getAttribute(Constantes.STRING_SUCURSAL).toString();
		
		ventaPedidoForm = ventaPedidoDispatchActions.cargaInicial(ventaPedidoForm, sucursal, sess);
	}

	
	@NotifyChange({"ventaPedidoForm"})
	@Command
	public void buscarCliente() {
		
		try {			
			
			ventaPedidoForm.setEstaGrabado(2);
			cliente = helper.traeClienteSeleccionado(ventaPedidoForm.getNif(),null);
			
			if (!cliente.getNif().equals("")) {			
				
				ventaPedidoForm.setNif(cliente.getNif());
				ventaPedidoForm.setDvnif(cliente.getDvnif());
				ventaPedidoForm.setNombre_cliente(cliente.getNombre() + " " + cliente.getApellido());
				ventaPedidoForm.setCliente(cliente.getCodigo());
				
				GraduacionesBean graduacion = helper.traeUltimaGraduacionCliente(cliente.getCodigo());	
				ventaPedidoForm.setGraduacion(graduacion);
				
				sess.setAttribute("nombre_cliente",cliente.getNombre() + " " + cliente.getApellido());			
				sess.setAttribute(Constantes.STRING_CLIENTE, cliente.getCodigo());
	        	sess.setAttribute(Constantes.STRING_CLIENTE_VENTA, cliente.getCodigo());	        	
	        	sess.setAttribute("NOMBRE_CLIENTE",cliente.getNombre() + " " + cliente.getApellido());	
	        	
	        	ventaPedidoForm.setAccion("agregarCliente");
	        	ventaPedidoForm.setFlujo(Constantes.STRING_FORMULARIO);                 
	        	
					
			}else {
				Messagebox.show("El cliente no existe");
			}
				
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
	}
	
	
	@NotifyChange({"ventaPedidoForm"})
	@Command
	public void nuevoFormulario() {
		
		ventaPedidoForm = ventaPedidoDispatchActions.nuevoFormulario(ventaPedidoForm, sess);
		
		if (!bWin) {
			wBusqueda.detach();
			bWin=true;
		}
		
	}
	
	
	@Command
	public void buscaProducto() {		
		
		if (bWin) {
			objetos = new HashMap<String,Object>();
			objetos.put("objetoForm",ventaPedidoForm);		
			wBusqueda = (Window)Executions.createComponents(
	                "/zul/presupuestos/SearchProducto.zul", null, objetos);
			
			wBusqueda.doModal();
			bWin=false;
		}else {
			wBusqueda.setVisible(true);
		}
       
	}
	
	@NotifyChange({"ventaPedidoForm"})
    @GlobalCommand
	public void actProdGridVentaPedido(@BindingParam("producto")ProductosBean arg) {		
		
		
		/*productoBean = arg;
		productoBean.setImporte(productoBean.getPrecio());
		productoBean.setCantidad(1);	
		
		productos.add(productoBean);
			
		actTotal(productos);*/
		System.out.println("estoy en otro controlador de venta pedido");				
	}
	
	
	
	
	//Getter and Setter
	
	public VentaPedidoForm getVentaPedidoForm() {
		return ventaPedidoForm;
	}

	public void setVentaPedidoForm(VentaPedidoForm ventaPedidoForm) {
		this.ventaPedidoForm = ventaPedidoForm;
	}

	public Double getEsfera() {
		return esfera;
	}

	public void setEsfera(Double esfera) {
		this.esfera = esfera;
	}

	public Double getCilindro() {
		return cilindro;
	}

	public void setCilindro(Double cilindro) {
		this.cilindro = cilindro;
	}

	public Double getDiametro() {
		return diametro;
	}

	public void setDiametro(Double diametro) {
		this.diametro = diametro;
	}

	public String getFecha_grad() {
		return fecha_grad;
	}

	public void setFecha_grad(String fecha_grad) {
		this.fecha_grad = fecha_grad;
	}

	public String getNumero_grad() {
		return numero_grad;
	}

	public void setNumero_grad(String numero_grad) {
		this.numero_grad = numero_grad;
	}
	
	public String getOjo2() {
		return ojo2;
	}

	public void setOjo2(String ojo2) {
		this.ojo2 = ojo2;
	}

	public String getFpagoDisable() {
		return fpagoDisable;
	}
	
	public void setFpagoDisable(String fpagoDisable) {
		this.fpagoDisable = fpagoDisable;
	}

	public String getAgenteDisable() {
		return agenteDisable;
	}

	public void setAgenteDisable(String agenteDisable) {
		this.agenteDisable = agenteDisable;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public Date getFechaEntrega() {
		return fechaEntrega;
	}

	public void setFechaEntrega(Date fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}
	
	
	
}
