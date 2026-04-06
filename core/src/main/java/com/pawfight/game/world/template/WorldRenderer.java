package com.pawfight.game.world.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.Assets;
import com.pawfight.game.engine.procedural.GerarObjetos;
import com.pawfight.game.engine.procedural.ObjetoGerado;
import com.pawfight.game.engine.procedural.sala.InfoGeraObjeto;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.entity.enemy.EnemyTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia renderização de objetos, inimigos e hitboxes do mundo.
 */
public class WorldRenderer {

    private final Renderizar renderizar = Renderizar.INSTANCE;
    private final ShapeRenderer shapeRenderer;
    private final Texture background;
    private final GerarObjetos gerarObjetos;
    private final List<ObjetoGerado> listaObjetos;
    private final List<Rectangle> listaObjetosHitbox;

    public WorldRenderer(String backgroundPath) {
        this.shapeRenderer = new ShapeRenderer();
        this.background = Assets.get(backgroundPath, Texture.class);
        this.gerarObjetos = new GerarObjetos();
        this.listaObjetos = new ArrayList<>();
        this.listaObjetosHitbox = new ArrayList<>();
    }

    public void renderizarInimigos(WorldTemplate world) {
        List<EnemyTemplate> inimigos = world.getEnemyManager().getListaInimigos();
        if (inimigos != null && !inimigos.isEmpty()) {
            renderizar.atualizarListaInimigos(Gdx.graphics.getDeltaTime(), inimigos);
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

    // ── Getters ──────────────────────────────────────────────────

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

