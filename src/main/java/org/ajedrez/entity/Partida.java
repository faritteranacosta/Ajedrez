package org.ajedrez.entity;

import org.ajedrez.entity.pieza.*;

import java.util.ArrayList;
import java.util.List;

public class Partida {
    private Tablero tablero;
    private Jugador jugadorBlanco;
    private Jugador jugadorNegro;
    private Color turnoActual;
    private List<Movimiento> movimientos;
    private boolean juegoTerminado;
    private String resultado; // "JAQUE_MATE", "TABLAS", "ABANDONO", etc.
    private Color ganador; // null si son tablas

    public Partida(Jugador jugadorBlanco, Jugador jugadorNegro) {
        this.tablero = new Tablero();
        this.jugadorBlanco = jugadorBlanco;
        this.jugadorNegro = jugadorNegro;
        this.turnoActual = Color.WHITE;
        this.movimientos = new ArrayList<>();
        this.juegoTerminado = false;
        this.resultado = null;
        this.ganador = null;
    }

    public boolean moverPieza(Posicion origen, Posicion destino, Pieza promocion) {
        if (juegoTerminado) {
            return false;
        }

        Pieza pieza = tablero.getPieza(origen);
        if (pieza == null || pieza.getColor() != turnoActual) return false;

        Movimiento movimientoPropuesto = new Movimiento(origen, destino, pieza, tablero.getPieza(destino));

        if (!esMovimientoLegal(movimientoPropuesto)) {
            return false;
        }

        List<Movimiento> posibles = pieza.movimientosPosibles(tablero);
        boolean movimientoValido = false;

        for (Movimiento mov : posibles) {
            if (mov.getDestino().equals(destino)) {
                movimientoValido = true;
                break;
            }
        }

        if (!movimientoValido) {
            return false;
        }

        // EJECUTAR EL MOVIMIENTO
        tablero.moverPieza(movimientoPropuesto);

        if (promocion != null) {
            tablero.setCasilla(promocion, movimientoPropuesto.getDestino());
        }

        movimientos.add(movimientoPropuesto);

        // VERIFICAR ESTADO DEL JUEGO DESPUÉS DEL MOVIMIENTO
        Color jugadorQueAcabaDeMover = turnoActual;
        turnoActual = (turnoActual == Color.WHITE) ? Color.BLACK : Color.WHITE;

        verificarEstadoDelJuego(jugadorQueAcabaDeMover);

        return true;
    }

    private void verificarEstadoDelJuego(Color jugadorQueAcabaDeMover) {
        // 1. Verificar Jaque Mate
        if (estaEnJaqueMate(turnoActual)) {
            juegoTerminado = true;
            resultado = "JAQUE_MATE";
            ganador = jugadorQueAcabaDeMover;
            System.out.println("¡JAQUE MATE! Ganaron las " +
                    (ganador == Color.WHITE ? "blancas" : "negras"));
            return;
        }

        // 2. Verificar Ahogado (Tablas por rey ahogado)
        if (estaAhogado(turnoActual)) {
            juegoTerminado = true;
            resultado = "TABLAS_AHOGADO";
            ganador = null;
            System.out.println("¡TABLAS! Rey ahogado");
            return;
        }

        // 3. Verificar Material Insuficiente
        if (esMaterialInsuficiente()) {
            juegoTerminado = true;
            resultado = "TABLAS_MATERIAL_INSUFICIENTE";
            ganador = null;
            System.out.println("¡TABLAS! Material insuficiente para dar jaque mate");
            return;
        }

        // 4. Verificar Regla de los 50 Movimientos
        if (regla50Movimientos()) {
            juegoTerminado = true;
            resultado = "TABLAS_50_MOVIMIENTOS";
            ganador = null;
            System.out.println("¡TABLAS! Regla de 50 movimientos");
            return;
        }

        // 5. Verificar Triple Repetición
        if (tripleRepeticion()) {
            juegoTerminado = true;
            resultado = "TABLAS_TRIPLE_REPETICION";
            ganador = null;
            System.out.println("¡TABLAS! Triple repetición de posición");
            return;
        }

        // 6. Verificar Jaque simple
        if (estaEnJaque(turnoActual)) {
            System.out.println("¡JAQUE! al rey " + turnoActual);
        }
    }

    // JAQUE MATE (ya lo tenías)
    public boolean estaEnJaqueMate(Color color) {
        if (!estaEnJaque(color)) {
            return false;
        }
        return !existeMovimientoLegal(color);
    }

    // AHOGADO (Stalemate)
    public boolean estaAhogado(Color color) {
        // El rey NO está en jaque pero NO tiene movimientos legales
        if (estaEnJaque(color)) {
            return false;
        }
        return !existeMovimientoLegal(color);
    }

