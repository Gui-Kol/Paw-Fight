package com.pawfight.game.engine.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.engine.design.AnimationEngine;
import com.pawfight.game.engine.design.SpriteDefinition;
import com.pawfight.game.engine.font.FontFactory;
import com.pawfight.game.entity.player.PlayerTemplate;

import static com.pawfight.game.engine.CommunVariable.HITBOX_ISVISIBLE;

public class Hud {
    private final BitmapFont font = FontFactory.createCustomFont("fonts/PixelOperator8-Bold.ttf", 20);
    private final GlyphLayout layoutCoord = new GlyphLayout();
    private final GlyphLayout layoutOlhando = new GlyphLayout();
    private final AnimationEngine animationEngine;
    private final SpriteDefinition coracaoDefinition;

    // câmera e viewport fixos para HUD
    private final OrthographicCamera hudCamera;
    private final Viewport hudViewport;

    private float scale; // Adicionado para armazenar o fator de escala

    public Hud() {
        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(1920, 1080, hudCamera); // mantém proporção
        hudCamera.position.set(hudViewport.getWorldWidth() / 2f, hudViewport.getWorldHeight() / 2f, 0);
        hudCamera.update();

        Texture coracao = new Texture("Hud/coracao.png");
        coracaoDefinition = new SpriteDefinition(coracao, 5, 0.5f, false, false);
        animationEngine = new AnimationEngine();
    }

    public void draw(Batch batch, PlayerTemplate playerTemplate, ShapeRenderer shapeRenderer) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float baseW = 1920f;
        float baseH = 1080f;
        scale = Math.min(screenW / baseW, screenH / baseH);

        hudCamera.update();
        batch.setProjectionMatrix(hudCamera.combined);

        batch.begin();
        font.getData().setScale(scale);

        int x = playerTemplate.getDx();
        int y = playerTemplate.getDy();

        font.setColor(Color.WHITE);

        desenharLevel(batch, playerTemplate.getLevel(), shapeRenderer);
        desenharCoracao(batch, playerTemplate.getVida(), playerTemplate.getVidaBase());

        if (HITBOX_ISVISIBLE) {
            desenharCoordenadas(batch, x, y);
            desenharDirecaoOlhar(batch, playerTemplate.isOlhandoEsquerda());
        }
        batch.end();
    }

    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
        hudCamera.position.set(hudViewport.getWorldWidth() / 2f, hudViewport.getWorldHeight() / 2f, 0);
    }

    private void desenharCoordenadas(Batch batch, int x, int y) {
        String coords = "X: " + x + " Y: " + y;
        layoutCoord.setText(font, coords);

        float screenWidth = hudViewport.getWorldWidth();
        float screenHeight = hudViewport.getWorldHeight();

        float posX = screenWidth - layoutCoord.width - 10 * scale; // Aplicar escala à posição
        float posY = screenHeight - 10 * scale; // Aplicar escala à posição

        font.draw(batch, layoutCoord, posX, posY);
    }

    private void desenharCoracao(Batch batch, int vidaAtual, int vidaMaxima) {
        Animation<TextureRegion> coracaoAnimation = animationEngine.animar(coracaoDefinition);

        int tamanho = (int) (128 * scale); // Aplicar escala ao tamanho
        float posX = 20 * scale; // Aplicar escala à posição
        float posY = hudViewport.getWorldHeight() - tamanho;

        float porcentagemVida = (float) vidaAtual / vidaMaxima;
        int frameIndex = (int) ((1 - porcentagemVida) * (coracaoAnimation.getKeyFrames().length - 1));
        frameIndex = Math.max(0, Math.min(frameIndex, coracaoAnimation.getKeyFrames().length - 1));

        batch.draw(coracaoAnimation.getKeyFrames()[frameIndex], posX, posY, tamanho, tamanho);
    }

    private void desenharLevel(Batch batch, int levelAtual, ShapeRenderer shapeRenderer) {
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, "Level: " + levelAtual);

        float screenWidth = hudViewport.getWorldWidth();
        float screenHeight = hudViewport.getWorldHeight();
        float ajusteAltura = layout.height * 2 * scale;

        float posX = screenWidth / 8 * scale;
        float posY = screenHeight - ajusteAltura;

        batch.end(); // fecha o batch antes de usar ShapeRenderer
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f / 255f, 100f / 255f, 0f / 255f, 1f));

        float padding = 8 * scale; // Aplicar escala ao padding
        shapeRenderer.rect(
            posX - padding,
            posY - layout.height - padding,
            layout.width + padding * 2,
            layout.height + padding * 2
        );
        shapeRenderer.end();
        batch.begin(); // volta para desenhar com o batch

        font.draw(batch, layout, posX, posY);
    }

    private void desenharDirecaoOlhar(Batch batch, boolean olhandoEsquerda) {
        String direcaoOlhar = olhandoEsquerda ? "esquerda" : "direita";
        String olhando = "Olhando para: " + direcaoOlhar;
        layoutOlhando.setText(font, olhando);

        float screenWidth = hudViewport.getWorldWidth();
        float screenHeight = hudViewport.getWorldHeight();

        float posX = screenWidth - layoutOlhando.width - 10 * scale; // Aplicar escala à posição
        float posY = screenHeight - 40 * scale; // Aplicar escala à posição

        font.draw(batch, layoutOlhando, posX, posY);
    }

    public void mostrarMensagemEmBaixo(Batch batch, String mensagem) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float baseW = 1920f;
        float baseH = 1080f;
        scale = Math.min(screenW / baseW, screenH / baseH);

        batch.setProjectionMatrix(hudCamera.combined);

        batch.begin();
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, mensagem);

        float screenWidth = hudViewport.getWorldWidth();
        float screenHeight = hudViewport.getWorldHeight();

        float posX = (screenWidth - layout.width) / 2f;
        float posY = (screenHeight + layout.height) / 8f * scale; // Aplicar escala à posição

        font.draw(batch, layout, posX, posY);
        batch.end();
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }
}
