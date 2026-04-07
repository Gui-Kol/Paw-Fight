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
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.engine.GameConfig;
import com.pawfight.game.engine.ScreenManager;

import static com.pawfight.game.engine.GameConfig.LARGURA_TELA_BASE;
import static com.pawfight.game.engine.GameConfig.ALTURA_TELA_BASE;
import com.pawfight.game.engine.hud.CriarBotao;
import com.pawfight.game.engine.hud.HudStage;
import com.pawfight.game.world.base.Base;


public class Home implements Screen {
    private final HudStage hudStage;

    // Texturas para os estados do botão
    private final Texture normalTexturePlay = Assets.get("menu/button/play/play1.png", Texture.class);
    private final Texture hoverTexturePlay = Assets.get("menu/button/play/play2.png", Texture.class);
    private final Texture pressedTexturePlay = Assets.get("menu/button/play/play3.png", Texture.class);

    private final Texture normalTextureQuit = Assets.get("menu/button/quit/quit1.png", Texture.class);
    private final Texture hoverTextureQuit = Assets.get("menu/button/quit/quit2.png", Texture.class);
    private final Texture pressedTextureQuit = Assets.get("menu/button/quit/quit3.png", Texture.class);

    private ImageButton playButton;
    private ImageButton quitButton;
    private final CriarBotao criarBotao;

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

        try {
            background = Assets.get("menu/menu.png", Texture.class);
            backgroundImage = new Image(background);
            backgroundImage.setFillParent(true); // ocupa toda a tela
        } catch (Exception e) {
            Gdx.app.error("Home", "Erro ao carregar background: " + e.getMessage(), e);
        }

        try {
            backMusic = Assets.get("audio/music/home.wav", Music.class);
            backMusic.setLooping(true);
            backMusic.setVolume(GameConfig.getInstance().getVolumeMusica());
            backMusic.play();
        } catch (Exception e) {
            Gdx.app.error("Home", "Erro ao carregar música: " + e.getMessage(), e);
        }
        criarBotao = new CriarBotao();

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
    }

    @Override
    public void show() {
        if (playButton == null && quitButton == null) {
            try {
                Stage stage = hudStage.getStage();
                playButton = criarBotao.create(stage,
                    LARGURA_TELA_BASE / 2,
                    ALTURA_TELA_BASE / 2,
                    200, 105,
                    normalTexturePlay, hoverTexturePlay, pressedTexturePlay);

                quitButton = criarBotao.create(stage,
                    LARGURA_TELA_BASE / 2,
                    ALTURA_TELA_BASE / 2 - 150,
                    200, 105,
                    normalTextureQuit, hoverTextureQuit, pressedTextureQuit);

                playButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ScreenManager.getInstance().fadeToScreen(new Base(game, camera, viewport), 1.5f, Color.BLACK, false);
                        criarBotao.playClickSound();
                        backMusic.stop();
                    }
                });
                quitButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        criarBotao.playClickSound();
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
        // Texturas e música são gerenciadas pelo AssetManager — NÃO dar dispose aqui
        // Transição é gerenciada pelo ScreenManager — NÃO dar dispose aqui
        if (hudStage != null) hudStage.dispose();
        Gdx.app.log("Home","foi disposed");
    }
}
