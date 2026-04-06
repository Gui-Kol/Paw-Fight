package com.pawfight.game.entity.component;

import com.pawfight.game.engine.input.GameAction;
import com.pawfight.game.engine.input.KeyBindings;

public class InputComponent {

    private KeyBindings keys() { return KeyBindings.getInstance(); }

    public boolean isMoveRight()     { return keys().isActive(GameAction.MOVE_RIGHT); }
    public boolean isMoveLeft()      { return keys().isActive(GameAction.MOVE_LEFT); }
    public boolean isMoveUp()        { return keys().isActive(GameAction.MOVE_UP); }
    public boolean isMoveDown()      { return keys().isActive(GameAction.MOVE_DOWN); }

    public boolean isAttackSpecial() { return keys().isActive(GameAction.ATTACK_SPECIAL); }
    public boolean isAbility()       { return keys().isActive(GameAction.ABILITY); }

    public boolean isPauseToggle()   { return keys().isActive(GameAction.PAUSE_TOGGLE); }
    public boolean isDebugToggle()   { return keys().isActive(GameAction.DEBUG_TOGGLE); }
    public boolean isCheatToggle()   { return keys().isActive(GameAction.CHEAT_TOGGLE); }
}

