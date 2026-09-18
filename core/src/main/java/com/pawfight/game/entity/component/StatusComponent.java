package com.pawfight.game.entity.component;

/**
 * Efeitos de status genéricos (queimadura, lentidão) aplicáveis a qualquer
 * entidade. Não conhece EnemyTemplate/PlayerTemplate nem dispara dano por
 * conta própria — quem o contém chama {@link #update(float)} e aplica o dano
 * retornado (DoT da queimadura) e o {@link #getMultiplicadorVelocidade()}
 * (lentidão) onde fizer sentido (movimento e cadência de ataque).
 */
public class StatusComponent {

    /** Intervalo entre ticks de dano da queimadura, em segundos. */
    public static final float TICK_QUEIMADURA = 0.5f;

    // ── Queimadura (dano ao longo do tempo) ────────────────────
    private boolean queimando = false;
    private int danoQueimaduraPorTick = 0;
    private float duracaoQueimadura = 0f;
    private float tempoQueimadura = 0f;
    private float tickQueimadura = 0f;

    // ── Lentidão (reduz movimento e cadência de ataque) ────────
    private boolean lento = false;
    private float multiplicadorLentidao = 1f;
    private float duracaoLentidao = 0f;
    private float tempoLentidao = 0f;

    /**
     * Tenta aplicar queimadura (DoT).
     *
     * @param danoPorTick dano aplicado a cada {@link #TICK_QUEIMADURA} segundos
     * @param duracao     duração total do efeito, em segundos
     * @param chance      probabilidade de aplicar (0f a 1f)
     * @return true se o efeito foi aplicado
     */
    public boolean aplicarQueimadura(int danoPorTick, float duracao, float chance) {
        if (chance < 1f && Math.random() > chance) return false;
        queimando = true;
        danoQueimaduraPorTick = Math.max(0, danoPorTick);
        duracaoQueimadura = Math.max(0f, duracao);
        tempoQueimadura = 0f;
        tickQueimadura = 0f;
        return true;
    }

    /**
     * Tenta aplicar lentidão.
     *
     * @param multiplicador fator de velocidade (ex.: 0.5 = metade), limitado a (0, 1]
     * @param duracao       duração do efeito, em segundos
     * @param chance        probabilidade de aplicar (0f a 1f)
     * @return true se o efeito foi aplicado
     */
    public boolean aplicarLentidao(float multiplicador, float duracao, float chance) {
        if (chance < 1f && Math.random() > chance) return false;
        lento = true;
        multiplicadorLentidao = Math.max(0.1f, Math.min(1f, multiplicador));
        duracaoLentidao = Math.max(0f, duracao);
        tempoLentidao = 0f;
        return true;
    }

    /**
     * Avança os timers dos efeitos ativos.
     *
     * @return dano de queimadura a aplicar neste frame (0 quando não há tick)
     */
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

    /** Multiplicador de velocidade atual (1f quando não há lentidão ativa). */
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
