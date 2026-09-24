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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class ScreenManager {

    private static ScreenManager instance;
    private final Matrix4 tempMatrix = new Matrix4();

    private final PawFight game;

    private boolean transitioning = false;
    private boolean fadingOut = true;
    private Screen nextScreen;
    private float transitionDuration;
    private TransitionEffect currentEffect;
    private boolean encerrarSessaoAoTrocar;
    private final Set<Screen> telasDescartadas = Collections.newSetFromMap(new IdentityHashMap<>());

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

    public void fadeToScreen(Screen nextScreen, float duration, Color color, boolean useGradient) {
        iniciarNovaTransicao(nextScreen, duration);
        this.currentEffect = new FadeTransitionEffect(transitionDuration, color, useGradient);
        this.currentEffect.reset();
        Gdx.app.log("ScreenManager", "Transição iniciada: fade -> " + nextScreen.getClass().getSimpleName()
            + " (" + duration + "s, gradiente=" + useGradient + ")");
    }

    public void fadeToScreenEncerrandoSessao(Screen nextScreen, float duration, Color color, boolean useGradient) {
        fadeToScreen(nextScreen, duration, color, useGradient);
        encerrarSessaoAoTrocar = true;
    }

    private void iniciarNovaTransicao(Screen nextScreen, float duration) {
        if (nextScreen == null) {
            throw new IllegalArgumentException("A próxima tela não pode ser nula.");
        }
        cancelarDestinoPendente();
        disposeCurrentEffect();
        this.nextScreen = nextScreen;
        this.transitioning = true;
        this.fadingOut = true;
        this.transitionDuration = Math.max(0.01f, duration);
        this.encerrarSessaoAoTrocar = false;
    }

    public void fadeToScreen(Screen nextScreen, float duration) {
        fadeToScreen(nextScreen, duration, Color.BLACK, false);
    }

    public void slideToScreen(Screen nextScreen, float duration, SlideTransitionEffect.Direction direction) {
        iniciarNovaTransicao(nextScreen, duration);
        this.currentEffect = new SlideTransitionEffect(transitionDuration, direction);
        this.currentEffect.reset();
        Gdx.app.log("ScreenManager", "Transição iniciada: slide " + direction + " -> " + nextScreen.getClass().getSimpleName()
            + " (" + duration + "s)");
    }

    public void update(float delta) {
        if (!transitioning || currentEffect == null) return;

        currentEffect.update(delta);

        // Troca de tela na metade da transição (quando o efeito cobre a tela toda)
        if (fadingOut && currentEffect.getElapsedTime() >= transitionDuration / 2f) {
            if (nextScreen != null) {
                Screen telaAnterior = game.getScreen();
                game.setScreen(nextScreen);
                Gdx.app.log("ScreenManager", "Tela trocada no meio da transição: " + nextScreen.getClass().getSimpleName());
                nextScreen = null;
                descartarTela(telaAnterior);
                if (encerrarSessaoAoTrocar) {
                    game.getGameSession().encerrar();
                    encerrarSessaoAoTrocar = false;
                }
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

        batch.setProjectionMatrix(tempMatrix);
    }

    public boolean isTransitioning() {
        return transitioning;
    }

    private void disposeCurrentEffect() {
        if (currentEffect != null) {
            currentEffect.dispose();
            currentEffect = null;
        }
    }

    private void cancelarDestinoPendente() {
        if (nextScreen != null && nextScreen != game.getScreen()) {
            descartarTela(nextScreen);
        }
        nextScreen = null;
    }

    private void descartarTela(Screen tela) {
        if (tela != null && telasDescartadas.add(tela)) {
            tela.dispose();
        }
    }

    public void dispose() {
        cancelarDestinoPendente();
        disposeCurrentEffect();
        descartarTela(game.getScreen());
        transitioning = false;
        instance = null;
    }
}
