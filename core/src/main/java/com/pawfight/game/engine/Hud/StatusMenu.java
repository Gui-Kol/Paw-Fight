package com.pawfight.game.engine.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.FitViewport; // Adicionado import
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.engine.font.FontFactory;
import com.pawfight.game.entity.player.PlayerTemplate;

public class StatusMenu {
    private final Stage stage;
    private final PlayerTemplate player;
    private final CreateButton buttonFactory;
    private final BitmapFont font;
    private final Texture coracao;
    private final Texture raio;
    private final Texture musculo;
    private final Image background;

    float centerX;
    float centerY;
    private final float scale; // Adicionado para escala proporcional

    private final Texture normalTexture;
    private final Texture hoverTexture;
    private final Texture pressedTexture;
    private final Texture backgroundTexture;

    public StatusMenu(PlayerTemplate player) {
        this.player = player;

        // Calcular escala proporcional baseada na resolução atual vs base 1920x1080
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float baseW = 1920f;
        float baseH = 1080f;
        scale = Math.min(screenW / baseW, screenH / baseH);

        // Mudar para FitViewport para manter proporções
        // Adicionado para viewport proporcional
        Viewport stageViewport = new FitViewport(1920, 1080);
        stage = new Stage(stageViewport);
        Gdx.input.setInputProcessor(stage);

        buttonFactory = new CreateButton();
        font = FontFactory.createCustomFont("fonts/PixelOperator8-Bold.ttf", (int)(30 * scale)); // Aplicar escala à fonte

        normalTexture = new Texture("menu/new_game_normal.png");
        hoverTexture = new Texture("menu/new_game_hover.png");
        pressedTexture = new Texture("menu/new_game_pressed.png");
        backgroundTexture = new Texture("menu/dark_back_groud.png");

        coracao = new Texture("Hud/coracao1.png");
        raio = new Texture("Hud/raio.png");
        musculo = new Texture("Hud/musculo.png");
        background = new Image(backgroundTexture);

        centerX = 1920 / 2f; // Usar coordenadas do viewport base
        centerY = 1080 / 2f;

        criarMenu();
    }

    public void criarMenu() {
        stage.clear();

        // Fundo centralizado e escalado
        background.setSize(600 * scale, 500 * scale);
        background.setPosition(
            (1920 - background.getWidth()) / 2f,
            (1080 - background.getHeight()) / 2f
        );
        stage.addActor(background);

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        // Pontos disponíveis
        Label pontosLabel = new Label("Points: " + player.getPontosDisponiveis(), style);
        pontosLabel.setPosition(centerX - (background.getWidth()/4), centerY + 150 * scale);
        stage.addActor(pontosLabel);

        // Vida Base
        Label vidaLabel = new Label("Life: " + player.getVidaBase(), style);
        vidaLabel.setPosition(centerX - (background.getWidth()/2) + 20 * scale, centerY + 60 * scale);
        stage.addActor(vidaLabel);

        var vidaButton = buttonFactory.create(stage, (int)(centerX + 150 * scale), (int)(centerY + 60 * scale), (int)(48 * scale), (int)(48 * scale),
            normalTexture, hoverTexture, pressedTexture);
        vidaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (player.getPontosDisponiveis() > 0) {
                    player.vidaBaseUp(1);
                    criarMenu();
                }
            }
        });

        // Força
        Label forcaLabel = new Label("Strength: " + player.getForca(), style);
        forcaLabel.setPosition(centerX - (background.getWidth()/2) + 20 * scale, centerY);
        stage.addActor(forcaLabel);

        var forcaButton = buttonFactory.create(stage, (int)(centerX + 150 * scale), (int)(centerY), (int)(48 * scale), (int)(48 * scale),
            normalTexture, hoverTexture, pressedTexture);
        forcaButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (player.getPontosDisponiveis() > 0) {
                    player.forcaUp(1);
                    criarMenu();
                }
            }
        });

        // Velocidade
        Label velocidadeLabel = new Label("Speed: " + player.getVelocidade(), style);
        velocidadeLabel.setPosition(centerX - (background.getWidth()/2) + 20 * scale, centerY - 60 * scale);
        stage.addActor(velocidadeLabel);

        var velocidadeButton = buttonFactory.create(stage, (int)(centerX + 150 * scale), (int)(centerY - 60 * scale), (int)(48 * scale), (int)(48 * scale),
            normalTexture, hoverTexture, pressedTexture);
        velocidadeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (player.getPontosDisponiveis() > 0) {
                    player.velocidadeUp(1);
                    criarMenu();
                }
            }
        });
    }

    public void draw(Batch batch, OrthographicCamera hudCamera) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        batch.draw(raio, centerX + 200 * scale, centerY - 70 * scale, 72 * scale, 72 * scale);
        batch.draw(musculo, centerX + 200 * scale, centerY - 10 * scale, 72 * scale, 72 * scale);
        batch.draw(coracao, centerX + 210 * scale, centerY + 60 * scale, 48 * scale, 48 * scale);
        batch.end();
    }

    public void dispose() {
        stage.dispose();
        font.dispose();
        normalTexture.dispose();
        hoverTexture.dispose();
        pressedTexture.dispose();
        backgroundTexture.dispose();
        coracao.dispose();
        raio.dispose();
        musculo.dispose();
    }
}
