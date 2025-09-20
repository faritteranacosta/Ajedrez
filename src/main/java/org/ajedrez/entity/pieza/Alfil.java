package org.ajedrez.entity.pieza;

import org.ajedrez.entity.Color;
import org.ajedrez.entity.Movimiento;
import org.ajedrez.entity.Posicion;
import org.ajedrez.entity.Tablero;

import java.util.ArrayList;
import java.util.List;

public class Alfil extends Pieza{

    public Alfil(Color color, Posicion position) {
        super(color, position);
    }

    @Override
    public List<Movimiento> movimientosPosibles(Tablero tablero) {
        List<Movimiento> movimientos = new ArrayList<>();

        int fila = position.getFila();
        int col = position.getColumna();

        // Diagonal Arriba-Izquierda (Up-Left)
        for (int i = fila - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (!agregarMovimiento(movimientos, tablero, i, j)) {
                break;
            }
        }

        // Diagonal Abajo-Derecha (Down-Right)
        for (int i = fila + 1, j = col + 1; i < 8 && j < 8; i++, j++) {
            if (!agregarMovimiento(movimientos, tablero, i, j)) {
                break;
            }
        }

        // Diagonal Arriba-Derecha (Up-Right)
        for (int i = fila - 1, j = col + 1; i >= 0 && j < 8; i--, j++) {
            if (!agregarMovimiento(movimientos, tablero, i, j)) {
                break;
            }
        }

        // Diagonal Abajo-Izquierda (Down-Left)
        for (int i = fila + 1, j = col - 1; i < 8 && j >= 0; i++, j--) {
            if (!agregarMovimiento(movimientos, tablero, i, j)) {
                break;
            }
        }

        return movimientos;
    }

    private boolean agregarMovimiento(List<Movimiento> movimientos, Tablero tablero, int fila, int col) {
        if (fila >= 0 && fila <= 7 && col >= 0 && col <= 7) {
            Posicion destino = new Posicion(fila, col);
            Pieza piezaEnDestino = tablero.getPieza(destino);

            if (piezaEnDestino == null) {
                movimientos.add(new Movimiento(position, destino, this, null));
                return true;
            } else if (piezaEnDestino.getColor() != this.color) {
                movimientos.add(new Movimiento(position, destino, this, piezaEnDestino));
                return false;
            }
        }
        return false;
    }
}
