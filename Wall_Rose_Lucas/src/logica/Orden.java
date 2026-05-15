package logica;

import java.util.ArrayList;
import java.util.List;
import logica.EstadoOrden;

public class Orden {

    private int numero;
    private Cliente cliente;
    private List<LineaOrden> lineas;
	@SuppressWarnings("unused")
	private EstadoOrden estado;

    public Orden(int numero, Cliente cliente) {
        this.numero = numero;
        this.cliente = cliente;
        this.lineas = new ArrayList<>();
        this.estado = EstadoOrden.BORRADOR; 
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }
    
    public int getNumero() {
        return numero;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void agregarLinea(LineaOrden linea) {
        lineas.add(linea);
    }

    public double calcularTotal() {
        double total = 0;

        for (LineaOrden linea : lineas) {
            total += linea.calcularSubtotal();
        }

        return total;
    }

    public List<LineaOrden> getLineas() {
        return lineas;
    }

	public void agregarProducto(Producto producto, int cantidad) {
		// TODO Auto-generated method stub
		
	}

	public EstadoOrden getEstado() {
		// TODO Auto-generated method stub
		return null;
	}

	public void agregarProducto(Producto producto, double cantidad) {
		// TODO Auto-generated method stub
		
	}
	
}