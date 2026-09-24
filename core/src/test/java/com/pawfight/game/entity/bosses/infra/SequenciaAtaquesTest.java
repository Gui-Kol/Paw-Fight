package com.pawfight.game.entity.bosses.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SequenciaAtaques")
class SequenciaAtaquesTest {

    @Test
    @DisplayName("Primeiro evento sai imediatamente na ativação")
    void primeiroEventoImediato() {
        SequenciaAtaques sequencia = new SequenciaAtaques(0.2f);
        sequencia.iniciar(3);
        AtomicInteger eventos = new AtomicInteger();

        sequencia.atualizar(0f, eventos::incrementAndGet);

        assertEquals(1, eventos.get());
        assertFalse(sequencia.concluida());
    }

    @Test
    @DisplayName("Eventos são espaçados pelo intervalo")
    void eventosEspacados() {
        SequenciaAtaques sequencia = new SequenciaAtaques(0.2f);
        sequencia.iniciar(3);
        AtomicInteger eventos = new AtomicInteger();

        sequencia.atualizar(0.01f, eventos::incrementAndGet);   // t=0 → 1º
        assertEquals(1, eventos.get());
        sequencia.atualizar(0.19f, eventos::incrementAndGet);   // t=0,2 → 2º
        assertEquals(2, eventos.get());
        sequencia.atualizar(0.2f, eventos::incrementAndGet);    // t=0,4 → 3º
        assertEquals(3, eventos.get());
        assertTrue(sequencia.concluida());
    }

    @Test
    @DisplayName("Delta grande emite todos os eventos devidos no mesmo frame")
    void deltaGrandeEmiteTudo() {
        SequenciaAtaques sequencia = new SequenciaAtaques(0.2f);
        sequencia.iniciar(3);
        AtomicInteger eventos = new AtomicInteger();

        sequencia.atualizar(5f, eventos::incrementAndGet);

        assertEquals(3, eventos.get());
        assertTrue(sequencia.concluida());
    }

    @Test
    @DisplayName("Sequência concluída não emite mais eventos")
    void concluidaNaoEmite() {
        SequenciaAtaques sequencia = new SequenciaAtaques(0.1f);
        sequencia.iniciar(1);
        AtomicInteger eventos = new AtomicInteger();

        sequencia.atualizar(1f, eventos::incrementAndGet);
        sequencia.atualizar(1f, eventos::incrementAndGet);

        assertEquals(1, eventos.get());
    }

    @Test
    @DisplayName("cancelar descarta os eventos pendentes")
    void cancelarDescartaPendentes() {
        SequenciaAtaques sequencia = new SequenciaAtaques(0.2f);
        sequencia.iniciar(3);
        AtomicInteger eventos = new AtomicInteger();

        sequencia.atualizar(0f, eventos::incrementAndGet);
        sequencia.cancelar();
        sequencia.atualizar(10f, eventos::incrementAndGet);

        assertEquals(1, eventos.get());
        assertTrue(sequencia.concluida());
    }
}
