package com.pawfight.game.entity.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.Entidade;
import com.pawfight.game.entity.component.AnimacaoComponent;
import com.pawfight.game.entity.component.AudioComponent;
import com.pawfight.game.entity.component.StatsComponent;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public abstract class EnemyTemplate implements Entidade {

    // ── Componentes ────────────────────────────────────────────
    protected final AnimacaoComponent animacao = new AnimacaoComponent();
    protected final AudioComponent audio = new AudioComponent();
    protected StatsComponent stats;

    // ── Referências ────────────────────────────────────────────
    protected PlayerTemplate player;
    protected List<EnemyTemplate> enemiesList;

    // ── Estado espacial ────────────────────────────────────────
    protected int dx, dy;
    protected Rectangle hitBox;
    protected int TAMANHO_PX = 64;
    protected int HITBOX_SIZE = 20;
    protected int HITBOX_OFFSET_X = -10;
    protected int HITBOX_OFFSET_Y = 0;
    protected boolean olhandoEsquerda = false;
    protected boolean moving = false;

    // ── Estado de combate ──────────────────────────────────────
    protected String nome;
    protected boolean forte;
    protected boolean atacando = false;
    protected boolean atacandoEspecial = false;
    protected float ataqueTimer = 0f;
    protected final float ATAQUE_COOLDOWN = 1.5f;
    protected final float ATAQUE_DURATION = 0.5f;
    protected final float DISTANCIA_ATAQUE = 60f;


    // ── Construtor ─────────────────────────────────────────────

    public EnemyTemplate(int dx, int dy, boolean forte, PlayerTemplate player) {
        DadosInimigo dadosInimigo = dadosInimigo();
        float multiplicador = forte ? dadosInimigo.multiplicador() : 1;

        this.nome = dadosInimigo.nome();
        this.player = player;
        this.dx = dx;
        this.dy = dy;
        this.forte = forte;

        // 1. Stats
        stats = new StatsComponent(
            dadosInimigo.VidaBase() * (int) multiplicador,
            dadosInimigo.Forca() * (int) multiplicador,
            dadosInimigo.Velocidade() * (int) multiplicador
        );

        // 2. Áudio
        audio.initEnemy(dadosInimigo.audioDano(), dadosInimigo.audioMorte());

        // 3. Animação
        animacao.initTextures(
            dadosInimigo.idleSheet(),
            dadosInimigo.walkSheet(),
            dadosInimigo.deadSheet(),
            dadosInimigo.hurtSheet(),
            dadosInimigo.atackSheet(),
            dadosInimigo.specialAtackSheet()
        );

        // 4. Hitbox
        criarHitBox();
        updateSpriteDefinitions();
        animacao.rebuildAnimations();
    }

    // ── Métodos abstratos ──────────────────────────────────────

    protected abstract DadosInimigo dadosInimigo();
    public abstract void ataqueBasico();
    public abstract void ataqueEspecial();
    public abstract void updateSpriteDefinitions();
    public abstract EnemyTemplate cloneEnemy();
    public abstract void andarIA(float delta);
    public abstract int getTamanho();
    protected abstract int moedasMorte();
    public abstract void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer);

    // ── Hitbox ─────────────────────────────────────────────────

    public void criarHitBox() {
        this.hitBox = new Rectangle(
            dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X,
            dy + HITBOX_OFFSET_Y,
            HITBOX_SIZE,
            HITBOX_SIZE
        );
    }

    public void setLocation(int x, int y) {
        dx = x;
        dy = y;
        hitBox.setPosition(dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X, dy + HITBOX_OFFSET_Y);
    }

    public void setEnemiesList(List<EnemyTemplate> enemiesList) {
        this.enemiesList = enemiesList;
    }

    // ══════════════════════════════════════════════════════════
    //  UPDATE
    // ══════════════════════════════════════════════════════════

    public void update(float delta) {
        if (player != null && player.isPause()) {
            return;
        }
        TAMANHO_PX = getTamanho();

        if (!stats.isMorto()) {
            executarIA(delta);
            ataqueTimer += delta;

            // Animação — troca direção do cache (sem rebuild)
            animacao.checkDirectionChange(olhandoEsquerda);

            // Duração do ataque
            if (atacando && animacao.getStateTime() >= ATAQUE_DURATION) {
                atacando = false;
            }
        }

        // Timers (sempre rodam, mesmo morto — para animação de morte e cooldowns)
        animacao.updateStateTime(delta);
        stats.updateTimers(delta);
    }

    public void executarIA(float delta) {
        andarIA(delta);

        float distanciaAoPlayer = calcularDistanciaAoPlayer();

        if (distanciaAoPlayer <= DISTANCIA_ATAQUE && ataqueTimer >= ATAQUE_COOLDOWN) {
            ataqueBasico();
            ataqueTimer = 0f;
            animacao.resetStateTime();
        }

        ataqueEspecial();
    }

    protected float calcularDistanciaAoPlayer() {
        if (player == null) return Float.MAX_VALUE;
        float ddx = this.dx - player.getDx();
        float ddy = this.dy - player.getDy();
        return (float) Math.sqrt(ddx * ddx + ddy * ddy);
    }

    // ══════════════════════════════════════════════════════════
    //  AÇÕES
    // ══════════════════════════════════════════════════════════

    public void dano(int forca) {
        if (!stats.aplicarDano(forca)) return;
        if (stats.isMorto()) {
            animacao.resetStateTime();
            player.moedaUp(moedasMorte());
            audio.playMorte();
        } else {
            audio.playDano();
        }
        Gdx.app.log(nome, "Tomou " + forca + " de dano!");
    }

    // ══════════════════════════════════════════════════════════
    //  DRAW
    // ══════════════════════════════════════════════════════════

    protected TextureRegion animaAtual() {
        return animacao.animaAtual(
            stats.isMorto(), stats.isHurt(),
            atacando, atacandoEspecial,
            moving, stats.getHurtTime()
        );
    }

    public void drawSprite(SpriteBatch batch) {
        batch.draw(animaAtual(), dx, dy, TAMANHO_PX, TAMANHO_PX);
    }


    // ══════════════════════════════════════════════════════════
    //  FORTE (modificador)
    // ══════════════════════════════════════════════════════════

    public void setForte(boolean forte) {
        if (this.forte != forte) {
            this.forte = forte;
            aplicarStatsForte();
        }
    }

    protected void aplicarStatsForte() {
        // implementação padrão vazia — subclasses sobrescrevem
    }

    // ══════════════════════════════════════════════════════════
    //  GETTERS (contrato Entidade + API pública)
    // ══════════════════════════════════════════════════════════

    public int getDx()              { return dx; }
    public int getDy()              { return dy; }
    public Rectangle getHitBox()    { return hitBox; }
    public int getVida()            { return stats.getVida(); }
    public int getVidaBase()        { return stats.getVidaBase(); }
    public int getForca()           { return stats.getForca(); }
    public int getVelocidade()      { return stats.getVelocidade(); }
    public boolean isMorto()        { return stats.isMorto(); }
    public boolean isOlhandoEsquerda() { return olhandoEsquerda; }
}
