package com.pawfight.game.entity.tiro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.GameConfig;
import com.pawfight.game.engine.fisica.Quadtree;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.world.template.WorldTemplate;

import java.util.ArrayList;
import java.util.List;

public class DanoTiro {

    private static final String TAG = "DanoTiro";

    // Quadtree reutilizável — tamanho generoso para qualquer mapa
    private static final float WORLD_SIZE = 4096f;
    private final Quadtree<EnemyTemplate> quadtree =
        new Quadtree<>(new Rectangle(0, 0, WORLD_SIZE, WORLD_SIZE));

    // Listas pré-alocadas (evita new ArrayList a cada frame)
    private final List<EnemyTemplate> candidatos = new ArrayList<>();
    private final List<TirosTemplate> tirosSnapshot = new ArrayList<>();

    public void darDanoListaInimigos(WorldTemplate world) {
        List<EnemyTemplate> inimigos = world.getEnemyManager().getListaInimigos();
        List<TirosTemplate> tiros = world.getPlayer().getTiros();

        if (inimigos == null || inimigos.isEmpty() || tiros == null || tiros.isEmpty()) {
            return;
        }

        boolean debug = GameConfig.getInstance().isDebugMode();

        // 1. Cópia defensiva reutilizável
        tirosSnapshot.clear();
        tirosSnapshot.addAll(tiros);

        if (debug) {
            Gdx.app.log(TAG, "Processando colisões: " + inimigos.size()
                + " inimigos × " + tirosSnapshot.size() + " tiros");
        }

        // 2. Preenche a quadtree com os inimigos e suas hitboxes
        quadtree.clear();
        for (int i = 0, n = inimigos.size(); i < n; i++) {
            EnemyTemplate inimigo = inimigos.get(i);
            quadtree.inserir(inimigo, inimigo.getHitBox());
        }

        // 3. Para cada tiro, consulta apenas os inimigos próximos
        int totalColisoes = 0;
        for (int t = 0, tn = tirosSnapshot.size(); t < tn; t++) {
            TirosTemplate tiro = tirosSnapshot.get(t);

            candidatos.clear();
            quadtree.consultar(tiro.getHitBox(), candidatos);

            for (int c = 0, cn = candidatos.size(); c < cn; c++) {
                EnemyTemplate inimigo = candidatos.get(c);
                if (inimigo.getHitBox().overlaps(tiro.getHitBox())) {
                    inimigo.dano(tiro.getDano());
                    totalColisoes++;

                    if (debug) {
                        Gdx.app.log(TAG, "Tiro acertou inimigo! Dano: " + tiro.getDano()
                            + " | Candidatos testados: " + candidatos.size()
                            + " (de " + inimigos.size() + " total)");
                    }
                }
            }
        }

        if (debug && totalColisoes > 0) {
            Gdx.app.log(TAG, "Frame finalizado: " + totalColisoes + " colisão(ões) detectada(s)");
        }
    }

    /**
     * Desenha as divisões da quadtree sobre o mundo.
     * Chamado quando F3 está ativo (hitboxes visíveis).
     */
    public void drawDebugQuadtree(ShapeRenderer shapeRenderer, Matrix4 cameraMatrix) {
        if (!GameConfig.getInstance().isHitboxVisivel()) return;

        shapeRenderer.setProjectionMatrix(cameraMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        quadtree.drawDebug(shapeRenderer);
        shapeRenderer.end();
    }
}
