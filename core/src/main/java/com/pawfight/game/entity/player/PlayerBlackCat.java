package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.pawfight.game.commun.animation.SpriteDefinition;

public class PlayerBlackCat extends PlayerTemplate {
    // Construtor
    public PlayerBlackCat(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        super(dx, dy, tileWidth, numTilesX, tileHeight, numTilesY, zoomCamera);

        vidaBase = 10;
        velocidade = 350;
        forca = 2;
        vida = vidaBase;

        TAMANHO_PX = 64;

        HITBOX_SIZE = 25;       // tamanho da hitbox (largura e altura)
        HITBOX_OFFSET_Y = 0;    // deslocamento vertical (abaixar ou subir)
        HITBOX_OFFSET_X = -5;
    }

    @Override
    public void texture() {
        // Spritesheets específicos
        idleSheet = new Texture("entitys/player/black_cat/Idle.png");
        walkSheet = new Texture("entitys/player/black_cat/Walk.png");
        deadSheet = new Texture("entitys/player/black_cat/Death.png");
        hurtSheet = new Texture("entitys/player/black_cat/Hurt.png");
        idleDefinition = new SpriteDefinition(idleSheet, 4, 0.1f, false, olhandoEsquerda);
        walkDefinition = new SpriteDefinition(walkSheet, 6, 0.1f, false, olhandoEsquerda);
        deadDefinition = new SpriteDefinition(deadSheet, 4, 0.1f, false, olhandoEsquerda);
        hurtDefinition = new SpriteDefinition(hurtSheet, 2, 0.1f, false, olhandoEsquerda);
    }

    @Override
    public void update(float delta) {
        TAMANHO_PX = 64;
        super.update(delta);
    }

    @Override
    public String getName() {
        return "Black Cat";
    }

}
