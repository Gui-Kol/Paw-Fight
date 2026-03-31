package com.pawfight.game.engine;

import java.util.List;

public final class Validar {

    private Validar() {
    }

    public static <T> boolean validarLista(List<T> list) {
        return list != null && !list.isEmpty();
    }
}
