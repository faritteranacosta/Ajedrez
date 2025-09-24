package org.ajedrez.view;

import org.ajedrez.entity.*;
import org.ajedrez.entity.pieza.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

class TableroGUI extends JPanel {
    private static final int SIZE = 8; // 8x8

    private int cellSize;

    private Partida partida;
    private Tablero tablero;
    private Posicion seleccionada = null;
    private List<Movimiento> movimientosPosibles = new ArrayList<>();
    private InfoPartidaPanel infoPanel;

    // Colores del tablero
    private final Color COLOR_CLARO = new Color(240, 217, 181);
    private final Color COLOR_OSCURO = new Color(181, 136, 99);
    private final Color COLOR_SELECCION = new Color(255, 255, 0, 100);
    private final Color COLOR_MOVIMIENTOS = new Color(144, 238, 144, 180);
    private final Color COLOR_MOVIMIENTOS_CAPTURA = new Color(255, 99, 71, 180);

    public void setInfoPanel(InfoPartidaPanel infoPanel) {
        this.infoPanel = infoPanel;
    }

    public TableroGUI() {
        setPreferredSize(new Dimension(540, 540));
        setBorder(BorderFactory.createLineBorder(new Color(80, 58, 43), 4));

        cellSize = Math.min(getWidth() / SIZE, getHeight() / SIZE);

        // Crear jugadores y partida
        Jugador jugadorBlanco = new Jugador("Player 1", org.ajedrez.entity.Color.WHITE);
        Jugador jugadorNegro = new Jugador("Player 2", org.ajedrez.entity.Color.BLACK);

        this.partida = new Partida(jugadorBlanco, jugadorNegro);
        this.tablero = partida.getTablero();

        // Inicializar tablero con piezas
        tablero.inicializarTablero();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (partida.isJuegoTerminado()) {
                    mostrarResultadoPartida();
                    return;
                }

                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int currentCellSize = Math.min(panelWidth / SIZE, panelHeight / SIZE);
                int offsetX = (panelWidth - (SIZE * currentCellSize)) / 2;
                int offsetY = (panelHeight - (SIZE * currentCellSize)) / 2;

                // Ajustar coordenadas del clic
                int col = (e.getX() - offsetX) / currentCellSize;
                int fila = (e.getY() - offsetY) / currentCellSize;

                // Verificar que el clic está dentro del tablero
                if (fila < 0 || fila >= SIZE || col < 0 || col >= SIZE) {
                    return;
                }

                Posicion clic = new Posicion(fila, col);

                if (seleccionada == null) {
                    // Fase de selección de pieza
                    Pieza pieza = tablero.getPieza(clic);
                    if (pieza != null && pieza.getColor() == partida.getTurnoActual()) {
                        seleccionada = clic;
                        movimientosPosibles = partida.obtenerMovimientosLegales(pieza);

                        // Mostrar mensaje si el rey está en jaque
                        if (partida.estaEnJaque(partida.getTurnoActual()) && infoPanel != null) {
                            infoPanel.mostrarEstado("¡Rey en jaque! Debes protegerlo", true);
                        }
                    }
                } else {
                    // Fase de movimiento
                    boolean movimientoValido = false;
                    for (Movimiento mov : movimientosPosibles) {
                        if (mov.getDestino().equals(clic)) {
                            movimientoValido = true;
                            break;
                        }
                    }

                    if (!movimientoValido) {
                        // Movimiento inválido
                        seleccionada = null;
                        movimientosPosibles.clear();
                        repaint();
                        return;
                    }

                    Pieza pieza = tablero.getPieza(seleccionada);
                    String notacion = obtenerNotacionAlgebraica(seleccionada, clic);

                    // Verificar si es enroque para el evento especial
                    boolean esEnroque = false;
                    boolean esEnroqueCorto = false;
                    if (pieza instanceof Rey && Math.abs(clic.getColumna() - seleccionada.getColumna()) == 2) {
                        esEnroque = true;
                        esEnroqueCorto = (clic.getColumna() == 6);
                    }

                    // Procesar movimiento según tipo de pieza
                    boolean movimientoRealizado = false;

                    // Promoción de peón
                    if (pieza instanceof Peon &&
                            ((pieza.getColor() == org.ajedrez.entity.Color.WHITE && clic.getFila() == 0) ||
                                    (pieza.getColor() == org.ajedrez.entity.Color.BLACK && clic.getFila() == 7))) {

                        Pieza promocion = elegirPromocion(pieza.getColor(), clic);
                        movimientoRealizado = partida.moverPieza(seleccionada, clic, promocion);

                        if (movimientoRealizado && infoPanel != null) {
                            infoPanel.agregarPromocion(getSimboloPieza(promocion));
                        }
                    } else {
                        // Movimiento normal
                        movimientoRealizado = partida.moverPieza(seleccionada, clic, null);
                    }

                    if (movimientoRealizado) {
                        // AGREGAR EVENTOS AL PANEL DE INFORMACIÓN
                        if (infoPanel != null) {
                            // 1. Movimiento normal
                            infoPanel.agregarMovimiento(notacion);

                            // 2. Captura
                            Pieza capturada = tablero.getUltimaCaptura();
                            if (capturada != null) {
                                String simbolo = getSimboloPieza(capturada);
                                boolean capturadaPorBlancas = (capturada.getColor() == org.ajedrez.entity.Color.BLACK);
                                infoPanel.agregarCaptura(simbolo, capturadaPorBlancas);
                                infoPanel.agregarCapturaEspecial(getSimboloPieza(capturada));
                                tablero.nullUltimaCapturada();
                            }

                            // 3. Enroque
                            if (esEnroque) {
                                infoPanel.agregarEnroque(esEnroqueCorto);
                            }

                            // 4. Estado del juego después del movimiento
                            org.ajedrez.entity.Color turnoAnterior = (partida.getTurnoActual() == org.ajedrez.entity.Color.WHITE) ?
                                    org.ajedrez.entity.Color.BLACK : org.ajedrez.entity.Color.WHITE;
                            org.ajedrez.entity.Color siguienteTurno = partida.getTurnoActual();

                            // Verificar jaque mate
                            if (partida.estaEnJaqueMate(siguienteTurno)) {
                                infoPanel.agregarJaqueMate(turnoAnterior);
                            }
                            // Verificar ahogado
                            else if (partida.estaAhogado(siguienteTurno)) {
                                infoPanel.agregarAhogado();
                            }
                            // Verificar jaque simple
                            else if (partida.estaEnJaque(siguienteTurno)) {
                                infoPanel.agregarJaque();
                            } else {
                                infoPanel.limpiarEstado();
                            }

                            // 5. Actualizar turno
                            infoPanel.actualizarTurno(partida.getTurnoActual());

                            // 6. Verificar finales de tablas automáticos
                            if (partida.isJuegoTerminado() && partida.getResultado() != null) {
                                switch (partida.getResultado()) {
                                    case "TABLAS_MATERIAL_INSUFICIENTE":
                                        infoPanel.agregarTablasMaterial();
                                        break;
                                    case "TABLAS_50_MOVIMIENTOS":
                                        infoPanel.agregarTablas50Movimientos();
                                        break;
                                    case "TABLAS_TRIPLE_REPETICION":
                                        infoPanel.agregarEventoEspecial("Tablas - Triple repetición", COLOR_CLARO);
                                        break;
                                }
                            }
                        }
                    } else {
                        // Movimiento fallido - mostrar mensaje de error
                        if (infoPanel != null) {
                            infoPanel.mostrarEstado("Movimiento no válido", true);
                            // Limpiar después de 2 segundos
                            new Timer(2000, evt -> {
                                infoPanel.limpiarEstado();
                                ((Timer)evt.getSource()).stop();
                            }).start();
                        }
                    }

                    seleccionada = null;
                    movimientosPosibles.clear();
                }
                repaint();
            }
        });
    }

    // Método auxiliar para mostrar resultado final
    private void mostrarResultadoPartida() {
        String resultado = partida.getDescripcionResultado();
        int tipoMensaje = JOptionPane.INFORMATION_MESSAGE;

        if (resultado.contains("Jaque Mate") || resultado.contains("Abandono")) {
            tipoMensaje = JOptionPane.WARNING_MESSAGE;
        }

        JOptionPane.showMessageDialog(this, resultado, "Fin de la Partida", tipoMensaje);

        // Opción para nueva partida
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Deseas comenzar una nueva partida?",
                "Nueva Partida",
                JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            reiniciarPartida();
            if (infoPanel != null) {
                infoPanel.reiniciarPartida();
                partida.reiniciar();
            }
        }
    }

    // Método auxiliar para agregar eventos genéricos (si necesitas más flexibilidad)
    private void agregarEventoInfoPanel(String evento) {
        if (infoPanel != null) {
            // Buscar método específico o usar genérico
            if (evento.contains("JAQUE MATE")) {
                org.ajedrez.entity.Color ganador = evento.contains("blancas") ? org.ajedrez.entity.Color.WHITE : org.ajedrez.entity.Color.BLACK;
                infoPanel.agregarJaqueMate(ganador);
            } else if (evento.contains("JAQUE")) {
                infoPanel.agregarJaque();
            } else if (evento.contains("Tablas")) {
                infoPanel.agregarTablasAcuerdo();
            } else {
                infoPanel.agregarEventoEspecial(evento, COLOR_CLARO);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Calcular cellSize basado en el tamaño actual del panel
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        cellSize = Math.min(panelWidth / SIZE, panelHeight / SIZE);

        // Centrar el tablero
        int offsetX = (panelWidth - (SIZE * cellSize)) / 2;
        int offsetY = (panelHeight - (SIZE * cellSize)) / 2;

        // Pintar casillas
        for (int fila = 0; fila < SIZE; fila++) {
            for (int col = 0; col < SIZE; col++) {
                boolean esClaro = (fila + col) % 2 == 0;
                g2d.setColor(esClaro ? COLOR_CLARO : COLOR_OSCURO);
                g2d.fillRect(offsetX + col * cellSize, offsetY + fila * cellSize, cellSize, cellSize);
            }
        }

        // Dibujar coordenadas
        dibujarCoordenadas(g2d, offsetX, offsetY);

        // Resaltar movimientos posibles
        for (Movimiento mov : movimientosPosibles) {
            int x = offsetX + mov.getDestino().getColumna() * cellSize;
            int y = offsetY + mov.getDestino().getFila() * cellSize;

            Pieza piezaDestino = tablero.getPieza(mov.getDestino());
            if (piezaDestino != null && piezaDestino.getColor() != partida.getTurnoActual()) {
                g2d.setColor(COLOR_MOVIMIENTOS_CAPTURA);
                g2d.fillRect(x, y, cellSize, cellSize);
            } else {
                g2d.setColor(COLOR_MOVIMIENTOS);
                g2d.fillOval(x + cellSize / 4, y + cellSize / 4, cellSize / 2, cellSize / 2);
            }
        }

        // Resaltar casilla seleccionada
        if (seleccionada != null) {
            g2d.setColor(COLOR_SELECCION);
            g2d.fillRect(offsetX + seleccionada.getColumna() * cellSize,
                    offsetY + seleccionada.getFila() * cellSize,
                    cellSize, cellSize);
        }

        // Dibujar piezas
        for (int fila = 0; fila < SIZE; fila++) {
            for (int col = 0; col < SIZE; col++) {
                Pieza pieza = tablero.getPieza(new Posicion(fila, col));
                if (pieza != null) {
                    dibujarPieza(g2d, pieza, fila, col, offsetX, offsetY);
                }
            }
        }

        if (partida.isJuegoTerminado()) {
            dibujarFinPartida(g2d);
        }
    }

    private void dibujarFinPartida(Graphics2D g2d) {
        // Fondo semitransparente
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Mensaje centrado
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI Chess", Font.BOLD, 24));
        String mensaje = partida.getDescripcionResultado();

        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(mensaje)) / 2;
        int y = getHeight() / 2;

        g2d.drawString(mensaje, x, y);

        // Instrucciones para nueva partida
        g2d.setFont(new Font("Segoe UI Chess", Font.PLAIN, 14));
        String instruccion = "Haz clic para comenzar una nueva partida";
        x = (getWidth() - fm.stringWidth(instruccion)) / 2;
        g2d.drawString(instruccion, x, y + 30);
    }

    private void dibujarCoordenadas(Graphics2D g2d, int offsetX, int offsetY) {
        g2d.setFont(new Font("Arial", Font.PLAIN, Math.max(10, cellSize / 7)));
        g2d.setColor(Color.DARK_GRAY);

        // Coordenadas de letras (a-h)
        for (int col = 0; col < SIZE; col++) {
            String letra = String.valueOf((char)('a' + col));
            g2d.drawString(letra,
                    offsetX + col * cellSize + cellSize / 2 -3,
                    offsetY + SIZE * cellSize - 10);
        }

        // Coordenadas de números (1-8)
        for (int fila = 0; fila < SIZE; fila++) {
            String numero = String.valueOf(8 - fila);
            g2d.drawString(numero,
                    offsetX + 5,
                    offsetY + fila * cellSize + cellSize / 2 );
        }
    }

    private void dibujarPieza(Graphics2D g2d, Pieza pieza, int fila, int col, int offsetX, int offsetY) {
        String simbolo = getSimboloPieza(pieza);
        boolean esBlanca = pieza.getColor() == org.ajedrez.entity.Color.WHITE;

        // Tamaño de fuente responsive
        int fontSize = Math.max(20, (int)(cellSize * 0.6));
        g2d.setFont(new Font("Segoe UI Chess", Font.PLAIN, fontSize));

        int x = offsetX + col * cellSize;
        int y = offsetY + fila * cellSize;

        // Sombra
        g2d.setColor(esBlanca ? new Color(0, 0, 0, 30) : new Color(255, 255, 255, 30));
        g2d.drawString(simbolo, x + cellSize/4, y + cellSize*3/4);

        // Pieza principal
        g2d.setColor(esBlanca ? Color.WHITE : Color.BLACK);
        g2d.drawString(simbolo, (x + cellSize/4 - 2)-2, y + cellSize*3/4 - 2);
    }

    private String getSimboloPieza(Pieza pieza) {
        if (pieza instanceof Rey) return pieza.getColor() == org.ajedrez.entity.Color.WHITE ? "♔" : "♚";
        if (pieza instanceof Dama) return pieza.getColor() == org.ajedrez.entity.Color.WHITE ? "♕" : "♛";
        if (pieza instanceof Torre) return pieza.getColor() == org.ajedrez.entity.Color.WHITE ? "♖" : "♜";
        if (pieza instanceof Alfil) return pieza.getColor() == org.ajedrez.entity.Color.WHITE ? "♗" : "♝";
        if (pieza instanceof Caballo) return pieza.getColor() == org.ajedrez.entity.Color.WHITE ? "♘" : "♞";
        if (pieza instanceof Peon) return pieza.getColor() == org.ajedrez.entity.Color.WHITE ? "♙" : "♟";
        return "?";
    }



    private String obtenerNotacionAlgebraica(Posicion origen, Posicion destino) {
        Pieza pieza = tablero.getPieza(origen);
        StringBuilder notacion = new StringBuilder();

        if (pieza instanceof Rey) {
            int diferenciaCol = destino.getColumna() - origen.getColumna();
            if (diferenciaCol == 2) {
                return "O-O";     // Enroque corto
            } else if (diferenciaCol == -2) {
                return "O-O-O";   // Enroque largo
            }
        }

        // Pieza (excepto peones)
        if (!(pieza instanceof Peon)) {
            notacion.append(getSimboloPieza(pieza));

        }

        // Captura
        Pieza piezaDestino = tablero.getPieza(destino);
        boolean esCaptura = (piezaDestino != null);

        if (esCaptura) {
            if (pieza instanceof Peon) {
                // Peones: columna de origen + x
                notacion.append((char)('a' + origen.getColumna()));
            }
            notacion.append("x");
        }

        // Casilla de destino
        notacion.append((char)('a' + destino.getColumna()));
        notacion.append(8 - destino.getFila());

        return notacion.toString();
    }


    public void reiniciarPartida() {
        partida.reiniciar();
        seleccionada = null;
        movimientosPosibles.clear();
        repaint();
    }

    private Pieza elegirPromocion(org.ajedrez.entity.Color color, Posicion destino) {
        // Colores consistentes con el diseño
        Color COLOR_FONDO = new Color(80, 58, 43);
        Color COLOR_CLARO = new Color(240, 217, 181);
        Color COLOR_OSCURO = new Color(181, 136, 99);
        Color COLOR_TEXTO = new Color(240, 240, 240);
        Color COLOR_BOTON = new Color(120, 90, 70);
        Color COLOR_BOTON_HOVER = new Color(150, 110, 85);

        // Crear un panel personalizado
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel("Elige una pieza para la promoción:", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI Chess", Font.BOLD, 16));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 4, 10, 0));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Símbolos Unicode de las piezas
        String[] simbolos = {"♕", "♖", "♗", "♘"};
        String[] nombres = {"Dama", "Torre", "Alfil", "Caballo"};

        JButton[] botones = new JButton[4];
        int[] seleccion = new int[1]; // Array para capturar la selección

        for (int i = 0; i < 4; i++) {
            botones[i] = new JButton("<html><center>" + simbolos[i] + "<br>" + nombres[i] + "</center></html>");
            botones[i].setFont(new Font("Segoe UI", Font.BOLD, 24));
            botones[i].setBackground(COLOR_BOTON);
            botones[i].setForeground(COLOR_TEXTO);
            botones[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_OSCURO, 2),
                    BorderFactory.createEmptyBorder(15, 10, 15, 10)
            ));
            botones[i].setFocusPainted(false);
            botones[i].setContentAreaFilled(true);
            botones[i].setOpaque(true);

            // Efecto hover
            botones[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((JButton) e.getSource()).setBackground(COLOR_BOTON_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((JButton) e.getSource()).setBackground(COLOR_BOTON);
                }
            });

            final int index = i;
            botones[i].addActionListener(e -> {
                seleccion[0] = index;
                // Cerrar el diálogo
                Window window = SwingUtilities.getWindowAncestor(panel);
                if (window != null) {
                    window.dispose();
                }
            });

            panelBotones.add(botones[i]);
        }

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(panelBotones, BorderLayout.CENTER);

        // Crear el diálogo personalizado
        JDialog dialog = new JDialog((Frame) null, "Promoción de Peón", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.getContentPane().setBackground(COLOR_FONDO);
        dialog.setResizable(false);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this); // Centrar respecto al tablero

        // Mostrar el diálogo (bloqueante)
        dialog.setVisible(true);

        // Retornar la pieza seleccionada
        switch (seleccion[0]) {
            case 1: return new Torre(color, destino);
            case 2: return new Alfil(color, destino);
            case 3: return new Caballo(color, destino);
            default: return new Dama(color, destino); // Por defecto Dama (0)
        }
    }


}