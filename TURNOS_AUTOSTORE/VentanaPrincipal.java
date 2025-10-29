import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VentanaPrincipal extends JFrame {

    // --- RUTAS DE IMAGENES ---
    private static final String RUTA_LOGO_NUEVO = "/Imagenes/logonuevo25.png";

    // --- COLORES DEFINIDOS ---
    private static final Color COLOR_NEON_VERDE = new Color(57, 255, 20); // #39FF14
    private static final Color COLOR_GRIS_CARBON = new Color(26, 26, 26);
    private static final Color COLOR_TRANSPARENTE = new Color(0, 0, 0, 0);

    // Referencias
    private NeonBackgroundPanel panelPrincipal;
    private JLabel labelLogo;
    private JPanel tarjetaCentral; // Para poder pintar el glow del logo dentro de ella

    // =======================================================================
    // CLASE INTERNA: NeonBackgroundPanel (Fondo con partículas)
    // =======================================================================
    private class NeonBackgroundPanel extends JPanel {
        private Color backgroundColor;
        private Color neonColor;
        private Random rand = new Random();
        private List<Particle> particles;
        private final int NUM_PARTICLES = 50;

        public NeonBackgroundPanel(Color bgColor, Color neonC) {
            this.backgroundColor = bgColor;
            this.neonColor = neonC;
            setOpaque(true);
            particles = new ArrayList<>();
            for (int i = 0; i < NUM_PARTICLES; i++) {
                particles.add(new Particle(rand.nextInt(1200), rand.nextInt(700), rand.nextInt(3) + 1));
            }
        }

        public void updateAnimations() {
            for (Particle p : particles) {
                p.update(getWidth(), getHeight());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(backgroundColor);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            // Borde neón sutil
            AlphaComposite borderAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f);
            g2d.setComposite(borderAlpha);
            g2d.setColor(neonColor);
            g2d.setStroke(new BasicStroke(2));
            int offset = 30;
            int cornerArc = 20;
            g2d.drawRoundRect(offset, offset, getWidth() - 2 * offset, getHeight() - 2 * offset, cornerArc, cornerArc);

            // Partículas
            AlphaComposite particleAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);
            g2d.setComposite(particleAlpha);
            for (Particle p : particles) {
                g2d.fillOval(p.getX(), p.getY(), p.getSize(), p.getSize());
            }

            g2d.dispose();
        }

        private class Particle {
            int x, y, size;
            float speedX, speedY;

            public Particle(int x, int y, int size) {
                this.x = x;
                this.y = y;
                this.size = size;
                this.speedX = (rand.nextFloat() * 0.8f - 0.4f);
                this.speedY = (rand.nextFloat() * 0.8f - 0.4f);
            }

            public void update(int panelWidth, int panelHeight) {
                x += speedX;
                y += speedY;
                if (x < 0) { x = 0; speedX *= -1; }
                if (x > panelWidth) { x = panelWidth; speedX *= -1; }
                if (y < 0) { y = 0; speedY *= -1; }
                if (y > panelHeight) { y = panelHeight; speedY *= -1; }
            }

            public int getX() { return x; }
            public int getY() { return y; }
            public int getSize() { return size; }
        }
    }

    // =======================================================================
    // MÉTODO AUXILIAR: Crear botón moderno (sin pintar texto manualmente)
    // =======================================================================
    private JButton crearBotonEstilo(String texto, Color fondoNormal, Color fondoHover,
                                     Color textoColor, boolean relleno) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (relleno) {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                } else {
                    g2.setColor(getBackground());
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                }
                g2.dispose();
                super.paintComponent(g); // Pinta el texto automáticamente
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Sin borde predeterminado
            }
        };

        // Configuración inicial
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(textoColor);
        btn.setBackground(fondoNormal);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(fondoHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(fondoNormal);
            }
        });

        return btn;
    }

    // =======================================================================
    // CONSTRUCTOR: Ventana Principal Moderna (CORREGIDA)
    // =======================================================================
    public VentanaPrincipal() {
        setTitle("Sistema de Turnos - Auto STORE!");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        panelPrincipal = new NeonBackgroundPanel(COLOR_GRIS_CARBON, COLOR_NEON_VERDE);
        panelPrincipal.setLayout(new GridBagLayout());
        add(panelPrincipal);

        // === TARJETA CENTRAL MODERNA ===
        tarjetaCentral = new JPanel() {
            private float logoGlowAlpha = 0.0f;
            private boolean glowingUp = true;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo semi-transparente oscuro
                g2.setColor(new Color(30, 30, 30, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Glow del logo (solo si hay logo visible)
                if (labelLogo != null && labelLogo.getIcon() != null && labelLogo.isShowing()) {
                    int logoX = labelLogo.getX();
                    int logoY = labelLogo.getY();
                    int logoWidth = labelLogo.getWidth();
                    int logoHeight = labelLogo.getHeight();

                    if (glowingUp) {
                        logoGlowAlpha += 0.015f;
                        if (logoGlowAlpha >= 0.3f) {
                            logoGlowAlpha = 0.3f;
                            glowingUp = false;
                        }
                    } else {
                        logoGlowAlpha -= 0.015f;
                        if (logoGlowAlpha <= 0.0f) {
                            logoGlowAlpha = 0.0f;
                            glowingUp = true;
                        }
                    }

                    AlphaComposite logoGlow = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, logoGlowAlpha);
                    g2.setComposite(logoGlow);
                    g2.setColor(COLOR_NEON_VERDE);
                    g2.fillOval(logoX - 10, logoY - 10, logoWidth + 20, logoHeight + 20);
                }

                g2.dispose();
            }
        };
        tarjetaCentral.setLayout(new GridBagLayout());
        tarjetaCentral.setOpaque(false);
        tarjetaCentral.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;

        // --- Logo ---
        labelLogo = new JLabel();
        boolean logoCargado = false;
        try {
            ImageIcon iconLogo = new ImageIcon(getClass().getResource(RUTA_LOGO_NUEVO));
            if (iconLogo != null && iconLogo.getIconWidth() > 0) {
                Image imgLogo = iconLogo.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                labelLogo.setIcon(new ImageIcon(imgLogo));
                logoCargado = true;
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }

        if (!logoCargado) {
            labelLogo.setText("AUTO STORE");
            labelLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
            labelLogo.setForeground(COLOR_NEON_VERDE);
        }
        tarjetaCentral.add(labelLogo, gbc);

        // --- Botón AGENDAR (sólido) ---
        JButton btnAgendar = crearBotonEstilo(
            "AGENDAR NUEVO TURNO",
            COLOR_NEON_VERDE,
            COLOR_NEON_VERDE.brighter(),
            Color.BLACK,
            true
        );
        btnAgendar.setPreferredSize(new Dimension(340, 65));
        btnAgendar.addActionListener(e -> new LoginApp());
        gbc.gridy = 1;
        gbc.insets = new Insets(30, 0, 15, 0);
        tarjetaCentral.add(btnAgendar, gbc);

        // --- Botón VER AGENDA (borde) ---
        JButton btnVerAgenda = crearBotonEstilo(
            "VER AGENDA",
            COLOR_TRANSPARENTE,
            new Color(57, 255, 20, 50),
            COLOR_NEON_VERDE,
            false
        );
        btnVerAgenda.setPreferredSize(new Dimension(340, 65));
        btnVerAgenda.addActionListener(e -> new VerTurnosVentana());
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 0, 25, 0);
        tarjetaCentral.add(btnVerAgenda, gbc);

        // --- Texto de ayuda ---
        JLabel ayudaLabel = new JLabel("<html><u>¿Necesitas ayuda? Contáctanos</u></html>");
        ayudaLabel.setForeground(COLOR_NEON_VERDE);
        ayudaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ayudaLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ayudaLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this,
                    "Soporte técnico:\nsoporte@autostore.com\nTel: +54 11 XXXX-XXXX",
                    "Ayuda",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        tarjetaCentral.add(ayudaLabel, gbc);

        // Añadir tarjeta al panel principal
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 1.0;
        gbcMain.weighty = 1.0;
        gbcMain.anchor = GridBagConstraints.CENTER;
        panelPrincipal.add(tarjetaCentral, gbcMain);

        setVisible(true);

        // Timer de animación (para el glow del logo y las partículas)
        Timer animationTimer = new Timer(50, e -> {
            if (panelPrincipal.getWidth() > 0 && panelPrincipal.getHeight() > 0) {
                panelPrincipal.updateAnimations();
                tarjetaCentral.repaint(); // Repaint solo la tarjeta para el glow
            }
        });
        animationTimer.start();
    }

    // =======================================================================
    // MAIN
    // =======================================================================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new VentanaPrincipal());
    }
}