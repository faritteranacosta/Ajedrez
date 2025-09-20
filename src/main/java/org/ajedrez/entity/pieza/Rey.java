package org.ajedrez.entity.pieza;

import org.ajedrez.entity.Color;
import org.ajedrez.entity.Movimiento;
import org.ajedrez.entity.Posicion;
import org.ajedrez.entity.Tablero;

import java.util.ArrayList;
import java.util.List;

public class Rey extends Pieza {
    public Rey(Color color, Posicion position) {
        super(color, position);
    }

    @Override
    public List<Movimiento> movimientosPosibles(Tablero tablero) {
        List<Movimiento> movimientos = new ArrayList<>();

        int fila = position.getFila();
        int col = position.getColumna();

        // Verificar los 8 movimientos posibles alrededor del rey
        for (int i = fila - 1; i <= fila + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                // Saltar la posición actual del rey
                if (i == fila && j == col) {
                    continue;
                }

                // Verificar si la posición está dentro del tablero
                if (i >= 0 && i < 8 && j >= 0 && j < 8) {
                    agregarMovimiento(movimientos, tablero, i, j);
                }
            }
        }

        return movimientos;
    }

    private void agregarMovimiento(List<Movimiento> movimientos, Tablero tablero, int fila, int col) {
        Posicion destino = new Posicion(fila, col);
        Pieza piezaEnDestino = tablero.getPieza(destino);

        if (piezaEnDestino == null) {
            movimientos.add(new Movimiento(position, destino, this, null));
        } else if (piezaEnDestino.getColor() != this.color) {
            movimientos.add(new Movimiento(position, destino, this, piezaEnDestino));
        }
    }
}