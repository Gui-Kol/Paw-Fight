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
    private float minLoadingTime = 2f; // tempo mínimo em segundos
    private boolean minTimePassed = false;
    private final TextureRegion primeiroFrame;

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
        primeiroFrame = gifAnimation.animation.getKeyFrame(0);
    }

    @Override
    public void render(float delta) {
        stateTime += delta;

        // ── 1) Desenha o GIF ──
        ScreenUtils.clear(0, 0, 0, 1);
        TextureRegion frame = gifAnimation.animation.getKeyFrame(stateTime);
        if (frame == null){
            frame = primeiroFrame;
        }
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(frame, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        // ── 2) Enfileira assets depois do primeiro frame ──
        if (!assetsQueued) {
            assetsQueued = true;
            Assets.queueAll();
            Gdx.app.log("LoadingScreen", "Assets enfileirados para carregamento.");
        }

        // ── 3) Atualiza carregamento ──
        boolean finishedLoading = Assets.manager.update();

        // ── 4) Verifica se tempo mínimo já passou ──
        if (!minTimePassed && stateTime >= minLoadingTime) {
            minTimePassed = true;
            Gdx.app.log("LoadingScreen", "Tempo mínimo de loading atingido.");
        }

        // ── 5) Só troca de tela quando assets + tempo mínimo ──
        if (!loadingDone && finishedLoading && minTimePassed) {
            loadingDone = true;
            Gdx.app.log("LoadingScreen", "Assets carregados e tempo mínimo atingido!");
            game.onAssetsLoaded();
        }
    }


    @Override
    public void show() {
        stateTime = 0f;
        Gdx.app.log("LoadingScreen", "Tela de carregamento iniciada.");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (gifAnimation != null) {
            gifAnimation.dispose();
            gifAnimation = null;
            Gdx.app.log("LoadingScreen", "GIF animation disposed.");
        }
    }
}
