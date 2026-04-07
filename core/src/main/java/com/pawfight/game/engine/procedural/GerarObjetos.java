package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.fisica.TilemapHitboxFactory;
import com.pawfight.game.engine.procedural.sala.InfoGeraObjeto;
import com.pawfight.game.world.template.WorldTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.math.MathUtils.random;

public class GerarObjetos {
    public GerarObjetos() {
    }

    public void gerar(WorldTemplate world, List<InfoGeraObjeto> infoObjs) {
        try {
            world.getWorldRenderer().getListaObjetos().clear();
            world.getWorldRenderer().getListaObjetosHitbox().clear();
            TilemapHitboxFactory tilemapHitboxFactory = world.getWorldPhysics().getTilemapHitboxFactory();
            List<ObjetoGerado> objetosGerados = new ArrayList<>();
            List<Rectangle> hitBoxes = new ArrayList<>();

            for (InfoGeraObjeto infoObj : infoObjs) {
                List<Rectangle> regiaoSpawn = tilemapHitboxFactory.createHitboxes(world.getMap(), infoObj.layerName());
                if (regiaoSpawn == null) {
                    Gdx.app.error(world.getWorldName(), "não existe região para gerar objetos!");
                    return;
                }

                // Gera objetos apenas se o tipo da sala combina com o tipo do objeto
                if (world.getRoomManager().getCurrentRoom().getType() == infoObj.typeRoom()) {
                    objetosGerados.addAll(gerarObj(infoObj, regiaoSpawn));
                }
            }
            for (ObjetoGerado obj : objetosGerados) {
                hitBoxes.add(obj.getHitbox());
            }

            world.getWorldRenderer().addListaObjetosHitbox(hitBoxes);
            world.getWorldRenderer().addListaObjetos(objetosGerados);
            world.getPlayer().adicionarColisao(world.getWorldRenderer().getListaObjetosHitbox());

        } catch (Exception e) {
            Gdx.app.error(world.getWorldName(), "Erro ao gerar objetos: " + e.getMessage(), e);
        }
    }

    private List<ObjetoGerado> gerarObj(InfoGeraObjeto info, List<Rectangle> regioesSpawn) {
        List<ObjetoGerado> novosObjetos = new ArrayList<>();

        int quantidade = info.qntMin() + random.nextInt(info.qntMax() - info.qntMin() + 1);

        while (novosObjetos.size() < quantidade) {
            // Escolhe uma região aleatória da lista
            Rectangle regiao = regioesSpawn.get((random.nextInt(regioesSpawn.size())));

            int x = random((int) regiao.x, (int) (regiao.x + regiao.width));
            int y = random((int) regiao.y, (int) (regiao.y + regiao.height));

            // Cria uma hitbox baseada na posição
            Rectangle novaHitbox = new Rectangle(x + info.ajusteXHitBox(), y + info.ajusteYHitBox(), info.texture().getWidth() + info.ajusteLarguraHitBox(), info.texture().getHeight() + info.ajusteAlturaHitBox());

            ObjetoGerado obj = new ObjetoGerado(info.texture(), info.nomeObjeto(), novaHitbox, x, y, info.areaToque(), info.tamanhoPx());
            novosObjetos.add(obj);
        }

        return novosObjetos;
    }

}
