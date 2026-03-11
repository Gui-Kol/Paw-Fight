package com.pawfight.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.pawfight.game.engine.animation.ScreenTransition;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.Home;

public class PawFight extends Game {
    private PawFight game = this;
    private SpriteBatch batch;
    private Texture image;
    private ScreenTransition transition;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("menu/BackGroundPawFight.png");

        // Inicializa transição
        transition = new ScreenTransition(this);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                transition.startFadeTransaction(new Home(game), 1f, Color.BLACK, false);
                image = new Texture("menu/dark_back_groud.png");
            }
        }, 2.5f);

        Gdx.app.log("PawFight", "Iniciando jogo...");
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


    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();
        batch.draw(image, 0, 0);
        batch.end();

        super.render();

        transition.update(delta);
        transition.render(batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        if (transition != null) {
            transition.dispose();
        }
    }
}
