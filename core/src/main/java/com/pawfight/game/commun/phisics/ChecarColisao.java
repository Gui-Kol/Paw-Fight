package com.pawfight.game.commun.phisics;

import com.badlogic.gdx.math.Rectangle;
import java.util.List;

public class ChecarColisao {

    public static boolean houveColisao(Rectangle playerHitbox, List<Rectangle> paredes) {
        for (Rectangle parede : paredes) {
            if (playerHitbox.overlaps(parede)) {
                return true;
            }
        }
        return false;
    }

    public static void ajustarPosicao(Rectangle playerHitbox, float nextX, float nextY, List<Rectangle> paredes) {
        // Testa colisão no eixo X
        Rectangle nextHitboxX = new Rectangle(nextX, playerHitbox.y, playerHitbox.width, playerHitbox.height);
        if (!houveColisao(nextHitboxX, paredes)) {
            playerHitbox.x = nextX;
        }

        // Testa colisão no eixo Y
        Rectangle nextHitboxY = new Rectangle(playerHitbox.x, nextY, playerHitbox.width, playerHitbox.height);
        if (!houveColisao(nextHitboxY, paredes)) {
            playerHitbox.y = nextY;
        }
    }
}
