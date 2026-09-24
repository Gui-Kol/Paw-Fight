package com.pawfight.game.entity.bosses.infra;

// Janela de contato: política explícita "um ataque atinge o mesmo alvo no máximo uma vez por abertura".
// Substitui flags booleanas soltas (danoAplicadoNaJanela) espalhadas pelos ataques.
public final class JanelaDeContato {

    private boolean aplicado = true;

    // Abre uma nova janela (início de um golpe/impacto).
    public void abrir() {
        aplicado = false;
    }

    // Fecha a janela sem aplicar (cancelamento de ataque, transição de fase, morte).
    public void fechar() {
        aplicado = true;
    }

    public boolean isAplicado() {
        return aplicado;
    }

    // Executa a ação no máximo uma vez por abertura; true quando aplica.
    public boolean aplicarUmaVez(Runnable acao) {
        if (aplicado) return false;
        aplicado = true;
        acao.run();
        return true;
    }
}
