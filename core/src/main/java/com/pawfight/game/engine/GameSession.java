package com.pawfight.game.engine;

import com.pawfight.game.entity.player.PlayerTemplate;

/** Estado da run compartilhado entre Base e mundos. A sessão é a proprietária do Player. */
public final class GameSession {
    private PlayerTemplate player;
    private long seed;
    private String mundoAtual;
    private boolean encerrada;

    public GameSession(long seed) {
        this.seed = seed;
    }

    public void definirPlayer(PlayerTemplate player) {
        if (this.player != null && this.player != player) {
            this.player.dispose();
        }
        this.player = player;
        this.encerrada = false;
    }

    public void encerrar() {
        if (encerrada) return;
        encerrada = true;
        if (player != null) {
            player.dispose();
            player = null;
        }
        mundoAtual = null;
    }

    public PlayerTemplate getPlayer() { return player; }
    public long getSeed() { return seed; }
    public void setSeed(long seed) { this.seed = seed; }
    public String getMundoAtual() { return mundoAtual; }
    public void setMundoAtual(String mundoAtual) { this.mundoAtual = mundoAtual; }
    public boolean isEncerrada() { return encerrada; }
}
