package com.pawfight.game.entity.player.dove;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.phisics.DrawHitBox;

public class Coco {
    private int x;
    private final int y;
    private final Rectangle hitBox;
    private final int dano;
    private final Texture texture;
    private final int tamanhoDraw;
    private final DrawHitBox drawHitBox;

    public Coco(int x, int y, int dano, int tamanho, boolean esquerda) {
        this.x = x;
        this.y = y - tamanho / 2;
        int xHitBox = x + tamanho / 4;
        int yHitBox = y - tamanho / 4;
        if (esquerda) {
            this.x += tamanho;
            xHitBox += tamanho;
        }
        this.dano = dano;
        this.texture = randomTex();
        tamanhoDraw = tamanho;
        hitBox = new Rectangle(xHitBox, yHitBox, (float) tamanho /2, (float) tamanho /2);
        drawHitBox = new DrawHitBox();
    }

    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        batch.begin();
        batch.draw(texture, x, y, tamanhoDraw, tamanhoDraw);
        batch.end();
        drawHitBox.draw(shapeRenderer,hitBox);
    }

    public int getDano() {
        return dano;
    }

    private Texture randomTex() {
        int random = (int) (Math.random() * 3);
        return switch (random) {
            case 1 -> new Texture("entitys/player/dove/coco/2.png");
            case 2 -> new Texture("entitys/player/dove/coco/3.png");
            default -> new Texture("entitys/player/dove/coco/1.png");
        };
    }
}
