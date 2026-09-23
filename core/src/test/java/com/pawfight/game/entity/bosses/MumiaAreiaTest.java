package com.pawfight.game.entity.bosses;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.HeadlessGdx;
import com.pawfight.game.entity.enemy.DadosInimigo;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Múmia da Areia")
class MumiaAreiaTest {

    private static class MumiaTestavel extends MumiaAreia {
        MumiaTestavel(PlayerTemplate player) {
            super(0, 0, false, player);
        }

        @Override
        protected EnemyTemplate criarInvocacao(int indice) {
            return new MinionFake(getDx() + indice * 10, getDy(), getPlayer());
        }
    }

    private static class MinionFake extends EnemyTemplate {
        MinionFake(int x, int y, PlayerTemplate player) {
            super(x, y, false, player);
        }

        @Override
        protected DadosInimigo dadosInimigo() {
            return new DadosInimigo("Múmia menor", 5, 1, 0, 16, 8, 0, 0,
                null, null, null, null, null, null, 1f, null, null);
        }

        @Override public void ataqueBasico() { }
        @Override public void ataqueEspecial() { }
        @Override public void updateSpriteDefinitions() { }
        @Override public EnemyTemplate cloneEnemy() { return new MinionFake(getDx(), getDy(), player); }
        @Override public void andarIA(float delta) { }
        @Override public int getTamanho() { return 16; }
        @Override protected int moedasMorte() { return 0; }
        @Override public void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) { }
    }

    private PlayerTemplate player;
    private MumiaTestavel boss;

    @BeforeAll
    static void iniciarGdx() {
        HeadlessGdx.ensureGdx();
    }

    @BeforeEach
    void preparar() {
        player = mock(PlayerTemplate.class);
        when(player.getDx()).thenReturn(0);
        when(player.getDy()).thenReturn(0);
        when(player.getHitBox()).thenReturn(new Rectangle(0, 0, 30, 30));
        when(player.getHitboxSize()).thenReturn(30);
        when(player.isMorto()).thenReturn(false);
        when(player.isPause()).thenReturn(false);
        boss = new MumiaTestavel(player);
        boss.setEnemiesList(new ArrayList<>());
    }

    @Test
    @DisplayName("Transiciona exatamente nos limiares de 65% e 30%")
    void transicoesNosLimiares() {
        boss.getStats().setVida(313);
        boss.update(0.01f);
        assertEquals(0, boss.getFaseAtual());

        boss.getStats().setVida(312);
        boss.update(0.01f);
        assertEquals(1, boss.getFaseAtual());

        boss.getStats().setVida(144);
        boss.update(0.01f);
        assertEquals(2, boss.getFaseAtual());
    }

    @Test
    @DisplayName("Dano que cruza dois limiares entra na fase final e regenera uma vez")
    void cruzaDoisLimiaresERegenera() {
        boss.getStats().setVida(100);

        boss.update(0.01f);

        assertEquals(2, boss.getFaseAtual());
        assertEquals(148, boss.getStats().getVida());
        assertTrue(boss.isRegeneracaoUsada());

        boss.update(1f);
        assertEquals(148, boss.getStats().getVida());
    }

    @Test
    @DisplayName("Golpe corpo a corpo respeita aviso e causa um único contato")
    void golpeAplicaUmaVez() {
        boss.update(0.01f);
        assertEquals(MumiaAreia.Estado.AVISO_GOLPE, boss.getEstado());
        verify(player, never()).dano(org.mockito.ArgumentMatchers.anyInt());

        boss.update(0.6f);
        boss.update(1f);

        verify(player).dano(5);
    }

    @Test
    @DisplayName("Faixa aplica uma única restrição temporária após o aviso")
    void faixaAplicaRestricaoUmaVez() {
        boss.getStats().setVida(250);
        boss.update(0.01f);
        assertEquals(MumiaAreia.Estado.AVISO_FAIXA, boss.getEstado());

        boss.update(0.55f);
        boss.update(0.1f);

        verify(player).aplicarRestricaoMovimento(0.35f, 2f);
    }

    @Test
    @DisplayName("Maldição de área causa dano uma vez dentro do raio")
    void maldicaoAplicaUmaVez() {
        boss.getStats().setVida(250);
        boss.update(0.01f);
        boss.update(0.55f);
        boss.update(0.01f);
        boss.update(0.4f);
        boss.update(0.01f);
        assertEquals(MumiaAreia.Estado.AVISO_MALDICAO, boss.getEstado());

        boss.update(0.8f);
        boss.update(1f);

        verify(player).dano(4);
    }

    @Test
    @DisplayName("Fase final mantém no máximo duas invocações")
    void limitaInvocacoes() {
        boss.getStats().setVida(100);
        boss.update(14.1f);

        List<EnemyTemplate> criadas = new ArrayList<>();
        boss.drenarInvocacoes(criadas);

        assertEquals(2, criadas.size());
        assertEquals(2, boss.getQuantidadeInvocacoesAtivas());
        assertTrue(criadas.stream().allMatch(inimigo -> inimigo.getInvocador() == boss));

        boss.update(30f);
        List<EnemyTemplate> extras = new ArrayList<>();
        boss.drenarInvocacoes(extras);
        assertEquals(0, extras.size());
    }

    @Test
    @DisplayName("Pause congela estados, cooldowns e ataques")
    void pausaCongelaBoss() {
        when(player.isPause()).thenReturn(true);

        boss.update(20f);

        assertEquals(MumiaAreia.Estado.APROXIMACAO, boss.getEstado());
        verify(player, never()).dano(org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("Ausência de alvo não produz ataque nem exceção")
    void alvoAusente() {
        MumiaAreia semAlvo = new MumiaAreia(0, 0, false, null);

        assertDoesNotThrow(() -> semAlvo.update(30f));
        assertEquals(MumiaAreia.Estado.APROXIMACAO, semAlvo.getEstado());
    }

    @Test
    @DisplayName("Morte durante aviso cancela efeito tardio e libera restrição")
    void morteDuranteAviso() {
        boss.getStats().setVida(250);
        boss.update(0.01f);
        assertEquals(MumiaAreia.Estado.AVISO_FAIXA, boss.getEstado());

        boss.getStats().aplicarDanoDireto(9999);
        boss.update(1f);

        assertEquals(MumiaAreia.Estado.MORTE, boss.getEstado());
        verify(player, never()).aplicarRestricaoMovimento(org.mockito.ArgumentMatchers.anyFloat(),
            org.mockito.ArgumentMatchers.anyFloat());
        verify(player, atLeastOnce()).removerRestricaoMovimento();
    }

    @Test
    @DisplayName("Faixa é liberada na troca para a fase final")
    void trocaDeFaseLiberaFaixa() {
        boss.getStats().setVida(250);
        boss.update(0.01f);
        boss.update(0.55f);
        verify(player, times(1)).aplicarRestricaoMovimento(0.35f, 2f);

        boss.getStats().setVida(140);
        boss.update(0.01f);

        assertEquals(2, boss.getFaseAtual());
        verify(player, atLeastOnce()).removerRestricaoMovimento();
    }
}
