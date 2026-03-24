package com.pawfight.game.engine.procedural.room;

import com.badlogic.gdx.graphics.Texture;

public record InfoGeraObjeto(
    String layerName,
    RoomType typeRoom,
    Texture texture,
    int qntMax,
    int qntMin,
    int ajusteAlturaHitBox,
    int ajusteLarguraHitBox,
    int ajusteXHitBox,
    int ajusteYHitBox
) {
}
