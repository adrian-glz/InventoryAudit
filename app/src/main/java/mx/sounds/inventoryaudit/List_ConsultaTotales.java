package mx.sounds.inventoryaudit;

public class List_ConsultaTotales {
	private String factura;
	private String marca; 
	private String modelo; 
	private String color; 
	private String cantidad; 

	public List_ConsultaTotales (String factura, String marca, String modelo, String color, String cantidad) { 
	    this.factura = factura; 
	    this.marca = marca; 
	    this.modelo = modelo; 
	    this.color = color; 
	    this.cantidad = cantidad; 
	}

	public String get_texto1() { 
	    return factura; 
	}
	
	public String get_texto2() { 
	    return marca; 
	}
	
	public String get_texto3() { 
	    return modelo; 
	}
	
	public String get_texto4() { 
	    return color; 
	}
	
	public String get_texto5() { 
	    return cantidad; 
	}
}