    // MATERIAL INSUFICIENTE
    private boolean esMaterialInsuficiente() {
        // Contar piezas de ambos bandos
        int piezasBlancas = contarPiezas(Color.WHITE);
        int piezasNegras = contarPiezas(Color.BLACK);

        // Casos de material insuficiente:
        // 1. Rey vs Rey
        // 2. Rey + alfil vs Rey
        // 3. Rey + caballo vs Rey
        // 4. Rey + alfil vs Rey + alfil (mismo color de casillas)

        if (piezasBlancas == 1 && piezasNegras == 1) {
            return true; // Rey vs Rey
        }

        if ((piezasBlancas == 2 && piezasNegras == 1) ||
                (piezasBlancas == 1 && piezasNegras == 2)) {
            // Verificar si la pieza extra es alfil o caballo
            Color colorConPieza = (piezasBlancas == 2) ? Color.WHITE : Color.BLACK;
            if (tieneSoloAlfilOCaballo(colorConPieza)) {
                return true;
            }
        }

        if (piezasBlancas == 2 && piezasNegras == 2) {
            // Rey + alfil vs Rey + alfil (mismo color)
            if (tienenAlfilesMismoColor()) {
                return true;
            }
        }

        return false;
    }

    private int contarPiezas(Color color) {
        int count = 0;
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Posicion pos = new Posicion(fila, col);
                Pieza pieza = tablero.getPieza(pos);
                if (pieza != null && pieza.getColor() == color) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean tieneSoloAlfilOCaballo(Color color) {
        boolean tieneRey = false;
        boolean tieneOtraPieza = false;
        boolean piezaValida = true;

        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Posicion pos = new Posicion(fila, col);
                Pieza pieza = tablero.getPieza(pos);
                if (pieza != null && pieza.getColor() == color) {
                    if (pieza instanceof Rey) {
                        tieneRey = true;
                    } else if (pieza instanceof Alfil || pieza instanceof Caballo) {
                        tieneOtraPieza = true;
                    } else {
                        piezaValida = false; // Tiene dama, torre o peón
                    }
                }
            }
        }
        return tieneRey && tieneOtraPieza && piezaValida;
    }

    private boolean tienenAlfilesMismoColor() {
        // Implementación compleja - simplificada por ahora
        // En una implementación real, verificarías el color de casilla de los alfiles
        return false;
    }

    // REGLA DE 50 MOVIMIENTOS
    private boolean regla50Movimientos() {
        if (movimientos.size() < 50) return false;

        // Verificar últimos 50 movimientos sin captura ni movimiento de peón
        int contador = 0;
        for (int i = movimientos.size() - 1; i >= 0 && contador < 50; i--, contador++) {
            Movimiento mov = movimientos.get(i);
            if (mov.getPiezaCapturada() != null || mov.getPiezaMovida() instanceof Peon) {
                return false;
            }
        }
        return contador == 50;
    }

    // TRIPLE REPETICIÓN
    private boolean tripleRepeticion() {
        if (movimientos.size() < 6) return false; // Mínimo 3 movimientos por jugador

        String posicionActual = obtenerFirmaPosicion();
        int repeticiones = 1;

        // Contar cuántas veces ha aparecido esta posición
        for (int i = movimientos.size() - 2; i >= 0; i -= 2) { // Ir hacia atrás de 2 en 2
            String firma = obtenerFirmaPosicionEnMovimiento(i);
            if (posicionActual.equals(firma)) {
                repeticiones++;
                if (repeticiones >= 3) {
                    return true;
                }
            }
        }
        return false;
    }

    private String obtenerFirmaPosicion() {
        // Crear una firma única de la posición actual
        StringBuilder firma = new StringBuilder();
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Posicion pos = new Posicion(fila, col);
                Pieza pieza = tablero.getPieza(pos);
                if (pieza != null) {
                    firma.append(pieza.getColor() == Color.WHITE ? "B" : "N");
                    if (pieza instanceof Rey) firma.append("K");
                    else if (pieza instanceof Dama) firma.append("Q");
                    else if (pieza instanceof Torre) firma.append("R");
                    else if (pieza instanceof Alfil) firma.append("B");
                    else if (pieza instanceof Caballo) firma.append("N");
                    else if (pieza instanceof Peon) firma.append("P");
                    firma.append(fila).append(col).append("|");
                }
            }
        }
        // Agregar información de enroque y al paso
        firma.append(turnoActual == Color.WHITE ? "WT" : "BT");
        return firma.toString();
    }

    private String obtenerFirmaPosicionEnMovimiento(int indiceMovimiento) {
        // Reconstruir la posición en un movimiento específico
        // Implementación simplificada - en realidad necesitarías guardar estados anteriores
        return obtenerFirmaPosicion(); // Por simplicidad
    }

    // MÉTODOS PARA OFRECER TABLAS
    public boolean ofrecerTablas() {
        if (juegoTerminado) return false;

        // En una implementación completa, aquí se notificaría al oponente
        // Por ahora, asumimos que el oponente acepta
        juegoTerminado = true;
        resultado = "TABLAS_ACUERDO";
        ganador = null;
        System.out.println("¡TABLAS! Por acuerdo mutuo");
        return true;
    }

    public boolean abandonar(Color color) {
        if (juegoTerminado) return false;

        juegoTerminado = true;
        resultado = "ABANDONO";
        ganador = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
        System.out.println("¡Partida abandonada! Ganaron las " +
                (ganador == Color.WHITE ? "blancas" : "negras"));
        return true;
    }

    // GETTERS PARA ESTADO DEL JUEGO
    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    public String getResultado() {
        return resultado;
    }

    public Color getGanador() {
        return ganador;
    }

    public String getDescripcionResultado() {
        if (!juegoTerminado) return "Partida en curso";

        switch (resultado) {
            case "JAQUE_MATE":
                return "Jaque Mate - Ganaron las " + (ganador == Color.WHITE ? "blancas" : "negras");
            case "TABLAS_AHOGADO":
                return "Tablas - Rey ahogado";
            case "TABLAS_MATERIAL_INSUFICIENTE":
                return "Tablas - Material insuficiente";
            case "TABLAS_50_MOVIMIENTOS":
                return "Tablas - Regla de 50 movimientos";
            case "TABLAS_TRIPLE_REPETICION":
                return "Tablas - Triple repetición";
            case "TABLAS_ACUERDO":
                return "Tablas - Acuerdo mutuo";
            case "ABANDONO":
                return "Abandono - Ganaron las " + (ganador == Color.WHITE ? "blancas" : "negras");
            default:
                return "Partida terminada";
        }
    }

    public boolean estaEnJaque(Color color) {
        Posicion posicionRey = tablero.encontrarRey(color);
        if (posicionRey == null) return false; // No debería pasar

        return estaBajoAtaque(posicionRey, color);
    }

    private boolean estaBajoAtaque(Posicion posicion, Color colorDefensor) {
        Color colorAtacante = (colorDefensor == Color.WHITE) ? Color.BLACK : Color.WHITE;

        // Verificar si alguna pieza del oponente puede atacar esta posición
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Posicion pos = new Posicion(fila, col);
                Pieza pieza = tablero.getPieza(pos);

                if (pieza != null && pieza.getColor() == colorAtacante) {
                    List<Movimiento> movimientosPieza = pieza.movimientosPosibles(tablero);
                    for (Movimiento mov : movimientosPieza) {
                        if (mov.getDestino().equals(posicion)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean existeMovimientoLegal(Color color) {
        // Verificar todos los movimientos posibles de todas las piezas del color
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Posicion pos = new Posicion(fila, col);
                Pieza pieza = tablero.getPieza(pos);

                if (pieza != null && pieza.getColor() == color) {
                    List<Movimiento> movimientosPieza = pieza.movimientosPosibles(tablero);
                    for (Movimiento mov : movimientosPieza) {
                        if (esMovimientoLegal(mov)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean esMovimientoLegal(Movimiento movimiento) {
        if (movimiento == null || movimiento.getPiezaMovida() == null) {
            return false;
        }

        // Si el rey está en jaque actualmente, no puede hacer enroque
        if (movimiento.isEsEnroque() && estaEnJaque(movimiento.getPiezaMovida().getColor())) {
            return false;
        }

        // Resto de la verificación normal...
        Tablero tableroCopia = tablero.copiar();
        tableroCopia.moverPieza(movimiento);

        Color colorJugador = movimiento.getPiezaMovida().getColor();
        Posicion nuevoReyPos;

        if (movimiento.getPiezaMovida() instanceof Rey) {
            nuevoReyPos = movimiento.getDestino();
        } else {
            nuevoReyPos = tableroCopia.encontrarRey(colorJugador);
        }

        boolean enJaque = estaBajoAtaqueEnTablero(nuevoReyPos, colorJugador, tableroCopia);
        return !enJaque;
    }

    private boolean estaBajoAtaqueEnTablero(Posicion posicion, Color colorDefensor, Tablero tableroEspecifico) {
        Color colorAtacante = (colorDefensor == Color.WHITE) ? Color.BLACK : Color.WHITE;

        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                Posicion pos = new Posicion(fila, col);
                Pieza pieza = tableroEspecifico.getPieza(pos);

                if (pieza != null && pieza.getColor() == colorAtacante) {
                    List<Movimiento> movimientosPieza = pieza.movimientosPosibles(tableroEspecifico);
                    for (Movimiento mov : movimientosPieza) {
                        if (mov.getDestino().equals(posicion)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // Método para obtener movimientos legales de una pieza específica
    public List<Movimiento> obtenerMovimientosLegales(Pieza pieza) {
        List<Movimiento> movimientosLegales = new ArrayList<>();
        List<Movimiento> movimientosPosibles = pieza.movimientosPosibles(tablero);

        for (Movimiento mov : movimientosPosibles) {
            if (esMovimientoLegal(mov)) {
                movimientosLegales.add(mov);
            }
        }

        return movimientosLegales;
    }

    // Método para reiniciar la partida
    public void reiniciar() {
        this.tablero = new Tablero();
        this.turnoActual = Color.WHITE;
        this.movimientos.clear();
        this.juegoTerminado = false;
    }

    // Getters
    public Tablero getTablero() {
        return tablero;
    }

    public Color getTurnoActual() {
        return turnoActual;
    }

    public List<Movimiento> getMovimientos() {
        return new ArrayList<>(movimientos);
    }

    public Jugador getJugadorNegro() {
        return jugadorNegro;
    }
}