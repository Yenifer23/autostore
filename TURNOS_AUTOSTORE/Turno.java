import javax.swing.Timer;
import java.awt.event.*;
import com.toedter.calendar.JDateChooser;
import java.util.Date;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class Turno extends JFrame {
    // Colores y fuentes
    private static final Color COLOR_FONDO = new Color(26,26,26);
    private static final Color COLOR_NEON  = new Color(57,255,20);
    private static final Color COLOR_TEXT  = Color.WHITE;
    private static final Font  FT_TITULO   = new Font("Bell MT", Font.BOLD, 32);
    private static final Font  FT_NAV      = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font  FT_SUB      = new Font("Segoe UI", Font.PLAIN, 18);
    private static final Font  FT_LABEL    = new Font("Segoe UI", Font.BOLD, 14);

    // Componentes principales
    private CardLayout cardLayout;
    private JPanel cards;            // el panel con las 3 pantallas
    private JLabel lblTitulo, lblSubtitulo;
    private JPanel navPanel;         // navigation labels (SERVICIOS / DATOS / RESUMEN)
    private String servicioSeleccionado = "";

    // Campos (datos)
    private JTextField txtNombre, txtCelular, txtMatricula;
    private JDateChooser dateChooser;
    private JComboBox<String> comboHora;
    private JTextArea resumenArea;

    public Turno() {
        setTitle("Agendar Turno - AutoStore");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // GlowPanel con borde neón animado (es el contentPane)
        GlowPanel content = new GlowPanel(new BorderLayout());
        setContentPane(content);

        // HEADER fijo (TITULO -> NAV -> SUBTITULO)
        JPanel header = crearHeader();
        content.add(header, BorderLayout.NORTH);

        // CARDS (center)
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setOpaque(false);
        content.add(cards, BorderLayout.CENTER);

        // Crear las 3 pantallas y agregarlas
        cards.add(crearPanelServicios(), "SERVICIOS");
        cards.add(crearPanelDatos(), "DATOS");
        cards.add(crearPanelResumen(), "RESUMEN");

        cardLayout.show(cards, "SERVICIOS");
        setVisible(true);
    }

    // ---------------- Header (TITULO + NAV + SUBTITULO) ----------------
    private JPanel crearHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(18,18,10,18));

        lblTitulo = new JLabel("Agenda Nuevo Turno", SwingConstants.CENTER);
        lblTitulo.setFont(FT_TITULO);
        lblTitulo.setForeground(COLOR_TEXT);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 6));
        navPanel.setOpaque(false);
        // crear los 3 labels de nav
        navPanel.add(crearNavLabel("SERVICIOS", true));
        navPanel.add(crearNavLabel("DATOS DEL CLIENTE", false));
        navPanel.add(crearNavLabel("RESUMEN", false));

        lblSubtitulo = new JLabel("Elige el servicio a continuación", SwingConstants.CENTER);
        lblSubtitulo.setFont(FT_SUB);
        lblSubtitulo.setForeground(COLOR_NEON);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblTitulo);
        header.add(navPanel);
        header.add(Box.createVerticalStrut(6));
        header.add(lblSubtitulo);

        return header;
    }

    private JLabel crearNavLabel(String text, boolean activo) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FT_NAV);
        lbl.setForeground(activo ? COLOR_NEON : Color.GRAY);
        return lbl;
    }

    private void actualizarNav(String activo, String subtitulo) {
        // actualizar navPanel: recolorear segun activo
        Component[] comps = navPanel.getComponents();
        for (Component c : comps) {
            if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                l.setForeground(l.getText().equals(activo) ? COLOR_NEON : Color.GRAY);
            }
        }
        lblSubtitulo.setText(subtitulo);
    }

    // ---------------- Panel SERVICIOS ----------------
    private JPanel crearPanelServicios() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        // body center: grid con 3 botones
        JPanel grid = new JPanel(new GridLayout(1,3,60,20));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));

        JButton b1 = crearBotonServicio("Instalación de Audio", "/Imagenes/audio2.png");
        JButton b2 = crearBotonServicio("Polarizado", "/Imagenes/polarizado2.png");
        JButton b3 = crearBotonServicio("Audio y Polarizado", "/Imagenes/audioypolarizado2.png");

        grid.add(b1); grid.add(b2); grid.add(b3);
        p.add(grid, BorderLayout.CENTER);

        // footer con botones a izquierda/derecha
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(12,40,22,40));

        JButton btnPrev = new JButton(" "); // vacío a la izquierda (no visible)
        btnPrev.setVisible(false);
        footer.add(btnPrev, BorderLayout.WEST);

        JButton btnNext = new JButton("Siguiente >>");
        estiloBotonPrincipal(btnNext);
        btnNext.addActionListener(e -> {
            if (servicioSeleccionado.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccioná un servicio primero.");
                return;
            }
            actualizarNav("DATOS DEL CLIENTE", "Ingresa los datos del cliente");
            cardLayout.show(cards, "DATOS");
        });
        footer.add(btnNext, BorderLayout.EAST);

        p.add(footer, BorderLayout.SOUTH);

        // asegurar header correcto
        actualizarNav("SERVICIOS", "Elige el servicio a continuación");
        return p;
    }

    private JButton crearBotonServicio(String texto, String recursoIcon) {
        JButton btn = new JButton(texto);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setFont(FT_LABEL);
        btn.setForeground(COLOR_NEON);
        btn.setBackground(new Color(38,38,38));
        btn.setBorder(BorderFactory.createLineBorder(COLOR_NEON, 2));
        btn.setFocusPainted(false);

        // intentar cargar icono
        try {
            java.net.URL url = getClass().getResource(recursoIcon);
            if (url != null) {
                ImageIcon ic = new ImageIcon(url);
                Image img = ic.getImage().getScaledInstance(96,96, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
            }
        } catch (Exception ex) {
            // si falla, se muestra solo texto
        }

        btn.addActionListener(e -> {
            servicioSeleccionado = texto;
            // marcar visualmente el seleccionado: cambiar borde y fondo
            Component parent = btn.getParent();
            if (parent instanceof Container) {
                for (Component c : ((Container) parent).getComponents()) {
                    if (c instanceof JButton) {
                        ((JButton)c).setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                    }
                }
            }
            btn.setBorder(BorderFactory.createLineBorder(COLOR_NEON, 3));
        });

        return btn;
    }

    private void estiloBotonPrincipal(JButton b) {
        b.setBackground(COLOR_NEON);
        b.setForeground(Color.BLACK);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
        b.setFocusPainted(false);
    }

    // ---------------- Panel DATOS DEL CLIENTE ----------------
    private JPanel crearPanelDatos() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        // form central, etiquetas alineadas a la izquierda y campos a la derecha
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(20,120,20,120));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(12,12,12,12);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0.35;
        JLabel lNombre = new JLabel("Nombre y Apellido:");
        lNombre.setForeground(COLOR_TEXT); lNombre.setFont(FT_LABEL);
        form.add(lNombre, g);
        g.gridx = 1; g.weightx = 0.65;
        txtNombre = new JTextField(); estiloCampo(txtNombre);
        form.add(txtNombre, g);

        g.gridx = 0; g.gridy++;
        JLabel lCel = new JLabel("Celular:");
        lCel.setForeground(COLOR_TEXT); lCel.setFont(FT_LABEL);
        form.add(lCel, g);
        g.gridx = 1;
        txtCelular = new JTextField(); estiloCampo(txtCelular);
        form.add(txtCelular, g);

        g.gridx = 0; g.gridy++;
        JLabel lMat = new JLabel("Matrícula:");
        lMat.setForeground(COLOR_TEXT); lMat.setFont(FT_LABEL);
        form.add(lMat, g);
        g.gridx = 1;
        txtMatricula = new JTextField(); estiloCampo(txtMatricula);
        form.add(txtMatricula, g);

        g.gridx = 0; g.gridy++;
        JLabel lFecha = new JLabel("Fecha:");
        lFecha.setForeground(COLOR_TEXT); lFecha.setFont(FT_LABEL);
        form.add(lFecha, g);
        g.gridx = 1;
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setMinSelectableDate(new Date());
        form.add(dateChooser, g);

        g.gridx = 0; g.gridy++;
        JLabel lHora = new JLabel("Hora (09:00-13:00 / 18:00-22:00):");
        lHora.setForeground(COLOR_TEXT); lHora.setFont(FT_LABEL);
        form.add(lHora, g);
        g.gridx = 1;
        comboHora = new JComboBox<>();
        for (int h=9; h<=13; h++) comboHora.addItem(String.format("%02d:00", h));
        for (int h=18; h<=22; h++) comboHora.addItem(String.format("%02d:00", h));
        form.add(comboHora, g);

        p.add(form, BorderLayout.CENTER);

        // footer: anterior (izq) y siguiente (der)
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10,40,20,40));

        JButton btnAnterior = new JButton("<< Anterior");
        btnAnterior.setFont(FT_LABEL);
        btnAnterior.setBackground(COLOR_NEON); btnAnterior.setForeground(Color.BLACK);
        btnAnterior.addActionListener(e -> {
            actualizarNav("SERVICIOS", "Elige el servicio a continuación");
            cardLayout.show(cards, "SERVICIOS");
        });
        footer.add(btnAnterior, BorderLayout.WEST);

        JButton btnSiguiente = new JButton("Siguiente >>");
        estiloBotonPrincipal(btnSiguiente);
        btnSiguiente.addActionListener(e -> {
            // validaciones
            if (servicioSeleccionado.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccioná un servicio.");
                return;
            }
            if (txtNombre.getText().trim().isEmpty() ||
                txtCelular.getText().trim().isEmpty() ||
                txtMatricula.getText().trim().isEmpty() ||
                dateChooser.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Completá todos los campos.");
                return;
            }
            actualizarNav("RESUMEN", "Resumen del turno");
            actualizarResumen();
            cardLayout.show(cards, "RESUMEN");
        });
        footer.add(btnSiguiente, BorderLayout.EAST);

        p.add(footer, BorderLayout.SOUTH);

        // cuando se muestra este panel, actualizar header
        // (si querés que se actualice automáticamente al navegar, lo hacemos en listeners)
        return p;
    }

    private void estiloCampo(JTextField t) {
        t.setBackground(new Color(35,35,35));
        t.setForeground(COLOR_NEON);
        t.setCaretColor(COLOR_NEON);
        t.setBorder(BorderFactory.createLineBorder(COLOR_NEON));
    }

    // ---------------- Panel RESUMEN ----------------
    private JPanel crearPanelResumen() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        // centro: resumen alineado y centrado horizontalmente
        resumenArea = new JTextArea();
        resumenArea.setEditable(false);
        resumenArea.setBackground(COLOR_FONDO);
        resumenArea.setForeground(Color.WHITE);
        resumenArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        resumenArea.setBorder(BorderFactory.createEmptyBorder(18,40,18,40));

        JScrollPane sp = new JScrollPane(resumenArea);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        p.add(sp, BorderLayout.CENTER);

        // footer: anterior izq, confirmar der
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10,40,20,40));

        JButton btnAnterior = new JButton("<< Anterior");
        btnAnterior.setFont(FT_LABEL);
        btnAnterior.setBackground(COLOR_NEON);
        btnAnterior.setForeground(Color.BLACK);
        btnAnterior.addActionListener(e -> {
            actualizarNav("DATOS DEL CLIENTE", "Ingresa los datos del cliente");
            cardLayout.show(cards, "DATOS");
        });
        footer.add(btnAnterior, BorderLayout.WEST);

        JButton btnConfirm = new JButton("Confirmar Turno");
        estiloBotonPrincipal(btnConfirm);
        btnConfirm.addActionListener(e -> {
            // aquí podés integrar guardado en DB
            JOptionPane.showMessageDialog(this, "Turno confirmado correctamente.");
            dispose();
        });
        footer.add(btnConfirm, BorderLayout.EAST);

        p.add(footer, BorderLayout.SOUTH);
        return p;
    }

    private void actualizarResumen() {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date f = dateChooser.getDate();
        String fecha = f == null ? "" : df.format(f);
        String hora = comboHora.getSelectedItem() == null ? "" : comboHora.getSelectedItem().toString();

        StringBuilder sb = new StringBuilder();
        sb.append("Servicio: ").append(servicioSeleccionado).append("\n\n");
        sb.append("Nombre y Apellido: ").append(txtNombre.getText()).append("\n");
        sb.append("Celular: ").append(txtCelular.getText()).append("\n");
        sb.append("Matrícula: ").append(txtMatricula.getText()).append("\n");
        sb.append("Fecha: ").append(fecha).append("\n");
        sb.append("Hora: ").append(hora).append("\n");

        resumenArea.setText(sb.toString());
    }

    // ---------------- GlowPanel (dibuja bordes + glow) ----------------
    private class GlowPanel extends JPanel {
        private float alpha = 0.0f;
        private boolean up = true;
        public GlowPanel(LayoutManager lm) {
            super(lm);
            setBackground(COLOR_FONDO);
            setOpaque(true);
            // start animation
            javax.swing.Timer t = new javax.swing.Timer(50, e -> {
                if (up) { alpha += 0.02f; if (alpha >= 0.8f) up = false; }
                else { alpha -= 0.02f; if (alpha <= 0.15f) up = true; }
                repaint();
            });
            t.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // dibujar glow exterior: varias capas con alpha decreciente
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            // capas de glow
            for (int i=8; i>=1; i--) {
                float a = alpha * (0.12f * i); // disminuye por capa
                if (a > 1f) a = 1f;
                g2.setComposite(AlphaComposite.SrcOver.derive(a));
                g2.setColor(COLOR_NEON);
                g2.setStroke(new BasicStroke(i));
                g2.drawRoundRect(6 - i/2, 6 - i/2, w - (12 - i), h - (12 - i), 8, 8);
            }
            // borde interior más definido
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(COLOR_NEON);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(8,8,w-16,h-16,8,8);

            g2.dispose();
        }
    }

    // ---------------- main ----------------
    public static void main(String[] args) {
        // asegurate de agregar jcalendar al classpath
        SwingUtilities.invokeLater(() -> new Turno());
    }
}
