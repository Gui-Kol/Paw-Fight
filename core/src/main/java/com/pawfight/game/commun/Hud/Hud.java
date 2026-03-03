package com.pawfight.game.commun.Hud;

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
    private GlyphLayout layoutCoord = new GlyphLayout();
    private GlyphLayout layoutOlhando = new GlyphLayout();

    // câmera fixa para HUD
    private OrthographicCamera hudCamera;

    public Hud() {
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.update();
    }

    public void draw(Batch batch, int x, int y, boolean olhandoEsquerda) {
        if (!HITBOX_ISVISIBLE) {
            return;
        }
        // usa câmera de HUD (coordenadas de tela)
        batch.setProjectionMatrix(hudCamera.combined);
        font.setColor(Color.WHITE);

        desenharCoordenadas(batch, x, y);
        desenharDirecaoOlhar(batch, olhandoEsquerda);
    }

    public void coodenada(Batch batch, int x, int y, boolean olhandoEsquerda) {

    }

    private void desenharCoordenadas(Batch batch, int x, int y) {
        String coords = "X: " + x + " Y: " + y;
        layoutCoord.setText(font, coords);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float posX = screenWidth - layoutCoord.width - 10;
        float posY = screenHeight - 10;

        font.draw(batch, layoutCoord, posX, posY);
    }

    private void desenharDirecaoOlhar(Batch batch, boolean olhandoEsquerda) {
        String direcaoOlhar = olhandoEsquerda ? "esquerda" : "direita";
        String olhando = "Olhando para: " + direcaoOlhar;
        layoutOlhando.setText(font, olhando);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float posX = screenWidth - layoutOlhando.width - 10;
        float posY = screenHeight - 40; // um pouco abaixo das coordenadas

        font.draw(batch, layoutOlhando, posX, posY);
    }

    public void mostrarMensagemEmBaixo(Batch batch, String mensagem) {
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, mensagem);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float posX = (screenWidth - layout.width) / 2f;
        float posY = (screenHeight + layout.height) / 8f;

        font.draw(batch, layout, posX, posY);
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }
}
