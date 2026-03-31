package com.pawfight.game.engine.procedural.sala;

import com.badlogic.gdx.graphics.Texture;

public record InfoGeraObjeto(
    String layerName,
    String nomeObjeto,
    TipoSala typeRoom,
    Texture texture,
    int qntMax,
    int qntMin,
    int ajusteAlturaHitBox,
    int ajusteLarguraHitBox,
    int ajusteXHitBox,
    int ajusteYHitBox,
    int areaToque,
    int tamanhoPx
) {
}
