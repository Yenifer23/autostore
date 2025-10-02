import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;
import com.toedter.calendar.JDateChooser;

public class Turno extends JFrame {

    // --- COLORES DEFINIDOS (copiados de ServiciosVentana.java) ---
    private static final Color COLOR_NEON_VERDE = new Color(57, 255, 20);
    private static final Color COLOR_NEON_VERDE_CLARO = new Color(102, 255, 127);
    private static final Color COLOR_NEON_VERDE_OSCURO = new Color(34, 139, 34);
    private static final Color COLOR_GRIS_CARBON = new Color(26, 26, 26);
    private static final Color COLOR_GRIS_OSCURO_FONDO = new Color(35, 35, 35);

    private final int ANCHO_VENTANA = 600;
    private final int ALTO_VENTANA = 650;

    // Componentes de entrada
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtMatricula;
    private JDateChooser dateChooser;
    private JComboBox<String> horaComboBox;
    private JComboBox<String> servicioComboBox;

    // Panel principal con fondo animado (igual que en ServiciosVentana.java)
    private NeonBackgroundPanel panelPrincipal;

    // =======================================================================
    // CLASE INTERNA: NeonBackgroundPanel (Fondo animado + Borde Neón Global Titilante SUAVE)
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

    public Turno() {
        setTitle("Agenda el Nuevo Turno - AUTO STORE");
        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Inicializar panel principal con fondo animado (igual que ServiciosVentana)
        panelPrincipal = new NeonBackgroundPanel(COLOR_GRIS_CARBON, COLOR_NEON_VERDE);
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));

        // Contenedor de contenido
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(80, 30, 30, 30));

        // Título
        JLabel titleLabel = new JLabel("Agenda el Nuevo Turno");
        titleLabel.setFont(new Font("Bell MT", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_NEON_VERDE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(300, 3));
        separator.setForeground(COLOR_NEON_VERDE_CLARO);
        separator.setBackground(COLOR_GRIS_CARBON);
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel);
        titleContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        titleContainer.add(separator);
        contentPanel.add(titleContainer);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(0, 1, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Campo: Nombre Completo
        txtNombre = createTextField("Nombre Completo del Cliente");
        formPanel.add(txtNombre);

        // Campo: Número de Teléfono
        txtTelefono = createTextField("Número de Teléfono");
        formPanel.add(txtTelefono);

        // Campo: Matrícula del Auto
        txtMatricula = createTextField("Matrícula del Auto");
        formPanel.add(txtMatricula);

        // Campo: Tipo de Servicio
        String[] servicios = {"Audio", "Polarizado", "Ambos"};
        servicioComboBox = new JComboBox<>(servicios);
        servicioComboBox.setPreferredSize(new Dimension(200, 30));
        servicioComboBox.setFont(new Font("Arial", Font.BOLD, 14));
        servicioComboBox.setBackground(COLOR_GRIS_CARBON);
        servicioComboBox.setForeground(Color.WHITE);
        servicioComboBox.setBorder(BorderFactory.createLineBorder(COLOR_NEON_VERDE, 1));
        formPanel.add(servicioComboBox);

        // Fecha y Hora
        JPanel dateHourPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        dateHourPanel.setOpaque(false);

        // Selector de fecha
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(new Dimension(150, 30));
        dateChooser.getCalendarButton().setPreferredSize(new Dimension(25, 25));
        dateChooser.getCalendarButton().setBackground(COLOR_NEON_VERDE);
        dateChooser.getCalendarButton().setForeground(COLOR_GRIS_CARBON);
        dateChooser.getCalendarButton().setFont(new Font("Arial", Font.BOLD, 12));
        dateChooser.getDateEditor().getUiComponent().setFont(new Font("Arial", Font.PLAIN, 14));
        dateChooser.setDate(Calendar.getInstance().getTime());

        // Selector de hora (de 30 en 30 minutos, desde 9:30 hasta 22:00)
        List<String> horasDisponibles = new ArrayList<>();
        for (int hour = 9; hour <= 21; hour++) {
            horasDisponibles.add(String.format("%02d:30", hour));
            if (hour < 21) {
                horasDisponibles.add(String.format("%02d:00", hour + 1));
            }
        }
        // Añadir la última hora: 22:00
        horasDisponibles.add("22:00");
        
        horaComboBox = new JComboBox<>(horasDisponibles.toArray(new String[0]));
        horaComboBox.setPreferredSize(new Dimension(100, 30));
        horaComboBox.setFont(new Font("Arial", Font.BOLD, 14));
        horaComboBox.setBackground(COLOR_GRIS_CARBON);
        horaComboBox.setForeground(Color.WHITE);
        horaComboBox.setBorder(BorderFactory.createLineBorder(COLOR_NEON_VERDE, 1));

        dateHourPanel.add(dateChooser);
        dateHourPanel.add(horaComboBox);
        formPanel.add(dateHourPanel);

        contentPanel.add(formPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Botón CONFIRMAR (con el estilo de ServiciosVentana.java)
        JButton btnConfirmar = createRoundedNeonButton("CONFIRMAR", e -> confirmarTurno());
        btnConfirmar.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(btnConfirmar);

        panelPrincipal.add(contentPanel);
        add(panelPrincipal);

        setVisible(true);

        // Timer de animación para las partículas (igual que en ServiciosVentana)
        Timer animationTimer = new Timer(30, e -> {
            if (panelPrincipal.getWidth() > 0 && panelPrincipal.getHeight() > 0) {
                panelPrincipal.updateAnimations();
                panelPrincipal.repaint();
            }
        });
        animationTimer.start();

        // Listener para redimensionar partículas
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (panelPrincipal.getWidth() > 0 && panelPrincipal.getHeight() > 0) {
                    panelPrincipal.initializeParticles(panelPrincipal.getWidth(), panelPrincipal.getHeight());
                }
            }
        });
    }

    // Método auxiliar: crear campo de texto con estilo neón
    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.BOLD, 16));
        field.setForeground(Color.WHITE);
        field.setBackground(COLOR_GRIS_CARBON);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(COLOR_NEON_VERDE, 2, true));
        field.setText(placeholder);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    // Método auxiliar: crear botón redondeado neón (igual que en ServiciosVentana.java)
    private JButton createRoundedNeonButton(String text, ActionListener action) {
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

    // Método para confirmar turno
    private void confirmarTurno() {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String matricula = txtMatricula.getText().trim();
        java.util.Date selectedDate = dateChooser.getDate();
        String horaSeleccionada = (String) horaComboBox.getSelectedItem();
        String tipoServicio = (String) servicioComboBox.getSelectedItem();

        // Validar campos
        if (nombre.isEmpty() || nombre.equals("Nombre Completo del Cliente")) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa tu nombre completo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (telefono.isEmpty() || telefono.equals("Número de Teléfono")) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa tu número de teléfono.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (matricula.isEmpty() || matricula.equals("Matrícula del Auto")) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa la matrícula del auto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una fecha.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Formatear fecha
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaFormateada = sdf.format(selectedDate);

        // Mostrar resumen
        String resumen = String.format(
            """
            🔍 Resumen de Turno:
            -------------------
            Cliente: %s
            Teléfono: %s
            Matrícula: %s
            Servicio: %s
            Fecha: %s
            Hora: %s
            -------------------
            ¿Confirmar este turno?
            """,
            nombre, telefono, matricula, tipoServicio, fechaFormateada, horaSeleccionada
        );

        int opcion = JOptionPane.showConfirmDialog(this, resumen, "Confirmar Turno", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            guardarTurnoEnBD(nombre, telefono, matricula, tipoServicio, fechaFormateada, horaSeleccionada);
        }
    }

    // Método para guardar el turno en la base de datos
    private void guardarTurnoEnBD(String nombre, String telefono, String matricula, String tipoServicio, String fecha, String hora) {
        String url = "jdbc:mysql://localhost:3306/autostore";
        String usuario = "root";
        String contraseña = "";

        try {
            // 👇 CARGAR EL DRIVER EXPLÍCITAMENTE (solución al error "No suitable driver")
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
                String sql = "INSERT INTO turnos (nombre, telefono, matricula, tipo_servicio, fecha, hora) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nombre);
                stmt.setString(2, telefono);
                stmt.setString(3, matricula);
                stmt.setString(4, tipoServicio);
                stmt.setString(5, fecha);
                stmt.setString(6, hora);
                int filasAfectadas = stmt.executeUpdate();

                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "✅ Turno agendado correctamente!\nTe contactaremos para confirmar.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ No se pudo guardar el turno. Inténtalo de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Driver de MySQL no encontrado:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error de conexión a la base de datos:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Turno());
    }
}