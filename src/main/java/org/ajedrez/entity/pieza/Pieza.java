package org.ajedrez.entity.pieza;


import org.ajedrez.entity.Color;
import org.ajedrez.entity.Movimiento;
import org.ajedrez.entity.Posicion;
import org.ajedrez.entity.Tablero;

import java.util.List;

public abstract class Pieza {
    Color color;
    Posicion position;

    public Pieza(Color color, Posicion position) {
        this.color = color;
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public Posicion getPosition() {
        return position;
    }

    public void setPosition(Posicion position) {
        this.position = position;
    }

    public abstract List<Movimiento> movimientosPosibles(Tablero tablero);

    public void setPosicion(Posicion destino) {
        this.position = destino;
    }
}
