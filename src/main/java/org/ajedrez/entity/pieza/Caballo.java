package org.ajedrez.entity.pieza;

import org.ajedrez.entity.Color;
import org.ajedrez.entity.Movimiento;
import org.ajedrez.entity.Posicion;
import org.ajedrez.entity.Tablero;

import java.util.ArrayList;
import java.util.List;

public class Caballo extends Pieza{
    public Caballo(Color color, Posicion position) {
        super(color, position);
    }

    public List<Movimiento> moviminetosPosibles(Tablero tablero){
        return null;
    }

    @Override
    public List<Movimiento> movimientosPosibles(Tablero tablero) {
        List<Movimiento> movimientos = new ArrayList<>();

        int fila = position.getFila();
        int col = position.getColumna();

        //Movimientos posibles
        int[][] posiblesMovimientos = {{-2, -1}, {-1, -2}, {1, -2}, {2, -1},
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1}};

        for (int[] move:  posiblesMovimientos) {
            int newFila = fila + move[0];
            int newCol = col + move[1];

            if(newFila >= 0 && newFila <= 7 && newCol >= 0 && newCol <= 7){
                agregarMovimiento(movimientos, tablero, newFila, newCol);
            }
        }

        return  movimientos;
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
