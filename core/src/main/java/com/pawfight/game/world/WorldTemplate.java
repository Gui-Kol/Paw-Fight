package com.pawfight.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.LayerRenderer;
import com.pawfight.game.engine.animation.DrawList;
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
    protected DrawList drawList;
    protected ShapeRenderer shapeRenderer;
    protected Texture background;
    protected PawFight game;
    protected SpriteBatch batch;
    protected Music backMusic;

    public WorldTemplate(PawFight game, String backgroundPath, String musicPath) {
        this.game = game;
        drawList = new DrawList();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        background = new Texture(backgroundPath);
        backMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
        tilemapHitboxFactory = new TilemapHitboxFactory();
        drawHitBox = new DrawHitBox();
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

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderLayers();
        if (player != null) {
            updatePlayer(delta);
        }
        renderLayersUp();
        checkPortals();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            pause();
        }
    }

    protected void updatePlayer(float delta) {
        player.draw(batch, shapeRenderer);
        player.update(delta);
    }

    public void carregarParede() {
        List<Rectangle> paredes = tilemapHitboxFactory.createHitboxes(map, "Parede");
        player.adicionarColisao(paredes, shapeRenderer);
    }

    protected abstract void renderLayers();

    protected abstract void renderLayersUp();

    protected abstract String getMapPath();

    protected abstract void checkPortals();

    @Override
    public void resize(int width, int height) {
        if (player != null) {
            player.getCamera().viewportWidth = width;
            player.getCamera().viewportHeight = height;
            player.getCamera().update();
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
}
