package com.pawfight.game.entity.component;

import com.badlogic.gdx.Gdx;
import com.pawfight.game.entity.player.DadosPlayer;

public class StatsComponent {

    private static final float HURT_DURATION = 0.5f;
    private static final float DANO_COOLDOWN_DURATION = 0.5f;

    // Stats base
    private int vidaBase;
    private int vida;
    private int forca;
    private int velocidade;
    private int level;
    private final int tamanhoTiro;
    private final float cadenciaTiro;
    private final float duracaoTiro;

    // Progressão
    private int xp;
    private float xpMultiplicador = 1;
    private int xpNecessario;
    private int pontosDisponiveis;
    private int moedas;

    // Estado de dano
    private boolean morto = false;
    private boolean hurt = false;
    private boolean podeTomarDano = true;
    private float hurtTime = 0f;
    private float danoCooldown = 0f;

    public StatsComponent(DadosPlayer dados) {
        this.forca = dados.forca();
        this.cadenciaTiro = dados.cadenciaTiro();
        this.duracaoTiro = dados.duracaoTiro();
        this.vidaBase = dados.vidaBase();
        this.velocidade = dados.velocidade();
        this.tamanhoTiro = dados.tamanhoTiro();
        this.vida = vidaBase;
        this.level = 1;
        this.xpNecessario = 50;
        this.xp = 0;
        this.pontosDisponiveis = 0;
        this.moedas = 0;
    }

    public StatsComponent(int vidaBase, int forca, int velocidade) {
        this.vidaBase = vidaBase;
        this.vida = vidaBase;
        this.forca = forca;
        this.velocidade = velocidade;
        this.tamanhoTiro = 0;
        this.cadenciaTiro = 0;
        this.duracaoTiro = 0;
        this.level = 0;
        this.xpNecessario = 0;
        this.xp = 0;
        this.pontosDisponiveis = 0;
        this.moedas = 0;
    }

    // ── Timers (chamado todo frame) ────────────────────────────
    public void updateTimers(float delta) {
        if (hurt) {
            hurtTime += delta;
            if (hurtTime >= HURT_DURATION) {
                hurt = false;
                hurtTime = 0f;
            }
        }
        if (!podeTomarDano) {
            danoCooldown += delta;
            if (danoCooldown >= DANO_COOLDOWN_DURATION) {
                podeTomarDano = true;
                danoCooldown = 0f;
            }
        }
    }

    // ── Dano ───────────────────────────────────────────────────
    public boolean aplicarDano(int forcaDano) {
        if (!podeTomarDano) return false;
        podeTomarDano = false;
        vida -= forcaDano;
        if (vida <= 0) {
            morto = true;
        } else {
            hurt = true;
            hurtTime = 0f;
        }
        return true;
    }

    // ── XP / Level ─────────────────────────────────────────────
    public int xpUp(int xpGanho) {
        xp += xpGanho;
        int levelsGanhos = 0;
        while (xp >= xpNecessario) {
            xp -= xpNecessario;
            level++;
            pontosDisponiveis++;
            levelsGanhos++;
            if (level % 5 == 0) {
                xpMultiplicador += 0.05f;
            }
            xpNecessario = (int) (level * 50 * xpMultiplicador);
            Gdx.app.log("StatsComponent", "XP necessário para próximo nível: " + xpNecessario);
        }
        return levelsGanhos;
    }

    // ── Moedas ─────────────────────────────────────────────────
    public void moedaUp(int moedasGanha) {
        moedas += moedasGanha;
    }

    // ── Upgrade de stats (gastando pontos) ─────────────────────
    public void vidaBaseUp(int pontosGastos) {
        vidaBase += 1;
        vida = vidaBase;
        gastouPontos(pontosGastos);
    }

    public void forcaUp(int pontosGastos) {
        forca += 1;
        gastouPontos(pontosGastos);
    }

    public void velocidadeUp(int pontosGastos) {
        velocidade += 20;
        gastouPontos(pontosGastos);
    }

    public void gastouPontos(int pontosGastos) {
        pontosDisponiveis -= pontosGastos;
    }

    // ── Getters ────────────────────────────────────────────────
    public int getVida() { return vida; }
    public int getVidaBase() { return vidaBase; }
    public int getForca() { return forca; }
    public int getVelocidade() { return velocidade; }
    public int getLevel() { return level; }
    public int getTamanhoTiro() { return tamanhoTiro; }
    public float getCadenciaTiro() { return cadenciaTiro; }
    public float getDuracaoTiro() { return duracaoTiro; }
    public int getXp() { return xp; }
    public int getXpNecessario() { return xpNecessario; }
    public int getPontosDisponiveis() { return pontosDisponiveis; }
    public int getMoedas() { return moedas; }
    public boolean isMorto() { return morto; }
    public boolean isHurt() { return hurt; }
    public float getHurtTime() { return hurtTime; }

    // ── Setters (para save/load e cheats) ──────────────────────
    public void setVida(int vida) { this.vida = vida; }
    public void setVidaBase(int vidaBase) { this.vidaBase = vidaBase; }
    public void setForca(int forca) { this.forca = forca; }
    public void setVelocidade(int velocidade) { this.velocidade = velocidade; }
    public void setLevel(int level) { this.level = level; }
    public void setXp(int xp) { this.xp = xp; }
    public void setXpNecessario(int xpNecessario) { this.xpNecessario = xpNecessario; }
    public void setPontosDisponiveis(int pontosDisponiveis) { this.pontosDisponiveis = pontosDisponiveis; }
    public void setMoedas(int moedas) { this.moedas = moedas; }
}

