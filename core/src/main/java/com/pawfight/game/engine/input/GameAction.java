package com.pawfight.game.engine.input;

import com.badlogic.gdx.Input;

public enum GameAction {

    MOVE_RIGHT   (Input.Keys.D,     "Mover Direita",    false),
    MOVE_LEFT    (Input.Keys.A,     "Mover Esquerda",   false),
    MOVE_UP      (Input.Keys.W,     "Mover Cima",       false),
    MOVE_DOWN    (Input.Keys.S,     "Mover Baixo",      false),

    ATTACK_SPECIAL (Input.Keys.SPACE, "Ataque Especial", true),
    ABILITY        (Input.Keys.R,     "Habilidade",      true),

    PAUSE_TOGGLE   (Input.Keys.ESCAPE, "Pausar",         true),
    DEBUG_TOGGLE   (Input.Keys.F3,     "Debug",          true),
    CHEAT_TOGGLE   (Input.Keys.F6,     "Cheat",          true),

    MENU_RIGHT (Input.Keys.RIGHT, "Menu Direita", true),
    MENU_LEFT  (Input.Keys.LEFT,  "Menu Esquerda", true),
    MENU_CONFIRM     (Input.Keys.ENTER, "Menu Confirmar", true),;

    private final int defaultKey;
    private final String label;
    private final boolean justPressed;

    GameAction(int defaultKey, String label, boolean justPressed) {
        this.defaultKey = defaultKey;
        this.label = label;
        this.justPressed = justPressed;
    }

    public int getDefaultKey()  { return defaultKey; }
    public String getLabel()    { return label; }
    public boolean isJustPressed() { return justPressed; }
}

