import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;
import com.toedter.calendar.JDateChooser;

public class Turno extends JFrame {

    // --- COLORES DEFINIDOS ---
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

    // Panel principal con fondo animado
    private NeonBackgroundPanel panelPrincipal;

    // --- NUEVO: Para manejar los pasos ---
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel panelServicios;
    private JPanel panelDatos;
    private JPanel panelResumen;

    // --- Datos temporales entre pasos ---
    private String servicioSeleccionado = "";
    private String nombreCliente = "";
    private String telefonoCliente = "";
    private String matriculaAuto = "";
    private java.util.Date fechaSeleccionada = null;
    private String horaSeleccionada = "";

    // --- NUEVO: Para saber en qué paso estamos ---
    private String currentStep = "SERVICIOS";

    // --- Botones de navegación ---
    private JButton btnAnterior, btnSiguiente, btnConfirmar;

    // --- Etiquetas del resumen ---
    private JLabel[] resumenLabels;

    // =======================================================================
    // CLASE INTERNA: NeonBackgroundPanel
    // =======================================================================
    private class NeonBackgroundPanel extends JPanel {
        private Color backgroundColor;
        private Color neonColor;
        private Random rand = new Random();
        private List<Particle> particles;
        private final int NUM_PARTICLES = 100;
        
        private Timer globalBlinkTimer;
        private boolean showGlobalNeonBorder = false;

        public NeonBackgroundPanel(Color bgColor, Color neonC) {
            this.backgroundColor = bgColor;
            this.neonColor = neonC;
            setOpaque(true);
            particles = new ArrayList<>();
            initializeParticles(ANCHO_VENTANA, ALTO_VENTANA);
            
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

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); 
            int width = getWidth();
            int height = getHeight();

            g2d.setColor(new Color(COLOR_NEON_VERDE_OSCURO.getRed(), COLOR_NEON_VERDE_OSCURO.getGreen(), COLOR_NEON_VERDE_OSCURO.getBlue(), 50));
            g2d.setStroke(new BasicStroke(4)); 
            g2d.drawRect(0, 0, width - 1, height - 1);
            
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
        setTitle("Registra el Turno");
        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        panelPrincipal = new NeonBackgroundPanel(COLOR_GRIS_CARBON, COLOR_NEON_VERDE);
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(80, 30, 30, 30));

        crearPanelServicios();
        crearPanelDatos();
        crearPanelResumen();

        cardPanel.add(panelServicios, "SERVICIOS");
        cardPanel.add(panelDatos, "DATOS");
        cardPanel.add(panelResumen, "RESUMEN");

