package com.pawfight.game.commun.animation;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface TransitionEffect {
    void update(float delta);
    void render(SpriteBatch batch, float screenWidth, float screenHeight);
    boolean isFinished();
    void reset();
    void dispose();
    float getElapsedTime();
}
