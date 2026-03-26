package com.pawfight.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.procedural.ObjetoGerado;
import com.pawfight.game.engine.procedural.room.InfoGeraObjeto;
import com.pawfight.game.engine.procedural.room.Room;
import com.pawfight.game.engine.procedural.room.RoomGenerator;
import com.pawfight.game.engine.procedural.room.RoomType;
import com.pawfight.game.entity.enemy.Skeleton;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.ArrayList;
import java.util.List;

public class MundoAreia extends WorldTemplate {


    public MundoAreia(PawFight game, PlayerTemplate player, OrthographicCamera camera, Viewport viewport) {
        super(game, "menu/menu.png", "audio/music/time_for_adventure.wav", camera, viewport);
        Gdx.app.log("MundoAreia", "Iniciando Mundo...");
        roomGenerator = new RoomGenerator();
        setPlayer(player);

        // Reseta estado do player
        player.setLocal(500, 100);
        player.setPodeAtacar(true);

        // Gera salas APENAS aqui, não carrega mapa
        try {
            roomGenerator.gerarRooms(this);
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

    @Override
    public void logRoomInfo(Room room) {
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


    @Override
    public List<InfoGeraObjeto> getInfoObjetos() {
        Texture cacto = new Texture("world/mundo_areia/Tilesets/obj/cacto.png");
        List<InfoGeraObjeto> infoGeraObjetoList = new ArrayList<>();

        infoGeraObjetoList.add(new InfoGeraObjeto("Obj", "cacto", RoomType.INIMIGOS, cacto, 6, 2,
            -245, -235, 15, 10, 10,48));

        return infoGeraObjetoList;
    }

    private void cactoDano() {
        if (validar.validarLista(listaObjetos)) {
            for (ObjetoGerado obj : listaObjetos) {
                if (obj.nomeObjeto.equals("cacto")) {
                    if (obj.areaToque.overlaps(player.getHitBox())) {
                        player.dano(1);
                    }
                }
            }
        }
    }

    @Override
    public String getWorldName() {
        return "MundoAreia";
    }

    @Override
    public List<EnemyTemplate> getInimigosModelo() {
        EnemyTemplate enemySkeleton = new Skeleton(0, 0, false, player);
        return List.of(enemySkeleton);
    }

    @Override
    public List<EnemyTemplate> getBossesModelo() {
        return List.of();
    }

    @Override
    public boolean deveCarregarMapaCompleto() {
        return true;
    }

    @Override
    public void render(float delta) {
        try {
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

            if (player != null && !player.isMorto()) {
                super.render(delta);
                renderizar.atualizarListaInimigos(delta,listaInimigos);
                renderizar.renderizarInimigos(this);

                cactoDano();

                renderizar.renderizarObjects(this);
                renderizar.hitBoxListObjeto(listaObjetos, shapeRenderer, player.getCamera().combined);
                podeEntrarPorta = !validar.validarLista(listaInimigos);
                danoTiro.darDanoListaInimigos(this);

            }
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro em render: " + e.getMessage(), e);
        }
    }

    @Override
    protected void renderLayers() {
        if (map == null || currentRoom == null) {
            Gdx.app.error("MundoAreia", "renderLayers - map ou currentRoom é null\nMap: " + map + "\nCurrentRoom: " + currentRoom);
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
            if (podeEntrarPorta) {
                if (currentRoom.hasNorth() && map.getLayers().get("PortaCima") != null) {
                    layers.add("PortaCima");
                }
                if (currentRoom.hasWest() && map.getLayers().get("PortaEsquerda") != null) {
                    layers.add("PortaEsquerda");
                }
                if (currentRoom.hasEast() && map.getLayers().get("PortaDireita") != null) {
                    layers.add("PortaDireita");
                }
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
            if (podeEntrarPorta) {
                if (currentRoom.getType() != RoomType.SPAWN && currentRoom.hasSouth() && map.getLayers().get("PortaBaixo") != null) {
                    layers.add("PortaBaixo");
                }
            }
            layerRenderer.renderLayers(layers.toArray(new String[0]), player.getCamera());

            if (shapeRenderer != null && !shapeRenderer.isDrawing()) {
                if (desenharMiniMapa != null && roomGenerator != null && player != null) {
                    desenharMiniMapa.desenharMiniMapa(this);
                }
            }
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro em renderLayersUp: " + e.getMessage(), e);
        }
    }

    @Override
    public String getMapPath() {
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


    @Override
    protected void checkPortals() {
        carregarPortas.carregar(this);

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
