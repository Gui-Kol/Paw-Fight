package com.pawfight.game.engine.fisica;

import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public class ChecarColisao {

    private static final Rectangle tempX = new Rectangle();
    private static final Rectangle tempY = new Rectangle();

    private static final int MAX_PUSHOUT_ITERACOES = 4;

    private static final float MARGEM_PUSHOUT = 1.0f;

    public static boolean houveColisao(Rectangle playerHitbox, List<Rectangle> colisores) {
        for (Rectangle colisor : colisores) {
            if (playerHitbox.overlaps(colisor)) {
                return true;
            }
        }
        return false;
    }

    public static void ajustarPosicaoSeBaterParede(Rectangle playerHitbox, float nextX, float nextY, List<Rectangle> paredes) {
        tempX.set(nextX, playerHitbox.y, playerHitbox.width, playerHitbox.height);
        if (!houveColisao(tempX, paredes)) {
            playerHitbox.x = nextX;
        }

        tempY.set(playerHitbox.x, nextY, playerHitbox.width, playerHitbox.height);
        if (!houveColisao(tempY, paredes)) {
            playerHitbox.y = nextY;
        }
    }

    public static void empurrarForaParedes(Rectangle hitbox, List<Rectangle> paredes) {
        for (int iter = 0; iter < MAX_PUSHOUT_ITERACOES; iter++) {
            boolean empurrou = false;
            for (Rectangle parede : paredes) {
                if (!hitbox.overlaps(parede)) continue;

                float overlapX = Math.min(hitbox.x + hitbox.width, parede.x + parede.width)
                               - Math.max(hitbox.x, parede.x);
                float overlapY = Math.min(hitbox.y + hitbox.height, parede.y + parede.height)
                               - Math.max(hitbox.y, parede.y);

                if (overlapX <= 0 || overlapY <= 0) continue;

                if (overlapX < overlapY) {
                    float centroHitbox = hitbox.x + hitbox.width / 2f;
                    float centroParede = parede.x + parede.width / 2f;
                    hitbox.x += (centroHitbox < centroParede)
                        ? -(overlapX + MARGEM_PUSHOUT)
                        : (overlapX + MARGEM_PUSHOUT);
                } else {
                    float centroHitbox = hitbox.y + hitbox.height / 2f;
                    float centroParede = parede.y + parede.height / 2f;
                    hitbox.y += (centroHitbox < centroParede)
                        ? -(overlapY + MARGEM_PUSHOUT)
                        : (overlapY + MARGEM_PUSHOUT);
                }
                empurrou = true;
            }
            if (!empurrou) break;
        }
    }

    public void checarColisaoSeparadoEixo(List<Rectangle> colisor, PlayerTemplate player) {
        Rectangle hitBox = player.getHitBox();
        float preX = hitBox.x;
        float preY = hitBox.y;
        empurrarForaParedes(hitBox, colisor);

        float pushDx = hitBox.x - preX;
        float pushDy = hitBox.y - preY;
        float adjustedNextX = player.getNextX() + pushDx;
        float adjustedNextY = player.getNextY() + pushDy;

        ajustarPosicaoSeBaterParede(hitBox, adjustedNextX, adjustedNextY, colisor);

        int offsetX = player.isOlhandoEsquerda()
            ? -(player.getHitboxOffsetX())
            : player.getHitboxOffsetX();

        player.setDx((int) (hitBox.x - (player.getTamanho() - player.getHitboxSize()) / 2f - offsetX));
        player.setDy((int) (hitBox.y - player.getHitboxOffsetY()));
    }


}
