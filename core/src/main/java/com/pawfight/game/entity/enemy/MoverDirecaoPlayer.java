package com.pawfight.game.entity.enemy;

import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.player.PlayerTemplate;

public class MoverDirecaoPlayer {
    private final Rectangle tempHitBox = new Rectangle();

    public void mover(EnemyTemplate enemy) {
        PlayerTemplate player = enemy.player;
        if (player == null) return;

        int HITBOX_SIZE = enemy.HITBOX_SIZE;
        int TAMANHO_PX = enemy.getTamanho();
        int HITBOX_OFFSET_X = enemy.HITBOX_OFFSET_X;
        int HITBOX_OFFSET_Y = enemy.HITBOX_OFFSET_Y;
        float delta = com.badlogic.gdx.Gdx.graphics.getDeltaTime();

        float playerX = player.getDx();
        float playerY = player.getDy();

        float deltaX = playerX - enemy.dx;
        float deltaY = playerY - enemy.dy;
        float distanciaTotal = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distanciaTotal > 0) {
            float moveX = (deltaX / distanciaTotal) * enemy.getVelocidade() * delta;
            float moveY = (deltaY / distanciaTotal) * enemy.getVelocidade() * delta;

            float newDx = enemy.dx + moveX;
            float newDy = enemy.dy + moveY;
            tempHitBox.set(
                newDx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X,
                newDy + HITBOX_OFFSET_Y,
                HITBOX_SIZE,
                HITBOX_SIZE
            );

            boolean canMove = true;
            if (enemy.enemiesList != null) {
                for (EnemyTemplate other : enemy.enemiesList) {
                    if (other != enemy && tempHitBox.overlaps(other.getHitBox())) {
                        canMove = false;
                        break;
                    }
                }
            }

            if (canMove) {
                enemy.dx = (int) newDx;
                enemy.dy = (int) newDy;
                enemy.olhandoEsquerda = deltaX < 0;
                enemy.moving = true;
            } else {
                enemy.moving = false;
            }

            enemy.hitBox.setPosition(
                enemy.dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X,
                enemy.dy + HITBOX_OFFSET_Y
            );
        } else {
            enemy.moving = false;
        }
    }
}
