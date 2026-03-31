package com.pawfight.game.engine.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.engine.design.animation.MotorAnimacao;
import com.pawfight.game.engine.design.desenhar.DesenharTexto;
import com.pawfight.game.engine.design.desenhar.DesenharTextura;
import com.pawfight.game.engine.font.FontFactory;
import com.pawfight.game.entity.player.PlayerTemplate;

import static com.pawfight.game.engine.VariavelComum.*;

public class Hud {
    private float atualizarDesenho;
    private float deltaTexto;
    private final float tempoParaDesenhar;
    private final BitmapFont font = FontFactory.createCustomFont("fonts/PixelOperator8-Bold.ttf", 20);
    private final MotorAnimacao MotorAnimacao;
    private final DefinirSprite coracaoDefinition;
    private final Texture coin;
    private final DesenharTexto desenharTexto;
    private final DesenharTextura desenharTextura;
    private final float opacidadeHud;

    // câmera e viewport fixos para HUD
    private final OrthographicCamera hudCamera;
    private final Viewport hudViewport;

    // layouts reutilizáveis (evita alocação por frame)
    private final GlyphLayout layoutMoeda = new GlyphLayout();
    private final GlyphLayout layoutLevel = new GlyphLayout();
    private final GlyphLayout layoutCoords = new GlyphLayout();
    private final GlyphLayout layoutDirecao = new GlyphLayout();
    private final GlyphLayout layoutDelta = new GlyphLayout();
    private final GlyphLayout layoutScale = new GlyphLayout();
    private final GlyphLayout layoutMensagem = new GlyphLayout();

    // cores reutilizáveis (evita alocação por frame)
    private final Color corMoeda;
    private final Color corLevel;
    private final Color corDebug = new Color(0, 0, 0, 0);


    public Hud() {
        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(GET_LARGURA_TELA_BASE(), GET_ALTURA_TELA_BASE(), hudCamera); // mantém proporção
        hudCamera.position.set(hudViewport.getWorldWidth() / 2f, hudViewport.getWorldHeight() / 2f, 0);
        hudCamera.update();
        desenharTexto = new DesenharTexto(hudViewport, hudCamera);
        desenharTextura = new DesenharTextura();
        opacidadeHud = 0.5f;
        corMoeda = new Color(0f / 255f, 100f / 255f, 0f / 255f, opacidadeHud);
        corLevel = new Color(0f / 255f, 100f / 255f, 0f / 255f, opacidadeHud);
        atualizarDesenho = 0;
        tempoParaDesenhar = 2;
        deltaTexto = 0;

        coin = new Texture("Hud/coin.png");
        coracaoDefinition = new DefinirSprite(new Texture("Hud/coracao.png"), 5, 1f, false, false);
        MotorAnimacao = new MotorAnimacao();
        Gdx.app.log("Hud", "Sendo carregado e desenhado...");
    }

