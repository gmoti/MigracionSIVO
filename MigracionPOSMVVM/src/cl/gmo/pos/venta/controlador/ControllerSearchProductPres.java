package cl.gmo.pos.venta.controlador;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
	private String codigoSap;
	private String codigoBarra;
	
	private List<FamiliaBean> familiaBeans;
	private ArrayList<SubFamiliaBean> subFamiliaBeans;
	private ArrayList<GrupoFamiliaBean> grupoFamiliaBeans;
	private List<ProductosBean> productos;
	
	private UtilesDAOImpl utilesDaoImpl;
	private BusquedaProductosHelper busquedaProdhelper;
	
	
	private String winVisibleBusqueda;
	private PresupuestoForm presupuesto;
	private BusquedaProductosForm busquedaProductosForm;
	private BusquedaProductosDispatchActions busquedaProductosDispatchActions;
	
	@Init
	public void inicial(@ContextParam(ContextType.VIEW) Component view, 
			@ExecutionArgParam("presupuestoForm")PresupuestoForm arg) {
		
		Selectors.wireComponents(view, this, false);
		
		winVisibleBusqueda = "TRUE";
		presupuesto = new PresupuestoForm();
		busquedaProductosForm = new BusquedaProductosForm(); 
		busquedaProductosDispatchActions = new BusquedaProductosDispatchActions();
		
		presupuesto = arg;
		sess.setAttribute(Constantes.STRING_GRADUACION, arg.getGraduacion());
		sess.setAttribute(Constantes.STRING_GRADUACION_LENTILLA, arg.getGraduacion_lentilla());
		
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
		
		codigoSap="";
		codigoBarra="";	
		
		
		//sess.setAttribute(Constantes.STRING_FORMULARIO, "PRESUPUESTO");	
		busquedaProductosForm = busquedaProductosDispatchActions.cargaBusquedaProductos(busquedaProductosForm, sess);
			
	}
	
	
	@NotifyChange("winVisibleBusqueda")
	@Command
	public void seleccionaProducto(@BindingParam("win")Window win) {		
		//win.detach();	
		winVisibleBusqueda="FALSE";
	}
	
	
	@NotifyChange("busquedaProductosForm")
	@Command
	public void despachador(@BindingParam("arg")String arg) {
		
		
		busquedaProductosForm.setFamilia(familiaBean.getCodigo());
		busquedaProductosForm.setSubFamilia(subFamiliaBean.getCodigo());
		busquedaProductosForm.setGrupo(grupoFamiliaBean.getCodigo());
		
		busquedaProductosForm.setAccion(arg);
		busquedaProductosForm = busquedaProductosDispatchActions.buscar(busquedaProductosForm, sess);
	}
	
	
		
	@NotifyChange("familiaBeans")
	public void cargaFamilias() {		
		try {
			familiaBeans = utilesDaoImpl.traeFamilias(TIPO_BUSQUEDA);			
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}
	
	@NotifyChange({"subFamiliaBeans","busquedaProductosForm"})
	@Command
	public void cargaSubFamilias() {	
		try {
			subFamiliaBeans = utilesDaoImpl.traeSubfamilias(familiaBean.getCodigo());
			busquedaProductosForm.setListaSubFamilias(subFamiliaBeans);
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
	
	/*@NotifyChange("productos")
	@Command
	public void buscarProducto(@BindingParam("arg")FamiliaBean arg,
			@BindingParam("arg2")SubFamiliaBean arg2,
			@BindingParam("arg3")GrupoFamiliaBean arg3,
			@BindingParam("arg4")String arg4, @BindingParam("arg5")String arg5){
		
			productos = busquedaProdhelper.traeProductos(arg.getCodigo(), arg2.getCodigo(), 
				arg3.getCodigo(), "", "", "", arg4, arg5, SUCURSAL, TIPO_BUSQUEDA);		
	}*/
	
	
	


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

	public String getCodigoSap() {
		return codigoSap;
	}

	public void setCodigoSap(String codigoSap) {
		this.codigoSap = codigoSap;
	}

	public String getCodigoBarra() {
		return codigoBarra;
	}

	public void setCodigoBarra(String codigoBarra) {
		this.codigoBarra = codigoBarra;
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

	
	
	
	
	
}
