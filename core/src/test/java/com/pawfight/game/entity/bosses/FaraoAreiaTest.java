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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Faraó da Areia")
class FaraoAreiaTest {

    private static class FaraoTestavel extends FaraoAreia {
        FaraoTestavel(PlayerTemplate player) {
            super(0, 0, false, player);
        }

        @Override
        protected EnemyTemplate criarInvocacao(int indice) {
            return new GuardiaoFake(getDx() + indice * 10, getDy(), getPlayer());
        }
    }

    private static class GuardiaoFake extends EnemyTemplate {
        GuardiaoFake(int x, int y, PlayerTemplate player) {
            super(x, y, false, player);
        }

        @Override
        protected DadosInimigo dadosInimigo() {
            return new DadosInimigo("Guardião", 5, 1, 0, 16, 8, 0, 0,
                null, null, null, null, null, null, 1f, null, null);
        }

        @Override public void ataqueBasico() { }
        @Override public void ataqueEspecial() { }
        @Override public void updateSpriteDefinitions() { }
        @Override public EnemyTemplate cloneEnemy() { return new GuardiaoFake(getDx(), getDy(), player); }
        @Override public void andarIA(float delta) { }
        @Override public int getTamanho() { return 16; }
        @Override protected int moedasMorte() { return 0; }
        @Override public void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) { }
    }

    private PlayerTemplate player;
    private Rectangle hitboxPlayer;
    private FaraoTestavel boss;

    @BeforeAll
    static void iniciarGdx() {
        HeadlessGdx.ensureGdx();
    }

    @BeforeEach
    void preparar() {
        player = mock(PlayerTemplate.class);
        hitboxPlayer = new Rectangle(300, 0, 30, 30);
        when(player.getDx()).thenAnswer(invocacao -> Math.round(hitboxPlayer.x));
        when(player.getDy()).thenAnswer(invocacao -> Math.round(hitboxPlayer.y));
        when(player.getHitBox()).thenReturn(hitboxPlayer);
        when(player.getHitboxSize()).thenReturn(30);
        when(player.isMorto()).thenReturn(false);
        when(player.isPause()).thenReturn(false);
        boss = new FaraoTestavel(player);
        boss.setEnemiesList(new ArrayList<>());
    }

    @Test
    @DisplayName("Transiciona exatamente nos limiares de 65% e 30%")
    void transicoesNosLimiares() {
        boss.getStats().setVida(365);
        boss.update(0.01f);
        assertEquals(0, boss.getFaseAtual());

        boss.getStats().setVida(364);
        boss.update(0.01f);
        assertEquals(1, boss.getFaseAtual());

        boss.getStats().setVida(168);
        boss.update(0.01f);
        assertEquals(2, boss.getFaseAtual());
    }

    @Test
    @DisplayName("Dano que cruza dois limiares entra diretamente na fase final")
    void cruzaDoisLimiares() {
        boss.getStats().setVida(100);

        boss.update(0.01f);

        assertEquals(2, boss.getFaseAtual());
    }

    @Test
    @DisplayName("Salva cria três projéteis em intervalos de 0,2 segundo")
    void salvaEspacada() {
        boss.update(0.01f);
        assertEquals(FaraoAreia.Estado.SALVA_PROJETEIS, boss.getEstado());

        boss.update(0.01f);
        assertEquals(1, boss.getQuantidadeProjeteisAtivos());

        boss.update(0.19f);
        assertEquals(2, boss.getQuantidadeProjeteisAtivos());

        boss.update(0.2f);
        assertEquals(3, boss.getQuantidadeProjeteisAtivos());
    }

    @Test
    @DisplayName("Projétil causa somente um contato e é consumido")
    void projetilColideUmaVez() {
        hitboxPlayer.setPosition(100, 0);
        boss.update(0.01f);
        boss.update(0.01f);

        boss.update(0.2f);
        boss.update(0.01f);

        verify(player, times(1)).dano(5);
    }

    @Test
    @DisplayName("Tempestade anuncia zona fixa, restringe e expira em quatro segundos")
    void tempestadeAnunciaERemoveRestricao() {
        hitboxPlayer.setPosition(200, 100);
        boss.getStats().setVida(300);
        boss.update(0.01f);
        assertEquals(FaraoAreia.Estado.AVISO_TEMPESTADE, boss.getEstado());
        assertEquals(215f, boss.getZonaX());
        assertEquals(115f, boss.getZonaY());

        hitboxPlayer.setPosition(205, 105);
        boss.update(0.8f);
        assertEquals(FaraoAreia.Estado.TEMPESTADE, boss.getEstado());
        verify(player, never()).aplicarRestricaoMovimento(
            org.mockito.ArgumentMatchers.anyFloat(), org.mockito.ArgumentMatchers.anyFloat());

        boss.update(2f);
        verify(player).aplicarRestricaoMovimento(0.6f, 0.2f);

        boss.update(2f);
        assertEquals(FaraoAreia.Estado.RECUPERACAO, boss.getEstado());
        verify(player, atLeastOnce()).removerRestricaoMovimento();
    }

    @Test
    @DisplayName("Escudo bloqueia dano somente durante sua janela de três segundos")
    void escudoExpira() {
        boss.getStats().setVida(300);
        boss.update(0.01f);
        boss.update(0.8f);
        boss.update(4f);
        boss.update(0.45f);
        boss.update(0.01f);
        assertTrue(boss.isEscudoAtivo());
        int vidaAntes = boss.getVida();

        assertFalse(boss.receberDano(10));
        assertEquals(vidaAntes, boss.getVida());

        boss.update(3f);

        assertFalse(boss.isEscudoAtivo());
        assertTrue(boss.receberDano(10));
        assertEquals(vidaAntes - 10, boss.getVida());
    }

    @Test
    @DisplayName("Ataque final respeita aviso, zona evitável e dano único")
    void ataqueFinalEvitavelEDanoUnico() {
        boss.getStats().setVida(100);
        boss.update(0.01f);
        assertEquals(FaraoAreia.Estado.AVISO_ATAQUE_FINAL, boss.getEstado());
        verify(player, never()).dano(12);

        boss.update(1.2f);
        boss.update(1f);

        verify(player, times(1)).dano(12);

        FaraoTestavel evitado = new FaraoTestavel(player);
        evitado.setEnemiesList(new ArrayList<>());
        evitado.getStats().setVida(100);
        evitado.update(0.01f);
        hitboxPlayer.setPosition(700, 700);
        evitado.update(1.2f);

        verify(player, times(1)).dano(12);
    }

    @Test
    @DisplayName("Fase final mantém no máximo dois guardiões")
    void limitaGuardioes() {
        boss.getStats().setVida(100);
        boss.update(15.1f);

        List<EnemyTemplate> criados = new ArrayList<>();
        boss.drenarInvocacoes(criados);

        assertEquals(2, criados.size());
        assertEquals(2, boss.getQuantidadeInvocacoesAtivas());
        assertTrue(criados.stream().allMatch(inimigo -> inimigo.getInvocador() == boss));

        boss.update(30f);
        List<EnemyTemplate> extras = new ArrayList<>();
        boss.drenarInvocacoes(extras);
        assertEquals(0, extras.size());
    }

    @Test
    @DisplayName("Troca de fase cancela projéteis pendentes e dano tardio")
    void trocaDeFaseCancelaAtaque() {
        boss.update(0.01f);
        boss.update(0.01f);
        assertEquals(1, boss.getQuantidadeProjeteisAtivos());

        boss.getStats().setVida(300);
        boss.update(0.01f);

        assertEquals(1, boss.getFaseAtual());
        assertEquals(0, boss.getQuantidadeProjeteisAtivos());
        verify(player, never()).dano(5);
    }

    @Test
    @DisplayName("Morte encerra efeitos, projéteis e ataque anunciado")
    void morteLimpaCombate() {
        boss.getStats().setVida(300);
        boss.update(0.01f);
        assertEquals(FaraoAreia.Estado.AVISO_TEMPESTADE, boss.getEstado());

        boss.getStats().aplicarDanoDireto(9999);
        boss.update(1f);

        assertEquals(FaraoAreia.Estado.MORTE, boss.getEstado());
        assertEquals(0, boss.getQuantidadeProjeteisAtivos());
        verify(player, never()).aplicarRestricaoMovimento(
            org.mockito.ArgumentMatchers.anyFloat(), org.mockito.ArgumentMatchers.anyFloat());
        verify(player, atLeastOnce()).removerRestricaoMovimento();
    }

    @Test
    @DisplayName("Pause congela o boss")
    void pausaCongelaBoss() {
        when(player.isPause()).thenReturn(true);

        boss.update(20f);

        assertEquals(FaraoAreia.Estado.APROXIMACAO, boss.getEstado());
        assertEquals(0, boss.getQuantidadeProjeteisAtivos());
        verify(player, never()).dano(org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("Ausência de alvo não produz ataque nem exceção")
    void alvoAusente() {
        FaraoAreia semAlvo = new FaraoAreia(0, 0, false, null);

        assertDoesNotThrow(() -> semAlvo.update(30f));
        assertEquals(FaraoAreia.Estado.APROXIMACAO, semAlvo.getEstado());
    }
}
