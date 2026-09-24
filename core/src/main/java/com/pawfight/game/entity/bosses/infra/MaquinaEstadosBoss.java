package com.pawfight.game.entity.bosses.infra;

// Máquina de estados explícita do boss: guarda o estado atual e o timer do estado (zerado a cada troca).
// Centraliza o padrão "mudar de estado reinicia o cronômetro", evitando flags booleanas espalhadas.
public final class MaquinaEstadosBoss<E extends Enum<E>> {

    private E estado;
    private float timer;

    public MaquinaEstadosBoss(E estadoInicial) {
        this.estado = estadoInicial;
    }

    // Troca o estado e reinicia o cronômetro do estado.
    public void mudar(E novoEstado) {
        estado = novoEstado;
        timer = 0f;
    }

    public void avancar(float delta) {
        timer += delta;
    }

    // Zera o cronômetro sem trocar de estado (passos intermediários de um mesmo estado).
    public void reiniciarTimer() {
        timer = 0f;
    }

    public boolean is(E esperado) {
        return estado == esperado;
    }

    public E getEstado() { return estado; }
    public float getTimer() { return timer; }
}
