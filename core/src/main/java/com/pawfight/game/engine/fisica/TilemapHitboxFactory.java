package com.pawfight.game.engine.fisica;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pawfight.game.engine.GameConfig;

public class TilemapHitboxFactory {

    // Cache: evita recriar listas de Rectangle a cada frame para o mesmo mapa/layer
    private final Map<String, List<Rectangle>> cache = new HashMap<>();

    public void clearCache() {
        cache.clear();
    }

    // Cria os retângulos de colisão (cacheado por layerName)
    public List<Rectangle> createHitboxes(TiledMap map, String layerName) {
        List<Rectangle> cached = cache.get(layerName);
        if (cached != null) {
            return cached;
        }

        List<Rectangle> hitboxes = new ArrayList<>();

        var layer = map.getLayers().get(layerName);
        if (layer == null) {
            cache.put(layerName, hitboxes);
            return hitboxes;
        }

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                hitboxes.add(((RectangleMapObject) object).getRectangle());
            } else if (object instanceof PolygonMapObject) {
                hitboxes.add(((PolygonMapObject) object).getPolygon().getBoundingRectangle());
            } else if (object instanceof EllipseMapObject) {
                Ellipse ellipse = ((EllipseMapObject) object).getEllipse();
                hitboxes.add(new Rectangle(ellipse.x, ellipse.y, ellipse.width, ellipse.height));
            } else if (object instanceof TextureMapObject texObj) {
                float x = texObj.getX();
                float y = texObj.getY();
                float w = texObj.getTextureRegion().getRegionWidth();
                float h = texObj.getTextureRegion().getRegionHeight();
                hitboxes.add(new Rectangle(x, y, w, h));
            }
        }

        cache.put(layerName, hitboxes);
        return hitboxes;
    }


    public void drawObjects(TiledMap map, String layerName, Batch batch, OrthographicCamera camera, boolean isInvertido) {
        var layer = map.getLayers().get(layerName);
        if (layer == null) {
            return;
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Copia os objetos para uma lista
        List<MapObject> objects = new ArrayList<>();
        for (MapObject obj : layer.getObjects()) {
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
            if (object instanceof TextureMapObject texObj) {

                float x = texObj.getX();
                float y = texObj.getY(); // sem subtrair altura

                batch.draw(texObj.getTextureRegion(), x, y);
            }
        }

        batch.end();
    }


    public List<Rectangle> createTileLayerHitboxes(TiledMap map, String layerName, int tileWidth, int tileHeight) {
        // Chave de cache com prefixo "tile:" para não colidir com createHitboxes
        String cacheKey = "tile:" + layerName;
        List<Rectangle> cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<Rectangle> hitboxes = new ArrayList<>();

        // pega apenas a ‘layer’ com o nome especificado
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
        if (layer == null) {
            return hitboxes; // se não existir ou não for ‘layer’ de tile, retorna vazio
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

        cache.put(cacheKey, hitboxes);
        return hitboxes;
    }


    public void draw(ShapeRenderer shapeRenderer, OrthographicCamera camera, List<Rectangle> hitBoxes) {
        if (GameConfig.getInstance().isHitboxVisivel()) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            drawRects(shapeRenderer, hitBoxes);
            shapeRenderer.end();
        }
    }

    /**
     * Desenha hitboxes SEM begin/end — para uso dentro de um bloco já aberto.
     */
    public void drawRects(ShapeRenderer shapeRenderer, List<Rectangle> hitBoxes) {
        shapeRenderer.setColor(Color.GREEN);
        for (Rectangle hitBox : hitBoxes) {
            shapeRenderer.rect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
        }
    }
}
