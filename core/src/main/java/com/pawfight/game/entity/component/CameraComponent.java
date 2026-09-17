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

    // Estado do zoom de morte (suave e focado no player)
    private float zoomMorteInicial;
    private float zoomMorteAlvo;
    private float tempoZoomMorte = 0f;
    private float duracaoZoomMorte = 1f;
    private boolean zoomMorteAtivo = false;

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

    // ── Zoom de morte (suave, focado no player) ───────────────

    public void iniciarZoomMorte(float zoomAlvo, float duracao) {
        this.zoomMorteInicial = camera.zoom;
        this.zoomMorteAlvo = zoomAlvo;
        this.duracaoZoomMorte = duracao;
        this.tempoZoomMorte = 0f;
        this.zoomMorteAtivo = true;
    }

    /** Interpola o zoom até o alvo. Retorna true quando a animação de zoom termina. */
    public boolean atualizarZoomMorte(float delta) {
        if (!zoomMorteAtivo) return true;

        tempoZoomMorte += delta;
        float progresso = Math.min(tempoZoomMorte / duracaoZoomMorte, 1f);
        progresso = progresso * progresso * (3f - 2f * progresso); // smoothstep

        camera.zoom = MathUtils.lerp(zoomMorteInicial, zoomMorteAlvo, progresso);
        camera.update();

        if (progresso >= 1f) {
            zoomMorteAtivo = false;
            return true;
        }
        return false;
    }

    // ── Getters ────────────────────────────────────────────────

    public OrthographicCamera getCamera() { return camera; }
    public float getMapWidth() { return mapWidth; }
    public float getMapHeight() { return mapHeight; }
}

