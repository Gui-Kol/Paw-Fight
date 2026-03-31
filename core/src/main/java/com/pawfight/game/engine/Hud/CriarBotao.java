package com.pawfight.game.engine.Hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CriarBotao {

    public ImageButton create(Stage stage, int x, int y, int width, int height,
                              Texture normalTexture, Texture hoverTexture, Texture pressedTexture) {

        // The stage/viewport already uses base screen units (FitViewport with base width/height).
        // Do not apply additional manual scaling here – use the base coordinates directly so
        // the Stage will correctly map actor positions to the scaled screen.

        TextureRegionDrawable normalDrawable = new TextureRegionDrawable(new TextureRegion(normalTexture));
        TextureRegionDrawable hoverDrawable = new TextureRegionDrawable(new TextureRegion(hoverTexture));
        TextureRegionDrawable pressedDrawable = new TextureRegionDrawable(new TextureRegion(pressedTexture));

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = normalDrawable;
        style.over = hoverDrawable;
        style.down = pressedDrawable;

        float bW = width; // use base units
        float bH = height; // use base units
        float bX = x - (bW / 2f);
        float bY = y - (bH / 2f);

        ImageButton button = new ImageButton(style);
        button.setPosition(bX, bY);
        button.setSize(bW, bH);

        stage.addActor(button);
        return button;
    }

    public ImageButton clone(ImageButton button) {
        return new ImageButton(button.getStyle());
    }
}
