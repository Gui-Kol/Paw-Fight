package com.pawfight.game.world.template;

import com.pawfight.game.engine.ecs.GerenciadorEcs;
import com.pawfight.game.engine.procedural.GerarInimigos;
import com.pawfight.game.entity.enemy.EnemyTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class EnemyManager {

    private final GerarInimigos gerarInimigos;
    private final List<EnemyTemplate> listaInimigos;
    private final GerenciadorEcs gerenciadorEcs;

    public EnemyManager(GerenciadorEcs gerenciadorEcs) {
        this.gerenciadorEcs = gerenciadorEcs;
        this.gerarInimigos = new GerarInimigos();
        this.listaInimigos = new ArrayList<>();
    }

    public GerarInimigos getGerarInimigos() {
        return gerarInimigos;
    }

    public List<EnemyTemplate> getListaInimigos() {
        return listaInimigos;
    }

    public boolean hasInimigos() {
        return listaInimigos != null && !listaInimigos.isEmpty();
    }

    public void clearInimigos() {
        for (EnemyTemplate inimigo : listaInimigos) {
            gerenciadorEcs.removerEntidade(inimigo.getEntidadeEcs());
        }
        listaInimigos.clear();
    }

    public void adicionarInimigos(List<EnemyTemplate> inimigos) {
        if (inimigos == null) return;
        for (EnemyTemplate inimigo : inimigos) {
            if (inimigo != null && !listaInimigos.contains(inimigo)) {
                listaInimigos.add(inimigo);
                gerenciadorEcs.adicionarEntidade(inimigo.getEntidadeEcs());
            }
        }
    }

    public void atualizarInimigos(float delta) {
        Iterator<EnemyTemplate> iterator = listaInimigos.iterator();
        while (iterator.hasNext()) {
            EnemyTemplate inimigo = iterator.next();
            inimigo.update(delta);
            if (inimigo.isMorto()) {
                iterator.remove();
                gerenciadorEcs.removerEntidade(inimigo.getEntidadeEcs());
            }
        }
    }
}
