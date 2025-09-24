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
        casillas[0][4] = new Rey(Color.BLACK, new Posicion(0, 4));
        casillas[0][7] = new Torre(Color.BLACK, new Posicion(0, 7));


        // --------------------
        // Piezas NEGRAS
        // --------------------
        casillas[7][0] = new Torre(Color.WHITE, new Posicion(7, 0));
        casillas[7][4] = new Rey(Color.WHITE, new Posicion(7, 4));
        casillas[6][7] = new Peon(Color.WHITE, new Posicion(6, 7));

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

        // Manejar enroque
        if (mov.isEsEnroque()) {
            int fila = origen.getFila();

            if (destino.getColumna() == 6) { // Enroque corto
                // Mover la torre
                Pieza torre = casillas[fila][7];
                casillas[fila][5] = torre;
                if (torre != null) {
                    torre.setPosicion(new Posicion(fila, 5));
                }
                casillas[fila][7] = null;
                mov.setEsEnroqueCorto(true);

            } else if (destino.getColumna() == 2) { // Enroque largo
                // Mover la torre
                Pieza torre = casillas[fila][0];
                casillas[fila][3] = torre;
                if (torre != null) {
                    torre.setPosicion(new Posicion(fila, 3));
                }
                casillas[fila][0] = null;
                mov.setEsEnroqueCorto(false);
            }
        }

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

        // Marcar que el rey y la torre se han movido
        if (pieza instanceof Torre) {
            ((Torre) pieza).setMovida(true);
        } else if (pieza instanceof Rey) {
            ((Rey) pieza).setHaMovido(true);
        }
    }

    public Posicion encontrarRey(Color color) {
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                if (casillas[fila][col] instanceof Rey && casillas[fila][col].getColor() == color) {
                    return new Posicion(fila, col);
                }
            }
        }
        return null;
    }

    public Tablero copiar() {
        Tablero copia = new Tablero();
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Posicion pos = new Posicion(fila, col);
                Pieza original = getPieza(pos);
                if (original != null) {
                    // Necesitarías implementar un método copy() en cada pieza
                    Pieza copiaPieza = copiarPieza(original);
                    copia.colocarPieza(copiaPieza, pos);
                }
            }
        }
        return copia;
    }

    private Pieza copiarPieza(Pieza original) {
        // Implementar según tus clases de piezas
        if (original instanceof Rey) return new Rey(original.getColor(), original.getPosition());
        if (original instanceof Dama) return new Dama(original.getColor(), original.getPosition());
        if (original instanceof Torre) return new Torre(original.getColor(), original.getPosition());
        if (original instanceof Alfil) return new Alfil(original.getColor(), original.getPosition());
        if (original instanceof Caballo) return new Caballo(original.getColor(), original.getPosition());
        if (original instanceof Peon) return new Peon(original.getColor(), original.getPosition());
        return null;
    }

    private boolean esPosicionValida(Posicion posicion) {
        return posicion.getFila() >= 0 && posicion.getFila() < 8 &&
                posicion.getColumna() >= 0 && posicion.getColumna() < 8;
    }

    public void colocarPieza(Pieza pieza, Posicion posicion) {
        if (esPosicionValida(posicion)) {
            casillas[posicion.getFila()][posicion.getColumna()] = pieza;
            if (pieza != null) {
                pieza.setPosition(posicion); // Actualizar la posición de la pieza
            }
        }
    }

    public Pieza getUltimaCaptura() {
        return ultimaCaptura;
    }

    public void nullUltimaCapturada(){
        this.ultimaCaptura = null;
    }

}
