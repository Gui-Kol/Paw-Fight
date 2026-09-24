package com.pawfight.game.entity.bosses.infra;

import com.pawfight.game.entity.bosses.FaseBoss;

import java.util.List;

// Controlador de fases: sincroniza a fase do boss com a vida atual, atravessando num mesmo frame
// quantos limiares tiverem sido cruzados — cada transição é notificada uma vez, em ordem.
public final class ControladorFases {

    // Notificado a cada transição de fase disparada pela vida; recebe o índice da nova fase.
    public interface OuvinteTransicao {
        void aoTransicionar(int novaFase);
    }

    private final List<FaseBoss> fases;

    public ControladorFases(List<FaseBoss> fases) {
        this.fases = fases;
    }

    // Avança da fase atual até a última fase cujo limiar foi cruzado, notificando cada passo.
    public void sincronizar(int faseAtual, int vidaAtual, int vidaBase, OuvinteTransicao ouvinte) {
        int alvo = faseAtual;
        while (alvo < fases.size() - 1 && fases.get(alvo).deveTransicionar(vidaAtual, vidaBase)) {
            alvo++;
        }
        for (int proxima = faseAtual + 1; proxima <= alvo; proxima++) {
            ouvinte.aoTransicionar(proxima);
        }
    }
}
