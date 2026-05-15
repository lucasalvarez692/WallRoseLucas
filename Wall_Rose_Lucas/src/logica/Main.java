package logica;

import control.ControladorWallRose;

public class Main {

    public static void main(String[] args) {

        ControladorWallRose control = ControladorWallRose.getInstance();

        control.agregarProducto("Mouse", 5000, 10);
        control.agregarProducto("Teclado", 12000, 5);

        control.agregarCliente("1", "Lucas", "lucas@gmail.com");

        Cliente cliente = control.getClientes().get(0);

        Orden orden = control.crearOrden(cliente);

        Producto producto = control.getProductos().get(0);

        control.agregarProductoOrden(orden, producto, 2);

        System.out.println("Cliente: " + cliente.getNombre());
        System.out.println("Orden: " + orden.getNumero());
        System.out.println("Total: ₡" + orden.calcularTotal());
    }
}