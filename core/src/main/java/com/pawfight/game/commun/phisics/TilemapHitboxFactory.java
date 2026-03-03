package com.pawfight.game.commun.phisics;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class TilemapHitboxFactory {

    public static List<Rectangle> createHitboxes(TiledMap map, String layerName) {
        List<Rectangle> hitboxes = new ArrayList<>();

        for (MapObject object : map.getLayers().get(layerName).getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                hitboxes.add(rect);
            }
        }

        return hitboxes;
    }

    public void parede(TiledMap map, String layerName, Player player) {
        List<Rectangle> colisaoHitboxes = TilemapHitboxFactory.createHitboxes(map, layerName);

        Rectangle playerBox = player.getHitBox();

        for (Rectangle rect : colisaoHitboxes) {
            if (rect.overlaps(playerBox)) {
                // Corrige posição do player para fora da parede
                if (player.getDx() < rect.x) {
                    // vindo pela esquerda
                    playerBox.x = (int)(rect.x - playerBox.width);
                } else if (player.getDx() > rect.x + rect.width) {
                    // vindo pela direita
                    playerBox.x = (int)(rect.x + rect.width);
                }

                if (player.getDy() < rect.y) {
                    // vindo por baixo
                    playerBox.y = (int)(rect.y - playerBox.height);
                } else if (player.getDy() > rect.y + rect.height) {
                    // vindo por cima
                    playerBox.y = (int)(rect.y + rect.height);
                }

                // atualiza coordenadas do player com base na hitbox corrigida
                player.setPosition((int)playerBox.x, (int)playerBox.y);
            }
        }
    }


}
