package com.pawfight.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.engine.ScreenManager;
import com.pawfight.game.engine.GameConfig;
import com.pawfight.game.engine.input.GameAction;
import com.pawfight.game.engine.input.GamepadBindings;
import com.pawfight.game.engine.input.GerenciadorGamepad;
import com.pawfight.game.engine.input.GerenciadorInput;
import com.pawfight.game.engine.input.KeyBindings;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.engine.loading.LoadingScreen;
import com.pawfight.game.world.Home;
import com.pawfight.game.world.template.WorldTemplate;

import static com.pawfight.game.engine.GameConfig.ALTURA_TELA_BASE;
import static com.pawfight.game.engine.GameConfig.LARGURA_TELA_BASE;

public class PawFight extends Game {
    private final PawFight game = this;
    private SpriteBatch batch;
    private Texture image;
    private boolean podeAlterarTelaCheia = true;
    private Music audio;

    private OrthographicCamera camera;
    private Viewport viewport;

    @Override
    public void create() {
        KeyBindings.init();
        GamepadBindings.init();
        GerenciadorGamepad.init(); // seguro sem controle: jogo segue só com teclado/mouse
        GerenciadorInput.init();
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(LARGURA_TELA_BASE, ALTURA_TELA_BASE, camera);

        // MUITO IMPORTANTE: posicionar a camera no centro do mundo
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);

        GameConfig.getInstance().carregarConfiguracoes();
        definirTelaCheia(GameConfig.getInstance().isTelaCheia());

        ScreenManager.init(this);

        setScreen(new LoadingScreen(this, camera, viewport));

        Gdx.app.log("PawFight", "Iniciando jogo...");
    }

    public void onAssetsLoaded() {
        // Faz dispose da LoadingScreen (libera texturas do GIF)
        Screen loadingScreen = getScreen();
        if (loadingScreen != null) {
            loadingScreen.dispose();
        }

        image = Assets.get("menu/dark_back_groud.png", Texture.class);

        setScreen(null);
        ScreenManager.getInstance().fadeToScreen(new Home(game, camera, viewport), 1f, Color.BLACK, false);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // Atualiza o estado dos dispositivos (bordas do gamepad, hotplug) antes de qualquer tela
        GerenciadorInput.getInstance().atualizar(delta);

        // Tela cheia sem tecla hardcoded: qualquer binding (teclado ou controle) dispara a ação
        if (GerenciadorInput.getInstance().isPressionadaAgora(GameAction.TELA_CHEIA)) {
            toggleFullscreen();
        }
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // desenha o background ocupando todo o mundo do viewport (null durante o loading)
        if (image != null) {
            batch.draw(image, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        }
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
        definirTelaCheia(!Gdx.graphics.isFullscreen());
    }

    public void definirTelaCheia(boolean telaCheia) {
        if (!podeAlterarTelaCheia) {
            return;
        }
        var graphics = Gdx.graphics;
        if (graphics.isFullscreen() != telaCheia) {
            if (telaCheia) {
                graphics.setFullscreenMode(graphics.getDisplayMode());
            } else {
                graphics.setUndecorated(false);
                graphics.setWindowedMode(1280, 720);
            }
        }
        GameConfig config = GameConfig.getInstance();
        config.setTelaCheia(graphics.isFullscreen());
        config.salvarConfiguracoes();
    }

    @Override
    public void dispose() {
        if (getScreen() instanceof WorldTemplate world && world.getPlayer() != null) {
            world.getPlayer().getHudPause().dispose();
        }
        batch.dispose();
        // image e audio são gerenciados pelo AssetManager — NÃO dar dispose aqui
        ScreenManager.getInstance().dispose();
        Assets.dispose();
        GerenciadorGamepad.getInstance().dispose();
        Gdx.app.log("PawFight", "foi disposed");
    }

    public void setPodeAlterarTelaCheia(boolean podeAlterarTelaCheia) {
        this.podeAlterarTelaCheia = podeAlterarTelaCheia;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
