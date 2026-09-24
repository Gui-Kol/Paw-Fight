package com.pawfight.game.entity.bosses.infra;

import com.pawfight.game.entity.enemy.EnemyTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ControladorInvocacoes {
    private final List<EnemyTemplate> ativas;
    private final List<EnemyTemplate> pendentes;

    public ControladorInvocacoes(int capacidade) {
        ativas = new ArrayList<>(capacidade);
        pendentes = new ArrayList<>(capacidade);
    }

    public void registrar(EnemyTemplate invocador, EnemyTemplate invocacao) {
        invocacao.setInvocador(invocador);
        invocacao.setEnemiesList(invocador.getEnemiesList());
        invocacao.setParedesColisores(invocador.getParedesColisores());
        ativas.add(invocacao);
        pendentes.add(invocacao);
    }

    public void removerMortas() {
        Iterator<EnemyTemplate> iterator = ativas.iterator();
        while (iterator.hasNext()) if (iterator.next().isMorto()) iterator.remove();
    }

    public void drenar(List<EnemyTemplate> destino) {
        destino.addAll(pendentes);
        pendentes.clear();
    }

    public void cancelarPendentes() { pendentes.clear(); }
    public int quantidadeVivas() { removerMortas(); return ativas.size(); }
}
