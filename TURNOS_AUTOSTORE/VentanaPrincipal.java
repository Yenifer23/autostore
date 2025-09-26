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
        
        // CAMBIO CLAVE: Borde más grueso y sin BevelBorder para un look moderno
        // Usamos un EmptyBorder interno más grande para dar espacio alrededor del texto
        btnAgendarNuevoTurno.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        btnAgendarNuevoTurno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(VentanaPrincipal.this, "Has hecho clic en AGENDAR NUEVO TURNO");
            }
        });
        
        // Quitamos el efecto de hundimiento ya que interfiere con el nuevo estilo visual
        // Si quieres un efecto, se puede reemplazar con un cambio de color del borde.
        btnAgendarNuevoTurno.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Efecto de brillo al pasar el mouse (hover)
                JPanel wrapper = (JPanel) ((JButton)evt.getSource()).getParent();
                wrapper.setBorder(BorderFactory.createLineBorder(COLOR_FLUOR_VERDE.brighter(), 7)); 
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Volver al color normal
                JPanel wrapper = (JPanel) ((JButton)evt.getSource()).getParent();
                wrapper.setBorder(BorderFactory.createLineBorder(COLOR_NEON_VERDE, 5));
            }
        });

        // Envolvemos el botón en un JPanel para darle el borde NEÓN REDONDEADO
        // Nota: JPanel no soporta bordes redondeados por defecto. Usaremos un JLabel para la simulación.
        JPanel panelBotonWrapper = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Pintamos un rectángulo negro redondeado como fondo del botón
                g.setColor(Color.BLACK);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Radio de esquina 30
                super.paintComponent(g);
            }
            @Override
            protected void paintBorder(Graphics g) {
                // Pintamos el borde NEÓN redondeado
                g.setColor(COLOR_NEON_VERDE);
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(5)); // Grosor del neón
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 30, 30);
            }
        };
        panelBotonWrapper.setOpaque(false);
        // Quitamos el setBorder estándar, ya que estamos pintando el borde en paintBorder()
        
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

// Clase BackgroundPanel revisada para una carga de recursos más robusta
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
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
                    setBackground(Color.DARK_GRAY); // Sigue mostrando DARK_GRAY si falla
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
                    setBackground(Color.DARK_GRAY);
                }
            } else {
                 setBackground(Color.DARK_GRAY);
            }
            
        } catch (Exception e) {
            System.err.println("Excepción general al cargar la imagen de fondo: " + imagePath);
            e.printStackTrace();
            setBackground(Color.DARK_GRAY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Asegura que el color de fondo sea el que definimos si la imagen falla
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}