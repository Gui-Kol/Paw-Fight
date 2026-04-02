package com.pawfight.game.engine.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.procedural.ObjetoGerado;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.world.WorldTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.pawfight.game.engine.VariavelComum.HITBOX_ISVISIBLE;

public class Renderizar {
    public static final Renderizar INSTANCE = new Renderizar();

    //Inimigo
    public void atualizarListaInimigos(float delta, List<EnemyTemplate> listaInimigos) {
        List<EnemyTemplate> inimigosMortos = new ArrayList<>();
        for (EnemyTemplate enemy : listaInimigos) {
            enemy.update(delta);
            if (enemy.isMorto()) {
                inimigosMortos.add(enemy);
            }
        }
        listaInimigos.removeAll(inimigosMortos);
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

        // Hitboxes e extras — fora do batch
        shapeRenderer.setProjectionMatrix(cameraCombined);
        for (EnemyTemplate enemy : inimigos) {
            enemy.drawHitbox(batch, shapeRenderer);
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

    //HitBox
    public void hitboxDraw(ShapeRenderer shapeRenderer, Rectangle hitbox) {
        if (HITBOX_ISVISIBLE) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
            shapeRenderer.end();
        }
    }
    public void hitBoxDrawList(List<Rectangle> hitboxes, ShapeRenderer shapeRenderer, Matrix4 cameraMatrix) {
        if (HITBOX_ISVISIBLE) {
            shapeRenderer.setProjectionMatrix(cameraMatrix);
            for (Rectangle hitbox : hitboxes) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
                shapeRenderer.end();
            }
        }
    }
    public void hitBoxListObjeto(List<ObjetoGerado> objetoGerados, ShapeRenderer shapeRenderer, Matrix4 cameraMatrix) {
        if (HITBOX_ISVISIBLE) {
            shapeRenderer.setProjectionMatrix(cameraMatrix);
            for (ObjetoGerado obj : objetoGerados) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(Color.GREEN);
                shapeRenderer.rect(obj.getHitbox().x, obj.getHitbox().y, obj.getHitbox().width, obj.getHitbox().height);
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.rect(obj.getAreaToque().x, obj.getAreaToque().y, obj.getAreaToque().width, obj.getAreaToque().height);
                shapeRenderer.end();
            }
        }
    }


}
