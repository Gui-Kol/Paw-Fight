package com.pawfight.game.commun.phisics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import static com.pawfight.game.commun.CommunVariable.HITBOX_ISVISIBLE;

public class TilemapHitboxFactory {

    public List<Rectangle> createHitboxes(TiledMap map, String layerName) {
        List<Rectangle> hitboxes = new ArrayList<>();

        for (MapObject object : map.getLayers().get(layerName).getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                hitboxes.add(rect);
            }
        }
        return hitboxes;
    }

    public List<Rectangle> createTileLayerHitboxes(TiledMap map, String layerName, int tileWidth, int tileHeight) {
        List<Rectangle> hitboxes = new ArrayList<>();

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
        if (layer == null) return hitboxes;

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = new Rectangle(
                        x * tileWidth ,
                        y * tileHeight,
                        tileWidth,
                        tileHeight
                    );
                    hitboxes.add(rect);
                }
            }
        }
        return hitboxes;
    }


    public void draw(ShapeRenderer shapeRenderer, OrthographicCamera camera, List<Rectangle> hitBoxes) {
        if (HITBOX_ISVISIBLE) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);

            for (Rectangle hitBox : hitBoxes) {
                shapeRenderer.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
            }
            shapeRenderer.end();
        }
    }
}
