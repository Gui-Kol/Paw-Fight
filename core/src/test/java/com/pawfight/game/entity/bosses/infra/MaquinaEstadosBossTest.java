package com.pawfight.game.entity.bosses.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("MaquinaEstadosBoss")
class MaquinaEstadosBossTest {

    private enum EstadoFake { PARADO, AVISO, EXECUCAO }

    @Test
    @DisplayName("Inicia no estado inicial com cronômetro zerado")
    void estadoInicial() {
        MaquinaEstadosBoss<EstadoFake> maquina = new MaquinaEstadosBoss<>(EstadoFake.PARADO);

        assertEquals(EstadoFake.PARADO, maquina.getEstado());
        assertEquals(0f, maquina.getTimer());
        assertTrue(maquina.is(EstadoFake.PARADO));
        assertFalse(maquina.is(EstadoFake.AVISO));
    }

    @Test
    @DisplayName("avancar acumula o tempo do estado atual")
    void avancarAcumula() {
        MaquinaEstadosBoss<EstadoFake> maquina = new MaquinaEstadosBoss<>(EstadoFake.PARADO);

        maquina.avancar(0.5f);
        maquina.avancar(0.25f);

        assertEquals(0.75f, maquina.getTimer(), 0.0001f);
    }

    @Test
    @DisplayName("mudar troca o estado e zera o cronômetro")
    void mudarZeraTimer() {
        MaquinaEstadosBoss<EstadoFake> maquina = new MaquinaEstadosBoss<>(EstadoFake.PARADO);
        maquina.avancar(3f);

        maquina.mudar(EstadoFake.EXECUCAO);

        assertEquals(EstadoFake.EXECUCAO, maquina.getEstado());
        assertEquals(0f, maquina.getTimer());
    }

    @Test
    @DisplayName("reiniciarTimer zera o cronômetro sem trocar de estado")
    void reiniciarTimerMantemEstado() {
        MaquinaEstadosBoss<EstadoFake> maquina = new MaquinaEstadosBoss<>(EstadoFake.AVISO);
        maquina.avancar(1f);

        maquina.reiniciarTimer();

        assertEquals(EstadoFake.AVISO, maquina.getEstado());
        assertEquals(0f, maquina.getTimer());
    }
}
