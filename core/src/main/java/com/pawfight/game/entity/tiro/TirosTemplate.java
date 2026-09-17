package com.pawfight.game.entity.tiro;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

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

    /** Quem disparou — usado por efeitos de acerto (ex.: roubo de vida). */
    protected PlayerTemplate dono;
    /** Direção do olhar no momento do disparo (posicionamento direcional). */
    protected boolean spawnEsquerda;
    /** Inimigos vivos da sala atual (referência do mundo, injetada no disparo). */
    protected List<EnemyTemplate> inimigos;
    /** Direção unitária de deslocamento (tiros que se movem). */
    protected float dirMovimentoX = 1f, dirMovimentoY = 0f;

    @SuppressWarnings("rawtypes")
    protected Pool ownerPool;

    public TirosTemplate(int x, int y, int dano, int tamanho, PlayerTemplate player) {
        duracao = definirDuracao();
        cadencia = definirIntervalo();
        tamanhoPadrao = definirTamanhoPadrao();
        intervalo = 0;

        this.dono = player;
        this.spawnEsquerda = player.isOlhandoEsquerda();

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
        this.dono = player;
        this.spawnEsquerda = player.isOlhandoEsquerda();
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

    /** Força a expiração (usado por tiros de alvo único após o acerto). */
    public void expirar() {
        tempoVida = duracao;
    }

    /**
     * true quando o tiro atinge apenas um inimigo e some (sem dano em área).
     * A verificação de dano em DanoTiro expira o tiro após o primeiro acerto.
     */
    public boolean isUnicoAlvo() {
        return false;
    }

    /**
     * Hook chamado por DanoTiro sempre que o dano do tiro é efetivamente
     * aplicado em um inimigo. Subclasses sobrescrevem para aplicar efeitos
     * (queimadura, lentidão, roubo de vida). {@link #dono} aponta o atirador.
     */
    public void aoAcertar(EnemyTemplate inimigo) {
        // padrão: sem efeito adicional
    }

    // ── Targeting / movimento (genérico e reutilizável) ────────

    /**
     * Injeta a lista de inimigos vivos da sala atual (mesma referência do
     * EnemyManager, compartilhada — nunca copiar nem modificar aqui).
     */
    public void setInimigos(List<EnemyTemplate> inimigos) {
        this.inimigos = inimigos;
    }

    /** Inimigo vivo mais próximo do atirador (null se não houver nenhum). */
    protected EnemyTemplate inimigoMaisProximo() {
        if (inimigos == null || inimigos.isEmpty() || dono == null) return null;
        EnemyTemplate maisProximo = null;
        float melhorDistancia = Float.MAX_VALUE;
        for (EnemyTemplate inimigo : inimigos) {
            if (inimigo == null || inimigo.isMorto()) continue;
            float ddx = inimigo.getDx() - dono.getDx();
            float ddy = inimigo.getDy() - dono.getDy();
            float distancia = ddx * ddx + ddy * ddy;
            if (distancia < melhorDistancia) {
                melhorDistancia = distancia;
                maisProximo = inimigo;
            }
        }
        return maisProximo;
    }

    /**
     * Move posição de desenho e hitbox juntos, centrando a hitbox em
     * (centroX, centroY). Base para qualquer tiro que se desloca/segue alvo.
     */
    protected void sincronizarPosicao(float centroX, float centroY) {
        xHitBox = Math.round(centroX - hitBox.width / 2f);
        yHitBox = Math.round(centroY - hitBox.height / 2f);
        hitBox.setPosition(xHitBox, yHitBox);
        x = Math.round(centroX - tamanhoDraw / 2f);
        y = Math.round(centroY - tamanhoDraw / 2f);
    }

    /**
     * Define a direção unitária de movimento apontando para o centro do alvo.
     * Sem alvo, segue a direção do olhar do player no momento do disparo.
     */
    protected void definirDirecaoPara(EnemyTemplate alvo) {
        if (alvo != null) {
            Rectangle hbAlvo = alvo.getHitBox();
            float ddx = (hbAlvo.x + hbAlvo.width / 2f) - (xHitBox + hitBox.width / 2f);
            float ddy = (hbAlvo.y + hbAlvo.height / 2f) - (yHitBox + hitBox.height / 2f);
            float modulo = (float) Math.sqrt(ddx * ddx + ddy * ddy);
            if (modulo > 0f) {
                dirMovimentoX = ddx / modulo;
                dirMovimentoY = ddy / modulo;
                return;
            }
        }
        dirMovimentoX = spawnEsquerda ? -1f : 1f;
        dirMovimentoY = 0f;
    }

    /** Desloca o tiro em linha reta na direção atual de movimento. */
    protected void moverNaDirecao(float velocidade, float delta) {
        float centroX = xHitBox + hitBox.width / 2f + dirMovimentoX * velocidade * delta;
        float centroY = yHitBox + hitBox.height / 2f + dirMovimentoY * velocidade * delta;
        sincronizarPosicao(centroX, centroY);
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


