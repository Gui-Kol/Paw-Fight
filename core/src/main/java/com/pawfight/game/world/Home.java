package com.pawfight.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.pawfight.game.PawFight;
import com.pawfight.game.commun.Hud.CreateButton;
import com.pawfight.game.commun.animation.ScreenTransition;
import com.pawfight.game.world.base.Base;

public class Home implements Screen {
    private ScreenTransition screenTransition;

    // Texturas para os estados do botão
    private Texture normalTexture = new Texture("menu/new_game_normal.png");
    private Texture hoverTexture = new Texture("menu/new_game_hover.png");
    private Texture pressedTexture = new Texture("menu/new_game_pressed.png");

    private ImageButton newGameButton;
    private CreateButton createButton;
    private Stage stage;

    private PawFight game;
    private SpriteBatch batch;
    private Texture background;
    private Music backMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/time_for_adventure.wav"));

    public Home(PawFight game) {
        this.game = game;
        batch = new SpriteBatch();
        background = new Texture("menu/menu.png");

        backMusic.setLooping(true);
        backMusic.setVolume(0.1f);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                backMusic.play();
            }
        }, 2f);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        createButton = new CreateButton();

        screenTransition = new ScreenTransition(game);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();
        batch.draw(background, 0, 0);
        batch.end();

        stage.act(delta);
        stage.draw();

        screenTransition.update(delta);
        screenTransition.render(batch);
    }

    @Override
    public void show() {
        if (newGameButton == null) {
            // Agora passamos o stage corretamente
            newGameButton = createButton.create(stage,
                Gdx.graphics.getWidth()/2 - 100, // centraliza horizontalmente
                Gdx.graphics.getHeight()/3,     // posição vertical
                200, 80,
                normalTexture, hoverTexture, pressedTexture);

            newGameButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    botaoNewGameApertado();
                }
            });
        }
    }

    public void botaoNewGameApertado() {
        screenTransition.start(new Base(game));
        backMusic.stop();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        if (backMusic != null) {
            backMusic.stop();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        if (backMusic != null) backMusic.dispose();
        if (stage != null) stage.dispose();
    }
}
