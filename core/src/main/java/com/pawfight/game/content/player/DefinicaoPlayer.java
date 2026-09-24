package com.pawfight.game.content.player;

import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.function.Function;

public record DefinicaoPlayer(
    String id,
    String nomeExibido,
    String assetSelecao,
    Function<ContextoCriacaoPlayer, PlayerTemplate> fabrica
) {
    public DefinicaoPlayer {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID do Player não pode ser vazio.");
        if (nomeExibido == null || nomeExibido.isBlank()) throw new IllegalArgumentException("Nome do Player não pode ser vazio.");
        if (assetSelecao == null || assetSelecao.isBlank()) throw new IllegalArgumentException("Asset de seleção não pode ser vazio.");
        if (fabrica == null) throw new IllegalArgumentException("Fábrica do Player não pode ser nula.");
    }

    public PlayerTemplate criar(ContextoCriacaoPlayer contexto) {
        return fabrica.apply(contexto);
    }
}
