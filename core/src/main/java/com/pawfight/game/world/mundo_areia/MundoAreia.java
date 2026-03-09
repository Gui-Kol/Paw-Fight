package com.pawfight.game.world.mundo_areia;

import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.PawFight;
import com.pawfight.game.commun.Hud.Hud;
import com.pawfight.game.commun.LayerRenderer;
import com.pawfight.game.commun.phisics.ChecarColisao;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.WorldTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MundoAreia extends WorldTemplate {
    private List<Room> rooms;
    private RoomGenerator roomGenerator;
    private Room currentRoom;
    private Hud hud;
    private Set<String> salasVisitadas = new HashSet<>();


    public MundoAreia(PawFight game, PlayerTemplate player) {
        super(game, "menu/menu.png", "audio/music/time_for_adventure.wav");
        setPlayer(player);
        player.setLocal(500, 100);

        roomGenerator = new RoomGenerator();
        rooms = roomGenerator.generate(10); // gera 10 salas
        currentRoom = rooms.get(0); // começa na primeira sala

        hud = player.getHud();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        checkPortas();        // verifica colisão com portas
        int indiceAtual = rooms.indexOf(currentRoom);
        hud.desenharSalaAtual(indiceAtual,currentRoom.getType(),batch);
    }


    @Override
    protected void renderLayers() {
        List<String> layers = new ArrayList<>();
        layers.add("Sub");
        layers.add("Solo");
        layers.add("ParedeLayer");

        if (currentRoom.hasNorth()) {
            layers.add("PortaCima");
        }
        if (currentRoom.hasWest()) {
            layers.add("PortaEsquerda");
        }
        if (currentRoom.hasEast()) {
            layers.add("PortaDireita");
        }
        layerRenderer.renderLayers(layers.toArray(new String[0]), player.getCamera());
    }


    @Override
    protected void renderLayersUp() {
        List<String> layers = new ArrayList<>();
        layers.add("Up");
        if (currentRoom.getType() != RoomType.SPAWN && currentRoom.hasSouth()) {
            layers.add("PortaBaixo");
        }
        layerRenderer.renderLayers(layers.toArray(new String[0]), player.getCamera());

        // chamada correta do minimapa
        hud.desenharMiniMapa(batch, shapeRenderer, salasVisitadas, currentRoom, roomGenerator.getRoomMap());
    }

    @Override
    protected String getMapPath() {
        // Decide o mapa baseado no tipo da sala atual
        switch (currentRoom.getType()) {
            case BOSS:
                return "world/mundo_areia/BOSS.tmx";
            case INIMIGOS:
                return "world/mundo_areia/INIMIGOS.tmx";
            case INIMIGOS_FORTES:
                return "world/mundo_areia/INIMIGOS_FORTES.tmx";
            case TESOURO:
                return "world/mundo_areia/TESOURO.tmx";
            case SPAWN:
                return "world/mundo_areia/SPAWN.tmx";
            default:
                return "world/mundo_areia/INIMIGOS.tmx";
        }
    }
    private void moverParaSala(int x, int y) {
        Room room = roomGenerator.getRoomMap().get(x + "," + y);
        if (room != null) {
            currentRoom = room;
            map = new TmxMapLoader().load(getMapPath());
            layerRenderer = new LayerRenderer(map);

            salasVisitadas.add(x + "," + y); // registra sala visitada
        }
    }



    private void checkPortas() {
        Rectangle playerBox = player.getHitBox();

        if (currentRoom.hasNorth()) {
            Room destino = roomGenerator.getRoomMap().get(currentRoom.getX() + "," + (currentRoom.getY() + 1));
            if (destino != null) {
                List<Rectangle> portaCima = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaCima",16,16);
                if (ChecarColisao.houveColisao(playerBox, portaCima)) {
                    moverParaSala(destino.getX(), destino.getY());
                    player.setLocal(430, 120);
                    return;
                }
            }
        }

        // Porta cima → sair embaixo da próxima sala
        if (currentRoom.hasNorth()) {
            List<Rectangle> portaCima = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaCima",16,16);
            if (ChecarColisao.houveColisao(playerBox, portaCima)) {
                moverParaSala(currentRoom.getX(), currentRoom.getY() + 1);
                // posição próxima à porta de baixo da nova sala
                player.setLocal(430, 120);
                return;
            }
        }

        // Porta baixo → sair em cima da sala anterior
        if (currentRoom.getType() != RoomType.SPAWN && currentRoom.hasSouth()) {
            List<Rectangle> portaBaixo = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaBaixo",16,16);
            if (ChecarColisao.houveColisao(playerBox, portaBaixo)) {
                moverParaSala(currentRoom.getX(), currentRoom.getY() - 1);
                // posição próxima à porta de cima da nova sala
                player.setLocal(350, 840);
                return;
            }
        }

        // Porta esquerda → sair na direita da próxima sala
        if (currentRoom.hasWest()) {
            List<Rectangle> portaEsq = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaEsquerda",16,16);
            if (ChecarColisao.houveColisao(playerBox, portaEsq)) {
                moverParaSala(currentRoom.getX() - 1, currentRoom.getY());
                // posição próxima à porta direita da nova sala
                player.setLocal(820, 420);
                return;
            }
        }

        // Porta direita → sair na esquerda da próxima sala
        if (currentRoom.hasEast()) {
            List<Rectangle> portaDir = tilemapHitboxFactory.createTileLayerHitboxes(map, "PortaDireita",16,16);
            if (ChecarColisao.houveColisao(playerBox, portaDir)) {
                moverParaSala(currentRoom.getX() + 1, currentRoom.getY());
                // posição próxima à porta esquerda da nova sala
                player.setLocal(70, 420);
            }
        }
    }




    @Override
    protected void checkPortals() {}

}
