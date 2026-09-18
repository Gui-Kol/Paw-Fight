package com.pawfight.game.entity.component;

import com.badlogic.gdx.Gdx;
import com.pawfight.game.engine.save.DadosSalvosJogador;
import com.pawfight.game.engine.save.SalvarJogo;
import com.pawfight.game.entity.player.PlayerTemplate;

public class SaveComponent {
    private final SalvarJogo save = new SalvarJogo();

    public void saveData(PlayerTemplate player) {
        DadosSalvosJogador data = new DadosSalvosJogador();
        StatsComponent stats = player.getStats();
        data.nomePersonagem    = player.getName();
        data.vidaBase          = stats.getVidaBase();
        data.velocidade        = stats.getVelocidade();
        data.forca             = stats.getForca();
        data.level             = stats.getLevel();
        data.xp                = stats.getXp();
        data.xpNecessario      = stats.getXpNecessario();
        data.moedas            = stats.getMoedas();
        data.pontosDisponiveis = stats.getPontosDisponiveis();
        save.salvar(data);
        Gdx.app.log("SaveComponent", "Dados coletados para save de " + player.getName()
            + " (level " + data.level + ", vida base " + data.vidaBase + ", força " + data.forca + ")");
    }

    public PlayerTemplate loadSaveData(PlayerTemplate player) {
        DadosSalvosJogador data = save.loadGame(player.getName());
        if (data == null) {
            Gdx.app.log("SaveComponent", "Nenhum save encontrado para " + player.getName() + " criando personagem default...");
            return player;
        }

        StatsComponent stats = player.getStats();
        stats.setVidaBase(data.vidaBase);
        stats.setVida(data.vidaBase);
        stats.setVelocidade(data.velocidade);
        stats.setForca(data.forca);
        stats.setLevel(data.level);
        stats.setXp(data.xp);
        stats.setXpNecessario(data.xpNecessario);
        stats.setMoedas(data.moedas);
        stats.setPontosDisponiveis(data.pontosDisponiveis);
        Gdx.app.log("SaveComponent", "Save aplicado a " + data.nomePersonagem
            + " — level " + data.level + ", vida " + data.vidaBase + ", força " + data.forca
            + ", velocidade " + data.velocidade + ", moedas " + data.moedas);
        return player;
    }
}

