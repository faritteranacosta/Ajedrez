package org.ajedrez.entity;

import org.ajedrez.entity.pieza.Pieza;

public class Movimiento {
    private Posicion origen;
    private Posicion destino;
    private Pieza piezaMovida;
    private Pieza piezaCapturada;
    private boolean esEnroque = false;
    private boolean esEnroqueCorto = false;

    public Movimiento(Posicion origen, Posicion destino,  Pieza piezaMovida, Pieza piezaCapturada) {
        this.origen = origen;
        this.destino = destino;
        this.piezaMovida = piezaMovida;
        this.piezaCapturada = piezaCapturada;
    }

    public Posicion getOrigen() {
        return origen;
    }

    public Posicion getDestino() {
        return destino;
    }

    public Pieza getPiezaMovida() {
        return piezaMovida;
    }

    public void setPiezaMovida(Pieza piezaMovida) {
        this.piezaMovida = piezaMovida;
    }

    public Pieza getPiezaCapturada() {
        return piezaCapturada;
    }

    public void setPiezaCapturada(Pieza piezaCapturada) {
        this.piezaCapturada = piezaCapturada;
    }

    public boolean esValido(){
        return origen != null && destino != null && piezaMovida != null;
    }

    public boolean isEsEnroque() {
        return esEnroque;
    }

    public void setEsEnroque(boolean esEnroque) {
        this.esEnroque = esEnroque;
    }

    public boolean isEsEnroqueCorto() {
        return esEnroqueCorto;
    }

    public void setEsEnroqueCorto(boolean esEnroqueCorto) {
        this.esEnroqueCorto = esEnroqueCorto;
    }

    @Override
    public String toString() {
        return "Movimiento{, destino=" + destino + '}';
    }

}
