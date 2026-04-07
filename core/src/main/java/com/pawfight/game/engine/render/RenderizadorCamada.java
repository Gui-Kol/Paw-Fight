package com.pawfight.game.engine.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import java.util.ArrayList;
import java.util.List;

public class RenderizadorCamada {

    private final OrthogonalTiledMapRenderer renderer;
    private final TiledMap map;
    private final List<Integer> indicesBuffer = new ArrayList<>();
    private int[] idxArray = new int[8]; // pre-allocated, resized if needed

    public RenderizadorCamada(TiledMap map) {
        this.map = map;
        this.renderer = new OrthogonalTiledMapRenderer(map);
    }

    // Renderiza apenas uma ‘layer’ pelo nome
    public void renderLayer(String layerName, OrthographicCamera camera) {
        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null) {
            Gdx.app.error("RenderizadorCamada", layerName + " não encontrado!");
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
        indicesBuffer.clear();
        for (String name : layerNames) {
            MapLayer layer = map.getLayers().get(name);
            if (layer == null) {
                Gdx.app.error("RenderizadorCamada", name + " não encontrado!");
                continue;
            }
            int idx = map.getLayers().getIndex(layer);
            if (idx != -1) indicesBuffer.add(idx);
        }

        int size = indicesBuffer.size();
        if (size == 0) return;

        if (idxArray.length < size) {
            idxArray = new int[size];
        }
        for (int i = 0; i < size; i++) idxArray[i] = indicesBuffer.get(i);

        renderer.setView(camera);
        renderer.render(size == idxArray.length ? idxArray : java.util.Arrays.copyOf(idxArray, size));
    }

    public void dispose() {
        renderer.dispose();
    }
}
