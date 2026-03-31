package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class ObjetoGerado {
    private final Texture textura;
    private final String nomeObjeto;
    private final Rectangle hitbox;
    private final Rectangle areaToque;
    private final int x, y, tamanhoPx;

    public ObjetoGerado(Texture textura, String nomeObjeto, Rectangle hitbox, int x, int y, int areaToque, int tamanhoPx) {
        this.nomeObjeto = nomeObjeto;
        this.textura = textura;
        this.hitbox = hitbox;
        this.tamanhoPx = tamanhoPx;
        this.x = x;
        this.y = y;
        int ajuste = areaToque / 2;
        this.areaToque = new Rectangle(hitbox.x - ajuste, hitbox.y - ajuste, hitbox.width + areaToque, hitbox.height + areaToque);
    }

    public Texture getTextura() {
        return textura;
    }

    public String getNomeObjeto() {
        return nomeObjeto;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public Rectangle getAreaToque() {
        return areaToque;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTamanhoPx() {
        return tamanhoPx;
    }
}
