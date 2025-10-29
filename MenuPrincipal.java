import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.EmptyBorder;

public class MenuPrincipal extends JFrame {

    // --- CONSTANTES DE COLOR MEJORADAS ---
    private static final Color COLOR_AZUL_OSCURO_FONDO = new Color(0x0A3D6D); // Azul más profundo y moderno
    private static final Color COLOR_FONDO_BEIGE = new Color(0xF5F0E6);       // Beige cálido y claro
    private static final Color COLOR_TEXTO_OSCURO = new Color(30, 30, 30);
    private static final Color COLOR_FONDO_CARD_TRANSPARENTE = new Color(255, 255, 255, 220);
    private static final Color COLOR_BORDE_CARD = new Color(200, 200, 200, 150);
    private static final Color COLOR_BUTTON_HOVER = new Color(0x1A5276); // Hover más oscuro

    // Dimensiones y radio
    private static final Dimension BUTTON_SIZE = new Dimension(300, 65);
    private static final int CORNER_RADIUS_BUTTON = 30;
    private static final int CORNER_RADIUS_CARD = 25;

    // --- RUTA DE IMAGEN ---
    private static final String RUTA_CAMIONETA_FONDO = "C:\\Users\\yenif\\OneDrive\\Documentos\\2025\\programar\\autostore\\TURNOS_AUTOSTORE\\src\\main\\resources\\Imagenes\\camioneta2.jpeg";

    private ImageIcon camionetaFondoIcon;

    // ==========================================================
    // PANEL IZQUIERDO CON IMAGEN DE FONDO
    // ==========================================================
    private class PanelImagenFondo extends JPanel {

        public PanelImagenFondo() {
            try {
                File imgFile = new File(RUTA_CAMIONETA_FONDO);
                if (imgFile.exists()) {
                    BufferedImage img = ImageIO.read(imgFile);
                    camionetaFondoIcon = new ImageIcon(img);
                } else {
                    System.err.println("⚠️ Imagen de camioneta no encontrada: " + RUTA_CAMIONETA_FONDO);
                    camionetaFondoIcon = crearFondoPorDefecto();
                }
            } catch (IOException e) {
                System.err.println("❌ Error al cargar imagen de camioneta: " + e.getMessage());
                camionetaFondoIcon = crearFondoPorDefecto();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (camionetaFondoIcon != null) {
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                Image img = camionetaFondoIcon.getImage();

                double imgWidth = img.getWidth(null);
                double imgHeight = img.getHeight(null);
                double aspectRatio = (imgHeight > 0) ? (imgWidth / imgHeight) : 1.0;

                int scaledWidth = panelWidth;
                int scaledHeight = (int) (scaledWidth / aspectRatio);

                if (scaledHeight < panelHeight) {
                    scaledHeight = panelHeight;
                    scaledWidth = (int) (scaledHeight * aspectRatio);
                }

                int x = (panelWidth - scaledWidth) / 2;
                int y = (panelHeight - scaledHeight) / 2;

                g.drawImage(img, x, y, scaledWidth, scaledHeight, this);
            }
        }

        private ImageIcon crearFondoPorDefecto() {
            BufferedImage blank = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = blank.createGraphics();
            g2d.setColor(new Color(190, 190, 190));
            g2d.fillRect(0, 0, 1, 1);
            g2d.dispose();
            return new ImageIcon(blank);
        }
    }

    // ==========================================================
    // PANEL TARJETERO CON SOMBRA Y BORDE REDONDEADO
    // ==========================================================
    private class PanelTarjetero extends JPanel {

        public PanelTarjetero() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(40, 50, 40, 50));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int width = getWidth();
            int height = getHeight();

            // Sombra suave
            g2.setColor(new Color(0, 0, 0, 20));
            for (int i = 1; i <= 5; i++) {
                g2.fillRoundRect(i, i, width - 2 * i, height - 2 * i, CORNER_RADIUS_CARD - i, CORNER_RADIUS_CARD - i);
            }

            // Fondo semitransparente
            g2.setColor(COLOR_FONDO_CARD_TRANSPARENTE);
            g2.fillRoundRect(0, 0, width, height, CORNER_RADIUS_CARD, CORNER_RADIUS_CARD);

            // Borde sutil
            g2.setColor(COLOR_BORDE_CARD);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, width - 3, height - 3, CORNER_RADIUS_CARD - 2, CORNER_RADIUS_CARD - 2);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==========================================================
    // CONSTRUCTOR PRINCIPAL
    // ==========================================================
    public MenuPrincipal() {
        setTitle("AutoStore - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new GridBagLayout());
        GridBagConstraints gbcMain = new GridBagConstraints();

        // Panel Izquierdo: Imagen de fondo
        PanelImagenFondo panelIzquierdo = new PanelImagenFondo();
        panelIzquierdo.setLayout(null);

        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 0.5;
        gbcMain.weighty = 1.0;
        gbcMain.fill = GridBagConstraints.BOTH;
        add(panelIzquierdo, gbcMain);

        // Panel Derecho: Fondo beige
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBackground(COLOR_FONDO_BEIGE);

        // Tarjeta de menú
        PanelTarjetero menuCard = new PanelTarjetero();

        // Título
        JLabel lblTitle = new JLabel("Panel de Control");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 44));
        lblTitle.setForeground(COLOR_AZUL_OSCURO_FONDO);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuCard.add(lblTitle);

        menuCard.add(Box.createVerticalStrut(70));

        // Botón 1: Agendar nuevo turno
        JButton btnAgendar = createStyledButton("Agendar un nuevo turno");
        btnAgendar.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginPrueba());
        });
        menuCard.add(btnAgendar);

        menuCard.add(Box.createVerticalStrut(30));

        // Botón 2: Ver agenda
        JButton btnVerAgenda = createStyledButton("Ver agenda");
        btnVerAgenda.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new VerTurnosVentana());
        });
        menuCard.add(btnVerAgenda);

        // Añadir tarjeta al panel derecho
        panelDerecho.add(menuCard);

        gbcMain.gridx = 1;
        gbcMain.weightx = 0.5;
        add(panelDerecho, gbcMain);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ==========================================================
    // BOTÓN ESTILIZADO CON HOVER Y EFECTOS
    // ==========================================================
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = isHovered ? COLOR_BUTTON_HOVER : COLOR_AZUL_OSCURO_FONDO;
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS_BUTTON, CORNER_RADIUS_BUTTON);

                // Brillo sutil en hover
                if (isHovered) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 40),
                        0, getHeight() / 2, new Color(255, 255, 255, 0)
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS_BUTTON, CORNER_RADIUS_BUTTON);
                }

                // Efecto de pulsado
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS_BUTTON, CORNER_RADIUS_BUTTON);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(BUTTON_SIZE);
        button.setMinimumSize(BUTTON_SIZE);
        button.setMaximumSize(BUTTON_SIZE);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hover interactivo
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(Cursor.getDefaultCursor());
                button.repaint();
            }
        });

        // Usamos un truco para detectar hover sin sobreescribir completamente el modelo
        button.getModel().addChangeListener(e -> {
            if (button.getModel().isRollover()) {
                button.repaint();
            }
        });

        // Sobreescribimos el rollover para activar el efecto
        button.putClientProperty("JButton.buttonType", "roundRect");

        return button;
    }

    // ==========================================================
    // MAIN
    // ==========================================================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new MenuPrincipal());
    }
}