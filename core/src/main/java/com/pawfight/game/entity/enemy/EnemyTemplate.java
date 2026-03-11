package com.pawfight.game.entity.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.animation.AnimationEngine;
import com.pawfight.game.engine.animation.SpriteDefinition;
import com.pawfight.game.engine.phisics.DrawHitBox;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public abstract class EnemyTemplate {
    protected PlayerTemplate player;

    //animação
    protected SpriteDefinition idleDefinition;
    protected SpriteDefinition walkDefinition;
    protected SpriteDefinition deadDefinition;
    protected SpriteDefinition hurtDefinition;
    protected SpriteDefinition atackDefinition;
    protected SpriteDefinition specialAtackDefinition;
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
    protected final AnimationEngine animationEngine = new AnimationEngine();
    protected boolean olhandoEsquerda = false;
    protected boolean moving = false;
    protected boolean atacando = false;
    protected boolean atacandoEspecial = false;

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
    protected static final float HURT_DURATION = 0.3f;
    protected float stateTime;
    protected float ataqueTimer = 0f;
    protected static final float ATAQUE_COOLDOWN = 1.5f; // Cooldown entre ataques
    protected static final float ATAQUE_DURATION = 0.5f; // Duração da animação de ataque
    protected static final float DISTANCIA_ATAQUE = 60f; // Distância para atacar

    protected Rectangle hitBox;
    protected DrawHitBox drawHitBox;
    protected static int TAMANHO_PX = 64;
    protected static int HITBOX_SIZE = 20;
    protected static int HITBOX_OFFSET_X = -10;
    protected static int HITBOX_OFFSET_Y = 0;

    protected List<EnemyTemplate> enemiesList;

    public EnemyTemplate(int dx, int dy, boolean forte, PlayerTemplate player) {
        this.player = player;
        this.dx = dx;
        this.dy = dy;
        this.stateTime = 0f;
        this.forte = forte;
        this.drawHitBox = new DrawHitBox();
        texture();
    }

    // Métodos abstratos de ataque
    public abstract void ataqueBasico();

    public abstract void criarHitBox();

    public abstract void ataqueEspecial();

    public abstract void texture();

    public abstract EnemyTemplate cloneEnemy();

    public void setLocation(int x, int y) {
        criarHitBox();
        dx = x;
        dy = y;
        hitBox.setPosition(dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X, dy + HITBOX_OFFSET_Y);
    }

    public void setEnemiesList(List<EnemyTemplate> enemiesList) {
        this.enemiesList = enemiesList;
    }

    // Métodos comuns
    public void update(float delta) {
        TAMANHO_PX = getTamanho();
        if (!morto) {
            executarIA(delta);
            stateTime += delta;
            ataqueTimer += delta;

            if (hurt) {
                hurtTime += delta;
                if (hurtTime >= HURT_DURATION) {
                    hurt = false;
                    hurtTime = 0f;
                }
            }

            // Resetar estado de ataque após duração
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

    protected void moverEmDirecaoAoPlayer(float delta) {
        if (player == null) return;

        float playerX = player.getDx();
        float playerY = player.getDy();

        float deltaX = playerX - this.dx;
        float deltaY = playerY - this.dy;
        float distanciaTotal = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distanciaTotal > 0) {
            float moveX = (deltaX / distanciaTotal) * velocidade * delta;
            float moveY = (deltaY / distanciaTotal) * velocidade * delta;

            // Calcular nova posição
            float newDx = this.dx + moveX;
            float newDy = this.dy + moveY;

            // Criar hitbox temporária para nova posição
            Rectangle tempHitBox = new Rectangle(
                newDx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X,
                newDy + HITBOX_OFFSET_Y,
                HITBOX_SIZE,
                HITBOX_SIZE
            );

            // Verificar se a nova posição sobrepõe outro inimigo
            boolean canMove = true;
            if (enemiesList != null) {
                for (EnemyTemplate other : enemiesList) {
                    if (other != this && tempHitBox.overlaps(other.getHitBox())) {
                        canMove = false;
                        break;
                    }
                }
            }

            if (canMove) {
                this.dx = (int) newDx;
                this.dy = (int) newDy;
                olhandoEsquerda = deltaX < 0;
                moving = true;
            } else {
                moving = false;
            }

            hitBox.setPosition(dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X, dy + HITBOX_OFFSET_Y);
        } else {
            moving = false;
        }
    }

    public void dano(int forca) {
        this.vida -= forca;
        if (vida <= 0) {
            morto = true;
        } else {
            hurt = true;
            hurtTime = 0f;
        }
    }

    protected TextureRegion animaAtual() {
        idleAnimation = animationEngine.animar(idleDefinition);
        walkAnimation = animationEngine.animar(walkDefinition);
        hurtAnimation = animationEngine.animar(hurtDefinition);
        deadAnimation = animationEngine.animar(deadDefinition);
        atackAnimation = animationEngine.animar(atackDefinition);
        specialAtackAnimation = animationEngine.animar(specialAtackDefinition);

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

    public abstract void draw(SpriteBatch batch, ShapeRenderer shapeRenderer);

    public void dispose() {
        if (idleSheet != null) idleSheet.dispose();
        if (walkSheet != null) walkSheet.dispose();
        if (deadSheet != null) deadSheet.dispose();
        if (hurtSheet != null) hurtSheet.dispose();
        if (atackSheet != null) atackSheet.dispose();
        if (specialAtackSheet != null) specialAtackSheet.dispose();
    }

    // Getters e Setters
    public int getVida() {
        return vida;
    }

    public int getVidaBase() {
        return vidaBase;
    }

    public int getForca() {
        return forca;
    }

    public int getVelocidade() {
        return velocidade;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public boolean isMorto() {
        return morto;
    }
}
