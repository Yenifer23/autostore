package TURNOS_AUTOSTORE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import TURNOS_AUTOSTORE.LoginApp; //esta parte no se si esta bien 

public class VentanaPrincipal extends JFrame {

    // --- RUTAS DE IMAGENES (¡VERIFICA ESTA RUTA EN TU PC!) ---
    private static final String RUTA_LOGO_NUEVO = "C:\\Users\\yenif\\OneDrive\\Documentos\\2025\\programar\\TURNOS_AUTOSTORE\\Imagenes\\logo2.png";
    private static final String RUTA_ICONO_MAXIMIZAR = "imagenes/icono_maximizar.png";

    // --- COLORES DEFINIDOS ---
    private static final Color COLOR_NEON_VERDE = new Color(57, 255, 20); // Verde neón principal
    private static final Color COLOR_GRIS_CARBON = new Color(26, 26, 26);
    private static final Color COLOR_TEXTO_NORMAL_BOTON = COLOR_GRIS_CARBON;
    private static final Color COLOR_TEXTO_HOVER_BOTON = Color.WHITE;

    // NUEVOS COLORES PARA EL BOTÓN ESTILO CRISTALINO/BRILLANTE
    private static final Color COLOR_VERDE_CLARO_BRILLO = new Color(144, 255, 144); // Parte superior degradado
    private static final Color COLOR_VERDE_OSCURO_BRILLO = new Color(0, 200, 0);    // Parte inferior degradado
    private static final Color COLOR_BORDE_CLARO_BOTON = new Color(200, 255, 200, 180);
    private static final Color COLOR_BORDE_OSCURO_BOTON = new Color(0, 120, 0);

    // Referencias para la animación y el logo
    private NeonBackgroundPanel panelPrincipal;
    private JLabel labelLogo;

    // =======================================================================
    // CLASE INTERNA 1: NeonBackgroundPanel (Fondo con animaciones)
    // =======================================================================
    private class NeonBackgroundPanel extends JPanel {
        private Color backgroundColor;
        private Color neonColor;
        private Random rand = new Random();

        private List<Particle> particles;
        private final int NUM_PARTICLES = 50;

        private float logoGlowAlpha = 0.0f;
        private boolean glowingUp = true;

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
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setColor(backgroundColor);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            AlphaComposite borderAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f);
            g2d.setComposite(borderAlpha);
            g2d.setColor(neonColor);
            g2d.setStroke(new BasicStroke(2));

            int offset = 30;
            int cornerArc = 20;
            g2d.drawRoundRect(offset, offset, getWidth() - 2 * offset, getHeight() - 2 * offset, cornerArc, cornerArc);

