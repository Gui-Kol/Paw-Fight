package com.pawfight.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.Hud.DesenharMiniMapa;
import com.pawfight.game.engine.Validar;
import com.pawfight.game.engine.design.transition.ScreenTransition;
import com.pawfight.game.engine.phisics.TilemapHitboxFactory;
import com.pawfight.game.engine.procedural.CarregarPortas;
import com.pawfight.game.engine.procedural.GerarInimigos;
import com.pawfight.game.engine.procedural.GerarObjetos;
import com.pawfight.game.engine.procedural.ObjetoGerado;
import com.pawfight.game.engine.procedural.room.InfoGeraObjeto;
import com.pawfight.game.engine.procedural.room.Room;
import com.pawfight.game.engine.procedural.room.RoomGenerator;
import com.pawfight.game.engine.render.LayerRenderer;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.DanoTiro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pawfight.game.engine.CommunVariable.GET_ALTURA_TELA_BASE;
import static com.pawfight.game.engine.CommunVariable.GET_LARGURA_TELA_BASE;

public abstract class WorldTemplate implements Screen {

    // Física
    private final Stage stage;
    protected TilemapHitboxFactory tilemapHitboxFactory;

    // Base
    protected LayerRenderer layerRenderer;
    protected TiledMap map;

    // Entidades
    protected PlayerTemplate player;

    // Mundo
    protected final DesenharMiniMapa desenharMiniMapa;
    protected final ScreenTransition screenTransition;
    protected final CarregarPortas carregarPortas;
    protected final GerarObjetos gerarObjetos;
    protected final Renderizar renderizar;
    protected final Validar validar;
    protected boolean errorFinal = false;
    protected final List<ObjetoGerado> listaObjetos;
    protected final List<Rectangle> listaObjetosHitbox;
    protected List<Room> rooms;
    protected RoomGenerator roomGenerator;
    protected Room currentRoom;
    protected boolean podeEntrarPorta;
    protected Set<String> salasVisitadas;
    protected DanoTiro danoTiro;
    protected ShapeRenderer shapeRenderer;
    protected Texture background;
    protected PawFight game;
    protected SpriteBatch batch;
    protected Music backMusic;
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected GerarInimigos gerarInimigos;
    protected List<EnemyTemplate> listaInimigos;


