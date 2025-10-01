import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.Timer; 
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.border.Border;

public class ServiciosVentana extends JFrame {

    // --- COLORES DEFINIDOS ---
    private static final Color COLOR_NEON_VERDE = new Color(57, 255, 20); 
    private static final Color COLOR_NEON_VERDE_CLARO = new Color(102, 255, 127); 
    private static final Color COLOR_NEON_VERDE_OSCURO = new Color(34, 139, 34); 
    
    private static final Color COLOR_GRIS_CARBON = new Color(26, 26, 26);
    private static final Color COLOR_GRIS_OSCURO_FONDO = new Color(35, 35, 35); 
    
    private final int ANCHO_VENTANA = 600;
    private final int ALTO_VENTANA = 650; 
    
    private List<ServiceCardPanel> allServiceCards = new ArrayList<>();

    // =======================================================================
    // CLASE INTERNA 1: NeonBackgroundPanel (Fondo animado + Borde Neón Global Titilante SUAVE)
    // =======================================================================
    private class NeonBackgroundPanel extends JPanel {
        private Color backgroundColor;
        private Color neonColor;
        private Random rand = new Random();
        private List<Particle> particles;
        private final int NUM_PARTICLES = 100;
        
        // --- Variables para el borde titilante global ---
        private Timer globalBlinkTimer;
        private boolean showGlobalNeonBorder = false;

        public NeonBackgroundPanel(Color bgColor, Color neonC) {
            this.backgroundColor = bgColor;
            this.neonColor = neonC;
            setOpaque(true);
            particles = new ArrayList<>();
            initializeParticles(ANCHO_VENTANA, ALTO_VENTANA);
            
            // Parpadeo más lento y suave: 800ms
            globalBlinkTimer = new Timer(800, e -> {
                showGlobalNeonBorder = !showGlobalNeonBorder;
                repaint();
            });
            globalBlinkTimer.start(); 
        }
        
        private void initializeParticles(int width, int height) {
            particles.clear();
            if (width > 0 && height > 0) {
                for (int i = 0; i < NUM_PARTICLES; i++) {
                    particles.add(new Particle(rand.nextInt(width), rand.nextInt(height), rand.nextInt(3) + 1));
                }
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
            
            // --- DIBUJAR REJILLA SUTIL Y PARTÍCULAS ---
            int spacing = 50; 
            Color gridColor = new Color(60, 60, 60, 20); 
            g2d.setColor(gridColor);

            for (int y = 0; y < getHeight(); y += spacing) {
                g2d.drawLine(0, y, getWidth(), y);
            }
            for (int x = 0; x < getWidth(); x += spacing) {
                g2d.drawLine(x, 0, x, getHeight());
            }
            
            AlphaComposite particleAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f); 
            g2d.setComposite(particleAlpha);
            
            for (Particle p : particles) {
                g2d.setColor(new Color(neonColor.getRed(), neonColor.getGreen(), neonColor.getBlue(), 100)); 
                g2d.fillOval(p.getX() - 1, p.getY() - 1, p.getSize() + 2, p.getSize() + 2);
                
                g2d.setColor(neonColor); 
                g2d.fillOval(p.getX(), p.getY(), p.getSize(), p.getSize());
            }

            // --- BORDE NEÓN DE LA VENTANA (Titilante y Sutil) ---
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); 
            int width = getWidth();
            int height = getHeight();

            // 1. Dibujar el Glow Fijo (muy sutil)
            g2d.setColor(new Color(COLOR_NEON_VERDE_OSCURO.getRed(), COLOR_NEON_VERDE_OSCURO.getGreen(), COLOR_NEON_VERDE_OSCURO.getBlue(), 50));
            g2d.setStroke(new BasicStroke(4)); 
            g2d.drawRect(0, 0, width - 1, height - 1);
            
            // 2. Dibujar el Borde Titilante (fino y usando el color oscuro)
            if (showGlobalNeonBorder) {
                g2d.setColor(COLOR_NEON_VERDE_OSCURO); 
                g2d.setStroke(new BasicStroke(1)); 
                g2d.drawRect(2, 2, width - 5, height - 5);
            }

            g2d.dispose();
        }

        private class Particle {
            int x, y, size;
            float speedY;
            private final Random rand = new Random();

            public Particle(int x, int y, int size) {
                this.x = x;
                this.y = y;
                this.size = size;
                this.speedY = rand.nextFloat() * 1.0f + 0.5f; 
            }

            public void update(int panelWidth, int panelHeight) {
                y += speedY;

                if (y > panelHeight) { 
                    y = -size; 
                    x = rand.nextInt(panelWidth); 
                    this.speedY = rand.nextFloat() * 1.0f + 0.5f; 
                }
            }

