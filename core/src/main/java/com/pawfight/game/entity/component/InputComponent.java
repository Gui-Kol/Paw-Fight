package com.pawfight.game.entity.component;

import com.badlogic.gdx.Gdx;
import com.pawfight.game.engine.input.GameAction;
import com.pawfight.game.engine.input.GerenciadorInput;

public class InputComponent {

    private static final String TAG = "InputComponent";

    // Fachada única: cada método continua o mesmo, mas agora responde a teclado e gamepad
    private GerenciadorInput entrada() { return GerenciadorInput.getInstance(); }

    private boolean acaoBorda(GameAction action) {
        boolean ativa = entrada().isPressionadaAgora(action);
        if (ativa) {
            Gdx.app.debug(TAG, "Ação disparada: " + action.getLabel());
        }
        return ativa;
    }

    public boolean isMoveRight()     { return entrada().isAtiva(GameAction.MOVE_RIGHT); }
    public boolean isMoveLeft()      { return entrada().isAtiva(GameAction.MOVE_LEFT); }
    public boolean isMoveUp()        { return entrada().isAtiva(GameAction.MOVE_UP); }
    public boolean isMoveDown()      { return entrada().isAtiva(GameAction.MOVE_DOWN); }

    public boolean isAttackSpecial() { return acaoBorda(GameAction.ATTACK_SPECIAL); }
    public boolean isAbility()       { return acaoBorda(GameAction.ABILITY); }

    public boolean isPauseToggle()   { return acaoBorda(GameAction.PAUSE_TOGGLE); }
    public boolean isDebugToggle()   { return acaoBorda(GameAction.DEBUG_TOGGLE); }
    public boolean isCheatToggle()   { return acaoBorda(GameAction.CHEAT_TOGGLE); }
}
