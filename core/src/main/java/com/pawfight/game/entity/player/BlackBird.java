package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.entity.tiro.TirosTemplate;
import com.pawfight.game.entity.tiro.dove.Coco;

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
        return 1;
    }

    @Override
    protected float definirCadenciaTiro() {
        return 1;
    }

    @Override
    protected int definirForca() {
        return 2;
    }

    @Override
    protected TirosTemplate modeloTiroExclusivo() {
        return new Coco(tamanhoTiro, this);
    }

    @Override
    public void loadTextures() {
        idleSheet = new Texture("entitys/player/black_bird/Idle.png");
        walkSheet = new Texture("entitys/player/black_bird/Walk.png");
        deadSheet = new Texture("entitys/player/black_bird/Death.png");
        hurtSheet = new Texture("entitys/player/black_bird/Hurt.png");
    }

    @Override
    public void updateSpriteDefinitions() {
        idleDefinition = new DefinirSprite(idleSheet, 4, 0.1f, false, olhandoEsquerda);
        walkDefinition = new DefinirSprite(walkSheet, 6, 0.1f, false, olhandoEsquerda);
        deadDefinition = new DefinirSprite(deadSheet, 4, 0.1f, false, olhandoEsquerda);
        hurtDefinition = new DefinirSprite(hurtSheet, 2, 0.1f, false, olhandoEsquerda);
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
