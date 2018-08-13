package cl.gmo.pos.venta.controlador;


import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import cl.gmo.pos.venta.controlador.ventaDirecta.VentaDirectaDispatchActions;
import cl.gmo.pos.venta.utils.Constantes;
import cl.gmo.pos.venta.web.Integracion.DAO.DAOImpl.ClienteDAOImpl;
import cl.gmo.pos.venta.web.Integracion.DAO.DAOImpl.UtilesDAOImpl;
import cl.gmo.pos.venta.web.beans.ClienteBean;
import cl.gmo.pos.venta.web.beans.FamiliaBean;
import cl.gmo.pos.venta.web.beans.ProductosBean;
import cl.gmo.pos.venta.web.forms.SeleccionPagoForm;
import cl.gmo.pos.venta.web.forms.VentaDirectaForm;

public class ControllerVentaDirecta implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1872185090221869401L;

	Session sess = Sessions.getCurrent();
	
	@Wire
	Window win;
		
	//constantes	

	private String local="";
	private int caja=0;
	private String glprofile="";
	private String gldescripcion="";
	private String nombreSucural="";
	
	//Definiciones 
	private ClienteDAOImpl clienteImp;
	private ClienteBean cliente;	
	private Timestamp fecha;	
	private ProductosBean productoBean;
	//private Integer total;		
	private VentaDirectaForm ventaDirectaForm;
	private SeleccionPagoForm seleccionPagoForm;
	
	
	//arreglos
	private List<FamiliaBean> familiaBeans;
	//private ArrayList<ProductosBean> productos;
	
	//Instacias
	private UtilesDAOImpl utilesDaoImpl;
	private VentaDirectaDispatchActions ventaDirectaAccion;
	
	//Modelos
	HashMap<String,Object> objetos;
	
	//Banderas e indicadores	
	private String disabledGrid;
	private String disableNew;
	private String disablePagar;
	private String disableGrabar;
	
	
	SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat tt = new SimpleDateFormat("hh:mm:ss");
	private boolean bWin=true;
	private Window wBusqueda;
	
	
	@Init
	public void inicio() {	
		
		local = (String) sess.getAttribute("sucursal");	
		caja  = (int) sess.getAttribute("caja");
		glprofile = (String) sess.getAttribute("glprofile");	
		gldescripcion = (String) sess.getAttribute("gldescripcion");
		nombreSucural = (String) sess.getAttribute("nombreSucural");		
				
		clienteImp = new ClienteDAOImpl();
		cliente = new ClienteBean();			
		productoBean = new ProductosBean();		
		familiaBeans = new ArrayList<>();	
		//productos = new ArrayList<ProductosBean>();
		
		utilesDaoImpl = new UtilesDAOImpl();	
		ventaDirectaAccion = new VentaDirectaDispatchActions();
		
		disabledGrid="true";
		disableGrabar="true";
		disableNew="false";
		disablePagar="true";
		
		fecha = new Timestamp(System.currentTimeMillis());		
		
		
		//Encabezado venta directa		
		ventaDirectaForm = new VentaDirectaForm();
		
		ventaDirectaForm = ventaDirectaAccion.carga(ventaDirectaForm, sess);
		ventaDirectaForm = ventaDirectaAccion.cargaCaja(ventaDirectaForm, sess);		
		ventaDirectaForm.setCajero(glprofile);
		ventaDirectaForm.setAgente(glprofile);
		ventaDirectaForm.setNumero_caja(caja);		
	}
	
	
	@NotifyChange({"ventaDirectaForm","cliente","productos","disabledGrid","disableGrabar","disablePagar"})
	@Command
	public void nuevaVenta() {
		
		fecha = new Timestamp(System.currentTimeMillis());		
		cliente = new ClienteBean();
		//productos = new ArrayList<ProductosBean>();
		disabledGrid="true";		
		disableGrabar="true";		
		disablePagar="true";
		
		ventaDirectaForm = ventaDirectaAccion.carga(ventaDirectaForm, sess);
		ventaDirectaForm.setCajero(glprofile);
		ventaDirectaForm.setAgente(glprofile);
		ventaDirectaForm.setNumero_caja(caja);
		ventaDirectaForm.setSumaTotal(0);
		ventaDirectaForm.setSumaTotalFinal(0);
		
		ventaDirectaForm.setListaProductos(new ArrayList<ProductosBean>());
		
		System.out.println(ventaDirectaForm.getCajero());
		System.out.println(ventaDirectaForm.getAgente());
		System.out.println(ventaDirectaForm.getNumero_caja());
		
		if (!bWin) {
			wBusqueda.detach();
			bWin=true;
		}
		
	}
	
	
	@NotifyChange({"ventaDirectaForm","cliente","disabledGrid","disableGrabar"})
	@Command
	public void buscarCliente(@BindingParam("arg")String arg) {
		
		try {
			
			cliente = clienteImp.traeCliente(arg, "");
			
			if (!cliente.getNif().equals("")) {
			
				ventaDirectaForm.setCodigo_cliente(cliente.getCodigo());
				ventaDirectaForm.setNombreCliente(cliente.getApellido());
				ventaDirectaForm.setNombre(cliente.getNombre());
				
				sess.setAttribute("nombre_cliente",cliente.getNombre() + " " + cliente.getApellido());			
				sess.setAttribute(Constantes.STRING_CLIENTE, cliente.getCodigo());
	        	sess.setAttribute(Constantes.STRING_CLIENTE_VENTA, cliente.getCodigo());	        	
	        	sess.setAttribute("NOMBRE_CLIENTE",cliente.getNombre() + " " + cliente.getApellido());       	
	    	    sess.setAttribute(Constantes.STRING_TIPO_ALBARAN, ventaDirectaForm.getTipoAlbaran());			
				
				disabledGrid="false";
				disableGrabar="false";
				//disablePagar="false";		
			}else {
				Messagebox.show("El cliente no existe");
			}
				
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
	}
	
	@NotifyChange({"disablePagar","ventaDirectaForm"})
	@Command
	public void grabaVenta() {
		
		//valida grabar venta
		if (ventaDirectaForm.getSumaTotal() < 1) {
			Messagebox.show("Venta sin lineas");
			return;
		}	
		
		//venta_directa.js
		ventaDirectaForm.setAccion(Constantes.STRING_AGREGAR_VENTA_DIRECTA);
		//ventaDirectaForm.setSumaTotal(total);		
		//ventaDirectaForm.setListaProductos(productos);		
		
		try {
			
			ventaDirectaForm = ventaDirectaAccion.generaVentaDirecta(ventaDirectaForm, sess);
			
			ventaDirectaForm = ventaDirectaAccion.IngresaVentaDirecta(ventaDirectaForm, sess);			
			Messagebox.show("Venta almacenada");
			
			//activar botn de pago
			disablePagar="false";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Command
	public void buscaProducto() {		
		
		if (bWin) {
			objetos = new HashMap<String,Object>();
			objetos.put("familiaBeans",familiaBeans);			
			wBusqueda = (Window)Executions.createComponents(
	                "/zul/venta_directa/SearchProducto.zul", null, objetos);
			
			wBusqueda.doModal(); 
	        bWin=false;
		}else {
			wBusqueda.setVisible(true);
		} 
		
	}
	
	@NotifyChange({"ventaDirectaForm"})
    @GlobalCommand
	public void actProdGrid(@BindingParam("arg")ProductosBean arg) {		
		
		arg.setImporte(arg.getPrecio());
		arg.setCantidad(1);
		
		ArrayList<ProductosBean> productos = new ArrayList<ProductosBean>();
		
		if (ventaDirectaForm.getListaProductos() == null) {
			productos.add(arg);
			ventaDirectaForm.setListaProductos(productos);
		}else {
			productos = ventaDirectaForm.getListaProductos();
			productos.add(arg);
			ventaDirectaForm.setListaProductos(productos);
		}		
		
		actTotal(ventaDirectaForm.getListaProductos());
		System.out.println("estoy en otro controlador "+arg.getDescripcion());
				
	}
	
	 
	@NotifyChange({"ventaDirectaForm"})
	@Command
	public void actImporteGrid(@BindingParam("arg")ProductosBean arg){
		Integer newImport=0;		
		
		newImport = arg.getPrecio() * arg.getCantidad();
		
		for(ProductosBean b : ventaDirectaForm.getListaProductos()) {
			if(b.getCod_barra().equals(arg.getCod_barra())) {
				b.setImporte(newImport);
				break;
			}
		}	
		
		/*Optional<ProductosBean> p = ventaDirectaForm.getListaProductos()
				.stream()
				.filter(s -> s.getCod_barra().equals(arg.getCod_barra()))
				.findFirst()	;*/
		
		actTotal(ventaDirectaForm.getListaProductos());
		System.out.println("nuevo importe " + newImport);
	}
	
	
	@NotifyChange("ventaDirectaForm")	
	public void actTotal(List<ProductosBean> arg){
		int sumar=0;
		
		sumar = arg.stream().mapToInt(s -> s.getImporte()).sum();	
		
		ventaDirectaForm.setSumaTotal(sumar);
		System.out.println("nuevo total:" + sumar);
	}	
	
	@NotifyChange({"ventaDirectaForm"})	
	@Command
	public void deleteItem(@BindingParam("arg")ProductosBean b){
		ventaDirectaForm.getListaProductos().remove(b);		
		actTotal(ventaDirectaForm.getListaProductos());
	}
	
	
	@Command
	public void pagoVenta() {
		
		
		if (ventaDirectaForm.getSumaTotal() > 0) {				
			try {
				
				ventaDirectaForm.setAccion("valida_venta_directa");	
				ventaDirectaForm = ventaDirectaAccion.IngresaVentaDirecta(ventaDirectaForm, sess);
				
				System.out.println(ventaDirectaForm.getEstado());
				//validacion de multioferta
				
				
				seleccionPagoForm = new SeleccionPagoForm();
				sess.setAttribute(Constantes.STRING_LISTA_PRODUCTOS, ventaDirectaForm.getListaProductos());				
				
				objetos = new HashMap<String,Object>();
				objetos.put("cliente",cliente);
				objetos.put("pagoForm",seleccionPagoForm);
				objetos.put("ventaDirectaForm",ventaDirectaForm);		
				
				Window window = (Window)Executions.createComponents(
		                "/zul/venta_directa/pagoVentaDirecta.zul", null, objetos);
				
		        window.doModal();  				
				
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	@Command
	public void salir() {
		
		Messagebox.show("Aviso","Salir de Ventas Directas",Messagebox.YES|Messagebox.NO,Messagebox.QUESTION ,new EventListener() {

			@Override
			public void onEvent(Event e) throws Exception {				
				if(  ((Integer) e.getData()).intValue() == Messagebox.YES)
				    win.detach();
			}
			
			
		});
		
	}
	
	
	//Combos Precargados para evitar recargas
	//========================================
	
	public void cargaFamilias() {		
		try {			
			familiaBeans = utilesDaoImpl.traeFamilias("DIRECTA");
			//cargaSubFamilias("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	
	
	//********* getter an setter
	//==================================

	public ClienteBean getCliente() {
		return cliente;
	}

	public void setCliente(ClienteBean cliente) {
		this.cliente = cliente;
	}

	public ProductosBean getProductoBean() {
		return productoBean;
	}

	public void setProductoBean(ProductosBean productoBean) {
		this.productoBean = productoBean;
	}

	public String getDisabledGrid() {
		return disabledGrid;
	}

	public void setDisabledGrid(String disabledGrid) {
		this.disabledGrid = disabledGrid;
	}

	public Timestamp getFecha() {
		return fecha;
	}


	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}


	public VentaDirectaForm getVentaDirectaForm() {
		return ventaDirectaForm;
	}


	public void setVentaDirectaForm(VentaDirectaForm ventaDirectaForm) {
		this.ventaDirectaForm = ventaDirectaForm;
	}


	public String getDisableNew() {
		return disableNew;
	}


	public void setDisableNew(String disableNew) {
		this.disableNew = disableNew;
	}


	public String getDisablePagar() {
		return disablePagar;
	}


	public void setDisablePagar(String disablePagar) {
		this.disablePagar = disablePagar;
	}


	public String getDisableGrabar() {
		return disableGrabar;
	}


	public void setDisableGrabar(String disableGrabar) {
		this.disableGrabar = disableGrabar;
	}
	
	
	
	
}
