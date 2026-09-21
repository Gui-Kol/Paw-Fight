package com.pawfight.game.entity.component;

// Efeitos de status genéricos: não dispara dano; quem contém chama update(), aplica o DoT retornado e o multiplicador de velocidade.
public class StatusComponent {

    // Intervalo entre ticks de dano da queimadura, em segundos.
    public static final float TICK_QUEIMADURA = 0.5f;

    private boolean queimando = false;
    private int danoQueimaduraPorTick = 0;
    private float duracaoQueimadura = 0f;
    private float tempoQueimadura = 0f;
    private float tickQueimadura = 0f;

    private boolean lento = false;
    private float multiplicadorLentidao = 1f;
    private float duracaoLentidao = 0f;
    private float tempoLentidao = 0f;

    // Aplica queimadura (DoT): danoPorTick a cada TICK_QUEIMADURA, por duracao s, com chance 0~1.
    public boolean aplicarQueimadura(int danoPorTick, float duracao, float chance) {
        if (chance < 1f && Math.random() > chance) return false;
        queimando = true;
        danoQueimaduraPorTick = Math.max(0, danoPorTick);
        duracaoQueimadura = Math.max(0f, duracao);
        tempoQueimadura = 0f;
        tickQueimadura = 0f;
        return true;
    }

    // Aplica lentidão: multiplicador de velocidade (0,1] por duracao s, com chance 0~1.
    public boolean aplicarLentidao(float multiplicador, float duracao, float chance) {
        if (chance < 1f && Math.random() > chance) return false;
        lento = true;
        multiplicadorLentidao = Math.max(0.1f, Math.min(1f, multiplicador));
        duracaoLentidao = Math.max(0f, duracao);
        tempoLentidao = 0f;
        return true;
    }

    // Avança os timers; retorna o dano de queimadura deste frame (0 se não houve tick).
    public int update(float delta) {
        int danoTick = 0;

        if (queimando) {
            tempoQueimadura += delta;
            tickQueimadura += delta;
            if (tempoQueimadura >= duracaoQueimadura) {
                queimando = false;
                tickQueimadura = 0f;
            } else if (tickQueimadura >= TICK_QUEIMADURA) {
                tickQueimadura -= TICK_QUEIMADURA;
                danoTick = danoQueimaduraPorTick;
            }
        }

        if (lento) {
            tempoLentidao += delta;
            if (tempoLentidao >= duracaoLentidao) {
                lento = false;
                multiplicadorLentidao = 1f;
            }
        }

        return danoTick;
    }

    // Multiplicador de velocidade atual (1f quando não há lentidão ativa).
    public float getMultiplicadorVelocidade() {
        return lento ? multiplicadorLentidao : 1f;
    }

    public boolean isQueimando() {
        return queimando;
    }

    public boolean isLento() {
        return lento;
    }

    public float getMultiplicadorLentidao() {
        return multiplicadorLentidao;
    }
}
