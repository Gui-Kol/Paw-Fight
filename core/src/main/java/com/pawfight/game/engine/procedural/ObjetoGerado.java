package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class ObjetoGerado {
    public Texture textura;
    public Rectangle hitbox;
    public int x, y;

    public ObjetoGerado(Texture textura, Rectangle hitbox, int x, int y) {
        this.textura = textura;
        this.hitbox = hitbox;
        this.x = x;
        this.y = y;
    }
}
