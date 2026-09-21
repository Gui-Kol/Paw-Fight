package com.pawfight.game.entity;

import com.badlogic.gdx.math.Rectangle;

public interface Entidade {
    int getDx();
    int getDy();

    Rectangle getHitBox();

    int getVida();
    int getVidaBase();
    int getForca();
    int getVelocidade();

    boolean isMorto();
    boolean isOlhandoEsquerda();

    void dano(int forca);
}

