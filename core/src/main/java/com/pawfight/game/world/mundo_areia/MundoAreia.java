package com.pawfight.game.world.mundo_areia;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.PawFight;
import com.pawfight.game.commun.Hud.DesenharMiniMapa;
import com.pawfight.game.commun.LayerRenderer;
import com.pawfight.game.commun.phisics.ChecarColisao;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.WorldTemplate;
import com.pawfight.game.world.base.Base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MundoAreia extends WorldTemplate {
    private List<Room> rooms;
    private final RoomGenerator roomGenerator;
    private Room currentRoom;
    private final Set<String> salasVisitadas;
    private final DesenharMiniMapa desenharMiniMapa;
    private boolean errorFinal = false;

    public MundoAreia(PawFight game, PlayerTemplate player) {
        super(game, "menu/menu.png", "audio/music/time_for_adventure.wav");
        roomGenerator = new RoomGenerator();
        setPlayer(player);
        player.setLocal(500, 100);
        salasVisitadas = new HashSet<>();
        desenharMiniMapa = new DesenharMiniMapa();

        int tentativas = 0;
        boolean sucesso = false;

        while (!sucesso && tentativas < 3) { // tenta no máximo 3 vezes
            try {
                gerarRooms(); // garante currentRoom
                if (currentRoom != null) {
                    map = new TmxMapLoader().load(getMapPath());
                    layerRenderer = new LayerRenderer(map);
                    Gdx.app.log("MundoAreia", "Mapa carregado com sucesso: " + getMapPath());
                    sucesso = true; // se chegou aqui, deu certo
                }
            } catch (Exception e) {
                tentativas++;
                Gdx.app.error("MundoAreia", "Erro ao carregar mapa (tentativa " + tentativas + "): " + e.getMessage(), e);
                if (tentativas >= 3) {
                    Gdx.app.error("MundoAreia", "Falha definitiva após 3 tentativas.");
                    screenTransition.start(new Base(game));
                    errorFinal = true;
                }
            }
        }
    }



    private void gerarRooms() {
        try {
            rooms = roomGenerator.generate(10);
            if (rooms == null || rooms.isEmpty()) {
                throw new RuntimeException("Erro: Nenhuma sala foi gerada.");
            }
            currentRoom = rooms.get(0); // inicializa primeiro
            salasVisitadas.add(currentRoom.getX() + "," + currentRoom.getY()); // só usa depois
            Gdx.app.log("MundoAreia", "Salas geradas com sucesso: " + rooms.size());
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro ao gerar salas: " + e.getMessage(), e);
        }
    }



    @Override
    public void preLoad() {
    }


    @Override
    public void render(float delta) {
        if (errorFinal) {
            screenTransition.update(Gdx.graphics.getDeltaTime());
            screenTransition.render(batch);
        }
        if (currentRoom == null || map == null) {
            Gdx.app.error("MundoAreia", "currentRoom ou map é null, pulando render.");
            return;
        }
        try {
            super.render(delta);
            int indiceAtual = rooms.indexOf(currentRoom);
            desenharMiniMapa.desenharSalaAtual(indiceAtual, currentRoom.getType(), batch);
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro em render: " + e.getMessage(), e);
        }
    }


    @Override
    protected void renderLayers() {
        if (map == null || currentRoom == null) {
            Gdx.app.error("MundoAreia", "map ou currentRoom é null em renderLayers.");
            return;
        }
        try {
            List<String> layers = new ArrayList<>();
            String[] baseLayers = {"Sub", "Solo", "ParedeLayer"};
            for (String layer : baseLayers) {
                if (map.getLayers().get(layer) != null) {
                    layers.add(layer);
                } else {
                    Gdx.app.error("MundoAreia", "Camada base não encontrada: " + layer);
                }
            }

            if (currentRoom.hasNorth() && map.getLayers().get("PortaCima") != null) {
                layers.add("PortaCima");
            }
            if (currentRoom.hasWest() && map.getLayers().get("PortaEsquerda") != null) {
                layers.add("PortaEsquerda");
            }
            if (currentRoom.hasEast() && map.getLayers().get("PortaDireita") != null) {
                layers.add("PortaDireita");
            }
            layerRenderer.renderLayers(layers.toArray(new String[0]), player.getCamera());
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro em renderLayers: " + e.getMessage(), e);
        }
    }

    @Override
    protected void renderLayersUp() {
        if (map == null || currentRoom == null) {
            Gdx.app.error("MundoAreia", "map ou currentRoom é null em renderLayersUp.");
            return;
        }
        try {
            List<String> layers = new ArrayList<>();
            if (map.getLayers().get("Up") != null) {
                layers.add("Up");
            } else {
                Gdx.app.error("MundoAreia", "Camada Up não encontrada.");
            }
            if (currentRoom.getType() != RoomType.SPAWN && currentRoom.hasSouth() && map.getLayers().get("PortaBaixo") != null) {
                layers.add("PortaBaixo");
            }
            layerRenderer.renderLayers(layers.toArray(new String[0]), player.getCamera());

            // chamada correta do minimapa
            if (shapeRenderer != null && shapeRenderer.isDrawing()) {
                shapeRenderer.end();
            }
            if (desenharMiniMapa != null) {
                desenharMiniMapa.desenharMiniMapa(player.getHud().getHudCamera(), batch, shapeRenderer, salasVisitadas, currentRoom, roomGenerator.getRoomMap());
            }
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro em renderLayersUp: " + e.getMessage(), e);
        }
    }

    @Override
    protected String getMapPath() {

        return switch (currentRoom.getType()) {
            case BOSS -> "world/mundo_areia/BOSS.tmx";
            case INIMIGOS -> "world/mundo_areia/INIMIGOS.tmx";
            case INIMIGOS_FORTES -> "world/mundo_areia/INIMIGOS_FORTES.tmx";
            case TESOURO -> "world/mundo_areia/TESOURO.tmx";
            case SPAWN -> "world/mundo_areia/SPAWN.tmx";
        };
    }

    private void moverParaSala(int x, int y) {
        if (roomGenerator == null) {
            Gdx.app.error("MundoAreia", "roomGenerator é null em moverParaSala.");
            return;
        }
        Room room = roomGenerator.getRoomMap().get(x + "," + y);
        if (room != null) {
            if (map != null) {
                map.dispose();
                if (layerRenderer != null) {
                    layerRenderer.dispose();
                }
            }
            try {
                currentRoom = room;
                map = new TmxMapLoader().load(getMapPath());
                layerRenderer = new LayerRenderer(map);
                salasVisitadas.add(x + "," + y);
                Gdx.app.log("MundoAreia", "Sala mudada com sucesso para: " + x + "," + y);
            } catch (Exception e) {
                Gdx.app.error("MundoAreia", "Erro ao mudar para sala " + x + "," + y + ": " + e.getMessage(), e);
            }
        } else {
            Gdx.app.error("MundoAreia", "Erro: Sala não encontrada em " + x + "," + y);
        }
        if (room != null) {
            Gdx.app.log("MundoAreia", "Room hasNorth: " + room.hasNorth());
        }
    }


    @Override
    protected void checkPortals() {
        if (player == null || currentRoom == null || map == null || roomGenerator == null) {
            Gdx.app.error("MundoAreia", "Objeto null em checkPortas: player, currentRoom, map ou roomGenerator.");
            return;
        }
        Rectangle playerBox = player.getHitBox();

        // Porta cima
        if (currentRoom.hasNorth()) {
            Room destino = roomGenerator.getRoomMap().get(currentRoom.getX() + "," + (currentRoom.getY() + 1));
            if (destino != null) {
                try {
                    if (map.getLayers().get("PortaCima") != null) {
                        List<Rectangle> portaCima = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaCima", 16, 16);
                        if (!portaCima.isEmpty() && ChecarColisao.houveColisao(playerBox, portaCima)) {
                            moverParaSala(destino.getX(), destino.getY());
                            player.setLocal(430, 120);
                            return;
                        }
                    } else {
                        Gdx.app.error("MundoAreia", "Camada PortaCima não encontrada no mapa.");
                    }
                } catch (Exception e) {
                    Gdx.app.error("MundoAreia", "Erro ao verificar porta cima: " + e.getMessage(), e);
                }
            } else {
                Gdx.app.error("MundoAreia", "Erro: Destino norte não encontrado para sala atual.");
            }
        }

        // Porta baixo
        if (currentRoom.getType() != RoomType.SPAWN && currentRoom.hasSouth()) {
            Room destino = roomGenerator.getRoomMap().get(currentRoom.getX() + "," + (currentRoom.getY() - 1));
            if (destino != null) {
                try {
                    if (map.getLayers().get("PortaBaixo") != null) {
                        List<Rectangle> portaBaixo = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaBaixo", 16, 16);
                        if (!portaBaixo.isEmpty() && ChecarColisao.houveColisao(playerBox, portaBaixo)) {
                            moverParaSala(destino.getX(), destino.getY());
                            player.setLocal(350, 840);
                            return;
                        }
                    } else {
                        Gdx.app.error("MundoAreia", "Camada PortaBaixo não encontrada no mapa.");
                    }
                } catch (Exception e) {
                    Gdx.app.error("MundoAreia", "Erro ao verificar porta baixo: " + e.getMessage(), e);
                }
            } else {
                Gdx.app.error("MundoAreia", "Erro: Destino sul não encontrado para sala atual.");
            }
        }

        // Porta esquerda
        if (currentRoom.hasWest()) {
            Room destino = roomGenerator.getRoomMap().get((currentRoom.getX() - 1) + "," + currentRoom.getY());
            if (destino != null) {
                try {
                    if (map.getLayers().get("PortaEsquerda") != null) {
                        List<Rectangle> portaEsq = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaEsquerda", 16, 16);
                        if (!portaEsq.isEmpty() && ChecarColisao.houveColisao(playerBox, portaEsq)) {
                            moverParaSala(destino.getX(), destino.getY());
                            player.setLocal(820, 420);
                            return;
                        }
                    } else {
                        Gdx.app.error("MundoAreia", "Camada PortaEsquerda não encontrada no mapa.");
                    }
                } catch (Exception e) {
                    Gdx.app.error("MundoAreia", "Erro ao verificar porta esquerda: " + e.getMessage(), e);
                }
            } else {
                Gdx.app.error("MundoAreia", "Erro: Destino oeste não encontrado para sala atual.");
            }
        }

        // Porta direita
        if (currentRoom.hasEast()) {
            Room destino = roomGenerator.getRoomMap().get((currentRoom.getX() + 1) + "," + currentRoom.getY());
            if (destino != null) {
                try {
                    if (map.getLayers().get("PortaDireita") != null) {
                        List<Rectangle> portaDir = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaDireita", 16, 16);
                        if (!portaDir.isEmpty() && ChecarColisao.houveColisao(playerBox, portaDir)) {
                            moverParaSala(destino.getX(), destino.getY());
                            player.setLocal(70, 420);
                        }
                    } else {
                        Gdx.app.error("MundoAreia", "Camada PortaDireita não encontrada no mapa.");
                    }
                } catch (Exception e) {
                    Gdx.app.error("MundoAreia", "Erro ao verificar porta direita: " + e.getMessage(), e);
                }
            } else {
                Gdx.app.error("MundoAreia", "Erro: Destino leste não encontrado para sala atual.");
            }
        }
    }

}
