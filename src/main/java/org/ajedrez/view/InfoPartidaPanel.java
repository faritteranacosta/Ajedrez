package org.ajedrez.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InfoPartidaPanel extends JPanel {
    private DefaultListModel<String> movimientosModel;
    private JList<String> listaMovimientos;

    private JLabel piezasBlancasCapturadas;
    private JLabel piezasNegrasCapturadas;

    private String capturasBlancas = "";
    private String capturasNegras = "";

    // Colores que combinan con el tablero de ajedrez
    private final Color COLOR_CLARO = new Color(240, 217, 181);  // Color de casillas claras
    private final Color COLOR_OSCURO = new Color(181, 136, 99);  // Color de casillas oscuras
    private final Color COLOR_FONDO = new Color(80, 58, 43);     // Marrón oscuro para fondo
    private final Color COLOR_TEXTO = new Color(240, 240, 240);  // Texto claro

    public InfoPartidaPanel() {
        setPreferredSize(new Dimension(280, 600));
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Título del panel
        JLabel titulo = new JLabel("INFORMACIÓN DE PARTIDA", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Lista de movimientos
        movimientosModel = new DefaultListModel<>();
        listaMovimientos = new JList<>(movimientosModel);
        listaMovimientos.setBackground(COLOR_CLARO);
        listaMovimientos.setForeground(Color.BLACK);
        listaMovimientos.setSelectionBackground(COLOR_OSCURO);
        listaMovimientos.setSelectionForeground(Color.WHITE);
        listaMovimientos.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scrollMovimientos = new JScrollPane(listaMovimientos);
        scrollMovimientos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COLOR_OSCURO, 2),
                        "Movimientos",
                        0, 0,
                        new Font("Arial", Font.BOLD, 14),
                        COLOR_TEXTO
                ),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollMovimientos.getViewport().setBackground(COLOR_CLARO);

        // Panel capturas
        JPanel capturasPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        capturasPanel.setBackground(COLOR_FONDO);
        capturasPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COLOR_OSCURO, 2),
                        "Piezas Capturadas",
                        0, 0,
                        new Font("Arial", Font.BOLD, 14),
                        COLOR_TEXTO
                ),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Panel para capturas blancas
        JPanel panelBlancas = new JPanel(new BorderLayout(5, 5));
        panelBlancas.setBackground(COLOR_CLARO);
        panelBlancas.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel lblBlancas = new JLabel("Blancas:");
        lblBlancas.setFont(new Font("Arial", Font.BOLD, 13));
        panelBlancas.add(lblBlancas, BorderLayout.WEST);

        piezasBlancasCapturadas = new JLabel("");
        piezasBlancasCapturadas.setFont(new Font("Segoe UI Chess", Font.PLAIN, 25));
        piezasBlancasCapturadas.setBackground(Color.WHITE);
        panelBlancas.add(piezasBlancasCapturadas, BorderLayout.CENTER);

        // Panel para capturas negras
        JPanel panelNegras = new JPanel(new BorderLayout(5, 5));
        panelNegras.setBackground(COLOR_OSCURO);
        panelNegras.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel lblNegras = new JLabel("Negras:");
        lblNegras.setFont(new Font("Arial", Font.BOLD, 13));
        lblNegras.setForeground(Color.WHITE);
        panelNegras.add(lblNegras, BorderLayout.WEST);

        piezasNegrasCapturadas = new JLabel("");
        piezasNegrasCapturadas.setFont(new Font("Segoe UI Chess", Font.PLAIN, 25));
        piezasNegrasCapturadas.setForeground(Color.BLACK);
        panelNegras.add(piezasNegrasCapturadas, BorderLayout.CENTER);

        capturasPanel.add(panelBlancas);
        capturasPanel.add(panelNegras);

        // Organizar componentes
        add(titulo, BorderLayout.NORTH);
        add(scrollMovimientos, BorderLayout.CENTER);
        add(capturasPanel, BorderLayout.SOUTH);
    }

    public void agregarMovimiento(String movimiento) {
        movimientosModel.addElement(movimiento);
        // Auto-scroll al último movimiento
        int lastIndex = movimientosModel.getSize() - 1;
        if (lastIndex >= 0) {
            listaMovimientos.ensureIndexIsVisible(lastIndex);
        }
    }

    public void agregarCaptura(String pieza, boolean capturadaPorBlancas) {
        if (capturadaPorBlancas) {
            capturasNegras += pieza + " ";
            piezasNegrasCapturadas.setText(capturasNegras);
        } else {
            capturasBlancas += pieza + " ";
            piezasBlancasCapturadas.setText(capturasBlancas);
        }
    }

    // Método para limpiar la información
    public void reiniciar() {
        movimientosModel.clear();
        capturasBlancas = "";
        capturasNegras = "";
        piezasBlancasCapturadas.setText("");
        piezasNegrasCapturadas.setText("");
    }
}