package com.pawfight.game.world.template;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pawfight.game.HeadlessGdx;
import com.pawfight.game.engine.ecs.GerenciadorEcs;
import com.pawfight.game.entity.enemy.DadosInimigo;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@DisplayName("EnemyManager — invocações")
class EnemyManagerInvocacoesTest {

    private static class InimigoFake extends EnemyTemplate {
        InimigoFake(PlayerTemplate player) {
            super(0, 0, false, player);
        }

        @Override protected DadosInimigo dadosInimigo() {
            return new DadosInimigo("Fake", 10, 1, 0, 16, 8, 0, 0,
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

    private static class InvocadorFake extends InimigoFake {
        private final List<EnemyTemplate> pendentes = new ArrayList<>();

        InvocadorFake(PlayerTemplate player) {
            super(player);
        }

        void preparar(EnemyTemplate invocacao) {
            invocacao.setInvocador(this);
            pendentes.add(invocacao);
        }

        @Override
        public void drenarInvocacoes(List<EnemyTemplate> destino) {
            destino.addAll(pendentes);
            pendentes.clear();
        }
    }

    @BeforeAll
    static void iniciarGdx() {
        HeadlessGdx.ensureGdx();
    }

    @Test
    @DisplayName("Invocação drenada entra na lista ativa e no Engine")
    void registraInvocacao() {
        GerenciadorEcs ecs = new GerenciadorEcs();
        EnemyManager manager = new EnemyManager(ecs);
        PlayerTemplate player = mock(PlayerTemplate.class);
        InvocadorFake invocador = new InvocadorFake(player);
        InimigoFake invocacao = new InimigoFake(player);
        invocador.preparar(invocacao);
        manager.adicionarInimigos(List.of(invocador));

        manager.atualizarInimigos(0.01f);

        assertEquals(2, manager.getListaInimigos().size());
        assertEquals(2, ecs.getEngine().getEntities().size());
    }

    @Test
    @DisplayName("Morte do invocador remove suas invocações do gameplay e do Engine")
    void removeInvocacoesComInvocador() {
        GerenciadorEcs ecs = new GerenciadorEcs();
        EnemyManager manager = new EnemyManager(ecs);
        PlayerTemplate player = mock(PlayerTemplate.class);
        InimigoFake invocador = new InimigoFake(player);
        InimigoFake invocacao = new InimigoFake(player);
        invocacao.setInvocador(invocador);
        manager.adicionarInimigos(List.of(invocador, invocacao));
        invocador.danoPorStatus(999);

        manager.atualizarInimigos(0.01f);

        assertEquals(0, manager.getListaInimigos().size());
        assertEquals(0, ecs.getEngine().getEntities().size());
    }
}
