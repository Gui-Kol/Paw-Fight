package com.pawfight.game.engine.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.pawfight.game.entity.player.PlayerTemplate;

public class SalvarJogo {

    public void salvar(DadosSalvosJogador data) {
        Json json = new Json();
        String saveString = json.toJson(data);

        FileHandle file = Gdx.files.local("save/" + data.nomePersonagem + "Save.json"); // salva no diretório local
        file.writeString(saveString, false);
        Gdx.app.log("SalvarJogo", "Jogo salvo com sucesso para o personagem: " + data.nomePersonagem);
    }

    public DadosSalvosJogador loadGame(String nomePersonagem) {
        String path = "save/" + nomePersonagem + "Save.json";
        // Primeiro procura no diretório local (workingDir = assets/), senão no classpath.
        // Assim funciona tanto pelo gradlew:run quanto rodando pela IDE (workingDir do projeto).
        FileHandle file = Gdx.files.local(path);
        if (!file.exists()) {
            file = Gdx.files.internal(path);
        }
        if (!file.exists()) return null; // se não existir, retorna null

        Json json = new Json();
        Gdx.app.log("SalvarJogo", "Jogo carregado com sucesso para o personagem: " + nomePersonagem);
        return json.fromJson(DadosSalvosJogador.class, file.readString());
    }
}
