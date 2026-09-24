package com.pawfight.game.content.tiro;

import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.TirosTemplate;

import java.util.List;
import java.util.function.BiFunction;

public record DefinicaoTiro(
    String id,
    List<String> assets,
    int frames,
    int framesPorSegundo,
    int quantidadePadrao,
    BiFunction<Integer, PlayerTemplate, TirosTemplate> fabrica
) {
    public DefinicaoTiro {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID do tiro não pode ser vazio.");
        assets = List.copyOf(assets);
        if (assets.isEmpty()) throw new IllegalArgumentException("Tiro sem assets: " + id);
        if (frames <= 0 || framesPorSegundo <= 0 || quantidadePadrao <= 0) {
            throw new IllegalArgumentException("Configuração numérica inválida do tiro: " + id);
        }
        if (fabrica == null) throw new IllegalArgumentException("Fábrica do tiro não pode ser nula: " + id);
    }

    public TirosTemplate criar(int tamanho, PlayerTemplate player) { return fabrica.apply(tamanho, player); }
}
