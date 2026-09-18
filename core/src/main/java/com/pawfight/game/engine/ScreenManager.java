package com.pawfight.game.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.design.transition.FadeTransitionEffect;
import com.pawfight.game.engine.design.transition.SlideTransitionEffect;
import com.pawfight.game.engine.design.transition.TransitionEffect;

public class ScreenManager {

    private static ScreenManager instance;
    private final Matrix4 tempMatrix = new Matrix4();

    private final PawFight game;

    // Estado da transição
    private boolean transitioning = false;
    private boolean fadingOut = true;
    private Screen nextScreen;
    private float transitionDuration;
    private TransitionEffect currentEffect;

    // ────────────────────────────── Lifecycle ──────────────────────────────

    private ScreenManager(PawFight game) {
        this.game = game;
    }

    public static void init(PawFight game) {
        if (instance != null) {
            instance.dispose();
        }
        instance = new ScreenManager(game);
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ScreenManager não foi inicializado! Chame ScreenManager.init(game) antes.");
        }
        return instance;
    }

    // ──────────────────────────── Iniciar transição ────────────────────────
    public void fadeToScreen(Screen nextScreen, float duration, Color color, boolean useGradient) {
        disposeCurrentEffect();
        this.nextScreen = nextScreen;
        this.transitioning = true;
        this.fadingOut = true;
        this.transitionDuration = duration;
        this.currentEffect = new FadeTransitionEffect(duration, color, useGradient);
        this.currentEffect.reset();
        Gdx.app.log("ScreenManager", "Transição iniciada: fade -> " + nextScreen.getClass().getSimpleName()
            + " (" + duration + "s, gradiente=" + useGradient + ")");
    }

    public void fadeToScreen(Screen nextScreen, float duration) {
        fadeToScreen(nextScreen, duration, Color.BLACK, false);
    }

    public void slideToScreen(Screen nextScreen, float duration, SlideTransitionEffect.Direction direction) {
        disposeCurrentEffect();
        this.nextScreen = nextScreen;
        this.transitioning = true;
        this.fadingOut = true;
        this.transitionDuration = duration;
        this.currentEffect = new SlideTransitionEffect(duration, direction);
        this.currentEffect.reset();
        Gdx.app.log("ScreenManager", "Transição iniciada: slide " + direction + " -> " + nextScreen.getClass().getSimpleName()
            + " (" + duration + "s)");
    }

    // ────────────────────────── Update / Render ───────────────────────────
    public void update(float delta) {
        if (!transitioning || currentEffect == null) return;

        currentEffect.update(delta);

        // Troca de tela na metade da transição (quando o efeito cobre a tela toda)
        if (fadingOut && currentEffect.getElapsedTime() >= transitionDuration / 2f) {
            if (nextScreen != null) {
                game.setScreen(nextScreen);
                Gdx.app.log("ScreenManager", "Tela trocada no meio da transição: " + nextScreen.getClass().getSimpleName());
            }
            fadingOut = false;

            if (currentEffect instanceof FadeTransitionEffect fade) {
                fade.switchToFadeIn();
            } else if (currentEffect instanceof SlideTransitionEffect slide) {
                slide.switchToSlideIn();
            }
        }

        if (currentEffect.isFinished()) {
            transitioning = false;
            Gdx.app.log("ScreenManager", "Transição concluída.");
            disposeCurrentEffect();
        }
    }

    public void render(SpriteBatch batch) {
        if (!transitioning || currentEffect == null) return;

        // Salva a projeção atual e troca para coordenadas de tela
        tempMatrix.set(batch.getProjectionMatrix());
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        currentEffect.render(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Restaura a projeção original
        batch.setProjectionMatrix(tempMatrix);
    }

    // ──────────────────────────── Consultas ────────────────────────────────
    public boolean isTransitioning() {
        return transitioning;
    }

    // ──────────────────────────── Dispose ──────────────────────────────────

    private void disposeCurrentEffect() {
        if (currentEffect != null) {
            currentEffect.dispose();
            currentEffect = null;
        }
    }

    public void dispose() {
        disposeCurrentEffect();
        instance = null;
    }
}

