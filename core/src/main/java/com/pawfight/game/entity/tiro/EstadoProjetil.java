package com.pawfight.game.entity.tiro;

public final class EstadoProjetil {
    private float tempoVida;
    private float tempoAnimacao;
    private boolean ativoParaColisao = true;
    private float tempoRemocaoVisual = Float.POSITIVE_INFINITY;

    public void atualizar(float delta) {
        tempoVida += delta;
        tempoAnimacao += delta;
    }

    public void reiniciar() {
        tempoVida = 0f;
        tempoAnimacao = 0f;
        ativoParaColisao = true;
        tempoRemocaoVisual = Float.POSITIVE_INFINITY;
    }

    public void desativar(int quantidadeFrames, float duracaoFrame) {
        if (!ativoParaColisao) return;
        ativoParaColisao = false;
        if (quantidadeFrames <= 1) {
            tempoRemocaoVisual = tempoAnimacao;
            return;
        }
        float duracaoCiclo = quantidadeFrames * duracaoFrame;
        float ciclos = (float) Math.ceil(Math.max(0f, tempoAnimacao - 0.0001f) / duracaoCiclo);
        tempoRemocaoVisual = Math.max(duracaoCiclo, ciclos * duracaoCiclo);
    }

    public float getTempoVida() { return tempoVida; }
    public float getTempoAnimacao() { return tempoAnimacao; }
    public void reiniciarAnimacao() { tempoAnimacao = 0f; }
    void definirTempoAnimacao(float tempoAnimacao) { this.tempoAnimacao = tempoAnimacao; }
    public boolean isAtivoParaColisao() { return ativoParaColisao; }
    public boolean isExpirado() { return !ativoParaColisao && tempoAnimacao + 0.0001f >= tempoRemocaoVisual; }
}
