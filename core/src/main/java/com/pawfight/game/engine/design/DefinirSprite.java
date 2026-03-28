package com.pawfight.game.engine.design;

import com.badlogic.gdx.graphics.Texture;

public record DefinirSprite(
    Texture texture,
    int numFrame,
    float frameDuration,
    boolean reverse,
    boolean olhandoEsquerda
) {
}
