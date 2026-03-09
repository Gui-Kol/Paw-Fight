package com.pawfight.game.commun;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class LayerRenderer {

    private final OrthogonalTiledMapRenderer renderer;
    private final TiledMap map;

    public LayerRenderer(TiledMap map) {
        this.map = map;
        this.renderer = new OrthogonalTiledMapRenderer(map);
    }

    // Renderiza apenas um layer pelo nome
    public void renderLayer(String layerName, OrthographicCamera camera) {
        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null) {
            System.out.println("Layer '" + layerName + "' não encontrado!");
            return;
        }

        int index = map.getLayers().getIndex(layer);
        renderer.setView(camera);
        renderer.render(new int[]{index});
    }

    // Novo método no LayerRenderer
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
        for (String name : layerNames) {
            renderLayer(name, camera); // chama um por um, na ordem
        }
    }

    public void dispose() {
        renderer.dispose();
    }
}
