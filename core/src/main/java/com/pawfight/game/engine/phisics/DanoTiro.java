package com.pawfight.game.engine.phisics;

import com.pawfight.game.entity.enemy.EnemyTemplate;

import java.util.List;

public class DanoTiro {

    public void darDanoListaInimigos(List<EnemyTemplate> hitBoxesInimigos, List<TirosTamplate> tiros) {
        for (EnemyTemplate inimigo : hitBoxesInimigos) {
            for (TirosTamplate tiro : tiros)
                if (inimigo.getHitBox().overlaps(tiro.hitBox)) {
                    inimigo.dano(tiro.dano);
                }
        }
    }
}
