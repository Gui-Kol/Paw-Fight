package com.pawfight.game.entity.tiro;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.HeadlessGdx;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Atirar — leque multi-tiro")
class AtirarTest {

    @BeforeAll
    static void initGdx() {
        HeadlessGdx.ensureGdx();
    }

    @Test
    @DisplayName("Com 1 projétil o desvio é zero (comportamento atual preservado)")
    void anguloComUmTiro() {
        assertEquals(0f, Atirar.anguloLeque(0, 1));
        assertEquals(0f, Atirar.anguloLeque(0, 0));
    }

    @Test
    @DisplayName("Leque de 3 tiros é simétrico: -15°, 0° e +15°")
    void anguloLequeTresTiros() {
        assertEquals(-15f, Atirar.anguloLeque(0, 3));
        assertEquals(0f, Atirar.anguloLeque(1, 3));
        assertEquals(15f, Atirar.anguloLeque(2, 3));
    }

    @Test
    @DisplayName("Leque de 2 tiros cobre as pontas: -15° e +15°")
    void anguloLequeDoisTiros() {
        assertEquals(-15f, Atirar.anguloLeque(0, 2));
        assertEquals(15f, Atirar.anguloLeque(1, 2));
    }

    private static class TiroStub extends TirosTemplate {
        TiroStub() {
            super();
        }

        @Override protected int definirTamanhoPadrao() { return 0; }
        @Override protected int definirQuantidadeFrames() { return 1; }
        @Override protected float definirDuracao() { return 1f; }
        @Override protected float definirIntervalo() { return 1f; }
        @Override protected Texture randomTex() { return null; }
        @Override protected Texture singleTex() { return null; }
        @Override protected Rectangle gerarHitBox() { return new Rectangle(); }
        @Override protected TirosTemplate obterDoPool(PlayerTemplate player) { return null; }
    }

    @Test
    @DisplayName("rotacionarDirecao gira o vetor de movimento (90°: direita → cima)")
    void rotacionarDirecaoGiraVetor() {
        TiroStub tiro = new TiroStub();
        tiro.dirMovimentoX = 1f;
        tiro.dirMovimentoY = 0f;

        tiro.rotacionarDirecao(90f);

        assertEquals(0f, tiro.dirMovimentoX, 0.0001f);
        assertEquals(1f, tiro.dirMovimentoY, 0.0001f);
    }

    @Test
    @DisplayName("rotacionarDirecao(0) não altera a direção")
    void rotacionarZeroNaoAlteraDirecao() {
        TiroStub tiro = new TiroStub();
        tiro.dirMovimentoX = 0.6f;
        tiro.dirMovimentoY = 0.8f;

        tiro.rotacionarDirecao(0f);

        assertEquals(0.6f, tiro.dirMovimentoX);
        assertEquals(0.8f, tiro.dirMovimentoY);
    }

    private PlayerTemplate playerCom(int quantidadeDeTiros) {
        PlayerTemplate player = mock(PlayerTemplate.class);
        when(player.getCadenciaTiro()).thenReturn(1f);
        when(player.getDuracaoTiro()).thenReturn(1f);
        when(player.getQuantidadeDeTiros()).thenReturn(quantidadeDeTiros);
        return player;
    }

    private TirosTemplate modeloProntoParaDisparo() {
        TirosTemplate modelo = mock(TirosTemplate.class);
        when(modelo.getCadencia()).thenReturn(1f);
        when(modelo.getDuracao()).thenReturn(1f);
        when(modelo.getIntervalo()).thenReturn(0f);
        return modelo;
    }

    @Test
    @DisplayName("Rajada de 1: um projétil do pool, sem desvio angular")
    void rajadaDeUm() {
        Atirar atirar = new Atirar();
        PlayerTemplate player = playerCom(1);
        TirosTemplate modelo = modeloProntoParaDisparo();
        TirosTemplate tiro = mock(TirosTemplate.class);
        when(modelo.obterDoPool(player)).thenReturn(tiro);

        atirar.atira(List.of(modelo), player, 1f, new ArrayList<EnemyTemplate>());

        verify(player, times(1)).adicionarTiro(tiro);
        verify(tiro).rotacionarDirecao(0f);
    }

    @Test
    @DisplayName("Rajada de 3: três projéteis do pool, em leque -15°/0°/+15°")
    void rajadaDeTresEmLeque() {
        Atirar atirar = new Atirar();
        PlayerTemplate player = playerCom(3);
        TirosTemplate modelo = modeloProntoParaDisparo();
        TirosTemplate tiro1 = mock(TirosTemplate.class);
        TirosTemplate tiro2 = mock(TirosTemplate.class);
        TirosTemplate tiro3 = mock(TirosTemplate.class);
        when(modelo.obterDoPool(player)).thenReturn(tiro1, tiro2, tiro3);

        atirar.atira(List.of(modelo), player, 1f, new ArrayList<EnemyTemplate>());

        verify(modelo, times(3)).obterDoPool(player);
        verify(tiro1).rotacionarDirecao(-15f);
        verify(tiro2).rotacionarDirecao(0f);
        verify(tiro3).rotacionarDirecao(15f);
        verify(player, times(3)).adicionarTiro(any(TirosTemplate.class));
    }
}
