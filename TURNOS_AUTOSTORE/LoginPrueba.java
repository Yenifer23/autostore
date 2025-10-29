import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoginPrueba extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JCheckBox chkRecordarme;
    private JButton btnIngresar;
    private JLabel lblOlvidoPassword; 

    // --- COLORES ---
    private static final Color COLOR_AZUL_OSCURO_FONDO = new Color(0x052659); // #052659 (Fondo Curvo Azul Oscuro)
    
    // ⭐ CAMBIADO: La curva y el fondo del formulario ahora son #E1D4C2 (Beige cálido)
    private static final Color COLOR_FONDO_TARJETA = new Color(0xE1D4C2); 
    
    private static final Color COLOR_TEXTO_OSCURO = new Color(30, 30, 30); // Texto oscuro para contraste
    private static final Color COLOR_CAMPO_CLARO = Color.WHITE; // Campos de texto en blanco
    private static final Color COLOR_BORDE_CAMPO = new Color(180, 180, 180); // Borde de campo
    private static final Color COLOR_AZUL_LINK = new Color(0, 102, 204); // Azul para links

    // --- RUTA LOGO ---
    private static final String RUTA_LOGO = "C:\\Users\\yenif\\OneDrive\\Documentos\\2025\\programar\\autostore\\TURNOS_AUTOSTORE\\src\\main\\resources\\Imagenes\\logo turno.png";

    // ==========================================================
    // CLASE PERSONALIZADA PARA EL FONDO CURVO
    // ==========================================================
    private class PanelCurvo extends JPanel {
        
        public PanelCurvo() {
            // El fondo del panel IZQUIERDO es el color azul oscuro
            this.setBackground(COLOR_AZUL_OSCURO_FONDO);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // DIBUJAR LA FORMA DE "PANZA" (AHORA BEIGE #E1D4C2) SOBRE EL FONDO AZUL
            GeneralPath path = new GeneralPath();
            
            // Empezar en la esquina superior derecha del panel
            path.moveTo(w, 0); 
            
            // Los puntos de control ajustados para la curva más ancha
            path.curveTo(
                w * 0.75, h * 0.25,     // Control Point 1
                w * 0.6, h * 0.75,      // Control Point 2
                w, h * 1.2              // End Point 
            );
            
            // Cerrar el camino
            path.lineTo(w, h); 
            path.lineTo(w, 0); 
            path.closePath(); 

            // ⭐ CAMBIADO: Rellenar la forma curva con COLOR_FONDO_TARJETA (#E1D4C2)
            g2.setColor(COLOR_FONDO_TARJETA);
            g2.fill(path);
            
            g2.dispose();
        }
    }
    // ==========================================================

    public LoginPrueba() {
        setTitle("AutoStore - Ingreso");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // === Panel izquierdo: FONDO CURVO ===
        PanelCurvo panelIzquierdo = new PanelCurvo();
        panelIzquierdo.setLayout(null); 

        // === Panel derecho: FORMULARIO ===
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        // El fondo del panel derecho es ahora #E1D4C2
        panelDerecho.setBackground(COLOR_FONDO_TARJETA); 

        // Crear la tarjeta interna del formulario
        JPanel tarjeta = new JPanel(new GridBagLayout());
        // El fondo de la tarjeta es ahora #E1D4C2
        tarjeta.setBackground(COLOR_FONDO_TARJETA);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40)); 
        tarjeta.setPreferredSize(new Dimension(400, 650)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Título ---
        JLabel lblTitle = new JLabel("¡Bienvenido!", SwingConstants.CENTER); 
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32)); 
        // El título usa el color de texto oscuro
        lblTitle.setForeground(COLOR_TEXTO_OSCURO); 
        gbc.insets = new Insets(10, 10, 5, 10); 
        tarjeta.add(lblTitle, gbc);

        // --- LOGO ---
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE; 
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel logoLabel = new JLabel();
        boolean logoCargado = false;
        try {
            File logoFile = new File(RUTA_LOGO);
            if (logoFile.exists()) {
                BufferedImage img = ImageIO.read(logoFile);
                Image scaledLogo = img.getScaledInstance(180, 150, Image.SCALE_SMOOTH); 
                logoLabel.setIcon(new ImageIcon(scaledLogo));
                logoCargado = true;
            } else {
                logoLabel.setText("AUTOSTORE");
                logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                logoLabel.setForeground(COLOR_TEXTO_OSCURO);
            }
        } catch (IOException e) {
            System.err.println("❌ Error al cargar el logo: " + e.getMessage());
            logoLabel.setText("AUTOSTORE");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            logoLabel.setForeground(COLOR_TEXTO_OSCURO);
        }

        gbc.insets = new Insets(5, 10, 5, 10);
        tarjeta.add(logoLabel, gbc);

        // --- Subtítulo ---
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel lblSubtitle = new JLabel("Inicia sesión para continuar", SwingConstants.CENTER); 
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(Color.GRAY.darker()); 
        gbc.insets = new Insets(5, 10, 25, 10); 
        tarjeta.add(lblSubtitle, gbc);

        // --- Campo Email ---
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 10, 5, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        JLabel lblEmail = new JLabel("Correo Electrónico"); 
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEmail.setForeground(COLOR_TEXTO_OSCURO);
        tarjeta.add(lblEmail, gbc);

        gbc.gridy = 4;
        txtEmail = new JTextField(20);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtEmail.setForeground(COLOR_TEXTO_OSCURO);
        txtEmail.setBackground(COLOR_CAMPO_CLARO);
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE_CAMPO, 1), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10) 
        ));
        txtEmail.setCaretColor(COLOR_TEXTO_OSCURO);
        tarjeta.add(txtEmail, gbc);

        // --- Campo Contraseña ---
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 10, 5, 10); 
        JLabel lblPassword = new JLabel("Contraseña"); 
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(COLOR_TEXTO_OSCURO);
        tarjeta.add(lblPassword, gbc);

        gbc.gridy = 6;
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setForeground(COLOR_TEXTO_OSCURO);
        txtPassword.setBackground(COLOR_CAMPO_CLARO);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE_CAMPO, 1), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10) 
        ));
        txtPassword.setEchoChar('•');
        txtPassword.setCaretColor(COLOR_TEXTO_OSCURO);
        tarjeta.add(txtPassword, gbc);

        // --- Recordarme + ¿Olvidaste tu contraseña? ---
        gbc.gridy = 7;
        gbc.insets = new Insets(15, 10, 25, 10); 
        JPanel row1 = new JPanel(new BorderLayout());
        // El fondo del panel row1 es ahora #E1D4C2
        row1.setBackground(COLOR_FONDO_TARJETA);

        chkRecordarme = new JCheckBox("Recordarme"); 
        chkRecordarme.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkRecordarme.setForeground(COLOR_TEXTO_OSCURO);
        // El fondo del checkbox es ahora #E1D4C2
        chkRecordarme.setBackground(COLOR_FONDO_TARJETA);

        lblOlvidoPassword = new JLabel("¿Olvidaste tu Contraseña?"); 
        lblOlvidoPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblOlvidoPassword.setForeground(COLOR_AZUL_LINK);
        lblOlvidoPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblOlvidoPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(LoginPrueba.this,
                    "Recuperación de contraseña:\nEnvía un correo a soporte@autostore.com",
                    "Recuperar contraseña",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        row1.add(chkRecordarme, BorderLayout.WEST);
        row1.add(lblOlvidoPassword, BorderLayout.EAST);
        tarjeta.add(row1, gbc);

        // --- Botón Ingresar ---
        gbc.gridy = 8;
        gbc.insets = new Insets(20, 10, 0, 10); 
        gbc.fill = GridBagConstraints.NONE; 
        gbc.anchor = GridBagConstraints.CENTER;
        btnIngresar = new JButton("Ingresar") { 
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25); 
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setBackground(COLOR_AZUL_OSCURO_FONDO); 
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setContentAreaFilled(false);
        btnIngresar.setPreferredSize(new Dimension(300, 50)); 
        btnIngresar.addActionListener(e -> validarLogin());
        tarjeta.add(btnIngresar, gbc);

        // Añadir tarjeta al panel derecho
        panelDerecho.add(tarjeta);

        // === Layout principal: 50% / 50% ===
        setLayout(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();

        // Panel izquierdo (fondo curvo)
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 0.5;
        gbcMain.weighty = 1.0;
        gbcMain.fill = GridBagConstraints.BOTH;
        add(panelIzquierdo, gbcMain);

        // Panel derecho (formulario de login)
        gbcMain.gridx = 1;
        gbcMain.weightx = 0.5;
        add(panelDerecho, gbcMain);

        setVisible(true);
    }

    private void validarLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if ("autostore@gmail.com".equals(email) && "autostore123".equals(password)) {
            JOptionPane.showMessageDialog(this, "✅ Ingreso exitoso!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Credenciales incorrectas", "Error de Ingreso", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel del sistema: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> new LoginPrueba());
    }
}