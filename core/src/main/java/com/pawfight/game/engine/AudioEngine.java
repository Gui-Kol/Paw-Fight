package com.pawfight.game.engine;

import com.badlogic.gdx.audio.Music;
import com.pawfight.game.engine.loading.Assets;

public class AudioEngine {

    private final GameConfig config = GameConfig.getInstance();

    public Music criarAudio(String caminho) {
        return Assets.get(caminho, Music.class);
    }

    public void efeito(Music audio) {
        if (audio != null && !audio.isPlaying()) {
            audio.setVolume(config.getVolumeEfeitos());
            audio.play();
        }
    }
    public void passos(Music audio) {
        if (audio != null && !audio.isPlaying()) {
            audio.setVolume(config.getVolumePassos());
            audio.play();
        }
    }

    public void musica(Music audio) {
        if (audio == null) return;

        audio.setVolume(config.getVolumeMusica());

        if (!audio.isPlaying()) {
            audio.setLooping(true);
            audio.play();
        }
    }
}
