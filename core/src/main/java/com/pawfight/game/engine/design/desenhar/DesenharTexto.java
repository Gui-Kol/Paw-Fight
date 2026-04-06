package com.pawfight.game.engine.design.desenhar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.pawfight.game.engine.GameConfig;

public class DesenharTexto {
    private final Viewport viewport;
    private final OrthographicCamera camera;
    private final GlyphLayout layout = new GlyphLayout();

    public DesenharTexto(Viewport viewport, OrthographicCamera camera) {
        this.viewport = viewport;
        this.camera = camera;
    }

    public void desenhar(Batch batch, ShapeRenderer shapeRenderer, Color corFundo, String texto, BitmapFont font,
                         float x, float y, float fundo, boolean renderizarFundo) {
        // Só encerra o batch se ele estiver ativo
        if (batch.isDrawing()) {
            batch.end();
        }
        font.getData().setScale(GameConfig.getInstance().getScale());

        layout.setText(font, texto);

        float posX = x;
        float posY = y;
        float largura = layout.width;
        float altura = layout.height;

        if (renderizarFundo) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(corFundo);

            shapeRenderer.rect(
                posX - fundo,
                posY - altura - fundo,
                largura + fundo * 2,
                altura + fundo * 2
            );
            shapeRenderer.end();
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, texto, posX, posY);
    }
}
