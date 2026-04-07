package com.pawfight.game.world.template;

import com.pawfight.game.engine.procedural.GerarInimigos;
import com.pawfight.game.entity.enemy.EnemyTemplate;

import java.util.ArrayList;
import java.util.List;

public class EnemyManager {

    private final GerarInimigos gerarInimigos;
    private final List<EnemyTemplate> listaInimigos;

    public EnemyManager() {
        this.gerarInimigos = new GerarInimigos();
        this.listaInimigos = new ArrayList<>();
    }

    public GerarInimigos getGerarInimigos() {
        return gerarInimigos;
    }

    public List<EnemyTemplate> getListaInimigos() {
        return listaInimigos;
    }

    public boolean hasInimigos() {
        return listaInimigos != null && !listaInimigos.isEmpty();
    }

    public void clearInimigos() {
        listaInimigos.clear();
    }
}

