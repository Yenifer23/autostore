package TURNOS_AUTOSTORE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class VentanaPrincipal extends JFrame {

    private static final String RUTA_FONDO = "imagenes/fondo.jpeg";
    private static final String RUTA_LOGO = "imagenes/logo_autostore.png";

    public VentanaPrincipal() {
        setTitle("Sistema de Turnos - Auto STORE!");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        BackgroundPanel panelPrincipal = new BackgroundPanel(RUTA_FONDO);
        panelPrincipal.setLayout(new GridBagLayout());
        add(panelPrincipal);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel labelLogo;
        if (new File(RUTA_LOGO).exists()) {
            ImageIcon iconLogo = new ImageIcon(RUTA_LOGO);
            labelLogo = new JLabel(iconLogo);
        } else {
            labelLogo = new JLabel("AUTO STORE", SwingConstants.CENTER);
            labelLogo.setFont(new Font("Arial", Font.BOLD, 48));
            labelLogo.setForeground(Color.WHITE);
        }
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
        panelPrincipal.add(labelLogo, gbc);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(2, 1, 10, 10));
        panelBotones.setBackground(Color.BLACK);
        panelBotones.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));

        JButton btnAgendar = new JButton("AGENDAR");
        btnAgendar.setFont(new Font("Arial", Font.BOLD, 30));
        btnAgendar.setForeground(Color.GREEN);
        btnAgendar.setBackground(Color.BLACK);
        btnAgendar.setFocusPainted(false);
        btnAgendar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAgendar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Has hecho clic en AGENDAR");
            }
        });
        panelBotones.add(btnAgendar);

        JButton btnNuevoTurno = new JButton("NUEVO TURNO");
        btnNuevoTurno.setFont(new Font("Arial", Font.BOLD, 30));
        btnNuevoTurno.setForeground(Color.GREEN);
        btnNuevoTurno.setBackground(Color.BLACK);
        btnNuevoTurno.setFocusPainted(false);
        btnNuevoTurno.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnNuevoTurno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Has hecho clic en NUEVO TURNO");
            }
        });
        panelBotones.add(btnNuevoTurno);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 50, 0, 0);
        panelPrincipal.add(panelBotones, gbc);

        JPanel panelInfo = new JPanel();
        panelInfo.setOpaque(false);
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));

        JLabel labelWeb = new JLabel("Autostore - Catamarca.com.ar");
        labelWeb.setFont(new Font("Arial", Font.PLAIN, 20));
        labelWeb.setForeground(Color.WHITE);
        labelWeb.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelSucursales = new JLabel("La Rioja - Catamarca");
        labelSucursales.setFont(new Font("Arial", Font.PLAIN, 18));
        labelSucursales.setForeground(Color.WHITE);
        labelSucursales.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelInfo.add(Box.createVerticalStrut(10));
        panelInfo.add(labelWeb);
        panelInfo.add(labelSucursales);
        panelInfo.add(Box.createVerticalStrut(10));

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(10, 10, 20, 10);
        panelPrincipal.add(panelInfo, gbc);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal());
    }
}

// ESTA CLASE FALTABA: Asegúrate de que esté incluida en el mismo archivo.
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            this.backgroundImage = icon.getImage();

            if (this.backgroundImage == null || icon.getIconWidth() == -1) {
                System.err.println("Error: No se pudo cargar la imagen de fondo: " + imagePath);
                setBackground(Color.DARK_GRAY);
            }
        } catch (Exception e) {
            System.err.println("Excepción al cargar la imagen de fondo: " + imagePath);
            e.printStackTrace();
            setBackground(Color.DARK_GRAY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}