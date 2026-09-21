package com.pawfight.game.engine.fisica;

import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.enemy.EnemyTemplate;

import java.util.List;

public class ColisaoResolver {

    private static final int MAX_ITERACOES = 4;

    private static final float MARGEM = 0.5f;

    public void resolver(List<EnemyTemplate> enemies, List<Rectangle> paredes) {
        if (enemies == null) return;

        for (int iter = 0; iter < MAX_ITERACOES; iter++) {
            boolean houveCorrecao = false;

            houveCorrecao |= resolverEntreInimigos(enemies);

            if (paredes != null && !paredes.isEmpty()) {
                houveCorrecao |= resolverInimigosVsParedes(enemies, paredes);
            }


            if (!houveCorrecao) break;
        }
    }

    private boolean resolverEntreInimigos(List<EnemyTemplate> enemies) {
        boolean corrigiu = false;
        for (int i = 0; i < enemies.size(); i++) {
            EnemyTemplate a = enemies.get(i);
            if (a.isMorto()) continue;

            for (int j = i + 1; j < enemies.size(); j++) {
                EnemyTemplate b = enemies.get(j);
                if (b.isMorto()) continue;

                if (a.getHitBox().overlaps(b.getHitBox())) {
                    separarDuasHitboxes(a.getHitBox(), b.getHitBox());
                    a.sincronizarPosicaoComHitbox();
                    b.sincronizarPosicaoComHitbox();
                    corrigiu = true;
                }
            }
        }
        return corrigiu;
    }

    private boolean resolverInimigosVsParedes(List<EnemyTemplate> enemies, List<Rectangle> paredes) {
        boolean corrigiu = false;
        for (EnemyTemplate enemy : enemies) {
            if (enemy.isMorto()) continue;
            boolean overlapping = ChecarColisao.houveColisao(enemy.getHitBox(), paredes);
            if (overlapping) {
                ChecarColisao.empurrarForaParedes(enemy.getHitBox(), paredes);
                enemy.sincronizarPosicaoComHitbox();
                corrigiu = true;
            }
        }
        return corrigiu;
    }


    // Separa pelo eixo de menor sobreposição (MTV)
    private void separarDuasHitboxes(Rectangle a, Rectangle b) {
        float overlapX = Math.min(a.x + a.width, b.x + b.width) - Math.max(a.x, b.x);
        float overlapY = Math.min(a.y + a.height, b.y + b.height) - Math.max(a.y, b.y);

        if (overlapX <= 0 || overlapY <= 0) return;

        float centroAX = a.x + a.width / 2f;
        float centroBX = b.x + b.width / 2f;
        float centroAY = a.y + a.height / 2f;
        float centroBY = b.y + b.height / 2f;

        if (overlapX < overlapY) {
            float halfPush = overlapX / 2f + MARGEM;
            // Direção determinística caso centros coincidam
            boolean aEsquerda = (centroAX != centroBX)
                ? centroAX < centroBX
                : (a.hashCode() < b.hashCode());
            if (aEsquerda) {
                a.x -= halfPush;
                b.x += halfPush;
            } else {
                a.x += halfPush;
                b.x -= halfPush;
            }
        } else {
            float halfPush = overlapY / 2f + MARGEM;
            boolean aBaixo = (centroAY != centroBY)
                ? centroAY < centroBY
                : (a.hashCode() < b.hashCode());
            if (aBaixo) {
                a.y -= halfPush;
                b.y += halfPush;
            } else {
                a.y += halfPush;
                b.y -= halfPush;
            }
        }
    }

}

