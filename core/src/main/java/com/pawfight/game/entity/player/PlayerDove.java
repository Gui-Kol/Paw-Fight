package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.pawfight.game.commun.animation.SpriteDefinition;

public class PlayerDove extends PlayerTemplate {
    public PlayerDove(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        super(dx, dy, tileWidth, numTilesX, tileHeight, numTilesY, zoomCamera);

        vidaBase = 6;
        velocidade = 600;
        forca = 1;
        vida = vidaBase;

        TAMANHO_PX = 32;

        HITBOX_SIZE = 20;       // tamanho da hitbox (largura e altura)
        HITBOX_OFFSET_Y = 0;    // deslocamento vertical (abaixar ou subir)
        HITBOX_OFFSET_X = -5;
    }

    @Override
    public void texture() {
        // Spritesheets específicos
        idleSheet = new Texture("entitys/player/blue_bird/Idle.png");
        walkSheet = new Texture("entitys/player/blue_bird/Walk.png");
        deadSheet = new Texture("entitys/player/blue_bird/Death.png");
        hurtSheet = new Texture("entitys/player/blue_bird/Hurt.png");
        idleDefinition = new SpriteDefinition(idleSheet, 4, 0.1f, false, olhandoEsquerda);
        walkDefinition = new SpriteDefinition(walkSheet, 6, 0.1f, false, olhandoEsquerda);
        deadDefinition = new SpriteDefinition(deadSheet, 4, 0.1f, false, olhandoEsquerda);
        hurtDefinition = new SpriteDefinition(hurtSheet, 2, 0.1f, false, olhandoEsquerda);
    }

    @Override
    public void update(float delta) {
        TAMANHO_PX = 32;
        super.update(delta);
    }

    @Override
    public String getName() {
        return "Dove";
    }

    @Override
    public void ataqueBasico() {

    }

    @Override
    public void ataqueEspecial() {

    }

    @Override
    public void usarHabilidadeEspecial() {

    }

    @Override
    public int calcularDefesa() {
        return 0;
    }

    @Override
    public boolean podeEsquivar() {
        return false;
    }
}
