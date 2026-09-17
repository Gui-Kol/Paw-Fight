package com.pawfight.game.entity.component;

import com.badlogic.gdx.Gdx;
import com.pawfight.game.engine.input.GameAction;
import com.pawfight.game.engine.input.KeyBindings;

public class InputComponent {

    private static final String TAG = "InputComponent";

    private KeyBindings keys() { return KeyBindings.getInstance(); }

    private boolean acaoBorda(GameAction action) {
        boolean ativa = keys().isActive(action);
        if (ativa) {
            Gdx.app.debug(TAG, "Ação disparada: " + action.getLabel() + " [" + keys().getKeyName(action) + "]");
        }
        return ativa;
    }

    public boolean isMoveRight()     { return keys().isActive(GameAction.MOVE_RIGHT); }
    public boolean isMoveLeft()      { return keys().isActive(GameAction.MOVE_LEFT); }
    public boolean isMoveUp()        { return keys().isActive(GameAction.MOVE_UP); }
    public boolean isMoveDown()      { return keys().isActive(GameAction.MOVE_DOWN); }

    public boolean isAttackSpecial() { return acaoBorda(GameAction.ATTACK_SPECIAL); }
    public boolean isAbility()       { return acaoBorda(GameAction.ABILITY); }

    public boolean isPauseToggle()   { return acaoBorda(GameAction.PAUSE_TOGGLE); }
    public boolean isDebugToggle()   { return acaoBorda(GameAction.DEBUG_TOGGLE); }
    public boolean isCheatToggle()   { return acaoBorda(GameAction.CHEAT_TOGGLE); }
}
