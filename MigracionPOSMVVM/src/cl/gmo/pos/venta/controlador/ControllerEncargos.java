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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import cl.gmo.pos.venta.controlador.presupuesto.PresupuestoHelper;
import cl.gmo.pos.venta.controlador.ventaDirecta.VentaPedidoDispatchActions;
import cl.gmo.pos.venta.utils.Constantes;
import cl.gmo.pos.venta.web.beans.AgenteBean;
import cl.gmo.pos.venta.web.beans.ClienteBean;
import cl.gmo.pos.venta.web.beans.DivisaBean;
import cl.gmo.pos.venta.web.beans.FormaPagoBean;
import cl.gmo.pos.venta.web.beans.GraduacionesBean;
import cl.gmo.pos.venta.web.beans.IdiomaBean;
import cl.gmo.pos.venta.web.beans.PedidosPendientesBean;
import cl.gmo.pos.venta.web.beans.ProductosBean;
import cl.gmo.pos.venta.web.beans.TipoPedidoBean;
import cl.gmo.pos.venta.web.forms.BusquedaPedidosForm;
import cl.gmo.pos.venta.web.forms.SeleccionPagoForm;
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
	private TipoPedidoBean tipoPedidoBean;
	
	private ClienteBean cliente;
	private ProductosBean productoBean;
	
	private String fpagoDisable;
	private String agenteDisable;	
	
	private Date fecha;
	private Date fechaEntrega;
	private String sucursal;
	
	private BeanControlBotones beanControlBotones;
	private BeanControlCombos beanControlCombos;
	
	
	@Init
	public void inicial(@ContextParam(ContextType.VIEW) Component view, 
			@ExecutionArgParam("origen")String arg) {	

			Selectors.wireComponents(view, this, false);
			
		beanControlBotones = new BeanControlBotones();	
		beanControlCombos  = new BeanControlCombos();
		
		beanControlBotones.setEnableNew("false");
		beanControlBotones.setEnableListar("true");
		
		ventaPedidoForm            = new VentaPedidoForm();
		ventaPedidoDispatchActions = new VentaPedidoDispatchActions();
		cliente      = new ClienteBean();
		productoBean = new ProductosBean();	
		
		agenteBean = new AgenteBean(); 
		formaPagoBean = new FormaPagoBean();
		divisaBean = new DivisaBean();
		idiomaBean = new IdiomaBean();
		tipoPedidoBean = new TipoPedidoBean();
		
		fpagoDisable ="True";
		agenteDisable="True";	
		
		fecha= new Date(System.currentTimeMillis());
		fechaEntrega= new Date(System.currentTimeMillis());		
		ventaPedidoForm.setFecha(dt.format(new Date(System.currentTimeMillis())));
		ventaPedidoForm.setHora(tt.format(new Date(System.currentTimeMillis())));
		
		sucursal = sess.getAttribute(Constantes.STRING_SUCURSAL).toString();
		
		ventaPedidoForm = ventaPedidoDispatchActions.cargaInicial(ventaPedidoForm, sucursal, sess);
		ventaPedidoForm.setFlujo(Constantes.STRING_FORMULARIO);
		
		//Si el encargo es invocado desde presupuesto, debe pasar por aqui
		if(arg.equals("presupuesto")) {			
			ventaPedidoForm = ventaPedidoDispatchActions.IngresaVentaPedidoDesdePresupuesto(ventaPedidoForm, sess);			
		}		
		
		beanControlCombos.setComboAgenteEnable("true");
		beanControlCombos.setComboDivisaEnable("true");
		beanControlCombos.setComboFpagoEnable("true");
		beanControlCombos.setComboIdiomaEnable("true");
		beanControlCombos.setComboPromoEnable("true");
		beanControlCombos.setComboTiposEnable("true");
		
				
		posicionCombo();
		
	}
	
	
	//===================== Acciones de la ToolBar ======================
	//===================================================================
	
	//============ Nuevo Pedido ====================
	//==============================================
	@NotifyChange({"ventaPedidoForm","beanControlBotones","beanControlCombos"})
	@Command
	public void nuevoFormulario() {
		
		ventaPedidoForm = ventaPedidoDispatchActions.nuevoFormulario(ventaPedidoForm, sess);
		ventaPedidoForm.setFlujo(Constantes.STRING_FORMULARIO);
		beanControlBotones.setEnableListar("true");
		
		fecha= new Date(System.currentTimeMillis());
		fechaEntrega= new Date(System.currentTimeMillis());		
		ventaPedidoForm.setFecha(dt.format(new Date(System.currentTimeMillis())));
		ventaPedidoForm.setHora(tt.format(new Date(System.currentTimeMillis())));
		
		beanControlCombos.setComboAgenteEnable("false");
		beanControlCombos.setComboDivisaEnable("false");
		beanControlCombos.setComboFpagoEnable("false");
		beanControlCombos.setComboIdiomaEnable("false");
		beanControlCombos.setComboPromoEnable("false");
		beanControlCombos.setComboTiposEnable("false");
		
		
		
		posicionCombo();
		
		if (!bWin) {
			wBusqueda.detach();
			bWin=true;
		}		
	}
	
	
	//============== Graba Pedido =================
	//=============================================
	@NotifyChange({"ventaPedidoForm"})
	@Command
	public void ingresa_pedido() {		
		
		boolean valtienda=false;
		boolean valGrabar=false;
		
		Optional<TipoPedidoBean> tp  = Optional.ofNullable(tipoPedidoBean);	
		if (!tp.isPresent())
			tipoPedidoBean = new TipoPedidoBean();
		
		
		ventaPedidoForm.setAgente(agenteBean.getUsuario());
		ventaPedidoForm.setForma_pago(formaPagoBean.getId());
		ventaPedidoForm.setIdioma(idiomaBean.getId());
		ventaPedidoForm.setDivisa(divisaBean.getId());
		ventaPedidoForm.setTipo_pedido(tipoPedidoBean.getCodigo());	
		ventaPedidoForm.setFecha_entrega(dt.format(fechaEntrega));
		
		if (ventaPedidoForm.getListaProductos().size() < 1) {
			Messagebox.show("Debe ingresar articulos para generar cobros");
			return;
		}
		
		if (ventaPedidoForm.getNombre_cliente().equals("")) {
			Messagebox.show("Debe seleccionar un Cliente");
			return;
		}
		
		if (agenteBean == null) {
			Messagebox.show("Debe seleccionar un agente");
			return;
		}
		
		if (ventaPedidoForm.getFecha().equals("")) {
			Messagebox.show("Debe ingresar una fecha");
			return;
		}
		
		
		
		if (!ventaPedidoForm.getFlujo().equals("formulario")) {	
			
			if (ventaPedidoForm.getFlujo().equals("modificar")) {
			
				try {
					valtienda = ventaPedidoDispatchActions.validaTipoPedido(ventaPedidoForm, sess);
					
					if (valtienda) {					
						
		 				ventaPedidoForm.setAccion("ingresa_pedido");
		 				ventaPedidoDispatchActions.IngresaVentaPedido(ventaPedidoForm, sess);
		 				valGrabar=true;
						
					}else {
						
						ventaPedidoForm.setAccion("ingresa_pedido");
		 				ventaPedidoDispatchActions.IngresaVentaPedido(ventaPedidoForm, sess);
		 				valGrabar=true;
					}		
					
					
				} catch (Exception e) {
					
					e.printStackTrace();
				}			
			}else {
				
				Messagebox.show("Encargo bloqueado, no es posible modificar la venta");
				return;
			}
			
		}else {
			
			if(tipoPedidoBean.getCodigo().equals("SEG")) {				
				
				try {
					int val = ventaPedidoDispatchActions.validaVentaSeguro(ventaPedidoForm, sess);
					
					switch (val) {
					case 1:		
						
						Messagebox.show("El encargo a utilizar no esta asociado a garantia.");
						break;
					case 2:
						
						Messagebox.show("El encargo garantia ya fue utilizado, no es posible volver a ocuparlo.");
						break;
					case 3:						
		 				
		 				ventaPedidoForm.setAccion("ingresa_pedido");
		 				ventaPedidoDispatchActions.IngresaVentaPedido(ventaPedidoForm, sess);
		 				valGrabar=true;
						break;
					default:
						
						Messagebox.show("Problema al conectarse a la Base de Datos.");
						break;
					} 					
					
					
				} catch (Exception e) {
					
					e.printStackTrace();
				}		
				
				
			}else {
				
				
				try {
					valtienda = ventaPedidoDispatchActions.validaTipoPedido(ventaPedidoForm, sess);
					
					if (valtienda) {
					
						
					}else {
						
		 				ventaPedidoForm.setAccion("ingresa_pedido");
		 				ventaPedidoDispatchActions.IngresaVentaPedido(ventaPedidoForm, sess);
		 				valGrabar=true;
					}
					
				} catch (Exception e) {					
					e.printStackTrace();
				}
				
			}		
		}		
		
		
		if (valGrabar)
		   Messagebox.show("Pedido Grabado");
		
	}
	
	@NotifyChange("ventaPedidoForm")
	@Command
	public void genera_venta() {		
		
		String cantidad="";
		String codigoMulti="";
		
		BeanGlobal bg = ventaPedidoDispatchActions.validaCantidadProductosMultiofertas(ventaPedidoForm, sess);
		
		cantidad = (String)bg.getObj_1();
		codigoMulti = (String)bg.getObj_2();
		
		if (!cantidad.equals("menor")) {
			
			valida_venta();
		}else {
			
			Messagebox.show("La cantidad de productos en la multioferta "+codigoMulti+" no esta completa");			
		}
		
		
		/*
		
		if (!ventaPedidoForm.getEstado().equals("cerrado")) {
			
			ventaPedidoDispatchActions.generaVentaPedido(ventaPedidoForm, sess);	
			seleccionPagoForm = new SeleccionPagoForm();
			
			seleccionPagoForm.setFech_pago(ventaPedidoForm.getFecha());
			seleccionPagoForm.setFecha(ventaPedidoForm.getFecha());
			seleccionPagoForm.setTipo_doc('G');
			seleccionPagoForm.setOrigen("PEDIDO");
			
			sess.setAttribute(Constantes.STRING_TOTAL, ventaPedidoForm.getTotal());
			sess.setAttribute(Constantes.STRING_CLIENTE, cliente.getCodigo()  );
			//sess.setAttribute(Constantes.STRING_TICKET,  ventaPedidoForm.get  .getEncabezado_ticket() + "/" + ventaDirectaForm.getNumero_ticket() );
			sess.setAttribute(Constantes.STRING_FECHA,   ventaPedidoForm.getFecha());		
			sess.setAttribute(Constantes.STRING_LISTA_PRODUCTOS, ventaPedidoForm.getListaProductos());		
			
			
			objetos = new HashMap<String,Object>();
			objetos.put("cliente",cliente);
			objetos.put("pagoForm",seleccionPagoForm);
			objetos.put("ventaOrigenForm",ventaPedidoForm);
			objetos.put("origen","PEDIDO");
			
			Window window = (Window)Executions.createComponents(
	                "/zul/venta_directa/pagoVentaDirecta.zul", null, objetos);
			
	        window.doModal();
			
			
		}else {
			
			Messagebox.show("Venta finalizada, no es posible generar cobro");
		}	*/
		
	}
	
	
	private void valida_venta() {
		
		SeleccionPagoForm seleccionPagoForm = new SeleccionPagoForm();
		
		if (ventaPedidoForm.getEstado().equals("cerrado")) {
			Messagebox.show("Venta finalizada, no es posible generar cobros");
			return;
		}
		
		if (ventaPedidoForm.getNombre_cliente().equals("")) {
			Messagebox.show("Debe seleccionar un Cliente");
			return;
		}
		
		if (ventaPedidoForm.getNombre_cliente().equals("")) {
			Messagebox.show("Debe seleccionar un Cliente");
			return;
		}
		
		if (ventaPedidoForm.getListaProductos().size() < 1) {
			Messagebox.show("Debe ingresar articulos para generar cobros");
			return;
		}
		
		if (ventaPedidoForm.getCodigo().equals("")) {
			Messagebox.show("Debe guardar la venta, antes de cobrar");
			return;
		}		
		
		
		try {
			ventaPedidoForm.setAccion("valida_pedido");
			ventaPedidoDispatchActions.IngresaVentaPedido(ventaPedidoForm, sess);
			
			
			if (ventaPedidoForm.getEstado().equals(Constantes.STRING_GENERA_COBRO)) {
				
				objetos = new HashMap<String,Object>();
				objetos.put("cliente",cliente);
				objetos.put("pagoForm",seleccionPagoForm);
				objetos.put("ventaOrigenForm",ventaPedidoForm);
				objetos.put("origen","PEDIDO");
				
				Window window = (Window)Executions.createComponents(
		                "/zul/venta_directa/pagoVentaDirecta.zul", null, objetos);
				
		        window.doModal();				
				
			}else {
				
				Messagebox.show(ventaPedidoForm.getError());
			}			
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		
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
	@NotifyChange({"ventaPedidoForm","beanControlBotones"})
	@Command
	public void buscarClienteGenerico() {		
		ventaPedidoForm.setNif("66666666");		
		beanControlBotones.setEnableGrid("false");
		beanControlBotones.setEnableGrabar("false");
		
		buscarCliente();
	}	
	
	
	
	@NotifyChange({"ventaPedidoForm","beanControlBotones"})
	@Command
	public void buscarCliente() {
		
		
		if(ventaPedidoForm.getNif().equals("")) {
			Messagebox.show("Debe ingresar un nif");
			return;
		}
		
		
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
	        	
	        	beanControlBotones.setEnableListar("false");
	        	
					
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
		
		
		sess.setAttribute(Constantes.STRING_LISTA_PRODUCTOS, ventaPedidoForm.getListaProductos());
		/*ventaPedidoForm.setAccion(Constantes.STRING_AGREGAR_PRODUCTOS);
		
		try {
			ventaPedidoForm = ventaPedidoDispatchActions.IngresaVentaPedido(ventaPedidoForm, sess);
			
		} catch (Exception e) {			
			e.printStackTrace();
		}*/
		
			
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
		ventaPedidoForm.setTotalPendiante(sumar - ventaPedidoForm.getDescuento());
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
		
		ventaPedidoForm.setDivisa("PESO");
		ventaPedidoForm.setIdioma("CAST");
		ventaPedidoForm.setForma_pago("1");
		
		Optional<AgenteBean> a = ventaPedidoForm.getListaAgentes().stream().filter(s -> ventaPedidoForm.getAgente().equals(s.getUsuario())).findFirst();		
		agenteBean = a.get();	
		
		Optional<DivisaBean> b = ventaPedidoForm.getListaDivisas().stream().filter(s -> ventaPedidoForm.getDivisa().equals(s.getId())).findFirst();
		divisaBean = b.get();		
		
		Optional<IdiomaBean> d = ventaPedidoForm.getListaIdiomas().stream().filter(s -> ventaPedidoForm.getIdioma().equals(s.getId())).findFirst();
		idiomaBean = d.get();	
		
		Optional<FormaPagoBean> e = ventaPedidoForm.getListaFormasPago().stream().filter(s -> ventaPedidoForm.getForma_pago().equals(s.getId())).findFirst();
		formaPagoBean = e.get();		
		
	}
	
	
   @NotifyChange({"agenteBean","divisaBean","formaPagoBean","idiomaBean","tipoPedidoBean"}) 	
   public void posicionCombo() {
		
		ventaPedidoForm.setDivisa("PESO");
		ventaPedidoForm.setIdioma("CAST");
		ventaPedidoForm.setForma_pago("1");
		
		
		Optional<DivisaBean> b = ventaPedidoForm.getListaDivisas().stream().filter(s -> ventaPedidoForm.getDivisa().equals(s.getId())).findFirst();
		divisaBean = b.get();		
		
		Optional<IdiomaBean> d = ventaPedidoForm.getListaIdiomas().stream().filter(s -> ventaPedidoForm.getIdioma().equals(s.getId())).findFirst();
		idiomaBean = d.get();
		
		agenteBean=null;
		formaPagoBean=null;
		tipoPedidoBean=null;
	}
	
	
	@NotifyChange({"formaPagoBean","agenteBean","tipoPedidoBean"})
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

	public BeanControlBotones getBeanControlBotones() {
		return beanControlBotones;
	}

	public void setBeanControlBotones(BeanControlBotones beanControlBotones) {
		this.beanControlBotones = beanControlBotones;
	}

	public TipoPedidoBean getTipoPedidoBean() {
		return tipoPedidoBean;
	}

	public void setTipoPedidoBean(TipoPedidoBean tipoPedidoBean) {
		this.tipoPedidoBean = tipoPedidoBean;
	}


	public BeanControlCombos getBeanControlCombos() {
		return beanControlCombos;
	}


	public void setBeanControlCombos(BeanControlCombos beanControlCombos) {
		this.beanControlCombos = beanControlCombos;
	}
	
	
	
}
