package com.pawfight.game.entity.bosses.infra;

// Ataque com aviso: antecipação (telegraph) e aplicação do efeito são eventos separados.
// O boss mantém o estado explícito na MaquinaEstadosBoss; este componente cronometra o aviso
// via timer da máquina e garante que o efeito seja aplicado no máximo uma vez por aviso.
public final class AtaqueComAviso {

    private final float antecipacao;
    private final JanelaDeContato janela = new JanelaDeContato();

    public AtaqueComAviso(float antecipacao) {
        this.antecipacao = antecipacao;
    }

    // Abre o telegraph: o efeito ainda não pode ser aplicado.
    public void iniciarAviso() {
        janela.abrir();
    }

    // True quando a antecipação terminou (momento de aplicar o efeito).
    public boolean avisoConcluido(float tempoDecorrido) {
        return tempoDecorrido >= antecipacao;
    }

    // Aplica o efeito uma única vez (evento separado do telegraph); true quando aplica.
    public boolean aplicarUmaVez(Runnable efeito) {
        return janela.aplicarUmaVez(efeito);
    }

    // Cancela o ataque: impede aplicação tardia do efeito.
    public void cancelar() {
        janela.fechar();
    }

    public boolean isEfeitoAplicado() {
        return janela.isAplicado();
    }

    public float getAntecipacao() {
        return antecipacao;
    }
}
