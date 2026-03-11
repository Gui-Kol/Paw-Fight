package com.pawfight.game.engine.animation;

import com.badlogic.gdx.graphics.Texture;

public record SpriteDefinition(
    Texture texture,
    int numFrame,
    float frameDuration,
    boolean reverse,
    boolean olhandoEsquerda
) {
}
