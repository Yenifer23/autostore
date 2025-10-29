import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*; // Clases de JDBC
import java.util.Vector;
import javax.swing.border.EmptyBorder;

public class VerTurnosVentana extends JFrame {

    private static final Color COLOR_NEON_VERDE = new Color(57, 255, 20); 
    private static final Color COLOR_GRIS_OSCURO = new Color(35, 35, 35);
    private static final Color COLOR_VERDE_SUAVE = new Color(152, 255, 152); // Nuevo color para la tabla
    
    private JTable tablaTurnos;
    private DefaultTableModel modeloTabla;
    private JTextField txtFechaFiltro; // Campo para ingresar la fecha

    public VerTurnosVentana() {
        setTitle("Agenda de Turnos - Auto STORE!");
        setSize(1000, 600); // Aumentamos un poco el tamaño
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); 
        getContentPane().setBackground(COLOR_GRIS_OSCURO);

        // --- Título ---
        JLabel lblTitulo = new JLabel("AGENDA DE TURNOS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Bell MT", Font.BOLD, 30));
        lblTitulo.setForeground(COLOR_NEON_VERDE);
        lblTitulo.setBorder(new EmptyBorder(15, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);
        
        // --- Panel de Filtro (NUEVO APARTADO) ---
        JPanel panelFiltro = crearPanelFiltro();
        add(panelFiltro, BorderLayout.BEFORE_FIRST_LINE); // Colocarlo justo debajo del título

        // --- Modelo de Tabla ---
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        
        modeloTabla.setColumnIdentifiers(new String[]{"ID", "Cliente", "Teléfono", "Servicio", "Fecha", "Hora"});

        // --- JTable y Estilo (MODIFICADO) ---
        tablaTurnos = new JTable(modeloTabla);
        
        // Estilo del cuerpo de la tabla
        tablaTurnos.setBackground(Color.WHITE); 
        tablaTurnos.setForeground(Color.BLACK); // Letras negras
        tablaTurnos.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaTurnos.setRowHeight(25);
        tablaTurnos.setGridColor(COLOR_NEON_VERDE.darker());
        tablaTurnos.setSelectionBackground(COLOR_NEON_VERDE.darker().darker());
        tablaTurnos.setSelectionForeground(Color.WHITE);
        
        // Estilo del encabezado (JTableHeader)
        tablaTurnos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        tablaTurnos.getTableHeader().setBackground(COLOR_VERDE_SUAVE); // Fondo verde suave
        tablaTurnos.getTableHeader().setForeground(Color.BLACK); // Letras negras
        
        JScrollPane scrollPane = new JScrollPane(tablaTurnos);
        scrollPane.getViewport().setBackground(COLOR_GRIS_OSCURO);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_NEON_VERDE, 2));
        
        add(scrollPane, BorderLayout.CENTER);

        // --- Cargar datos iniciales (sin filtro) ---
        cargarTurnosDesdeDB(null); 

        setVisible(true);
    }
    
    /**
     * Crea el panel de entrada para filtrar los turnos por fecha.
     */
    private JPanel crearPanelFiltro() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(COLOR_GRIS_OSCURO.darker());
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel lblFecha = new JLabel("Filtrar por Fecha (YYYY-MM-DD):");
        lblFecha.setForeground(Color.WHITE);
        lblFecha.setFont(new Font("Arial", Font.BOLD, 14));
        
        txtFechaFiltro = new JTextField(12);
        txtFechaFiltro.setFont(new Font("Arial", Font.PLAIN, 14));
        txtFechaFiltro.setBorder(BorderFactory.createLineBorder(COLOR_NEON_VERDE));
        
        JButton btnFiltrar = new JButton("FILTRAR");
        btnFiltrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnFiltrar.setBackground(COLOR_NEON_VERDE);
        btnFiltrar.setForeground(Color.BLACK);
        btnFiltrar.setFocusPainted(false);
        
        // Listener para el botón de filtrar
        btnFiltrar.addActionListener(e -> {
            String fecha = txtFechaFiltro.getText().trim();
            // Si el campo está vacío, cargamos todos los turnos (pasamos null)
            cargarTurnosDesdeDB(fecha.isEmpty() ? null : fecha); 
        });

        // Botón para mostrar todos los turnos
        JButton btnMostrarTodo = new JButton("MOSTRAR TODOS");
        btnMostrarTodo.setFont(new Font("Arial", Font.BOLD, 14));
        btnMostrarTodo.setBackground(COLOR_GRIS_OSCURO.brighter());
        btnMostrarTodo.setForeground(COLOR_NEON_VERDE);
        btnMostrarTodo.setFocusPainted(false);
        
        btnMostrarTodo.addActionListener(e -> {
            txtFechaFiltro.setText("");
            cargarTurnosDesdeDB(null);
        });

        panel.add(lblFecha);
        panel.add(txtFechaFiltro);
        panel.add(btnFiltrar);
        panel.add(btnMostrarTodo);

        return panel;
    }

    /**
     * Método para conectar a la Base de Datos y cargar los turnos, aplicando un filtro de fecha.
     * @param fechaFiltro Fecha en formato 'YYYY-MM-DD' para filtrar. Si es null, carga todos.
     */
    private void cargarTurnosDesdeDB(String fechaFiltro) {
        // *** CONFIGURACIÓN DE TU BASE DE DATOS (REEMPLAZAR) ***
        String url = "jdbc:mysql://localhost:3306/nombre_tu_base_datos";
        String usuario = "tu_usuario";
        String password = "tu_password";
        
        // Consulta base: Asegúrate que las columnas coincidan con tu base de datos
        String consultaBase = "SELECT id, nombre_cliente, telefono, servicio, fecha, hora FROM tabla_turnos";
        
        String consultaFinal;
        
        // 1. Construir la consulta con o sin filtro
        if (fechaFiltro != null && !fechaFiltro.isEmpty()) {
            // Utilizamos el campo 'fecha' de tu base de datos
            consultaFinal = consultaBase + " WHERE fecha = ? ORDER BY hora ASC";
        } else {
            consultaFinal = consultaBase + " ORDER BY fecha, hora ASC";
        }

        // Limpia la tabla antes de cargar
        modeloTabla.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(url, usuario, password);
             PreparedStatement pstmt = conn.prepareStatement(consultaFinal)) {

            // 2. Si hay filtro, establecer el parámetro en el PreparedStatement
            if (fechaFiltro != null && !fechaFiltro.isEmpty()) {
                pstmt.setString(1, fechaFiltro);
            }
            
            // 3. Ejecutar la consulta
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // 4. Llenar la tabla
                while (rs.next()) {
                    Vector<Object> row = new Vector<>(columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getObject(i));
                    }
                    modeloTabla.addRow(row);
                }
                
                // Mensaje de éxito al cargar
                if (fechaFiltro != null && !fechaFiltro.isEmpty() && modeloTabla.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No se encontraron turnos para la fecha: " + fechaFiltro, "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                }

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error de Base de Datos al cargar/filtrar turnos. \nDetalles: " + e.getMessage(), 
                "Error de Base de Datos", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Puedes descomentar este main para probar solo la ventana de turnos:
    /*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VerTurnosVentana());
    }
    */
}