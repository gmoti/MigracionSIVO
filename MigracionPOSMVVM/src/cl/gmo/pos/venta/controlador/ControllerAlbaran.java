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

import cl.gmo.pos.venta.utils.Constantes;
import cl.gmo.pos.venta.utils.Utils;
import cl.gmo.pos.venta.web.actions.DevolucionDispatchActions;
import cl.gmo.pos.venta.web.beans.DevolucionBean;
import cl.gmo.pos.venta.web.beans.IdiomaBean;
import cl.gmo.pos.venta.web.forms.DevolucionForm;

public class ControllerAlbaran extends DevolucionForm{
	
	DevolucionForm dform ;
	DevolucionBean devbean ;
	DevolucionDispatchActions dev_dis = new DevolucionDispatchActions();
	Utils util = new Utils();
	Session sesion = Sessions.getCurrent();
	
	
	private String codigo_completo;
	private Date fecha_alb;
	private Date fecha_gar;
	private boolean rboleta;
	private boolean rguia;
	private boolean ch_entrega;
	private boolean ch_facturado;
	
	@Init	
	public void inicial() {
		
		//String local = sesion.getAttribute("sucursal").toString();
		dform = dev_dis.cargaInicial(sesion);
		//this.setCodigo_completo(dform.getCodigo1()+"/"+dform.getCodigo2());
		this.setListaFormasPago(dform.getListaFormasPago());
		this.setLista_mot_devo(dform.getLista_mot_devo());
		this.setLista_productos(dform.getLista_productos());
		this.setLista_albaranes(dform.getLista_albaranes());
		this.setListaAgentes(dform.getListaAgentes());
		this.setListaConvenios(dform.getListaConvenios());
		this.setListaIdiomas(dform.getListaIdiomas());
		this.setListaDivisas(dform.getListaDivisas());
		this.setListaProvincia(dform.getListaProvincia());
		this.setListaTipoAlbaranes(dform.getListaTipoAlbaranes());
	}
	@Command
	@NotifyChange({"*"})
	public void cargaDatos(@BindingParam("dev") DevolucionForm form) {
		dform = new DevolucionForm();
		
		//DEFINO CONTSANTES POR DEFECTO
		form.setTipoAlbaran("D");
		form.setAccion("cargarDatos");
		
		if(this.isRboleta() == true) {
			form.setBoleta_guia("B");
		}else {
			form.setBoleta_guia("G");
		}
		
		System.out.println("BOLETA_GUIA ==>"+form.getBoleta_guia()+" "+form.getNumero_boleta_guia());
		dform =	dev_dis.cargaAlbaran(form,sesion);
		//this.setCodigo_completo(dform.getCodigo1()+"/"+dform.getCodigo2());
		this.setNif(dform.getNif());
		this.setDvnif(dform.getDvnif());
		this.setHora(dform.getHora());
		this.setFecha_alb(util.formatoFechaCh(dform.getFecha()));
		System.out.println("HORA ==> "+dform.getHora());
		
		
		this.getListaFormasPago().forEach(t->{
			if(t.getId().equals(dform.getFormaPago())) {
					this.setFormaPago(t.getDescripcion());
				}
			}
		);
		this.getListaIdiomas().forEach(t->{
				if(t.getId().equals(dform.getIdioma())) {
					this.setIdioma(t.getDescripcion());
				}
			}
			
		);
		this.getListaDivisas().forEach(t->{
			if(t.getId().equals(dform.getDivisa())) {
				this.setDivisa(t.getDescripcion());
			}
		 }
		
	    );
		this.getListaTipoAlbaranes().forEach(t->{
			if(String.valueOf(t.getCodigo()).equals(dform.getTipoAlbaran())) {
				this.setTipo_albaran(t.getDescripcion());
				}
			}
		
		);
		
		
		/*this.getListaTipoAlbaranes().forEach(t->{
			if(t.getDescripcion().equals(retform.getTipoAlbaran())){
				this.setTipo_via(t.getCodigo());
			}
		});*/
	
	}
	
	
	@Command
	@NotifyChange({"*"})
	public void nuevoAlbaran(@BindingParam("arg1") DevolucionForm form){
		this.setFecha_gar(null);
		
	}
	/*@Command
	@NotifyChange({"*"})
	public void cobrar(@BindingParam("arg1") GraduacionesForm gform){
	}
	
	*/
	
	@Command
	public void cerrar(@BindingParam("arg1")  Window x) {
	    x.detach();
	}
	
	public String getCodigo_completo() {
		return codigo_completo;
	}

	public void setCodigo_completo(String codigo_completo) {
		this.codigo_completo = codigo_completo;
	}

	public Date getFecha_alb() {
		return fecha_alb;
	}

	public void setFecha_alb(Date fecha_alb) {
		this.fecha_alb = fecha_alb;
	}

	public boolean isRboleta() {
		return rboleta;
	}

	public void setRboleta(boolean rboleta) {
		this.rboleta = rboleta;
	}

	public boolean isRguia() {
		return rguia;
	}

	public void setRguia(boolean rguia) {
		this.rguia = rguia;
	}

	public boolean isCh_entrega() {
		return ch_entrega;
	}

	public void setCh_entrega(boolean ch_entrega) {
		this.ch_entrega = ch_entrega;
	}

	public boolean isCh_facturado() {
		return ch_facturado;
	}

	public void setCh_facturado(boolean ch_facturado) {
		this.ch_facturado = ch_facturado;
	}

	public Date getFecha_gar() {
		return fecha_gar;
	}

	public void setFecha_gar(Date fecha_gar) {
		this.fecha_gar = fecha_gar;
	}

	
}
