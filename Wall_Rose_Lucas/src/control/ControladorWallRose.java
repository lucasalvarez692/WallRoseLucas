package control;

import java.util.ArrayList;
import java.util.List;
import logica.Cliente;
import logica.Orden;
import logica.Producto;
import logica.LineaOrden;
import logica.EstadoOrden;

public class ControladorWallRose {
    private static ControladorWallRose instancia;
    
    private int consecutivoProducto;
    private int nextNumeroOrden;
    private List<Producto> productos;
    private List<Cliente> clientes;
    private List<Orden> ordenes;
    
    private ControladorWallRose() {
        consecutivoProducto = 1;
        nextNumeroOrden = 1;
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
    
    // ==================== MÉTODOS PARA MAIN ====================
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
        Orden orden = new Orden(nextNumeroOrden, cliente);
        ordenes.add(orden);
        nextNumeroOrden++;
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
    
    // ==================== CLIENTES ====================
    public List<Cliente> listarClientes() {
        return new ArrayList<>(clientes);
    }
    
    public Cliente obtenerClientePorId(String id) {
        for (Cliente c : clientes) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }
    
    public List<Orden> listarOrdenesPorCliente(String idCliente) {
        List<Orden> ordenesCliente = new ArrayList<>();
        for (Orden o : ordenes) {
            if (o.getCliente() != null && o.getCliente().getId().equals(idCliente)) {
                ordenesCliente.add(o);
            }
        }
        return ordenesCliente;
    }
    
    public List<Orden> listarOrdenesPendientes(String idCliente) {
        List<Orden> pendientes = new ArrayList<>();
        for (Orden o : ordenes) {
            if (o.getCliente() != null && 
                o.getCliente().getId().equals(idCliente) && 
                o.getEstado() == EstadoOrden.PENDIENTE) {
                pendientes.add(o);
            }
        }
        return pendientes;
    }
    
    public List<Orden> listarOrdenesTerminadas(String idCliente) {
        List<Orden> terminadas = new ArrayList<>();
        for (Orden o : ordenes) {
            if (o.getCliente() != null && 
                o.getCliente().getId().equals(idCliente) && 
                o.getEstado() == EstadoOrden.TERMINADA) {
                terminadas.add(o);
            }
        }
        return terminadas;
    }
    
    public boolean crearCliente(String id, String nombre, String email) {
        if (idClienteRepetido(id)) return false;
        if (!emailValido(email)) return false;
        clientes.add(new Cliente(id, nombre, email));
        return true;
    }
    
    public boolean actualizarCliente(String id, String nombre, String email) {
        for (Cliente c : clientes) {
            if (c.getId().equals(id)) {
                c.setNombre(nombre);
                c.setEmail(email);
                return true;
            }
        }
        return false;
    }
    
    public boolean borrarCliente(String id) {
        for (Orden o : ordenes) {
            if (o.getCliente() != null && o.getCliente().getId().equals(id)) {
                return false;
            }
        }
        return clientes.removeIf(c -> c.getId().equals(id));
    }
    
    public double getTotalPendienteCliente(String idCliente) {
        double total = 0;
        for (Orden o : ordenes) {
            if (o.getCliente() != null && 
                o.getCliente().getId().equals(idCliente) && 
                o.getEstado() == EstadoOrden.PENDIENTE) {
                total += o.calcularTotal();
            }
        }
        return total;
    }
    
    // ==================== PRODUCTOS ====================
    public List<Producto> listarProductos() {
        return new ArrayList<>(productos);
    }
    
    public Producto obtenerProductoPorCodigo(int codigo) {
        for (Producto p : productos) {
            if (p.getCodigo() == codigo) {
                return p;
            }
        }
        return null;
    }
    
    public boolean crearProducto(String nombre, double existencia, double precio) {
        if (existencia < 0 || precio < 0) return false;
        Producto producto = new Producto(consecutivoProducto, nombre, precio, existencia);
        productos.add(producto);
        consecutivoProducto++;
        return true;
    }
    
    public boolean actualizarProducto(int codigo, String nombre, double existencia, double precio) {
        for (Producto p : productos) {
            if (p.getCodigo() == codigo) {
                p.setNombre(nombre);
                p.setExistencia(existencia);
                p.setPrecio(precio);
                return true;
            }
        }
        return false;
    }
    
    public boolean borrarProducto(int codigo) {
        if (productoEnUso(codigo)) return false;
        return productos.removeIf(p -> p.getCodigo() == codigo);
    }
    
    // ==================== ORDENES ====================
    public List<Orden> listarOrdenes() {
        return new ArrayList<>(ordenes);
    }
    
    public double obtenerMontoTotalPendiente() {
        double total = 0;
        for (Orden o : ordenes) {
            if (o.getEstado() == EstadoOrden.PENDIENTE) {
                total += o.calcularTotal();
            }
        }
        return total;
    }
    
    public int obtenerOrdenVacia(String idCliente) {
        for (Orden o : ordenes) {
            if (o.getCliente() != null &&
                o.getCliente().getId().equals(idCliente) && 
                o.getEstado() == EstadoOrden.BORRADOR && 
                o.getLineas().isEmpty()) {
                return o.getNumero();
            }
        }
        return -1;
    }
    
    public Orden obtenerDatosOrden(int numero) {
        for (Orden o : ordenes) {
            if (o.getNumero() == numero) {
                return o;
            }
        }
        return null;
    }
    
    public List<LineaOrden> listarLineasDeOrden(int numeroOrden) {
        for (Orden o : ordenes) {
            if (o.getNumero() == numeroOrden) {
                return new ArrayList<>(o.getLineas());
            }
        }
        return new ArrayList<>();
    }
    
    public boolean ponerOrdenPermanente(int numero) {
        for (Orden o : ordenes) {
            if (o.getNumero() == numero && o.getEstado() == EstadoOrden.BORRADOR && !o.getLineas().isEmpty()) {
                o.setEstado(EstadoOrden.PENDIENTE);
                return true;
            }
        }
        return false;
    }
    
    public boolean ponerOrdenTerminada(int numero) {
        for (Orden o : ordenes) {
            if (o.getNumero() == numero && o.getEstado() == EstadoOrden.PENDIENTE) {
                o.setEstado(EstadoOrden.TERMINADA);
                return true;
            }
        }
        return false;
    }
    
    public boolean agregarLinea(int numeroOrden, int codigoProducto, double cantidad) {
        Orden orden = buscarOrdenPorNumero(numeroOrden);
        if (orden == null || orden.getEstado() != EstadoOrden.BORRADOR) return false;
        
        Producto producto = obtenerProductoPorCodigo(codigoProducto);
        if (producto == null || !producto.disponible(cantidad)) return false;
        
        orden.agregarProducto(producto, cantidad);
        return true;
    }
    
    public boolean actualizarLinea(int numeroOrden, int indiceLinea, int codigoProducto, double cantidad) {
        Orden orden = buscarOrdenPorNumero(numeroOrden);
        if (orden == null || orden.getEstado() != EstadoOrden.BORRADOR) return false;
        
        Producto producto = obtenerProductoPorCodigo(codigoProducto);
        if (producto == null || !producto.disponible(cantidad)) return false;
        
        List<LineaOrden> lineas = orden.getLineas();
        if (indiceLinea >= 0 && indiceLinea < lineas.size()) {
            LineaOrden linea = lineas.get(indiceLinea);
            linea.setProducto(producto);
            linea.setCantidad(cantidad);
            return true;
        }
        return false;
    }
    
    public boolean borrarLinea(int numeroOrden, int indiceLinea) {
        Orden orden = buscarOrdenPorNumero(numeroOrden);
        if (orden == null || orden.getEstado() != EstadoOrden.BORRADOR) return false;
        
        List<LineaOrden> lineas = orden.getLineas();
        if (indiceLinea >= 0 && indiceLinea < lineas.size()) {
            lineas.remove(indiceLinea);
            return true;
        }
        return false;
    }
    
    // ==================== VALIDACIONES ====================
    public boolean idClienteRepetido(String id) {
        for (Cliente c : clientes) {
            if (c.getId().equals(id)) return true;
        }
        return false;
    }
    
    public boolean emailValido(String email) {
        return email != null && email.contains("@");
    }
    
    public boolean productoEnUso(int codigo) {
        for (Orden o : ordenes) {
            for (LineaOrden l : o.getLineas()) {
                if (l.getProducto().getCodigo() == codigo) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean ordenEnBorrador(int numero) {
        Orden orden = buscarOrdenPorNumero(numero);
        return orden != null && orden.getEstado() == EstadoOrden.BORRADOR;
    }
    
    // ==================== MÉTODOS PRIVADOS ====================
    private Orden buscarOrdenPorNumero(int numero) {
        for (Orden o : ordenes) {
            if (o.getNumero() == numero) return o;
        }
        return null;
    }
}