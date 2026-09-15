package com.pawfight.game.engine.design.particle;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pawfight.game.engine.design.animation.MotorAnimacao;

public class Particula {

    // ── Configuração (definida no spawn) ───────────────────────
    private final Animation<TextureRegion> animacao;
    private final MotorAnimacao motorAnimacao;
    private final float x, y;
    private final float offsetX, offsetY;
    private final float w, h;
    private final float tempoVida;
    private final boolean reverse;

    // ── Estado ─────────────────────────────────────────────────
    private float stateTime = 0f;
    private boolean acabou = false;

    public Particula(Animation<TextureRegion> animacao, MotorAnimacao motorAnimacao,
                     float x, float y, float offsetX, float offsetY,
                     float w, float h, float tempoVida, boolean reverse) {
        this.animacao = animacao;
        this.motorAnimacao = motorAnimacao;
        this.x = x;
        this.y = y;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.w = w;
        this.h = h;
        this.tempoVida = tempoVida;
        this.reverse = reverse;
    }

    // ── Update ─────────────────────────────────────────────────
    public void atualizar(float delta) {
        if (acabou) return;
        stateTime += delta;
        if (stateTime >= tempoVida) {
            acabou = true;
        }
    }

    // ── Render ─────────────────────────────────────────────────
    public void desenhar(Batch batch) {
        if (acabou || animacao == null) return;
        TextureRegion frame = motorAnimacao.executarUmaVez(animacao, stateTime, reverse);
        float posX = x + offsetX - w / 2f;
        float posY = y + offsetY - h / 2f;
        batch.draw(frame, posX, posY, w, h);
    }

    // ── Getters ────────────────────────────────────────────────
    public boolean isAcabou() { return acabou; }
}