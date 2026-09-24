package com.pawfight.game.entity.bosses.infra;

// Cooldown nomeado de uma habilidade: desce com o delta (nunca negativo) e é religado ao disparar.
// Pause congela o cooldown porque atualizar() só roda com o jogo em andamento.
public final class ControladorCooldown {

    private final float duracaoPadrao;
    private float restante;

    public ControladorCooldown(float duracaoPadrao, boolean iniciarCarregado) {
        this.duracaoPadrao = duracaoPadrao;
        this.restante = iniciarCarregado ? duracaoPadrao : 0f;
    }

    public void atualizar(float delta) {
        if (restante > 0f) restante = Math.max(0f, restante - delta);
    }

    public boolean pronto() {
        return restante <= 0f;
    }

    // Religa o cooldown com a duração padrão.
    public void disparar() {
        restante = duracaoPadrao;
    }

    // Religa o cooldown com uma duração específica (ex.: cadência muda por fase).
    public void disparar(float duracao) {
        restante = duracao;
    }

    // Zera o cooldown, liberando o uso imediato.
    public void cancelar() {
        restante = 0f;
    }

    public float getRestante() { return restante; }
}
