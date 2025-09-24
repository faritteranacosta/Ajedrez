package org.ajedrez.view;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AjedrezGUI extends JFrame {
    private TableroGUI tableroPanel;
    private InfoPartidaPanel infoPanel;
    private JPanel panelBotones;

    public AjedrezGUI() {
        ImageIcon imagen = new ImageIcon(getClass().getResource("/piezas/icon(2).png"));

        setTitle("Juego de Ajedrez");
        setIconImage(imagen.getImage());

        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600)); // Tamaño mínimo de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Usar BorderLayout con pesos para distribución responsive
        setLayout(new BorderLayout());

        // Crear componentes
        tableroPanel = new TableroGUI();
        infoPanel = new InfoPartidaPanel();
        panelBotones = crearPanelBotones();

        // Conectar tablero con panel de info
        tableroPanel.setInfoPanel(infoPanel);

        // Panel derecho que contendrá infoPanel y botones
        JPanel panelDerecho = new JPanel(new BorderLayout());

        // Usar BoxLayout vertical para mejor distribución
        JPanel panelContenidoDerecho = new JPanel();
        panelContenidoDerecho.setLayout(new BoxLayout(panelContenidoDerecho, BoxLayout.Y_AXIS));
        panelContenidoDerecho.setBackground(new Color(80, 58, 43));

        // InfoPanel con peso
        panelContenidoDerecho.add(infoPanel);

        // Espacio flexible entre infoPanel y botones
        panelContenidoDerecho.add(Box.createVerticalStrut(10));

        // Panel de botones con tamaño preferido
        panelBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panelContenidoDerecho.add(panelBotones);

        panelDerecho.add(panelContenidoDerecho, BorderLayout.CENTER);

        // Usar JSplitPane para redimensionamiento interactivo
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableroPanel, panelDerecho);
        splitPane.setDividerLocation(600); // Posición inicial del divisor
        splitPane.setResizeWeight(0.7); // 70% para el tablero, 30% para el panel derecho
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(8);

        // Color del divisor que combine con el diseño
        splitPane.setBackground(new Color(80, 58, 43));

        add(splitPane, BorderLayout.CENTER);

        // Añadir listener para redimensionamiento
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Asegurar que el divisor mantenga proporciones razonables
                int width = getWidth();
                if (width > 0) {
                    splitPane.setDividerLocation(0.7); // Mantener 70% para tablero
                }
            }
        });
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(80, 58, 43));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); // Altura máxima

        // Botones con tamaño flexible
        JButton btnReiniciar = new JButton("Nueva Partida");
        estiloBoton(btnReiniciar, new Color(120, 90, 70));
        btnReiniciar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            // Action Listeners
        btnReiniciar.addActionListener(e -> reiniciarPartida());
        panel.add(btnReiniciar);

        return panel;
    }

    private void estiloBoton(JButton boton, Color colorFondo) {
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 217, 181), 1),
                BorderFactory.createEmptyBorder(8, 5, 8, 5)
        ));

        // Hacer botones más flexibles
        boton.setPreferredSize(new Dimension(120, 35));
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo);
            }
        });
    }



    private void reiniciarPartida() {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Comenzar una nueva partida? Se perderá la partida actual.",
                "Nueva Partida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            tableroPanel.reiniciarPartida();
            infoPanel.reiniciarPartida();
            JOptionPane.showMessageDialog(this,
                    "¡Nueva partida comenzada!",
                    "Partida Reiniciada",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AjedrezGUI().setVisible(true);
        });
    }
}