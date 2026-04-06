package com.pawfight.game.entity.tiro.dove;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.Assets;
import com.pawfight.game.entity.tiro.TirosTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;

public class Coco extends TirosTemplate {
    private final Texture coco1, coco2, coco3;

    public Coco(int tamanho, PlayerTemplate player) {
        super(player.getDx() - tamanho / 2, player.getDy() - tamanho / 2, player.getForca(), tamanho, player);
        // Texturas devem ser carregadas ANTES de randomTex()
        coco1 = Assets.get("entitys/player/dove/coco/2.png", Texture.class);
        coco2 = Assets.get("entitys/player/dove/coco/3.png", Texture.class);
        coco3 = Assets.get("entitys/player/dove/coco/1.png", Texture.class);
        texture = randomTex();
    }

    @Override
    protected Rectangle gerarHitBox() {
        this.xHitBox += tamanho / 4; //Centraliza a hitbox no meio do tiro
        this.yHitBox += tamanho / 4;

        return new Rectangle(xHitBox, yHitBox, (float) tamanho / 2, (float) tamanho / 2);
    }

    @Override
    protected TirosTemplate clonar(PlayerTemplate player) {
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
            case 1 -> coco1;
            case 2 -> coco2;
            default -> coco3;
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
