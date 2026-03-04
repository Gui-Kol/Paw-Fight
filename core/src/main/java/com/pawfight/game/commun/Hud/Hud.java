package com.pawfight.game.commun.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.pawfight.game.commun.animation.SpriteDefinition;
import com.pawfight.game.commun.animation.AnimationEngine;
import com.pawfight.game.commun.font.FontFactory;
import com.pawfight.game.entity.player.Player;

import static com.pawfight.game.commun.CommunVariable.HITBOX_ISVISIBLE;

public class Hud {
    private BitmapFont font = FontFactory.createCustomFont("fonts/PixelOperator8-Bold.ttf", 20);
    private GlyphLayout layoutCoord = new GlyphLayout();
    private GlyphLayout layoutOlhando = new GlyphLayout();
    private AnimationEngine animationEngine;
    private Animation<TextureRegion> coracaoAnimation;
    private SpriteDefinition coracaoDefinition;

    Texture coracao;

    // câmera fixa para HUD
    private OrthographicCamera hudCamera;

    public Hud() {
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.update();
        coracao = new Texture("Hud/coracao.png");
        coracaoDefinition = new SpriteDefinition(coracao,5, 0.5f, false, false);
        animationEngine = new AnimationEngine();
    }

    public void draw(Batch batch, Player player) {
        if (!HITBOX_ISVISIBLE) {
            return;
        }
        int x = player.getDx();
        int y = player.getDy();

        // usa câmera de HUD (coordenadas de tela)
        batch.setProjectionMatrix(hudCamera.combined);
        font.setColor(Color.WHITE);

        desenharCoordenadas(batch, x, y);
        desenharDirecaoOlhar(batch, player.isOlhandoEsquerda());
        desenharCoracao(batch,player.getVida(),player.getVidaBase());
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
    private void desenharCoracao(Batch batch, int vidaAtual, int vidaMaxima) {
        coracaoAnimation = animationEngine.animar(coracaoDefinition);

        int tamanho = 128;
        float posX = 20;
        float posY = Gdx.graphics.getHeight() - tamanho;

        float porcentagemVida = (float) vidaAtual / vidaMaxima;

        int frameIndex = (int) ((1 - porcentagemVida) * (coracaoAnimation.getKeyFrames().length - 1));

        frameIndex = Math.max(0, Math.min(frameIndex, coracaoAnimation.getKeyFrames().length - 1));

        batch.draw(coracaoAnimation.getKeyFrames()[frameIndex], posX, posY, tamanho, tamanho);
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
