package org.ajedrez.entity;

import org.ajedrez.entity.pieza.*;

import java.util.List;


public class Tablero {
    private Pieza[][] casillas = new Pieza[8][8];
    private Pieza ultimaCaptura;

    public Tablero() {}

    public Tablero(Tablero tablero) {
        this.casillas = tablero.casillas;
        this.ultimaCaptura = tablero.ultimaCaptura;
    }

    public void inicializarTablero() {
        // --------------------
        // Piezas BLANCAS
        // --------------------
        casillas[0][0] = new Torre(Color.BLACK, new Posicion(0, 0));
        casillas[0][1] = new Caballo(Color.BLACK, new Posicion(0, 1));
        casillas[0][2] = new Alfil(Color.BLACK, new Posicion(0, 2));
        casillas[0][3] = new Dama(Color.BLACK, new Posicion(0, 3));
        casillas[0][4] = new Rey(Color.BLACK, new Posicion(0, 4));
        casillas[0][5] = new Alfil(Color.BLACK, new Posicion(0, 5));
        casillas[0][6] = new Caballo(Color.BLACK, new Posicion(0, 6));
        casillas[0][7] = new Torre(Color.BLACK, new Posicion(0, 7));

        for (int col = 0; col < 8; col++) {
            casillas[1][col] = new Peon(Color.BLACK, new Posicion(1, col));
        }

        // --------------------
        // Piezas NEGRAS
        // --------------------
        casillas[7][0] = new Torre(Color.WHITE, new Posicion(7, 0));
        casillas[7][1] = new Caballo(Color.WHITE, new Posicion(7, 1));
        casillas[7][2] = new Alfil(Color.WHITE, new Posicion(7, 2));
        casillas[7][3] = new Dama(Color.WHITE, new Posicion(7, 3));
        casillas[7][4] = new Rey(Color.WHITE, new Posicion(7, 4));
        casillas[7][5] = new Alfil(Color.WHITE, new Posicion(7, 5));
        casillas[7][6] = new Caballo(Color.WHITE, new Posicion(7, 6));
        casillas[7][7] = new Torre(Color.WHITE, new Posicion(7, 7));

        for (int col = 0; col < 8; col++) {
            casillas[6][col] = new Peon(Color.WHITE, new Posicion(6, col));
        }
    }

    public void setCasilla(Pieza pieza, Posicion pos) {
        casillas[pos.getFila()][pos.getColumna()] = pieza;
    }

    public Pieza getPieza(@org.jetbrains.annotations.NotNull Posicion pos) {
        return casillas[pos.getFila()][pos.getColumna()];
    }

    public void moverPieza(Movimiento mov) {
        Posicion origen = mov.getOrigen();
        Posicion destino = mov.getDestino();
        Pieza pieza = casillas[origen.getFila()][origen.getColumna()];

        // Si había una pieza en destino, es capturada
        Pieza capturada = casillas[destino.getFila()][destino.getColumna()];

        // Mover la pieza
        casillas[destino.getFila()][destino.getColumna()] = pieza;
        casillas[origen.getFila()][origen.getColumna()] = null;

        // Actualizar posición interna de la pieza
        pieza.setPosicion(destino);

        // Guardar en el movimiento qué pieza se capturó
        mov.setPiezaMovida(pieza);
        mov.setPiezaCapturada(capturada);

        System.out.println("Movimiento: " + pieza.getClass().getSimpleName() +
                " de " + origen + " a " + destino);
        if (capturada != null) {
            ultimaCaptura = capturada;
            System.out.println("Se capturó: " + capturada.getClass().getSimpleName());
        }
    }

    public boolean esJaque(Color color) {
        Posicion reyPos = encontrarRey(color);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza p = casillas[i][j];
                if(p != null && p.getColor() != color){
                    List<Movimiento> movs = p.movimientosPosibles(this);
                    for (Movimiento mov : movs) {
                        if(mov.getDestino().equals(reyPos)){
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    private Posicion encontrarRey(Color color) {
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                if (casillas[fila][col] instanceof Rey && casillas[fila][col].getColor().equals(color)) {
                    return new Posicion(fila, col);
                }
            }
        }
        return null;
    }


    public Pieza getUltimaCaptura() {
        return ultimaCaptura;
    }

    public void nullUltimaCapturada(){
        this.ultimaCaptura = null;
    }

}
