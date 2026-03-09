package com.pawfight.game.commun.phisics;

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

//    public static void danoSeColidir(Player player, List<Rectangle> colisores, List<EnemyTemplate> entitys){
//        if (houveColisao(player.getHitBox(),colisores)){
//           player.dano(entitys.get(1).getForca());
//        }
//    }

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
            ? -(player.getHitboxOffsetX())
            : player.getHitboxOffsetX();

        player.setDx((int) (hitBox.x - (player.getTamanhoPx() - player.getHitboxSize()) / 2f - offsetX));
        player.setDy((int) (hitBox.y - player.getHitboxOffsetY()));
    }



}
