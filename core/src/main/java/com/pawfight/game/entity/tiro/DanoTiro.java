package com.pawfight.game.entity.tiro;

import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.world.WorldTemplate;

import java.util.List;

public class DanoTiro {

    public void darDanoListaInimigos(WorldTemplate world) {
        List<EnemyTemplate> inimigos = world.getListaInimigos();
        List<TirosTamplate> tiros = world.getPlayer().getTiros();

        if (inimigos == null || inimigos.isEmpty()) {
            return;
        }

        for (EnemyTemplate inimigo : inimigos) {
            for (TirosTamplate tiro : tiros)
                if (inimigo.getHitBox().overlaps(tiro.getHitBox())) {
                    inimigo.dano(tiro.getDano());
                }
        }
    }
}

