package com.pawfight.game.engine.design.desenhar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.pawfight.game.engine.GameConfig;

public class DesenharTextura {

    public void desenhar(Batch batch, Texture textura, TextureRegion textureRegion,
                         float x, float y, float tamanhoPx) {
        if (textura == null && textureRegion == null) {
            Gdx.app.error("DesenharTextura", "Erro ao desenhar textura...");
            return;
        }
        float scale = GameConfig.getInstance().getScale();
        float tamanho  = tamanhoPx * scale;

        if (textureRegion != null) {
            batch.draw(textureRegion, x - tamanho, y, tamanho, tamanho);
        } else {
            batch.draw(textura, x, y, tamanho, tamanho);
        }
    }


}
