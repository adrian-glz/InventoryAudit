package mx.sounds.inventoryaudit;

public class List_Consulta {
	private String codigo;
	private String gondola;
	private String cantidad;
	private String descripcion;
	private String fecha;

	public List_Consulta (String codigo, String gondola, String cantidad, String descripcion, String fecha) {
	    this.codigo = codigo;
	    this.gondola = gondola;
	    this.cantidad = cantidad;
	    this.descripcion = descripcion;
	    this.fecha = fecha;
	}

	public String get_texto1() { 
	    return codigo;
	}

	public String get_texto2() { 
	    return gondola;
	}
	
	public String get_texto3() { 
	    return cantidad;
	}
	
	public String get_texto4() { 
	    return descripcion;
	}
	
	public String get_texto5() { 
	    return fecha;
	}
	
}
