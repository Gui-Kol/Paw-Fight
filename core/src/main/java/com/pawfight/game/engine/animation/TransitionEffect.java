package com.pawfight.game.engine.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface TransitionEffect {
    void update(float delta);
    void render(SpriteBatch batch, float screenWidth, float screenHeight);
    boolean isFinished();
    void reset();
    void dispose();
    float getElapsedTime();
}
