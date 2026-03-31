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
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.Hud.CriarBotao;
import com.pawfight.game.engine.Hud.HudStage;
import com.pawfight.game.engine.design.desenhar.DesenharTextura;
import com.pawfight.game.engine.design.transition.TransicaoTela;
import com.pawfight.game.world.base.Base;

import static com.pawfight.game.engine.VariavelComum.*;

public class Home implements Screen {
    private final TransicaoTela TransicaoTela;
    private final HudStage hudStage;
    private final DesenharTextura desenharTextura;

    // Texturas para os estados do botão
    private final Texture normalTexturePlay = new Texture("menu/button/play/play1.png");
    private final Texture hoverTexturePlay = new Texture("menu/button/play/play2.png");
    private final Texture pressedTexturePlay = new Texture("menu/button/play/play3.png");

    private final Texture normalTextureQuit = new Texture("menu/button/quit/quit1.png");
    private final Texture hoverTextureQuit = new Texture("menu/button/quit/quit2.png");
    private final Texture pressedTextureQuit = new Texture("menu/button/quit/quit3.png");

    private ImageButton playButton;
    private ImageButton quitButton;
    private final CriarBotao CriarBotao;

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
        this.batch = game.getBatch();
        hudStage = new HudStage();
        desenharTextura = new DesenharTextura();

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
        } catch (Exception e) {
            Gdx.app.error("Home", "Erro ao carregar música: " + e.getMessage(), e);
        }
        CriarBotao = new CriarBotao();

        TransicaoTela = new TransicaoTela(game);
        Gdx.app.log("Home", "Iniciando Home...");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();
        hudStage.render();

        TransicaoTela.update(delta);
        TransicaoTela.render(batch);
    }

    @Override
    public void show() {
        if (backMusic != null && !backMusic.isPlaying()) {
            backMusic.play();
        }
        if (playButton == null && quitButton == null) {
            try {
                Stage stage = hudStage.getStage();
                playButton = CriarBotao.create(stage,
                    GET_LARGURA_TELA_BASE() / 2,
                    GET_ALTURA_TELA_BASE() / 2,
                    200, 105,
                    normalTexturePlay, hoverTexturePlay, pressedTexturePlay);

                quitButton = CriarBotao.create(stage,
                    GET_LARGURA_TELA_BASE() / 2,
                    GET_ALTURA_TELA_BASE() / 2 - 150,
                    200, 105,
                    normalTextureQuit, hoverTextureQuit, pressedTextureQuit);

                playButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        TransicaoTela.startFadeTransaction(new Base(game, camera, viewport), 1.5f, Color.BLACK, false);
                        backMusic.stop();
                    }
                });
                quitButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Gdx.app.exit();
                    }
                });
            } catch (Exception e) {
                Gdx.app.error("Home", "Erro ao criar botão: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        hudStage.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        if (backMusic != null) {
            backMusic.stop();
        }
    }

    @Override
    public void dispose() {
        if (background != null) background.dispose();
        if (backMusic != null) backMusic.dispose();
        if (hudStage != null) hudStage.dispose();
        if (TransicaoTela != null) {
            TransicaoTela.dispose();
        }
        Gdx.app.log("Home","foi disposed");
    }
}
