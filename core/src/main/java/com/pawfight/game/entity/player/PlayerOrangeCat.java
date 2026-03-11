package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.pawfight.game.engine.animation.SpriteDefinition;

public class PlayerOrangeCat extends PlayerTemplate{
    public PlayerOrangeCat(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        super(dx, dy, tileWidth, numTilesX, tileHeight, numTilesY, zoomCamera);

        vidaBase = 13;
        velocidade = 300;
        forca = 1;
        vida = vidaBase;

        TAMANHO_PX = 56;

        HITBOX_SIZE = 25;       // tamanho da hitbox (largura e altura)
        HITBOX_OFFSET_Y = 0;    // deslocamento vertical (abaixar ou subir)
        HITBOX_OFFSET_X = -5;
    }

    @Override
    public void texture() {
        idleSheet = new Texture("entitys/player/orange_cat/Idle.png");
        walkSheet = new Texture("entitys/player/orange_cat/Walk.png");
        deadSheet = new Texture("entitys/player/orange_cat/Death.png");
        hurtSheet = new Texture("entitys/player/orange_cat/Hurt.png");
        idleDefinition = new SpriteDefinition(idleSheet, 4, 0.1f, false, olhandoEsquerda);
        walkDefinition = new SpriteDefinition(walkSheet, 6, 0.1f, false, olhandoEsquerda);
        deadDefinition = new SpriteDefinition(deadSheet, 4, 0.1f, false, olhandoEsquerda);
        hurtDefinition = new SpriteDefinition(hurtSheet, 2, 0.1f, false, olhandoEsquerda);
    }

    @Override
    protected int getTamanho() {
        return 56;
    }

    @Override
    public String getName() {
        return "Orange Cat";
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
