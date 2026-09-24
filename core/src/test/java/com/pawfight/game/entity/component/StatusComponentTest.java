package com.pawfight.game.entity.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StatusComponent (queimadura e lentidão)")
class StatusComponentTest {

    private StatusComponent status;

    @BeforeEach
    void setUp() {
        status = new StatusComponent();
    }

    @Test
    @DisplayName("Queimadura com chance 1 sempre é aplicada")
    void queimaduraComChanceCerta() {
        assertTrue(status.aplicarQueimadura(2, 3f, 1f));
        assertTrue(status.isQueimando());
    }

    @Test
    @DisplayName("Queimadura com chance 0 nunca é aplicada")
    void queimaduraComChanceZero() {
        for (int i = 0; i < 50; i++) {
            StatusComponent s = new StatusComponent();
            assertFalse(s.aplicarQueimadura(2, 3f, 0f));
            assertFalse(s.isQueimando());
        }
    }

    @Test
    @DisplayName("Queimadura causa dano em ticks de 0.5s")
    void queimaduraTickDeDano() {
        status.aplicarQueimadura(4, 3f, 1f);

        // Antes do primeiro tick não há dano
        assertEquals(0, status.update(0.25f));

        // No tick de 0.5s retorna o dano por tick
        assertEquals(4, status.update(0.25f));

        // Próximo tick em 0.5s
        assertEquals(4, status.update(0.5f));
    }

    @Test
    @DisplayName("Queimadura expira após a duração e para de causar dano")
    void queimaduraExpira() {
        status.aplicarQueimadura(4, 1f, 1f);
        status.update(1f); // atinge a duração
        assertFalse(status.isQueimando());
        assertEquals(0, status.update(1f), "Sem ticks após expirar");
    }

    @Test
    @DisplayName("Reaplicar queimadura reinicia duração e tick")
    void reaplicarQueimaduraReinicia() {
        status.aplicarQueimadura(4, 3f, 1f);
        status.update(0.4f);
        assertTrue(status.aplicarQueimadura(6, 3f, 1f));
        // Tick reiniciado: 0.4s não é suficiente para o novo tick
        assertEquals(0, status.update(0.4f));
        assertEquals(6, status.update(0.1f), "Novo dano por tick deve valer");
    }

    @Test
    @DisplayName("Lentidão com chance 1 sempre é aplicada")
    void lentidaoComChanceCerta() {
        assertTrue(status.aplicarLentidao(0.5f, 2f, 1f));
        assertTrue(status.isLento());
        assertEquals(0.5f, status.getMultiplicadorVelocidade());
    }

    @Test
    @DisplayName("Lentidão com chance 0 nunca é aplicada")
    void lentidaoComChanceZero() {
        for (int i = 0; i < 50; i++) {
            StatusComponent s = new StatusComponent();
            assertFalse(s.aplicarLentidao(0.5f, 2f, 0f));
            assertEquals(1f, s.getMultiplicadorVelocidade());
        }
    }

    @Test
    @DisplayName("Lentidão expira após a duração e velocidade volta ao normal")
    void lentidaoExpira() {
        status.aplicarLentidao(0.5f, 2f, 1f);
        status.update(1f);
        assertTrue(status.isLento());
        status.update(1f); // completa a duração
        assertFalse(status.isLento());
        assertEquals(1f, status.getMultiplicadorVelocidade());
    }

    @Test
    @DisplayName("Multiplicador de lentidão é limitado ao intervalo (0, 1]")
    void lentidaoLimitaMultiplicador() {
        status.aplicarLentidao(0f, 2f, 1f);
        assertEquals(0.1f, status.getMultiplicadorVelocidade(), "Nunca abaixo de 0.1");

        StatusComponent outro = new StatusComponent();
        outro.aplicarLentidao(2f, 2f, 1f);
        assertEquals(1f, outro.getMultiplicadorVelocidade(), "Nunca acima de 1");
    }

    @Test
    @DisplayName("Sem efeitos, update não causa dano e multiplicador é 1")
    void estadoNeutro() {
        assertEquals(0, status.update(10f));
        assertEquals(1f, status.getMultiplicadorVelocidade());
        assertFalse(status.isQueimando());
        assertFalse(status.isLento());
    }

    @Test
    @DisplayName("Delta grande processa todos os ticks vencidos")
    void deltaGrandeProcessaMultiplosTicks() {
        status.aplicarQueimadura(3, 3f, 1f);
        assertEquals(9, status.update(1.5f));
    }

    @Test
    @DisplayName("Efeito mais forte de lentidão prevalece")
    void lentidaoMaisFortePrevalece() {
        status.aplicarLentidao(0.7f, 2f, 1f);
        status.aplicarLentidao(0.4f, 1f, 1f);
        assertEquals(0.4f, status.getMultiplicadorVelocidade());
        status.aplicarLentidao(0.8f, 4f, 1f);
        assertEquals(0.4f, status.getMultiplicadorVelocidade());
    }

    @Test
    @DisplayName("Política único por fonte mantém fontes independentes")
    void fontesIndependentes() {
        Object fonteA = new Object();
        Object fonteB = new Object();
        status.aplicar(new EfeitoStatus("marca", fonteA, 2f, 1f, 1, 0f,
            PoliticaAcumuloStatus.UNICO_POR_FONTE, false));
        status.aplicar(new EfeitoStatus("marca", fonteB, 2f, 1f, 1, 0f,
            PoliticaAcumuloStatus.UNICO_POR_FONTE, false));
        assertEquals(2, status.getEfeitos().size());
    }

    @Test
    @DisplayName("Limpeza de troca de sala preserva apenas efeitos persistentes")
    void limpaNaoPersistentes() {
        status.aplicar(new EfeitoStatus("temporario", this, 2f, 1f, 1, 0f,
            PoliticaAcumuloStatus.SUBSTITUIR, false));
        status.aplicar(new EfeitoStatus("persistente", this, 2f, 1f, 1, 0f,
            PoliticaAcumuloStatus.SUBSTITUIR, true));
        status.limparNaoPersistentes();
        assertFalse(status.possui("temporario"));
        assertTrue(status.possui("persistente"));
    }
}
