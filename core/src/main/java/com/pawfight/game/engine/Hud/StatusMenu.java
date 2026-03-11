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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pawfight.game.engine.font.FontFactory;
import com.pawfight.game.entity.player.PlayerTemplate;

public class StatusMenu {
    private Stage stage;
    private PlayerTemplate player;
    private CreateButton buttonFactory;
    private BitmapFont font;
    private Texture coracao;
    private Texture raio;
    private Texture musculo;
    private Texture requa;
    private Image background;

    float centerX;
    float centerY;

    private Texture normalTexture;
    private Texture hoverTexture;
    private Texture pressedTexture;
    private Texture backgroundTexture;

    public StatusMenu(PlayerTemplate player) {
        this.player = player;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        buttonFactory = new CreateButton();
        font = FontFactory.createCustomFont("fonts/PixelOperator8-Bold.ttf", 30);

        normalTexture = new Texture("menu/new_game_normal.png");
        hoverTexture = new Texture("menu/new_game_hover.png");
        pressedTexture = new Texture("menu/new_game_pressed.png");
        backgroundTexture = new Texture("menu/dark_back_groud.png");

        coracao = new Texture("Hud/coracao1.png");
        raio = new Texture("Hud/raio.png");
        musculo = new Texture("Hud/musculo.png");
        background = new Image(backgroundTexture);


        centerX = Gdx.graphics.getWidth() / 2f;
        centerY = Gdx.graphics.getHeight() / 2f;

        criarMenu();
    }

    public void criarMenu() {
        stage.clear();

        // Fundo centralizado
        background.setSize(600, 500);
        background.setPosition(
            (Gdx.graphics.getWidth() - background.getWidth()) / 2f,
            (Gdx.graphics.getHeight() - background.getHeight()) / 2f
        );
        stage.addActor(background);

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        // Pontos disponíveis
        Label pontosLabel = new Label("Points: " + player.getPontosDisponiveis(), style);
        pontosLabel.setPosition(centerX - background.getWidth()/4, centerY + 150);
        stage.addActor(pontosLabel);

        // Vida Base
        Label vidaLabel = new Label("Life: " + player.getVidaBase(), style);
        vidaLabel.setPosition(centerX - background.getWidth()/2 + 20, centerY + 60);
        stage.addActor(vidaLabel);

        var vidaButton = buttonFactory.create(stage, (int)(centerX + 150), (int)(centerY + 60), 48, 48,
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
        forcaLabel.setPosition(centerX - background.getWidth()/2 + 20, centerY);
        stage.addActor(forcaLabel);

        var forcaButton = buttonFactory.create(stage, (int)(centerX + 150), (int)(centerY), 48, 48,
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
        velocidadeLabel.setPosition(centerX - background.getWidth()/2 + 20, centerY - 60);
        stage.addActor(velocidadeLabel);

        var velocidadeButton = buttonFactory.create(stage, (int)(centerX + 150), (int)(centerY - 60), 48, 48,
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
        batch.draw(raio, centerX + 200, centerY - 70, 72, 72);
        batch.draw(musculo, centerX + 200, centerY - 10, 72, 72);
        batch.draw(coracao, centerX + 210, centerY + 60, 48, 48);
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
        requa.dispose();
    }
}