    public WorldTemplate(PawFight game, String backgroundPath, String musicPath, OrthographicCamera camera, Viewport viewport) {
        this.game = game;
        this.camera = camera;
        this.viewport = viewport;

        gerarObjetos = new GerarObjetos();
        validar = new Validar();
        listaObjetos = new ArrayList<>();
        listaObjetosHitbox = new ArrayList<>();
        listaInimigos = new ArrayList<>();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        background = new Texture(backgroundPath);
        backMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
        tilemapHitboxFactory = new TilemapHitboxFactory();
        danoTiro = new DanoTiro();
        salasVisitadas = new HashSet<>();
        podeEntrarPorta = true;
        renderizar = new Renderizar();
        gerarInimigos = new GerarInimigos();
        desenharMiniMapa = new DesenharMiniMapa();
        screenTransition = new ScreenTransition(game);
        carregarPortas = new CarregarPortas();
        stage = new Stage(new FitViewport(GET_LARGURA_TELA_BASE(), GET_ALTURA_TELA_BASE()), batch);
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
            Gdx.app.error(getWorldName(), "Erro no Show: " + e.getMessage(), e);
        }
        carregarMapaCompleto();
    }

    private void carregarMapaCompleto() {
        if (deveCarregarMapaCompleto()) {
            if (errorFinal) {
                Gdx.app.error(getWorldName(), "show() chamado mas errorFinal = true");
                return;
            }
            if (currentRoom == null) {
                Gdx.app.error(getWorldName(), "show() chamado mas currentRoom é null!");
                errorFinal = true;
                return;
            }
            try {
                gerarObjetos();
                carregarParede();
            } catch (Exception e) {
                Gdx.app.error(getWorldName(), "Erro no Show: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void render(float delta) {
        if (map == null || layerRenderer == null) {
            Gdx.app.error(getWorldName(), "Mapa não carregado!");
            return;
        }

        renderLayers();
        if (player != null) {
            updatePlayer(delta);
            if (player.isPause()) {
                pause();
            }
            if (listaInimigos != null && !listaInimigos.isEmpty()) {
                renderizar.atualizarListaInimigos(Gdx.graphics.getDeltaTime(), listaInimigos);
                renderizar.renderizarInimigos(this);
            }
        }

        renderLayersUp();

        if (player != null) {
            player.drawHud(batch, shapeRenderer, this);
        }
        checkPortals();
    }

    protected void updatePlayer(float delta) {
        player.update(delta);
        player.draw(this);
    }

    public void carregarParede() {
        List<Rectangle> paredes = tilemapHitboxFactory.createHitboxes(map, "Parede");
        player.adicionarColisao(paredes);
    }

    protected abstract void renderLayers();

    protected abstract void renderLayersUp();

    public abstract String getMapPath();

    protected abstract void checkPortals();

    public abstract void logRoomInfo(Room room);

    public abstract List<InfoGeraObjeto> getInfoObjetos();

    public void gerarObjetos() {
        if (getInfoObjetos() == null || getInfoObjetos().isEmpty()) {
            return;
        }
        gerarObjetos.gerar(this, getInfoObjetos());
    }

    public abstract String getWorldName();

    public abstract List<EnemyTemplate> getInimigosModelo();

    public abstract List<EnemyTemplate> getBossesModelo();

    protected abstract boolean deveCarregarMapaCompleto();

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
        Gdx.app.log(getWorldName(), "foi disposed");
    }

    public boolean currentRoomFoiVisitada() {
        Room currentRoom = getCurrentRoom();

        String key = currentRoom.getX() + "," + currentRoom.getY();
        return getSalasVisitadas().contains(key);
    }

    public PlayerTemplate getPlayer() {
        return player;
    }

    public Set<String> getSalasVisitadas() {
        return salasVisitadas;
    }

    public TilemapHitboxFactory getTilemapHitboxFactory() {
        return tilemapHitboxFactory;
    }

    public LayerRenderer getLayerRenderer() {
        return layerRenderer;
    }

    public TiledMap getMap() {
        return map;
    }

    public boolean isPodeEntrarPorta() {
        return podeEntrarPorta;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public RoomGenerator getRoomGenerator() {
        return roomGenerator;
    }

    public GerarInimigos getGerarInimigos() {
        return gerarInimigos;
    }

    public List<EnemyTemplate> getListaInimigos() {
        return listaInimigos;
    }

    public void setLayerRenderer(LayerRenderer layerRenderer) {
        this.layerRenderer = layerRenderer;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public void addSalasVisitadas(String newRoom) {
        salasVisitadas.add(newRoom);
    }

    public List<ObjetoGerado> getListaObjetos() {
        return listaObjetos;
    }

    public List<Rectangle> getListaObjetosHitbox() {
        return listaObjetosHitbox;
    }

    public void addListaObjetos(List<ObjetoGerado> objetoGerados) {
        listaObjetos.addAll(objetoGerados);
    }

    public void addListaObjetosHitbox(List<Rectangle> hitBoxs) {
        listaObjetosHitbox.addAll(hitBoxs);
    }

    public GerarObjetos getGerarObjetos() {
        return gerarObjetos;
    }

    public boolean isErrorFinal() {
        return errorFinal;
    }

    public DanoTiro getDanoTiro() {
        return danoTiro;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public Texture getBackground() {
        return background;
    }

    public PawFight getGame() {
        return game;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Music getBackMusic() {
        return backMusic;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Stage getStage() {
        return stage;
    }
}
