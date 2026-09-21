package com.pawfight.game.entity.tiro;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.HeadlessGdx;
import com.pawfight.game.entity.player.PlayerTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TirosTemplate — animação por frames obrigatória")
class TirosTemplateTest {

    @BeforeAll
    static void initGdx() {
        HeadlessGdx.ensureGdx();
    }

    // Tiro de teste sem contexto gráfico: frames definidos via campo estático ANTES da construção, pois o construtor base lê definirQuantidadeFrames() antes dos campos da subclasse existirem.
    private static class TiroAnimacaoTeste extends TirosTemplate {
        static int framesDoProximoTiro = 1;
        private final Texture texturaFixa;

        TiroAnimacaoTeste(Texture texturaFixa) {
            super();
            this.texturaFixa = texturaFixa;
            this.texture = texturaFixa;
        }

        TiroAnimacaoTeste(int x, int y, int dano, int tamanho, PlayerTemplate player, Texture texturaFixa) {
            super(x, y, dano, tamanho, player);
            this.texturaFixa = texturaFixa;
            this.texture = texturaFixa;
        }

        @Override protected int definirQuantidadeFrames() { return framesDoProximoTiro; }
        @Override protected int definirTamanhoPadrao() { return 0; }
        @Override protected float definirDuracao() { return 1f; }
        @Override protected float definirIntervalo() { return 1f; }
        @Override protected Texture randomTex() { return texturaFixa; }
        @Override protected Texture singleTex() { return texturaFixa; }
        @Override protected Rectangle gerarHitBox() { return new Rectangle(xHitBox, yHitBox, tamanho, tamanho); }
        @Override protected TirosTemplate obterDoPool(PlayerTemplate player) { return null; }
    }

    private static Texture texturaMockada(int largura, int altura) {
        Texture textura = mock(Texture.class);
        when(textura.getWidth()).thenReturn(largura);
        when(textura.getHeight()).thenReturn(altura);
        return textura;
    }

    @Test
    @DisplayName("Tiro com 1 frame: exibe sempre o único frame (equivalente ao sprite estático atual)")
    void tiroComUmFrame() {
        TiroAnimacaoTeste.framesDoProximoTiro = 1;
        TiroAnimacaoTeste tiro = new TiroAnimacaoTeste(texturaMockada(50, 50));

        tiro.garantirAnimacao();

        assertNotNull(tiro.frameAtual());
        assertEquals(1, tiro.quantidadeFrames);
        assertEquals(0, tiro.frameAtual().getRegionX());
        assertEquals(50, tiro.frameAtual().getRegionWidth());
        assertEquals(50, tiro.frameAtual().getRegionHeight());

        // Mesmo avançando o tempo da animação, o único frame permanece
        tiro.update(0.5f);
        assertEquals(0, tiro.frameAtual().getRegionX());

        // A animação é preparada apenas uma vez (nunca recriada a cada frame)
        Animation<TextureRegion> animacaoPreparada = tiro.animacao;
        tiro.garantirAnimacao();
        assertSame(animacaoPreparada, tiro.animacao);
    }

    @Test
    @DisplayName("Tiro com 6 frames: spritesheet de 384px dividida em frames de 64px, em loop contínuo")
    void tiroComMultiplosFrames() {
        TiroAnimacaoTeste.framesDoProximoTiro = 6;
        TiroAnimacaoTeste tiro = new TiroAnimacaoTeste(texturaMockada(384, 64));

        tiro.garantirAnimacao();

        // Divisão automática: 384px / 6 frames = 64px por frame, altura completa da textura
        assertEquals(64, tiro.frameAtual().getRegionWidth());
        assertEquals(64, tiro.frameAtual().getRegionHeight());

        assertEquals(0, tiro.frameAtual().getRegionX());

        // Avanço do tempo acumulado troca o frame (0.08s por frame), sem nenhum controle manual
        tiro.tempoAnimacao = 0.1f; // frame 1
        assertEquals(64, tiro.frameAtual().getRegionX());

        tiro.tempoAnimacao = 0.25f; // frame 3
        assertEquals(192, tiro.frameAtual().getRegionX());

        // Loop: após todos os 6 frames, o ciclo recomeça no frame 0
        tiro.tempoAnimacao = 0.5f; // 6.25 ciclos → frame 0
        assertEquals(0, tiro.frameAtual().getRegionX());
    }

    @Test
    @DisplayName("O tempo padrão entre frames da animação é 0,08f e está centralizado")
    void tempoPadraoEntreFrames() {
        assertEquals(0.08f, TirosTemplate.DURACAO_FRAME_PADRAO);

        TiroAnimacaoTeste.framesDoProximoTiro = 4;
        TiroAnimacaoTeste tiro = new TiroAnimacaoTeste(texturaMockada(128, 32));
        tiro.garantirAnimacao();

        // 4 frames de 32px; apenas após 0.08s o frame troca
        tiro.tempoAnimacao = 0.05f;
        assertEquals(0, tiro.frameAtual().getRegionX());
        tiro.tempoAnimacao = 0.25f; // 3.125 frames → frame 3
        assertEquals(96, tiro.frameAtual().getRegionX());
    }

