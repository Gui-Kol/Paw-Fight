package com.pawfight.game.engine.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.pawfight.game.PawFight;

public class ScreenTransition {
    private boolean transitioning = false;
    private boolean fadingOut = true;
    private Screen nextScreen;
    private final PawFight game;
    private float transitionDuration;
    private TransitionEffect currentEffect;
    private static final float DEFAULT_DURATION = 1.2f;

    public ScreenTransition(PawFight game) {
        this(game, DEFAULT_DURATION);
    }

    public ScreenTransition(PawFight game, float duration) {
        this.game = game;
        this.transitionDuration = duration;
        this.currentEffect = new FadeTransitionEffect(duration);
    }

    public void startSlideTransaction(Screen nextScreen, float duration, SlideTransitionEffect.Direction direction) {
        this.nextScreen = nextScreen;
        this.transitioning = true;
        this.fadingOut = true;
        this.currentEffect = new SlideTransitionEffect(duration, direction);
        this.currentEffect.reset();
        this.transitionDuration = duration;
    }

    public void startFadeTransaction(Screen nextScreen, float duration, com.badlogic.gdx.graphics.Color color, boolean useGradient) {
        this.nextScreen = nextScreen;
        this.transitioning = true;
        this.fadingOut = true;
        this.currentEffect = new FadeTransitionEffect(duration, color, useGradient);
        this.currentEffect.reset();
        this.transitionDuration = duration;
    }

    public void start(Screen screen) {
        game.setScreen(screen);
    }

    public void update(float delta) {
        if (transitioning) {
            currentEffect.update(delta);

            // Muda de tela na metade da transição
            if (fadingOut && currentEffect instanceof FadeTransitionEffect) {
                FadeTransitionEffect fade = (FadeTransitionEffect) currentEffect;
                if (fade.getElapsedTime() >= transitionDuration / 2f) {
                    if (nextScreen != null) {
                        game.setScreen(nextScreen);
                    }
                    fadingOut = false;
                    fade.switchToFadeIn();
                }
            } else if (fadingOut && currentEffect instanceof SlideTransitionEffect) {
                SlideTransitionEffect slide = (SlideTransitionEffect) currentEffect;
                if (slide.getElapsedTime() >= transitionDuration / 2f) {
                    if (nextScreen != null) {
                        game.setScreen(nextScreen);
                    }
                    fadingOut = false;
                    slide.switchToSlideIn();
                }
            }

            if (currentEffect.isFinished()) {
                transitioning = false;
                currentEffect.reset(); // Reset para reutilização
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (transitioning) {
            // Salva a matriz de projeção atual
            Matrix4 oldMatrix = new Matrix4(batch.getProjectionMatrix());

            // Define para coordenadas de tela
            batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            currentEffect.render(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            // Restaura a matriz original
            batch.setProjectionMatrix(oldMatrix);
        }
    }

    public boolean isTransitioning() {
        return transitioning;
    }

    public void dispose() {
        if (currentEffect != null) {
            currentEffect.dispose();
        }
    }
}