            public int getX() { return x; }
            public int getY() { return y; }
            public int getSize() { return size; }
        }
    }
    
    // =======================================================================
    // CLASE INTERNA 2: ServiceCardPanel (Tarjetas con borde neón SÓLIDO AL SELECCIONAR/HOVER)
    // =======================================================================
    private class ServiceCardPanel extends JPanel {
        private String serviceName;
        private String description;
        private Icon icon;
        public boolean isSelected = false;
        // SE ELIMINA: private Timer blinkTimer;
        // SE ELIMINA: private boolean showBlinkBorder = false;
        private boolean isHovered = false; 

        public ServiceCardPanel(String serviceName, String description, Icon icon) {
            this.serviceName = serviceName;
            this.description = description;
            this.icon = icon; 
            
            setLayout(new BorderLayout(15, 0)); 
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); 
            setBackground(COLOR_GRIS_OSCURO_FONDO);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Componentes internos (Icono, Texto, Checkmark)
            JLabel iconLabel = new JLabel(icon);
            add(iconLabel, BorderLayout.WEST);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            
            JLabel nameLabel = new JLabel(serviceName);
            nameLabel.setFont(new Font("Bell MT", Font.BOLD, 18));
            nameLabel.setForeground(Color.WHITE); 
            
            JLabel descLabel = new JLabel(description);
            descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            descLabel.setForeground(Color.LIGHT_GRAY); 

            textPanel.add(nameLabel);
            textPanel.add(Box.createVerticalStrut(3)); 
            textPanel.add(descLabel);

            add(textPanel, BorderLayout.CENTER);

            JLabel checkLabel = new JLabel("✅");
            checkLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
            checkLabel.setVisible(false);
            checkLabel.setName("CHECK"); 
            add(checkLabel, BorderLayout.EAST);
            
            // SE ELIMINA la inicialización del blinkTimer.

            // Listener para la interacción
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleSelection();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    // SE ELIMINA: if (!blinkTimer.isRunning()) { blinkTimer.start(); }
                    if (!isSelected) {
                        setBackground(COLOR_GRIS_CARBON.brighter()); 
                    }
                    repaint(); // Para que el borde aparezca inmediatamente
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    // SE ELIMINA: blinkTimer.stop() logic
                    if (!isSelected) {
                        setBackground(COLOR_GRIS_OSCURO_FONDO);
                    }
                    repaint(); // Para que el borde desaparezca inmediatamente
                }
            });
        }
        
        public String getServiceName() {
            return serviceName;
        }

        public void toggleSelection() {
            if (isSelected) {
                setSelected(false);
            } else {
                selectOnlyThis(this);
            }
        }
        
        public void setSelected(boolean select) {
            isSelected = select;
            
            Component checkComponent = null;
            for (Component c : getComponents()) {
                if (c.getName() != null && c.getName().equals("CHECK")) {
                    checkComponent = c;
                    break;
                }
            }
            if (checkComponent != null) {
                checkComponent.setVisible(isSelected);
            }
            
            if (isSelected) {
                // SE ELIMINA: if (!blinkTimer.isRunning()) { blinkTimer.start(); }
                setBackground(COLOR_GRIS_OSCURO_FONDO); 
            } else {
                if (!isHovered) { 
                    setBackground(COLOR_GRIS_OSCURO_FONDO);
                }
            }
            repaint(); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            int arc = 20;

            // 1. FONDO REDONDEADO
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, width, height, arc, arc); 
            
            // 2. RECTÁNGULO DECORATIVO LATERAL
            int rectWidth = 10;
            g2.setColor(isSelected ? COLOR_NEON_VERDE : new Color(50, 50, 50)); 
            g2.fillRect(0, 0, rectWidth, height);

            g2.dispose();
            super.paintComponent(g); 

            // =================================================================
            // DIBUJAMOS EL BORDE SÓLIDO AL FINAL
            // =================================================================
            g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // BORDE NEÓN SÓLIDO / GLOW (Tarjetas)
            if (isSelected || isHovered) {
                Color neonGlow = isSelected ? COLOR_NEON_VERDE : COLOR_NEON_VERDE_CLARO.darker();
                
                // Borde exterior fijo (Glow sutil)
                g2.setStroke(new BasicStroke(4));
                g2.setColor(new Color(neonGlow.getRed(), neonGlow.getGreen(), neonGlow.getBlue(), 70)); 
                g2.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);
                
                // Borde INTERIOR SÓLIDO (fino)
                g2.setStroke(new BasicStroke(2));
                g2.setColor(neonGlow); 
                g2.drawRoundRect(2, 2, width - 5, height - 5, arc - 2, arc - 2); 
            }
            
            g2.dispose(); 
        }
    }

    // =======================================================================
    // LÓGICA DE SELECCIÓN ÚNICA
    // =======================================================================
    private void selectOnlyThis(ServiceCardPanel cardToSelect) {
        for (ServiceCardPanel card : allServiceCards) {
            if (card != cardToSelect) {
                card.setSelected(false);
            }
        }
        cardToSelect.setSelected(true);
    }


    // =======================================================================
    // CONSTRUCTOR PRINCIPAL
    // =======================================================================
    public ServiciosVentana() {
        setTitle("Selección de Servicios - AUTO STORE!");
        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        NeonBackgroundPanel panelPrincipal = new NeonBackgroundPanel(COLOR_GRIS_CARBON, COLOR_NEON_VERDE);
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS)); 
        
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Borde del contenido eliminado: Solo se aplica el padding principal.
        contentPanel.setBorder(
            BorderFactory.createEmptyBorder(80, 30, 30, 30)
        );

        // --- Contenido (Título, Separador, Tarjetas, Botón) ---
        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);
        titleContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Selecciona Tus Servicios"); 
        titleLabel.setFont(new Font("Bell MT", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_NEON_VERDE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(300, 3)); 
        separator.setForeground(COLOR_NEON_VERDE_CLARO); 
        separator.setBackground(COLOR_GRIS_CARBON);
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleContainer.add(titleLabel);
        titleContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        titleContainer.add(separator);
        
        contentPanel.add(titleContainer);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 40))); 

        Icon iconAudio = loadIcon("/Imagenes/parlantes2025.png", 80); 
        ServiceCardPanel cardAudio = new ServiceCardPanel(
            "Audio y Multimedia", 
            "Instalación y configuración de equipos de sonido, pantallas, GPS, etc.", 
            iconAudio
        );
        cardAudio.setMaximumSize(new Dimension(ANCHO_VENTANA - 60, 110)); 
        allServiceCards.add(cardAudio); 
        contentPanel.add(cardAudio);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); 

        Icon iconPolarizado = loadIcon("/Imagenes/polarizado2025.png", 80); 
        ServiceCardPanel cardPolarizado = new ServiceCardPanel(
            "Polarizado de Cristales", 
            "Láminas de seguridad y control solar. Diferentes tonos disponibles.", 
            iconPolarizado
        );
        cardPolarizado.setMaximumSize(new Dimension(ANCHO_VENTANA - 60, 130)); 
        allServiceCards.add(cardPolarizado); 
        contentPanel.add(cardPolarizado);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 50))); 

        JButton btnConfirmar = createRoundedNeonButton("Confirmar Selección", e -> {
            String selectedService = "Ninguno";
            for (ServiceCardPanel card : allServiceCards) {
                if (card.isSelected) {
                    selectedService = card.getServiceName();
                    break;
                }
            }
            if (selectedService.equals("Ninguno")) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un servicio antes de confirmar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Servicio seleccionado: " + selectedService + ". ¡Continuando al agendamiento!");
            }
        });
        btnConfirmar.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(btnConfirmar);

        panelPrincipal.add(contentPanel);
        add(panelPrincipal);
        
        setVisible(true);
        
        // Timer de animación para las partículas
        Timer animationTimer = new Timer(30, e -> { 
            if (panelPrincipal.getWidth() > 0 && panelPrincipal.getHeight() > 0) {
                panelPrincipal.updateAnimations();
                panelPrincipal.repaint();
            }
        });
        animationTimer.start();
        
        // Listener para redimensionar y redistribuir las partículas
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                 if (panelPrincipal.getWidth() > 0 && panelPrincipal.getHeight() > 0) {
                     panelPrincipal.initializeParticles(panelPrincipal.getWidth(), panelPrincipal.getHeight());
                 }
            }
        });
    }
    
    // Método auxiliar para crear un botón redondeado y moderno (sin cambios)
    private JButton createRoundedNeonButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Bell MT", Font.BOLD, 18));
        button.setForeground(COLOR_GRIS_CARBON);
        button.setBackground(COLOR_NEON_VERDE);
        
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false); 
        button.setPreferredSize(new Dimension(300, 55)); 
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = c.getWidth();
                int height = c.getHeight();
                int arc = 25; 
                
                // 1. Dibujar el borde "glow" más grueso
                g2.setColor(COLOR_NEON_VERDE_CLARO);
                g2.fillRoundRect(0, 0, width, height, arc, arc);

                // 2. Dibujar el fondo principal del botón más pequeño
                g2.setColor(button.getBackground());
                g2.fillRoundRect(2, 2, width - 4, height - 4, arc - 2, arc - 2); 

                g2.dispose();
                super.paint(g, c); 
            }
        });
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COLOR_NEON_VERDE.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(COLOR_NEON_VERDE);
            }
        });
        
        button.addActionListener(action);
        return button;
    }
    
    // Método auxiliar para cargar íconos (sin cambios)
    private ImageIcon loadIcon(String path, int size) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            if (icon.getImage() != null) {
                Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el icono: " + path + ". Usando icono por defecto.");
        }
        
        BufferedImage emptyImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = emptyImage.createGraphics();
        g2.setColor(Color.RED);
        g2.drawString("X", size/2 - 5, size/2 + 5);
        g2.dispose();
        return new ImageIcon(emptyImage);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorar si falla
        }
        SwingUtilities.invokeLater(() -> new ServiciosVentana());
    }
}