    public void draw(Batch batch, PlayerTemplate playerTemplate, ShapeRenderer shapeRenderer) {
        if (playerTemplate.isPause()) {
            return;
        }
        float delta = Gdx.graphics.getDeltaTime();
        atualizarDesenho += delta;

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
            desenharDeltaTime(batch, shapeRenderer, delta);
        }
        batch.end();
    }

    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
        hudCamera.position.set(hudViewport.getWorldWidth() / 2f, hudViewport.getWorldHeight() / 2f, 0);
    }

    private void desenharCoracao(Batch batch, int vidaAtual, int vidaMaxima) {
        Animation<TextureRegion> coracaoAnimation = MotorAnimacao.animar(coracaoDefinition);
        float porcentagemVida = (float) vidaAtual / vidaMaxima;
        int frameIndex = (int) ((1 - porcentagemVida) * (coracaoAnimation.getKeyFrames().length - 1));
        frameIndex = Math.max(0, Math.min(frameIndex, coracaoAnimation.getKeyFrames().length - 1));
        var x = GET_LARGURA_TELA_BASE() - 15;
        var y = 40;

        desenharTextura.desenhar(batch, null, coracaoAnimation.getKeyFrames()[frameIndex], x, y, 256);
    }

    private void desenharMoeda(Batch batch, int moedas, ShapeRenderer shapeRenderer) {
        String moedaText = String.valueOf(moedas);
        layoutMoeda.setText(font,moedaText);
        var x = 20;
        var y = (GET_ALTURA_TELA_BASE() - layoutMoeda.height) - 60;
        var yC = (GET_ALTURA_TELA_BASE() - layoutMoeda.height) - 90;

        desenharTextura.desenhar(batch, coin, null, x, yC, 48);
        desenharTexto.desenhar(batch, shapeRenderer, corMoeda, moedaText, font, x + 70, y, 10, true);
    }

    private void desenharLevel(Batch batch, int levelAtual, ShapeRenderer shapeRenderer) {
        String texto = "Level: " + levelAtual;
        layoutLevel.setText(font, texto);
        var x = 20;
        var y = (GET_ALTURA_TELA_BASE() - layoutLevel.height) - 10;

        desenharTexto.desenhar(batch, shapeRenderer, corLevel, texto, font, x + 70, y, 10, true);
    }

    private void desenharCoordenadas(Batch batch, ShapeRenderer shapeRenderer, int x, int y) {
        String coords = "X: " + x + " Y: " + y;
        layoutCoords.setText(font, coords);
        var xW = (GET_LARGURA_TELA_BASE() - layoutCoords.width) - 10;
        var yH = (GET_ALTURA_TELA_BASE() - layoutCoords.height) - 10;

        desenharTexto.desenhar(batch, shapeRenderer, corDebug, coords, font, xW, yH, 0, false);
    }

    private void desenharDirecaoOlhar(Batch batch, ShapeRenderer shapeRenderer, boolean olhandoEsquerda) {
        String direcaoOlhar = olhandoEsquerda ? "esquerda" : "direita";
        String olhando = "Olhando para: " + direcaoOlhar;
        layoutDirecao.setText(font, olhando);
        var x = (GET_LARGURA_TELA_BASE() - layoutDirecao.width) - 10;
        var y = (GET_ALTURA_TELA_BASE() - layoutDirecao.height) - 40;

        desenharTexto.desenhar(batch, shapeRenderer, corDebug, olhando, font, x, y, 0, false);
    }

    private void desenharDeltaTime(Batch batch, ShapeRenderer shapeRenderer, float delta) {
        String deltaT = "Delta timer: " + deltaTexto;
        layoutDelta.setText(font, deltaT);
        var x = (GET_LARGURA_TELA_BASE() - layoutDelta.width) - 10;
        var y = (GET_ALTURA_TELA_BASE() - layoutDelta.height) - 100;
        if (atualizarDesenho >= tempoParaDesenhar) {
            atualizarDesenho = 0;
            deltaTexto = delta;
        }
        desenharTexto.desenhar(batch, shapeRenderer, corDebug, deltaT, font, x, y, 0, false);
    }

    private void desenharScale(Batch batch, ShapeRenderer shapeRenderer) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float scale = Math.min(screenW / GET_LARGURA_TELA_BASE(), screenH / GET_ALTURA_TELA_BASE());
        String scaleText = "Scale: " + String.format("%.2f", scale);
        layoutScale.setText(font, scaleText);
        var x = (GET_LARGURA_TELA_BASE() - layoutScale.width) - 10;
        var y = (GET_ALTURA_TELA_BASE() - layoutScale.height) - 70;

        desenharTexto.desenhar(batch, shapeRenderer, corDebug, scaleText, font, x, y, 0, false);
    }

    public void mostrarMensagemEmBaixo(Batch batch, ShapeRenderer shapeRenderer, String mensagem) {
        layoutMensagem.setText(font, mensagem);
        var x = (GET_LARGURA_TELA_BASE() - layoutMensagem.width) / 2f;

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        desenharTexto.desenhar(batch, shapeRenderer, corDebug, mensagem, font, x, 100, 10, true);
        batch.end();
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }

    public DesenharTexto getDesenharTexto() {
        return desenharTexto;
    }
}
