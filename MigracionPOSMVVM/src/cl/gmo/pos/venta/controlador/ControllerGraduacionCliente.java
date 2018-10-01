package cl.gmo.pos.venta.controlador;



import java.util.Date;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

import com.ibm.icu.util.Calendar;

import cl.gmo.pos.venta.utils.Utils;
import cl.gmo.pos.venta.web.actions.GraduacionesDispatchActions;
import cl.gmo.pos.venta.web.beans.GraduacionesBean;
import cl.gmo.pos.venta.web.forms.GraduacionesForm;

public class ControllerGraduacionCliente extends GraduacionesForm{
	
	
	private boolean diferenteAdicion = false;
	private boolean prismaInterno= false;
	private boolean prismaExterno= false;
	private String sagente = "Seleccione Agente";
	
	GraduacionesForm cform ;
	GraduacionesBean gradb ;
	GraduacionesDispatchActions grad_dis = new GraduacionesDispatchActions();
	Utils util = new Utils();
	Session sesion = Sessions.getCurrent();
	private Date fechaEm = null;

	@Init	
	public void inicial() {
		 //String local = sesion.getAttribute("sucursal").toString();
		cform = grad_dis.cargaFormulario("0","T002");
		this.setListaAgentes(cform.getListaAgentes());
		this.setListaCantidadOD(cform.getListaCantidadOD());
		this.setListaCantidadOI(cform.getListaCantidadOI());
		this.setListaBaseOD(cform.getListaBaseOD());
		this.setListaBaseOI(cform.getListaBaseOI());
	}
	@Command
	@NotifyChange({"*"})
	public void buscarGrad(@BindingParam("arg1")  String nif) {
		
		String nif_f = (nif.indexOf("-") != -1)? nif.substring(0,nif.indexOf("-")):nif;

		System.out.println("NIF LIMPIO ==>"+nif_f);
		if(nif_f.indexOf("-") == -1 ) {
			
			System.out.println("PASO CON GUION  ==>"+nif);
			cform = grad_dis.cargaFormulario(nif_f,"T002");
			
			if(cform.getCliente() != 0){
				this.setCliente(cform.getCliente());
				this.setNombre_cliente(cform.getNombre_cliente());
				//OJO DERECHO	
				this.setOD_esfera(cform.getOD_esfera());
				this.setOD_cilindro(cform.getOD_cilindro());
				this.setOD_eje(cform.getOD_eje());
				this.setOD_cerca(cform.getOD_cerca());
				this.setOD_adicion(cform.getOD_adicion());
				this.setOD_dnpl(cform.getOD_dnpl());
				this.setOD_dnpc(cform.getOD_dnpc());
				this.setOD_avsc(cform.getOD_avsc());
				this.setOD_avcc(cform.getOD_avcc());
				this.setOD_observaciones(cform.getOD_observaciones());
		
				//OJO IZQUIERDO
				this.setOI_esfera(cform.getOI_esfera());
				this.setOI_cilindro(cform.getOI_cilindro());
				this.setOI_eje(cform.getOI_eje());
				this.setOI_cerca(cform.getOI_cerca());
				this.setOI_adicion(cform.getOI_adicion());
				this.setOI_dnpl(cform.getOI_dnpl());
				this.setOI_dnpc(cform.getOI_dnpc());
				this.setOI_avsc(cform.getOI_avsc());
				this.setOI_avcc(cform.getOI_avcc());
				this.setOI_observaciones(cform.getOI_observaciones());
				
				this.setListaGraduaciones(cform.getListaGraduaciones());
				this.setFechaEm(util.formatoFechaCh(cform.getFechaEmision()));
				this.setFechaProxRevision(cform.getFechaProxRevision());
				this.setNombre_doctor(cform.getNombre_doctor());
				this.setNifdoctor(cform.getNifdoctor());
				this.setDvnifdoctor(cform.getDvnifdoctor());
					
				for(GraduacionesBean b : this.getListaGraduaciones()){
					System.out.println("FECHA GRADUACION ==>"+b.getFecha()+" NUMERO==>"+b.getNumero()+" TIPO==>"+b.getTipo());
				}
				
			}else {
				Messagebox.show("No existe Graduaciones para el usuario "+nif_f);
			}
	   }
	
	}
	@Command
	@NotifyChange({"*"})
	public void nuevoUsuario(@BindingParam("arg1") GraduacionesForm gform){
		//OJO DERECHO	
		this.setOD_esfera(0);
		this.setOD_cilindro(0);
		this.setOD_eje(0);
		this.setOD_cerca(0);
		this.setOD_adicion(0);
		this.setOD_dnpl(0);
		this.setOD_dnpc(0);
		this.setOD_avsc(0);
		this.setOD_avcc(0);
		this.setOD_observaciones("");

		//OJO IZQUIERDO
		this.setOI_esfera(0);
		this.setOI_cilindro(0);
		this.setOI_eje(0);
		this.setOI_cerca(0);
		this.setOI_adicion(0);
		this.setOI_dnpl(0);
		this.setOI_dnpc(0);
		this.setOI_avsc(0);
		this.setOI_avcc(0);
		this.setOI_observaciones("");
		
		this.setNombre_doctor("");
		this.setNifdoctor("");
		this.setDvnifdoctor("");
		this.setFechaEmision("");
		this.setFechaProxRevision("");
		
		this.setDiferenteAdd(false);
		
		this.setTipo("");
		this.setPrismaInterno(false);
		this.setPrismaExterno(false);
		this.setFechaEm(null);
		
		this.setListaGraduaciones(null);
	}
	@Command
	@NotifyChange({"*"})
	public void selGraduacion(@BindingParam("sel") GraduacionesBean gb){
		GraduacionesForm ftemp = new GraduacionesForm();
		ftemp.setAccion("verGraduacion");
		ftemp.setFecha_graduacion(gb.getFecha());
		ftemp.setNumero_graduacion(gb.getNumero());
		ftemp.setCliente(this.getCliente());
		cform = grad_dis.IngresaGraduacion(ftemp, sesion);
		this.setNombre_cliente(cform.getNombre_cliente());
		
		//OJO DERECHO	
		this.setOD_esfera(cform.getOD_esfera());
		this.setOD_cilindro(cform.getOD_cilindro());
		this.setOD_eje(cform.getOD_eje());
		this.setOD_cerca(cform.getOD_cerca());
		this.setOD_adicion(cform.getOD_adicion());
		this.setOD_dnpl(cform.getOD_dnpl());
		this.setOD_dnpc(cform.getOD_dnpc());
		this.setOD_avsc(cform.getOD_avsc());
		this.setOD_avcc(cform.getOD_avcc());
		this.setOD_observaciones(cform.getOD_observaciones());

		//OJO IZQUIERDO
		this.setOI_esfera(cform.getOI_esfera());
		this.setOI_cilindro(cform.getOI_cilindro());
		this.setOI_eje(cform.getOI_eje());
		this.setOI_cerca(cform.getOI_cerca());
		this.setOI_adicion(cform.getOI_adicion());
		this.setOI_dnpl(cform.getOI_dnpl());
		this.setOI_dnpc(cform.getOI_dnpc());
		this.setOI_avsc(cform.getOI_avsc());
		this.setOI_avcc(cform.getOI_avcc());
		this.setOI_observaciones(cform.getOI_observaciones());
		
		this.setNombre_doctor(cform.getNombre_doctor());
		this.setNifdoctor(cform.getNifdoctor());
		this.setDvnifdoctor(cform.getDvnifdoctor());
		this.setFechaEm(util.formatoFechaCh(cform.getFechaEmision()));
		this.setFechaProxRevision(cform.getFechaProxRevision());
		
		this.setListaGraduaciones(cform.getListaGraduaciones());
		
		System.out.println("fechagrad controller ===>"+gb.getFecha()+" <=> numerograd ==>"+gb.getNumero());
	}
	
