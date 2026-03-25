package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.phisics.TilemapHitboxFactory;
import com.pawfight.game.engine.procedural.room.InfoGeraObjeto;
import com.pawfight.game.engine.procedural.room.RoomType;
import com.pawfight.game.world.WorldTemplate;

import java.util.ArrayList;
import java.util.List;

public class GerarObjetos {
    public GerarObjetos() {
    }

    public void gerar(WorldTemplate world, List<InfoGeraObjeto> infoObjs) {
        try {
            world.getListaObjetos().clear();
            world.getListaObjetosHitbox().clear();
            TilemapHitboxFactory tilemapHitboxFactory = world.getTilemapHitboxFactory();
            List<ObjetoGerado> objetosGerados = new ArrayList<>();
            List<Rectangle> hitBoxes = new ArrayList<>();

            for (InfoGeraObjeto infoObj : infoObjs) {
                List<Rectangle> regiaoSpawn = tilemapHitboxFactory.createHitboxes(world.getMap(), infoObj.layerName());
                if (regiaoSpawn == null) {
                    Gdx.app.error(world.getWorldName(), "não existe região para gerar objetos!");
                    return;
                }

                switch (world.getCurrentRoom().getType()) {
                    case BOSS -> {
                        if (infoObj.typeRoom().equals(RoomType.BOSS)) {
                            objetosGerados.addAll(gerarObj(infoObj, regiaoSpawn));
                        }
                    }
                    case INIMIGOS -> {
                        if (infoObj.typeRoom().equals(RoomType.INIMIGOS)) {
                            objetosGerados.addAll(gerarObj(infoObj, regiaoSpawn));
                        }
                    }
                    case INIMIGOS_FORTES -> {
                        if (infoObj.typeRoom().equals(RoomType.INIMIGOS_FORTES)) {
                            objetosGerados.addAll(gerarObj(infoObj, regiaoSpawn));
                        }
                    }
                    case TESOURO -> {
                        if (infoObj.typeRoom().equals(RoomType.TESOURO)) {
                            objetosGerados.addAll(gerarObj(infoObj, regiaoSpawn));
                        }
                    }
                    case SPAWN -> {
                        if (infoObj.typeRoom().equals(RoomType.SPAWN)) {
                            objetosGerados.addAll(gerarObj(infoObj, regiaoSpawn));
                        }
                    }
                }
            }
            for (ObjetoGerado obj : objetosGerados) {
                hitBoxes.add(obj.hitbox);
            }

            world.addListaObjetosHitbox(hitBoxes);
            world.addListaObjetos(objetosGerados);
            world.getPlayer().adicionarColisao(world.getListaObjetosHitbox());

        } catch (Exception e) {
            Gdx.app.error(world.getWorldName(), "Erro ao gerar objetos: " + e.getMessage(), e);
        }
    }

    private List<ObjetoGerado> gerarObj(InfoGeraObjeto info, List<Rectangle> regioesSpawn) {
        List<ObjetoGerado> novosObjetos = new ArrayList<>();

        int quantidade = info.qntMin() + (int) (Math.random() * (info.qntMax() - info.qntMin() + 1));

        while (novosObjetos.size() < quantidade) {
            // Escolhe uma região aleatória da lista
            Rectangle regiao = regioesSpawn.get((int) (Math.random() * regioesSpawn.size()));

            int x = (int) (regiao.x + Math.random() * regiao.width);
            int y = (int) (regiao.y + Math.random() * regiao.height);

            // Cria uma hitbox baseada na posição
            Rectangle novaHitbox = new Rectangle(x + info.ajusteXHitBox(), y + info.ajusteYHitBox(), info.texture().getWidth() + info.ajusteLarguraHitBox(), info.texture().getHeight() + info.ajusteAlturaHitBox());

            ObjetoGerado obj = new ObjetoGerado(info.texture(), info.nomeObjeto(), novaHitbox, x, y, info.areaToque(), info.tamanhoPx());
            novosObjetos.add(obj);
        }

        return novosObjetos;
    }

}
