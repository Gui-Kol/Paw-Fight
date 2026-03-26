package com.pawfight.game.entity.tiro;

import com.badlogic.gdx.utils.Timer;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public class Atirar {

    public void atira(List<TirosTamplate> modelos, PlayerTemplate player, float delta) {
        if (modelos == null || player == null || modelos.isEmpty()) return;

        for (TirosTamplate tiroModelo : modelos) {
            float cadencia = tiroModelo.getCadencia() * player.getCadenciaTiro();
            float duracao = tiroModelo.getDuracao() * player.getDuracaoTiro();
            float intervalo = tiroModelo.getIntervalo();

            // Acumula o tempo decorrido
            intervalo += delta;

            // Se passou o intervalo, dispara
            if (intervalo >= cadencia) {
                intervalo = 0f; // reseta o acumulador

                TirosTamplate tiroNovo = tiroModelo.clonar(player);
                player.adicionarTiro(tiroNovo);

                tiroNovo.setDuracao(duracao);
                removerTiro(tiroNovo, player);
            }
            tiroModelo.setIntervalo(intervalo);
        }
    }

    private void removerTiro(TirosTamplate tiroNovo, PlayerTemplate player) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                player.removerTiro(tiroNovo);
            }
        }, tiroNovo.getDuracao());
    }
}
