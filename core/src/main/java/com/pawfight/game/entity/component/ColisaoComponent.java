package com.pawfight.game.entity.component;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.GameConfig;
import com.pawfight.game.engine.fisica.ChecarColisao;
import com.pawfight.game.engine.fisica.TilemapHitboxFactory;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.ArrayList;
import java.util.List;

public class ColisaoComponent {

    private final List<Rectangle> listColisores = new ArrayList<>();
    private final ChecarColisao checarColisao = new ChecarColisao();
    private final TilemapHitboxFactory tilemapHitboxFactory = new TilemapHitboxFactory();

    // ── Checagem de colisão ────────────────────────────────────

    public void checarColisao(PlayerTemplate player) {
        // Em modo debug, aplica movimento sem verificar paredes (noclip)
        List<Rectangle> colisores = GameConfig.getInstance().isDebugMode()
            ? java.util.Collections.emptyList()
            : listColisores;
        checarColisao.checarColisaoSeparadoEixo(colisores, player);
    }

    // ── Gerenciamento de colisores ─────────────────────────────

    public void adicionarColisao(List<Rectangle> colisores) {
        listColisores.addAll(colisores);
    }

    public void adicionarColisaoPorLevel(List<Rectangle> colisores, int playerLevel, int levelNecessario) {
        if (colisores == null || colisores.isEmpty()) return;
        if (playerLevel < levelNecessario) {
            if (!listColisores.contains(colisores.get(0))) {
                listColisores.addAll(colisores);
            }
        } else {
            listColisores.removeAll(colisores);
        }
    }

    public void clearColisores() {
        listColisores.clear();
    }

    // ── Desenho de debug ───────────────────────────────────────

    public void drawDebugHitboxes(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        tilemapHitboxFactory.draw(shapeRenderer, camera, listColisores);
    }

    public void drawDebugHitboxesNoBatch(ShapeRenderer shapeRenderer) {
        tilemapHitboxFactory.drawRects(shapeRenderer, listColisores);
    }

    // ── Getters ────────────────────────────────────────────────

    public List<Rectangle> getListColisores() { return listColisores; }
    public TilemapHitboxFactory getTilemapHitboxFactory() { return tilemapHitboxFactory; }
}

