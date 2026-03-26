package com.pawfight.game.entity.tiro.dove;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.tiro.TirosTamplate;
import com.pawfight.game.entity.player.PlayerTemplate;

public class Coco extends TirosTamplate {

    public Coco(int tamanho, PlayerTemplate player) {
        super(player.getDx() - tamanho / 2, player.getDy() - tamanho / 2, player.getForca(), tamanho, player);
        texture = randomTex();
    }

    @Override
    protected Rectangle gerarHitBox() {
        this.xHitBox += tamanho / 4; //Centraliza a hitbox no meio do tiro
        this.yHitBox += tamanho / 4;

        return new Rectangle(xHitBox, yHitBox, (float) tamanho / 2, (float) tamanho / 2);
    }

    @Override
    protected TirosTamplate clonar(PlayerTemplate player) {
        return new Coco(tamanho,player);
    }

    @Override
    protected int definirTamanhoPadrao() {
        return 16;
    }

    @Override
    protected float definirDuracao() {
        return 2;
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
        return texture;
    }

    @Override
    protected float definirIntervalo() {
        return 1;
    }
}
