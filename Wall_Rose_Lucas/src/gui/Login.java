package gui;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Login() {
        setTitle("Login Tienda");
        setSize(300, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        panel.add(new JLabel("Usuario:"));
        JTextField txtUser = new JTextField();
        panel.add(txtUser);
        
        panel.add(new JLabel("Clave:"));
        JPasswordField txtPass = new JPasswordField();
        panel.add(txtPass);
        
        JButton btnIngresar = new JButton("Ingresar");
        JButton btnSalir = new JButton("Salir");
        
        btnIngresar.addActionListener(e -> {
            if (txtUser.getText().equals("admin")) {
                new VentanaPrincipal().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Usuario incorrecto");
            }
        });
        
        btnSalir.addActionListener(e -> System.exit(0));
        
        panel.add(btnIngresar);
        panel.add(btnSalir);
        
        add(panel);
    }
    
    public static void main(String[] args) {
        new Login().setVisible(true);
    }
}