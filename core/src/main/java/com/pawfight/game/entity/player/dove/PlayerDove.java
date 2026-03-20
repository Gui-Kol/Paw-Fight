package com.pawfight.game.entity.player.dove;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;
import com.pawfight.game.engine.design.SpriteDefinition;
import com.pawfight.game.engine.phisics.TirosTamplate;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.ArrayList;
import java.util.List;

public class PlayerDove extends PlayerTemplate {
    private final float tempoCoco;
    private final float duracaoCoco;

    public PlayerDove(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        super(dx, dy, tileWidth, numTilesX, tileHeight, numTilesY, zoomCamera);

        vidaBase = 6;
        velocidade = 600;
        forca = 1;
        vida = vidaBase;
        tempoCoco = 2;
        duracaoCoco = 3;

        TAMANHO_PX = 32;

        HITBOX_SIZE = 20;
        HITBOX_OFFSET_Y = 0;
        HITBOX_OFFSET_X = -5;
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
    protected int getTamanho() {
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

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Coco coco = new Coco(dx, dy, forca, 16, isOlhandoEsquerda());
                tiros.add(coco);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        tiros.remove(coco); // Remove o coco
                    }
                }, duracaoCoco);
            }
        }, 1, tempoCoco);
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

    @Override
    public int calcularDefesa() {
        return 0;
    }

}
