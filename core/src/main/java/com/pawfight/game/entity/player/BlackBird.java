package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.pawfight.game.engine.design.SpriteDefinition;

public class BlackBird extends PlayerTemplate{
    public BlackBird(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        super(dx, dy, tileWidth, numTilesX, tileHeight, numTilesY, zoomCamera);
    }

    @Override
    protected int definirTamanhoTiro() {
        return 0;
    }

    @Override
    protected int definirHitBoxOffY() {
        return 0;
    }

    @Override
    protected int definirHitBoxOffX() {
        return -5;
    }

    @Override
    protected int definirHitBoxSize() {
        return 20;
    }

    @Override
    protected int definirVelocidade() {
        return 500;
    }

    @Override
    protected int definirVidaBase() {
        return 8;
    }

    @Override
    protected float definirDuracaoTiro() {
        return 0;
    }

    @Override
    protected float definirCadenciaTiro() {
        return 0;
    }

    @Override
    protected int definirForca() {
        return 2;
    }

    @Override
    public void texture() {
        // Spritesheets específicos
        idleSheet = new Texture("entitys/player/black_bird/Idle.png");
        walkSheet = new Texture("entitys/player/black_bird/Walk.png");
        deadSheet = new Texture("entitys/player/black_bird/Death.png");
        hurtSheet = new Texture("entitys/player/black_bird/Hurt.png");

        idleDefinition = new SpriteDefinition(idleSheet, 4, 0.1f, false, olhandoEsquerda);
        walkDefinition = new SpriteDefinition(walkSheet, 6, 0.1f, false, olhandoEsquerda);
        deadDefinition = new SpriteDefinition(deadSheet, 4, 0.1f, false, olhandoEsquerda);
        hurtDefinition = new SpriteDefinition(hurtSheet, 2, 0.1f, false, olhandoEsquerda);
    }
    @Override
    protected int definirTamanho() {
        return 32;
    }

    @Override
    public String getName() {
        return "Black Bird";
    }

    @Override
    public void ataqueBasico(float delta) {

    }

    @Override
    public void ataqueEspecial() {

    }

    @Override
    public void usarHabilidadeEspecial() {

    }

}
