package com.pawfight.game.engine;

import com.badlogic.gdx.Gdx;

public class CommunVariable {
    public static boolean HITBOX_ISVISIBLE = false;

    public static void setHitboxIsvisible(boolean hitboxIsvisible) {
        HITBOX_ISVISIBLE = hitboxIsvisible;
    }

    public static float GET_SCALE(){
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float scale = Math.min(screenW / GET_LARGURA_TELA_BASE(), screenH / GET_ALTURA_TELA_BASE());
        if (scale < 1){
            return 1;
        }
        return scale;
    }
    public static int GET_ALTURA_TELA_BASE(){
        return 1080;
    }
    public static int GET_LARGURA_TELA_BASE(){
        return 1920;
    }
}
