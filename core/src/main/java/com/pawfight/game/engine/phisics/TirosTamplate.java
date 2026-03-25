package com.pawfight.game.engine.phisics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.player.PlayerTemplate;

public abstract class TirosTamplate {
    protected int x, y;
    protected Rectangle hitBox;
    protected int dano;
    protected float duracao;
    protected Texture texture;
    protected int tamanhoDraw, tamanho;
    protected DrawHitBox drawHitBox;
    protected int xHitBox, yHitBox;

    public TirosTamplate(int x, int y, int dano, int tamanho, PlayerTemplate player) {
        setTexture();
        drawHitBox = new DrawHitBox();
        this.tamanho = tamanho;
        this.tamanhoDraw = tamanho;
        this.x = x;
        this.y = y;
        this.dano = dano;

        this.xHitBox = x;
        this.yHitBox = y;

        if (player.isOlhandoEsquerda()) {
            this.x += player.getTamanho();
            xHitBox += player.getTamanho();
        }

        hitBox = gerarHitBox();
    }

    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        batch.begin();
        batch.draw(texture, x, y, tamanhoDraw, tamanhoDraw);
        batch.end();
        drawHitBox.draw(shapeRenderer, hitBox);
    }

    protected void setTexture() {
        texture = singleTex();
    }

    protected void update() {
    }

    protected abstract Texture randomTex();

    protected abstract Rectangle gerarHitBox();

    protected abstract Texture singleTex();

    public float getDuracao() {
        return duracao;
    }
}


