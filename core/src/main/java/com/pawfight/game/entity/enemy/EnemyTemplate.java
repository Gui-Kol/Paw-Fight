package com.pawfight.game.entity.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.player.PlayerTemplate;

public abstract class EnemyTemplate {
    protected int vida;
    protected int vidaBase;
    protected int forca;
    protected int velocidade;
    protected int dx, dy;
    protected boolean morto = false;
    protected boolean hurt = false;
    protected boolean miniBoss;
    protected float hurtTime = 0f;
    protected static final float HURT_DURATION = 0.3f;
    protected float stateTime;
    protected float ataqueTimer = 0f;

    protected Rectangle hitBox;
    protected static int TAMANHO_PX = 64;
    protected static int HITBOX_SIZE = 20;
    protected static int HITBOX_OFFSET_X = -10;
    protected static int HITBOX_OFFSET_Y = 0;

    public EnemyTemplate(int dx, int dy, int vidaBase, int forca, int velocidade) {
        this.dx = dx;
        this.dy = dy;
        this.vidaBase = vidaBase;
        this.vida = vidaBase;
        this.forca = forca;
        this.velocidade = velocidade;
        this.stateTime = 0f;
        this.hitBox = new Rectangle(
            dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X,
            dy + HITBOX_OFFSET_Y,
            HITBOX_SIZE,
            HITBOX_SIZE
        );
    }

    // Métodos abstratos de ataque
    public abstract void ataqueBasico(PlayerTemplate player);

    public abstract void ataqueEspecial(PlayerTemplate player);

    public abstract void executarIA(PlayerTemplate player, float delta);

    public abstract void texture();

    public abstract String getTipo();

    public abstract void miniBoss();

    // Métodos comuns
    public void update(float delta) {
        if (!morto) {
            executarIA(delta);
            stateTime += delta;

            if (hurt) {
                hurtTime += delta;
                if (hurtTime >= HURT_DURATION) {
                    hurt = false;
                    hurtTime = 0f;
                }
            }
        }
    }

    public void executarIA(float delta) {
        // Será implementado por subclasses
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

    public abstract void draw(SpriteBatch batch, ShapeRenderer shapeRenderer);

    public void dispose() {
        // Será implementado por subclasses
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

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public boolean isMorto() {
        return morto;
    }

    public boolean isHurt() {
        return hurt;
    }

    public float getAtaqueTimer() {
        return ataqueTimer;
    }

    public void setAtaqueTimer(float timer) {
        this.ataqueTimer = timer;
    }
}
