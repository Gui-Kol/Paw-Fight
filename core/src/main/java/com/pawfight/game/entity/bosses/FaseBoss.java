package com.pawfight.game.entity.bosses;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// Fase de um boss: comportamento (ataques), animações próprias e vidaLimiar — fração da vida base que dispara a transição.
public abstract class FaseBoss {

    protected final String nome;
    // Fração da vida base (0..1) que dispara a transição; 0 = última fase (até morrer).
    protected final float vidaLimiar;

    // Animações próprias da fase (null = não usada)
    protected Animation<TextureRegion> animacaoAtaque;
    protected Animation<TextureRegion> animacaoDefesa;
    protected Animation<TextureRegion> animacaoMovimentacao;

    protected FaseBoss(String nome, float vidaLimiar) {
        this.nome = nome;
        this.vidaLimiar = vidaLimiar;
    }

    public abstract void executarAtaqueNormal(BossTemplate boss);
    public abstract void executarAtaqueEspecial(BossTemplate boss);

    // Condição de transição: vida atual atingiu o limiar da fase.
    public boolean deveTransicionar(int vidaAtual, int vidaBase) {
        return vidaAtual <= vidaBase * vidaLimiar;
    }

    public String getNome() { return nome; }
    public float getVidaLimiar() { return vidaLimiar; }
    public Animation<TextureRegion> getAnimacaoAtaque() { return animacaoAtaque; }
    public Animation<TextureRegion> getAnimacaoDefesa() { return animacaoDefesa; }
    public Animation<TextureRegion> getAnimacaoMovimentacao() { return animacaoMovimentacao; }
}
