package com.pawfight.game.engine.phisics;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

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
}
