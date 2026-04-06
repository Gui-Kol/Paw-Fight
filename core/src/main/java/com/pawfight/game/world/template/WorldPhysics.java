package com.pawfight.game.world.template;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.fisica.TilemapHitboxFactory;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.DanoTiro;

import java.util.List;

/**
 * Gerencia física e colisões do mundo: hitboxes de paredes e dano de tiros.
 */
public class WorldPhysics {

    private final TilemapHitboxFactory tilemapHitboxFactory;
    private final DanoTiro danoTiro;

    public WorldPhysics() {
        this.tilemapHitboxFactory = new TilemapHitboxFactory();
        this.danoTiro = new DanoTiro();
    }

    public void clearCache() {
        tilemapHitboxFactory.clearCache();
    }

    public void carregarParede(TiledMap map, PlayerTemplate player) {
        List<Rectangle> paredes = tilemapHitboxFactory.createHitboxes(map, "Parede");
        player.adicionarColisao(paredes);
    }

    public void processarDanoTiro(WorldTemplate world) {
        danoTiro.darDanoListaInimigos(world);
    }

    public void drawDebugQuadtree(ShapeRenderer shapeRenderer, Matrix4 cameraMatrix) {
        danoTiro.drawDebugQuadtree(shapeRenderer, cameraMatrix);
    }

    public TilemapHitboxFactory getTilemapHitboxFactory() {
        return tilemapHitboxFactory;
    }

    public DanoTiro getDanoTiro() {
        return danoTiro;
    }
}

