// gui/VentanaPrincipal.java
package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import control.ControladorWallRose;
import logica.Cliente	;
import logica.EstadoOrden;
import logica.LineaOrden;
import logica.Orden;
import logica.Producto;

public class VentanaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;
    private ControladorWallRose control;
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloClientes;
    private DefaultTableModel modeloOrdenes;
    private DefaultTableModel modeloLineasOrden;
    
    private JTable tablaProductos;
    private JTable tablaClientes;
    private JTable tablaOrdenes;
    private JTable tablaLineas;
    
    private int ordenSeleccionadaNumero = -1;

    public VentanaPrincipal() {
        control = ControladorWallRose.getInstance();

        setTitle("Tienda WallRose - Sistema de Gestión");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Productos", panelProductos());
        tabs.addTab("Clientes", panelClientes());
        tabs.addTab("Órdenes", panelOrdenes());

        add(tabs);
        
        cargarProductos();
        cargarClientes();
        cargarOrdenes();
    }

    // ==================== PANEL PRODUCTOS ====================
    private JPanel panelProductos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columnas = {"Código", "Nombre", "Precio (₡)", "Existencia"};
        modeloProductos = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaProductos = new JTable(modeloProductos);
        tablaProductos.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("➕ Agregar Producto");
        JButton btnEditar = new JButton("✏️ Editar Producto");
        JButton btnEliminar = new JButton("🗑️ Eliminar Producto");
        JButton btnRefrescar = new JButton("🔄 Refrescar");
        
        btnAgregar.addActionListener(e -> dialogAgregarProducto());
        btnEditar.addActionListener(e -> dialogEditarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnRefrescar.addActionListener(e -> cargarProductos());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);
        
        panel.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void dialogAgregarProducto() {
        JDialog dialog = new JDialog(this, "Agregar Producto", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField txtNombre = new JTextField(20);
        JTextField txtPrecio = new JTextField(20);
        JTextField txtExistencia = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtPrecio, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Existencia:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtExistencia, gbc);
        
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(btnGuardar, gbc);
        gbc.gridx = 1;
        dialog.add(btnCancelar, gbc);
        
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                double existencia = Double.parseDouble(txtExistencia.getText().trim());
                
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "El nombre no puede estar vacío");
                    return;
                }
                if (precio < 0 || existencia < 0) {
                    JOptionPane.showMessageDialog(dialog, "Precio y existencia deben ser ≥ 0");
                    return;
                }
                
                if (control.crearProducto(nombre, existencia, precio)) {
                    JOptionPane.showMessageDialog(dialog, "Producto agregado exitosamente");
                    cargarProductos();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al agregar producto");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Ingrese valores numéricos válidos");
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    private void dialogEditarProducto() {
        int selectedRow = tablaProductos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar");
            return;
        }
        
        int codigo = (int) modeloProductos.getValueAt(selectedRow, 0);
        Producto producto = control.obtenerProductoPorCodigo(codigo);
        if (producto == null) return;
        
        JDialog dialog = new JDialog(this, "Editar Producto", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField txtNombre = new JTextField(producto.getNombre(), 20);
        JTextField txtPrecio = new JTextField(String.valueOf(producto.getPrecio()), 20);
        JTextField txtExistencia = new JTextField(String.valueOf(producto.getExistencia()), 20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtPrecio, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Existencia:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtExistencia, gbc);
        
        JButton btnGuardar = new JButton("Actualizar");
        JButton btnCancelar = new JButton("Cancelar");
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(btnGuardar, gbc);
        gbc.gridx = 1;
        dialog.add(btnCancelar, gbc);
        
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                double existencia = Double.parseDouble(txtExistencia.getText().trim());
                
                if (control.actualizarProducto(codigo, nombre, existencia, precio)) {
                    JOptionPane.showMessageDialog(dialog, "Producto actualizado");
                    cargarProductos();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al actualizar");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Valores inválidos");
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    private void eliminarProducto() {
        int selectedRow = tablaProductos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar");
            return;
        }
        
        int codigo = (int) modeloProductos.getValueAt(selectedRow, 0);
        String nombre = (String) modeloProductos.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar producto '" + nombre + "'?\n(No se puede eliminar si está en uso)", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (control.borrarProducto(codigo)) {
                JOptionPane.showMessageDialog(this, "Producto eliminado");
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar: el producto está en uso en alguna orden");
            }
        }
    }
    
    private void cargarProductos() {
        modeloProductos.setRowCount(0);
        for (Producto p : control.listarProductos()) {
            modeloProductos.addRow(new Object[]{
                p.getCodigo(), p.getNombre(), p.getPrecio(), p.getExistencia()
            });
        }
    }

    // ==================== PANEL CLIENTES ====================
    private JPanel panelClientes() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columnas = {"ID", "Nombre", "Email"};
        modeloClientes = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaClientes = new JTable(modeloClientes);
        tablaClientes.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("➕ Agregar Cliente");
        JButton btnEditar = new JButton("✏️ Editar Cliente");
        JButton btnEliminar = new JButton("🗑️ Eliminar Cliente");
        JButton btnRefrescar = new JButton("🔄 Refrescar");
        
        btnAgregar.addActionListener(e -> dialogAgregarCliente());
        btnEditar.addActionListener(e -> dialogEditarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnRefrescar.addActionListener(e -> cargarClientes());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);
        
        panel.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void dialogAgregarCliente() {
        JDialog dialog = new JDialog(this, "Agregar Cliente", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField txtId = new JTextField(20);
        JTextField txtNombre = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtId, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtEmail, gbc);
        
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(btnGuardar, gbc);
        gbc.gridx = 1;
        dialog.add(btnCancelar, gbc);
        
        btnGuardar.addActionListener(e -> {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String email = txtEmail.getText().trim();
            
            if (id.isEmpty() || nombre.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios");
                return;
            }
            
            if (control.crearCliente(id, nombre, email)) {
                JOptionPane.showMessageDialog(dialog, "Cliente agregado");
                cargarClientes();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "ID duplicado o email inválido");
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    private void dialogEditarCliente() {
        int selectedRow = tablaClientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para editar");
            return;
        }
        
        String id = (String) modeloClientes.getValueAt(selectedRow, 0);
        String nombreActual = (String) modeloClientes.getValueAt(selectedRow, 1);
        String emailActual = (String) modeloClientes.getValueAt(selectedRow, 2);
        
        JDialog dialog = new JDialog(this, "Editar Cliente", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField txtNombre = new JTextField(nombreActual, 20);
        JTextField txtEmail = new JTextField(emailActual, 20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(new JLabel(id), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtEmail, gbc);
        
        JButton btnGuardar = new JButton("Actualizar");
        JButton btnCancelar = new JButton("Cancelar");
        
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(btnGuardar, gbc);
        gbc.gridx = 1;
        dialog.add(btnCancelar, gbc);
        
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String email = txtEmail.getText().trim();
            
            if (control.actualizarCliente(id, nombre, email)) {
                JOptionPane.showMessageDialog(dialog, "Cliente actualizado");
                cargarClientes();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error al actualizar");
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    private void eliminarCliente() {
        int selectedRow = tablaClientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar");
            return;
        }
        
        String id = (String) modeloClientes.getValueAt(selectedRow, 0);
        String nombre = (String) modeloClientes.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar cliente '" + nombre + "'?\n(No se puede eliminar si tiene órdenes asociadas)", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (control.borrarCliente(id)) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado");
                cargarClientes();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar: el cliente tiene órdenes asociadas");
            }
        }
    }
    
    private void cargarClientes() {
        modeloClientes.setRowCount(0);
        for (Cliente c : control.listarClientes()) {
            modeloClientes.addRow(new Object[]{c.getId(), c.getNombre(), c.getEmail()});
        }
    }

    // ==================== PANEL ÓRDENES ====================
    private JPanel panelOrdenes() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con botones de orden
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevaOrden = new JButton("🆕 Nueva Orden");
        JButton btnAgregarProducto = new JButton("➕ Agregar Producto a Orden");
        JButton btnEditarLinea = new JButton("✏️ Editar Línea");
        JButton btnEliminarLinea = new JButton("🗑️ Eliminar Línea");
        JButton btnCambiarEstado = new JButton("📝 Cambiar Estado");
        JButton btnRefrescar = new JButton("🔄 Refrescar");
        
        btnNuevaOrden.addActionListener(e -> dialogNuevaOrden());
        btnAgregarProducto.addActionListener(e -> dialogAgregarProductoAOrden());
        btnEditarLinea.addActionListener(e -> dialogEditarLineaOrden());
        btnEliminarLinea.addActionListener(e -> eliminarLineaOrden());
        btnCambiarEstado.addActionListener(e -> dialogCambiarEstadoOrden());
        btnRefrescar.addActionListener(e -> {
            cargarOrdenes();
            if (ordenSeleccionadaNumero != -1) {
                cargarLineasOrden(ordenSeleccionadaNumero);
            }
        });
        
        panelSuperior.add(btnNuevaOrden);
        panelSuperior.add(btnAgregarProducto);
        panelSuperior.add(btnEditarLinea);
        panelSuperior.add(btnEliminarLinea);
        panelSuperior.add(btnCambiarEstado);
        panelSuperior.add(btnRefrescar);
        
        // Tabla de órdenes
        String[] columnasOrdenes = {"Número", "Cliente", "Estado", "Total (₡)"};
        modeloOrdenes = new DefaultTableModel(columnasOrdenes, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaOrdenes = new JTable(modeloOrdenes);
        tablaOrdenes.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener para seleccionar orden y mostrar sus líneas
        tablaOrdenes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tablaOrdenes.getSelectedRow();
                if (selectedRow != -1) {
                    ordenSeleccionadaNumero = (int) modeloOrdenes.getValueAt(selectedRow, 0);
                    cargarLineasOrden(ordenSeleccionadaNumero);
                }
            }
        });
        
        // Tabla de líneas de orden
        String[] columnasLineas = {"Producto Código", "Producto", "Cantidad", "Subtotal (₡)"};
        modeloLineasOrden = new DefaultTableModel(columnasLineas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaLineas = new JTable(modeloLineasOrden);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(tablaOrdenes), new JScrollPane(tablaLineas));
        splitPane.setResizeWeight(0.5);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void dialogNuevaOrden() {
        // Primero seleccionar cliente
        if (control.listarClientes().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe haber al menos un cliente para crear una orden");
            return;
        }
        
        String[] clientesArray = control.listarClientes().stream()
            .map(c -> c.getId() + " - " + c.getNombre())
            .toArray(String[]::new);
        
        String seleccion = (String) JOptionPane.showInputDialog(this, 
            "Seleccione el cliente:", "Nueva Orden",
            JOptionPane.QUESTION_MESSAGE, null, clientesArray, clientesArray[0]);
        
        if (seleccion != null) {
            String idCliente = seleccion.split(" - ")[0];
            Cliente cliente = control.obtenerClientePorId(idCliente);
            if (cliente != null) {
                Orden orden = control.crearOrden(cliente);
                JOptionPane.showMessageDialog(this, "Orden #" + orden.getNumero() + " creada en estado BORRADOR");
                cargarOrdenes();
            }
        }
    }
    
    private void dialogAgregarProductoAOrden() {
        if (ordenSeleccionadaNumero == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden primero");
            return;
        }
        
        Orden orden = control.obtenerDatosOrden(ordenSeleccionadaNumero);
        if (orden == null || orden.getEstado() != EstadoOrden.BORRADOR) {
            JOptionPane.showMessageDialog(this, "Solo se pueden agregar productos a órdenes en estado BORRADOR");
            return;
        }
        
        if (control.listarProductos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos disponibles");
            return;
        }
        
        JDialog dialog = new JDialog(this, "Agregar Producto a Orden #" + ordenSeleccionadaNumero, true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        String[] productosArray = control.listarProductos().stream()
            .map(p -> p.getCodigo() + " - " + p.getNombre() + " (Stock: " + p.getExistencia() + ")")
            .toArray(String[]::new);
        
        JComboBox<String> cbProductos = new JComboBox<>(productosArray);
        JTextField txtCantidad = new JTextField(10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Producto:"), gbc);
        gbc.gridx = 1;
        dialog.add(cbProductos, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtCantidad, gbc);
        
        JButton btnAgregar = new JButton("Agregar");
        JButton btnCancelar = new JButton("Cancelar");
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(btnAgregar, gbc);
        gbc.gridx = 1;
        dialog.add(btnCancelar, gbc);
        
        btnAgregar.addActionListener(e -> {
            try {
                String seleccionProducto = (String) cbProductos.getSelectedItem();
                int codigo = Integer.parseInt(seleccionProducto.split(" - ")[0]);
                double cantidad = Double.parseDouble(txtCantidad.getText().trim());
                
                if (control.agregarLinea(ordenSeleccionadaNumero, codigo, cantidad)) {
                    JOptionPane.showMessageDialog(dialog, "Producto agregado a la orden");
                    cargarLineasOrden(ordenSeleccionadaNumero);
                    cargarOrdenes(); // Actualizar total
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error: Producto no disponible o cantidad insuficiente");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Cantidad inválida");
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    private void dialogEditarLineaOrden() {
        if (ordenSeleccionadaNumero == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden primero");
            return;
        }
        
        int selectedLinea = tablaLineas.getSelectedRow();
        if (selectedLinea == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una línea para editar");
            return;
        }
        
        Orden orden = control.obtenerDatosOrden(ordenSeleccionadaNumero);
        if (orden == null || orden.getEstado() != EstadoOrden.BORRADOR) {
            JOptionPane.showMessageDialog(this, "Solo se pueden editar líneas de órdenes en BORRADOR");
            return;
        }
        
        LineaOrden linea = orden.getLineas().get(selectedLinea);
        Producto productoActual = linea.getProducto();
        double cantidadActual = linea.getCantidad();
        
        JDialog dialog = new JDialog(this, "Editar Línea de Orden #" + ordenSeleccionadaNumero, true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        String[] productosArray = control.listarProductos().stream()
            .map(p -> p.getCodigo() + " - " + p.getNombre() + " (Stock: " + p.getExistencia() + ")")
            .toArray(String[]::new);
        
        JComboBox<String> cbProductos = new JComboBox<>(productosArray);
        // Seleccionar el producto actual
        for (int i = 0; i < productosArray.length; i++) {
            if (productosArray[i].startsWith(String.valueOf(productoActual.getCodigo()))) {
                cbProductos.setSelectedIndex(i);
                break;
            }
        }
        
        JTextField txtCantidad = new JTextField(String.valueOf(cantidadActual), 10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Producto:"), gbc);
        gbc.gridx = 1;
        dialog.add(cbProductos, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtCantidad, gbc);
        
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnCancelar = new JButton("Cancelar");
        
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(btnActualizar, gbc);
        gbc.gridx = 1;
        dialog.add(btnCancelar, gbc);
        
        btnActualizar.addActionListener(e -> {
            try {
                String seleccionProducto = (String) cbProductos.getSelectedItem();
                int codigo = Integer.parseInt(seleccionProducto.split(" - ")[0]);
                double cantidad = Double.parseDouble(txtCantidad.getText().trim());
                
                if (control.actualizarLinea(ordenSeleccionadaNumero, selectedLinea, codigo, cantidad)) {
                    JOptionPane.showMessageDialog(dialog, "Línea actualizada");
                    cargarLineasOrden(ordenSeleccionadaNumero);
                    cargarOrdenes();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Error al actualizar");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Cantidad inválida");
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    private void eliminarLineaOrden() {
        if (ordenSeleccionadaNumero == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden primero");
            return;
        }
        
        int selectedLinea = tablaLineas.getSelectedRow();
        if (selectedLinea == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una línea para eliminar");
            return;
        }
        
        Orden orden = control.obtenerDatosOrden(ordenSeleccionadaNumero);
        if (orden == null || orden.getEstado() != EstadoOrden.BORRADOR) {
            JOptionPane.showMessageDialog(this, "Solo se pueden eliminar líneas de órdenes en BORRADOR");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar esta línea de la orden?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (control.borrarLinea(ordenSeleccionadaNumero, selectedLinea)) {
                JOptionPane.showMessageDialog(this, "Línea eliminada");
                cargarLineasOrden(ordenSeleccionadaNumero);
                cargarOrdenes();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar línea");
            }
        }
    }
    
    private void dialogCambiarEstadoOrden() {
        if (ordenSeleccionadaNumero == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden primero");
            return;
        }
        
        Orden orden = control.obtenerDatosOrden(ordenSeleccionadaNumero);
        if (orden == null) return;
        
        EstadoOrden estadoActual = orden.getEstado();
        String[] opciones;
        
        if (estadoActual == EstadoOrden.BORRADOR) {
            opciones = new String[]{"PENDIENTE"};
        } else if (estadoActual == EstadoOrden.PENDIENTE) {
            opciones = new String[]{"TERMINADA"};
        } else {
            JOptionPane.showMessageDialog(this, "Órdenes TERMINADAS no pueden cambiar de estado");
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Cambiar orden #" + ordenSeleccionadaNumero + " de " + estadoActual + " a " + opciones[0] + "?", 
            "Cambiar Estado", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            boolean exito = false;
            if (estadoActual == EstadoOrden.BORRADOR) {
                exito = control.ponerOrdenPermanente(ordenSeleccionadaNumero);
            } else if (estadoActual == EstadoOrden.PENDIENTE) {
                exito = control.ponerOrdenTerminada(ordenSeleccionadaNumero);
            }
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Estado actualizado");
                cargarOrdenes();
                cargarLineasOrden(ordenSeleccionadaNumero);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo cambiar el estado");
            }
        }
    }
    
    private void cargarOrdenes() {
        modeloOrdenes.setRowCount(0);
        for (Orden o : control.listarOrdenes()) {
            String cliente = o.getCliente() != null ? o.getCliente().getNombre() : "Sin cliente";
            modeloOrdenes.addRow(new Object[]{
                o.getNumero(), cliente, o.getEstado(), o.calcularTotal()
            });
        }
    }
    
    private void cargarLineasOrden(int numeroOrden) {
        modeloLineasOrden.setRowCount(0);
        Orden orden = control.obtenerDatosOrden(numeroOrden);
        if (orden != null) {
            for (LineaOrden linea : orden.getLineas()) {
                modeloLineasOrden.addRow(new Object[]{
                    linea.getProducto().getCodigo(),
                    linea.getProducto().getNombre(),
                    linea.getCantidad(),
                    linea.calcularSubtotal()
                });
            }
        }
    }
}