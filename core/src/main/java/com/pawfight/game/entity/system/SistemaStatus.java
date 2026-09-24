package com.pawfight.game.entity.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.pawfight.game.entity.component.StatusComponent;
import com.pawfight.game.entity.enemy.EnemyTemplate;

public class SistemaStatus extends IteratingSystem {
    private static final ComponentMapper<StatusComponent> STATUS = ComponentMapper.getFor(StatusComponent.class);
    public SistemaStatus() { super(Family.all(StatusComponent.class).get()); }
    @Override protected void processEntity(Entity entity, float deltaTime) {
        StatusComponent status = STATUS.get(entity);
        int dano = status.update(deltaTime);
        if (dano <= 0 || status.getAlvo() == null) return;
        if (status.getAlvo() instanceof EnemyTemplate inimigo) inimigo.danoPorStatus(dano);
        else status.getAlvo().dano(dano);
    }
}
