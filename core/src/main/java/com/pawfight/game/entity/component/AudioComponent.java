package com.pawfight.game.entity.component;

import com.badlogic.gdx.audio.Music;
import com.pawfight.game.engine.AudioEngine;
import com.pawfight.game.entity.player.DadosPlayer;

public class AudioComponent {

    private final AudioEngine audioEngine = new AudioEngine();
    private Music audioDano;
    private Music audioMorte;
    private Music audioLevelUp;
    private Music audioMoving;

    // ── Inicialização ──────────────────────────────────────────

    public void init(DadosPlayer dados) {
        audioLevelUp = audioEngine.criarAudio("entitys/player/audios/power_up.wav");
        audioDano = audioEngine.criarAudio("entitys/player/audios/hurt.wav");
        audioMoving = dados.audioMoving();
    }

    public void initEnemy(Music audioDano, Music audioMorte) {
        this.audioDano = audioDano;
        this.audioMorte = audioMorte;
    }

    // ── Update (chamado todo frame) ────────────────────────────

    public void updateAudio(boolean moving) {
        if (moving) {
            audioEngine.passos(audioMoving);
        } else if (audioMoving != null && audioMoving.isPlaying()) {
            audioMoving.stop();
        }
    }

    // ── Tocar efeitos ──────────────────────────────────────────

    public void playDano()    { audioEngine.efeito(audioDano); }
    public void playMorte()   { audioEngine.efeito(audioMorte); }
    public void playLevelUp() { audioEngine.efeito(audioLevelUp); }

    // ── Getters / Setters ──────────────────────────────────────

    public AudioEngine getAudioEngine() { return audioEngine; }

    public Music getAudioMorte() { return audioMorte; }
    public void setAudioMorte(Music audioMorte) { this.audioMorte = audioMorte; }

    public Music getAudioDano() { return audioDano; }
    public void setAudioDano(Music audioDano) { this.audioDano = audioDano; }
}

