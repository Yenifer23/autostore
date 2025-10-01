import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.plaf.basic.BasicButtonUI;

public class ServiciosVentana extends JFrame {

    // --- COLORES DEFINIDOS ---
    private static final Color COLOR_NEON_VERDE = new Color(57, 255, 20); 
    private static final Color COLOR_NEON_VERDE_CLARO = new Color(102, 255, 127); 
    private static final Color COLOR_GRIS_CARBON = new Color(26, 26, 26);
    private static final Color COLOR_GRIS_OSCURO_FONDO = new Color(35, 35, 35); 
    
    private final int ANCHO_VENTANA = 600;
    private final int ALTO_VENTANA = 650; 
    
    // Lista para controlar la selección única de tarjetas
    private List<ServiceCardPanel> allServiceCards = new ArrayList<>();

    // =======================================================================
    // CLASE INTERNA 1: NeonBackgroundPanel (Fondo animado y rejilla sutil)
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
            // Inicializar partículas
            for (int i = 0; i < NUM_PARTICLES; i++) {
                particles.add(new Particle(rand.nextInt(ANCHO_VENTANA), rand.nextInt(ALTO_VENTANA), rand.nextInt(2) + 1));
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
            
            // --- DIBUJAR REJILLA SUTIL ---
            int spacing = 50; 
            Color gridColor = new Color(60, 60, 60, 50); 
            g2d.setColor(gridColor);

            // Líneas horizontales
            for (int y = 0; y < getHeight(); y += spacing) {
                g2d.drawLine(0, y, getWidth(), y);
            }
            // Líneas verticales
            for (int x = 0; x < getWidth(); x += spacing) {
                g2d.drawLine(x, 0, x, getHeight());
            }
            
            // --- DIBUJAR PARTÍCULAS ---
            AlphaComposite particleAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f); 
            g2d.setComposite(particleAlpha);
            g2d.setColor(neonColor);
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
                this.speedX = (rand.nextFloat() * 0.3f - 0.15f); 
                this.speedY = (rand.nextFloat() * 0.3f - 0.15f);
            }

            public void update(int panelWidth, int panelHeight) {
                x += speedX;
                y += speedY;

                if (x < 0 || x > panelWidth) { speedX *= -1; }
                if (y < 0 || y > panelHeight) { speedY *= -1; }
            }

            public int getX() { return x; }
            public int getY() { return y; }
            public int getSize() { return size; }
        }
    }
    
    // =======================================================================
    // CLASE INTERNA 2: ServiceCardPanel (Tarjetas de servicios)
    // =======================================================================
    private class ServiceCardPanel extends JPanel {
        private String serviceName;
        private String description;
        private Icon icon;
        private boolean isSelected = false;

        public ServiceCardPanel(String serviceName, String description, Icon icon) {
            this.serviceName = serviceName;
            this.description = description;
            this.icon = icon; 
            
            setLayout(new BorderLayout(15, 0)); 
            // Quitamos el borde inicial para que el paintComponent lo maneje
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); 
            setBackground(COLOR_GRIS_OSCURO_FONDO);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Icono
            JLabel iconLabel = new JLabel(icon);
            add(iconLabel, BorderLayout.WEST);

            // Texto (Nombre y Descripción)
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

            // Checkmark (inicialmente oculto)
            JLabel checkLabel = new JLabel("✅");
            checkLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
            checkLabel.setVisible(false);
            checkLabel.setName("CHECK"); 
            add(checkLabel, BorderLayout.EAST);

            // Listener para la interacción
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleSelection();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isSelected) {
                        setBackground(COLOR_GRIS_CARBON.brighter()); 
                    }
                    repaint(); // Para refrescar el borde al pasar el ratón
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isSelected) {
                        setBackground(COLOR_GRIS_OSCURO_FONDO);
                    }
                    repaint(); // Para refrescar el borde al salir del ratón
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
            
            // Ya no gestionamos el borde aquí, se gestiona en paintComponent
            if (!isSelected) {
                setBackground(COLOR_GRIS_OSCURO_FONDO);
            }
            repaint(); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // --- 1. FONDO REDONDEADO DE LA TARJETA ---
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); 
            
            // --- 2. RECTÁNGULO DECORATIVO LATERAL (Nuevo) ---
            int rectWidth = 10;
            int rectHeight = getHeight();
            
            if (isSelected) {
                // Rectángulo neón completo al seleccionar
                g2.setColor(COLOR_NEON_VERDE);
                g2.fillRect(0, 0, rectWidth, rectHeight);
                
                // Borde exterior neón (para resaltar toda la tarjeta)
                g2.setColor(COLOR_NEON_VERDE);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                
            } else if (getBackground().equals(COLOR_GRIS_CARBON.brighter())) {
                // Rectángulo tenue al hacer hover
                g2.setColor(COLOR_GRIS_CARBON.brighter().brighter()); 
                g2.fillRect(0, 0, rectWidth, rectHeight);
            } else {
                // Rectángulo muy tenue cuando no está seleccionado ni en hover
                g2.setColor(new Color(50, 50, 50)); 
                g2.fillRect(0, 0, rectWidth, rectHeight);
            }

            g2.dispose();
            // Esto asegura que los componentes (iconos, textos, checkmark) se dibujen encima del fondo
            super.paintComponent(g); 
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
        // Aumentamos el margen superior (top inset) para bajar el contenido y centrarlo verticalmente
        contentPanel.setBorder(BorderFactory.createEmptyBorder(80, 30, 30, 30)); 
        
        // --- TÍTULO Y SUBRAYADO ---
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

        // --- LISTA DE SERVICIOS ---
        
        // 1. Audio (Tamaño 60)
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

        // 2. Polarizado (Tamaño 80)
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

        // --- BOTÓN CONFIRMAR ---
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
        
        // Timer de animación (solo para las partículas)
        Timer animationTimer = new Timer(50, e -> {
            if (panelPrincipal.getWidth() > 0 && panelPrincipal.getHeight() > 0) {
                panelPrincipal.updateAnimations();
                panelPrincipal.repaint();
            }
        });
        animationTimer.start();
    }
    
    // Método auxiliar para crear un botón redondeado y moderno
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
                
                // 1. Dibujar la "sombra" neón 
                g2.setColor(COLOR_NEON_VERDE_CLARO);
                g2.fillRoundRect(0, 0, width, height, arc, arc);

                // 2. Dibujar el fondo principal del botón
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
    
    // Método auxiliar para cargar íconos
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
        
        // Nota: Asegúrate de tener 'import java.awt.image.BufferedImage;' en la parte superior
        BufferedImage emptyImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
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