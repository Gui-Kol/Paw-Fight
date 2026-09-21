package com.pawfight.game.engine;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AudioEngineTest {
    @Test
    void atualizaVolumesSemReiniciarAudioEmUso() {
        GameConfig config = GameConfig.getInstance();
        float musica = config.getVolumeMusica();
        float efeitos = config.getVolumeEfeitos();
        float passos = config.getVolumePassos();
        try {
            config.setVolumeMusica(0.1f);
            config.setVolumeEfeitos(0.4f);
            config.setVolumePassos(0.6f);
            Music audio = mock(Music.class);
            when(audio.isPlaying()).thenReturn(true);
            AudioEngine motor = new AudioEngine();
            motor.musica(audio);
            motor.efeito(audio);
            motor.passos(audio);
            verify(audio).setVolume(0.1f);
            verify(audio).setVolume(0.4f);
            verify(audio).setVolume(0.6f);
            verify(audio, never()).play();
        } finally {
            config.setVolumeMusica(musica);
            config.setVolumeEfeitos(efeitos);
            config.setVolumePassos(passos);
        }
    }

    @Test
    void iniciaMusicaEmLoopEIgnoraAudioNulo() {
        Application anterior = Gdx.app;
        Gdx.app = mock(Application.class);
        try {
            Music audio = mock(Music.class);
            AudioEngine motor = new AudioEngine();
            motor.musica(audio);
            verify(audio).setVolume(GameConfig.getInstance().getVolumeMusica());
            verify(audio).setLooping(true);
            verify(audio).play();
            motor.musica(null);
            motor.efeito(null);
            motor.passos(null);
        } finally {
            Gdx.app = anterior;
        }
    }
}
