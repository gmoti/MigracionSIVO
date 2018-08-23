package cl.gmo.pos.venta.controlador;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import cl.gmo.pos.venta.controlador.ventaDirecta.BusquedaProductosDispatchActions;
import cl.gmo.pos.venta.utils.Constantes;
import cl.gmo.pos.venta.web.Integracion.DAO.DAOImpl.UtilesDAOImpl;
import cl.gmo.pos.venta.web.beans.FamiliaBean;
import cl.gmo.pos.venta.web.beans.GrupoFamiliaBean;
import cl.gmo.pos.venta.web.beans.ProductosBean;
import cl.gmo.pos.venta.web.beans.SubFamiliaBean;
import cl.gmo.pos.venta.web.forms.BusquedaProductosForm;
import cl.gmo.pos.venta.web.forms.PresupuestoForm;
import cl.gmo.pos.venta.web.forms.VentaPedidoForm;
import cl.gmo.pos.venta.web.helper.BusquedaProductosHelper;

public class ControllerSearchProductPres implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7360268478883882968L;
	
	Session sess = Sessions.getCurrent();

	@Wire("#winBuscaProducto")
	private Window win;
	
	//constantes	
	private final String TIPO_BUSQUEDA="DIRECTA";
	
	private FamiliaBean familiaBean;
	private SubFamiliaBean subFamiliaBean;
	private GrupoFamiliaBean grupoFamiliaBean;
	private ProductosBean productoBean;

	private List<FamiliaBean> familiaBeans;
	private ArrayList<SubFamiliaBean> subFamiliaBeans;
	private ArrayList<GrupoFamiliaBean> grupoFamiliaBeans;
	private List<ProductosBean> productos;
	
	private UtilesDAOImpl utilesDaoImpl;
	private BusquedaProductosHelper busquedaProdhelper;	
	
	private String winVisibleBusqueda;
	private PresupuestoForm presupuesto;
	private VentaPedidoForm ventaPedido;
	private BusquedaProductosForm busquedaProductosForm;
	private BusquedaProductosDispatchActions busquedaProductosDispatchActions;
	
	private boolean ojoDerecho;
	private boolean ojoIzquierdo;
	private boolean cerca;
	private String busquedaAvanzada;
	private String busquedaAvanzadaLentilla;
	
	private int instancia=0;
	HashMap<String,Object> objetos;
	
	
	@Init
	public void inicial(@ContextParam(ContextType.VIEW) Component view, 
			//@ExecutionArgParam("presupuestoForm")PresupuestoForm arg) {
			@ExecutionArgParam("objetoForm")Object arg) {
		
		Selectors.wireComponents(view, this, false);
		
		if (arg instanceof PresupuestoForm) {
			presupuesto = new PresupuestoForm();
			presupuesto = (PresupuestoForm)arg;
			sess.setAttribute(Constantes.STRING_GRADUACION, presupuesto.getGraduacion());
			sess.setAttribute(Constantes.STRING_GRADUACION_LENTILLA, presupuesto.getGraduacion_lentilla());
			instancia=1;
		}
		
        if (arg instanceof VentaPedidoForm) {
        	ventaPedido = new VentaPedidoForm();
        	ventaPedido = (VentaPedidoForm)arg;
			sess.setAttribute(Constantes.STRING_GRADUACION, ventaPedido.getGraduacion());
			sess.setAttribute(Constantes.STRING_GRADUACION_LENTILLA, ventaPedido.getGraduacion_lentilla());
			instancia=2;
		}
		
		
		winVisibleBusqueda = "TRUE";		
		busquedaProductosForm = new BusquedaProductosForm(); 
		busquedaProductosDispatchActions = new BusquedaProductosDispatchActions();	
		
		familiaBean = new FamiliaBean();
		subFamiliaBean = new SubFamiliaBean();
		grupoFamiliaBean = new GrupoFamiliaBean();
		productoBean = new ProductosBean();
		
		familiaBeans = new ArrayList<>();
		subFamiliaBeans = new ArrayList<>();
		grupoFamiliaBeans = new ArrayList<>();
		productos = new ArrayList<>();
		
		utilesDaoImpl = new UtilesDAOImpl();
		busquedaProdhelper = new BusquedaProductosHelper();
		
		ojoDerecho=false;
		ojoIzquierdo=false;
		cerca=false;
		busquedaAvanzada = "false";
		busquedaAvanzadaLentilla= "false";		
		
		sess.setAttribute(Constantes.STRING_FORMULARIO, "PRESUPUESTO");	
		busquedaProductosForm = busquedaProductosDispatchActions.cargaBusquedaProductos(busquedaProductosForm, sess);
			
	}
	
	
	@NotifyChange("winVisibleBusqueda")
	@Command
	public void seleccionaProducto(@BindingParam("producto")ProductosBean producto) {	
		
		objetos = new HashMap<String,Object>();
		objetos.put("producto",producto);
				
		if (instancia==1)
			BindUtils.postGlobalCommand(null, null, "actProdGridPresupuesto", objetos);
			
		if (instancia==2)
			BindUtils.postGlobalCommand(null, null, "actProdGridVentaPedido", objetos);			
		
		winVisibleBusqueda="FALSE";
	}
	
	@NotifyChange("winVisibleBusqueda")
	@Command
	public void cierraVentana() {	
		
		winVisibleBusqueda="FALSE";
	}
	
	
	
	@NotifyChange("busquedaProductosForm")
	@Command
	public void despachador(@BindingParam("arg")String arg) {	
		
		Optional<FamiliaBean> fam    = Optional.ofNullable(familiaBean);
		Optional<SubFamiliaBean> subfam = Optional.ofNullable(subFamiliaBean);
		Optional<GrupoFamiliaBean> grufam = Optional.ofNullable(grupoFamiliaBean);
		
		if (fam.isPresent())		
			busquedaProductosForm.setFamilia(fam.get().getCodigo());
		else
			busquedaProductosForm.setFamilia("");
		
		if(subfam.isPresent())
			busquedaProductosForm.setSubFamilia(subfam.get().getCodigo());
		else	
			busquedaProductosForm.setSubFamilia("");
		
		if(grufam.isPresent())		
			busquedaProductosForm.setGrupo(grufam.get().getCodigo());
		else
			busquedaProductosForm.setGrupo("");
		
	    busquedaProductosForm.setAccion(arg);     	
     	
     	if (arg.equals("buscar")) { 
     		
     		if (busquedaAvanzada.equals("true")) {
	     		if (!isOjoDerecho() && !isOjoIzquierdo()) {
	     			
	     			Messagebox.show("Debe seleccionar un ojo, para realizar la busqueda.");
	     			busquedaProductosForm.setAccion("error");
	     			//busquedaProductosForm = busquedaProductosDispatchActions.buscar(busquedaProductosForm, sess);					
	     		}
	     		else {	     			
	     			
	     			busquedaProductosForm.setAccion("busqueda_graduada");
	     			busquedaProductosForm = busquedaProductosDispatchActions.buscar(busquedaProductosForm, sess);	     			
	     		}
     		}    		
			
			if (busquedaAvanzadaLentilla.equals("true")) {
				if (!isOjoDerecho() && !isOjoIzquierdo()) {
					Messagebox.show("Debe seleccionar un ojo, para realizar la busqueda.");
					busquedaProductosForm.setAccion("error");
	     			//busquedaProductosForm = busquedaProductosDispatchActions.buscar(busquedaProductosForm, sess);
				}
			}			
		}
     	
     	busquedaProductosForm = busquedaProductosDispatchActions.buscar(busquedaProductosForm, sess);		
	}
	
	
	@NotifyChange({"busquedaProductosForm"})
	@Command
	public void cleanProducts() {
		
		busquedaProductosForm.setListaProductos(new ArrayList<ProductosBean>());
	}
	
	/*	
	@NotifyChange("familiaBeans")
	public void cargaFamilias() {		
		try {
			familiaBeans = utilesDaoImpl.traeFamilias(TIPO_BUSQUEDA);			
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}*/
	
	@NotifyChange({"subFamiliaBeans","busquedaProductosForm","busquedaAvanzada","busquedaAvanzadaLentilla"})
	@Command
	public void cargaSubFamilias() {	
		try {
			subFamiliaBeans = utilesDaoImpl.traeSubfamilias(familiaBean.getCodigo());
			busquedaProductosForm.setListaSubFamilias(subFamiliaBeans);	
			
			if (familiaBean.getTipo_fam().equals("C")) {
				setBusquedaAvanzada("true");
				cleanProducts();
			}
			else
				setBusquedaAvanzada("false");
			
			if (familiaBean.getTipo_fam().equals("L")) {
				setBusquedaAvanzadaLentilla("true");
				cleanProducts();
			}
			else
				setBusquedaAvanzadaLentilla("false");
			
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}	
	
	@NotifyChange({"grupoFamiliaBeans","busquedaProductosForm"})
	@Command
	public void cargaGrupoFamilias() {	
		try {
			grupoFamiliaBeans = utilesDaoImpl.traeGruposFamilias(familiaBean.getCodigo(), subFamiliaBean.getCodigo());
			busquedaProductosForm.setListaGruposFamilias(grupoFamiliaBeans);
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}	
	
		
	
	@NotifyChange({"familiaBean","subFamiliaBean","grupoFamiliaBean"})
	@Command
	public void comboSetNull(@BindingParam("objetoBean")Object arg) {
		
		if (arg instanceof FamiliaBean) 
			familiaBean=null;			
		
		if (arg instanceof SubFamiliaBean)
			subFamiliaBean=null;				
		
		if (arg instanceof GrupoFamiliaBean) 
			grupoFamiliaBean=null;				
			
	}
	
	
	


	//---------getter and setter-----------
	//-------------------------------------

	public FamiliaBean getFamiliaBean() {
		return familiaBean;
	}
	public void setFamiliaBean(FamiliaBean familiaBean) {
		this.familiaBean = familiaBean;
	}

	public SubFamiliaBean getSubFamiliaBean() {
		return subFamiliaBean;
	}
	public void setSubFamiliaBean(SubFamiliaBean subFamiliaBean) {
		this.subFamiliaBean = subFamiliaBean;
	}

	public GrupoFamiliaBean getGrupoFamiliaBean() {
		return grupoFamiliaBean;
	}
	public void setGrupoFamiliaBean(GrupoFamiliaBean grupoFamiliaBean) {
		this.grupoFamiliaBean = grupoFamiliaBean;
	}

	public List<FamiliaBean> getFamiliaBeans() {
		return familiaBeans;
	}
	public void setFamiliaBeans(List<FamiliaBean> familiaBeans) {
		this.familiaBeans = familiaBeans;
	}

	public ArrayList<SubFamiliaBean> getSubFamiliaBeans() {
		return subFamiliaBeans;
	}
	public void setSubFamiliaBeans(ArrayList<SubFamiliaBean> subFamiliaBeans) {
		this.subFamiliaBeans = subFamiliaBeans;
	}

	public ArrayList<GrupoFamiliaBean> getGrupoFamiliaBeans() {
		return grupoFamiliaBeans;
	}
	public void setGrupoFamiliaBeans(ArrayList<GrupoFamiliaBean> grupoFamiliaBeans) {
		this.grupoFamiliaBeans = grupoFamiliaBeans;
	}

	public List<ProductosBean> getProductos() {
		return productos;
	}

	public void setProductos(List<ProductosBean> productos) {
		this.productos = productos;
	}

	public ProductosBean getProductoBean() {
		return productoBean;
	}

	public void setProductoBean(ProductosBean productoBean) {
		this.productoBean = productoBean;
	}

	public BusquedaProductosForm getBusquedaProductosForm() {
		return busquedaProductosForm;
	}

	public void setBusquedaProductosForm(BusquedaProductosForm busquedaProductosForm) {
		this.busquedaProductosForm = busquedaProductosForm;
	}

	public String getWinVisibleBusqueda() {
		return winVisibleBusqueda;
	}

	public void setWinVisibleBusqueda(String winVisibleBusqueda) {
		this.winVisibleBusqueda = winVisibleBusqueda;
	}

	public boolean isOjoDerecho() {
		return ojoDerecho;
	}

	public void setOjoDerecho(boolean ojoDerecho) {
		this.ojoDerecho = ojoDerecho;
	}

	public boolean isOjoIzquierdo() {
		return ojoIzquierdo;
	}

	public void setOjoIzquierdo(boolean ojoIzquierdo) {
		this.ojoIzquierdo = ojoIzquierdo;
	}

	public String getBusquedaAvanzada() {
		return busquedaAvanzada;
	}

	public void setBusquedaAvanzada(String busquedaAvanzada) {
		this.busquedaAvanzada = busquedaAvanzada;
	}

	public String getBusquedaAvanzadaLentilla() {
		return busquedaAvanzadaLentilla;
	}

	public void setBusquedaAvanzadaLentilla(String busquedaAvanzadaLentilla) {
		this.busquedaAvanzadaLentilla = busquedaAvanzadaLentilla;
	}

	public boolean isCerca() {
		return cerca;
	}

	public void setCerca(boolean cerca) {
		this.cerca = cerca;
	} 

	
}
