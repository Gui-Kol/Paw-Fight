package com.pawfight.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.engine.procedural.ObjetoGerado;
import com.pawfight.game.engine.procedural.sala.InfoGeraObjeto;
import com.pawfight.game.engine.procedural.sala.Sala;
import com.pawfight.game.engine.procedural.sala.TipoSala;
import com.pawfight.game.entity.enemy.Skeleton;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.template.WorldTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.pawfight.game.engine.Validar.validarLista;

public class MundoAreia extends WorldTemplate {

    private Texture cactoTexture;
    private final List<String> layerBuffer = new ArrayList<>();
    private final List<String> layerUpBuffer = new ArrayList<>();
    private String[] layerArray = new String[8]; // pré-alocado, realocado se necessário

    public MundoAreia(PawFight game, PlayerTemplate player, OrthographicCamera camera, Viewport viewport) {
        super(game, "menu/menu.png", "audio/music/mundoAreia.wav", camera, viewport);
        Gdx.app.log("MundoAreia", "Iniciando Mundo...");
        setPlayer(player);

        player.setLocal(500, 100);
        player.setPodeAtacar(true);

        // Gera salas APENAS aqui, não carrega mapa
        try {
            roomManager.getRoomGenerator().gerarRooms(this);
            if (roomManager.getCurrentRoom() == null) {
                throw new RuntimeException("Erro: currentRoom não foi inicializado.");
            }
            Gdx.app.log("MundoAreia", "Salas geradas com sucesso no construtor. Mapa será carregado em show().");
            logRoomInfo(roomManager.getCurrentRoom());
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro ao gerar salas: " + e.getMessage(), e);
            errorFinal = true;
        }
    }

    @Override
    public void logRoomInfo(Sala sala) {
        if (sala == null) {
            Gdx.app.error("MundoAreia", "Sala é null em logRoomInfo!");
            return;
        }
        Sala currentRoom = roomManager.getCurrentRoom();
        if (!currentRoom.hasEast() && !currentRoom.hasWest() && !currentRoom.hasNorth() && !currentRoom.hasSouth()) {
            Gdx.app.error("MundoAreia", "Sala não tem portas.");
        } else {
            Gdx.app.log("MundoAreia", "Sala [" + sala.getX() + "," + sala.getY() + "] Type: " + sala.getType());
            Gdx.app.log("MundoAreia", "hasNorth: " + sala.hasNorth() + ", hasSouth: " + sala.hasSouth() +
                ", hasEast: " + sala.hasEast() + ", hasWest: " + sala.hasWest());
        }
    }


    @Override
    public List<InfoGeraObjeto> getInfoObjetos() {
        if (cactoTexture == null) {
            cactoTexture = Assets.get("world/mundo_areia/Tilesets/obj/cacto.png", Texture.class);
        }
        List<InfoGeraObjeto> infoGeraObjetoList = new ArrayList<>();

        infoGeraObjetoList.add(new InfoGeraObjeto("Obj", "cacto", TipoSala.INIMIGOS, cactoTexture, 6, 2,
            -245, -235, 15, 10, 10,48));

        return infoGeraObjetoList;
    }

