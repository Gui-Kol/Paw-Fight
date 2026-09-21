package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.render.RenderizadorCamada;
import com.pawfight.game.engine.fisica.ChecarColisao;
import com.pawfight.game.engine.fisica.TilemapHitboxFactory;
import com.pawfight.game.engine.procedural.sala.Sala;
import com.pawfight.game.engine.procedural.sala.GeradorSalas;
import com.pawfight.game.engine.procedural.sala.TipoSala;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.template.WorldTemplate;

import java.util.List;

public class CarregarPortas {

    private enum Direcao {
        CIMA("PortaCima", 0, 1, 430, 120),
        BAIXO("PortaBaixo", 0, -1, 350, 840),
        ESQUERDA("PortaEsquerda", -1, 0, 820, 420),
        DIREITA("PortaDireita", 1, 0, 70, 420);

        final String layer;
        final int dx, dy;
        final int posX, posY;

        Direcao(String layer, int dx, int dy, int posX, int posY) {
            this.layer = layer;
            this.dx = dx;
            this.dy = dy;
            this.posX = posX;
            this.posY = posY;
        }
    }

    public void carregar(WorldTemplate world) {
        PlayerTemplate player = world.getPlayer();
        Sala currentRoom = world.getRoomManager().getCurrentRoom();
        TiledMap map = world.getMap();
        GeradorSalas geradorSalas = world.getRoomManager().getRoomGenerator();
        TilemapHitboxFactory tilemapHitboxFactory = world.getWorldPhysics().getTilemapHitboxFactory();
        String nomeClasseOrigem = world.getWorldName();

        if (player == null || currentRoom == null || map == null || geradorSalas == null || !world.getRoomManager().isPodeEntrarPorta()) {
            return;
        }

        Rectangle playerBox = player.getHitBox();

        for (Direcao dir : Direcao.values()) {
            // SPAWN não tem porta para baixo
            if (dir == Direcao.BAIXO && currentRoom.getType() == TipoSala.SPAWN) continue;

            Sala destino = geradorSalas.getRoomMap().get((currentRoom.getX() + dir.dx) + "," + (currentRoom.getY() + dir.dy));

            // só continua se a sala atual realmente tiver conexão nessa direção
            boolean conexaoValida =
                (dir == Direcao.CIMA && currentRoom.hasNorth()) ||
                    (dir == Direcao.BAIXO && currentRoom.hasSouth()) ||
                    (dir == Direcao.ESQUERDA && currentRoom.hasWest()) ||
                    (dir == Direcao.DIREITA && currentRoom.hasEast());

            if (destino != null && conexaoValida && map.getLayers().get(dir.layer) != null) {
                try {
                    List<Rectangle> portas = tilemapHitboxFactory.createTileLayerHitboxes(map, dir.layer, 16, 16);
                    if (!portas.isEmpty() && ChecarColisao.houveColisao(playerBox, portas)) {
                        Gdx.app.log(nomeClasseOrigem, "Atravessando porta " + dir.name().toLowerCase()
                            + " — sala [" + currentRoom.getX() + "," + currentRoom.getY() + "] -> [" + destino.getX() + "," + destino.getY() + "]");
                        moverParaSala(destino.getX(), destino.getY(), world);
                        player.setLocal(dir.posX, dir.posY);
                        return;
                    }
                } catch (Exception e) {
                    Gdx.app.error(nomeClasseOrigem, "Erro porta " + dir.name().toLowerCase() + ": " + e.getMessage(), e);
                }
            }
        }
    }


    private void moverParaSala(int x, int y, WorldTemplate world) {
        PlayerTemplate player = world.getPlayer();
        TiledMap oldMap = world.getMap();
        GeradorSalas geradorSalas = world.getRoomManager().getRoomGenerator();
        RenderizadorCamada renderizadorCamada = world.getLayerRenderer();
        String nomeClasseOrigem = world.getWorldName();

        player.clearList();
        Sala sala = geradorSalas.getRoomMap().get(x + "," + y);

        if (sala == null) {
            Gdx.app.error(nomeClasseOrigem, "Erro: Sala não encontrada em " + x + "," + y);
            return;
        }

        if (oldMap != null) {
            oldMap.dispose();
            if (renderizadorCamada != null) renderizadorCamada.dispose();
        }

        try {
            world.getRoomManager().setCurrentRoom(sala);
            world.getWorldPhysics().getTilemapHitboxFactory().clearCache();

            TiledMap newMap = new TmxMapLoader().load(world.getMapPath());
            world.setMap(newMap);
            world.setLayerRenderer(new RenderizadorCamada(newMap));

            // Recarrega paredes a partir do NOVO mapa
            world.carregarParede();

            moverSalaInimigos(world);

            world.getRoomManager().getSalasVisitadas().add(x + "," + y);
            Gdx.app.log(nomeClasseOrigem, "Sala mudada com sucesso para: " + x + "," + y);
            world.logRoomInfo(sala);

            world.gerarObjetos();
        } catch (Exception e) {
            Gdx.app.error(nomeClasseOrigem, "Erro ao mudar para sala " + x + "," + y + ": " + e.getMessage(), e);
        }
    }

    private void moverSalaInimigos(WorldTemplate world) {
        Sala currentRoom = world.getRoomManager().getCurrentRoom();
        String nomeClasseOrigem = world.getWorldName();
        List<EnemyTemplate> listaInimigos = world.getEnemyManager().getListaInimigos();
        GerarInimigos gerarInimigos = world.getEnemyManager().getGerarInimigos();

        listaInimigos.clear();
        if (!world.getRoomManager().currentRoomFoiVisitada()) {
            List<EnemyTemplate> novos = gerarInimigos.gerarInimigos(world);
            if (novos != null) {
                listaInimigos.addAll(novos);
                java.util.List<Rectangle> paredes = world.getWorldPhysics().getParedes();
                listaInimigos.forEach(enemy -> {
                    enemy.setEnemiesList(listaInimigos);
                    enemy.setParedesColisores(paredes);
                });
            }
        }

        Gdx.app.log(nomeClasseOrigem, "Inimigos gerados para sala [" + currentRoom.getX() + "," + currentRoom.getY() + "]: " + listaInimigos.size());
    }
}
