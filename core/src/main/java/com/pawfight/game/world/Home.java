package com.pawfight.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.Hud.CreateButton;
import com.pawfight.game.engine.design.transition.ScreenTransition;
import com.pawfight.game.world.base.Base;

import static com.pawfight.game.engine.CommunVariable.GET_ALTURA_TELA_BASE;
import static com.pawfight.game.engine.CommunVariable.GET_LARGURA_TELA_BASE;

public class Home implements Screen {
    private final ScreenTransition screenTransition;

    // Texturas para os estados do botão
    private final Texture normalTexturePlay = new Texture("menu/button/play/play1.png");
    private final Texture hoverTexturePlay = new Texture("menu/button/play/play2.png");
    private final Texture pressedTexturePlay = new Texture("menu/button/play/play3.png");

    private final Texture normalTextureQuit = new Texture("menu/button/quit/quit1.png");
    private final Texture hoverTextureQuit = new Texture("menu/button/quit/quit2.png");
    private final Texture pressedTextureQuit = new Texture("menu/button/quit/quit3.png");

    private ImageButton playButton;
    private ImageButton quitButton;
    private final CreateButton createButton;
    private final Stage stage;

    private final PawFight game;
    private final SpriteBatch batch;
    private Texture background;
    private Image backgroundImage;
    private Music backMusic;
    protected OrthographicCamera camera;
    protected Viewport viewport;

    public Home(PawFight game, OrthographicCamera camera, Viewport viewport) {
        this.game = game;
        this.camera = camera;
        this.viewport = viewport;

        batch = new SpriteBatch();

        try {
            background = new Texture("menu/menu.png");
            backgroundImage = new Image(background);
            backgroundImage.setFillParent(true); // ocupa toda a tela
        } catch (Exception e) {
            Gdx.app.error("Home", "Erro ao carregar background: " + e.getMessage(), e);
        }

        try {
            backMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/time_for_adventure.wav"));
            backMusic.setLooping(true);
            backMusic.setVolume(0.1f);
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    backMusic.play();
                }
            }, 2f);
        } catch (Exception e) {
            Gdx.app.error("Home", "Erro ao carregar música: " + e.getMessage(), e);
        }

        stage = new Stage(new FitViewport(GET_LARGURA_TELA_BASE(), GET_ALTURA_TELA_BASE()), batch);
        stage.addActor(backgroundImage);

        Gdx.input.setInputProcessor(stage);
        createButton = new CreateButton();

        screenTransition = new ScreenTransition(game);
        Gdx.app.log("Home", "Iniciando Home...");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        stage.act(delta);
        stage.draw();

        screenTransition.update(delta);
        screenTransition.render(batch);
    }

    @Override
    public void show() {
        if (playButton == null && quitButton == null) {
            try {
                playButton = createButton.create(stage,
                    GET_LARGURA_TELA_BASE()/2,
                    GET_ALTURA_TELA_BASE()/2,
                    200, 105,
                    normalTexturePlay, hoverTexturePlay, pressedTexturePlay);

                quitButton = createButton.create(stage,
                    GET_LARGURA_TELA_BASE()/2,
                    GET_ALTURA_TELA_BASE()/2 - 150,
                    200, 105,
                    normalTextureQuit, hoverTextureQuit, pressedTextureQuit);

                playButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        botaoPlayApertado();
                    }
                });
                quitButton.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        botaoQuitApertado();
                    }
                });
            } catch (Exception e) {
                Gdx.app.error("Home", "Erro ao criar botão: " + e.getMessage(), e);
            }
        }
    }

    private void botaoPlayApertado() {
        screenTransition.startFadeTransaction(new Base(game, camera, viewport), 1.5f, Color.BLACK, false);
        backMusic.stop();
    }

    public void botaoQuitApertado() {
        Gdx.app.exit();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        Gdx.input.setInputProcessor(stage);
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
        if (background != null) background.dispose();
        if (backMusic != null) backMusic.dispose();
        if (stage != null) stage.dispose();
        if (screenTransition != null) {
            screenTransition.dispose();
        }
    }
}
