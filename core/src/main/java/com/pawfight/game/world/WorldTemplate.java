package com.pawfight.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.LayerRenderer;
import com.pawfight.game.engine.design.desenhar.DrawList;
import com.pawfight.game.engine.phisics.DanoTiro;
import com.pawfight.game.engine.phisics.DrawHitBox;
import com.pawfight.game.engine.phisics.TilemapHitboxFactory;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public abstract class WorldTemplate implements Screen {

    // Física
    protected TilemapHitboxFactory tilemapHitboxFactory;

    // Base
    protected LayerRenderer layerRenderer;
    protected TiledMap map;

    // Entidades
    protected PlayerTemplate player;

    // Mundo
    protected DrawHitBox drawHitBox;
    protected DanoTiro danoTiro;
    protected DrawList drawList;
    protected ShapeRenderer shapeRenderer;
    protected Texture background;
    protected PawFight game;
    protected SpriteBatch batch;
    protected Music backMusic;
    protected OrthographicCamera camera;
    protected Viewport viewport;

    public WorldTemplate(PawFight game, String backgroundPath, String musicPath, OrthographicCamera camera, Viewport viewport) {
        this.game = game;
        this.camera = camera;
        this.viewport = viewport;

        drawList = new DrawList();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        background = new Texture(backgroundPath);
        backMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
        tilemapHitboxFactory = new TilemapHitboxFactory();
        drawHitBox = new DrawHitBox();
        danoTiro = new DanoTiro();
    }

    public void setPlayer(PlayerTemplate player) {
        this.player = player;
    }

    @Override
    public void show() {
        try {
            map = new TmxMapLoader().load(getMapPath());
            layerRenderer = new LayerRenderer(map);
            backMusic.setLooping(true);
            backMusic.setVolume(0);
            backMusic.play();
        } catch (Exception e) {
            Gdx.app.error("WorldTemplate", "Erro ao carregar mundo: " + e.getMessage(), e);
        }
    }

    @Override
    public void render(float delta) {
        if (map == null || layerRenderer == null) {
            Gdx.app.error("WorldTemplate", "Mapa não carregado!");
            return;
        }

        renderLayers();
        if (player != null) {
            updatePlayer(delta);
        }
        renderLayersUp();

        if (player != null) {
            player.drawHud(batch, shapeRenderer);
            player.drawStatusMenu(batch);
        }
        checkPortals();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            pause();
        }
    }

    protected void updatePlayer(float delta) {
        player.update(delta);
        player.draw(batch, shapeRenderer);
    }

    public void carregarParede() {
        List<Rectangle> paredes = tilemapHitboxFactory.createHitboxes(map, "Parede");
        player.adicionarColisao(paredes);
    }

    protected abstract void renderLayers();

    protected abstract void renderLayersUp();

    protected abstract String getMapPath();

    protected abstract void checkPortals();

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // Adicionado 'true' para centrar o viewport
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
        camera.update();
        if (player != null) {
            player.getHud().resize(width, height);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        backMusic.stop();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        background.dispose();
        backMusic.dispose();
        if (map != null) map.dispose();
        if (layerRenderer != null) layerRenderer.dispose();
        if (player != null) player.dispose();
    }

    public PlayerTemplate getPlayer() {
        return player;
    }
}
