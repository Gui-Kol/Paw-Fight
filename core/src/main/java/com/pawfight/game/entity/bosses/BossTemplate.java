package com.pawfight.game.entity.bosses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.ArrayList;
import java.util.List;

// Base para bosses multi-fase: fases registradas em definirFases; update() cuida do ciclo e das transições; ataques delegados à fase vigente.
public abstract class BossTemplate extends EnemyTemplate {

    private static final String TAG = "BossTemplate";

    protected int faseAtual = 0;
    protected final List<FaseBoss> fases = new ArrayList<>();

    // ataqueNormal/ataqueEspecial espelham a fase ativa (ver aplicarAnimacoesFase)
    protected Animation<TextureRegion> ataqueNormal;
    protected Animation<TextureRegion> ataqueEspecial;
    protected Animation<TextureRegion> transicaoFase;
    protected boolean emTransicao = false;

    public BossTemplate(int dx, int dy, boolean forte, PlayerTemplate player) {
        super(dx, dy, forte, player);
        definirFases(fases);
        if (fases.isEmpty()) {
            Gdx.app.error(TAG, nome + " foi criado sem nenhuma fase!");
            return;
        }
        aplicarAnimacoesFase(fases.get(0));
    }

    // Registra as fases do boss na lista, em ordem (da inicial à final).
    protected abstract void definirFases(List<FaseBoss> fases);

    public abstract void executarAtaqueNormal();
    public abstract void executarAtaqueEspecial();
    public abstract void mudarFase(int novaFase);
    public abstract void atualizarEstado();

    @Override
    public void update(float delta) {
        super.update(delta);
        if (stats.isMorto()) return;

        // Durante a transição o boss não age; ela dura até a animação terminar
        if (emTransicao) {
            if (transicaoFase != null && !transicaoFase.isAnimationFinished(animacao.getStateTime())) {
                return;
            }
            emTransicao = false;
        }

        atualizarEstado();

        if (atacandoEspecial && animacao.getStateTime() >= ATAQUE_DURATION) {
            atacandoEspecial = false;
        }
    }

    // Dispara mudarFase quando a vida atinge o vidaLimiar da fase atual.
    protected void verificarTransicaoFase() {
        FaseBoss fase = faseVigente();
        if (fase == null || faseAtual >= fases.size() - 1) return;
        if (fase.deveTransicionar(stats.getVida(), stats.getVidaBase())) {
            mudarFase(faseAtual + 1);
        }
    }

    // Ganchos da IA herdada — viram o ciclo de ataque do boss (bloqueados em transição)
    @Override
    public final void ataqueBasico() {
        if (!emTransicao) executarAtaqueNormal();
    }

    @Override
    public final void ataqueEspecial() {
        if (!emTransicao) executarAtaqueEspecial();
    }

    // Copia as animações da fase para os campos ativos (sem alocação por frame).
    protected void aplicarAnimacoesFase(FaseBoss fase) {
        this.ataqueNormal = fase.getAnimacaoAtaque();
        this.ataqueEspecial = fase.getAnimacaoDefesa();
    }

    // Começa a tocar a animação de transição (se houver uma definida).
    protected void iniciarTransicao() {
        if (transicaoFase == null) return;
        emTransicao = true;
        animacao.resetStateTime();
    }

    @Override
    protected TextureRegion animaAtual() {
        float stateTime = animacao.getStateTime();
        if (emTransicao && transicaoFase != null) {
            return transicaoFase.getKeyFrame(stateTime, false);
        }
        if (atacando && ataqueNormal != null) {
            return ataqueNormal.getKeyFrame(stateTime, true);
        }
        if (atacandoEspecial && ataqueEspecial != null) {
            return ataqueEspecial.getKeyFrame(stateTime, true);
        }
        return super.animaAtual();
    }

    public PlayerTemplate getPlayer() { return player; }

    // Sinaliza o ataque normal (chamado pelas fases ao acertar).
    public void setAtacando(boolean atacando) { this.atacando = atacando; }

    // Sinaliza o ataque especial (chamado pelas fases ao acertar).
    public void setAtacandoEspecial(boolean atacandoEspecial) { this.atacandoEspecial = atacandoEspecial; }

    public int getFaseAtual() { return faseAtual; }
    public List<FaseBoss> getFases() { return fases; }
    public boolean isEmTransicao() { return emTransicao; }
    public Animation<TextureRegion> getTransicaoFase() { return transicaoFase; }

    // Fase vigente; null quando a lista está vazia ou o índice é inválido.
    protected FaseBoss faseVigente() {
        if (fases.isEmpty() || faseAtual < 0 || faseAtual >= fases.size()) return null;
        return fases.get(faseAtual);
    }
}
