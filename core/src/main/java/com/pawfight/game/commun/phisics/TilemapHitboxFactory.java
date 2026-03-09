package com.pawfight.game.commun.phisics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

import static com.pawfight.game.commun.CommunVariable.HITBOX_ISVISIBLE;

public class TilemapHitboxFactory {

    // Cria os retângulos de colisão
    public List<Rectangle> createHitboxes(TiledMap map, String layerName) {
        List<Rectangle> hitboxes = new ArrayList<>();

        for (MapObject object : map.getLayers().get(layerName).getObjects()) {
            if (object instanceof RectangleMapObject) {
                hitboxes.add(((RectangleMapObject) object).getRectangle());
            } else if (object instanceof PolygonMapObject) {
                hitboxes.add(((PolygonMapObject) object).getPolygon().getBoundingRectangle());
            } else if (object instanceof EllipseMapObject) {
                Ellipse ellipse = ((EllipseMapObject) object).getEllipse();
                hitboxes.add(new Rectangle(ellipse.x, ellipse.y, ellipse.width, ellipse.height));
            } else if (object instanceof TextureMapObject) {
                TextureMapObject texObj = (TextureMapObject) object;
                float x = texObj.getX();
                float y = texObj.getY();
                float w = texObj.getTextureRegion().getRegionWidth();
                float h = texObj.getTextureRegion().getRegionHeight();
                hitboxes.add(new Rectangle(x, y, w, h));
            }
        }
        return hitboxes;
    }


    public void drawObjects(TiledMap map, String layerName, Batch batch, OrthographicCamera camera, boolean isInvertido) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Copia os objetos para uma lista
        List<MapObject> objects = new ArrayList<>();
        for (MapObject obj : map.getLayers().get(layerName).getObjects()) {
            objects.add(obj);
        }

        // Ordena conforme o parâmetro isInvertido
        objects.sort((o1, o2) -> {
            float y1 = (o1 instanceof TextureMapObject) ? ((TextureMapObject) o1).getY() : 0;
            float y2 = (o2 instanceof TextureMapObject) ? ((TextureMapObject) o2).getY() : 0;

            if (isInvertido) {
                // maior Y primeiro → menor Y por último
                return Float.compare(y2, y1);
            } else {
                // menor Y primeiro → maior Y por último
                return Float.compare(y1, y2);
            }
        });

        // Desenha na ordem escolhida
        for (MapObject object : objects) {
            if (object instanceof TextureMapObject) {
                TextureMapObject texObj = (TextureMapObject) object;

                float x = texObj.getX();
                float y = texObj.getY(); // sem subtrair altura

                batch.draw(texObj.getTextureRegion(), x, y);
            }
        }

        batch.end();
    }


    public List<Rectangle> createTileLayerHitboxes(TiledMap map, String layerName, int tileWidth, int tileHeight) {
        List<Rectangle> hitboxes = new ArrayList<>();

        // pega apenas a layer com o nome especificado
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
        if (!(layer instanceof TiledMapTileLayer)) {
            return hitboxes; // se não existir ou não for layer de tile, retorna vazio
        }

        // percorre apenas os tiles dessa layer
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = new Rectangle(
                        x * tileWidth,
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
            shapeRenderer.setColor(Color.GREEN);

            for (Rectangle hitBox : hitBoxes) {
                shapeRenderer.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
            }
            shapeRenderer.end();
        }
    }
}
