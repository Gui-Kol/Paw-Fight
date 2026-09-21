package com.pawfight.game.entity.tiro;

import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public class Atirar {

    /** Ângulo total do leque (graus) quando o player dispara mais de um projétil. */
    private static final float ANGULO_LEQUE = 30f;

    /**
     * Desvio angular do projétil de índice {@code indice} numa rajada de
     * {@code total} projéteis, simétrico ao redor da direção original.
     * Com um único projétil, retorna 0 (comportamento idêntico ao original).
     */
    static float anguloLeque(int indice, int total) {
        if (total <= 1) return 0f;
        return -ANGULO_LEQUE / 2f + ANGULO_LEQUE * indice / (total - 1);
    }

    public void atira(List<TirosTemplate> modelos, PlayerTemplate player, float delta, List<EnemyTemplate> inimigos) {
        if (modelos == null || player == null || modelos.isEmpty()) return;

        for (TirosTemplate tiroModelo : modelos) {
            // Modelo sempre conhece os inimigos da sala — os tiros do pool
            // usam essa lista para mirar no momento em que nascem
            tiroModelo.setInimigos(inimigos);
            float cadencia = tiroModelo.getCadencia() * player.getCadenciaTiro();
            float duracao = tiroModelo.getDuracao() * player.getDuracaoTiro();
            float intervalo = tiroModelo.getIntervalo();

            // Acumula o tempo decorrido
            intervalo += delta;

            // Se passou o intervalo, dispara
            if (intervalo >= cadencia) {
                intervalo = 0f; // reseta o acumulador

                // Rajada em leque: cada projétil sai do pool com um desvio angular
                int quantidade = player.getQuantidadeDeTiros();
                for (int i = 0; i < quantidade; i++) {
                    TirosTemplate tiroNovo = tiroModelo.obterDoPool(player);
                    tiroNovo.setDuracao(duracao);
                    tiroNovo.rotacionarDirecao(anguloLeque(i, quantidade));
                    player.adicionarTiro(tiroNovo);
                    // Remoção agora é feita pelo game loop em PlayerTemplate.updateTiros()
                }
            }
            tiroModelo.setIntervalo(intervalo);
        }
    }
}
