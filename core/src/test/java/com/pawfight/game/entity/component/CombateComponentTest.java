package com.pawfight.game.entity.component;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.system.SistemaCombate;
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

    // Modelo de tiro mínimo: cadência alta para nunca disparar nos testes.
    private static class TiroStub extends TirosTemplate {
        List<EnemyTemplate> inimigosRecebidos;
        boolean liberado;

        TiroStub() {
            super();
        }

        @Override
        public void setInimigos(List<EnemyTemplate> inimigos) {
            super.setInimigos(inimigos);
            this.inimigosRecebidos = inimigos;
        }

        @Override protected int definirTamanhoPadrao() { return 0; }
        @Override protected void definirTamanhoSprite() { }
        @Override protected int definirQuantidadeFrames() { return 1; }
        @Override protected int definirFramesPorSegundo() { return 12; }
        @Override protected int definirQuantidadeTirosPadrao() { return 1; }
        @Override protected float definirDuracao() { return 1f; }
        @Override protected float definirIntervalo() { return 10f; }
        @Override protected Texture randomTex() { return null; }
        @Override protected Texture singleTex() { return null; }
        @Override protected Rectangle gerarHitBox() { return new Rectangle(); }
        @Override protected TirosTemplate obterDoPool(PlayerTemplate player) { return null; }

        @Override
        public void liberar() {
            liberado = true;
            super.liberar();
        }
    }

    private CombateComponent combate;
    private TiroStub modelo;
    private PlayerTemplate player;
    private Engine engine;

    @BeforeEach
    void setUp() {
        combate = new CombateComponent();
        modelo = new TiroStub();
        combate.addModeloTiro(modelo);
        combate.setPodeAtacar(true);

        player = mock(PlayerTemplate.class);
        when(player.getCadenciaTiro()).thenReturn(1f);
        when(player.getDuracaoTiro()).thenReturn(1f);
        when(player.getQuantidadeDeTiros()).thenReturn(1);

        engine = new Engine();
        engine.addSystem(new SistemaCombate());
        engine.addEntity(new Entity()
            .add(combate)
            .add(new StatsComponent(10, 1, 1))
            .add(new ReferenciaEntidadeComponent(player)));
    }

    @Test
    @DisplayName("Não processa disparo quando não há inimigos na sala (lista vazia)")
    void naoDisparaSemInimigos() {
        combate.setFonteInimigos(new ArrayList<EnemyTemplate>());

        engine.update(0.5f);

        assertEquals(0f, modelo.getIntervalo(), "Intervalo não deveria avançar sem inimigos");
        assertNull(modelo.inimigosRecebidos, "A lista de inimigos não deveria ser propagada ao modelo");
    }

    @Test
    @DisplayName("Não processa disparo quando a fonte de inimigos nunca foi injetada")
    void naoDisparaSemFonteInjetada() {
        engine.update(0.5f);

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

        engine.update(0.5f);

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

        engine.update(0.5f);

        assertEquals(0f, modelo.getIntervalo());
    }

    @Test
    @DisplayName("Atualiza e remove tiro expirado pelo sistema")
    void removeTiroExpiradoPeloSistema() {
        TiroStub tiro = new TiroStub();
        combate.adicionarTiro(tiro);

        engine.update(1f);

        assertTrue(combate.getTiros().isEmpty());
        assertTrue(tiro.liberado, "O tiro removido deve ser devolvido ao pool");
    }
}
