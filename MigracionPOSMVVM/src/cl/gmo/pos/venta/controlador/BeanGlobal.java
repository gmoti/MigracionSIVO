package cl.gmo.pos.venta.controlador;

import java.io.Serializable;

public class BeanGlobal implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 560122016634691174L;
	
	private Object obj_1;
	private Object obj_2;
	
	public BeanGlobal(Object obj_1, Object obj_2) {		
		this.obj_1 = obj_1;
		this.obj_2 = obj_2;
	}
	
	public BeanGlobal() {}
	
	public Object getObj_1() {
		return obj_1;
	}

	public void setObj_1(Object obj_1) {
		this.obj_1 = obj_1;
	}

	public Object getObj_2() {
		return obj_2;
	}

	public void setObj_2(Object obj_2) {
		this.obj_2 = obj_2;
	}	

}
