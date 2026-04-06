package com.pawfight.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.Assets;
import com.pawfight.game.engine.Validar;
import com.pawfight.game.engine.procedural.ObjetoGerado;
import com.pawfight.game.engine.procedural.sala.InfoGeraObjeto;
import com.pawfight.game.engine.procedural.sala.Sala;
import com.pawfight.game.engine.procedural.sala.GeradorSalas;
import com.pawfight.game.engine.procedural.sala.TipoSala;
import com.pawfight.game.entity.enemy.Skeleton;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.pawfight.game.engine.Validar.validarLista;

public class MundoAreia extends WorldTemplate {

    private Texture cactoTexture;

    public MundoAreia(PawFight game, PlayerTemplate player, OrthographicCamera camera, Viewport viewport) {
        super(game, "menu/menu.png", "audio/music/mundoAreia.wav", camera, viewport);
        Gdx.app.log("MundoAreia", "Iniciando Mundo...");
        GeradorSalas = new GeradorSalas();
        setPlayer(player);

        // Reseta estado do player
        player.setLocal(500, 100);
        player.setPodeAtacar(true);

        // Gera salas APENAS aqui, não carrega mapa
        try {
            GeradorSalas.gerarRooms(this);
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
    public void logRoomInfo(Sala Sala) {
        if (Sala == null) {
            Gdx.app.error("MundoAreia", "Sala é null em logRoomInfo!");
            return;
        }
        if (!currentRoom.hasEast() && !currentRoom.hasWest() && !currentRoom.hasNorth() && !currentRoom.hasSouth()) {
            Gdx.app.error("MundoAreia", "Sala não tem portas.");
        } else {
            Gdx.app.log("MundoAreia", "Sala [" + Sala.getX() + "," + Sala.getY() + "] Type: " + Sala.getType());
            Gdx.app.log("MundoAreia", "hasNorth: " + Sala.hasNorth() + ", hasSouth: " + Sala.hasSouth() +
                ", hasEast: " + Sala.hasEast() + ", hasWest: " + Sala.hasWest());
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
                TransicaoTela.update(Gdx.graphics.getDeltaTime());
                TransicaoTela.render(batch);
                return;
            }

            if (currentRoom == null || map == null || RenderizadorCamada == null) {
                Gdx.app.error("MundoAreia", "render() - objeto null: currentRoom=" + (currentRoom == null) +
                    ", map=" + (map == null) + ", RenderizadorCamada=" + (RenderizadorCamada == null));
                return;
            }

            if (player != null && !player.isMorto()) {
                super.render(delta);
                cactoDano();

                podeEntrarPorta = !validarLista(listaInimigos);
            }
        } catch (Exception e) {
            // Log original PRIMEIRO — para não perder o erro real
            Gdx.app.error("MundoAreia", "Erro em render: " + e.getMessage(), e);

            // Cleanup seguro — não pode lançar exceção, senão esconde o erro original
            try {
                if (batch != null && batch.isDrawing()) batch.end();
            } catch (Exception ignored) { }
            try {
                if (shapeRenderer != null && shapeRenderer.isDrawing()) shapeRenderer.end();
            } catch (Exception ignored) { }
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
            RenderizadorCamada.renderLayers(layers.toArray(new String[0]), player.getCamera());
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
                if (currentRoom.getType() != TipoSala.SPAWN && currentRoom.hasSouth() && map.getLayers().get("PortaBaixo") != null) {
                    layers.add("PortaBaixo");
                }
            }
            RenderizadorCamada.renderLayers(layers.toArray(new String[0]), player.getCamera());

            if (shapeRenderer != null && !shapeRenderer.isDrawing()) {
                if (desenharMiniMapa != null && GeradorSalas != null && player != null) {
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
        if (TransicaoTela != null) {
            TransicaoTela.dispose();
        }
        listaInimigos.clear();
    }
}
