package com.pawfight.game.entity.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.pawfight.game.entity.component.StatsComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SistemaStats")
class SistemaStatsTest {

    @Test
    @DisplayName("Processa qualquer entidade com StatsComponent sem depender do tipo de template")
    void processaPlayerInimigoEBossPorComposicao() {
        Engine engine = new Engine();
        engine.addSystem(new SistemaStats());
        StatsComponent player = statsFerido();
        StatsComponent inimigo = statsFerido();
        StatsComponent boss = statsFerido();
        engine.addEntity(new Entity().add(player));
        engine.addEntity(new Entity().add(inimigo));
        engine.addEntity(new Entity().add(boss));

        engine.update(0.5f);

        assertFalse(player.isHurt());
        assertFalse(inimigo.isHurt());
        assertFalse(boss.isHurt());
    }

    @Test
    @DisplayName("Entidade removida do Engine deixa de ter timers processados")
    void entidadeRemovidaNaoContinuaProcessando() {
        Engine engine = new Engine();
        engine.addSystem(new SistemaStats());
        StatsComponent stats = statsFerido();
        Entity entidade = new Entity().add(stats);
        engine.addEntity(entidade);
        engine.removeEntity(entidade);

        engine.update(1f);

        assertTrue(stats.isHurt());
    }

    private StatsComponent statsFerido() {
        StatsComponent stats = new StatsComponent(10, 1, 1);
        stats.aplicarDano(1);
        return stats;
    }
}
