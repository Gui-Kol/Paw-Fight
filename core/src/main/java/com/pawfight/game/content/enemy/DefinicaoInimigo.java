package com.pawfight.game.content.enemy;

import com.pawfight.game.entity.enemy.EnemyTemplate;

import java.util.function.Function;

public record DefinicaoInimigo(
    String id,
    String nomeExibido,
    int tamanho,
    Function<ContextoSpawn, EnemyTemplate> fabrica
) {
    public DefinicaoInimigo {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID do inimigo não pode ser vazio.");
        if (nomeExibido == null || nomeExibido.isBlank()) throw new IllegalArgumentException("Nome do inimigo não pode ser vazio.");
        if (tamanho <= 0) throw new IllegalArgumentException("Tamanho do inimigo deve ser positivo: " + id);
        if (fabrica == null) throw new IllegalArgumentException("Fábrica do inimigo não pode ser nula: " + id);
    }

    public EnemyTemplate criar(ContextoSpawn contexto) {
        EnemyTemplate inimigo = fabrica.apply(contexto);
        inimigo.setEnemiesList(contexto.entidades());
        inimigo.setParedesColisores(contexto.paredes());
        return inimigo;
    }
}
