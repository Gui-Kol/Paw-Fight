package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.pawfight.game.PawFight;
import com.pawfight.game.commun.Hud;
import com.pawfight.game.commun.animation.ScreenTransition;
import com.pawfight.game.entity.player.Player;

public class Base implements Screen {
    //Base
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //MudarFase
    private ScreenTransition screenTransition;

    //Entidades
    private Player player;

    //Mundo
    private Hud hud;
    private ShapeRenderer shapeRenderer;
    private Texture background;
    private PawFight game;
    private SpriteBatch batch;
    private Music backMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/time_for_adventure.wav"));


    public Base(PawFight game) {
        this.game = game;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        player = new Player(33, 2335, 3200, 1280, 2400, 720, 0.5f);

        background = new Texture("menu/menu.png");

        hud = new Hud();
    }

    @Override
    public void show() {
        map = new TmxMapLoader().load("world/base/base.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);



    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Atualiza player e câmera
        player.update(delta, 3180, 2400);
        player.updateCamera();

        // Renderiza mapa com câmera
        renderer.setView(player.getCamera());
        renderer.render();

        // Renderiza player com câmera
        batch.setProjectionMatrix(player.getCamera().combined);
        batch.begin();
        player.draw(batch);
        batch.end();

        // Renderiza hitbox separadamente
        shapeRenderer.setProjectionMatrix(player.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        player.drawHitbox(shapeRenderer, batch);
        hud.draw(batch, player.getDx(), player.getDy());
        shapeRenderer.end();
    }


    @Override
    public void resize(int width, int height) {
        player.getCamera().viewportWidth = width;
        player.getCamera().viewportHeight = height;
        player.getCamera().update();
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
        batch.dispose();
        shapeRenderer.dispose();
        background.dispose();
        backMusic.dispose();
        map.dispose();
        renderer.dispose();
        player.dispose();
    }
}
