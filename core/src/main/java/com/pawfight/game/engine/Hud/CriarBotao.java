package com.pawfight.game.engine.Hud;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pawfight.game.engine.AudioEngine;

public class CriarBotao {
    private final AudioEngine audioEngine = new AudioEngine();
    private Music clickAudio;

    public ImageButton create(Stage stage, int x, int y, int width, int height,
                              Texture normalTexture, Texture hoverTexture, Texture pressedTexture, String buttonClickAudioPath) {
        if (buttonClickAudioPath != null && !buttonClickAudioPath.isEmpty()) {
            clickAudio = audioEngine.criarAudio(buttonClickAudioPath);
        }else {
            clickAudio = audioEngine.criarAudio("menu/button/button.wav");
        }

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

    public Music playClickSound() {
        if (clickAudio != null) {
            clickAudio.play();
            return clickAudio;
        }
        return null;
    }


    public ImageButton create(Stage stage, int x, int y, int width, int height,
                              Texture normalTexture, Texture hoverTexture, Texture pressedTexture) {
        return create(stage,x,y,width,height,normalTexture,hoverTexture,pressedTexture,null);
    }
}
