package com.pawfight.game.engine.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class SalvarJogo {

    public void salvar(DadosSalvosJogador data) {
        Json json = new Json();
        String saveString = json.toJson(data);

        FileHandle file = Gdx.files.local("save/" + data.getNomePersonagem() + "Save.json"); // salva no diretório local
        file.writeString(saveString, false);
        Gdx.app.log("SalvarJogo", "Jogo salvo com sucesso para o personagem: " + data.getNomePersonagem());
    }

    public DadosSalvosJogador loadGame(String nomePersonagem) {
        FileHandle file = Gdx.files.local("save/" + nomePersonagem + "Save.json");
        if (!file.exists()) return null; // se não existir, retorna null

        Json json = new Json();
        Gdx.app.log("SalvarJogo", "Jogo carregado com sucesso para o personagem: " + nomePersonagem);
        return json.fromJson(DadosSalvosJogador.class, file.readString());
    }
}
