package com.pawfight.game.entity.tiro;

import com.badlogic.gdx.utils.Timer;
import com.pawfight.game.entity.player.PlayerTemplate;

public class Atirar {

    public void atira(TirosTamplate tiroModelo, PlayerTemplate player) {
        if (tiroModelo == null || player == null){return;}
        float duracao = tiroModelo.getDuracao() * player.getDuracaoTiro();
        float intervalo = tiroModelo.getIntervalo() * player.getCadenciaTiro();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                TirosTamplate tiroNovo = tiroModelo.clonar(player);
                player.adicionarTiro(tiroNovo);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        player.removerTiro(tiroNovo);
                    }
                }, duracao);
            }
        }, 2, intervalo);
    }
}