	@Command
	@NotifyChange({"*"})
	public void ingresarGraduacion(@BindingParam("arg1") GraduacionesForm gradform) {
		 if(gradform.getOD_base().toLowerCase().equals("")){
			 
		 }
		 gradform.setAccion("insertarGraduacion");
		 grad_dis.IngresaGraduacion(gradform,sesion);
	}
	
	@Command
	@NotifyChange({"fechaProxRevision"})
	public void calculoFechaRev(@BindingParam("arg") Date infecha) {
		Calendar c = Calendar.getInstance();
		c.setTime(infecha);
		c.add(Calendar.YEAR,+2);
		this.setFechaProxRevision(util.formatoFecha(c.getTime()));
	}
	
	@Command
	public void cerrar(@BindingParam("arg1")  Window x) {
	    x.detach();
	}
	

	public boolean isDiferenteAdicion() {
		return diferenteAdicion;
	}

	public void setDiferenteAdicion(boolean diferenteAdicion) {
		this.diferenteAdicion = diferenteAdicion;
	}

	public boolean isPrismaInterno() {
		return prismaInterno;
	}

	public void setPrismaInterno(boolean prismaInterno) {
		this.prismaInterno = prismaInterno;
	}

	public boolean isPrismaExterno() {
		return prismaExterno;
	}

	public void setPrismaExterno(boolean prismaExterno) {
		this.prismaExterno = prismaExterno;
	}

	public String getSagente() {
		return sagente;
	}

	public void setSagente(String sagente) {
		this.sagente = sagente;
	}
	public Date getFechaEm() {
		return fechaEm;
	}
	public void setFechaEm(Date fechaEm) {
		this.fechaEm = fechaEm;
	}
}
