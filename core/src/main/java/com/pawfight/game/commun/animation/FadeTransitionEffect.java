package com.pawfight.game.commun.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class FadeTransitionEffect implements TransitionEffect {
    private float alpha;
    private float duration;
    private float elapsedTime;
    private boolean fadingOut;
    private Texture fadeTexture;
    private Color fadeColor;
    private boolean useGradient;

    public FadeTransitionEffect(float duration) {
        this(duration, Color.BLACK, false);
    }

    public FadeTransitionEffect(float duration, Color fadeColor, boolean useGradient) {
        this.duration = duration;
        this.alpha = 0f;
        this.elapsedTime = 0f;
        this.fadingOut = true;
        this.fadeColor = fadeColor;
        this.useGradient = useGradient;
        this.fadeTexture = createFadeTexture();
    }

    private Texture createFadeTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(fadeColor);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void update(float delta) {
        elapsedTime += delta;
        float progress = Math.min(elapsedTime / duration, 1f);
        progress = easeInOutSine(progress); // Easing mais suave

        if (fadingOut) {
            alpha = progress;
        } else {
            alpha = 1f - progress;
        }
    }

    @Override
    public void render(SpriteBatch batch, float screenWidth, float screenHeight) {
        if (alpha > 0.01f) {
            batch.begin();
            batch.setColor(fadeColor.r, fadeColor.g, fadeColor.b, alpha);
            if (useGradient) {
                // Gradiente radial simples para efeito mais bonito
                for (int i = 0; i < 10; i++) {
                    float radius = (i / 10f) * Math.max(screenWidth, screenHeight);
                    batch.draw(fadeTexture, screenWidth / 2 - radius, screenHeight / 2 - radius, radius * 2, radius * 2);
                }
            } else {
                batch.draw(fadeTexture, 0, 0, screenWidth, screenHeight);
            }
            batch.setColor(1, 1, 1, 1);
            batch.end();
        }
    }

    @Override
    public boolean isFinished() {
        return elapsedTime >= duration;
    }

    public void switchToFadeIn() {
        fadingOut = false;
        elapsedTime = 0f;
    }

    private float easeInOutSine(float t) {
        return -(MathUtils.cos(MathUtils.PI * t) - 1) / 2;
    }

    @Override
    public void reset() {
        alpha = 0f;
        elapsedTime = 0f;
        fadingOut = true;
    }

    @Override
    public float getElapsedTime() {
        return elapsedTime;
    }

    @Override
    public void dispose() {
        if (fadeTexture != null) {
            fadeTexture.dispose();
        }
    }
}
