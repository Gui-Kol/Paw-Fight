package com.pawfight.game.entity.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Restrição temporária de movimento")
class RestricaoMovimentoComponentTest {

    @Test
    @DisplayName("Expira pelo delta e restaura a velocidade integral")
    void expiraERestauraVelocidade() {
        RestricaoMovimentoComponent restricao = new RestricaoMovimentoComponent();

        restricao.aplicar(0.35f, 2f);
        restricao.atualizar(1.5f);

        assertTrue(restricao.isAtiva());
        assertEquals(0.35f, restricao.getMultiplicador());
        assertEquals(0.5f, restricao.getTempoRestante());

        restricao.atualizar(0.5f);

        assertFalse(restricao.isAtiva());
        assertEquals(1f, restricao.getMultiplicador());
        assertEquals(0f, restricao.getTempoRestante());
    }

    @Test
    @DisplayName("Limita multiplicadores inválidos e permite remoção imediata")
    void limitaERemove() {
        RestricaoMovimentoComponent restricao = new RestricaoMovimentoComponent();

        restricao.aplicar(-2f, 3f);
        assertEquals(0.1f, restricao.getMultiplicador());

        restricao.limpar();

        assertFalse(restricao.isAtiva());
        assertEquals(1f, restricao.getMultiplicador());
    }
}
