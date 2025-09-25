package com.autostore.turnos; // Mantén este paquete si lo creaste

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File; // Necesario para comprobar si la imagen existe

public class VentanaPrincipal extends JFrame {

    private static final String RUTA_FONDO = "imagenes/fondo.jpeg"; // Ruta de tu imagen de fondo
    // Asumiendo que tienes una imagen para el logo, si no, se usará texto
    private static final String RUTA_LOGO = "imagenes/logo_autostore.png"; // Ruta de tu imagen de logo

    public VentanaPrincipal() {
        setTitle("Sistema de Turnos - Auto STORE");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // Para que la ventana no se pueda redimensionar y el fondo se vea bien

        // --- Panel personalizado para el fondo de imagen ---
        BackgroundPanel panelPrincipal = new BackgroundPanel(RUTA_FONDO);
        panelPrincipal.setLayout(new GridBagLayout()); // Usaremos GridBagLayout para un control preciso de la posición
        add(panelPrincipal);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Márgenes entre componentes

        // --- Logo de Auto STORE ---
        JLabel labelLogo;
        // Intentamos cargar la imagen del logo
        if (new File(RUTA_LOGO).exists()) {
            ImageIcon iconLogo = new ImageIcon(RUTA_LOGO);
            // Si la imagen es muy grande, puedes escalarla
            // Image img = iconLogo.getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH);
            // labelLogo = new JLabel(new ImageIcon(img));
            labelLogo = new JLabel(iconLogo); // Usamos la imagen directamente
        } else {
            // Si la imagen no existe, usamos el texto como fallback
            labelLogo = new JLabel("auto STORE", SwingConstants.CENTER);
            labelLogo.setFont(new Font("Arial", Font.BOLD, 48));
            labelLogo.setForeground(Color.WHITE);
        }
        gbc.gridx = 1; // Columna
        gbc.gridy = 0; // Fila
        gbc.gridwidth = 2; // Ocupa 2 columnas
        gbc.anchor = GridBagConstraints.NORTH; // Alineado arriba
        panelPrincipal.add(labelLogo, gbc);


        // --- Panel para los botones (AGENDAR y NUEVO TURNO) ---
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(2, 1, 10, 10)); // 2 filas, 1 columna, con espacios de 10px
        panelBotones.setBackground(Color.BLACK); // Fondo negro
        panelBotones.setBorder(BorderFactory.createLineBorder(Color.GREEN, 5)); // Borde verde neón

        // --- Botón AGENDAR ---
        JButton btnAgendar = new JButton("AGENDAR");
        btnAgendar.setFont(new Font("Arial", Font.BOLD, 30));
        btnAgendar.setForeground(Color.GREEN);
        btnAgendar.setBackground(Color.BLACK);
        btnAgendar.setFocusPainted(false);
        btnAgendar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding interno
        btnAgendar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane