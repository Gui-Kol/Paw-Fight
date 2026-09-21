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

    public void checarColisao(PlayerTemplate player) {
        // Debug (noclip): move sem verificar paredes
        List<Rectangle> colisores = GameConfig.getInstance().isDebugMode()
            ? java.util.Collections.emptyList()
            : listColisores;
        checarColisao.checarColisaoSeparadoEixo(colisores, player);
    }

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

    public void drawDebugHitboxes(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        TilemapHitboxFactory.draw(shapeRenderer, camera, listColisores);
    }

    public void drawDebugHitboxesNoBatch(ShapeRenderer shapeRenderer) {
        TilemapHitboxFactory.drawRects(shapeRenderer, listColisores);
    }

    public List<Rectangle> getListColisores() { return listColisores; }
}

