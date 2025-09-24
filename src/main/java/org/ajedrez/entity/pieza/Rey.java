package org.ajedrez.entity.pieza;

import org.ajedrez.entity.Color;
import org.ajedrez.entity.Movimiento;
import org.ajedrez.entity.Posicion;
import org.ajedrez.entity.Tablero;

import java.util.ArrayList;
import java.util.List;

public class Rey extends Pieza {
    private boolean haMovido = false;

    public Rey(Color color, Posicion position) {
        super(color, position);
    }

    public boolean isHaMovido() {
        return haMovido;
    }

    public void setHaMovido(boolean haMovido) {
        this.haMovido = haMovido;
    }

    @Override
    public List<Movimiento> movimientosPosibles(Tablero tablero) {
        List<Movimiento> movimientos = new ArrayList<>();

        int fila = position.getFila();
        int col = position.getColumna();

        // Movimientos normales (1 casilla en cualquier dirección)
        for (int i = fila - 1; i <= fila + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if ((i == fila && j == col) || i < 0 || i >= 8 || j < 0 || j >= 8) {
                    continue;
                }
                agregarMovimiento(movimientos, tablero, i, j);
            }
        }

        // Enroque - solo si el rey no se ha movido y no está en jaque
        if (!haMovido && !estaEnJaque(tablero)) {
            agregarEnroque(movimientos, tablero);
        }

        return movimientos;
    }

    private void agregarEnroque(List<Movimiento> movimientos, Tablero tablero) {
        int fila = position.getFila();

        // Enroque corto (lado del rey)
        if (puedeEnrocarCorto(tablero)) {
            Movimiento enroqueCorto = new Movimiento(
                    position,
                    new Posicion(fila, 6),
                    this,
                    null
            );
            enroqueCorto.setEsEnroque(true);
            movimientos.add(enroqueCorto);
        }

        // Enroque largo (lado de la dama)
        if (puedeEnrocarLargo(tablero)) {
            Movimiento enroqueLargo = new Movimiento(
                    position,
                    new Posicion(fila, 2),
                    this,
                    null
            );
            enroqueLargo.setEsEnroque(true);
            movimientos.add(enroqueLargo);
        }
    }

    private boolean puedeEnrocarCorto(Tablero tablero) {
        int fila = position.getFila();

        // Verificar que la torre de la derecha no se haya movido
        Pieza torre = tablero.getPieza(new Posicion(fila, 7));
        if (!(torre instanceof Torre) || ((Torre) torre).isMovida()) {
            return false;
        }

        // Verificar que las casillas entre el rey y la torre estén vacías
        if (tablero.getPieza(new Posicion(fila, 5)) != null ||
                tablero.getPieza(new Posicion(fila, 6)) != null) {
            return false;
        }

        // Verificar que el rey no pase por casillas en jaque
        return !estaBajoAtaque(new Posicion(fila, 5), tablero) &&
                !estaBajoAtaque(new Posicion(fila, 6), tablero);
    }

    private boolean puedeEnrocarLargo(Tablero tablero) {
        int fila = position.getFila();

        // Verificar que la torre de la izquierda no se haya movido
        Pieza torre = tablero.getPieza(new Posicion(fila, 0));
        if (!(torre instanceof Torre) || ((Torre) torre).isMovida()) {
            return false;
        }

        // Verificar que las casillas entre el rey y la torre estén vacías
        if (tablero.getPieza(new Posicion(fila, 1)) != null ||
                tablero.getPieza(new Posicion(fila, 2)) != null ||
                tablero.getPieza(new Posicion(fila, 3)) != null) {
            return false;
        }

        // Verificar que el rey no pase por casillas en jaque
        return !estaBajoAtaque(new Posicion(fila, 2), tablero) &&
                !estaBajoAtaque(new Posicion(fila, 3), tablero);
    }

    private boolean estaEnJaque(Tablero tablero) {
        // Este método debería usar la lógica de Partida.estaEnJaque()
        // Por ahora, asumimos que hay acceso a la partida
        return false; // Se verificará en Partida.esMovimientoLegal()
    }

    private boolean estaBajoAtaque(Posicion posicion, Tablero tablero) {
        // Verificación simplificada - en la práctica se usa Partida.estaBajoAtaque()
        return false;
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