    private void cactoDano() {
        List<ObjetoGerado> listaObjetos = worldRenderer.getListaObjetos();
        if (validarLista(listaObjetos)) {
            for (ObjetoGerado obj : listaObjetos) {
                if (obj.getNomeObjeto().equals("cacto")) {
                    if (obj.getAreaToque().overlaps(player.getHitBox())) {
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
                // Transição gerenciada pelo ScreenManager — renderizada em PawFight.render()
                return;
            }

            Sala currentRoom = roomManager.getCurrentRoom();
            if (currentRoom == null || map == null || renderizadorCamada == null) {
                Gdx.app.error("MundoAreia", "render() - objeto null: currentRoom=" + (currentRoom == null) +
                    ", map=" + (map == null) + ", RenderizadorCamada=" + (renderizadorCamada == null));
                return;
            }

            if (player != null) {
                super.render(delta);

                if (!player.isMorto()) {
                    cactoDano();
                    roomManager.setPodeEntrarPorta(!validarLista(enemyManager.getListaInimigos()));
                }
            }
        } catch (Exception e) {
            // Log original PRIMEIRO — para não perder o erro real
            Gdx.app.error("MundoAreia", "Erro em render: " + e.getMessage(), e);

            // Cleanup seguro — não pode lançar exceção, senão esconde o erro original
            try {
                if (batch != null && batch.isDrawing()) batch.end();
            } catch (Exception ignored) { }
            try {
                var shapeRenderer = worldRenderer.getShapeRenderer();
                if (shapeRenderer != null && shapeRenderer.isDrawing()) shapeRenderer.end();
            } catch (Exception ignored) { }
        }
    }

    @Override
    protected void renderLayers() {
        Sala currentRoom = roomManager.getCurrentRoom();
        if (map == null || currentRoom == null) {
            Gdx.app.error("MundoAreia", "renderLayers - map ou currentRoom é null\nMap: " + map + "\nCurrentRoom: " + currentRoom);
            return;
        }
        try {
            layerBuffer.clear();
            String[] baseLayers = {"Sub", "Solo", "ParedeLayer"};
            for (String layer : baseLayers) {
                if (map.getLayers().get(layer) != null) {
                    layerBuffer.add(layer);
                }
            }
            boolean podeEntrarPorta = roomManager.isPodeEntrarPorta();
            if (podeEntrarPorta) {
                if (currentRoom.hasNorth() && map.getLayers().get("PortaCima") != null) {
                    layerBuffer.add("PortaCima");
                }
                if (currentRoom.hasWest() && map.getLayers().get("PortaEsquerda") != null) {
                    layerBuffer.add("PortaEsquerda");
                }
                if (currentRoom.hasEast() && map.getLayers().get("PortaDireita") != null) {
                    layerBuffer.add("PortaDireita");
                }
            }
            renderizadorCamada.renderLayers(toArray(layerBuffer), player.getCamera());
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro em renderLayers: " + e.getMessage(), e);
        }
    }

    @Override
    protected void renderLayersUp() {
        Sala currentRoom = roomManager.getCurrentRoom();
        if (map == null || currentRoom == null) {
            Gdx.app.error("MundoAreia", "renderLayersUp - map ou currentRoom é null");
            return;
        }
        try {
            layerUpBuffer.clear();
            if (map.getLayers().get("Up") != null) {
                layerUpBuffer.add("Up");
            }
            boolean podeEntrarPorta = roomManager.isPodeEntrarPorta();
            if (podeEntrarPorta) {
                if (currentRoom.getType() != TipoSala.SPAWN && currentRoom.hasSouth() && map.getLayers().get("PortaBaixo") != null) {
                    layerUpBuffer.add("PortaBaixo");
                }
            }
            renderizadorCamada.renderLayers(toArray(layerUpBuffer), player.getCamera());

            var shapeRenderer = worldRenderer.getShapeRenderer();
            if (shapeRenderer != null && !shapeRenderer.isDrawing()) {
                var desenharMiniMapa = roomManager.getDesenharMiniMapa();
                var geradorSalas = roomManager.getRoomGenerator();
                if (desenharMiniMapa != null && geradorSalas != null && player != null) {
                    desenharMiniMapa.desenharMiniMapa(this);
                }
            }
        } catch (Exception e) {
            Gdx.app.error("MundoAreia", "Erro em renderLayersUp: " + e.getMessage(), e);
        }
    }

    @Override
    public String getMapPath() {
        Sala currentRoom = roomManager.getCurrentRoom();
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
        roomManager.getCarregarPortas().carregar(this);
    }

    // Reutiliza o layerArray para evitar alocação de String[] a cada frame.
    private String[] toArray(List<String> list) {
        int size = list.size();
        if (layerArray.length < size) {
            layerArray = new String[size];
        }
        for (int i = 0; i < size; i++) {
            layerArray[i] = list.get(i);
        }
        return size == layerArray.length ? layerArray : java.util.Arrays.copyOf(layerArray, size);
    }

    @Override
    public void dispose() {
        super.dispose();
        // Transição é gerenciada pelo ScreenManager — NÃO dar dispose aqui
        enemyManager.clearInimigos();
    }
}
