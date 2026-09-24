package com.pawfight.game.entity.bosses.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AtaqueComAviso")
class AtaqueComAvisoTest {

    @Test
    @DisplayName("Telegraph termina somente após a antecipação inteira")
    void avisoRespeitaAntecipacao() {
        AtaqueComAviso ataque = new AtaqueComAviso(0.5f);

        assertFalse(ataque.avisoConcluido(0.49f));
        assertTrue(ataque.avisoConcluido(0.5f));
        assertTrue(ataque.avisoConcluido(1.2f));
    }

    @Test
    @DisplayName("Telegraph e aplicação são eventos separados: sem iniciarAviso não há aplicação")
    void aplicacaoSeparadaDoAviso() {
        AtaqueComAviso ataque = new AtaqueComAviso(0.1f);
        AtomicInteger aplicacoes = new AtomicInteger();

        boolean aplicou = ataque.aplicarUmaVez(aplicacoes::incrementAndGet);

        assertFalse(aplicou);
        assertEquals(0, aplicacoes.get());
    }

    @Test
    @DisplayName("Aplica o efeito uma única vez por aviso")
    void aplicaUmaVezPorAviso() {
        AtaqueComAviso ataque = new AtaqueComAviso(0.1f);
        AtomicInteger aplicacoes = new AtomicInteger();

        ataque.iniciarAviso();
        ataque.aplicarUmaVez(aplicacoes::incrementAndGet);
        ataque.aplicarUmaVez(aplicacoes::incrementAndGet);

        assertEquals(1, aplicacoes.get());
        assertTrue(ataque.isEfeitoAplicado());
    }

    @Test
    @DisplayName("cancelar impede a aplicação tardia após o aviso")
    void cancelarImpedeDanoTardio() {
        AtaqueComAviso ataque = new AtaqueComAviso(0.1f);
        AtomicInteger aplicacoes = new AtomicInteger();

        ataque.iniciarAviso();
        ataque.cancelar();

        assertFalse(ataque.aplicarUmaVez(aplicacoes::incrementAndGet));
        assertEquals(0, aplicacoes.get());
    }
}
