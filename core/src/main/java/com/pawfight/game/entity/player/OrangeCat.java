package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.entity.tiro.TirosTemplate;
import com.pawfight.game.entity.tiro.dove.Coco;

public class OrangeCat extends PlayerTemplate{
    public OrangeCat(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
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
        return 25;
    }

    @Override
    protected int definirVelocidade() {
        return 300;
    }

    @Override
    protected int definirVidaBase() {
        return 13;
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
        return 1;
    }

    @Override
    protected TirosTemplate modeloTiroExclusivo() {
        return new Coco(tamanhoTiro, this);
    }

    @Override
    public void loadTextures() {
        idleSheet = new Texture("entitys/player/orange_cat/Idle.png");
        walkSheet = new Texture("entitys/player/orange_cat/Walk.png");
        deadSheet = new Texture("entitys/player/orange_cat/Death.png");
        hurtSheet = new Texture("entitys/player/orange_cat/Hurt.png");
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
        return 56;
    }

    @Override
    public String getName() {
        return "Orange Cat";
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
