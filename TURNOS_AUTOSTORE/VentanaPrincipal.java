package TURNOS_AUTOSTORE;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class VentanaPrincipal extends JFrame {

    // Asegúrate de que tu carpeta 'Imagenes' contenga 'fondo 1.jpeg'
    private static final String RUTA_FONDO = "Imagenes/fondo 1.jpeg"; 
    private static final String RUTA_LOGO = "imagenes/logo_autostore.png";
    private static final String RUTA_ICONO_MAXIMIZAR = "imagenes/icono_maximizar.png"; 

    // Definición de un color Verde Flúor personalizado para el borde
    private static final Color COLOR_FLUOR_VERDE = new Color(51, 255, 51); 
    // Nuevo color de neón para el borde (más brillante y un poco más grande)
    private static final Color COLOR_NEON_VERDE = new Color(0, 255, 0); 

    public VentanaPrincipal() {
        setTitle("Sistema de Turnos - Auto STORE!");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // El panel principal usará la clase BackgroundPanel que ahora aplica transparencia
        BackgroundPanel panelPrincipal = new BackgroundPanel(RUTA_FONDO);
        panelPrincipal.setLayout(new GridBagLayout());
        add(panelPrincipal);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- 1. Botón de Maximizar Pantalla ---
        JLabel labelMaximizar = new JLabel();
        File archivoIcono = new File(RUTA_ICONO_MAXIMIZAR);
        
        if (archivoIcono.exists()) {
            ImageIcon iconOriginal = new ImageIcon(RUTA_ICONO_MAXIMIZAR);
            Image img = iconOriginal.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            labelMaximizar.setIcon(new ImageIcon(img));
            
            labelMaximizar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            });

            gbc.gridx = 2; 
            gbc.gridy = 0; 
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.insets = new Insets(20, 10, 10, 20);
            panelPrincipal.add(labelMaximizar, gbc);
        } else {
             System.err.println("Advertencia: El ícono de maximizar no se encontró en la ruta: " + RUTA_ICONO_MAXIMIZAR);
        }
        
        // Reiniciamos gbc para los siguientes componentes
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // --- 2. Logo sin texto ---
        JLabel labelLogo;
        if (new File(RUTA_LOGO).exists()) {
            ImageIcon iconLogo = new ImageIcon(RUTA_LOGO);
            labelLogo = new JLabel(iconLogo);
        } else {
            labelLogo = new JLabel(""); 
        }
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        if (new File(RUTA_LOGO).exists() || !labelLogo.getText().isEmpty()) {
            panelPrincipal.add(labelLogo, gbc);
        }
        // --- Fin de Modificación del logo ---


        // --- Creación y estilo del botón AGENDAR NUEVO TURNO ---
        JButton btnAgendarNuevoTurno = new JButton("<html><center>Agendar<br>Nuevo Turno</center></html>"); 
        
        btnAgendarNuevoTurno.setFont(new Font("Bell MT", Font.BOLD, 20)); 
        btnAgendarNuevoTurno.setForeground(COLOR_NEON_VERDE);
        btnAgendarNuevoTurno.setBackground(Color.BLACK);
        btnAgendarNuevoTurno.setFocusPainted(false);
        
        // Borde interno grande para el estilo Neón
        btnAgendarNuevoTurno.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        btnAgendarNuevoTurno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Has hecho clic en AGENDAR NUEVO TURNO");
            }
        });
        
        // Efecto Hover para el borde neón
        btnAgendarNuevoTurno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Efecto de brillo al pasar el mouse (hover)
                JPanel wrapper = (JPanel) ((JButton)evt.getSource()).getParent();
                Graphics2D g2 = (Graphics2D) wrapper.getGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(7)); // Más grueso
                g2.setColor(COLOR_FLUOR_VERDE.brighter()); // Más brillante
                g2.drawRoundRect(2, 2, wrapper.getWidth() - 5, wrapper.getHeight() - 5, 30, 30);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Forzar el redibujado al salir para restaurar el borde
                JPanel wrapper = (JPanel) ((JButton)evt.getSource()).getParent();
                wrapper.repaint();
            }
        });

        // Envolvemos el botón en un JPanel para darle el fondo y borde NEÓN REDONDEADO
        JPanel panelBotonWrapper = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Pintamos un rectángulo negro redondeado como fondo del botón
                g.setColor(Color.BLACK);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Radio de esquina 30
            }
            @Override
            protected void paintBorder(Graphics g) {
                // Pintamos el borde NEÓN redondeado
                g.setColor(COLOR_NEON_VERDE);
                Graphics2D g2 = (Graphics2D) g;
                // Mejorar la calidad de los bordes redondeados
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
                g2.setStroke(new BasicStroke(5)); // Grosor del neón
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 30, 30);
            }
        };
        panelBotonWrapper.setOpaque(false);
        
        // Hacemos el botón interior transparente para que el fondo negro del wrapper se vea
        btnAgendarNuevoTurno.setOpaque(false);
        btnAgendarNuevoTurno.setBackground(new Color(0,0,0,0)); // Fondo transparente
        
        panelBotonWrapper.add(btnAgendarNuevoTurno);
        
        // --- Fin de la modificación del botón ---

        // Posicionamiento del contenedor del botón
        gbc.gridx = 1; 
        gbc.gridy = 1; 
        gbc.gridwidth = 1; 
        gbc.anchor = GridBagConstraints.CENTER; 
        
        // Margen superior para la posición final (320 píxeles)
        gbc.insets = new Insets(320, 10, 10, 10); 
        
        panelPrincipal.add(panelBotonWrapper, gbc);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal());
    }
}

// =======================================================================
// CLASE BACKGROUNDPANEL CON TRANSPARENCIA
// =======================================================================
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    // Constante para el nivel de opacidad (0.7f = 70% opaco)
    private static final float OPACITY = 0.7f; 

    public BackgroundPanel(String imagePath) {
        // Establecemos el color de fondo del JPanel a negro. Al dibujar la imagen 
        // con 70% de opacidad, el 30% restante será de color negro, oscureciendo la imagen.
        setBackground(Color.BLACK); 

        try {
            // Intenta cargar el recurso usando el ClassLoader (la forma estándar)
            java.net.URL imgURL = getClass().getClassLoader().getResource(imagePath);
            
            if (imgURL != null) {
                this.backgroundImage = new ImageIcon(imgURL).getImage();
            } else {
                // Si el ClassLoader falla (común en entornos IDE como VS Code),
                // intenta cargar directamente como un archivo local.
                File file = new File(imagePath);
                if (file.exists()) {
                    this.backgroundImage = Toolkit.getDefaultToolkit().getImage(imagePath);
                } else {
                    System.err.println("Error de carga: Imagen no encontrada en ClassLoader ni como archivo local: " + imagePath);
                }
            }

            // Esperar a que la imagen cargue
            if (this.backgroundImage != null) {
                MediaTracker mt = new MediaTracker(this);
                mt.addImage(this.backgroundImage, 0);
                try {
                    mt.waitForID(0);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                if (this.backgroundImage.getWidth(this) == -1) {
                    System.err.println("Error: Imagen de fondo inválida/corrupta: " + imagePath);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Excepción general al cargar la imagen de fondo: " + imagePath);
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 1. Primero, pintamos el fondo (negro)
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            
            // 2. Definimos y aplicamos la opacidad (70%)
            AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, OPACITY);
            g2d.setComposite(alpha);
            
            // 3. Dibujamos la imagen con la opacidad aplicada
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            
            // 4. Liberamos los recursos
            g2d.dispose();
        }
    }
}

//Comentario de prueba c:
//12344