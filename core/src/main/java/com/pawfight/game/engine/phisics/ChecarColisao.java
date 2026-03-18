package com.pawfight.game.engine.phisics;

import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.player.PlayerTemplate;

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

    public static void ajustarPosicaoSeBaterParede(Rectangle playerHitbox, float nextX, float nextY, List<Rectangle> paredes) {
        Rectangle nextHitboxX = new Rectangle(nextX, playerHitbox.y, playerHitbox.width, playerHitbox.height);
        if (!houveColisao(nextHitboxX, paredes)) {
            playerHitbox.x = nextX;
        }

        Rectangle nextHitboxY = new Rectangle(playerHitbox.x, nextY, playerHitbox.width, playerHitbox.height);
        if (!houveColisao(nextHitboxY, paredes)) {
            playerHitbox.y = nextY;
        }
    }
    public void checarColisaoSeparadoEixo(List<Rectangle> colisor, PlayerTemplate player) {
        ajustarPosicaoSeBaterParede(player.getHitBox(), player.getNextX(), player.getNextY(), colisor);

        Rectangle hitBox = player.getHitBox();

        int offsetX = player.isOlhandoEsquerda()
            ? -(PlayerTemplate.getHitboxOffsetX())
            : PlayerTemplate.getHitboxOffsetX();

        player.setDx((int) (hitBox.x - (player.getTamanhoPx() - PlayerTemplate.getHitboxSize()) / 2f - offsetX));
        player.setDy((int) (hitBox.y - PlayerTemplate.getHitboxOffsetY()));
    }



}