    @Test
    @DisplayName("quantidadeFrames = 0 é rejeitado na criação do tiro")
    void rejeitaZeroFrames() {
        TiroAnimacaoTeste.framesDoProximoTiro = 0;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new TiroAnimacaoTeste(texturaMockada(64, 64)));

        assertTrue(ex.getMessage().contains("maior que zero"));
    }

    @Test
    @DisplayName("quantidadeFrames negativo é rejeitado na criação do tiro")
    void rejeitaFramesNegativos() {
        TiroAnimacaoTeste.framesDoProximoTiro = -3;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> new TiroAnimacaoTeste(texturaMockada(64, 64)));

        assertTrue(ex.getMessage().contains("maior que zero"));
    }

    @Test
    @DisplayName("Spritesheet cuja largura não é divisível pela quantidade de frames é configuração inválida")
    void rejeitaSpritesheetNaoDivisivel() {
        TiroAnimacaoTeste.framesDoProximoTiro = 6;
        // 385px não é divisível por 6 → frames ficariam cortados incorretamente
        TiroAnimacaoTeste tiro = new TiroAnimacaoTeste(texturaMockada(385, 64));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            tiro::garantirAnimacao);

        assertTrue(ex.getMessage().contains("não é divisível"));
        assertNull(tiro.animacao, "A animação não deve ser construída com frames cortados");
    }

    @Test
    @DisplayName("A animação não altera dano, posição, hitbox ou movimento do tiro")
    void animacaoNaoAlteraPropriedades() {
        PlayerTemplate player = mock(PlayerTemplate.class);
        when(player.getDx()).thenReturn(100);
        when(player.getDy()).thenReturn(50);
        when(player.getForca()).thenReturn(5);
        when(player.isOlhandoEsquerda()).thenReturn(false);
        when(player.getTamanho()).thenReturn(32);

        TiroAnimacaoTeste.framesDoProximoTiro = 6;
        TiroAnimacaoTeste tiro = new TiroAnimacaoTeste(100, 50, 7, 10, player, texturaMockada(384, 64));

        assertEquals(7, tiro.getDano());
        assertEquals(100, tiro.x);
        assertEquals(50, tiro.y);
        assertEquals(10f, tiro.getHitBox().width);
        assertEquals(10f, tiro.getHitBox().height);
        assertEquals(100f, tiro.getHitBox().x);
        assertEquals(50f, tiro.getHitBox().y);

        tiro.garantirAnimacao();

        tiro.update(0.1f);
        assertEquals(0.1f, tiro.tempoAnimacao, 0.0001f);
        assertEquals(0.1f, tiro.tempoVida, 0.0001f);
        assertEquals(100, tiro.x);
        assertEquals(50, tiro.y);
        assertEquals(100f, tiro.getHitBox().x);
        assertEquals(50f, tiro.getHitBox().y);
        assertEquals(64, tiro.frameAtual().getRegionX());

        tiro.moverNaDirecao(100f, 0.1f);
        assertEquals(110, tiro.x); // 10px para a direita (direção padrão)
        assertEquals(110f, tiro.getHitBox().x);
        assertEquals(0.1f, tiro.tempoAnimacao, 0.0001f);
    }

    @Test
    @DisplayName("Textura aleatória: animação é reaproveitada enquanto a textura for a mesma e reconstruída quando muda")
    void reconstruiAnimacaoApenasQuandoTexturaMuda() {
        TiroAnimacaoTeste.framesDoProximoTiro = 1;
        Texture textura1 = texturaMockada(50, 50);
        TiroAnimacaoTeste tiro = new TiroAnimacaoTeste(textura1);

        tiro.garantirAnimacao();
        Animation<TextureRegion> primeira = tiro.animacao;

        // Mesma textura: animação é a mesma instância (nenhum objeto recriado por frame)
        tiro.garantirAnimacao();
        assertSame(primeira, tiro.animacao);

        // Textura nova (ex.: textura aleatória do TiroCoco): reconstrói uma única vez
        tiro.texture = texturaMockada(128, 50);
        tiro.garantirAnimacao();
        assertNotSame(primeira, tiro.animacao);
    }

    @Test
    @DisplayName("desenhar usa o frame atual da animação; tiro sem textura não desenha nada")
    void desenharUsaFrameAtual() {
        Batch batch = mock(Batch.class);

        TiroAnimacaoTeste.framesDoProximoTiro = 1;
        TiroAnimacaoTeste semTextura = new TiroAnimacaoTeste(null);
        semTextura.desenhar(batch);
        verify(batch, never()).draw(any(TextureRegion.class), anyFloat(), anyFloat(), anyFloat(), anyFloat());

        TiroAnimacaoTeste.framesDoProximoTiro = 1;
        TiroAnimacaoTeste tiro = new TiroAnimacaoTeste(texturaMockada(50, 50));
        tiro.x = 10;
        tiro.y = 20;
        tiro.tamanhoDraw = 30;

        tiro.desenhar(batch);

        verify(batch, times(1)).draw(any(TextureRegion.class), eq(10f), eq(20f), eq(30f), eq(30f));
    }
}
