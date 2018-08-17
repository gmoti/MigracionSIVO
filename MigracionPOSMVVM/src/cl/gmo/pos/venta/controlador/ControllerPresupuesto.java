package cl.gmo.pos.venta.controlador;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

import cl.gmo.pos.venta.controlador.presupuesto.PresupuestoDispatchActions;
import cl.gmo.pos.venta.controlador.presupuesto.PresupuestoHelper;
import cl.gmo.pos.venta.utils.Constantes;
import cl.gmo.pos.venta.web.Integracion.DAO.DAOImpl.ClienteDAOImpl;
import cl.gmo.pos.venta.web.beans.AgenteBean;
import cl.gmo.pos.venta.web.beans.ClienteBean;
import cl.gmo.pos.venta.web.beans.DivisaBean;
import cl.gmo.pos.venta.web.beans.FamiliaBean;
import cl.gmo.pos.venta.web.beans.FormaPagoBean;
import cl.gmo.pos.venta.web.beans.GraduacionesBean;
import cl.gmo.pos.venta.web.beans.IdiomaBean;
import cl.gmo.pos.venta.web.beans.PresupuestosBean;
import cl.gmo.pos.venta.web.beans.ProductosBean;
import cl.gmo.pos.venta.web.forms.PresupuestoForm;

