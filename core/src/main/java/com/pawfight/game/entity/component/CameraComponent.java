package com.pawfight.game.entity.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.pawfight.game.engine.design.AlteradorZoom;

/**
 * Componente responsável pela câmera do jogador.
 * Gerencia posição, zoom e limites do mapa.
 */
public class CameraComponent {

    private final OrthographicCamera camera;
    private final AlteradorZoom alteradorZoom;
    private final float mapWidth;
    private final float mapHeight;

    public CameraComponent(int dx, int dy, float zoomCamera,
                           int tileWidth, int numTilesX,
                           int tileHeight, int numTilesY) {
        this.alteradorZoom = new AlteradorZoom();
        this.mapWidth = tileWidth * numTilesX;
        this.mapHeight = tileHeight * numTilesY;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = zoomCamera;

        camera.position.set(dx, dy, 0);
        camera.position.x = MathUtils.clamp(camera.position.x,
            camera.viewportWidth / 2f, mapWidth - camera.viewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y,
            camera.viewportHeight / 2f, mapHeight - camera.viewportHeight / 2f);
        camera.update();
    }

    // ── Update ─────────────────────────────────────────────────

    public void updateCamera(int dx, int dy) {
        camera.position.set(dx, dy, 0);
        camera.update();
    }

    public void updateZoom() {
        camera.zoom = alteradorZoom.changeZoom();
    }

    // ── Getters ────────────────────────────────────────────────

    public OrthographicCamera getCamera() { return camera; }
    public float getMapWidth() { return mapWidth; }
    public float getMapHeight() { return mapHeight; }
}

