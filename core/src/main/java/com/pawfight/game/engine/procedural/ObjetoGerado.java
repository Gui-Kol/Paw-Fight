package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class ObjetoGerado {
    public Texture textura;
    public String nomeObjeto;
    public Rectangle hitbox;
    public Rectangle areaToque;
    public int x, y, tamanhoPx;

    public ObjetoGerado(Texture textura, String nomeObjeto,Rectangle hitbox, int x, int y, int areaToque, int tamanhoPx) {
        this.nomeObjeto = nomeObjeto;
        this.textura = textura;
        this.hitbox = hitbox;
        this.tamanhoPx = tamanhoPx;
        this.x = x;
        this.y = y;
        int ajuste = areaToque / 2;
        this.areaToque = new Rectangle(hitbox.x - ajuste, hitbox.y - ajuste, hitbox.width + areaToque, hitbox.height + areaToque);
    }
}