        JPanel navPanel = crearBarraNavegacion();
        navPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Registra el Turno");
        titleLabel.setFont(new Font("Impact", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_NEON_VERDE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.add(titleLabel);
        topContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        panelPrincipal.add(topContainer);
        panelPrincipal.add(cardPanel);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        panelPrincipal.add(navPanel);
        add(panelPrincipal);

        setVisible(true);

        Timer animationTimer = new Timer(30, e -> {
            if (panelPrincipal.getWidth() > 0 && panelPrincipal.getHeight() > 0) {
                panelPrincipal.updateAnimations();
                panelPrincipal.repaint();
            }
        });
        animationTimer.start();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (panelPrincipal.getWidth() > 0 && panelPrincipal.getHeight() > 0) {
                    panelPrincipal.initializeParticles(panelPrincipal.getWidth(), panelPrincipal.getHeight());
                }
            }
        });
    }

    // =======================================================================
    // MÉTODOS PARA LOS 3 PASOS
    // =======================================================================

    private void crearPanelServicios() {
        panelServicios = new JPanel();
        panelServicios.setLayout(new BoxLayout(panelServicios, BoxLayout.Y_AXIS));
        panelServicios.setOpaque(false);

        JLabel title = new JLabel("Elige el tipo de Servicio");
        title.setFont(new Font("Impact", Font.BOLD, 24));
        title.setForeground(COLOR_NEON_VERDE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] servicios = {"Servicios", "Audio", "Polarizado", "Ambos"};
        servicioComboBox = new JComboBox<>(servicios);
        servicioComboBox.setSelectedIndex(0);
        servicioComboBox.setPreferredSize(new Dimension(200, 35));
        servicioComboBox.setFont(new Font("Consolas", Font.BOLD, 16));
        servicioComboBox.setBackground(COLOR_GRIS_CARBON);
        servicioComboBox.setForeground(Color.WHITE);
        servicioComboBox.setBorder(BorderFactory.createLineBorder(COLOR_NEON_VERDE, 1));
        servicioComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        servicioComboBox.addActionListener(e -> {
            if (servicioComboBox.getSelectedIndex() > 0) {
                servicioComboBox.setForeground(COLOR_NEON_VERDE);
            } else {
                servicioComboBox.setForeground(Color.WHITE);
            }
        });

        panelServicios.add(Box.createVerticalGlue());
        panelServicios.add(title);
        panelServicios.add(Box.createRigidArea(new Dimension(0, 20)));
        panelServicios.add(servicioComboBox);
        panelServicios.add(Box.createVerticalGlue());
    }

    private void crearPanelDatos() {
        panelDatos = new JPanel();
        panelDatos.setLayout(new BoxLayout(panelDatos, BoxLayout.Y_AXIS));
        panelDatos.setOpaque(false);

        JLabel title = new JLabel("Ingresa los Datos");
        title.setFont(new Font("Impact", Font.BOLD, 24));
        title.setForeground(COLOR_NEON_VERDE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtNombre = createTextField("Nombre Completo");
        txtTelefono = createTextField("Número de Teléfono");
        txtMatricula = createTextField("Matrícula del Auto");

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(new Dimension(150, 30));
        dateChooser.getCalendarButton().setPreferredSize(new Dimension(25, 25));
        dateChooser.getCalendarButton().setBackground(COLOR_NEON_VERDE);
        dateChooser.getCalendarButton().setForeground(COLOR_GRIS_CARBON);
        dateChooser.setDate(Calendar.getInstance().getTime());

        dateChooser.getDateEditor().getUiComponent().setFont(new Font("Consolas", Font.BOLD, 14));
        dateChooser.getDateEditor().getUiComponent().setForeground(Color.WHITE);
        dateChooser.getDateEditor().getUiComponent().setBackground(COLOR_GRIS_CARBON);
        dateChooser.getDateEditor().getUiComponent().setBorder(BorderFactory.createLineBorder(COLOR_NEON_VERDE, 1));

        List<String> horas = new ArrayList<>();
        for (int h = 9; h <= 21; h++) {
            horas.add(String.format("%02d:30", h));
            if (h < 21) horas.add(String.format("%02d:00", h + 1));
        }
        horas.add("22:00");
        horaComboBox = new JComboBox<>(horas.toArray(new String[0]));
        horaComboBox.setPreferredSize(new Dimension(100, 30));
        horaComboBox.setFont(new Font("Consolas", Font.BOLD, 14));
        horaComboBox.setBackground(COLOR_GRIS_CARBON);
        horaComboBox.setForeground(Color.WHITE);
        horaComboBox.setBorder(BorderFactory.createLineBorder(COLOR_NEON_VERDE, 1));

        JPanel dateHourPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        dateHourPanel.setOpaque(false);
        dateHourPanel.add(dateChooser);
        dateHourPanel.add(horaComboBox);

        panelDatos.add(Box.createVerticalGlue());
        panelDatos.add(title);
        panelDatos.add(Box.createRigidArea(new Dimension(0, 20)));
        panelDatos.add(txtNombre);
        panelDatos.add(Box.createRigidArea(new Dimension(0, 10)));
        panelDatos.add(txtTelefono);
        panelDatos.add(Box.createRigidArea(new Dimension(0, 10)));
        panelDatos.add(txtMatricula);
        panelDatos.add(Box.createRigidArea(new Dimension(0, 15)));
        panelDatos.add(dateHourPanel);
        panelDatos.add(Box.createVerticalGlue());
    }

    private void crearPanelResumen() {
        panelResumen = new JPanel();
        panelResumen.setLayout(new BoxLayout(panelResumen, BoxLayout.Y_AXIS));
        panelResumen.setOpaque(false);

        JLabel title = new JLabel("Resumen del Turno");
        title.setFont(new Font("Impact", Font.BOLD, 24));
        title.setForeground(COLOR_NEON_VERDE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblServicio = new JLabel("Servicio: --");
        lblServicio.setFont(new Font("Consolas", Font.PLAIN, 18));
        lblServicio.setForeground(Color.WHITE);
        lblServicio.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNombre = new JLabel("Nombre: --");
        lblNombre.setFont(new Font("Consolas", Font.PLAIN, 18));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTelefono = new JLabel("Teléfono: --");
        lblTelefono.setFont(new Font("Consolas", Font.PLAIN, 18));
        lblTelefono.setForeground(Color.WHITE);
        lblTelefono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMatricula = new JLabel("Matrícula: --");
        lblMatricula.setFont(new Font("Consolas", Font.PLAIN, 18));
        lblMatricula.setForeground(Color.WHITE);
        lblMatricula.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblFechaHora = new JLabel("Fecha y Hora: --");
        lblFechaHora.setFont(new Font("Consolas", Font.PLAIN, 18));
        lblFechaHora.setForeground(Color.WHITE);
        lblFechaHora.setAlignmentX(Component.CENTER_ALIGNMENT);

        resumenLabels = new JLabel[]{lblServicio, lblNombre, lblTelefono, lblMatricula, lblFechaHora};

        panelResumen.add(Box.createVerticalGlue());
        panelResumen.add(title);
        panelResumen.add(Box.createRigidArea(new Dimension(0, 20)));
        for (JLabel lbl : resumenLabels) {
            panelResumen.add(lbl);
            panelResumen.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        panelResumen.add(Box.createVerticalGlue());
    }

    private JPanel crearBarraNavegacion() {
        // 👇 Panel con altura fija para evitar corte
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(600, 80); // 👈 Fuerza espacio suficiente
            }
        };
        nav.setOpaque(false);

        btnAnterior = createRoundedNeonButton("« Anterior", e -> navegarAtras());
        btnSiguiente = createRoundedNeonButton("Siguiente »", e -> navegarAdelante());
        btnConfirmar = createRoundedNeonButton("✅ Confirmar Turno", e -> confirmarTurno());

        nav.add(btnAnterior);
        nav.add(btnSiguiente);
        nav.add(btnConfirmar);

        btnAnterior.setVisible(false);
        btnConfirmar.setVisible(false);

        return nav;
    }

    private void navegarAdelante() {
        if ("SERVICIOS".equals(currentStep)) {
            servicioSeleccionado = (String) servicioComboBox.getSelectedItem();
            if (servicioSeleccionado == null || servicioSeleccionado.trim().isEmpty() || "Servicios".equals(servicioSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Selecciona un tipo de servicio.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cardLayout.show(cardPanel, "DATOS");
            currentStep = "DATOS";
            btnAnterior.setVisible(true);
            btnSiguiente.setVisible(true);
            btnSiguiente.setText("Siguiente »");
            btnConfirmar.setVisible(false);
        } else if ("DATOS".equals(currentStep)) {
            nombreCliente = txtNombre.getText().trim();
            telefonoCliente = txtTelefono.getText().trim();
            matriculaAuto = txtMatricula.getText().trim();
            fechaSeleccionada = dateChooser.getDate();
            horaSeleccionada = (String) horaComboBox.getSelectedItem();

            if (nombreCliente.isEmpty() || nombreCliente.equals("Nombre Completo")) {
                JOptionPane.showMessageDialog(this, "Ingresa tu nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (telefonoCliente.isEmpty() || telefonoCliente.equals("Número de Teléfono")) {
                JOptionPane.showMessageDialog(this, "Ingresa tu teléfono.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (matriculaAuto.isEmpty() || matriculaAuto.equals("Matrícula del Auto")) {
                JOptionPane.showMessageDialog(this, "Ingresa la matrícula.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (fechaSeleccionada == null) {
                JOptionPane.showMessageDialog(this, "Selecciona una fecha.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            actualizarResumen();
            cardLayout.show(cardPanel, "RESUMEN");
            currentStep = "RESUMEN";
            btnSiguiente.setVisible(false);
            btnConfirmar.setVisible(true);
        }
    }

    private void navegarAtras() {
        if ("DATOS".equals(currentStep)) {
            cardLayout.show(cardPanel, "SERVICIOS");
            currentStep = "SERVICIOS";
            btnAnterior.setVisible(false);
            btnSiguiente.setVisible(true);
            btnConfirmar.setVisible(false);
        } else if ("RESUMEN".equals(currentStep)) {
            cardLayout.show(cardPanel, "DATOS");
            currentStep = "DATOS";
            btnSiguiente.setVisible(true);
            btnConfirmar.setVisible(false);
            btnAnterior.setVisible(true);
        }
    }

    private void actualizarResumen() {
        resumenLabels[0].setText("Servicio: " + servicioSeleccionado);
        resumenLabels[1].setText("Nombre: " + nombreCliente);
        resumenLabels[2].setText("Teléfono: " + telefonoCliente);
        resumenLabels[3].setText("Matrícula: " + matriculaAuto);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaStr = fechaSeleccionada != null ? sdf.format(fechaSeleccionada) : "--";
        resumenLabels[4].setText("Fecha y Hora: " + fechaStr + " a las " + horaSeleccionada);
    }

    // Método auxiliar: crear campo de texto con estilo neón
    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Consolas", Font.BOLD, 16));
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

    // Método auxiliar: crear botón redondeado neón
    private JButton createRoundedNeonButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Impact", Font.BOLD, 18));
        button.setForeground(COLOR_GRIS_CARBON);
        button.setBackground(COLOR_NEON_VERDE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        // 👇 Reducido a 280 para que quepan dos botones
        button.setPreferredSize(new Dimension(280, 55));
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
                g2.setColor(COLOR_NEON_VERDE_CLARO);
                g2.fillRoundRect(0, 0, width, height, arc, arc);
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaFormateada = sdf.format(fechaSeleccionada);

        guardarTurnoEnBD(nombreCliente, telefonoCliente, matriculaAuto, servicioSeleccionado, fechaFormateada, horaSeleccionada);
    }

    // Método para guardar el turno en la base de datos
    private void guardarTurnoEnBD(String nombre, String telefono, String matricula, String tipoServicio, String fecha, String hora) {
        String url = "jdbc:mysql://localhost:3306/autostore";
        String usuario = "root";
        String contraseña = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
                String sql = "INSERT INTO turnos (nombre, telefono, matricula, tipoServicio, fecha, hora) VALUES (?, ?, ?, ?, ?, ?)";
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