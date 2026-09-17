package com.pawfight.game.engine.design;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.pawfight.game.engine.input.GameAction;
import com.pawfight.game.engine.input.KeyBindings;

public class AlteradorZoom {
    private float zoom = 0.5f;

    public float changeZoom() {
        KeyBindings key = KeyBindings.getInstance();
        if (key.isActive(GameAction.ZOOM_MENOS)) {
            if (zoom < 0.7f) {
                zoom += 0.1f;
                Gdx.app.log("AlteradorZoom", "Zoom aumentado: " + zoom);
            } else {
                Gdx.app.log("AlteradorZoom", "Zoom máximo atingido: " + zoom);
            }
        }
        if (key.isActive(GameAction.ZOOM_MAIS)) {
            if (zoom > 0.3f) {
                zoom -= 0.1f;
                Gdx.app.log("AlteradorZoom", "Zoom diminuído: " + zoom);
            } else {
                Gdx.app.log("AlteradorZoom", "Zoom mínimo atingido: " + zoom);
            }
        }

        return zoom;
    }
}
