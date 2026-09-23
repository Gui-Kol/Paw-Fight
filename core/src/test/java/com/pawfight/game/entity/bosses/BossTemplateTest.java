package com.pawfight.game.entity.bosses;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pawfight.game.HeadlessGdx;
import com.pawfight.game.entity.enemy.DadosInimigo;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.component.StatsComponent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("BossTemplate")
class BossTemplateTest {

    // Fase falsa que registra quantas vezes cada ataque foi delegado.
    static class FaseFake extends FaseBoss {
        int ataquesNormais = 0;
        int ataquesEspeciais = 0;

        FaseFake(String nome, float vidaLimiar) {
            super(nome, vidaLimiar);
        }

        @Override
        public void executarAtaqueNormal(BossTemplate boss) { ataquesNormais++; }

        @Override
        public void executarAtaqueEspecial(BossTemplate boss) { ataquesEspeciais++; }
    }

    // Boss mínimo (sem texturas/áudio) para exercitar o template method.
    static class BossFake extends BossTemplate {
        int mudancasFase = 0;
        int atualizacoesEstado = 0;

        BossFake() {
            super(0, 0, false, null);
        }

        @Override
        protected DadosInimigo dadosInimigo() {
            return new DadosInimigo(
                "Boss Fake", 100, 1, 0, 64, 20, 0, -10,
                null, null, null, null, null, null,
                1f, null, null
            );
        }

        @Override
        protected void definirFases(List<FaseBoss> fases) {
            fases.add(new FaseFake("Fase Inicial", 0.5f));
            fases.add(new FaseFake("Fase Final", 0f));
        }

        @Override
        public void executarAtaqueNormal() {
            FaseBoss fase = faseVigente();
            if (fase != null) fase.executarAtaqueNormal(this);
        }

        @Override
        public void executarAtaqueEspecial() {
            FaseBoss fase = faseVigente();
            if (fase != null) fase.executarAtaqueEspecial(this);
        }

        @Override
        public void mudarFase(int novaFase) {
            if (novaFase < 0 || novaFase >= fases.size()) return;
            mudancasFase++;
            faseAtual = novaFase;
            aplicarAnimacoesFase(fases.get(novaFase));
            iniciarTransicao();
        }

        @Override
        public void atualizarEstado() {
            atualizacoesEstado++;
            verificarTransicaoFase();
        }

        @Override public void updateSpriteDefinitions() { }
        @Override public EnemyTemplate cloneEnemy() { return null; }
        @Override public void andarIA(float delta) { }
        @Override public int getTamanho() { return 64; }
        @Override protected int moedasMorte() { return 0; }
        @Override public void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) { }

