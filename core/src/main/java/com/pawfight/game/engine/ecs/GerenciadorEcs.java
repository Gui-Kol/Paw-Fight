package com.pawfight.game.engine.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectSet;
import com.pawfight.game.entity.system.SistemaCombate;
import com.pawfight.game.entity.system.SistemaStats;
import com.pawfight.game.entity.system.SistemaStatus;

public class GerenciadorEcs {
    private final Engine engine = new Engine();
    private final ObjectSet<Entity> entidadesRegistradas = new ObjectSet<>();

    public GerenciadorEcs() {
        engine.addSystem(new SistemaStats());
        engine.addSystem(new SistemaStatus());
        engine.addSystem(new SistemaCombate());
    }

    public void adicionarEntidade(Entity entidade) {
        if (entidade != null && entidadesRegistradas.add(entidade)) {
            engine.addEntity(entidade);
        }
    }

    public void removerEntidade(Entity entidade) {
        if (entidade != null && entidadesRegistradas.remove(entidade)) {
            engine.removeEntity(entidade);
        }
    }

    public void limpar() {
        engine.removeAllEntities();
        entidadesRegistradas.clear();
    }

    public void atualizar(float delta) {
        engine.update(delta);
    }

    public Engine getEngine() {
        return engine;
    }
}
