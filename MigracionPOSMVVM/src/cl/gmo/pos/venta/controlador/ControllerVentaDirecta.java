package cl.gmo.pos.venta.controlador;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Window;

import cl.gmo.pos.venta.utils.Utils;
import cl.gmo.pos.venta.web.Integracion.DAO.DAOImpl.ClienteDAOImpl;
import cl.gmo.pos.venta.web.Integracion.DAO.DAOImpl.UtilesDAOImpl;
import cl.gmo.pos.venta.web.beans.AgenteBean;
import cl.gmo.pos.venta.web.beans.ClienteBean;
import cl.gmo.pos.venta.web.beans.DivisaBean;
import cl.gmo.pos.venta.web.beans.FamiliaBean;
import cl.gmo.pos.venta.web.beans.GrupoFamiliaBean;
import cl.gmo.pos.venta.web.beans.ProductosBean;
import cl.gmo.pos.venta.web.beans.SubFamiliaBean;

public class ControllerVentaDirecta {
	
	//constantes
	private final String SUCURSAL="T002";
	private final String MONEDA="PESO";
	
	//Definiciones 
	private ClienteDAOImpl clienteImp;
	private ClienteBean cliente;	
	private AgenteBean agente;
	private ArrayList<AgenteBean> agentes;	
	private DivisaBean divisa;
	private Timestamp fecha;
	private String monedas;
	private ProductosBean productoBean;
	private Integer total;
	
	//arreglos
	private List<FamiliaBean> familiaBeans;
	private List<SubFamiliaBean> subFamiliaBeans;
	private List<GrupoFamiliaBean> grupoFamiliaBeans;
	private List<ProductosBean> productos;
	
	//Instacias
	private Utils utils = new Utils();
	private UtilesDAOImpl utilesDaoImpl;
	
	//Modelos
	final HashMap<String,Object> objetos = new HashMap<String,Object>();
	
	//Banderas e indicadores
	private String disabledGrid;
	
	
	@Init
	public void inicio() {		
		
		clienteImp = new ClienteDAOImpl();
		cliente = new ClienteBean();
		agente = new AgenteBean();
		agentes = new ArrayList<>();
		divisa = new DivisaBean();		
		productoBean = new ProductosBean();
		
		familiaBeans = new ArrayList<>();
		subFamiliaBeans = new ArrayList<>();
		grupoFamiliaBeans = new ArrayList<>();
		productos = new ArrayList<>();
		
		utilesDaoImpl = new UtilesDAOImpl();
		
		disabledGrid="true";
		divisa.setId(MONEDA);
		fecha = new Timestamp(System.currentTimeMillis());
		monedas=MONEDA;
		total=0;
		
		try {
			agentes = utils.traeAgentes(SUCURSAL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@NotifyChange({"cliente","disabledGrid"})
	@Command
	public void buscarCliente(@BindingParam("arg")String arg) {
		
		try {
			cliente = clienteImp.traeCliente(arg, "");
			disabledGrid="false";
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
	}
	
	public void cargarAgentes() {
		
		
	}
	
	@Command
	public void buscaProducto() {		
		
		objetos.put("familiaBeans",familiaBeans);		
		objetos.put("subFamiliaBeans",subFamiliaBeans);
		objetos.put("grupoFamiliaBeans",grupoFamiliaBeans);
		
		Window window = (Window)Executions.createComponents(
                "/zul/SearchProducto.zul", null, objetos);
		
        window.doModal();        
	}
	
	@NotifyChange({"productos","total"})
    @GlobalCommand
	public void actProdGrid(@BindingParam("arg")ProductosBean arg) {
		productoBean = arg;
		productoBean.setImporte(productoBean.getPrecio());
		productoBean.setCantidad(1);
		productos.add(productoBean);		
		
		actTotal(productos);
		System.out.println("estoy en otro controlador "+arg.getDescripcion());
	}
	
	//onChange="@command('actImporteGrid',arg=each)" 
	@NotifyChange({"productoBean","productos","total"})
	@Command
	public void actImporteGrid(@BindingParam("arg")ProductosBean arg){
		Integer newImport=0;		
		
		newImport = arg.getPrecio() * arg.getCantidad();
		arg.setImporte(newImport);
		//productoBean = arg;	
		
		for(int i=0; i< productos.size(); i++) {
			
			if (productos.get(i).getCod_barra().equals(arg.getCod_barra())){
				productos.get(i).setImporte(newImport);
				break;
			}
            
        }	
		
		actTotal(productos);
		System.out.println("nuevo importe " + newImport);
	}
	
	
	@NotifyChange("total")	
	public void actTotal(List<ProductosBean> arg){
		int sumar=0;
		//arg.stream().collect(Collectors.summingInt((ProductosBean)o -> o));
		for (ProductosBean pb:arg){
			sumar += pb.getImporte();
		}
		
		total=sumar;
		System.out.println("nuevo total:" + total);
	}	
	
	@NotifyChange({"productos","total"})	
	@Command
	public void deleteItem(@BindingParam("arg")ProductosBean b){
		productos.remove(b);
		actTotal(productos);
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
	
	public void cargaSubFamilias(String familia) {
		
		try {
			subFamiliaBeans = utilesDaoImpl.traeSubfamilias(familia);	
			cargaGrupoFamilias("","");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public void cargaGrupoFamilias(String familia, String subFamilia) {
		
		try {
			grupoFamiliaBeans = utilesDaoImpl.traeGruposFamilias(familia, subFamilia);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//**** getter an setter

	public ClienteBean getCliente() {
		return cliente;
	}

	public void setCliente(ClienteBean cliente) {
		this.cliente = cliente;
	}

	public AgenteBean getAgente() {
		return agente;
	}

	public void setAgente(AgenteBean agente) {
		this.agente = agente;
	}

	public ArrayList<AgenteBean> getAgentes() {
		return agentes;
	}

	public void setAgentes(ArrayList<AgenteBean> agentes) {
		this.agentes = agentes;
	}

	public DivisaBean getDivisa() {
		return divisa;
	}

	public void setDivisa(DivisaBean divisa) {
		this.divisa = divisa;
	}

	public Timestamp getFecha() {
		return fecha;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	public String getMonedas() {
		return monedas;
	}

	public void setMonedas(String monedas) {
		this.monedas = monedas;
	}

	public ProductosBean getProductoBean() {
		return productoBean;
	}

	public void setProductoBean(ProductosBean productoBean) {
		this.productoBean = productoBean;
	}

	public List<ProductosBean> getProductos() {
		return productos;
	}

	public void setProductos(List<ProductosBean> productos) {
		this.productos = productos;
	}

	public String getDisabledGrid() {
		return disabledGrid;
	}

	public void setDisabledGrid(String disabledGrid) {
		this.disabledGrid = disabledGrid;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}
	
	
}
