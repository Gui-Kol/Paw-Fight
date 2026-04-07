package com.pawfight.game.entity.tiro;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.entity.player.PlayerTemplate;

public abstract class TirosTemplate implements Pool.Poolable {
    protected int x, y;
    protected Rectangle hitBox;
    protected int dano;
    protected float duracao, cadencia, intervalo;
    protected float tempoVida = 0f;
    protected Texture texture;
    protected int tamanhoDraw, tamanho, tamanhoPadrao;
    protected Renderizar renderizar = Renderizar.INSTANCE;
    protected int xHitBox, yHitBox;

    @SuppressWarnings("rawtypes")
    protected Pool ownerPool;

    public TirosTemplate(int x, int y, int dano, int tamanho, PlayerTemplate player) {
        duracao = definirDuracao();
        cadencia = definirIntervalo();
        tamanhoPadrao = definirTamanhoPadrao();
        intervalo = 0;

        this.tamanho = tamanho + tamanhoPadrao;
        this.tamanhoDraw = tamanho + tamanhoPadrao;
        this.x = x;
        this.y = y;
        this.dano = dano;

        this.xHitBox = x;
        this.yHitBox = y;

        if (player.isOlhandoEsquerda()) {
            this.x += player.getTamanho();
            xHitBox += player.getTamanho();
        }

        texture = singleTex();
        hitBox = gerarHitBox();
    }

    protected TirosTemplate() {
        duracao = definirDuracao();
        cadencia = definirIntervalo();
        tamanhoPadrao = definirTamanhoPadrao();
        intervalo = 0;
        hitBox = new Rectangle();
    }

    // ── Pool helpers ────────────────────────────────────────────

    protected void reiniciarBase(int x, int y, int dano, int tamanho, PlayerTemplate player) {
        this.tempoVida = 0f;
        this.tamanho = tamanho + tamanhoPadrao;
        this.tamanhoDraw = tamanho + tamanhoPadrao;
        this.x = x;
        this.y = y;
        this.dano = dano;
        this.xHitBox = x;
        this.yHitBox = y;
        if (player.isOlhandoEsquerda()) {
            this.x += player.getTamanho();
            xHitBox += player.getTamanho();
        }
    }

    /** Devolve este projétil ao pool de onde veio (se houver). */
    @SuppressWarnings("unchecked")
    public void liberar() {
        if (ownerPool != null) {
            Pool p = ownerPool;
            ownerPool = null;      // previne double-free
            p.free(this);
        }
    }

    @Override
    public void reset() {
        tempoVida = 0f;
    }

    protected abstract int definirTamanhoPadrao();

    protected abstract float definirDuracao();

    public void desenhar(Batch batch) {
        if (texture == null) return;
        batch.draw(texture, x, y, tamanhoDraw, tamanhoDraw);
    }

    public void desenharHitbox(ShapeRenderer shapeRenderer) {
        if (!com.pawfight.game.engine.GameConfig.getInstance().isHitboxVisivel()) return;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        renderizar.hitboxDraw(shapeRenderer, hitBox);
        shapeRenderer.end();
    }

    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        batch.begin();
        desenhar(batch);
        batch.end();
        desenharHitbox(shapeRenderer);
    }

    public void update(float delta) {
        tempoVida += delta;
    }

    public boolean isExpirado() {
        return tempoVida >= duracao;
    }

    protected abstract Texture randomTex();

    protected abstract Rectangle gerarHitBox();


    protected abstract TirosTemplate obterDoPool(PlayerTemplate player);

    protected abstract Texture singleTex();

    protected abstract float definirIntervalo();

    public float getCadencia() {
        return cadencia;
    }

    public float getDuracao() {
        return duracao;
    }

    public int getDano() {
        return dano;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public float getIntervalo() {
        return intervalo;
    }

    public void setDuracao(float duracao) {
        this.duracao = duracao;
    }

    public void setIntervalo(float intervalo) {
        this.intervalo = intervalo;
    }

    public void dispose() {
        // Texturas são gerenciadas pelo AssetManager — NÃO dar dispose aqui
    }
}


