package logica;

import control.ControladorWallRose;

public class Main {

    public static void main(String[] args) {

        ControladorWallRose control = ControladorWallRose.getInstance();

        // PRODUCTOS
        control.agregarProducto("Mouse", 5000, 10);
        control.agregarProducto("Teclado", 12000, 5);

        // CLIENTE
        control.agregarCliente("1", "Lucas", "lucas@gmail.com");

        // OBTENER CLIENTE
        Cliente cliente = control.getClientes().get(0);

        // CREAR ORDEN
        Orden orden = control.crearOrden(cliente);

        // OBTENER PRODUCTO
        Producto producto = control.getProductos().get(0);

        // AGREGAR PRODUCTO A LA ORDEN
        control.agregarProductoOrden(orden, producto, 2);

        // MOSTRAR RESULTADOS
        System.out.println("Cliente: " + cliente.getNombre());
        System.out.println("Orden: " + orden.getNumero());
        System.out.println("Total: ₡" + orden.calcularTotal());
    }
}