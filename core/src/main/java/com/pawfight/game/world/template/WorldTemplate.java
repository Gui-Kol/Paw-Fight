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
import com.pawfight.game.engine.ecs.GerenciadorEcs;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.engine.GameConfig;
import com.pawfight.game.world.Home;

import static com.pawfight.game.engine.GameConfig.LARGURA_TELA_BASE;
import static com.pawfight.game.engine.GameConfig.ALTURA_TELA_BASE;
import com.pawfight.game.engine.procedural.sala.InfoGeraObjeto;
import com.pawfight.game.engine.procedural.sala.Sala;
import com.pawfight.game.engine.render.RenderizadorCamada;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.content.enemy.DefinicaoInimigo;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;


public abstract class WorldTemplate implements Screen {

    protected final WorldRenderer worldRenderer;
    protected final WorldPhysics worldPhysics;
    protected final RoomManager roomManager;
    protected final EnemyManager enemyManager;
    protected final GerenciadorEcs gerenciadorEcs;

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

    private static final float DURACAO_ZOOM_MORTE = 0.8f;
    private static final float ZOOM_MORTE_ALVO = 0.2f; // zoom < 1 = aproxima do player
    private static final float DURACAO_FADE_MORTE = 1.8f; // configurável (1.5s – 2s)
    private boolean morteIniciada = false;
    private boolean morteTransicaoDisparada = false;
    private boolean zoomMorteConcluido = false;
    private boolean animacaoMorteConcluida = false;


    public WorldTemplate(PawFight game, String backgroundPath, String musicPath, OrthographicCamera camera, Viewport viewport) {
        this.game = game;
        this.camera = camera;
        this.viewport = viewport;
        this.batch = game.getBatch();

        worldRenderer = new WorldRenderer(backgroundPath);
        worldPhysics = new WorldPhysics();
        roomManager = new RoomManager();
        gerenciadorEcs = new GerenciadorEcs();
        enemyManager = new EnemyManager(gerenciadorEcs);

        backMusic = Assets.get(musicPath, Music.class);
        stage = new Stage(new FitViewport(LARGURA_TELA_BASE, ALTURA_TELA_BASE), batch);
    }


    public void setPlayer(PlayerTemplate player) {
        if (this.player != null) {
            gerenciadorEcs.removerEntidade(this.player.getEntidadeEcs());
        }
        this.player = player;
        if (player != null) {
            game.getGameSession().definirPlayer(player);
            game.getGameSession().setMundoAtual(getWorldName());
            // Compartilha a lista viva de inimigos da sala — tiros só nascem com inimigos presentes e miram neles
            player.setFonteInimigos(enemyManager.getListaInimigos());
            // A entidade do player é registrada somente enquanto esta tela é a ativa. Um player
            // herdado de outro mundo durante a transição entra no Engine deste mundo no show().
            if (game.getScreen() == this) {
                gerenciadorEcs.adicionarEntidade(player.getEntidadeEcs());
            }
        }
    }

