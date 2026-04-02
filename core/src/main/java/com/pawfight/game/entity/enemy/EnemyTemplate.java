package com.pawfight.game.entity.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.AudioEngine;
import com.pawfight.game.engine.design.animation.MotorAnimacao;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.entity.Entidade;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public abstract class EnemyTemplate implements Entidade {
    // Constantes
    private static final float DANO_COOLDOWN_DURATION = 0.5f;

    protected PlayerTemplate player;

    //animação
    protected DefinirSprite idleDefinition;
    protected DefinirSprite walkDefinition;
    protected DefinirSprite deadDefinition;
    protected DefinirSprite hurtDefinition;
    protected DefinirSprite atackDefinition;
    protected DefinirSprite specialAtackDefinition;
    protected Texture idleSheet;
    protected Texture walkSheet;
    protected Texture deadSheet;
    protected Texture hurtSheet;
    protected Texture atackSheet;
    protected Texture specialAtackSheet;
    protected Animation<TextureRegion> hurtAnimation;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> deadAnimation;
    protected Animation<TextureRegion> atackAnimation;
    protected Animation<TextureRegion> specialAtackAnimation;
    protected final MotorAnimacao MotorAnimacao = new MotorAnimacao();
    protected boolean olhandoEsquerda = false;
    private boolean lastOlhandoEsquerda = false;
    private boolean animationsDirty = true;
    protected boolean moving = false;
    protected boolean atacando = false;
    protected boolean atacandoEspecial = false;
    protected boolean podeTomarDano = true;

    protected String nome;
    protected int vida;
    protected int vidaBase;
    protected int forca;
    protected int velocidade;
    protected int dx, dy;
    protected boolean morto = false;
    protected boolean hurt = false;
    protected boolean forte;
    protected float hurtTime = 0f;
    protected final float HURT_DURATION = 0.3f;
    protected float stateTime;
    protected float ataqueTimer = 0f;
    protected final float ATAQUE_COOLDOWN = 1.5f;
    protected final float ATAQUE_DURATION = 0.5f;
    protected final float DISTANCIA_ATAQUE = 60f;
    private float danoCooldown = 0f;

    protected Rectangle hitBox;
    protected Renderizar renderizar = Renderizar.INSTANCE;
    protected int TAMANHO_PX = 64;
    protected int HITBOX_SIZE = 20;
    protected int HITBOX_OFFSET_X = -10;
    protected int HITBOX_OFFSET_Y = 0;

    protected AudioEngine audioEngine = new AudioEngine();
    protected Music audioDano;
    protected Music audioMorte;

    protected List<EnemyTemplate> enemiesList;

    public EnemyTemplate(int dx, int dy, boolean forte, PlayerTemplate player) {
        this.player = player;
        this.dx = dx;
        this.dy = dy;
        this.stateTime = 0f;
        this.forte = forte;
        criarHitBox();
        // Carrega texturas UMA VEZ e cria definições iniciais
        loadTextures();
        updateSpriteDefinitions();
        rebuildAnimations();
        definirAudios();
    }

    // Métodos abstratos
    public abstract void ataqueBasico();

    public abstract void criarHitBox();

    public abstract void ataqueEspecial();

    public abstract void loadTextures();

    public abstract void updateSpriteDefinitions();

    public abstract EnemyTemplate cloneEnemy();

    public abstract String getNome();

    private void rebuildAnimations() {
        idleAnimation = MotorAnimacao.animar(idleDefinition);
        walkAnimation = MotorAnimacao.animar(walkDefinition);
        hurtAnimation = MotorAnimacao.animar(hurtDefinition);
        deadAnimation = MotorAnimacao.animar(deadDefinition);
        atackAnimation = MotorAnimacao.animar(atackDefinition);
        specialAtackAnimation = MotorAnimacao.animar(specialAtackDefinition);
        animationsDirty = false;
    }

    public void setLocation(int x, int y) {
        dx = x;
        dy = y;
        hitBox.setPosition(dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X, dy + HITBOX_OFFSET_Y);
    }

    public void setEnemiesList(List<EnemyTemplate> enemiesList) {
        this.enemiesList = enemiesList;
    }

    // Métodos comuns
    public void update(float delta) {
        if (player != null && player.isPause()){
            return;
        }
        TAMANHO_PX = getTamanho();
        if (!morto) {
            executarIA(delta);
            stateTime += delta;
            ataqueTimer += delta;

            // Atualiza animações apenas quando direção muda
            if (olhandoEsquerda != lastOlhandoEsquerda) {
                lastOlhandoEsquerda = olhandoEsquerda;
                updateSpriteDefinitions();
                animationsDirty = true;
            }

            // Atualizar cooldown de dano
            if (!podeTomarDano) {
                danoCooldown += delta;
                if (danoCooldown >= DANO_COOLDOWN_DURATION) {
                    podeTomarDano = true;
                    danoCooldown = 0f;
                }
            }

            if (hurt) {
                hurtTime += delta;
                if (hurtTime >= HURT_DURATION) {
                    hurt = false;
                    hurtTime = 0f;
                }
            }

            if (atacando && stateTime >= ATAQUE_DURATION) {
                atacando = false;
            }
        }
    }

    public void executarIA(float delta) {
        andarIA(delta);

        float distanciaAoPlayer = calcularDistanciaAoPlayer();

        if (distanciaAoPlayer <= DISTANCIA_ATAQUE && ataqueTimer >= ATAQUE_COOLDOWN) {
            ataqueBasico();
            ataqueTimer = 0f;
            stateTime = 0f;
        }

        ataqueEspecial();
    }

    public abstract void andarIA(float delta);
    public abstract int getTamanho();

    protected float calcularDistanciaAoPlayer() {
        if (player == null) return Float.MAX_VALUE;
        float dx = this.dx - player.getDx();
        float dy = this.dy - player.getDy();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public void dano(int forca) {
        if (podeTomarDano) {
            podeTomarDano = false;
            this.vida -= forca;
            if (vida <= 0) {
                morto = true;
                player.moedaUp(moedasMorte());
                audioEngine.efeito(audioMorte);
            } else {
                hurt = true;
                hurtTime = 0f;
                audioEngine.efeito(audioDano);
            }
            Gdx.app.log(getNome(), "Tomou " + forca + " de dano!");
        }
    }

    protected TextureRegion animaAtual() {
        if (animationsDirty) {
            rebuildAnimations();
        }

        if (morto) {
            if (deadAnimation.isAnimationFinished(stateTime)) {
                return deadAnimation.getKeyFrames()[deadAnimation.getKeyFrames().length - 1];
            } else {
                return deadAnimation.getKeyFrame(stateTime, false);
            }
        }
        if (hurt) {
            return hurtAnimation.getKeyFrame(hurtTime, false);
        }
        if (atacando){
            return atackAnimation.getKeyFrame(stateTime, false);
        }
        if (atacandoEspecial){
            return specialAtackAnimation.getKeyFrame(stateTime, false);
        }
        return moving ? walkAnimation.getKeyFrame(stateTime, true) : idleAnimation.getKeyFrame(stateTime, true);
    }
    protected abstract void definirAudios();

    public void drawSprite(SpriteBatch batch) {
        batch.draw(animaAtual(), dx, dy, TAMANHO_PX, TAMANHO_PX);
    }

    public void drawHitbox(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        renderizar.hitboxDraw(shapeRenderer, hitBox);
        extraDraw(batch, shapeRenderer);
    }

    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        var cameraCombined = player.getCamera().combined;

        shapeRenderer.setProjectionMatrix(cameraCombined);
        batch.setProjectionMatrix(cameraCombined);

        batch.begin();
        drawSprite(batch);
        batch.end();
        drawHitbox(batch, shapeRenderer);
    }

    protected abstract int moedasMorte();

    public abstract void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer);

    public void dispose() {
        if (idleSheet != null) idleSheet.dispose();
        if (walkSheet != null) walkSheet.dispose();
        if (deadSheet != null) deadSheet.dispose();
        if (hurtSheet != null) hurtSheet.dispose();
        if (atackSheet != null) atackSheet.dispose();
        if (specialAtackSheet != null) specialAtackSheet.dispose();
    }

    public int getVelocidade() {
        return velocidade;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public boolean isMorto() {
        return morto;
    }

    // ── Getters do contrato Entidade ──────────────────────────

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaBase() {
        return vidaBase;
    }

    public int getForca() {
        return forca;
    }

    public boolean isOlhandoEsquerda() {
        return olhandoEsquerda;
    }

    // ─────────────────────────────────────────────────────────

    public void setForte(boolean forte) {
        if (this.forte != forte) {
            this.forte = forte;
            aplicarStatsForte();
        }
    }

    protected void aplicarStatsForte() {
        // implementação padrão vazia — subclasses sobrescrevem
    }
}
