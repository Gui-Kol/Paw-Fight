package com.pawfight.game.world.template;

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
import com.pawfight.game.engine.Assets;
import com.pawfight.game.engine.GameConfig;
import com.pawfight.game.engine.fisica.TilemapHitboxFactory;

import static com.pawfight.game.engine.GameConfig.LARGURA_TELA_BASE;
import static com.pawfight.game.engine.GameConfig.ALTURA_TELA_BASE;
import com.pawfight.game.engine.procedural.GerarInimigos;
import com.pawfight.game.engine.procedural.GerarObjetos;
import com.pawfight.game.engine.procedural.ObjetoGerado;
import com.pawfight.game.engine.procedural.sala.GeradorSalas;
import com.pawfight.game.engine.procedural.sala.InfoGeraObjeto;
import com.pawfight.game.engine.procedural.sala.Sala;
import com.pawfight.game.engine.render.RenderizadorCamada;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.DanoTiro;

import java.util.List;
import java.util.Set;


public abstract class WorldTemplate implements Screen {

    // ── Managers (composição) ──────────────────────────────────
    protected final WorldRenderer worldRenderer;
    protected final WorldPhysics worldPhysics;
    protected final RoomManager roomManager;
    protected final EnemyManager enemyManager;

    // ── Core (permanece no WorldTemplate) ──────────────────────
    private final Stage stage;
    protected RenderizadorCamada renderizadorCamada;
    protected TiledMap map;
    protected PlayerTemplate player;
    protected boolean errorFinal = false;
    protected PawFight game;
    protected SpriteBatch batch;
    protected Music backMusic;
    protected OrthographicCamera camera;
    protected Viewport viewport;


    public WorldTemplate(PawFight game, String backgroundPath, String musicPath, OrthographicCamera camera, Viewport viewport) {
        this.game = game;
        this.camera = camera;
        this.viewport = viewport;
        this.batch = game.getBatch();

        // Inicializa managers
        worldRenderer = new WorldRenderer(backgroundPath);
        worldPhysics = new WorldPhysics();
        roomManager = new RoomManager();
        enemyManager = new EnemyManager();

        backMusic = Assets.get(musicPath, Music.class);
        stage = new Stage(new FitViewport(LARGURA_TELA_BASE, ALTURA_TELA_BASE), batch);
    }


    public void setPlayer(PlayerTemplate player) {
        this.player = player;
    }

    @Override
    public void show() {
        try {
            worldPhysics.clearCache();
            map = new TmxMapLoader().load(getMapPath());
            renderizadorCamada = new RenderizadorCamada(map);
            backMusic.setLooping(true);
            backMusic.setVolume(GameConfig.getInstance().getVolumeMusica());
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
            if (roomManager.getCurrentRoom() == null) {
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
        if (map == null || renderizadorCamada == null) {
            Gdx.app.error(getWorldName(), "Mapa não carregado!");
            return;
        }

        renderLayers();
        if (player != null) {
            updatePlayer(delta);
            if (player.isPause()) {
                pause();
            }
            worldRenderer.renderizarInimigos(this);
        }

        renderLayersUp();

        worldRenderer.renderizarObjects(this);

        if (player != null) {
            player.drawHud(batch, worldRenderer.getShapeRenderer(), this);
        }
        checkPortals();
        worldPhysics.processarDanoTiro(this);

        // Debug: desenha a quadtree quando F3 está ativo
        if (player != null) {
            worldPhysics.drawDebugQuadtree(
                worldRenderer.getShapeRenderer(),
                player.getCamera().combined
            );
        }
    }

    protected void updatePlayer(float delta) {
        player.update(delta);
        player.draw(this);
    }

    public void carregarParede() {
        worldPhysics.carregarParede(map, player);
    }

    protected abstract void renderLayers();

    protected abstract void renderLayersUp();

    public abstract String getMapPath();

    protected abstract void checkPortals();

    public abstract void logRoomInfo(Sala sala);

    public abstract List<InfoGeraObjeto> getInfoObjetos();

    public void gerarObjetos() {
        worldRenderer.gerarObjetos(this, getInfoObjetos());
    }

    public abstract String getWorldName();

    public abstract List<EnemyTemplate> getInimigosModelo();

    public abstract List<EnemyTemplate> getBossesModelo();

    protected abstract boolean deveCarregarMapaCompleto();

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        worldRenderer.dispose();
        roomManager.dispose();
        // background e backMusic são gerenciados pelo AssetManager — NÃO dar dispose aqui
        if (map != null) map.dispose();
        if (renderizadorCamada != null) renderizadorCamada.dispose();
        if (player != null) player.dispose();
        stage.dispose();
        Gdx.app.log(getWorldName(), "foi disposed");
    }

    public boolean currentRoomFoiVisitada() {
        return roomManager.currentRoomFoiVisitada();
    }

    // ── Acesso aos managers ────────────────────────────────────

    public WorldRenderer getWorldRenderer() {
        return worldRenderer;
    }

    public WorldPhysics getWorldPhysics() {
        return worldPhysics;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    // ── Delegate getters (compatibilidade com código externo) ──

    public PlayerTemplate getPlayer() {
        return player;
    }

    public Set<String> getSalasVisitadas() {
        return roomManager.getSalasVisitadas();
    }

    public TilemapHitboxFactory getTilemapHitboxFactory() {
        return worldPhysics.getTilemapHitboxFactory();
    }

    public RenderizadorCamada getLayerRenderer() {
        return renderizadorCamada;
    }

    public TiledMap getMap() {
        return map;
    }

    public boolean isPodeEntrarPorta() {
        return roomManager.isPodeEntrarPorta();
    }

    public Sala getCurrentRoom() {
        return roomManager.getCurrentRoom();
    }

    public GeradorSalas getRoomGenerator() {
        return roomManager.getRoomGenerator();
    }

    public GerarInimigos getGerarInimigos() {
        return enemyManager.getGerarInimigos();
    }

    public List<EnemyTemplate> getListaInimigos() {
        return enemyManager.getListaInimigos();
    }

    public void setLayerRenderer(RenderizadorCamada renderizadorCamada) {
        this.renderizadorCamada = renderizadorCamada;
    }

    public void setCurrentRoom(Sala currentRoom) {
        roomManager.setCurrentRoom(currentRoom);
    }

    public List<Sala> getRooms() {
        return roomManager.getRooms();
    }

    public void setRooms(List<Sala> rooms) {
        roomManager.setRooms(rooms);
    }

    public void addSalasVisitadas(String newRoom) {
        roomManager.addSalasVisitadas(newRoom);
    }

    public List<ObjetoGerado> getListaObjetos() {
        return worldRenderer.getListaObjetos();
    }

    public List<Rectangle> getListaObjetosHitbox() {
        return worldRenderer.getListaObjetosHitbox();
    }

    public void addListaObjetos(List<ObjetoGerado> objetoGerados) {
        worldRenderer.addListaObjetos(objetoGerados);
    }

    public void addListaObjetosHitbox(List<Rectangle> hitBoxs) {
        worldRenderer.addListaObjetosHitbox(hitBoxs);
    }

    public GerarObjetos getGerarObjetos() {
        return worldRenderer.getGerarObjetos();
    }

    public boolean isErrorFinal() {
        return errorFinal;
    }

    public DanoTiro getDanoTiro() {
        return worldPhysics.getDanoTiro();
    }

    public ShapeRenderer getShapeRenderer() {
        return worldRenderer.getShapeRenderer();
    }

    public Texture getBackground() {
        return worldRenderer.getBackground();
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
