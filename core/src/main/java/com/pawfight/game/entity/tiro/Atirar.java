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

            if (tiroModelo.temTirosPendentesNaRajada()) {
                if (tiroModelo.atualizarEsperaDaRajada(delta)) {
                    dispararProximoDaRajada(tiroModelo, player, duracao);
                }
                continue;
            }

            float intervalo = tiroModelo.getIntervalo();

            intervalo += delta;

            if (intervalo >= cadencia) {
                intervalo = 0f;
                int quantidade = tiroModelo.getQuantidadeTirosPadrao() * player.getQuantidadeDeTiros();
                tiroModelo.iniciarRajada(quantidade);
                dispararProximoDaRajada(tiroModelo, player, duracao);
            }
            tiroModelo.setIntervalo(intervalo);
        }
    }

    private void dispararProximoDaRajada(TirosTemplate modelo, PlayerTemplate player, float duracao) {
        int indice = modelo.consumirProximoTiroDaRajada();
        int total = modelo.getTotalTirosRajada();
        TirosTemplate tiroNovo = modelo.obterDoPool(player);
        tiroNovo.setDuracao(duracao);
        tiroNovo.rotacionarDirecao(anguloLeque(indice, total));
        player.adicionarTiro(tiroNovo);
        // Remoção e devolução ao pool são feitas pelo SistemaCombate.
    }
}
