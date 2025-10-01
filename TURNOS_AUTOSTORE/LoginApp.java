import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class LoginApp extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtPassword;

    // --- COLORES DEFINIDOS (copiados de VentanaPrincipal) ---
    private static final Color COLOR_NEON_VERDE = new Color(57, 255, 20); // Verde neón principal
    private static final Color COLOR_GRIS_CARBON = new Color(26, 26, 26);

    // --- DATOS DE SEGURIDAD (para recuperación) ---
    private static final String PREGUNTA_SEGURIDAD = "¿Cuál es tu color favorito?";
    private static final String RESPUESTA_SECRETA = "verde"; // en minúsculas para comparación

    // Variables para la animación del borde
    private float glowAlpha = 0.0f;
    private boolean glowingUp = true;

    public LoginApp() {
        setTitle("AutoStore - Admin Login");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal con fondo oscuro y borde neón animado
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // Fondo oscuro
                g2d.setColor(COLOR_GRIS_CARBON);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Borde neón con opacidad animada
                AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glowAlpha);
                g2d.setComposite(composite);
                g2d.setColor(COLOR_NEON_VERDE);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(5, 5, getWidth() - 10, getHeight() - 10);

                g2d.dispose();
            }
        };

        // Temporizador para animar el borde (usando javax.swing.Timer)
        Timer glowTimer = new Timer(50, e -> {
            if (glowingUp) {
                glowAlpha += 0.015f;
                if (glowAlpha >= 0.8f) {
                    glowAlpha = 0.8f;
                    glowingUp = false;
                }
            } else {
                glowAlpha -= 0.015f;
                if (glowAlpha <= 0.2f) {
                    glowAlpha = 0.2f;
                    glowingUp = true;
                }
            }
            panel.repaint(); // Redibujar el panel
        });
        glowTimer.start();

        panel.setBackground(COLOR_GRIS_CARBON);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Logo AutoStore
        JLabel logoLabel = createCircularImage("/Imagenes/auto.png", 150);
        gbc.gridy = 0;
        panel.add(logoLabel, gbc);

        // Campo Correo
        txtCorreo = new JTextField(20);
        txtCorreo.setFont(new Font("Arial", Font.BOLD, 16));
        txtCorreo.setForeground(Color.WHITE);
        txtCorreo.setBackground(COLOR_GRIS_CARBON);
        txtCorreo.setCaretColor(Color.WHITE);
        txtCorreo.setBorder(BorderFactory.createLineBorder(COLOR_NEON_VERDE, 2, true));
        gbc.gridy = 1;
        panel.add(txtCorreo, gbc);

        // Campo Contraseña
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.BOLD, 16));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setBackground(COLOR_GRIS_CARBON);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createLineBorder(COLOR_NEON_VERDE, 2, true));
        gbc.gridy = 2;
        panel.add(txtPassword, gbc);

        // "Forgot Password?" - INTERACTIVO Y SEGURO
        JLabel forgotLabel = new JLabel("<html><a href='#'>Forgot Password?</a></html>");
        forgotLabel.setForeground(Color.GRAY);
        forgotLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLabel.setHorizontalAlignment(JLabel.CENTER);

        forgotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                forgotLabel.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                forgotLabel.setForeground(Color.GRAY);
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Pedir la respuesta a la pregunta de seguridad
                String respuesta = JOptionPane.showInputDialog(
                    LoginApp.this,
                    PREGUNTA_SEGURIDAD,
                    "Recuperación de Contraseña",
                    JOptionPane.QUESTION_MESSAGE
                );

                if (respuesta != null) { // null = usuario canceló
                    if (respuesta.trim().equalsIgnoreCase(RESPUESTA_SECRETA)) {
                        JOptionPane.showMessageDialog(
                            LoginApp.this,
                            "La contraseña de administrador es:\n\nautostore123",
                            "✅ Contraseña Recuperada",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                            LoginApp.this,
                            "❌ Respuesta incorrecta. Inténtalo de nuevo.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });
        gbc.gridy = 3;
        panel.add(forgotLabel, gbc);

        // Botón Login
        JButton btnLogin = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(getForeground());
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        btnLogin.setFont(new Font("Bell MT", Font.BOLD, 20));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setBackground(COLOR_NEON_VERDE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setPreferredSize(new Dimension(200, 60));
        btnLogin.addActionListener(e -> validarLogin());

        gbc.gridy = 4;
        panel.add(btnLogin, gbc);

        add(panel);
        setVisible(true);
    }

    private void validarLogin() {
        String correo = txtCorreo.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (correo.equals("autostore@gmail.com") && password.equals("autostore123")) {
            // ✅ Credenciales correctas: abrir la ventana de servicios
            SwingUtilities.invokeLater(() -> {
                new ServiciosVentana(); // Crea y muestra la nueva ventana
            });
            dispose(); // Cierra la ventana de login
        } else {
            JOptionPane.showMessageDialog(this, "❌ Credenciales incorrectas", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel createCircularImage(String relativePath, int size) {
        System.out.println("🔍 Intentando cargar: " + relativePath);

        try {
            URL resourceURL = getClass().getResource(relativePath);
            if (resourceURL == null) {
                System.err.println("❌ ERROR: Recurso NO ENCONTRADO: " + relativePath);
                return new JLabel("LOGO");
            }

            ImageIcon icon = new ImageIcon(resourceURL);
            Image img = icon.getImage();

            int width = img.getWidth(null);
            int height = img.getHeight(null);
            System.out.println("🖼️ Imagen cargada. Dimensiones: " + width + " x " + height);

            if (width <= 0 || height <= 0) {
                System.err.println("⚠️ Imagen sin dimensiones válidas.");
                return new JLabel("LOGO");
            }

            Image scaledImg = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(scaledImg));
            label.setPreferredSize(new Dimension(size, size));
            label.setMinimumSize(new Dimension(size, size));
            label.setMaximumSize(new Dimension(size, size));
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            label.setOpaque(true);
            label.setBackground(COLOR_GRIS_CARBON);

            System.out.println("✅ Imagen cargada como JLabel normal.");
            return label;
        } catch (Exception ex) {
            System.err.println("💥 Excepción inesperada: " + ex.getMessage());
            ex.printStackTrace();
            return new JLabel("LOGO");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginApp::new);
    }
}