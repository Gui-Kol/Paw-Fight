package com.pawfight.game.entity.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.pawfight.game.HeadlessGdx;
import com.pawfight.game.engine.ecs.GerenciadorEcs;
import com.pawfight.game.entity.component.CombateComponent;
import com.pawfight.game.entity.component.ReferenciaEntidadeComponent;
import com.pawfight.game.entity.component.StatsComponent;
import com.pawfight.game.entity.component.StatusComponent;
import com.pawfight.game.entity.enemy.DadosInimigo;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.TirosTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Reproduz o wiring do mundo: entidade do player real no GerenciadorEcs (SistemaStats/SistemaStatus/SistemaCombate),
// CombateComponent com modelo de tiro real (pool de verdade) e inimigo vivo na fonte de alvos.
@DisplayName("Player no ECS — dano/hurt e tiro automático")
class PlayerEcsIntegracaoTest {

    // Inimigo mínimo (mesma fixture dos testes de boss).
    private static class InimigoFake extends EnemyTemplate {
        InimigoFake(PlayerTemplate player) {
            super(100, 100, false, player);
        }

        @Override protected DadosInimigo dadosInimigo() {
            return new DadosInimigo("Fake", 50, 1, 0, 16, 8, 0, 0,
                null, null, null, null, null, null, 1f, null, null);
        }
        @Override public void ataqueBasico() { }
        @Override public void ataqueEspecial() { }
        @Override public void updateSpriteDefinitions() { }
        @Override public EnemyTemplate cloneEnemy() { return new InimigoFake(player); }
        @Override public void andarIA(float delta) { }
        @Override public int getTamanho() { return 16; }
        @Override protected int moedasMorte() { return 0; }
        @Override public void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) { }
    }

    // Tiro mínimo com pool real, como os tiros de produção (modelo cria pool; obterDoPool reinicia).
    private static class TiroFake extends TirosTemplate {
        private Pool<TiroFake> pool;

        TiroFake(PlayerTemplate player) {
            super(player.getDx(), player.getDy(), 5, 0, player);
            pool = new Pool<TiroFake>(2, 8) {
                @Override protected TiroFake newObject() {
                    return new TiroFake();
                }
            };
        }

        private TiroFake() {
            super();
        }

        @Override protected int definirTamanhoPadrao() { return 0; }
        @Override protected void definirTamanhoSprite() { }
        @Override protected int definirQuantidadeFrames() { return 1; }
        @Override protected int definirFramesPorSegundo() { return 12; }
        @Override protected int definirQuantidadeTirosPadrao() { return 1; }
        @Override protected float definirDuracao() { return 2f; }
        @Override protected float definirIntervalo() { return 0.5f; }
        @Override protected Texture randomTex() { return null; }
        @Override protected Texture singleTex() { return null; }
        @Override protected Rectangle gerarHitBox() { return new Rectangle(0, 0, 4, 4); }

        @Override
        protected TirosTemplate obterDoPool(PlayerTemplate player) {
            TiroFake t = pool.obtain();
            t.reiniciarBase(player.getDx(), player.getDy(), 5, 0, player);
            t.inimigos = inimigos;
            t.hitBox.set(0, 0, 4, 4);
            t.ownerPool = pool;
            return t;
        }
    }

    private GerenciadorEcs ecs;
    private CombateComponent combate;
    private StatsComponent stats;
    private PlayerTemplate player;
    private final List<EnemyTemplate> inimigos = new ArrayList<>();

    @BeforeAll
    static void iniciarGdx() {
        HeadlessGdx.ensureGdx();
    }

    @BeforeEach
    void montar() {
        ecs = new GerenciadorEcs();
        player = mock(PlayerTemplate.class);
        when(player.isOlhandoEsquerda()).thenReturn(false);
        when(player.getCadenciaTiro()).thenReturn(1f);
        when(player.getDuracaoTiro()).thenReturn(1f);
        when(player.getQuantidadeDeTiros()).thenReturn(1);
        when(player.getHitBox()).thenReturn(new Rectangle(0, 0, 20, 20));

        stats = new StatsComponent(100, 5, 100);
        combate = new CombateComponent();
        // O mock do player replica a ponte real: PlayerTemplate.adicionarTiro → CombateComponent.adicionarTiro
        org.mockito.Mockito.doAnswer(invocacao -> {
            combate.adicionarTiro(invocacao.getArgument(0));
            return null;
        }).when(player).adicionarTiro(org.mockito.ArgumentMatchers.any(TirosTemplate.class));
        combate.addModeloTiro(new TiroFake(player));
        combate.setFonteInimigos(inimigos);
        combate.setPodeAtacar(true);

        StatusComponent status = new StatusComponent();
        status.setAlvo(player);

        Entity entidade = new Entity();
        entidade.add(stats);
        entidade.add(combate);
        entidade.add(status);
        entidade.add(new ReferenciaEntidadeComponent(player));
        ecs.adicionarEntidade(entidade);

        inimigos.clear();
        inimigos.add(new InimigoFake(player));
    }

    @Test
    @DisplayName("Com inimigo vivo e podeAtacar, o tiro sai após a cadência")
    void tiroAutomaticoSai() {
        ecs.atualizar(0.2f);
        assertTrue(combate.getTiros().isEmpty(), "Antes da cadência (0,5s) não deve haver tiro");

        ecs.atualizar(0.4f); // total 0,6s >= cadência (0,5s)
        assertFalse(combate.getTiros().isEmpty(), "O player deveria ter disparado um projétil");
    }

    @Test
    @DisplayName("Dano liga hurt; timers do ECS fazem hurt expirar após 0,5s")
    void hurtExpiraComUpdateDoEcs() {
        assertTrue(stats.aplicarDano(1), "Dano deveria ser aplicado");
        assertTrue(stats.isHurt(), "Dano deveria acionar o estado hurt");

        ecs.atualizar(0.2f);
        assertTrue(stats.isHurt(), "Hurt ainda deve estar ativo antes de 0,5s");

        ecs.atualizar(0.4f); // total 0,6s
        assertFalse(stats.isHurt(), "Hurt deveria ter expirado — a animação não pode travar");
    }

    @Test
    @DisplayName("Cooldown de dano expira pelo ECS, permitindo novo dano")
    void cooldownDeDanoExpira() {
        assertTrue(stats.aplicarDano(1));
        assertFalse(stats.aplicarDano(1), "Cooldown de 0,5s bloqueia dano imediato");

        ecs.atualizar(0.6f);

        assertTrue(stats.aplicarDano(1), "Após o cooldown (via ECS) o player deve voltar a tomar dano");
    }
}
