package com.pawfight.game.content.world;

import com.pawfight.game.world.portal.WorldFactory;

public record DefinicaoMundo(String id, String nomeExibido, WorldFactory fabrica) {
    public DefinicaoMundo {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID do mundo não pode ser vazio.");
        if (nomeExibido == null || nomeExibido.isBlank()) throw new IllegalArgumentException("Nome do mundo não pode ser vazio.");
        if (fabrica == null) throw new IllegalArgumentException("Fábrica do mundo não pode ser nula.");
    }
}
