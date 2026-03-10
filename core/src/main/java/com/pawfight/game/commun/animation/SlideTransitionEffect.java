package com.pawfight.game.commun.animation;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class SlideTransitionEffect implements TransitionEffect {
    public enum Direction { LEFT, RIGHT, UP, DOWN }

    private float offset;
    private float duration;
    private float elapsedTime;
    private boolean slidingOut;
    private Texture slideTexture;
    private Direction direction;
    private Vector2 startPosition;

    public SlideTransitionEffect(float duration) {
        this(duration, Direction.RIGHT);
    }

    public SlideTransitionEffect(float duration, Direction direction) {
        this.duration = duration;
        this.offset = 0f;
        this.elapsedTime = 0f;
        this.slidingOut = true;
        this.direction = direction;
        this.slideTexture = createSlideTexture();
        this.startPosition = getStartPosition(direction);
    }

    private Texture createSlideTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 1);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Vector2 getStartPosition(Direction dir) {
        switch (dir) {
            case LEFT: return new Vector2(-1, 0);
            case RIGHT: return new Vector2(1, 0);
            case UP: return new Vector2(0, 1);
            case DOWN: return new Vector2(0, -1);
            default: return new Vector2(1, 0);
        }
    }

    @Override
    public void update(float delta) {
        elapsedTime += delta;
        float progress = Math.min(elapsedTime / duration, 1f);
        progress = easeInOutCubic(progress); // Easing mais avançado

        offset = slidingOut ? progress : 1f - progress;
    }

    @Override
    public void render(SpriteBatch batch, float screenWidth, float screenHeight) {
        if (offset > 0.01f) {
            batch.begin();
            batch.setColor(0, 0, 0, 0.8f); // Alpha reduzido para borda suave
            float x = startPosition.x * offset * screenWidth;
            float y = startPosition.y * offset * screenHeight;
            batch.draw(slideTexture, x, y, screenWidth, screenHeight);
            batch.setColor(1, 1, 1, 1);
            batch.end();
        }
    }

    @Override
    public boolean isFinished() {
        return elapsedTime >= duration;
    }

    public void switchToSlideIn() {
        slidingOut = false;
        elapsedTime = 0f;
    }

    private float easeInOutCubic(float t) {
        return t < 0.5f ? 4 * t * t * t : 1 - (float)Math.pow(-2 * t + 2, 3) / 2;
    }

    @Override
    public void reset() {
        offset = 0f;
        elapsedTime = 0f;
        slidingOut = true;
    }

    @Override
    public float getElapsedTime() {
        return elapsedTime;
    }

    @Override
    public void dispose() {
        if (slideTexture != null) {
            slideTexture.dispose();
        }
    }
}
