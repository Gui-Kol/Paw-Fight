package com.pawfight.game.entity.tiro;

import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public class Atirar {

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

                TirosTemplate tiroNovo = tiroModelo.obterDoPool(player);
                tiroNovo.setDuracao(duracao);
                player.adicionarTiro(tiroNovo);
                // Remoção agora é feita pelo game loop em PlayerTemplate.updateTiros()
            }
            tiroModelo.setIntervalo(intervalo);
        }
    }
}
