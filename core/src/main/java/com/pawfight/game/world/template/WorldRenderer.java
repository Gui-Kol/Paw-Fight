package com.pawfight.game.world.template;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.engine.procedural.GerarObjetos;
import com.pawfight.game.engine.procedural.ObjetoGerado;
import com.pawfight.game.engine.procedural.sala.InfoGeraObjeto;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.entity.enemy.EnemyTemplate;

import java.util.ArrayList;
import java.util.List;

public class WorldRenderer {

    private final Renderizar renderizar;
    private final ShapeRenderer shapeRenderer;
    private final Texture background;
    private final GerarObjetos gerarObjetos;
    private final List<ObjetoGerado> listaObjetos;
    private final List<Rectangle> listaObjetosHitbox;

    public WorldRenderer(String backgroundPath) {
        this(new ShapeRenderer(), Assets.get(backgroundPath, Texture.class), Renderizar.INSTANCE);
    }

    WorldRenderer(ShapeRenderer shapeRenderer, Texture background, Renderizar renderizar) {
        this.shapeRenderer = shapeRenderer;
        this.background = background;
        this.renderizar = renderizar;
        this.gerarObjetos = new GerarObjetos();
        this.listaObjetos = new ArrayList<>();
        this.listaObjetosHitbox = new ArrayList<>();
    }

    public void renderizarInimigos(WorldTemplate world) {
        List<EnemyTemplate> inimigos = world.getEnemyManager().getListaInimigos();
        if (inimigos != null && !inimigos.isEmpty()) {
            renderizar.renderizarInimigos(world);
        }
    }

    public void renderizarObjects(WorldTemplate world) {
        renderizar.renderizarObjects(world);
        renderizar.hitBoxListObjeto(listaObjetos, shapeRenderer, world.getPlayer().getCamera().combined);
    }

    public void gerarObjetos(WorldTemplate world, List<InfoGeraObjeto> infoObjetos) {
        if (infoObjetos == null || infoObjetos.isEmpty()) {
            return;
        }
        gerarObjetos.gerar(world, infoObjetos);
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public Texture getBackground() {
        return background;
    }

    public GerarObjetos getGerarObjetos() {
        return gerarObjetos;
    }

    public List<ObjetoGerado> getListaObjetos() {
        return listaObjetos;
    }

    public List<Rectangle> getListaObjetosHitbox() {
        return listaObjetosHitbox;
    }

    public void addListaObjetos(List<ObjetoGerado> objetoGerados) {
        listaObjetos.addAll(objetoGerados);
    }

    public void addListaObjetosHitbox(List<Rectangle> hitBoxs) {
        listaObjetosHitbox.addAll(hitBoxs);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
