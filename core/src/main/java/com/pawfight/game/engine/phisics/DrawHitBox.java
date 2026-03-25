package com.pawfight.game.engine.phisics;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.procedural.ObjetoGerado;

import java.util.List;

import static com.pawfight.game.engine.CommunVariable.HITBOX_ISVISIBLE;

public class DrawHitBox {
    public void draw(ShapeRenderer shapeRenderer, Rectangle hitbox) {
        if (HITBOX_ISVISIBLE) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
            shapeRenderer.end();
        }
    }
    public void drawList(List<Rectangle> hitboxes, ShapeRenderer shapeRenderer, Matrix4 cameraMatrix) {
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
    public void drawListObjeto(List<ObjetoGerado> objetoGerados, ShapeRenderer shapeRenderer, Matrix4 cameraMatrix) {
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
