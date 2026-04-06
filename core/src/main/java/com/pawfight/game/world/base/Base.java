package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.procedural.sala.InfoGeraObjeto;
import com.pawfight.game.engine.procedural.sala.Sala;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.world.template.WorldTemplate;

import java.util.List;

public class Base extends WorldTemplate {
    private EntradaPortais entradaPortais;
    private final EscolherPersonagem escolherPersonagem;
    private boolean entrouPortal;
    private Portoes portoes;
    private int carregarPosPlayer = 0;

    // Hitboxes cacheadas — criadas uma única vez em vez de cada frame
    private List<Rectangle> entradaPortalAreia;
    private List<Rectangle> entradaPortalNeve;
    private List<Rectangle> portaoNeve;
    private List<Rectangle> portaoNeveMensagem;

    public Base(PawFight game, OrthographicCamera camera, Viewport viewport) {
        super(game, "menu/menu.png", "audio/music/menu.wav", camera, viewport);
        Gdx.app.log("Base", "Iniciando Base...");
        escolherPersonagem = new EscolherPersonagem(game);
        entrouPortal = false;
    }

    @Override
    public String getMapPath() {
        return "world/base/base.tmx";
    }

    @Override
    public void render(float delta) {
        if (player == null) {
            escolherPersonagem.draw();
            player = escolherPersonagem.update();
            if (player != null) {
                Gdx.app.log("Base", "Personagem escolhido: " + player.getName());
            }
            return;
        }

        if (carregarPosPlayer == 0) {
            portoes = new Portoes(player.getHud());
            game.resize(viewport.getScreenWidth(), viewport.getScreenHeight());
            entradaPortais = new EntradaPortais(player.getHud());
            carregarParede();

            // Cachear hitboxes dos portais uma única vez
            var tilemapHitboxFactory = worldPhysics.getTilemapHitboxFactory();
            entradaPortalAreia = tilemapHitboxFactory.createHitboxes(map, "EntradaPortalAreia");
            entradaPortalNeve = tilemapHitboxFactory.createHitboxes(map, "EntradaPortalNeve");
            portaoNeve = tilemapHitboxFactory.createHitboxes(map, "PortaoNeve");
            portaoNeveMensagem = tilemapHitboxFactory.createHitboxes(map, "portaoNeveMensagem");

            carregarPosPlayer++;
        }

        if (!entrouPortal) {
            super.render(delta);
        }
        // Quando entrouPortal == true, a transição é renderizada pelo ScreenManager
    }

    @Override
    protected void renderLayers() {
        renderizadorCamada.renderLayers(new String[]{"Sub", "Solo", "Up", "DetalhesMapa"}, player.getCamera());
    }

    @Override
    protected void renderLayersUp() {
        renderizadorCamada.renderLayer("DetalhesMapaCima", player.getCamera());
    }


    @Override
    protected void checkPortals() {
        try {
            var shapeRenderer = worldRenderer.getShapeRenderer();
            if (entradaPortais.entrarPortalAreia(player, entradaPortalAreia, batch, shapeRenderer, game, camera, viewport)) {
                entrouPortal = true;
            }
            if (entradaPortais.entrarPortalNeve(player, entradaPortalNeve, batch, game)) {
                entrouPortal = true;
            }

            if (player.getLevel() < 5) {
                worldPhysics.getTilemapHitboxFactory().drawObjects(map, "PortaoNeve", batch, player.getCamera(), true);
                portoes.mensagemPortao(player, portaoNeveMensagem, batch, shapeRenderer, "Level 5 necessario!");
            }
            player.adicionarColisaoPorLevel(portaoNeve, 5);
        } catch (Exception e) {
            Gdx.app.error("Base", "Erro ao verificar portais: " + e.getMessage());
        }
    }

    @Override
    public void logRoomInfo(Sala sala) {

    }

    @Override
    public List<InfoGeraObjeto> getInfoObjetos() {
        return List.of();
    }

    @Override
    public String getWorldName() {
        return "Base";
    }

    @Override
    public List<EnemyTemplate> getInimigosModelo() {
        return List.of();
    }

    @Override
    public List<EnemyTemplate> getBossesModelo() {
        return List.of();
    }

    @Override
    public boolean deveCarregarMapaCompleto() {
        return false;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (player == null) {
            escolherPersonagem.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        escolherPersonagem.dispose();
        // Transição é gerenciada pelo ScreenManager — NÃO dar dispose aqui
    }
}
