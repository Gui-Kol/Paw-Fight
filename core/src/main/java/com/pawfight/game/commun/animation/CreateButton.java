package com.pawfight.game.commun.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CreateButton {
    private Stage stage;

    public ImageButton create(int x, int y, int width, int height,
                              Texture normalTexture, Texture hoverTexture, Texture pressedTexture) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        TextureRegionDrawable normalDrawable = new TextureRegionDrawable(new TextureRegion(normalTexture));
        TextureRegionDrawable hoverDrawable = new TextureRegionDrawable(new TextureRegion(hoverTexture));
        TextureRegionDrawable pressedDrawable = new TextureRegionDrawable(new TextureRegion(pressedTexture));

        // Cria o botão com diferentes estados
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = normalDrawable;       // estado normal
        style.over = hoverDrawable;      // quando mouse está em cima
        style.down = pressedDrawable;    // quando clicado

        ImageButton button = new ImageButton(style);

        // posição e tamanho do botão
        button.setPosition(x, y);
        button.setSize(width, height);

        stage.addActor(button);
        return button;
    }

    public Stage getStage() {
        return stage;
    }
}
