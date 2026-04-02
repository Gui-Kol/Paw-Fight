package com.pawfight.game.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import static com.pawfight.game.engine.VariavelComum.*;

public class AudioEngine {

    public Music criarAudio(String caminho) {
        return Gdx.audio.newMusic(Gdx.files.internal(caminho));
    }

    public void efeito(Music audio) {
        if (audio != null && !audio.isPlaying()) {
            audio.setVolume(VOLUME_EFEITOS);
            audio.play();
        }
    }
    public void passos(Music audio) {
        if (audio != null && !audio.isPlaying()) {
            audio.setVolume(VOLUME_PASSOS);
            audio.play();
        }
    }

    public void musica(Music audio) {
        if (audio == null) return;

        audio.setVolume(VOLUME_MUSICA);

        if (!audio.isPlaying()) {
            audio.setLooping(true);
            audio.play();
        }
    }
}
