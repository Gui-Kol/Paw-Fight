package com.pawfight.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;
import com.pawfight.game.engine.design.SpriteDefinition;
import com.pawfight.game.engine.phisics.TirosTamplate;
import com.pawfight.game.entity.tiro.dove.Coco;

public class Dove extends PlayerTemplate {

    public Dove(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        super(dx, dy, tileWidth, numTilesX, tileHeight, numTilesY, zoomCamera);
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
        return 15;
    }

    @Override
    protected int definirVelocidade() {
        return 600;
    }

    @Override
    protected int definirVidaBase() {
        return 6;
    }

    @Override
    protected float definirDuracaoTiro() {
        return 0;
    }

    @Override
    protected float definirCadenciaTiro() {
        return 0;
    }

    @Override
    protected int definirForca() {
        return 1;
    }

    @Override
    public void texture() {
        idleSheet = new Texture("entitys/player/dove/Idle.png");
        walkSheet = new Texture("entitys/player/dove/Walk.png");
        deadSheet = new Texture("entitys/player/dove/Death.png");
        hurtSheet = new Texture("entitys/player/dove/Hurt.png");
        idleDefinition = new SpriteDefinition(idleSheet, 4, 0.1f, false, olhandoEsquerda);
        walkDefinition = new SpriteDefinition(walkSheet, 6, 0.1f, false, olhandoEsquerda);
        deadDefinition = new SpriteDefinition(deadSheet, 4, 0.1f, false, olhandoEsquerda);
        hurtDefinition = new SpriteDefinition(hurtSheet, 2, 0.1f, false, olhandoEsquerda);
    }

    @Override
    protected int definirTamanho() {
        return 32;
    }

    @Override
    public String getName() {
        return "Dove";
    }

    @Override
    public void ataqueBasico() {
        if (podeAtacar) {
            cagando();
            Gdx.app.log("PlayerDove", "Atacando...");
            podeAtacar = false;
        }
    }

    private void cagando() {
        PlayerTemplate player = this;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Coco coco = new Coco(tamanhoTiro, player);
                tiros.add(coco);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        tiros.remove(coco); // Remove o coco
                    }
                }, coco.getDuracao() + duracaoTiro);
            }
        }, 1, 5 - cadenciaTiro);
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        batch.setProjectionMatrix(camera.combined);

        for (TirosTamplate coco : tiros) {
            coco.draw(batch,shapeRenderer);
        }
        super.draw(batch, shapeRenderer);
    }

    @Override
    public void ataqueEspecial() {
    }

    @Override
    public void usarHabilidadeEspecial() {
    }

}
