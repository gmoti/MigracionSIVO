package cl.gmo.pos.venta.web.beans;

public class TipoPedidoBean {
	
	private String codigo;
	private String descripcion;
	
	public TipoPedidoBean() {
		codigo="";
		descripcion="";
	}
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
