package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import control.ControladorWallRose;
import logica.Producto;
import logica.Cliente;

public class VentanaPrincipal extends JFrame {
    
    private ControladorWallRose control;
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloClientes;
    
    public VentanaPrincipal() {
        control = ControladorWallRose.getInstance();
        
        setTitle("Tienda WallRose");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Productos", panelProductos());
        tabs.addTab("Clientes", panelClientes());
        tabs.addTab("Ordenes", panelOrdenes());
        
        add(tabs);
        
        cargarProductos();
        cargarClientes();
    }
    
    private JPanel panelProductos() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnas = {"Codigo", "Nombre", "Precio", "Existencia"};
        modeloProductos = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloProductos);
        
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarProductos());
        
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(btnRefrescar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel panelClientes() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnas = {"ID", "Nombre", "Email"};
        modeloClientes = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modeloClientes);
        
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarClientes());
        
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(btnRefrescar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel panelOrdenes() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columnas = {"Numero", "Cliente", "Total"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(modelo);
        
        // Cargar ordenes
        for (logica.Orden o : control.listarOrdenes()) {
            String cliente = o.getCliente() != null ? o.getCliente().getNombre() : "Sin cliente";
            modelo.addRow(new Object[]{o.getNumero(), cliente, o.calcularTotal()});
        }
        
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void cargarProductos() {
        modeloProductos.setRowCount(0);
        for (Producto p : control.listarProductos()) {
            modeloProductos.addRow(new Object[]{
                p.getCodigo(), p.getNombre(), p.getPrecio(), p.getExistencia()
            });
        }
    }
    
    private void cargarClientes() {
        modeloClientes.setRowCount(0);
        for (Cliente c : control.listarClientes()) {
            modeloClientes.addRow(new Object[]{c.getId(), c.getNombre(), c.getEmail()});
        }
    }
}