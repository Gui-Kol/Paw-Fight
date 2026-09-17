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

    // Cache de animações: [0] = direita, [1] = esquerda
    private static final int DIR_RIGHT = 0;
    private static final int DIR_LEFT = 1;

    @SuppressWarnings("unchecked")
    private final Animation<TextureRegion>[] idleCache = new Animation[2];
    @SuppressWarnings("unchecked")
    private final Animation<TextureRegion>[] walkCache = new Animation[2];
    @SuppressWarnings("unchecked")
    private final Animation<TextureRegion>[] hurtCache = new Animation[2];
    @SuppressWarnings("unchecked")
    private final Animation<TextureRegion>[] deadCache = new Animation[2];
    @SuppressWarnings("unchecked")
    private final Animation<TextureRegion>[] atackCache = new Animation[2];
    @SuppressWarnings("unchecked")
    private final Animation<TextureRegion>[] specialAtackCache = new Animation[2];

    // Animações ativas (apontam para cache da direção atual)
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

    private DefinirSprite withDirection(DefinirSprite def, boolean esquerda) {
        if (def == null) return null;
        return new DefinirSprite(def.texture(), def.numFrame(), def.frameDuration(), def.reverse(), esquerda);
    }

    public void rebuildAnimations() {
        if (idleDefinition == null) return;

        // Constrói versões direita (olhandoEsquerda = false)
        idleCache[DIR_RIGHT] = motorAnimacao.animar(withDirection(idleDefinition, false));
        walkCache[DIR_RIGHT] = motorAnimacao.animar(withDirection(walkDefinition, false));
        hurtCache[DIR_RIGHT] = motorAnimacao.animar(withDirection(hurtDefinition, false));
        // A animação de morte NÃO loopa — toca uma vez e fica no último frame (isDeadAnimationFinished)
        deadCache[DIR_RIGHT] = motorAnimacao.criarAnimacao(withDirection(deadDefinition, false), false);

        // Constrói versões esquerda (olhandoEsquerda = true)
        idleCache[DIR_LEFT] = motorAnimacao.animar(withDirection(idleDefinition, true));
        walkCache[DIR_LEFT] = motorAnimacao.animar(withDirection(walkDefinition, true));
        hurtCache[DIR_LEFT] = motorAnimacao.animar(withDirection(hurtDefinition, true));
        deadCache[DIR_LEFT] = motorAnimacao.criarAnimacao(withDirection(deadDefinition, true), false);

        if (atackDefinition != null) {
            atackCache[DIR_RIGHT] = motorAnimacao.animar(withDirection(atackDefinition, false));
            atackCache[DIR_LEFT] = motorAnimacao.animar(withDirection(atackDefinition, true));
        }
        if (specialAtackDefinition != null) {
            specialAtackCache[DIR_RIGHT] = motorAnimacao.animar(withDirection(specialAtackDefinition, false));
            specialAtackCache[DIR_LEFT] = motorAnimacao.animar(withDirection(specialAtackDefinition, true));
        }

        // Aponta animações ativas para a direção atual
        applyDirection(lastOlhandoEsquerda);
        animationsDirty = false;
    }

    private void applyDirection(boolean esquerda) {
        int dir = esquerda ? DIR_LEFT : DIR_RIGHT;
        idleAnimation = idleCache[dir];
        walkAnimation = walkCache[dir];
        hurtAnimation = hurtCache[dir];
        deadAnimation = deadCache[dir];
        atackAnimation = atackCache[dir];
        specialAtackAnimation = specialAtackCache[dir];
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

            // Se o cache já foi construído, apenas troca a direção — SEM rebuild
            if (!animationsDirty && idleCache[DIR_RIGHT] != null) {
                applyDirection(olhandoEsquerda);
                return true;
            }

            // Se cache não existe ainda, marca dirty para rebuild completo
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

    /** Retorna true quando a animação de morte (dead) terminou de tocar por completo. */
    public boolean isDeadAnimationFinished() {
        return deadAnimation != null && deadAnimation.isAnimationFinished(stateTime);
    }
}
