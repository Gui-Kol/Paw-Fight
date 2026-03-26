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

import static com.pawfight.game.engine.CommunVariable.HITBOX_ISVISIBLE;

public class Renderizar {
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
        for (EnemyTemplate enemy : world.getListaInimigos()) {
            enemy.draw(world.getBatch(), world.getShapeRenderer());
        }
    }

    //Objeto
    public void renderizarObjects(WorldTemplate world) {
        if (world.getListaObjetos() == null || world.getListaObjetos().isEmpty()) {
            return;
        }
        world.getBatch().setProjectionMatrix(world.getPlayer().getCamera().combined);
        world.getBatch().begin();
        for (ObjetoGerado objeto : world.getListaObjetos()) {
            world.getBatch().draw(objeto.textura, objeto.x, objeto.y,
                objeto.tamanhoPx, objeto.tamanhoPx);
        }
        world.getBatch().end();
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
                shapeRenderer.rect(obj.hitbox.x, obj.hitbox.y, obj.hitbox.width, obj.hitbox.height);
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.rect(obj.areaToque.x, obj.areaToque.y, obj.areaToque.width, obj.areaToque.height);
                shapeRenderer.end();
            }
        }
    }


}
