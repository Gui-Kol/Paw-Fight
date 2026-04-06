package com.pawfight.game.entity.component;

import com.badlogic.gdx.Gdx;
import com.pawfight.game.engine.save.DadosSalvosJogador;

/**
 * Componente responsável por salvar e carregar dados do jogador.
 */
public class SaveComponent {

    public DadosSalvosJogador saveData(String nome, StatsComponent stats) {
        DadosSalvosJogador data = new DadosSalvosJogador();
        data.setNomePersonagem(nome);
        data.setVidaBase(stats.getVidaBase());
        data.setVelocidade(stats.getVelocidade());
        data.setForca(stats.getForca());
        data.setLevel(stats.getLevel());
        data.setXp(stats.getXp());
        data.setXpNecessario(stats.getXpNecessario());
        data.setMoedas(stats.getMoedas());
        data.setPontosDisponiveis(stats.getPontosDisponiveis());
        return data;
    }

    public void loadSaveData(DadosSalvosJogador data, StatsComponent stats) {
        stats.setVidaBase(data.getVidaBase());
        stats.setVida(data.getVidaBase());
        stats.setVelocidade(data.getVelocidade());
        stats.setForca(data.getForca());
        stats.setLevel(data.getLevel());
        stats.setXp(data.getXp());
        stats.setXpNecessario(data.getXpNecessario());
        stats.setMoedas(data.getMoedas());
        stats.setPontosDisponiveis(data.getPontosDisponiveis());
        Gdx.app.log("SaveComponent", "Save carregado para " + data.getNomePersonagem());
    }
}

