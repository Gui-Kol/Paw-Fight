package com.pawfight.game.entity.component;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.engine.design.animation.MotorAnimacao;

public class AnimacaoComponent {

    private final MotorAnimacao motorAnimacao = new MotorAnimacao();

    // Texturas (carregadas uma vez)
    private Texture idleSheet;
    private Texture walkSheet;
    private Texture deadSheet;
    private Texture hurtSheet;
    private Texture atackSheet;
    private Texture specialAtackSheet;

    // Definições de sprite (atualizadas quando direção muda)
    private DefinirSprite idleDefinition;
    private DefinirSprite walkDefinition;
    private DefinirSprite deadDefinition;
    private DefinirSprite hurtDefinition;
    private DefinirSprite atackDefinition;
    private DefinirSprite specialAtackDefinition;

    // Animações compiladas
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private Animation<TextureRegion> deadAnimation;
    private Animation<TextureRegion> atackAnimation;
    private Animation<TextureRegion> specialAtackAnimation;

    private boolean animationsDirty = true;
    private boolean lastOlhandoEsquerda = false;
    private float stateTime = 0f;

    // ── Inicialização ──────────────────────────────────────────

    public void initTextures(Texture idle, Texture walk, Texture dead, Texture hurt) {
        this.idleSheet = idle;
        this.walkSheet = walk;
        this.deadSheet = dead;
        this.hurtSheet = hurt;
    }

    public void initTextures(Texture idle, Texture walk, Texture dead, Texture hurt,
                             Texture atack, Texture specialAtack) {
        initTextures(idle, walk, dead, hurt);
        this.atackSheet = atack;
        this.specialAtackSheet = specialAtack;
    }

    public void setDefinitions(DefinirSprite idle, DefinirSprite walk, DefinirSprite dead, DefinirSprite hurt) {
        this.idleDefinition = idle;
        this.walkDefinition = walk;
        this.deadDefinition = dead;
        this.hurtDefinition = hurt;
        this.animationsDirty = true;
    }

    public void setDefinitions(DefinirSprite idle, DefinirSprite walk, DefinirSprite dead, DefinirSprite hurt,
                               DefinirSprite atack, DefinirSprite specialAtack) {
        setDefinitions(idle, walk, dead, hurt);
        this.atackDefinition = atack;
        this.specialAtackDefinition = specialAtack;
    }

    public void rebuildAnimations() {
        if (idleDefinition == null) return;
        idleAnimation = motorAnimacao.animar(idleDefinition);
        walkAnimation = motorAnimacao.animar(walkDefinition);
        hurtAnimation = motorAnimacao.animar(hurtDefinition);
        deadAnimation = motorAnimacao.animar(deadDefinition);
        if (atackDefinition != null) {
            atackAnimation = motorAnimacao.animar(atackDefinition);
        }
        if (specialAtackDefinition != null) {
            specialAtackAnimation = motorAnimacao.animar(specialAtackDefinition);
        }
        animationsDirty = false;
    }

    // ── Update ─────────────────────────────────────────────────

    public void updateStateTime(float delta) {
        stateTime += delta;
    }

    public void resetStateTime() {
        stateTime = 0f;
    }

    public boolean checkDirectionChange(boolean olhandoEsquerda) {
        if (olhandoEsquerda != lastOlhandoEsquerda) {
            lastOlhandoEsquerda = olhandoEsquerda;
            animationsDirty = true;
            return true;
        }
        return false;
    }

    // ── Obter frame atual ──────────────────────────────────────

    public TextureRegion animaAtual(boolean morto, boolean hurt, boolean moving, float hurtTime) {
        if (animationsDirty) {
            rebuildAnimations();
        }
        if (morto) {
            if (deadAnimation.isAnimationFinished(stateTime)) {
                return deadAnimation.getKeyFrames()[deadAnimation.getKeyFrames().length - 1];
            }
            return deadAnimation.getKeyFrame(stateTime, false);
        }
        if (hurt) {
            return hurtAnimation.getKeyFrame(hurtTime, false);
        }
        return moving
            ? walkAnimation.getKeyFrame(stateTime, true)
            : idleAnimation.getKeyFrame(stateTime, true);
    }

    public TextureRegion animaAtual(boolean morto, boolean hurt, boolean atacando,
                                    boolean atacandoEspecial, boolean moving, float hurtTime) {
        if (animationsDirty) {
            rebuildAnimations();
        }
        if (morto) {
            if (deadAnimation.isAnimationFinished(stateTime)) {
                return deadAnimation.getKeyFrames()[deadAnimation.getKeyFrames().length - 1];
            }
            return deadAnimation.getKeyFrame(stateTime, false);
        }
        if (hurt) {
            return hurtAnimation.getKeyFrame(hurtTime, false);
        }
        if (atacando && atackAnimation != null) {
            return atackAnimation.getKeyFrame(stateTime, false);
        }
        if (atacandoEspecial && specialAtackAnimation != null) {
            return specialAtackAnimation.getKeyFrame(stateTime, false);
        }
        return moving
            ? walkAnimation.getKeyFrame(stateTime, true)
            : idleAnimation.getKeyFrame(stateTime, true);
    }

    // ── Getters (para subclasses acessarem texturas/direção) ───

    public Texture getIdleSheet() { return idleSheet; }
    public Texture getWalkSheet() { return walkSheet; }
    public Texture getDeadSheet() { return deadSheet; }
    public Texture getHurtSheet() { return hurtSheet; }
    public Texture getAtackSheet() { return atackSheet; }
    public Texture getSpecialAtackSheet() { return specialAtackSheet; }
    public float getStateTime() { return stateTime; }
    public MotorAnimacao getMotorAnimacao() { return motorAnimacao; }
}

