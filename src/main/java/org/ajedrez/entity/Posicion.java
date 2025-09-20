package org.ajedrez.entity;

public class Posicion {
    private int fila;
    private int columna;

    public Posicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getColumna() {
        return columna;
    }
    public int getFila() {
        return fila;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Posicion)) return false;
        Posicion pos = (Posicion) o;
        return fila == pos.fila && columna == pos.columna;
    }

    @Override
    public String toString() {
        return "(" + fila + "," + columna + ")";
    }
}
