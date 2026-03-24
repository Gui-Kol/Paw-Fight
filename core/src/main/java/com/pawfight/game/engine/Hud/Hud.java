package com.pawfight.game.engine.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.engine.design.SpriteDefinition;
import com.pawfight.game.engine.design.animation.AnimationEngine;
import com.pawfight.game.engine.design.desenhar.DesenharTexto;
import com.pawfight.game.engine.design.desenhar.DesenharTextura;
import com.pawfight.game.engine.font.FontFactory;
import com.pawfight.game.entity.player.PlayerTemplate;

import static com.pawfight.game.engine.CommunVariable.*;

public class Hud {
    private final BitmapFont font = FontFactory.createCustomFont("fonts/PixelOperator8-Bold.ttf", 20);
    private final AnimationEngine animationEngine;
    private final SpriteDefinition coracaoDefinition;
    private final Texture coin;
    private final DesenharTexto desenharTexto;
    private final DesenharTextura desenharTextura;
    private final float opacidadeHud;

    // câmera e viewport fixos para HUD
    private final OrthographicCamera hudCamera;
    private final Viewport hudViewport;

    public Hud() {
        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(GET_LARGURA_TELA_BASE(), GET_ALTURA_TELA_BASE(), hudCamera); // mantém proporção
        hudCamera.position.set(hudViewport.getWorldWidth() / 2f, hudViewport.getWorldHeight() / 2f, 0);
        hudCamera.update();
        desenharTexto = new DesenharTexto(hudViewport, hudCamera);
        desenharTextura = new DesenharTextura();
        opacidadeHud = 0.5f;

        coin = new Texture("Hud/coin.png");
        coracaoDefinition = new SpriteDefinition(new Texture("Hud/coracao.png"), 5, 1f, false, false);
        animationEngine = new AnimationEngine();
        Gdx.app.log("Hud", "Hud sendo carregado e desenhado...");
    }

    public void draw(Batch batch, PlayerTemplate playerTemplate, ShapeRenderer shapeRenderer) {
        hudCamera.update();

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.getData().setScale(GET_SCALE());

        int x = playerTemplate.getDx();
        int y = playerTemplate.getDy();

        font.setColor(Color.WHITE);
        desenharLevel(batch, playerTemplate.getLevel(), shapeRenderer);
        desenharMoeda(batch, playerTemplate.getMoedas(), shapeRenderer);
        desenharCoracao(batch, playerTemplate.getVida(), playerTemplate.getVidaBase());

        if (HITBOX_ISVISIBLE) {
            desenharCoordenadas(batch, shapeRenderer, x, y);
            desenharDirecaoOlhar(batch, shapeRenderer, playerTemplate.isOlhandoEsquerda());
            desenharScale(batch, shapeRenderer);
        }
        batch.end();
    }

    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
        hudCamera.position.set(hudViewport.getWorldWidth() / 2f, hudViewport.getWorldHeight() / 2f, 0);
    }

    private void desenharCoracao(Batch batch, int vidaAtual, int vidaMaxima) {
        Animation<TextureRegion> coracaoAnimation = animationEngine.animar(coracaoDefinition);
        float porcentagemVida = (float) vidaAtual / vidaMaxima;
        int frameIndex = (int) ((1 - porcentagemVida) * (coracaoAnimation.getKeyFrames().length - 1));
        frameIndex = Math.max(0, Math.min(frameIndex, coracaoAnimation.getKeyFrames().length - 1));
        var x = GET_LARGURA_TELA_BASE() - 180;
        var y = 40;

        desenharTextura.desenhar(batch, null, coracaoAnimation.getKeyFrames()[frameIndex], x, y, 128);
    }

    private void desenharMoeda(Batch batch, int moedas, ShapeRenderer shapeRenderer) {
        Color cor = new Color(0f / 255f, 100f / 255f, 0f / 255f, opacidadeHud);
        String moedaText = String.valueOf(moedas);
        GlyphLayout layout = new GlyphLayout(font, moedaText);
        var x = 20;
        var y = (GET_ALTURA_TELA_BASE() - layout.height) - 60;
        var yC = (GET_ALTURA_TELA_BASE() - layout.height) - 90;

        desenharTextura.desenhar(batch, coin, null, x, yC, 48);
        desenharTexto.desenhar(batch, shapeRenderer, cor, moedaText, font, x + 70, y, 10, true);
    }

    private void desenharLevel(Batch batch, int levelAtual, ShapeRenderer shapeRenderer) {
        Color cor = new Color(0f / 255f, 100f / 255f, 0f / 255f, opacidadeHud);
        String texto = "Level: " + levelAtual;
        GlyphLayout layout = new GlyphLayout(font, texto);
        var x = 20;
        var y = (GET_ALTURA_TELA_BASE() - layout.height) - 10;

        desenharTexto.desenhar(batch, shapeRenderer, cor, texto, font, x + 70, y, 10, true);
    }

    private void desenharCoordenadas(Batch batch, ShapeRenderer shapeRenderer, int x, int y) {
        String coords = "X: " + x + " Y: " + y;
        GlyphLayout layout = new GlyphLayout(font, coords);
        Color corFundo = new Color();
        var xW = (GET_LARGURA_TELA_BASE() - layout.width) - 10;
        var yH = (GET_ALTURA_TELA_BASE() - layout.height) - 10;

        desenharTexto.desenhar(batch, shapeRenderer, corFundo, coords, font, xW, yH, 0, false);
    }

    private void desenharDirecaoOlhar(Batch batch, ShapeRenderer shapeRenderer, boolean olhandoEsquerda) {
        String direcaoOlhar = olhandoEsquerda ? "esquerda" : "direita";
        String olhando = "Olhando para: " + direcaoOlhar;
        GlyphLayout layout = new GlyphLayout(font, olhando);
        Color corFundo = new Color();
        var x = (GET_LARGURA_TELA_BASE() - layout.width) - 10;
        var y = (GET_ALTURA_TELA_BASE() - layout.height) - 40;

        desenharTexto.desenhar(batch, shapeRenderer, corFundo, olhando, font, x, y, 0, false);
    }

    private void desenharScale(Batch batch, ShapeRenderer shapeRenderer) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float scale = Math.min(screenW / GET_LARGURA_TELA_BASE(), screenH / GET_ALTURA_TELA_BASE());
        String scaleText = "Scale: " + String.format("%.2f", scale);
        GlyphLayout layout = new GlyphLayout(font, scaleText);
        Color corFundo = new Color();
        var x = (GET_LARGURA_TELA_BASE() - layout.width) - 10;
        var y = (GET_ALTURA_TELA_BASE() - layout.height) - 70;

        desenharTexto.desenhar(batch, shapeRenderer, corFundo, scaleText, font, x, y, 0, false);
    }

    public void mostrarMensagemEmBaixo(Batch batch, ShapeRenderer shapeRenderer, String mensagem) {
        GlyphLayout layout = new GlyphLayout(font, mensagem);
        Color corFundo = new Color();
        var x = (GET_LARGURA_TELA_BASE() - layout.width) / 2f;

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        desenharTexto.desenhar(batch, shapeRenderer, corFundo, mensagem, font, x, 100, 10, true);
        batch.end();
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }
}
