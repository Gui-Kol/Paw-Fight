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
import com.pawfight.game.engine.design.transition.TransicaoTela;
import com.pawfight.game.engine.fisica.TilemapHitboxFactory;
import com.pawfight.game.engine.procedural.CarregarPortas;
import com.pawfight.game.engine.procedural.GerarInimigos;
import com.pawfight.game.engine.procedural.GerarObjetos;
import com.pawfight.game.engine.procedural.ObjetoGerado;
import com.pawfight.game.engine.procedural.sala.GeradorSalas;
import com.pawfight.game.engine.procedural.sala.InfoGeraObjeto;
import com.pawfight.game.engine.procedural.sala.Sala;
import com.pawfight.game.engine.render.RenderizadorCamada;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.DanoTiro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pawfight.game.engine.VariavelComum.*;

public abstract class WorldTemplate implements Screen {

    // Física
    private final Stage stage;
    protected TilemapHitboxFactory tilemapHitboxFactory;

    // Base
    protected RenderizadorCamada RenderizadorCamada;
    protected TiledMap map;

    // Entidades
    protected PlayerTemplate player;

    // Mundo
    protected final DesenharMiniMapa desenharMiniMapa;
    protected final TransicaoTela TransicaoTela;
    protected final CarregarPortas carregarPortas;
    protected final GerarObjetos gerarObjetos;
    protected final Renderizar renderizar = Renderizar.INSTANCE;
    protected boolean errorFinal = false;
    protected final List<ObjetoGerado> listaObjetos;
    protected final List<Rectangle> listaObjetosHitbox;
    protected List<Sala> rooms;
    protected GeradorSalas GeradorSalas;
    protected Sala currentRoom;
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
        this.batch = game.getBatch();

        gerarObjetos = new GerarObjetos();
        listaObjetos = new ArrayList<>();
        listaObjetosHitbox = new ArrayList<>();
        listaInimigos = new ArrayList<>();
        shapeRenderer = new ShapeRenderer();
        background = new Texture(backgroundPath);
        backMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
        tilemapHitboxFactory = new TilemapHitboxFactory();
        danoTiro = new DanoTiro();
        salasVisitadas = new HashSet<>();
        podeEntrarPorta = true;
        gerarInimigos = new GerarInimigos();
        desenharMiniMapa = new DesenharMiniMapa();
        TransicaoTela = new TransicaoTela(game);
        carregarPortas = new CarregarPortas();
        stage = new Stage(new FitViewport(GET_LARGURA_TELA_BASE, GET_ALTURA_TELA_BASE), batch);
    }


    public void setPlayer(PlayerTemplate player) {
        this.player = player;
    }

    @Override
    public void show() {
        try {
            tilemapHitboxFactory.clearCache();
            map = new TmxMapLoader().load(getMapPath());
            RenderizadorCamada = new RenderizadorCamada(map);
            backMusic.setLooping(true);
            backMusic.setVolume(VOLUME_MUSICA);
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
        if (map == null || RenderizadorCamada == null) {
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

        renderizar.renderizarObjects(this);
        renderizar.hitBoxListObjeto(listaObjetos, shapeRenderer, player.getCamera().combined);

        if (player != null) {
            player.drawHud(batch, shapeRenderer, this);
        }
        checkPortals();
        danoTiro.darDanoListaInimigos(this);
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

    public abstract void logRoomInfo(Sala Sala);

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
        shapeRenderer.dispose();
        background.dispose();
        backMusic.dispose();
        if (map != null) map.dispose();
        if (RenderizadorCamada != null) RenderizadorCamada.dispose();
        if (player != null) player.dispose();
        stage.dispose();
        Gdx.app.log(getWorldName(), "foi disposed");
    }

    public boolean currentRoomFoiVisitada() {
        Sala currentRoom = getCurrentRoom();

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

    public RenderizadorCamada getLayerRenderer() {
        return RenderizadorCamada;
    }

    public TiledMap getMap() {
        return map;
    }

    public boolean isPodeEntrarPorta() {
        return podeEntrarPorta;
    }

    public Sala getCurrentRoom() {
        return currentRoom;
    }

    public GeradorSalas getRoomGenerator() {
        return GeradorSalas;
    }

    public GerarInimigos getGerarInimigos() {
        return gerarInimigos;
    }

    public List<EnemyTemplate> getListaInimigos() {
        return listaInimigos;
    }

    public void setLayerRenderer(RenderizadorCamada RenderizadorCamada) {
        this.RenderizadorCamada = RenderizadorCamada;
    }

    public void setCurrentRoom(Sala currentRoom) {
        this.currentRoom = currentRoom;
    }

    public List<Sala> getRooms() {
        return rooms;
    }

    public void setRooms(List<Sala> rooms) {
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
