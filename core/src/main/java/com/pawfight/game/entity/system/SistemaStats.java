package com.pawfight.game.entity.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.pawfight.game.entity.component.StatsComponent;

public class SistemaStats extends IteratingSystem {
    private static final ComponentMapper<StatsComponent> STATS = ComponentMapper.getFor(StatsComponent.class);

    public SistemaStats() {
        super(Family.all(StatsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entidade, float deltaTime) {
        STATS.get(entidade).updateTimers(deltaTime);
    }
}
