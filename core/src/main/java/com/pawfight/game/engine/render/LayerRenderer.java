package com.pawfight.game.engine.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import java.util.ArrayList;
import java.util.List;

public class LayerRenderer {

    private final OrthogonalTiledMapRenderer renderer;
    private final TiledMap map;

    public LayerRenderer(TiledMap map) {
        this.map = map;
        this.renderer = new OrthogonalTiledMapRenderer(map);
    }

    // Renderiza apenas uma ‘layer’ pelo nome
    public void renderLayer(String layerName, OrthographicCamera camera) {
        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null) {
            Gdx.app.error("LayerRenderer", layerName + " não encontrado!");
            return;
        }

        int index = map.getLayers().getIndex(layer);
        renderer.setView(camera);
        renderer.render(new int[]{index});
    }

    public void renderLayerTiled(TiledMapTileLayer layer, OrthographicCamera camera) {
        int index = map.getLayers().getIndex(layer);
        if (index == -1) {
            // Se não estiver no mapa, adiciona
            map.getLayers().add(layer);
            index = map.getLayers().getIndex(layer);
        }
        renderer.setView(camera);
        renderer.render(new int[]{index});
    }


    // Renderiza múltiplos layers pelo nome
    public void renderLayers(String[] layerNames, OrthographicCamera camera) {
        if (layerNames == null || layerNames.length == 0) return;
        List<Integer> indices = new ArrayList<>();
        for (String name : layerNames) {
            MapLayer layer = map.getLayers().get(name);
            if (layer == null) {
                Gdx.app.error("LayerRenderer", name + " não encontrado!");
                continue;
            }
            int idx = map.getLayers().getIndex(layer);
            if (idx != -1) indices.add(idx);
        }

        if (indices.isEmpty()) return;

        int[] idxArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) idxArray[i] = indices.get(i);

        renderer.setView(camera);
        renderer.render(idxArray);
    }

    public void dispose() {
        renderer.dispose();
    }
}
