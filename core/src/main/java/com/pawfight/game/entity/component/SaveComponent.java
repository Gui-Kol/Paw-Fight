package com.pawfight.game.entity.component;

import com.badlogic.gdx.Gdx;
import com.pawfight.game.engine.save.DadosSalvosJogador;

public class SaveComponent {

    public DadosSalvosJogador saveData(String nome, StatsComponent stats) {
        DadosSalvosJogador data = new DadosSalvosJogador();
        data.nomePersonagem    = nome;
        data.vidaBase          = stats.getVidaBase();
        data.velocidade        = stats.getVelocidade();
        data.forca             = stats.getForca();
        data.level             = stats.getLevel();
        data.xp                = stats.getXp();
        data.xpNecessario      = stats.getXpNecessario();
        data.moedas            = stats.getMoedas();
        data.pontosDisponiveis = stats.getPontosDisponiveis();
        return data;
    }

    public void loadSaveData(DadosSalvosJogador data, StatsComponent stats) {
        stats.setVidaBase(data.vidaBase);
        stats.setVida(data.vidaBase);
        stats.setVelocidade(data.velocidade);
        stats.setForca(data.forca);
        stats.setLevel(data.level);
        stats.setXp(data.xp);
        stats.setXpNecessario(data.xpNecessario);
        stats.setMoedas(data.moedas);
        stats.setPontosDisponiveis(data.pontosDisponiveis);
        Gdx.app.log("SaveComponent", "Save carregado para " + data.nomePersonagem);
    }
}

