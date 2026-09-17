package com.pawfight.game.world.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.ScreenManager;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.engine.GameConfig;
import com.pawfight.game.world.Home;

import static com.pawfight.game.engine.GameConfig.LARGURA_TELA_BASE;
import static com.pawfight.game.engine.GameConfig.ALTURA_TELA_BASE;
import com.pawfight.game.engine.procedural.sala.InfoGeraObjeto;
import com.pawfight.game.engine.procedural.sala.Sala;
import com.pawfight.game.engine.render.RenderizadorCamada;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;


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

    // ── Fluxo de morte (sequencial e automático) ───────────────
    private static final float DURACAO_ZOOM_MORTE = 0.8f;
    private static final float ZOOM_MORTE_ALVO = 0.2f; // zoom < 1 = aproxima do player
    private static final float DURACAO_FADE_MORTE = 1.8f; // configurável (1.5s – 2s)
    private boolean morteIniciada = false;
    private boolean morteTransicaoDisparada = false;


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
        if (player != null) {
            // Compartilha a lista viva de inimigos da sala — tiros só nascem
            // quando houver inimigos e miram neles
            player.setFonteInimigos(enemyManager.getListaInimigos());
        }
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

        // Fluxo de morte (sequencial e automático)
        gerenciarMorte(delta);
    }

    protected void updatePlayer(float delta) {
        player.update(delta);
        player.draw(this);
    }

    // ══════════════════════════════════════════════════════════
    //  FLUXO DE MORTE (sequencial e automático)
    //  1. Zoom da câmera focado no player
    //  2. Animação de morte toca por completo
    //  3. Fade to black configurável
    //  4. Retorno automático ao menu inicial (Home)
    // ══════════════════════════════════════════════════════════
    private void gerenciarMorte(float delta) {
        if (player == null || !player.isMorto()) {
            morteIniciada = false;
            morteTransicaoDisparada = false;
            return;
        }

        // 1. Inicia a sequência: zoom suave focado no player
        if (!morteIniciada) {
            morteIniciada = true;
            morteTransicaoDisparada = false;
            // Remove todos os inimigos — nenhum continua agindo ou aparecendo durante a morte
            enemyManager.clearInimigos();
            player.getCameraComponent().iniciarZoomMorte(ZOOM_MORTE_ALVO, DURACAO_ZOOM_MORTE);
            Gdx.app.log(getWorldName(), "Player morreu — iniciando sequência de morte.");
        }

        // 2. Executa o zoom e espera a animação de morte terminar
        boolean zoomPronto = player.getCameraComponent().atualizarZoomMorte(delta);
        boolean animacaoPronta = player.getAnimacao().isDeadAnimationFinished();

        // 3. Após zoom + animação, dispara o fade to black e retorna ao Home
        if (zoomPronto && animacaoPronta && !morteTransicaoDisparada) {
            morteTransicaoDisparada = true;
            // Esconde o player (último frame não deve ficar "congelado" na tela)
            player.setMorteFinalizada(true);
            Gdx.app.log(getWorldName(), "Fim da animação de morte — fade para o menu inicial.");
            voltarAoMenu(DURACAO_FADE_MORTE);
        }
    }

    // ══════════════════════════════════════════════════════════
    //  NAVEGAÇÃO CENTRALIZADA
    //  Retorno ao menu inicial (Home) via ScreenManager.
    //  Não chama dispose() nas telas/recursos — o ScreenManager
    //  e o AssetManager cuidam disso.
    // ══════════════════════════════════════════════════════════
    public void voltarAoMenu(float duracaoTransicao) {
        if (ScreenManager.getInstance().isTransitioning()) {
            return;
        }
        backMusic.stop();
        if (player != null) {
            player.setPause(false);
        }
        Gdx.app.log(getWorldName(), "Voltando ao menu inicial.");
        ScreenManager.getInstance().fadeToScreen(
            new Home(game, camera, viewport), duracaoTransicao, Color.BLACK, false);
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

    // ── Getters de campos próprios ─────────────────────────────

    public PlayerTemplate getPlayer() {
        return player;
    }

    public RenderizadorCamada getLayerRenderer() {
        return renderizadorCamada;
    }

    public void setLayerRenderer(RenderizadorCamada renderizadorCamada) {
        this.renderizadorCamada = renderizadorCamada;
    }

    public TiledMap getMap() {
        return map;
    }

    public void setMap(TiledMap map) {
        this.map = map;
    }

    public boolean isErrorFinal() {
        return errorFinal;
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