public class ControllerPresupuesto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 834007943950085993L;
	Session sess = Sessions.getCurrent();
	PresupuestoHelper helper = new PresupuestoHelper();
	
	private PresupuestoForm presupuestoForm;
	private PresupuestoDispatchActions presupuestoDispatchActions;
	private ClienteDAOImpl clienteImp;
	private ClienteBean cliente;
	
	private Date fecha;
	private Date fechaEntrega;	
	
	private AgenteBean agenteBean;
	private FormaPagoBean formaPagoBean;
	private DivisaBean divisaBean;
	private IdiomaBean idiomaBean;
	private ProductosBean productoBean;
	
	private String fpagoDisable;
	private String agenteDisable;
	
	private List<FamiliaBean> familiaBeans;
	
	HashMap<String,Object> objetos;
	private Window wBusqueda;
	private boolean bWin=true;
	
	
	@Init
	public void inicio() {
		
		fecha 		 = new Date(System.currentTimeMillis());
		fechaEntrega = new Date(System.currentTimeMillis());
		
		fpagoDisable="True";
		agenteDisable="True";
		
		agenteBean = new AgenteBean(); 
		formaPagoBean = new FormaPagoBean();
		divisaBean = new DivisaBean();
		idiomaBean = new IdiomaBean();		
		productoBean = new ProductosBean();	
		
		presupuestoDispatchActions = new PresupuestoDispatchActions(); 
		presupuestoForm = new PresupuestoForm();
		clienteImp = new ClienteDAOImpl();
		cliente = new ClienteBean();
		
		familiaBeans = new ArrayList<>();
		
		sess.setAttribute(Constantes.STRING_PRESUPUESTO, 0);
		presupuestoDispatchActions.cargaFormulario(presupuestoForm, sess);
		
	}


	@Command
	public void busquedaPresupuesto() {
		
		objetos = new HashMap<String,Object>();		
		objetos.put("presupuestoForm",presupuestoForm);
		Window window = (Window)Executions.createComponents(
                "/zul/presupuestos/BusquedaPresupuesto.zul", null, objetos);
		
        window.doModal(); 		
		
	}
	
	
	@NotifyChange({"presupuestoForm"})
	@Command
	public void buscarCliente(@BindingParam("arg")String arg) {
		
		try {
			
			
			presupuestoForm.setEstaGrabado(2);
			cliente = helper.traeClienteSeleccionado(arg,null);
			
			if (!cliente.getNif().equals("")) {
			
				
				presupuestoForm.setNif(cliente.getNif());
				presupuestoForm.setDvnif(cliente.getDvnif());
				presupuestoForm.setNombre_cliente(cliente.getNombre() + " " + cliente.getApellido());
				presupuestoForm.setCliente(cliente.getCodigo());
				
				GraduacionesBean graduacion = helper.traeUltimaGraduacionCliente(cliente.getCodigo());	
				presupuestoForm.setGraduacion(graduacion);
				
				sess.setAttribute("nombre_cliente",cliente.getNombre() + " " + cliente.getApellido());			
				sess.setAttribute(Constantes.STRING_CLIENTE, cliente.getCodigo());
	        	sess.setAttribute(Constantes.STRING_CLIENTE_VENTA, cliente.getCodigo());	        	
	        	sess.setAttribute("NOMBRE_CLIENTE",cliente.getNombre() + " " + cliente.getApellido());	
	        	
	        	presupuestoForm.setAccion("agregarCliente");
	        	presupuestoForm.setFlujo(Constantes.STRING_FORMULARIO);                 
	    		presupuestoForm = presupuestoDispatchActions.IngresaPresupuesto(presupuestoForm, sess);
					
			}else {
				Messagebox.show("El cliente no existe");
			}
				
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
	}
	
	
	@NotifyChange({"presupuestoForm","agenteBean","divisaBean","formaPagoBean","idiomaBean"})
	@GlobalCommand
	public void presupuestoSeleccionado(@BindingParam("arg")PresupuestosBean arg) {			
		
		sess.setAttribute(Constantes.STRING_PRESUPUESTO, 0);
		presupuestoForm.setAccion(Constantes.STRING_SELECCIONA_PRESUPUESTO);
		presupuestoForm = presupuestoDispatchActions.cargaPresupuestos(presupuestoForm, sess);	
		
		posicionaCombos();
		
	}
	
	
	@NotifyChange({"productoBean"})
	@Command
	public void actualizaDetalles(@BindingParam("arg")ProductosBean arg ) {
		
		productoBean = arg;			
	}
	
	
	@NotifyChange({"presupuestoForm","fpagoDisable","agenteDisable","divisaBean","idiomaBean"})
	@Command
	public void nuevoPresupuesto() {		
		
		fpagoDisable="False";
		agenteDisable="False";
		
		//presupuestoForm = new PresupuestoForm();
		//sess.setAttribute(Constantes.STRING_PRESUPUESTO, 0);
		presupuestoForm = presupuestoDispatchActions.nuevoFormulario(presupuestoForm, sess);
		
		presupuestoForm.setDivisa("PESO");
		presupuestoForm.setIdioma("CAST");
		
		posicionComboNuevo();
		
		if (!bWin) {
			wBusqueda.detach();
			bWin=true;
		}		
	}
	
	
	@Command
	public void cerrarVentana(@BindingParam("arg")Window arg) {
		
		if (!bWin) {
			wBusqueda.detach();
		}
		
		arg.detach();
	}
	
	@Command
	public void buscaProducto() {		
		
		if (bWin) {
			objetos = new HashMap<String,Object>();
			objetos.put("objetoForm",presupuestoForm);		
			wBusqueda = (Window)Executions.createComponents(
	                "/zul/presupuestos/SearchProducto.zul", null, objetos);
			
			wBusqueda.doModal();
			bWin=false;
		}else {
			wBusqueda.setVisible(true);
		}
       
	}
	
	@NotifyChange({"presupuestoForm"})
    @GlobalCommand
	public void actProdGridPresupuesto(@BindingParam("producto")ProductosBean arg) {	
		
		
		arg.setImporte(arg.getPrecio());
		arg.setCantidad(1);
		
		ArrayList<ProductosBean> productos = new ArrayList<ProductosBean>();
		
		if (presupuestoForm.getListaProductos() == null) {
			productos.add(arg);
			presupuestoForm.setListaProductos(productos);
		}else {
			productos = presupuestoForm.getListaProductos();
			productos.add(arg);
			presupuestoForm.setListaProductos(productos);
		}	
			
		actTotal(presupuestoForm.getListaProductos());
		System.out.println("estoy en otro controlador "+arg.getDescripcion());				
	}
	
	@NotifyChange({"presupuestoForm"})	
	@Command
	public void deleteItem(@BindingParam("arg")ProductosBean b){
		
		presupuestoForm.getListaProductos().remove(b);		
		actTotal(presupuestoForm.getListaProductos());
	}
	
	@NotifyChange("presupuestoForm")	
	public void actTotal(List<ProductosBean> arg){
		int sumar=0;
		
		sumar = arg.stream().mapToInt(ProductosBean::getImporte).sum();
		presupuestoForm.setSubTotal(sumar);
		presupuestoForm.setTotal(sumar);
		
		//System.out.println("nuevo total:" + total);
	}
	
	
	@NotifyChange("presupuestoForm")
	@Command
	public void grabarPresupuesto() {		
		
		//sess.setAttribute(Constantes.STRING_FORMULARIO, "PRESUPUESTO");
		presupuestoForm.setEstado(Constantes.STRING_FORMULARIO);
		presupuestoForm.setAccion("ingresa_presupuesto");		
		
		//presupuestoForm.setCodigo(Constantes.STRING_BLANCO);		
		presupuestoForm.setForma_pago(formaPagoBean.getId());
		presupuestoForm.setAgente(agenteBean.getUsuario());		
		
		presupuestoForm = presupuestoDispatchActions.IngresaPresupuesto(presupuestoForm, sess);		
		
		Messagebox.show("Grabacion exitosa");
		
	}	
	
	@NotifyChange({"presupuestoForm"})
	@Command
	public void actImporteGrid(@BindingParam("arg")ProductosBean arg){
		Integer newImport=0;		
		
		newImport = arg.getPrecio() * arg.getCantidad();
		
		for(ProductosBean b : presupuestoForm.getListaProductos()) {
			if(b.getCod_barra().equals(arg.getCod_barra())) {
				b.setImporte(newImport);
				break;
			}
		}	
		
		/*Optional<ProductosBean> p = ventaDirectaForm.getListaProductos()
				.stream()
				.filter(s -> s.getCod_barra().equals(arg.getCod_barra()))
				.findFirst()	;*/
		
		actTotal(presupuestoForm.getListaProductos());
		System.out.println("nuevo importe " + newImport);
	}
	
	
	@Command
	public void imprimirPresupuesto() {
		
		Window window = (Window)Executions.createComponents(
                "/zul/reportes/ReportePresupuesto.zul", null, objetos);		
        window.doModal();
	}	
	
	
	public void posicionaCombos() {
			
		Optional<AgenteBean> a = presupuestoForm.getListaAgentes().stream().filter(s -> presupuestoForm.getAgente().equals(s.getUsuario())).findFirst();		
		agenteBean = a.get();		
		
		Optional<DivisaBean> b = presupuestoForm.getListaDivisas().stream().filter(s -> presupuestoForm.getDivisa().equals(s.getId())).findFirst();
		divisaBean = b.get();
		
		Optional<FormaPagoBean> c = presupuestoForm.getListaFormasPago().stream().filter(s -> presupuestoForm.getForma_pago().equals(s.getId())).findFirst();
		formaPagoBean = c.get();
		
		Optional<IdiomaBean> d = presupuestoForm.getListaIdiomas().stream().filter(s -> presupuestoForm.getIdioma().equals(s.getId())).findFirst();
		idiomaBean = d.get();
	}
	
	public void posicionComboNuevo() {
		
		Optional<DivisaBean> b = presupuestoForm.getListaDivisas().stream().filter(s -> presupuestoForm.getDivisa().equals(s.getId())).findFirst();
		divisaBean = b.get();		
		
		Optional<IdiomaBean> d = presupuestoForm.getListaIdiomas().stream().filter(s -> presupuestoForm.getIdioma().equals(s.getId())).findFirst();
		idiomaBean = d.get();		
		
	}
	
	
	//Combos Precargados para evitar recargas
	//========================================
	/*	
		public void cargaFamilias() {		
			try {			
				familiaBeans = utilesDaoImpl.traeFamilias("DIRECTA");
				//cargaSubFamilias("");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	*/
	
	
	
	//=================getter and setter=========================
	//============================================================
	public PresupuestoForm getPresupuestoForm() {
		return presupuestoForm;
	}

	public void setPresupuestoForm(PresupuestoForm presupuestoForm) {
		this.presupuestoForm = presupuestoForm;
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


	public ProductosBean getProductoBean() {
		return productoBean;
	}


	public void setProductoBean(ProductosBean productoBean) {
		this.productoBean = productoBean;
	}
	
	


}
