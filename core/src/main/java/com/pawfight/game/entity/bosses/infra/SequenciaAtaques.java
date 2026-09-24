package com.pawfight.game.entity.bosses.infra;

// Sequência de ataques: emite eventos espaçados por um intervalo até completar a quantidade.
// O primeiro evento sai no instante da ativação; deltas grandes emitem todos os eventos devidos.
public final class SequenciaAtaques {

    private final float intervalo;
    private int restantes;
    private float tempoProximo;

    public SequenciaAtaques(float intervalo) {
        this.intervalo = intervalo;
    }

    // Arma a sequência; o primeiro evento é devido imediatamente.
    public void iniciar(int quantidade) {
        restantes = quantidade;
        tempoProximo = 0f;
    }

    // Cancela a sequência: nenhum evento pendente é emitido.
    public void cancelar() {
        restantes = 0;
    }

    public boolean concluida() {
        return restantes <= 0;
    }

    // Dispara todos os eventos devidos neste frame, mantendo o espaçamento por acúmulo do intervalo.
    public void atualizar(float delta, Runnable evento) {
        tempoProximo -= delta;
        while (restantes > 0 && tempoProximo <= 0f) {
            restantes--;
            evento.run();
            tempoProximo += intervalo;
        }
    }
}
