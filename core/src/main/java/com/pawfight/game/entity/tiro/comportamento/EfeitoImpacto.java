package com.pawfight.game.entity.tiro.comportamento;

import com.pawfight.game.entity.enemy.EnemyTemplate;

@FunctionalInterface
public interface EfeitoImpacto {
    EfeitoImpacto NENHUM = inimigo -> { };
    void aplicar(EnemyTemplate inimigo);
}
