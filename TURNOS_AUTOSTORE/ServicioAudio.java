import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;
import com.toedter.calendar.JDateChooser;

public class ServicioAudio extends JFrame {

    // --- COLORES DEFINIDOS (coherentes con ServiciosVentana) ---
    private static final Color COLOR_NEON_VERDE = new Color(57, 255, 20);
    private static final Color COLOR_NEON_VERDE_CLARO = new Color(102, 255, 127);
    private static final Color COLOR_GRIS_CARBON = new Color(26, 26, 26);
    private static final Color COLOR_GRIS_OSCURO_FONDO = new Color(35, 35, 35);

    private final int ANCHO_VENTANA = 600;
    private final int ALTO_VENTANA = 650;

    // Componentes de entrada
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtMatricula;
    private JDateChooser dateChooser; // Para elegir fecha
    private JComboBox<String> horaComboBox; // Para elegir hora

    // Panel principal con fondo animado
    private NeonBackgroundPanel panelPrincipal;

    public ServicioAudio() {
        setTitle("Instalación de Audio - AUTO STORE");
        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Inicializar panel principal con fondo animado
        panelPrincipal = new NeonBackgroundPanel(COLOR_GRIS_CARBON, COLOR_NEON_VERDE);
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));

        // Contenedor de contenido
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(80, 30, 30, 30));

        // Título
        JLabel titleLabel = new JLabel("Instalación de Audio");
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

        // Fecha y Hora
        JPanel dateHourPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        dateHourPanel.setOpaque(false);

        // Selector de fecha (usaremos JDateChooser)
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(new Dimension(150, 30));
        dateChooser.getCalendarButton().setPreferredSize(new Dimension(25, 25));
        dateChooser.getCalendarButton().setBackground(COLOR_NEON_VERDE);
        dateChooser.getCalendarButton().setForeground(COLOR_GRIS_CARBON);
        dateChooser.getCalendarButton().setFont(new Font("Arial", Font.BOLD, 12));
        dateChooser.getDateEditor().getUiComponent().setFont(new Font("Arial", Font.PLAIN, 14));
        dateChooser.setDate(Calendar.getInstance().getTime()); // Hoy por defecto

        // Selector de hora
        String[] horasDisponibles = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};
        horaComboBox = new JComboBox<>(horasDisponibles);
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

        // Botón CONFIRMAR
        JButton btnConfirmar = createRoundedNeonButton("CONFIRMAR", e -> confirmarTurno());
        btnConfirmar.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(btnConfirmar);

        panelPrincipal.add(contentPanel);
        add(panelPrincipal);

        setVisible(true);

        // Timer de animación (partículas)
        Timer animationTimer = new Timer(50, ev -> {
            if (panelPrincipal.getWidth() > 0 && panelPrincipal.getHeight() > 0) {
                panelPrincipal.updateAnimations();
                panelPrincipal.repaint();
            }
        });
        animationTimer.start();
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

    // Método auxiliar: crear botón redondeado neón (igual que en ServiciosVentana)
    private JButton createRoundedNeonButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Bell MT", Font.BOLD, 18));
        button.setForeground(COLOR_GRIS_CARBON);
        button.setBackground(COLOR_NEON_VERDE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setPreferredSize(new Dimension(250, 55));
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
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String matricula = txtMatricula.getText().trim();
        java.util.Date selectedDate = dateChooser.getDate();
        String horaSeleccionada = (String) horaComboBox.getSelectedItem();

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
            Fecha: %s
            Hora: %s
            -------------------
            ¿Confirmar este turno?
            """,
            nombre, telefono, matricula, fechaFormateada, horaSeleccionada
        );

        int opcion = JOptionPane.showConfirmDialog(this, resumen, "Confirmar Turno", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            guardarTurnoEnBD(nombre, telefono, matricula, fechaFormateada, horaSeleccionada);
        }
    }

    // Método para guardar el turno en la base de datos
    private void guardarTurnoEnBD(String nombre, String telefono, String matricula, String fecha, String hora) {
        String url = "jdbc:mysql://localhost:3306/autostore"; // base de datos
        String usuario = "root"; // Cambia si usas otro usuario
        String contraseña = ""; // Cambia si tienes contraseña

        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
            String sql = "INSERT INTO turnos_audio (nombre, telefono, matricula, fecha, hora) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            stmt.setString(2, telefono);
            stmt.setString(3, matricula);
            stmt.setString(4, fecha);
            stmt.setString(5, hora);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "✅ Turno agendado correctamente!\nTe contactaremos para confirmar.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Cerrar la ventana después de guardar
            } else {
                JOptionPane.showMessageDialog(this, "❌ No se pudo guardar el turno. Inténtalo de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error de conexión a la base de datos:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Clase interna: Fondo animado (igual que en ServiciosVentana)
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

            // Rejilla sutil
            int spacing = 50;
            Color gridColor = new Color(60, 60, 60, 50);
            g2d.setColor(gridColor);
            for (int y = 0; y < getHeight(); y += spacing) {
                g2d.drawLine(0, y, getWidth(), y);
            }
            for (int x = 0; x < getWidth(); x += spacing) {
                g2d.drawLine(x, 0, x, getHeight());
            }

            // Partículas
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
                if (x < 0 || x > panelWidth) speedX *= -1;
                if (y < 0 || y > panelHeight) speedY *= -1;
            }

            public int getX() { return x; }
            public int getY() { return y; }
            public int getSize() { return size; }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServicioAudio());
    }
}