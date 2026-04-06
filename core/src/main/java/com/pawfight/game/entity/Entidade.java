package com.pawfight.game.entity;

import com.badlogic.gdx.math.Rectangle;

public interface Entidade {
    // ── Posição ──────────────────────────────────────────────
    int getDx();
    int getDy();

    // ── Hitbox ───────────────────────────────────────────────
    Rectangle getHitBox();

    // ── Stats ────────────────────────────────────────────────
    int getVida();
    int getVidaBase();
    int getForca();
    int getVelocidade();

    // ── Estado ───────────────────────────────────────────────
    boolean isMorto();
    boolean isOlhandoEsquerda();

    // ── Ações ────────────────────────────────────────────────
    void dano(int forca);
}

