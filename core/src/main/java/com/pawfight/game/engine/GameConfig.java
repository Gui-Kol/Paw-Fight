package com.pawfight.game.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

public final class GameConfig {

    private static final GameConfig INSTANCE = new GameConfig();

    private GameConfig() { }

    public static GameConfig getInstance() {
        return INSTANCE;
    }

    public static final int LARGURA_TELA_BASE = 1920;
    public static final int ALTURA_TELA_BASE = 1080;

    private boolean debugMode = false;
    private boolean hitboxVisivel = false;

    private float volumeMusica = 0.3f;
    private float volumeEfeitos = 0.7f;
    private float volumePassos = 0.2f;
    private boolean telaCheia = true;

    public boolean isTelaCheia() {
        return telaCheia;
    }

    public void setTelaCheia(boolean telaCheia) {
        this.telaCheia = telaCheia;
    }

    public void carregarConfiguracoes() {
        Preferences preferencias = Gdx.app.getPreferences("pawfight-config");
        setVolumeMusica(preferencias.getFloat("volumeMusica", 0.3f));
        setVolumeEfeitos(preferencias.getFloat("volumeEfeitos", 0.7f));
        setVolumePassos(preferencias.getFloat("volumePassos", 0.2f));
        setTelaCheia(preferencias.getBoolean("telaCheia", true));
    }

    public void salvarConfiguracoes() {
        Preferences preferencias = Gdx.app.getPreferences("pawfight-config");
        preferencias.putFloat("volumeMusica", volumeMusica);
        preferencias.putFloat("volumeEfeitos", volumeEfeitos);
        preferencias.putFloat("volumePassos", volumePassos);
        preferencias.putBoolean("telaCheia", telaCheia);
        preferencias.flush();
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public boolean isHitboxVisivel() {
        return hitboxVisivel;
    }

    public void setHitboxVisivel(boolean hitboxVisivel) {
        this.hitboxVisivel = hitboxVisivel;
    }

    public float getVolumeMusica() {
        return volumeMusica;
    }

    public void setVolumeMusica(float volume) {
        this.volumeMusica = MathUtils.clamp(volume, 0f, 1f);
    }

    public void setVolumeMusicaPercent(int percent) {
        setVolumeMusica(percent / 100f);
    }

    public float getVolumeEfeitos() {
        return volumeEfeitos;
    }

    public void setVolumeEfeitos(float volume) {
        this.volumeEfeitos = MathUtils.clamp(volume, 0f, 1f);
    }

    public void setVolumeEfeitosPercent(int percent) {
        setVolumeEfeitos(percent / 100f);
    }

    public float getVolumePassos() {
        return volumePassos;
    }

    public void setVolumePassos(float volume) {
        this.volumePassos = MathUtils.clamp(volume, 0f, 1f);
    }

    public void setVolumePassosPercent(int percent) {
        setVolumePassos(percent / 100f);
    }

    public float getScale() {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        return Math.min(screenW / LARGURA_TELA_BASE, screenH / ALTURA_TELA_BASE);
    }
}
