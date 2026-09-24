package com.pawfight.game.entity.bosses.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("JanelaDeContato")
class JanelaDeContatoTest {

    @Test
    @DisplayName("Aplica no máximo uma vez por abertura")
    void umaAplicacaoPorAbertura() {
        JanelaDeContato janela = new JanelaDeContato();
        janela.abrir();
        AtomicInteger aplicacoes = new AtomicInteger();

        assertTrue(janela.aplicarUmaVez(aplicacoes::incrementAndGet));
        assertFalse(janela.aplicarUmaVez(aplicacoes::incrementAndGet));
        assertEquals(1, aplicacoes.get());
        assertTrue(janela.isAplicado());
    }

    @Test
    @DisplayName("Nova abertura permite nova aplicação")
    void reabrirPermiteNovaAplicacao() {
        JanelaDeContato janela = new JanelaDeContato();
        AtomicInteger aplicacoes = new AtomicInteger();

        janela.abrir();
        janela.aplicarUmaVez(aplicacoes::incrementAndGet);
        janela.abrir();
        janela.aplicarUmaVez(aplicacoes::incrementAndGet);

        assertEquals(2, aplicacoes.get());
    }

    @Test
    @DisplayName("fechar impede aplicação tardia (cancelamento)")
    void fecharImpedeAplicacaoTardia() {
        JanelaDeContato janela = new JanelaDeContato();
        janela.abrir();

        janela.fechar();

        assertTrue(janela.isAplicado());
        assertFalse(janela.aplicarUmaVez(() -> { }));
    }

    @Test
    @DisplayName("Janela recém-criada não aplica sem abertura")
    void fechadaPorPadrao() {
        JanelaDeContato janela = new JanelaDeContato();

        assertTrue(janela.isAplicado());
        assertFalse(janela.aplicarUmaVez(() -> { }));
    }
}
