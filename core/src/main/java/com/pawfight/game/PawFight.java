package com.pawfight.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.engine.Assets;
import com.pawfight.game.engine.GameConfig;
import com.pawfight.game.engine.ScreenManager;
import com.pawfight.game.engine.input.KeyBindings;
import com.pawfight.game.engine.save.DadosSalvosJogador;
import com.pawfight.game.engine.save.SalvarJogo;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.Home;
import com.pawfight.game.world.template.WorldTemplate;

import static com.pawfight.game.engine.GameConfig.LARGURA_TELA_BASE;
import static com.pawfight.game.engine.GameConfig.ALTURA_TELA_BASE;

public class PawFight extends Game {
    private final PawFight game = this;
    private SpriteBatch batch;
    private Texture image;
    private boolean telaCheia = true;
    private boolean podeAlterarTelaCheia = true;
    private Music audio;

    // Camera + Viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    @Override
    public void create() {
        Assets.loadAll();
        KeyBindings.init();
        batch = new SpriteBatch();
        image = Assets.get("menu/BackGroundPawFight.png", Texture.class);

        camera = new OrthographicCamera();
        viewport = new FitViewport(LARGURA_TELA_BASE, ALTURA_TELA_BASE, camera);

        // MUITO IMPORTANTE: posicionar a camera no centro do mundo
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);

        toggleFullscreen();
        toggleFullscreen();

        ScreenManager.init(this);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                ScreenManager.getInstance().fadeToScreen(new Home(game, camera, viewport), 1f, Color.BLACK, false);
                image = Assets.get("menu/dark_back_groud.png", Texture.class);
            }
        }, 2.5f);
        audio = Assets.get("audio/sounds/MenuInicial/inicio.wav", Music.class);
        audio.setVolume(GameConfig.getInstance().getVolumeMusica());
        audio.play();

        Gdx.app.log("PawFight", "Iniciando jogo...");
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            toggleFullscreen();
        }

        float delta = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // desenha o background ocupando todo o mundo do viewport
        batch.draw(image, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        super.render();

        ScreenManager.getInstance().update(delta);
        ScreenManager.getInstance().render(batch);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);

        if (getScreen() instanceof WorldTemplate world) {
            if (world.getPlayer() != null) {
                world.getPlayer().getHud().resize(width, height);
            }
        }

        if (getScreen() instanceof Home home) {
           home.resize(width, height);
        }
    }


    public void toggleFullscreen() {
        if (!podeAlterarTelaCheia) {
            return;
        }
        var graphics = Gdx.graphics;
        Graphics.DisplayMode displayMode = graphics.getDisplayMode();

        if (!telaCheia) {
            Gdx.app.log("PawFight", "Entrando em modo fullscreen...");
            graphics.setUndecorated(true);
            graphics.setWindowedMode(displayMode.width, displayMode.height);
            telaCheia = true;
        } else {
            Gdx.app.log("PawFight", "Entrando em modo janela...");
            graphics.setUndecorated(false);
            graphics.setWindowedMode(1280, 720);
            telaCheia = false;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        // image e audio são gerenciados pelo AssetManager — NÃO dar dispose aqui
        ScreenManager.getInstance().dispose();
        Assets.dispose(); // libera TODOS os assets de uma vez
        Gdx.app.log("PawFight","foi disposed");
    }

    public void setPodeAlterarTelaCheia(boolean podeAlterarTelaCheia) {
        this.podeAlterarTelaCheia = podeAlterarTelaCheia;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
