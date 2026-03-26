package com.pawfight.game.engine.Hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.pawfight.game.engine.CommunVariable.GET_SCALE;

public class CreateButton {

    public ImageButton create(Stage stage, int x, int y, int width, int height,
                              Texture normalTexture, Texture hoverTexture, Texture pressedTexture) {

        float scale = GET_SCALE();

        TextureRegionDrawable normalDrawable = new TextureRegionDrawable(new TextureRegion(normalTexture));
        TextureRegionDrawable hoverDrawable = new TextureRegionDrawable(new TextureRegion(hoverTexture));
        TextureRegionDrawable pressedDrawable = new TextureRegionDrawable(new TextureRegion(pressedTexture));

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = normalDrawable;
        style.over = hoverDrawable;
        style.down = pressedDrawable;

        float bW = width * scale;
        float bH = height * scale;
        float bX = (x - (bW/2)) * scale;
        float bY = (y - (bH/2)) * scale;

        ImageButton button = new ImageButton(style);
        button.setPosition(bX,bY);
        button.setSize(bW,bH);

        stage.addActor(button);
        return button;
    }

    public ImageButton clone(ImageButton button){
        return new ImageButton(button.getStyle());
    }
}
