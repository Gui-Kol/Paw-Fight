package com.pawfight.game.world.template;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.fisica.ColisaoResolver;
import com.pawfight.game.engine.fisica.TilemapHitboxFactory;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.DanoTiro;

import java.util.List;

public class WorldPhysics {

    private final TilemapHitboxFactory tilemapHitboxFactory;
    private final DanoTiro danoTiro;
    private final ColisaoResolver colisaoResolver;
    private List<Rectangle> paredes;

    public WorldPhysics() {
        this.tilemapHitboxFactory = new TilemapHitboxFactory();
        this.danoTiro = new DanoTiro();
        this.colisaoResolver = new ColisaoResolver();
    }

    public void clearCache() {
        tilemapHitboxFactory.clearCache();
    }

    public void carregarParede(TiledMap map, PlayerTemplate player) {
        List<Rectangle> paredes = tilemapHitboxFactory.createHitboxes(map, "Parede");
        this.paredes = paredes;
        player.adicionarColisao(paredes);
    }

    public void resolverColisoes(List<EnemyTemplate> enemies) {
        colisaoResolver.resolver(enemies, paredes);
    }

    public void processarDanoTiro(WorldTemplate world) {
        danoTiro.darDanoListaInimigos(world);
    }

    public void drawDebugQuadtree(ShapeRenderer shapeRenderer, Matrix4 cameraMatrix) {
        danoTiro.drawDebugQuadtree(shapeRenderer, cameraMatrix);
    }

    // ── Getters ────────────────────────────────────────────────

    public TilemapHitboxFactory getTilemapHitboxFactory() {
        return tilemapHitboxFactory;
    }

    public DanoTiro getDanoTiro() {
        return danoTiro;
    }

    public List<Rectangle> getParedes() {
        return paredes;
    }
}

