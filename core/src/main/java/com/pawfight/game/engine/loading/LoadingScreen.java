package com.pawfight.game.engine.loading;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;

public class LoadingScreen implements Screen {

    private final PawFight game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;

    private GifDecoder.GifAnimation gifAnimation;
    private float stateTime = 0f;
    private boolean assetsQueued = false;
    private boolean loadingDone = false;

    public LoadingScreen(PawFight game, OrthographicCamera camera, Viewport viewport) {
        this.game = game;
        this.camera = camera;
        this.viewport = viewport;
        this.batch = game.getBatch();

        // Escolhe aleatoriamente qual GIF exibir
        String gifPath = MathUtils.randomBoolean()
            ? "menu/loading/loadingScreen.gif"
            : "menu/loading/loadingScreen2.gif";

        Gdx.app.log("LoadingScreen", "Exibindo: " + gifPath);
        gifAnimation = GifDecoder.loadGifAnimation(Gdx.files.internal(gifPath));
    }

    @Override
    public void render(float delta) {
        stateTime += delta;

        // ── 1) Desenha o GIF PRIMEIRO — garante que a tela aparece antes de tudo ──
        ScreenUtils.clear(0, 0, 0, 1);

        TextureRegion frame = gifAnimation.animation.getKeyFrame(stateTime);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(frame, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        // ── 2) Só enfileira os assets DEPOIS do primeiro frame já estar na tela ──
        if (!assetsQueued) {
            assetsQueued = true;
            Assets.queueAll();
            Gdx.app.log("LoadingScreen", "Assets enfileirados para carregamento.");
            return; // volta para renderizar o próximo frame antes de começar update()
        }

        // ── 3) Atualiza o carregamento assíncrono dos assets ──
        if (!loadingDone && Assets.manager.update()) {
            loadingDone = true;
            Gdx.app.log("LoadingScreen", "Assets carregados! Progresso: 100%");
            game.onAssetsLoaded();
        }
    }

    @Override
    public void show() {
        Gdx.app.log("LoadingScreen", "Tela de carregamento iniciada.");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        if (gifAnimation != null) {
            gifAnimation.dispose();
            gifAnimation = null;
            Gdx.app.log("LoadingScreen", "GIF animation disposed.");
        }
    }
}
