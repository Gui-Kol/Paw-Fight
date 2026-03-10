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
import com.pawfight.game.commun.LayerRenderer;
import com.pawfight.game.commun.animation.ScreenTransition;
import com.pawfight.game.commun.phisics.TilemapHitboxFactory;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.base.EntradaPortais;
import com.pawfight.game.world.base.EscolherPersonagem;
import com.pawfight.game.world.base.Portoes;

import java.util.List;

public abstract class WorldTemplate implements Screen {


    // Física
    protected TilemapHitboxFactory tilemapHitboxFactory;

    // Base
    protected LayerRenderer layerRenderer;
    protected EntradaPortais entradaPortais;
    protected TiledMap map;

    // Controle de fase
    protected ScreenTransition screenTransition;
    protected boolean entrouPortal = false;

    // Entidades
    protected PlayerTemplate player;
    protected EscolherPersonagem escolherPersonagem;

    // Mundo
    protected Portoes portoes;
    protected ShapeRenderer shapeRenderer;
    protected Texture background;
    protected PawFight game;
    protected SpriteBatch batch;
    protected Music backMusic;

    public WorldTemplate(PawFight game, String backgroundPath, String musicPath) {
        this.game = game;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        background = new Texture(backgroundPath);
        backMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath));

        entradaPortais = new EntradaPortais(new ScreenTransition(game));
        escolherPersonagem = new EscolherPersonagem();
        tilemapHitboxFactory = new TilemapHitboxFactory();
        portoes = new Portoes();
    }

    public void setPlayer(PlayerTemplate player) {
        this.player = player;
    }

    @Override
    public void show() {
        map = new TmxMapLoader().load(getMapPath());
        layerRenderer = new LayerRenderer(map);
        backMusic.setLooping(true);
        backMusic.setVolume(0);
        backMusic.play();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Chamando lógica comum
        renderLayers();
        updatePlayer(delta);
        renderLayersUp();
        renderPortals();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            pause();
        }
    }
    protected abstract void preLoad();

    protected void updatePlayer(float delta) {
        List<Rectangle> paredes = tilemapHitboxFactory.createHitboxes(map, "Parede");
        player.adicionarColisao(paredes, shapeRenderer);
        player.draw(batch, shapeRenderer);
        player.update(delta);
        player.clearList();
    }

    protected abstract void renderLayers();

    protected abstract void renderLayersUp();

    protected void renderPortals() {
        if (!entrouPortal) {
            checkPortals();
        } else {
            entradaPortais.entrou(batch);
        }
    }

    // --- MÉTODOS ABSTRATOS ---
    protected abstract String getMapPath(); // cada Base define seu mapa

    protected abstract void checkPortals(); // cada Base define seus portais

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
        map.dispose();
        layerRenderer.dispose();
        if (player != null) player.dispose();
        escolherPersonagem.dispose();
    }
}
