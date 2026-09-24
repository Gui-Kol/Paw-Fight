package com.pawfight.game.entity.bosses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pawfight.game.entity.bosses.infra.ControladorFases;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.ArrayList;
import java.util.List;

// Base para bosses multi-fase. O template cuida da infraestrutura comum do ciclo:
// pausa, sincronização de fases por limiar de vida, transição (com cancelamento de ataques pendentes),
// delegação dos ataques à fase vigente e gancho de morte. Cada boss descreve apenas suas fases,
// ataques e decisões específicas.
public abstract class BossTemplate extends EnemyTemplate {

    private static final String TAG = "BossTemplate";

    protected final List<FaseBoss> fases = new ArrayList<>();
    protected int faseAtual = 0;

    // ataqueNormal/ataqueEspecial espelham a fase ativa (ver aplicarAnimacoesFase)
    protected Animation<TextureRegion> ataqueNormal;
    protected Animation<TextureRegion> ataqueEspecial;
    protected Animation<TextureRegion> transicaoFase;
    protected boolean emTransicao = false;

    private ControladorFases controladorFases;
    private float deltaAtual;

    public BossTemplate(int dx, int dy, boolean forte, PlayerTemplate player) {
        super(dx, dy, forte, player);
        definirFases(fases);
        if (fases.isEmpty()) {
            Gdx.app.error(TAG, nome + " foi criado sem nenhuma fase!");
        } else {
            aplicarAnimacoesFase(fases.get(0));
        }
        controladorFases = new ControladorFases(fases);
    }

    // Registra as fases do boss na lista, em ordem (da inicial à final).
    protected abstract void definirFases(List<FaseBoss> fases);

    // Ciclo do boss: pausa congela tudo; fases sincronizam pela vida; a transição dura até a animação terminar.
    @Override
    public void update(float delta) {
        if (player != null && player.isPause()) return;
        if (!stats.isMorto()) verificarTransicaoFase();

        deltaAtual = delta;
        super.update(delta);

        if (stats.isMorto()) {
            aoMorrer();
            return;
        }

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

    // Dispara mudarFase para cada limiar cruzado pela vida atual (atravessa múltiplos limiares no mesmo frame).
    protected void verificarTransicaoFase() {
        controladorFases.sincronizar(faseAtual, stats.getVida(), stats.getVidaBase(), this::mudarFase);
    }

    // Ataque normal delegado à fase vigente (chamado pelo gancho ataqueBasico da IA herdada).
    public void executarAtaqueNormal() {
        FaseBoss fase = faseVigente();
        if (fase != null) fase.executarAtaqueNormal(this);
    }

    // Ataque especial delegado à fase vigente (chamado pelo gancho ataqueEspecial da IA herdada).
    public void executarAtaqueEspecial() {
        FaseBoss fase = faseVigente();
        if (fase != null) fase.executarAtaqueEspecial(this);
    }

    // Ponto de extensão do ciclo; por padrão mantém as fases sincronizadas com a vida.
    public void atualizarEstado() {
        verificarTransicaoFase();
    }

    // Transição de fase: cancela ataques pendentes, aplica as animações da nova fase e toca a transição.
    public void mudarFase(int novaFase) {
        if (novaFase <= faseAtual || novaFase >= fases.size()) return;
        faseAtual = novaFase;
        aoTransicionarFase(novaFase);
        aplicarAnimacoesFase(fases.get(novaFase));
        iniciarTransicao();
        aposTransicionarFase(novaFase);
    }

    // Gancho pré-animações: cancelar ataques, limpar efeitos, entrar no estado de transição.
    protected void aoTransicionarFase(int novaFase) { }

    // Gancho pós-transição: efeitos de entrada da nova fase (regeneração, estado padrão, logs).
    protected void aposTransicionarFase(int novaFase) { }

    // Gancho de morte: cada boss cancela ataques, efeitos e invocações pendentes.
    protected void aoMorrer() { }

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
    protected float getDeltaAtual() { return deltaAtual; }

    // Fase vigente; null quando a lista está vazia ou o índice é inválido.
    protected FaseBoss faseVigente() {
        if (fases.isEmpty() || faseAtual < 0 || faseAtual >= fases.size()) return null;
        return fases.get(faseAtual);
    }
}
