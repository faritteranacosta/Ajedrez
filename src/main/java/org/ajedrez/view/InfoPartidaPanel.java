package org.ajedrez.view;

import org.ajedrez.entity.Partida;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class InfoPartidaPanel extends JPanel {
    private JLabel piezasBlancasCapturadas;
    private JLabel piezasNegrasCapturadas;

    private String capturasBlancas = "";
    private String capturasNegras = "";

    private JLabel indicadorTurno;
    private JPanel panelTurno;
    private JLabel etiquetaEstado;

    private DefaultTableModel tableModel;
    private JTable tablaMovimientos;

    // Colores que combinan con el tablero de ajedrez
    private final Color COLOR_CLARO = new Color(240, 217, 181);
    private final Color COLOR_OSCURO = new Color(181, 136, 99);
    private final Color COLOR_FONDO = new Color(80, 58, 43);
    private final Color COLOR_TEXTO = new Color(240, 240, 240);
    private final Color COLOR_JAQUE = new Color(255, 100, 100);
    private final Color COLOR_MATE = new Color(200, 50, 50);
    private final Color COLOR_TABLAS = new Color(100, 150, 255);

    // Variables para control de movimientos
    private int index = 0;
    private int numeroMovimiento = 1;

    public InfoPartidaPanel() {
        tableModel = new DefaultTableModel(new String[]{"", "Blancas", "Negras"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaMovimientos = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                // Verificar si es una fila de evento especial
                String valor = getValueAt(row, 1).toString();
                boolean esEventoEspecial = valor.startsWith("¡") ||
                        valor.contains("Tablas") ||
                        valor.contains("Abandono");

                if (esEventoEspecial) {
                    if (valor.contains("JAQUE MATE") || valor.contains("Abandono")) {
                        c.setBackground(COLOR_MATE);
                        c.setForeground(Color.WHITE);
                    } else if (valor.contains("JAQUE")) {
                        c.setBackground(COLOR_JAQUE);
                        c.setForeground(Color.WHITE);
                    } else if (valor.contains("Tablas")) {
                        c.setBackground(COLOR_TABLAS);
                        c.setForeground(Color.WHITE);
                    }
                } else if (isRowSelected(row)) {
                    c.setBackground(getSelectionBackground());
                    c.setForeground(getSelectionForeground());
                } else {
                    if (row % 2 == 0) {
                        c.setBackground(COLOR_CLARO);
                    } else {
                        c.setBackground(COLOR_OSCURO);
                    }
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };

        setMinimumSize(new Dimension(250, 400)); // Tamaño mínimo
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_FONDO);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel superior (turno + estado)
        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        panelSuperior.setBackground(COLOR_FONDO);

        // Panel de turno
        panelTurno = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTurno.setBackground(COLOR_FONDO);
        panelTurno.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        indicadorTurno = new JLabel("Turno: BLANCAS ♔", SwingConstants.CENTER);
        indicadorTurno.setFont(new Font("Segoe UI Chess", Font.BOLD, 18));
        indicadorTurno.setForeground(COLOR_TEXTO);
        panelTurno.add(indicadorTurno);

        // Etiqueta de estado (jaque, mate, etc.)
        etiquetaEstado = new JLabel("", SwingConstants.CENTER);
        etiquetaEstado.setFont(new Font("Arial", Font.BOLD, 12));
        etiquetaEstado.setForeground(COLOR_JAQUE);
        etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));

        panelSuperior.add(panelTurno, BorderLayout.NORTH);
        panelSuperior.add(etiquetaEstado, BorderLayout.CENTER);

        // Personalizar la tabla
        personalizarTabla();

        JScrollPane scrollMovimientos = new JScrollPane(tablaMovimientos);
        scrollMovimientos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COLOR_OSCURO, 2),
                        "Movimientos y Eventos",
                        0, 0,
                        new Font("Segoe UI Chess", Font.BOLD, 14),
                        COLOR_TEXTO
                ),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollMovimientos.getViewport().setBackground(COLOR_CLARO);

        // Panel capturas
        JPanel capturasPanel = crearPanelCapturas();

        // Organizar componentes
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollMovimientos, BorderLayout.CENTER);
        add(capturasPanel, BorderLayout.SOUTH);
        tablaMovimientos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private void personalizarTabla() {
        tablaMovimientos.setBackground(COLOR_CLARO);
        tablaMovimientos.setSelectionBackground(COLOR_OSCURO);
        tablaMovimientos.setSelectionForeground(Color.WHITE);
        tablaMovimientos.setFont(new Font("Segoe UI Chess", Font.PLAIN, 12));
        tablaMovimientos.setRowHeight(25);

        // Header de la tabla
        tablaMovimientos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaMovimientos.getTableHeader().setBackground(COLOR_OSCURO);
        tablaMovimientos.getTableHeader().setForeground(Color.WHITE);
        tablaMovimientos.getTableHeader().setReorderingAllowed(false);

        // Centrar contenido
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tablaMovimientos.getColumnCount(); i++) {
            tablaMovimientos.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Ancho de columnas
        tablaMovimientos.getColumnModel().getColumn(0).setPreferredWidth(30);  // Número
        tablaMovimientos.getColumnModel().getColumn(1).setPreferredWidth(100); // Blancas
        tablaMovimientos.getColumnModel().getColumn(2).setPreferredWidth(100); // Negras
    }

    private JPanel crearPanelCapturas() {
        JPanel capturasPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        capturasPanel.setBackground(COLOR_FONDO);
        capturasPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(COLOR_OSCURO, 2),
                        "Piezas Capturadas",
                        0, 0,
                        new Font("Segoe UI Chess", Font.BOLD, 14),
                        COLOR_TEXTO
                ),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Panel para capturas blancas
        JPanel panelBlancas = new JPanel(new BorderLayout(5, 5));
        panelBlancas.setBackground(COLOR_CLARO);
        panelBlancas.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblBlancas = new JLabel("Blancas:");
        lblBlancas.setFont(new Font("Arial", Font.BOLD, 13));
        panelBlancas.add(lblBlancas, BorderLayout.WEST);

        piezasBlancasCapturadas = new JLabel("", SwingConstants.CENTER);
        piezasBlancasCapturadas.setFont(new Font("Segoe UI Chess", Font.PLAIN, 20));
        panelBlancas.add(piezasBlancasCapturadas, BorderLayout.CENTER);

        // Panel para capturas negras
        JPanel panelNegras = new JPanel(new BorderLayout(5, 5));
        panelNegras.setBackground(COLOR_OSCURO);
        panelNegras.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblNegras = new JLabel("Negras:");
        lblNegras.setFont(new Font("Arial", Font.BOLD, 13));
        lblNegras.setForeground(Color.WHITE);
        panelNegras.add(lblNegras, BorderLayout.WEST);

        piezasNegrasCapturadas = new JLabel("", SwingConstants.CENTER);
        piezasNegrasCapturadas.setFont(new Font("Segoe UI Chess", Font.PLAIN, 20));
        piezasNegrasCapturadas.setForeground(Color.BLACK);
        panelNegras.add(piezasNegrasCapturadas, BorderLayout.CENTER);

        capturasPanel.add(panelBlancas);
        capturasPanel.add(panelNegras);

        return capturasPanel;
    }

    // ========== MÉTODOS PÚBLICOS PARA EVENTOS ==========

    public void actualizarTurno(org.ajedrez.entity.Color turno) {
        String texto = "Turno: " + (turno == org.ajedrez.entity.Color.WHITE ? "BLANCAS ♔" : "NEGRAS ♚");
        indicadorTurno.setText(texto);

        if (turno == org.ajedrez.entity.Color.WHITE) {
            panelTurno.setBackground(COLOR_CLARO);
            indicadorTurno.setForeground(Color.BLACK);
        } else {
            panelTurno.setBackground(new Color(60, 60, 60));
            indicadorTurno.setForeground(Color.WHITE);
        }
    }

    public void mostrarEstado(String estado, boolean esUrgente) {
        etiquetaEstado.setText(estado);
        if (esUrgente) {
            etiquetaEstado.setForeground(COLOR_JAQUE);
            etiquetaEstado.setFont(new Font("Arial", Font.BOLD, 13));
        } else {
            etiquetaEstado.setForeground(COLOR_TEXTO);
            etiquetaEstado.setFont(new Font("Arial", Font.PLAIN, 12));
        }
    }

    public void limpiarEstado() {
        etiquetaEstado.setText("");
    }

    public void agregarMovimiento(String movimiento) {
        if (index == 0) {
            // Nuevo número de movimiento
            String[] nuevaFila = {numeroMovimiento + ".", movimiento, ""};
            tableModel.addRow(nuevaFila);
            index = 1;
        } else {
            // Completar movimiento de negras
            int ultimaFila = tableModel.getRowCount() - 1;
            tableModel.setValueAt(movimiento, ultimaFila, 2);
            index = 0;
            numeroMovimiento++;
        }
        autoScroll();
    }

    // ========== EVENTOS ESPECIALES ==========

    public void agregarJaque() {
        agregarEventoEspecial("¡JAQUE!", COLOR_JAQUE);
        mostrarEstado("¡Rey en jaque!", true);
    }

    public void agregarJaqueMate(org.ajedrez.entity.Color ganador) {
        String mensaje = "¡JAQUE MATE! Ganaron las " +
                (ganador == org.ajedrez.entity.Color.WHITE ? "blancas" : "negras");
        agregarEventoEspecial(mensaje, COLOR_MATE);
        mostrarEstado("¡Jaque Mate! Partida terminada", true);
    }

    public void agregarAhogado() {
        agregarEventoEspecial("¡TABLAS! Rey ahogado", COLOR_TABLAS);
        mostrarEstado("Tablas por ahogado", true);
    }

    public void agregarTablasMaterial() {
        agregarEventoEspecial("Tablas - Material insuficiente", COLOR_TABLAS);
        mostrarEstado("Tablas por material insuficiente", false);
    }

    public void agregarTablas50Movimientos() {
        agregarEventoEspecial("Tablas - Regla de 50 movimientos", COLOR_TABLAS);
        mostrarEstado("Tablas por regla de 50 movimientos", false);
    }

    public void agregarTablasAcuerdo() {
        agregarEventoEspecial("Tablas - Acuerdo mutuo", COLOR_TABLAS);
        mostrarEstado("Tablas por acuerdo", false);
    }

    public void agregarAbandono(org.ajedrez.entity.Color abandona) {
        String ganador = (abandona == org.ajedrez.entity.Color.WHITE) ? "negras" : "blancas";
        String mensaje = "Abandono - Ganaron las " + ganador;
        agregarEventoEspecial(mensaje, COLOR_MATE);
        mostrarEstado("Partida abandonada", true);
    }

    public void agregarPromocion(String pieza) {
        agregarEventoEspecial("Promoción a " + pieza, new Color(150, 200, 100));
    }

    public void agregarEnroque(boolean esCorto) {
        String tipo = esCorto ? "corto" : "largo";
        agregarEventoEspecial("Enroque " + tipo, new Color(100, 150, 200));
    }

    public void agregarCapturaEspecial(String piezaCapturada) {
        agregarEventoEspecial("Captura de " + piezaCapturada, new Color(200, 150, 100));
    }

    // ========== MÉTODO PRIVADO PARA EVENTOS ==========

    public void agregarEventoEspecial(String evento, Color colorFondo) {
        String[] filaEvento = {"", evento, ""};
        tableModel.addRow(filaEvento);

        // Marcar la última fila como evento especial
        int ultimaFila = tableModel.getRowCount() - 1;
        tablaMovimientos.repaint();

        autoScroll();
    }

    public void agregarCaptura(String pieza, boolean capturadaPorBlancas) {
        if (capturadaPorBlancas) {
            capturasNegras += pieza;
            piezasNegrasCapturadas.setText(capturasNegras);
        } else {
            capturasBlancas += pieza;
            piezasBlancasCapturadas.setText(capturasBlancas);
        }
    }

    public void reiniciarPartida() {
        // Limpiar tabla
        tableModel.setRowCount(0);

        // Reiniciar contadores
        index = 0;
        numeroMovimiento = 1;

        // Limpiar capturas
        capturasBlancas = "";
        capturasNegras = "";
        piezasBlancasCapturadas.setText("");
        piezasNegrasCapturadas.setText("");

        // Reiniciar UI
        actualizarTurno(org.ajedrez.entity.Color.WHITE);
        limpiarEstado();
    }

    private void autoScroll() {
        SwingUtilities.invokeLater(() -> {
            int lastRow = tablaMovimientos.getRowCount() - 1;
            if (lastRow >= 0) {
                tablaMovimientos.scrollRectToVisible(tablaMovimientos.getCellRect(lastRow, 0, true));
            }
        });
    }


    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        ajustarFuentes();
    }

    private void ajustarFuentes() {
        int height = getHeight();
        int baseFontSize = Math.max(10, height / 40);

        if (indicadorTurno != null) {
            indicadorTurno.setFont(new Font("Segoe UI Chess", Font.BOLD, baseFontSize + 4));
        }

        if (etiquetaEstado != null) {
            etiquetaEstado.setFont(new Font("Arial", Font.PLAIN, baseFontSize));
        }

        if (tablaMovimientos != null) {
            tablaMovimientos.setFont(new Font("Arial", Font.PLAIN, baseFontSize - 1));
            tablaMovimientos.setRowHeight(baseFontSize + 10);
        }

        if (piezasBlancasCapturadas != null && piezasNegrasCapturadas != null) {
            int capturasFontSize = Math.max(16, baseFontSize + 4);
            piezasBlancasCapturadas.setFont(new Font("Segoe UI Chess", Font.PLAIN, capturasFontSize));
            piezasNegrasCapturadas.setFont(new Font("Segoe UI Chess", Font.PLAIN, capturasFontSize));
        }
    }
}