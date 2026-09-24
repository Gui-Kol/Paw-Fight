package com.pawfight.game.entity.tiro.comportamento;

import com.badlogic.gdx.graphics.g2d.Batch;

@FunctionalInterface
public interface ApresentacaoProjetil {
    void desenhar(Batch batch);
}