        FaseFake fase(int indice) { return (FaseFake) fases.get(indice); }
    }

    private BossFake boss;

    @BeforeAll
    static void initGdx() {
        HeadlessGdx.ensureGdx(); // BossTemplate loga erro se não houver fases
    }

    @BeforeEach
    void setUp() {
        boss = new BossFake();
    }

    @Test
    @DisplayName("Inicia na primeira fase com todas as fases registradas")
    void inicializacaoCorreta() {
        assertEquals(0, boss.getFaseAtual());
        assertEquals(2, boss.getFases().size());
        assertFalse(boss.isEmTransicao());
        assertSame(boss.getStats(), boss.getEntidadeEcs().getComponent(StatsComponent.class));
    }

    @Test
    @DisplayName("vidaLimiar: só transiciona com vida menor ou igual ao limiar")
    void condicaoDeTransicaoPorVidaLimiar() {
        FaseBoss fase = new FaseBoss("Teste", 0.5f) {
            @Override public void executarAtaqueNormal(BossTemplate boss) { }
            @Override public void executarAtaqueEspecial(BossTemplate boss) { }
        };

        assertFalse(fase.deveTransicionar(51, 100));
        assertTrue(fase.deveTransicionar(50, 100));
        assertTrue(fase.deveTransicionar(10, 100));
    }

    @Test
    @DisplayName("Ataque normal é delegado à fase atual")
    void delegaAtaqueNormalParaFaseAtual() {
        boss.executarAtaqueNormal();

        assertEquals(1, boss.fase(0).ataquesNormais);
        assertEquals(0, boss.fase(1).ataquesNormais);
    }

    @Test
    @DisplayName("Após mudarFase, ataques passam a ser delegados à nova fase")
    void delegaParaNovaFaseAposMudarFase() {
        boss.mudarFase(1);
        boss.executarAtaqueNormal();
        boss.executarAtaqueEspecial();

        assertEquals(0, boss.fase(0).ataquesNormais);
        assertEquals(0, boss.fase(0).ataquesEspeciais);
        assertEquals(1, boss.fase(1).ataquesNormais);
        assertEquals(1, boss.fase(1).ataquesEspeciais);
    }

    @Test
    @DisplayName("mudarFase ignora índice fora da lista de fases")
    void mudarFaseIgnoraIndiceInvalido() {
        boss.mudarFase(5);
        boss.mudarFase(-1);

        assertEquals(0, boss.getFaseAtual());
        assertEquals(0, boss.mudancasFase);
    }

    @Test
    @DisplayName("mudarFase inicia a animação de transição")
    void mudarFaseDisparaTransicao() {
        @SuppressWarnings("unchecked")
        Animation<TextureRegion> animacao = mock(Animation.class);
        boss.transicaoFase = animacao;

        boss.mudarFase(1);

        assertTrue(boss.isEmTransicao());
    }

    @Test
    @DisplayName("Transição termina quando a animação acaba e o estado volta a atualizar")
    void transicaoTerminaQuandoAnimacaoAcaba() {
        @SuppressWarnings("unchecked")
        Animation<TextureRegion> animacao = mock(Animation.class);
        when(animacao.isAnimationFinished(anyFloat())).thenReturn(true);
        boss.transicaoFase = animacao;
        boss.mudarFase(1);
        assertTrue(boss.isEmTransicao());

        boss.update(0.016f);

        assertFalse(boss.isEmTransicao());
        assertEquals(1, boss.atualizacoesEstado);
    }

    @Test
    @DisplayName("Ataques são bloqueados enquanto a transição toca")
    void ataquesBloqueadosEmTransicao() {
        @SuppressWarnings("unchecked")
        Animation<TextureRegion> animacao = mock(Animation.class);
        when(animacao.isAnimationFinished(anyFloat())).thenReturn(false);
        boss.transicaoFase = animacao;
        boss.mudarFase(1);

        boss.ataqueBasico();
        boss.ataqueEspecial();

        assertEquals(0, boss.fase(1).ataquesNormais);
        assertEquals(0, boss.fase(1).ataquesEspeciais);
    }

    @Test
    @DisplayName("atualizarEstado não transiciona com vida acima do limiar")
    void naoTransicionaAcimaDoLimiar() {
        boss.danoPorStatus(49); // 100 -> 51 (acima dos 50% da fase 1)

        boss.atualizarEstado();

        assertEquals(0, boss.getFaseAtual());
        assertEquals(0, boss.mudancasFase);
    }

    @Test
    @DisplayName("update transiciona automaticamente ao atingir o vidaLimiar")
    void transicaoAutomaticaPorVida() {
        boss.danoPorStatus(50); // 100 -> 50 (limiar da fase 1 = 50%)

        boss.update(0.016f);

        assertEquals(1, boss.getFaseAtual());
        assertEquals(1, boss.mudancasFase);
        assertEquals(1, boss.atualizacoesEstado);
    }

    @Test
    @DisplayName("Última fase nunca transiciona, mesmo com vida mínima")
    void ultimaFaseNaoTransiciona() {
        boss.mudarFase(1); // fase final (vidaLimiar 0)
        boss.danoPorStatus(99); // 100 -> 1

        boss.update(0.016f);

        assertEquals(1, boss.getFaseAtual());
        assertEquals(1, boss.mudancasFase);
    }
}
