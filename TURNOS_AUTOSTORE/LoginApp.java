package TURNOS_AUTOSTORE;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class LoginApp extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtPassword;

    public LoginApp() {
        setTitle("AutoStore - Admin Login");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal con fondo negro
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Logo AutoStore (circular)
        JLabel logoLabel = createCircularImage ("/imagenes/log1.jpeg", 150);
        gbc.gridy = 0;
        panel.add(logoLabel, gbc);

        // Campo Correo
        txtCorreo = new JTextField(20);
        txtCorreo.setFont(new Font("Arial", Font.BOLD, 16));
        txtCorreo.setForeground(Color.WHITE);
        txtCorreo.setBackground(Color.BLACK);
        txtCorreo.setCaretColor(Color.WHITE);
        txtCorreo.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2, true));
        gbc.gridy = 1;
        panel.add(txtCorreo, gbc);

        // Campo Contraseña
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.BOLD, 16));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setBackground(Color.BLACK);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2, true));
        gbc.gridy = 2;
        panel.add(txtPassword, gbc);

        // "Forgot Password?"
        JLabel forgotLabel = new JLabel("Forgot Password?");
        forgotLabel.setForeground(Color.GRAY);
        forgotLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        gbc.gridy = 3;
        panel.add(forgotLabel, gbc);

        // Botón Login
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 20));
        btnLogin.setBackground(Color.GREEN);
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        gbc.gridy = 4;
        panel.add(btnLogin, gbc);

        // Acción del botón
        btnLogin.addActionListener(e -> validarLogin());

        add(panel);
        setVisible(true);
    }

    // Validación de correo y contraseña
    private void validarLogin() {
        String correo = txtCorreo.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (correo.equals("autostore@gmail.com") && password.equals("autostore123")) {
            JOptionPane.showMessageDialog(this, "✅ Puedes Agendar un Nuevo Turno");
            dispose(); // cerrar login
            // Aquí podrías abrir la VentanaPrincipal o la pantalla de turnos
        } else {
            JOptionPane.showMessageDialog(this, "❌ Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para hacer la imagen circular
    private JLabel createCircularImage(String path, int size) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);

            BufferedImage circleBuffer = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = circleBuffer.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new Ellipse2D.Float(0, 0, size, size));
            g2.drawImage(img, 0, 0, null);
            g2.dispose();

            return new JLabel(new ImageIcon(circleBuffer));
        } catch (Exception ex) {
            return new JLabel("LOGO");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginApp::new);
    }
}