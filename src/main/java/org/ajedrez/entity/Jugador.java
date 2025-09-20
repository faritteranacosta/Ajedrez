package org.ajedrez.entity;

import org.ajedrez.entity.pieza.Pieza;

import java.util.List;

public class Jugador {
    private String nombre;
    private Color color;
    private boolean esIA;  // true si es controlado por la computadora

    public Jugador(String nombre, Color color) {
        this.nombre = nombre;
        this.color = color;
        this.esIA = false;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isEsIA() {
        return esIA;
    }

    public void setEsIA(boolean esIA) {
        this.esIA = esIA;
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "nombre='" + nombre + '\'' +
                ", color=" + color +
                ", esIA=" + esIA +
                '}';
    }
}
