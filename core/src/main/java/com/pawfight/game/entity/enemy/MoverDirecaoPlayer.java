package com.pawfight.game.entity.enemy;

import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

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

        if (distanciaTotal <= 0) {
            enemy.moving = false;
            return;
        }

        float moveX = (deltaX / distanciaTotal) * enemy.getVelocidade() * delta;
        float moveY = (deltaY / distanciaTotal) * enemy.getVelocidade() * delta;

        // Direção visual que o inimigo vai olhar
        boolean willFaceLeft = deltaX < 0;
        int offsetX = willFaceLeft ? -HITBOX_OFFSET_X : HITBOX_OFFSET_X;
        boolean moved = false;

        // ── Eixo X ──────────────────────────────────────────────
        float newDx = enemy.dx + moveX;
        tempHitBox.set(
            newDx + (TAMANHO_PX - HITBOX_SIZE) / 2f + offsetX,
            enemy.dy + HITBOX_OFFSET_Y,
            HITBOX_SIZE,
            HITBOX_SIZE
        );
        if (!colide(tempHitBox, enemy)) {
            enemy.dx = newDx;
            moved = true;
        }

        // ── Eixo Y ──────────────────────────────────────────────
        float newDy = enemy.dy + moveY;
        tempHitBox.set(
            enemy.dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + offsetX,
            newDy + HITBOX_OFFSET_Y,
            HITBOX_SIZE,
            HITBOX_SIZE
        );
        if (!colide(tempHitBox, enemy)) {
            enemy.dy = newDy;
            moved = true;
        }

        enemy.olhandoEsquerda = willFaceLeft;
        enemy.moving = moved;

        // Atualiza hitbox com posição final
        offsetX = enemy.olhandoEsquerda ? -HITBOX_OFFSET_X : HITBOX_OFFSET_X;
        enemy.hitBox.setPosition(
            enemy.dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + offsetX,
            enemy.dy + HITBOX_OFFSET_Y
        );
    }

    private boolean colide(Rectangle testHitbox, EnemyTemplate self) {
        // Verifica colisão com paredes
        List<Rectangle> paredes = self.paredesColisores;
        if (paredes != null) {
            for (int i = 0, n = paredes.size(); i < n; i++) {
                if (testHitbox.overlaps(paredes.get(i))) {
                    return true;
                }
            }
        }

        // Verifica colisão com outros inimigos
        List<EnemyTemplate> others = self.enemiesList;
        if (others != null) {
            for (EnemyTemplate other : others) {
                if (other != self && !other.isMorto() && testHitbox.overlaps(other.getHitBox())) {
                    return true;
                }
            }
        }
        return false;
    }
}
