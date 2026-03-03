package com.pawfight.game.commun;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.pawfight.game.commun.font.FontFactory;

import static com.pawfight.game.commun.CommunVariable.HITBOX_ISVISIBLE;

public class Hud {
    private BitmapFont font = FontFactory.createCustomFont("fonts/PixelOperator8-Bold.ttf", 20);
    private GlyphLayout layout = new GlyphLayout();

    // câmera fixa para HUD
    private OrthographicCamera hudCamera;

    public Hud() {
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.update();
    }

    public void draw(Batch batch, int x, int y) {
        coodenada(batch,x,y);
    }

    public void coodenada(Batch batch, int x, int y){
        if (HITBOX_ISVISIBLE) {
            // usa câmera de HUD (coordenadas de tela)
            batch.setProjectionMatrix(hudCamera.combined);

            batch.begin();
            font.setColor(Color.WHITE);

            String coords = "X: " + x + " Y: " + y;
            layout.setText(font, coords);

            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();

            float posX = layout.width; // margem direita
            float posY = screenHeight - 10;               // margem superior

            font.draw(batch, layout, posX, posY);
            batch.end();
        }
    }
}
