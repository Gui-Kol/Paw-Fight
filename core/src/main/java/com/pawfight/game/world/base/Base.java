package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.design.transition.ScreenTransition;
import com.pawfight.game.engine.procedural.room.InfoGeraObjeto;
import com.pawfight.game.engine.procedural.room.Room;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.world.WorldTemplate;

import java.util.List;

public class Base extends WorldTemplate {
    private EntradaPortais entradaPortais;
    private final EscolherPersonagem escolherPersonagem;
    private final ScreenTransition screenTransition;
    private boolean entrouPortal;
    private Portoes portoes;
    private int carregarPosPlayer = 0;

    public Base(PawFight game, OrthographicCamera camera, Viewport viewport) {
        super(game, "menu/menu.png", "audio/music/time_for_adventure.wav", camera, viewport);
        Gdx.app.log("Base", "Iniciando Base...");
        screenTransition = new ScreenTransition(game);
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
            entradaPortais = new EntradaPortais(screenTransition, player.getHud());
            carregarParede();
            carregarPosPlayer++;
        }

        if (!entrouPortal) {
            super.render(delta);
        } else {
            entradaPortais.entrou(batch);
        }
    }

    @Override
    protected void renderLayers() {
        layerRenderer.renderLayers(new String[]{"Sub", "Solo", "Up", "DetalhesMapa"}, player.getCamera());
    }

    @Override
    protected void renderLayersUp() {
        layerRenderer.renderLayer("DetalhesMapaCima", player.getCamera());
    }


    @Override
    protected void checkPortals() {
        try {
            List<Rectangle> entradaPortalAreia = tilemapHitboxFactory.createHitboxes(map, "EntradaPortalAreia");
            List<Rectangle> entradaPortalNeve = tilemapHitboxFactory.createHitboxes(map, "EntradaPortalNeve");

            if (entradaPortais.entrarPortalAreia(player, entradaPortalAreia, batch, shapeRenderer,game, camera, viewport)) {
                entrouPortal = true;
            }
            if (entradaPortais.entrarPortalNeve(player, entradaPortalNeve, batch, game)) {
                entrouPortal = true;
            }

            List<Rectangle> portaoNeve = tilemapHitboxFactory.createHitboxes(map, "PortaoNeve");
            List<Rectangle> portaoNeveMenssagem = tilemapHitboxFactory.createHitboxes(map, "PortaoNeveMenssagem");

            if (player.getLevel() < 5) {
                tilemapHitboxFactory.drawObjects(map, "PortaoNeve", batch, player.getCamera(), true);
                portoes.menssagemPortao(player, portaoNeveMenssagem, batch, shapeRenderer,"Level 5 necessario!");
            }
            player.adicionarColisaoPorLevel(portaoNeve, 5);
        } catch (Exception e) {
            Gdx.app.error("Base", "Erro ao verificar portais: " + e.getMessage());
        }
    }

    @Override
    public void logRoomInfo(Room room) {

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
    public List<EnemyTemplate> getInimigos() {
        return List.of();
    }

    @Override
    public List<EnemyTemplate> getBosses() {
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
        if (screenTransition != null) {
            screenTransition.dispose();
        }
    }
}
