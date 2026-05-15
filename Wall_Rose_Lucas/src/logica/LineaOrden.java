package logica;

public class LineaOrden {

    private Producto producto;
    private double cantidad;

    public LineaOrden(Producto producto, double cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public double calcularSubtotal() {
        return producto.getPrecio() * cantidad;
    }

	public void setProducto(Producto producto2) {
		// TODO Auto-generated method stub
		
	}

	public void setCantidad(double cantidad2) {
		// TODO Auto-generated method stub
		
	}
    
}