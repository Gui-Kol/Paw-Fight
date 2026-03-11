package com.pawfight.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.Hud.DesenharMiniMapa;
import com.pawfight.game.engine.LayerRenderer;
import com.pawfight.game.engine.animation.ScreenTransition;
import com.pawfight.game.engine.phisics.ChecarColisao;
import com.pawfight.game.engine.procedural.GerarInimigos;
import com.pawfight.game.entity.bosses.BossesTemplate;
import com.pawfight.game.entity.enemy.EnemySkeleton;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.engine.procedural.Room;
import com.pawfight.game.engine.procedural.RoomGenerator;
import com.pawfight.game.engine.procedural.RoomType;

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
    private ScreenTransition screenTransition;
    private boolean initialized = false;
    private GerarInimigos gerarInimigos;
    private List<EnemyTemplate> listaInimigos;

    public MundoAreia(PawFight game, PlayerTemplate player) {
        super(game, "menu/menu.png", "audio/music/time_for_adventure.wav");
        Gdx.app.log("MundoAreia", "Iniciando Mundo...");
        screenTransition = new ScreenTransition(game);
        roomGenerator = new RoomGenerator();
        setPlayer(player);

        // Reseta estado do player
        player.setLocal(500, 100);
        salasVisitadas = new HashSet<>();
        desenharMiniMapa = new DesenharMiniMapa();
        gerarInimigos = new GerarInimigos();
        listaInimigos = new ArrayList<>();

        // Gera salas APENAS aqui, não carrega mapa
        try {
            gerarRooms();
            if (currentRoom == null) {
                throw new RuntimeException("Erro: currentRoom não foi inicializado.");
            }
            Gdx.app.log("MundoAreia", "Salas geradas com sucesso no construtor. Mapa será carregado em show().");
            logRoomInfo(currentRoom);
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro ao gerar salas: " + e.getMessage(), e);
            errorFinal = true;
        }
    }

    private void logRoomInfo(Room room) {
        if (room == null) {
            Gdx.app.error("MundoAreia", "Room é null em logRoomInfo!");
            return;
        }
        if (!currentRoom.hasEast() && !currentRoom.hasWest() && !currentRoom.hasNorth() && !currentRoom.hasSouth()) {
            Gdx.app.error("MundoAreia", "Sala não tem portas.");
        } else {
            Gdx.app.log("MundoAreia", "Room [" + room.getX() + "," + room.getY() + "] Type: " + room.getType());
            Gdx.app.log("MundoAreia", "hasNorth: " + room.hasNorth() + ", hasSouth: " + room.hasSouth() +
                ", hasEast: " + room.hasEast() + ", hasWest: " + room.hasWest());
        }
    }

    private void gerarRooms() {
        try {
            rooms = roomGenerator.generate(10,5);
            if (rooms == null || rooms.isEmpty()) {
                throw new RuntimeException("Erro: Nenhuma sala foi gerada.");
            }
            currentRoom = rooms.get(0);
            salasVisitadas.add(currentRoom.getX() + "," + currentRoom.getY());
            Gdx.app.log("MundoAreia", "Salas geradas com sucesso: " + rooms.size());
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro ao gerar salas: " + e.getMessage(), e);
            currentRoom = null;
            throw new RuntimeException(e);
        }
    }

    private List<EnemyTemplate> gerarInimigos() {
        if (currentRoom == null) {
            Gdx.app.error("MundoAreia", "gerarInimigos() chamado mas currentRoom é null!");
            return new ArrayList<>();
        }

        List<Rectangle> regiaoSpawn = tilemapHitboxFactory.createHitboxes(map,"Inimigos");
        if (regiaoSpawn == null){
            Gdx.app.error("MundoAreia", "não existe região para gerar inimigos!");
            return null;
        }
        List<EnemyTemplate> inimigosGerados = new ArrayList<>();

        switch (currentRoom.getType()) {
            case INIMIGOS -> {
                EnemyTemplate base = new EnemySkeleton(0, 0, false, player);
                inimigosGerados.addAll(gerarInimigos.inimigos(base, 10, 4, regiaoSpawn));
            }
            case INIMIGOS_FORTES -> {
                EnemyTemplate base = new EnemySkeleton(0, 0, false, player);
                inimigosGerados.addAll(gerarInimigos.inimigos(base, 7, 4, regiaoSpawn));
                EnemyTemplate baseForte = new EnemySkeleton(0, 0, true, player);
                inimigosGerados.addAll(gerarInimigos.inimigos(baseForte, 5, 2, regiaoSpawn));
            }
        }
        return inimigosGerados;
    }


    @Override
    public void show() {
        if (errorFinal) {
            Gdx.app.error("MundoAreia", "show() chamado mas errorFinal = true");
            return;
        }

        if (currentRoom == null) {
            Gdx.app.error("MundoAreia", "show() chamado mas currentRoom é null!");
            errorFinal = true;
            return;
        }

        try {
            map = new TmxMapLoader().load(getMapPath());
            layerRenderer = new LayerRenderer(map);
            backMusic.setLooping(true);
            backMusic.setVolume(0);
            backMusic.play();
            initialized = true;
            Gdx.app.log("MundoAreia", "show() completado com sucesso. Mapa: " + getMapPath());
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro em show(): " + e.getMessage(), e);
            errorFinal = true;
        }
    }


    @Override
    public void render(float delta) {
        if (!initialized) {
            Gdx.app.error("MundoAreia", "render() chamado mas não foi inicializado. Chamando show()...");
            show();
        }

        if (errorFinal) {
            screenTransition.update(Gdx.graphics.getDeltaTime());
            screenTransition.render(batch);
            return;
        }

        if (currentRoom == null || map == null || layerRenderer == null) {
            Gdx.app.error("MundoAreia", "render() - objeto null: currentRoom=" + (currentRoom == null) +
                ", map=" + (map == null) + ", layerRenderer=" + (layerRenderer == null));
            return;
        }

        try {
            if (player != null && !player.isMorto()) {
                super.render(delta);
                int indiceAtual = rooms.indexOf(currentRoom);
                desenharMiniMapa.desenharSalaAtual(indiceAtual, currentRoom.getType(), batch);

                // Atualizar e renderizar inimigos
                List<EnemyTemplate> inimigosMortos = new ArrayList<>();
                for (EnemyTemplate enemy : listaInimigos) {
                    enemy.update(delta);
                    if (enemy.isMorto()) {
                        inimigosMortos.add(enemy);
                    }
                }
                // Remover inimigos mortos
                listaInimigos.removeAll(inimigosMortos);
                // Renderizar inimigos após atualização
                for (EnemyTemplate enemy : listaInimigos) {
                    enemy.draw(batch, shapeRenderer);
                }
            }
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro em render: " + e.getMessage(), e);
        }
    }

    @Override
    protected void renderLayers() {
        if (map == null || currentRoom == null) {
            Gdx.app.error("MundoAreia", "renderLayers - map ou currentRoom é null");
            return;
        }
        try {
            List<String> layers = new ArrayList<>();
            String[] baseLayers = {"Sub", "Solo", "ParedeLayer"};
            for (String layer : baseLayers) {
                if (map.getLayers().get(layer) != null) {
                    layers.add(layer);
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
            Gdx.app.error("MundoAreia", "renderLayersUp - map ou currentRoom é null");
            return;
        }
        try {
            List<String> layers = new ArrayList<>();
            if (map.getLayers().get("Up") != null) {
                layers.add("Up");
            }
            if (currentRoom.getType() != RoomType.SPAWN && currentRoom.hasSouth() && map.getLayers().get("PortaBaixo") != null) {
                layers.add("PortaBaixo");
            }
            layerRenderer.renderLayers(layers.toArray(new String[0]), player.getCamera());

            if (shapeRenderer != null && !shapeRenderer.isDrawing()) {
                if (desenharMiniMapa != null && roomGenerator != null) {
                    desenharMiniMapa.desenharMiniMapa(player.getHud().getHudCamera(), batch, shapeRenderer, salasVisitadas, currentRoom, roomGenerator.getRoomMap());
                }
            }
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro em renderLayersUp: " + e.getMessage(), e);
        }
    }

    @Override
    protected String getMapPath() {
        if (currentRoom == null) {
            Gdx.app.error("MundoAreia", "getMapPath() - currentRoom é null!");
            return "world/mundo_areia/SPAWN.tmx";
        }

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

                // Gera inimigos ANTES de marcar como visitada
                moverSalaInimigos();

                salasVisitadas.add(x + "," + y);
                Gdx.app.log("MundoAreia", "Sala mudada com sucesso para: " + x + "," + y);
                logRoomInfo(room);
            } catch (Exception e) {
                Gdx.app.error("MundoAreia", "Erro ao mudar para sala " + x + "," + y + ": " + e.getMessage(), e);
            }
        } else {
            Gdx.app.error("MundoAreia", "Erro: Sala não encontrada em " + x + "," + y);
        }
    }

    private void moverSalaInimigos(){
        listaInimigos.clear();
        if (!currentRoomFoiVisitada()) {
            listaInimigos = gerarInimigos();
            // Definir lista de inimigos para cada inimigo
            for (EnemyTemplate enemy : listaInimigos) {
                enemy.setEnemiesList(listaInimigos);
            }
        }
        Gdx.app.log("MundoAreia", "Inimigos gerados para sala [" + currentRoom.getX() + "," + currentRoom.getY() + "]: " + listaInimigos.size());
    }

    private boolean currentRoomFoiVisitada() {
        String key = currentRoom.getX() + "," + currentRoom.getY();
        return salasVisitadas.contains(key);
    }

    @Override
    protected void checkPortals() {
        if (player == null || currentRoom == null || map == null || roomGenerator == null) {
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
                        moverParaSala(destino.getX(), destino.getY());
                        player.setLocal(430, 120);
                        return;
                    }
                } catch (Exception e) {
                    Gdx.app.error("MundoAreia", "Erro porta cima: " + e.getMessage());
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
                        moverParaSala(destino.getX(), destino.getY());
                        player.setLocal(350, 840);
                        return;
                    }
                } catch (Exception e) {
                    Gdx.app.error("MundoAreia", "Erro porta baixo: " + e.getMessage());
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
                        moverParaSala(destino.getX(), destino.getY());
                        player.setLocal(820, 420);
                        return;
                    }
                } catch (Exception e) {
                    Gdx.app.error("MundoAreia", "Erro porta esquerda: " + e.getMessage());
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
                        moverParaSala(destino.getX(), destino.getY());
                        player.setLocal(70, 420);
                    }
                } catch (Exception e) {
                    Gdx.app.error("MundoAreia", "Erro porta direita: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (desenharMiniMapa != null) {
            desenharMiniMapa.dispose();
        }
        if (screenTransition != null) {
            screenTransition.dispose();
        }
        // Dispor inimigos restantes
        for (EnemyTemplate enemy : listaInimigos) {
            enemy.dispose();
        }
        listaInimigos.clear();
    }
}
