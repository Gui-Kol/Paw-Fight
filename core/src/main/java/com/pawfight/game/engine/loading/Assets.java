package com.pawfight.game.engine.loading;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    public static final AssetManager manager = new AssetManager();

    public static void queueAll() {
        loadTextures();
        loadMusic();
    }

    private static void loadTextures() {
        manager.load("menu/BackGroundPawFight.png", Texture.class);
        manager.load("menu/dark_back_groud.png", Texture.class);
        manager.load("menu/menu.png", Texture.class);

        manager.load("menu/button/play/play1.png", Texture.class);
        manager.load("menu/button/play/play2.png", Texture.class);
        manager.load("menu/button/play/play3.png", Texture.class);

        manager.load("menu/button/quit/quit1.png", Texture.class);
        manager.load("menu/button/quit/quit2.png", Texture.class);
        manager.load("menu/button/quit/quit3.png", Texture.class);

        manager.load("menu/button/save/save1.png", Texture.class);
        manager.load("menu/button/save/save2.png", Texture.class);
        manager.load("menu/button/save/save3.png", Texture.class);

        manager.load("menu/button/resume/resume1.png", Texture.class);
        manager.load("menu/button/resume/resume2.png", Texture.class);
        manager.load("menu/button/resume/resume3.png", Texture.class);

        manager.load("menu/button/settings/settings1.png", Texture.class);
        manager.load("menu/button/settings/settings2.png", Texture.class);
        manager.load("menu/button/settings/settings3.png", Texture.class);

        manager.load("menu/pause/pauseFundo-Sheet.png", Texture.class);

        manager.load("Hud/coin.png", Texture.class);
        manager.load("Hud/coracao.png", Texture.class);
        manager.load("Hud/coracao1.png", Texture.class);
        manager.load("Hud/raio.png", Texture.class);
        manager.load("Hud/musculo.png", Texture.class);
        manager.load("Hud/requa.png", Texture.class);
        manager.load("Hud/nuvemChao.png", Texture.class);

        manager.load("entitys/player/selecao/black_cat.png", Texture.class);
        manager.load("entitys/player/selecao/orange_cat.png", Texture.class);
        manager.load("entitys/player/selecao/black_bird.png", Texture.class);
        manager.load("entitys/player/selecao/blue_bird.png", Texture.class);
        manager.load("world/base/nuvens/back.png", Texture.class);
        manager.load("world/base/nuvens/4.png", Texture.class);

        manager.load("entitys/player/black_cat/Idle.png", Texture.class);
        manager.load("entitys/player/black_cat/Walk.png", Texture.class);
        manager.load("entitys/player/black_cat/Death.png", Texture.class);
        manager.load("entitys/player/black_cat/Hurt.png", Texture.class);

        manager.load("entitys/player/orange_cat/Idle.png", Texture.class);
        manager.load("entitys/player/orange_cat/Walk.png", Texture.class);
        manager.load("entitys/player/orange_cat/Death.png", Texture.class);
        manager.load("entitys/player/orange_cat/Hurt.png", Texture.class);

        manager.load("entitys/player/black_bird/Idle.png", Texture.class);
        manager.load("entitys/player/black_bird/Walk.png", Texture.class);
        manager.load("entitys/player/black_bird/Death.png", Texture.class);
        manager.load("entitys/player/black_bird/Hurt.png", Texture.class);

        manager.load("entitys/player/dove/Idle.png", Texture.class);
        manager.load("entitys/player/dove/Walk.png", Texture.class);
        manager.load("entitys/player/dove/Death.png", Texture.class);
        manager.load("entitys/player/dove/Hurt.png", Texture.class);

        manager.load("entitys/player/dove/coco/1.png", Texture.class);
        manager.load("entitys/player/dove/coco/2.png", Texture.class);
        manager.load("entitys/player/dove/coco/3.png", Texture.class);

        // Fantasmagórico, Solar e Gelo reutilizam texturas do HUD (raio.png / nuvemChao.png) já carregadas acima
        manager.load("entitys/player/black_cat/arranhao.png", Texture.class);
        manager.load("effects/sangue/sangue.png", Texture.class);

        manager.load("entitys/enemy/Skeleton/Idle.png", Texture.class);
        manager.load("entitys/enemy/Skeleton/Walk.png", Texture.class);
        manager.load("entitys/enemy/Skeleton/Death.png", Texture.class);

        manager.load("world/mundo_areia/Tilesets/obj/cacto.png", Texture.class);

        manager.load("particles/dano.png", Texture.class);
        manager.load("particles/cura.png", Texture.class);
        manager.load("particles/levelup.png", Texture.class);
    }

    private static void loadMusic() {
        manager.load("audio/music/home.wav", Music.class);
        manager.load("audio/music/mundoAreia.wav", Music.class);
        manager.load("audio/music/menu.wav", Music.class);
        manager.load("menu/button/button.wav", Music.class);
        manager.load("entitys/player/audios/power_up.wav", Music.class);
        manager.load("entitys/player/audios/hurt.wav", Music.class);
        manager.load("entitys/player/audios/asas_passaro.wav", Music.class);
        manager.load("entitys/player/audios/passos.wav", Music.class);
        manager.load("entitys/enemy/Skeleton/audioMorte.wav", Music.class);
    }

    public static <T> T get(String path, Class<T> type) {
        return manager.get(path, type);
    }

    public static void dispose() {
        manager.dispose();
    }
}
