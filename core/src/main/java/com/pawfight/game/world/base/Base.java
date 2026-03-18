package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.design.ScreenTransition;
import com.pawfight.game.world.WorldTemplate;

import java.util.List;

public class Base extends WorldTemplate {
    private EntradaPortais entradaPortais;
    private final EscolherPersonagem escolherPersonagem;
    private final ScreenTransition screenTransition;
    private boolean entrouPortal;
    private final Portoes portoes;
    private int carregarParede = 0;

    public Base(PawFight game, OrthographicCamera camera, Viewport viewport) {
        super(game, "menu/menu.png", "audio/music/time_for_adventure.wav", camera, viewport);
        Gdx.app.log("Base", "Iniciando Base...");
        screenTransition = new ScreenTransition(game);
        escolherPersonagem = new EscolherPersonagem(game);
        entrouPortal = false;
        portoes = new Portoes();
    }

    @Override
    protected String getMapPath() {
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

        if (carregarParede == 0) {
            game.resize(viewport.getScreenWidth(), viewport.getScreenHeight());
            entradaPortais = new EntradaPortais(screenTransition, player.getHud());
            carregarParede();
            carregarParede ++;
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

            if (entradaPortais.entrarPortalAreia(player, entradaPortalAreia, batch, game, camera, viewport)) {
                entrouPortal = true;
            }
            if (entradaPortais.entrarPortalNeve(player, entradaPortalNeve, batch, game)) {
                entrouPortal = true;
            }

            List<Rectangle> portaoNeve = tilemapHitboxFactory.createHitboxes(map, "PortaoNeve");
            List<Rectangle> portaoNeveMenssagem = tilemapHitboxFactory.createHitboxes(map, "PortaoNeveMenssagem");

            if (player.getLevel() < 5) {
                tilemapHitboxFactory.drawObjects(map, "PortaoNeve", batch, player.getCamera(), true);
                portoes.menssagemPortao(player, portaoNeveMenssagem, batch, "Level 5 necessario!");
            }
            player.adicionarColisaoPorLevel(portaoNeve, 5);
        } catch (Exception e) {
            Gdx.app.error("Base", "Erro ao verificar portais: " + e.getMessage());
        }
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
