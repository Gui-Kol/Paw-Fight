package com.pawfight.game.engine.design.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pawfight.game.engine.design.DefinirSprite;

import static com.pawfight.game.engine.GameConfig.ALTURA_TELA_BASE;
import static com.pawfight.game.engine.GameConfig.LARGURA_TELA_BASE;

public class MotorAnimacao {
    private Animation<TextureRegion> animation;
    private boolean fundoAtivo = false;
    private float stateTime = 0;
    private TextureRegion frameFinal;

    public Animation<TextureRegion> animar(DefinirSprite definirSprite) {
        Texture texture = definirSprite.texture();
        int numFrames = definirSprite.numFrame();
        float frameDuration = definirSprite.frameDuration();
        boolean reverse = definirSprite.reverse();
        boolean olhandoEsquerda = definirSprite.olhandoEsquerda();


        TextureRegion[][] tmp = TextureRegion.split(
            texture,
            texture.getWidth() / numFrames,
            texture.getHeight()
        );

        // Usa apenas a primeira linha do spritesheet
        TextureRegion[] frames = new TextureRegion[numFrames];
        for (int i = 0; i < numFrames; i++) {
            frames[i] = tmp[0][i];
        }

        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
        if (reverse) {
            animation.setPlayMode(Animation.PlayMode.REVERSED);
        }
        if (olhandoEsquerda) {
            for (TextureRegion frame : animation.getKeyFrames()) {
                frame.flip(true, false);
            }
        }

        return animation;
    }

    public Animation<TextureRegion> criarAnimacao(DefinirSprite def, boolean reversa) {
        Animation<TextureRegion> anim = animar(def);
        if (!reversa) {
            anim.setPlayMode(Animation.PlayMode.NORMAL);
        } else {
            anim.setPlayMode(Animation.PlayMode.REVERSED);
        }
        return anim;
    }

    public TextureRegion obterFrame(Animation<TextureRegion> anim, boolean ultimoFrame) {
        if (ultimoFrame) {
            return anim.getKeyFrames()[anim.getKeyFrames().length - 1];
        } else {
            return anim.getKeyFrames()[0];
        }
    }

    public TextureRegion executarUmaVez(Animation<TextureRegion> anim, float stateTime, boolean reverse) {
        if (reverse && anim.isAnimationFinished(stateTime)) {
            return obterFrame(anim, false);
        } else if (!reverse && anim.isAnimationFinished(stateTime)) {
            return obterFrame(anim, true);
        }
        return anim.getKeyFrame(stateTime, false);
    }
    public boolean desenharFundo(Batch batch, boolean abrirMenu, DefinirSprite sprite, int tamanhoFundo, Camera camera) {
        return desenharFundo(batch, abrirMenu, sprite, tamanhoFundo, (OrthographicCamera) camera);
    }

    public boolean desenharFundo(Batch batch, boolean abrirMenu, DefinirSprite sprite, int tamanhoFundo, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);
        int x = (LARGURA_TELA_BASE / 2) - (tamanhoFundo / 2);
        int y = (ALTURA_TELA_BASE / 2) - (tamanhoFundo / 2);

        // Inicializa animação de abertura
        if (abrirMenu && animation == null && !fundoAtivo) {
            animation = criarAnimacao(sprite, false); // normal
            stateTime = 0f;
            fundoAtivo = true;
            frameFinal = null;
        }

        // Inicializa animação de fechamento
        if (!abrirMenu && fundoAtivo && animation == null) {
            animation = criarAnimacao(sprite, true); // reversa
            stateTime = 0f;
            frameFinal = null;
        }

        if (animation != null) {
            stateTime += Gdx.graphics.getDeltaTime();

            // Fechamento terminado: desativa fundo
            if (!abrirMenu && animation.isAnimationFinished(stateTime)) {
                fundoAtivo = false;
                animation = null;
                frameFinal = null;
                return fundoAtivo;
            }

            // Abertura terminada: fixa o último frame
            if (abrirMenu && animation.isAnimationFinished(stateTime)) {
                frameFinal = obterFrame(animation, true);
                animation = null;
            }

            if (animation != null) {
                TextureRegion frameAtual = executarUmaVez(animation, stateTime, !abrirMenu);
                desenharFrame(batch, frameAtual, x, y, tamanhoFundo);
            }
        }

        if (frameFinal != null && abrirMenu) {
            desenharFrame(batch, frameFinal, x, y, tamanhoFundo);
            return true;
        }
        return false;
    }

    private void desenharFrame(Batch batch, TextureRegion frame, int x, int y, int tamanho) {
        batch.begin();
        batch.draw(frame, x, y, tamanho, tamanho);
        batch.end();
    }
}


