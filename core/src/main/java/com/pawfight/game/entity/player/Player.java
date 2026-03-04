package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.commun.animation.SpriteDefinition;

import java.util.List;

public class Player extends PlayerTemplate {
    // Construtor
    public Player(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        super(dx, dy, tileWidth, numTilesX, tileHeight, numTilesY, zoomCamera);

        // Spritesheets específicos
        idleSheet = new Texture("entitys/player/black_cat/Idle.png");
        walkSheet = new Texture("entitys/player/black_cat/Walk.png");
        deadSheet = new Texture("entitys/player/black_cat/Death.png");
        hurtSheet = new Texture("entitys/player/black_cat/Hurt.png");
    }

    @Override
    public void update(float delta, List<Rectangle> paredes) {
        idleDefinition = new SpriteDefinition(idleSheet, 4, 0.1f, false, olhandoEsquerda);
        walkDefinition = new SpriteDefinition(walkSheet, 6, 0.1f, false, olhandoEsquerda);
        deadDefinition = new SpriteDefinition(deadSheet, 4, 0.1f, false, olhandoEsquerda);
        hurtDefinition = new SpriteDefinition(hurtSheet, 2, 0.1f, false, olhandoEsquerda);
        super.update(delta, paredes);
    }

    @Override
    public void dispose() {
        idleSheet.dispose();
        walkSheet.dispose();
        deadSheet.dispose();
        hurtSheet.dispose();
    }
}
