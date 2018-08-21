package cl.gmo.pos.venta.controlador;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import cl.gmo.pos.venta.controlador.presupuesto.PresupuestoHelper;
import cl.gmo.pos.venta.controlador.ventaDirecta.VentaPedidoDispatchActions;
import cl.gmo.pos.venta.utils.Constantes;
import cl.gmo.pos.venta.web.beans.AgenteBean;
import cl.gmo.pos.venta.web.beans.ClienteBean;
import cl.gmo.pos.venta.web.beans.DivisaBean;
import cl.gmo.pos.venta.web.beans.FamiliaBean;
import cl.gmo.pos.venta.web.beans.FormaPagoBean;
import cl.gmo.pos.venta.web.beans.GraduacionesBean;
import cl.gmo.pos.venta.web.beans.IdiomaBean;
import cl.gmo.pos.venta.web.beans.PedidosPendientesBean;
import cl.gmo.pos.venta.web.beans.PresupuestosBean;
import cl.gmo.pos.venta.web.beans.ProductosBean;
import cl.gmo.pos.venta.web.forms.BusquedaPedidosForm;
import cl.gmo.pos.venta.web.forms.PresupuestoForm;
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
	
	private AgenteBean agenteBean;
	private FormaPagoBean formaPagoBean;
	private DivisaBean divisaBean;
	private IdiomaBean idiomaBean;
	
	private ClienteBean cliente;
	private ProductosBean productoBean;
	
	private String fpagoDisable;
	private String agenteDisable;	
	
	private Date fecha;
	private Date fechaEntrega;
	private String sucursal;
	
	
	@Init
	public void inicial(@ContextParam(ContextType.VIEW) Component view, 
			@ExecutionArgParam("origen")String arg) {	

			Selectors.wireComponents(view, this, false);
		
		ventaPedidoForm            = new VentaPedidoForm();
		ventaPedidoDispatchActions = new VentaPedidoDispatchActions();
		cliente      = new ClienteBean();
		productoBean = new ProductosBean();	
		
		agenteBean = new AgenteBean(); 
		formaPagoBean = new FormaPagoBean();
		divisaBean = new DivisaBean();
		idiomaBean = new IdiomaBean();
		
		fpagoDisable ="True";
		agenteDisable="True";	
		
		fecha= new Date(System.currentTimeMillis());
		fechaEntrega= new Date(System.currentTimeMillis());		
		//ventaPedidoForm.setFecha(dt.format(new Date(System.currentTimeMillis())));
		//ventaPedidoForm.setHora(tt.format(new Date(System.currentTimeMillis())));
		sucursal = sess.getAttribute(Constantes.STRING_SUCURSAL).toString();
		
		ventaPedidoForm = ventaPedidoDispatchActions.cargaInicial(ventaPedidoForm, sucursal, sess);
		
		//Si el encargo es invocado desde presupuesto, debe pasar por aqui
		if(arg.equals("presupuesto")) {			
			ventaPedidoForm = ventaPedidoDispatchActions.IngresaVentaPedidoDesdePresupuesto(ventaPedidoForm, sess);
			posicionComboNuevo();
		}		
		
	}
	
	
	//===================== Acciones de la ToolBar ======================
	//===================================================================
	
	//============ Nuevo Pedido ====================
	//==============================================
	@NotifyChange({"ventaPedidoForm","divisaBean","idiomaBean","formaPagoBean","agenteBean"})
	@Command
	public void nuevoFormulario() {
		
		ventaPedidoForm = ventaPedidoDispatchActions.nuevoFormulario(ventaPedidoForm, sess);
		
		ventaPedidoForm.setDivisa("PESO");
		ventaPedidoForm.setIdioma("CAST");
		ventaPedidoForm.setForma_pago("1");
		ventaPedidoForm.setAgente(sess.getAttribute("agente").toString());
		
		posicionComboNuevo();
		
		if (!bWin) {
			wBusqueda.detach();
			bWin=true;
		}		
	}
	
	
	//============== Graba Pedido =================
	//=============================================
	@NotifyChange("ventaPedidoForm")
	@Command
	public void grabarVentaPedido() {		
		
		System.out.println("forma de pago" + formaPagoBean.getDescripcion());
		System.out.println("Agente de la venta" + agenteBean.getUsuario());
		
		Messagebox.show("Grabacion exitosa");
		
	}
	
	
	//=========== Busqueda de presupuesto ===========
	//===============================================	
	@NotifyChange("ventaPedidoForm")
	@Command
	public void busquedaEncargo() {
		
		BusquedaPedidosForm busquedaPedidosForm = new BusquedaPedidosForm();
		
		ventaPedidoForm = ventaPedidoDispatchActions.cargaPedidoAnterior(ventaPedidoForm, sess);
		//Constantes.STRING_ACTION_LISTA_PEDIDOS
		
		objetos = new HashMap<String,Object>();		
		objetos.put("listaPedidos",sess.getAttribute(Constantes.STRING_ACTION_LISTA_PEDIDOS));
		
		Window window = (Window)Executions.createComponents(
                "/zul/encargos/BusquedaEncargo.zul", null, objetos);
		
        window.doModal(); 		
		
	}
	
	//=========== Recupera Encargo seleccionado======
	//===============================================	
		
	@NotifyChange({"ventaPedidoForm","agenteBean","divisaBean","formaPagoBean","idiomaBean"})
	@GlobalCommand
	public void encargoSeleccionado(@BindingParam("arg")ArrayList<PedidosPendientesBean> arg,
									@BindingParam("arg2")PedidosPendientesBean arg2) {		
		
		
		
		try {
			sess.setAttribute(Constantes.STRING_ACTION_CDG, arg2.getCdg());
			ventaPedidoForm.setAccion(Constantes.STRING_ACTION_CARGA_PEDIDO_SELECCION);
			ventaPedidoForm = ventaPedidoDispatchActions.IngresaVentaPedido(ventaPedidoForm, sess);
			
			posicionComboNuevo();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}	
		
	}
	
	
	//===================== Acciones comunes de la ventana ======================
	//===========================================================================
	
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
	
	@Command
	public void cerrarVentana(@BindingParam("arg")Window arg) {
		
		if (!bWin) {
			wBusqueda.detach();
		}
		
		arg.detach();
	}
	
	@NotifyChange({"ventaPedidoForm"})
    @GlobalCommand
	public void actProdGridVentaPedido(@BindingParam("producto")ProductosBean arg) {	
		
		
		arg.setImporte(arg.getPrecio());
		arg.setCantidad(1);
		
		ArrayList<ProductosBean> productos = new ArrayList<ProductosBean>();
		
		if (ventaPedidoForm.getListaProductos() == null) {
			productos.add(arg);
			ventaPedidoForm.setListaProductos(productos);
		}else {
			productos = ventaPedidoForm.getListaProductos();
			productos.add(arg);
			ventaPedidoForm.setListaProductos(productos);
		}	
			
		actTotal(ventaPedidoForm.getListaProductos());
		System.out.println("estoy en otro controlador de venta pedido");				
	}
	
	@NotifyChange({"ventaPedidoForm"})	
	@Command
	public void deleteItem(@BindingParam("arg")ProductosBean b){
		
		ventaPedidoForm.getListaProductos().remove(b);		
		actTotal(ventaPedidoForm.getListaProductos());
	}
	
	@NotifyChange("ventaPedidoForm")	
	public void actTotal(List<ProductosBean> arg){
		int sumar=0;
		
		sumar = arg.stream().mapToInt(ProductosBean::getImporte).sum();
		ventaPedidoForm.setSubTotal(sumar);
		ventaPedidoForm.setTotal(sumar);		
	}
	
	@NotifyChange({"productoBean"})
	@Command
	public void actualizaDetalles(@BindingParam("arg")ProductosBean arg ) {
		
		productoBean = arg;			
	}
	
	
	@NotifyChange({"ventaPedidoForm"})
	@Command
	public void actImporteGrid(@BindingParam("arg")ProductosBean arg){
		Integer newImport=0;		
		
		newImport = arg.getPrecio() * arg.getCantidad();
		
		for(ProductosBean b : ventaPedidoForm.getListaProductos()) {
			if(b.getCod_barra().equals(arg.getCod_barra())) {
				b.setImporte(newImport);
				break;
			}
		}	
		
		/*Optional<ProductosBean> p = ventaDirectaForm.getListaProductos()
				.stream()
				.filter(s -> s.getCod_barra().equals(arg.getCod_barra()))
				.findFirst()	;*/
		
		actTotal(ventaPedidoForm.getListaProductos());
		System.out.println("nuevo importe " + newImport);
	}
	
	
	public void posicionComboNuevo() {
		
		Optional<AgenteBean> a = ventaPedidoForm.getListaAgentes().stream().filter(s -> ventaPedidoForm.getAgente().equals(s.getUsuario())).findFirst();		
		agenteBean = a.get();	
		
		Optional<DivisaBean> b = ventaPedidoForm.getListaDivisas().stream().filter(s -> ventaPedidoForm.getDivisa().equals(s.getId())).findFirst();
		divisaBean = b.get();		
		
		Optional<IdiomaBean> d = ventaPedidoForm.getListaIdiomas().stream().filter(s -> ventaPedidoForm.getIdioma().equals(s.getId())).findFirst();
		idiomaBean = d.get();	
		
		Optional<FormaPagoBean> e = ventaPedidoForm.getListaFormasPago().stream().filter(s -> ventaPedidoForm.getForma_pago().equals(s.getId())).findFirst();
		formaPagoBean = e.get();		
		
	}
	
	
	@NotifyChange({"formaPagoBean","agenteBean"})
	@Command
	public void comboSetNull(@BindingParam("objetoBean")Object arg) {
		
		if (arg instanceof FormaPagoBean)		
		    formaPagoBean=null;
		if (arg instanceof AgenteBean)		
		    agenteBean=null;		
	}
	
	
	//Getter and Setter
	
	public VentaPedidoForm getVentaPedidoForm() {
		return ventaPedidoForm;
	}

	public void setVentaPedidoForm(VentaPedidoForm ventaPedidoForm) {
		this.ventaPedidoForm = ventaPedidoForm;
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


	public ProductosBean getProductoBean() {
		return productoBean;
	}

	public void setProductoBean(ProductosBean productoBean) {
		this.productoBean = productoBean;
	}


	public AgenteBean getAgenteBean() {
		return agenteBean;
	}


	public void setAgenteBean(AgenteBean agenteBean) {
		this.agenteBean = agenteBean;
	}


	public FormaPagoBean getFormaPagoBean() {
		return formaPagoBean;
	}


	public void setFormaPagoBean(FormaPagoBean formaPagoBean) {
		this.formaPagoBean = formaPagoBean;
	}


	public DivisaBean getDivisaBean() {
		return divisaBean;
	}


	public void setDivisaBean(DivisaBean divisaBean) {
		this.divisaBean = divisaBean;
	}


	public IdiomaBean getIdiomaBean() {
		return idiomaBean;
	}


	public void setIdiomaBean(IdiomaBean idiomaBean) {
		this.idiomaBean = idiomaBean;
	}	
	
	
	
	
}
