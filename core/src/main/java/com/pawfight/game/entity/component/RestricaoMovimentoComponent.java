package com.pawfight.game.entity.component;

public class RestricaoMovimentoComponent {
    private boolean ativa;
    private float multiplicador = 1f;
    private float tempoRestante;

    public void aplicar(float multiplicador, float duracao) {
        this.multiplicador = Math.max(0.1f, Math.min(1f, multiplicador));
        this.tempoRestante = Math.max(0f, duracao);
        this.ativa = tempoRestante > 0f && this.multiplicador < 1f;
    }

    public void atualizar(float delta) {
        if (!ativa) return;
        tempoRestante -= delta;
        if (tempoRestante <= 0f) limpar();
    }

    public void limpar() {
        ativa = false;
        multiplicador = 1f;
        tempoRestante = 0f;
    }

    public boolean isAtiva() { return ativa; }
    public float getMultiplicador() { return ativa ? multiplicador : 1f; }
    public float getTempoRestante() { return tempoRestante; }
}
