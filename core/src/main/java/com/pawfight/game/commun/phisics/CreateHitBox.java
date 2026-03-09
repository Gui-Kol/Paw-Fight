package com.pawfight.game.commun.phisics;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.commun.font.FontFactory;

import static com.pawfight.game.commun.CommunVariable.HITBOX_ISVISIBLE;

public class CreateHitBox {
    public BitmapFont font = FontFactory.createCustomFont("fonts/PixelOperator8-Bold.ttf", 20);

    public Rectangle create(int x, int y, int largura, int altura) {
        return new Rectangle(x, y, largura, altura);
    }

    public void drawHitBox(ShapeRenderer shapeRenderer, Rectangle hitbox) {
        if (HITBOX_ISVISIBLE) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
            shapeRenderer.end();
        }
    }
}
