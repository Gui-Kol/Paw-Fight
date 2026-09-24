package com.pawfight.game.entity.bosses.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ControladorCooldown")
class ControladorCooldownTest {

    @Test
    @DisplayName("Cooldown descarregado começa pronto")
    void iniciaPronto() {
        ControladorCooldown cooldown = new ControladorCooldown(2f, false);
        assertTrue(cooldown.pronto());
    }

    @Test
    @DisplayName("Cooldown carregado só fica pronto após a duração inteira")
    void iniciaCarregado() {
        ControladorCooldown cooldown = new ControladorCooldown(2f, true);

        assertFalse(cooldown.pronto());
        cooldown.atualizar(1.5f);
        assertFalse(cooldown.pronto());
        cooldown.atualizar(0.5f);
        assertTrue(cooldown.pronto());
    }

    @Test
    @DisplayName("atualizar nunca deixa o restante negativo")
    void nuncaNegativo() {
        ControladorCooldown cooldown = new ControladorCooldown(1f, true);

        cooldown.atualizar(99f);

        assertEquals(0f, cooldown.getRestante());
        assertTrue(cooldown.pronto());
    }

    @Test
    @DisplayName("disparar religa a duração padrão")
    void dispararReliga() {
        ControladorCooldown cooldown = new ControladorCooldown(2f, false);
        cooldown.disparar();

        assertEquals(2f, cooldown.getRestante());
        assertFalse(cooldown.pronto());
    }

    @Test
    @DisplayName("disparar com duração específica sobrescreve a padrão")
    void dispararComDuracao() {
        ControladorCooldown cooldown = new ControladorCooldown(5f, false);

        cooldown.disparar(0.6f);

        assertEquals(0.6f, cooldown.getRestante());
        cooldown.atualizar(0.6f);
        assertTrue(cooldown.pronto());
    }

    @Test
    @DisplayName("cancelar libera o uso imediato")
    void cancelarLibera() {
        ControladorCooldown cooldown = new ControladorCooldown(10f, true);

        cooldown.cancelar();

        assertTrue(cooldown.pronto());
        assertEquals(0f, cooldown.getRestante());
    }

    @Test
    @DisplayName("Sem atualizar (pause) o cooldown não avança")
    void pauseCongela() {
        ControladorCooldown cooldown = new ControladorCooldown(1f, true);

        cooldown.atualizar(0.4f);
        float antes = cooldown.getRestante();

        cooldown.atualizar(0f);

        assertEquals(antes, cooldown.getRestante());
        assertFalse(cooldown.pronto());
    }
}
