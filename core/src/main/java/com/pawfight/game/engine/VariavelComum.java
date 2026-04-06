package com.pawfight.game.engine;

import com.badlogic.gdx.Gdx;

public class VariavelComum {
    public static boolean DEBUG_MODE = false;
    public static boolean HITBOX_ISVISIBLE = false;
    public static final int GET_ALTURA_TELA_BASE = 1080;
    public static final int GET_LARGURA_TELA_BASE = 1920;
    public static float VOLUME_MUSICA = 0.3f;
    public static float VOLUME_EFEITOS = 0.7f;
    public static float VOLUME_PASSOS = 0.2f;

    public static void setHitboxIsvisible(boolean hitboxIsvisible) {
        HITBOX_ISVISIBLE = hitboxIsvisible;
    }

    public static float GET_SCALE(){
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        return Math.min(screenW / GET_LARGURA_TELA_BASE, screenH / GET_ALTURA_TELA_BASE);
    }
    public static void SET_VOLUME_PASSOS(int volume) {
        VOLUME_PASSOS = volume / 100f;
    }
    public static void SET_VOLUME_MUSICA(int volume) {
        VOLUME_MUSICA = volume / 100f;
    }
    public static void SET_VOLUME_EFEITO(int volume) {
        VOLUME_EFEITOS = volume / 100f;
    }

}
