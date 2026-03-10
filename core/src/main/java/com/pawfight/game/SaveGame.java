package com.pawfight.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class SaveGame {

    public void saveGame(SaveDataPlayer data) {
        Json json = new Json();
        String saveString = json.toJson(data);

        FileHandle file = Gdx.files.local("save/" + data.nomePersonagem + "Save.json"); // salva no diretório local
        file.writeString(saveString, false);
    }

    public SaveDataPlayer loadGame(String nomePersonagem) {
        FileHandle file = Gdx.files.local("save/" + nomePersonagem + "Save.json");
        if (!file.exists()) return null; // se não existir, retorna null

        Json json = new Json();
        return json.fromJson(SaveDataPlayer.class, file.readString());
    }
}
