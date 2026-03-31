package com.pawfight.game.entity.tiro;

import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public class Atirar {

    public void atira(List<TirosTemplate> modelos, PlayerTemplate player, float delta) {
        if (modelos == null || player == null || modelos.isEmpty()) return;

        for (TirosTemplate tiroModelo : modelos) {
            float cadencia = tiroModelo.getCadencia() * player.getCadenciaTiro();
            float duracao = tiroModelo.getDuracao() * player.getDuracaoTiro();
            float intervalo = tiroModelo.getIntervalo();

            // Acumula o tempo decorrido
            intervalo += delta;

            // Se passou o intervalo, dispara
            if (intervalo >= cadencia) {
                intervalo = 0f; // reseta o acumulador

                TirosTemplate tiroNovo = tiroModelo.clonar(player);
                tiroNovo.setDuracao(duracao);
                player.adicionarTiro(tiroNovo);
                // Remoção agora é feita pelo game loop em PlayerTemplate.updateTiros()
            }
            tiroModelo.setIntervalo(intervalo);
        }
    }
}
