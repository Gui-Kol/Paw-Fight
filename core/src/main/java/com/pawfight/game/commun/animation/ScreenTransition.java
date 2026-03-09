package com.pawfight.game.commun.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pawfight.game.PawFight;

public class ScreenTransition {
    private float alpha = 0f;
    private boolean transitioning = false;
    private boolean fadingOut = true;
    private Screen nextScreen;
    private PawFight game;
    private Texture fadeTexture;

    public ScreenTransition(PawFight game) {
        this.game = game;
        fadeTexture = new Texture("menu/dark_back_groud.png"); // textura preta
    }

    public void start(Screen nextScreen) {
        this.nextScreen = nextScreen;
        this.transitioning = true;
        this.fadingOut = true;
        this.alpha = 0f;
    }

    public void update(float delta) {
        if (transitioning) {
            if (fadingOut) {
                alpha += delta; // escurece
                if (alpha >= 1f) {
                    // troca de tela
                    game.setScreen(nextScreen);
                    fadingOut = false; // começa fade-in
                }
            } else {
                alpha -= delta; // clareia
                if (alpha <= 0f) {
                    transitioning = false; // transição terminou
                    alpha = 0f;
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (transitioning) {
            batch.begin();
            batch.setColor(0, 0, 0, alpha);
            batch.draw(fadeTexture, 0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(1, 1, 1, 1);
            batch.end();
        }
    }

    public void dispose() {
        fadeTexture.dispose();
    }
}
