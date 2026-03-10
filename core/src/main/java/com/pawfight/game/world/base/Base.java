package com.pawfight.game.world.base;

import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.PawFight;
import com.pawfight.game.world.WorldTemplate;

import java.util.List;

public class Base extends WorldTemplate {

    public Base(PawFight game) {
        super(game, "menu/menu.png", "audio/music/time_for_adventure.wav");
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
            return;
        }

        super.render(delta);
    }

    @Override
    protected void preLoad() {

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
        // Aqui você define os portais que existem nessa base
        List<Rectangle> entradaPortalAreia = tilemapHitboxFactory.createHitboxes(map, "EntradaPortalAreia");
        List<Rectangle> entradaPortalNeve = tilemapHitboxFactory.createHitboxes(map, "EntradaPortalNeve");

        if (entradaPortais.entrarPortalAreia(player, entradaPortalAreia, batch, game)) {
            entrouPortal = true;
        }
        if (entradaPortais.entrarPortalNeve(player, entradaPortalNeve, batch, game)) {
            entrouPortal = true;
        }

        // Exemplo de portão com requisito de nível
        List<Rectangle> portaoNeve = tilemapHitboxFactory.createHitboxes(map, "PortaoNeve");
        List<Rectangle> portaoNeveMenssagem = tilemapHitboxFactory.createHitboxes(map, "PortaoNeveMenssagem");

        if (player.getLevel() < 5) {
            tilemapHitboxFactory.drawObjects(map, "PortaoNeve", batch, player.getCamera(), true);
            portoes.menssagemPortao(player, portaoNeveMenssagem, batch, "Level 5 necessario!");
        }
        player.adicionarColisaoPorLevel(portaoNeve, 5);
    }
}
