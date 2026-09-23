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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Escorpião da Areia")
class EscorpiaoAreiaTest {

    private static class EscorpiaoTestavel extends EscorpiaoAreia {
        EscorpiaoTestavel(PlayerTemplate player) {
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
            return new DadosInimigo("Minion", 5, 1, 0, 16, 8, 0, 0,
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
    private EscorpiaoTestavel boss;

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
        when(player.isMorto()).thenReturn(false);
        when(player.isPause()).thenReturn(false);
        boss = new EscorpiaoTestavel(player);
        boss.setEnemiesList(new ArrayList<>());
    }

    @Test
    @DisplayName("Transiciona exatamente nos limiares de 65% e 30%")
    void transicoesNosLimiares() {
        boss.getStats().setVida(274);
        boss.update(0.01f);
        assertEquals(0, boss.getFaseAtual());

        boss.getStats().setVida(273);
        boss.update(0.01f);
        assertEquals(1, boss.getFaseAtual());

        boss.getStats().setVida(126);
        boss.update(0.01f);
        assertEquals(2, boss.getFaseAtual());
    }

    @Test
    @DisplayName("Dano que cruza dois limiares entra diretamente na terceira fase")
    void danoCruzaDoisLimiares() {
        boss.getStats().setVida(100);

        boss.update(0.01f);

        assertEquals(2, boss.getFaseAtual());
    }

    @Test
    @DisplayName("Ferrão respeita antecipação e aplica um único contato")
    void ferraoAplicaUmaVez() {
        boss.update(0.01f);
        assertEquals(EscorpiaoAreia.Estado.AVISO, boss.getEstado());
        verify(player, never()).dano(org.mockito.ArgumentMatchers.anyInt());

        boss.update(0.5f);
        boss.update(0.5f);

        verify(player).dano(7);
    }

    @Test
    @DisplayName("Golpe duplo abre duas janelas de contato separadas")
    void golpeDuploPossuiDoisContatos() {
        boss.getStats().setVida(200);
        boss.update(0.01f);
        boss.update(0.45f);
        boss.update(0.35f);

        verify(player, org.mockito.Mockito.times(2)).dano(6);
    }

    @Test
    @DisplayName("Salto marca destino e causa dano de aterrissagem uma vez")
    void saltoCausaDanoUmaVez() {
        boss.getStats().setVida(200);
        when(player.getDx()).thenReturn(300);
        when(player.getDy()).thenReturn(200);
        boss.update(6.1f);
        assertEquals(EscorpiaoAreia.Estado.SALTO_AVISO, boss.getEstado());
        assertEquals(300f, boss.getDestinoSaltoX());
        assertEquals(200f, boss.getDestinoSaltoY());

        boss.update(0.65f);
        boss.update(0.01f);
        boss.update(1f);

        verify(player).dano(8);
    }

    @Test
    @DisplayName("Fase final nunca mantém mais de três invocações")
    void limitaInvocacoes() {
        boss.getStats().setVida(100);
        boss.update(12.1f);
        assertEquals(EscorpiaoAreia.Estado.ENTERRADO, boss.getEstado());
        boss.update(1f);

        List<EnemyTemplate> criadas = new ArrayList<>();
        boss.drenarInvocacoes(criadas);

        assertEquals(3, criadas.size());
        assertEquals(3, boss.getQuantidadeInvocacoesAtivas());
        assertTrue(criadas.stream().allMatch(inimigo -> inimigo.getInvocador() == boss));
    }

    @Test
    @DisplayName("Pause congela o estado e os cooldowns")
    void pausaCongelaBoss() {
        when(player.isPause()).thenReturn(true);

        boss.update(20f);

        assertEquals(EscorpiaoAreia.Estado.APROXIMACAO, boss.getEstado());
        verify(player, never()).dano(org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("Ausência de alvo não produz ataque nem exceção")
    void alvoAusente() {
        EscorpiaoAreia semAlvo = new EscorpiaoAreia(0, 0, false, null);

        assertDoesNotThrow(() -> semAlvo.update(30f));
        assertEquals(EscorpiaoAreia.Estado.APROXIMACAO, semAlvo.getEstado());
    }

    @Test
    @DisplayName("Morte durante aviso do salto cancela dano tardio")
    void morteDuranteSalto() {
        boss.getStats().setVida(200);
        when(player.getDx()).thenReturn(300);
        boss.update(6.1f);
        assertEquals(EscorpiaoAreia.Estado.SALTO_AVISO, boss.getEstado());

        boss.getStats().aplicarDanoDireto(9999);
        boss.update(1f);

        assertEquals(EscorpiaoAreia.Estado.MORTE, boss.getEstado());
        verify(player, never()).dano(org.mockito.ArgumentMatchers.anyInt());
    }
}
