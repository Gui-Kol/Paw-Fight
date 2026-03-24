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
    public void carregar(WorldTemplate world) {
        PlayerTemplate player = world.getPlayer();
        Room currentRoom = world.getCurrentRoom();
        TiledMap map = world.getMap();
        RoomGenerator roomGenerator = world.getRoomGenerator();
        boolean podeEntrarPorta = world.isPodeEntrarPorta();
        TilemapHitboxFactory tilemapHitboxFactory = world.getTilemapHitboxFactory();
        String nomeClasseOrigem = world.getWorldName();

        if (player == null || currentRoom == null || map == null || roomGenerator == null || !podeEntrarPorta) {
            return;
        }
        Rectangle playerBox = player.getHitBox();

        // Porta cima
        if (currentRoom.hasNorth()) {
            Room destino = roomGenerator.getRoomMap().get(currentRoom.getX() + "," + (currentRoom.getY() + 1));
            if (destino != null && map.getLayers().get("PortaCima") != null) {
                try {
                    List<Rectangle> portaCima = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaCima", 16, 16);
                    if (!portaCima.isEmpty() && ChecarColisao.houveColisao(playerBox, portaCima)) {
                        moverParaSala(destino.getX(), destino.getY(), world);
                        player.setLocal(430, 120);
                        return;
                    }
                } catch (Exception e) {
                    Gdx.app.error(nomeClasseOrigem, "Erro porta cima: " + e.getMessage());
                }
            }
        }

        // Porta baixo
        if (currentRoom.getType() != RoomType.SPAWN && currentRoom.hasSouth()) {
            Room destino = roomGenerator.getRoomMap().get(currentRoom.getX() + "," + (currentRoom.getY() - 1));
            if (destino != null && map.getLayers().get("PortaBaixo") != null) {
                try {
                    List<Rectangle> portaBaixo = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaBaixo", 16, 16);
                    if (!portaBaixo.isEmpty() && ChecarColisao.houveColisao(playerBox, portaBaixo)) {
                        moverParaSala(destino.getX(), destino.getY(), world);
                        player.setLocal(350, 840);
                        return;
                    }
                } catch (Exception e) {
                    Gdx.app.error(nomeClasseOrigem, "Erro porta baixo: " + e.getMessage());
                }
            }
        }

        // Porta esquerda
        if (currentRoom.hasWest()) {
            Room destino = roomGenerator.getRoomMap().get((currentRoom.getX() - 1) + "," + currentRoom.getY());
            if (destino != null && map.getLayers().get("PortaEsquerda") != null) {
                try {
                    List<Rectangle> portaEsq = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaEsquerda", 16, 16);
                    if (!portaEsq.isEmpty() && ChecarColisao.houveColisao(playerBox, portaEsq)) {
                        moverParaSala(destino.getX(), destino.getY(), world);
                        player.setLocal(820, 420);
                        return;
                    }
                } catch (Exception e) {
                    Gdx.app.error(nomeClasseOrigem, "Erro porta esquerda: " + e.getMessage());
                }
            }
        }

        // Porta direita
        if (currentRoom.hasEast()) {
            Room destino = roomGenerator.getRoomMap().get((currentRoom.getX() + 1) + "," + currentRoom.getY());
            if (destino != null && map.getLayers().get("PortaDireita") != null) {
                try {
                    List<Rectangle> portaDir = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaDireita", 16, 16);
                    if (!portaDir.isEmpty() && ChecarColisao.houveColisao(playerBox, portaDir)) {
                        moverParaSala(destino.getX(), destino.getY(), world);
                        player.setLocal(70, 420);
                    }
                } catch (Exception e) {
                    Gdx.app.error(nomeClasseOrigem, "Erro porta direita: " + e.getMessage());
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
        if (room != null) {
            if (map != null) {
                map.dispose();
                if (layerRenderer != null) {
                    layerRenderer.dispose();
                }
            }
            try {
                world.setCurrentRoom(room);
                map = new TmxMapLoader().load(world.getMapPath());
                world.setLayerRenderer(new LayerRenderer(map));

                // Gera inimigos ANTES de marcar como visitada
                moverSalaInimigos(world);

                world.getSalasVisitadas().add(x + "," + y);
                Gdx.app.log(nomeClasseOrigem, "Sala mudada com sucesso para: " + x + "," + y);
                world.logRoomInfo(room);
                world.gerarObjetos();
            } catch (Exception e) {
                Gdx.app.error(nomeClasseOrigem, "Erro ao mudar para sala " + x + "," + y + ": " + e.getMessage(), e);
            }
        } else {
            Gdx.app.error(nomeClasseOrigem, "Erro: Sala não encontrada em " + x + "," + y);
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
                for (EnemyTemplate enemy : listaInimigos) {
                    enemy.setEnemiesList(listaInimigos);
                }
            }
        }

        Gdx.app.log(nomeClasseOrigem, "Inimigos gerados para sala [" + currentRoom.getX() + "," + currentRoom.getY() + "]: " + listaInimigos.size());
    }
}