    @Override
    public void show() {
        try {
            // O player pertence ao Engine apenas da tela ativa (o hide da anterior já o removeu).
            if (player != null) {
                gerenciadorEcs.adicionarEntidade(player.getEntidadeEcs());
            }
            worldPhysics.clearCache();
            map = new TmxMapLoader().load(getMapPath());
            renderizadorCamada = new RenderizadorCamada(map);
            backMusic.setLooping(true);
            backMusic.setVolume(GameConfig.getInstance().getVolumeMusica());
            backMusic.play();
            Gdx.app.log(getWorldName(), "show() — mapa carregado: " + getMapPath());
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

        if (player != null) {
            atualizarGameplay(delta);
        }

        renderizarCena();
    }

    protected void atualizarGameplay(float delta) {
        player.update(delta);
        if (player.isPause()) {
            pause();
            return;
        }

        if (!player.isMorto()) {
            gerenciadorEcs.atualizar(delta);
            enemyManager.atualizarInimigos(delta);
            worldPhysics.resolverColisoes(enemyManager.getListaInimigos());
            worldPhysics.processarDanoTiro(this, delta);
            atualizarGameplayEspecifico(delta);
            checkPortals();
        }

        player.atualizarCamera();
        gerenciarMorte(delta);
    }

    protected void atualizarGameplayEspecifico(float delta) {
        // Mundos concretos podem acrescentar regras sem misturá-las à renderização.
    }

    private void renderizarCena() {
        renderLayers();

        if (player != null) {
            player.draw(this);
            worldRenderer.renderizarInimigos(this);
            player.desenharTiros(this);
        }

        renderLayersUp();

        worldRenderer.renderizarObjects(this);

        if (player != null) {
            player.drawHud(batch, worldRenderer.getShapeRenderer(), this);
        }
        // Debug: desenha a quadtree quando F3 está ativo
        if (player != null) {
            worldPhysics.drawDebugQuadtree(
                worldRenderer.getShapeRenderer(),
                player.getCamera().combined
            );
        }

    }

    // Fluxo de morte: 1) zoom da câmera no player, 2) animação de morte completa, 3) fade to black, 4) retorno ao Home
    private void gerenciarMorte(float delta) {
        if (player == null || !player.isMorto()) {
            morteIniciada = false;
            morteTransicaoDisparada = false;
            zoomMorteConcluido = false;
            animacaoMorteConcluida = false;
            return;
        }

        if (!morteIniciada) {
            morteIniciada = true;
            morteTransicaoDisparada = false;
            zoomMorteConcluido = false;
            animacaoMorteConcluida = false;
            // Remove todos os inimigos — nenhum continua agindo ou aparecendo durante a morte
            enemyManager.clearInimigos();
            player.getCameraComponent().iniciarZoomMorte(ZOOM_MORTE_ALVO, DURACAO_ZOOM_MORTE);
            Gdx.app.log(getWorldName(), "Player morreu — iniciando sequência de morte (zoom para " + ZOOM_MORTE_ALVO
                + " em " + DURACAO_ZOOM_MORTE + "s).");
        }

        boolean zoomPronto = player.getCameraComponent().atualizarZoomMorte(delta);
        boolean animacaoPronta = player.getAnimacao().isDeadAnimationFinished();

        if (zoomPronto && !zoomMorteConcluido) {
            zoomMorteConcluido = true;
            Gdx.app.log(getWorldName(), "Zoom de morte concluído.");
        }
        if (animacaoPronta && !animacaoMorteConcluida) {
            animacaoMorteConcluida = true;
            Gdx.app.log(getWorldName(), "Animação de morte concluída.");
        }

        if (zoomPronto && animacaoPronta && !morteTransicaoDisparada) {
            morteTransicaoDisparada = true;
            // Esconde o player (último frame não deve ficar "congelado" na tela)
            player.setMorteFinalizada(true);
            Gdx.app.log(getWorldName(), "Fade de morte disparado (" + DURACAO_FADE_MORTE + "s) — retornando ao menu inicial.");
            voltarAoMenu(DURACAO_FADE_MORTE);
        }
    }

    // Navegação centralizada: retorna ao menu inicial via ScreenManager; dispose fica a cargo do ScreenManager e do AssetManager
    public void voltarAoMenu(float duracaoTransicao) {
        if (ScreenManager.getInstance().isTransitioning()) {
            return;
        }
        backMusic.stop();
        if (player != null) {
            player.setPause(false);
        }
        Gdx.app.log(getWorldName(), "Voltando ao menu inicial.");
        ScreenManager.getInstance().fadeToScreenEncerrandoSessao(
            new Home(game, camera, viewport), duracaoTransicao, Color.BLACK, false);
    }

    public void carregarParede() {
        worldPhysics.carregarParede(map, player);
    }

    protected abstract void renderLayers();

    protected abstract void renderLayersUp();

    public abstract String getMapPath();

    public String getMapPath(Sala sala) {
        return getMapPath();
    }

    protected abstract void checkPortals();

    public abstract void logRoomInfo(Sala sala);

    public abstract List<InfoGeraObjeto> getInfoObjetos();

    public void gerarObjetos() {
        worldRenderer.gerarObjetos(this, getInfoObjetos());
    }

    public abstract String getWorldName();

    public abstract List<DefinicaoInimigo> getDefinicoesInimigos();

    public abstract List<DefinicaoInimigo> getDefinicoesBosses();

    protected abstract boolean deveCarregarMapaCompleto();

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
        camera.update();
        if (player != null) {
            player.getHud().resize(width, height);
        }
        Gdx.app.debug(getWorldName(), "resize() — " + width + "x" + height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        Gdx.app.log(getWorldName(), "resume");
    }

    @Override
    public void hide() {
        // Libera a entidade do player deste Engine antes de ela entrar no da próxima tela —
        // o Ashley não suporta a mesma Entity registrada em dois Engines ao mesmo tempo.
        if (player != null) {
            gerenciadorEcs.removerEntidade(player.getEntidadeEcs());
        }
        backMusic.stop();
        Gdx.app.log(getWorldName(), "hide — música parada.");
    }

    @Override
    public void dispose() {
        enemyManager.clearInimigos();
        if (player != null) {
            gerenciadorEcs.removerEntidade(player.getEntidadeEcs());
        }
        gerenciadorEcs.limpar();
        worldRenderer.dispose();
        roomManager.dispose();
        // background e backMusic são gerenciados pelo AssetManager — NÃO dar dispose aqui
        if (map != null) map.dispose();
        if (renderizadorCamada != null) renderizadorCamada.dispose();
        stage.dispose();
        Gdx.app.log(getWorldName(), "foi disposed");
    }

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

    public GerenciadorEcs getGerenciadorEcs() {
        return gerenciadorEcs;
    }

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
