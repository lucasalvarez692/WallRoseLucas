package logica;

public class Producto {

    private int codigo;
    private String nombre;
    private double precio;
    private double existencia;

    public Producto(int codigo, String nombre, double precio, double existencia) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.existencia = existencia;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public double getExistencia() {
        return existencia;
    }

    public void setExistencia(double existencia) {
        this.existencia = existencia;
    }

    public boolean disponible(double cantidad) {
        return existencia >= cantidad;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " - ₡" + precio;
    }

	public void setNombre(String nombre2) {
		// TODO Auto-generated method stub
		
	}

	public void setPrecio(double precio2) {
		// TODO Auto-generated method stub
		
	}
}