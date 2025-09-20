package org.ajedrez.entity.pieza;

import org.ajedrez.entity.Color;
import org.ajedrez.entity.Movimiento;
import org.ajedrez.entity.Posicion;
import org.ajedrez.entity.Tablero;

import java.util.ArrayList;
import java.util.List;

public class Peon extends Pieza{
    public Peon(Color color, Posicion position) {
        super(color, position);
    }

    @Override
    public List<Movimiento> movimientosPosibles(Tablero tablero) {
        List<Movimiento> movimientos = new ArrayList<>();

        int fila = position.getFila();
        int col = position.getColumna();
        int direccion = (this.color == Color.WHITE) ? -1 : 1; // Blancas suben, negras bajan

        // Movimiento hacia adelante (1 casilla)
        Posicion adelante1 = new Posicion(fila + direccion, col);
        if (tablero.getPieza(adelante1) == null) {
            movimientos.add(new Movimiento(position, adelante1, this, null));

            // Movimiento inicial de 2 casillas
            if ((color == Color.WHITE && fila == 6) || (color == Color.BLACK && fila == 1)) {
                Posicion adelante2 = new Posicion(fila + 2 * direccion, col);
                if (tablero.getPieza(adelante2) == null) {
                    movimientos.add(new Movimiento(position, adelante2, this, null));
                }
            }
        }

        // Capturas diagonales
        agregarCapturaPeon(movimientos, tablero, fila + direccion, col - 1); // Izquierda
        agregarCapturaPeon(movimientos, tablero, fila + direccion, col + 1); // Derecha

        return movimientos;
    }

    private void agregarCapturaPeon(List<Movimiento> movimientos, Tablero tablero, int fila, int col) {
        if (fila >= 0 && fila < 8 && col >= 0 && col < 8) {
            Posicion destino = new Posicion(fila, col);
            Pieza pieza = tablero.getPieza(destino);
            if (pieza != null && pieza.getColor() != this.color) {
                movimientos.add(new Movimiento(position, destino, this, pieza));
            }
        }
    }
}
