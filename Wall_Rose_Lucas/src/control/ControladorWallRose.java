package control;

import java.util.ArrayList;
import java.util.List;
import logica.Cliente;
import logica.Orden;
import logica.Producto;

public class ControladorWallRose {
    private static ControladorWallRose instancia;
    
    private int consecutivoProducto;
    private int consecutivoOrden;
    
    private List<Producto> productos;
    private List<Cliente> clientes;
    private List<Orden> ordenes;
    
    private ControladorWallRose() {
        consecutivoProducto = 1;
        consecutivoOrden = 1;
        
        productos = new ArrayList<>();
        clientes = new ArrayList<>();
        ordenes = new ArrayList<>();
    }
    
    public static ControladorWallRose getInstance() {
        if (instancia == null) {
            instancia = new ControladorWallRose();
        }
        return instancia;
    }
    
    public void agregarProducto(String nombre, double precio, double existencia) {
        Producto producto = new Producto(consecutivoProducto, nombre, precio, existencia);
        productos.add(producto);
        consecutivoProducto++;
    }
    
    public void agregarCliente(String id, String nombre, String email) {
        Cliente cliente = new Cliente(id, nombre, email);
        clientes.add(cliente);
    }
    
    public Orden crearOrden(Cliente cliente) {
        Orden orden = new Orden(consecutivoOrden, cliente);
        ordenes.add(orden);
        consecutivoOrden++;
        return orden;
    }
    
    public void agregarProductoOrden(Orden orden, Producto producto, int cantidad) {
        orden.agregarProducto(producto, cantidad);
    }
    
    public List<Producto> getProductos() {
        return productos;
    }
    
    public List<Cliente> getClientes() {
        return clientes;
    }
    
    public List<Orden> getOrdenes() {
        return ordenes;
    }
}