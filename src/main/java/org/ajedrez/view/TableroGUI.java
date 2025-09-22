package org.ajedrez.view;

import org.ajedrez.entity.*;
import org.ajedrez.entity.pieza.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class TableroGUI extends JPanel {
    private static final int SIZE = 8; // 8x8
    private static final int CELL_SIZE = 70;

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
        setPreferredSize(new Dimension(SIZE * CELL_SIZE, SIZE * CELL_SIZE));
        setBorder(BorderFactory.createLineBorder(new Color(80, 58, 43), 4));

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
                int col = e.getX() / CELL_SIZE;
                int fila = e.getY() / CELL_SIZE;

                // Verificar que el clic está dentro del tablero
                if (fila < 0 || fila >= SIZE || col < 0 || col >= SIZE) {
                    return;
                }

                Posicion clic = new Posicion(fila, col);

                if (seleccionada == null) {
                    Pieza pieza = tablero.getPieza(clic);
                    if (pieza != null && pieza.getColor() == partida.getTurnoActual()) {
                        seleccionada = clic;
                        movimientosPosibles = pieza.movimientosPosibles(tablero);
                    }
                } else {

                    boolean movimientoValido = false;
                    for (Movimiento mov : movimientosPosibles) {
                        if (mov.getDestino().equals(clic)) {
                            movimientoValido = true;
                            break;
                        }
                    }

                    String notacion = obtenerNotacionAlgebraica(seleccionada, clic);
                    Pieza pieza = tablero.getPieza(seleccionada);
                    if (infoPanel != null) {
                        if(movimientoValido){
                            infoPanel.agregarMovimiento(notacion);
                            Pieza capturada = tablero.getUltimaCaptura();
                            if (capturada != null) {
                                String simbolo = getSimboloPieza(capturada);
                                boolean capturadaPorBlancas = (capturada.getColor() == org.ajedrez.entity.Color.BLACK);
                                infoPanel.agregarCaptura(simbolo, capturadaPorBlancas);
                                tablero.nullUltimaCapturada();
                            }
                        }
                    }
                    // Si es peón y llega a la última fila → promoción
                    if (pieza instanceof Peon &&
                            ((pieza.getColor() == org.ajedrez.entity.Color.WHITE && clic.getFila() == 0) ||
                                    (pieza.getColor() == org.ajedrez.entity.Color.BLACK && clic.getFila() == 7))) {

                        Pieza promocion = elegirPromocion(pieza.getColor(), clic);
                        partida.moverPieza(seleccionada, clic, promocion);
                        if (infoPanel != null) {
                            infoPanel.actualizarTurno(partida.getTurnoActual());
                        }

                    } else {
                        partida.moverPieza(seleccionada, clic, null);
                        if (infoPanel != null) {
                            infoPanel.actualizarTurno(partida.getTurnoActual());
                        }
                    }

                    seleccionada = null;
                    movimientosPosibles.clear();
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Pintar casillas
        for (int fila = 0; fila < SIZE; fila++) {
            for (int col = 0; col < SIZE; col++) {
                boolean esClaro = (fila + col) % 2 == 0;
                g2d.setColor(esClaro ? COLOR_CLARO : COLOR_OSCURO);
                g2d.fillRect(col * CELL_SIZE, fila * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // Dibujar coordenadas
        dibujarCoordenadas(g2d);

        // Resaltar movimientos posibles
        for (Movimiento mov : movimientosPosibles) {
            int x = mov.getDestino().getColumna() * CELL_SIZE;
            int y = mov.getDestino().getFila() * CELL_SIZE;

            // Diferente color para movimientos de captura
            Pieza piezaDestino = tablero.getPieza(mov.getDestino());
            if (piezaDestino != null && piezaDestino.getColor() != partida.getTurnoActual()) {
                g2d.setColor(COLOR_MOVIMIENTOS_CAPTURA);
                g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            } else {
                g2d.setColor(COLOR_MOVIMIENTOS);
                g2d.fillOval(x + CELL_SIZE / 4, y + CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);
            }
        }

        // Resaltar casilla seleccionada
        if (seleccionada != null) {
            g2d.setColor(COLOR_SELECCION);
            g2d.fillRect(seleccionada.getColumna() * CELL_SIZE,
                    seleccionada.getFila() * CELL_SIZE,
                    CELL_SIZE, CELL_SIZE);
        }

        // Dibujar piezas
        for (int fila = 0; fila < SIZE; fila++) {
            for (int col = 0; col < SIZE; col++) {
                Pieza pieza = tablero.getPieza(new Posicion(fila, col));
                if (pieza != null) {
                    dibujarPieza(g2d, pieza, fila, col);
                }
            }
        }
    }

    private void dibujarCoordenadas(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(Color.DARK_GRAY);

        // Coordenadas de letras (a-h) en la parte inferior
        for (int col = 0; col < SIZE; col++) {
            String letra = String.valueOf((char)('a' + col));
            g2d.drawString(letra,
                    col * CELL_SIZE + CELL_SIZE / 2 - 5,
                    SIZE * CELL_SIZE - 5);
        }

        // Coordenadas de números (1-8) en el lado izquierdo
        for (int fila = 0; fila < SIZE; fila++) {
            String numero = String.valueOf(8 - fila);
            g2d.drawString(numero,
                    5,
                    fila * CELL_SIZE + CELL_SIZE / 2 + 5);
        }
    }

    private void dibujarPieza(Graphics2D g2d, Pieza pieza, int fila, int col) {
        String simbolo = getSimboloPieza(pieza);
        boolean esBlanca = pieza.getColor() == org.ajedrez.entity.Color.WHITE;


        // Sombra para mejor contraste
        g2d.setFont(new Font("Segoe UI Chess", Font.PLAIN, 45));
        g2d.setColor(esBlanca ? new Color(0, 0, 0, 30) : new Color(255, 255, 255, 30));
        g2d.drawString(simbolo, col * CELL_SIZE + 15, fila * CELL_SIZE + 50);

        // Pieza principal
        g2d.setColor(esBlanca ? Color.WHITE : Color.BLACK);
        g2d.drawString(simbolo, col * CELL_SIZE + 10, fila * CELL_SIZE + 50);
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

        // Pieza (excepto peones)
        if (!(pieza instanceof Peon)) {
            notacion.append(getLetraPieza(pieza));

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

    private String getLetraPieza(Pieza pieza) {
        if (pieza instanceof Rey) return "K";
        if (pieza instanceof Dama) return "Q";
        if (pieza instanceof Torre) return "R";
        if (pieza instanceof Alfil) return "B";
        if (pieza instanceof Caballo) return "N";
        if (pieza instanceof Peon) return "E";
        return "";
    }

    private void dibujarPiezaConImagen(Graphics2D g2d, Pieza pieza, int fila, int col) {
        String nombreImagen = getNombreImagenPieza(pieza);

        try {
            Image imagen = ImageIO.read(getClass().getResource("/piezas/" + nombreImagen));
            int nuevoAncho = (int) (CELL_SIZE * 0.8);
            int nuevoAlto = (int) (CELL_SIZE * 0.8);

            Image imagenRedimensionada = imagen.getScaledInstance(
                    nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);

            int x = col * CELL_SIZE + (CELL_SIZE - imagenRedimensionada.getWidth(null)) / 2;
            int y = fila * CELL_SIZE + (CELL_SIZE - imagenRedimensionada.getHeight(null)) / 2;
            g2d.drawImage(imagenRedimensionada, x, y, null);
        } catch (IOException e) {
            // Fallback a texto si la imagen no está disponible
            dibujarPieza(g2d, pieza, fila, col);
        }
    }

    private String getNombreImagenPieza(Pieza pieza) {
        String color = (pieza.getColor() == org.ajedrez.entity.Color.WHITE) ? "WHITE" : "BLACK";

        if (pieza instanceof Rey) return color + "K.png";
        if (pieza instanceof Dama) return color + "Q.png";
        if (pieza instanceof Torre) return color + "R.png";
        if (pieza instanceof Alfil) return color + "B.png";
        if (pieza instanceof Caballo) return color + "N.png";
        if (pieza instanceof Peon) return color + "P.png";
        return "unknown.png";
    }

    //reiniciar el tablero
    public void reiniciarPartida() {
        tablero.inicializarTablero();
        seleccionada = null;
        movimientosPosibles.clear();
        partida = new Partida(
                new Jugador("Player 1", org.ajedrez.entity.Color.WHITE),
                new Jugador("Player 2", org.ajedrez.entity.Color.BLACK)
        );
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
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
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