            AlphaComposite particleAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);
            g2d.setComposite(particleAlpha);

            for (Particle p : particles) {
                g2d.fillOval(p.getX(), p.getY(), p.getSize(), p.getSize());
            }

            if (labelLogo != null && labelLogo.getIcon() != null && labelLogo.isShowing()) {
                int logoX = labelLogo.getX();
                int logoY = labelLogo.getY();
                int logoWidth = labelLogo.getWidth();
                int logoHeight = labelLogo.getHeight();

                AlphaComposite logoGlow = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, logoGlowAlpha);
                g2d.setComposite(logoGlow);
                g2d.setColor(neonColor);

                g2d.fillOval(logoX - 10, logoY - 10, logoWidth + 20, logoHeight + 20);
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
    // CLASE INTERNA 2: BotonWrapperPanel (Caja del botón estilo brillante/3D)
    // =======================================================================
    private class BotonWrapperPanel extends JPanel {
        private boolean isHovering = false;

        public BotonWrapperPanel() {
            super(new GridBagLayout());
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = 30;

            LinearGradientPaint lgp = new LinearGradientPaint(
                    0, 0, 0, h,
                    new float[]{0.0f, 0.5f, 1.0f},
                    new Color[]{COLOR_VERDE_CLARO_BRILLO, COLOR_NEON_VERDE, COLOR_VERDE_OSCURO_BRILLO}
            );

            if (isHovering) {
                lgp = new LinearGradientPaint(
                        0, 0, 0, h,
                        new float[]{0.0f, 0.5f, 1.0f},
                        new Color[]{COLOR_VERDE_CLARO_BRILLO.brighter(), COLOR_NEON_VERDE.brighter(), COLOR_VERDE_OSCURO_BRILLO.brighter()}
                );
            }

            g2d.setPaint(lgp);
            g2d.fillRoundRect(0, 0, w, h, arc, arc);

            // 💡 EFECTO GLOSSY (reflejo en la parte superior)
            GradientPaint gloss = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 150),
                    0, h / 2, new Color(255, 255, 255, 0)
            );
            g2d.setPaint(gloss);
            g2d.fillRoundRect(5, 5, w - 10, h / 2, arc, arc);

            g2d.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = 30;

            g2d.setColor(COLOR_BORDE_OSCURO_BOTON);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);

            g2d.setColor(COLOR_BORDE_CLARO_BOTON);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(arc, 2, w - arc - 1, 2);
            g2d.drawArc(1, 1, arc * 2, arc * 2, 90, 90);
            g2d.drawArc(w - (arc * 2) - 1, 1, arc * 2, arc * 2, 0, 90);

            g2d.dispose();
        }

        public void setHovering(boolean h) {
            isHovering = h;
            repaint();
        }
    }

    // =======================================================================
    // VENTANA PRINCIPAL
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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- Logo ---
        labelLogo = new JLabel();
        boolean logoCargado = false;

        if (new File(RUTA_LOGO_NUEVO).exists()) {
            try {
                ImageIcon iconLogo = new ImageIcon(RUTA_LOGO_NUEVO);
                Image imgLogo = iconLogo.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                labelLogo.setIcon(new ImageIcon(imgLogo));
                logoCargado = true;
            } catch (Exception e) {
                System.err.println("Error al procesar la imagen del logo: " + e.getMessage());
            }
        }

        if (!logoCargado) {
            System.err.println("Advertencia: El logo no se encontró. Usando texto temporal.");
            labelLogo.setText("AUTO STORE LOGO");
            labelLogo.setFont(new Font("Arial", Font.BOLD, 40));
            labelLogo.setForeground(COLOR_NEON_VERDE);
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.5;
        gbc.anchor = GridBagConstraints.SOUTH;
        panelPrincipal.add(labelLogo, gbc);

        gbc.weighty = 0;

        // --- Botón ---
        JButton btnAgendarNuevoTurno = new JButton("Agendar Nuevo Turno");
        btnAgendarNuevoTurno.setFont(new Font("Arial", Font.BOLD, 22));
        btnAgendarNuevoTurno.setForeground(COLOR_TEXTO_NORMAL_BOTON);
        btnAgendarNuevoTurno.setFocusPainted(false);
        btnAgendarNuevoTurno.setOpaque(false);
        btnAgendarNuevoTurno.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));

        //MODIFIQUE ESTA PARTE PARA QUE DIRECTAMENTE ABRA LA PARTE DE LOGIN, LO DEJO ACÁ POR LAS DUDAS
        //btnAgendarNuevoTurno.addActionListener(e ->
        //        JOptionPane.showMessageDialog(VentanaPrincipal.this, "Has hecho clic en AGENDAR NUEVO TURNO")
        //);

        btnAgendarNuevoTurno.addActionListener(e -> {
        new LoginApp(); // abre la ventana de login
        });

        BotonWrapperPanel panelBotonWrapper = new BotonWrapperPanel();
        panelBotonWrapper.add(btnAgendarNuevoTurno);

        btnAgendarNuevoTurno.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnAgendarNuevoTurno.setForeground(COLOR_TEXTO_HOVER_BOTON);
                panelBotonWrapper.setHovering(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnAgendarNuevoTurno.setForeground(COLOR_TEXTO_NORMAL_BOTON);
                panelBotonWrapper.setHovering(false);
                panelBotonWrapper.repaint();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.5;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(50, 10, 10, 10);
        panelPrincipal.add(panelBotonWrapper, gbc);

        setVisible(true);

        Timer animationTimer = new Timer(50, e -> {
            if (panelPrincipal.getWidth() > 0 && panelPrincipal.getHeight() > 0) {
                panelPrincipal.updateAnimations();
                panelPrincipal.repaint();
            }
        });
        animationTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal());
    }
}
