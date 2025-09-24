package org.ajedrez.entity.pieza;

import org.ajedrez.entity.Color;
import org.ajedrez.entity.Movimiento;
import org.ajedrez.entity.Posicion;
import org.ajedrez.entity.Tablero;

import java.util.ArrayList;
import java.util.List;

public class Torre extends Pieza{
    private boolean movida;
    public Torre(Color color, Posicion position) {
        super(color, position);
        this.movida = false;
    }

    public boolean isMovida() {
        return movida;
    }

    public void setMovida(boolean movida) {
        this.movida = movida;
    }

    @Override
    public List<Movimiento> movimientosPosibles(Tablero tablero) {
        List<Movimiento> movimientos = new ArrayList<>();

        int fila = position.getFila();
        int col = position.getColumna();

        // Arriba
        for (int i = fila - 1; i >= 0; i--) {
            if (!agregarMovimiento(movimientos, tablero, i, col)) break;
        }
        // Abajo
        for (int i = fila + 1; i < 8; i++) {
            if (!agregarMovimiento(movimientos, tablero, i, col)) break;
        }
        // Izquierda
        for (int j = col - 1; j >= 0; j--) {
            if (!agregarMovimiento(movimientos, tablero, fila, j)) break;
        }
        // Derecha
        for (int j = col + 1; j < 8; j++) {
            if (!agregarMovimiento(movimientos, tablero, fila, j)) break;
        }

        return movimientos;
    }

    private boolean agregarMovimiento(List<Movimiento> movimientos, Tablero tablero, int fila, int col) {
        Posicion destino = new Posicion(fila, col);
        Pieza piezaEnDestino = tablero.getPieza(destino);

        if (piezaEnDestino == null) {
            movimientos.add(new Movimiento(position, destino, this, null));
            return true;
        } else if (piezaEnDestino.getColor() != this.color) {
            movimientos.add(new Movimiento(position, destino, this, piezaEnDestino));
            return false;
        } else {
            return false;
        }
    }
}
