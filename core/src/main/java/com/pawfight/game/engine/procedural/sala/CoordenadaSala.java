package com.pawfight.game.engine.procedural.sala;

public record CoordenadaSala(int x, int y) {
    public static CoordenadaSala de(Sala sala) {
        return new CoordenadaSala(sala.getX(), sala.getY());
    }
}
