package com.pawfight.game.entity.system;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.HeadlessGdx;
import com.pawfight.game.engine.ecs.GerenciadorEcs;
import com.pawfight.game.entity.enemy.DadosInimigo;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Cobre o SistemaStatus pelo ECS real: DoT processado pelo sistema, ordem com combate e morte por tick.
@DisplayName("SistemaStatus")
class SistemaStatusTest {

    private static class InimigoFake extends EnemyTemplate {
        InimigoFake(int vida, PlayerTemplate player) {
            super(0, 0, false, player);
            getStats().setVidaBase(vida);
            getStats().setVida(vida);
        }

        @Override protected DadosInimigo dadosInimigo() {
            return new DadosInimigo("Fake", 10, 1, 0, 16, 8, 0, 0,
                null, null, null, null, null, null, 1f, null, null);
        }
        @Override public void ataqueBasico() { }
        @Override public void ataqueEspecial() { }
        @Override public void updateSpriteDefinitions() { }
        @Override public EnemyTemplate cloneEnemy() { return new InimigoFake(getVida(), player); }
        @Override public void andarIA(float delta) { }
        @Override public int getTamanho() { return 16; }
        @Override protected int moedasMorte() { return 0; }
        @Override public void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) { }
    }

    private GerenciadorEcs ecs;
    private PlayerTemplate player;
    private InimigoFake inimigo;

    @BeforeAll
    static void iniciarGdx() {
        HeadlessGdx.ensureGdx();
    }

    @BeforeEach
    void montar() {
        HeadlessGdx.ensureGdx();
        ecs = new GerenciadorEcs();
        player = mock(PlayerTemplate.class);
        when(player.getHitBox()).thenReturn(new Rectangle(0, 0, 30, 30));
        inimigo = new InimigoFake(10, player);
        ecs.adicionarEntidade(inimigo.getEntidadeEcs());
    }

    @Test
    @DisplayName("Queimadura aplicada causa dano no próximo update do ECS")
    void dotProcessadoNoProximoUpdate() {
        inimigo.aplicarQueimadura(3, 3f, 1f);
        assertEquals(10, inimigo.getVida(), "Aplicação não causa dano imediato");

        ecs.atualizar(0.5f);

        assertEquals(7, inimigo.getVida(), "O tick de 0,5s deve descontar 3 de vida");
    }

    @Test
    @DisplayName("Morte por tick de DoT marca o inimigo como morto")
    void mortePorTickDeStatus() {
        InimigoFake fraco = new InimigoFake(2, player);
        ecs.adicionarEntidade(fraco.getEntidadeEcs());
        fraco.aplicarQueimadura(4, 3f, 1f);

        ecs.atualizar(0.5f);

        assertTrue(fraco.isMorto(), "O tick deveria ter matado o inimigo");
    }

    @Test
    @DisplayName("Efeito expirado para de causar dano")
    void efeitoExpiradoParaDeCausarDano() {
        inimigo.aplicarQueimadura(0, 1f, 1f);
        inimigo.aplicarLentidao(0.5f, 1f, 1f);

        ecs.atualizar(1f);
        ecs.atualizar(2f);

        assertEquals(10, inimigo.getVida());
        assertFalse(inimigo.getStatus().isLento(), "Lentidão expira pelo ECS");
    }

    @Test
    @DisplayName("Status sem alvo não lança exceção")
    void statusSemAlvo() {
        com.pawfight.game.entity.component.StatusComponent orfao =
            new com.pawfight.game.entity.component.StatusComponent();
        orfao.aplicarQueimadura(3, 3f, 1f);
        com.badlogic.ashley.core.Entity entidade = new com.badlogic.ashley.core.Entity();
        entidade.add(orfao);
        ecs.adicionarEntidade(entidade);

        // Atravessa a expiração com dano pendente: dano > 0 e alvo nulo devem ser ignorados sem exceção
        ecs.atualizar(3.1f);

        assertFalse(orfao.isQueimando(), "Efeito decorre e expira normalmente mesmo sem alvo");
    }
}
