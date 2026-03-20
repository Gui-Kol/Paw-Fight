package com.pawfight.game.entity.player.dove;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.phisics.TirosTamplate;

public class Coco extends TirosTamplate {

    public Coco(int x, int y, int dano, int tamanho, boolean esquerda) {
        super(x, y, dano, tamanho, esquerda);
        this.y -= tamanho / 2;
    }

    @Override
    protected Rectangle gerarHitBox() {
        this.xHitBox += tamanho / 4; //Centraliza a hitbox no meio do tiro
        this.yHitBox += tamanho / 4;

        return new Rectangle(xHitBox, yHitBox, (float) tamanho / 2, (float) tamanho / 2);
    }

    @Override
    protected Texture randomTex() {
        int random = (int) (Math.random() * 3);
        return switch (random) {
            case 1 -> new Texture("entitys/player/dove/coco/2.png");
            case 2 -> new Texture("entitys/player/dove/coco/3.png");
            default -> new Texture("entitys/player/dove/coco/1.png");
        };
    }

    @Override
    protected Texture singleTex() {
        return randomTex();
    }
}
