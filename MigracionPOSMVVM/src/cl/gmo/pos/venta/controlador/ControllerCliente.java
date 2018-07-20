package cl.gmo.pos.venta.controlador;


import java.sql.Date;
import java.util.ArrayList;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import cl.gmo.pos.venta.web.Integracion.DAO.DAOImpl.ClienteDAOImpl;
import cl.gmo.pos.venta.web.Integracion.DAO.DAOImpl.UtilesDAOImpl;
import cl.gmo.pos.venta.web.beans.AgenteBean;
import cl.gmo.pos.venta.web.beans.ClienteBean;

public class ControllerCliente{
	
	private ClienteDAOImpl clienteImp;
	private ClienteBean cliente;
	private ArrayList<AgenteBean> listaAgentes;
	private UtilesDAOImpl utiles=new UtilesDAOImpl();
	
	//ObjectMapper mapper = new ObjectMapper();
	AgenteBean agente;	
	Date fechaNac;	
	
	@Init	
	public void inicial() {
		cliente= new ClienteBean();
		agente = new AgenteBean();
		listaAgentes = new ArrayList<>();
		clienteImp = new ClienteDAOImpl();
		 
		System.out.println("incializando");	
		
		try {
			listaAgentes= utiles.traeAgentes("T002");		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	@Command
	@NotifyChange({"cliente","agente","fechaNac"})
	public void buscar(@BindingParam("arg1")String arg1, @BindingParam("arg2")String arg2) {
		
		System.out.println("buscando");
				
		try {
			cliente = clienteImp.traeCliente(arg1, arg2);
			agente = buscaAgente(cliente.getAgente());			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}	
	}
	
	private AgenteBean buscaAgente(String cod) {
		AgenteBean bean=null;
		
		for(AgenteBean a: listaAgentes) {
			
			if(a.getUsuario().equalsIgnoreCase(cod)) {
				bean=a;
				break;
			}
		}		
		return bean;
	}
	

	public ClienteBean getCliente() {
		return cliente;
	}

	public void setCliente(ClienteBean cliente) {
		this.cliente = cliente;
	}

	public ArrayList<AgenteBean> getListaAgentes() {
		return listaAgentes;
	}

	public void setListaAgentes(ArrayList<AgenteBean> listaAgentes) {
		this.listaAgentes = listaAgentes;
	}

	public AgenteBean getAgente() {
		return agente;
	}

	public void setAgente(AgenteBean agente) {
		this.agente = agente;
	}

	public Date getFechaNac() {
		return fechaNac;
	}

	public void setFechaNac(Date fechaNac) {
		this.fechaNac = fechaNac;
	}		
}
