package com.pawfight.game.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.procedural.ObjetoGerado;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.world.WorldTemplate;

import java.util.List;

import static com.pawfight.game.engine.VariavelComum.HITBOX_ISVISIBLE;

public class Renderizar {
    public static final Renderizar INSTANCE = new Renderizar();

    //Inimigo
    public void atualizarListaInimigos(float delta, List<EnemyTemplate> listaInimigos) {
        for (EnemyTemplate enemy : listaInimigos) {
            enemy.update(delta);
        }
        listaInimigos.removeIf(EnemyTemplate::isMorto);
    }
    public void renderizarInimigos(WorldTemplate world) {
        List<EnemyTemplate> inimigos = world.getListaInimigos();
        if (inimigos.isEmpty()) return;

        SpriteBatch batch = world.getBatch();
        ShapeRenderer shapeRenderer = world.getShapeRenderer();
        var cameraCombined = world.getPlayer().getCamera().combined;

        // 1 único begin/end para TODOS os sprites
        batch.setProjectionMatrix(cameraCombined);
        batch.begin();
        for (EnemyTemplate enemy : inimigos) {
            enemy.drawSprite(batch);
        }
        batch.end();

        // Hitboxes — 1 único begin/end para TODAS as hitboxes de inimigos
        if (HITBOX_ISVISIBLE) {
            shapeRenderer.setProjectionMatrix(cameraCombined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (EnemyTemplate enemy : inimigos) {
                hitboxRect(shapeRenderer, enemy.getHitBox(), Color.RED);
            }
            shapeRenderer.end();
        }

        // Extras (subclasses podem usar batch/shapeRenderer livremente)
        for (EnemyTemplate enemy : inimigos) {
            enemy.extraDraw(batch, shapeRenderer);
        }
    }

    //Objeto
    public void renderizarObjects(WorldTemplate world) {
        if (world.getListaObjetos() == null || world.getListaObjetos().isEmpty()) {
            return;
        }
        SpriteBatch batch = world.getBatch();
        if (batch.isDrawing()) {
            batch.end();
        }
        batch.setProjectionMatrix(world.getPlayer().getCamera().combined);
        batch.begin();
        for (ObjetoGerado objeto : world.getListaObjetos()) {
            if (objeto.getTextura() != null) {
                batch.draw(objeto.getTextura(), objeto.getX(), objeto.getY(),
                    objeto.getTamanhoPx(), objeto.getTamanhoPx());
            }
        }
        batch.end();
    }

    // ── HitBox ──────────────────────────────────────────────────

    public void hitboxRect(ShapeRenderer shapeRenderer, Rectangle hitbox, Color color) {
        shapeRenderer.setColor(color);
        shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public void hitboxDraw(ShapeRenderer shapeRenderer, Rectangle hitbox) {
        if (!HITBOX_ISVISIBLE) return;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        hitboxRect(shapeRenderer, hitbox, Color.RED);
        shapeRenderer.end();
    }

    public void hitBoxDrawList(List<Rectangle> hitboxes, ShapeRenderer shapeRenderer, Matrix4 cameraMatrix) {
        if (!HITBOX_ISVISIBLE || hitboxes.isEmpty()) return;
        shapeRenderer.setProjectionMatrix(cameraMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Rectangle hitbox : hitboxes) {
            hitboxRect(shapeRenderer, hitbox, Color.RED);
        }
        shapeRenderer.end();
    }

    public void hitBoxListObjeto(List<ObjetoGerado> objetoGerados, ShapeRenderer shapeRenderer, Matrix4 cameraMatrix) {
        if (!HITBOX_ISVISIBLE || objetoGerados == null || objetoGerados.isEmpty()) return;
        shapeRenderer.setProjectionMatrix(cameraMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (ObjetoGerado obj : objetoGerados) {
            hitboxRect(shapeRenderer, obj.getHitbox(), Color.GREEN);
            hitboxRect(shapeRenderer, obj.getAreaToque(), Color.RED);
        }
        shapeRenderer.end();
    }


}
