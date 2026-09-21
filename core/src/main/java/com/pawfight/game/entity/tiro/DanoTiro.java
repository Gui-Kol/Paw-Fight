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
    private static final float LOG_INTERVALO = 5f; // segundos entre logs

    // Quadtree reutilizável — tamanho generoso para qualquer mapa
    private static final float WORLD_SIZE = 3000f;
    private final Quadtree<EnemyTemplate> quadtree =
        new Quadtree<>(new Rectangle(0, 0, WORLD_SIZE, WORLD_SIZE));

    // Listas pré-alocadas (evita new ArrayList a cada frame)
    private final List<EnemyTemplate> candidatos = new ArrayList<>();
    private final List<TirosTemplate> tirosSnapshot = new ArrayList<>();

    // Acumuladores para log throttled
    private float logTimer = 0f;
    private int acumColisoes = 0;
    private int acumFramesProcessados = 0;

    public void darDanoListaInimigos(WorldTemplate world) {
        List<EnemyTemplate> inimigos = world.getEnemyManager().getListaInimigos();
        List<TirosTemplate> tiros = world.getPlayer().getTiros();

        if (inimigos == null || inimigos.isEmpty() || tiros == null || tiros.isEmpty()) {
            return;
        }

        // Cópia defensiva reutilizável
        tirosSnapshot.clear();
        tirosSnapshot.addAll(tiros);

        quadtree.clear();
        for (int i = 0, n = inimigos.size(); i < n; i++) {
            EnemyTemplate inimigo = inimigos.get(i);
            quadtree.inserir(inimigo, inimigo.getHitBox());
        }

        int totalColisoes = 0;
        for (int t = 0, tn = tirosSnapshot.size(); t < tn; t++) {
            TirosTemplate tiro = tirosSnapshot.get(t);

            candidatos.clear();
            quadtree.consultar(tiro.getHitBox(), candidatos);

            for (int c = 0, cn = candidatos.size(); c < cn; c++) {
                EnemyTemplate inimigo = candidatos.get(c);
                if (inimigo.getHitBox().overlaps(tiro.getHitBox())) {
                    if (inimigo.receberDano(tiro.getDano())) {
                        // Efeitos de acerto (queimadura, lentidão, roubo de vida)
                        tiro.aoAcertar(inimigo);
                        totalColisoes++;

                        // Tiro de alvo único: consome o projétil no primeiro acerto
                        if (tiro.isUnicoAlvo()) {
                            tiro.expirar();
                            break;
                        }
                    }
                }
            }
        }

        // Estatísticas acumuladas para log throttled
        if (GameConfig.getInstance().isDebugMode()) {
            acumColisoes += totalColisoes;
            acumFramesProcessados++;
            logTimer += Gdx.graphics.getDeltaTime();

            if (logTimer >= LOG_INTERVALO) {
                Gdx.app.log(TAG, "Resumo (" + LOG_INTERVALO + "s): "
                    + acumColisoes + " colisões em "
                    + acumFramesProcessados + " frames | "
                    + inimigos.size() + " inimigos, "
                    + tirosSnapshot.size() + " tiros ativos");
                logTimer = 0f;
                acumColisoes = 0;
                acumFramesProcessados = 0;
            }
        }
    }

    // Desenha as divisões da quadtree (chamado quando F3 ativa as hitboxes visíveis).
    public void drawDebugQuadtree(ShapeRenderer shapeRenderer, Matrix4 cameraMatrix) {
        if (!GameConfig.getInstance().isHitboxVisivel()) return;

        shapeRenderer.setProjectionMatrix(cameraMatrix);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        quadtree.drawDebug(shapeRenderer);
        shapeRenderer.end();
    }
}
