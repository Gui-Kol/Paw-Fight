package com.pawfight.game.entity.tiro;

import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public class Atirar {

    // Ângulo total do leque (graus) quando o player dispara mais de um projétil.
    private static final float ANGULO_LEQUE = 30f;

    // Desvio angular simétrico do projétil de índice i numa rajada de n; com 1 projétil retorna 0.
    static float anguloLeque(int indice, int total) {
        if (total <= 1) return 0f;
        return -ANGULO_LEQUE / 2f + ANGULO_LEQUE * indice / (total - 1);
    }

    public void atira(List<TirosTemplate> modelos, PlayerTemplate player, float delta, List<EnemyTemplate> inimigos) {
        if (modelos == null || player == null || modelos.isEmpty()) return;

        for (TirosTemplate tiroModelo : modelos) {
            // Os tiros do pool usam a lista de inimigos da sala para mirar ao nascer
            tiroModelo.setInimigos(inimigos);
            float cadencia = tiroModelo.getCadencia() * player.getCadenciaTiro();
            float duracao = tiroModelo.getDuracao() * player.getDuracaoTiro();
            float intervalo = tiroModelo.getIntervalo();

            intervalo += delta;

            if (intervalo >= cadencia) {
                intervalo = 0f;

                // Rajada em leque: cada projétil sai do pool com um desvio angular
                int quantidade = player.getQuantidadeDeTiros();
                for (int i = 0; i < quantidade; i++) {
                    TirosTemplate tiroNovo = tiroModelo.obterDoPool(player);
                    tiroNovo.setDuracao(duracao);
                    tiroNovo.rotacionarDirecao(anguloLeque(i, quantidade));
                    player.adicionarTiro(tiroNovo);
                    // Remoção e devolução ao pool são feitas pelo SistemaCombate.
                }
            }
            tiroModelo.setIntervalo(intervalo);
        }
    }
}
