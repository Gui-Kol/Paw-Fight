package com.pawfight.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.engine.design.transition.ScreenTransition;
import com.pawfight.game.engine.save.SaveDataPlayer;
import com.pawfight.game.engine.save.SaveGame;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.Home;
import com.pawfight.game.world.WorldTemplate;

import static com.pawfight.game.engine.CommunVariable.GET_ALTURA_TELA_BASE;
import static com.pawfight.game.engine.CommunVariable.GET_LARGURA_TELA_BASE;

public class PawFight extends Game {
    private final PawFight game = this;
    private SpriteBatch batch;
    private Texture image;
    private ScreenTransition transition;
    private boolean telaCheia = true;
    private boolean podeAlterarTelaCheia = true;

    // Camera + Viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("menu/BackGroundPawFight.png");

        camera = new OrthographicCamera();
        viewport = new FitViewport(GET_LARGURA_TELA_BASE(), GET_ALTURA_TELA_BASE(), camera);

        // MUITO IMPORTANTE: posicionar a camera no centro do mundo
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);

        toggleFullscreen();

        transition = new ScreenTransition(this);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                transition.startFadeTransaction(new Home(game, camera, viewport), 1f, Color.BLACK, false);
                image = new Texture("menu/dark_back_groud.png");
            }
        }, 2.5f);

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

        transition.update(delta);
        transition.render(batch);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);

        if (getScreen() instanceof WorldTemplate world) {
            if (world.getPlayer() != null) {
                world.getPlayer().getHud().resize(width, height);
                world.getPlayer().getStatusMenu().resize(width, height);
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
        image.dispose();
        if (transition != null) {
            transition.dispose();
        }
    }

    public void savePlayer(PlayerTemplate player) {
        SaveGame saveGame = new SaveGame();
        saveGame.saveGame(player.saveData());
    }

    public PlayerTemplate loadPlayer(PlayerTemplate player, String nomePersonagem) {
        SaveGame saveGame = new SaveGame();
        SaveDataPlayer data = saveGame.loadGame(nomePersonagem);
        if (data != null) {
            player.loadSaveData(data);
        }
        return player;
    }

    public void setPodeAlterarTelaCheia(boolean podeAlterarTelaCheia) {
        this.podeAlterarTelaCheia = podeAlterarTelaCheia;
    }
}
