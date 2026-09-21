package com.pawfight.game.engine.fisica;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Quadtree<T> {

    private static final int MAX_OBJETOS = 8;
    private static final int MAX_NIVEIS  = 5;

    // Cores por nível de profundidade (para distinguir visualmente)
    private static final Color[] CORES_NIVEL = {
        new Color(0f, 1f, 1f, 1f),     // Nível 0 — Ciano
        new Color(0f, 1f, 0.5f, 1f),   // Nível 1 — Verde-água
        new Color(1f, 1f, 0f, 1f),     // Nível 2 — Amarelo
        new Color(1f, 0.5f, 0f, 1f),   // Nível 3 — Laranja
        new Color(1f, 0f, 1f, 1f),     // Nível 4 — Magenta
        new Color(0.5f, 0.5f, 1f, 1f), // Nível 5 — Lilás
    };

    private final int nivel;
    private final Rectangle limites;

    // Listas paralelas: items[i] tem hitbox hitboxes[i]
    private final List<T>         items;
    private final List<Rectangle> hitboxes;

    @SuppressWarnings("unchecked")
    private final Quadtree<T>[] nos = new Quadtree[4];
    private boolean dividida;

    public Quadtree(Rectangle limites) {
        this(0, limites);
    }

    private Quadtree(int nivel, Rectangle limites) {
        this.nivel    = nivel;
        this.limites  = new Rectangle(limites);
        this.items    = new ArrayList<>(MAX_OBJETOS);
        this.hitboxes = new ArrayList<>(MAX_OBJETOS);
        this.dividida = false;
    }

    public void clear() {
        items.clear();
        hitboxes.clear();
        if (dividida) {
            for (int i = 0; i < 4; i++) {
                nos[i].clear();
            }
            dividida = false;
        }
    }

    public void inserir(T item, Rectangle hitbox) {
        if (!limites.overlaps(hitbox)) {
            return;
        }

        if (dividida) {
            inserirNosFilhos(item, hitbox);
            return;
        }

        items.add(item);
        hitboxes.add(hitbox);

        if (items.size() > MAX_OBJETOS && nivel < MAX_NIVEIS) {
            subdividir();

            // Redistribui os objetos existentes para os filhos
            for (int i = items.size() - 1; i >= 0; i--) {
                inserirNosFilhos(items.get(i), hitboxes.get(i));
            }
            items.clear();
            hitboxes.clear();
        }
    }

    public void consultar(Rectangle regiao, List<T> resultado) {
        if (!limites.overlaps(regiao)) return;

        for (int i = 0, n = hitboxes.size(); i < n; i++) {
            if (regiao.overlaps(hitboxes.get(i))) {
                resultado.add(items.get(i));
            }
        }

        if (dividida) {
            for (int i = 0; i < 4; i++) {
                nos[i].consultar(regiao, resultado);
            }
        }
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        Color cor = CORES_NIVEL[Math.min(nivel, CORES_NIVEL.length - 1)];
        shapeRenderer.setColor(cor);
        shapeRenderer.rect(limites.x, limites.y, limites.width, limites.height);

        // Quantidade de itens do nó como marcas no canto
        if (!items.isEmpty()) {
            float markSize = 3f;
            for (int i = 0, n = items.size(); i < n; i++) {
                float mx = limites.x + 2 + (i * (markSize + 1));
                float my = limites.y + 2;
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.rect(mx, my, markSize, markSize);
            }
        }

        if (dividida) {
            for (int i = 0; i < 4; i++) {
                nos[i].drawDebug(shapeRenderer);
            }
        }
    }

    private void subdividir() {
        float x     = limites.x;
        float y     = limites.y;
        float halfW = limites.width  / 2f;
        float halfH = limites.height / 2f;

        if (nos[0] == null) {
            nos[0] = new Quadtree<>(nivel + 1, new Rectangle(x + halfW, y + halfH, halfW, halfH)); // NE
            nos[1] = new Quadtree<>(nivel + 1, new Rectangle(x,         y + halfH, halfW, halfH)); // NW
            nos[2] = new Quadtree<>(nivel + 1, new Rectangle(x,         y,         halfW, halfH)); // SW
            nos[3] = new Quadtree<>(nivel + 1, new Rectangle(x + halfW, y,         halfW, halfH)); // SE
        } else {
            nos[0].limites.set(x + halfW, y + halfH, halfW, halfH);
            nos[1].limites.set(x,         y + halfH, halfW, halfH);
            nos[2].limites.set(x,         y,         halfW, halfH);
            nos[3].limites.set(x + halfW, y,         halfW, halfH);
        }

        dividida = true;
    }

    private void inserirNosFilhos(T item, Rectangle hitbox) {
        for (int i = 0; i < 4; i++) {
            if (nos[i].limites.overlaps(hitbox)) {
                nos[i].inserir(item, hitbox);
            }
        }
    }
}
