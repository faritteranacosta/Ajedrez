package org.ajedrez.entity;

import org.ajedrez.entity.pieza.Pieza;

import java.util.ArrayList;
import java.util.List;

public class Partida {
    private Tablero tablero;
    private Jugador jugadorBlanco;
    private Jugador jugadorNegro;
    private Color turnoActual;
    private List<Movimiento> movimientos;


    public Partida(Jugador jugadorBlanco, Jugador jugadorNegro) {
        this.tablero = new Tablero();
        this.jugadorBlanco = jugadorBlanco;
        this.jugadorNegro =  jugadorNegro;
        this.turnoActual = Color.WHITE;
        this.movimientos = new ArrayList<>();
    }

    public boolean moverPieza(Posicion origen, Posicion destino) {
        Pieza pieza = tablero.getPieza(origen);
        if (pieza == null || pieza.getColor() != turnoActual) return false;
        List<Movimiento> posibles = pieza.movimientosPosibles(tablero);
        for (Movimiento mov : posibles) {
            if (mov.getDestino().equals(destino)) {
                tablero.moverPieza(mov);
                movimientos.add(mov);
                turnoActual = (turnoActual == Color.WHITE) ? Color.BLACK : Color.WHITE;
                return true;
            }
        }
        return false;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public Color getTurnoActual() {
        return turnoActual;
    }
    public boolean isJaque(Color color) {
        return false;
    }

    public boolean isJaqueMate(Color color) {
        return false;
    }

    public void isTabla(){

    }

}
