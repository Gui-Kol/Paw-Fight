package com.pawfight.game.commun.Hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CreateButton {

    public ImageButton create(Stage stage, int x, int y, int width, int height,
                              Texture normalTexture, Texture hoverTexture, Texture pressedTexture) {

        TextureRegionDrawable normalDrawable = new TextureRegionDrawable(new TextureRegion(normalTexture));
        TextureRegionDrawable hoverDrawable = new TextureRegionDrawable(new TextureRegion(hoverTexture));
        TextureRegionDrawable pressedDrawable = new TextureRegionDrawable(new TextureRegion(pressedTexture));

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = normalDrawable;
        style.over = hoverDrawable;
        style.down = pressedDrawable;

        ImageButton button = new ImageButton(style);
        button.setPosition(x, y);
        button.setSize(width, height);

        stage.addActor(button);
        return button;
    }
}
