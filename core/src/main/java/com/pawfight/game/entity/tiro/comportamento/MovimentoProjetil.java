package com.pawfight.game.entity.tiro.comportamento;

@FunctionalInterface
public interface MovimentoProjetil {
    MovimentoProjetil PARADO = delta -> { };
    void atualizar(float delta);
}
