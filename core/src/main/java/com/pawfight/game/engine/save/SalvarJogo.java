package com.pawfight.game.engine.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.pawfight.game.entity.player.PlayerTemplate;

public class SalvarJogo {

    public void salvar(DadosSalvosJogador data) {
        Json json = new Json();
        String saveString = json.toJson(data);

        FileHandle file = Gdx.files.local("save/" + data.nomePersonagem + "Save.json");
        file.writeString(saveString, false);
        Gdx.app.log("SalvarJogo", "Jogo salvo em " + file.file().getPath() + " — personagem: " + data.nomePersonagem
            + " (level " + data.level + ", xp " + data.xp + "/" + data.xpNecessario + ", moedas " + data.moedas + ")");
    }

    public DadosSalvosJogador loadGame(String nomePersonagem) {
        String path = "save/" + nomePersonagem + "Save.json";
        // Procura no diretório local (workingDir); senão, no classpath — funciona via gradlew e pela IDE
        FileHandle file = Gdx.files.local(path);
        if (!file.exists()) {
            file = Gdx.files.internal(path);
        }
        if (!file.exists()) {
            Gdx.app.log("SalvarJogo", "Nenhum save em " + path + " para: " + nomePersonagem);
            return null;
        }

        Json json = new Json();
        DadosSalvosJogador dados = json.fromJson(DadosSalvosJogador.class, file.readString());
        Gdx.app.log("SalvarJogo", "Jogo carregado de " + file.file().getPath() + " — personagem: " + nomePersonagem
            + " (level " + dados.level + ", xp " + dados.xp + "/" + dados.xpNecessario + ", moedas " + dados.moedas + ")");
        return dados;
    }
}
