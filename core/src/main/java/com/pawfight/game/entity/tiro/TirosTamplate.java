package com.pawfight.game.entity.tiro;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.entity.player.PlayerTemplate;

public abstract class TirosTamplate {
    protected int x, y;
    protected Rectangle hitBox;
    protected int dano;
    protected float duracao, cadencia, intervalo;
    protected Texture texture;
    protected int tamanhoDraw, tamanho, tamanhoPadrao;
    protected Renderizar renderizar;
    protected int xHitBox, yHitBox;

    public TirosTamplate(int x, int y, int dano, int tamanho, PlayerTemplate player) {
        renderizar = new Renderizar();
        duracao = definirDuracao();
        cadencia = definirIntervalo();
        tamanhoPadrao = definirTamanhoPadrao();
        intervalo = 0;

        this.tamanho = tamanho + tamanhoPadrao;
        this.tamanhoDraw = tamanho + tamanhoPadrao;
        this.x = x;
        this.y = y;
        this.dano = dano;

        this.xHitBox = x;
        this.yHitBox = y;

        if (player.isOlhandoEsquerda()) {
            this.x += player.getTamanho();
            xHitBox += player.getTamanho();
        }

        texture = singleTex();
        hitBox = gerarHitBox();
    }

    protected abstract int definirTamanhoPadrao();

    protected abstract float definirDuracao();

    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        batch.begin();
        batch.draw(texture, x, y, tamanhoDraw, tamanhoDraw);
        batch.end();
        renderizar.hitboxDraw(shapeRenderer, hitBox);
    }
    protected void update() {
    }

    protected abstract Texture randomTex();

    protected abstract Rectangle gerarHitBox();

    protected abstract TirosTamplate clonar(PlayerTemplate player);

    protected abstract Texture singleTex();

    protected abstract float definirIntervalo();

    public float getCadencia() {
        return cadencia;
    }

    public float getDuracao() {
        return duracao;
    }

    public int getDano() {
        return dano;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public float getIntervalo() {
        return intervalo;
    }

    public void setDuracao(float duracao) {
        this.duracao = duracao;
    }

    public void setIntervalo(float intervalo) {
        this.intervalo = intervalo;
    }
}


