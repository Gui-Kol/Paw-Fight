package com.pawfight.game.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Validar")
class ValidarTest {

    @Test
    @DisplayName("Lista nula é inválida")
    void listaNula() {
        assertFalse(Validar.validarLista(null));
    }

    @Test
    @DisplayName("Lista vazia é inválida")
    void listaVazia() {
        assertFalse(Validar.validarLista(List.of()));
    }

    @Test
    @DisplayName("Lista com elementos é válida")
    void listaComElementos() {
        assertTrue(Validar.validarLista(List.of("a")));
    }
}
