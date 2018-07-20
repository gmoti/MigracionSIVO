package cl.gmo.pos.venta.controlador;

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
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Window;

import cl.gmo.pos.venta.web.Integracion.DAO.DAOImpl.UtilesDAOImpl;
import cl.gmo.pos.venta.web.beans.FamiliaBean;
import cl.gmo.pos.venta.web.beans.GrupoFamiliaBean;
import cl.gmo.pos.venta.web.beans.ProductosBean;
import cl.gmo.pos.venta.web.beans.SubFamiliaBean;
import cl.gmo.pos.venta.web.helper.BusquedaProductosHelper;

public class ControllerSearchProduct {
	
	@Wire("#winBuscaProducto")
	private Window win;
	
	//constantes
	private final String SUCURSAL="T002";
	private final String MONEDA="PESO";
	private final String TIPO_BUSQUEDA="DIRECTA";
	
	//protected UnderlayingWindowCtrl ctrl;
	protected ControllerSearchProduct model;
	
	private FamiliaBean familiaBean;
	private SubFamiliaBean subFamiliaBean;
	private GrupoFamiliaBean grupoFamiliaBean;
	private ProductosBean productoBean;
	private String codigoSap;
	private String codigoBarra;
	
	private List<FamiliaBean> familiaBeans;
	private List<SubFamiliaBean> subFamiliaBeans;
	private List<GrupoFamiliaBean> grupoFamiliaBeans;
	private List<ProductosBean> productos;
	
	private UtilesDAOImpl utilesDaoImpl;
	private BusquedaProductosHelper busquedaProdhelper;
	
	private List<SubFamiliaBean> argumento2;
	private List<GrupoFamiliaBean> argumento3;
	
	@Init
	public void inicial(@ContextParam(ContextType.VIEW) Component view, 
			@ExecutionArgParam("familiaBeans")List<FamiliaBean> arg,
			@ExecutionArgParam("subFamiliaBeans")List<SubFamiliaBean> arg2,
			@ExecutionArgParam("grupoFamiliaBeans")List<GrupoFamiliaBean> arg3) {
		
		Selectors.wireComponents(view, this, false);
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
		
		argumento2 = arg2;
		argumento3 = arg3;
		
		//cargaFamilias(arg);	
		cargaFamilias();	
	}
	
	@NotifyChange("familiaBeans")
	public void cargaFamilias() {
		//familiaBeans=arg;	
		//cargaSubFamilias(familiaBean.getCodigo());
		try {
			familiaBeans = utilesDaoImpl.traeFamilias(TIPO_BUSQUEDA);
			//cargaSubFamilias(familiaBean.getCodigo());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@NotifyChange("subFamiliaBeans")
	@Command
	public void cargaSubFamilias(@BindingParam("familia")String familia) {
	//	public void cargaSubFamilias(String familia) {
		try {
			subFamiliaBeans = utilesDaoImpl.traeSubfamilias(familia);
			//cargaGrupoFamilias(familia, subFamiliaBean.getCodigo());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	@NotifyChange("grupoFamiliaBeans")
	@Command
	public void cargaGrupoFamilias(@BindingParam("familia")String familia,
			@BindingParam("subFamilia")String subFamilia) {
	//	public void cargaGrupoFamilias(String familia, String subFamilia) {	
		try {
			grupoFamiliaBeans = utilesDaoImpl.traeGruposFamilias(familia, subFamilia);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@NotifyChange("productos")
	@Command
	public void buscarProducto(@BindingParam("arg")FamiliaBean arg,
			@BindingParam("arg2")SubFamiliaBean arg2,
			@BindingParam("arg3")GrupoFamiliaBean arg3,
			@BindingParam("arg4")String arg4, @BindingParam("arg5")String arg5){
		
			productos = busquedaProdhelper.traeProductos(arg.getCodigo(), arg2.getCodigo(), 
				arg3.getCodigo(), "", "", "", arg4, arg5, SUCURSAL, TIPO_BUSQUEDA);		
	}
	
	//@Command
	//public void seleccionaProducto(@BindingParam("arg")ProductosBean producto,
	//		@BindingParam("win")Window win) {
	@Command
	public void seleccionaProducto(@BindingParam("win")Window win) {		
		win.detach();		
	}




	//---------getter and setter

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

	public List<SubFamiliaBean> getSubFamiliaBeans() {
		return subFamiliaBeans;
	}
	public void setSubFamiliaBeans(List<SubFamiliaBean> subFamiliaBeans) {
		this.subFamiliaBeans = subFamiliaBeans;
	}

	public List<GrupoFamiliaBean> getGrupoFamiliaBeans() {
		return grupoFamiliaBeans;
	}
	public void setGrupoFamiliaBeans(List<GrupoFamiliaBean> grupoFamiliaBeans) {
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
	
	

}
