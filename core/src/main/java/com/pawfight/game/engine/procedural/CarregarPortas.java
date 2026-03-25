package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.LayerRenderer;
import com.pawfight.game.engine.phisics.ChecarColisao;
import com.pawfight.game.engine.phisics.TilemapHitboxFactory;
import com.pawfight.game.engine.procedural.room.Room;
import com.pawfight.game.engine.procedural.room.RoomGenerator;
import com.pawfight.game.engine.procedural.room.RoomType;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.WorldTemplate;

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
        Room currentRoom = world.getCurrentRoom();
        TiledMap map = world.getMap();
        RoomGenerator roomGenerator = world.getRoomGenerator();
        TilemapHitboxFactory tilemapHitboxFactory = world.getTilemapHitboxFactory();
        String nomeClasseOrigem = world.getWorldName();

        if (player == null || currentRoom == null || map == null || roomGenerator == null || !world.isPodeEntrarPorta()) {
            return;
        }

        Rectangle playerBox = player.getHitBox();

        for (Direcao dir : Direcao.values()) {
            // SPAWN não tem porta para baixo
            if (dir == Direcao.BAIXO && currentRoom.getType() == RoomType.SPAWN) continue;

            Room destino = roomGenerator.getRoomMap().get((currentRoom.getX() + dir.dx) + "," + (currentRoom.getY() + dir.dy));

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
                        moverParaSala(destino.getX(), destino.getY(), world);
                        player.setLocal(dir.posX, dir.posY);
                        return;
                    }
                } catch (Exception e) {
                    Gdx.app.error(nomeClasseOrigem, "Erro porta " + dir.name().toLowerCase() + ": " + e.getMessage());
                }
            }
        }
    }


    private void moverParaSala(int x, int y, WorldTemplate world) {
        PlayerTemplate player = world.getPlayer();
        TiledMap map = world.getMap();
        RoomGenerator roomGenerator = world.getRoomGenerator();
        LayerRenderer layerRenderer = world.getLayerRenderer();
        String nomeClasseOrigem = world.getWorldName();

        player.clearList();
        world.carregarParede();
        Room room = roomGenerator.getRoomMap().get(x + "," + y);

        if (room == null) {
            Gdx.app.error(nomeClasseOrigem, "Erro: Sala não encontrada em " + x + "," + y);
            return;
        }

        if (map != null) {
            map.dispose();
            if (layerRenderer != null) layerRenderer.dispose();
        }

        try {
            world.setCurrentRoom(room);
            map = new TmxMapLoader().load(world.getMapPath());
            world.setLayerRenderer(new LayerRenderer(map));

            moverSalaInimigos(world);

            world.getSalasVisitadas().add(x + "," + y);
            Gdx.app.log(nomeClasseOrigem, "Sala mudada com sucesso para: " + x + "," + y);
            world.logRoomInfo(room);

            world.gerarObjetos(); // agora garante que os objetos sejam carregados
        } catch (Exception e) {
            Gdx.app.error(nomeClasseOrigem, "Erro ao mudar para sala " + x + "," + y + ": " + e.getMessage(), e);
        }
    }

    private void moverSalaInimigos(WorldTemplate world) {
        Room currentRoom = world.getCurrentRoom();
        String nomeClasseOrigem = world.getWorldName();
        List<EnemyTemplate> listaInimigos = world.getListaInimigos();
        GerarInimigos gerarInimigos = world.getGerarInimigos();

        listaInimigos.clear();
        if (!world.currentRoomFoiVisitada()) {
            List<EnemyTemplate> novos = gerarInimigos.gerarInimigos(world);
            if (novos != null) {
                listaInimigos.addAll(novos);
                listaInimigos.forEach(enemy -> enemy.setEnemiesList(listaInimigos));
            }
        }

        Gdx.app.log(nomeClasseOrigem, "Inimigos gerados para sala [" + currentRoom.getX() + "," + currentRoom.getY() + "]: " + listaInimigos.size());
    }
}
