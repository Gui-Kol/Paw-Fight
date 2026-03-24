package com.pawfight.game.engine.design.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pawfight.game.engine.design.SpriteDefinition;

public class AnimationEngine {
    public Animation<TextureRegion> animar(SpriteDefinition spriteDefinition) {
        Texture texture = spriteDefinition.texture();
        int numFrames = spriteDefinition.numFrame();
        float frameDuration = spriteDefinition.frameDuration();
        boolean reverse = spriteDefinition.reverse();
        boolean olhandoEsquerda = spriteDefinition.olhandoEsquerda();


        // Divide o spritesheet em regiões
        TextureRegion[][] tmp = TextureRegion.split(
            texture,
            texture.getWidth() / numFrames,
            texture.getHeight()
        );

        // Copia os frames da primeira linha
        TextureRegion[] frames = new TextureRegion[numFrames];
        for (int i = 0; i < numFrames; i++) {
            frames[i] = tmp[0][i];
        }

        // Cria a animação
        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        if (reverse) {
            animation.setPlayMode(Animation.PlayMode.REVERSED);
        }
        if (olhandoEsquerda){
            for (TextureRegion frame : animation.getKeyFrames()) {
                frame.flip(true, false);
            }
        }

        return animation;
    }
}


