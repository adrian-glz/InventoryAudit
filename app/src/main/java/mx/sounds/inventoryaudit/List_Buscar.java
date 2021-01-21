package mx.sounds.inventoryaudit;

public class List_Buscar {
	private String codigo;
	private String codigoprov;
	private String codigo2;
	private String descripcion;
	private String precio;

	public List_Buscar(String codigo, String codigoprov, String codigo2, String descripcion, String precio) {
	    this.codigo = codigo;
	    this.codigoprov = codigoprov;
	    this.codigo2 = codigo2;
	    this.descripcion = descripcion;
	    this.precio = precio;
	}

	public String get_texto_codigo() {
	    return codigo;
	}

	public String get_texto_codigoprov() {
	    return codigoprov;
	}
	
	public String get_texto_codigo2() {
	    return codigo2;
	}
	
	public String get_texto_descripcion() {
	    return descripcion;
	}
	
	public String get_texto_precio() {
	    return precio;
	}
	
}
