package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.fisica.TilemapHitboxFactory;
import com.pawfight.game.engine.procedural.sala.InfoGeraObjeto;
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

                // Gera objetos apenas se o tipo da sala combina com o tipo do objeto
                if (world.getCurrentRoom().getType() == infoObj.typeRoom()) {
                    objetosGerados.addAll(gerarObj(infoObj, regiaoSpawn));
                }
            }
            for (ObjetoGerado obj : objetosGerados) {
                hitBoxes.add(obj.getHitbox());
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
