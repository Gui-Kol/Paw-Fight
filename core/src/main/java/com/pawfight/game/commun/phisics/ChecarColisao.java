package com.pawfight.game.commun.phisics;

import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.player.Player;

import java.util.List;

public class ChecarColisao {

    public static boolean houveColisao(Rectangle playerHitbox, List<Rectangle> colisores) {
        for (Rectangle colisor : colisores) {
            if (playerHitbox.overlaps(colisor)) {
                return true;
            }
        }
        return false;
    }
                                                    //INIMIGOS
    public static void danoSeColidir(Player player, List colisores){
        if (houveColisao(player.getHitBox(),colisores)){
           // player.dano(colisores.get(1).getDano());
        }
    }

    public static void ajustarPosicaoSeBaterParede(Rectangle playerHitbox, float nextX, float nextY, List<Rectangle> paredes) {
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
