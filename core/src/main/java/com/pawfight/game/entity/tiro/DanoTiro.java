package com.pawfight.game.entity.tiro;

import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.world.WorldTemplate;

import java.util.ArrayList;
import java.util.List;

public class DanoTiro {

    public void darDanoListaInimigos(WorldTemplate world) {
        List<EnemyTemplate> inimigos = world.getListaInimigos();
        List<TirosTemplate> tiros = world.getPlayer().getTiros();

        if (inimigos == null || inimigos.isEmpty() || tiros == null || tiros.isEmpty()) {
            return;
        }

        // Cópia defensiva — evita ConcurrentModificationException caso
        // a lista original seja modificada durante a iteração
        List<TirosTemplate> tirosSnapshot = new ArrayList<>(tiros);

        for (EnemyTemplate inimigo : inimigos) {
            for (TirosTemplate tiro : tirosSnapshot) {
                if (inimigo.getHitBox().overlaps(tiro.getHitBox())) {
                    inimigo.dano(tiro.getDano());
                }
            }
        }
    }
}

