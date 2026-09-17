package com.pawfight.game.entity.component;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.TirosTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CombateComponent — guarda global de inimigos na sala")
class CombateComponentTest {

    /** Modelo de tiro mínimo: cadência alta para nunca disparar nos testes. */
    private static class TiroStub extends TirosTemplate {
        List<EnemyTemplate> inimigosRecebidos;

        TiroStub() {
            super();
        }

        @Override
        public void setInimigos(List<EnemyTemplate> inimigos) {
            super.setInimigos(inimigos);
            this.inimigosRecebidos = inimigos;
        }

        @Override protected int definirTamanhoPadrao() { return 0; }
        @Override protected float definirDuracao() { return 1f; }
        @Override protected float definirIntervalo() { return 10f; }
        @Override protected Texture randomTex() { return null; }
        @Override protected Texture singleTex() { return null; }
        @Override protected Rectangle gerarHitBox() { return new Rectangle(); }
        @Override protected TirosTemplate obterDoPool(PlayerTemplate player) { return null; }
    }

    private CombateComponent combate;
    private TiroStub modelo;
    private PlayerTemplate player;

    @BeforeEach
    void setUp() {
        combate = new CombateComponent();
        modelo = new TiroStub();
        combate.addModeloTiro(modelo);
        combate.setPodeAtacar(true);

        player = mock(PlayerTemplate.class);
        when(player.getCadenciaTiro()).thenReturn(1f);
        when(player.getDuracaoTiro()).thenReturn(1f);
    }

    @Test
    @DisplayName("Não processa disparo quando não há inimigos na sala (lista vazia)")
    void naoDisparaSemInimigos() {
        combate.setFonteInimigos(new ArrayList<EnemyTemplate>());

        combate.processarTirosAutomaticos(player, 0.5f);

        assertEquals(0f, modelo.getIntervalo(), "Intervalo não deveria avançar sem inimigos");
        assertNull(modelo.inimigosRecebidos, "A lista de inimigos não deveria ser propagada ao modelo");
    }

    @Test
    @DisplayName("Não processa disparo quando a fonte de inimigos nunca foi injetada")
    void naoDisparaSemFonteInjetada() {
        combate.processarTirosAutomaticos(player, 0.5f);

        assertEquals(0f, modelo.getIntervalo(), "Intervalo não deveria avançar sem fonte de inimigos");
    }

    @Test
    @DisplayName("Com inimigos na sala, acumula intervalo e propaga a lista ao modelo")
    void processaComInimigos() {
        EnemyTemplate inimigo = mock(EnemyTemplate.class);
        when(inimigo.isMorto()).thenReturn(false);
        List<EnemyTemplate> inimigos = new ArrayList<>();
        inimigos.add(inimigo);
        combate.setFonteInimigos(inimigos);

        combate.processarTirosAutomaticos(player, 0.5f);

        assertTrue(modelo.getIntervalo() > 0f, "Intervalo deveria avançar com inimigos na sala");
        assertSame(inimigos, modelo.inimigosRecebidos, "O modelo deve receber a lista de inimigos da sala");
    }

    @Test
    @DisplayName("Não processa quando podeAtacar está desligado, mesmo com inimigos")
    void naoProcessaSemPodeAtacar() {
        combate.setPodeAtacar(false);
        List<EnemyTemplate> inimigos = new ArrayList<>();
        inimigos.add(mock(EnemyTemplate.class));
        combate.setFonteInimigos(inimigos);

        combate.processarTirosAutomaticos(player, 0.5f);

        assertEquals(0f, modelo.getIntervalo());
    }
}
